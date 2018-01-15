package java.io.typeset.sphinx.tests;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import io.typeset.sphinx.tests.ConfigClass;

public class Screenshot extends TestListenerAdapter {
	private static String fileSeperator = System.getProperty("file.separator");

	public static String takeScreenShot(WebDriver driver, String screenShotName, String testName) {
		try {
			final File file = new File("Screenshots" + fileSeperator + "Results");
			if (!file.exists()) {
				System.out.println("File created " + file);
				file.mkdir();
			}

			final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			final File targetFile = new File("Screenshots" + fileSeperator + "Results" + fileSeperator + testName,
					screenShotName);
			FileUtils.copyFile(screenshotFile, targetFile);

			return screenShotName;
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

		final String testMethodName = result.getName().toString().trim();
		final String screenShotName = testMethodName + ".png";

		if (this.driver != null) {
			final String imagePath = ".." + fileSeperator + "Screenshots" + fileSeperator + "Results" + fileSeperator
					+ testClassName + fileSeperator + takeScreenShot(this.driver, screenShotName, testClassName);
			System.out.println("Screenshot can be found : " + imagePath);
		}
	}

}
