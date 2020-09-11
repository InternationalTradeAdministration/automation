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
import libs.FTZLib;
public class TestOne {

	public static GuiTools guiTools;
	HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static ArrayList<LinkedHashMap<String, String>> scenarios, caseTypes;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	static FTZLib fTZLib;
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
		fTZLib = new FTZLib();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList(); 
		//String dataPoolPath = InitTools.getInputDataFolder()+"\\datapool\\Steel prodcuts.xlsx";
		scenarios = XlsxTools.readXlsxSheetInOrderAndFilter(
				InitTools.getInputDataFolder()+"\\datapool\\FTZ_Date_Validation.xlsx", "Dates", "Active=TRUE");
		caseTypes = XlsxTools.readXlsxSheetInOrderAndFilter(
				InitTools.getInputDataFolder()+"\\datapool\\FTZ_Date_Validation.xlsx", "Case_Type", "Active=TRUE");
		String testNgPath = InitTools.getRootFolder()+"\\testng.xml";
		//build
		//buildTestNgFromDataPool(scenarios, testNgPath);
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
		HtmlReport.setEnvironmentName(mapConfInfos.get("env_name"));
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
	 @DataProvider(name = "fetchingData")
		public static Object[][] fetchData() 
		{
			
			Object obj [][]= new  Object[scenarios.size()][2];
			int i = 0;
			for (LinkedHashMap<String, String> map : scenarios)
			{
				obj[i][0] = map;
				obj[i][1] = i;
				i++;
			}
			return (Object[][]) obj;
			
	   }
	 /**
	 * This method is E-File creation
	*/
	@Test(dataProvider = "fetchingData")
 
	void Validate_ftz_dates(LinkedHashMap<String, String> map, int i) throws Exception
	{
		
		GuiTools.setTestCaseName(map.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(map.get("Description"));
		printLog(GuiTools.getTestCaseName());
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");	
		String caseid = mapConfInfos.get("caseid");
		if (!logged) 
		{
			guiTools.openBrowser(browserType);
			logged = FTZLib.loginToFtz(url, user, password, caseid);
		}
		testCaseStatus = FTZLib.validateFtzDate(map, caseTypes);
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
