package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

public class ConfigClass {
	public static String testDataFile;
	protected static WebDriver driver;
	static String OS = System.getProperty("os.name").toLowerCase();
	InputStream inputStream;

	Properties prop;

	@AfterMethod
	public void closedriver() {
		driver.close();
	}

	@BeforeSuite
	public void Setconfiguration() throws Exception {
		this.prop = new Properties();
		final String propFileName = "config.properties";
		final FileInputStream ip = new FileInputStream(propFileName);
		this.prop.load(ip);
		testDataFile = this.prop.getProperty("testdata");
		System.out.println("input data file " + testDataFile);
		ExcelReader ereader = new ExcelReader();
		ereader.readData(testDataFile);
		ereader.verifyAllInputsPresent();
	}

	@BeforeMethod
	@Parameters({ "Browser", "url" })
	public void Setconfiguration(String Browser, String Url) throws IOException {

		if (Browser.equalsIgnoreCase("phantom")) {
			driver = new PhantomJSDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			System.out.println("phantom driver selected");
			driver.get(Url);

		}

		if (Browser.equalsIgnoreCase("firefox")) {
			driver = new FirefoxDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			System.out.println("firefox driver selected");
			driver.get(Url);
		}

		if (Browser.equalsIgnoreCase("chrome")) {
			System.out.println("Chrome path : " + System.getProperty("webdriver.chrome.driver"));
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			System.out.println("Chrome driver selected");
			driver.get(Url);

		}
		if (Browser.equalsIgnoreCase("chrome-headless")) {
			System.out.println("Chrome path : " + System.getProperty("webdriver.chrome.driver"));

			final ChromeOptions options = new ChromeOptions();
			options.addArguments("headless");
			options.addArguments("window-size=1200x1072");
			driver = new ChromeDriver(options);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			System.out.println("Chrome-headless driver selected");
			driver.get(Url);

		}
		if (Browser.equalsIgnoreCase("iexplorer")) {
			driver = new InternetExplorerDriver();
			driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
			driver.manage().window().maximize();
			driver.get(Url);
		}
	}

}
