package tests;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.testCaseStatus;
import static GuiLibs.GuiTools.updateHtmlReport;
import static ReportLibs.ReportTools.printLog;
import static XmlLibs.XmlTools.buildTestNgFromDataPool;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
	dataPoolInvestigation, dataPoolOrder, dataPoolSegment,dataPoolLitigation;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar cal = Calendar.getInstance();
	public static HttpClient httpclient;
	public static String caseId, caseName, petitionId, petitionName,
	investigationId, investigationName, orderId, orderName,adminReviewId,
	adminReviewName, litigationId, litigationName, remandId, remandName;
	public static void main(String[] args) throws Exception 
	{
		initiateRecordType();
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		ADCVDLib adcvdLibs = new ADCVDLib();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"/datapool/adcvd_datapool.xlsx";
		dataPoolCase  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Case", "Active=TRUE");
		dataPoolPetition  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Petition", "Active=TRUE");
		dataPoolInvestigation  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Invetigation", "Active=TRUE");
		dataPoolOrder  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Order", "Active=TRUE");
		dataPoolSegment = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Segments", "Active=TRUE"); 
		ADCVDLib.tollingDates = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Tolling Dates", "Active=TRUE");
		dataPool = mergeDataPools(dataPoolCase, dataPoolPetition, dataPoolInvestigation, dataPoolOrder, dataPoolSegment);
		String testNgPath = InitTools.getRootFolder()+"/testng.xml";
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
		String guiMapFilePath = InitTools.getInputDataFolder()+"/script/gui_map.xlsx";
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
			caseId = APITools.createObjectRecord("ADCVD_Case__c", record);
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
		petitionId = APITools.createObjectRecord("Petition__c", record);
		if(petitionId != null)
	    {
		    JSONObject rObj = APITools.getRecordFromObject(row.get("Query").replace("petitionId", petitionId));
		   	petitionName = rObj.getString("Name");
		   	updateHtmlReport("Create Petition", "User is able to create a new Petition", 
					"Petition <span class = 'boldy'>"+" "+petitionName+"</span>", "Step", "pass", "" );
		   //	testCaseStatus = testCaseStatus & ADCVDLib.validatePetitionFields(rObj);
	    }
		else 
		{
			failTestSuite("Create new Petition", "User is able to create a new Petition",
						"Not as expected", "Step", "fail", "");
		}
		
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
		investigationId = APITools.createObjectRecord("Investigation__c", record);
		if(investigationId != null)
       {
	       	JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("investigationId", investigationId));
	       	investigationName = jObj.getString("Name");
	       	updateHtmlReport("Create Investigation", "User is able to create a new Investigation", 
					"investigatioon id: <span class = 'boldy'>"+" "+investigationName+"</span>", "Step", "pass", "" );
	        //testCaseStatus = testCaseStatus & ADCVDLib.validateInvestigationFields(jObj);
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
		orderId = APITools.createObjectRecord("ADCVD_Order__c", record);
		if(orderId != null)
       {
	       	JSONObject jObj = APITools.getRecordFromObject("Select+Name+From+ADCVD_Order__c+Where+id='"+orderId+"'");
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
		//JSONObject jObj = APITools.getRecordFromObject("Select+id+From+RecordType+Where+Name='"+row.get("Segment_Type")+"'");
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Will_you_Amend_the_Final__c", "Yes");
		record.put("Final_Date_of_Anniversary_Month__c", row.get("Final_Date_of_Anniversary_Month__c"));
		adminReviewId = APITools.createObjectRecord("Segment__c", record);
		if(adminReviewId != null)
        {
			String sqlString = "select+Name+from+segment__c+where+id='"+adminReviewId+"'";
			//HtmlReport.addHtmlStepTitle("Validate all dates for a happy path","Title");
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
	       	adminReviewName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+adminReviewName+"</span>", "Step", "pass", "" );
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/validate_admin_review_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> adminReviewDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "admin review", "");
	         //*********************************I. VALIDATE DATES WHEN THEY FALL ON WEEKEND************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:adminReviewDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Final_Date_of_Anniversary_Month__c", dates.get("Date_For_Weekend"));
			       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAdministrativeReview(jObj, dates.get("Field_Name"), "Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Final_Date_of_Anniversary_Month__c", dates.get("Date_For_Holiday"));
			       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAdministrativeReview(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Final_Date_of_Anniversary_Month__c", dates.get("Date_For_Tolling"));
			       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAdministrativeReview(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
	       		}
	       	}
	        //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Majore Deadline","Title");
	        String actualValue, expectedValue;
	        //Next_Major_Deadline__c
	        //1
	        sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c,Calculated_Amended_Final_Signature__c+"
	        		+ "from+segment__c+where+id='"+adminReviewId+"'";
	        jObj = APITools.getRecordFromObject(sqlString);
	        String clause = "IF Published_Date__c (Type: Preliminary) is blank AND Published_Date__c "
	        		+ "(Type:Rescission) is blank THEN Calculated_Preliminary_Signature__c";
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Calculated_Final_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) is blank THEN Calculated_Final_Signature__c";
	        record.clear();
    		record.put("Final_Date_of_Anniversary_Month__c", "");
	       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);

	        //3
	        clause  = "IF Actual_Amended_Final_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) is blank AND Will_you_amended_the_final__c = Yes THEN "
	        		+ "Calculated_Amended_Final_Signature__c"; 
	        record.clear();
    		record.put("Final_Date_of_Anniversary_Month__c", "2019-05-05");
    		record.put("Will_you_Amend_the_Final__c", "Yes");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
   //Next Due to DAS Deadline
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline","Title");
	        //1
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  "
	        		+ "is blank THEN Prelim_Issues_Due_to_DAS__c";
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        
	        //2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", "2019-05-05");
    		record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//4
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", "2019-05-05");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", "2019-05-05");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", "2019-05-05");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank"
					+ " AND Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Signature__c", "2019-05-05");	
			record.put("Will_you_Amend_the_Final__c", "Yes");
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank "
					+ "AND Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "2019-05-05");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank "
					+ "THEN Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "2019-05-05");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    //initiate
	        record.clear();	        
			record.put("Will_you_Amend_the_Final__c", "");
			record.put("Segment_Outcome__c", "");
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	  //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
	        //1
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c "
					+ "is not past THEN Prelim_Team_Meeting_Deadline__c";
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  is blank "
					+ "THEN Prelim_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Final_Date_of_Anniversary_Month__c", "2018-07-25");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Next_Office_Deadline__c  is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Final_Date_of_Anniversary_Month__c", "2018-03-25");
			record.put("Actual_Preliminary_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	//
	       	record.clear();
			record.put("Actual_Preliminary_Signature__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	//
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", "2019-05-05");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			
			//8
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", "2019-05-05");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF published_date_c (type: Final) is blank AND Actual_Final_Signaturec is not blank THEN "
					+ "Calculated_Final_FR_signature_c";
			record.clear();
			record.put("Actual_Final_Signature__c", "2019-05-05"); 
			record.put("Segment_Outcome__c", "Completed");	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//10
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Final");
			String frIdR = APITools.createObjectRecord("Federal_Register__c", record);
			
			record.clear();
			record.put("Will_you_Amend_the_Final__c", "Yes"); 
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//11
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//12
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank THEN "
					+ "Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        
	        //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
			record.clear();
			//record.put("Final_Date_of_Anniversary_Month__c", "2019-05-05");	
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
			record.clear();
			record.put("Final_Date_of_Anniversary_Month__c", "2018-06-25");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	//
	       	record.clear();
			record.put("Actual_Preliminary_Signature__c", "2019-05-05");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	//
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        code = APITools.deleteRecordObject("Federal_Register__c", frIdR);
	        record.clear();	        
			record.put("Will_you_Amend_the_Final__c", "");
			record.put("Segment_Outcome__c", "");
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	        
	        //*********************************III. VALIDATE ALL STATUSES FOR POSITIVE AND NEGATIVE SCENARIOS**************
	       	//*************************************************************************************************************
       	//A)-//Prelim
	        HtmlReport.addHtmlStepTitle("III. VALIDATE ALL STATUSES FOR POSITIVE AND NEGATIVE SCENARIOS","Title");
	        String condition = "Initial Status";
	        sqlString = "select+Status__c+from+segment__c+where+id='"+adminReviewId+"'";
	        HtmlReport.addHtmlStepTitle("Validate Status - Prelim","Title");
	        jObj = APITools.getRecordFromObject(sqlString);
	        testCaseStatus = testCaseStatus & 
		    ADCVDLib.validateObjectStatus("Positive", "Prelim", jObj.getString("Status__c"), condition); 
	        //-1
	        condition = "If the Published Date (Type: Full recission) is not blank "
	        		+ "AND Segment Outcome is 'Full Rescission'";
	    	record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Rescission");
			frIdR = APITools.createObjectRecord("Federal_Register__c", record);
	        record.clear();
	       	record.put("Segment_Outcome__c", "Full Rescission");
	       	record.put("Will_you_Amend_the_Final__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			jObj = APITools.getRecordFromObject(sqlString);
			testCaseStatus = testCaseStatus & 
			ADCVDLib.validateObjectStatus("Negative", "Prelim", jObj.getString("Status__c"), condition); 
			//-2
			condition = "If the Published Date (Type: Preliminary) is not blank "
					+ "AND Segment Outcome is 'Full Rescission'";
			record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Preliminary");
			String frIdP = APITools.createObjectRecord("Federal_Register__c", record);
			jObj = APITools.getRecordFromObject(sqlString);
			testCaseStatus = testCaseStatus & 
					ADCVDLib.validateObjectStatus("Negative", "Prelim", jObj.getString("Status__c"), condition); 
			//-3
			condition = "If the Published Date (Type: Preliminary) is not blank "
					+ "AND Segment Outcome is not 'Full Rescission'";
			code = APITools.deleteRecordObject("Federal_Register__c", frIdR);
			record.clear();	
	       	record.put("Segment_Outcome__c", "Withdrawn");	       
	    	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			jObj = APITools.getRecordFromObject(sqlString);
			testCaseStatus = testCaseStatus & 
					ADCVDLib.validateObjectStatus("Negative", "Prelim", jObj.getString("Status__c"), condition); 
		//B)-Final
			//1-
			condition = "The Published Date (Type: Preliminary) is not blank AND Actual_Final_Signature is blank "
					+ "AND Actual_Preliminary_Signature is not blank AND Segment Outcome is not 'Full Rescission'";
	        HtmlReport.addHtmlStepTitle("Validate Status - Final","Title");
	        record.clear();
	       	record.put("Actual_Preliminary_Signature__c", "2019-08-31");
	       	record.put("Segment_Outcome__c", "");
	    	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			jObj = APITools.getRecordFromObject(sqlString);
			testCaseStatus = testCaseStatus & ADCVDLib.validateObjectStatus("Positive", 
					"Final", jObj.getString("Status__c"), condition); 
	        //3-
	        condition = "If the Published Date (Type: Preliminary) is not blank AND Actual_Final_Signature is not blank"
	        		+ " AND Actual_Preliminary_Signature is not blank AND Segment Outcome is not 'Full Rescission'";
			record.clear();
	       	record.put("Actual_Final_Signature__c", "2019-08-31"); 
	       	record.put("Segment_Outcome__c", "Withdrawn");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus &
	       		 ADCVDLib.validateObjectStatus("Negative", "Final", jObj.getString("Status__c"), condition);
	        //4-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Actual_Final_Signature is blank"
	       			+ " AND Actual_Preliminary_Signature is blank AND Segment Outcome is not 'Full Rescission'";
	    	record.clear();
	       	record.put("Actual_Preliminary_Signature__c", "");
	       	record.put("Actual_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Final", jObj.getString("Status__c"), condition);
	       	//5-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Actual_Final_Signature is blank "
	       			+ "AND Actual_Preliminary_Signature is not blank AND Segment Outcome is 'Full Rescission'";
	        record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Rescission");
			frIdR = APITools.createObjectRecord("Federal_Register__c", record);
	     	record.clear();
	       	record.put("Segment_Outcome__c", "Full Rescission");
	       	record.put("Actual_Preliminary_Signature__c", "2019-08-31");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Final", jObj.getString("Status__c"), condition);
	        //2-
	        condition="If the Published Date (Type: Preliminary) is blank AND Actual_Final_Signature is blank "
	        		+ "AND Actual_Preliminary_Signature is not blank AND Segment Outcome is not 'Full Rescission'";
	        APITools.deleteRecordObject("Federal_Register__c", frIdP);
	        APITools.deleteRecordObject("Federal_Register__c", frIdR);
	        record.clear();
	       	record.put("Segment_Outcome__c", "Withdrawn");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        testCaseStatus = testCaseStatus & 
		    ADCVDLib.validateObjectStatus("Negative", "Final", jObj.getString("Status__c"), condition); 
       	//C) Amend final
	        HtmlReport.addHtmlStepTitle("Validate Status - Amend Final","Title");
	       	//-1
	       	condition = "The Published Date (Type: Preliminary) is not blank AND Published Date (Type: Final) "
	       			+ "is not blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is "
	       			+ "not blank AND Will_You_Amend_The_Final is Yes "
	       			+ "AND Actual_Amended_Final_Determination_Sig is blank AND Segment_Outcome is not Full Rescission'";
	       	record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Preliminary");
			frIdP = APITools.createObjectRecord("Federal_Register__c", record);
			record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Final");
			String frIdF = APITools.createObjectRecord("Federal_Register__c", record);
			record.clear();
	       	record.put("Segment_Outcome__c", "Completed");
	       	record.put("Actual_Final_Signature__c", "2019-08-31");
	       	record.put("Actual_Preliminary_Signature__c", "2019-08-31");
	       	record.put("Will_you_Amend_the_Final__c", "Yes");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Positive", "Amend Final", jObj.getString("Status__c"), condition);
	       	//2-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Published Date (Type: Final) is "
	       			+ "blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is not blank AND "
	       			+ "Will_You_Amend_The_Final is Yes AND Actual_Amended_Final_Determination_Sig is blank AND"
	       			+ " Segment_Outcome is not 'Full Rescission'";
	       	APITools.deleteRecordObject("Federal_Register__c", frIdF);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//3-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Published Date (Type: Final) "
	       			+ "is not blank AND Actual_Preliminary_Signature is blank AND Actual_Final_Signature is not blank AND "
	       			+ "Will_You_Amend_The_Final is Yes AND Actual_Amended_Final_Determination_Sig is blank AND "
	       			+ "Segment_Outcome is not 'Full Rescission'";
	       	record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Final");
			frIdF = APITools.createObjectRecord("Federal_Register__c", record);
			record.clear();
	       	record.put("Actual_Preliminary_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//4-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Published Date (Type: Final) "
	       			+ "is not blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is blank"
	       			+ " AND Will_You_Amend_The_Final is Yes "
	       			+ "AND Actual_Amended_Final_Determination_Sig is blank AND Segment_Outcome is not 'Full Rescission'";
	       	record.clear();
	       	record.put("Actual_Preliminary_Signature__c", "2019-08-31");
	       	record.put("Actual_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//5-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Published Date (Type: Final) is not "
	       			+ "blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is not "
	       			+ "blank AND Will_You_Amend_The_Final is "
	       			+ "NO AND Actual_Amended_Final_Determination_Sig is blank AND Segment_Outcome is not 'Full Rescission'";
	       	record.clear();
	       	record.put("Will_you_Amend_the_Final__c", "No");
	       	record.put("Actual_Final_Signature__c", "2019-08-31");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//6-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Published Date (Type: Final) is not "
	       			+ "blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is not blank AND "
	       			+ "Will_You_Amend_The_Final is Yes"
	       			+ " AND Actual_Amended_Final_Determination_Sig is NOT blank AND Segment_Outcome is not 'Full Rescission'";
	    	record.clear();
	       	record.put("Will_you_Amend_the_Final__c", "Yes");
	       	record.put("Actual_Amended_Final_Determination_Sig__c", "2019-08-31");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//7-
	       	condition = "If the Published Date (Type: Preliminary) is not blank AND Published Date (Type: Final) is "
	       			+ "not blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is "
	       			+ "not blank AND Will_You_Amend_The_Final is Yes"
	       			+ "AND Actual_Amended_Final_Determination_Sig is blank AND Segment_Outcome is 'Full Rescission'";
	       	record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Rescission");
			frIdR = APITools.createObjectRecord("Federal_Register__c", record);
			record.clear();
	       	record.put("Segment_Outcome__c", "Full Rescission");
	       	record.put("Actual_Amended_Final_Determination_Sig__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//8-
	       	condition = "If the Published Date (Type: ITC Final) is not blank AND Published Date (Type: Final)"
	       			+ " is not blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is not "
	       			+ "blank AND Will_You_Amend_The_Final is Yes "
	       			+ "AND Actual_Amended_Final_Determination_Sig is blank AND Segment_Outcome is not 'Full Rescission'";
	       	APITools.deleteRecordObject("Federal_Register__c", frIdR);
	       	APITools.deleteRecordObject("Federal_Register__c", frIdP);
	       	record.clear();record.put("segment__c", adminReviewId);
	    	record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "ITC Final");
			String frIdITC = APITools.createObjectRecord("Federal_Register__c", record);
	       	record.clear();
	       	record.put("Segment_Outcome__c", "WithDrawn");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//9-
	       	condition = "If the Published Date (Type: ITC Final) is not blank AND Published Date (Type: Initiation) is"
	       			+ " not blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature is not blank AND "
	       			+ "Will_You_Amend_The_Final is Yes AND Actual_Amended_Final_Determination_Sig is blank AND "
	       			+ "Segment_Outcome is not 'Full Rescission'";
	       	APITools.deleteRecordObject("Federal_Register__c", frIdF);
	    	record.clear();record.put("segment__c", adminReviewId);
	    	record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Initiation");
			String frIdI = APITools.createObjectRecord("Federal_Register__c", record);
			jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	//10-
	       	condition = "If the Published Date (Type: Preliminary) is blank AND Published Date (Type: Final) "
	       			+ "is not blank AND Actual_Preliminary_Signature is not blank AND Actual_Final_Signature "
	       			+ "is not blank AND Will_You_Amend_The_Final is"
	       			+ " Yes AND Actual_Amended_Final_Determination_Sig is blank AND Segment_Outcome is not 'Full Rescission'";
	       	APITools.deleteRecordObject("Federal_Register__c", frIdP);
	       	APITools.deleteRecordObject("Federal_Register__c", frIdITC);
	       	record.clear();record.put("segment__c", adminReviewId);
	    	record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Final");
			jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Amend Final", jObj.getString("Status__c"), condition);
	       	
       //D) - Hold
	        HtmlReport.addHtmlStepTitle("Validate Status - Hold","Title");
	       	//1
	       	condition = "The Litigation is Null AND Segment_Outcome is 'Full Rescission' "
	       			+ "THEN Published date (Type:Rescission) +30 or 45 days";
	      // 	APITools.deleteRecordObject("Federal_Register__c", frIdI);
	     	record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Actual_Final_Signature__c", "2019-10-31"); //>45
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Rescission");
			frIdR = APITools.createObjectRecord("Federal_Register__c", record);
			record.clear();
	       	record.put("Segment_Outcome__c", "Full Rescission");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Positive", "Hold", jObj.getString("Status__c"), condition);
	       	//2
	       	condition = "If the Litigation is not Null AND Segment_Outcome is "
	       			+ "'Full Rescission' THEN Published date (Type:Rescission) +30 or 45 days";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes"); record.put("Litigation_YesNo__c", "No");//Litigation is not Null
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	       			ADCVDLib.validateObjectStatus("Negative", "Hold", jObj.getString("Status__c"), condition);
	       	//3
	       	condition = "If the Litigation is Null AND Segment_Outcome is not "
	       			+ "'Full Rescission' THEN Published date (Type:Rescission) +30 or 45 days";
	       	//code = APITools.deleteRecordObject("Federal_Register__c", frIdR);
	       	record.clear();
	       	record.put("Segment_Outcome__c", "Withdrawn"); 
	       	record.put("Litigation_YesNo__c", ""); record.put("Litigation_Resolved__c", ""); //Litigation is Null
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Hold", jObj.getString("Status__c"), condition);
	       	//4
	       	condition = "If the Litigation is Null AND Segment_Outcome is 'Full Rescission' THEN Published date "
	       			+ "(Type: Final) +30 or 45 days";
	    	record.clear();
	       	record.put("segment__c", adminReviewId);
			record.put("Actual_Final_Signature__c", "2019-10-31"); //>45
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Rescission");
			frIdR = APITools.createObjectRecord("Federal_Register__c", record);
	       	record.clear();
	       	record.put("Segment_Outcome__c", "Full Rescission");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Hold", jObj.getString("Status__c"), condition);
	       	//5
	       	condition = "If the Litigation is Yes AND Segment_Outcome is not 'Full Rescission' THEN Published date"
	       			+ "(Type:Rescission) +30 or 45 days";
	       	APITools.deleteRecordObject("Federal_Register__c", frIdR);
	    	record.clear();
	    	record.put("Segment_Outcome__c", "Withdrawn");
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is Yes
	       	record.put("Litigation_Resolved__c", "No");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Hold", jObj.getString("Status__c"), condition);
   		//E) - Litigation
	        HtmlReport.addHtmlStepTitle("Validate Status - Litigation","Title");
	       	//1
	       	condition = "The Litigation is Yes AND Litigation_Resolved is No AND Litigation_Status "
	       			+ "is blank OR Litigation_Status is 'Not Active'";
	       	record.clear();
	    	//record.put("Segment_Outcome__c", "Withdrawn");//Litigation_Status  is 'Not Active'
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Positive", "Litigation", jObj.getString("Status__c"), condition);
	       	//2
	       	condition = "If the Litigation is NO AND Litigation_Resolved is No AND Litigation_Status "
	       			+ "is blank OR Litigation_Status is 'Not Active'";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "No");//Litigation is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Litigation", jObj.getString("Status__c"), condition);
	       	//3
	       	condition = "If the Litigation is Yes AND Litigation_Resolved is YES AND Litigation_Status is "
	       			+ "blank OR Litigation_Status is 'Not Active'";
	       	record.clear();
	    	record.put("Litigation_YesNo__c", "Yes");//Litigation  is 'Yes'
	       	record.put("Litigation_Resolved__c", "Yes");//Litigation_Resolved is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Litigation", jObj.getString("Status__c"), condition);
	       	//4
	       	condition = "If the Litigation is Yes AND Litigation_Resolved is No AND Litigation_Status is 'Active'";
	       	jObj = APITools.getRecordFromObject(sqlString);
	    	record.clear();
	    	record.put("Litigation_Status__c", "Active");//Litigation_Status is 'Active'
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Litigation", jObj.getString("Status__c"), condition);
	       	//5
	       	condition = "If the Litigation is NO AND Litigation_Resolved is YES AND  Litigation_Status is 'Active'";
	       	record.clear();
	    	record.put("Litigation_YesNo__c", "No");//Litigation  is 'No'
	       	record.put("Litigation_Resolved__c", "Yes");//Litigation_Resolved is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Litigation", jObj.getString("Status__c"), condition);
       	//F) - Customs
	        HtmlReport.addHtmlStepTitle("Validate Status - Customs","Title");
	       	//1
	       	condition = "The Litigation is Yes AND Litigation_Resolved is Yes AND Have_Custom_Instruction_been_sent is No";
	     	record.clear();
	    	record.put("Litigation_YesNo__c", "Yes");//Litigation  is 'Yes'
	       	record.put("Have_Custom_Instruction_been_sent__c", "No");//Have_Custom_Instruction_been_sent is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Positive", "Customs", jObj.getString("Status__c"), condition);
	       	//2
	       	condition = "If the Litigation is No AND Litigation_Resolved is Yes AND Have_Custom_Instruction_been_sent is No";
	       	record.clear();
	    	record.put("Litigation_YesNo__c", "No");//Litigation  is 'No'
	       	record.put("Litigation_Resolved__c", "Yes");//Litigation_Resolved is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Customs", jObj.getString("Status__c"), condition);
	       	//3
	       	condition = "If the Litigation is Yes AND Litigation_Resolved is NO AND Have_Custom_Instruction_been_sent is No";
	       	record.clear();
	    	record.put("Litigation_YesNo__c", "Yes");//Litigation  is 'Yes'
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Customs", jObj.getString("Status__c"), condition);
	       	//4
	       	condition = "If the Litigation is Yes AND Litigation_Resolved is Yes AND Have_Custom_Instruction_been_sent is YES";
	    	record.clear();
	    	record.put("Have_Custom_Instruction_been_sent__c", "Yes");//Have_Custom_Instruction_been_sent is YES
	       	record.put("Litigation_Resolved__c", "Yes");//Litigation_Resolved is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Customs", jObj.getString("Status__c"), condition);
	       	//5
	       	condition = "If the Litigation is NO AND Litigation_Resolved is NO AND Have_Custom_Instruction_been_sent is No";
	       	record.clear();
	    	record.put("Have_Custom_Instruction_been_sent__c", "No");//Have_Custom_Instruction_been_sent is No
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is No
	       	record.put("Litigation_YesNo__c", "No");//Litigation
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Customs", jObj.getString("Status__c"), condition);
	       	//6
	       	condition = "The Litigation is No AND Have_Custom_Instruction_been_sent is No";
	    	record.clear();
	       	record.put("Litigation_Resolved__c", "");//Litigation_Resolved is EMPTY
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Positive", "Customs", jObj.getString("Status__c"), condition);
	       	//7
	       	condition = "If the Litigation is YES AND Have_Custom_Instruction_been_sent is No";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is YES 
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Customs", jObj.getString("Status__c"), condition);
	       	//8
	       	condition = "If the Litigation is No AND Have_Custom_Instruction_been_sent is YES";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "No");//Litigation is No
	       	record.put("Have_Custom_Instruction_been_sent__c", "Yes");//Have_Custom_Instruction_been_sent is YES
	       	record.put("Litigation_Resolved__c", "");//Litigation_Resolved EMPTY
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Customs", jObj.getString("Status__c"), condition);
	       	//9
	       	condition = "If the Litigation is YES AND Have_Custom_Instruction_been_sent is YES";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Customs", jObj.getString("Status__c"), condition);
       	//G) - Close
	        HtmlReport.addHtmlStepTitle("Validate Status - Close","Title");
	       	//1
	       	condition = "The Litigation is Yes AND Litigation_Resolved is Yes AND Have_Custom_Instruction_been_sent is Yes";
	    	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is Yes
	       	record.put("Litigation_Resolved__c", "Yes");//Litigation_Resolved is Yes
	       	record.put("Have_Custom_Instruction_been_sent__c", "Yes");//Have_Custom_Instruction_been_sent is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Positive", "Closed", jObj.getString("Status__c"), condition);
	       	//2
	       	condition = "If the Litigation is NO AND Litigation_Resolved is Yes AND Have_Custom_Instruction_been_sent is Yes";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "No");//Litigation is No
	       	record.put("Litigation_Resolved__c", "Yes");//Litigation_Resolved is Yes
	       	record.put("Have_Custom_Instruction_been_sent__c", "Yes");//Have_Custom_Instruction_been_sent is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Closed", jObj.getString("Status__c"), condition);
	       	//3
	       	condition = "If the Litigation is Yes AND Litigation_Resolved is NO AND Have_Custom_Instruction_been_sent is Yes";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is Yes
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is No
	       	record.put("Have_Custom_Instruction_been_sent__c", "Yes");//Have_Custom_Instruction_been_sent is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Closed", jObj.getString("Status__c"), condition);
	       	//4
	       	condition = "If the Litigation is Yes AND Litigation_Resolved is Yes AND Have_Custom_Instruction_been_sent is NO";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is Yes
	       	record.put("Litigation_Resolved__c", "Yes");//Litigation_Resolved is Yes
	       	record.put("Have_Custom_Instruction_been_sent__c", "No");//Have_Custom_Instruction_been_sent is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Closed", jObj.getString("Status__c"), condition);
	       	//5
	       	condition = "If the Litigation is NO AND Litigation_Resolved is NO AND Have_Custom_Instruction_been_sent is NO";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "No");//Litigation is No
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is No
	       	record.put("Have_Custom_Instruction_been_sent__c", "No");//Have_Custom_Instruction_been_sent is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Closed", jObj.getString("Status__c"), condition);
	       	//6
	       	condition = "The Litigation is No AND Have_Custom_Instruction_been_sent is Yes";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "No");//Litigation is No
	       	record.put("Litigation_Resolved__c", "Empty");//Litigation_Resolved is Empty
	       	record.put("Have_Custom_Instruction_been_sent__c", "Yes");//Have_Custom_Instruction_been_sent is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Positive", "Closed", jObj.getString("Status__c"), condition);
	       	//7
	       	condition = "The Litigation is YES AND Have_Custom_Instruction_been_sent is Yes";
	    	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is Yes
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is Empty
	       	record.put("Have_Custom_Instruction_been_sent__c", "Yes");//Have_Custom_Instruction_been_sent is Yes
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Closed", jObj.getString("Status__c"), condition);
	       	//8
	       	condition = "The Litigation is No AND Have_Custom_Instruction_been_sent is NO";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "No");//Litigation is No
	       	record.put("Litigation_Resolved__c", "Empty");//Litigation_Resolved is Empty
	       	record.put("Have_Custom_Instruction_been_sent__c", "No");//Have_Custom_Instruction_been_sent is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Closed", jObj.getString("Status__c"), condition);
	       	//9
	       	condition = "The Litigation is YES AND Have_Custom_Instruction_been_sent is NO";
	       	record.clear();
	       	record.put("Litigation_YesNo__c", "Yes");//Litigation is Yes
	       	record.put("Litigation_Resolved__c", "No");//Litigation_Resolved is Empty
	       	record.put("Have_Custom_Instruction_been_sent__c", "No");//Have_Custom_Instruction_been_sent is No
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	       	testCaseStatus = testCaseStatus & 
	    		    ADCVDLib.validateObjectStatus("Negative", "Closed", jObj.getString("Status__c"), condition);
	       	
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
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Segment - Anti_Circumvention_Review");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_006");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Request_Filed__c", row.get("Request_Filed__c"));
		record.put("Application_Accepted__c", row.get("Application_Accepted__c"));		
		String antiCircumventionId = APITools.createObjectRecord("Segment__c", record);
		if(antiCircumventionId != null)
       {
	       	String sqlString = "select+Name+from+segment__c+where+id='"+antiCircumventionId+"'";
			//HtmlReport.addHtmlStepTitle("Validate all dates for a happy path","Title");
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
			String antiCircumventionName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+antiCircumventionName+"</span>", "Step", "pass", "" );
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/validate_admin_review_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> antiCircumventionDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "anti-circumvention", "");
	        HtmlReport.addHtmlStepTitle("VALIDATE DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        
	        //Request_Filed__c	Application_Accepted__c

	        for(LinkedHashMap<String, String> dates:antiCircumventionDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Application_Accepted__c", dates.get("Date_For_Weekend"));//Application_Accepted__c
			       	String code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAntiCircumventionReview(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Application_Accepted__c", dates.get("Date_For_Holiday"));
			       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAntiCircumventionReview(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Application_Accepted__c", dates.get("Date_For_Tolling"));
			       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAntiCircumventionReview(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
	       		}
	       	}
       }
	   else 
	   {
			failTestSuite("Create new Anti-Circumvention Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
		
		
		
		//testCaseStatus = ADCVDLib.createNewSegment(row);
		//testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentAntiCircumventionReview();
	}
	/**
	 * This method is for ADCVD segment(Changed Circumstances Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=7)
	void Create_Segment_Changed_Circumstances_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 3");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_007");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Request_Filed__c", row.get("Request_Filed__c"));
		record.put("Preliminary_Determination__c", row.get("Preliminary_Determination__c"));
		String changedCircumstanceId = APITools.createObjectRecord("Segment__c", record);
		if(changedCircumstanceId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	       	String changedCircumstanceName = jObj.getString("Name");
	       	updateHtmlReport("Create Changed Circumstances Review", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+changedCircumstanceName+"</span>", "Step", "pass", "" );
	       	testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentChangedCircumstancesReview(jObj);
	       	
	       	//update
	       	/*record.clear();
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
	       	}*/
       }
	   else 
	   {
			failTestSuite("Create new Circumstances Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
		
		
		//testCaseStatus = ADCVDLib.createNewSegment(row);
		//testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentChangedCircumstancesReview();
	}
	
	/**
	 * This method is for ADCVD segment(Expedited Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=8)
	void Create_Segment_Expedited_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 4");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_008");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Calculated_Initiation_Signature__c", row.get("Calculated_Initiation_Signature__c"));
		String expiditedReviewId = APITools.createObjectRecord("Segment__c", record);
		if(expiditedReviewId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	       	String expiditedReviewName = jObj.getString("Name");
	       	updateHtmlReport("Create Admin Review", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+expiditedReviewName+"</span>", "Step", "pass", "" );
	       	testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentExpeditedReview(jObj);
	       	
	       	//update
	       	/*record.clear();
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
	       	}*/
       }
	   else 
	   {
			failTestSuite("Create new Expidited Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
		
		//testCaseStatus = ADCVDLib.createNewSegment(row);
		//testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentExpeditedReview();
	}
	
	/**
	 * This method is for ADCVD segment(Shipper Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=9)
	void Create_Segment_New_Shipper_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 5");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_009");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Calculated_Initiation_Signature__c", row.get("Calculated_Initiation_Signature__c"));
		String newShipperReviewId = APITools.createObjectRecord("Segment__c", record);
		if(newShipperReviewId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	       	String newShipperReviewName = jObj.getString("Name");
	       	updateHtmlReport("Create New Shipper Review", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+newShipperReviewName+"</span>", "Step", "pass", "" );
	       	testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentNewShipperReview(jObj);
	       	
	       	//update
	       	/*record.clear();
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
	       	}*/
       }
	   else 
	   {
			failTestSuite("Create New Shipper Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
		
		
		//testCaseStatus = ADCVDLib.createNewSegment(row);
		//testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentNewShipperReview();
	}
	
	/**
	 * This method is for ADCVD segment(Scope Inquiry) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=10)
	void Create_Segment_Scope_Inquiry() throws Exception
	{
		printLog("Create_And_Validate_Segment - 6");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_010");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Request_Filed__c", row.get("Request_Filed__c"));
		record.put("Actual_Date_of_Decision_on_HoP__c", row.get("Actual_Date_of_Decision_on_HoP__c"));
		record.put("Decision_on_How_to_Proceed__c", row.get("Decision_on_How_to_Proceed__c"));
		record.put("Type_of_Scope_Ruling__c", row.get("Type_of_Scope_Ruling__c"));
		String ScopeInquiryId = APITools.createObjectRecord("Segment__c", record);
		if(ScopeInquiryId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", ScopeInquiryId));
	       	String ScopeInquiryName = jObj.getString("Name");
	       	updateHtmlReport("Create Scope Inquiry", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+ScopeInquiryName+"</span>", "Step", "pass", "" );
	       	testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentNewScoprInquiry(jObj);
	       	
	       	//update
	       	/*record.clear();
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
	       	}*/
       }
	   else 
	   {
			failTestSuite("Create Scope Inquiry", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
		
		
		//testCaseStatus = ADCVDLib.createNewSegment(row);
		//testCaseStatus = testCaseStatus & ADCVDLib.validateNewSegmentNewScoprInquiry();
	}
	/**
	 * This method is for ADCVD segment(Sunset Inquiry) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=11)
	void Create_Segment_Sunset_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 7");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_011");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Notice_of_intent_to_participate_Ips__c", "Yes");
		record.put("Domestic_Party_File_Substan_Response__c", "No");
		String sunsetReviewId = APITools.createObjectRecord("Segment__c", record);
		if(sunsetReviewId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	       	String sunsetReviewName = jObj.getString("Name");
	       	updateHtmlReport("Create Sunset Review", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+sunsetReviewName+"</span>", "Step", "pass", "" );
	       	//create FR init Federal_Register__c Federal_Register__c Published_Date__c Cite_Number__c
	       	record.clear();
	       	record.put("segment__c", sunsetReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Initiation");
			String frid = APITools.createObjectRecord("Federal_Register__c", record);
			//90Days
			HtmlReport.addHtmlStepTitle("Validate sunset 90 Day","Title");
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			testCaseStatus = testCaseStatus & ADCVDLib.validateSunSetReviewDatesByType(jObj, "90 Day", 
					row.get("Published_Date__c"));
			//120 Day
			record.clear();
			record.put("Domestic_Party_File_Substan_Response__c", "Yes");
			String code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
			HtmlReport.addHtmlStepTitle("Validate sunset 120 Day","Title");
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			testCaseStatus = testCaseStatus & ADCVDLib.validateSunSetReviewDatesByType(jObj, "120 Day", 
					row.get("Published_Date__c"));
			//240 Day
			record.clear();
			record.put("Review_to_address_zeroing_in_Segments__c", "Yes");
			record.put("Respondent_File_Substantive_Response__c", "Yes");
			code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
			HtmlReport.addHtmlStepTitle("Validate sunset 140 Day","Title");
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			testCaseStatus = testCaseStatus & ADCVDLib.validateSunSetReviewDatesByType(jObj, "240 Day", 
					row.get("Published_Date__c"));
       }
	   else 
	   {
			failTestSuite("Create new Sunset Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
	}
	

	/**
	 * This method is creating Litigation
	 * creation and validation
	*/
	@Test(enabled = true, priority=12)
	void Create_International_Litigation() throws Exception
	{
		printLog("Create_And_Validate_litigation");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_012");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("Petition__c", petitionId);
		record.put("RecordTypeId", recordType.get("Remand"));
		record.put("Expected_Final_Signature_Before_Ext__c", row.get("Expected_Final_Signature_Before_Ext__c"));
		litigationId = APITools.createObjectRecord("Litigation__c", record);
		if(litigationId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	       	litigationName = jObj.getString("Name");
	       	updateHtmlReport("Create International Litigation", "User is able to create a new litigation", 
					"Litigation id: <span class = 'boldy'>"+" "+litigationName+"</span>", "Step", "pass", "" );
	       	testCaseStatus = testCaseStatus & ADCVDLib.validateLitigationFields(jObj, "International Litigation");
       }
	   else 
	   {
			failTestSuite("Create new Litigation", "user is able to create Litigation", "Not as expected",
					"Step", "fail", "");
	   }
	}
	/**
	 * This method is creating Remand
	 * creation and validation
	*/
	@Test(enabled = true, priority=13)
	void Create_Remand() throws Exception
	{
		printLog("Create_And_Validate_Remand");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_013");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("Petition__c", petitionId);
		record.put("RecordTypeId", recordType.get("International Litigation"));
		record.put("Request_Filed__c", row.get("Request_Filed__c"));
		litigationId = APITools.createObjectRecord("Litigation__c", record);
		if(litigationId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	       	litigationName = jObj.getString("Name");
	       	updateHtmlReport("Create Remand", "User is able to create a new Remand", 
					"Remand id: <span class = 'boldy'>"+" "+litigationName+"</span>", "Step", "pass", "" );
	       	testCaseStatus =testCaseStatus & ADCVDLib.validateLitigationFields(jObj, "Remand");
       }
	   else 
	   {
			failTestSuite("Create new Remand", "user is able to create Remand", "Not as expected",
					"Step", "fail", "");
	   }
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
    	recordType.put("Anti-Circumvention Review","012t0000000TSjyAAG");
    	recordType.put("Changed Circumstances Review","012t0000000TSjzAAG");
    	recordType.put("Expedited Review","012t0000000TSk0AAG");
    	recordType.put("New Shipper Review","012t0000000TSk1AAG");
    	recordType.put("Scope Inquiry","012t0000000TSk2AAG");
    	recordType.put("Sunset Review","012t0000000TSk3AAG");
    	recordType.put("Remand","012t0000000TSjsAAG");
    	recordType.put("International Litigation","012t0000000TSjrAAG");
    	
    }
}
