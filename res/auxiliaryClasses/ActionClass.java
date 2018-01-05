package controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigClass;

public class ActionClass extends ConfigClass {

	public String attribute(By locator, String attrName) {
		final WebDriverWait wait = new WebDriverWait(driver, 15);

		final WebElement element = driver.findElement(locator);

		return element.getAttribute(attrName);

	}

	public String textContent(By locator) {
		return driver.findElement(locator).getText();
	}

	public boolean contains(By locator, String expectedContent) {
		if (locator == null) {
			return true;
		}
		String observedContent = driver.findElement(locator).getText();
		System.out.println("Obseved content in " + locator + "  is  " + observedContent);
		if (observedContent.toLowerCase().contains(expectedContent.toLowerCase())) {
			return true;
		}

		return false;
	}

	public void waitTime(int time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean canSee(By locator) {
		if (locator == null) {
			return true;
		}
		if (driver.findElements(locator).size() > 0) {
			System.out.println("Can see " + locator);
			return true;
		} else {
			System.out.println("Cannot see " + locator);
			return false;
		}

	}

	public void visit(String url) {
		driver.navigate().to("https://typeset.io/accounts/login/");
	}

	public boolean atPage(String url) {
		String pageUrl = driver.getCurrentUrl().replace("https://", "").replace("http://", "").replace("www.", "");
		if (pageUrl.startsWith(url)) {
			System.out.println("At page " + url);
			return true;
		} else {
			System.out.println("Not at page " + url + " X " + pageUrl);
			return false;
		}
	}

	public void goToHomePage() throws IOException {
		final URL url = new URL(driver.getCurrentUrl());
		final HttpURLConnection hurcon = (HttpURLConnection) url.openConnection();
		hurcon.setRequestMethod("GET");
		hurcon.connect();

	}

	public void click(By locator) // To click on a locator
	{
		System.out.println("Clicking " + locator.toString());
		final WebDriverWait wait = new WebDriverWait(driver, 15);
		final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		// element.click();
		driver.findElement(locator).click();

	}

	public WebElement element(By locator) {
		WebElement element;
		try {
			waitForElementVisible(locator);
			element = driver.findElement(locator);
		} catch (final NoSuchElementException e) {
			throw new NoSuchElementException("No Such Element present  " + e.getLocalizedMessage());
		}

		return element;
	}

	public String getAllAttributes(WebElement element) {
		final JavascriptExecutor executor = (JavascriptExecutor) driver;
		final Object aa = executor.executeScript(
				"var items = {}; for (index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value }; return items;",
				element);
		System.out.println(aa.toString());
		return aa.toString();
	}

	public void keyBoardEvent(By locator, Keys event) {

		driver.findElement(locator).sendKeys(event);

	}

	public void Mousehovr(By hoverlocator, By clickLocator) {

		final Actions builder = new Actions(driver);
		builder.moveToElement(driver.findElement(hoverlocator)).perform();
		waitForElementVisible(clickLocator);
		driver.findElement(clickLocator).click();

	}

	public void selectText(By locator) throws InterruptedException {
		final WebDriverWait wait = new WebDriverWait(driver, 15);

		final WebElement element = driver.findElement(locator);

		final int length = element.getSize().getWidth();
		System.out.println("length : " + length);
		new Actions(driver).moveToElement(element).moveByOffset(-length / 2, 0).clickAndHold().moveByOffset(length, 0)
				.release().perform();

	}

	public void otherType(By locator, String data) throws InterruptedException {
		System.out.println("locator " + locator.toString());
		System.out.println("Type " + data);

		final WebDriverWait wait = new WebDriverWait(driver, 15);
		final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		try {
			driver.findElement(locator).click();
			waitForAShortWhile();
		} catch (final InvalidElementStateException e) {
			System.out.println("Exception while clearing");
			waitForALongWhile();
		}

		driver.findElement(locator).sendKeys(data);
	}

	public String substituteKeys(String textData) {

		while (textData.contains("\\n")) {
			textData = textData.replace("\\n", Keys.RETURN);
		}

		while (textData.contains("\\t")) {
			textData = textData.replace("\\n", Keys.TAB);
		}

		return textData;

	}

	public void type(By locator, String data) throws InterruptedException {

		final WebDriverWait wait = new WebDriverWait(driver, 15);

		final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		driver.findElement(locator).click();
		waitForAWhile();

		data = substituteKeys(data);

		System.out.println("locator " + locator.toString());
		System.out.println("Type " + data);
		new Actions(driver).sendKeys(driver.findElement(locator), data).perform();

	}

	public void waitForALongWhile() {
		try {
			Thread.sleep(20000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void waitForAShortWhile() {
		try {
			Thread.sleep(2000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void waitForAWhile() {
		try {
			Thread.sleep(5000);
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void waitForElementVisible(By locator) // Waits for an element to be
	{
		try {
			final WebDriverWait wait = new WebDriverWait(driver, 40);
			wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(locator)));
		} catch (final TimeoutException e) {
			throw new TimeoutException("Error message:  " + e.getMessage());
		}

	}

	public void waitForPage(String urlFraction) // Waits for an element to be
	{
		try {
			final WebDriverWait wait = new WebDriverWait(driver, 40);
			wait.until(ExpectedConditions.urlContains(urlFraction));
		} catch (final TimeoutException e) {
			throw new TimeoutException("Error message:  " + e.getMessage());
		}

	}
}
