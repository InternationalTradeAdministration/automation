package tests;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.holdSeconds;
import static GuiLibs.GuiTools.testCaseStatus;
import static ReportLibs.ReportTools.printLog;
import static XmlLibs.XmlTools.buildTestNgFromDataPool;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.testng.TestNG;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import GuiLibs.GuiTools;
import InitLibs.InitTools;
import OfficeLibs.XlsxTools;
import ReportLibs.HtmlReport;
import libs.ADCVDLib;


public class TestOne {

	public static GuiTools guiTools;
	//static HtmlReport htmlReport;
	HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static ArrayList<LinkedHashMap<String, String>> dataPool;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	static ADCVDLib adcvdLib;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar cal = Calendar.getInstance();
	public boolean loginOn = false;
	
	public static void main(String[] args) throws Exception 
	{
		printLog("MainMethod()");
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		adcvdLib = new ADCVDLib();
		//TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"\\datapool\\Regession_TC.xlsx";
		dataPool  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Regression", "Active=TRUE");
		String testNgTemplate = InitTools.getInputDataFolder()+"\\template\\testng_template.xml";
		String testNgPath = InitTools.getRootFolder()+"\\testng.xml";
		//build
		buildTestNgFromDataPool(dataPool, testNgPath);
		suites.add(testNgPath);//path to xml..
		testng.setTestSuites(suites);
		testng.run();
	}
	
