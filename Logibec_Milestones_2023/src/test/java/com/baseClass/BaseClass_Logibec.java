package com.baseClass;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass_Logibec {

	public static WebDriver driver;
	public static ExtentReports reports;
	public static ExtentHtmlReporter html;
	public static ExtentTest createTest;
	public static WebDriverWait wait;
	public static Actions action;
	public static Robot robot;




	public void clickHomeButton() {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'ACCUEIL')] | //a[contains(text(),'HOME')]"))).click();

		if(driver.getPageSource().contains("Do you want to save the data?")|driver.getPageSource().contains("Voulez-vous enregistrer les donn�es?")) {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),' Oui ')] | //span[contains(text(),' Yes ')]"))).click();
		}

	}
	public void navigateToDoctor() throws InterruptedException {
		navigateToManagementMenu();
		driver.findElement(By.xpath("//div[contains(text(),' Doctors')] | //div[contains(text(),' M�decins')]")).click();
	}
	public void tabKey() throws AWTException {
		robot=new Robot();
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
	}
	public void spaceKey() throws AWTException {
		robot=new Robot();
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_SPACE);
	}
	public void verifyReadOnly(WebElement locator, String attribute) {
		WebElement element = locator;
		if(element.getAttribute(attribute).equalsIgnoreCase("true")) {
			createTest.log(Status.PASS,element+"is readonly");
		}
		createTest.log(Status.FAIL,element+"is Editable");
	}

	public void clickCreateSubWindow() throws InterruptedException {
		sleeps();
		WebElement createSub = driver.findElement(By.xpath("(//button[contains(text(),'Cr�er')] | //button[contains(text(),'Create')])[2]"));
		createSub.click();
	}
	public void clickOKSubWindow() throws InterruptedException {
		sleeps();
		WebElement OKSub = driver.findElement(By.xpath("//div[@class='col-sm-12 p-0 pt-2 mt-2 d-flex justify-content-end']//button[contains(text(),'OK')]"));
		OKSub.click();
	}
	public void clickDeleteSubWindow() throws InterruptedException {
		sleeps();
		WebElement deleteSub = driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]"));
		deleteSub.click();
	}
	public void clickRestoreSubWindow() throws InterruptedException {
		sleeps();
		WebElement deleteSub = driver.findElement(By.xpath("(//button[contains(text(),'R�tablir')] | //button[contains(text(),'Restore')])[2]"));
		deleteSub.click();
	}
	public void pressDownArrow() throws AWTException {
		robot=new Robot();
		robot.keyPress(KeyEvent.VK_KP_DOWN);
		robot.keyRelease(KeyEvent.VK_KP_DOWN);
	}
	public void navigateToLogout() throws InterruptedException {

		WebElement createSub = driver.findElement(By.xpath("//div[contains(text(),' Fermeture de la session (d�connexion)')] | //div[contains(text(),' Logout(disconnection)')]"));
		createSub.click();

	}
	public void PressAndReleaseKeyR() throws AWTException {

		robot=new Robot();
		robot.keyPress(KeyEvent.VK_R);
		robot.keyRelease(KeyEvent.VK_R);
	}
	public void navigateToallergiesPMH() throws InterruptedException {

		sleeps();
		WebElement allergiesPMH = driver.findElement(By.xpath("//div[contains(text(),' Allergies & ant�c�dents')] | //div[contains(text(),' Allergies/P.M.H.')]"));
		//		WebElement allergiesPMH = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Allergies & ant�c�dents')] | //div[contains(text(),' Allergies/P.M.H.')]")));
		sleeps();
		allergiesPMH.click();
	}
	public void activeRadiobtn(String Code) throws InterruptedException, AWTException {


		try {
			sleeps();
			sleeps();
			driver.findElement(By.xpath("(//span[@class='mat-radio-outer-circle'])[2]")).click();
			clickActiveFilter();

		}catch(ElementNotInteractableException e) {
			driver.findElement(By.xpath("(//span[@class='mat-radio-inner-circle'])[2]")).click();
			clickActiveFilter();
		}

		if((driver.getPageSource().contains(Code))) {
			createTest.log(Status.PASS,"The Active radio button is working fine");
		}else {
			createTest.log(Status.FAIL,"The Active radio button is not working fine (or) The Active column checkbox is not gets selected by default");
		}
	}


	public void nonActiveRadiobtn(String Code) throws InterruptedException, AWTException {

		try {
			sleeps();
			sleeps();
			driver.findElement(By.xpath("(//span[@class='mat-radio-outer-circle'])[3]")).click();
			clickNonActiveFilter();

		}catch(ElementNotInteractableException e) {
			driver.findElement(By.xpath("(//span[@class='mat-radio-inner-circle'])[3]")).click();
			clickNonActiveFilter();
		}

		if((driver.getPageSource().contains(Code))) {
			createTest.log(Status.FAIL,"The Non-Active radio button is not working fine (or) The Active column checkbox is not gets selected by default");
		}else {
			createTest.log(Status.PASS,"The Non-Active radio button is working fine ");
		}

	}

	public void reservedRadiobtn(String Code) throws InterruptedException, AWTException {

		try {
			sleeps();
			driver.findElement(By.xpath("(//span[@class='mat-radio-outer-circle'])[4]")).click();
			clickReservedFilter();

		}catch(ElementNotInteractableException e) {
			driver.findElement(By.xpath("(//span[@class='mat-radio-inner-circle'])[4]")).click();
			clickReservedFilter();
		}

		if((driver.getPageSource().contains(Code))) {
			createTest.log(Status.FAIL,"The Reserved radio button is not working fine");
		}else {
			createTest.log(Status.PASS,"The Reserved radio button is working fine");
		}

	}
	public void AllRadiobtn(String Code) throws InterruptedException, AWTException {

		try {
			driver.findElement(By.xpath("(//span[@class='mat-radio-outer-circle'])[1]")).click();
			clickAllFilter();

		}catch(ElementNotInteractableException e) {
			driver.findElement(By.xpath("(//span[@class='mat-radio-inner-circle'])[1]")).click();
			clickAllFilter();
		}
	}

	public void navigateToVitalSigns() {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Vital signs - Qualitative')] | //div[contains(text(),' Qualitatif - Signes vitaux')]"))).click();

	}
	public void navigateToScreeningTools() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Questionnaires de d�pistage')] | //div[contains(text(),'Screening tools')]"))).click();

	}
	public void navigatePrintManagement() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Print management')] | //div[contains(text(),'impression')]"))).click();

	}
	public void verifyingTheConfirmationMessage() throws InterruptedException {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'ACCUEIL')] | //a[contains(text(),'HOME')]"))).click();
		sleeps();

		if(driver.getPageSource().contains("Do you want to save the data?")|driver.getPageSource().contains("Voulez-vous enregistrer les donn�es?")) {

			createTest.log(Status.PASS,"The application throws an error when we leave without saving the changes");
		}else {
			createTest.log(Status.FAIL,"The application not throws an error when we leave without saving the changes");
		}

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='btn2']"))).click();
		verifyClearfiltrebutton();
	}

	public void clickCancel() {

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@id='btn2']"))).click();
	}

	public void navigateToScreeningToolPrint() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),' Screening tools')] | //div[contains(text(),'Questionnaires de d�pistage')]"))).click();
	}
	//	}
	public void timeOutMessage() throws InterruptedException {
		if((driver.getPageSource().contains("Votre session demeure inactive 1 min et se termine automatiquement.")) | (driver.getPageSource().contains("All doctors must have a domain."))) {
			clickYesButton();
		}

	}
	public void UrlLaunch() throws IOException {
		FileReader file= new FileReader(System.getProperty("user.dir")+"\\src\\test\\resources\\config.properties");
		Properties p=new Properties();
		p.load(file);
		
		driver.get(p.getProperty("Url"));

	}
	@BeforeClass(alwaysRun=true)
	public void reportsGeneration() {
		reports=new ExtentReports();
		html= new ExtentHtmlReporter("Extentreport.html");
		reports.attachReporter(html);
	}

	//@AfterClass(alwaysRun=true)
	public void tearDown()
	{
		driver.quit();
	}
	
	public void testCreation(String a) {
		createTest= reports.createTest(a);
	}
	public void chromeBrowserConfig() {
		WebDriverManager.chromedriver().setup();
		driver= new ChromeDriver();
	}
	public void firefoxBrowserConfig() {
		WebDriverManager.firefoxdriver().setup();
		driver= new FirefoxDriver();
	}
	public void edgeBrowserConfig() {
		WebDriverManager.edgedriver().setup();
		driver= new EdgeDriver();
	}
	public void windowMaximize() {
		driver.manage().window().maximize();
	}
	public void sleeps() throws InterruptedException {
		Thread.sleep(2000);
	}
	public void applicationClosing() throws AWTException {
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_F4);
		robot.keyRelease(KeyEvent.VK_F4);
		robot.keyRelease(KeyEvent.VK_ALT);
	}
	public void hidingAddressbar() throws AWTException {
		Robot a=new Robot();
		a.keyPress(KeyEvent.VK_F11);
		a.keyRelease(KeyEvent.VK_F11);
	}
	public void LaunchApplication() throws InterruptedException, AWTException, IOException
	{
		//chromeBrowserConfig();
		UrlLaunch();
		windowMaximize();
		sleeps();
		applicationLoginCredentials();
		System.out.println("After login passed");
		System.out.println("Login passed");
		sleeps();
		if(driver.getPageSource().contains("Changer le r�le ou l'unit�")| driver.getPageSource().contains("Choice of user role in system"))
		{
			driver.findElement(By.xpath("(//button[text()='MEDIAMED'])[2]")).click();
		}
		

	}
	public void LaunchApplication1() throws InterruptedException, AWTException, IOException
	{
		chromeBrowserConfig();
		UrlLaunch();
		windowMaximize();
		sleeps();
		applicationLoginCredentials();
		System.out.println("After login passed");
		System.out.println("Login passed");
		sleeps();


	}

	public void zoomOutScreen() throws AWTException
	{
		Robot robot= new Robot();
		for (int i = 0; i < 4; i++) {
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		}
	}
	public void launchClinicalRoom() throws InterruptedException, AWTException, IOException
	{
		//chromeBrowserConfig();
		UrlLaunch();		

		windowMaximize();
		sleeps();
		System.out.println("Before Login class");
		applicationLoginCredentials();
		System.out.println("After login passed");
		System.out.println("Login passed");
		sleeps();

		UrlLaunch();
	}
	public void takingScreenShot() throws AWTException {
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_WINDOWS);
		robot.keyPress(KeyEvent.VK_PRINTSCREEN);
		robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
		robot.keyRelease(KeyEvent.VK_WINDOWS);
	}
	
	@AfterClass(alwaysRun=true)
	public void afterClass() {
		reports.flush();
	}
	public void clickingMainMenu() {//img[contains(@src,'portal.svg')]
		//WebElement mainMenu = driver.findElement(By.xpath("(//mat-icon[contains(@class,'mat-icon notranslate material-icons mat-icon-no-color')])[1]"));
		WebElement mainMenu = driver.findElement(By.xpath("//img[contains(@src,'portal.svg')]"));
		
		mainMenu.click();
	}
	public void logout() throws InterruptedException, AWTException
	{
		clickingMainMenu();
		navigateToPortalMenu();

		driver.findElement(By.xpath("//div[text()=' Logout(disconnection)'] | //div[text()=' Fermeture de la session (d�connexion)']")).click();
		sleeps();
		if((driver.getPageSource().contains("Voulez-vous enregistrer les donn�es?"))||(driver.getPageSource().contains("Do you want to save the data?")) )
		{
			clickYesButton();

		}
		Thread.sleep(15000);

		if((driver.getPageSource().contains("Tous les utilisateurs doivent avoir une unit�. Voici la liste des utilisateurs sans unit�:"))||(driver.getPageSource().contains("All users must have a unit. Here is the list of users without a unit:")) )
			//		if((driver.findElement(By.xpath("(//h2[contains(text(),'Tous les utilisateurs doivent avoir une unit�. Voici la liste des utilisateurs sans unit�:')] | //h2[contains(text(),'"
			//				+ "All users must have a unit. Here is the list of users without a unit:')])[1]")).isDisplayed()))
		{
			System.out.println("before click");
			WebElement OkButton = driver.findElement(By.xpath("(//button//span[contains(text(),'OK')])[1]"));
			OkButton.click();
			System.out.println("after click");
			//pressAndReleaseEnterKey();
			sleeps();
		}
		System.out.println("out of tous");
		sleeps();
	}
	public void clickCreateButton() throws InterruptedException {
		sleeps();
		WebElement createButton = driver.findElement(By.xpath("//button[contains(text(),'Cr�er')] | //button[contains(text(),'Create')]"));
		createButton.click();
		sleeps();
	}
	public void verifyColumnHeader(int columnIndex, String columnNameFrench,String columnNameEnglish)
	{
		String columnName = driver.findElement(By.xpath("(//div[contains(@class,'ag-header-cell')]//span[@class='ag-header-cell-text'])["+columnIndex+"]")).getText();
		{
			if((columnName.equals(columnNameFrench))||(columnName.equals(columnNameEnglish)))
			{
				createTest.log(Status.PASS,"Column Header of index "+columnIndex+" is correct : "+columnNameEnglish+" | "+columnNameFrench);
			}
			else
			{
				System.out.println(columnName);
				System.out.println(columnNameFrench);
				System.out.println(columnNameEnglish);
				createTest.log(Status.FAIL,"Column Header of index "+columnIndex+" is  not correct ");


			}
		}

	}
	public void pressAndReleaseTabKey() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		sleeps();
	}

	public void pressAndReleaseEnterKey() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		sleeps();
	}

	public void pressAndReleaseControlAKey() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_CONTROL);
	}

	public void pressAndReleaseBackSPaceKey() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_BACK_SPACE);

		robot.keyRelease(KeyEvent.VK_BACK_SPACE);
	}
	public void pressAndReleaseRKey() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();

		robot.keyPress(KeyEvent.VK_R);
		robot.keyRelease(KeyEvent.VK_R);

	}
	public void pressAndRelease1Key() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();

		robot.keyPress(KeyEvent.VK_1);
		robot.keyRelease(KeyEvent.VK_1);

	}
	public void pressAndReleaseAKey() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();

		robot.keyPress(KeyEvent.VK_A);
		robot.keyRelease(KeyEvent.VK_A);

	}
	public void pressAndReleaseDeleteKey() throws AWTException, InterruptedException
	{
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_DELETE);

		robot.keyRelease(KeyEvent.VK_DELETE);

	}
	public void navigateToPortalMenu() throws InterruptedException
	{	
		sleeps();
		sleeps();
		//	WebElement portal1 = driver.findElement(By.xpath("(//mat-drawer[@id='sidebar']//descendant::span[@class='mat-button-wrapper']//span[@class='sidenav-text ms-2'])[1]"));
		//		WebElement portal1 = driver.findElement(By.xpath("//div//span[contains(text(),'Portal')] | //div//span[contains(text(),'Portail')]"));
		//		portal1.click();
		Actions action =new Actions(driver);
		action.moveToElement(driver.findElement(By.xpath("(//mat-drawer[@id='sidebar']//descendant::span[@class='mat-button-wrapper'])[1]"))).build().perform();
		sleeps();
		action.moveToElement(driver.findElement(By.xpath("//div[contains(text(),'Portail')] | //div[contains(text(),'Portal')]"))).build().perform();

		System.out.println("clicked portal..");
	}	

	public void clickHome() throws InterruptedException
	{
		WebElement homeLink = driver.findElement(By.xpath("//li//a"));
		homeLink.click();
		sleeps();
				
	}

	public void verifyBreadCrumb(String pageNameEnglish, String pageNameFrench)
	{
		String homeLink = driver.findElement(By.xpath("//li//a")).getText();
		System.out.println(homeLink);
		String pageName = driver.findElement(By.xpath("//li[@class='breadcrumb-item active']")).getText();
		if((homeLink.equals("HOME"))||(homeLink.equals("ACCUEIL")))
		{	
			if((pageName.equals(pageNameEnglish)) || (pageName.equals(pageNameFrench)))
			{
				createTest.log(Status.PASS,"Home and Page Name is displaying in breadcrumb - "+pageNameEnglish+" | "+ pageNameFrench);
			}
			else
			{
				createTest.log(Status.FAIL,"Wrong page name displaying in breadcrumb");

			}
		}
		else
		{	
			createTest.log(Status.FAIL,"Home not displaying in breadcrumb");

		}
	}
	public void pressAndReleaseSpaceKey() throws AWTException
	{	
		Robot robot=new Robot();
		robot.keyPress(KeyEvent.VK_SPACE);
		robot.keyRelease(KeyEvent.VK_SPACE);

	}
	public void pressAndRelease2Key() throws AWTException
	{	
		Robot robot=new Robot();
		robot.delay(2000);
		robot.keyPress(KeyEvent.VK_2);
		robot.keyRelease(KeyEvent.VK_2);

	}
	public void pressAndRelease7Key() throws AWTException
	{	
		Robot robot=new Robot();
		robot.delay(2000);
		robot.keyPress(KeyEvent.VK_7);
		robot.keyRelease(KeyEvent.VK_7);

	}
	public void pressAndRelease9Key() throws AWTException
	{	
		Robot robot=new Robot();
		robot.delay(2000);
		robot.keyPress(KeyEvent.VK_9);
		robot.keyRelease(KeyEvent.VK_9);

	}
	public void pressAndRelease0Key() throws AWTException
	{	
		Robot robot=new Robot();
		robot.delay(2000);
		robot.keyPress(KeyEvent.VK_0);
		robot.keyRelease(KeyEvent.VK_0);

	}
	public void pressAndRelease5Key() throws AWTException
	{	
		Robot robot=new Robot();
		robot.delay(2000);
		robot.keyPress(KeyEvent.VK_5);
		robot.keyRelease(KeyEvent.VK_5);

	}
	public void pressAndRelease8Key() throws AWTException
	{	
		Robot robot=new Robot();
		robot.delay(2000);
		robot.keyPress(KeyEvent.VK_8);
		robot.keyRelease(KeyEvent.VK_8);

	}
	public  void clickSaveAndContinueButton() throws InterruptedException
	{
		sleeps();
		sleeps();
		WebElement saveAndContinueButton = driver.findElement(By.xpath("//div[@class='col text-end']//button[@class='btn btn-primary me-2']"));
		saveAndContinueButton.click();
		sleeps();
	}
	public  void clickSaveAndCloseButton() throws InterruptedException
	{
		sleeps();
		sleeps();
		WebElement saveAndCloseButton = driver.findElement(By.xpath("//div[@class='col text-end']//button[@class='btn btn-primary']"));
		saveAndCloseButton.click();
		sleeps();
	}
	public  void clickSaveButton() throws InterruptedException
	{
		sleeps();
		WebElement saveButton = driver.findElement(By.xpath("//button[contains(text(),'Sauvegarder')] | //button[contains(text(),'Save')]"));
		saveButton.click();
		sleeps();
	}
	public  void clickDeleteButton() throws InterruptedException
	{
		sleeps();
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')]"));
		deleteButton.click();
		sleeps();
	}
	public  void clickRestoreButton() throws InterruptedException
	{
		sleeps();
		WebElement deleteButton = driver.findElement(By.xpath("//button[contains(text(),'R�tablir')] | //button[contains(text(),'Restore')]"));
		deleteButton.click();
		sleeps();
	}
	public boolean verifyDropDownValuePresent(String OptionNameFR, String OptionNameEN)
	{
		if(driver.findElement(By.xpath("//mat-option//span[contains(text(),'"+OptionNameFR+"')] | //mat-option//span[contains(text(),'"+OptionNameEN+"')]")).isDisplayed() )
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	public  void filter(String value, int  index) throws InterruptedException, AWTException
	{
		sleeps();

		WebElement noSort = driver.findElement(By.xpath("(//span[@class='ag-icon ag-icon-menu'])["+index+"]"));
		noSort.click();

		pressAndReleaseControlAKey();

		pressAndReleaseDeleteKey();


		WebElement contains = driver.findElement(By.xpath("(//div[contains(@class,'ag-wrapper ag-input-wrapper ag-text-field-input-wrapper')])[1]//input[contains(@ref,'eInput')]"));
		contains.sendKeys(value);
		sleeps();
		try {
			driver.findElement(By.xpath("//h1")).click();
		} catch (Exception e) {
			// TODO: handle exception
		}

		sleeps();
	}

	public void clickAllFilter() throws InterruptedException
	{
		//WebElement reservedFilter = driver.findElement(By.xpath("(//span[contains(text(),'R�serv�')] | //span[contains(text(),'Reserved')])//ancestor::mat-radio-button//span[@class='mat-radio-outer-circle']"));

		WebElement allFilter = driver.findElement(By.xpath("(//span[contains(text(),'Tous')] | //span[contains(text(),'All')])//ancestor::mat-radio-button//span[@class='mat-radio-container']"));
		allFilter.click();
		sleeps();
	}

	public void clickActiveFilter() throws InterruptedException
	{	

		//WebElement activeFilter = driver.findElement(By.xpath("(//span[contains(text(),'Actif')] | //span[contains(text(),'Active')])//ancestor::mat-radio-button//span[@class='mat-radio-outer-circle']"));
		WebElement activeFilter = driver.findElement(By.xpath("(//span[contains(text(),'Actif')] | //span[contains(text(),'Active')])//ancestor::mat-radio-button//span[@class='mat-radio-container']"));

		activeFilter.click();
		sleeps();
	}

	public void clickNonActiveFilter() throws InterruptedException
	{

		WebElement nonActiveFilter = driver.findElement(By.xpath("(//span[contains(text(),'Non actif')] | //span[contains(text(),'Non active')])//ancestor::mat-radio-button//span[@class='mat-radio-container']"));
		nonActiveFilter.click();
		sleeps();
	}

	public void clickReservedFilter() throws InterruptedException
	{

		WebElement reservedFilter = driver.findElement(By.xpath("(//span[contains(text(),'R�serv�')] | //span[contains(text(),'Reserved')])//ancestor::mat-radio-button//span[@class='mat-radio-container']"));
		//WebElement reservedFilter = driver.findElement(By.xpath("//input[@value='reserved']"));

		reservedFilter.click();
		sleeps();
	}


	public void applicationLoginCredentials() throws InterruptedException, AWTException, IOException {

		FileReader file= new FileReader(System.getProperty("user.dir")+"\\src\\test\\resources\\config.properties");
		Properties p=new Properties();
		p.load(file);



		{
			sleeps();
			if(driver.getPageSource().contains("La configuration de ce poste n'est pas faite pour l'affichage des statistiques, les statistiques g�n�rales seront affich�es"))
			{
				driver.findElement(By.id("btn1")).click();
				sleeps();
			}

			WebElement userName = driver.findElement(By.xpath("//input[@id='txtUsername']"));
			WebElement password = driver.findElement(By.xpath("//input[@id='txtPWD']"));
			userName.click();

			pressAndReleaseControlAKey();
			pressAndReleaseDeleteKey();
			userName.sendKeys((p.getProperty("Username")));
//			userName.sendKeys("Test1995");
			password.click();

			pressAndReleaseControlAKey();
			pressAndReleaseDeleteKey();
			
			password.sendKeys((p.getProperty("Password")));
//			password.sendKeys("macro04");
			driver.findElement(By.xpath("//button[@type='submit']")).click();
			sleeps();

		}

	}
		//	
		public void login(String userName, String password) throws InterruptedException, AWTException 
		{
			sleeps();
			sleeps();
			if(driver.getPageSource().contains("La configuration de ce poste n'est pas faite pour l'affichage des statistiques, les statistiques g�n�rales seront affich�es"))
			{
				driver.findElement(By.id("btn1")).click();
				sleeps();
			}
			WebElement un = driver.findElement(By.xpath("//input[@id='txtUsername']"));
			WebElement pwd = driver.findElement(By.xpath("//input[@id='txtPWD']"));

			un.click();
			pressAndReleaseControlAKey();
			pressAndReleaseDeleteKey();
			un.sendKeys(userName);
			pwd.click();
			pressAndReleaseControlAKey();
			pressAndReleaseDeleteKey();
			pwd.sendKeys(password);
			sleeps();
			driver.findElement(By.xpath("//button[@type='submit']")).click();
			sleeps();

		}
		public void navigateToManagementMenu() throws InterruptedException
		{
			clickingMainMenu();
			sleeps();

			navigateToPortalMenu();
			sleeps();

			//Move to Management menu
			WebElement management = driver.findElement(By.xpath("//div[text()=' Management'] | //div[text()=' Gestion']"));
			sleeps();
			Actions action=new Actions(driver);
			action.moveToElement(management).build().perform();
			sleeps();
		}

		public void navigateToLocalTablesMenu() throws InterruptedException
		{
			clickingMainMenu();
			sleeps();
			sleeps();
			//Move to portal menu
			navigateToPortalMenu();

			//Move to Management menu
			WebElement localTables = driver.findElement(By.xpath("//div[text()=' Local tables'] | //div[text()=' Tables locales']"));
			sleeps();
			Actions action=new Actions(driver);
			action.moveToElement(localTables).build().perform();
			sleeps();
		}

		public void navigateToMedicationTablesAndKardexMenu() throws InterruptedException
		{
			clickingMainMenu();
			sleeps();
			sleeps();
			//Move to portal menu
			navigateToPortalMenu();

			//Move to Management menu
			System.out.println("before clicking kardex");
			WebElement medicationTablesAndKardex = driver.findElement(By.xpath("//div[text()=' Tables li�es aux m�dicaments et DCI'] | //div[text()=' Medication tables and kardex']"));
			sleeps();
			Actions action=new Actions(driver);
			action.moveToElement(medicationTablesAndKardex).build().perform();
			sleeps();
		}


		public void navigateToPredefinedTables() throws InterruptedException
		{
			navigateToLocalTablesMenu();
			WebElement localTables = driver.findElement(By.xpath("//div[text()=' Predefined tables (Normative framework)'] | //div[text()=' Tables pr�d�finies (Cadre Normatif)']"));
			sleeps();
			Actions action=new Actions(driver);
			action.moveToElement(localTables).build().perform();
			sleeps();
		}
		
		public void navigateToPrintManagementMenu() throws InterruptedException
		{
			navigateToManagementMenu();
			
			//Move to Print Management menu
			moveToElementAndClick("//div[contains(text(),'Gestionnaire ')] | //div[contains(text(),' Print management')]");
			sleeps();
			
		}
		public void clickOKButton() throws InterruptedException
		{

			sleeps();
			WebElement OkButton = driver.findElement(By.xpath("(//button//span[contains(text(),'OK')])[1]"));
			OkButton.click();
		}
		public void clickCloseIcon() throws InterruptedException
		{
			sleeps();
			sleeps();
			WebElement closeIcon = driver.findElement(By.xpath("//button//span[contains(text(),'close')]"));
			closeIcon.click();
		}
		public void clickNoButton() throws InterruptedException
		{
			sleeps();
			sleeps();
			WebElement noButton = driver.findElement(By.xpath("//button//span[contains(text(),'Non ')] | //button//span[contains(text(),'No')]"));
			noButton.click();
		}
		public void clickYesButton() throws InterruptedException
		{
			sleeps();
			sleeps();
			WebElement OkButton = driver.findElement(By.xpath("//button//span[contains(text(),'Yes')] | //button//span[contains(text(),'Oui')]"));
			OkButton.click();
		}
		public void verifyHeader(String expectedEnglish, String expectedFrench)
		{
			WebElement locator=driver.findElement(By.xpath("//h1"));
			String actualText = locator.getText();

			System.out.println("Actual - "+actualText);
			System.out.println("Exp EN "+expectedEnglish);
			System.out.println("Exp FR "+expectedFrench);

			if (expectedFrench.equals(actualText))
			{
				createTest.log(Status.PASS,"Verified-header is "+expectedFrench);
			}
			else
			{

				if(expectedEnglish.equals(actualText)) 
				{
					createTest.log(Status.PASS,"Header is correct "+expectedEnglish);
				}
				else
				{
					createTest.log(Status.FAIL,"A wrong header is displayed. Expected : "+expectedEnglish+", Found : "+actualText);
				}
				createTest.log(Status.FAIL,"A wrong header is displayed. Expected : "+expectedFrench+", Found : "+actualText);
			}
		}

		public void verifyText(WebElement locator, String text,String expectedEnglish, String expectedFrench)
		{

			String actualText = locator.getText();

			System.out.println(actualText);
			System.out.println(expectedFrench);

			if (expectedFrench.equals(actualText))
			{
				createTest.log(Status.PASS,"Verified-"+text+" is correct- "+expectedFrench);


			}
			else
			{

				if(expectedEnglish.equals(actualText)) 
				{
					createTest.log(Status.PASS,text+" is correct -"+expectedEnglish);
				}
				else
				{
					createTest.log(Status.FAIL,"A wrong text is displayed");
				}
			}
		}

		public void switchToWindowIndex(int index) 
		{		
			Set<String> windows = driver.getWindowHandles();
			int totalWin = windows.size();
			System.out.println("No of windows opened " + totalWin);
			String winTitle = null;
			for (int i =0; i<= totalWin; i++) 
			{
				if (i == index) 
				{
					winTitle = windows.toArray()[i].toString();
					return;
				}
				System.out.println(windows.toArray()[i].toString());

			}
			driver.switchTo().window(winTitle);
			System.out.println("Switched to " + driver.getTitle());
		}


		/**
		 * Used to verify button enabled
		 * @param locator - locator to find element
		 * @param elementName - name of the element

		 */

		public boolean verifyButtonEnabled(WebElement locator, String elementName)
		{
			WebElement element = locator;
			if(element.isEnabled())
			{
				System.out.println(elementName+" is enabled");
				return true;
			}
			else
			{
				System.out.println(elementName+" is disabled");
				return false;
			}
		}

		/**
		 * Used to verify button disabled
		 * @param locator - locator to find element
		 * @param elementName - name of the element

		 */
		public boolean verifyButtonDisabled(WebElement locator, String elementName)
		{
			WebElement element = locator;
			if(element.isEnabled()==false)
			{
				System.out.println(elementName+" is disabled");
				return true;
			}
			else
			{
				System.out.println(elementName+" is enabled");
				return false;
			}
		}
		public void pressAndReleaseDownArrowKey() throws AWTException, InterruptedException {
			Robot a=new Robot();
			a.keyPress(KeyEvent.VK_DOWN);
			a.keyRelease(KeyEvent.VK_DOWN);
			sleeps();
		}
		public void verifyClearfiltrebutton() {
			
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(text(),'cer le filtre')] | //button[contains(text(),'ClearFilter')]"))).click();
			
			if(driver.getPageSource().contains("//span[@class='ag-header-icon ag-header-label-icon ag-filter-icon']")) {
				
				createTest.log(Status.FAIL,"Clear filter button is not working");
			}else {
				
				createTest.log(Status.PASS,"Clear filter button is working");
				
			}
		}
		
		/**
		 * function will verify All, Non Active, Active and Reserved filters in Screen
		 * @param code - Code with which record is created
		 **/
			public void checkFilterAndDeletion(String code) throws InterruptedException, AWTException
			{
				filter(code,1);
				clickAllFilter();
				if(driver.getPageSource().contains(code))
				{
					System.out.println("contains in ALl-1");
					clickActiveFilter();

					if(driver.getPageSource().contains(code))
					{
						System.out.println("contains in Active-1");
						sleeps();
						clickNonActiveFilter();
						if(!driver.getPageSource().contains(code))
						{
							System.out.println(" not contains in nonActive-1");
							sleeps();
							clickReservedFilter();

							if(!driver.getPageSource().contains(code))
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
				if(driver.getPageSource().contains(code))
				{
					System.out.println("contains in All-2");
					clickActiveFilter();
								
					if(!driver.getPageSource().contains(code))
					{
						System.out.println("not contains in Active-2");
						clickNonActiveFilter();
								
						if(driver.getPageSource().contains(code))
						{
							System.out.println(" contains in Non Active-2");
							clickReservedFilter();

							if(!driver.getPageSource().contains(code))
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
			}
			
			
			/**
			 * function will click clear Filter button in Screen
			 **/
			
			public void clickClearFilterButton() throws InterruptedException
			{
				driver.findElement(By.xpath("//button[contains(text(),'Effacer le filtre')] | //button[contains(text(),'ClearFilter')]")).click();
				sleeps();
			}
			
			/**
			 * function will verify clear Filter button succesfully working in Screen
			 * @param code - Code with which record is created
			 **/
			public void verifyClearFilter() throws InterruptedException
			{
				if(!driver.getPageSource().contains("(//div[contains(@class,'ag-cell-auto-height cell-wrap-text')]//span)[1]"))
				{	
					System.out.println("not containing");
					clickClearFilterButton();
					sleeps();
					driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-auto-height cell-wrap-text')]//span)[1]")).click();
					createTest.log(Status.PASS,"Clear Filter is succesfully working");
				
				}
			}
	
			/**
			 * function will delete the record if already exist in page in Screen
			 * @param code - Code with which record is created
			 **/
			public void deleteIfExit(String code) throws InterruptedException, AWTException
			{
				filter(code,1);
				if(driver.getPageSource().contains(code))
				{
					driver.findElement(By.xpath("//span[text()='"+code+"']")).click();
					sleeps();
					clickDeleteButton();
					clickSaveButton();
					sleeps();
				}
			}
			
			/**
			 * function will perform Edit
			 * @param code - Code with which record is created
			 * @param editCode - Code to be used for editing
			 **/
			public void checkEdit(String code, String editCode) throws AWTException, InterruptedException
			{
				driver.findElement(By.xpath("//span[text()='"+code+"']")).click();
				pressAndReleaseControlAKey();
				pressAndReleaseDeleteKey();
				driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(editCode);
				clickSaveButton();
				sleeps();
			
				filter(editCode,1);
				if(driver.getPageSource().contains(editCode))
				{
					createTest.log(Status.PASS,"Successfully edited new row ");
				}
				else
				{
					createTest.log(Status.FAIL,"Failed to edited new row ");
				}
				
				driver.findElement(By.xpath("//span[text()='"+editCode+"']")).click();
				pressAndReleaseControlAKey();
				pressAndReleaseDeleteKey();
				driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(code);
				clickSaveButton();
				sleeps();
				clickClearFilterButton();
			}
			
			public void checkRestore(String code, String restoreCode) throws InterruptedException, AWTException
			{
				filter(code,1);
				driver.findElement(By.xpath("//span[text()='"+code+"']")).click();
				pressAndReleaseControlAKey();
				pressAndReleaseDeleteKey();
				driver.findElement(By.xpath("//div[contains(@class,'ag-cell-inline-editing')]//input")).sendKeys(restoreCode);
				clickRestoreButton();
				sleeps();
			
				filter(restoreCode,1);
				if(driver.getPageSource().contains(restoreCode))
				{
					createTest.log(Status.FAIL,"Restore Button is not working fine");
				}
				else
				{
					filter(code,1);
					if(driver.getPageSource().contains(code))
					{
						createTest.log(Status.PASS,"Restore Button is working fine in ");
					}
					else
					{
						createTest.log(Status.FAIL,"Restore Button is not working fine ");

					}
				}
				clickSaveButton();
				sleeps();
			}
			
			public void verifyDuplicationNotAllowed() throws InterruptedException 
			{
				if((driver.getPageSource().contains("La commande d�extraction et/ou de sauvegarde de donn�es a �chou�."))|(driver.getPageSource().contains("Data extraction and/or saving command failed.")) )
				{	
					createTest.log(Status.PASS,"Verified duplicate is not allowed to create ");
					clickOKButton();
					clickRestoreButton();
					clickSaveButton();
				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify Verified duplicate is not allowed to create");
				}
				
			}
			
			public void verifyDuplicationAllowed() throws InterruptedException
			{
				if(!((driver.getPageSource().contains("La commande d�extraction et/ou de sauvegarde de donn�es a �chou�."))|(driver.getPageSource().contains("Data extraction and/or saving command failed."))) )
				{	
					createTest.log(Status.PASS,"Verified duplicate is allowed to create with same description ");
					clickDeleteButton();
					clickSaveButton();
				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify Verified duplicate is allowed to create");
				}
			}
			public void deleteRecordAndExitFromPage(String code) throws InterruptedException, AWTException
			{
				filter(code,1);
				WebElement record = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
				record.click();
				clickDeleteButton();
				clickHome();
				if((driver.getPageSource().contains("Voulez-vous enregistrer les donn�es?"))|(driver.getPageSource().contains("Do you want to save the data?")) )
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
				
				
			}
			
			public void deleteRecord(String Code) throws InterruptedException, AWTException
			{
				filter(Code,1);
				WebElement record = driver.findElement(By.xpath("(//div[contains(@class,'ag-cell-not-inline-editing ag-cell-auto-height')]//span//input)[1]"));
				record.click();
				clickDeleteButton();
				clickSaveButton();
				clickHome();
				Thread.sleep(4000);
			}
			
			public void verifyDeletion(String code) throws InterruptedException, AWTException
			{
				filter (code,1);
				if(!driver.getPageSource().contains(code))
				{
					createTest.log(Status.PASS,"Created record is successfully deleted!!!");
				}
				else
				{
					createTest.log(Status.FAIL,"Deletion is not working properly");
				}
			}
			
			public void verifyUnitButton() throws InterruptedException
			{
				driver.findElement(By.xpath("//button[text()='Unit�'] | //button[text()='Unit']")).click();
				sleeps();
				WebElement createButton1 = driver.findElement(By.xpath("(//button[contains(text(),'Cr�er')] | //button[contains(text(),'Create')])[2]"));
				createButton1.click();
				sleeps();

				if((driver.getPageSource().contains("Choix de l'unit�")) | (driver.getPageSource().contains("Choice of unit")) )
				{	
					createTest.log(Status.PASS,"Unit window is opened when Unit button was clicked" );
					createTest.log(Status.PASS,"Verified Choice of unit window is opened when clicked on Create Unit button");
				}
				else
				{
					createTest.log(Status.FAIL,"Failed to verify Verify Choice of unit window is opened when clicked on Create Unit button");
				}

				driver.findElement(By.xpath("//span[text()='urg']")).click();
				driver.findElement(By.xpath("//button[contains(text(),'OK')]")).click();
				//Thread.sleep(7000);

				clickSaveAndCloseButton();
				Thread.sleep(4000);

				driver.findElement(By.xpath("//button[text()='Unit�'] | //button[text()='Unit']")).click();
				sleeps();

				if(driver.findElement(By.xpath("//span[text()='urg']")).isDisplayed())
				{
					createTest.log(Status.PASS,"Units selected are successfully displaying under protocols");
				}
				else
				{
					createTest.log(Status.FAIL,"Unit is not successfully added to protocol");
				}
				//delete unit
				
				driver.findElement(By.xpath("//span[text()='urg']")).click();
				driver.findElement(By.xpath("(//button[contains(text(),'Supprimer')] | //button[contains(text(),'Delete')])[2]")).click();
				clickSaveAndContinueButton();
				sleeps();

				if(!driver.getPageSource().contains("Urgence"))
				{
					createTest.log(Status.PASS,"Unit is succesfully deleted  ");

				}
				else
				{
					createTest.log(Status.FAIL,"Unit is not  deleted i" );

				}


				sleeps();
				clickCloseIcon();
				sleeps();
				if((driver.getPageSource().contains("Voulez-vous enregistrer les donn�es?")) | (driver.getPageSource().contains("Do you want to save the data?")) )
				{
					clickYesButton();
					sleeps();
					createTest.log(Status.PASS,"Confirmation for saving data is shown when user tried switching to click close without saving from Choice of Unit window");
				}
			}
			
			public void moveToElementAndClick(String xpath)
			{
				Actions a = new Actions(driver);
				WebElement element =driver.findElement(By.xpath(""+xpath+""));
				a.moveToElement(element).build().perform();
				element.click();
			}
			
			public void navigateTOMedicationTablesAndKardexScreen() throws InterruptedException {
				
				navigateToPortalMenu();
				wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'dicaments et DCI')] | //div[contains(text(),' Medication tables and kardex')]"))).click();
				
				

			}

		}









