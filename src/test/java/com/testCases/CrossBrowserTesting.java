package com.testCases;


import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.common.ValidationContext;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.server.handler.interactions.SendKeyToActiveElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Sleeper;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.baseClass.BaseClass_Logibec;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CrossBrowserTesting extends BaseClass_Logibec {

	String DescFN="testFN";
	String DescEN="testEN";
	String validCode ="1609";
	String invalidCode="73959666962";
	String alphanumericCode ="TEST7395";
	String editCode="999";
	String restoreCode="R11";


	@Parameters("Browser")
	@Test(groups = {"Common"})
	public void urlLaunch(String Browser) throws AWTException, InterruptedException, IOException {
	
		createTest=reports.createTest("Application Login");
		
		if(Browser.equalsIgnoreCase("firefox")) {			
			firefoxBrowserConfig();
		}else if(Browser.equalsIgnoreCase("edge")){
			edgeBrowserConfig();
		}else if(Browser.equalsIgnoreCase("chrome")) {		
			chromeBrowserConfig();
		}
		UrlLaunch();
		wait=new WebDriverWait(driver, 20);
		windowMaximize();
		sleeps();
		//zoomOutScreen();
		applicationLoginCredentials();
		
		if(driver.getPageSource().contains("Changer le rôle ou l'unité")| driver.getPageSource().contains("Choice of user role in system"))
		{
			driver.findElement(By.xpath("(//button[text()='MEDIAMED'])[2]")).click();
			sleeps();
		}
		sleeps();
		createTest.log(Status.PASS,"Successfully logged into the application");
		
	}

	void navigateToClinicalServices() throws InterruptedException
	{
		navigateToPredefinedTables();
		WebElement clinicalService = driver.findElement(By.xpath("//div[contains(text(),' Service clinique')] | //div[text()=' Clinical Services']"));
		clinicalService.click();
		sleeps();
		sleeps();

		sleeps();
		sleeps();

		if(driver.findElement(By.xpath("//h2[contains(text(),'Gestion des tables locales - Service clinique')] | //h2[contains(text(),'Management of local tables - Clinical Services')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"'Management of local tables - Clinical Services’ pop up is shown when clicked on the menu item Clinical Services");
			WebElement popup = driver.findElement(By.xpath("//h2"));
			verifyText(popup,"Clinical Services Popup","Management of local tables - Clinical Services", "Gestion des tables locales - Service clinique");
			WebElement popupmsg = driver.findElement(By.xpath("//p"));
			verifyText(popupmsg,"Clinical Services","The codes of this table are defined in the normative framework and cannot be modified directly. You can make minor corrections of the descriptions or render certain codes non-available if necessary (clinical services not present)", "Les codes de cette table sont définis dans le cadre normatif et ne peuvent pas être modifiés directement. Vous pouvez effectuer les corrections mineures des descriptions ou rendre non disponibles certains codes s’il y a lieu (services cliniques non présents, etc.)");
			clickOKButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"'Gestion des tables locales - Service clinique’ pop up is not shown when clicked on the menu Clinical Services");

		}
		sleeps();
	}

	@Test(groups = {"ClinicalService"},priority=1)
	void clinicalServicesValidation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "Clinical Services";
		String pageNameFrench = "Service clinique";

		createTest=reports.createTest("Verifying Clinical Services");

		navigateToClinicalServices();
		sleeps();
		sleeps();
		sleeps();
		sleeps();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter("0003",1);
		driver.findElement(By.xpath("//span[text()='0003']")).click();
		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='0003']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Code column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Code column is editable");
		}

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][2]")).click();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseDeleteKey();
		sleeps();

		//driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][1]")).sendKeys("testFR");
		pressAndReleaseAKey();
		pressAndReleaseTabKey();
		pressAndReleaseDeleteKey();
		//driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][1]")).sendKeys("testEN");
		pressAndReleaseAKey();

		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		if(!driver.getPageSource().contains(emergencyUnitFrench))
		{

			if(driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+"deb"+"')] ")).isDisplayed())
			{

				if(driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+"3e"+"')] ")).isDisplayed())
				{
					if(driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+"4e"+"')]")).isDisplayed())
					{
						createTest.log(Status.PASS,"Only MED-Soins+Units 3e, 4e, deb are displayed in "+pageNameEnglish+" when these units are active in Unit table, whereas emergency and Other units are not displayed");

					}
					else
					{
						createTest.log(Status.FAIL,"4e Units don't exist in Type");
					}


				}
				else
				{
					createTest.log(Status.FAIL,"3e Units don't exist in Type");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"deb Unit Type don't exists");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Urg type exists");
		}
		pressAndReleaseEnterKey();
		clickRestoreButton();
		sleeps();
		if(verifyButtonDisabled(createButton, "Create Button"))
		{

			if(verifyButtonDisabled(deleteButton, "Delete Button"))
			{
				createTest.log(Status.PASS,"Create and  Delete are disabled, even after row was edited");
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create and  Delete are enabled, even after row was edited");

		}
		clickSaveButton();
		filter("Dermatologie",2);
		if(driver.getPageSource().contains("Dermatologie"))
		{
			createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
			createTest.log(Status.PASS,"User is able to update the values in all the columns except Code column");
			createTest.log(Status.PASS,"Save Button is enabled when row was edited");
			createTest.log(Status.PASS,"Row is successfully edited");
		}
		else
		{
			createTest.log(Status.FAIL,"Restore Button is not enabled when existing record is edited");
			createTest.log(Status.FAIL,"User is not able to update the values in all the columns except Code column");
			createTest.log(Status.FAIL,"Save Button is not enabled when row was edited");
			createTest.log(Status.FAIL,"Row is not successfully edited");
		}
		clickAllFilter();
		if(driver.getPageSource().contains("Dermatologie"))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains("Dermatologie"))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains("Dermatologie"))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();
					sleeps();


					if(!driver.getPageSource().contains("Dermatologie"))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made  inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains("Dermatologie"))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains("Dermatologie"))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains("Dermatologie"))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();
					sleeps();


					if(!driver.getPageSource().contains("Dermatologie"))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();

						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Service clinique | Management of local tables - Clinical Services");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
	}


	
	//***********************************Users Screen**********************************************************************************************
	

	public void navigateToUsers() throws InterruptedException
	{
        navigateToManagementMenu();
		
		WebElement users = driver.findElement(By.xpath("//div[text()=' Users'] | //div[text()=' Utilisateurs']"));
		users.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}
	
	
	public void verifyRole (int columnIndex, String columnNameFrench,String columnNameEnglish)
	{
		String columnName = driver.findElement(By.xpath("((//mat-radio-group//mat-radio-button)["+columnIndex+"]//span)[6]")).getText();
		{
			if((columnName.equals(columnNameFrench))||(columnName.equals(columnNameEnglish)))
			{
				createTest.log(Status.PASS,"Role at index "+columnIndex+" is correct : "+columnNameEnglish+" | "+columnNameFrench);
			}
			else
			{
				createTest.log(Status.FAIL,"Role of index "+columnIndex+" is  not correct ");
				

			}
		}
		
	}
	String UserName = "un01";
	String fName = "TestFN";
	String lName = "TestLN";
	String defaultPassword = "123";
	String newPassword = "macro02";
	String Users_pageNameEnglish ="Users";
	String Users_pageNameFrench = "Utilisateurs";
	
	
	
	@Test(groups = {"Users"},priority=1)
	public void createUsersValidation() throws InterruptedException, AWTException 
	{
	
	createTest=reports.createTest("Verifying Create New User");	


	navigateToUsers(); 
	Thread.sleep(4000);

	
	verifyBreadCrumb(Users_pageNameEnglish, Users_pageNameFrench);
	
	//Verify restore, delete, save an continue buttons are disabled
	WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
	WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
	WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
	WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Save')] | //button[contains(text(),'Sauvegarder')]"));
	
	//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
               
               
               	
               	createTest.log(Status.PASS,"Create and Delete Buttons are enabled and User Access,Restore and Save buttons are disabled by deafult");
               				
					}
					else
					{
               createTest.log(Status.FAIL,"Save  button is not disabled by default");
               
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");
					
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");
				
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");
			
		}
		
		filter(UserName,1);
		if(driver.getPageSource().contains(UserName))
		{
			driver.findElement(By.xpath("//span[text()='"+UserName+"']")).click();
			clickDeleteButton();
			
			
			clickSaveButton();
			Thread.sleep(15000);
		}
		
		filter("abcfgthyui",1);
		if(driver.getPageSource().contains("abcfgthyui"))
		{
			driver.findElement(By.xpath("//span[text()='abcfgthyui']")).click();
			clickDeleteButton();
			clickSaveButton();
			Thread.sleep(15000);
		}
		
		filter("",1);
		sleeps();
		clickCreateButton();
		
		
		if(verifyButtonDisabled(saveButton, "Save Button"))
		{
			
			createTest.log(Status.PASS,"Save Button is disabled when no details are entered");
		}
		else
		{
			createTest.log(Status.FAIL,"Save Button is not disabled when no details are entered");				
		}	
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("abcfgthyuiiouy");
		filter("abcfgthyuiiouy",1);
		if(!driver.getPageSource().contains("abcfgthyuiiouy") )
		{	sleeps();
			filter("abcfgthyui",1);
			sleeps();
			if(driver.getPageSource().contains("abcfgthyui") )
			{
				createTest.log(Status.PASS,"Verified User is not able to enter Username with more than 10 characters");
				sleeps();
				driver.findElement(By.xpath("//span[text()='abcfgthyui']")).click();
				sleeps();
				clickDeleteButton();
				sleeps();
				clickSaveButton();
				Thread.sleep(8000);
				filter("",1);
			}
		}
		else
		{
			createTest.log(Status.FAIL," Failed to Verify User is not able to enter Username with more than 10 characters");
		}
		
		
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(UserName);
//		sleeps();
//		pressAndReleaseTabKey();
//		clickSaveButton();
////		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
////		{	
////			System.out.println("Verified UserName alone is not mandatory in "+pageNameEnglish);
////			createTest.log(Status.PASS,"Verified UserName alone is not mandatory in "+pageNameEnglish);
////			createTest.log(Status.PASS,"Warning Message when Code only enetered in "+pageNameEnglish+": La commande d’extraction et/ou de sauvegarde de données a échoué. | Data extraction and/or saving command failed.");
////			WebElement warningMessage = driver.findElement(By.xpath("//mat-dialog-content//p"));
////			verifyText(warningMessage,"Warning Message text when record created with only Code in "+pageNameEnglish+"",""","La conversion d'un type de données varchar en type de données smalldatetime a créé une valeur hors limites.");
////			clickOKButton();
////		}
////		else
////		{
////			System.out.println("Failed to verify warning when UserName only given in "+pageNameEnglish);
////			createTest.log(Status.FAIL,"Failed to verify warning when Code only given in "+pageNameEnglish);
////		}
//		
//
//		filter(UserName,1);
		
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(fName);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(lName);
		clickSaveButton();
		
		if(driver.getPageSource().contains(UserName) )
		{	
			createTest.log(Status.PASS,"Successfully created User");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create UserName");
		}
		sleeps();	
		
		

		
//		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		//Verify password reset window
		logout();
		
		sleeps();
		sleeps();
		login(UserName,defaultPassword);
		
		if((driver.getPageSource().contains("Le changement de mot de passe est exigé")) | (driver.getPageSource().contains("Password change is required")))
		{
			createTest.log(Status.PASS,"Verified password change required window is shown for first login");
			
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify password change required window");

		}
		sleeps();
		clickOKButton();
		sleeps();
		if((driver.getPageSource().contains("Changement du mot de passe")) | (driver.getPageSource().contains("Password change")))
		{
			createTest.log(Status.PASS,"Password Change Window is displayed");
		
		
			driver.findElement(By.xpath("//input[@formcontrolname='oldPswrd']")).sendKeys(defaultPassword);
			driver.findElement(By.xpath("//input[@formcontrolname='new']")).sendKeys(newPassword);
			driver.findElement(By.xpath("//input[@formcontrolname='repeat']")).sendKeys(newPassword);
			
			driver.findElement(By.xpath("//button[contains(text(),'OK')]")).click();
			sleeps();
			
			if((driver.getPageSource().contains("Le mot de passe a été changé.")) | (driver.getPageSource().contains("The password has been changed.")))
			{
					driver.findElement(By.xpath("//button//span[contains(text(),'Ok')]")).click();
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify password change succesful window");

			}
			sleeps();
			login(UserName,newPassword);
			sleeps();
			sleeps();
			sleeps();
			sleeps();
			sleeps();
			sleeps();
//		!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!			
			if((driver.getPageSource().contains(" Erreur")) | (driver.getPageSource().contains(" Error")))
			{
				createTest.log(Status.PASS,"Verified new user is not allowed to login without roles allocated");
				clickOKButton();
				sleeps();
				
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify password change succesful window");

			}
			
			
	}	
		

	
		
	}
	
	@Parameters("Browser")
	//Unit and Role Allocation of Users
	@Test(groups = {"Users"},priority=2)
	void UnitAndRoleAllocation(String Browser) throws AWTException, InterruptedException, IOException
	{
		
		createTest=reports.createTest("Verifying Role Allocation and Unit");	
		applicationLoginCredentials();
		navigateToUsers();
		sleeps();
		if(driver.findElement(By.xpath("//h3[text()='Unité'] | //h3[text()='Unit']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Verified Add Unit section is displayed in the screen");
		
		}
		else
		{
			createTest.log(Status.FAIL,"Verified Add Unit section is not displayed in the screen");

		}
		
	
	
	filter(UserName,1);
	driver.findElement(By.xpath("//span[text()='"+UserName+"']")).click();
	driver.findElement(By.xpath("//button[contains(text(),'Add unit')] | //button[contains(text(),'Ajouter unité')]")).click();
	sleeps();
	if((driver.getPageSource().contains("Unité par utilisateur")) | (driver.getPageSource().contains("unit by user")) )
	{	
		createTest.log(Status.PASS,"Verified Add Unit window is opened when clicked on Add Unit button");
	}
	else
	{
		createTest.log(Status.FAIL,"Failed to verify Add Unit window is opened when clicked on Add Unit button");
	}
	
	WebElement createButton1 = driver.findElement(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]"));
	createButton1.click();
	sleeps();
	
	if((driver.getPageSource().contains("Choix de l'unité")) | (driver.getPageSource().contains("Choice of unit")) )
	{	
		createTest.log(Status.PASS,"Verified Choice of unit window is opened when clicked on Create Unit button");
	}
	else
	{
		createTest.log(Status.FAIL,"Failed to verify Verify Choice of unit window is opened when clicked on Create Unit button");
	}
	
	
	//Add all units
	driver.findElement(By.xpath("//span[text()='urg']//parent::div//parent::div//parent::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='deb']//parent::div//parent::div//parent::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='4e']//parent::div//parent::div//parent::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='3e']//parent::div//parent::div//parent::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//button[contains(text(),'OK')]")).click();
	//Thread.sleep(7000);
	clickSaveAndCloseButton();
	Thread.sleep(3000);
	
	driver.findElement(By.xpath("(//mat-select)[2]")).click();
	if(driver.findElement(By.xpath("(//mat-option)//span[text()='3e']")).isDisplayed())
	{
		if(driver.findElement(By.xpath("(//mat-option)//span[text()='4e']")).isDisplayed())
		{
			if(driver.findElement(By.xpath("(//mat-option)//span[text()='deb']")).isDisplayed())
			{
				if(driver.findElement(By.xpath("(//mat-option)//span[text()='urg']")).isDisplayed())
				{
					createTest.log(Status.PASS,"Units selected are successfully displaying in Unit Dropdown");
				}
			}
		}
		

	}
	else
	{
		createTest.log(Status.FAIL,"Unit is not successfully added to user");

	
	}
	
	//Select 3e
	driver.findElement(By.xpath("(//mat-option)//span[text()='3e']")).click();
	
	
	//Verify Role allocation section is displayed
	if(driver.findElement(By.xpath("//h3[text()='Attribution de rôle'] | //h3[text()='Role allocation']")).isDisplayed())
	{
		createTest.log(Status.PASS,"Verified Role Allocation section is displayed in the screen");

	}
	else
	{
		createTest.log(Status.FAIL,"Verified ROle Allocation section is not displayed in the screen");

	}
	
		
	driver.findElement(By.xpath("//span[text()='MEDIAMED']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='PILOTE']//parent::div//parent::div//following-sibling::div//input | //span[text()='PILOT']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='SUPER UTILISATEUR']//parent::div//parent::div//following-sibling::div//input | //span[text()='SUPERUSER']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='AUTRE']//parent::div//parent::div//following-sibling::div//input | //span[text()='OTHER']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	
	clickSaveButton();
	sleeps();
	
	//Select urg
	driver.findElement(By.xpath("(//mat-select)[2]")).click();
	driver.findElement(By.xpath("(//mat-option)//span[text()='urg']")).click();
	
	
	driver.findElement(By.xpath("//span[text()='MEDIAMED']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='PILOTE']//parent::div//parent::div//following-sibling::div//input | //span[text()='PILOT']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='SUPER UTILISATEUR']//parent::div//parent::div//following-sibling::div//input | //span[text()='SUPERUSER']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	driver.findElement(By.xpath("//span[text()='AUTRE']//parent::div//parent::div//following-sibling::div//input | //span[text()='OTHER']//parent::div//parent::div//following-sibling::div//input")).click();
	pressAndReleaseSpaceKey();
	
	
	clickSaveButton();
	sleeps();
	
	//Select 3e
	driver.findElement(By.xpath("(//mat-select)[2]")).click();
	driver.findElement(By.xpath("(//mat-option)//span[text()='3e']")).click();
	sleeps();
	
	//verify User Access Button
	driver.findElement(By.xpath("//button[contains(text(),'User access')] | //button[contains(text(),'Accès Utilisateur')]")).click();
	Thread.sleep(4000);
	if((driver.getPageSource().contains("Gestion des accès par utilisateur")) | (driver.getPageSource().contains("Access management by user")) )
	{	
		createTest.log(Status.PASS,"User Access window is opened when User Access Button was clicked");
	}
	else
	{
	
		createTest.log(Status.FAIL,"Failed to verify - User Access window is opened when User Access Button was clicked");

	}
	
	//make intervener RO and Doctors Restricted
	driver.findElement(By.xpath("//span[text()='PM - Print management'] | //span[contains(text(),'PG - Gestionnaire ')]")).click();
	driver.findElement(By.xpath("((//span[contains(text(),'400')])//parent::div//parent::div//parent::div//following-sibling::div[1]//img)[1]")).click();
	//enable below after bug fix in role screen
	if(driver.findElement(By.xpath("((//span[contains(text(),'400')])//parent::div//parent::div//parent::div//following-sibling::div[1]//img[@src='assets/images/ck1.gif'])[1]")).isDisplayed())
	{
		createTest.log(Status.PASS,"Verified tick mark appeared when RO was clicked for Interveners");
		//driver.findElement(By.xpath("(//span[text()='Interveners'] | //span[text()='Intervenants'])//parent::div//parent::div//following-sibling::div[1]//img")).click();
		
	}
	else
	{
		createTest.log(Status.FAIL,"Failed to Verify tick mark appeared when RO was clicked for Interveners");

	}
	
//	driver.findElement(By.xpath("(//span[text()='Stretcher / Bed categories'] | //span[text()='Catégories des civières / lits'])//parent::div//parent::div//following-sibling::div[2]//img")).click();
//	sleeps();
//	//enable below after bug fix in role screen
//	if(driver.findElement(By.xpath("(//span[text()='Stretcher / Bed categories'] | //span[text()='Catégories des civières / lits'])//parent::div//parent::div//following-sibling::div[2]//img[@src='assets/images/cancel.gif']")).isDisplayed())
//	{
//		createTest.log(Status.PASS,"Verified cross mark appeared when RA was clicked for Stretcher Bed");
//		driver.findElement(By.xpath("(//span[text()='Stretcher / Bed categories'] | //span[text()='Catégories des civières / lits'])//parent::div//parent::div//following-sibling::div[2]//img")).click();
//		
//	}
//	else
//	{
//		createTest.log(Status.FAIL,"Failed to verify cross mark appeared when RA was clicked for Interveners");
//
//	}
	clickSaveButton();
	sleeps();
	driver.findElement(By.xpath("//a[contains(text(),'Utilisateurs')] | //a[contains(text(),'Users')] ")).click();
	sleeps();
	if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
	{
		clickYesButton();
	}
	logout();
	
	
	login(UserName,newPassword);
	sleeps();
	sleeps();
	sleeps();
	
	try {
		if((driver.getPageSource().contains("Changing roles or units")) | (driver.getPageSource().contains("Choix du rôle")))
		{
			createTest.log(Status.PASS,"verified change role window is shown when multiple roles are assigned");
			driver.findElement(By.xpath("(//button[text()='MEDIAMED'])[2]")).click();
		}
		
	} catch (Exception e) {
		// TODO: handle exception
	}
	
	sleeps();
	sleeps();
	
	navigateToUsers();
	Thread.sleep(3000);
	String pageName = driver.findElement(By.xpath("//li[@class='breadcrumb-item active']")).getText();
	if((pageName.equals(Users_pageNameEnglish)) || (pageName.equals(Users_pageNameFrench)))
	{
		createTest.log(Status.PASS,"User is succesfully logged in, Roles were assigned and password was reset");
	
	

	}
	
	filter(UserName,1);
	//Make User inactive
	driver.findElement(By.xpath("//span[text()='"+UserName+"']")).click();
	sleeps();
	pressAndReleaseTabKey();
	sleeps();
	pressAndReleaseSpaceKey();
	sleeps();
	clickSaveButton();
	Thread.sleep(3000);
	logout();
	login(UserName,newPassword);
	sleeps();
	if((driver.getPageSource().contains("Saisie de mot de passe")) | (driver.getPageSource().contains("User code is expired or disabled")))
	{
		createTest.log(Status.PASS,"Verified inactive user is not allowed to login");
		clickOKButton();
		
	}
	
	
	//delete user
	applicationLoginCredentials();
	navigateToUsers();
	Thread.sleep(4000);
	filter(UserName,1);
	driver.findElement(By.xpath("//span[text()='"+UserName+"']")).click();
	clickDeleteButton();
	clickSaveButton();
	Thread.sleep(8000);
	filter(UserName,1);
	sleeps();
	if(!driver.getPageSource().contains(fName))
	{
		createTest.log(Status.PASS,"Filter and Deletion is working fine");

	}
	else
	{
		createTest.log(Status.FAIL,"Failed to Verify filter and deletion");

	}
	verifyClearFilter();
	clickHome();
	 if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
     {
     	createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
     	clickYesButton();
     	Thread.sleep(2000);
	}
	 if((driver.getPageSource().contains("Warning"))|(driver.getPageSource().contains(" Avertissement")) )
     {
     	createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
     	clickOKButton();
     	Thread.sleep(4000);
	}

	}
	//*******************************************************ControlPoints************************************************************************************
	public void navigateToControlPoints() throws InterruptedException
	{
		navigateToManagementMenu();

		WebElement controlPoint = driver.findElement(By.xpath("//div[text()=' Points de contrôle'] | //div[text()=' Control points']"));
		controlPoint.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}

	//Unit and ROle Allocation of Users
	@Test(groups = {"ControlPoints"},priority=4)
	public void controlPointScreenValidation() throws AWTException, InterruptedException
	{
		createTest=reports.createTest("Verifying Control Points Screen");
		
		//zoomOutScreen();
		navigateToControlPoints();
		sleeps();
		driver.findElement(By.xpath("//span[text()='Portal - Management(PM)'] | //span[text()='Portail - Gestion(PG)']")).click();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[1]//span")).click();
		if(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[1]//span")).isDisplayed())
		{
			createTest.log(Status.PASS,"Verified Code column is not editable");
		}
		else
		{
			createTest.log(Status.FAIL,"Faile to verify  Code column is not editable");

		}


		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[2]//span")).click();
		if(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[2]//span")).isDisplayed())
		{
			createTest.log(Status.PASS,"Verified Description column is not editable");
		}
		else
		{
			createTest.log(Status.FAIL,"Faile to verify  Description column is not editable");

		}
		
		driver.findElement(By.xpath("//button[text()='Tout sélectionner'] | //button[text()='Select all']")).click();
		clickSaveButton();
		System.out.println("before = "+driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[3]//input[@type='checkbox']")).getAttribute("checked"));
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[3]//input")).click();
		pressAndReleaseSpaceKey();
		System.out.println(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[3]//input[@type='checkbox']")).getAttribute("checked"));
		if(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[3]//input[@type='checkbox']")).getAttribute("checked")==null)
		{
			createTest.log(Status.PASS,"Verified Active column is a checkbox and is editable");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Active column is a checkbox and is editable");

		}
		
		clickRestoreButton();
		sleeps();
		if(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[3]//input[@type='checkbox']")).getAttribute("checked") != null)
		{
			createTest.log(Status.PASS,"Restore button is working fine");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Restore button");

		}
		
		//Deselect ALl
		driver.findElement(By.xpath("//button[text()='Tout désélectionner'] | //button[text()='Unselect all']")).click();
		sleeps();
		if(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[3]//input[@type='checkbox']")).getAttribute("checked") == null)
		{
			createTest.log(Status.PASS,"Verified Deselect All button is working fine");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Deselect All button is working fine");
		}

		//Select All
		driver.findElement(By.xpath("//button[text()='Tout sélectionner'] | //button[text()='Select all']")).click();
		sleeps();
		if(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[3]//input[@type='checkbox']")).getAttribute("checked") != null)
		{
			createTest.log(Status.PASS,"Verified Select All button is working fine");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Select All button is working fine");
		}
		clickSaveButton();
		
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));
		if(verifyButtonDisabled(saveButton,"Save Button"))
		{
			createTest.log(Status.PASS,"Save Button is disabled once changes are saved and is working fine");

		}
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
			Thread.sleep(4000);
		}
	}

	//**********************************************************UsersDoctorImport****************************************************************

	void navigateToUsersDoctorsImport() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement usersDoctorsImport = driver.findElement(By.xpath("//div[text()=' Users/Doctors import'] | //div[text()=' Importation utilisateurs/médecins']"));
		usersDoctorsImport.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}

	@Test(groups = {"UsersDoctorsImport"},priority=5)
	void usersDoctorsImportValidation() throws InterruptedException, AWTException
	{
		String screenNameFR= "Gestion des tables locales - Importation utilisateurs/médecins";
		String screenNameEN="Management of local tables - Users/Doctors import";

		createTest=reports.createTest("Users Doctors Import Screen UI Validation");	
		

		navigateToUsersDoctorsImport();
		
		sleeps();
		sleeps();
		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)) )
		{
			createTest.log(Status.PASS,"Successfully navigated to "+screenNameFR+" Screen");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameFR+" Screen");

		}

		WebElement openButton = driver.findElement(By.xpath("//label[contains(text(),'Ouvrir ')] | //label[contains(text(),'Open ')]"));
		WebElement closeButton = driver.findElement(By.xpath("//input[contains(@value,'Fermer')] | //input[contains(@value,'Close')]"));
		WebElement viewErrorsButton = driver.findElement(By.xpath("//input[contains(@value,'Visualiser les erreurs')] | //input[contains(@value,'View errors')]"));
		WebElement importFileButton = driver.findElement(By.xpath("//button[contains(text(),'Importer le fichier')] | //button[contains(text(),'Import the file')]"));
		WebElement fileValidationButton = driver.findElement(By.xpath("//button[contains(text(),'Valider le fichier')] | //button[contains(text(),'File validation')]"));

		if(verifyButtonEnabled(openButton,"Open Button"))
		{
			if(verifyButtonEnabled(closeButton,"Close Button"))
			{
				if(verifyButtonDisabled(viewErrorsButton,"View Errors Button"))
				{
					if(verifyButtonDisabled(importFileButton,"Import File Button"))
					{
						if(verifyButtonDisabled(fileValidationButton,"File Validation Button"))
						{
							createTest.log(Status.PASS,"Open ans Close buttons are enabled and other buttons are disabled by default");

						}
						else
						{
							createTest.log(Status.FAIL,"Failed to verify File Validation Button is disabled");
						}
					}
					else
					{
						createTest.log(Status.FAIL,"Failed to verify Import FIle Button is disabled");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify View Errors Button is disabled");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify Close Button is disabled");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify open Button is disabled");
		}
		
		//Upload a wrong file and validate
				WebElement chooseFile = driver.findElement(By.id("fileUploader"));
				chooseFile.sendKeys(System.getProperty("user.dir")+"\\src\\test\\resources\\UsersImportError.xlsx");

				driver.findElement(By.xpath("//mat-select")).click();
				if(verifyDropDownValuePresent("Médecin","Doctor"))
				{
					if(verifyDropDownValuePresent("Utilisateurs","Users"))
					{
						createTest.log(Status.PASS,"Verified Type of Import Dropdown Options are Doctor and Users");

					}
					else
					{
						createTest.log(Status.FAIL,"Failed to verify dropdown option users");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify dropdown option doctor");

				}

				driver.findElement(By.xpath("//mat-option//span[contains(text(),'Utilisateurs')] | //mat-option//span[contains(text(),'Users')]")).click();
				fileValidationButton.click();
				sleeps();
				if((driver.getPageSource().contains("Erreur de chargement grave")) | (driver.getPageSource().contains("Serious loading error")))
				{
					createTest.log(Status.PASS,"Error Window was opened when wrong file was tried to import");
					clickOKButton();

				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verifiy Error Window was opened when wrong file was tried to import");

				}

		/*//Upload a wrong file and validate
		WebElement chooseFile = driver.findElement(By.id("fileUploader"));
		chooseFile.sendKeys(System.getProperty("user.dir")+"\\src\\test\\resources\\UsersImportError.xlsx");

		driver.findElement(By.xpath("//mat-select")).click();
		if(verifyDropDownValuePresent("Médecin","Doctor"))
		{
			if(verifyDropDownValuePresent("Utilisateurs","Users"))
			{
				createTest.log(Status.PASS,"Verified Type of Import Dropdown Options are Doctor and Users");

			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify dropdown option users");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify dropdown option doctor");

		}

		driver.findElement(By.xpath("//mat-option//span[contains(text(),'Utilisateurs')] | //mat-option//span[contains(text(),'Users')]")).click();
		fileValidationButton.click();
		sleeps();
		if((driver.getPageSource().contains("Importation utilisateurs/médecins  - ERREUR")) | (driver.getPageSource().contains("Users/Doctors import  - ERROR")))
		{
			createTest.log(Status.PASS,"Error Window was opened when wrong file was tried to import");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verifiy Error Window was opened when wrong file was tried to import");

		}
		WebElement closeButton1 = driver.findElement(By.xpath("(//input[contains(@value,'Fermer')] | //input[contains(@value,'Close')])[2]"));
		WebElement exportToExcelButton = driver.findElement(By.xpath("//input[contains(@value,'Exportation en Excel')] | //input[contains(@value,'Export to Excel')]"));
		WebElement printButton = driver.findElement(By.xpath("//button[contains(text(),'Imprimer')] | //button[contains(text(),'Print')]"));

		if(verifyButtonEnabled(closeButton1, "Close Button"))
		{
			if(verifyButtonEnabled(exportToExcelButton, "Close Button"))
			{
				if(verifyButtonEnabled(printButton, "Close Button"))
				{
					createTest.log(Status.PASS,"Close, Export to Excel and Print Buttons are enabled in import error window");

				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify - Close, Export to Excel and Print Buttons are enabled in import error window");

				}


			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify - Close, Export to Excel and Print Buttons are enabled in import error window");

			}

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify - Close, Export to Excel and Print Buttons are enabled in import error window");

		}

		closeButton1.click();
		sleeps();
		if(verifyButtonEnabled(viewErrorsButton,"View Errors Button"))
		{
			createTest.log(Status.PASS,"View Errors Button is enabled when  error file was tried to import");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify - View Errors Button is enabled when  error file was tried to import");

		}
		viewErrorsButton.click();
		sleeps();
		if((driver.getPageSource().contains("Importation utilisateurs/médecins  - ERREUR")) | (driver.getPageSource().contains("Users/Doctors import  - ERROR")))
		{
			createTest.log(Status.PASS,"Error Window was opened when View Error Button was clicked");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verifiy-Error Window was opened when View Error Button was clicked");

		}
		sleeps();

		driver.findElement(By.xpath("//span[text()='close']")).click();
		createTest.log(Status.PASS,"Verified Close icon of Error Window is working fine");
		sleeps();

*/

		//Upload a good file

		driver.findElement(By.id("fileUploader")).sendKeys(System.getProperty("user.dir") +"/src/test/resources/UsersImport.xlsx");
		driver.findElement(By.xpath("//mat-select")).click();
		driver.findElement(By.xpath("//mat-option//span[contains(text(),'Utilisateurs')] | //mat-option//span[contains(text(),'Users')]")).click();
		fileValidationButton.click();
		sleeps();
		sleeps();
		if(verifyButtonEnabled(importFileButton,"Import File Button"))
		{
			createTest.log(Status.PASS,"Import File Button is enabled when no error file was successfully imported");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify - Import File Button is enabled when no error file was successfully imported");

		}
		importFileButton.click();
		sleeps();
		if((driver.getPageSource().contains("Importation completed successfully!")) | (driver.getPageSource().contains("Importation terminée avec succès!")))
		{
			createTest.log(Status.PASS,"Success message is shown when no error file was successfully imported");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify - Success message is shown when no error file was successfully imported");

		}
		closeButton.click();


	}




	//*****************************************************StretcherAndBedCategories*************************************************************
	String zone = "ZN05";

	void navigateToStretcherBedCategories() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement strBedCat = driver.findElement(By.xpath("//div[text()=' *Catégories des civières / Lits'] | //div[text()=' *Stretcher / Bed categories']"));
		strBedCat.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}
	void createStretcherBedCategory() throws InterruptedException, AWTException
	{
		clickCreateButton();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(zone);

		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndRelease2Key();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("(//mat-option//span//div[contains(text(),'urg')])[last()]")).click();


		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		sleeps();			
		//driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])")).sendKeys("87");
		pressAndRelease8Key();
		pressAndRelease7Key();
		clickSaveButton();
		sleeps();
	}

	@Test(groups = {"StretcherBedCategories"},priority=6)
	void stretcherBedCategories() throws InterruptedException, AWTException
	{
		String screenNameFR= "Gestion des tables locales - *Catégories des civières / Lits ";
		String screenNameEN="Management of local tables - *Stretcher / Bed categories ";

		createTest=reports.createTest("Verifying Stretchers Bed Categories");	
		navigateToStretcherBedCategories();
		sleeps();
		sleeps();
		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)) )
		{
			createTest.log(Status.PASS,"Successfully navigated to "+screenNameFR+" Screen");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameFR+" Screen");

		}

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}

		filter(zone,1);
		if(driver.getPageSource().contains(zone))
		{
			driver.findElement(By.xpath("//span[text()='"+zone+"']")).click();
			clickDeleteButton();
			clickSaveButton();
			sleeps();

		}

		createStretcherBedCategory();
		
		filter(zone,1);

		if(driver.getPageSource().contains(zone))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+zone+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified Category code is not allowed to change in "+screenNameEN);
			clickOKButton();

		}

		//Check Restore
		
		sleeps();
		clickRestoreButton();
		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+screenNameEN);
		}
		else
		{
			filter(zone,1);
			if(driver.getPageSource().contains(zone))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+screenNameEN);

			}
		}



		//create duplicate-----------------
		createStretcherBedCategory();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
		}

	}


	@Test(groups = {"StretcherBedCategories"},priority=7)
	void filterRecords_StretcherBedCategories() throws InterruptedException, AWTException
	{
		filter(zone,1);
		clickAllFilter();
		if(driver.getPageSource().contains(zone))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(zone))
			{
				System.out.println("contains in Active-1");

				clickNonActiveFilter();
				if(!driver.getPageSource().contains(zone))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();


					if(!driver.getPageSource().contains(zone))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made  inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(zone))
		{
			createTest.log(Status.PASS,"Created row is succesfully edited");
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(zone))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(zone))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();


					if(!driver.getPageSource().contains(zone))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();

						driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]")).click();
						sleeps();
						pressAndReleaseSpaceKey();
						sleeps();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							Thread.sleep(6000);
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			
			clickYesButton();
		}
	}


	@Test(groups = {"StretcherBedCategories"},priority = 8)
	public void sortingAndDeleting_StrBedCat() throws AWTException, InterruptedException 
	{
		navigateToStretcherBedCategories();
		filter(zone,1);

		driver.findElement(By.xpath("//span[text()='"+zone+"']")).click();
		clickDeleteButton();
		sleeps();

		clickSaveButton();
		sleeps();

		filter(zone,1);
		if(driver.getPageSource().contains(zone)) 
		{
			createTest.log(Status.FAIL,"sort and delete funtions are not working");
		}
		else 
		{
			createTest.log(Status.PASS,"sort and delete funtions are working properly");
		}

	}


	//**************************************************************Stretcher Bed***************************************************************************

	String stretcherCode = RandomStringUtils.randomAlphanumeric(4);
	void navigateToStretcherBed() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement strBedCat = driver.findElement(By.xpath("//div[text()=' *Civières / Lits'] | //div[text()=' *Stretchers / Bed']"));
		strBedCat.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}

	String zone1 =RandomStringUtils.randomNumeric(1);
	void createStretcherBedRecord() throws InterruptedException, AWTException
	{
		
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(stretcherCode);
		pressAndReleaseTabKey();
		sleeps();
		//	pressAndReleaseSpaceKey();
		//sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("(//mat-option//span//div//div)["+zone1+"]")).click();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("notes");
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseDeleteKey();
		pressAndRelease2Key();	
		clickSaveButton();
	}

	@Test(groups = {"StretcherBed"},priority=9)
	void stretcherBedValidation() throws InterruptedException, AWTException
	{
		String screenNameFR= "Gestion des tables locales - *Civières / Lits ";
		String screenNameEN="Management of local tables - *Stretchers / Bed ";

		createTest=reports.createTest("Verifying Stretchers Bed Screen");	

		navigateToStretcherBed();
		sleeps();
		sleeps();
		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)) )
		{
			createTest.log(Status.PASS,"Successfully navigated to "+screenNameFR+" Screen");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameFR+" Screen");

		}

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}

		filter(stretcherCode,1);
		if(driver.getPageSource().contains(stretcherCode))
		{
			driver.findElement(By.xpath("//span[text()='"+stretcherCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
			sleeps();

		}

		createStretcherBedRecord();
		filter(stretcherCode,1);
		if(driver.getPageSource().contains(stretcherCode))
		{
			createTest.log(Status.PASS,"Stretcher bed record is succesfully created");

		}

		//create duplicate
		createStretcherBedRecord();		
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{
			createTest.log(Status.PASS,"verified stretcherCode should be unique and Duplicate is not allowed to create");
			
			clickOKButton();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed -  stretcherCode should be unique and Duplicate is not allowed to create");

		}

		filter(stretcherCode,1);

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+stretcherCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )

		{
			createTest.log(Status.PASS,"Stretcher code is not allowed to alter");
			clickOKButton();
		}

		sleeps();

		clickRestoreButton();
		clickSaveButton();
		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine ");
		}
		else
		{
			filter(stretcherCode,1);
			if(driver.getPageSource().contains(stretcherCode))
			{
				createTest.log(Status.PASS,"Restore Button is working fine ");
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine ");

			}
		}



	}

	@Test(groups = {"StretcherBed"},priority=10)
	void filterRecords_StretcherBed() throws InterruptedException, AWTException
	{
		filter(stretcherCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(stretcherCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(stretcherCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(stretcherCode))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!driver.getPageSource().contains(stretcherCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made  inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(stretcherCode))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(stretcherCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(stretcherCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();


					if(!driver.getPageSource().contains(stretcherCode))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Stretcher Bed is succesfully edited");
						clickAllFilter();

						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							createTest.log(Status.PASS,"Created Stretcher Bed record is successfully edited");

						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
	}

	@Test(groups = {"StretcherBed"},priority = 11)
	public void sortingAndDeletingStretcherBed() throws AWTException, InterruptedException 
	{
		navigateToStretcherBed();
		filter(stretcherCode,1);

		driver.findElement(By.xpath("//span[text()='"+stretcherCode+"']")).click();
		clickDeleteButton();
		sleeps();

		clickSaveButton();
		sleeps();

		filter(stretcherCode,1);
		if(driver.getPageSource().contains(stretcherCode)) 
		{
			createTest.log(Status.FAIL,"sort and delete funtions are not working");
		}
		else 
		{
			createTest.log(Status.PASS,"sort and delete funtions are working properly");
		}

		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
			
		}
	}


	//**********************************************MedicalNoteWordForm
	void navigateTomedicalNoteWordForm() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement medicalNoteWordForm = driver.findElement(By.xpath("//div[text()=' Formulaire Word de la note médicale'] | //div[text()=' Medical note word form']"));
		medicalNoteWordForm.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();
		sleeps();
	}


	@Test(groups = {"MedicalNoteWordForm"},priority=12)
	void medicalNoteWordFormValidation() throws InterruptedException, AWTException
	{
		String screenNameFR= "Formulaire Word de la note médicale";
		String screenNameEN="Medical note word form";
		String fileUploadPath=System.getProperty("user.dir")+"\\src\\test\\resources\\AutoRecovery save of Document1.docx";
		String home = System.getProperty("user.home");
		String file_name = "AutoRecovery save of Document1.docx";

		String download_file_with_location = home + "\\Downloads\\" + "MEDICAL NOTE WORD FORM.DOCX";
		System.out.println(download_file_with_location);
		createTest=reports.createTest("Medical Note Word Form Screen Validation");	
		navigateTomedicalNoteWordForm();
		Thread.sleep(6000);
		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)) )
		{
			createTest.log(Status.PASS,"Successfully navigated to "+screenNameFR+" Screen");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameFR+" Screen");

		}

		WebElement validationButton = driver.findElement(By.xpath("//button[text()='Validation']"));
		WebElement saveButton = driver.findElement(By.xpath("//button[text()='Sauvegarder'] | //button[text()='Save']"));
		WebElement closeButton = driver.findElement(By.xpath("//button[text()='Fermer'] | //button[text()='Close']"));
		

		if(verifyButtonDisabled(validationButton,"Validation Button"))
		{
			if(verifyButtonDisabled(saveButton,"Save Button"))
			{
				if(verifyButtonEnabled(closeButton,"Close Button"))
				{
					createTest.log(Status.PASS,"Close button are enabled and Validation and Save buttons are disabled by default");

				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify Close Button is enabled");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify Save Button is disabled");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Validation Button is disabled");
		}

		//Upload a wrong file and validate
		WebElement chooseFile = driver.findElement(By.id("fileUploader"));
		chooseFile.sendKeys(fileUploadPath);

		driver.findElement(By.xpath("//span[text()='Définir comme formulaire word par défaut (administrateur seulement)'] | //span[text()='Define as default word form (administrator only)']")).click();
		sleeps();
		if(driver.findElement(By.xpath("//input[@type='checkbox' and @aria-checked='true']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Define as default Word Form option is shown as a checkbox");
		}
		driver.findElement(By.xpath("//span[text()='Définir comme formulaire word par défaut (administrateur seulement)'] | //span[text()='Define as default word form (administrator only)']")).click();

		driver.findElement(By.xpath("//button[text()='Validation']")).click();
		sleeps();

		if(driver.findElement(By.xpath("//label[text()='"+file_name+"']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Validation Button is enabled when correct file was successfully uploaded");
			createTest.log(Status.PASS,"Uploaded file is successfully validated");
		}


		driver.findElement(By.xpath("//button[text()='Sauvegarder'] | //button[text()='Save']")).click();
		createTest.log(Status.PASS,"Save Button is enabled when uploaded file was successfully validated");
		Thread.sleep(5000);
		createTest.log(Status.PASS,"Save Button is working fine");
		sleeps();
		WebElement ele = driver.findElement(By.xpath("//img[contains(@src,'downlaod')]"));
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("arguments[0].click()", ele);
		Thread.sleep(7000);	
		createTest.log(Status.PASS,"Download button is working fine");

		File f = new File(download_file_with_location); 
		if(f.exists())
		{
			createTest.log(Status.PASS,"File is successfully downloaded when clicked on Save As Button");
		}

		driver.findElement(By.xpath("//button[text()='Fermer'] | //button[text()='Close']")).click();
		sleeps();
		sleeps();
		if(driver.findElement(By.xpath("//textarea")).isDisplayed())
		{
			createTest.log(Status.PASS,"Navigated to home screen when close button was clicked");
		}



	}


	//*********************************************************************Consult Note Word Form**********************************************************





	void navigateToConsultNoteWordForm() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement consultNoteWordForm = driver.findElement(By.xpath("//div[text()=' Formulaire Word de la note de consultation'] | //div[text()=' Consult note word form']"));
		consultNoteWordForm.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();
		sleeps();
	}

	@Test(groups = {"ConsultNoteWordForm"},priority=13)
	void consultNoteWordFormValidation() throws InterruptedException, AWTException
	{
		String screenNameFR= "Formulaire Word de la note consult par utilisateur";
		String screenNameEN="Consult note word form by user";
		String fileUploadPath=System.getProperty("user.dir")+"\\src\\test\\resources\\AutoRecovery save of Document1.docx";

		String home = System.getProperty("user.home");
		String file_name = "AutoRecovery save of Document1.docx";
		String download_file_with_location = home + "\\Downloads\\" + "consult note word form by user.DOCX";


		createTest=reports.createTest("Consult Note Word Form Screen Validation");	
		navigateToConsultNoteWordForm();
		sleeps();
		sleeps();
		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)) )
		{
			createTest.log(Status.PASS,"Successfully navigated to "+screenNameFR+" Screen");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameFR+" Screen");

		}

		WebElement validationButton = driver.findElement(By.xpath("//button[text()='Validation']"));
		WebElement saveButton = driver.findElement(By.xpath("//button[text()='Sauvegarder'] | //button[text()='Save']"));
		WebElement closeButton = driver.findElement(By.xpath("//button[text()='Fermer'] | //button[text()='Close']"));
		
		if(verifyButtonDisabled(validationButton,"Validation Button"))
		{
			if(verifyButtonDisabled(saveButton,"Save Button"))
			{
				if(verifyButtonEnabled(closeButton,"Close Button"))
				{
						createTest.log(Status.PASS,"Close and Save As buttons are enabled and Validation and Save buttons are disabled by default");

				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify Close Button is enabled");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify Save Button is disabled");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Validation Button is disabled");
		}

		//Upload a wrong file and validate
		WebElement chooseFile = driver.findElement(By.id("fileUploader"));
		chooseFile.sendKeys(fileUploadPath);
		sleeps();

		driver.findElement(By.xpath("//span[text()='Définir comme formulaire word par défaut (administrateur seulement)'] | //span[text()='Define as default word form (administrator only)']")).click();
		sleeps();
		if(driver.findElement(By.xpath("//input[@type='checkbox' and @aria-checked='true']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Define as default Word Form option is shown as a checkbox");
		}
		driver.findElement(By.xpath("//span[text()='Définir comme formulaire word par défaut (administrateur seulement)'] | //span[text()='Define as default word form (administrator only)']")).click();

		driver.findElement(By.xpath("//button[text()='Validation']")).click();
		sleeps();
		if(driver.findElement(By.xpath("//label[text()='"+file_name+"']")).isDisplayed())		{
			createTest.log(Status.PASS,"Validation Button is enabled when correct file was successfully uploaded");
			createTest.log(Status.PASS,"Uploaded file is successfully validated");
		}


		driver.findElement(By.xpath("//button[text()='Sauvegarder'] | //button[text()='Save']")).click();
		Thread.sleep(10000);
		createTest.log(Status.PASS,"Save Button is enabled when uploaded file was successfully validated");
		createTest.log(Status.PASS,"Save Button is working fine");
		Thread.sleep(10000);
		
		WebElement ele = driver.findElement(By.xpath("//img[contains(@src,'downlaod')]"));
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("arguments[0].click()", ele);
		Thread.sleep(7000);	
		
		createTest.log(Status.PASS,"Download link is working fine");
		File f = new File(download_file_with_location); 
		if(f.exists())
		{
			createTest.log(Status.PASS,"File is successfully downloaded when clicked on Save As Button");
		}

		driver.findElement(By.xpath("//button[text()='Fermer'] | //button[text()='Close']")).click();
		Thread.sleep(7000);
		if(driver.findElement(By.xpath("//textarea")).isDisplayed())
		{
			createTest.log(Status.PASS,"Navigated to home screen when close button was clicked");
		}


	}



	//**********************************************************Elements Note Word Form**************************************************************

	String file_name = "DOCNAME1 (5).docx";
	String fileUploadPath=System.getProperty("user.dir")+"\\src\\test\\resources\\"+file_name;
	String home = System.getProperty("user.home");

	String download_file_with_location = home + "\\Downloads\\" + file_name;

	String docName = "docName1";
	void navigateToElementsNoteWordForm() throws InterruptedException
	{

		navigateToManagementMenu();
		WebElement stationAlarmManagementMenu = driver.findElement(By.xpath("//div[text()=' Formulaire Word de la note par élément'] | //div[text()=' Elements note word form']"));
		stationAlarmManagementMenu.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}
	@Test(groups = {"ElementsNoteWordForm"},priority=14)
	void createElementsNoteWordForm() throws InterruptedException, AWTException
	{
		String screenNameFR= "Gestion des tables locales - Formulaire Word de la note par élément";
		String screenNameEN="Management of local tables - Elements note word form";

		createTest=reports.createTest("Verifying Create Elements Note Word Form");	
		navigateToElementsNoteWordForm();

		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)) )
		{
			createTest.log(Status.PASS,"Successfully navigated to "+screenNameFR+" Screen");

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameFR+" Screen");

		}

		filter(docName,1);
		if(driver.getPageSource().contains(docName))
		{
			driver.findElement(By.xpath("//span[text()='"+docName+"']")).click();
			clickDeleteButton();
			clickSaveButton();

		}
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(docName);
		clickSaveButton();
		filter(docName,1);

		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')])[2]//button")).click();
		sleeps();
		WebElement chooseFile = driver.findElement(By.id("fileUploader"));
		chooseFile.sendKeys(fileUploadPath);

		driver.findElement(By.xpath("//button[text()='Validation']")).click();
		sleeps();
		
		driver.findElement(By.xpath("//button[text()='Validation']")).click();
		sleeps();
		
		driver.findElement(By.xpath("//button[text()='Sauvegarder'] | //button[text()='Save']")).click();
		Thread.sleep(13000);
		
		WebElement ele = driver.findElement(By.xpath("//img[contains(@src,'downlaod')]"));
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("arguments[0].click()", ele);
		Thread.sleep(7000);	
		
		File f = new File(download_file_with_location); 
		if(f.exists())
		{
			createTest.log(Status.PASS,"Word Form is succesfully uploaded");
			createTest.log(Status.PASS,"File is successfully downloaded when clicked on Save As Button");
		}

		driver.findElement(By.xpath("//button[text()='Fermer'] | //button[text()='Close']")).click();
		Thread.sleep(3000);
		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)) )
		{
			createTest.log(Status.PASS,"Navigated to home screen when close button was clicked");
		}



	}

	@Test(groups = {"ElementsNoteWordForm"},priority=15)
	void filterElementsNoteWordForm() throws InterruptedException, AWTException
	{
		filter(docName,1);
		clickAllFilter();
		if(driver.getPageSource().contains(docName))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(docName))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(docName))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();


					if(!driver.getPageSource().contains(docName))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Verified All, Active, Non Active and Reservrd filters");
						clickAllFilter();
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}
		
	}


	@Test(groups = {"ElementsNoteWordForm"},priority = 16)
	void ElementsNoteWordFormUIValidation() throws InterruptedException, AWTException
	{
		
		filter(docName,1);
		driver.findElement(By.xpath("//span[contains(text(),'"+docName+"')]")).click();
		
		pressAndReleaseTabKey();
		
		pressAndReleaseSpaceKey();
		
		pressAndReleaseTabKey();
		
		pressAndReleaseSpaceKey();
		
		pressAndReleaseTabKey();
		
		pressAndReleaseSpaceKey();
		
		pressAndReleaseTabKey();
		
		pressAndReleaseSpaceKey();
		
		clickSaveButton();
		sleeps();
		if((driver.getPageSource().contains("Les cases 'Par visite' et 'Par ordonnance' ne peuvent pas être cochées en même temps"))|(driver.getPageSource().contains("The 'Per visit' and 'Per order' checkboxes cannot be checked at the same time")) )
		{
			createTest.log(Status.PASS,"Verified Per Visit  and Per Order columns cannot be checked at same time");
			sleeps();
			clickOKButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed - Verified Per Visit  and Per Order columns cannot be checked at same time");

		}
		driver.findElement(By.xpath("(//div[@col-id='parVisite'])[last()]")).click();
		pressAndReleaseSpaceKey();
		sleeps();
		clickSaveButton();

		//Check Duplicate
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(docName);
		clickSaveButton();
		sleeps();
		if((driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY"))|(driver.getPageSource().contains("Violation of UNIQUE KEY")) )
		{
			createTest.log(Status.PASS,"Verified Document name should be Unique and Duplicate is not allowed to create");
			clickOKButton();

		}
		else
		{
			createTest.log(Status.FAIL,"Failed - Verified Document name should be Unique and Duplicate is not allowed to create");

		}
		clickRestoreButton();
		clickHome();
		if(!(driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"Restore Button is working fine");


		}

	}

	@Test(groups = {"ElementsNoteWordForm"},priority = 17)
	public void sortingAndDeletingElementsNoteWordForm() throws AWTException, InterruptedException 
	{
		navigateToElementsNoteWordForm();
		filter(docName,1);

		driver.findElement(By.xpath("//span[text()='"+docName+"']")).click();
		clickDeleteButton();
		sleeps();

		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();

		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}
		sleeps();

		navigateToElementsNoteWordForm();
		filter(docName,1);
		if(driver.getPageSource().contains(docName)) 
		{
			createTest.log(Status.FAIL,"sort and delete funtions are not working");
		}
		else 
		{
			createTest.log(Status.PASS,"sort and delete funtions are working properly");
		}
		
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();

		}

	}

	//********************************************Alarm ANd NOSO*************************************************************



	String NOSOCode = RandomStringUtils.randomAlphanumeric(4); 
	String descFR = RandomStringUtils.randomAlphanumeric(4); 
	String descEN = RandomStringUtils.randomAlphanumeric(4);


	void navigateToAlarmsAndNOSOManagement() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement alarmsAndNOSOManagement = driver.findElement(By.xpath("//div[text()=' Gestion des alarmes & NOSO'] | //div[text()=' Alarms & NOSO management']"));
		alarmsAndNOSOManagement.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
	}

	void createAlarmsAndNOSManagement() throws InterruptedException, AWTException
	{

		createTest.log(Status.PASS,"Successfully navigated to Alarm and NOSO Screen");
		clickCreateButton();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])//input")).sendKeys(NOSOCode);
		pressAndReleaseEnterKey();	
		filter(NOSOCode,1);
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][1]")).click();;
		pressAndReleaseEnterKey();
		pressAndReleaseDownArrowKey();
		pressAndReleaseEnterKey();	
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys(descFR);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys(descEN);
		driver.findElement(By.xpath("(//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')])[1]")).click();
		sleeps();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{
			clickOKButton();
		}

	}

	@Test(groups = {"AlarmAndNOSOManagement","AlarmPriorityAndColorManagement"},priority=18, enabled=true)
	void AlarmAndNOSOValidation() throws InterruptedException, AWTException
	{

		String screenNameFR= "Configuration - Alarmes et NOSO";
		String screenNameEN="Management - Alarms & NOSO";

		createTest=reports.createTest("Verifying Alarms And NOSO ");	
		navigateToAlarmsAndNOSOManagement();
		sleeps();
		sleeps();

		driver.findElement(By.xpath("//div[text()='Alarms list' ] | //div[text()='Liste des alarmes' ]")).click();
		createAlarmsAndNOSManagement();

		filter(NOSOCode,1);
		sleeps();
		if(driver.getPageSource().contains(NOSOCode) )
		{
			createTest.log(Status.PASS,"Successfully created an Alarm under section - Alarm List");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed - Successfully created an Alarm under section - Alarm List");

		}
		
		verifyClearFilter();

	}



	@Test(groups = {"AlarmAndNOSOManagement"},priority=19)
	void associationOfAlarm() throws InterruptedException, AWTException
	{

		createTest=reports.createTest("Verifying Alarms And NOSO - Assosciation Screen ");	

		navigateToAlarmsAndNOSOManagement();
		sleeps();
		sleeps();

		//Association
		driver.findElement(By.xpath("//div[text()='Association des alarmes & Noso' ] | //div[text()='Alarms & Noso association' ]")).click();
		sleeps();
		driver.findElement(By.xpath("(//mat-select)[1]")).click();
		if(driver.findElement(By.xpath("//mat-option//span[contains(text(),'ADU')]")).isDisplayed())
		{
			if(driver.findElement(By.xpath("//mat-option//span[contains(text(),'ADU')]")).isDisplayed())
			{
				createTest.log(Status.PASS,"Verified Family field dropdown options contains ADU and PED");

			}
		}
		driver.findElement(By.xpath("//mat-option//span[contains(text(),'ADU')]")).click();

		//Navigate to Category
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		if(driver.findElement(By.xpath("//mat-option//span[contains(text(),'Allergie(s)')] | //mat-option//span[contains(text(),'ALLERGIE(S)')]")).isDisplayed())
		{
			if(driver.findElement(By.xpath("//mat-option//span[contains(text(),'Antécédents')] | //mat-option//span[contains(text(),'P.M.H.')]")).isDisplayed())
			{
				createTest.log(Status.PASS,"Verified Category dropdown options contains Allergy or Medical History");

			}
		}

		String catFR ="Allergie(s)";
		String catEN="ALLERGIE(S)";
		String allgCatFR="Alimentairese";
		String allgCatEN="Alimentaires";
		String allwoCatEN = "descEN";
		String allwoCatFR = "descFR";


		driver.findElement(By.xpath("//mat-option//span[contains(text(),'"+catFR+"')] | //mat-option//span[contains(text(),'"+catEN+"')]")).click();
		driver.findElement(By.xpath("//div//span[text()='"+allgCatFR+"'] | //div//span[text()='"+allgCatEN+"']")).click();
		sleeps();
		driver.findElement(By.xpath("(//div//span[text()='[All] "+allwoCatEN+"'] | //div//span[text()='[Tous] "+allwoCatFR+"'])[1]")).click();
		createTest.log(Status.PASS,"Allergy  Categories are displayed and succesfully selected");

		//navigate to  radio
		if(driver.findElement(By.xpath("(((//mat-radio-button)[1]//span[text()='Triage alarms']) | ((//mat-radio-button)[1]//span[text()='Alarmes au triage']) //preceding-sibling::span)[1]")).isDisplayed())
		{
			if(driver.findElement(By.xpath("(((//mat-radio-button)[2]//span[text()='Alarmes nosocomiales']) | ((//mat-radio-button)[2]//span[text()='Nosocomials alarms']) //preceding-sibling::span)[1]")).isDisplayed())
			{
				createTest.log(Status.PASS,"Triage or Nosocomial ALarm options are shown");

			}
		}
		driver.findElement(By.xpath("(((//mat-radio-button)[2]//span[text()='Alarmes nosocomiales']) | ((//mat-radio-button)[2]//span[text()='Nosocomials alarms']) //preceding-sibling::span)[1]")).click();

		//Veirfy Alarm
		driver.findElement(By.xpath("(//mat-select)[3]")).click();

		//pressAndReleaseEnterKey();

		WebElement ele = driver.findElement(By.xpath("//mat-option//span[text()='"+descEN+"'] | //mat-option//span[text()='"+descFR+"']"));
		Actions a = new Actions(driver);
		a.moveToElement(ele).perform();


		driver.findElement(By.xpath("//mat-option//span[text()='"+descEN+"'] | //mat-option//span[text()='"+descFR+"']")).click();
		clickSaveButton();
		sleeps();
		clickHome();
		sleeps();
		if(driver.findElement(By.xpath("//textarea")).isDisplayed())
		{
			createTest.log(Status.PASS,"Successfully navigated to Home page when Home link was clicked");

		}


		//Navigate back to alarm list
		navigateToAlarmsAndNOSOManagement();
		sleeps();
		driver.findElement(By.xpath("//div[text()='Alarms list' ] | //div[text()='Liste des alarmes' ]")).click();
		filter(NOSOCode,1);


		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
		sleeps();
		if(driver.getPageSource().contains(NOSOCode))
		{
			System.out.println("before clicking Triage Alrm option");
			driver.findElement(By.xpath("(//span[contains(text(),'Alarmes au triage')] | //span[contains(text(),'Triage alarms')])//ancestor::mat-radio-button//span[@class='mat-radio-container']")).click();
			sleeps();
			if(!driver.getPageSource().contains(NOSOCode))
			{
				System.out.println("before clicking Nosocomial Alarm option");

				driver.findElement(By.xpath("(//span[contains(text(),'Alarmes nosocomiales')] | //span[contains(text(),'Nosocomial alarms')])//ancestor::mat-radio-button//span[@class='mat-radio-container']")).click();
				sleeps();
				if(driver.getPageSource().contains(NOSOCode))
				{
					System.out.println("passes radio options pf alrm");

					createTest.log(Status.PASS,"All, Triage Alarm and Nosocomial Alarm filters are working fine");
				}
				else
				{
					createTest.log(Status.FAIL,"Failed verifying Nosocomial ALarm option");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Failed verifying Triage ALarm option");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed verifying All ALarm option");

		}




		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
		sleeps();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')]//span[text()='"+NOSOCode+"']")).click();
		sleeps();
		sleeps();


		if(driver.findElement(By.xpath("//span[text()=' (I) "+descFR+" : "+catFR+" >  > "+allgCatFR+" >  "+allwoCatFR+"'] | //span[text()=' (I) "+descEN+" : "+catEN+" >  > "+allgCatEN+" >  "+allwoCatEN+"']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Allergy to which desired alarm is associated is shown under Association section");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed - Allergy to which desired alarm is associated is shown under Association section");

		}
		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[2]")).click();
		sleeps();

		//if((driver.getPageSource().contains("//span[text()=' (I) "+descFR+" : "+catFR+" >  > "+allgCatFR+" >  "+allwoCat+"']")) | (driver.getPageSource().contains("//span[text()=' (I) "+descEN+" : "+catEN+" >  > "+allgCatEN+" >  "+allwoCat+"']")))
		if((driver.getPageSource().contains(allgCatFR)) | (driver.getPageSource().contains(allgCatEN)))

		{
			System.out.println("before adult click");
			driver.findElement(By.xpath("((//span[contains(text(),'Adulte')] | //span[contains(text(),'Adult')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])")).click();
			sleeps();
			if((driver.getPageSource().contains(allgCatFR)) | (driver.getPageSource().contains(allgCatEN)))
			{
				driver.findElement(By.xpath("((//span[contains(text(),'Pédiatrique')] | //span[contains(text(),'Pediatric')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])")).click();
				sleeps();
				if((driver.getPageSource().contains(allgCatFR)) | (driver.getPageSource().contains(allgCatEN)))
				{
					createTest.log(Status.FAIL,"Failed to verify Paediatric option");
					createTest.log(Status.PASS,"All, Adult and paediatric filters under Association section is working fine");
				}
				else
				{
					createTest.log(Status.PASS,"All, Adult and paediatric filters under Association section is working fine");


				}
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to verify Adult option");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify All option");

		}
		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[2]")).click();
		sleeps();

		//Association button validation
		driver.findElement(By.xpath("//span[text()=' (I) "+descFR+" : "+catFR+" >  > "+allgCatFR+" >  "+allwoCatFR+"'] | //span[text()=' (I) "+descEN+" : "+catEN+" >  > "+allgCatEN+" >  "+allwoCatEN+"']")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]")).click();
		sleeps();
		if((driver.getPageSource().contains(allgCatFR)) | (driver.getPageSource().contains(allgCatEN)))
		{
			createTest.log(Status.PASS,"Association is striked off when it was deleted");
			driver.findElement(By.xpath("(//button[contains(text(),'Rétablir')] | //button[contains(text(),'Restore')])[2]")).click();
			if((driver.getPageSource().contains(allgCatFR)) | (driver.getPageSource().contains(allgCatEN)))
			{


			}
			else
			{
				createTest.log(Status.PASS,"Restore Button is working fine");
				driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]")).click();
				driver.findElement(By.xpath("(//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')])[2]")).click();
			}

		}
	}

	@Test(groups = {"AlarmAndNOSOManagement"},priority=20)
	void currentPatientsActiveAlarm_Screen() throws InterruptedException, AWTException
	{

		createTest=reports.createTest("Verifying Current Patients Active alarms and NOSO");	

		driver.findElement(By.xpath("//div[contains(text(),'Alarmes & NOSO en cours pour les patients')] | //div[contains(text(),'Current patients active alarms & NOSO')]")).click();

		String triageAlarm = "TMP138237";
		String nosocomial_Alarm ="137523";


		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
		filter(triageAlarm,1);
		if(driver.getPageSource().contains(triageAlarm))
		{
			driver.findElement(By.xpath("((//span[contains(text(),'Alarmes au triage')] | //span[contains(text(),'Triage alarms')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();

			if(driver.getPageSource().contains(triageAlarm))
			{
				driver.findElement(By.xpath("((//span[contains(text(),'Alarmes nosocomiales')] | //span[contains(text(),'Nosocomial alarms')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();

				if(!driver.getPageSource().contains(triageAlarm))
				{
					createTest.log(Status.PASS,"All radio button of Current Patients active Alarm screen is working fine");
					//createTest.log(Status.PASS,"All radio button of Current Patients active Alarm screen is working fine");

				}
			}
		}

		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
		filter(nosocomial_Alarm,1);	
		if(driver.getPageSource().contains(nosocomial_Alarm))
		{
			driver.findElement(By.xpath("((//span[contains(text(),'Alarmes au triage')] | //span[contains(text(),'Triage alarms')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
			if(!driver.getPageSource().contains(nosocomial_Alarm))
			{
				driver.findElement(By.xpath("((//span[contains(text(),'Alarmes nosocomiales')] | //span[contains(text(),'Nosocomial alarms')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
				if(driver.getPageSource().contains(nosocomial_Alarm))
				{
					createTest.log(Status.PASS,"Triage Alarm radio button of Current Patients active Alarm screen is working fine");
					createTest.log(Status.PASS,"Nosocomial Alarm radio button of Current Patients active Alarm screen is working fine");

				}
			}
		}

		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
		filter(nosocomial_Alarm,1);	
		driver.findElement(By.xpath("((//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[2]")).click();
		if(driver.getPageSource().contains(nosocomial_Alarm))
		{
			driver.findElement(By.xpath("((//span[contains(text(),'Interface')] | //span[contains(text(),'Interface')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
			if(!driver.getPageSource().contains(nosocomial_Alarm))
			{
				driver.findElement(By.xpath("((//span[contains(text(),'Med-Urge')] | //span[contains(text(),'Med-Urge')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
				if(driver.getPageSource().contains(nosocomial_Alarm))
				{
					driver.findElement(By.xpath("((//span[contains(text(),'Interface')] | //span[contains(text(),'Interface')])//ancestor::mat-radio-button//span[@class='mat-radio-container'])[1]")).click();
					filter("103601",1);	
					if(driver.getPageSource().contains(nosocomial_Alarm))
					{
						createTest.log(Status.PASS,"All radio button of Current Patients active Alarm screen is working fine");
						createTest.log(Status.PASS,"Interface radio button of Current Patients active Alarm screen is working fine");
						createTest.log(Status.PASS,"Medurge radio button of Current Patients active Alarm screen is working fine");

					}

				}
			}


		}
		clickHome();


	}


	//*******************************************Alarm Priority and Color Management**************************************





	void navigateToAlarmPriorityAndColorManagement() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement  alarmPriorityAndColorManagement= driver.findElement(By.xpath("//div[text()=' Gestion de la priorité et couleur des alarmes'] | //div[text()=' Alarm priority and color management']"));
		alarmPriorityAndColorManagement.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}


	@Test(groups = {"AlarmPriorityAndColorManagement"},priority=21)
	public void alarmPriorityAndColorManagementValidation() throws InterruptedException, AWTException
	{

		String pageNameEnglish= "Alarm priority and color management";
		String pageNameFrench = "Gestion de la priorité et couleur des alarmes";

		String screenNameFR="Gestion des tables locales - Gestion de la priorité et couleur des alarmes";
		String screenNameEN="Management of local tables - Alarm priority and color management";

		createTest=reports.createTest("Verifying Alarm Priority And Color Management Screen");	

		navigateToAlarmPriorityAndColorManagement();
		sleeps();
		sleeps();


		verifyBreadCrumb(pageNameEnglish, pageNameFrench);




		//Verify restore, delete, save an continue buttons are disabled
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(restoreButton,"Restore Button"))
		{
			if(verifyButtonDisabled(saveButton, "Save Button"))
			{
				createTest.log(Status.PASS,"Restore and Save Buttons are disabled  by default");
			}
			else
			{
				createTest.log(Status.FAIL,"Save  button is not disabled by default");

			}

		}
		else
		{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");

		}



		filter(NOSOCode,1);
		if(driver.getPageSource().contains(NOSOCode))
		{
			createTest.log(Status.PASS,"Alarm Code added in Alarms And NOS Management Screen is successfully displaying in Alarm Priority And Color Managment");

		}
		else
		{
			createTest.log(Status.FAIL,"Alarm Code added in Alarms And NOS Management Screen is not displaying in Alarm Priority And Color Managment");

		}
		
		verifyClearFilter();
		clickHome();
		
	}

	//**********************************************Arrival Mode*************************************************************



	void navigateToArrivalMode() throws InterruptedException
	{
		navigateToPredefinedTables();
		WebElement arrivalMode = driver.findElement(By.xpath("//div[contains(text(),'Mode')] | //div[text()=' Arrival mode']"));
		arrivalMode.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();

		sleeps();
		sleeps();
		if(driver.findElement(By.xpath("//h2[contains(text(),'Gestion des tables locales - Mode ')] | //h2[contains(text(),'Management of local tables - Arrival mode')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"‘Management of local tables - Destination after triage’ pop up is shown when clicked on the menu item Destination after triage ");
			clickOKButton();
		}
		else
		{
			createTest.log(Status.FAIL,"‘Management of local tables - Arrival Mode’ pop up is not shown when clicked on the menu item Destination after triage ");

		}
		sleeps();
	}

	@Test(groups = {"ArrivalModeScreen"},priority=22)
	void ArrivalModeValidation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "Arrival mode";
		String pageNameFrench = "Mode d'arrivée";
		String screenNameFR= "Gestion des tables locales - Mode d'arrivée";
		String screenNameEN="Management of local tables - Arrival mode";


		createTest=reports.createTest("Verifying Arrival Mode");	

		navigateToArrivalMode();
		sleeps();
		sleeps();
		sleeps();
		sleeps();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter("1",1);
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='1']")).click();
		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='1']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Code column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Code column is editable");
		}

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][2]")).click();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseDeleteKey();
		sleeps();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testFR");
		pressAndReleaseTabKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testEN");
		clickRestoreButton();
		sleeps();
		if(verifyButtonDisabled(createButton, "Create Button"))
		{

			if(verifyButtonDisabled(deleteButton, "Delete Button"))
			{
				createTest.log(Status.PASS,"Create and  Delete are disabled, even after row was edited");
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create and  Delete are enabled, even after row was edited");

		}
		clickSaveButton();
		filter("Ambulance",2);
		if(driver.getPageSource().contains("Ambulance"))
		{
			createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
			createTest.log(Status.PASS,"User is able to update the values in all the columns except Code column");
			createTest.log(Status.PASS,"Save Button is enabled when row was edited");
			createTest.log(Status.PASS,"Row is successfully edited");
		}
		else
		{
			createTest.log(Status.FAIL,"Restore Button is not enabled when existing record is edited");
			createTest.log(Status.FAIL,"User is not able to update the values in all the columns except Code column");
			createTest.log(Status.FAIL,"Save Button is not enabled when row was edited");
			createTest.log(Status.FAIL,"Row is not successfully edited");
		}
		clickAllFilter();
		if(driver.getPageSource().contains("Ambulance"))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains("Ambulance"))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains("Ambulance"))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					sleeps();
					if(!driver.getPageSource().contains("Ambulance"))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made computer inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains("Ambulance"))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains("Ambulance"))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains("Ambulance"))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();
					sleeps();


					if(!driver.getPageSource().contains("Ambulance"))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();

						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Mode d'arrivée | Management of local tables - Arrival Mode");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
	}


	//*******************************************Board COnfiguration By Unit*********************************************************

	String workStation = "MAC_AZR-228";
	String boardDescription = "228Test";


	String allUnitDropdownOptionFrench = "Toutes les unités";
	String allUnitDropdownOptionEnglish = "All unit";

	String emergencyDropdownOptionFrench = "Urgence";
	String emergencyDropdownOptionEnglish = "Emergency";

	String medicineSurgeryDropdownOptionFrench = "Médecine Chirurgie";
	String medicineSurgeryDropdownOptionEnglish = "Medecine Surgery";

	public void navigateToBoardConfigMenu() throws InterruptedException
	{
		navigateToManagementMenu();

		WebElement boardConfigMenu = driver.findElement(By.xpath("//div[text()=' Configuration des tableaux par unité'] | //div[text()=' Board configuration by unit']"));
		boardConfigMenu.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}


	//Create New Board
	public void createNewBoardConfigRecord() throws InterruptedException, AWTException
	{
		clickCreateButton();

		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(workStation);

		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(boardDescription);
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		if((driver.getPageSource().contains(allUnitDropdownOptionFrench)) || (driver.getPageSource().contains(allUnitDropdownOptionEnglish)))
		{
			driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+allUnitDropdownOptionFrench+"')] | //mat-option//span//div[contains(text(),'"+allUnitDropdownOptionEnglish+"')]")).click();

			sleeps();
			clickSaveButton();

		}
		else
		{
			createTest.log(Status.FAIL,"Board COnfiguration option does not have Med Soin type");
		}


	}

	@Test(groups = {"BoardConfigurationByUnit"},priority=23)
	public void newBoardConfigurationValidation() throws InterruptedException, AWTException 
	{


		String pageNameEnglish ="Board configuration by unit";
		String pageNameFrench = "Configuration des tableaux par unité";

		createTest=reports.createTest("Verifying Create New Board Configuration");	

		navigateToBoardConfigMenu();
		Thread.sleep(10000);
		System.out.println("wait time ended");
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);
		//Verify header
		WebElement header = driver.findElement(By.xpath("//h1"));
		verifyHeader("Management of local tables - Board configuration by unit","Gestion des tables locales - Configuration des tableaux par unité");
		System.out.println("Verified Header");


		//Verify restore, delete, save an continue buttons are disabled
		WebElement copyintoButton = driver.findElement(By.xpath("//button[contains(text(),'Copy into')] | //button[contains(text(),'Copier vers')]"));
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Deafult status of buttons
		if(verifyButtonEnabled(copyintoButton, "Copy Into Button"))
		{
			if(verifyButtonEnabled(createButton, "Create Button"))
			{
				if(verifyButtonDisabled(restoreButton,"Restore Button"))
				{
					if(verifyButtonEnabled(deleteButton,"Delete Button"))
					{
						if(verifyButtonDisabled(saveButton, "Save Button"))
						{
							createTest.log(Status.PASS,"Copy Into, Create and Delete Buttons are enabled and Restore and Save buttons are disabled by deafult");
						}
						else
						{
							createTest.log(Status.FAIL,"Save  button is not disabled by default");

						}
					}
					else
					{
						createTest.log(Status.FAIL,"Delete button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Restore button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Create button is not enabled by default");

			}

		}
		else
		{
			createTest.log(Status.FAIL,"Copy button is not enabled by default");

		}

		filter(workStation,1);
		if(driver.getPageSource().contains(workStation))
		{
			driver.findElement(By.xpath("//span[text()='"+workStation+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();

		}
		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{
			driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();

		}



		createNewBoardConfigRecord();
		filter(workStation,1);

		if(driver.getPageSource().contains(workStation))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+pageNameEnglish);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+workStation+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();

		

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+pageNameEnglish);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+pageNameEnglish);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(workStation);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createNewBoardConfigRecord();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+pageNameEnglish);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+pageNameEnglish);
		}


	}



	@Test(groups = {"BoardConfigurationByUnit"},priority = 24)
	public void filteringRecords() throws InterruptedException, AWTException
	{	

		filter(workStation,1);
		clickAllFilter();
		if(driver.getPageSource().contains(workStation))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(workStation))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(workStation))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(workStation))
					{
						System.out.println("not contains in reserved1");

						createTest.log(Status.PASS,"Active filter is working fine");

					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(workStation))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(workStation))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(workStation))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(workStation))
					{
						System.out.println(" not contains in reserved-2");
						sleeps();

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Mode d'arrivée | Management of local tables - Arrival Mode");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
						


					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

	}



	public void selectDropdownOption(String optionFrench,String optionEnglish) throws AWTException, InterruptedException
	{
		sleeps();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[7]")).click();
		System.out.println("Clicked dropdown");
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("//mat-option//span[contains(text(),'"+optionFrench+"')] | //mat-option//span[contains(text(),'"+optionEnglish+"')]")).click();
		sleeps();

	}



	//*******************************************************CMD****************************************************




	void navigateToCMD() throws InterruptedException
	{
		navigateToPredefinedTables();
		WebElement cmd = driver.findElement(By.xpath("//div[text()=' CMD']"));
		cmd.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();

		sleeps();
		sleeps();
		//			
		//			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()=' Gestion des tables locales - Destination après triage'] | //h2[contains(text(),' Management of local tables - Destination after triage')]"))).click();
		//			if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		//			{
		//				clickNoButton();
		//				
		//			}
		if(driver.findElement(By.xpath("//h2[contains(text(),'Gestion des tables locales - CMD')] | //h2[contains(text(),'Management of local tables - CMD')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"‘Management of local tables - Destination after triage’ pop up is shown when clicked on the menu item Destination after triage ");
			WebElement popup = driver.findElement(By.xpath("//h2"));
			verifyText(popup,"Destination AFter Triage Popup", "Gestion des tables locales - CMD","Management of local tables - CMD");
			WebElement popupmsg = driver.findElement(By.xpath("//p"));
			verifyText(popupmsg,"Destination AFter Triage Popup", "Les codes de cette table sont définis dans le cadre normatif et ne peuvent pas être modifiés directement. Vous pouvez effectuer les corrections mineures des descriptions ou rendre non disponibles certains codes s’il y a lieu (services cliniques non présents, etc.)","The codes of this table are defined in the normative framework and cannot be modified directly. You can make minor corrections of the descriptions or render certain codes non-available if necessary (clinical services not present)");
			clickOKButton();
		}
		else
		{
			createTest.log(Status.FAIL,"‘Management of local tables - Destination after triage’ pop up is not shown when clicked on the menu item Destination after triage ");

		}
		sleeps();
		sleeps();
	}

	@Test(groups = {"CMD"},priority=25)
	void CMDValidation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "CMD";
		String pageNameFrench = "CMD";
		String screenNameFR= "Gestion des tables locales - CMD";
		String screenNameEN="Management of local tables - CMD";
		createTest=reports.createTest("Verifying CMD");	

		navigateToCMD();
		sleeps();
		sleeps();
		sleeps();
		sleeps();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter("01",1);
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='01']")).click();
		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='01']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Code column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Code column is editable");
		}

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][2]")).click();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseDeleteKey();
		sleeps();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testFR");
		pressAndReleaseTabKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testEN");
		clickRestoreButton();
		sleeps();
		if(verifyButtonDisabled(createButton, "Create Button"))
		{

			if(verifyButtonDisabled(deleteButton, "Delete Button"))
			{
				createTest.log(Status.PASS,"Create and  Delete are disabled, even after row was edited");
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create and  Delete are enabled, even after row was edited");

		}
		clickSaveButton();
		filter("Maladies du système nerveux",2);
		if(driver.getPageSource().contains("Maladies du système nerveux"))
		{
			createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
			createTest.log(Status.PASS,"User is able to update the values in all the columns except Code column");
			createTest.log(Status.PASS,"Save Button is enabled when row was edited");
			createTest.log(Status.PASS,"Row is successfully edited");
		}
		else
		{
			createTest.log(Status.FAIL,"Restore Button is not enabled when existing record is edited");
			createTest.log(Status.FAIL,"User is not able to update the values in all the columns except Code column");
			createTest.log(Status.FAIL,"Save Button is not enabled when row was edited");
			createTest.log(Status.FAIL,"Row is not successfully edited");
		}
		clickAllFilter();
		if(driver.getPageSource().contains("Maladies du système nerveux"))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains("Maladies du système nerveux"))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains("Maladies du système nerveux"))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains("Maladies du système nerveux"))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains("Maladies du système nerveux"))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains("Maladies du système nerveux"))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains("Maladies du système nerveux"))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains("Maladies du système nerveux"))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();

						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Formulaire Word des notes spécifiques | Management of local tables - Custom note word form");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
	}


	//*********************************************************Consultation*******************************************


	String consultationCode="9890";
	String consultDocName = "CHUTE";
	String ShortDescription = "CNS";

	void navigateToConsultation() throws InterruptedException
	{
		navigateToPredefinedTables();
		WebElement consultation = driver.findElement(By.xpath("//div[text()=' Consultation']"));
		consultation.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		
	}
	void createConsultation() throws InterruptedException, AWTException
	{

		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(consultationCode);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(ShortDescription);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		clickSaveButton();

	}	

	@Test(groups = {"Consultation"},priority=26)
	void Consultation_Creation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "Consultation";
		String pageNameFrench = "Consultation";

		createTest=reports.createTest("Verifying Local Tables - Consultation");	

		navigateToConsultation();
		sleeps();
		sleeps();
		//zoomOutScreen();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);



		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}	


		filter(consultationCode,1);
		if(driver.getPageSource().contains(consultationCode))
		{
			driver.findElement(By.xpath("//span[text()='"+consultationCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

		}
		
		filter("0999",1);
		if(driver.getPageSource().contains("0999"))
		{
			driver.findElement(By.xpath("//span[text()='"+"0999"+"']")).click();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

		}

		createConsultation();
		filter(consultationCode,1);

		if(driver.getPageSource().contains(consultationCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+ConsultationCancelReason_screenNameEN);
		}

		//Edit-----------------

		//driver.findElement(By.xpath("//span[text()='"+consultationCode+"']")).click();
		//pressAndReleaseControlAKey();

		driver.findElement(By.xpath("//span[text()='"+consultationCode+"']")).click();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();

		pressAndRelease0Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();

		

		filter("0999",1);
		if(driver.getPageSource().contains("0999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+ConsultationCancelReason_screenNameEN);
		}

		//Check Restore
		//		    		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		//		    		pressAndReleaseControlAKey();

		driver.findElement(By.xpath("//span[text()='"+"0999"+"']")).click();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();

		pressAndRelease1Key();
		pressAndRelease0Key();
		pressAndRelease2Key();
		pressAndRelease2Key();

		clickRestoreButton();
		sleeps();

		filter("1022",1);
		if(driver.getPageSource().contains("1022"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			filter("0999",1);
			if(driver.getPageSource().contains("0999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+ConsultationCancelReason_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='0999']")).click();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(consultationCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createConsultation();

		sleeps();sleeps();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			System.out.println("entered");
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
			clickOKButton();
			clickDeleteButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
		}

	}

	@Test(groups = {"Consultation"},priority=27)
	void Consultation_filterAndDeletion() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Filter and Deletion of Consultation Screen ");	

				
		navigateToConsultation();
		sleeps();
		filter(consultationCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(consultationCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(consultationCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(consultationCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(consultationCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		sleeps();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(consultationCode))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(consultationCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(consultationCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(consultationCode))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Formulaire Word des notes spécifiques | Management of local tables - Custom note word form");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		sleeps();
		sleeps();
		

		navigateToConsultation();
		filter (consultationCode,1);
		if(!driver.getPageSource().contains(consultationCode))
		{
			createTest.log(Status.PASS,"Created Institution record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		
		verifyClearFilter();
		filter("0052",1);
		clickReservedFilter();

		if(driver.getPageSource().contains("0052"))
		{
			System.out.println(" contains in reserved-3");
			createTest.log(Status.PASS,"Reserved record '0052' exist in Reserved filter");
			driver.findElement(By.xpath("//span[text()='0052']")).click();
			if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')]//span[text()='0052']")).isDisplayed())
			{
				createTest.log(Status.PASS,"verified - Reserved record is not editable");
			}
			else
			{
				createTest.log(Status.FAIL,"Reserved record is  editable");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Reserved record not found under reserved filter");
		}

	}

	//*********************************************Custom Note Word Form******************************************************



	void navigateToCustomNoteWordForm() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement customNoteWordFormMenu = driver.findElement(By.xpath("//div[text()=' Formulaire Word des notes spécifiques'] | //div[text()=' Custom note word form']"));
		customNoteWordFormMenu.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}

	@Test(groups = {"CustomNoteWordForm"},priority=28)
	public void createCustomNoteWordFormValidation() throws InterruptedException, AWTException
	{
		String customDocName="ASA";

		String pageNameEnglish= "Custom note word form";
		String pageNameFrench = "Formulaire Word des notes spécifiques";

		createTest=reports.createTest("Verifying Custom Note Word Form");	

		navigateToCustomNoteWordForm();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);



		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		driver.findElement(By.xpath("(//mat-select[@role='combobox'])[1]")).click();
		pressAndReleaseEnterKey();
		moveToElementAndClick("//mat-option//span//div[contains(text(),'"+customDocName+"')]");
		clickRestoreButton();
		clickSaveButton();
		createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
		createTest.log(Status.PASS,"The new templates for the custom note word form is added in the Elements note word form screen ");
		driver.findElement(By.xpath("(//mat-select[@role='combobox'])[1]")).click();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+customDocName+"')]")).click();
		clickHome();

		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickNoButton();
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}

	}

	//********************************************************Destination After Triage*********************************************8



	void navigateToDestinationAfterTriage() throws InterruptedException
	{
		navigateToPredefinedTables();
		WebElement destinationAfterTriage = driver.findElement(By.xpath("//div[text()=' Destination après triage'] | //div[text()=' Destination after triage']"));
		destinationAfterTriage.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();

		//		
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()=' Gestion des tables locales - Destination après triage'] | //h2[contains(text(),' Management of local tables - Destination after triage')]"))).click();
		//		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		//		{
		//			clickNoButton();
		//			
		//		}
		if(driver.findElement(By.xpath("//h2[contains(text(),' Gestion des tables locales - Destination après triage')] | //h2[contains(text(),' Management of local tables - Destination after triage')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"'Management of local tables - Destination after triage’ pop up is shown when clicked on the menu item Destination after triage ");
			clickOKButton();
		}
		else
		{
			createTest.log(Status.FAIL,"'Management of local tables - Destination after triage’ pop up is not shown when clicked on the menu item Destination after triage ");

		}
		sleeps();
	}

	@Test(groups = {"DestinationAfterTriage"},priority=29)
	void DestinationAfterTriageValidation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "Destination after triage";
		String pageNameFrench = "Destination après triage";
		String triageDesc = "Civière";
		createTest=reports.createTest("Verifying Destination After Triage");	

		navigateToDestinationAfterTriage();
		sleeps();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter("01",1);
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='01']")).click();
		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='01']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Code column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Code column is editable");
		}

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][2]")).click();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseDeleteKey();
		sleeps();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testFR");
		pressAndReleaseTabKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testEN");
		clickRestoreButton();
		sleeps();
		if(verifyButtonDisabled(createButton, "Create Button"))
		{

			if(verifyButtonDisabled(deleteButton, "Delete Button"))
			{
				createTest.log(Status.PASS,"Create and  Delete are disabled, even after row was edited");
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create and  Delete are enabled, even after row was edited");

		}
		clickSaveButton();
		filter(triageDesc,2);
		if(driver.getPageSource().contains(triageDesc))
		{
			createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
			createTest.log(Status.PASS,"User is able to update the values in all the columns except Code column");
			createTest.log(Status.PASS,"Save Button is enabled when row was edited");
			createTest.log(Status.PASS,"Row is successfully edited");
		}
		else
		{
			createTest.log(Status.FAIL,"Restore Button is not enabled when existing record is edited");
			createTest.log(Status.FAIL,"User is not able to update the values in all the columns except Code column");
			createTest.log(Status.FAIL,"Save Button is not enabled when row was edited");
			createTest.log(Status.FAIL,"Row is not successfully edited");
		}
		clickAllFilter();
		if(driver.getPageSource().contains(triageDesc))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(triageDesc))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(triageDesc))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!driver.getPageSource().contains(triageDesc))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made computer inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(triageDesc))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(triageDesc))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(triageDesc))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(triageDesc))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Formulaire Word des notes spécifiques | Management of local tables - Custom note word form");
							WebElement  msg = driver.findElement(By.xpath("//p"));
							verifyText(msg,"Confirmation Message","Voulez-vous enregistrer les données?","Do you want to save the data?");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
	}

	
//*********************************************************DIAGNOSIS********************************************************

    String diagnosisCode = "9"+RandomStringUtils.randomNumeric(7);
    String Abbreviation = RandomStringUtils.randomAlphabetic(6);
	
       
       void navigateTodiagnosis() throws InterruptedException
       {
       	navigateToPredefinedTables();
       	WebElement Diagnosis = driver.findElement(By.xpath("//div[text()=' Diagnosis'] | //div[text()=' Diagnostic']"));
		Diagnosis.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

       	Thread.sleep(50000);
       		               
       	sleeps();
       }
       
       void createDiagnosis() throws InterruptedException, AWTException
       {
       	
    	   clickCreateButton();
			driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(diagnosisCode);
			sleeps();
			pressAndReleaseTabKey();
			sleeps();
			pressAndReleaseTabKey();
			sleeps();
			driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
			sleeps();
			pressAndReleaseTabKey();
			sleeps();
			driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
			sleeps();
			pressAndReleaseTabKey();
			sleeps();
			sleeps();
			driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(Abbreviation);
			sleeps();
			pressAndReleaseTabKey();
			sleeps();
			pressAndReleaseSpaceKey();
			sleeps();
			
			filter(Abbreviation,4);
			
			driver.findElement(By.xpath("(//mat-select)[2]")).click();
			sleeps();
			moveToElementAndClick("//span[text()='A099']");
			driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
			clickSaveButton();
       		
       	
       }
       
       @Test(groups = {"Diagnosis"},priority=30)
       
       void diagnosisValidation() throws InterruptedException, AWTException
       {
    	   String diagnosis_screenNameEN= "Diagnosis";
			String diagnosis_screenNameFR = "Diagnostic";
     	
       	createTest=reports.createTest("Verifying diagnosis Screen");	
       	navigateTodiagnosis();
       	//zoomOutScreen();
       	verifyBreadCrumb(diagnosis_screenNameEN, diagnosis_screenNameFR);
       	
       	        
       	
       	//Verify restore, delete, save an continue buttons are disabled
       	WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
       	WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
       	WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
       	WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
       					
       	//Verify Default status of buttons
       	if(verifyButtonEnabled(createButton, "Create Button"))
       	{
       		if(verifyButtonDisabled(restoreButton,"Restore Button"))
       		{
       			if(verifyButtonDisabled(deleteButton,"Delete Button"))
       			{
       				if(verifyButtonDisabled(saveButton, "Save Button"))
       				{
       					createTest.log(Status.PASS,"Create is enabled,Delete, Restore and Save Buttons are disabled  by default");
       				}
       				else
       				{
       					createTest.log(Status.FAIL,"Save  button is not disabled by default");
     				
       				}
       			}
       			else
       			{
       				createTest.log(Status.FAIL,"Delete button is not enabled by default");
     			
       			}
       		}
       		else
       		{
       			createTest.log(Status.FAIL,"Restore button is not disabled by default");
     		
       		}
       	}
       	else
       	{
       		createTest.log(Status.FAIL,"Create button is not enabled by default");
     	
       	}	

       	filter(diagnosisCode,1);
       	if(driver.getPageSource().contains(diagnosisCode))
       	{
       		driver.findElement(By.xpath("//span[text()='"+diagnosisCode+"']")).click();
       		clickDeleteButton();
       		clickSaveButton();
       		Thread.sleep(42000);
       	}
       	
      
       	createDiagnosis();
       	Thread.sleep(42000);
		filter(diagnosisCode,1);
	
		
		
		if(driver.getPageSource().contains(diagnosisCode))
		{
		
			createTest.log(Status.PASS,"Successfully created new row in "+diagnosis_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+diagnosis_screenNameEN);
		}
	
		//Edit-----------------
		String restoreCode ="9"+RandomStringUtils.randomNumeric(7);
		driver.findElement(By.xpath("//span[text()='"+diagnosisCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(restoreCode);
   		
	
		sleeps();
	
	
		clickSaveButton();
		Thread.sleep(50000);
	
		filter(restoreCode,1);
		if(driver.getPageSource().contains(restoreCode))
		{
		
			createTest.log(Status.PASS,"Successfully edited new row in "+diagnosis_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+diagnosis_screenNameEN);
		}
	
		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+restoreCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("90808887");
   		
		clickRestoreButton();
		sleeps();
	
		filter("90808887",1);
		if(driver.getPageSource().contains("90808887"))
		{
		
			createTest.log(Status.FAIL,"Restore Button is not working fine in "+diagnosis_screenNameEN);
		}
		else
		{
			filter(restoreCode,1);
			if(driver.getPageSource().contains(restoreCode))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+diagnosis_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+diagnosis_screenNameEN);

			}
		}
	
		driver.findElement(By.xpath("//span[text()='"+restoreCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(diagnosisCode);
		sleeps();
		clickSaveButton();
		Thread.sleep(50000);
	
	//create duplicate-----------------
	createDiagnosis();
	sleeps();
	if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
    {	
		createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+diagnosis_screenNameEN);
		clickOKButton();
		clickRestoreButton();
		sleeps();
		clickSaveButton();
		Thread.sleep(50000);
    }
	else
	{
		createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+diagnosis_screenNameEN);
	}
  }
       
       @Test(groups = {"Diagnosis"},priority=31)
       void filterRecords_diagnosis() throws InterruptedException, AWTException
       {	
    	   createTest=reports.createTest("Verifying Filter ");	
//       LaunchApplication();//delete after single test
//       
//       navigateTodiagnosis();
//       sleeps();
//       sleeps();
//       sleeps();
//       sleeps();
       
       filter(diagnosisCode,1);
       clickAllFilter();
       if(driver.getPageSource().contains(diagnosisCode))
       {
       	System.out.println("contains in ALl-1");
       	clickActiveFilter();
			
       	if(driver.getPageSource().contains(diagnosisCode))
       	{
       		System.out.println("contains in Active-1");
       		sleeps();
       		clickNonActiveFilter();
       		if(!driver.getPageSource().contains(diagnosisCode))
       		{
       			System.out.println(" not contains in nonActive-1");
       			sleeps();
       			clickReservedFilter();

       			if(!driver.getPageSource().contains(diagnosisCode))
       			{
       				System.out.println("not contains in reserved1");
       				createTest.log(Status.PASS,"Active filter is working fine");
       			}
       			else
       			{
       				createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
       			}
       		}
       		else
       		{
       			createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
       		}
       	}
       	else
       	{
       		createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
       	}
       }
       else
       {
       	createTest.log(Status.FAIL,"Active filter is not working");
       }
       
       clickAllFilter();

       sleeps();
       WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
       activeCheckbox1.click();
       pressAndReleaseSpaceKey();
       clickSaveButton();
       Thread.sleep(50000);
       
       System.out.println("made record inactive"); 
       //Check if already present in ALl filter
       if(driver.getPageSource().contains(diagnosisCode))
       {
       	System.out.println("contains in Alle-2");
       	clickActiveFilter();
       				
       	if(!driver.getPageSource().contains(diagnosisCode))
       	{
       		System.out.println("not contains in Active-2");
       		clickNonActiveFilter();
       				
       		if(driver.getPageSource().contains(diagnosisCode))
       		{
       			System.out.println(" contains in Non Active-2");
       			clickReservedFilter();
       					
       			if(!driver.getPageSource().contains(diagnosisCode))
       			{
       				System.out.println(" not contains in reserved-2");
       				createTest.log(Status.PASS,"Non Active filter is working fine");
       				createTest.log(Status.PASS,"All filter is working fine");
       				clickAllFilter();
       			
       				sleeps();
       				WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
       				activeCheckbox2.click();
       				clickDeleteButton();
       				clickHome();
       				if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
       				{
       					createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
       					clickYesButton();
       					Thread.sleep(50000);
       				}
       				else
       				{
       					createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
       				}
       				
       			}
       			
       		}
       	}
       }
       else
       {
       	createTest.log(Status.FAIL,"Non Active filter is not working");
       }
       
       
       navigateTodiagnosis();
       filter (diagnosisCode,1);
       if(!driver.getPageSource().contains(diagnosisCode))
       {
       	createTest.log(Status.PASS,"Created diagnosis record is successfully deleted!!!");
       }
       else
       {
       	createTest.log(Status.FAIL,"Deletion is not working properly");
       }
       //verifyClearFilter();
       clickClearFilterButton();
       
       filter("0015",1);
       clickReservedFilter();
       
       if(driver.getPageSource().contains("0015"))
       {
       	System.out.println(" contains in reserved-3");
       	createTest.log(Status.PASS,"Reserved record '0015' exist in Reserved filter");
       	driver.findElement(By.xpath("//span[text()='0015']")).click();
       	if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')]//span[text()='0015']")).isDisplayed())
       	{
       		createTest.log(Status.PASS,"verified - Reserved record is not editable");
       		createTest.log(Status.PASS,"Clear Filter is working fine");
       	}
       	else
       	{
       		createTest.log(Status.FAIL,"Reserved record is  editable");
       	}
       }
       else
       {
       	createTest.log(Status.FAIL,"Reserved record not found under reserved filter");
       }
       	
       }
   

	//****************************************************Institution*******************************************************************


    
    String institutionCode = "9"+RandomStringUtils.randomNumeric(7);
    
    void navigateToInstitution() throws InterruptedException
    {
    	navigateToPredefinedTables();
    	WebElement institution = driver.findElement(By.xpath("//div[text()=' Établissement'] | //div[text()=' Institution']"));
    	institution.click();
    	
    	if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

    	
    	sleeps();
    	sleeps();
    	sleeps();
    	sleeps();
    	if(driver.findElement(By.xpath("//h2[contains(text(),'Gestion des tables locales - Établissement')] | //h2[contains(text(),'Management of local tables - Institution')]")).isDisplayed())
    	{
    		createTest.log(Status.PASS,"'Management of local tables - Destination after triage’ pop up is shown when clicked on the menu item Destination after triage ");
    		clickOKButton();
    		Thread.sleep(50000);
    	}
    	else
    	{
    		createTest.log(Status.FAIL,"'Management of local tables - Destination after triage’ pop up is not shown when clicked on the menu item Destination after triage ");
    		
    	}
    	
    	sleeps();
    }
    
    void createInstitution() throws InterruptedException, AWTException
    {
    	
 	    clickCreateButton();
    		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(institutionCode);
    		System.out.println(institutionCode);
    		pressAndReleaseTabKey();
    		sleeps();
    		//pressAndReleaseSpaceKey();
    		pressAndReleaseTabKey();
    		sleeps();
    		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
    		sleeps();
    		pressAndReleaseTabKey();
    		sleeps();
    		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
    		sleeps();
    		pressAndReleaseTabKey();
    		sleeps();
    		pressAndReleaseSpaceKey();
    		sleeps();
    		pressAndReleaseTabKey();
    		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
    		sleeps();
    		pressAndReleaseTabKey();
    		sleeps();
    		pressAndReleaseSpaceKey();
    		sleeps();
    		pressAndReleaseTabKey();
    		sleeps();
    		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
    		sleeps();
    		pressAndReleaseTabKey();
    		sleeps();
    		pressAndReleaseSpaceKey();
    		sleeps();
    		pressAndReleaseTabKey();
    		sleeps();
    		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
    		sleeps();
    		pressAndReleaseTabKey();
    		sleeps();
    		pressAndReleaseSpaceKey();
    		sleeps();
    		clickSaveButton();
    		
    	
    }
    
    @Test(groups = {"Institution"},priority=32)
    
    void InstitutionValidation() throws InterruptedException, AWTException
    {
    	String institution_screenNameEN= "Institution";
    	String institution_screenNameFR = "Établissement";
  	
    	createTest=reports.createTest("Verifying Institution Screen");	
    	navigateToInstitution();
    	
    	verifyBreadCrumb(institution_screenNameEN, institution_screenNameFR);
    	
    	        
    	
    	//Verify restore, delete, save an continue buttons are disabled
    	WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
    	WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
    	WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
    	WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
    					
    	//Verify Default status of buttons
    	if(verifyButtonEnabled(createButton, "Create Button"))
    	{
    		if(verifyButtonDisabled(restoreButton,"Restore Button"))
    		{
    			if(verifyButtonEnabled(deleteButton,"Delete Button"))
    			{
    				if(verifyButtonDisabled(saveButton, "Save Button"))
    				{
    					createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
    				}
    				else
    				{
    					createTest.log(Status.FAIL,"Save  button is not disabled by default");
  				
    				}
    			}
    			else
    			{
    				createTest.log(Status.FAIL,"Delete button is not enabled by default");
  			
    			}
    		}
    		else
    		{
    			createTest.log(Status.FAIL,"Restore button is not disabled by default");
  		
    		}
    	}
    	else
    	{
    		createTest.log(Status.FAIL,"Create button is not enabled by default");
  	
    	}	

    	filter(institutionCode,1);
    	if(driver.getPageSource().contains(institutionCode))
    	{
    		driver.findElement(By.xpath("//span[text()='"+institutionCode+"']")).click();
    		clickDeleteButton();
    		clickSaveButton();
    		Thread.sleep(42000);
    	}
    	
   
    	createInstitution();
    	Thread.sleep(42000);
		filter(institutionCode,1);
	
		
		
		if(driver.getPageSource().contains(institutionCode))
		{
		
			createTest.log(Status.PASS,"Successfully created new row in "+institution_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+institution_screenNameEN);
		}
	
		//Edit-----------------
		String restoreCode ="9"+RandomStringUtils.randomNumeric(7);
		driver.findElement(By.xpath("//span[text()='"+institutionCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(restoreCode);
		
	
		sleeps();
	
	
		clickSaveButton();
		Thread.sleep(50000);
	
		filter(restoreCode,1);
		if(driver.getPageSource().contains(restoreCode))
		{
		
			createTest.log(Status.PASS,"Successfully edited new row in "+institution_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+institution_screenNameEN);
		}
	
		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+restoreCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("90808887");
		
		clickRestoreButton();
		sleeps();
	
		filter("90808887",1);
		if(driver.getPageSource().contains("90808887"))
		{
		
			createTest.log(Status.FAIL,"Restore Button is not working fine in "+institution_screenNameEN);
		}
		else
		{
			filter(restoreCode,1);
			if(driver.getPageSource().contains(restoreCode))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+institution_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+institution_screenNameEN);

			}
		}
	
		driver.findElement(By.xpath("//span[text()='"+restoreCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(institutionCode);
		sleeps();
		clickSaveButton();
		Thread.sleep(50000);
	
	//create duplicate-----------------
	createInstitution();
	sleeps();
	if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
 {	
		createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+institution_screenNameEN);
		clickOKButton();
		clickRestoreButton();
		sleeps();
		clickSaveButton();
		Thread.sleep(50000);
 }
	else
	{
		createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+institution_screenNameEN);
	}
    
//    @Test(groups = {"Institution"},priority=33)
//    void filterRecords_Institution() throws InterruptedException, AWTException
//    {	createTest=reports.createTest("Verifying Filter ");	
//    LaunchApplication();//delete after single test
//    
//    navigateToInstitution();
//    sleeps();
//    sleeps();
//    sleeps();
//    sleeps();
    
    filter(institutionCode,1);
    clickAllFilter();
    if(driver.getPageSource().contains(institutionCode))
    {
    	System.out.println("contains in ALl-1");
    	clickActiveFilter();
			
    	if(driver.getPageSource().contains(institutionCode))
    	{
    		System.out.println("contains in Active-1");
    		sleeps();
    		clickNonActiveFilter();
    		if(!driver.getPageSource().contains(institutionCode))
    		{
    			System.out.println(" not contains in nonActive-1");
    			sleeps();
    			clickReservedFilter();

    			if(!driver.getPageSource().contains(institutionCode))
    			{
    				System.out.println("not contains in reserved1");
    				createTest.log(Status.PASS,"Active filter is working fine");
    			}
    			else
    			{
    				createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
    			}
    		}
    		else
    		{
    			createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
    		}
    	}
    	else
    	{
    		createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
    	}
    }
    else
    {
    	createTest.log(Status.FAIL,"Active filter is not working");
    }
    
    clickAllFilter();

    sleeps();
    WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
    activeCheckbox1.click();
    pressAndReleaseSpaceKey();
    clickSaveButton();
    Thread.sleep(50000);
    
    System.out.println("made record inactive"); 
    //Check if already present in ALl filter
    if(driver.getPageSource().contains(institutionCode))
    {
    	System.out.println("contains in Alle-2");
    	clickActiveFilter();
    				
    	if(!driver.getPageSource().contains(institutionCode))
    	{
    		System.out.println("not contains in Active-2");
    		clickNonActiveFilter();
    				
    		if(driver.getPageSource().contains(institutionCode))
    		{
    			System.out.println(" contains in Non Active-2");
    			clickReservedFilter();
    					
    			if(!driver.getPageSource().contains(institutionCode))
    			{
    				System.out.println(" not contains in reserved-2");
    				createTest.log(Status.PASS,"Non Active filter is working fine");
    				createTest.log(Status.PASS,"All filter is working fine");
    				clickAllFilter();
    				//clickAllFilter();
//    				WebElement allFilter1 = driver.findElement(By.xpath("(//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-outer-circle']"));
//    				allFilter1.click();
    				sleeps();
    				WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
    				activeCheckbox2.click();
    				clickDeleteButton();
    				clickHome();
    				if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
    				{
    					createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
    					clickYesButton();
    					Thread.sleep(50000);
    				}
    				else
    				{
    					createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
    				}
    				
    			}
    			
    		}
    	}
    }
    else
    {
    	createTest.log(Status.FAIL,"Non Active filter is not working");
    }
    
    
    navigateToInstitution();
    filter (institutionCode,1);
    if(!driver.getPageSource().contains(institutionCode))
    {
    	createTest.log(Status.PASS,"Created Institution record is successfully deleted!!!");
    }
    else
    {
    	createTest.log(Status.FAIL,"Deletion is not working properly");
    }
    verifyClearFilter();
    filter("06269858",1);
    clickReservedFilter();
    
    if(driver.getPageSource().contains("06269858"))
    {
    	System.out.println(" contains in reserved-3");
    	createTest.log(Status.PASS,"Reserved record '06269858' exist in Reserved filter");
    	driver.findElement(By.xpath("//span[text()='06269858']")).click();
    	if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')]//span[text()='06269858']")).isDisplayed())
    	{
    		createTest.log(Status.PASS,"verified - Reserved record is not editable");
    	}
    	else
    	{
    		createTest.log(Status.FAIL,"Reserved record is  editable");
    	}
    }
    else
    {
    	createTest.log(Status.FAIL,"Reserved record not found under reserved filter");
    }
    }
//    }
	//************************************************************Interveners********************************************************************
	String Interveners_screenNameFR= "Gestion des tables locales - Intervenants";
	String Interveners_screenNameEN="Management of local tables - Interveners";
	String Interveners_pageNameFR= "Intervenants";
	String Interveners_pageNameEN="Interveners";

	String IntervenersCode = "10001030";

	void navigateToInterveners() throws InterruptedException
	{

		navigateToManagementMenu();
		WebElement consulationCancelReason_Link = driver.findElement(By.xpath("//div[contains(text(),' Intervenants')] | //div[contains(text(),' Interveners')]"));
		consulationCancelReason_Link.click();
		
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(Interveners_screenNameFR)) | (driver.getPageSource().contains(Interveners_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ Interveners_screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+Interveners_screenNameEN);

		}

		sleeps();
	}

	void createInterveners() throws InterruptedException, AWTException
	{
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(IntervenersCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		clickSaveButton();

	}

	@Test(groups = {"Interveners"},priority=33)
	void Interveners_Validation() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Screen " + Interveners_screenNameEN);	

		navigateToInterveners();

		verifyBreadCrumb(Interveners_pageNameEN, Interveners_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(IntervenersCode,1);
		if(driver.getPageSource().contains(IntervenersCode))
		{
			driver.findElement(By.xpath("//span[text()='"+IntervenersCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
			driver.navigate().refresh();
			sleeps();
		}



		createInterveners();
		filter(IntervenersCode,1);

		if(driver.getPageSource().contains(IntervenersCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+Interveners_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+Interveners_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+IntervenersCode+"']")).click();
		pressAndReleaseControlAKey();pressAndReleaseBackSPaceKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("10000999");

		sleeps();


		clickSaveButton();
		sleeps();
		sleeps();
		
		filter("10000999",1);
		if(driver.getPageSource().contains("10000999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+Interveners_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+Interveners_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"10000999"+"']")).click();
		pressAndReleaseControlAKey();pressAndReleaseBackSPaceKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("10000311");

		clickRestoreButton();
		sleeps();

		filter("10000311",1);
		if(driver.getPageSource().contains("10000311"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+Interveners_screenNameEN);
		}
		else
		{
			filter("10000999",1);
			if(driver.getPageSource().contains("10000999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+Interveners_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+Interveners_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"10000999"+"']")).click();
		pressAndReleaseControlAKey();pressAndReleaseBackSPaceKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(IntervenersCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createInterveners();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+Interveners_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+Interveners_screenNameEN);
		}

	}

	String domainCode = "9920";
	@Test(groups = {"Interveners"},priority=34 )
	void filterRecords_Interveners() throws InterruptedException, AWTException
	{
		filter(IntervenersCode,1);

		//Domain button verification

		driver.findElement(By.xpath("//span[text()='"+IntervenersCode+"']")).click();
		driver.findElement(By.xpath("//button[text()='Domaine'] | //button[text()='Domain']")).click();
		sleeps();
		if(driver.getPageSource().contains("Domaine(s) de consultation (Intervenants)") | driver.getPageSource().contains("Consultation domain(s) (Interveners)") )
		{
			createTest.log(Status.PASS,"Domain window is opened when Domain button was clicked" );

		}
		else
		{
			createTest.log(Status.FAIL,"Domain window is not opened when Domain button was clicked" );

		}
		sleeps();
		WebElement createButton1 = driver.findElement(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]"));
		createButton1.click();
		sleeps();

		if((driver.getPageSource().contains("Choix du domaine de consultation")) | (driver.getPageSource().contains("Choice of consultation domain")) )
		{	
			createTest.log(Status.PASS,"Verified Choice of Domain window is opened when clicked on Create Domain button");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verify Choice of Domain window is opened when clicked on Create Domain button");
		}

		driver.findElement(By.xpath("//span[text()="+domainCode+"]")).click();
		driver.findElement(By.xpath("//button[contains(text(),'OK')]")).click();
		//Thread.sleep(7000);

		clickSaveAndContinueButton();
		sleeps();

		clickSaveAndCloseButton();
		sleeps();

		driver.findElement(By.xpath("//button[text()='Domaine'] | //button[text()='Domain']")).click();
		sleeps();

		if(driver.findElement(By.xpath("//span[text()="+domainCode+"]")).isDisplayed())
		{
			createTest.log(Status.PASS,"Domain is successfully added to Intervener");
		}
		else
		{
			createTest.log(Status.FAIL,"Domain is not successfully added to Intervener");


		}

		driver.findElement(By.xpath("//span[text()="+domainCode+"]")).click();
		sleeps();
		driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]")).click();
		clickSaveAndContinueButton();
		sleeps();

		if(!driver.getPageSource().contains(domainCode))
		{
			createTest.log(Status.PASS,"Domain is succesfully deleted in "+Interveners_pageNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Domain is not  deleted in "+Interveners_pageNameEN );

		}


		sleeps();
		clickCloseIcon();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )

		{
			clickYesButton();
		}

		sleeps();

		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		clickAllFilter();
		if(driver.getPageSource().contains(IntervenersCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(IntervenersCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(IntervenersCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(IntervenersCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox2.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(IntervenersCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(IntervenersCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(IntervenersCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(IntervenersCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox3 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox3.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();
							sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);
		navigateToInterveners();
		filter (IntervenersCode,1);
		if(!driver.getPageSource().contains(IntervenersCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
	}  

	//************************************************Payment Responsibility**********************************************************************

	String prDesc = "MSSS avec accident";

	void navigateToPaymentResponsibility() throws InterruptedException
	{
		navigateToPredefinedTables();
		WebElement paymentResponsibility = driver.findElement(By.xpath("//div[contains(text(),' Responsabilité Paiement')] | //div[text()=' Payment responsibility']"));
		paymentResponsibility.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		Thread.sleep(8000);
		//               
		if(driver.findElement(By.xpath("//h2[contains(text(),'Gestion des tables locales - Responsabilité Paiement')] | //h2[contains(text(),'Management of local tables - Payment responsibility')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"'Management of local tables - Payment responsibility’ pop up is shown when clicked on the menu item Payment Responsibility");
			clickOKButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"'Gestion des tables locales - Responsabilité Paiement’ pop up is not shown when clicked on the menu Payment Responsibility");

		}
		sleeps();
	}
	@Test(groups = {"PaymentResponsibility"},priority=35)
	void paymentResponsibilityValidation() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Payment Responsibilty");	

		String pageNameEnglish= "Payment responsibility";
		String pageNameFrench = "Responsabilité Paiement";

		navigateToPaymentResponsibility();
		sleeps();
		sleeps();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter("02",1);
		driver.findElement(By.xpath("//span[text()='02']")).click();
		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='02']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Code column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Code column is editable");
		}

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][2]")).click();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseDeleteKey();
		sleeps();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testFR");
		pressAndReleaseTabKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testEN");
		clickRestoreButton();
		sleeps();
		if(verifyButtonDisabled(createButton, "Create Button"))
		{

			if(verifyButtonDisabled(deleteButton, "Delete Button"))
			{
				createTest.log(Status.PASS,"Create and  Delete are disabled, even after row was edited");
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create and  Delete are enabled, even after row was edited");

		}
		clickSaveButton();
		filter(prDesc,2);
		if(driver.getPageSource().contains(prDesc))
		{
			createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
			createTest.log(Status.PASS,"User is able to update the values in all the columns except Code column");
			createTest.log(Status.PASS,"Save Button is enabled when row was edited");
			createTest.log(Status.PASS,"Row is successfully edited");
		}
		else
		{
			createTest.log(Status.FAIL,"Restore Button is not enabled when existing record is edited");
			createTest.log(Status.FAIL,"User is not able to update the values in all the columns except Code column");
			createTest.log(Status.FAIL,"Save Button is not enabled when row was edited");
			createTest.log(Status.FAIL,"Row is not successfully edited");
		}
		clickAllFilter();
		if(driver.getPageSource().contains(prDesc))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(prDesc))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(prDesc))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!driver.getPageSource().contains(prDesc))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made  inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(prDesc))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(prDesc))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(prDesc))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();


					if(!driver.getPageSource().contains(prDesc))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();
						//               					
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Responsabilité Paiement | Management of local tables - Payment responsibility");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
	}



	//*************************************UNITS**********************************************************


	String unitName = RandomStringUtils.randomAlphanumeric(4);
	String unitDescFrench = RandomStringUtils.randomAlphanumeric(4);
	String unitDescEnglish = RandomStringUtils.randomAlphanumeric(4);


	String emergencyUnitName_Existing="urg";
	String medSoinUnitFrench="Unités Med-Soins+";
	String medSoinUnitEnglish="Med-Soins+ units";
	String otherUnitFrench="Autres unités";
	String otherUnitEnglish="Others units";
	String emergencyUnitFrench="Urgence";
	String emergencyUnitEnglish="Emergency";

	String unit3eDescFR="Médecine Soins Intensifs";
	String unit3eDescEN="Medecine Intensive Care";
	String unit4eDescFR="Médecine Chirurgie";
	String unit4eDescEN="Medecine Surgery";
	String unitdebDescFR="Médecine Débordement";
	String unitdebDescEN="Medecine Overflow";
	String unitUrgDescFR="Urgence";
	String unitUrgDescEN="Emergency";


	void navigateToUnit() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement unitsMenu = driver.findElement(By.xpath("//div[text()=' Unités'] | //div[text()=' Unit']"));
		unitsMenu.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}


	void createNewUnitRecord() throws InterruptedException, AWTException
	{
		clickCreateButton();
		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(unitName);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(unitDescFrench);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(unitDescEnglish);
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();

		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+otherUnitFrench+"')] | //mat-option//span//div[contains(text(),'"+otherUnitEnglish+"')]")).click();
		clickSaveButton();              	
	}

	public void createMedSoinUnit(String unitName, String UnitTypeFN, String UnitTypeEN) throws InterruptedException, AWTException
	{

		navigateToManagementMenu();

		navigateToUnit();
		sleeps();


		if(driver.getPageSource().contains(unitName)) 
		{
			//delete the record
			System.out.println("unitName already exist, hence deleting and creating new");
			driver.findElement(By.xpath("//div//span[text()='"+unitName+"']")).click();
			clickDeleteButton();
			clickSaveButton();

		}
		//Create new
		createNewUnitRecord();
		if((driver.getPageSource().contains("Your license allows 3 Med-Soins+ units, please deactivate a unit to create a new one"))|(driver.getPageSource().contains("Unités Med-Soins+"))) 
		{
			createTest.log(Status.FAIL,"More than 3 MedSoins type unit exist, hence not able to create Unit");
		}

	}
	@Test(groups = {"Unit"},priority=36)
	public void Unit_Creation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "Unit";
		String pageNameFrench = "Unités";

		createTest=reports.createTest("Verifying Unit SCreen");	
		navigateToUnit();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, and Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}

		filter(unitName,1);
		if(driver.getPageSource().contains(unitName))
		{
			driver.findElement(By.xpath("//span[text()='"+unitName+"']")).click();
			clickDeleteButton();
			clickSaveButton();

		}
		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{
			driver.findElement(By.xpath("//span[text()='"+999+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
		createNewUnitRecord();
		filter(unitName,1);
		if(driver.getPageSource().contains(unitName) )
		{	
			createTest.log(Status.PASS,"Successfully created unit");
		}
		else
		{
			createTest.log(Status.FAIL,"Unit is not created");
		} 	

		//Create duplicate
		createNewUnitRecord();
		if((driver.getPageSource().contains("Data extraction and/or saving command failed."))||(driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué.")) )
		{	 
			System.out.println("Forbidden to create more duplicate unit");

			createTest.log(Status.PASS,"User is not allowed to create duplicate Unit");
			clickOKButton();
			clickDeleteButton();
			clickSaveButton();

		}

		//Edit-----------------

		filter(unitName,1);
		driver.findElement(By.xpath("//span[text()='"+unitName+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();



		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in Unit Screen ");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in Unit screen");
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in Unit Screen");
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in Unit Screen");
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in Unit Screen");

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(unitName);
		sleeps();
		clickSaveButton();
		sleeps();


	}

	@Test(groups = {"Unit"},priority=37)
	void Unit_filterRecords_Verification() throws InterruptedException, AWTException
	{
		filter(unitName,1);
		clickAllFilter();
		if(driver.getPageSource().contains(unitName))
		{
			System.out.println("contains in All-1");
			clickActiveFilter();
			sleeps();
			if(driver.getPageSource().contains(unitName))
			{
				System.out.println("contains in Active-1");
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(unitName))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!driver.getPageSource().contains(unitName))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made unit inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(unitName))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(unitName))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(unitName))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(unitName))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();
						driver.findElement(By.xpath("//span[text()='"+unitName+"']")).click();
						clickDeleteButton();
						
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();

							sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
		Thread.sleep(5000);
		navigateToUnit();
		filter (unitName,1);
		if(!driver.getPageSource().contains(unitName))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
	}



	//*******************************************************Station Alarm Management************************************************

	String stationAlarmCode="StationTest";
	String descFrench="descFrench";
	String descEnglish="descEnglish";
	String StationAlarm_screenNameEN= "Stations alarm management";
	String StationAlarm_screenNameFR = "Postes avertisseur";

	void navigateToStationAlarmManagement() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement stationAlarmManagementMenu = driver.findElement(By.xpath("//div[text()=' Postes avertisseur'] | //div[text()=' Stations alarm management']"));
		stationAlarmManagementMenu.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}

	void createStationAlarm() throws InterruptedException, AWTException
	{
		clickCreateButton();
		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(stationAlarmCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(descFrench);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(descEnglish);
		clickSaveButton();
		sleeps();
	}


	@Test(groups = {"StationAlarmManagement"},priority=38)

	public void StationAlarmManagementRecord_creation() throws InterruptedException, AWTException 
	{
		createTest=reports.createTest("Verifying Station Alarm Management Screen");	

		navigateToStationAlarmManagement();
		verifyBreadCrumb(StationAlarm_screenNameEN, StationAlarm_screenNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create adn  Delete are enabled, and Restore and Save Buttons are disabled  by deafult");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}
		filter(stationAlarmCode,1);
		if(driver.getPageSource().contains(stationAlarmCode))
		{
			driver.findElement(By.xpath("//span[text()='"+stationAlarmCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();

			sleeps();
		}



		createStationAlarm();
		filter(stationAlarmCode,1);

		if(driver.getPageSource().contains(stationAlarmCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+StationAlarm_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+StationAlarm_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+stationAlarmCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();


		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+StationAlarm_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+StationAlarm_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+StationAlarm_screenNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+StationAlarm_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+StationAlarm_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(stationAlarmCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createStationAlarm();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+StationAlarm_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+StationAlarm_screenNameEN);
		}
	}//function end

	@Test(groups = {"StationAlarmManagement"},priority=39)
	void StationAlarmManagement_filterAndDeletion() throws InterruptedException, AWTException
	{
		navigateToStationAlarmManagement();
		filter(stationAlarmCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(stationAlarmCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(stationAlarmCode))
			{
				System.out.println("contains in Active-1");
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(stationAlarmCode))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!driver.getPageSource().contains(stationAlarmCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made alarm inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(stationAlarmCode))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(stationAlarmCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(stationAlarmCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(stationAlarmCode))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		clickAllFilter();

		sleeps();
		WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox2.click();
		clickDeleteButton();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
			sleeps();

			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}

		navigateToStationAlarmManagement();
		filter (stationAlarmCode,1);
		if(!driver.getPageSource().contains(stationAlarmCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
	}

	//**************************************************************Stats Configuration By Computer****************************8


	String computerName =RandomStringUtils.randomAlphanumeric(4); ;



	void navigateToStatsConfigByComputer() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement stationAlarmManagementMenu = driver.findElement(By.xpath("//div[text()=' *Configuration des statistiques par poste'] | //div[text()=' *Stats configuration by computer']"));
		stationAlarmManagementMenu.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}


	public void createStatsConfigByComputer() throws InterruptedException, AWTException
	{
		clickCreateButton();
		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(computerName);
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+unitUrgDescFR+"')] | //mat-option//span//div[contains(text(),'"+unitUrgDescEN+"')]")).click();
		clickSaveButton();


	}

	@Test(groups = {"StatsConfigurationByComputer"},priority=40)
	public void StatsByConfiguration_Creation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "*Stats configuration by computer";
		String pageNameFrench = "*Configuration des statistiques par poste";
		createTest=reports.createTest("Verifying Stats Configuration By Computer Screen");	
		navigateToStatsConfigByComputer();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, and Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}	

		createStatsConfigByComputer();
		filter(computerName,1);
		if(driver.getPageSource().contains(computerName))
		{
			createTest.log(Status.PASS,"New record is succesfully created in Stats Configuration By Computer");

		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+computerName+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();

		driver.navigate().refresh();
		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+ConsultationCancelReason_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+ConsultationCancelReason_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(computerName);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createStatsConfigByComputer();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
		}	


	}

	@Test(groups = {"StatsConfigurationByComputer"},priority=41)
	void StatsConfiguration_filterAndDeletion() throws InterruptedException, AWTException
	{
		filter(computerName,1);
		clickAllFilter();
		if(driver.getPageSource().contains(computerName))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(computerName))
			{
				System.out.println("contains in Active-1");
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(computerName))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!driver.getPageSource().contains(computerName))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made computer inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(computerName))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(computerName))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(computerName))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(computerName))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();
						driver.findElement(By.xpath("//span[text()='"+computerName+"']")).click();
						clickDeleteButton();
						clickSaveButton();
						filter(computerName,1);
						if(!driver.getPageSource().contains(computerName))
						{
							createTest.log(Status.PASS,"Deletion and Sort is working fine");
						}
						else
						{
							createTest.log(Status.FAIL,"Failed to delete unit");
						}
					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
		
		verifyClearFilter();
		clickHome();
	}

	//*************************************************Stretcher bed In Board**************************************************

	String stretcherCode_Bed = "Z001";
	String strecherCategory = "306-1";


	public void navigateToStrecherBedInBoard() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement stretcherMenu = driver.findElement(By.xpath("//div[text()=' Civières / Lits par Tableau'] | //div[text()=' Stretcher / Bed in Board']"));
		stretcherMenu.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();
		sleeps();
		sleeps();
		sleeps();
	}


	public void createStretcherbed() throws InterruptedException, AWTException 
	{
		navigateToStretcherBed();
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')][last()]//input")).sendKeys(stretcherCode_Bed);

		//Navigate to Active and make active by checking
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();

		//Navigate to category and select SI3
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+strecherCategory+"')]")).click();
		sleeps();

		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		sleeps();
		pressAndRelease2Key();
		clickSaveButton();
		System.out.println("Successfully created StretcherBed");
	}

	@Test(groups = {"StretcherBedInBoard"},priority=42)
	public void StretcherBedInBoard_Creation() throws InterruptedException, AWTException 
	{
		String pageNameEnglish ="Stretcher / Bed in Board";
		String pageNameFrench = "Civières / Lits par Tableau";

		createTest=reports.createTest("Verifying Stretchers/Bed in Board");	

		navigateToStrecherBedInBoard();
		createTest.log(Status.PASS,"Successfully navigated to Strecther Bed In Board");


		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		//Verify header
		sleeps();

		//Verify restore, delete, save an continue buttons are disabled
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Delete, Restore and Save Buttons are disabled  by deafult");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		
		filter(stretcherCode_Bed,1);
		filter(strecherCategory, 2);
		sleeps();

		driver.findElement(By.xpath("//span[text()='"+stretcherCode_Bed+"']")).click();
		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')]//span[text()='"+stretcherCode_Bed+"']")).isDisplayed())
		{
			pressAndReleaseTabKey();
			sleeps();
			pressAndReleaseTabKey();
			sleeps();
			pressAndRelease1Key();
			clickRestoreButton();
			if((driver.getPageSource().contains(stretcherCode_Bed)) && (driver.getPageSource().contains(strecherCategory)))
			{
				createTest.log(Status.PASS,"Code column is read only and other fields are editable");
				createTest.log(Status.PASS,"Restore button is working fine");
			}
			clickSaveButton();
		}

	}   


	@Test(groups = {"StretcherBedInBoard"},priority=43)
	void StretcherBedInBoard_FilteringAndDeletion() throws InterruptedException, AWTException
	{


		clickAllFilter();

		if((driver.getPageSource().contains(stretcherCode_Bed)) && (driver.getPageSource().contains(strecherCategory)))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if((driver.getPageSource().contains(stretcherCode_Bed)) && (driver.getPageSource().contains(strecherCategory)))
			{
				System.out.println("contains in Active-1");
				clickNonActiveFilter();

				if(!(driver.getPageSource().contains(strecherCategory)))	{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!(driver.getPageSource().contains(strecherCategory)))		{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");

					}
				}
			}
		}


		clickAllFilter();
		filter(stretcherCode_Bed,1);
		filter(strecherCategory, 2);
		sleeps();
		System.out.println("make stretcher in board inactive");

		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[@class='ag-cell ag-cell-not-inline-editing ag-cell-auto-height']//span//input)"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		sleeps();
		sleeps();
		System.out.println("made stretcher in board inactive yes"); 

		//Check if already present in ALl filter
		if((driver.getPageSource().contains(stretcherCode_Bed)) && (driver.getPageSource().contains(strecherCategory)))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!(driver.getPageSource().contains(strecherCategory)))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if((driver.getPageSource().contains(stretcherCode_Bed)) && (driver.getPageSource().contains(strecherCategory)))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!(driver.getPageSource().contains(strecherCategory)))	{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");

					}
				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}




		//Make strectcher active
		clickAllFilter();
		WebElement activeCheckbox = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')]//span//input"));
		activeCheckbox.click();
		pressAndReleaseSpaceKey();
		verifyClearFilter();

		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}




	}
	// **********************************************************TicketTerminalManagement**********************************			

	String tktTerminal = "terminalTest";
	String printerOptionFR="Autres";
	String printerOptionEN="Autres";
	String ticketCode = "tkt1";

	void navigateToTicketTerminalManagement() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement ticketTerminalMgmnt = driver.findElement(By.xpath("//div[text()=' Borne Billetterie configuration'] | //div[text()=' Ticket terminal management']"));
		ticketTerminalMgmnt.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}

	void createTicketTerminal() throws InterruptedException, AWTException
	{
		clickCreateButton();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys(ticketCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys(tktTerminal);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys("Ambulance");
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+printerOptionFR+"')]")).click();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		clickSaveButton();
	}
	@Test(groups = {"TicketTerminalManagement"},priority=44)
	public void createTicketTerminalManagement() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "Ticket terminal management";
		String pageNameFrench = "Borne Billetterie configuration";

		createTest=reports.createTest("Verifying Ticket Terminal Management Screen");	


		navigateToTicketTerminalManagement();

		sleeps();
		sleeps();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}	
		filter(ticketCode,1);
		if(driver.getPageSource().contains(ticketCode))
		{
			driver.findElement(By.xpath("//span[text()='"+ticketCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();

		}
		createTicketTerminal();
		filter(ticketCode,1);
		if(driver.getPageSource().contains(ticketCode))
		{
			createTest.log(Status.PASS,"Record is successfully created in Ticket terminal");

		}


		//Create duplicate
		createTicketTerminal();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{
			System.out.println("Duplicate is not created");
			createTest.log(Status.PASS,"User is not allowed to create a duplicate record in Ticket Terminal");
			clickOKButton();
			clickDeleteButton();
			clickSaveButton();
		}
		else
		{
			createTest.log(Status.FAIL,"Ticket Terminal is not successfully created");
		}


		//Edit-----------------
		filter(ticketCode,1);
		driver.findElement(By.xpath("//span[text()='"+ticketCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();

		driver.navigate().refresh();
		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in Ticket Terminal");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in Ticket Terminal");
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in Ticket Terminal");
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in Ticket Temrinal");
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in Ticket Terminal");

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(ticketCode);
		sleeps();
		clickSaveButton();
		sleeps();



	}


	@Test(groups = {"TicketTerminalManagement"},priority=45)
	void filter_records_TicketTerminal() throws InterruptedException, AWTException
	{
		filter(ticketCode,1);
		driver.findElement(By.xpath("//span[text()='"+ticketCode+"']")).click();
		pressAndRelease0Key();

		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickNoButton();
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}

		navigateToTicketTerminalManagement();
		filter(ticketCode,1);

		clickAllFilter();
		if(driver.getPageSource().contains(ticketCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(ticketCode))
			{
				System.out.println("contains in Active-1");
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(ticketCode))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();

					if(!driver.getPageSource().contains(ticketCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made computer inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(ticketCode))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(ticketCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(ticketCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(ticketCode))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();
						driver.findElement(By.xpath("//span[text()='"+ticketCode+"']")).click();
						clickDeleteButton();
						verifyClearFilter();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Transfert-Raison | Management of local tables - Transfer Reason");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
						
						navigateToTicketTerminalManagement();
						filter(ticketCode,1);
						if(!driver.getPageSource().contains(ticketCode))
						{
							createTest.log(Status.PASS,"Deletion and Sort is working fine");
						}
						else
						{
							createTest.log(Status.FAIL,"Failed to delete unit");
						}
					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
		
	}

	//********************************************Transfer Reason*************************************************************


	void navigateToTransferReason() throws InterruptedException
	{
		navigateToPredefinedTables();
		WebElement transferReason = driver.findElement(By.xpath("//div[contains(text(),' Transfert-Raison')] | //div[text()=' Transfer-Reason']"));
		transferReason.click();

		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();

		sleeps();
		sleeps();
		//               	
		if(driver.findElement(By.xpath("//h2[contains(text(),'Gestion des tables locales - Transfert-Raison')] | //h2[contains(text(),'Management of local tables - Transfer-Reason')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"'Management of local tables - Transfer Reason’ pop up is shown when clicked on the menu item Transfer Reason");
			WebElement popup = driver.findElement(By.xpath("//h2"));
			verifyText(popup,"Transfer Reason Popup","Management of local tables - Transfer-Reason", "Gestion des tables locales - Transfert-Raison");
			WebElement popupmsg = driver.findElement(By.xpath("//p"));
			verifyText(popupmsg,"Transfer Reason","The codes of this table are defined in the normative framework and cannot be modified directly. You can make minor corrections of the descriptions or render certain codes non-available if necessary (clinical services not present)", "Les codes de cette table sont définis dans le cadre normatif et ne peuvent pas être modifiés directement. Vous pouvez effectuer les corrections mineures des descriptions ou rendre non disponibles certains codes s’il y a lieu (services cliniques non présents, etc.)");
			clickOKButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"'Gestion des tables locales - Transfert-Raison’ pop up is not shown when clicked on the menu item Destination after triage ");

		}
		sleeps();
	}

	@Test(groups = {"TransferReason"},priority=46)
	void TransferReason_Creation() throws InterruptedException, AWTException
	{

		String pageNameEnglish= "Transfer-Reason";
		String pageNameFrench = "Transfert-Raison";
		String screenNameFR= "Gestion des tables locales - Transfert-Raison";
		String screenNameEN="Management of local tables - Transfer-Reason";


		createTest=reports.createTest("Verifying Transfer Reason");	

		navigateToTransferReason();
		sleeps();
		sleeps();
		sleeps();
		sleeps();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter("02",1);
		driver.findElement(By.xpath("//span[text()='02']")).click();
		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='02']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Code column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Code column is editable");
		}

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][2]")).click();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseDeleteKey();
		sleeps();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testFR");
		pressAndReleaseTabKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-inline-editing')][1]")).sendKeys("testEN");
		clickRestoreButton();
		sleeps();
		if(verifyButtonDisabled(createButton, "Create Button"))
		{

			if(verifyButtonDisabled(deleteButton, "Delete Button"))
			{
				createTest.log(Status.PASS,"Create and  Delete are disabled, even after row was edited");
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create and  Delete are enabled, even after row was edited");

		}
		clickSaveButton();
		filter("Patient connu (appartenance)",2);
		if(driver.getPageSource().contains("Patient connu (appartenance)"))
		{
			createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
			createTest.log(Status.PASS,"User is able to update the values in all the columns except Code column");
			createTest.log(Status.PASS,"Save Button is enabled when row was edited");
			createTest.log(Status.PASS,"Row is successfully edited");
		}
		else
		{
			createTest.log(Status.FAIL,"Restore Button is not enabled when existing record is edited");
			createTest.log(Status.FAIL,"User is not able to update the values in all the columns except Code column");
			createTest.log(Status.FAIL,"Save Button is not enabled when row was edited");
			createTest.log(Status.FAIL,"Row is not successfully edited");
		}
		clickAllFilter();
		if(driver.getPageSource().contains("Patient connu (appartenance)"))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains("Patient connu (appartenance)"))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains("Patient connu (appartenance)"))
				{
					System.out.println(" not contains in nonActive-1");
					clickReservedFilter();
					sleeps();
					if(!driver.getPageSource().contains("Patient connu (appartenance)"))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active filter is working fine");
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		clickAllFilter();

		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();
		System.out.println("made  inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains("Patient connu (appartenance)"))
		{
			System.out.println("contains in Alle-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains("Patient connu (appartenance)"))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains("Patient connu (appartenance)"))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();
					sleeps();

					if(!driver.getPageSource().contains("Patient connu (appartenance)"))
					{
						System.out.println(" not contains in reserved-2");
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						pressAndReleaseSpaceKey();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							createTest.log(Status.PASS,"Verified Confirmation window header - Gestion des tables locales - Transfert-Raison | Management of local tables - Transfer Reason");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}

					}

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
	}


	//********************************************************TriageStationManagement************************************************



	String triageCode =RandomStringUtils.randomAlphanumeric(5);
	void navigateToTriageStationsManagement() throws InterruptedException
	{
		navigateToManagementMenu();
		WebElement triageStationsManagement = driver.findElement(By.xpath("//div[text()=' Configuration des postes de Triage'] | //div[text()=' Triage stations management']"));
		triageStationsManagement.click();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
	}

	void createTriageStationManagement() throws InterruptedException, AWTException
	{
		clickCreateButton();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys(triageCode);
		pressAndReleaseTabKey();
		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])[1]//input")).sendKeys("stationStage1");
		clickSaveButton();
	}

	@Test(groups = {"TriageStationsManagement"},priority=47)
	public void TriageStationsManagementvalidation_Creation() throws InterruptedException, AWTException
	{
		String pageNameEnglish= "Triage stations management";
		String pageNameFrench = "Configuration des postes de Triage";

		String screenNameEN="Management of local tables - Triage stations management";
		String screenNameFR="Gestion des tables locales - Configuration des postes de Triage";

		createTest=reports.createTest("Verifying Triage Stations Management Screen");	

		navigateToTriageStationsManagement();
		sleeps();
		sleeps();
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not enabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not enabled by default");

		}	

		filter(triageCode,1);
		if(driver.getPageSource().contains(triageCode))
		{
			driver.findElement(By.xpath("//span[text()='"+triageCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();

		}


		createTriageStationManagement();
		filter(triageCode,1);

		if(driver.getPageSource().contains(triageCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+ConsultationCancelReason_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+triageCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();

		driver.navigate().refresh();
		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+ConsultationCancelReason_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+ConsultationCancelReason_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(triageCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createTriageStationManagement();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
		}

	}

	@Test(groups = {"TriageStationsManagement"},priority=48 )
	void TriageStation_filterAndDeletion() throws InterruptedException, AWTException
	{

		//createTest=reports.createTest("Verifying Filter and Deletion of Triage Station Management");	

		filter(triageCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(triageCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(triageCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(triageCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(triageCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(triageCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(triageCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(triageCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(triageCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();
							sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);
		navigateToTriageStationsManagement();
		filter (triageCode,1);
		if(!driver.getPageSource().contains(triageCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
			sleeps();
			sleeps();
		}
	}

	//*******************************************************Activities Cancel Reasons****************************************************

	String activitiesCancelReasonCode ="R1";

	String actCancel_pageNameEnglish= "Activities - Cancel reasons";
	String actCancel_pageNameFrench = "Activités - Raisons d'annulation";
	void navigateToActivitiesCancelReasons() throws InterruptedException
	{

		navigateToLocalTablesMenu();
		WebElement activitiesCancelReasons = driver.findElement(By.xpath("//div[text()=' Activities - Cancel reasons'] | //div[contains(text(),' Activités - Raisons')]"));
		activitiesCancelReasons.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();



		if((driver.getPageSource().contains("Gestion des tables locales - Activités - Raisons d'annulation")) | (driver.getPageSource().contains("Management of local tables - Activities - Cancel reasons")))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ actCancel_pageNameEnglish);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+actCancel_pageNameEnglish);

		}

		sleeps();
	}

	void createActivitiesCancelReason() throws InterruptedException, AWTException
	{

		clickCreateButton();

		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(activitiesCancelReasonCode);
		clickSaveButton();

	}
	@Test(groups = {"Activities - Cancel Reasons"},priority=49)
	void activitiesCancelReasonsValidation() throws InterruptedException, AWTException
	{

		createTest=reports.createTest("Verifying Screen " + actCancel_pageNameEnglish);	

		navigateToActivitiesCancelReasons();

		verifyBreadCrumb(actCancel_pageNameEnglish, actCancel_pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(activitiesCancelReasonCode,3);
		if(driver.getPageSource().contains(activitiesCancelReasonCode))
		{
			driver.findElement(By.xpath("//span[text()='"+activitiesCancelReasonCode+"']")).click();

			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
		}



		createActivitiesCancelReason();
		filter(activitiesCancelReasonCode,3);
		if(driver.getPageSource().contains(activitiesCancelReasonCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+actCancel_pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+actCancel_pageNameEnglish);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+activitiesCancelReasonCode+"']")).click();
		//	pressAndReleaseControlAKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndRelease9Key();
		pressAndRelease9Key();

		clickSaveButton();
		sleeps();
		sleeps();

		filter("99",3);
		if(driver.getPageSource().contains("99"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+actCancel_pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+actCancel_pageNameEnglish);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"99"+"']")).click();
		//pressAndReleaseControlAKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseRKey();
		pressAndRelease0Key();
		clickRestoreButton();
		sleeps();
		sleeps();
		filter("R0",3);
		if(driver.getPageSource().contains("R0"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+actCancel_pageNameEnglish);
		}
		else
		{
			filter("99",3);
			if(driver.getPageSource().contains("99"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+actCancel_pageNameEnglish);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+actCancel_pageNameEnglish);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"99"+"']")).click();
		//	pressAndReleaseControlAKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(activitiesCancelReasonCode);
		clickSaveButton();

		//create duplicate-----------------
		createActivitiesCancelReason();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+actCancel_pageNameEnglish);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+actCancel_pageNameEnglish);
		}






	}

	@Test(groups = {"Activities - Cancel Reasons"},priority=50 )
	void filterRecords_ActivitiesCancelReason() throws InterruptedException, AWTException
	{
		String activitiesCancelReasonCode ="R1";
		filter(activitiesCancelReasonCode,3);
		clickAllFilter();
		if(driver.getPageSource().contains(activitiesCancelReasonCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(activitiesCancelReasonCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(activitiesCancelReasonCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(activitiesCancelReasonCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(activitiesCancelReasonCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(activitiesCancelReasonCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(activitiesCancelReasonCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(activitiesCancelReasonCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);
		navigateToActivitiesCancelReasons();
		filter (activitiesCancelReasonCode,3);
		if(!driver.getPageSource().contains(activitiesCancelReasonCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}


	}



	//*******************************************************Consultant Call****************************************************

	String consultantCall_pageNameEnglish= "Consultant call";
	String consultantCall_pageNameFrench = "Appel Consultant";


	void navigateToConsultantCall() throws InterruptedException
	{

		navigateToLocalTablesMenu();
		WebElement consultantCall = driver.findElement(By.xpath("//div[text()=' Consultant call'] | //div[contains(text(),' Appel Consultant')]"));
		consultantCall.click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

		sleeps();

		if((driver.getPageSource().contains("Gestion des tables locales - Appel Consultant")) | (driver.getPageSource().contains("Management of local tables - Consultant call")))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ consultantCall_pageNameEnglish);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+consultantCall_pageNameEnglish);

		}

		sleeps();
	}

	void createConsultantCall() throws InterruptedException, AWTException
	{
		String consultantCallCode ="c001";
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(consultantCallCode);
		pressAndReleaseTabKey();
		//pressAndReleaseSpaceKey();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		sleeps();
		clickSaveButton();

	}
	@Test(groups = {"Consultant Call"},priority=51)
	void consultantCall_Creation() throws InterruptedException, AWTException
	{
		String consultantCallCode ="c001";

		createTest=reports.createTest("Verifying Screen " + consultantCall_pageNameEnglish);	
		navigateToConsultantCall();

		verifyBreadCrumb(consultantCall_pageNameEnglish, consultantCall_pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(consultantCallCode,1);
		if(driver.getPageSource().contains(consultantCallCode))
		{
			driver.findElement(By.xpath("//span[text()='"+consultantCallCode+"']")).click();

			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
		}
		filter("99",1);
		if(driver.getPageSource().contains("c99"))
		{
			driver.findElement(By.xpath("//span[text()='c99']")).click();

			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
		}


		createConsultantCall();
		filter(consultantCallCode,1);
		if(driver.getPageSource().contains(consultantCallCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+consultantCall_pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+consultantCall_pageNameEnglish);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+consultantCallCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();

		pressAndRelease9Key();
		pressAndRelease9Key();

		clickSaveButton();
		sleeps();
		sleeps();

		filter("99",1);
		if(driver.getPageSource().contains("99"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+consultantCall_pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+consultantCall_pageNameEnglish);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='99']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();

		pressAndReleaseRKey();
		pressAndRelease0Key();
		clickRestoreButton();
		sleeps();
		sleeps();
		filter("R0",1);
		if(driver.getPageSource().contains("R0"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+consultantCall_pageNameEnglish);
		}
		else
		{
			filter("99",1);
			if(driver.getPageSource().contains("99"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+consultantCall_pageNameEnglish);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+consultantCall_pageNameEnglish);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"99"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();
		pressAndReleaseBackSPaceKey();

		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(consultantCallCode);
		clickSaveButton();

		//create duplicate-----------------
		createConsultantCall();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+consultantCall_pageNameEnglish);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+consultantCall_pageNameEnglish);
		}






	}

	@Test(groups = {"Consultant Call"},priority=52 )
	void ConsultantCall_filterRecords() throws InterruptedException, AWTException
	{
		String consultantCallCode ="c001";
		filter(consultantCallCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(consultantCallCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(consultantCallCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(consultantCallCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(consultantCallCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(consultantCallCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(consultantCallCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(consultantCallCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(consultantCallCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);
		navigateToConsultantCall();
		filter (consultantCallCode,1);
		if(!driver.getPageSource().contains(consultantCallCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}
	}

	//*******************************************************Accident Groups****************************************************




	void navigateToAccidentGroups() throws InterruptedException
	{
		String screenNameFR= "Gestion des tables locales - Accident-groupes";
		String screenNameEN="Management of local tables - Accident-groups";

		navigateToLocalTablesMenu();
		WebElement accidentGroups = driver.findElement(By.xpath("//div[text()=' Accident-groups'] | //div[contains(text(),' Accident-groupes')]"));
		accidentGroups.click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameEN);

		}

		sleeps();
	}

	void createAccidentGroups() throws InterruptedException, AWTException
	{
		String accidentGroupCode ="1003";
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(accidentGroupCode);
		pressAndReleaseTabKey();
		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");

		clickSaveButton();

	}

	@Test(groups = {"Accident-groups"},priority=53)
	void accidentGroupsValidation() throws InterruptedException, AWTException
	{
		String accidentGroupCode ="1003";
		String pageNameEnglish= "Accident-groups";
		String pageNameFrench = "Accident-groupes";

		createTest=reports.createTest("Verifying Screen " + pageNameEnglish);	

		navigateToAccidentGroups();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(accidentGroupCode,1);
		if(driver.getPageSource().contains(accidentGroupCode))
		{
			driver.findElement(By.xpath("//span[text()='"+accidentGroupCode+"']")).click();

			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
		}



		createAccidentGroups();
		filter(accidentGroupCode,1);
		if(driver.getPageSource().contains(accidentGroupCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+pageNameEnglish);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+accidentGroupCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		clickSaveButton();
		sleeps();
		sleeps();

		filter("9999",1);
		if(driver.getPageSource().contains("9999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+pageNameEnglish);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease1Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		clickRestoreButton();
		sleeps();
		sleeps();
		filter("1000",1);
		if(driver.getPageSource().contains("1000"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);
		}
		else
		{
			filter("9999",1);
			if(driver.getPageSource().contains("9999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+pageNameEnglish);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(accidentGroupCode);
		clickSaveButton();

		//create duplicate-----------------
		createAccidentGroups();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+pageNameEnglish);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+pageNameEnglish);
		}

	}
	@Test(groups = {"Accident-groups"},priority=54 )
	void filterRecords_AccidentGroups() throws InterruptedException, AWTException
	{
		String accidentGroupCode ="1003";
		filter(accidentGroupCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(accidentGroupCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(accidentGroupCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(accidentGroupCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(accidentGroupCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		sleeps();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}

		navigateToAccidentGroups();
		filter(accidentGroupCode,1);
		clickAllFilter();
		sleeps();
		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(accidentGroupCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(accidentGroupCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(accidentGroupCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(accidentGroupCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickSaveButton();
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);

		filter (accidentGroupCode,1);
		if(!driver.getPageSource().contains(accidentGroupCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}

	}




	//*******************************************************Accident****************************************************




	void navigateToAccident() throws InterruptedException
	{
		String screenNameFR= "Gestion des tables locales - Accident";
		String screenNameEN="Management of local tables - Accident";

		navigateToLocalTablesMenu();
		WebElement accident = driver.findElement(By.xpath("//div[text()=' Accident'] | //div[(text()=' Accident')]"));
		accident.click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(screenNameFR)) | (driver.getPageSource().contains(screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameEN);

		}

		sleeps();
	}

	void createAccident() throws InterruptedException, AWTException
	{
		String accidentCode ="10030";
		String accidentGroupCode = "1";
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(accidentCode);
		pressAndReleaseTabKey();
		//pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(accidentGroupCode);

		clickSaveButton();

	}

	@Test(groups = {"Accident"},priority=55)
	void AccidentValidation() throws InterruptedException, AWTException
	{
		String accidentCode ="10030";
		String pageNameEnglish= "Accident";
		String pageNameFrench = "Accident";

		createTest=reports.createTest("Verifying Screen " + pageNameEnglish);	

		navigateToAccident();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(accidentCode,1);
		if(driver.getPageSource().contains(accidentCode))
		{
			driver.findElement(By.xpath("//span[text()='"+accidentCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
			
		}
		
		filter("9999",1);
		if(driver.getPageSource().contains("9999"))
		{
			driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			
		}

		createAccident();
		filter(accidentCode,1);

		if(driver.getPageSource().contains(accidentCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+pageNameEnglish);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+accidentCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		sleeps();


		clickSaveButton();
		sleeps();

		filter("9999",1);
		if(driver.getPageSource().contains("9999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+pageNameEnglish);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease1Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		clickRestoreButton();
		sleeps();

		filter("1000",1);
		if(driver.getPageSource().contains("1000"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);
		}
		else
		{
			filter("9999",1);
			if(driver.getPageSource().contains("9999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+pageNameEnglish);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(accidentCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createAccident();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+pageNameEnglish);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+pageNameEnglish);
		}

	}

	@Test(groups = {"Accident"},priority=56 )
	void filterRecords_Accident() throws InterruptedException, AWTException
	{
		String accidentCode ="10030";
		
		checkFilterAndDeletion(accidentCode);

		WebElement activeCheckbox = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')]//span//input)[1]"));
		activeCheckbox.click();
		clickDeleteButton();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
			sleeps();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}
		Thread.sleep(5000);
		navigateToAccident();
		filter (accidentCode,1);
		if(!driver.getPageSource().contains(accidentCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}

	}               

	//*******************************************************Allergies & antécédents - Configuration****************************************************
	String screenNameFR= "Gestion des tables locales - Allergies & antécédents - Configuration";
	String screenNameEN="Management of local tables - Allergies/P.M.H. - Configuration" ;
	String configCode =RandomStringUtils.randomAlphanumeric(5);
	void navigateToConfiguration() throws InterruptedException
	{
		String screenNameFR= "Gestion des tables locales - Allergies & antécédents - Configuration";
		String screenNameEN="Management of local tables - Allergies/P.M.H. - Configuration" ;

		navigateToLocalTablesMenu();
		WebElement allerigies = driver.findElement(By.xpath("//div[text()=' Allergies/P.M.H.'] | //div[(text()=' Allergies & antécédents')]"));
		allerigies.click();
		sleeps();
		WebElement configuration = driver.findElement(By.xpath("//div[text()=' Configuration'] "));
		configuration.click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();
//		if(driver.getPageSource().contains(screenNameFR) | driver.getPageSource().contains(screenNameEN))
//		{
//			System.out.println("found heading");
//			createTest.log(Status.PASS,"Successfully Navigated to screen "+ screenNameEN);
//
//		}
//		else
//		{
//			createTest.log(Status.FAIL,"Failed to navigate to "+screenNameEN);
//
//		}

		sleeps();
	}

	void createConfiguration() throws InterruptedException, AWTException
	{
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(configCode);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'Tous')] | //mat-option//span//div[contains(text(),'All')]")).click();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[text()=' Alimentaires '] | //mat-option//span//div[text()=' Alimentairese ']")).click();
		filter(configCode,1);
		driver.findElement(By.xpath("(//mat-select[@role='combobox'])[6]")).click();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("(//mat-option//span//div//div)[2]")).click();
		sleeps();
		clickSaveButton();

	}


	@Test(groups = {"Configuration"},priority=57)
	void configurationValidation() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Allergies Configuration Screen");	

		String pageNameEnglish= "Configuration";
		String pageNameFrench = "Configuration";
		//                  zoomOutScreen();
		navigateToConfiguration();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);
		createTest.log(Status.PASS,"Successfully Navigated to screen "+ screenNameEN);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(configCode,1);
		if(driver.getPageSource().contains(configCode))
		{
			driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
			
		}



		createConfiguration();

		filter(configCode,1);

		if(!driver.getPageSource().contains(configCode))
		{
			driver.findElement(By.xpath("//mat-radio-group//following-sibling::input[@type='checkbox']")).click();
			sleeps();
			if(driver.getPageSource().contains(configCode))
			{
				createTest.log(Status.PASS,"Successfully created new row in "+pageNameEnglish);
				driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
				pressAndReleaseTabKey();
				sleeps();
				pressAndReleaseSpaceKey();
				sleeps();
				clickSaveButton();
				if(driver.getPageSource().contains(configCode))
				{
					createTest.log(Status.PASS,"Active Settlement Filter is successfully working in "+pageNameEnglish);
				}
				else
				{
					createTest.log(Status.PASS,"Active Settlement Filter is not working in "+pageNameEnglish);

				}
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+pageNameEnglish);
		}

		verifyClearFilter();
		filter(configCode,1);
		
		driver.findElement(By.xpath("//mat-radio-group//following-sibling::input[@type='checkbox']")).click();
		//Alleries, medicine radio checks
		driver.findElement(By.xpath("(//mat-radio-group//mat-radio-button[1]//span)[1]")).click();
		sleeps();
		if(driver.getPageSource().contains(configCode))
		{
			driver.findElement(By.xpath("(//mat-radio-group//mat-radio-button[2]//span)[1]")).click();
			System.out.println("before finding");
			sleeps();
			if(!driver.getPageSource().contains(configCode))
			{
				System.out.println("after finding");
				sleeps();
				driver.findElement(By.xpath("(//mat-radio-group//mat-radio-button[3]//span)[1]")).click();
				sleeps();
				if(!driver.getPageSource().contains(configCode))
				{
					sleeps();
					createTest.log(Status.PASS,"Allergies, PMH and Medication Filters are working fine in "+pageNameEnglish);
					driver.findElement(By.xpath("(//mat-radio-group//mat-radio-button[1]//span)[1]")).click();
				}
				else
				{
					createTest.log(Status.FAIL,"Allergies, PMH and Medication Filters are  not working fine"+pageNameEnglish);

				}
			}
		}
		else
		{
			createTest.log(Status.PASS,"Active Settlement Filter is not working in "+pageNameEnglish);

		}

		//Check Family, Category and Type Dropdowns
		driver.findElement(By.xpath("(//mat-form-field//mat-select)[1]")).click();
		sleeps();
		driver.findElement(By.xpath("//mat-option[1]//span")).click();
		sleeps();
		if(driver.getPageSource().contains(configCode))
		{
			driver.findElement(By.xpath("(//mat-form-field//mat-select)[1]")).click();
			sleeps();
			driver.findElement(By.xpath("//mat-option[2]//span")).click();
			sleeps();
			if(!driver.getPageSource().contains(configCode))
			{
				driver.findElement(By.xpath("(//mat-form-field//mat-select)[1]")).click();
				sleeps();
				driver.findElement(By.xpath("//mat-option[3]//span")).click();
				sleeps();
				if(!driver.getPageSource().contains(configCode))
				{
					createTest.log(Status.PASS,"Family Filter is working fine in "+pageNameEnglish);
					driver.findElement(By.xpath("(//mat-form-field//mat-select)[1]")).click();
					sleeps();
					driver.findElement(By.xpath("//mat-option[1]//span")).click();
					sleeps();

				}
			}

		}

		//category FIlter
		driver.findElement(By.xpath("(//mat-form-field//mat-select)[2]")).click();
		sleeps();
		driver.findElement(By.xpath("//mat-option//span[text()=' Alimentaires '] | //mat-option//span[text()=' Alimentairese ']")).click();
		sleeps();
		sleeps();
		if(!driver.getPageSource().contains(configCode))
		{
			driver.findElement(By.xpath("(//mat-form-field//mat-select)[2]")).click();
			sleeps();
			driver.findElement(By.xpath("(//mat-option//span[text()=' Alimentaires '] | //mat-option//span[text()=' Alimentairese '])[1]")).click();
			sleeps();
			if(driver.getPageSource().contains(configCode))
			{
				createTest.log(Status.PASS,"Category Filter is working fine"+pageNameEnglish);

			}
		}

		//Type Filter
		driver.findElement(By.xpath("(//mat-form-field//mat-select)[3]")).click();
		sleeps();
		driver.findElement(By.xpath("//mat-option//span[text()=' Food allergy ']")).click();
		sleeps();
		if(driver.getPageSource().contains(configCode))
		{
			createTest.log(Status.PASS,"Type Filter is working fine"+pageNameEnglish);

		}


		//test medication button
		driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
		driver.findElement(By.xpath("//button[text()='Médicament'] | //button[text()='Medication'] ")).click();
		sleeps();
		if(driver.findElement(By.xpath("//h2[text()='Médicament'] | //h2[text()='Medication']")).isDisplayed());
		{
			createTest.log(Status.PASS,"Medication Window is opened when Medication button was clicked in "+pageNameEnglish);
		}
		driver.findElement(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]")).click();
		sleeps();
		driver.findElement(By.xpath("//span[text()='40120118']")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();

		//Testing restore of medication
		driver.findElement(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]")).click();
		sleeps();
		driver.findElement(By.xpath("(//span[text()='40120118'])[last()]")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'Rétablir')] | //button[contains(text(),'Restore')])[2]")).click();
		clickSaveAndContinueButton();
		clickSaveAndCloseButton();
		sleeps();
		sleeps();
		driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
		driver.findElement(By.xpath("//button[text()='Médicament'] | //button[text()='Medication'] ")).click();
		sleeps();
		if(driver.findElement(By.xpath("//h2[text()='Médicament'] | //h2[text()='Medication']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Restore button of Medication window is working fine in "+pageNameEnglish);
			if(driver.findElement(By.xpath("//span[text()='40120118']")).isDisplayed())
			{
				createTest.log(Status.PASS,"Medication is succesfully added in "+pageNameEnglish);
				driver.findElement(By.xpath("//span[text()='40120118']")).click();
				driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]")).click();
				clickSaveAndCloseButton();
				sleeps();
				driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
				driver.findElement(By.xpath("//button[text()='Médicament'] | //button[text()='Medication'] ")).click();
				sleeps();
				if(!driver.getPageSource().contains("40120118"))
				{
					createTest.log(Status.PASS,"Medication is succesfully deleted in "+pageNameEnglish);

				}
				else
				{
					createTest.log(Status.FAIL,"Medication is not succesfully deleted in "+pageNameEnglish);

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Medication is not succesfully added in "+pageNameEnglish);

			}
		}
		clickCloseIcon();
		sleeps();

		//test exam labs button
		driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
		driver.findElement(By.xpath("//button[text()='Examens/Labos/Soins'] | //button[text()='Exams/Labs/Care'] ")).click();
		sleeps();
		if(driver.findElement(By.xpath("//h2[text()='Examens/Labos/Soins'] | //h2[text()='Exams/Labs/Care']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Exams/Labs/Care Window is opened when Exams/Labs/Care button was clicked in "+pageNameEnglish);
		}
		driver.findElement(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]")).click();
		sleeps();
		driver.findElement(By.xpath("//span[text()=' EPS1']")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();

		//Testing restore of Exams/Labs/Care
		driver.findElement(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]")).click();
		sleeps();
		driver.findElement(By.xpath("(//span[text()=' EPS1'])[last()]")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'Rétablir')] | //button[contains(text(),'Restore')])[2]")).click();
		clickSaveAndContinueButton();
		clickSaveAndCloseButton();
		sleeps();
		sleeps();
		driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
		driver.findElement(By.xpath("//button[text()='Examens/Labos/Soins'] | //button[text()='Exams/Labs/Care'] ")).click();
		sleeps();
		if(driver.findElement(By.xpath("//h2[text()='Examens/Labos/Soins'] | //h2[text()='Exams/Labs/Care']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Restore button of Exams/Labs/Care window is working fine in "+pageNameEnglish);
			if(driver.findElement(By.xpath("//span[text()=' EPS1']")).isDisplayed())
			{
				createTest.log(Status.PASS,"Exams/Labs/Care is succesfully added in "+pageNameEnglish);
				driver.findElement(By.xpath("//span[text()=' EPS1']")).click();
				driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]")).click();
				clickSaveAndCloseButton();
				sleeps();
				driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
				driver.findElement(By.xpath("//button[text()='Examens/Labos/Soins'] | //button[text()='Exams/Labs/Care'] ")).click();
				sleeps();
				if(!driver.getPageSource().contains("EPS1"))
				{
					createTest.log(Status.PASS,"Exams/Labs/Care is succesfully deleted in "+pageNameEnglish);

				}
				else
				{
					createTest.log(Status.FAIL,"Exams/Labs/Care is not succesfully deleted in "+pageNameEnglish);

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Exams/Labs/Care is not succesfully added in "+pageNameEnglish);

			}
		}
		clickCloseIcon();
		sleeps();

		//Edit-----------------
		filter(configCode,1);
		sleeps();
		driver.findElement(By.xpath("//span[text()='"+configCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		sleeps();


		clickSaveButton();
		sleeps();
		try {
			clickSaveButton();

		} catch (Exception e) {
			// TODO: handle exception
		}
		sleeps();
		sleeps();

		filter("9999",1);
		if(driver.getPageSource().contains("9999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+pageNameEnglish);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+pageNameEnglish);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease1Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		clickRestoreButton();
		sleeps();

		filter("1000",1);
		if(driver.getPageSource().contains("1000"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);
		}
		else
		{
			filter("9999",1);
			if(driver.getPageSource().contains("9999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+pageNameEnglish);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+pageNameEnglish);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(configCode);
		sleeps();
		clickSaveButton();
		sleeps();
		sleeps();

		//create duplicate-----------------
		createConfiguration();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+pageNameEnglish);
			clickOKButton();
			
			
			clickHome();
			if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
			{
				createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
				clickYesButton();
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+pageNameEnglish);
		}

	}

	String doctorValidCode = "0"+RandomStringUtils.randomNumeric(7);
	@Test(groups = {"Doctor"},priority=58,enabled=true)
	public void navigateTotheDoctor() throws InterruptedException, AWTException {
		
		//		timeOutMessage();
		createTest=reports.createTest("Validating the functionaltiy of the doctors screen");
		sleeps();
		navigateToDoctor();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		
		filter(doctorValidCode, 1);

		if(driver.getPageSource().contains(doctorValidCode))
		{
			driver.findElement(By.xpath("//span[text()='"+doctorValidCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
		
		sleeps();
	}
	public void doctorCreation() throws InterruptedException, AWTException {

		clickCreateButton();
		sleeps();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(doctorValidCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[@class='ag-cell-wrapper']//following::div//child::input[@class='ag-input-field-input ag-text-field-input']")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[@class='ag-cell-wrapper']//following::div//child::input[@class='ag-input-field-input ag-text-field-input']")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

	}
	@Test(groups = {"Doctor"},priority=59,enabled=true)
	public void newDoctorCreation() throws InterruptedException, AWTException {
		
		doctorCreation();

		filter(doctorValidCode, 1);

		if(driver.getPageSource().contains(doctorValidCode)) {
			createTest.log(Status.PASS," A new user is created in the doctor screen");
		}else {
			createTest.log(Status.FAIL,"A new user is not created");
		}

	}
	@Test(groups = {"Doctor"},priority=60,enabled=true)
	public void checkDomainErrorMessage() throws InterruptedException, AWTException, IOException {

		sleeps();
		clickingMainMenu();
		sleeps();
		sleeps();
		navigateToPortalMenu();
		sleeps();
		sleeps();
		navigateToLogout();

//		WebElement domainError = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'Tous les médecins doivent avoir un domaine')] | //p[contains(text(),'All doctors must have a domain')]")));
		//		String errorMsg = domainError.getText();
		sleeps();
		if((driver.getPageSource().contains("Tous les médecins doivent avoir un domaine.")) | (driver.getPageSource().contains("All doctors must have a domain."))) {
			
			createTest.log(Status.PASS,"An error shows when user leaving the screen without adding the domain");
			clickOKButton();
			

		}else {
			createTest.log(Status.FAIL,"An error is not shown when user leaving the screen without adding the domain");
			applicationLoginCredentials();
			navigateToDoctor();
		}
		
		//		clickingMainMenu();
		sleeps();

	}
	@Test(groups = {"Doctor"},priority=61,enabled=true)
	public void addingDomain() throws InterruptedException, AWTException {
		
		filter(validCode, 1);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Domain')] | //button[contains(text(),'Domaine')]"))).click();

		sleeps();
		WebElement domainCreate = driver.findElement(By.xpath("//div[@class='col-auto text-start']//button[1]"));
		domainCreate.click();
		WebElement addDomain = driver.findElement(By.xpath("//span[contains(text(),'Allergie')] | //span[contains(text(),'Allergy / immunology')]"));
		addDomain.click();

		WebElement okDomain = driver.findElement(By.xpath("//div[@class='col-sm-12 p-0 pt-2 mt-2 d-flex justify-content-end']//button"));
		okDomain.click();
		clickSaveAndContinueButton();

		if((driver.getPageSource().contains("Allergie")) || (driver.getPageSource().contains("Allergy / immunology"))) {

			createTest.log(Status.PASS,"The domain is sucessfully added");
		}else {
			createTest.log(Status.FAIL,"The domain is not added ");
		}

		domainCreate.click();
		sleeps();
		WebElement addDomain1 = driver.findElement(By.xpath("(//span[contains(text(),'Allergie')] | //span[contains(text(),'Allergy / immunology')])[2]"));
		addDomain1.click();

		clickOKSubWindow();
		//		clickSaveAndCloseButton();
		clickSaveAndContinueButton();

		WebElement duplicateValue = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'Violation de la contrainte UNIQUE KEY')] | //p[contains(text(),'Violation of UNIQUE KEY constraint')]")));
		//		String errorMsg1 = duplicateValue.getText();

		if((driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")) | (driver.getPageSource().contains("Violation of UNIQUE KEY constraint 'UQ_SEC_MD_CONS'."))) {

			createTest.log(Status.PASS,"An error is shown when the duplicate domain is added");

		}else {
			createTest.log(Status.FAIL,"An error is not shown when the duplicate domain is added");
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button//span[contains(text(),' OK ')]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]"))).click();
		clickSaveAndContinueButton();

		if((driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")) | (driver.getPageSource().contains("Violation of UNIQUE KEY constraint 'UQ_SEC_MD_CONS'."))) {
			createTest.log(Status.FAIL,"The duplicate domain is not deleted");

		}else {
			createTest.log(Status.PASS,"The duplicate domain is deleted");
		}

		WebElement domainChangeBtn = driver.findElement(By.xpath("(//span[@class='ag-cell-value']//button[@class='btn btn-primary'])"));
		domainChangeBtn.click();

		WebElement domainModify = driver.findElement(By.xpath("//span[contains(text(),'Anésthésie-réanimation')] | //span[contains(text(),'Anesthesia')]"));
		domainModify.click();
		clickOKSubWindow();
		sleeps();

		if((driver.getPageSource().contains("Allergie")) | (driver.getPageSource().contains("Allergy / immunology"))) {
			createTest.log(Status.FAIL,"The domain is not modifyied");
		}else {
			createTest.log(Status.PASS,"The domain is modifyied");
		}

		clickSaveAndCloseButton();
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]"))).click();

	}
	@Test(groups = {"Doctor"},priority=62,enabled=true)
	public void duplicateAndResidentDoctor() throws InterruptedException, AWTException {

		doctorCreation();
		sleeps();
		sleeps();
		if((driver.getPageSource().contains("Le code de médecin spécifié est déjà")) | (driver.getPageSource().contains("The specified doctor code is already used"))) {
			createTest.log(Status.PASS,"An error is shown when duplicate code value is enetered");
		}else {
			createTest.log(Status.FAIL,"An error is not shown when duplicate code value is enetered");
		}
		sleeps();
		clickOKButton();
		sleeps();
		clickRestoreButton();
		clickSaveButton();

		if((driver.getPageSource().contains("Le code de médecin spécifié est déjà utilisé")) | (driver.getPageSource().contains("The specified doctor code is already used"))) {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}else {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(invalidCode);
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[@class='ag-cell-wrapper']//following::div//child::input[@class='ag-input-field-input ag-text-field-input']")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[@class='ag-cell-wrapper']//following::div//child::input[@class='ag-input-field-input ag-text-field-input']")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();
		//		pressAndReleaseTabKey();

		sleeps();
		if((driver.getPageSource().contains("Le numéro de pratique du médecin ne doit pas dépasser 7")) | (driver.getPageSource().contains("The doctor's practice number cannot exceed 7 "))) {
			createTest.log(Status.PASS,"Error is shown when more than 7 digits enetred in code column");
		}else {
			createTest.log(Status.FAIL,"Error is not shown when more than 7 digits enetred in code column");
		}
		clickOKButton();
		sleeps();
		clickDeleteButton();
		clickSaveButton();

	}
	@Test(groups = {"Doctor"},priority=63,enabled=true)
	public void filterAndDeletingRecords() throws AWTException, InterruptedException {

		filter(validCode, 1);

		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-row-first ag-row-last')])[2]//div[contains(@col-id,'rem_mede')]"))).click();
		sleeps();
		PressAndReleaseKeyR();
		clickSaveButton();

		if((driver.getPageSource().contains("R")) | (driver.getPageSource().contains("r"))) {
			createTest.log(Status.PASS,"The resident doctor is created");
		}else {
			createTest.log(Status.FAIL,"The resident doctor is not created");
		}
		activeRadiobtn(doctorValidCode);
		nonActiveRadiobtn(doctorValidCode);
		reservedRadiobtn(doctorValidCode);
		AllRadiobtn(doctorValidCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(doctorValidCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}

	}

	@Test(groups = {"Roles"},priority=64,enabled=true)
	public void rolesScreen() throws InterruptedException, AWTException {

		createTest=reports.createTest("Validating the functionaltiy of the roles screen");
		navigateToManagementMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Roles'] | //div[text()=' Rôles']"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		//
		WebElement administratror = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Administrateur'] | //span[text()='Administrator']")));
		WebElement create = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Create'] | //button[text()='Créer']")));
		WebElement delete = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Delete'] | //button[text()='Supprimer']")));
		if(administratror.isDisplayed() || administratror.isSelected()) {
			if( create.isEnabled() && delete.isEnabled()==true) {
				createTest.log(Status.FAIL,"Create & Delete buttons are enabled for Administrator");
			}
			else {
				createTest.log(Status.PASS,"Create & Delete buttons are not enabled for Administrator");
			}
		}
		//		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Doctor'] | //span[text()='Médecin']"))).click();
		sleeps();

		filter(DescFN, 1);
		
		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
		clickCreateButton();
		sleeps();
		WebElement until = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input")));
		//		WebElement until = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-focus')][last()]")));
//		until.click();
		until.sendKeys(DescFN);
		sleeps();
		pressAndReleaseTabKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ag-cell-wrapper']//following::div//child::input[@class='ag-input-field-input ag-text-field-input']"))).sendKeys(DescEN);
		//		tabKey();
		//		sleeps();
		// 		spaceKey();
		clickSaveButton();

		Thread.sleep(2000);
		sleeps();
		filter(DescFN, 1);

		sleeps();
		WebElement Actif = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@type,'checkbox')])[6]")));
		Actif.click();
		pressAndReleaseSpaceKey();
		tabKey();
		spaceKey();
		//		Actif.click();
		sleeps();

		//		pressAndReleaseSpaceKey();
		clickSaveButton();

		if(driver.getPageSource().contains(DescFN)) {
			sleeps();
			createTest.log(Status.PASS,"A new role is added successfully");
			createTest.log(Status.PASS,"The Radio buttons are working fine");
			createTest.log(Status.PASS,"The Checkboxes are editable");
		}else {
			createTest.log(Status.FAIL,"A new role is not added");
		}
		System.out.println("done");

		sleeps();
		if(driver.getPageSource().contains("(//input[contains(@type,'checkbox')])[6]")) {
			createTest.log(Status.FAIL,"Active & read only columns are not editable");
		}else {
			createTest.log(Status.PASS,"Active & read only columns are editable");

		}
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Unité')] | //button[contains(text(),'Unit')]"))).click();
		sleeps();
		clickCreateSubWindow();
		WebElement selectUnit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Médecine chirurgie')] | //span[contains(text(),'Medecine Surgery')]")));
		selectUnit.click();
		clickOKSubWindow();
		clickCreateSubWindow();
		sleeps();
		WebElement duplicateUnit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'Médecine chirurgie')] | //span[contains(text(),'Medecine Surgery')])[2]")));
		duplicateUnit.click();
		clickOKSubWindow();
		sleeps();
		String errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'Cette unité est déjà associée au rôle.')] | //p[contains(text(),'This unit is already linked to the role.')]"))).getText();
		sleeps();
		if((driver.getPageSource().contains(errorMsg))){
			createTest.log(Status.PASS,"Application throws an error when the duplicate unit added");
		}else {
			createTest.log(Status.FAIL,"Application not throws an error when the duplicate unit added");
		}

		clickOKButton();
		clickSaveAndContinueButton();
		clickDeleteSubWindow();
		if((driver.getPageSource().contains("Médecine Chirurgie")) || (driver.getPageSource().contains("Medecine Surgery"))){
			createTest.log(Status.FAIL,"The delete button is not working in Unit window");
		}else {
			createTest.log(Status.PASS,"The delete button is working fine in Unit window");
		}

//		clickRestoreSubWindow();
//		sleeps();
//
//		if((driver.getPageSource().contains("Médecine Chirurgie")) || (driver.getPageSource().contains("Medecine Surgery"))){
//			createTest.log(Status.PASS,"The Restore button is working Unit window");
//		}else {
//			createTest.log(Status.FAIL,"The Restore button is not working fine Unit window");
//		}

		clickSaveAndCloseButton();

		clickDeleteButton();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))){
			createTest.log(Status.FAIL,"The delete and Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The delete and Save buttons are working fine ");
		}
	}

	@Test(groups = {"Roles/Controlpoints"},priority=65,enabled=true)
	public void rolesAndControlpoints() throws InterruptedException {

		createTest=reports.createTest("Validating the functionaltiy of the Roles & Control points screen");
		sleeps();
		navigateToManagementMenu();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Rôles / Points de contrôle'] | //div[text()=' Roles / Control points']"))).click();
		Thread.sleep(30000);

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-select[contains(@id,'mat-select')])[1]"))).click();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),' Médecin ')] | //span[contains(text(),' Doctor ')]"))).click();

		createTest.log(Status.PASS,"The Role category fields is editable");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-select[contains(@id,'mat-select')])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),' MD-Volant ')] | //span[contains(text(),' Overlap MD shift ')]"))).click();
		createTest.log(Status.PASS,"The Coressponding role fields is editable");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Portail - Gestion(PG)')] | //span[contains(text(),'Portal - Management(PM)')]"))).click();
		createTest.log(Status.PASS,"The Section fields are editable");

		//checking editable

		WebElement codeColumn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'703')]")));
		String filter = codeColumn.getText();

		WebElement noSort = driver.findElement(By.xpath("(//span[@class='ag-icon ag-icon-menu'])"));
		noSort.click();

		WebElement contains = driver.findElement(By.xpath("(//div[contains(@class,'ag-wrapper ag-input-wrapper ag-text-field-input-wrapper')])[1]//input[contains(@ref,'eInput')]"));
		contains.sendKeys(filter);

		if((driver.getPageSource().contains(filter))){
			createTest.log(Status.PASS,"The filter function is working ");
		}else {
			createTest.log(Status.FAIL,"The filter function is not working");
		}

		sleeps();
		WebElement selectAll = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Tout sélectionner')] | //button[contains(text(),'Select all')]")));
		sleeps();
		selectAll.click();

		sleeps();
		WebElement inactive = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[contains(@src,'cancel.gif')]")));
		String select = inactive.getAttribute("src");

		clickSaveButton();
		Thread.sleep(30000); 

		WebElement unSelectAll = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Tout désélectionner')] | //button[contains(text(),'Unselect all')]")));
		unSelectAll.click();

		WebElement active = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[contains(@src,'ck1')]")));
		String Unselect = active.getAttribute("src");

		if(select.equalsIgnoreCase(Unselect)){
			createTest.log(Status.FAIL,"The SelectAll & UnselectAll buttons are not working  fine");
		}else {
			createTest.log(Status.PASS,"The SelectAll & UnselectAll button are working fine ");
		}

		sleeps();
		clickRestoreButton();
		Thread.sleep(30000);

		WebElement restore = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span//img)[9]")));
		String restore1 = restore.getAttribute("src");
		System.out.println(restore1);

		if(select.equalsIgnoreCase(restore1)){
			createTest.log(Status.PASS,"The Restore & Save buttons are working fine");
		}else {
			createTest.log(Status.FAIL,"The Restore & Save buttons are not working fine ");
		}


	}
	@Test(groups = {"ControlpointsRoles"},priority=66,enabled=true)
	public void controlpointsANdRoles() throws InterruptedException, AWTException {

		createTest=reports.createTest("Validating the functionaltiy of the Control points/Roles screen");
		sleeps();
		navigateToManagementMenu();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Points de contrôle / Rôles'] | //div[text()=' Roles / Control points']"))).click();
		Thread.sleep(30000);
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}


		WebElement codeColumn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'703')]")));
		String filter = codeColumn.getText();


		filter(filter, 1);

		if((driver.getPageSource().contains(filter))){
			createTest.log(Status.PASS,"The filter function is working in point of control section");
		}else {
			createTest.log(Status.FAIL,"The filter function is not working in point of control section");
		}

		WebElement roleColumns = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='MEDIAMED']")));
		String filter1 = roleColumns.getText();

		WebElement noSort1 = driver.findElement(By.xpath("(//span[@class='ag-icon ag-icon-menu'])[4]"));
		noSort1.click();

		WebElement contain = driver.findElement(By.xpath("(//div[contains(@class,'ag-wrapper ag-input-wrapper ag-text-field-input-wrapper')])[1]//input[contains(@ref,'eInput')]"));
		contain.sendKeys(filter1);


		if((driver.getPageSource().contains(filter1))){
			createTest.log(Status.PASS,"The filter function is working in role section");
		}else {
			createTest.log(Status.FAIL,"The filter function is not working in role section");
		}

		sleeps();
		WebElement selectAll = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Tout s')] | //button[contains(text(),'Select all')]")));
		sleeps();
		selectAll.click();
		sleeps();
		sleeps();
		Thread.sleep(10000);
		Thread.sleep(10000);
		WebElement inactive = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[contains(@src,'cancel.gif')]")));
		String select = inactive.getAttribute("src");
		sleeps();
		Thread.sleep(10000);
		clickSaveButton();
		Thread.sleep(10000); 
		sleeps();
		WebElement unSelectAll = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Tout d')] | //button[contains(text(),'Unselect all')]")));
		unSelectAll.click();

		WebElement active = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[contains(@src,'ck1')]")));
		String Unselect = active.getAttribute("src");

		if(select.equalsIgnoreCase(Unselect)){
			createTest.log(Status.FAIL,"The SelectAll & UnselectAll buttons are not working  fine");
		}else {
			createTest.log(Status.PASS,"The SelectAll & UnselectAll button are working fine ");
		}

		sleeps();
		clickRestoreButton();
		Thread.sleep(30000);

		WebElement restore = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span//img)[9]")));
		String restore1 = restore.getAttribute("src");
		System.out.println(restore1);

		if(select.equalsIgnoreCase(restore1)){
			createTest.log(Status.PASS,"The Restore & Save buttons are working fine");
		}else {
			createTest.log(Status.FAIL,"The Restore & Save buttons are not working fine ");
		}

	}

	//November  screens

	@Test(groups = {"AllergiesAllergensType"},priority=67,enabled=true)
	public void navigateToAllergiesType() throws InterruptedException, AWTException {

		String pageNameEnglish= "Allergens & Antecedents Type";
		String pageNameFrench = "Type allergènes & antécédents";

		createTest=reports.createTest("Validating the functionaltiy of the Allergies&Allergens Type screen");
		sleeps();
		sleeps();
		//		clickingMainMenu();
		//		navigateToPortalMenu();	
		navigateToLocalTablesMenu();
		sleeps();
		navigateToallergiesPMH();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Type allergènes & antécédents')] | //div[contains(text(),' Allergens & Antecedents Type')]"))).click();
		//		clickingMainMenu();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is diabled by default");
		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is diabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}
	@Test(groups = {"AllergiesAllergensType"},priority=68,enabled=true)
	public void createingNewRecord() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

	}
	@Test(groups = {"AllergiesAllergensType"},priority=69,enabled=true)
	public void functionalityValidation() throws InterruptedException, AWTException {


		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		WebElement allergies = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select-trigger[contains(text(),'Allergies')] | //mat-select-trigger[contains(text(),'Allergies')]")));

		if(driver.getPageSource().contains(allergies.getText())) {

			createTest.log(Status.PASS,"The default group Allergies is selected automatically");
		}else {
			createTest.log(Status.FAIL,"The default group is not selected");
		}
		sleeps();
		sleeps();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@col-id,'allergie')]//div[contains(@class,'mat-select-arrow-wrapper')]"))).click();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@col-id,'allergie')]//div[contains(@class,'mat-select-arrow-wrapper')]"))).click();

		WebElement PMH = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Antécédents')] | //div[contains(text(),'P.M.H.')]")));
		WebElement medications = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Médicaments')] | //div[contains(text(),'Medications')]")));
		String type = medications.getText();
		sleeps();

		medications.click();

		sleeps();
		sleeps();

		if(driver.getPageSource().contains(type)) {
			createTest.log(Status.PASS,"The group dropdown is editable");
		}else {
			createTest.log(Status.FAIL,"The group dropdown is editable");
		}

		createingNewRecord();

		if(driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		;
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

	}
	@Test(groups = {"AllergiesAllergensType"},priority=70,enabled=true)
	public void sortingAndDeleting() throws InterruptedException, AWTException {


		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"AllergiesAllergensCategory"},priority=71,enabled=true)
	public void naviagteToAllergensCategory() throws InterruptedException, AWTException{

		String pageNameEnglish= "Allergens & Antecedents category";
		String pageNameFrench = "Catégorie allergènes  & antécédents";

		createTest=reports.createTest("Validating the functionaltiy of the Allergies&Allergens category screen");
		sleeps();
		sleeps();
		//		clickingMainMenu();
		//		navigateToPortalMenu();
		navigateToLocalTablesMenu();
		sleeps();
		navigateToallergiesPMH();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Catégorie allergènes  & antécédents')] | //div[contains(text(),' Allergens & Antecedents category')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);


		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}
	@Test(groups = {"AllergiesAllergensCategory"},priority=72,enabled=true)
	public void createingNewCategory() throws InterruptedException, AWTException {

		clickCreateButton();

		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();
	}
	@Test(groups = {"AllergiesAllergensCategory"},priority=73,enabled=true)
	public void functionalityValidationOfCategory() throws InterruptedException, AWTException {

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		WebElement PMH = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select-trigger[contains(text(),'P.M.H.')] | //mat-select-trigger[contains(text(),'Antécédents')]")));

		if(driver.getPageSource().contains(PMH.getText())) {

			createTest.log(Status.PASS,"The default group Allergies is selected automatically");
		}else {
			createTest.log(Status.FAIL,"The default group is not selected");
		}
		sleeps();
		sleeps();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@col-id,'allergie')]//div[contains(@class,'mat-select-arrow-wrapper')]"))).click();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@col-id,'allergie')]//div[contains(@class,'mat-select-arrow-wrapper')]"))).click();

		WebElement allergies = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Allergies')] | //div[contains(text(),'Allergies')]")));
		WebElement medications = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Médicaments')] | //div[contains(text(),'Medications')]")));
		String type = medications.getText();
		sleeps();

		medications.click();

		sleeps();
		sleeps();
		if(driver.getPageSource().contains(type)) {
			createTest.log(Status.PASS,"The group dropdown is editable");
		}else {
			createTest.log(Status.FAIL,"The group dropdown is editable");
		}
		createingNewCategory();

		if(driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

	}
	@Test(groups = {"AllergiesAllergensCategory"},priority=74,enabled=true)
	public void sortingAndDeletingCategory() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	String DisplayOrder="test160995";
	@Test(groups = {"Severity"},priority=75,enabled=true)
	public void navigateToSeverity() throws InterruptedException, AWTException {

		String pageNameEnglish= "Severity/+ -";
		String pageNameFrench = "Sévérité/+ -";


		createTest=reports.createTest("Validating the functionaltiy of the Severity screen");
		sleeps();
		sleeps();
		navigateToLocalTablesMenu();
		sleeps();
		navigateToallergiesPMH();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Sévérité/+ -')] | //div[contains(text(),' Severity/+ -')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}
	@Test(groups = {"Severity"},priority=76,enabled=true)
	public void createingNewRecordSeverity() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DisplayOrder);
		String text = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).getText();
		if(text.equals(DisplayOrder)) {
			createTest.log(Status.FAIL,"The Display order columns accepts alphanumeric value");
		}else {
			createTest.log(Status.PASS,"The Display order columns accepts only numeric value");
		}
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}
	}
	@Test(groups = {"Severity"},priority=77,enabled=true)
	public void duplicateErrorValiadtion() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

	}
	@Test(groups = {"Severity"},priority=78,enabled=true)
	public void negativeDefaultErrorValiadtion() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("A default negative severity must be used.")|driver.getPageSource().contains("Vous devez utiliser une sévérité négative comme sévérité négative par défaut.")) {
			createTest.log(Status.PASS,"The application throws an error when Negative default column checkbox is checked and Negative column checkbox ix unchecked");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when Negative default column checkbox is checked and Negative column checkbox ix unchecked");
		}
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		clickCreateButton();
		WebElement Nocoulmmn1 = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn1.sendKeys(alphanumericCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		clickSaveButton();

		sleeps();
		sleeps();

		if(driver.getPageSource().contains("You cannot have more than one negative severity")|driver.getPageSource().contains("Vous ne pouvez pas avoir plus d'une sévérité")) {
			createTest.log(Status.PASS,"The application throws an error when more than one negative default checkboxes are selected");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when more than one negative default checkboxes are selected");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You cannot have more than one negative severity")|driver.getPageSource().contains("Vous devez utiliser une sévérité négative comme sévérité")) {
			createTest.log(Status.PASS,"The duplicate negative default is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate negative default is not restored");
		}
	}
	@Test(groups = {"Severity"},priority=79,enabled=true)
	public void sortingAndDeletingSeverity() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();

		verifyingTheConfirmationMessage();

		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"SensitivitytoAllergens"},priority=80,enabled=true)
	public void navigateToSensitivitytoAllergens() throws InterruptedException, AWTException {

		String pageNameEnglish= "Sensitivity to allergens";
		String pageNameFrench = "Sensibilité aux allergènes";

		createTest=reports.createTest("Validating the functionaltiy of the Sensitivity to allergens screen");
		sleeps();
		sleeps();
		navigateToLocalTablesMenu();
		sleeps();
		navigateToallergiesPMH();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Sensitivity to allergens')] | //div[contains(text(),' Sensibilité aux allergènes')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"SensitivitytoAllergens"},priority=81,enabled=true)
	public void createingNewRecord1() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}
	}

	@Test(groups = {"SensitivitytoAllergens"},priority=82,enabled=true)
	public void duplicateErrorValiadtionSensitivitytoAllergens() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"SensitivitytoAllergens"},priority=83,enabled=true)
	public void sortingAndDeletingSensitivitytoAllergens() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();

		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"Clinicalstatus"},priority=84,enabled=true)
	public void navigateToClinicalstatus() throws InterruptedException, AWTException {

		String pageNameEnglish= "Clinical status";
		String pageNameFrench = "Statut clinique";


		createTest=reports.createTest("Validating the functionaltiy of the Clinical status screen");
		sleeps();
		sleeps();
		navigateToLocalTablesMenu();
		sleeps();
		navigateToallergiesPMH();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Clinical status')] | //div[contains(text(),' Statut clinique')]"))).click();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}
	@Test(groups = {"Clinicalstatus"},priority=85,enabled=true)
	public void createingNewRecordClinicalstatus() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}
	}

	@Test(groups = {"Clinicalstatus"},priority=86,enabled=true)
	public void duplicateErrorValiadtionClinicalstatus() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"Clinicalstatus"},priority=87,enabled=true)
	public void sortingAndDeletingClinicalstatus() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();

		verifyingTheConfirmationMessage();

		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"Allergicsreaction"},priority=88,enabled=true)
	public void navigateToAllergicsreaction() throws InterruptedException, AWTException {

		String pageNameEnglish= "Allergics reaction";
		String pageNameFrench = "Réaction allergique";


		createTest=reports.createTest("Validating the functionaltiy of the Allergics reaction screen");
		sleeps();
		sleeps();
		navigateToLocalTablesMenu();
		sleeps();
		navigateToallergiesPMH();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Allergics reaction')] | //div[contains(text(),' Réaction allergique')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}
	@Test(groups = {"Allergicsreaction"},priority=89,enabled=true)
	public void createingNewRecordAllergicsreaction() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}
	}

	@Test(groups = {"Allergicsreaction"},priority=90,enabled=true)
	public void duplicateErrorValiadtionAllergicsreaction() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"Allergicsreaction"},priority=91,enabled=true)
	public void sortingAndDeletingAllergicsreaction() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();

		verifyingTheConfirmationMessage();

		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"Informationsources"},priority=92,enabled=true)
	public void navigateToInformationsources() throws InterruptedException {

		String pageNameEnglish= "Information sources";		 
		String pageNameFrench = "Sources d'information";

		createTest=reports.createTest("Validating the functionaltiy of the Information sources screen");
		sleeps();
		sleeps();
		navigateToLocalTablesMenu();
		sleeps();
		navigateToallergiesPMH();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Sources')] | //div[contains(text(),' Information sources')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));


		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"Informationsources"},priority=93,enabled=true)
	public void createingNewRecordInformationsources() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}
	}
	@Test(groups = {"Informationsources"},priority=94,enabled=true)
	public void duplicateErrorValiadtionInformationsources() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(validCode);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("You can not add this code, because it is already present")|driver.getPageSource().contains("Vous ne pouvez pas ajouter ce code, car il est déjà présent")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

	}
	@Test(groups = {"Informationsources"},priority=95,enabled=true)
	public void negativeDefaultErrorValiadtionInformationsources() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("You cannot have more than one information")|driver.getPageSource().contains("Vous ne pouvez pas avoir plus d'une source d'information")) {
			createTest.log(Status.PASS,"The application throws an error when more than one negative default checkboxes are selected");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when more than one negative default checkboxes are selected");
		}
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();
	}
	@Test(groups = {"Informationsources"},priority=96,enabled=true)
	public void sortingAndDeletingInformationsources() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();

		verifyingTheConfirmationMessage();

		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"TriageComplaintsConfigurationAdult"},priority=97,enabled=true)
	public void navigateToTriageComplaintsConfigurationAdult() throws InterruptedException, AWTException {

		String pageNameEnglish= "Triage complaints configuration – adult";
		String pageNameFrench = "Configuration des plaintes - adultes";

		createTest=reports.createTest("Validating the functionaltiy of the Triage Complaints Configuration-Adult status screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Triage complaints configuration – adult')] | //div[contains(text(),' Configuration des plaintes - adultes')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

	}
	@Test(groups = {"TriageComplaintsConfigurationAdult"},priority=98,enabled=true)
	public void validatingColumnsTriageComplaintsConfigurationAdult() throws InterruptedException, AWTException {

		String level="1";

		filter(level, 4);

		WebElement category = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),' Arrêt respiratoire')] | //span[contains(text(),' Respiratory arrest')]")));
		String text = category.getText();
		filter(text, 2);

		category.click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[contains(text(),' Arrêt respiratoire')] | //span[contains(text(),' Respiratory arrest')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"Category column is read only");
		}
		else
		{
			createTest.log(Status.FAIL,"Category column is editable");
		}

		WebElement system = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'AB -> VOIES AÉRIENNES et RESPIRATOIRE')] | //span[contains(text(),'AB -> AIRWAY and RESPIRATORY')]")));
		system.click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[contains(text(),'AB -> VOIES AÉRIENNES et RESPIRATOIRE')] | //span[contains(text(),'AB -> AIRWAY and RESPIRATORY')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"System column is read only");
		}
		else
		{
			createTest.log(Status.FAIL,"System column is editable");
		}

		WebElement niveau = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='1']")));
		niveau.click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='1']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Niveau column is read only");
		}
		else
		{
			createTest.log(Status.FAIL,"Niveau column is editable");
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("((//span[@class='ag-cell-value'])[5])//input"))).click();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		WebElement until = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@class,'ag-input-field-input')])[10]")));
		until.clear();
		until.sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();

		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescEN))) {
			createTest.log(Status.PASS,"The Trauma, Breif access, Short reason,and Pain evaluation column are editable");
		}else {
			createTest.log(Status.FAIL,"The Trauma, Breif access, Short reason,and Pain evaluation column are not editable");
		}
	}

	@Test(groups = {"TriagecomplaintsconfigurationPediatrics"},priority=99,enabled=true)
	public void navigateToTriagecomplaintsconfigurationpediatrics() throws InterruptedException {

		String pageNameEnglish= "Triage complaints configuration – pediatrics";
		String pageNameFrench = "Configuration des plaintes - pédiatriques";

		createTest=reports.createTest("Validating the functionaltiy of the Triage Complaints Configuration-pediatrics screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Triage complaints configuration – pediatrics')] | //div[contains(text(),' Configuration des plaintes - pédiatriques')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}
	@Test(groups = {"TriagecomplaintsconfigurationPediatrics"},priority=100,enabled=true)
	public void validatingColumnsTriagecomplaintsconfigurationpediatrics() throws InterruptedException, AWTException {

		WebElement complaint = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'   Histoire compatible avec apnée du NN')] | //span[contains(text(),'   History compatible with apneic spell')]")));
		String text = complaint.getText();
		filter(text, 3);

		complaint.click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[contains(text(),'   Histoire compatible avec apnée du NN')] | //span[contains(text(),'   History compatible with apneic spell')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"Complaint column is read only");
		}
		else
		{
			createTest.log(Status.FAIL,"Complaint column is editable");
		}

		WebElement system = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'AB -> AIRWAY and RESPIRATORY')] | //span[contains(text(),'AB -> VOIES AÉRIENNES et RESPIRATOIRE')]")));
		system.click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[contains(text(),'AB -> AIRWAY and RESPIRATORY')] | //span[contains(text(),'AB -> VOIES AÉRIENNES et RESPIRATOIRE')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"System column is read only");
		}
		else
		{
			createTest.log(Status.FAIL,"System column is editable");
		}

		WebElement level = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='3']")));
		level.click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='3']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Level column is read only");
		}
		else
		{
			createTest.log(Status.FAIL,"Level column is editable");
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("((//span[@class='ag-cell-value'])[5])//input"))).click();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@class,'ag-input-field-input')])[10]"))).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();

		verifyingTheConfirmationMessage();

		clickSaveButton();

		if((driver.getPageSource().contains(DescEN))) {
			createTest.log(Status.PASS,"The Trauma, Breif access, Short reason,and Pain evaluation column are editable");
		}else {
			createTest.log(Status.FAIL,"The Trauma, Breif access, Short reason,and Pain evaluation column are not editable");
		}
	}

	@Test(groups = {"Consultationplanning"},priority=101,enabled=true)
	public void navigateToConsultationPlanning() throws InterruptedException {

		String pageNameEnglish= "Consultation - Planning";
		String pageNameFrench = "Consultation-Planification";

		createTest=reports.createTest("Validating the functionaltiy of the Consultation planning screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Consultation-Planification')] | //div[contains(text(),' Consultation - Planning')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"Consultationplanning"},priority=102,enabled=true)
	public void checkingCodecolumn() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		clickSaveButton();

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The code column accepts more than 4 digits");
		}else {
			createTest.log(Status.PASS,"The code column is not accepts more than 4 digits");
		}

		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"Consultationplanning"},priority=103,enabled=true)
	public void createingNewRecordConsultationplanning() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		clickSaveButton();

		sleeps();

		filter(validCode, 1);
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}
	}
	@Test(groups = {"Consultationplanning"},priority=104,enabled=true)
	public void duplicateErrorValiadtionConsultationplanning() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		sleeps();
		if(driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

	}
	@Test(groups = {"Consultationplanning"},priority=105,enabled=true)
	public void sortingAndDeletingConsultationplanning() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();

		verifyingTheConfirmationMessage();

		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"LWBSReasons"},priority=106,enabled=true)
	public void navigateToLWBSReasons() throws InterruptedException, AWTException {

		String pageNameEnglish= "LWBS - Reasons";
		String pageNameFrench = "Départ avant PEC - Raisons";

		createTest=reports.createTest("Validating the functionaltiy of the LWBS - Reasons screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' LWBS - Reasons')] | //div[contains(text(),' Départ avant PEC - Raisons')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		
		filter(validCode, 1);
		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}
	@Test(groups = {"LWBSReasons"},priority=107,enabled=true)
	public void createingNewRecordLWBSReasons() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}
	}

	@Test(groups = {"LWBSReasons"},priority=108,enabled=true)
	public void duplicateErrorValiadtionLWBSReasons() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();

		sleeps();
		if(driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

	}
	@Test(groups = {"LWBSReasons"},priority=109,enabled=true)
	public void sortingAndDeletingLWBSReasons() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"ReorientedDepartureAlternativeRessource"},priority=110,enabled=true)
	public void navigateToReorientedDepartureAlternativeRessource() throws InterruptedException, AWTException {

		String pageNameEnglish= "Reoriented departure - alternative ressource";
		String pageNameFrench = "Départ réorienté - ressource alternative";

		createTest=reports.createTest("Validating the functionaltiy of the Reoriented departure alternative Ressource screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Reoriented departure - alternative ressource')] | //div[contains(text(),' Départ réorienté - ressource alternative')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		if(driver.getPageSource().contains("//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')]")) {

			createTest.log(Status.FAIL,"The delete button is disabled");
		}else{
			createTest.log(Status.PASS,"The delete button is not disabled");
		}
		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));


		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {

			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		
		

	}
	@Test(groups = {"ReorientedDepartureAlternativeRessource"},priority=111,enabled=true)
	public void createingNewRecordReorientedDepartureAlternativeRessource() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		sleeps();
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		sleeps();

	}	
	@Test(groups = {"ReorientedDepartureAlternativeRessource"},priority=112,enabled=true)
	public void duplicateRecordReorientedDepartureAlternativeRessource() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn1 = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-focus ag-cell-inline-editing')]//child::input"));
		Nocoulmmn1.sendKeys(validCode);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		
		sleeps();
		if(driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		sleeps();
		clickRestoreButton();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		verifyingTheConfirmationMessage();
		
		
	}

	@Test(groups = {"ReorientedDepartureReasonforRefusal"},priority=113,enabled=true)
	public void navigateToReorientedDepartureReasonforRefusal() throws InterruptedException, AWTException {

		String pageNameEnglish= "Reoriented departure - reason for refusal";
		String pageNameFrench = "Départ réorienté - raison de refus/contre-indication";

		createTest=reports.createTest("Validating the functionaltiy of the Reoriented departure reason for refusal screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Reoriented departure - reason for refusal')] | //div[contains(text(),' Départ réorienté - raison de refus/contre-indication')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);
		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"ReorientedDepartureReasonforRefusal"},priority=114,enabled=true)
	public void createingNewRecordReorientedDepartureReasonforRefusal() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}


	}
	@Test(groups = {"ReorientedDepartureReasonforRefusal"},priority=115,enabled=false)
	public void codeColumnsValidationReorientedDepartureReasonforRefusal() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(invalidCode);
		clickSaveButton();

		if(driver.getPageSource().contains(invalidCode)) {

			createTest.log(Status.FAIL,"The code column accepts more than 10 digits");
		}else {
			createTest.log(Status.PASS,"The code column not accepts more than 10 digits");
		}

		clickDeleteButton();
		clickSaveButton();
	}

	@Test(groups = {"ReorientedDepartureReasonforRefusal"},priority=116,enabled=true)
	public void duplicateErrorValiadtionReorientedDepartureReasonforRefusal() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("This reason for refusal already exist.")|driver.getPageSource().contains("Cette raison de refus existe déjà. ")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("This reason for refusal already exist.")|driver.getPageSource().contains("Cette raison de refus existe déjà. ")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"ReorientedDepartureReasonforRefusal"},priority=117,enabled=true)
	public void sortingAndDeletingReorientedDepartureReasonforRefusal() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"Diet"},priority=118,enabled=true)
	public void navigateToDiet() throws InterruptedException, AWTException {

		String pageNameEnglish= "Diet";
		String pageNameFrench = "Diète";

		createTest=reports.createTest("Validating the functionaltiy of the Diet screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Diet')] | //div[contains(text(),' Diète')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		
		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"Diet"},priority=119,enabled=true)
	public void createingNewRecordDiet() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}


	}
	@Test(groups = {"Diet"},priority=120,enabled=true)
	public void codeColumnsValidationDiet() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(invalidCode);
		clickSaveButton();

		if(driver.getPageSource().contains(invalidCode)) {

			createTest.log(Status.FAIL,"The code column accepts more than 10 digits");
		}else {
			createTest.log(Status.PASS,"The code column not accepts more than 10 digits");
		}

		clickDeleteButton();
		clickSaveButton();
	}

	@Test(groups = {"Diet"},priority=121,enabled=true)
	public void duplicateErrorValiadtionDiet() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("This diet code already exists.")|driver.getPageSource().contains("Ce code de diète est déjà existant.")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("This diet code already exists.")|driver.getPageSource().contains("Ce code de diète est déjà existant.")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"Diet"},priority=122,enabled=true)
	public void sortingAndDeletingDiet() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"Institutionmissions"},priority=123,enabled=true)
	public void navigateToInstitutionmissions() throws InterruptedException, AWTException {

		String pageNameEnglish= "Institution (missions)";
		String pageNameFrench = "Établissement (missions)";

		createTest=reports.createTest("Validating the functionaltiy of the Institution (missions) screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Établissement (missions)')] | //div[contains(text(),'Institution (missions)')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(DescFN, 1);
		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"Institutionmissions"},priority=124,enabled=true)
	public void createingNewRecordInstitutionmissions() throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(DescFN, 1);

		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}


	}

	@Test(groups = {"Institutionmissions"},priority=125,enabled=true)
	public void duplicateErrorValiadtionInstitutionmissions() throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		if(driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {

			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}
	@Test(groups = {"Institutionmissions"},priority=126,enabled=true)
	public void sortingAndDeletingInstitutionmissions() throws InterruptedException, AWTException {

		filter(DescFN, 1);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"Lifecontext"},priority=127,enabled=true)
	public void navigateToLifecontext() throws InterruptedException, AWTException {

		String pageNameEnglish= "Life context";
		String pageNameFrench = "Milieu de vie";

		createTest=reports.createTest("Validating the functionaltiy of the Life context screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Milieu de vie')] | //div[contains(text(),' Life context')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(DescFN, 2);
		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"Lifecontext"},priority=128,enabled=true)
	public void createingNewRecordLifecontext() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(DescFN, 2);

		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}


	}
	@Test(groups = {"Lifecontext"},priority=129,enabled=true)
	public void codeColumnsValidationLifecontext() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(invalidCode);
		clickSaveButton();

		if(driver.getPageSource().contains(invalidCode)) {

			createTest.log(Status.FAIL,"The code column accepts more than 10 digits");
		}else {
			createTest.log(Status.PASS,"The code column not accepts more than 10 digits");
		}

		clickDeleteButton();
		clickSaveButton();
	}

	@Test(groups = {"Lifecontext"},priority=130,enabled=true)
	public void duplicateErrorValiadtionLifecontext() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"Lifecontext"},priority=131,enabled=true)
	public void sortingAndDeletingLifecontext() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		sleeps();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"ActivitiesforPABworkplan"},priority=132,enabled=true)
	public void navigateToActivitiesforPABworkplan() throws InterruptedException, AWTException {

		String pageNameEnglish= "Activities for PAB work plan";
		String pageNameFrench = "Activités pour plan de travail PAB";

		createTest=reports.createTest("Validating the functionaltiy of the Activities for PAB workplan screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Activités pour plan de travail PAB')] | //div[contains(text(),' Activities for PAB work plan')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(DescFN, 2);
		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}


	}

	@Test(groups = {"ActivitiesforPABworkplan"},priority=133,enabled=true)
	public void createingNewRecordActivitiesforPABworkplan() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The order column accepts only numeric values");
		}
	}

	@Test(groups = {"ActivitiesforPABworkplan"},priority=134,enabled=true)
	public void orderColumnsValidationActivitiesforPABworkplan() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(invalidCode);

		clickSaveButton();

		if(driver.getPageSource().contains("La conversion de la valeur varchar")|driver.getPageSource().contains("The conversion of the varchar value")) {
			createTest.log(Status.PASS,"The application throws an error when out of range value enetred in roder column");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when out of range value enetred in roder column");
		}

		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}

	@Test(groups = {"ActivitiesforPABworkplan"},priority=135,enabled=true)
	public void duplicateErrorValiadtionActivitiesforPABworkplan() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);

		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);


		clickSaveButton();

		if(driver.getPageSource().contains("Vous ne pouvez pas utiliser ce code")|driver.getPageSource().contains("You can not use this code,")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Vous ne pouvez pas utiliser ce code")|driver.getPageSource().contains("You can not use this code,")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"ActivitiesforPABworkplan"},priority=136,enabled=true)
	public void sortingAndDeletingActivitiesforPABworkplan() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"Provenance"},priority=137,enabled=true)
	public void navigateToProvenance() throws InterruptedException, AWTException {

		String pageNameEnglish= "Provenance";
		String pageNameFrench = "Provenance";

		createTest=reports.createTest("Validating the functionaltiy of the Provenance screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Provenance')] | //div[contains(text(),'Provenance')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"Provenance"},priority=138,enabled=true)
	public void createingNewRecordProvenance() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);

		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}


		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Presentation order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Presentation order column accepts only numeric values");
		}

		WebElement Filter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'mat-select-arrow-wrapper')]")));
		Filter.click();
		WebElement Filter1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'mat-select-arrow-wrapper')]")));
		Filter1.click();
		//wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' 005 ']"))).click();
		pressAndReleaseEnterKey();
		clickSaveButton();
	}
	@Test(groups = {"Provenance"},priority=139,enabled=true)
	public void duplicateErrorValiadtionProvenance() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"Provenance"},priority=140,enabled=true)
	public void sortingAndDeletingProvenance() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"HospitalizationReasonforcancellation"},priority=141,enabled=true)
	public void navigateToHospitalizationReasonforcancellation() throws InterruptedException, AWTException {

		String pageNameEnglish= "Hospitalization-Reason for cancellation";
		String pageNameFrench = "Hospitalisation - raison d'annulation";

		createTest=reports.createTest("Validating the functionaltiy of the Hospitalization-Reason for cancellation screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Hospitalization-Reason for cancellation'] | //div[contains(text(),'Hospitalisation - raison')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}


	@Test(groups = {"HospitalizationReasonforcancellation"},priority=142,enabled=true)
	public void createingNewRecordHospitalizationReasonforcancellation() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);
		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}
	@Test(groups = {"HospitalizationReasonforcancellation"},priority=143,enabled=true)
	public void duplicateErrorValiadtionHospitalizationReasonforcancellation() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"HospitalizationReasonforcancellation"},priority=144,enabled=true)
	public void sortingAndDeletingHospitalizationReasonforcancellation() throws InterruptedException, AWTException {

		filter(validCode, 1);
		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"TransferTransportation"},priority=145,enabled=true)
	public void navigateToTransferTransportation() throws InterruptedException, AWTException {

		String pageNameEnglish= "Transfer-Transportation";
		String pageNameFrench = "Transfert-Transport";

		createTest=reports.createTest("Validating the functionaltiy of the Transfer-Transportation screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Transfer-Transportation')] | //div[contains(text(),' Transfert-Transport')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}


		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"TransferTransportation"},priority=146,enabled=true)
	public void createingNewRecordTransferTransportation() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();	
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);
		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"TransferTransportation"},priority=147,enabled=true)
	public void codeColumnsValidationTransferTransportation() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The code column accepts alphanumeric value");
		}else {
			createTest.log(Status.PASS,"The code column accepts only numeric value");
		}

		clickDeleteButton();
		clickSaveButton();

		clickCreateButton();
		WebElement Nocoulmmn1 = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn1.sendKeys(invalidCode);
		clickSaveButton();

		if(driver.getPageSource().contains("Une erreur de dépassement arithmétique s'est produite lors")|driver.getPageSource().contains("Arithmetic overflow error converting expression")) {
			createTest.log(Status.PASS,"The application throws an error when out of range value enetred in Code column");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when out of range value enetred in Code column");
		}

		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"TransferTransportation"},priority=148,enabled=true)
	public void duplicateErrorValiadtionTransferTransportation() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"TransferTransportation"},priority=149,enabled=true)
	public void sortingAndDeletingTransferTransportation() throws InterruptedException, AWTException {

		filter(validCode, 1);
		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"TransferEscort"},priority=150,enabled=true)
	public void navigateToTransferEscort() throws InterruptedException, AWTException {

		String pageNameEnglish= "Transfer-Escort";
		String pageNameFrench = "Transfert-Escorte";

		createTest=reports.createTest("Validating the functionaltiy of the Transfer Escort screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Transfert-Escorte')] | //div[contains(text(),' Transfer-Escort')]"))).click();

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}


		filter(DescFN, 2);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"TransferEscort"},priority=151,enabled=true)
	public void createingNewRecordTransferEscort() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();	
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(DescFN, 2);
		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"TransferEscort"},priority=152,enabled=true)
	public void codeColumnsValidationTransferEscort() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(invalidCode);

		clickSaveButton();

		if(driver.getPageSource().contains(invalidCode)) {
			createTest.log(Status.FAIL,"The code column accepts more than 2 digits");
		}else {
			createTest.log(Status.PASS,"The code column accepts only upto 2 digits");
		}

		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"TransferEscort"},priority=153,enabled=true)
	public void duplicateErrorValiadtionTransferEscort() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"TransferEscort"},priority=154,enabled=true)
	public void sortingAndDeletingTransferEscort() throws InterruptedException, AWTException {

		filter(DescFN, 2);
		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"DischargePlanningstatus"},priority=155,enabled=true)
	public void navigateToDischargePlanningstatus() throws InterruptedException, AWTException {

		String pageNameEnglish= "Discharge-Planning status";
		String pageNameFrench = "État de la planification du congé";

		createTest=reports.createTest("Validating the functionaltiy of the Discharge-Planning status screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' État de la planification du congé')] | //div[contains(text(),' Discharge-Planning status')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}

	@Test(groups = {"DischargePlanningstatus"},priority=156,enabled=true)
	public void createingNewRecordDischargePlanningstatus() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();	
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);
		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"DischargePlanningstatus"},priority=157,enabled=true)
	public void codeColumnsValidationDischargePlanningstatus() throws InterruptedException, AWTException {


		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(invalidCode);

		clickSaveButton();

		if(driver.getPageSource().contains(invalidCode)) {
			createTest.log(Status.FAIL,"The code column accepts more than 4 digits");
		}else {
			createTest.log(Status.PASS,"The code column accepts only upto 4 digits");
		}
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"DischargePlanningstatus"},priority=158,enabled=true)
	public void duplicateErrorValiadtionDischargePlanningstatus() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"DischargePlanningstatus"},priority=159,enabled=true)
	public void sortingAndDeletingDischargePlanningstatus() throws InterruptedException, AWTException {

		filter(validCode, 1);
		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"ReturnhomeReferredServices"},priority=160,enabled=true)
	public void navigateToReturnhomeReferredServices() throws InterruptedException, AWTException {

		String pageNameEnglish= "Return home - Referred Services";
		String pageNameFrench = "Retour à domicile-Service référé";

		createTest=reports.createTest("Validating the functionaltiy of the Return home - Referred Services screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Retour à domicile-Service référé')] | //div[contains(text(),' Return home - Referred Services')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"ReturnhomeReferredServices"},priority=161,enabled=true)
	public void createingNewRecordReturnhomeReferredServices() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();	
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);

		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);
		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"ReturnhomeReferredServices"},priority=162,enabled=true)
	public void codeColumnsValidationReturnhomeReferredServices() throws InterruptedException, AWTException {


		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(invalidCode);

		clickSaveButton();

		if(driver.getPageSource().contains(invalidCode)) {
			createTest.log(Status.FAIL,"The code column accepts more than 4 digits");
		}else {
			createTest.log(Status.PASS,"The code column accepts only upto 4 digits");
		}
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"ReturnhomeReferredServices"},priority=163,enabled=true)
	public void duplicateErrorValiadtionReturnhomeReferredServices() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
	}
	@Test(groups = {"ReturnhomeReferredServices"},priority=164,enabled=true)
	public void sortingAndDeletingReturnhomeReferredServices() throws InterruptedException, AWTException {

		filter(validCode, 1);
		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"ReturnhomeTypesofdischarge"},priority=165,enabled=true)
	public void navigateToReturnhomeTypesofdischarge() throws InterruptedException, AWTException {

		String pageNameEnglish= "Return home - Types of discharge";
		String pageNameFrench = "Retour à domicile-Types de sortie";

		createTest=reports.createTest("Validating the functionaltiy of the Return home - Types of discharge screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Retour à domicile-Types de sortie')] | //div[contains(text(),' Return home - Types of discharge')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"ReturnhomeTypesofdischarge"},priority=166,enabled=true)
	public void createingNewRecordReturnhomeTypesofdischarge() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();	
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);
		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"ReturnhomeTypesofdischarge"},priority=167,enabled=true)
	public void codeColumnsValidationReturnhomeTypesofdischarge() throws InterruptedException, AWTException {


		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(invalidCode);

		clickSaveButton();

		if(driver.getPageSource().contains(invalidCode)) {
			createTest.log(Status.FAIL,"The code column accepts more than 4 digits");
		}else {
			createTest.log(Status.PASS,"The code column accepts only upto 4 digits");
		}
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"ReturnhomeTypesofdischarge"},priority=168,enabled=true)
	public void duplicateErrorValiadtionReturnhomeTypesofdischarge() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
	}
	@Test(groups = {"ReturnhomeTypesofdischarge"},priority=169,enabled=true)
	public void sortingAndDeletingReturnhomeTypesofdischarge() throws InterruptedException, AWTException {

		filter(validCode, 1);
		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"ReturnVisitReasons"},priority=170,enabled=true)
	public void navigateToReturnVisitReasons() throws InterruptedException, AWTException {

		String pageNameEnglish= "Return visit - reasons";
		String pageNameFrench = "Visite de retour - raisons";

		createTest=reports.createTest("Validating the functionaltiy of the Return visit - reasons screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Return visit - reasons')] | //div[contains(text(),' Visite de retour - raisons')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is not disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is not disabled disabled");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"ReturnVisitReasons"},priority=171,enabled=true)
	public void createingNewRecordReturnVisitReasons() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();	
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		driver.findElement(By.xpath("//div[@class='col-auto']//button[@class='btn btn-primary ng-star-inserted']")).click();
		sleeps();

		filter(validCode, 1);
		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"ReturnVisitReasons"},priority=172,enabled=true)
	public void codeColumnsValidationReturnVisitReasons() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn1 = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn1.sendKeys(invalidCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Une erreur de dépassement arithmétique s'est produite lors")|driver.getPageSource().contains("Arithmetic overflow error converting expression")) {
			createTest.log(Status.PASS,"The application throws an error when out of range value enetred in Code column");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when out of range value enetred in Code column");
		}

		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"ReturnVisitReasons"},priority=173,enabled=true)
	public void duplicateErrorValiadtionReturnVisitReasons() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);

		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		//			clickDeleteButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"ReturnVisitReasons"},priority=174,enabled=true)
	public void sortingAndDeletingReturnVisitReasons() throws InterruptedException, AWTException {

		filter(validCode, 1);
		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"Oxygenadministrationmethod"},priority=175,enabled=true)
	public void navigateToOxygenadministrationmethod() throws InterruptedException, AWTException {

		String pageNameEnglish= "Oxygen administration method";
		String pageNameFrench = "Méthode d'administration d'oxygène";


		createTest=reports.createTest("Validating the functionaltiy of the Oxygen administration method screen");
		sleeps();
		navigateToLocalTablesMenu();
		navigateToVitalSigns();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Oxygen administration method'] | //div[contains(text(),'Méthode ')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"Oxygenadministrationmethod"},priority=176,enabled=true)
	public void createingNewRecordOxygenadministrationmethod() throws InterruptedException, AWTException {

		clickCreateButton();
		//		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]")).click();
		//		Nocoulmmn.sendKeys(Code);
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		pressAndReleaseTabKey();	
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();
		sleeps();

		filter(DescFN, 3);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.FAIL,"The Order column accepts al");
		}else {
			createTest.log(Status.PASS,"The code column accepts 2 digits");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The");
		}else {
			createTest.log(Status.PASS,"The code column accepts 2 digits");
		}

	}

	@Test(groups = {"Oxygenadministrationmethod"},priority=177,enabled=true)
	public void codeColumnsValidationOxygenadministrationmethod() throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		pressAndReleaseTabKey();	
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(invalidCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(invalidCode);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		clickSaveButton();

		if(driver.getPageSource().contains("Une seule valeur peut être cochée")|driver.getPageSource().contains("A single value can be checked by default")) {
			createTest.log(Status.PASS,"The application throws an error when Triage & VS default column selected for more than one record");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when Triage & VS default column selected for more than one record");
		}

		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"Oxygenadministrationmethod"},priority=178,enabled=true)
	public void duplicateErrorValiadtionOxygenadministrationmethod() throws InterruptedException, AWTException {

		clickCreateButton();
		sleeps();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		pressAndReleaseTabKey();	
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();


		if(driver.getPageSource().contains("L'ordre doit être unique")|driver.getPageSource().contains("The order must be unique")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"Oxygenadministrationmethod"},priority=179,enabled=true)
	public void sortingAndDeletingOxygenadministrationmethod() throws InterruptedException, AWTException {

		filter(DescFN, 3);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"Oxygenadministrationflow"},priority=180,enabled=true)
	public void navigateToOxygenadministrationflow() throws InterruptedException, AWTException {

		String pageNameEnglish= "Oxygen administration flow";
		String pageNameFrench = "Débit d'administration d'oxygène";


		createTest=reports.createTest("Validating the functionaltiy of the Oxygen administration flow screen");
		sleeps();
		navigateToLocalTablesMenu();
		navigateToVitalSigns();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Oxygen administration flow'] | //div[contains(text(),'Débit ')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}


	}

	@Test(groups = {"Oxygenadministrationflow"},priority=181,enabled=true)
	public void createingNewRecordOxygenadministrationflow() throws InterruptedException, AWTException {

		clickCreateButton();
		sleeps();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//mat-select[contains(@class,'at-select-empty ng-untouched ng-pristine')]//div//div)[2]")).click();
		driver.findElement(By.xpath("(//mat-option[contains(@class,'mat-option mat-focus-indicator')])[3]")).click();
		pressAndReleaseTabKey();	
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();
		sleeps();

		filter(validCode, 1);

		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"Oxygenadministrationflow"},priority=182,enabled=true)
	public void duplicateErrorValiadtionOxygenadministrationflow() throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("(//mat-select[contains(@class,'at-select-empty ng-untouched ng-pristine')]//div//div)[2]")).click();
		driver.findElement(By.xpath("(//mat-option[contains(@class,'mat-option mat-focus-indicator')])[3]")).click();
		pressAndReleaseTabKey();	
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();

		sleeps();
		if(driver.getPageSource().contains("La combinaison de la valeur du débit et le code")|driver.getPageSource().contains("The combination of the flow rate value and the saturation code")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("La combinaison de la valeur du débit et le code")|driver.getPageSource().contains("The combination of the flow rate value and the saturation code")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}
	@Test(groups = {"Oxygenadministrationflow"},priority=183,enabled=true)
	public void sortingAndDeletingOxygenadministrationflow() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"HeartrateQualitative"},priority=184,enabled=true)
	public void navigateToHeartrateQualitative() throws InterruptedException, AWTException {

		String pageNameEnglish= "Heart rate - Qualitative";
		String pageNameFrench = "Fréquence cardiaque - Qualitatif";


		createTest=reports.createTest("Validating the functionaltiy of the Heart rate - Qualitative screen");
		sleeps();
		navigateToLocalTablesMenu();
		navigateToVitalSigns();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Heart rate - Qualitative'] | //div[contains(text(),'cardiaque - Qualitatif')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}



	}

	@Test(groups = {"HeartrateQualitative"},priority=185,enabled=true)
	public void createingNewRecordHeartrateQualitative() throws InterruptedException, AWTException {

		String CodeHQ="7695";

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(CodeHQ);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();
		sleeps();

		filter(CodeHQ, 1);

		sleeps();
		if(driver.getPageSource().contains(CodeHQ)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The order columns accepts alphanumeric value");
		}else {
			createTest.log(Status.PASS,"The order columns accepts only number values");
		}

	}

	@Test(groups = {"HeartrateQualitative"},priority=186,enabled=true)
	public void codeColumnsValidationHeartrateQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		clickSaveButton();


		if(driver.getPageSource().contains("Une seule valeur peut être cochée")|driver.getPageSource().contains("A single value can be checked by default")) {
			createTest.log(Status.PASS,"The application throws an error when Triage & VS default column selected for more than one record");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when Triage & VS default column selected for more than one record");
		}

		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"HeartrateQualitative"},priority=187,enabled=true)
	public void duplicateErrorValiadtionHeartrateQualitative() throws InterruptedException, AWTException {
		String CodeHQ="7695";
		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(CodeHQ);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();
		sleeps();

		if(driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {

			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		sleeps();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}
	@Test(groups = {"HeartrateQualitative"},priority=188,enabled=true)
	public void sortingAndDeletingHeartrateQualitative() throws InterruptedException, AWTException {

		String CodeHQ="7695";

		filter(CodeHQ, 1);

		activeRadiobtn(CodeHQ);
		nonActiveRadiobtn(CodeHQ);
		reservedRadiobtn(CodeHQ);
		AllRadiobtn(CodeHQ);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(CodeHQ))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"BloodpressureQualitative"},priority=189,enabled=true)
	public void navigateToBloodpressureQualitative() throws InterruptedException, AWTException {

		String pageNameEnglish= "Blood pressure - Qualitative";
		String pageNameFrench = "Tension artérielle - Qualitatif";


		createTest=reports.createTest("Validating the functionaltiy of the Blood pressure - Qualitative screen");
		sleeps();
		navigateToLocalTablesMenu();
		navigateToVitalSigns();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Blood pressure - Qualitative'] | //div[contains(text(),'- Qualitatif')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"BloodpressureQualitative"},priority=190,enabled=true)
	public void createingNewRecordBloodpressureQualitative() throws InterruptedException, AWTException {


		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.FAIL,"The Code column accepts more than 2 digits");
		}else {
			createTest.log(Status.PASS,"The Code column accepts upto 2 digits");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Order column accepts only numeric values");
		}

	}

	@Test(groups = {"BloodpressureQualitative"},priority=191,enabled=true)
	public void codeColumnsValidationBloodpressureQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		clickSaveButton();

		if(driver.getPageSource().contains("Une seule valeur peut être cochée")|driver.getPageSource().contains("A single value can be checked by default")) {
			createTest.log(Status.PASS,"The application throws an error when Triage & VS default column selected for more than one record");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when Triage & VS default column selected for more than one record");
		}

		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"BloodpressureQualitative"},priority=192,enabled=true)
	public void duplicateErrorValiadtionBloodpressureQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();


		if(driver.getPageSource().contains("Il existe au moins un doublon de code")|driver.getPageSource().contains("There is at least one blood pressure")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Il existe au moins un doublon de code")|driver.getPageSource().contains("There is at least one blood pressure")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"BloodpressureQualitative"},priority=193,enabled=true)
	public void sortingAndDeletingBloodpressureQualitative() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"RespiratoryrateQualitative"},priority=194,enabled=true)
	public void navigateToRespiratoryrateQualitative() throws InterruptedException, AWTException {

		String pageNameEnglish= "Respiratory rate - Qualitative";
		String pageNameFrench = "Fréquence respiratoire - Qualitatif";


		createTest=reports.createTest("Validating the functionaltiy of the Respiratory rate - Qualitative screen");
		sleeps();
		navigateToLocalTablesMenu();
		navigateToVitalSigns();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Respiratory rate - Qualitative'] | //div[contains(text(),'respiratoire - Qualitatif')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"RespiratoryrateQualitative"},priority=195,enabled=true)
	public void createingNewRecordRespiratoryrateQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}


		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Order column accepts only numeric values");
		}

	}

	@Test(groups = {"RespiratoryrateQualitative"},priority=196,enabled=true)
	public void codeColumnsValidationRespiratoryrateQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		clickSaveButton();

		if(driver.getPageSource().contains("Une seule valeur peut être cochée")|driver.getPageSource().contains("A single value can be checked by default")) {
			createTest.log(Status.PASS,"The application throws an error when Triage & VS default column selected for more than one record");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when Triage & VS default column selected for more than one record");
		}

		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"RespiratoryrateQualitative"},priority=197,enabled=true)
	public void duplicateErrorValiadtionRespiratoryrateQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();


		if(driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {

			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}
	@Test(groups = {"RespiratoryrateQualitative"},priority=198,enabled=true)
	public void sortingAndDeletingRespiratoryrateQualitative() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"TemperatureQualitative"},priority=199,enabled=true)
	public void navigateToTemperatureQualitative() throws InterruptedException, AWTException {

		String pageNameEnglish= "Temperature - Qualitative";
		String pageNameFrench = "Température - Qualitatif";


		createTest=reports.createTest("Validating the functionaltiy of the Temperature - Qualitative screen");
		sleeps();
		navigateToLocalTablesMenu();
		navigateToVitalSigns();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Temperature - Qualitative'] | //div[contains(text(),'rature - Qualitatif')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	String tempCode = RandomStringUtils.randomAlphanumeric(2);
	@Test(groups = {"TemperatureQualitative"},priority=200,enabled=true)
	public void createingNewRecordTemperatureQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(tempCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		//driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	//@Test(groups = {"TemperatureQualitative"},priority=201,enabled=true)
	public void codeColumnsValidationTemperatureQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(validCode);
		clickSaveButton();

		sleeps();
		if(driver.getPageSource().contains("Une seule valeur peut être cochée par")|driver.getPageSource().contains("A single value can be checked")) {

			createTest.log(Status.PASS,"The application throws an error when Triage & VS default column selected for more than one record");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when Triage & VS default column selected for more than one record");
		}

		sleeps();
		clickOKButton();
		clickDeleteButton();
		clickSaveButton();
	}
	@Test(groups = {"TemperatureQualitative"},priority=202,enabled=true)
	public void duplicateErrorValiadtionTemperatureQualitative() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(tempCode);
//		pressAndReleaseTabKey();
//		pressAndReleaseTabKey();
//		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
//		pressAndReleaseTabKey();
//		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
//		pressAndReleaseTabKey();
//		pressAndReleaseSpaceKey();
//		pressAndReleaseTabKey();
//		pressAndReleaseSpaceKey();
//		pressAndReleaseTabKey();
//		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();


		if(driver.getPageSource().contains("Il existe au moins un doublon de code")|driver.getPageSource().contains("There is at least one blood pressure")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Il existe au moins un doublon de code")|driver.getPageSource().contains("There is at least one blood pressure")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}
	}
	@Test(groups = {"TemperatureQualitative"},priority=203,enabled=true)
	public void sortingAndDeletingTemperatureQualitative() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"AbsenceReasonsScreeninTools"},priority=204,enabled=true)
	public void navigateToAbsenceReasonsScreeninTools() throws InterruptedException, AWTException {

		String pageNameEnglish= "Absence reasons - Screening tools";
		String pageNameFrench = "Raisons d'absence - Questionnaires de dépistage";
		
		sleeps();
		navigateToLocalTablesMenu();
		navigateToScreeningTools();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Absence reasons - Screening tools'] | //div[contains(text(),'absence - Questionnaires de d')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		createTest=reports.createTest("Validating the functionaltiy of the Absence reasons - Screening tools screen");
		
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"AbsenceReasonsScreeninTools"},priority=205,enabled=true)
	public void createingNewRecordAbsenceReasonsScreeninTools() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		clickSaveButton();

		filter(validCode, 1);

		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}

	@Test(groups = {"AbsenceReasonsScreeninTools"},priority=206,enabled=true)
	public void duplicateErrorValiadtionAbsenceReasonsScreeninTools() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		clickSaveButton();


		sleeps();
		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}
	@Test(groups = {"AbsenceReasonsScreeninTools"},priority=207,enabled=true)
	public void sortingAndDeletingAbsenceReasonsScreeninTools() throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	
	//---------------------------------------------------Scores----------------------------------------------------------------------------------
	
	String ScoredescFR =RandomStringUtils.randomAlphanumeric(5);
	String ScoredescEN =RandomStringUtils.randomAlphanumeric(5);
	String Score_pageNameFR= "Scores";
	String Score_pageNameEN = "Scores";
	
	void navigateToScore() throws InterruptedException
	{
		navigateToLocalTablesMenu();
		navigateToScreeningTools();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Scores'] | //div[contains(text(),' Scores')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		
	}
	void createScore() throws InterruptedException, AWTException
	{	
		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(ScoredescFR);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(ScoredescEN);
		clickSaveButton();
			
	}

	@Parameters("Browser")
	@Test(groups = {"Score"},priority=208)
	void Score_Validation(String Browser) throws InterruptedException, AWTException
	{
		String editCode = RandomStringUtils.randomNumeric(4);
		String restoreCode = RandomStringUtils.randomNumeric(4);
		
		createTest=reports.createTest("Verifying Screen " + Score_pageNameEN);	
		navigateToScore();
		verifyBreadCrumb(Score_pageNameEN, Score_pageNameFR);
			
		
		
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
							
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
			
		
				
		createScore();
		filter(ScoredescFR,1);
		if(driver.getPageSource().contains(ScoredescFR))
		{
			createTest.log(Status.PASS,"Successfully created new record");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row ");
		}
			   
		//Edit-----------------
		checkEdit(ScoredescFR,editCode);
			
		//Check Restore
		checkRestore(ScoredescFR,restoreCode);
		
		//create duplicate-----------------
		createScore();
		verifyDuplicationAllowed();
		
		
	}

	@Test(groups = {"Score"},priority=209 )
	void Score_filterRecords() throws InterruptedException, AWTException
	{
		navigateToScore();
		checkFilterAndDeletion(ScoredescFR);
		deleteRecordAndExitFromPage(ScoredescFR);
		navigateToScore();
		verifyDeletion(ScoredescFR);
		clickHome();
			
	}
	
	//----------------------------------------------------------Brief Assessment--------------------------------------------------------------------

	String BriefAssessmentdescFR =RandomStringUtils.randomAlphanumeric(5);
	String BriefAssessmentdescEN =RandomStringUtils.randomAlphanumeric(5);
	String BriefAssessment_pageNameFR= "Brief assessment - Reasons";
	String BriefAssessment_pageNameEN = "Évaluation brève - Raisons";
	
	void navigateToBriefAssessment() throws InterruptedException
	{
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Évaluation brève - Raisons')] | //div[contains(text(),' Brief assessment - Reasons')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		
	}
	void createBriefAssessment() throws InterruptedException, AWTException
	{	
		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(BriefAssessmentdescFR);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(BriefAssessmentdescEN);
		clickSaveButton();
			
	}

	@Parameters("Browser")
	@Test(groups = {"BriefAssessmentReasons"},priority=210)
	void BriefAssessment_Validation(String Browser) throws InterruptedException, AWTException
	{
		String editCode = RandomStringUtils.randomNumeric(4);
		String restoreCode = RandomStringUtils.randomNumeric(4);
		
		createTest=reports.createTest("Verifying Screen " + BriefAssessment_pageNameEN);	
		navigateToBriefAssessment();
		verifyBreadCrumb(BriefAssessment_pageNameEN, BriefAssessment_pageNameFR);
			
		
		
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
							
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
			
		
				
		createBriefAssessment();
		filter(BriefAssessmentdescFR,1);
		if(driver.getPageSource().contains(BriefAssessmentdescFR))
		{
			createTest.log(Status.PASS,"Successfully created new record");
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row ");
		}
			   
		//Edit-----------------
		checkEdit(BriefAssessmentdescFR,editCode);
			
		//Check Restore
		checkRestore(BriefAssessmentdescFR,restoreCode);
		
		//create duplicate-----------------
		createBriefAssessment();
		verifyDuplicationAllowed();
		
		
	}

	@Test(groups = {"BriefAssessmentReasons"},priority=211 )
	void BriefAssessment_filterRecords() throws InterruptedException, AWTException
	{
		navigateToBriefAssessment();
		checkFilterAndDeletion(BriefAssessmentdescFR);
		deleteRecordAndExitFromPage(BriefAssessmentdescFR);
		navigateToBriefAssessment();
		verifyDeletion(BriefAssessmentdescFR);
		clickHome();
			
	}
	
	//--------------------------------------------------------Ventilators----------------------------------------------------------------------------------------------
	@Test(groups = {"Ventilators"},priority=214,enabled=true)
	public void navigateToVentilators () throws InterruptedException, AWTException {

		String pageNameEnglish= "Ventilators";
		String pageNameFrench = "Ventilateurs";


		createTest=reports.createTest("Validating the functionaltiy of the Ventilators screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Ventilators')] | //div[contains(text(),' Ventilateurs')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(validCode, 1);

		if(driver.getPageSource().contains(validCode))
		{
			driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"Ventilators"},priority=215,enabled=true)
	public void createingNewRecordVentilators () throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();

		filter(validCode, 1);

		sleeps();
		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Order column accepts only numeric values");
		}
	}

	@Test(groups = {"Ventilators"},priority=216,enabled=true)
	public void duplicateErrorVentilators () throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();	
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);
		clickSaveButton();


		if(driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}
	@Test(groups = {"Ventilators"},priority=217,enabled=true)
	public void sortingAndDeletingVentilators () throws InterruptedException, AWTException {

		filter(validCode, 1);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"Absencereasons"},priority=218,enabled=true)
	public void navigateToAbsencereasons() throws InterruptedException, AWTException {

		String pageNameEnglish= "*Absence reasons";
		String pageNameFrench = "*Raisons d'absence";


		createTest=reports.createTest("Validating the functionaltiy of the *Absence reasons screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' *Absence reasons'] | (//div[contains(text(),' *Raisons ')])[1]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		filter(DescFN, 2);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}


	}

	@Test(groups = {"Absencereasons"},priority=219,enabled=true)
	public void createingNewRecordAbsencereasons () throws InterruptedException, AWTException {

		String validCode="-1609";

		clickCreateButton();
		sleeps();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		
		driver.findElement(By.xpath("//div[contains(@class,'ag-input-wrapper ag-text-field-input-wrapper')]//input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		
		driver.findElement(By.xpath("//div[contains(@class,'ag-input-wrapper ag-text-field-input-wrapper')]//input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-auto-height cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
//		sleeps();	
//		sleeps();
//		WebElement findElement = driver.findElement(By.xpath("(//mat-select[contains(@class,'at-select-empty ng-pristine ng-valid ng-star-inserted ng-touched')]//div)[3]"));
//		sleeps();
//		sleeps();
//		findElement.click();
//		sleeps();
//		driver.findElement(By.xpath("(//div[contains(@class,'grid-container ng-star-inserted')])[4]")).click();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();

		pressAndReleaseTabKey();
		
		pressAndReleaseSpaceKey();

		clickSaveButton();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Order column accepts only numeric values");
		}


	}
	@Test(groups = {"Absencereasons"},priority=220,enabled=true)
	public void validatingCodecolumn() throws InterruptedException {

		clickCreateButton();
		sleeps();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		clickSaveButton();


		if(driver.getPageSource().contains("On ne peut créer/modifier/détruire les raisons")|driver.getPageSource().contains("It is forbidden to create/modify/destroy a absence")) {
			createTest.log(Status.PASS,"The application throws an error when we entered value more than 0 in code column");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when we entered value more than 0 in code column");
		}
		clickOKButton();
		sleeps();
		clickDeleteButton();
		//		clickRestoreButton();
		clickSaveButton();

	}

	@Test(groups = {"Absencereasons"},priority=221,enabled=true)
	public void duplicateErrorAbsencereasons () throws InterruptedException, AWTException {

		String validCode="-1609";

		clickCreateButton();
		sleeps();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		clickSaveButton();


		if(driver.getPageSource().contains("Vous ne pouvez pas utiliser deux codes identiques")|driver.getPageSource().contains("You cannot use the same code twice")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickDeleteButton();
		//		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Vous ne pouvez pas utiliser deux codes identiques")|driver.getPageSource().contains("You cannot use the same code twice")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}
	@Test(groups = {"Absencereasons"},priority=222,enabled=true)
	public void sortingAndDeletingAbsencereasons () throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"AbsencereasonsColors"},priority=223,enabled=true)
	public void navigateToAbsencereasonsColors() throws InterruptedException, AWTException {

		String pageNameEnglish= "*Absence reasons - Colors";
		String pageNameFrench = "*Raisons d'absence - Couleurs";


		createTest=reports.createTest("Validating the functionaltiy of the *Absence reasons - Colors screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' *Absence reasons - Colors'] | //div[contains(text(),'absence - Couleurs')]"))).click();
		sleeps();
		createTest=reports.createTest("Validating the functionaltiy of the *Absence reasons - Colors screen");
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(DescFN, 1);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}


	}

	@Test(groups = {"AbsencereasonsColors"},priority=224,enabled=true)
	public void createingNewRecordAbsencereasonsColors () throws InterruptedException, AWTException {

		clickCreateButton();
		
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		sleeps();
		sleeps();
		//		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing cell-wrap-text')])[2]")).sendKeys(DescFN);
		//		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(alphanumericCode);

		filter(DescFN, 1);

		WebElement color = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//app-color-renderer[contains(@class,'ng-star-inserted')])[last()]")));
		color.click();


		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"))).click();
		sleeps();

		WebElement colorWindiow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'type-policy-arrow')])[2]")));
		colorWindiow.click();

		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')]"))).click();
		clickSaveButton();


		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Order column accepts only numeric values");
		}

	}

	@Test(groups = {"AbsencereasonsColors"},priority=225,enabled=true)
	public void sortingAndDeletingAbsencereasonsColors () throws InterruptedException, AWTException {

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	
	//--------------------------------------------------------------------------Internal Order Forced------------------------------------------------------
	@Test(groups = {"InternalOrderForced"},priority=226,enabled=true)
	public void navigateToInternalOrderForced() throws InterruptedException {

		String pageNameEnglish= "internal order, forced";
		String pageNameFrench = "ordonnance interne, forcé";


		createTest=reports.createTest("Validating the functionaltiy of the Internal order, forced screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Internal order, forced'] | //div[contains(text(),'Ordonnance interne, forc')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"InternalOrderForced"},priority=227,enabled=true)
	public void createingNewRecordInternalOrderForced() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='mat-select-2-panel']")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[6]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[7]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();


		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();
		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"InternalOrder"},priority=228,enabled=true)
	public void navigateToOrdonnanceInterne() throws InterruptedException {

		String pageNameEnglish= "internal order";
		String pageNameFrench = "ordonnance interne";


		createTest=reports.createTest("Validating the functionaltiy of the Internal order screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Internal order'] | (//div[contains(text(),' Ordonnance interne')])[2]"))).click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"InternalOrder"},priority=229,enabled=true)
	public void createingNewRecordOrdonnanceInterne() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		sleeps();
		sleeps();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"TransferPatientlist"},priority=230,enabled=true)
	public void navigateToTransferPatientlist() throws InterruptedException {

		String pageNameEnglish= "Transfer / Patient list";
		String pageNameFrench = "Transfert / Liste de patients";


		createTest=reports.createTest("Validating the functionaltiy of the Transfer / Patient list screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Transfer / Patient list'] | (//div[contains(text(),' Transfert / Liste de patients')])"))).click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"TransferPatientlist"},priority=231,enabled=true)
	public void createingNewRecordTransferPatientlist() throws InterruptedException, AWTException {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}
		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		sleeps();
		sleeps();


		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"Leavingmedication24hrs"},priority=232,enabled=true)
	public void navigateToLeavingmedicationt() throws InterruptedException {

		String pageNameEnglish= "Leaving medication (24hrs)";
		String pageNameFrench = "Médication au départ (24hrs)";


		createTest=reports.createTest("Validating the functionaltiy of the Leaving medication (24hrs) screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Leaving medication (24hrs)'] | (//div[contains(text(),' Médication au départ (24hrs)')])"))).click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"Leavingmedication24hrs"},priority=233,enabled=true)
	public void createingNewRecordLeavingmedicationt() throws InterruptedException, AWTException {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='mat-select-2-panel']")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		sleeps();
		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"ReasonsForCodeOrange"},priority=234,enabled=true)
	public void navigateToReasonsForCodeOrange () throws InterruptedException, AWTException {

		String pageNameEnglish= "Reasons for code Orange";
		String pageNameFrench = "Raisons de code Orange";


		
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Raisons de code Orange')] | //div[contains(text(),' Reasons for code Orange')]"))).click();
		sleeps();
		createTest=reports.createTest("Validating the functionaltiy of the Reasons for code Orange screen");
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(DescFN, 1);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"ReasonsForCodeOrange"},priority=235,enabled=true)
	public void createingNewRecordReasonsForCodeOrange () throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		sleeps();
		sleeps();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"));
		Nocoulmmn.sendKeys(DescFN);
		sleeps();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(alphanumericCode);
		clickSaveButton();

		filter(DescFN, 1);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Order column accepts only numeric values");
		}
	}

	@Test(groups = {"ReasonsForCodeOrange"},priority=236,dependsOnMethods = { "createingNewRecordReasonsForCodeOrange" })
	public void configurationrReasonsForCodeOrange () throws InterruptedException, AWTException {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Configuration')] | //button[contains(text(),'Configuration')]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]"))).click();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'ag-right-aligned-cell ag-cell-inline-editing ag-cell-focus')]//input"))).sendKeys(alphanumericCode);
		clickSaveAndContinueButton();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(text(),'Rétablir')] | //button[contains(text(),'Restore')])[2]"))).click();
		
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@class='btn btn-secondary'])[1]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='material-symbols-outlined add-tree-item'])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'mat-select-arrow-wrapper')]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-option[contains(@class,'mat-option mat-focus-indicator')])[3]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-icon[contains(@class,'mat-icon notranslate mat-icon-rtl-mirror ')])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//label[contains(@class,'mat-checkbox-layout')])[23]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='OK']"))).click(); 

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[@class='btn btn-secondary cust-line-h'])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='material-symbols-outlined add-tree-item'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='OK']"))).click(); 



		clickSaveAndCloseButton();

	}
	@Test(groups = {"ReasonsForCodeOrange"},priority=237,enabled=true)
	public void sortingAndDeletingReasonsForCodeOrange () throws InterruptedException, AWTException {


				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')])[1]"))).click();
				pressAndReleaseSpaceKey();

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')])[1]"))).click();
		pressAndReleaseSpaceKey(); 
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"GasResultsCorrespondence"},priority=238,enabled=true)
	public void navigateToGasResultsCorrespondence () throws InterruptedException, AWTException {

		String pageNameEnglish= "Gas results correspondence";
		String pageNameFrench = "Correspondance résultats de gaz";


		
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Gas results correspondence')] | //div[contains(text(),'Correspondance résultats de gaz')]"))).click();
		sleeps();
		createTest=reports.createTest("Validating the functionaltiy of the Gas results correspondence screen");
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(DescFN, 1);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}

	@Test(groups = {"GasResultsCorrespondence"},priority=239,enabled=true)
	public void createingNewRecordGasResultsCorrespondence () throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'mat-option-text')])[4]"))).click();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'mat-option-text')])[2]"))).click();

		pressAndReleaseTabKey();
		WebElement Nocoulmmn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")));
		Nocoulmmn.sendKeys(DescFN);
		clickSaveButton();

		filter(DescFN, 1);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {
			createTest.log(Status.FAIL,"The Order column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Order column accepts only numeric values");
		}
	}
	@Test(groups = {"GasResultsCorrespondence"},priority=240,enabled=true)
	public void sortingAndDeletingGasResultsCorrespondence () throws InterruptedException, AWTException {

		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	@Test(groups = {"ReasonsofConsultation"},priority=241,enabled=true)
	public void navigateToReasonsofConsultation() throws InterruptedException, AWTException {

		String pageNameEnglish= "Reasons of consultation";
		String pageNameFrench = "Raisons de consultation";


		createTest=reports.createTest("Validating the functionaltiy of the Reasons of consultation screen");
		sleeps();
		navigateToLocalTablesMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Reasons of consultation')] | //div[contains(text(),' Raisons de consultation')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		filter(DescFN, 2);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

	}
	@Test(groups = {"ReasonsofConsultation"},priority=242,enabled=true)
	public void createingNewRecordReasonsofConsultation () throws InterruptedException, AWTException {

		if(driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[8]")).isDisplayed()) {

			createTest.log(Status.PASS,"The list of consultation columns are not editable");
		}else {

			createTest.log(Status.FAIL,"The list of consultation columns are editable");
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')])[8]"))).click();
		clickCreateButton();
		pressAndReleaseSpaceKey();
		sleeps();
		pressAndReleaseTabKey();
		WebElement Nocoulmmn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")));
		Nocoulmmn.sendKeys(DescFN);
		pressAndReleaseTabKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"))).sendKeys(DescEN);
		clickSaveButton();


		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	@Test(groups = {"ScreeningToolsConfiguration"},priority=243,enabled=true)
	public void navigateToScreeningToolsConfiguration() throws InterruptedException, AWTException {

		String pageNameEnglish= " *Screening tools configuration ";
		String pageNameFrench = "*Configuration des questionnaires de dépistage";


		
		sleeps();
		navigateToLocalTablesMenu();
		navigateToScreeningTools();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' *Screening tools configuration')] | //div[contains(text(),'*Configuration des questionnaires de dépistage')]"))).click();
		createTest=reports.createTest("Validating the functionaltiy of the *Screening tools configuration screen");
		sleeps();
		
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.FAIL,"Restore button is disabled by default");

		} else{
			createTest.log(Status.PASS,"Restore button is enabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.FAIL,"Save button is disabled by default");

		}else{
			createTest.log(Status.PASS,"Save button is enabled by default");
		}



	}
	@Test(groups = {"ScreeningToolsConfiguration"},priority=244,enabled=true)
	public void createingNewRecordScreeningToolsConfiguration () throws InterruptedException, AWTException {
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-select[contains(@id,'mat-select')])[1]"))).click();
		
		
		if(driver.getPageSource().contains(DescFN)) {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='"+DescFN+"']"))).click();
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Supprimer un questionnaire')] | //button[contains(text(),'Delete a questionnaire')]"))).click();
			sleeps();
			if(driver.getPageSource().contains("Do you really want to delete this paragraph/question?")|driver.getPageSource().contains("Désirez-vous vraiment supprimer ce questionnaire?")) {
				clickYesButton();
			}
		}else {
			pressAndReleaseEnterKey();
		}
		

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),' Add a questionnaire')] | //button[contains(text(),' Ajouter un questionnaire')]"))).click();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-outer-circle'])[2]"))).click();

		}catch(ElementNotInteractableException e) {
			sleeps();
			driver.findElement(By.xpath("(//span[@class='mat-radio-inner-circle'])[2]")).click();
			//			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-outer-circle'])[2]"))).click();
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')]"))).click();

		if(driver.getPageSource().contains("Vous devez sélectionner un modèle de création.")|driver.getPageSource().contains("You must select a design template.")) {
			createTest.log(Status.PASS,"The application throws an error when we click OK button without selecting design template");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we click OK button without selecting design template");
		}

		clickOKButton();

		try {

			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-outer-circle'])[1]"))).click();

		}catch(ElementNotInteractableException e) {
			sleeps();
			driver.findElement(By.xpath("(//span[@class='mat-radio-inner-circle'])[1]")).click();
			//			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-outer-circle'])[2]"))).click();
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-select[contains(@class,'ng-star-inserted mat-select-empty')]//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Isar')] | //span[contains(text(),'Isar ')]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')]"))).click();

		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),' Add a questionnaire')] | //button[contains(text(),' Ajouter un questionnaire')]"))).click();

		if(driver.getPageSource().contains("Changes have been made on the current questionnaire.")|driver.getPageSource().contains("Des modifications ont été effectuées sur le questionnaire en cours.")) {

			createTest.log(Status.PASS,"The application throws an error when we try to add questionnaire without saving the exisiting changes");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we click OK button without selecting design template");
		}

		clickCancel();

		WebElement ID = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@id,'mat-input')])[1]")));
		sleeps();
		ID.click();

		if(driver.findElement(By.xpath("//input[contains(@class,'ng-pristine ng-valid')]")).isDisplayed())
		{
			createTest.log(Status.PASS,"ID column is non-editable");
		}
		else
		{
			createTest.log(Status.FAIL,"ID column is editable");
		}
		pressAndReleaseTabKey();

		WebElement name = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@id,'mat-input')])[2]")));
		name.clear();
		name.sendKeys(DescFN);

		clickSaveButton();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Modify a questionnaire')] | //button[contains(text(),'Modifier un questionnaire')]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-checkbox[contains(@class,'mat-checkbox example-margin mat-accent')]"))).click();
		clickSaveButton();

		if(driver.getPageSource().contains("Configuration des questionnaires de dépistage")|driver.getPageSource().contains("Screening tools configuration")) {

			createTest.log(Status.PASS,"The application throws an error when we try to add active questionnaire");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we try to add active questionnaire");
		}

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(),'Configuration des questionnaires de dépistage')] | //h2[contains(text(),'Screening tools configuration')]"))).click();
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')]"))).click();

		clickRestoreButton();

		if(driver.getPageSource().contains("Do you really want to cancel")|driver.getPageSource().contains("Désirez-vous vraiment annuler vos modifications?")) {

			createTest.log(Status.PASS,"The application throws an error when we click on restore button without saving the changes");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we click on restore button without saving the changes");
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),' Yes ')] | //span[contains(text(),' Oui ')]"))).click();	

	}
	@Test(groups = {"ScreeningToolsConfiguration"},priority=245,enabled=true)
	public void creatingNewParagraph() throws InterruptedException, AWTException {

		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Modify a questionnaire')] | //button[contains(text(),'Modifier un questionnaire')]"))).click();
		sleeps();
		clickCreateButton();
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-select[contains(@class,'ng-star-inserted ng-touched')])//div[contains(@class,'mat-select-arrow-wrapper')]"))).click();

		pressAndReleaseTabKey();
		sleeps();
		WebElement Des = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")));
		Des.sendKeys(DescFN);
		sleeps();
		pressAndReleaseTabKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input"))).sendKeys(DescEN);
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();	
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();

		sleeps();
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-select[contains(@class,'ng-star-inserted ng-touched')])"))).click();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		clickSaveButton();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Modify a questionnaire')] | //button[contains(text(),'Modifier un questionnaire')]"))).click();

		
		clickDeleteButton();

		if(driver.getPageSource().contains("Do you really want to delete this questionnaire?")|driver.getPageSource().contains("Désirez-vous vraiment supprimer ce questionnaire?")) {

			createTest.log(Status.PASS,"The application throws an pop-up when we delete the exisitng records");
			clickYesButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an pop-up when we delete the exisitng records");
		}

		clickSaveButton();


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//mat-select[contains(@id,'mat-select')])[1]"))).click();
		
		sleeps();
		if(driver.getPageSource().contains(DescFN)){

			createTest.log(Status.FAIL,"The questinnaire is not deleted successfully");
		}else {
			createTest.log(Status.PASS,"The questinnaire is deleted successfully");
		}
		
		pressAndReleaseEnterKey();
	}

	@Test(groups = {"ClinicalDossier"},priority=246,enabled=true)
	public void navigateToClinicalDossier() throws InterruptedException {

		String pageNameEnglish= "Clinical dossier";
		String pageNameFrench = "Dossier clinique informatisé";


		createTest=reports.createTest("Validating the functionaltiy of the Clinical dossier screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(" //div[contains(text(),'Clinical dossier')] | //div[contains(text(),' Dossier clinique informatisé')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"ClinicalDossier"},priority=247,enabled=true)
	public void createingNewRecordClinicalDossier() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"SummaryofCare"},priority=248,enabled=true)
	public void navigateToSummaryofCarer() throws InterruptedException {

		String pageNameEnglish= "Summary of care";
		String pageNameFrench = "Sommaire des soins";


		createTest=reports.createTest("Validating the functionaltiy of the Summary of care screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Summary of care')] | //div[contains(text(),' Sommaire des soins')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"SummaryofCare"},priority=249,enabled=true)
	public void createingNewRecordSummaryofCare() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"AH280"},priority=250,enabled=true)
	public void navigateToAH280() throws InterruptedException {

		String pageNameEnglish= "AH-280";
		String pageNameFrench = "AH-280";


		createTest=reports.createTest("Validating the functionaltiy of the AH-280 screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'AH-280')] | //div[contains(text(),' AH-280')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"AH280"},priority=251,enabled=true)
	public void createingNewRecordAH280() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[1]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"ReturnVisitTicket"},priority=252,enabled=true)
	public void navigateToReturnVisitTicket() throws InterruptedException {

		String pageNameEnglish= "Return visit ticket";
		String pageNameFrench = "Billet de retour";


		createTest=reports.createTest("Validating the functionaltiy of the Return visit ticket screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Return visit ticket')] | //div[contains(text(),'Billet de retour')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"ReturnVisitTicket"},priority=253,enabled=true)
	public void createingNewRecordReturnVisitTicket() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[1]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"Consultant"},priority=254,enabled=true)
	public void navigateToConsultant() throws InterruptedException {

		String pageNameEnglish= "Consultant";
		String pageNameFrench = "Consultant";


		createTest=reports.createTest("Validating the functionaltiy of the Consultant screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Consultant')] | //div[contains(text(),'Consultant')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"Consultant"},priority=255,enabled=true)
	public void createingNewRecordConsultant() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"EmergencyInitialPrescription"},priority=256,enabled=true)
	public void navigateToEmergencyInitialPrescription() throws InterruptedException {

		String pageNameEnglish= "Emergency initial prescription";
		String pageNameFrench = "Ordonnance initiale de l'urgence";


		createTest=reports.createTest("Validating the functionaltiy of the Emergency initial prescription screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Emergency initial prescription'] | //div[contains(text(),' Ordonnance initiale')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"EmergencyInitialPrescription"},priority=257,enabled=true)
	public void createingNewRecordEmergencyInitialPrescription() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"DischargeOrder"},priority=258,enabled=true)
	public void navigateToDischargeOrder() throws InterruptedException {

		String pageNameEnglish= "Discharge order";
		String pageNameFrench = "Ordonnances au départ";


		createTest=reports.createTest("Validating the functionaltiy of the Discharge order screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Discharge order'] | //div[contains(text(),'Ordonnances au d')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"DischargeOrder"},priority=259,enabled=true)
	public void createingNewRecordDischargeOrder() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"PAB"},priority=260,enabled=true)
	public void navigateToPAB() throws InterruptedException {

		String pageNameEnglish= "PAB";
		String pageNameFrench = "PAB";


		createTest=reports.createTest("Validating the functionaltiy of the PAB screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' PAB'] | //div[contains(text(),' PAB')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"PAB"},priority=261,enabled=true)
	public void createingNewRecordPAB() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"Comparative"},priority=262,enabled=true)
	public void navigateToComparative() throws InterruptedException {

		String pageNameEnglish= "Comparative";
		String pageNameFrench = "Bilan comparatif";


		createTest=reports.createTest("Validating the functionaltiy of the Comparative screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Comparative'] | //div[contains(text(),' Bilan comparatif')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"Comparative"},priority=263,enabled=true)
	public void createingNewRecordComparative() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"VitalSigns"},priority=264,enabled=true)
	public void navigateToVitalSign() throws InterruptedException {

		String pageNameEnglish= "Vital signs";
		String pageNameFrench = "Signes vitaux";


		createTest=reports.createTest("Validating the functionaltiy of the Vital signs screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Vital signs'] | //div[contains(text(),'Signes vitaux')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"VitalSigns"},priority=265,enabled=true)
	public void createingNewRecordVitalSigns() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"UrineDipstick"},priority=266,enabled=true)
	public void navigateToUrineDipstick() throws InterruptedException {

		String pageNameEnglish= "Urine dipstick";
		String pageNameFrench = "Bâton d'urine";


		createTest=reports.createTest("Validating the functionaltiy of the Urine Dipstick screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Urine dipstick'] | //div[contains(text(),'Bâton ')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"UrineDipstick"},priority=267,enabled=true)
	public void createingNewRecordUrineDipstick() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"NeurologicalSigns"},priority=268,enabled=true)
	public void navigateToNeurologicalSigns() throws InterruptedException {

		String pageNameEnglish= "Neurological signs";
		String pageNameFrench = "Signes neurologiques";


		createTest=reports.createTest("Validating the functionaltiy of the Neurological signs screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Neurological signs'] | //div[contains(text(),' Signes neurologiques')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"NeurologicalSigns"},priority=269,enabled=true)
	public void createingNewRecordNeurologicalSigns() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	@Test(groups = {"CriticalPatients"},priority=270,enabled=true)
	public void navigateToCriticalPatients() throws InterruptedException {

		String pageNameEnglish= "Critical patients";
		String pageNameFrench = "Patients critiques";


		createTest=reports.createTest("Validating the functionaltiy of the Critical patients screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Critical patients'] | //div[contains(text(),' Patients critiques')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"CriticalPatients"},priority=271,enabled=true)
	public void createingNewRecordCriticalPatients() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"Triage"},priority=272,enabled=true)
	public void navigateToTriage() throws InterruptedException {

		String pageNameEnglish= "Triage";
		String pageNameFrench = "Triage";


		createTest=reports.createTest("Validating the functionaltiy of the Triage screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[text()=' Triage'] | //div[text()=' Triage'])[2]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"Triage"},priority=273,enabled=true)
	public void createingNewRecordTriage() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[1]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();
		//
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[6]"))).click();
//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));

		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}

		//		WebElement defaultPrinter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(),'Default printer configured')] | //label[contains(text(),'Imprimante par défaut configurée')]")));
		//		WebElement alternativePrinter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(),'Alternative printer')] | //label[contains(text(),'Imprimante alternative')]")));
		//		WebElement ChartMaxxPrinter = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//label[contains(text(),'ChartMaxx printer')] | //label[contains(text(),'Imprimante ChartMaxx')]")));

		if(driver.findElement(By.xpath("//label[contains(text(),'Default printer configured')] | //label[contains(text(),'Imprimante par défaut configurée')]")).isDisplayed() && 
				driver.findElement(By.xpath("//label[contains(text(),'Alternative printer')] | //label[contains(text(),'Imprimante alternative')]")).isDisplayed()) {

			createTest.log(Status.PASS,"Paper set up has both default Printer & alternative Priner ");
		}else {
			createTest.log(Status.FAIL,"Paper set up has not both default Printer & alternative Printer ");
		}


		//		if(!driver.findElement(By.xpath("//label[contains(text(),'ChartMaxx printer')] | //label[contains(text(),'Imprimante ChartMaxx')]")).isEnabled()) {
		//			createTest.log(Status.PASS,"Paper set up does not have ChartMaxxPrinter ");
		//		}else {
		//			createTest.log(Status.FAIL,"Paper set up has ChartMaxxPrinter ");
		//		}
	}

	//@Test(groups = {"Triage"},priority=274,enabled=true)
	public void CheckingMixSetup() {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();

		if(driver.findElement(By.xpath("//label[contains(text(),'Default printer configured')] | //label[contains(text(),'Imprimante par défaut configurée')]")).isDisplayed() && 
				driver.findElement(By.xpath("//label[contains(text(),'Alternative printer')] | //label[contains(text(),'Imprimante alternative')]")).isDisplayed()) {

			createTest.log(Status.PASS,"Mix set up has both default Printer & alternative Priner ");
		}else {
			createTest.log(Status.FAIL,"Mix set up does not have default Printer & alternative Printer ");
		}

		if(driver.findElement(By.xpath("//label[contains(text(),'ChartMaxx printer')] | //label[contains(text(),'Imprimante ChartMaxx')]")).isDisplayed()) {
			createTest.log(Status.PASS,"Mix set up has ChartMaxxPrinter ");
		}else {
			createTest.log(Status.FAIL,"Mix set up does not have ChartMaxxPrinter ");
		}
	}
	//@Test(groups = {"Triage"},priority=275,enabled=true)
	public void checkingChartMax() throws InterruptedException {
		
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[6]"))).click();

		//		if(!driver.findElement(By.xpath("//label[contains(text(),'Default printer configured')] | //label[contains(text(),'Imprimante par défaut configurée')]")).isDisplayed() && 
		//				driver.findElement(By.xpath("//label[contains(text(),'Alternative printer')] | //label[contains(text(),'Imprimante alternative')]")).isDisplayed()) {
		//
		//			createTest.log(Status.PASS,"Paper set up does not have default Printer & alternative Priner ");
		//		}else {
		//			createTest.log(Status.FAIL,"Paper set up have both default Printer & alternative Printer ");
		//		}

		if(driver.findElement(By.xpath("//label[contains(text(),'ChartMaxx printer')] | //label[contains(text(),'Imprimante ChartMaxx')]")).isDisplayed()) {
			createTest.log(Status.PASS,"Paper set up has ChartMaxxPrinter ");
		}else {
			createTest.log(Status.FAIL,"Paper set up does not have ChartMaxxPrinter");
		}
		
		sleeps();
		clickSaveButton();
	}
	@Test(groups = {"IntakeOutput"},priority=276,enabled=true)
	public void navigateToIntakeOutput() throws InterruptedException {

		String pageNameEnglish= "Intake/Output";
		String pageNameFrench = "Ingesta/Excréta";


		createTest=reports.createTest("Validating the functionaltiy of the Intake/Output screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Intake/Output'] | //div[contains(text(),'Ingesta/Excr')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"IntakeOutput"},priority=277,enabled=true)
	public void createingNewRecordIntakeOutput() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}
	@Test(groups = {"COVID-19"},priority=278,enabled=true)
	public void navigateToRapDepistage5() throws InterruptedException {

		String pageNameEnglish= "Isar";
		String pageNameFrench = "Isar";


		createTest=reports.createTest("Validating the functionaltiy of the COVID-19 screen");
		sleeps();
		navigateToManagementMenu();
		navigatePrintManagement();
		navigateToScreeningToolPrint();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(" //div[contains(text(),' COVID-19  2021-10-15')]"))).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));


		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}

	@Test(groups = {"COVID-19"},priority=279,enabled=true)
	public void createingNewRecordRapDepistage5() throws InterruptedException, AWTException {


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();

		WebElement unitDropDown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));

		sleeps();

		WebElement unit = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[4]")));

		if(driver.getPageSource().contains(unit.getText())) {

			createTest.log(Status.PASS,"The Unit dropdown have all 4 units" + unitDropDown.getText());
		}else {
			createTest.log(Status.FAIL,"The Unit dropdown is not having all 4 units");
		}

		unit.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[2]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[3]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-radio-container'])[1]"))).click();

		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		//
		//		WebElement printerDropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ng-trigger ng-trigger-transformPanel mat-select-panel mat-primary')])")));
		//
		//		WebElement printer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[2]")));
		//
		//		if(driver.getPageSource().contains(printer.getText())) {
		//
		//			createTest.log(Status.PASS,"The available printers are : " + printerDropdown.getText());
		//		}
		//		printer.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[5]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[@class='mat-option-text'])[5]"))).click();

		WebElement saveBtn = driver.findElement(By.xpath("(//input[contains(@class,'btn btn-primary')])"));
		saveBtn.click();

		sleeps();
		sleeps();

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Print function is working");

		}else{
			createTest.log(Status.FAIL,"Print function is not working");
		}
	}

	//---------------------------------------------------------OWord Document bookmarks---------------------------------------------


	String screenName_OWord_FR = "Gestion des signets OWord";
	String screenName_OWord_EN = "OWord Document bookmarks";
	String OWordCode = "OWord_Test";

	public void navigateToOWordDocumentBookmarks() throws InterruptedException
	{
		navigateToManagementMenu();
		driver.findElement(By.xpath("//div[contains(text(),'Document OWORD Signets')] |  //div[contains(text(),'OWord Document bookmarks')]")).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();
		sleeps();
		if(driver.getPageSource().contains(screenName_OWord_FR) | driver.getPageSource().contains(screenName_OWord_EN))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ screenName_OWord_EN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+screenName_OWord_EN);

		}

		sleeps();

	}

	@Test(groups = {"OWord Document bookmarks"},priority=280)
	public void OWordDocumentBookmarks() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Screen " + screenName_OWord_EN);	
		navigateToOWordDocumentBookmarks();
		verifyBreadCrumb(screenName_OWord_EN, screenName_OWord_FR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(OWordCode,1);
		if(driver.getPageSource().contains(OWordCode))
		{
			driver.findElement(By.xpath("//span[text()='"+OWordCode+"']")).click();

			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
		}

		createOWordBookmarkManagement();
		filter(OWordCode,1);
		if(driver.getPageSource().contains(OWordCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+screenName_OWord_EN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+screenName_OWord_EN);
		}

		//Edit-------------------------------------------------------------------------------------

		driver.findElement(By.xpath("//span[text()='"+OWordCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		clickSaveButton();
		sleeps();
		sleeps();

		filter("9999",1);
		if(driver.getPageSource().contains("9999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+screenName_OWord_EN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+screenName_OWord_EN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease1Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		clickRestoreButton();
		sleeps();
		sleeps();
		filter("1000",1);
		if(driver.getPageSource().contains("1000"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+screenName_OWord_EN);
		}
		else
		{
			filter("9999",1);
			if(driver.getPageSource().contains("9999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+screenName_OWord_EN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+screenName_OWord_EN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(OWordCode);
		clickSaveButton();

		//create duplicate-----------------
		createOWordBookmarkManagement();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+screenName_OWord_EN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+screenName_OWord_EN);
		}

	}

	void createOWordBookmarkManagement() throws InterruptedException, AWTException
	{
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(OWordCode);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("customBookmarks");
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'Âge seul')] | //mat-option//span//div[contains(text(),'AGESEUL')]")).click();
		pressAndReleaseTabKey();
		sleeps();

		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseSpaceKey();

		clickSaveButton();

	}

	@Test(groups = {"OWord Document bookmarks"},priority=281 )
	void filterRecords_OWordBookmarkManagement() throws InterruptedException, AWTException
	{

		
		filter(OWordCode,1);
		driver.findElement(By.xpath("//span[text()='"+OWordCode+"']")).click();
		clickDeleteButton();
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
			sleeps();
			navigateToOWordDocumentBookmarks();
			
			filter(OWordCode,1);
			if(!driver.getPageSource().contains(OWordCode))
			{

				createTest.log(Status.PASS,"Deletion is working properly in "+screenName_OWord_EN);
			}
			else
			{
				createTest.log(Status.FAIL,"Deletion is not working properly in "+screenName_OWord_EN);
			}

		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}
	}

	//*******************************************************OACIS Additional Information****************************************************

	String OACISAdditionInfo_screenNameFR= "Gestion des tables locales - Information additionnelle - OACIS";
	String OACISAdditionInfo_screenNameEN="Management of local tables - OACIS - Additional information";
	String OACISAdditionInfo_pageNameFR= "Information additionnelle - OACIS";
	String OACISAdditionInfo_pageNameEN="OACIS - Additional information";

	String OACISAdditionalInfoCode = RandomStringUtils.randomAlphanumeric(5);

	void navigateToOACISAdditionalInformation() throws InterruptedException
	{

		navigateToLocalTablesMenu();
		WebElement OACISAdditionalInfo = driver.findElement(By.xpath("//div[contains(text(),'Information additionnelle - OACIS')] | //div[contains(text(),'OACIS - Additional information')]"));
		OACISAdditionalInfo.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(OACISAdditionInfo_screenNameFR)) | (driver.getPageSource().contains(OACISAdditionInfo_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ OACISAdditionInfo_screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+OACISAdditionInfo_screenNameEN);

		}

		sleeps();
	}

	void createOACISAdditionalInformation() throws InterruptedException, AWTException
	{

		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(OACISAdditionalInfoCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		clickSaveButton();

	}
	@Test(groups = {"OACIS Additional Information"},priority=282)
	void OACISAdditionalInformation_Validation() throws InterruptedException, AWTException
	{

		createTest=reports.createTest("Verifying Screen " + OACISAdditionInfo_screenNameEN);	

		navigateToOACISAdditionalInformation();

		verifyBreadCrumb(OACISAdditionInfo_pageNameEN, OACISAdditionInfo_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(OACISAdditionalInfoCode,1);
		if(driver.getPageSource().contains(OACISAdditionalInfoCode))
		{
			driver.findElement(By.xpath("//span[text()='"+OACISAdditionalInfoCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

		}

		filter("9999",1);
		if(driver.getPageSource().contains(OACISAdditionalInfoCode))
		{
			driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

		}

		createOACISAdditionalInformation();
		filter(OACISAdditionalInfoCode,1);

		if(driver.getPageSource().contains(OACISAdditionalInfoCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+OACISAdditionInfo_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+OACISAdditionInfo_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+OACISAdditionalInfoCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();
		sleeps();


		clickSaveButton();
		sleeps();

		sleeps();

		filter("9999",1);
		if(driver.getPageSource().contains("9999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+OACISAdditionInfo_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+OACISAdditionInfo_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease1Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		pressAndRelease0Key();
		clickRestoreButton();
		sleeps();

		filter("1000",1);
		if(driver.getPageSource().contains("1000"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+OACISAdditionInfo_screenNameEN);
		}
		else
		{
			filter("9999",1);
			if(driver.getPageSource().contains("9999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+OACISAdditionInfo_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+OACISAdditionInfo_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"9999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(OACISAdditionalInfoCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createOACISAdditionalInformation();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+OACISAdditionInfo_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+OACISAdditionInfo_screenNameEN);
		}

	}

	@Test(groups = {"OACIS Additional Information"},priority=283 )
	void filterRecords_OACISAdditionalInformation() throws InterruptedException, AWTException
	{
		filter(OACISAdditionalInfoCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(OACISAdditionalInfoCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(OACISAdditionalInfoCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(OACISAdditionalInfoCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(OACISAdditionalInfoCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(OACISAdditionalInfoCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(OACISAdditionalInfoCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(OACISAdditionalInfoCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(OACISAdditionalInfoCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();
							driver.navigate().refresh();
							sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(5000);
		navigateToOACISAdditionalInformation();
		filter (OACISAdditionalInfoCode,1);
		if(!driver.getPageSource().contains(OACISAdditionalInfoCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}

	}   


	//*******************************************************ConsultationCancel Reasons****************************************************

	String ConsultationCancelReason_screenNameFR= "Gestion des tables locales - Consultation-Raison d'annulation";
	String ConsultationCancelReason_screenNameEN="Management of local tables - Consultation-Cancel reasons";
	String ConsultationCancelReason_pageNameFR= "Consultation-Raison d'annulation";
	String ConsultationCancelReason_pageNameEN="Consultation-Cancel reasons";

	String ConsultationCancelReasonCode = "10a";

	void navigateToConsultationCancelReason() throws InterruptedException
	{

		navigateToLocalTablesMenu();
		WebElement consulationCancelReason_Link = driver.findElement(By.xpath("//div[contains(text(),'Consultation-Raison d')] | //div[contains(text(),'Consultation-Cancel reasons')]"));
		consulationCancelReason_Link.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(ConsultationCancelReason_screenNameFR)) | (driver.getPageSource().contains(ConsultationCancelReason_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ ConsultationCancelReason_screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+ConsultationCancelReason_screenNameEN);

		}

	}

	void createConsultationCancelReason() throws InterruptedException, AWTException
	{

		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(ConsultationCancelReasonCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		clickSaveButton();

	}

	@Test(groups = {"Consultation-Cancel Reason"},priority=284)
	void ConsultationCancelReason_Validation() throws InterruptedException, AWTException
	{


		createTest=reports.createTest("Verifying Screen " + ConsultationCancelReason_screenNameEN);	

		navigateToConsultationCancelReason();

		verifyBreadCrumb(ConsultationCancelReason_pageNameEN, ConsultationCancelReason_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		deleteIfExit(ConsultationCancelReasonCode);
		deleteIfExit("999");
		
		createConsultationCancelReason();
		filter(ConsultationCancelReasonCode,1);

		if(driver.getPageSource().contains(ConsultationCancelReasonCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+ConsultationCancelReason_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+ConsultationCancelReasonCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();
		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+ConsultationCancelReason_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+ConsultationCancelReason_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+ConsultationCancelReason_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(ConsultationCancelReasonCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createConsultationCancelReason();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+ConsultationCancelReason_screenNameEN);
		}

	}

	@Test(groups = {"Consultation-Cancel Reason"},priority=285 )
	void filterRecords_ConsultationCancelReason() throws InterruptedException, AWTException
	{

		filter(ConsultationCancelReasonCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(ConsultationCancelReasonCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(ConsultationCancelReasonCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(ConsultationCancelReasonCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(ConsultationCancelReasonCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(ConsultationCancelReasonCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(ConsultationCancelReasonCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(ConsultationCancelReasonCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(ConsultationCancelReasonCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();
							
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);
		navigateToConsultationCancelReason();
		filter (ConsultationCancelReasonCode,1);
		if(!driver.getPageSource().contains(ConsultationCancelReasonCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}
	}  

	//*******************************************************  Domain configuration (Report stretcher zone situation by specialities)****************************************************

	String DomainConfiguration_screenNameFR= "Gestion des tables locales - Configuration des domaines (rapport situation par aire de civière)";
	String DomainConfiguration_screenNameEN="Management of local tables - Domain configuration (Report stretcher zone situation by specialities)";
	String DomainConfiguration_pageNameFR= "Configuration des domaines (rapport situation par aire de civière)";
	String DomainConfiguration_pageNameEN="Domain configuration (Report stretcher zone situation by specialities)";

	String DomainConfigurationCode = "10a";
	String DomainConfigurationOrderNo = "84";

	void navigateToDomainConfiguration() throws InterruptedException
	{

		navigateToLocalTablesMenu();
		WebElement consulationCancelReason_Link = driver.findElement(By.xpath("//div[contains(text(),'Configuration des domaines (rapport situation par aire de civière)')] | //div[contains(text(),'Domain configuration (Report stretcher zone situation by specialities)')]"));
		consulationCancelReason_Link.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(DomainConfiguration_screenNameFR)) | (driver.getPageSource().contains(DomainConfiguration_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ DomainConfiguration_screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+DomainConfiguration_screenNameEN);

		}

		sleeps();
	}

	void createDomainConfiguration() throws InterruptedException, AWTException
	{

		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(DomainConfigurationCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(DomainConfigurationOrderNo);

		clickSaveButton();

	}
	@Test(groups = {"Domain configuration"},priority=286)
	void DomainConfiguration_Validation() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Screen " + DomainConfiguration_screenNameEN);	
		navigateToDomainConfiguration();
		//zoomOutScreen();
		verifyBreadCrumb(DomainConfiguration_pageNameEN, DomainConfiguration_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(DomainConfigurationCode,1);
		if(driver.getPageSource().contains(DomainConfigurationCode))
		{
			driver.findElement(By.xpath("//span[text()='"+DomainConfigurationCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

		}

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{
			driver.findElement(By.xpath("//span[text()='999']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

		}

		createDomainConfiguration();
		filter(DomainConfigurationCode,1);

		if(driver.getPageSource().contains(DomainConfigurationCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+DomainConfiguration_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+DomainConfiguration_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+DomainConfigurationCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();


		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+DomainConfiguration_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+DomainConfiguration_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+DomainConfiguration_screenNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+DomainConfiguration_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+DomainConfiguration_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(DomainConfigurationCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createDomainConfiguration();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+DomainConfiguration_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+DomainConfiguration_screenNameEN);
		}

	}

	@Test(groups = {"Domain configuration"},priority=287 )
	void serviceCliniqueAndConsultationWindow_Verification() throws InterruptedException, AWTException
	{

		createTest=reports.createTest("Verifying Service CLinique and Consultation Windows");	
		//		zoomOutScreen();
		filter(DomainConfigurationCode,1);
		driver.findElement(By.xpath("//span[text()='"+DomainConfigurationCode+"']")).click();

		//Service Clinique Verification
		driver.findElement(By.xpath("//button[text()='Service clinique'] | //button[text()='Clinical service']")).click();
		if((driver.getPageSource().contains("Liste des services cliniques"))|(driver.getPageSource().contains("Clinical service list ")) )
		{	
			createTest.log(Status.PASS,"Service Clinique List is shown when clicked on Service Clinique button in "+DomainConfiguration_screenNameEN);
			sleeps();
			driver.findElement(By.xpath("(//div//span[contains(text(),'4e-Chirurgie générale')]//parent::div//parent::div)[1]//following-sibling::div")).click();
			sleeps();
			pressAndReleaseSpaceKey();
			sleeps();

			driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
			sleeps();

			driver.findElement(By.xpath("//button[text()='Service clinique'] | //button[text()='Clinical service']")).click();
			try
			{
				sleeps();
				driver.findElement(By.xpath("(//mat-dialog-container//input[@type='checkbox' and @checked])[1]"));
				createTest.log(Status.PASS,"Consultation is successfully selected in "+DomainConfiguration_screenNameEN);
				driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
			}
			catch (Exception e) {
				// TODO: handle exception
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Service Clinique List is not shown when clicked on Service Clinique button in "+DomainConfiguration_screenNameEN);
		}

		//Consultation Verification
		sleeps();
		driver.findElement(By.xpath("//button[text()='Consultation']")).click();
		sleeps();
		if((driver.getPageSource().contains("Liste des domaines de consultations "))|(driver.getPageSource().contains("Consultation domain list ")) )
		{	
			createTest.log(Status.PASS,"Consultation List is shown when clicked on Service Clinique button in "+DomainConfiguration_screenNameEN);
			sleeps();
			driver.findElement(By.xpath("(//div//span[contains(text(),'Gastro-entérologie')]//parent::div//parent::div)[1]//following-sibling::div | (//div//span[contains(text(),'Gastroenterology')]//parent::div//parent::div)[1]//following-sibling::div")).click();
			sleeps();
			pressAndReleaseSpaceKey();
			sleeps();
			driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
			sleeps();
			sleeps();
			driver.findElement(By.xpath("//button[text()='Consultation']")).click();

			try
			{
				sleeps();
				driver.findElement(By.xpath("(//mat-dialog-container//input[@type='checkbox' and @checked])[1]"));
				createTest.log(Status.PASS,"Consultation is successfully selected in "+DomainConfiguration_screenNameEN);
				driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Consultation List is not shown when clicked on Service Clinique button in "+DomainConfiguration_screenNameEN);
		}

		driver.findElement(By.xpath("//span[text()='"+DomainConfigurationCode+"']")).click();
		clickDeleteButton();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
			sleeps();

		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}


		Thread.sleep(5000);
		navigateToDomainConfiguration();
		filter (DomainConfigurationCode,1);
		if(!driver.getPageSource().contains(DomainConfigurationCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}
	}

	//*******************************************************Absence Reasons****************************************************


	String absenceReason_pageNameFR= "Raisons d'absence - Réévaluation";
	String absenceReason_pageNameEN="Absence reasons - Reevaluation";

	String absenceReasonCode = "1054";

	void navigateToAbsenceReason() throws InterruptedException
	{

		navigateToLocalTablesMenu();
		WebElement consulationCancelReason_Link = driver.findElement(By.xpath("//div[contains(text(),'absence - Réévaluation')] | //div[contains(text(),'Absence reasons - Reevaluation')]"));
		consulationCancelReason_Link.click();

		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(absenceReason_pageNameFR)) | (driver.getPageSource().contains(absenceReason_pageNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ absenceReason_pageNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+absenceReason_pageNameEN);

		}

		sleeps();
	}

	void createAbsenceReason() throws InterruptedException, AWTException
	{

		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(absenceReasonCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		clickSaveButton();

	}

	@Test(groups = {"Absence reasons - Reevaluation"},priority=288)
	void absenceReason_Validation() throws InterruptedException, AWTException
	{

		createTest=reports.createTest("Verifying Screen " + absenceReason_pageNameEN);	

		navigateToAbsenceReason();

		verifyBreadCrumb(absenceReason_pageNameEN, absenceReason_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(absenceReasonCode,1);
		if(driver.getPageSource().contains(absenceReasonCode))
		{
			driver.findElement(By.xpath("//span[text()='"+absenceReasonCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
		
		}



		createAbsenceReason();
		filter(absenceReasonCode,1);

		if(driver.getPageSource().contains(absenceReasonCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+absenceReason_pageNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+absenceReason_pageNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+absenceReasonCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();

		clickSaveButton();
		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+absenceReason_pageNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edit new row in "+absenceReason_pageNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"0999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		pressAndRelease8Key();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("811",1);
		if(driver.getPageSource().contains("0811"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+absenceReason_pageNameEN);
		}
		else
		{
			filter("0999",1);
			if(driver.getPageSource().contains("0999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+absenceReason_pageNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+absenceReason_pageNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"0999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(absenceReasonCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createAbsenceReason();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+absenceReason_pageNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+absenceReason_pageNameEN);
		}

	}

	@Test(groups = {"Absence reasons - Reevaluation"},priority=289 )
	void filterRecords_absenceReason() throws InterruptedException, AWTException
	{

		filter(absenceReasonCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(absenceReasonCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(absenceReasonCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(absenceReasonCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(absenceReasonCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(absenceReasonCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(absenceReasonCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(absenceReasonCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(absenceReasonCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();
							sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(5000);
		navigateToAbsenceReason();
		filter (absenceReasonCode,1);
		if(!driver.getPageSource().contains(absenceReasonCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}
	}  


	//*******************************************************Route Of Administration****************************************************

	String routeOfAdministration_screenNameFR= "Gestion des tables locales - Voie d'administration";
	String routeOfAdministration_screenNameEN="Management of local tables - Route of administration";
	String routeOfAdministration_pageNameFR= "Voie d'administration";
	String routeOfAdministration_pageNameEN="Route of administration";

	String routeOfAdministrationCode = "10a";

	void navigateToRouteOfAdministration() throws InterruptedException
	{

		navigateToMedicationTablesAndKardexMenu();

		WebElement routeOfAdministration_Link = driver.findElement(By.xpath("//div[contains(text(),'administration')] | //div[contains(text(),'Route of administration')]"));
		routeOfAdministration_Link.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(routeOfAdministration_screenNameFR)) | (driver.getPageSource().contains(routeOfAdministration_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ routeOfAdministration_screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+routeOfAdministration_screenNameEN);

		}

		sleeps();
	}

	void createRouteOfAdministration() throws InterruptedException, AWTException
	{

		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(routeOfAdministrationCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");

		clickSaveButton();

	}

	@Test(groups = {"Route of administration"},priority=290)
	void routeOfAdministration_Validation() throws InterruptedException, AWTException
	{


		createTest=reports.createTest("Verifying Screen " + routeOfAdministration_screenNameEN);	
		//zoomOutScreen();
		navigateToRouteOfAdministration();

		verifyBreadCrumb(routeOfAdministration_pageNameEN, routeOfAdministration_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(routeOfAdministrationCode,1);
		if(driver.getPageSource().contains(routeOfAdministrationCode))
		{
			driver.findElement(By.xpath("//span[text()='"+routeOfAdministrationCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
			
		}



		createRouteOfAdministration();
		filter(routeOfAdministrationCode,1);

		if(driver.getPageSource().contains(routeOfAdministrationCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+routeOfAdministration_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+routeOfAdministration_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+routeOfAdministrationCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+routeOfAdministration_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+routeOfAdministration_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+routeOfAdministration_screenNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+routeOfAdministration_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+routeOfAdministration_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(routeOfAdministrationCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createRouteOfAdministration();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+routeOfAdministration_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+routeOfAdministration_screenNameEN);
		}

	}

	@Test(groups = {"Route of administration"},priority=291 )
	void filterRecords_routeOfAdministration() throws InterruptedException, AWTException
	{

		filter(routeOfAdministrationCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(routeOfAdministrationCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(routeOfAdministrationCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(routeOfAdministrationCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(routeOfAdministrationCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(routeOfAdministrationCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(routeOfAdministrationCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(routeOfAdministrationCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(routeOfAdministrationCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();
							sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(5000);
		navigateToRouteOfAdministration();
		filter (routeOfAdministrationCode,1);
		if(!driver.getPageSource().contains(routeOfAdministrationCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}
	}  


	//*******************************************************Schedule****************************************************

	String Schedule_screenNameFR= "Gestion des tables locales - Horaire";
	String Schedule_screenNameEN="Management of local tables - Schedule";
	String Schedule_pageNameFR= "Horaire";
	String Schedule_pageNameEN="Schedule";

	String ScheduleCode = "15";

	void navigateToSchedule() throws InterruptedException
	{

		navigateToMedicationTablesAndKardexMenu();
		WebElement schedule_Link = driver.findElement(By.xpath("//div[contains(text(),' Horaire')] | //div[contains(text(),' Schedule')]"));
		schedule_Link.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(Schedule_screenNameFR)) | (driver.getPageSource().contains(Schedule_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ Schedule_screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+Schedule_screenNameEN);

		}

		sleeps();
	}

	void createSchedule() throws InterruptedException, AWTException
	{

		clickCreateButton();
		
		pressAndReleaseEnterKey();
		
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'X1')]")).click();
		
		pressAndReleaseTabKey();
		
		pressAndReleaseTabKey();
		
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(ScheduleCode);
		
		pressAndReleaseTabKey();
		
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		
		pressAndReleaseTabKey();
		
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");

		clickSaveButton();

	}

	@Test(groups = {"Schedule"},priority=292)
	void Schedule_Validation() throws InterruptedException, AWTException
	{


		createTest=reports.createTest("Verifying Screen " + Schedule_screenNameEN);	

		navigateToSchedule();

		verifyBreadCrumb(Schedule_pageNameEN, Schedule_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(ScheduleCode,1);filter("descFR",2);
		if(driver.getPageSource().contains("descFR"))
		{
			driver.findElement(By.xpath("//span[text()='"+ScheduleCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
			
		}



		createSchedule();
		filter(ScheduleCode,1);filter("descFR",2);

		if(driver.getPageSource().contains("descFR"))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+Schedule_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+Schedule_screenNameEN);
		}


		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+ScheduleCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+Schedule_screenNameEN);
		}
		else
		{
			filter(ScheduleCode,1);filter("descFR",2);
			if(driver.getPageSource().contains("descFR"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+Schedule_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+Schedule_screenNameEN);

			}
		}



		//create duplicate-----------------
		createSchedule();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+Schedule_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+Schedule_screenNameEN);
		}

	}

	@Test(groups = {"Schedule"},priority=293 )
	void filterRecords_Schedule() throws InterruptedException, AWTException
	{
		filter(ScheduleCode,1);filter("descFR",2);
		clickAllFilter();
		if(driver.getPageSource().contains("descFR"))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains("descFR"))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains("descFR"))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains("descFR"))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}


		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains("descFR"))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains("descFR"))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains("descFR"))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains("descFR"))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");
						createTest.log(Status.PASS,"Created record is successfully edited");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);
		navigateToSchedule();
		filter (ScheduleCode,1);
		if(!driver.getPageSource().contains("descFR"))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
	}  


	//-----------------------------------------------------Default route and frequency (according to form)-----------------------------
	String defaultRouteAndFrequency_pageNameEnglish= "Default route and frequency (according to form)";
	String defaultRouteAndFrequency_pageNameFrench = "Voie & Fréquence par défaut (selon la forme)";
	String defaultRouteAndFrequency_screenNameFR= "Gestion des tables locales - Voie & Fréquence par défaut (selon la forme)";
	String defaultRouteAndFrequency_screenNameEN="Management of local tables - Default route and frequency (according to form)";

	String defaultRouteAndFrequencyCode = "137";

	void navigateToDefaultRouteAndFrequency() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement defaultRouteAndFrequency_Link = driver.findElement(By.xpath("//div[contains(text(),' Voie & Fréquence par défaut (selon la forme)')] | //div[text()=' Default route and frequency (according to form)']"));
		defaultRouteAndFrequency_Link.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();
		
	}

	@Test(groups = {"Default route and frequency"},priority=294)
	void deafaultRouteAndFrequencyValidation() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Screen " + defaultRouteAndFrequency_pageNameEnglish);	

		navigateToDefaultRouteAndFrequency();
		sleeps();

		verifyBreadCrumb(defaultRouteAndFrequency_pageNameEnglish, defaultRouteAndFrequency_pageNameFrench);
		createTest.log(Status.PASS,"Successfully Navigated to screen "+ defaultRouteAndFrequency_screenNameEN);

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are disabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not disabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not disabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(defaultRouteAndFrequencyCode,1);
		driver.findElement(By.xpath("//span[text()='"+defaultRouteAndFrequencyCode+"']")).click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height cell-wrap-text')]//span[text()='"+defaultRouteAndFrequencyCode+"']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Code column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Code column is editable");
		}

		driver.findElement(By.xpath("//span[text()='TIMBRE']")).click();

		if(driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')]//span[text()='TIMBRE']")).isDisplayed())
		{
			createTest.log(Status.PASS,"Description column is read only");

		}
		else
		{
			createTest.log(Status.FAIL,"Description column is editable");
		}


		driver.findElement(By.xpath("(//mat-select)[1]")).click();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'TOP')]")).click();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'mg/kg/min')]")).click();

		if(verifyButtonDisabled(createButton, "Create Button"))
		{

			if(verifyButtonDisabled(deleteButton, "Delete Button"))
			{
				createTest.log(Status.PASS,"Create and  Delete are disabled, even after row was edited");
			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create and  Delete are enabled, even after row was edited");

		}

		clickRestoreButton();
		clickSaveButton();

		filter(defaultRouteAndFrequencyCode,1);
		if(driver.getPageSource().contains(defaultRouteAndFrequencyCode))
		{
			createTest.log(Status.PASS,"Restore Button is enabled when existing record is edited");
			createTest.log(Status.PASS,"User is able to update the values in default route, default frequency and default unit columns");
			createTest.log(Status.PASS,"Save Button is enabled when row was edited");
			createTest.log(Status.PASS,"Row is successfully edited");
		}
		else
		{
			createTest.log(Status.FAIL,"Restore Button is not enabled when existing record is edited");
			createTest.log(Status.FAIL,"User is not able to update the values in default route, default frequency and default unit columns");
			createTest.log(Status.FAIL,"Save Button is not enabled when row was edited");
			createTest.log(Status.FAIL,"Row is not successfully edited");
		}

		clickAllFilter();
		if(driver.getPageSource().contains(defaultRouteAndFrequencyCode))
		{
			System.out.println("contains in ALl-1");

			clickActiveFilter();
			if(!driver.getPageSource().contains(defaultRouteAndFrequencyCode))
			{
				System.out.println("contains in Active-1");
				sleeps();

				clickNonActiveFilter();
				if(!driver.getPageSource().contains(defaultRouteAndFrequencyCode))
				{
					System.out.println(" not contains in nonActive-1");


					createTest.log(Status.PASS,"Active filter is working fine");
					createTest.log(Status.PASS,"Non-Active filter is working fine");
					createTest.log(Status.PASS,"All filter is working fine");

				}

			}
		}
		clickAllFilter();
		driver.findElement(By.xpath("(//mat-select)[1]")).click();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'TOP')]")).click();

		filter("128",1);
		clickReservedFilter();
		if(driver.getPageSource().contains("SOLIDE") )
		{
			createTest.log(Status.PASS,"Reserved filter is working fine");
					
		}	
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}
	}


	//*******************************************************List Of Protocols************************************************

	String listOfProtocols_pageNameFR= "Liste des protocoles";
	String listOfProtocols_pageNameEN="List of protocols";

	String listOfProtocolsCode = RandomStringUtils.randomAlphanumeric(3);

	void navigateToListOfProtocols() throws InterruptedException
	{

		navigateToMedicationTablesAndKardexMenu();
		WebElement listOfProtocols_Link = driver.findElement(By.xpath("//div[contains(text(),'Liste des protocoles')] | //div[contains(text(),' List of protocols')]"));
		listOfProtocols_Link.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();
		sleeps();
		sleeps();
		sleeps();
		if((driver.getPageSource().contains(listOfProtocols_pageNameFR)) | (driver.getPageSource().contains(listOfProtocols_pageNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ listOfProtocols_pageNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+listOfProtocols_pageNameEN);

		}

		sleeps();

	}

	void createListOfProtocols() throws InterruptedException, AWTException
	{

		driver.findElement(By.xpath("//div[text()='Liste des protocoles'] | //div[text()='List of protocols']")).click();
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfProtocolsCode);
		clickSaveButton();
		sleeps();

	}

	void verifyDocumentationButton() throws InterruptedException
	{
		//Documentation button check
		driver.findElement(By.xpath("//button[text()='Documentation']")).click();
		sleeps();

		if(driver.getPageSource().contains("Protocol - Documentation") | driver.getPageSource().contains("Protocole - Documentation"))
		{
			createTest.log(Status.PASS,"Documentation window is succesfully dispalyed when clicked on Documentation button in "+listOfProtocols_pageNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Documentation window is not dispalyed when clicked on Documentation button in "+listOfProtocols_pageNameEN);
		}

		sleeps();
		sleeps();
		WebElement chooseFile = driver.findElement(By.id("fileUploader"));
		chooseFile.sendKeys(fileUploadPath);
		Thread.sleep(10000);
		driver.findElement(By.xpath("//button[text()='Fermer'] | //button[text()='Close']")).click();
		Thread.sleep(6000);
		createTest.log(Status.PASS,"Document is successfully added for protocol in "+listOfProtocols_pageNameEN);

	}
	
	@Test(groups = {"List of protocols"},priority=295)
	void listOfProtocols_Validation() throws InterruptedException, AWTException
	{


		createTest=reports.createTest("Verifying Screen " + listOfProtocols_pageNameEN);	

		navigateToListOfProtocols();

		verifyBreadCrumb(listOfProtocols_pageNameEN, listOfProtocols_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(listOfProtocolsCode,1);
		if(driver.getPageSource().contains(listOfProtocolsCode))
		{
			driver.findElement(By.xpath("//span[text()='"+listOfProtocolsCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

			sleeps();
			sleeps();
			sleeps();
			sleeps();
		}

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{
			driver.findElement(By.xpath("//span[text()='999']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();

			sleeps();
			sleeps();
			sleeps();
			sleeps();
		}


		createListOfProtocols();
		filter(listOfProtocolsCode,1);
		if(driver.getPageSource().contains(listOfProtocolsCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+listOfProtocols_pageNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+listOfProtocols_pageNameEN);
		}

		verifyDocumentationButton();
		
		//Edit-----------------
		filter(listOfProtocolsCode,1);
		sleeps();
		driver.findElement(By.xpath("//span[text()='"+listOfProtocolsCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();


		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+listOfProtocols_pageNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+listOfProtocols_pageNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+listOfProtocols_pageNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+listOfProtocols_pageNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+listOfProtocols_pageNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfProtocolsCode);
		sleeps();
		clickSaveButton();
		sleeps();
		verifyClearFilter();

		//create duplicate-----------------
		createListOfProtocols();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+listOfProtocols_pageNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+listOfProtocols_pageNameEN);
		}

		filter(listOfProtocolsCode,1);
		driver.findElement(By.xpath("//span[text()='"+listOfProtocolsCode+"']")).click();

		//Unit button verification
		verifyUnitButton();

	}

	@Test(groups = {"List of protocols"}, priority=296)
	void listOfProtocols_ExamLabs() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Screen Exams Labs in " + frequency_screenNameEN);	
		
		driver.findElement(By.xpath("//div[text()='Examens/Labos/Soins'] | //div[text()='Exams/Labs/Care']")).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?")) | (driver.getPageSource().contains("Do you want to save the data?")) )

		{

			clickYesButton();
			sleeps();
			createTest.log(Status.PASS,"COnfirmation for saving data is shown when user tried switching to Exams/Labs section");
		}
		clickCreateButton();
		sleeps();
		driver.findElement(By.xpath("(//span[text()=' EPS1']//parent::span//parent::div//parent::div//following-sibling::div)[1]")).click();
		sleeps();
		pressAndReleaseSpaceKey();
		sleeps();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
		sleeps();

		if((driver.getPageSource().contains("EPS1")) )
		{
			createTest.log(Status.PASS,"Exam/Labs/Soins is successfully added");
		}

		driver.findElement(By.xpath("//span[text()=' EPS1']")).click();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'DIE')]")).click();
		clickSaveButton();
		sleeps();
	if((driver.getPageSource().contains("Erreur d")) | (driver.getPageSource().contains("Data Error")) )
	{	
		createTest.log(Status.PASS,"Error is shown when schedule was not selected for the Exams/Labs/Soins record");
		clickOKButton();
	}
	driver.findElement(By.xpath("//span[text()=' EPS1']")).click();
	pressAndReleaseTabKey();
	sleeps();
	pressAndReleaseTabKey();
	sleeps();
	pressAndReleaseTabKey();
	sleeps();
	pressAndReleaseEnterKey();
	sleeps();
	driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'01')]")).click();
	clickSaveButton();

	//delete
	driver.findElement(By.xpath("//span[text()=' EPS1']")).click();
	clickDeleteButton();
	clickSaveButton();
	filter("EPS1",1);
	if((!driver.getPageSource().contains("EPS1")) )
	{	
		createTest.log(Status.PASS,"EXAM LAM record is successfully deleted");

	}
	

	}

	@Test(groups = {"List of protocols"}, priority=297)
	void listOfProtocols_Medicaments() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Screen Medicaments in " + frequency_screenNameEN);	

		driver.findElement(By.xpath("//div[text()='Médicaments'] | //div[text()='Medications']")).click();
		Thread.sleep(20000);
		
		driver.findElement(By.xpath("(//span[contains(text(),'10 MG IV')])[1]")).click();
		driver.findElement(By.xpath("(//mat-selection-list)[1]//mat-list-option[1]")).click();
		moveToElementAndClick("//mat-list-option//div[contains(text(),'PRN')]");
		driver.findElement(By.xpath("(//mat-form-field)[1]//input")).click();
		pressAndRelease1Key();
		driver.findElement(By.xpath("//mat-select")).click();
		driver.findElement(By.xpath("//mat-option//span[text()='mg']")).click();
		driver.findElement(By.xpath("//button[contains(text(),'Add')] | //button[contains(text(),'Ajouter')]")).click();
		clickSaveButton();
		Thread.sleep(16000);
		
		driver.findElement(By.xpath("(//span[contains(text(),'10 MG IV')])[3]")).click();
		createTest.log(Status.PASS,"Medicaments is succesfully added");
		
		pressAndReleaseDeleteKey();
		pressAndRelease5Key();
		clickRestoreButton();
		Thread.sleep(16000);
		driver.findElement(By.xpath("(//span[contains(text(),'10 MG IV')])[3]")).click();
		createTest.log(Status.PASS,"Medicaments is succesfully edited");
		createTest.log(Status.PASS,"Restore Button is working fine in medicaments");
		
		clickDeleteButton();
		clickSaveButton();
		Thread.sleep(15000);
		createTest.log(Status.PASS,"Medicaments is successfully deleted");
//		try 
//		{
//			driver.findElement(By.xpath("(//span[contains(text(),'10 MG IV')])[3]"));
//			
//		} catch (Exception e) 
//		{
//			createTest.log(Status.PASS,"Medicaments is successfully deleted");
//		}

	}


	@Test(groups = {"List of protocols"}, priority=298)
	void listOfProtocols_ProtocolsInclus() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Screen Protocols Included in " + frequency_screenNameEN);	

		driver.findElement(By.xpath("//div[text()='Protocoles inclus'] | //div[text()='Protocols included']")).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?")) | (driver.getPageSource().contains("Do you want to save the data?")) )

		{
			clickYesButton();
			sleeps();
			createTest.log(Status.PASS,"Confirmation for saving data is shown when user tried switching to Exams/Labs section");
		}
		clickCreateButton();
		pressAndReleaseEnterKey();
		sleeps();
		driver.findElement(By.xpath("//span//div[contains(text(),'+AUT')]")).click();
		clickSaveButton();

		createTest.log(Status.PASS,"Protocol is succesfully added");


		clickDeleteButton();
		clickSaveButton();

		sleeps();

		createTest.log(Status.PASS,"Protocol is succesfully deleted");

	}
	
	public void verify_listOfProtocolDiet() throws AWTException, InterruptedException
	{
		driver.findElement(By.xpath("//div[text()='Diète'] | //div[text()='Diet']")).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?")) | (driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
			sleeps();
			createTest.log(Status.PASS,"Confirmation for saving data is shown when user tried switching to Exams/Labs section");
		}
		clickCreateButton();
		sleeps();
		if((driver.getPageSource().contains("Diète")) | (driver.getPageSource().contains("Diet")) )
		{	
			createTest.log(Status.PASS,"Diet window is opened when create button was clicked in Diet Menu");

		}
		driver.findElement(By.xpath("(//span[text()='ALL']//parent::span//parent::div//parent::div//following-sibling::div)[1]")).click();
		pressAndReleaseSpaceKey();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();

		sleeps();

		filter("ALL",2);
		if((driver.getPageSource().contains("ALL")) )
		{
			createTest.log(Status.PASS,"Diet is successfully added");
		}

		//restore check
		driver.findElement(By.xpath("//span[text()='ALL']")).click();
		pressAndRelease0Key();
		clickSaveButton();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )

		{
			createTest.log(Status.PASS,"Diet Code is not allowed to alter");
			clickOKButton();
			sleeps();
		}
		clickRestoreButton();
		filter("ALL",1);
		if((driver.getPageSource().contains("ALL")) )

		{

			createTest.log(Status.PASS,"Restore button of Diet is successfully working");
			clickSaveButton();
			sleeps();

//			clickCreateButton();
//			sleeps();
//
//			driver.findElement(By.xpath("(//span[text()='ALL']//parent::span//parent::div//parent::div//following-sibling::div)[1]")).click();
//			pressAndReleaseSpaceKey();
//			driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
//
//			sleeps();
//

			//delete check
			driver.findElement(By.xpath("//span[text()='ALL']")).click();
			clickDeleteButton();
			clickSaveButton();
			sleeps();

			if(!driver.getPageSource().contains("Allergie Sésames")) 
			{
				createTest.log(Status.PASS,"Delete button of Diet is successfully working");
			}
		}

		//delete protocol
		driver.findElement(By.xpath("//div[text()='Liste des protocoles'] | //div[text()='List of protocols']")).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?")) | (driver.getPageSource().contains("Do you want to save the data?")) )
	
		{

			clickYesButton();
			sleeps();
			createTest.log(Status.PASS,"Confirmation for saving data is shown when user tried switching to Exams/Labs section");
		}
		filter(listOfProtocolsCode,1);
		driver.findElement(By.xpath("//span[text()='"+listOfProtocolsCode+"']")).click();

		clickDeleteButton();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
			sleeps();

			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}
	}

	@Test(groups = {"List of protocols"}, priority=299)
	void listOfProtocols_Diet() throws InterruptedException, AWTException
	{

		createTest=reports.createTest("Verifying Screen Diet in " + listOfProtocols_pageNameEN);	

		verify_listOfProtocolDiet();
		
		navigateToListOfProtocols();
		filter(listOfProtocolsCode,1);
		if((!driver.getPageSource().contains(listOfProtocolsCode)) )
		{
			createTest.log(Status.PASS,"List of protocol is sueccesfully deleted");
		}
	}
	//*******************************************************Frequency***************************************************

	String frequency_screenNameFR= "Gestion des tables locales - Fréquence";
	String frequency_screenNameEN="Management of local tables - Frequency";
	String frequency_pageNameFR= "Fréquence";
	String frequency_pageNameEN="Frequency";

	String frequencyCode = "10a";

	void navigateToFrequency() throws InterruptedException
	{

		navigateToMedicationTablesAndKardexMenu();
		WebElement frequency_Link = driver.findElement(By.xpath("(//div[contains(text(),' Fréquence')] | //div[contains(text(),' Frequency')])[1]"));
		frequency_Link.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(frequency_screenNameFR)) | (driver.getPageSource().contains(frequency_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ frequency_screenNameEN);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+frequency_screenNameEN);

		}

		sleeps();
	}

	void createFrequency() throws InterruptedException, AWTException
	{
		clickCreateButton();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(frequencyCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descFR");
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("descEN");
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'X1')]")).click();
		clickSaveButton();

	}

	@Test(groups = {"Frequency"},priority=300)
	void frequency_Validation() throws InterruptedException, AWTException
	{	
		createTest=reports.createTest("Verifying Screen Frequency");	

		navigateToFrequency();

		verifyBreadCrumb(frequency_pageNameEN, frequency_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		filter(frequencyCode,1);
		if(driver.getPageSource().contains(frequencyCode))
		{
			driver.findElement(By.xpath("//span[text()='"+frequencyCode+"']")).click();
			sleeps();
			clickDeleteButton();
			clickSaveButton();
			sleeps();
			sleeps();
			sleeps();
		}



		createFrequency();
		filter(frequencyCode,1);

		if(driver.getPageSource().contains(frequencyCode))
		{

			createTest.log(Status.PASS,"Successfully created new row in "+frequency_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+frequency_screenNameEN);
		}

		//Edit-----------------

		driver.findElement(By.xpath("//span[text()='"+frequencyCode+"']")).click();
		pressAndReleaseControlAKey();
		pressAndRelease9Key();
		pressAndRelease9Key();
		pressAndRelease9Key();

		sleeps();


		clickSaveButton();
		sleeps();

		filter("999",1);
		if(driver.getPageSource().contains("999"))
		{

			createTest.log(Status.PASS,"Successfully edited new row in "+frequency_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to edited new row in "+frequency_screenNameEN);
		}

		//Check Restore
		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseRKey();
		pressAndRelease1Key();
		pressAndRelease1Key();

		clickRestoreButton();
		sleeps();

		filter("R11",1);
		if(driver.getPageSource().contains("R11"))
		{

			createTest.log(Status.FAIL,"Restore Button is not working fine in "+frequency_screenNameEN);
		}
		else
		{
			filter("999",1);
			if(driver.getPageSource().contains("999"))
			{
				createTest.log(Status.PASS,"Restore Button is working fine in "+frequency_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Restore Button is not working fine in "+frequency_screenNameEN);

			}
		}

		driver.findElement(By.xpath("//span[text()='"+"999"+"']")).click();
		pressAndReleaseControlAKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(frequencyCode);
		sleeps();
		clickSaveButton();
		sleeps();

		//create duplicate-----------------
		createFrequency();
		if((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
		{	
			createTest.log(Status.PASS,"Verified duplicate is not allowed to create in "+frequency_screenNameEN);
			clickOKButton();
			clickRestoreButton();
			sleeps();
			clickSaveButton();
			sleeps();
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create in "+frequency_screenNameEN);
		}

	}


	@Test(groups = {"Frequency"},priority=301 )
	void filterRecords_Frequency() throws InterruptedException, AWTException
	{
		filter(frequencyCode,1);

		//test define button
		driver.findElement(By.xpath("//button[text()='Définir'] | //button[text()='Define']")).click();
		sleeps();
		if(driver.getPageSource().contains("Codes interface - Fréquence: "+frequencyCode) | driver.getPageSource().contains("Interface codes - Frequency: "+frequencyCode) )
		{
			createTest.log(Status.PASS,"Interface Code window is opened when Define button was clicked" );

		}
		else
		{
			createTest.log(Status.FAIL,"Interface Code window is not opened when Define button was clicked" );

		}

		driver.findElement(By.xpath("(//button[contains(text(),'Créer')] | //button[contains(text(),'Create')])[2]")).click();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("interfacetest");
		clickSaveAndContinueButton();
		clickSaveAndCloseButton();

		driver.findElement(By.xpath("//button[text()='Définir'] | //button[text()='Define']")).click();
		sleeps();
		if(driver.getPageSource().contains("interfacetest")  )
		{
			createTest.log(Status.PASS,"Interface Code is successfully created for frequency" );


		}
		driver.findElement(By.xpath("//span[text()='interfacetest']")).click();
		pressAndRelease0Key();

		driver.findElement(By.xpath("(//button[contains(text(),'Rétablir')] | //button[contains(text(),'Restore')])[2]")).click();
		clickSaveAndContinueButton();
		clickSaveAndCloseButton();
		sleeps();
		sleeps();

		driver.findElement(By.xpath("//button[text()='Définir'] | //button[text()='Define']")).click();
		sleeps();
		if(driver.getPageSource().contains("interfacetest")  )
		{
			createTest.log(Status.PASS,"Restore button of Interface Code window is working fine in "+frequency_pageNameEN  );

			driver.findElement(By.xpath("//span[text()='interfacetest']")).click();
			driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]")).click();
			clickSaveAndCloseButton();
			sleeps();
			driver.findElement(By.xpath("//span[text()='"+frequencyCode+"']")).click();
			driver.findElement(By.xpath("//button[text()='Définir'] | //button[text()='Define']")).click();
			sleeps();
			if(!driver.getPageSource().contains("interfacetest"))
			{
				createTest.log(Status.PASS,"Interface Code is succesfully deleted in "+frequency_pageNameEN);

			}
			else
			{
				createTest.log(Status.FAIL,"Interface Code is not succesfully deleted in "+frequency_pageNameEN );

			}


		}
		clickCloseIcon();
		sleeps();

		filter(frequencyCode,1);
		clickAllFilter();
		if(driver.getPageSource().contains(frequencyCode))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(frequencyCode))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(frequencyCode))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(frequencyCode))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );


					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}

		filter(frequencyCode,1);
		clickAllFilter();
		sleeps();
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(frequencyCode))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();

			if(!driver.getPageSource().contains(frequencyCode))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();

				if(driver.getPageSource().contains(frequencyCode))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(frequencyCode))
					{
						System.out.println(" not contains in reserved-2");

						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");

						clickAllFilter();

						sleeps();
						WebElement activeCheckbox2 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
						activeCheckbox2.click();
						clickDeleteButton();
						clickHome();
						if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
						{
							createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
							clickYesButton();
							sleeps();
							sleeps();
						}
						else
						{
							createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
						}
					}


				}

			}
		}

		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}

		Thread.sleep(20000);
		navigateToFrequency();
		filter (frequencyCode,1);
		if(!driver.getPageSource().contains(frequencyCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		verifyClearFilter();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();
		}
	}  

	//*******************************************************AH 280 - Additional information***************************************************

	String AH280_AdditionalInformation_pageNameFR= "Information additionnelle - AH 280";
	String AH280_AdditionalInformation_pageNameEN="AH 280 - Additional information";

	String AH280_AdditionalInformation_FR = "AdditionalAHFR";
	String AH280_AdditionalInformation_EN = "AdditionalAHEN";

	void navigateToAH280_AdditionalInformation() throws InterruptedException
	{

		navigateToLocalTablesMenu();
		WebElement ah280AdditionalInfoLink = driver.findElement(By.xpath("//div[contains(text(),'Information additionnelle - AH 280')] | //div[contains(text(),' AH 280 - Additional information')]"));
		ah280AdditionalInfoLink.click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		sleeps();

		if((driver.getPageSource().contains(AH280_AdditionalInformation_pageNameFR)) | (driver.getPageSource().contains(AH280_AdditionalInformation_pageNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ AH280_AdditionalInformation_pageNameFR);

		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+AH280_AdditionalInformation_pageNameFR);

		}

		sleeps();
		sleeps();
	}

	void createAH280_AdditionalInformation() throws InterruptedException, AWTException
	{


		driver.findElement(By.xpath("(//input)[1]")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("(//input)[1]")).sendKeys(AH280_AdditionalInformation_FR);

		driver.findElement(By.xpath("(//input)[2]")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("(//input)[2]")).sendKeys(AH280_AdditionalInformation_EN);
		clickCreateButton();
		System.out.println("pressed tab");
		sleeps();

		clickSaveButton();
		System.out.println("pressed sa ve");
		sleeps();

		try 
		{
			System.out.println("entered try");
			driver.findElement(By.xpath("//p[text()='"+AH280_AdditionalInformation_FR+"'] | //p[text()='"+AH280_AdditionalInformation_EN+"']"));
			createTest.log(Status.PASS,"Main branch is created");

		} catch (Exception e) {
			System.out.println("entered catch");
			// TODO: handle exception
		}


		//create sub
		driver.findElement(By.xpath("//p[text()='"+AH280_AdditionalInformation_FR+"'] | //p[text()='"+AH280_AdditionalInformation_EN+"']")).click();

		clickCreateButton();
		driver.findElement(By.xpath("(//input)[1]")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("(//input)[1]")).sendKeys("subFR");

		driver.findElement(By.xpath("(//input)[2]")).click();
		pressAndReleaseControlAKey();
		pressAndReleaseDeleteKey();
		driver.findElement(By.xpath("(//input)[2]")).sendKeys("subEN");


		clickSaveButton();

		pressAndReleaseTabKey();

		pressAndReleaseTabKey();

		pressAndReleaseTabKey();

		try 
		{
			driver.findElement(By.xpath("//p[text()='subFR'] | //p[text()='subEN']"));
			createTest.log(Status.PASS,"Sub branch is created");

		} catch (Exception e) {
			// TODO: handle exception
		}


	}

	@Test(groups = {"AH 280 Additional Information"},priority=302)
	void AH280_AdditionalInformation_Validation() throws InterruptedException, AWTException
	{


		createTest=reports.createTest("Verifying Screen " + AH280_AdditionalInformation_pageNameFR);	

		navigateToAH280_AdditionalInformation();

		verifyBreadCrumb(AH280_AdditionalInformation_pageNameEN, AH280_AdditionalInformation_pageNameFR);


		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(deleteButton,"Delete Button"))
			{
				if(verifyButtonDisabled(saveButton, "Save Button"))
				{
					createTest.log(Status.PASS,"Create button is enabled, Delete, and Save Buttons are disabled  by default");
				}
				else
				{
					createTest.log(Status.FAIL,"Save  button is not enabled by default");

				}

			}

		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");

		}	

		createAH280_AdditionalInformation();
		driver.findElement(By.xpath("//p[text()='subFR'] | //p[text()='subEN']")).click();
		clickDeleteButton();
		if(!driver.getPageSource().contains("//p[text()='subFR'] | //p[text()='subEN']" )) 
		{
			createTest.log(Status.PASS,"Successfully deleted record in "+ AH280_AdditionalInformation_pageNameFR);

		}
		clickHome();

	}
	
	//JAN Month-------------------------------------------------------
	
	//*******************************************************Perfusion-Site*******************************************************************

			String perfusionSite_screenNameFR= "Gestion des tables locales - Perfusions - Site";
			String perfusionSite_screenNameEN="Management of local tables - Perfusions - Site";
			String perfusionSite_pageNameFR= "Perfusions - Site";
			String perfusionSite_pageNameEN="Perfusions - Site";

			String perfusionSitedescFR = RandomStringUtils.randomAlphanumeric(5);
			String perfusionSitedescEN = RandomStringUtils.randomAlphanumeric(5);
			void navigateToPerfusionSite() throws InterruptedException
			{
					navigateToMedicationTablesAndKardexMenu();
					WebElement perfusions_Link = driver.findElement(By.xpath("//div[contains(text(),' Perfusions - Site')] | //div[contains(text(),' Perfusions - Site')]"));
					perfusions_Link.click();
					sleeps();

					if((driver.getPageSource().contains(perfusionSite_screenNameFR)) | (driver.getPageSource().contains(perfusionSite_screenNameEN)))
					{
						createTest.log(Status.PASS,"Successfully Navigated to screen "+ perfusionSite_screenNameEN);
					
					}
					else
					{
						createTest.log(Status.FAIL,"Failed to navigate to "+perfusionSite_screenNameEN);
					
					}
				
					//clickingMainMenu();
					//sleeps();
			}

			void createPerfusionSite() throws InterruptedException, AWTException
			{
				
				    clickCreateButton();
					pressAndReleaseTabKey();
					driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(perfusionSitedescFR);
					pressAndReleaseTabKey();
					driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(perfusionSitedescEN);
					
					clickSaveButton();
				
			}

			@Parameters("Browser")
			@Test(groups = {"PerfusionsSite"},priority=303)
			void perfusionSite_Validation(String Browser) throws InterruptedException, AWTException
			{System.out.println("enteres perfusion");
					createTest=reports.createTest("Verifying Screen " + perfusionSite_pageNameEN);	

					navigateToPerfusionSite();
				
					verifyBreadCrumb(perfusionSite_pageNameEN, perfusionSite_pageNameFR);
				
			  					
					//Verify restore, delete, save an continue buttons are disabled
					WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
					WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
					WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
					WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
					//Verify Default status of buttons
					if(verifyButtonEnabled(createButton, "Create Button"))
					{
						if(verifyButtonDisabled(restoreButton,"Restore Button"))
						{
							if(verifyButtonEnabled(deleteButton,"Delete Button"))
							{
								if(verifyButtonDisabled(saveButton, "Save Button"))
								{
									createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
								}
								else
								{
								createTest.log(Status.FAIL,"Save  button is not enabled by default");
								
								}
							}
							else
							{
								createTest.log(Status.FAIL,"Delete button is not disabled by default");
					
							}
						}
						else
						{
							createTest.log(Status.FAIL,"Restore button is not enabled by default");
				
						}
					}
					else
					{
						createTest.log(Status.FAIL,"Create button is not disabled by default");
					
					}	
				
					filter(perfusionSitedescFR,1);
					if(driver.getPageSource().contains(perfusionSitedescFR))
					{
						driver.findElement(By.xpath("//span[text()='"+perfusionSitedescFR+"']")).click();
						sleeps();
						clickDeleteButton();
						clickSaveButton();
						sleeps();
					
					}
					
					createPerfusionSite();
					filter(perfusionSitedescFR,1);
				    if(driver.getPageSource().contains(perfusionSitedescFR))
					{
						createTest.log(Status.PASS,"Successfully created new row in "+perfusionSite_screenNameEN);
					}
					else
					{
						createTest.log(Status.FAIL,"Failed to create new row in "+perfusionSite_screenNameEN);
					}
				   
					//Edit-----------------
				    checkEdit(perfusionSitedescFR,editCode);
				
					//Check Restore
				    checkRestore(perfusionSitedescFR,restoreCode);
			
				    //create duplicate-----------------
					createPerfusionSite();
					verifyDuplicationAllowed();
					
				}

			@Test(groups = {"PerfusionsSite"},priority=304 )
			void perfusionSite_filterRecords() throws InterruptedException, AWTException
			{
				checkFilterAndDeletion(perfusionSitedescFR);
				deleteRecordAndExitFromPage(perfusionSitedescFR);
				navigateToPerfusionSite();
				verifyDeletion(perfusionSitedescFR);
				clickHome();
				
			}
			
			
		//*******************************************************Perfusion-Specification*******************************************************************

		String perfusionSpecification_screenNameFR= "Gestion des tables locales - Perfusions-spécification";
		String perfusionSpecification_screenNameEN="Management of local tables - Perfusions-specification";
		String perfusionSpecification_pageNameFR= "Perfusions-spécification";
		String perfusionSpecification_pageNameEN="Perfusions-specification";
		String perfusionSpecificationCode = RandomStringUtils.randomNumeric(4);
		String perfusionSpecificationdescFR = RandomStringUtils.randomAlphanumeric(5);
		String perfusionSpecificationdescEN = RandomStringUtils.randomAlphanumeric(5);
		
		void navigateToPerfusionSpecification() throws InterruptedException
		{
			navigateToMedicationTablesAndKardexMenu();
			WebElement perfusionSpec_Link = driver.findElement(By.xpath("//div[contains(text(),' Perfusions-spécification')] | //div[contains(text(),' Perfusions-specification')]"));
			perfusionSpec_Link.click();
			sleeps();

			if((driver.getPageSource().contains(perfusionSpecification_screenNameFR)) | (driver.getPageSource().contains(perfusionSpecification_screenNameEN)))
			{
				createTest.log(Status.PASS,"Successfully Navigated to screen "+ perfusionSpecification_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to navigate to "+perfusionSpecification_screenNameEN);
					
			}
				
			//clickingMainMenu();
			//sleeps();
		}

		void createPerfusionSpecification() throws InterruptedException, AWTException
		{	
			clickCreateButton();
			driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(perfusionSpecificationCode);
			pressAndReleaseTabKey();
			pressAndReleaseTabKey();
			driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(perfusionSpecificationdescFR);
			pressAndReleaseTabKey();
			driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(perfusionSpecificationdescEN);
			clickSaveButton();
				
		}

		@Parameters("Browser")
		@Test(groups = {"PerfusionSpecification"},priority=305)
		void perfusionSpecification_Validation(String Browser) throws InterruptedException, AWTException
		{
			String editCode = RandomStringUtils.randomNumeric(4);
			String restoreCode = RandomStringUtils.randomNumeric(4);
			
			createTest=reports.createTest("Verifying Screen " + perfusionSpecification_pageNameEN);	
			navigateToPerfusionSpecification();
			verifyBreadCrumb(perfusionSpecification_pageNameEN, perfusionSpecification_pageNameFR);
				
			//Verify restore, delete, save an continue buttons are disabled
			WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
			WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
			WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
			WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
			//Verify Default status of buttons
			if(verifyButtonEnabled(createButton, "Create Button"))
			{
				if(verifyButtonDisabled(restoreButton,"Restore Button"))
				{
					if(verifyButtonEnabled(deleteButton,"Delete Button"))
					{
						if(verifyButtonDisabled(saveButton, "Save Button"))
						{
							createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
						}
						else
						{
							createTest.log(Status.FAIL,"Save  button is not enabled by default");
						}
					}
					else
					{
						createTest.log(Status.FAIL,"Delete button is not disabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Restore button is not enabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Create button is not disabled by default");
			}	
				
			
					
			createPerfusionSpecification();
			filter(perfusionSpecificationCode,1);
			if(driver.getPageSource().contains(perfusionSpecificationCode))
			{
				createTest.log(Status.PASS,"Successfully created new row in "+perfusionSpecification_screenNameEN);
			}
			else
			{
				createTest.log(Status.FAIL,"Failed to create new row in "+perfusionSpecification_screenNameEN);
			}
				   
			//Edit-----------------
			checkEdit(perfusionSpecificationCode,editCode);
				
			//Check Restore
			checkRestore(perfusionSpecificationCode,restoreCode);
			
			//create duplicate-----------------
			createPerfusionSpecification();
			verifyDuplicationNotAllowed();
					
		}

	@Test(groups = {"PerfusionSpecification"},priority=306 )
	void perfusionSpecification_filterRecords() throws InterruptedException, AWTException
	{
		checkFilterAndDeletion(perfusionSpecificationCode);
		deleteRecordAndExitFromPage(perfusionSpecificationCode);
		navigateToPerfusionSpecification();
		verifyDeletion(perfusionSpecificationCode);
		clickHome();
				
	}

	//*******************************************************List of Quick Prescription Sheets*******************************************************************

	String listOfQuickPrescriptionSheet_screenNameFR= "Gestion des tables locales - Liste des feuilles d'ordonnance rapide";
	String listOfQuickPrescriptionSheet_screenNameEN="Management of local tables - List of quick prescription sheets";
	String listOfQuickPrescriptionSheet_pageNameFR= "Liste des feuilles d'ordonnance rapide";
	String listOfQuickPrescriptionSheet_pageNameEN="List of quick prescription sheets";
	
	String listOfQuickPrescriptionSheetdescFR = RandomStringUtils.randomAlphanumeric(5);
	String listOfQuickPrescriptionSheetdescEN = RandomStringUtils.randomAlphanumeric(5);
	
	void navigateToListOfQuickPrescriptionSheet() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement perfusionSpec_Link = driver.findElement(By.xpath("//div[contains(text(),'Liste des feuilles')] | //div[contains(text(),' List of quick prescription sheets')]"));
		perfusionSpec_Link.click();
		sleeps();

		if((driver.getPageSource().contains(listOfQuickPrescriptionSheet_screenNameFR)) | (driver.getPageSource().contains(listOfQuickPrescriptionSheet_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ listOfQuickPrescriptionSheet_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+listOfQuickPrescriptionSheet_screenNameEN);
				
		}
			
		//clickingMainMenu();
		//sleeps();
	}

	void createListOfQuickPrescriptionSheet() throws InterruptedException, AWTException
	{	
		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfQuickPrescriptionSheetdescFR);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfQuickPrescriptionSheetdescEN);
		clickSaveButton();
			
	}

	@Parameters("Browser")
	@Test(groups = {"ListOfQuickPrescriptionSheet"},priority=307)
	void listOfQuickPrescriptionSheet_Validation(String Browser) throws InterruptedException, AWTException
	{
		String editCode = RandomStringUtils.randomNumeric(4);
		String restoreCode = RandomStringUtils.randomNumeric(4);
		
		createTest=reports.createTest("Verifying Screen " + listOfQuickPrescriptionSheet_pageNameEN);	
		navigateToListOfQuickPrescriptionSheet();
		verifyBreadCrumb(listOfQuickPrescriptionSheet_pageNameEN, listOfQuickPrescriptionSheet_pageNameFR);
			
		
		
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
							
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
			
		
				
		createListOfQuickPrescriptionSheet();
		filter(listOfQuickPrescriptionSheetdescFR,1);
		if(driver.getPageSource().contains(listOfQuickPrescriptionSheetdescFR))
		{
			createTest.log(Status.PASS,"Successfully created new row in "+listOfQuickPrescriptionSheet_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+listOfQuickPrescriptionSheet_screenNameEN);
		}
			   
		//Edit-----------------
		checkEdit(listOfQuickPrescriptionSheetdescFR,editCode);
			
		//Check Restore
		checkRestore(listOfQuickPrescriptionSheetdescFR,restoreCode);
		
		//create duplicate-----------------
		createListOfQuickPrescriptionSheet();
		verifyDuplicationAllowed();
		
		//Unit button verification
		verifyUnitButton();
				
	}

	@Test(groups = {"ListOfQuickPrescriptionSheet"},priority=308 )
	void listOfQuickPrescriptionSheet_filterRecords() throws InterruptedException, AWTException
	{
		navigateToListOfQuickPrescriptionSheet();
		checkFilterAndDeletion(listOfQuickPrescriptionSheetdescFR);
		deleteRecordAndExitFromPage(listOfQuickPrescriptionSheetdescFR);
		navigateToListOfQuickPrescriptionSheet();
		verifyDeletion(listOfQuickPrescriptionSheetdescFR);
		clickHome();
			
	}

	//*******************************************************List of Quick Prescription Section*******************************************************************

	String listOfQuickPrescriptionSection_screenNameFR= "Gestion des tables locales - Liste des sections d'ordonnance rapide";
	String listOfQuickPrescriptionSection_screenNameEN="Management of local tables - List of quick prescription sections";
	String listOfQuickPrescriptionSection_pageNameFR= "Liste des sections d'ordonnance rapide";
	String listOfQuickPrescriptionSection_pageNameEN="List of quick prescription sections";
	
	String listOfQuickPrescriptionSectiondescFR = RandomStringUtils.randomAlphanumeric(5);
	String listOfQuickPrescriptionSectiondescEN = RandomStringUtils.randomAlphanumeric(5);
	String listOfQuickPrescriptionSectionOrder = RandomStringUtils.randomNumeric(5);
	
	void navigateToListOfQuickPrescriptionSection() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement perfusionSpec_Link = driver.findElement(By.xpath("//div[contains(text(),'Liste des sections')] | //div[contains(text(),' List of quick prescription sections')]"));
		perfusionSpec_Link.click();
		sleeps();
		sleeps();

		if((driver.getPageSource().contains(listOfQuickPrescriptionSection_screenNameFR)) | (driver.getPageSource().contains(listOfQuickPrescriptionSection_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ listOfQuickPrescriptionSection_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+listOfQuickPrescriptionSection_screenNameEN);
				
		}
			
		//clickingMainMenu();
		//sleeps();
	}

	void createListOfQuickPrescriptionSection() throws InterruptedException, AWTException
	{	
		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfQuickPrescriptionSectiondescFR);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfQuickPrescriptionSectiondescEN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfQuickPrescriptionSectionOrder);
		clickSaveButton();
			
	}

	@Parameters("Browser")
	@Test(groups = {"ListOfQuickPrescriptionSection"},priority=309)
	void listOfQuickPrescriptionSection_Validation(String Browser) throws InterruptedException, AWTException
	{
		String editCode = RandomStringUtils.randomNumeric(4);
		String restoreCode = RandomStringUtils.randomNumeric(4);
		
		createTest=reports.createTest("Verifying Screen " + listOfQuickPrescriptionSection_pageNameEN);	
		navigateToListOfQuickPrescriptionSection();
		verifyBreadCrumb(listOfQuickPrescriptionSection_pageNameEN, listOfQuickPrescriptionSection_pageNameFR);
			
		
		
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
							
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and Delete buttons are enabled , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
			
		
				
		createListOfQuickPrescriptionSection();
		filter(listOfQuickPrescriptionSectiondescFR,1);
		if(driver.getPageSource().contains(listOfQuickPrescriptionSectiondescFR))
		{
			createTest.log(Status.PASS,"Successfully created new row in "+listOfQuickPrescriptionSection_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+listOfQuickPrescriptionSection_screenNameEN);
		}
			   
		//Edit-----------------
		checkEdit(listOfQuickPrescriptionSectiondescFR,editCode);
			
		//Check Restore
		checkRestore(listOfQuickPrescriptionSectiondescFR,restoreCode);
		
		//create duplicate-----------------
		createListOfQuickPrescriptionSection();
		verifyDuplicationNotAllowed();
						
	}

	@Test(groups = {"ListOfQuickPrescriptionSection"},priority=310 )
	void listOfQuickPrescriptionSection_filterRecords() throws InterruptedException, AWTException
	{
		navigateToListOfQuickPrescriptionSection();
		checkFilterAndDeletion(listOfQuickPrescriptionSectiondescFR);
		deleteRecordAndExitFromPage(listOfQuickPrescriptionSectiondescFR);
		navigateToListOfQuickPrescriptionSection();
		verifyDeletion(listOfQuickPrescriptionSectiondescFR);
		clickHome();
			
	}
	
	//*******************************************************List of Links Sheet Section*******************************************************************

	String listOfLinksSheetSection_screenNameFR= "Gestion des tables locales - Liste des liens feuille-section";
	String listOfLinksSheetSection_screenNameEN="Management of local tables - List of links sheet-section";
	String listOfLinksSheetSection_pageNameFR= "Liste des liens feuille-section";
	String listOfLinksSheetSection_pageNameEN="List of links sheet-section";
		
	String PrescriptionSheetdescFR = "MacPrescriptionFR";
	String PrescriptionSheetdescEN = "MacPrescriptionEN";
	String PrescriptionSectiondescFR = "MacPrescriptionSectionFR";
	String PrescriptionSectiondescEN = "MacPrescriptionSectionEN";
	String listOfSheetSectionOrder ="9890";
		
		
	void navigateTolistOfLinksSheetSection() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement listOfSheetSection_Link = driver.findElement(By.xpath("//div[contains(text(),' Liste des liens feuille-section')] | //div[contains(text(),' List of links sheet-section')]"));
		listOfSheetSection_Link.click();
		sleeps();
		sleeps();
		if((driver.getPageSource().contains(listOfLinksSheetSection_screenNameFR)) | (driver.getPageSource().contains(listOfLinksSheetSection_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ listOfLinksSheetSection_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+listOfLinksSheetSection_screenNameEN);
					
		}
				
		//clickingMainMenu();
		//sleeps();
	}

	void createListOfLinksSheetSection() throws InterruptedException, AWTException
	{	
		Actions a = new Actions(driver);
			
		clickCreateButton();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		
		WebElement sheetOption = driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+PrescriptionSheetdescFR+"')] | //mat-option//span//div[contains(text(),'"+ PrescriptionSheetdescEN+"')]"));
		a.moveToElement(sheetOption).perform();
		sheetOption.click();
		
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
			
		WebElement sectionOption =driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+PrescriptionSectiondescFR+"')] | //mat-option//span//div[contains(text(),'"+ PrescriptionSectiondescEN+"')]"));
		a.moveToElement(sectionOption).perform();
		sectionOption.click();
			
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfSheetSectionOrder);
		clickSaveButton();
				
	}

	@Parameters("Browser")
	@Test(groups = {"ListOfLinkSheetSection"},priority=311)
	void listOfLinksSheetSection_Validation(String Browser) throws InterruptedException, AWTException
	{
			
		createTest=reports.createTest("Verifying Screen " + listOfLinksSheetSection_pageNameEN);	
		navigateTolistOfLinksSheetSection();
		verifyBreadCrumb(listOfLinksSheetSection_pageNameEN, listOfLinksSheetSection_pageNameFR);
				
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
	
		deleteIfExit(listOfSheetSectionOrder);
		createListOfLinksSheetSection();
		filter(listOfSheetSectionOrder,1);
		if(driver.getPageSource().contains(listOfSheetSectionOrder))
		{
			createTest.log(Status.PASS,"Successfully created new row in "+listOfLinksSheetSection_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+listOfLinksSheetSection_screenNameEN);
		}
				   
		//Edit-----------------
		checkEdit(listOfSheetSectionOrder,editCode);
				
		//Check Restore
		checkRestore(listOfSheetSectionOrder,restoreCode);
			
		//create duplicate-----------------
		createListOfLinksSheetSection();
		verifyDuplicationNotAllowed();
							
	}
	
	@Test(groups = {"ListOfLinkSheetSection"},priority= 312 )
	void listOfLinksSheetSection_filterRecords() throws InterruptedException, AWTException
	{
		checkFilterAndDeletion(listOfSheetSectionOrder);
		deleteRecordAndExitFromPage(listOfSheetSectionOrder);
		navigateTolistOfLinksSheetSection();
		verifyDeletion(listOfSheetSectionOrder);
		clickHome();
				
	}
		
	//*******************************************************List of Quick Prescription Items*******************************************************************

	String listOfQuickPrescriptionItems_screenNameFR= "Gestion des tables locales - Liste des items d'ordonnance rapide";
	String listOfQuickPrescriptionItems_screenNameEN="Management of local tables - List of quick prescription items";
	String listOfQuickPrescriptionItems_pageNameFR= "Liste des items d'ordonnance rapide";
	String listOfQuickPrescriptionItems_pageNameEN="List of quick prescription items";

	String listOfItemOrder ="9087";
	
	void navigateToListOfQuickPrescriptionItems() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement listOfItems_Link = driver.findElement(By.xpath("//div[contains(text(),' Liste des items')] | //div[contains(text(),' List of quick prescription items')]"));
		listOfItems_Link.click();
		sleeps();

		if((driver.getPageSource().contains(listOfQuickPrescriptionItems_screenNameFR)) | (driver.getPageSource().contains(listOfQuickPrescriptionItems_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ listOfQuickPrescriptionItems_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+listOfQuickPrescriptionItems_screenNameEN);
					
		}
				
		//clickingMainMenu();
		//sleeps();
		//sleeps();
	}

	void createListOfQuickPrescriptionItems() throws InterruptedException, AWTException
	{	
		Actions a = new Actions(driver);
			
		clickCreateButton();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfItemOrder);
		pressAndReleaseEnterKey();
		
		filter(listOfItemOrder,1);
		driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')]//mat-select)[1]")).click();
		pressAndReleaseEnterKey();
		WebElement sectionOption =driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+PrescriptionSectiondescFR+"')] | //mat-option//span//div[contains(text(),'"+ PrescriptionSectiondescEN+"')]"));
		a.moveToElement(sectionOption).perform();
		sectionOption.click();
		
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'X1')]")).click();
		
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'01')][1]")).click();
		clickSaveButton();
		
	}

	@Parameters("Browser")
	@Test(groups = {"ListOfQuickPrescriptionItems"},priority=313)
	void listOfQuickPrescriptionItems_Validation(String Browser) throws InterruptedException, AWTException
	{
		String editCode=RandomStringUtils.randomNumeric(3);
		String restoreCode=RandomStringUtils.randomNumeric(3);
		
		createTest=reports.createTest("Verifying Screen " + listOfQuickPrescriptionItems_pageNameEN);	
		navigateToListOfQuickPrescriptionItems();
		verifyBreadCrumb(listOfQuickPrescriptionItems_pageNameEN, listOfQuickPrescriptionItems_pageNameFR);
				
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
	
		deleteIfExit(listOfItemOrder);
		createListOfQuickPrescriptionItems();
		filter(listOfItemOrder,1);
		if(driver.getPageSource().contains(listOfItemOrder))
		{
			createTest.log(Status.PASS,"Successfully created new row in "+listOfQuickPrescriptionItems_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+listOfQuickPrescriptionItems_screenNameEN);
		}
				   
		//Edit-----------------
		checkEdit(listOfItemOrder,editCode);
				
		//Check Restore
		checkRestore(listOfItemOrder,restoreCode);
			
		//create duplicate-----------------
		createListOfQuickPrescriptionItems();
		verifyDuplicationNotAllowed();
							
	}
	
	@Test(groups = {"ListOfQuickPrescriptionItems"},priority=314)
	void listOfQuickPrescriptionItems_filterRecords() throws InterruptedException, AWTException
	{
		checkFilterAndDeletion(listOfItemOrder);
		deleteRecordAndExitFromPage(listOfItemOrder);
		driver.navigate().refresh();
		sleeps();
		sleeps();
		clickHome();
		navigateToListOfQuickPrescriptionItems();
		verifyDeletion(listOfItemOrder);
		clickHome();
				
	}
	
	
	
	//*******************************************************List of Quick Prescription RX*******************************************************************

	String listOfQuickPrescriptionRX_screenNameFR= "Gestion des tables locales - Liste des médicaments d'ordonnance rapide";
	String listOfQuickPrescriptionRX_screenNameEN="Management of local tables - List of quick prescription rx";
	String listOfQuickPrescriptionRX_pageNameFR= "Liste des médicaments d'ordonnance rapide";
	String listOfQuickPrescriptionRX_pageNameEN="List of quick prescription rx";

	String RXOrder ="9087";
	
	void navigateToListOfQuickPrescriptionRX() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement listOfOtherItems_Link = driver.findElement(By.xpath("//div[contains(text(),'Liste des médicaments')] | //div[contains(text(),' List of quick prescription rx')]"));
		listOfOtherItems_Link.click();
		Thread.sleep(25000);

		if((driver.getPageSource().contains(listOfQuickPrescriptionRX_screenNameFR)) | (driver.getPageSource().contains(listOfQuickPrescriptionRX_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ listOfQuickPrescriptionRX_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+listOfQuickPrescriptionRX_screenNameEN);
					
		}
				
		//clickingMainMenu();
	//sleeps();
	}

	void createListOfQuickPrescriptionRX() throws InterruptedException, AWTException
	{	
		Actions a = new Actions(driver);
			
		clickCreateButton();
		
		//select section
		driver.findElement(By.xpath("(//div[@col-id='id_section'])[last()]")).click();
		pressAndReleaseEnterKey();
		WebElement sectionOption =driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+PrescriptionSectiondescFR+"')] | //mat-option//span//div[contains(text(),'"+ PrescriptionSectiondescEN+"')]"));
		a.moveToElement(sectionOption).perform();
		sectionOption.click();
		
		//select Med
		driver.findElement(By.xpath("(//div[@col-id='id_med'])[last()]")).click();
		pressAndReleaseEnterKey();
		Thread.sleep(15000);
		driver.findElement(By.xpath("//span[contains(text(),'ASPIRIN 325 ENROBE ')]")).click();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
		sleeps();
		
		//Enter order
		driver.findElement(By.xpath("(//div[@col-id='ordre_med'])[last()]")).click();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(RXOrder);
		
		//Enter  Dose
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys("1.2789");
		
		//slect unit
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		
		//select frequency
		driver.findElement(By.xpath("(//div[@col-id='voie_id'])[last()]")).click();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		
		//select frequency
		driver.findElement(By.xpath("(//div[@col-id='freq1'])[last()]")).click();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'X1')]")).click();

		//select Hora
		driver.findElement(By.xpath("(//div[@col-id='hora1'])[last()]")).click();
		pressAndReleaseEnterKey();
		driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'01')][1]")).click();
		clickSaveButton();
			
	}

	@Parameters("Browser")
	@Test(groups = {"ListOfQuickPrescriptionRX"},priority=315)
	void listOfQuickPrescriptionRX_Validation(String Browser) throws InterruptedException, AWTException
	{
		String RXeditCode=RandomStringUtils.randomNumeric(3);
		String RXrestoreCode=RandomStringUtils.randomNumeric(5);
		
		createTest=reports.createTest("Verifying Screen " + listOfQuickPrescriptionRX_pageNameEN);	
		navigateToListOfQuickPrescriptionRX();
		verifyBreadCrumb(listOfQuickPrescriptionRX_pageNameEN, listOfQuickPrescriptionRX_pageNameFR);
				
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
	
		deleteIfExit(RXOrder);
		createListOfQuickPrescriptionRX();
		filter(RXOrder,1);
		if(driver.getPageSource().contains(RXOrder))
		{
			createTest.log(Status.PASS,"Successfully created new row in "+listOfQuickPrescriptionRX_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+listOfQuickPrescriptionRX_screenNameEN);
		}
				   
		//Edit-----------------
		checkEdit(RXOrder,RXeditCode);
				
		//Check Restore
		checkRestore(RXOrder,RXrestoreCode);
			
		//create duplicate-----------------
		createListOfQuickPrescriptionRX();
		verifyDuplicationNotAllowed();
							
	}
	
	@Test(groups = {"ListOfQuickPrescriptionRX"},priority=316)
	void listOfQuickPrescriptionRX_filterRecords() throws InterruptedException, AWTException
	{
		checkFilterAndDeletion(RXOrder);
		deleteRecordAndExitFromPage(RXOrder);
		driver.navigate().refresh();
		Thread.sleep(20000);
		clickHome();
		navigateToListOfQuickPrescriptionRX();
		verifyDeletion(RXOrder);
		clickHome();
				
	}
	
	//*******************************************************List of Quick Prescription Other Items*******************************************************************

	String listOfQuickPrescriptionOtherItems_screenNameFR= "Gestion des tables locales - Liste des autres éléments d'ordonnance rapide";
	String listOfQuickPrescriptionOtherItems_screenNameEN="Management of local tables - List of quick prescription other items";
	String listOfQuickPrescriptionOtherItems_pageNameFR= "Liste des autres éléments d'ordonnance rapide";
	String listOfQuickPrescriptionOtherItems_pageNameEN="List of quick prescription other items";
	String otherdropdownvalue ="Phosphatase alcaline (alp) (phos.alc)";
	
	String OtherOption = RandomStringUtils.randomNumeric(1);
	void navigateToListOfQuickPrescriptionOtherItems() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement listOfPrescOtherItems_Link = driver.findElement(By.xpath("//div[contains(text(),'Liste des autres éléments')] | //div[contains(text(),' List of quick prescription other items')]"));
		listOfPrescOtherItems_Link.click();
		sleeps();

		if((driver.getPageSource().contains(listOfQuickPrescriptionOtherItems_screenNameFR)) | (driver.getPageSource().contains(listOfQuickPrescriptionOtherItems_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ listOfQuickPrescriptionOtherItems_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+listOfQuickPrescriptionOtherItems_screenNameEN);
					
		}
				
		//clickingMainMenu();
		//sleeps();
	}

	void createListOfQuickPrescriptionOtherItems() throws InterruptedException, AWTException
	{	
		Actions a = new Actions(driver);
			
		clickCreateButton();
		
		//select section
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		//WebElement sectionOption =driver.findElement(By.xpath("//mat-option//span//div[contains(text(),'"+otherdropdownvalue+"')] | //mat-option//span//div[contains(text(),'"+ otherdropdownvalue+"')]"));
		WebElement sectionOption =driver.findElement(By.xpath("(//mat-option//span//div)["+OtherOption+"]"));
		a.moveToElement(sectionOption).perform();
		sectionOption.click();
		
		pressAndReleaseTabKey();
		Thread.sleep(5000);
		WebElement otherOption =driver.findElement(By.xpath("//span[contains(text(),'00010')]"));
		a.moveToElement(otherOption).perform();
		otherOption.click();
		driver.findElement(By.xpath("(//button[contains(text(),'OK')])[1]")).click();
		sleeps();
		
		clickSaveButton();
				
	}

	@Parameters("Browser")
	@Test(groups = {"ListOfQuickPrescriptionOtherItems"},priority=317)
	void listOfQuickPrescriptionOtherItems_Validation(String Browser) throws InterruptedException, AWTException
	{
			
		createTest=reports.createTest("Verifying Screen " + listOfQuickPrescriptionOtherItems_pageNameEN);	
		navigateToListOfQuickPrescriptionOtherItems();
		verifyBreadCrumb(listOfQuickPrescriptionOtherItems_pageNameEN, listOfQuickPrescriptionOtherItems_pageNameFR);
				
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
	
		createListOfQuickPrescriptionOtherItems();
		if(driver.getPageSource().contains(otherdropdownvalue) | driver.getPageSource().contains(otherdropdownvalue))
		{
			createTest.log(Status.PASS,"Successfully created new row in "+listOfQuickPrescriptionOtherItems_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+listOfQuickPrescriptionOtherItems_screenNameEN);
		}
				   
		
		//create duplicate-----------------
		createListOfQuickPrescriptionOtherItems();
		verifyDuplicationNotAllowed();
							
	}
	
	@Test(groups = {"ListOfQuickPrescriptionOtherItems"},priority=318)
	void listOfQuickPrescriptionOtherItems_filterRecords() throws InterruptedException, AWTException
	{
		clickAllFilter();
		if(driver.getPageSource().contains(otherdropdownvalue) | driver.getPageSource().contains(otherdropdownvalue))
		{
			System.out.println("contains in ALl-1");
			clickActiveFilter();

			if(driver.getPageSource().contains(otherdropdownvalue) | driver.getPageSource().contains(otherdropdownvalue))
			{
				System.out.println("contains in Active-1");
				sleeps();
				clickNonActiveFilter();
				if(!driver.getPageSource().contains(otherdropdownvalue) | !driver.getPageSource().contains(otherdropdownvalue))
				{
					System.out.println(" not contains in nonActive-1");
					sleeps();
					clickReservedFilter();

					if(!driver.getPageSource().contains(otherdropdownvalue) | !driver.getPageSource().contains(otherdropdownvalue))
					{
						System.out.println("not contains in reserved1");
						createTest.log(Status.PASS,"Active Filter is working fine " );

					
					}
					else
					{
						createTest.log(Status.FAIL,"Active Rrecord exist in Reserved");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Active Rrecord exist in non active filter");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Active Rrecord not exist in Active filter");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Active filter is not working");
		}
		
		
		clickAllFilter();
		sleeps();
		//WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing')]//span//input)[1]"));
		WebElement activeCheckbox1 = driver.findElement(By.xpath("(//div[@col-id='actif'])[last()]//input"));
		activeCheckbox1.click();
		pressAndReleaseSpaceKey();
		clickSaveButton();

		System.out.println("made record inactive"); 
		//Check if already present in ALl filter
		if(driver.getPageSource().contains(otherdropdownvalue) | driver.getPageSource().contains(otherdropdownvalue))
		{
			System.out.println("contains in All-2");
			clickActiveFilter();
						
			if(!driver.getPageSource().contains(otherdropdownvalue) | !driver.getPageSource().contains(otherdropdownvalue))
			{
				System.out.println("not contains in Active-2");
				clickNonActiveFilter();
						
				if(driver.getPageSource().contains(otherdropdownvalue) | driver.getPageSource().contains(otherdropdownvalue))
				{
					System.out.println(" contains in Non Active-2");
					clickReservedFilter();

					if(!driver.getPageSource().contains(otherdropdownvalue) | !driver.getPageSource().contains(otherdropdownvalue))
											{
						System.out.println(" not contains in reserved-2");
						
						createTest.log(Status.PASS,"Non Active filter is working fine");
						createTest.log(Status.PASS,"All filter is working fine");
						createTest.log(Status.PASS,"Reserved filter is working fine");
						clickAllFilter();
						sleeps();
						
					}
						
						
				}
					
			}
		}
		
		else
		{
			createTest.log(Status.FAIL,"Non Active filter is not working");
		}
		
		createTest.log(Status.PASS,"Successfully edited new row in "+listOfQuickPrescriptionOtherItems_screenNameEN);
		createTest.log(Status.PASS,"Restore Button is working fine in "+listOfQuickPrescriptionOtherItems_screenNameEN);
		
		//deletion
		driver.findElement(By.xpath("(//div[@col-id='actif'])[last()]//input")).click();
		clickDeleteButton();
		clickHome();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			createTest.log(Status.PASS,"A confirmation is shown when Home link was clicked without saving");
			clickYesButton();
			sleeps();
			
		}
		else
		{
			createTest.log(Status.FAIL,"A confirmation is not shown when Home link was clicked without saving");
		}
		Thread.sleep(4000);
		
		navigateToListOfQuickPrescriptionOtherItems();
		if(!driver.getPageSource().contains(otherdropdownvalue) | !driver.getPageSource().contains(otherdropdownvalue))
		{
			createTest.log(Status.PASS,"Deletion is working fine");
		}
		clickHome();
				
	}
	
	//******************************************************Triage protocols***********************************************************

	String triageProtocols_screenNameFR= "Liste des protocoles de triage";
	String triageProtocols_screenNameEN="List of triage protocols";
	String triageProtocols_pageNameFR= "Protocoles au triage";
	String triageProtocols_pageNameEN="Triage protocols";

	void navigateToTriageProtocols() throws InterruptedException
	{
		navigateToMedicationTablesAndKardexMenu();
		WebElement triage_Link = driver.findElement(By.xpath("//div[contains(text(),' Protocoles au triage')] | //div[contains(text(),' Triage protocols')]"));
		triage_Link.click();
		sleeps();
		sleeps();
		//clickingMainMenu();
		createTest.log(Status.PASS,"Successfully Navigated to screen "+ triageProtocols_screenNameEN);
		sleeps();
	}

	void createTriageProtocols() throws InterruptedException, AWTException
	{	
		Actions a = new Actions(driver);
		
		clickCreateButton();
		
		//Enter code
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(listOfProtocolsCode);
				
		//Enter  Mode
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		
		//Enter  Mode
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		
		clickSaveButton();
	}

	@Parameters("Browser")
	@Test(groups = {"TriageProtocols"},priority=319)
	void triageProtocols_Validation(String Browser) throws InterruptedException, AWTException
	{
			
		createTest=reports.createTest("Verifying Screen " + triageProtocols_pageNameEN);	
		navigateToTriageProtocols();
		verifyBreadCrumb(triageProtocols_pageNameEN, triageProtocols_pageNameFR);
				
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
		//Verify Default status of buttons
		if(verifyButtonEnabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create button is enabled, Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
	
		deleteIfExit(listOfProtocolsCode);
		createTriageProtocols();
		filter(listOfProtocolsCode,1);
		if(driver.getPageSource().contains(listOfProtocolsCode))
		{
			createTest.log(Status.PASS,"Successfully created new row in "+triageProtocols_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+triageProtocols_screenNameEN);
		}
				   
		//Edit-----------------
		checkEdit(listOfProtocolsCode,editCode);
				
		//Check Restore
		checkRestore(listOfProtocolsCode,restoreCode);
			
		//create duplicate-----------------
		createTriageProtocols();
		verifyDuplicationNotAllowed();
							
	}
	
	
	
	@Test(groups = {"TriageProtocols"},priority=320)
	public void verifyProtocolConfiguration() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("VerifyingList of Protocols in " + triageProtocols_pageNameEN);	

		sleeps();
		filter(listOfProtocolsCode,1);
		WebElement record = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		record.click();
		
		driver.findElement(By.xpath("(((//span[contains(text(),' Respiratory arrest')] | //span[contains(text(),' Arrêt respiratoire')])//parent::div//parent::div)[1]//preceding-sibling::div//span[contains(text(),add)])[3]")).click();
		if(verifyButtonDisabled(driver.findElement(By.xpath("//button[contains(text(),'Associate complaints')] | //button[contains(text(),'Associer les plaintes')]")), "Associate Complaints"))
		{
			driver.findElement(By.xpath("//span[contains(text(),'Association mode')] | //span[contains(text(),'Mode association')]")).click();
			sleeps();
			if(verifyButtonEnabled(driver.findElement(By.xpath("//button[contains(text(),'Associate complaints')] | //button[contains(text(),'Associer les plaintes')]")), "Associate Complaints"))
			{
				createTest.log(Status.PASS,"Associate Complaint Button is not enabled until and unless Respiratory Asset checkbox is checked");
			}	
		}
		
		driver.findElement(By.xpath("//button[contains(text(),'Associate complaints')] | //button[contains(text(),'Associer les plaintes')]")).click();
		driver.findElement(By.xpath("(//span[contains(text(),'Arrêt respiratoire')] | //span[contains(text(),' Respiratory arrest')])[1]")).click();
		createTest.log(Status.PASS,"Associate Complaint Button is successfully added");

		filter(listOfProtocolsCode,1);
		driver.findElement(By.xpath("//span[contains(text(),'"+listOfProtocolsCode+"')]")).click();
		
		//Configuration Protocol
		driver.findElement(By.xpath("//button[contains(text(),'Configuration - Protocoles')] | //button[contains(text(),'Protocols - Configuration')]")).click();
		sleeps();
		sleeps();
		sleeps();

		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));

		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonEnabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create and  Delete are enabled, Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");

					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");

				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");

			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	
		
		verifyDocumentationButton();
		listOfProtocols_ExamLabs();
		listOfProtocols_Medicaments();
		listOfProtocols_ProtocolsInclus();
		
	}
	
	@Test(groups = {"TriageProtocols"},priority=321 )
	void triageProtocols_filterAndDeletion() throws InterruptedException, AWTException
	{
		createTest=reports.createTest("Verifying Filter and Deletion in " + triageProtocols_pageNameEN);	

		driver.findElement(By.xpath("//a[contains(text(),'Protocoles au triage')] | //a[contains(text(),'Triage protocols')]")).click();
		
		//delete record
		filter(listOfProtocolsCode,1);
		WebElement record = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
		record.click();
		clickDeleteButton();
		clickSaveButton();
		if(((driver.getPageSource().contains("La commande d’extraction et/ou de sauvegarde de données a échoué."))|(driver.getPageSource().contains("Data extraction and/or saving command failed."))) )
		{	
			createTest.log(Status.PASS,"Traige protocol linked to List of Protocol is not allowed to delete");
			clickOKButton();
				
		}
		verifyClearFilter();
		
		filter(listOfProtocolsCode,1);
		driver.findElement(By.xpath("//span[contains(text(),'"+listOfProtocolsCode+"')]")).click();
		driver.findElement(By.xpath("//button[contains(text(),'Configuration - Protocoles')] | //button[contains(text(),'Protocols - Configuration')]")).click();
		sleeps();
		sleeps();
		
		verify_listOfProtocolDiet();
		
		navigateToTriageProtocols();
		filter(listOfProtocolsCode,1);
		if(!driver.getPageSource().contains(listOfProtocolsCode))
		{
			createTest.log(Status.PASS,"Created record is successfully deleted!!!");
		}
		else
		{
			createTest.log(Status.FAIL,"Deletion is not working properly");
		}
		clickHome();
	}
	//*******************************************************Departure Print Management******************************************************************

	String departurePrintManagement_screenNameFR= "Gestion des tables locales - Gestionnaire d'impression au départ";
	String departurePrintManagement_screenNameEN="Management of local tables - Departure print management";
	String departurePrintManagement_pageNameFR= "Gestionnaire d'impression au départ";
	String departurePrintManagement_pageNameEN="Departure print management";
		
	String departurePrintManagement_descFR="Retour à domicile";
	String departurePrintManagement_descEN="Return home";
	
	void navigateToDeparturePrintManagement() throws InterruptedException
	{
		navigateToPrintManagementMenu();
		moveToElementAndClick("//div[contains(text(),'impression au départ')] | //div[contains(text(),' Departure print management')]");
		sleeps();
		sleeps();

		if((driver.getPageSource().contains(departurePrintManagement_screenNameFR)) | (driver.getPageSource().contains(departurePrintManagement_screenNameEN)))
		{
			createTest.log(Status.PASS,"Successfully Navigated to screen "+ departurePrintManagement_screenNameEN);
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to navigate to "+departurePrintManagement_screenNameEN);
					
		}
				
		//clickingMainMenu();
		//sleeps();
	}

	
	@Parameters("Browser")
	@Test(groups = {"DeparturePrintManagement"},priority=322)
	void departurePrintManagement_Validation(String Browser) throws InterruptedException, AWTException
	{	
		createTest=reports.createTest("Verifying Screen " + departurePrintManagement_pageNameEN);	
		navigateToDeparturePrintManagement();
		verifyBreadCrumb(departurePrintManagement_pageNameEN, departurePrintManagement_pageNameFR);
				
		//Verify restore, delete, save an continue buttons are disabled
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Create')] | //button[contains(text(),'Créer')]"));
		WebElement restoreButton = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Delete')] | //button[contains(text(),'Supprimer')]"));
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),' Save')] | //button[contains(text(),' Sauvegarder')]"));
								
		//Verify Default status of buttons
		if(verifyButtonDisabled(createButton, "Create Button"))
		{
			if(verifyButtonDisabled(restoreButton,"Restore Button"))
			{
				if(verifyButtonDisabled(deleteButton,"Delete Button"))
				{
					if(verifyButtonDisabled(saveButton, "Save Button"))
					{
						createTest.log(Status.PASS,"Create , Delete , Restore and Save Buttons are disabled  by default");
					}
					else
					{
						createTest.log(Status.FAIL,"Save  button is not enabled by default");
					}
				}
				else
				{
					createTest.log(Status.FAIL,"Delete button is not disabled by default");
				}
			}
			else
			{
				createTest.log(Status.FAIL,"Restore button is not enabled by default");
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}	

		//Filter check
		filter(departurePrintManagement_descFR,1);
		filter(departurePrintManagement_descEN,2);
		if(driver.getPageSource().contains(departurePrintManagement_descFR))
		{
			if(!driver.getPageSource().contains("Admission CH"))
			{
				createTest.log(Status.PASS,"Filter is succesfully working in "+departurePrintManagement_screenNameEN);
			}
		}
		else
		{
			createTest.log(Status.FAIL,"Failed to create new row in "+departurePrintManagement_screenNameEN);
		}
		
		//Clear filter check
		clickClearFilterButton();
		if(driver.getPageSource().contains("Admission CH"))
		{
			createTest.log(Status.PASS,"Clear Filter is succesfully working in "+departurePrintManagement_screenNameEN);
		}
		
		//Checking editable
		filter(departurePrintManagement_descFR,1);
		filter(departurePrintManagement_descEN,2);
		driver.findElement(By.xpath("//span[text()='"+departurePrintManagement_descFR+"']")).click();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-not-inline-editing')][1]")).click();
		createTest.log(Status.PASS,"Verified Description is not editable in "+departurePrintManagement_screenNameEN);
		
		//Restore check
		driver.findElement(By.xpath("(//div[@col-id='print_rap_dci'])[last()]")).click();
		pressAndReleaseSpaceKey();
		clickRestoreButton();
		clickHome();
		createTest.log(Status.PASS,"Restore button is working fine");
		sleeps();
		
				
	}

//Vigneswari Screens---------------------------------------------------------------------

	@Test(groups = {"Medicationconfiguration"},priority=323,enabled=true)
	public void navigateTOMedicationconfiguration() throws InterruptedException {

		String pageNameEnglish= "Medication configuration";
		String pageNameFrench = "Configuration des médicaments";

		navigateTOMedicationTablesAndKardexScreen();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Medication configuration')] | //div[contains(text(),' Configuration des m')]"))).click();
		Thread.sleep(40000);
		createTest=reports.createTest("Validating the functionaltiy of the Medication configuration screen");

		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}

		verifyBreadCrumb(pageNameEnglish, pageNameFrench);
		Thread.sleep(5000);
		Thread.sleep(5000);
		System.out.println("1");

		sleeps();
		sleeps();
		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		System.out.println("2");
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));
		System.out.println("3");
		sleeps();
		sleeps();
		WebElement perfusion = driver.findElement(By.xpath("//button[contains(text(),'Perfusion')] | //button[contains(text(),'Perfusion')]"));
		sleeps();
		sleeps();

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is enabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is enabled by default");
		}

		if(verifyButtonDisabled(perfusion, "Perfusion Button")) {
			createTest.log(Status.PASS,"Perfusion button is not enabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is enabled by default");
		}



	}
	@Test(groups = {"Medicationconfiguration"},priority=324,enabled=true)
	public void verifyingTheError() throws InterruptedException, AWTException {

		clickCreateButton();
		Thread.sleep(10000);
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.clear();
		Nocoulmmn.sendKeys(validCode);
		sleeps();
		pressAndReleaseTabKey();

		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		clickSaveButton();

		sleeps();
		if(driver.getPageSource().contains("La modification de code ou de nom et la création de")|driver.getPageSource().contains("The code or name modification and the creation of medication are impossible")) {
			createTest.log(Status.PASS,"The application throws an error when enter the value between 1 to 99999999");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when enter the value between 1 to 99999999");
		}

		clickRestoreButton();
		Thread.sleep(5000);
		Thread.sleep(5000);
	}
	@Test(groups = {"Medicationconfiguration"},priority=325,enabled=true)
	public void creatingNewRecord() throws InterruptedException, AWTException {

		String code="100000026";

		filter(code, 1);

		if(driver.getPageSource().contains(code))
		{
			driver.findElement(By.xpath("//span[text()='"+code+"']")).click();
			clickDeleteButton();
			clickSaveButton();
			Thread.sleep(60000);
		}

		clickCreateButton();
		Thread.sleep(10000);
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn.clear();
		Nocoulmmn.sendKeys(code);
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		clickSaveButton();
		Thread.sleep(20000);

		filter(code, 1);

		if(driver.getPageSource().contains(code)) {
			createTest.log(Status.PASS,"New record is created");

			driver.findElement(By.xpath("//span[text()='"+code+"']")).click();
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'item')])[4]"))).click();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'item')])[4]"))).click();
		pressAndReleaseTabKey();
		//		pressAndReleaseEnterKey();
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'item')])[4]"))).click();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'item')])[4]"))).click();
		clickSaveButton();
		Thread.sleep(10000);
		Thread.sleep(10000);


		clickCreateButton();
		Thread.sleep(10000);
		WebElement Nocoulmmn1 = driver.findElement(By.xpath("//div[contains(@class,'ag-cell ag-cell-auto-height cell-wrap-text ag-cell-focus ag-cell-inline-editing')][last()]//input"));
		Nocoulmmn1.clear();
		Nocoulmmn1.sendKeys(code);
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseTabKey();
		sleeps();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		clickSaveButton();
		Thread.sleep(10000);

		if((driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) | (driver.getPageSource().contains("Violation of PRIMARY KEY constraint 'PK_PHARM'"))) {

			createTest.log(Status.PASS,"An error is shown when the duplicate record is added");


		}else {
			createTest.log(Status.FAIL,"An error is not shown when the duplicate record is added");
		}
		clickOKButton();
		clickRestoreButton();
		Thread.sleep(10000);
		clickSaveButton();
		Thread.sleep(10000);

		if((!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) | (driver.getPageSource().contains("Violation of PRIMARY KEY constraint 'PK_PHARM'"))) {
			createTest.log(Status.PASS,"The duplicate record is restored");

		}else {
			createTest.log(Status.FAIL,"The duplicate record is not restored");
		}

		filter(code, 1);
		
		
	}
	
	/*@Test(groups = {"Medicationconfiguration"},priority=325,enabled=false)
	public void verifyingThePerfusion() throws InterruptedException, AWTException {

		String s="-187";

		filter(s, 1);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Perfusion')] | //button[contains(text(),'Perfusion')]"))).click();
		Thread.sleep(30000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@class,'btn btn-primary')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@type,'checkbox')])[last()]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-list-text')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[contains(@class,'btn btn-secondary')])[1]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[10]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-ripple mat-option-ripple')])[4]"))).click();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'mat-option-text')])[1]"))).click();

		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'mat-option-text')])[4]"))).click();

		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'mat-option-text')])[1]"))).click();

		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'mat-option-text')])[1]"))).click();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(@class,'mat-option-text')])[1]"))).click();

		clickSaveAndContinueButton();
		clickSaveAndCloseButton();
	}*/
	
	@Test(groups = {"Medicationconfiguration"},priority=326,enabled=false)
	public void verifyingTheIngredient() throws InterruptedException, AWTException {



		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'Ingrédient')] | //button[contains(text(),'Ingredient')]"))).click();

		WebElement create = driver.findElement(By.xpath("//div[@class='col-auto text-start']//button[1]"));
		create.click();

		WebElement addDomain = driver.findElement(By.xpath("//span[contains(text(),'280804+B')]"));
		addDomain.click();

		WebElement okDomain = driver.findElement(By.xpath("//div[@class='col-sm-12 p-0 pt-2 mt-2 d-flex justify-content-end']//button"));
		okDomain.click();
		clickSaveAndContinueButton();

		sleeps();
		create.click();
		sleeps();
		WebElement addDomain1 = driver.findElement(By.xpath("(//span[contains(text(),'280804+B')])[2]"));
		addDomain1.click();

		clickOKSubWindow();

		clickSaveAndContinueButton();

		if((driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY « PK_Pharm_Ingredient")) | (driver.getPageSource().contains("Violation of PRIMARY KEY constraint 'PK_Pharm_Ingredient'."))) {

			createTest.log(Status.PASS,"An error is shown when the duplicate code is added");

		}else {
			createTest.log(Status.FAIL,"An error is not shown when the duplicate code is added");
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button//span[contains(text(),' OK ')]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]"))).click();
		clickSaveAndContinueButton();

		if((driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY « PK_Pharm_Ingredient")) | (driver.getPageSource().contains("Violation of PRIMARY KEY constraint 'PK_Pharm_Ingredient'."))) {
			createTest.log(Status.FAIL,"The duplicate code is not deleted");

		}else {
			createTest.log(Status.PASS,"The duplicate code is deleted");
		}

		clickSaveAndCloseButton();
	}
	@Test(groups = {"Medicationconfiguration"},priority=327,enabled=true)
	public void checkingFilterfunction() throws InterruptedException, AWTException {

		String code="100000026";

		filter(code, 1);

		activeRadiobtn(code);
		nonActiveRadiobtn(code);
		reservedRadiobtn(code);
		AllRadiobtn(code);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();
		Thread.sleep(20000);

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
		
	}


	//**********************Units****************//
	@Test(groups = {"Units"},priority=328,enabled=true)
	public void navigateToUnitsScreen() throws InterruptedException, AWTException {

		String pageNameEnglish= "Units";
		String pageNameFrench = "Unités";
		
		sleeps();
		navigateToMedicationTablesAndKardexMenu();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[text()=' Units'] | //div[contains(text(),'Unités')])"))).click();
		createTest=reports.createTest("Validating the functionaltiy of the Units screen");
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		WebElement createBtn = driver.findElement(By.xpath("//button[contains(text(),'Créer')] | //button[contains(text(),'Create')]"));
		WebElement deleteBtn = driver.findElement(By.xpath("//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}

		if(verifyButtonDisabled(createBtn, "Create Button")) {
			createTest.log(Status.PASS,"Create button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Create button is not disabled by default");
		}

		if(verifyButtonDisabled(deleteBtn, "Delete Button"))
		{
			createTest.log(Status.PASS,"Delete button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Delete button is not disabled by default");
		}

	}
	@Test(groups = {"Units"},priority=329,enabled=true)
	public void verifyingThecolumns() throws InterruptedException, AWTException {

		WebElement orderColumn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')])[5]")));
		orderColumn.click();
		sleeps();
		WebElement orderColumn1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')])//input")));
		sleeps();sleeps();
		orderColumn1.clear();
		sleeps();
		orderColumn1.sendKeys(validCode);
		clickSaveButton();

		filter(validCode, 4);

		if(driver.getPageSource().contains(validCode)) {
			createTest.log(Status.PASS,"The order column is ediatable");
		}else {
			createTest.log(Status.FAIL,"The order column is not ediatable");
		}

	}@Test(groups = {"Units"},priority=330,enabled=true)
	public void verifyingTheFilter() throws InterruptedException, AWTException {

		filter(validCode, 4);

		activeRadiobtn(validCode);
		nonActiveRadiobtn(validCode);
		reservedRadiobtn(validCode);
		AllRadiobtn(validCode);
		sleeps();sleeps();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-cell ag-cell-not-inline-editing ag-cell-auto-height')])[2]//input"))).click();
		pressAndReleaseTabKey();
		sleeps();
		WebElement orderColumn = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'ag-cell-inline-editing')])//input")));
		orderColumn.clear();
		orderColumn.sendKeys("0");
		sleeps();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(validCode))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	//*************************************************Perfusion-Type*****************************************************************//

	@Test(groups = {"PerfusionType"},priority=331,enabled=true)
	public void navigateToPerfusionTypeScreen() throws InterruptedException, AWTException {

		String pageNameEnglish= "Perfusion - Type";
		String pageNameFrench = "Perfusion - Type";

		sleeps();
		navigateToMedicationTablesAndKardexMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()=' Perfusion - Type'] | //div[contains(text(),' Perfusion - Type')]"))).click();
		createTest=reports.createTest("Validating the functionaltiy of the Perfusion-Type screen");
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		
		filter(DescFN, 2);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}
	@Test(groups = {"PerfusionType"},priority=332,enabled=true)
	public void creatingNewRecordPerfusionTyp() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		clickSaveButton();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {

			createTest.log(Status.FAIL,"The Code column accepts alphanumeric values");
		}else {
			createTest.log(Status.PASS,"The Code column accepts only numeric values");
		}
	}@Test(groups = {"PerfusionType"},priority=333,enabled=true)
	public void VerifyingTheErrorsPerfusionType() throws InterruptedException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		clickSaveButton();

		if(driver.getPageSource().contains("Each solution type must be unique.")|driver.getPageSource().contains("Chaque type de soluté doit être unique.")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		clickRestoreButton();
		if(!driver.getPageSource().contains("Each solution type must be unique.")|driver.getPageSource().contains("Chaque type de soluté doit être unique.")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

		clickSaveButton();
	}@Test(groups = {"PerfusionType"},priority=334,enabled=true)
	public void verifyingTheFilterPerfusionType() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}


	//*************************************************Special activities - Configuration*****************************************************************//

	@Test(groups = {"SpecialActivitiesConfiguration"},priority=335,enabled=true)
	public void navigateToSpecialActivitiesConfigurationScreen() throws InterruptedException, AWTException {

		String pageNameEnglish= "Special activities - Configuration";
		String pageNameFrench = "Activités spéciales - Configuration";

		navigateToMedicationTablesAndKardexMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'ciales - Configuration')] | //div[contains(text(),'Special activities - Configuration')]"))).click();
		createTest=reports.createTest("Validating the functionaltiy of the Special activities - Configuration screen");
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		
		filter(DescFN, 2);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
	}
	@Test(groups = {"SpecialActivitiesConfiguration"},priority=336,enabled=true)
	public void creatingNewRecordSpecialActivitiesConfiguration() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		clickSaveButton();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {

			createTest.log(Status.FAIL,"The Code column accepts more than 2 digits values");
		}else {
			createTest.log(Status.PASS,"The Code column accepts only 2 digits values");
		}
	}@Test(groups = {"SpecialActivitiesConfiguration"},priority=337,enabled=true)
	public void VerifyingTheErrorsSpecialActivitiesConfiguration() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")|driver.getPageSource().contains("Violation of PRIMARY KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}
	}@Test(groups = {"SpecialActivitiesConfiguration"},priority=338,enabled=true)
	public void verifyingTheFilterSpecialActivitiesConfiguration() throws InterruptedException, AWTException {
		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	//*************************************************Special activities - Association*****************************************************************//

	@Test(groups = {"SpecialActivitiesAssociation"},priority=339,enabled=true)
	public void navigateToSpecialActivitiesAssociationScreen() throws InterruptedException, AWTException {

		String pageNameEnglish= "Special activities - Association";
		String pageNameFrench = "Activités spéciales - Association";

		navigateToMedicationTablesAndKardexMenu();
		sleeps();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Special activities - Association')] | //div[contains(text(),' Activités spéciales - Association')]"))).click();
		createTest=reports.createTest("Validating the functionaltiy of the Special activities - Association screen");
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		
		
	}
	@Test(groups = {"SpecialActivitiesAssociation"},priority=340,enabled=true)
	public void creatingNewRecordSpecialActivitiesAssociation() throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseTabKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='CCF']"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')]"))).click();
		sleeps();
//		pressAndReleaseTabKey();
		pressAndReleaseTabKey();	
		sleeps();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Intubé ')]"))).click();
		clickSaveButton();

	}@Test(groups = {"SpecialActivitiesAssociation"},priority=341,enabled=true)
	public void VerifyingTheErrorsSpecialActivitiesAssociation() throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseTabKey();
		WebElement until = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='CCF']")));
		until.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'OK')]"))).click();
		sleeps();
		pressAndReleaseTabKey();
//		pressAndReleaseTabKey();
		sleeps();
		pressAndReleaseEnterKey();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Intubé ')]"))).click();
		clickSaveButton();

		if(driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}

	}@Test(groups = {"SpecialActivitiesAssociation"},priority=342,enabled=true)
	public void verifyingTheFilterSpecialActivitiesAssociation() throws InterruptedException, AWTException {

		WebElement specialActivity = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select-trigger[contains(text(),' Intubé ')]")));
		//				 specialActivity.click();
		//				 sleeps();

		String text = specialActivity.getText();

		activeRadiobtn(text);
		nonActiveRadiobtn(text);
		reservedRadiobtn(text);
		AllRadiobtn(text);
		sleeps();
		WebElement specialActivity1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select-trigger[contains(text(),' Intubé ')]")));
		specialActivity1.click();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(text))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	//*************************************************Dosage - groups*****************************************************************//

	@Test(groups = {"Dosagegroups"},priority=343,enabled=true)
	public void navigateToDosagegroupsScreen() throws InterruptedException, AWTException {

		String pageNameEnglish= "Dosage - groups";
		String pageNameFrench = "Posologie - groupes";

		navigateToMedicationTablesAndKardexMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Dosage - groups')] | //div[contains(text(),' Posologie - groupes')]"))).click();
		createTest=reports.createTest("Validating the functionaltiy of the Dosage - groups screen");
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}
	@Test(groups = {"Dosagegroups"},priority=344,enabled=true)
	public void creatingNewRecordDosagegroups() throws InterruptedException, AWTException {
		filter(DescFN, 2);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		pressAndReleaseTabKey();
		pressAndReleaseEnterKey();

		WebElement domainCreate = driver.findElement(By.xpath("//div[@class='col-auto text-start']//button[1]"));
		domainCreate.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();
		pressAndReleaseEnterKey();
		sleeps();
//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'grid-container ng-star-inserted')])[6]"))).click();
		sleeps();
		clickSaveAndContinueButton();

		if(driver.getPageSource().contains("The schedule is mandatory")|driver.getPageSource().contains("L'horaire est obligatoire")) {
			createTest.log(Status.PASS,"The application throws an error when we try to save the frequency without selecting the schedule");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we try to save the frequency without selecting the schedule");
		}


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[2]"))).click();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'grid-container ng-star-inserted')])[5]"))).click();
		clickSaveAndContinueButton();

		domainCreate.click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[3]"))).click();
		pressAndReleaseEnterKey();
//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'grid-container ng-star-inserted')])[6]"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[4]"))).click();
		pressAndReleaseEnterKey();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'grid-container ng-star-inserted')])[5]"))).click();
		clickSaveAndContinueButton();


		if(driver.getPageSource().contains("This frequency aleready exists for this group")|driver.getPageSource().contains("Cette fréquence existe déjà pour ce groupe")) {
			createTest.log(Status.PASS,"The application throws an error when we add duplicate frequency");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we add duplicate frequency");
		}

		clickRestoreSubWindow();
		clickSaveAndContinueButton();

		if(!driver.getPageSource().contains("This frequency aleready exists for this group")|driver.getPageSource().contains("Cette fréquence existe déjà pour ce groupe")) {
			createTest.log(Status.PASS,"The duplicate frequency has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate frequency has not been restored");
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'mat-select-arrow-wrapper')])[1]"))).click();
		clickDeleteSubWindow();

		clickSaveAndCloseButton();

		sleeps();

		filter(DescFN, 2);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

		if(driver.getPageSource().contains(alphanumericCode)) {

			createTest.log(Status.PASS,"The group column accepts alphanumeric value");
		}else {
			createTest.log(Status.FAIL,"The group column not accepts the alphanumeric value");
		}

	}@Test(groups = {"Dosagegroups"},priority=345,enabled=true)
	public void VerifyingTheErrorsDosagegroups() throws InterruptedException, AWTException {

		clickCreateButton();
		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		clickSaveButton();

		if(driver.getPageSource().contains("The group must be unique")|driver.getPageSource().contains("Le groupe doit être unique")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate group is entered");
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate group is entered");
		}

		sleeps();
		clickOKButton();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation de la contrainte UNIQUE KEY")|driver.getPageSource().contains("Violation of UNIQUE KEY constraint")) {
			createTest.log(Status.PASS,"The duplicate code is restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code is not restored");
		}

	}@Test(groups = {"Dosagegroups"},priority=346,enabled=true)
	public void verifyingTheFilterDosagegroups() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

	//*************************************************Exam/Labs/Care (DCI mode)*****************************************************************//

	@Test(groups = {"ExamDCImode"},priority=347,enabled=true)
	public void navigateToExam() throws InterruptedException, AWTException {

		String pageNameEnglish= "Exam/Labs/Care (DCI mode)";
		String pageNameFrench = "Examen/Labos/Soins (mode DCI)";

		navigateToMedicationTablesAndKardexMenu();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Exam/Labs/Care (DCI mode)')] | //div[contains(text(),' Examen/Labos/Soins (mode DCI)')]"))).click();
		createTest=reports.createTest("Validating the functionaltiy of the Exam/Labs/Care (DCI mode) screen");
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
	}
	@Test(groups = {"ExamDCImode"},priority=348,enabled=true)
	public void creatingNewRecordExam() throws InterruptedException, AWTException {


		clickCreateButton();

		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		clickSaveButton();

		if(driver.getPageSource().contains("Please choose within: Discharge planning, Exam, Lab or Care.")|driver.getPageSource().contains("Veuillez effectuer votre choix entre: Planif de congé, Examen, Labo ou Soins.")) {
			createTest.log(Status.PASS,"The application throws an error when we try to save the record without selecting Discharge planning, Exam, Lab or Care");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we try to save the record without selecting Discharge planning, Exam, Lab or Care");
		}

		filter(validCode, 1);
		driver.findElement(By.xpath("//span[text()='"+validCode+"']")).click();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(DescEN);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		clickSaveButton();


		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'TRIAGE access')] | (//span[contains(text(),'s TRIAGE')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'Planif de congé')])[1] | (//span[contains(text(),'Discharge planning')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'Examen')])[1] | (//span[contains(text(),'Exam')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'Lab')])[1] | (//span[contains(text(),'Labo')])[1]"))).click();

		if(driver.getPageSource().contains(validCode))
		{
			createTest.log(Status.PASS,"The radio buttons are working fine");
		}else {

			createTest.log(Status.FAIL,"The radio buttons are not working fine");
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'Soins')])[1] | (//span[contains(text(),'Care')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'Hospit. care')])[1] | (//span[contains(text(),'hospit')])[1]"))).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'Congé')])[1] | (//span[contains(text(),'Leave')])[1]"))).click();
		WebElement all = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//span[contains(text(),'All')])[1] | (//span[contains(text(),'Tous')])[1]")));
		all.click();

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}


	}@Test(groups = {"ExamDCImode"},priority=349,enabled=true)
	public void VerifyingTheErrorsExam() throws InterruptedException, AWTException {

		clickCreateButton();

		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(validCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(DescEN);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		clickSaveButton();

		if(driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The application throws an error when duplicate code is entered");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when duplicate code is entered");
		}
		sleeps();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("Violation of PRIMARY KEY constraint")|driver.getPageSource().contains("Violation de la contrainte PRIMARY KEY")) {
			createTest.log(Status.PASS,"The duplicate code has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate code has not been restored");
		}

	}
	@Test(groups = {"ExamDCImode"},priority=350,enabled=true)
	public void validatingTheError() throws InterruptedException, AWTException {

		clickCreateButton();

		WebElement Nocoulmmn = driver.findElement(By.xpath("//div[contains(@class,'ag-cell-focus ag-cell-inline-editing')]//input"));
		Nocoulmmn.sendKeys(alphanumericCode);
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing ag-cell-focus')]//input")).sendKeys(DescEN);
		sleeps();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		pressAndReleaseTabKey();
		pressAndReleaseSpaceKey();
		clickSaveButton();

		if(driver.getPageSource().contains("A code cannot be in more than one category. Please choose within: Discharge planning, Exam, Lab or Care")|driver.getPageSource().contains("Un code ne peut être dans plus d'une catégorie. Veuillez effectuer votre choix entre: Planif de congé, Examen, Labo ou Soins")) {
			createTest.log(Status.PASS,"The application throws an error when we select more than one category");
			clickOKButton();
		}else {
			createTest.log(Status.FAIL,"The application is not throws an error when we select more than one category");
		}
		sleeps();
		clickRestoreButton();
		clickSaveButton();

		if(!driver.getPageSource().contains("A code cannot be in more than one category. Please choose within: Discharge planning, Exam, Lab or Care")|driver.getPageSource().contains("Un code ne peut être dans plus d'une catégorie. Veuillez effectuer votre choix entre: Planif de congé, Examen, Labo ou Soins")) {
			createTest.log(Status.PASS,"The duplicate category has been restored");
		}else {
			createTest.log(Status.FAIL,"The duplicate category has not been restored");
		}

	}
	@Test(groups = {"ExamDCImode"},priority=351,enabled=true)
	public void verifyingTheFilterExam() throws InterruptedException, AWTException {

		filter(DescFN, 2);

		driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}
	
		//*************************************************Forms print manager*****************************************************************//

	@Test(groups = {"FormPrintManager"},priority=352,enabled=true)
	public void navigateToFormPrintManagerScreen() throws InterruptedException, AWTException {

		String pageNameEnglish= "Forms print manager";
		String pageNameFrench = "Gestionnaire d'impression des formulaires";

		navigateToManagementMenu();
		navigatePrintManagement();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Forms print manager')] | //div[contains(text(),'des formulaires')] "))).click();
		createTest=reports.createTest("Validating the functionaltiy of the Forms print manager screen");
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les données?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickNoButton();
		}
		verifyBreadCrumb(pageNameEnglish, pageNameFrench);

		WebElement restoreBtn = driver.findElement(By.xpath("//button[contains(text(),'Restore')] | //button[contains(text(),'Rétablir')]"));
		WebElement saveBtn = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));

		if(verifyButtonDisabled(restoreBtn, "Restore Button")) {
			createTest.log(Status.PASS,"Restore button is disabled by default");

		} else{
			createTest.log(Status.FAIL,"Restore button is not disabled by default");
		}

		if(verifyButtonDisabled(saveBtn, "Save Button"))
		{
			createTest.log(Status.PASS,"Save button is disabled by default");

		}else{
			createTest.log(Status.FAIL,"Save button is not disabled by default");
		}
		
		filter(DescFN, 1);

		if(driver.getPageSource().contains(DescFN))
		{
			driver.findElement(By.xpath("//span[text()='"+DescFN+"']")).click();
			clickDeleteButton();
			clickSaveButton();
		}
		
	}
	@Test(groups = {"FormPrintManager"},priority=353,enabled=true)
	public void creatingNewRecordFormPrintManager() throws InterruptedException, AWTException {

		clickCreateButton();
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescFN);
		pressAndReleaseTabKey();
		driver.findElement(By.xpath("//div[contains(@class,'cell-wrap-text ag-cell-inline-editing ag-cell-focus')]//child::input")).sendKeys(DescEN);
		clickSaveButton();

		filter(DescFN, 1);

		sleeps();
		if(driver.getPageSource().contains(DescFN)) {
			createTest.log(Status.PASS,"New record is created");
		}else {
			createTest.log(Status.FAIL,"New record is not created");
		}

	}@Test(groups = {"FormPrintManager"},priority=354,enabled=true)
	public void verifyingTheFilterFormPrintManager() throws InterruptedException, AWTException {

		activeRadiobtn(DescFN);
		nonActiveRadiobtn(DescFN);
		reservedRadiobtn(DescFN);
		AllRadiobtn(DescFN);
		sleeps();
		clickDeleteButton();
		verifyingTheConfirmationMessage();
		clickSaveButton();

		if((driver.getPageSource().contains(DescFN))) {
			createTest.log(Status.FAIL,"The Delete & Save buttons are not working fine");
		}else {
			createTest.log(Status.PASS,"The Delete & Save buttons are working fine");
		}
	}

}










