package java.io.typeset.sphinx.tests;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReporterNG implements IReporter {
	private ExtentReports extent;

	private void buildTestNodes(IResultMap tests, LogStatus status) {
		ExtentTest test;

		if (tests.size() > 0) {
			for (final ITestResult result : tests.getAllResults()) {
				test = this.extent.startTest(result.getMethod().getMethodName());
				final StringBuffer details = new StringBuffer();
				test.setStartedTime(getTime(result.getStartMillis()));
				test.setEndedTime(getTime(result.getEndMillis()));
				final Object[] instance = result.getParameters();
				for (int i = 0; i < instance.length; i++) {
					details.append(instance[i]);
					if (!(i == instance.length - 1)) {
						details.append(",");
					}
				}

				if (result.FAILURE > 0) {
					test.addScreenCapture(".png");
				}

				if (result.getThrowable() != null) {
					test.log(status, "Test Data - (" + details.toString() + ")");
					test.log(status, result.getThrowable());
				} else {
					test.log(status, "Test Data - (" + details.toString() + ")");
					test.log(status, "Test " + status.toString().toLowerCase() + "ed");
				}

				this.extent.endTest(test);
			}
		}
	}

	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		this.extent = new ExtentReports(outputDirectory + File.separator + "Extent.html", true);

		for (final ISuite suite : suites) {
			final Map<String, ISuiteResult> result = suite.getResults();

			for (final ISuiteResult r : result.values()) {
				final ITestContext context = r.getTestContext();

				buildTestNodes(context.getPassedTests(), LogStatus.PASS);
				buildTestNodes(context.getFailedTests(), LogStatus.FAIL);
				buildTestNodes(context.getSkippedTests(), LogStatus.SKIP);
			}
		}

		this.extent.flush();
		this.extent.close();
	}

	private Date getTime(long millis) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}
}