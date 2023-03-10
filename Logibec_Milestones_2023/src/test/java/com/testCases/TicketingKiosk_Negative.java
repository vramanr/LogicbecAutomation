package com.testCases;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
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

public class TicketingKiosk_Negative extends BaseClass_Logibec {
	
	public static WebDriverWait wait;

	@Test(groups ={"KioskNegative"},priority=1)
	public void launchingUrl() throws AWTException {
		firefoxBrowserConfig();
		hidingAddressbar();
//		UrlLaunch("http://192.168.1.211:7300/kiosk/system/MAC-AZR-138");
		wait=new WebDriverWait(driver, 20);

	}
	@Test(groups = {"KioskNegative"},priority=2)
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
	
	
	@Test(groups ={"KioskNegative"},priority=3)
	public void kioskHomePage() throws AWTException {
		createTest=reports.createTest("Negative sceneario");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='col'][2]/child::h3")));
		if(driver.getPageSource().contains("Ce poste n'est pas configur� comme �tant une borne de billetterie (Voir table de configuration des bornes).")) {
			createTest.log(Status.PASS, "Kiosk throws an error when the request came from unconfigured server");
		}else {
			createTest.log(Status.FAIL, "Kiosk is not thrown an error when the request came from unconfigured server");
		}
		applicationClosing();
}}
