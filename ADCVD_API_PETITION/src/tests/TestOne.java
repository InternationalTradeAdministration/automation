package tests;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.testCaseStatus;
import static GuiLibs.GuiTools.updateHtmlReport;
import static ReportLibs.ReportTools.printLog;
import static XmlLibs.XmlTools.buildTestNgFromDataPool;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.http.client.HttpClient;
import org.json.JSONObject;
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
import ServiceLibs.APITools;
import libs.ADCVDLib;

//import libs.ADCVDLib;
public class TestOne {

	public static GuiTools guiTools;
	//static HtmlReport htmlReport;
	HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static LinkedHashMap<String, String> recordType = new LinkedHashMap<String, String>();
	static ArrayList<LinkedHashMap<String, String>> dataPool, dataPoolCase, dataPoolPetition, 
	dataPoolInvestigation, dataPoolOrder, dataPoolSegment;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar cal = Calendar.getInstance();
	public static HttpClient httpclient;
	public static String caseId, caseName, petitionId, petitionName,
	investigationId, investigationName, orderId, orderName,adminReviewId,adminReviewName;
	public static void main(String[] args) throws Exception 
	{
		printLog("MainMethod()");
		initiateRecordType();
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"\\datapool\\adcvd_datapool.xlsx";
		dataPoolCase  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Case", "Active=TRUE");
		dataPoolPetition  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Petition", "Active=TRUE");
		dataPoolInvestigation  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Invetigation", "Active=TRUE");
		dataPoolOrder  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Order", "Active=TRUE");
		dataPoolSegment = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Segments", "Active=TRUE");
		ADCVDLib.tollingDates = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Tolling Dates", "Active=TRUE");
		dataPool = mergeDataPools(dataPoolCase, dataPoolPetition, dataPoolInvestigation, dataPoolOrder, dataPoolSegment);
		String testNgPath = InitTools.getRootFolder()+"\\testng.xml";
		//build
		buildTestNgFromDataPool(dataPool, testNgPath);
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
		guiPool = XlsxTools.readXlsxSheetAndFilter(guiMapFilePath, "guiMap", "");
		guiMap = XlsxTools.readGuiMap(guiPool);
		HtmlReport.setTestSuiteName(mapConfInfos.get("PROJECTNAME"));
		HtmlReport.setEnvironmentName(mapConfInfos.get("ENVNAME"));
		HtmlReport.setTotalTcs(dataPool.size());
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
			System.exit(0);
	    }
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
		String url = mapConfInfos.get("LOGINURL");
		String grantService = mapConfInfos.get("GRANTSERVICE");
		String grantType = mapConfInfos.get("GRANTTYPE");
		String grantId = mapConfInfos.get("CLIENTID");
		String clientSecret = mapConfInfos.get("CLIENTSECRET");
		String userName = mapConfInfos.get("USERNAME");
		String password = mapConfInfos.get("PASSWORD");
		String accessToken = APITools.getAccesToken(url, grantService, grantType, grantId,
				clientSecret , userName, password);
		if(accessToken!=null)
		{
			LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
			row.put("Name", row.get("Name")+ADCVDLib.getCaseName());
			row.put("Product__c", row.get("Product__c")+InitTools.getActualResultFolder());
			for (Entry<String, String> entry : row.entrySet())  
            {
				if (entry.getKey().equalsIgnoreCase("Name")||entry.getKey().equalsIgnoreCase("Commodity__c")||
				entry.getKey().equalsIgnoreCase("ADCVD_Case_Type__c")||entry.getKey().equalsIgnoreCase("Product__c")||
				entry.getKey().equalsIgnoreCase("Product_Short_Name__c")||entry.getKey().equalsIgnoreCase("Country__c"))
				record.put(entry.getKey(), entry.getValue());
            } 
			caseId = APITools.createRecordObject("ADCVD_Case__c", record);
			if(caseId!=null)
			{
				caseName = record.get("Name");
				updateHtmlReport("Create Case", "User is able to create a new case", 
						"Case <span class = 'boldy'>"+" "+caseName+"</span>", "Step", "pass", "" );
			}
		}else 
		{
			failTestSuite("Create new Case", "User is able to create a new Case",
						"Not as expected", "Step", "fail", "");
		}
		
		
		
		/*System.out.println("start Test");
		String url = mapConfInfos.get("url");
		String user = mapConfInfos.get("user_name");
		String password = mapConfInfos.get("password");		
		guiTools.openBrowser(browserType);
		ADCVDLib.loginToAdCvd(url, user, password);
		holdSeconds(2);
		row.put("TimeStamp", InitTools.getActualResultFolder());
		
		testCaseStatus =  ADCVDLib.createNewCase(row);
		if(! testCaseStatus) GuiTools.tearDown =true;*/
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
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		record.put("ADCVD_Case_Number__c", caseName);
		record.put("ADCVD_Case__c", caseId);
		record.put("Petition_Filed__c", row.get("Petition_Filed__c"));
		record.put("Initiation_Extension_of_days__c", row.get("Initiation_Extension_of_days__c"));
		
