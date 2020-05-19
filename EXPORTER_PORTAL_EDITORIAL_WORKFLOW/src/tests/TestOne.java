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
import org.testng.annotations.DataProvider;
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
	static HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static ArrayList<LinkedHashMap<String, String>> dataPool;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	static ExporterLib exporterLib;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar cal = Calendar.getInstance();
	public static String publisherName, publisherPassword, editorName, editorPassword,
						 creatorName, creatorPassword, articleTitle, oldUser="";
	public boolean loginOn = false;
	public static void main(String[] args) throws Exception 
	{
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		exporterLib = new ExporterLib();
		TestNG testng = new TestNG();
		mapConfInfos = guiTools.getConfigInfos();		
		creatorName = mapConfInfos.get("creator_name");
		creatorPassword= mapConfInfos.get("creator_password");
		editorName = mapConfInfos.get("editor_name");
		editorPassword= mapConfInfos.get("editor_password");
		publisherName = mapConfInfos.get("publisher_name");
		publisherPassword= mapConfInfos.get("publisher_password");		
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"/datapool/Exporter_Editorial_Workflow.xlsx";
		System.out.println("dataPoolPath "+dataPoolPath);
		dataPool  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "workflow", "Active=TRUE");
		String testNgTemplate = InitTools.getInputDataFolder()+"/template/testng_template.xml";
		String testNgPath = InitTools.getRootFolder()+"/testng.xml";
		System.out.println("testNgTemplate "+testNgTemplate);
		System.out.println("testNgPath "+testNgPath);
		//build
		//buildTestNgFromDataPool(dataPool, testNgPath);
		suites.add(testNgPath);//path to xml..
		testng.setTestSuites(suites);
		testng.run();
	}
	
	@BeforeClass
	void beforeClass() throws Exception
	{
		printLog("Executing Before class");
		GuiTools.guiMap = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		browserType = mapConfInfos.get("browser_type");
		String guiMapFilePath = InitTools.getInputDataFolder()+"/script/gui_map.xlsx";
		guiPool = XlsxTools.readXlsxSheetAndFilter(guiMapFilePath, "guiMap", "");
		guiMap = XlsxTools.readGuiMap(guiPool);
		HtmlReport.setTestSuiteName(mapConfInfos.get("project_name"));
		HtmlReport.setEnvironmentName(mapConfInfos.get("env_name"));
		HtmlReport.setTotalTcs(dataPool.size());
		java.util.Date date = new java.util.Date();
		suiteStartTime = new Timestamp(date.getTime());
		guiTools.openBrowser(browserType);
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
	
	
	@DataProvider(name = "fetchingData")
	public static Object[][] fetchData() 
	{
		
		Object obj [][]= new  Object[dataPool.size()][2];
		int i = 0;
		for (LinkedHashMap<String, String> map : dataPool)
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
	void validate_work_flow(LinkedHashMap<String, String> row, int i) throws Exception
	{
		String contentType = row.get("Content_Type");
		int contentCat = 1;
		//LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_001");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String thisCreatorName = creatorName, ThisCreatorPsw = creatorPassword;
		String thisEditorName = editorName, ThisEditorPsw = editorPassword;
		String thisPublisherName = publisherName, thisPublisherPsw = publisherPassword;
		if(contentType.contains("One-Off Landing Page")||contentType.contains("Topic Landing Page"))
		{
			contentCat = 2;
			thisCreatorName = editorName;
			ThisCreatorPsw = editorPassword;
		}else if (contentType.contains("Section Landing Page"))
		{
			contentCat = 3;
			thisCreatorName = publisherName;
			ThisCreatorPsw = publisherPassword;
			thisEditorName = publisherName;
			ThisEditorPsw = publisherPassword;
		}
		
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			if(!oldUser.equals(thisCreatorName))
			{
				loginOn = ExporterLib.loginToExporter(url, thisCreatorName, ThisCreatorPsw);
			}
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			if(contentCat == 1)
			loginOn = ExporterLib.loginToExporter(url, thisEditorName, ThisEditorPsw);
			ExporterLib.approveArticle(articleTitle, contentType);
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			if(contentCat == 1 || contentCat == 2)
			loginOn = ExporterLib.loginToExporter(url, thisPublisherName, thisPublisherPsw);
			ExporterLib.publishArticle(articleTitle, contentType);
		}
		oldUser = thisPublisherName;
	}
	
	/*
	*//**
	 * This method is creating Feature Article content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Feature Article");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	*//**
	 * This method is creating How To content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "How-To");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	
	*//**
	 * This method is creating Image Library content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Image Library");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}


	*//**
	 * This method is creating Internship content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Internship");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	
	*//**
	 * This method is creating Knowledge Product content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Knowledge Product");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	
	*//**
	 * This method is creating News Blog content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "News Blog");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	
	*//**
	 * This method is creating Office content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Office");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	
	*//**
	 * This method is creating Press Release content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Press Release");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	
	*//**
	 * This method is creating Series Aggregator content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Series Aggregator");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	*//**
	 * This method is creating Service Offering content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Service Offering");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	*//**
	 * This method is creating Success Story content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Success Story");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Biography");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Biography");
		}
	}
	
	*//**
	 * This method is creating Sub-topic Page content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Sub-topic Page");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Sub-topic Page");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Sub-topic Page");
		}
	}
	
	
	*//**
	 * This method is creating Trade Lead content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Trade Lead");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Trade Lead");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Trade Lead");
		}
	}
	
	*//**
	 * This method is creating Video Library content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "Video Library");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "Video Library");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "Video Library");
		}
	}
	
	
	*//**
	 * This method is creating About Us content
	*//*
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
		loginOn = ExporterLib.loginToExporter(url, creatorName, creatorPassword);
		row.put("Content_Type", "About Us");
		if("Yes".equalsIgnoreCase(row.get("Create")))
		{
			articleTitle = ExporterLib.createContent(row);
		}
		if("Yes".equalsIgnoreCase(row.get("Approve")))
		{
			loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
			ExporterLib.approveArticle(articleTitle, "About Us");
		}
		if("Yes".equalsIgnoreCase(row.get("Publish")))
		{
			loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
			ExporterLib.publishArticle(articleTitle, "About Us");
		}
	}
	
	
	
	
	*//**
	 * This method is creating T1 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T1() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T1");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_026");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");		
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T1");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T1");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T1");
	}
	
	
	*//**
	 * This method is creating T2 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T2() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T2");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_027");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T2");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T2");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T2");
	}
	
	*//**
	 * This method is creating T3 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T3() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T3");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_028");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T3");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T3");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T3");
	}
	
	*//**
	 * This method is creating T4 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T4() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T4");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_029");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T4");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T4");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T4");
	}
	
	
	*//**
	 * This method is creating T5 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T5() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T5");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_030");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T5");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T5");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T5");
	}
	
	
	
	*//**
	 * This method is creating T6 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T6() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T6");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_031");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T6");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T6");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T6");
	}
	
	
	*//**
	 * This method is creating T7 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T7() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T7");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_032");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T7");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T7");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T7");
	}
	
	*//**
	 * This method is creating T8 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T8() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T8");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_033");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T8");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T8");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T8");
	}
	
	
	*//**
	 * This method is creating T9 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T9() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T9");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_034");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T9");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T9");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T9");
	}
	
	
	*//**
	 * This method is creating T10 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T10() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T10");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_035");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T10");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T10");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T10");
	}
	
	
	*//**
	 * This method is creating T11 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T11() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T11");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_036");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T11");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T11");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T11");
	}
	

	*//**
	 * This method is creating T12 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T12() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T12");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_037");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T12");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T12");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T12");
	}
	
	
	*//**
	 * This method is creating T14 content
	*//*
	@Test(enabled = true)
	void Create_Topic_Landing_Page_T14() throws Exception
	{
		printLog("Create_Topic_Landing_Page_T14");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_038");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "Topic Landing Page - T14");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Topic Landing Page - T14");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Topic Landing Page - T14");
	}
	
	
	*//**
	 * This method is creating T5 content
	*//*
	@Test(enabled = true)
	void Create_One_Off_Landing_Page_C5() throws Exception
	{
		printLog("Create_One_Off_Landing_Page_C5");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_039");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "One-Off Landing Page - C5");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"One-Off Landing Page - C5");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "One-Off Landing Page - C5");
		
		
	}
	
	*//**
	 * This method is creating TC6 content
	*//*
	@Test(enabled = true)
	void Create_One_Off_Landing_Page_C6() throws Exception
	{
		printLog("Create_One_Off_Landing_Page_C6");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_040");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");		
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "One-Off Landing Page - C6");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"One-Off Landing Page - C6");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "One-Off Landing Page - C6");
	}
	
	*//**
	 * This method is creating C11 content
	*//*
	@Test(enabled = true)
	void Create_One_Off_Landing_Page_C11() throws Exception
	{
		printLog("Create_One_Off_Landing_Page_C11");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_041");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");		
		loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		row.put("Content_Type", "One-Off Landing Page - C11");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"One-Off Landing Page - C11");
		//publish
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "One-Off Landing Page - C11");
	}

	
	*//**
	 * This method is creating S1 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S1() throws Exception
	{
		printLog("Create_Section_Landing_Page_S1");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_017");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");		
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S1");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S1");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S1");
	}
	

	*//**
	 * This method is creating S2 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S2() throws Exception
	{
		printLog("Create_Section_Landing_Page_S2");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_018");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S2");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S2");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S2");
	}
	
	*//**
	 * This method is creating S3 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S3() throws Exception
	{
		printLog("Create_Section_Landing_Page_S3");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_019");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S3");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S3");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S3");
	}
	
	*//**
	 * This method is creating S4 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S4() throws Exception
	{
		printLog("Create_Section_Landing_Page_S4");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_020");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S4");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S4");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S4");
	}
	
	
	*//**
	 * This method is creating S5 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S5() throws Exception
	{
		printLog("Create_Section_Landing_Page_S5");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_021");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S5");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S5");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S5");
	}
	
	
	*//**
	 * This method is creating S6 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S6() throws Exception
	{
		printLog("Create_Section_Landing_Page_S6");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_022");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S6");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S6");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S6");
	}
	
	*//**
	 * This method is creating S7 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S7() throws Exception
	{
		printLog("Create_Section_Landing_Page_S7");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_023");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S7");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S7");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S7");
	}
	
	*//**
	 * This method is creating S8 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S8() throws Exception
	{
		printLog("Create_Section_Landing_Page_S8");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_024");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S8");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S8");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S8");
	}
	
	
	*//**
	 * This method is creating S8 content
	*//*
	@Test(enabled = true)
	void Create_Section_Landing_Page_S9() throws Exception
	{
		printLog("Create_Section_Landing_Page_S9");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_025");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String url = mapConfInfos.get("url");
		loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		row.put("Content_Type", "Section Landing Page - S9");
		articleTitle = ExporterLib.createContent(row);
		//approve
		//loginOn = ExporterLib.loginToExporter(url, editorName, editorPassword);
		ExporterLib.approveArticle(articleTitle,"Section Landing Page - S9");
		//publish
		//loginOn = ExporterLib.loginToExporter(url, publisherName, publisherPassword);
		ExporterLib.publishArticle(articleTitle, "Section Landing Page - S9");
	}*/
	
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
