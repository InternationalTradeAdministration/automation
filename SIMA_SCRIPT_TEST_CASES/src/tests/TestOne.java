package tests;
import static GuiLibs.GuiTools.checkElementExists;
import static GuiLibs.GuiTools.clickElementJs;
import static GuiLibs.GuiTools.enterTextAndClear;
import static GuiLibs.GuiTools.enterTextFile;
import static GuiLibs.GuiTools.failTestCase;
import static GuiLibs.GuiTools.getElementAttribute;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.holdSeconds;
import static GuiLibs.GuiTools.replaceGui;
import static GuiLibs.GuiTools.scrollToElement;
import static GuiLibs.GuiTools.selectElementByText;
import static GuiLibs.GuiTools.testCaseStatus;
import static GuiLibs.GuiTools.updateHtmlReport;
import static ReportLibs.ReportTools.printLog;
import static XmlLibs.XmlTools.buildTestNgFromDataPool;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import org.testng.TestNG;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import GuiLibs.GuiTools;
import InitLibs.InitTools;
import OfficeLibs.XlsxTools;
import ReportLibs.HtmlReport;
import libs.SIMALib;
public class TestOne {

	public static GuiTools guiTools;
	HashMap<String, String> mapConfInfos;
	String browserType;
	String modifCreate;
	static XlsxTools xlsxTools;
	static ArrayList<LinkedHashMap<String, String>> scenarios, htsCodesUnitValue, allCountry, 
	naftaCountries, euCountries;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	static SIMALib sIMALib;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime, 
							uploadStartTime, uploadEndTime;
	public static Timestamp endTime;
	public static boolean logged = false;
	public static Calendar cal = Calendar.getInstance();
	public static void main(String[] args) throws Exception 
	{
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		sIMALib = new SIMALib();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList(); 
		String dataPoolPath = InitTools.getInputDataFolder()+"\\datapool\\Steel prodcuts.xlsx";
		scenarios = XlsxTools.readXlsxSheetInOrderAndFilter(
				InitTools.getInputDataFolder()+"\\datapool\\SIMA_TEST_CASES.xlsx", "Test Cases", "Active=TRUE");
		htsCodesUnitValue = XlsxTools.readXlsxSheetInOrderAndFilter(
				InitTools.getInputDataFolder()+"\\datapool\\Steel prodcuts.xlsx", "prodcuts_LN", "");
		allCountry = XlsxTools.readXlsxSheetInOrderAndFilter(
				InitTools.getInputDataFolder()+"\\datapool\\Countries.xlsx", "Countries", "");		
		naftaCountries= XlsxTools.readXlsxSheetInOrderAndFilter(
				InitTools.getInputDataFolder()+"\\datapool\\Countries.xlsx", "Countries", "CountryOfNAFTA=TRUE");
		euCountries= XlsxTools.readXlsxSheetInOrderAndFilter(
				InitTools.getInputDataFolder()+"\\datapool\\Countries.xlsx", "Countries", "CountryOfEU=TRUE");
		String testNgPath = InitTools.getRootFolder()+"\\testng.xml";
		//build
		buildTestNgFromDataPool(scenarios, testNgPath);
		suites.add(testNgPath);//path to xml..
		testng.setTestSuites(suites);
		testng.run();
	}
	
