package controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import utils.ConfigClass;

public class ActionClass extends ConfigClass {

	public void visit(String url) {
		driver.navigate().to(url);
	}

	public void goToHomePage() throws IOException {
		final URL url = new URL(driver.getCurrentUrl());
		final HttpURLConnection hurcon = (HttpURLConnection) url.openConnection();
		hurcon.setRequestMethod("GET");
		hurcon.connect();

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

	public void click(By locator, String elementNumber) // To click on a locator
	{
		int eNo = getElementNumber(elementNumber);
		System.out.println("Clicking " + locator.toString());
		final WebDriverWait wait = new WebDriverWait(driver, 15);
		final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		driver.findElements(locator).get(eNo).click();
	}

	public boolean contains(By locator, String expectedContent, String elementNumber) {
		if (locator == null) {
			return true;
		}
		int eNo = getElementNumber(elementNumber);
		String observedContent = driver.findElements(locator).get(eNo).getText();
		System.out.println("Obseved content in " + locator + "  is  " + observedContent);
		if (observedContent.toLowerCase().contains(expectedContent.toLowerCase())) {
			return true;
		}

		return false;
	}
	
	public boolean empty(By locator, String elementNumber) {
		if (locator == null) {
			return true;
		}
		int eNo = getElementNumber(elementNumber);
		String observedContent = driver.findElements(locator).get(eNo).getText();
		System.out.println("Obseved content in " + locator + "  is  " + observedContent);
		if (observedContent.length() == 0) {
			return true;
		}

		return false;
	}

	public boolean canSee(By locator, String elementNumber) {
		if (locator == null) {
			return true;
		}
		int eNo = getElementNumber(elementNumber);
		if (driver.findElements(locator).size() >= eNo) {
			System.out.println("Can see " + locator);
			return true;
		} else {
			System.out.println("Cannot see " + locator);
			return false;
		}

	}

	
	public void writeAtBegining(By locator, String data, String elementNumber) throws InterruptedException {
		int eNo = getElementNumber(elementNumber);
		final WebDriverWait wait = new WebDriverWait(driver, 15);
		data = substituteKeys(data);
		System.out.println("Typing "+data);
		
		final WebElement element = driver.findElements(locator).get(eNo);

		int length = element.getSize().getWidth();
		String [] movement = new String[length/2];
		for(int idx = 0; idx<length/2; idx++) {
			movement[idx] = Keys.ARROW_LEFT + "";
		} 
		new Actions(driver).moveToElement(element).sendKeys(movement).sendKeys(data).perform();
	}
	
	public void selectText(By locator, String elementNumber) throws InterruptedException {
		int eNo = getElementNumber(elementNumber);
		final WebDriverWait wait = new WebDriverWait(driver, 15);

		final WebElement element = driver.findElement(locator);

		final int length = element.getSize().getWidth();
		System.out.println("length : " + length);
		new Actions(driver).moveToElement(element).moveByOffset(-length / 2, 0).clickAndHold().moveByOffset(length, 0)
				.release().perform();
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

	private int getElementNumber(String elementNumber) {
		int action_no = 0;
		try {
			action_no = Integer.parseInt(elementNumber);
		} catch (Exception e) {
			System.out.println("Error parsing action _no");
		}
		return action_no;
	}

	public void type(By locator, String data, String elementNumber) throws InterruptedException {

		int eNo = getElementNumber(elementNumber);
		System.out.println("locator " + locator.toString());
		final WebDriverWait wait = new WebDriverWait(driver, 15);

		final WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
		driver.findElements(locator).get(eNo).click();
		waitForAWhile();

		data = substituteKeys(data);

		System.out.println("Type " + data);
		new Actions(driver).sendKeys(driver.findElements(locator).get(eNo), data).perform();

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
