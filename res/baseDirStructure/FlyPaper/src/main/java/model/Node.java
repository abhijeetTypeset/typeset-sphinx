package model;

import org.openqa.selenium.By;

public abstract class Node {
	public abstract By getIdentifier();
	public abstract String getName();
}