	@BeforeClass
	void beforeClass() throws Exception
	{
		printLog("Executing Before class");
		GuiTools.guiMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		mapConfInfos = guiTools.getConfigInfos();
		browserType = mapConfInfos.get("browser_type");
		String guiMapFilePath = InitTools.getInputDataFolder()+"\\script\\gui_map.xlsx";
		guiPool = XlsxTools.readXlsxSheetAndFilter(guiMapFilePath, "guiMap", "");
		guiMap = XlsxTools.readGuiMap(guiPool);
		HtmlReport.setTestSuiteName(mapConfInfos.get("project_name"));
		HtmlReport.setEnvironmentName(mapConfInfos.get("env_name"));
		HtmlReport.setTotalTcs(dataPool.size());
		java.util.Date date = new java.util.Date();
		suiteStartTime = new Timestamp(date.getTime());
	}
	@AfterClass
	void afterClass() throws Exception
	{
		printLog("Executing After class");
		java.util.Date date = new java.util.Date();
		endTime = new Timestamp(date.getTime());
		HtmlReport.setSuiteExecutionTime(endTime.getTime() - suiteStartTime.getTime());
		HtmlReport.buildHtmalReportForTestSuite();
		guiTools.closeBrowser();
		//stopRecording();
	}
	@BeforeMethod
	public static void beforeMethod() throws IOException
	{
		printLog("beforeMethod()");
		testCaseStatus = true;
		printLog("beforeMethod()");
		java.util.Date date = new java.util.Date();
		startTime = new Timestamp(date.getTime());
	}
	@AfterMethod
	public void afterMethod() throws Exception
	{
		printLog("afterMethod()");
		java.util.Date date = new java.util.Date();
		endTime = new Timestamp(date.getTime());
	    printLog(GuiTools.getTestCaseName());
	    HtmlReport.setTitle(GuiTools.getTestCaseName());
	    HtmlReport.setTcStatus(testCaseStatus);
	   // printLog(GuiTools.getExecutionTime());
	    HtmlReport.setTcExecutionTime(endTime.getTime() - startTime.getTime());
		//htmlReport.fillThetest();
	    HtmlReport.buildHtmlReportForTestCase();
	    HtmlReport.addTestCaseToSuite(GuiTools.getTestCaseName(), testCaseStatus);
	    HtmlReport.testCaseSteps.clear();
	    HtmlReport.setStepNumber(0);
	    if (guiTools.tearDown)
	    {
	    	//java.util.Date dateFail = new java.util.Date();
			endTime = new Timestamp(date.getTime());
			HtmlReport.setSuiteExecutionTime(endTime.getTime() - suiteStartTime.getTime());
	    	HtmlReport.buildHtmalReportForTestSuite();
			guiTools.closeBrowser();
		//	stopRecording();
			System.exit(0);
	    }
	}
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=1)
	void CVD_Align_With_AD() throws Exception
	{
		printLog("CVD_Align_With_AD");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_001");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		guiTools.openBrowser(browserType);
		loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		
		row.put("ADCVD_Case", "C-");
		row.put("ADCVD_Case_Type", "CVD");
		HtmlReport.addHtmlStepTitle("Create New CVD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		
		HtmlReport.addHtmlStepTitle("Align CVD Investigation To AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.checkCvdAlignedWithAd();
		//if(! testCaseStatus) guiTools.tearDown =true;
	}
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=2)
	void Litigation_Dates_Validation() throws Exception
	{
		printLog("Litigation_Dates_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_002");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		HtmlReport.addHtmlStepTitle("Create 'International Litigation' and Validate Dates","Title");
		row.put("Litigation_Type", "International Litigation");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewLitigation(row);
		testCaseStatus =testCaseStatus & ADCVDLib.validateLitigationFields(row);
		HtmlReport.addHtmlStepTitle("Create Remand Litigation and Validate Dates","Title");
		row.put("Litigation_Type", "Remand");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewLitigation(row);
		testCaseStatus =testCaseStatus & ADCVDLib.validateLitigationFields(row);
	}
	
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=3)
	void Self_Initiated_Petition_Investigation_Dates() throws Exception
	{
		printLog("Self_Initiated_Petition_Investigation_Dates");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_003");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(row);
		
		//-----------------------------------
		
		row.put("ADCVD_Case", "C-");
		row.put("ADCVD_Case_Type", "CVD");
		HtmlReport.addHtmlStepTitle("Create New CVD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		///if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(row);
	
	}
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=4)
	void Align_NSR_To_NR() throws Exception
	{
		LinkedHashMap<String, String> arDates, nsrDates;
		printLog("Align_NSR_To_NR");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_004");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewOrder(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		
		row.put("Segment_Type", "Administrative Review");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewSegment(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		arDates = ADCVDLib.readSegmentDates("Segment AR Dates");
		
		row.put("Segment_Type", "New Shipper Review");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewSegment(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		nsrDates = ADCVDLib.readSegmentDates("Segment NFR Dates Before align it to AR Segment");
		
		testCaseStatus =testCaseStatus & ADCVDLib.alignNsrToArAndValidate(arDates, nsrDates);
	}
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=5)
	void Petition_Status_Validation() throws Exception
	{
		LinkedHashMap<String, String> arDates, nsrDates;
		printLog("Petition_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_005");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		//HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		testCaseStatus = testCaseStatus & ADCVDLib.validatePetitionStatus(row);
	}
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=6)
	void Investigation_Status_Validation() throws Exception
	{
		LinkedHashMap<String, String> arDates, nsrDates;
		printLog("Investigation_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_006");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		//HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		testCaseStatus = testCaseStatus & ADCVDLib.validateInvestigationStatus(row);
	}

	/**
	 * This method is admin review status
	*/
	@Test(enabled = true, priority=7)
	void Admin_Review_Status_Validation() throws Exception
	{   
		LinkedHashMap<String, String> arDates, nsrDates;
		printLog("Admin_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_007");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		//HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewOrder(row);
		//if(! testCaseStatus) guiTools.tearDown =true;		
		row.put("Segment_Type", "Administrative Review");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewSegment(row);
		ADCVDLib.validateNewShipperReviewStatus_A(row);
	}
	
	
	/**
	 * This method is Expedited_Review_Status_Validation status
	*/
	@Test(enabled = true, priority=8)
	void Expedited_Review_Status_Validation() throws Exception
	{
		LinkedHashMap<String, String> arDates, nsrDates;
		printLog("Expedited_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_008");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		//HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title"); 
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewOrder(row);
		//if(! testCaseStatus) guiTools.tearDown =true;		
		row.put("Segment_Type", "Expedited Review");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewSegment(row);
		ADCVDLib.validateNewShipperReviewStatus_A(row);
	}
	/**
	 * This method is New_Shipper_Review_Status_Validation status
	*/
	@Test(enabled = true, priority=9)
	void New_Shipper_Review_Status_Validation() throws Exception
	{
		LinkedHashMap<String, String> arDates, nsrDates;
		printLog("New_Shipper_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_009");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		//HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewOrder(row);
		//if(! testCaseStatus) guiTools.tearDown =true;		
		row.put("Segment_Type", "New Shipper Review");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewSegment(row);
		ADCVDLib.validateNewShipperReviewStatus_A(row);
	}
	
	/**
	 * This method is New_Shipper_Review_Status_Validation status
	*/
	@Test(enabled = true, priority=10)
	void Changed_Circumstance_Review_Status_Validation() throws Exception
	{
		LinkedHashMap<String, String> arDates, nsrDates;
		printLog("Changed_Circumstance_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_010");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, user, password);
		}
		holdSeconds(2);
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		//HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewCase(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus & ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus =testCaseStatus & ADCVDLib.createNewOrder(row);
		//if(! testCaseStatus) guiTools.tearDown =true;		
		row.put("Segment_Type", "Changed Circumstances Review");
		testCaseStatus =testCaseStatus & ADCVDLib.createNewSegment(row);
		ADCVDLib.validateChangedCircumstanceReviewStatus(row);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=1)
	void Create_Adcvd_Case() throws Exception
	{
		printLog("Create_Adcvd_Case");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_001");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		guiTools.openBrowser(browserType);
		ADCVDLib.loginToAdCvd(url, user, password);
		holdSeconds(2);
	}
	
	/**
	 * This method is for ADCVD Petition creation and validation
	*/
	@Test(enabled = true, priority=2)
	void Create_And_Validate_Petition() throws Exception
	{
		printLog("Create_And_Validate_Petition");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_002");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		
	}
	
	/**
	 * This method is for ADCVD Investigation creation and validation
	*/
	@Test(enabled = true, priority=3)
	void Create_And_Validate_Investigation() throws Exception
	{
		printLog("Create_And_Validate_Investigation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_003");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewInvestigation(row);
		if(! testCaseStatus) guiTools.tearDown =true;
		testCaseStatus = testCaseStatus && ADCVDLib.validateInvestigationFields(row);
	}
	/**
	 * This method is for ADCVD order creation and validation
	*/
	@Test(enabled = true, priority=4)
	void Create_Order() throws Exception
	{
		printLog("Create_And_Validate_Investigation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_004");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewOrder(row);
		if(! testCaseStatus) guiTools.tearDown =true;
	}
	
	/**
	 * This method is for ADCVD segment(Administrative Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=5)
	void Create_Segment_Administrative_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 1");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_005");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewSegment(row);
		testCaseStatus = testCaseStatus && ADCVDLib.validateNewSegmentAdministrativeReview();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This method is for ADCVD segment(Anti Circumvention Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=6)
	void Create_Segment_Anti_Circumvention_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 2");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_006");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewSegment(row);
		testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentAntiCircumventionReview();
	}
	/**
	 * This method is for ADCVD segment(Changed Circumstances Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=7)
	void Create_Segment_Changed_Circumstances_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 3");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_007");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewSegment(row);
		testCaseStatus = testCaseStatus && ADCVDLib.validateNewSegmentChangedCircumstancesReview();
	}
	
	/**
	 * This method is for ADCVD segment(Expedited Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=8)
	void Create_Segment_Expedited_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 4");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_008");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewSegment(row);
		testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentExpeditedReview();
	}
	
	/**
	 * This method is for ADCVD segment(Shipper Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=9)
	void Create_Segment_New_Shipper_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 5");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_009");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewSegment(row);
		testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentNewShipperReview();
	}
	
	/**
	 * This method is for ADCVD segment(Scope Inquiry) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=10)
	void Create_Segment_Scope_Inquiry() throws Exception
	{
		printLog("Create_And_Validate_Segment - 6");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_010");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewSegment(row);
		testCaseStatus = testCaseStatus && ADCVDLib.validateNewSegmentNewScoprInquiry();
	}
	/**
	 * This method is for ADCVD segment(Sunset Inquiry) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=11)
	void Create_Segment_Sunset_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 7");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_011");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		testCaseStatus = ADCVDLib.createNewSegment(row);
		testCaseStatus = testCaseStatus && ADCVDLib.validateNewSegmentSunsetReview(row);
	}
	/**
	 * This method if for getting the current test case information
	*/
	public LinkedHashMap<String, String> getTestCaseInfo(ArrayList<LinkedHashMap<String, String>> dataPool, String tcTagName)
	{
		for(LinkedHashMap<String, String> map : dataPool)
		{
			if(tcTagName.equalsIgnoreCase(map.get("Test_Case_Tag")))
			{
				return map;
			}
		}
		return null;
	}
	/**
	 * This method if for merging data from all objects
	*/
	static ArrayList<LinkedHashMap<String, String>> mergeDataPools(ArrayList<LinkedHashMap<String, String>> dataPool1, 
			ArrayList<LinkedHashMap<String, String>> dataPool2, ArrayList<LinkedHashMap<String, String>> dataPool3, 
			ArrayList<LinkedHashMap<String, String>> dataPool4, ArrayList<LinkedHashMap<String, String>> dataPool5)
	{
		ArrayList<LinkedHashMap<String, String>> dataPoolMerged = new ArrayList<LinkedHashMap<String, String>>();
		for(LinkedHashMap<String, String> map: dataPool1)
		{
		dataPoolMerged.add(map);
		}
		for(LinkedHashMap<String, String> map: dataPool2)
		{
		dataPoolMerged.add(map);
		}
		for(LinkedHashMap<String, String> map: dataPool3)
		{
		dataPoolMerged.add(map);
		}
		for(LinkedHashMap<String, String> map: dataPool4)
		{
		dataPoolMerged.add(map);
		}
		for(LinkedHashMap<String, String> map: dataPool5)
		{
		dataPoolMerged.add(map);
		}
		return dataPoolMerged;
	}
}