	@BeforeClass
	void beforeClass() throws IOException
	{
		printLog("Executing Before class");
		GuiTools.guiMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		mapConfInfos = guiTools.getConfigInfos();
		browserType = mapConfInfos.get("browser_type");
		String guiMapFilePath = InitTools.getInputDataFolder()+"\\script\\gui_map.xlsx";
		guiPool = XlsxTools.readXlsxSheetInOrderAndFilter(guiMapFilePath, "guiMap", "");
		guiMap = XlsxTools.readGuiMap(guiPool);
		HtmlReport.setTestSuiteName(mapConfInfos.get("project_name"));
		HtmlReport.setEnvironmentName(mapConfInfos.get("env_name") + " (License "+mapConfInfos.get("create_modify")+")");
		HtmlReport.setTotalTcs(scenarios.size());
		java.util.Date date = new java.util.Date();
		suiteStartTime = new Timestamp(date.getTime());
	}
	@AfterClass
	void afterClass() throws IOException
	{
		printLog("Executing After class");
		java.util.Date date = new java.util.Date();
		endTime = new Timestamp(date.getTime());
		HtmlReport.setSuiteExecutionTime(endTime.getTime() - suiteStartTime.getTime());
		HtmlReport.buildHtmalReportForTestSuite();
		guiTools.closeBrowser();
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
	public void afterMethod() throws IOException
	{
		printLog("afterMethod()");
		java.util.Date date = new java.util.Date();
		endTime = new Timestamp(date.getTime());
	    printLog(GuiTools.getTestCaseName());
	    HtmlReport.setTitle(GuiTools.getTestCaseName());
	    HtmlReport.setTcStatus(testCaseStatus);
	    HtmlReport.setTcExecutionTime(endTime.getTime() - startTime.getTime());
	    HtmlReport.buildHtmlReportForTestCase();
	    HtmlReport.addTestCaseToSuite(GuiTools.getTestCaseName(), testCaseStatus);
	    HtmlReport.testCaseSteps.clear();
	    HtmlReport.setStepNumber(0);
	    if (GuiTools.tearDown)
	    {
			endTime = new Timestamp(date.getTime());
			HtmlReport.setSuiteExecutionTime(endTime.getTime() - suiteStartTime.getTime());
	    	HtmlReport.buildHtmalReportForTestSuite();
			guiTools.closeBrowser();
			System.exit(0);
	    }
	}
	/*@DataProvider(name = "fetchingData")
	public static Object[][] fetchData() 
	{
		Object obj [][]= new  Object[scenarios.size()][2];
		int i = 0;
		for (HashMap<String, String> map : scenarios)
		{
			obj[i][0] = i;
			obj[i][1] =  map;
			i++;
		}
		return (Object[][]) obj;
	}*/
	/**
	 * This method is validation Unit value field
	*/
	 @Test()
	void VALIDATE_HTS_UNIT_VALUE() throws Exception
	{
		printLog("VALIDATE_HTS_UNIT_VALUE");
		LinkedHashMap<String, String> row = getTestCaseInfo(scenarios, 
				"Test_Case_Name", "VALIDATE_HTS_UNIT_VALUE");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Description"));
		printLog(GuiTools.getTestCaseName());
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");	
		modifCreate = mapConfInfos.get("create_modify");	
		if (!logged) 
		{
			guiTools.openBrowser(browserType);
			logged = SIMALib.loginToSima(url, user, password, modifCreate);
		}
		testCaseStatus = SIMALib.CheckHtsUnitValue(htsCodesUnitValue);
	}

	 /**
		 * This method is for validation of unusual countries of origin
		*/
		 @Test()
		void VALIDATE_UNUSUAL_COUNTRY_OF_ORIGIN() throws Exception
		{
			 printLog("VALIDATE_UNUSUAL_COUNTRY_OF_ORIGIN");
			LinkedHashMap<String, String> row = getTestCaseInfo(scenarios, 
					"Test_Case_Name", "VALIDATE_UNUSUAL_COUNTRY_OF_ORIGIN");
			GuiTools.setTestCaseName(row.get("Test_Case_Name"));
			GuiTools.setTestCaseDescription(row.get("Description"));
			printLog(GuiTools.getTestCaseName());
			String url = mapConfInfos.get("url");
			String user = mapConfInfos.get("user_name");
			String password = mapConfInfos.get("password");		
			if (!logged) 
			{
				guiTools.openBrowser(browserType);
				logged = SIMALib.loginToSima(url, user, password, modifCreate);
			}
			testCaseStatus = SIMALib.checkUnusualCountries(allCountry,
					"Country in which the product(s) were manufactured (Country of Orgin)");
		}
	 
	 
		 /**
			 * This method is for validation of unusual countries of export
		*/
		 @Test()
		void VALIDATE_UNUSUAL_COUNTRY_OF_EXPORT() throws Exception
		{
			 printLog("VALIDATE_UNUSUAL_COUNTRY_OF_EXPORT");
			LinkedHashMap<String, String> row = getTestCaseInfo(scenarios, 
					"Test_Case_Name", "VALIDATE_UNUSUAL_COUNTRY_OF_EXPORT");
			GuiTools.setTestCaseName(row.get("Test_Case_Name"));
			GuiTools.setTestCaseDescription(row.get("Description"));
			printLog(GuiTools.getTestCaseName());
			String url = mapConfInfos.get("url");
			String user = mapConfInfos.get("user_name");
			String password = mapConfInfos.get("password");		
			if (!logged) 
			{
				guiTools.openBrowser(browserType);
				logged = SIMALib.loginToSima(url, user, password, modifCreate);
			}
			testCaseStatus = SIMALib.checkUnusualCountries(allCountry,
					"Country from which the product(s) were exported");
		}
		 
		 /**
			 * This method is for validation of unusual countries of smelt
		*/
		 @Test()
		void VALIDATE_UNUSUAL_COUNTRY_OF_SMELT() throws Exception
		{
			 printLog("VALIDATE_UNUSUAL_COUNTRY_OF_SMELT");
			LinkedHashMap<String, String> row = getTestCaseInfo(scenarios, 
					"Test_Case_Name", "VALIDATE_UNUSUAL_COUNTRY_OF_SMELT");
			GuiTools.setTestCaseName(row.get("Test_Case_Name"));
			GuiTools.setTestCaseDescription(row.get("Description"));
			printLog(GuiTools.getTestCaseName());
			String url = mapConfInfos.get("url");
			String user = mapConfInfos.get("user_name");
			String password = mapConfInfos.get("password");		
			if (!logged) 
			{
				guiTools.openBrowser(browserType);
				logged = SIMALib.loginToSima(url, user, password, modifCreate);
			}
			testCaseStatus = SIMALib.checkUnusualCountries(allCountry,
					"Country of Melt & Pour");
		}
		 /**
			 * This method is for validation of unusual countries of export
			*/
			 @Test()
			void VALIDATE_MATCHING_COUNTRIES() throws Exception
			{
				 printLog("VALIDATE_MATCHING_COUNTRIES");
				LinkedHashMap<String, String> row = getTestCaseInfo(scenarios, 
						"Test_Case_Name", "VALIDATE_MATCHING_COUNTRIES");
				GuiTools.setTestCaseName(row.get("Test_Case_Name"));
				GuiTools.setTestCaseDescription(row.get("Description"));
				printLog(GuiTools.getTestCaseName());
				String url = mapConfInfos.get("url");
				String user = mapConfInfos.get("user_name");
				String password = mapConfInfos.get("password");		
				if (!logged) 
				{
					guiTools.openBrowser(browserType);
					logged = SIMALib.loginToSima(url, user, password, modifCreate);
				}
				allCountry.removeAll(euCountries);
				allCountry.removeAll(naftaCountries);
				testCaseStatus = SIMALib.checkMismatchCountries(listFromHash(allCountry, "Country Name"),
						listFromHash(naftaCountries, "Country Name"),
						listFromHash(euCountries, "Country Name"));
			}
		 
	/* *//**
		 * This method if for getting list of value of a given column
		*//*
		public List<String> substructList(List<String> list1, List<String> list2)
		{
			return list1.removeAll(list2);
		}*/
	 
	 /**
		 * This method if for getting list of value of a given column
		*/
		public List<String> listFromHash(ArrayList<LinkedHashMap<String, String>> dataPool, String fieldName)
		{
			List<String> str = new ArrayList<String>();
			for(LinkedHashMap<String, String> map : dataPool)
			{
				str.add(map.get(fieldName));
			}
			return str;
		}

	 
	 
	 
	 
	/**
	 * This method if for getting the current test case information
	*/
	public LinkedHashMap<String, String> 
	getTestCaseInfo(ArrayList<LinkedHashMap<String, String>> dataPool, String colName, String vlaue)
	{
		for(LinkedHashMap<String, String> map : dataPool)
		{
			if(vlaue.equalsIgnoreCase(map.get(colName)))
			{
				return map;
			}
		}
		return null;
	}
	
	public String getTimeToUpload(long temps)
	{
		int  minutes=0, hours=0;
		int seconds = (int) temps / 1000;
	    hours = seconds / 3600;
	    minutes = (seconds % 3600) / 60;
	    seconds = (seconds % 3600) % 60;
	    String hour = (hours<10)?"0"+hours:""+hours;
	    String minute = minutes<10?"0"+minutes:""+minutes;
	    String second = seconds<10?"0"+seconds:""+seconds;
		return ""+hour+":"+minute+":"+second;
	}

}