		petitionId = APITools.createRecordObject("Petition__c", record);
		if(petitionId != null)
	    {
		    JSONObject jObj = APITools.getRecordObject("Select+Name+From+Petition__c+Where+id='"+petitionId+"'");
		   	petitionName = jObj.getString("Name");
		   	updateHtmlReport("Create Petition", "User is able to create a new Petition", 
					"Petition <span class = 'boldy'>"+" "+petitionName+"</span>", "Step", "pass", "" );
	    }
		else 
		{
			failTestSuite("Create new Petition", "User is able to create a new Petition",
						"Not as expected", "Step", "fail", "");
		}
		
		//JSONObject petitionJsonObj = APITools.getRecordObject(row.get("Query").replace("Petition_id", petitonId));
		//System.out.println(petitionJsonObj.getString("Name"));
		//testCaseStatus = ADCVDLib.createNewPetition(row);
		//if(! testCaseStatus) GuiTools.tearDown =true;
		//testCaseStatus = testCaseStatus & ADCVDLib.validatePetitionFields(row);
	}
	
	/**
	 * This method is for ADCVD Investigation creation and validation
	*/
	@Test(enabled = true, priority=3)
	void Create_And_Validate_Investigation() throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Investigation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_003");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		
		//Investigation__c
		record.put("Petition__c", petitionId);
		investigationId = APITools.createRecordObject("Investigation__c", record);
		if(investigationId != null)
       {
	       	JSONObject jObj = APITools.getRecordObject("Select+Name+From+Investigation__c+Where+id='"+investigationId+"'");
	       	investigationName = jObj.getString("Name");
	       	updateHtmlReport("Create Investigation", "User is able to create a new Investigation", 
					"investigatioon id: <span class = 'boldy'>"+" "+investigationName+"</span>", "Step", "pass", "" );
       }
		else 
		{
			failTestSuite("Create new Investigation", "user is able to create a new investigation",
						"Not as expected", "Step", "fail", "");
		}
		//testCaseStatus = ADCVDLib.createNewInvestigation(row);
		//if(! testCaseStatus) GuiTools.tearDown =true;
		//testCaseStatus = testCaseStatus & ADCVDLib.validateInvestigationFields(row);
	}
	/**
	 * This method is for ADCVD order creation and validation
	*/
	@Test(enabled = true, priority=4)
	void Create_Order() throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Order");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_004");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		
		record.put("Investigation__c", investigationId);
		orderId = APITools.createRecordObject("ADCVD_Order__c", record);
		if(orderId != null)
       {
	       	JSONObject jObj = APITools.getRecordObject("Select+Name+From+ADCVD_Order__c+Where+id='"+orderId+"'");
	       	orderName = jObj.getString("Name");
	       	updateHtmlReport("Create Order", "User is able to create a new Order", 
					"Order <span class = 'boldy'>"+" "+orderName+"</span>", "Step", "pass", "" );
       }
		else 
		{
			failTestSuite("Create new Order", "user is able to create an order", "Not as expected",
						"Step", "fail", "");
		}
		//testCaseStatus = ADCVDLib.createNewOrder(row);
		//if(! testCaseStatus) GuiTools.tearDown = true;
	}
	
	/**
	 * This method is for ADCVD segment(Administrative Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=5)
	void Create_Segment_Administrative_Review() throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Segment - 1");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_005");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		//JSONObject jObj = APITools.getRecordObject("Select+id+From+RecordType+Where+Name='"+row.get("Segment_Type")+"'");
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Final_Date_of_Anniversary_Month__c", row.get("Final_Date_of_Anniversary_Month__c"));
		adminReviewId = APITools.createRecordObject("Segment__c", record);
		if(orderId != null)
       {
			JSONObject jObj = APITools.getRecordObject("Select+Name+From+Segment__c+Where+id='"+adminReviewId+"'");
	       	adminReviewName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+adminReviewName+"</span>", "Step", "pass", "" );
	       	//update
	       	record.clear();
	       	record.put("Segment_Outcome__c", "Deficient");
	       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	if(code.equals("204"))
	       	{
	       		updateHtmlReport("update admin review", "User is able to update admin review", 
	       				record.toString(), "Step", "pass", "");
	       	}
	       	else
	       	{
	       		failTestSuite("update admin review ["+adminReviewName+"]", "user is able to update the record",
	       				"Not as expected",	"Step", "fail", "");
	       	}
       }
	   else 
	   {
			failTestSuite("Create new Admin review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
		
		//testCaseStatus = ADCVDLib.createNewSegment(row);
		//testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentAdministrativeReview();
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
		testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentChangedCircumstancesReview();
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
		testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentNewScoprInquiry();
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
		testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentSunsetReview(row);
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
	
	 public static void initiateRecordType()
    {
    	recordType.put("Administrative Review","012t0000000TSjxAAG");
    	recordType.put("Anti-Circumvention Review","012t0000000TSjxAAG");
    	recordType.put("Changed Circumstances Review","012t0000000TSjxAAG");
    	recordType.put("Expedited Review","012t0000000TSjxAAG");
    	recordType.put("New Shipper Review","012t0000000TSjxAAG");
    	recordType.put("Scope Inquiry","012t0000000TSjxAAG");
    	recordType.put("Sunset Review","012t0000000TSjxAAG");
    	
    }
}
