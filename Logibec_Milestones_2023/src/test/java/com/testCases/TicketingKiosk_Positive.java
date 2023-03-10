package com.testCases;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.baseClass.BaseClass_Logibec;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TicketingKiosk_Positive extends BaseClass_Logibec {
	public static WebDriverWait wait;

	@Test(groups ={"KioskPositive"},priority=1)
	public void launchingUrl() throws AWTException {
		firefoxBrowserConfig();
		hidingAddressbar();
//		UrlLaunch("http://192.168.1.211:7300/kiosk/system/MAC-AZR-137");
		wait=new WebDriverWait(driver, 20);

	}
	@Test(groups = {"KioskPositive"},priority=2)
	public void authentication() throws InterruptedException {
		
		if(driver.getPageSource().contains("Device Authentication")) {
			createTest=reports.createTest("Authenticating the current device");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href]"))).click();
			
			String parent = driver.getWindowHandle();
			Set<String> windowHandles = driver.getWindowHandles();
			Iterator<String> i1 = windowHandles.iterator();
			while(i1.hasNext()) {
				String child = i1.next();
				if(!parent.equals(child)) {
					driver.switchTo().window(child);
				}
			}
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username"))).sendKeys("msoft-admin");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password"))).sendKeys("Z\\mAPyc-3SYJ\\+$c");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("kc-login"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("kc-login"))).click();
			if(driver.getPageSource().contains("You may close this browser window and go back to your device.")) {
			createTest.log(Status.PASS, "Authentication is successfull for the current device");
			driver.close();
			}
			else {
				createTest.log(Status.PASS, "Authentication not successfull for the current device");
			}
			driver.switchTo().window(parent);
	}
	}
	
	@Test(groups ={"KioskPositive"},priority=3)
	public void kiosk_homeScreen(){
		testCreation("Kiosk home screen");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[text()='Press here']")));
		if(driver.getPageSource().contains("Press here")) {
			createTest.log(Status.PASS, "User can able to see the default message Press here to obtain a ticket on the home screen");
			driver.findElement(By.xpath("//h1[text()='Press here']")).click();
			createTest.log(Status.PASS, "User successfully pressed print option");
		}else {
			createTest.log(Status.FAIL, "User unable to see the default message Press here to obtain a ticket on the home screen");
		}

	}

	@Test(groups ={"KioskPositive"},priority=5)
	public void kiosk_printingScreen() throws InterruptedException, AWTException{
		testCreation("Kiosk printing screen");
		WebElement text=driver.findElement(By.xpath("//h1[text()='Please wait']"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[text()='Please wait']")));
		if(driver.getPageSource().contains("Please wait")) {
			createTest.log(Status.PASS, "User can able to see the message Please wait, your ticket will be printed soon");
		}else {
			createTest.log(Status.FAIL, "User unable to see the Please wait, your ticket will be printed soon");
		}
		sleeps();
		sleeps();
		takingScreenShot();
		sleeps();
	}
	@Test(groups ={"KioskPositive"},priority=4)
	public void VerifyingLogo_Kiosk() {
		createTest=reports.createTest("Logo display confirmation");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//img[contains(@src,'../../assets/images')])[1]")));
		boolean QuebecLogo = driver.findElement(By.xpath("(//img[contains(@src,'../../assets/images')])[1]")).isDisplayed();
		if(QuebecLogo==true) {
			createTest.log(Status.PASS, "Quebec logo is available");
		}else{
			createTest.log(Status.FAIL, "Quebec logo is not available");
		}

		boolean LogibecLogo = driver.findElement(By.xpath("(//img[contains(@src,'../../assets/images')])[2]")).isDisplayed();
		if(LogibecLogo==true) {
			createTest.log(Status.PASS, "Logibec logo is available");
		}else{
			createTest.log(Status.FAIL, "Logibec logo is not available");
		}
	}
	@Test(groups = {"KioskPositive"},priority=6)
	public void closingTheApplication() throws AWTException {
		createTest=reports.createTest("Validating application closing function by using ALT+F4");
		applicationClosing();
		createTest.log(Status.INFO,"The application is closed successfully by pressing ALT+F4");
	}

}

