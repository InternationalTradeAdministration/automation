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
import libs.ExporterLib;

public class TestOne {
	public static GuiTools guiTools;
	//static HtmlReport htmlReport;
	HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static ArrayList<LinkedHashMap<String, String>> dataPool;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	static ExporterLib exporterLib;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar cal = Calendar.getInstance();
	public boolean loginOn = false;
	public static void main(String[] args) throws Exception 
	{
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		exporterLib = new ExporterLib();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"/datapool/Exporter_Regression.xlsx";
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
	 * This method is E-File creation
	*/
	@Test(enabled = true)
	void Create_Content_Biography() throws Exception
	{
		printLog("Create_Content_Biography");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Biography");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating Feature Article content
	*/
	@Test(enabled = true)
	void Create_Content_Feature_Article() throws Exception
	{
		printLog("Create_Content_Feature_Article");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_002");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Feature Article");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	/**
	 * This method is creating How To content
	*/
	@Test(enabled = true)
	void Create_Content_How_To() throws Exception
	{
		printLog("Create_Content_How_To");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_003");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "How To");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating Image Library content
	*/
	@Test(enabled = true)
	void Create_Content_Image_Library() throws Exception
	{
		printLog("Create_Content_Image_Library");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_004");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Image Library");
		testCaseStatus = ExporterLib.createContent(row);
	}


	/**
	 * This method is creating Internship content
	*/
	@Test(enabled = true)
	void Create_Content_Internship() throws Exception
	{
		printLog("Create_Content_Internship");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_005");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Internship");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating Knowledge Product content
	*/
	@Test(enabled = true)
	void Create_Content_Knowledge_Product() throws Exception
	{
		printLog("Create_Content_Knowledge_Product");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_006");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Knowledge Product");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating News Blog content
	*/
	@Test(enabled = true)
	void Create_Content_News_Blog() throws Exception
	{
		printLog("Create_Content_News_Blog");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_007");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "News Blog");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating Office content
	*/
	@Test(enabled = true)
	void Create_Content_Office() throws Exception
	{
		printLog("Create_Content_Office");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_008");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Office");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating Press Release content
	*/
	@Test(enabled = true)
	void Create_Content_Press_Release() throws Exception
	{
		printLog("Create_Content_Press_Release");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_009");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Press Release");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating Series Aggregator content
	*/
	@Test(enabled = true)
	void Create_Content_Series_Aggregator() throws Exception
	{
		printLog("Create_Content_Series_Aggregator");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_010");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Series Aggregator");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	/**
	 * This method is creating Service Offering content
	*/
	@Test(enabled = true)
	void Create_Content_Service_Offering() throws Exception
	{
		printLog("Create_Content_Service_Offering");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_011");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Service Offering");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	/**
	 * This method is creating Success Story content
	*/
	@Test(enabled = true)
	void Create_Content_Success_Story() throws Exception
	{
		printLog("Create_Content_Success_Story");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_012");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Success Story");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	/**
	 * This method is creating Sub-topic Page content
	*/
	@Test(enabled = true)
	void Create_Content_Sub_topic_Page() throws Exception
	{
		printLog("Create_Content_Sub_topic_Page");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_013");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Sub-topic Page");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating Trade Lead content
	*/
	@Test(enabled = true)
	void Create_Content_Trade_Lead() throws Exception
	{
		printLog("Create_Content_Trade_Lead");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_014");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Trade Lead");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	/**
	 * This method is creating Video Library content
	*/
	@Test(enabled = true)
	void Create_Content_Video_Library() throws Exception
	{
		printLog("Create_Content_Video_Library");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_015");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "Video Library");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	
	/**
	 * This method is creating About Us content
	*/
	@Test(enabled = true)
	void Create_Content_About_Us() throws Exception
	{
		printLog("Create_Content_About_Us");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_016");
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
			loginOn = ExporterLib.loginToExporter(url, user, password);
		}
		row.put("Content_Type", "About Us");
		testCaseStatus = ExporterLib.createContent(row);
	}
	
	/**
	 * This method if for getting the current test case information
	*/
	public LinkedHashMap<String, String> getTestCaseInfo(ArrayList<LinkedHashMap<String, String>> dataPool, 
																			String tcTagName)
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
