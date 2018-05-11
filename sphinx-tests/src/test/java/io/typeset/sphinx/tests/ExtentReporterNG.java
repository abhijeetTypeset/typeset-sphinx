package io.typeset.sphinx.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.model.ImageAttribute;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ullink.slack.simpleslackapi.SlackAttachment;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.impl.SlackSessionFactory;

import io.typeset.sphinx.Main;

public class ExtentReporterNG implements IReporter {

	String functionStart = "[FUNCTION_START]";
	String functionEnd = "[FUNCTION_END]";
	String testStart = "METHOD_NAME:execute";
	String testName = "Required Test Name";
	String screenShotName = "Screenshot can be found at";
	private static final String S3_BUCKET_NAME = "typeset-sphinx-output";
	private static final String S3_LINK = "https://s3.console.aws.amazon.com/s3/buckets/";

	private List<String> readLogs(String filename) {
		List<String> logLines = new ArrayList<String>();
		try {
			File f = new File(filename);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";
			while ((line = b.readLine()) != null) {
				logLines.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return logLines;
	}

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		// String outputFile = System.getProperty("user.dir") + File.separator +
		// "target" + File.separator
		// + "surefire-reports" + File.separator + "TestSuite-output.txt";
		// List<String> logLines = readLogs(outputFile);
		// processFiles(logLines, outputFile);

		File sphinxDir = new File(System.getProperty("user.dir")).getParentFile();
		String logFile = sphinxDir + File.separator + "sphinx.log";
		String screenshotDir = sphinxDir + File.separator + "Screenshots";

		System.out.println("Screenshots " + logFile + " -- " + screenshotDir);
		
		if(!new File(screenshotDir).exists()) {
			return;
		}

		List<String> allScreenshots = getAllScreenshots(screenshotDir);

		String jobName = System.getenv("JOB_NAME");
		if (jobName == null) {
			jobName = "local";
		}
		String buildURL = System.getenv("BUILD_URL");

		if (allScreenshots.size() > 0) {
			String folderName = UUID.randomUUID().toString();
			uploadToS3(folderName, logFile, screenshotDir, allScreenshots);

			String slackMsg = "*<!channel> Errors have occured in Sphinx " + jobName + ".*\n";
			
			if (buildURL != null) {
				slackMsg += "Jenkins logs can be found at " + buildURL + "console \n";
			}

			slackMsg += "Maven logs and screenshots can be found at\n" + S3_LINK + S3_BUCKET_NAME + "/" + folderName
					+ "/?region=us-west-2&tab=overview";
			sendSlackMessage(slackMsg);
		}

	}

	private List<String> getAllScreenshots(String screenshotDir) {
		File folder = new File(screenshotDir);
		File[] listOfFiles = folder.listFiles();

		List<String> allScreenshots = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());

				String fileName = listOfFiles[i].getName();
				if (fileName.endsWith(".png")) {
					allScreenshots.add(fileName);
				}

			}
		}
		return allScreenshots;
	}

	private void writeProcessedOutput(List<List> content, String processedFilename, String outputFile) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(processedFilename);
			bw = new BufferedWriter(fw);
			for (List<String> testLog : content) {
				for (String line : testLog) {
					bw.write(line + "\n");
				}
				bw.write("=== *** ===");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void processFiles(List<String> logLines, String outputFile) {

		List<List> allTtestLogs = new ArrayList<List>();

		int fncStart = 0;
		int fncEnd = 0;

		boolean foundErrors = false;
		List<String> imageList = new ArrayList<String>();
		Set<String> failingSpecs = new TreeSet<String>();

		List<String> testLogs = new ArrayList<String>();
		for (String l : logLines) {
			if (l.contains(functionStart) && l.contains(testStart)) {
				if (testLogs.size() > 0) {
					if (fncStart > 0 && (fncStart > fncEnd)) {
						testLogs.add("POTENTIAL ERROR");
					}
					allTtestLogs.add(testLogs);
					testLogs = new ArrayList<String>();
					fncStart = 0;
					fncEnd = 0;
				}

			} else {
				if (l.contains(functionStart)) {
					testLogs.add(prettify(l));
					String specName = getSpecName(l);
					if (specName != null) {
						failingSpecs.add(specName);
					}
					fncStart++;
				}

				if (l.contains(functionEnd)) {
					testLogs.add(prettify(l));
					fncEnd++;
				}

				if (l.contains(screenShotName)) {
					String imagePath = getImagepath(l);
					if (imagePath != null) {
						testLogs.add("image_" + imageList.size() + ".png");
						imageList.add(imagePath);
					}
				}

				if (l.contains("Error")) {
					testLogs.add(l);
					foundErrors = true;
				}
			}
		}
		if (testLogs.size() > 0) {
			if (fncStart > 0 && (fncStart > fncEnd)) {
				testLogs.add("POTENTIAL ERROR");
				foundErrors = true;
			}
			allTtestLogs.add(testLogs);
		}

		String processedFilename = System.getProperty("user.dir") + File.separator + "target" + File.separator
				+ "surefire-reports" + File.separator + "TestSuite-processed.txt";

		writeProcessedOutput(allTtestLogs, processedFilename, outputFile);
		if (foundErrors) {
			String folderName = UUID.randomUUID().toString();
			// uploadToS3(folderName, processedFilename, outputFile, imageList);
			String failingSpecString = "";
			for (String s : failingSpecs) {
				failingSpecString += s + ", ";
			}
			String slackMsg = "*Errors have occured in Sphinx execution.*\n" + "Potentially failing specs : "
					+ failingSpecString + "More details, screenshots can be found at\n" + S3_LINK + S3_BUCKET_NAME + "/"
					+ folderName + "/?region=us-west-2&tab=overview";
			sendSlackMessage(slackMsg);
		}

	}

	private String getSpecName(String l) {
		try {
			int idx1 = l.indexOf("SPEC_NAME:");
			int idx2 = l.indexOf(";");

			if (idx1 > -1 && idx2 > -1) {
				return l.substring(idx1 + 10, idx2).trim();
			}
		} catch (Exception e) {

		}

		return null;
	}

	private String getImagepath(String l) {
		int idx1 = l.indexOf("/");
		int idx2 = l.indexOf(".png");

		if (idx1 > -1 && idx2 > -1) {
			return l.substring(idx1, idx2 + 4).trim().replace("sphinx-tests/", "");
		}

		return null;
	}

	private void sendSlackMessage(String slackMsg) {
		try {
			SlackSession session = SlackSessionFactory
					.createWebSocketSlackSession("xoxb-276710034624-GyYuox2vRUJ2zbfc0l3BE6Qe");

			session.connect();
			SlackChannel channel = session.findChannelByName(Main.slackChannel);

			SlackAttachment arg2 = null;
			session.sendMessage(channel, slackMsg, arg2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void createFolder(String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(S3_BUCKET_NAME, folderName, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}

	private String uploadToS3(String folderName, String mavenLog, String screenshotDir, List<String> imageList) {
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJJAUVBYLZ7H2JJRQ",
				"Bq1wg1OLSLwClcnYIBDIwl6H1ZGLsEtIbksp3cD9");
		AmazonS3 s3client = new AmazonS3Client(credentials);

		createFolder(folderName, s3client);

		s3client.putObject(new PutObjectRequest(S3_BUCKET_NAME, folderName + "/maven_log.txt", new File(mavenLog)));

		for (String image : imageList) {
			String imageName = folderName + "/" + image;
			s3client.putObject(
					new PutObjectRequest(S3_BUCKET_NAME, imageName, new File(screenshotDir + File.separator + image)));
		}

		return folderName;
	}

	private String prettify(String l) {
		l = l.replace(functionStart, "Started executing --> ");
		l = l.replace(functionEnd, "Finished executing --> ");
		l = l.replace("SPEC_NAME:", " Spec ");
		l = l.replace("METHOD_NAME:", " Method ");
		return l;
	}
}
