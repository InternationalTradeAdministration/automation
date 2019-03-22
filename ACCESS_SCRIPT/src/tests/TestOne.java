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
import libs.AccessLib;

public class TestOne {
	public static GuiTools guiTools;
	//static HtmlReport htmlReport;
	HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static ArrayList<LinkedHashMap<String, String>> dataPool;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	static AccessLib accessLib;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar cal = Calendar.getInstance();
	public boolean loginOn = false;
	public static void main(String[] args) throws Exception 
	{
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		accessLib = new AccessLib();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"/datapool/Access_Regression.xlsx";
		System.out.println("dataPoolPath "+dataPoolPath);
		dataPool  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Regression", "Active=TRUE");
		String testNgTemplate = InitTools.getInputDataFolder()+"/template/testng_template.xml";
		String testNgPath = InitTools.getRootFolder()+"/testng.xml";
		System.out.println("testNgTemplate "+testNgTemplate);
		System.out.println("testNgPath "+testNgPath);
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
		String guiMapFilePath = InitTools.getInputDataFolder()+"/script/gui_map.xlsx";
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
		HtmlReport.setSuiteExecutionTime(endTime.getTime() - 
										suiteStartTime.getTime());
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
	    if (GuiTools.tearDown)
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
	void First_test_Case() throws Exception
	{
		printLog("First_test_Case");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_001");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");	
		System.out.println(url+"___"+user);
		if (!loginOn)
		{
			
			guiTools.openBrowser(browserType);
			loginOn = AccessLib.loginToAccess(url, user, password);
		}
		holdSeconds(2);
		AccessLib.createEFileDocument(row);
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
	
}
