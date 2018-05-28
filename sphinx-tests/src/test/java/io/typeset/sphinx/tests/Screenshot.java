package io.typeset.sphinx.tests;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import io.typeset.sphinx.tests.ConfigClass;

public class Screenshot extends TestListenerAdapter {
	public static String takeScreenShot(WebDriver driver, String screenShotName) {
		try {
			String directoryName = "Screenshots";
			final File file = new File(directoryName);
			if (!file.exists()) {
				System.out.println("File created " + file);
				file.mkdir();
			}

			final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			final File targetFile = new File(directoryName, screenShotName);

			try {
				FileUtils.copyFile(screenshotFile, targetFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return targetFile.getAbsolutePath();
		} catch (final Exception e) {
			System.out.println("An exception occured while taking screenshot " + e.getCause());
			return null;
		}
	}

	WebDriver driver;
	ConfigClass con = new ConfigClass();

	public String getTestClassName(String testName) {
		final String[] reqTestClassname = testName.split("\\.");
		final int i = reqTestClassname.length - 1;
		System.out.println("Required Test Name : " + reqTestClassname[i]);
		return reqTestClassname[i];
	}

	@Override
	public void onTestFailure(ITestResult result) {
		System.out.println("***** Error " + result.getName() + " test has failed *****");

		this.driver = this.con.driver;

		final String testClassName = getTestClassName(result.getInstanceName()).trim();
		final String screenShotName = testClassName + ".png";

		if (this.driver != null) {
			final String imagePath = takeScreenShot(this.driver, screenShotName);
			System.out.println("Screenshot can be found at : " + imagePath);
		}
	}

}
