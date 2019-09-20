package tests;
import static GuiLibs.GuiTools.failTestCase;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.getElementAttribute;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.replaceGui;
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
	static HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static LinkedHashMap<String, String> recordType = new LinkedHashMap<String, String>();
	static ArrayList<LinkedHashMap<String, String>> dataPool, dataPoolCase, dataPoolPetition, 
	dataPoolInvestigation, dataPoolOrder, dataPoolSegment,dataPoolLitigation, dataPoolRegression;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar todayCal = Calendar.getInstance();
	public static HttpClient httpclient;
	public static Date todayDate;
	public static DateFormat dateFormat;
	public static String todayStr, caseId, caseName, petitionId, petitionName,
	investigationId, investigationName, orderId, orderName,adminReviewId,
	adminReviewName;
	public static void main(String[] args) throws Exception 
	{
		initiateRecordType();
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		todayDate = new Date();
		todayStr = dateFormat.format(todayDate);
		todayCal.setTime(todayDate);
		ADCVDLib adcvdLibs = new ADCVDLib();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"/datapool/adcvd_regression_full_datapool.xlsx";
		dataPoolCase  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Case", "Active=TRUE");
		dataPoolPetition  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Petition", "Active=TRUE");
		dataPoolInvestigation  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Invetigation", "Active=TRUE");
		dataPoolOrder  = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Order", "Active=TRUE");
		dataPoolSegment = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Segments", "Active=TRUE");
		dataPoolRegression = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Regression Tcs", "Active=TRUE");
		dataPoolLitigation = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Litigations", "Active=TRUE");
		ADCVDLib.tollingDates = XlsxTools.readXlsxSheetAndFilter(dataPoolPath, "Tolling Dates", "Active=TRUE");
		dataPool = mergeDataPools(dataPoolCase, dataPoolPetition, dataPoolInvestigation, dataPoolOrder, dataPoolSegment,
				dataPoolLitigation, dataPoolRegression);
		
		mapConfInfos = guiTools.getConfigInfos();
		String url = mapConfInfos.get("LOGINURL");
		String grantService = mapConfInfos.get("GRANTSERVICE");
		String grantType = mapConfInfos.get("GRANTTYPE");
		String grantId = mapConfInfos.get("CLIENTID");
		String clientSecret = mapConfInfos.get("CLIENTSECRET");
		String userName = mapConfInfos.get("USERNAME");
		String password = mapConfInfos.get("PASSWORD");
		String accessToken = APITools.getAccesToken(url, grantService, grantType, grantId,
				clientSecret , userName, password);
		if(accessToken==null || accessToken.equals("Nothing"))
		{
			failTestSuite("Create new Case", "Connect through API",
						"Not as expected", "Step", "fail", "");
		}
		
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
	
	////////////////////////////////////
	
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
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "Self-Initiated");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String cvdCaseId = createNewCase(row, "C-");
		String cvdPetitionId = createNewPetition(row, cvdCaseId, "Self-Initiated");
		String cvdInvestigationIdName = createNewInvestigation(row, cvdPetitionId);
		//record.put("ADCVD_Case_Number__c", caseNameCvd);
		// align CVD to AD
		record.clear();
		record.put("AD_Investigation_Aligned_To__c", adInvestigationIdName.split("###")[0]);
		String code = APITools.updateRecordObject("Investigation__c", cvdInvestigationIdName.split("###")[0], record);
		if(code==null || !code.equals("204"))
		{
			failTestSuite("Align CVD to AD", "User should be able to link a CVD investigation to an AD investigation", 
					"Not as expected", "VP", "fail", "" );
		}
		else
		{
			updateHtmlReport("Align CVD to AD", "User is able to link CVD investigation to AD investigation", 
					 "<span class = 'boldy'>"+ cvdInvestigationIdName.split("###")[1]+"---->"+
							 adInvestigationIdName.split("###")[1]+"</span>", "VP", "pass", "" );
		}
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
		String sqlString = "select+id,name,Actual_Draft_Remand_Issues_to_DAS__c,Actual_Draft_Remand_Concurrence_to_DAS__c,"
				+ "Next_Office_Deadline__c,Next_Due_to_DAS_Deadline__c,Next_Major_Deadline__c,Final_Team_Meeting_Deadline__c,"
				+ "Final_Issues_Due_to_DAS__c,Final_Concurrence_Due_to_DAS__c,Calculated_Final_Signature__c,"
				+ "Draft_Remand_Concurrence_Due_to_DAS__c,Draft_Remand_Issues_Due_to_DAS__c,Calculated_Draft_Remand_release_to_party__c,"
				+ "Prelim_Team_Meeting_Deadline__c,Prelim_Concurrence_Due_to_DAS__c,Prelim_Issues_Due_to_DAS__c,"
				+ "Calculated_Preliminary_Signature__c,Expected_Final_Signature_Before_Ext__c,Request_Filed__c,"
				+ "Prelim_Extension_of_days__c,Final_Extension_of_days__c+from+litigation__c+where+id='litigationId'";
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "");
		HtmlReport.addHtmlStepTitle("Create 'International Litigation' and Validate Dates","Title");
		String litigationId = createNewLitigation(adPetitionId, "International Litigation");
		JSONObject jObj = APITools.getRecordFromObject(sqlString.replace("litigationId", litigationId));
		testCaseStatus =testCaseStatus & ADCVDLib.validateLitigationFields(jObj, "International Litigation");
		//Remand
		HtmlReport.addHtmlStepTitle("Create Remand and Validate Dates","Title");
		String remandId = createNewLitigation(adPetitionId, "Remand");
		jObj = APITools.getRecordFromObject(sqlString.replace("litigationId", remandId));
		testCaseStatus =testCaseStatus & ADCVDLib.validateLitigationFields(jObj, "Remand");
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
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String query = "select+Name,Calculated_Preliminary_Signature__c,Prelim_Team_Meeting_Deadline__c,"
				+ "Prelim_Issues_Due_to_DAS__c,Prelim_Concurrence_Due_to_DAS__c,Calculated_Final_Signature__c,"
				+ "Final_Issues_Due_to_DAS__c,Final_Concurrence_Due_to_DAS__c,Final_Team_Meeting_Deadline__c,"
				+ "Final_Announcement_Date__c,Est_ITC_Notification_to_DOC_of_Final_Det__c,Estimated_Order_FR_Published__c,"
				+ "Next_Announcement_Date__c,Next_Major_Deadline__c+from+investigation__c+where+id='investigationId'";
		JSONObject jObj = APITools.getRecordFromObject(query.replace("investigationId", adInvestigationIdName.split("###")[0]));
		HtmlReport.addHtmlStepTitle("I. AD CASE","Title");
		HtmlReport.addHtmlStepTitle("Check dates when petition first created","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(jObj, true);
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		record.put("Actual_Initiation_Signature__c", todayStr);
		record.put("Calculated_Initiation_Signature__c", todayStr);
		record.put("Petition_Outcome__c", "Self-Initiated");		
		String code = APITools.updateRecordObject("Petition__c", adPetitionId, record);
		HtmlReport.addHtmlStepTitle("Check dates when petition is converted to Self-Initiated","Title");
		jObj = APITools.getRecordFromObject(query.replace("investigationId", adInvestigationIdName.split("###")[0]));
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(jObj, false);
		record.clear();
       	record.put("Petition__c", adPetitionId);
		record.put("Published_Date__c", todayStr);
		record.put("Cite_Number__c", "None");
		record.put("Type__c", "Initiation");
		String fridI = APITools.createObjectRecord("Federal_Register__c", record);
		HtmlReport.addHtmlStepTitle("Check dates when an initaited FR associated with petition","Title");
		jObj = APITools.getRecordFromObject(query.replace("investigationId", adInvestigationIdName.split("###")[0]));
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(jObj, true);
		HtmlReport.addHtmlStepTitle("II. CVD CASE","Title");
		String cvdCaseId = createNewCase(row, "C-");
		String cvdPetitionId = createNewPetition(row, cvdCaseId, "");
		String cvdInvestigationIdName = createNewInvestigation(row, cvdPetitionId);
		jObj = APITools.getRecordFromObject(query.replace("investigationId", cvdInvestigationIdName.split("###")[0]));
		HtmlReport.addHtmlStepTitle("Check dates when petition first created","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(jObj, true);
		HtmlReport.addHtmlStepTitle("Check dates when petition is converted to Self-Initiated","Title");
		record.clear();
		record.put("Actual_Initiation_Signature__c", todayStr);
		record.put("Calculated_Initiation_Signature__c", todayStr);
		record.put("Petition_Outcome__c", "Self-Initiated");		
		code = APITools.updateRecordObject("Petition__c", cvdPetitionId, record);
		jObj = APITools.getRecordFromObject(query.replace("investigationId", cvdInvestigationIdName.split("###")[0]));
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(jObj, false);
		HtmlReport.addHtmlStepTitle("Check dates when an initaited FR associated with petition","Title");
		record.clear();
       	record.put("Petition__c", cvdPetitionId);
		record.put("Published_Date__c", todayStr);
		record.put("Cite_Number__c", "None");
		record.put("Type__c", "Initiation");
		fridI = APITools.createObjectRecord("Federal_Register__c", record);
		jObj = APITools.getRecordFromObject(query.replace("investigationId", cvdInvestigationIdName.split("###")[0]));
		testCaseStatus =testCaseStatus & ADCVDLib.checkSelfInitiatedDates(jObj, true);
	}
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=4)
	void Align_NSR_To_AR() throws Exception
	{
		LinkedHashMap<String, String> arDates = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> nsrDatesBefore = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> nsrDatesAfter = new LinkedHashMap<String, String>();
		printLog("Align_NSR_To_AR");
		JSONObject jObj = null;
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_004");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		String adminReviewId = createNewSegment(orderId, "Administrative Review");
		String query = "select+id,Name,Prelim_Team_Meeting_Deadline__c,Prelim_Issues_Due_to_DAS__c,"
					+ "Prelim_Concurrence_Due_to_DAS__c,Calculated_Preliminary_Signature__c,Final_Team_Meeting_Deadline__c,"
					+ "Final_Issues_Due_to_DAS__c,Final_Concurrence_Due_to_DAS__c,Calculated_Final_Signature__c,"
					+ "Final_Announcement_Date__c,Next_Office_Deadline__c,Next_Due_to_DAS_Deadline__c,"
					+ "Next_Major_Deadline__c,Next_Announcement_Date__c+from+segment__c+where+id='segmentId'";
		String arName="";
		jObj = APITools.getRecordFromObject(query.replace("segmentId", adminReviewId));
		arDates = ADCVDLib.readSegmentDates(jObj);
		arName = jObj.getString("Name");
		String shipperReviewId = createNewSegment(orderId, "New Shipper Review");
		jObj = APITools.getRecordFromObject(query.replace("segmentId", shipperReviewId));
		nsrDatesBefore = ADCVDLib.readSegmentDates(jObj);
		String nsrName = jObj.getString("Name");
	   //Aling SFR to AR
	    HtmlReport.addHtmlStepTitle("Align New shipper review to Admin review","Title");
	    LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
	    record.put("Administrative_Review_Aligned_To__c", adminReviewId);
	    String code = APITools.updateRecordObject("Segment__c", shipperReviewId, record);
	    if(code.equals("204"))
	    {
		   updateHtmlReport("Align NSR to AR", "user is able to align NSR segment to AR segement[ <span class = 'boldy'>"+
				   ""+nsrName+"--->"+arName+"]</span>", "As expected",
					"VP", "pass", "");
		   jObj = APITools.getRecordFromObject(query.replace("segmentId", shipperReviewId));
		   nsrDatesAfter = ADCVDLib.readSegmentDates(jObj);
		   testCaseStatus =testCaseStatus & ADCVDLib.alignNsrToArAndValidate(arDates, nsrDatesBefore, nsrDatesAfter);
	    }
	    else
	    {
		   updateHtmlReport("Align NSR to AR", "user is able to align NSR segment to AR segement", "Not as expected",
					"VP", "fail", "");
	    }
	}
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=5)
	void Petition_Status_Validation() throws Exception
	{
		printLog("Petition_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_005");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "");		
		testCaseStatus = testCaseStatus & ADCVDLib.validatePetitionStatus(adPetitionId);
	}
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=6)
	void Investigation_Status_Validation() throws Exception
	{		
		printLog("Investigation_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_006");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		row.put("ADCVD_Case", "A-");
		row.put("ADCVD_Case_Type", "AD ME");
		//HtmlReport.addHtmlStepTitle("Create New AD Investigation","Title");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		testCaseStatus = testCaseStatus & ADCVDLib.validateInvestigationStatus(adInvestigationIdName.split("###")[0]);
	}

	/**
	 * This method is Admin review status
	*/
	@Test(enabled = true, priority=7)
	void Admin_Review_Status_Validation() throws Exception
	{  
		printLog("Admin_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_007");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		//record.put("ADCVD_Order__c", orderId);
		String adminReviewId = createNewSegment(orderId, "Administrative Review");
		testCaseStatus = testCaseStatus & ADCVDLib.validateSegmentStatus_A(adminReviewId, "Administrative Review");
	}
	
	/**
	 * This method is Expedited_Review_Status_Validation status
	*/
	@Test(enabled = true, priority=8)
	void Expedited_Review_Status_Validation() throws Exception
	{
		printLog("Expedited_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_008");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		//record.put("ADCVD_Order__c", orderId);
		String adminReviewId = createNewSegment(orderId, "Expedited Review");
		testCaseStatus = testCaseStatus & ADCVDLib.validateSegmentStatus_A(adminReviewId, "Expedited Review");
	}
	/**
	 * This method is New_Shipper_Review_Status_Validation status
	*/
	@Test(enabled = true, priority=9)
	void New_Shipper_Review_Status_Validation() throws Exception
	{
		printLog("New_Shipper_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_009");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		//record.put("ADCVD_Order__c", orderId);
		String adminReviewId = createNewSegment(orderId, "New Shipper Review");
		testCaseStatus = testCaseStatus & ADCVDLib.validateSegmentStatus_A(adminReviewId, "New Shipper Review");
	}
	
	/**
	 * This method is New_Shipper_Review_Status_Validation status
	*/
	@Test(enabled = true, priority=10)
	void Changed_Circumstance_Review_Status_Validation() throws Exception
	{
		printLog("Changed_Circumstance_Review_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_010");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		//record.put("ADCVD_Order__c", orderId);
		String adminReviewId = createNewSegment(orderId, "Changed Circumstances Review");
		testCaseStatus = testCaseStatus & ADCVDLib.validateSegmentStatus_B(adminReviewId, "Changed Circumstances Review");
	}
	/**
	 * This method is New_Shipper_Review_Status_Validation status
	*/
	@Test(enabled = true, priority=11)
	void Anticircumvention_Status_Validation() throws Exception
	{
		printLog("Anticircumvention_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_011");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		//record.put("ADCVD_Order__c", orderId);
		String adminReviewId = createNewSegment(orderId, "Anti-Circumvention Review");
		testCaseStatus = testCaseStatus & ADCVDLib.validateSegmentStatus_B(adminReviewId, "Anti-Circumvention Review");
	}
	
	/**
	 * This method is for scope inquirey status validation
	*/
	@Test(enabled = true, priority=12)
	void Scope_Status_Validation() throws Exception
	{
		printLog("Scope_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_012");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		//record.put("ADCVD_Order__c", orderId);
		String scopeInquiryId = createNewSegment(orderId, "Scope Inquiry");
		testCaseStatus = testCaseStatus & ADCVDLib.validateSegmentStatus_C(scopeInquiryId, "Scope Inquiry");
	}
	
	/**
	 * This method is for Sunset status validation
	*/
	@Test(enabled = true, priority=13)
	void Sunset_Status_Validation() throws Exception
	{
		printLog("Sunset_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_013");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		System.out.println("start Test");
		String adCaseId = createNewCase(row, "A-");		
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String orderId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		//record.put("ADCVD_Order__c", orderId);
		String sunsetReviewId = createNewSegment(orderId, "Sunset Review");
		testCaseStatus = testCaseStatus & ADCVDLib.validateSegmentStatus_D(sunsetReviewId, "Sunset Review");
	}
	
	/**
	 * This method is Remand Litigation statuses
	*/
	@Test(enabled = true, priority=14)
	void Remand_Status_Validation() throws Exception
	{
		printLog("Remand_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_014");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String remandId = createNewLitigation(adPetitionId, "Remand");
		testCaseStatus = testCaseStatus & ADCVDLib.validateLitigationStatus(remandId, "remand");
	}
		
	/**
	 * This method is validating International Litigation statuses
	*/
	@Test(enabled = true, priority=15)
	void International_Litigation_Status_Validation() throws Exception
	{
		printLog("International_Litigation_Status_Validation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_015");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "");
		String litigationId = createNewLitigation(adPetitionId, "International Litigation");
		testCaseStatus = testCaseStatus & ADCVDLib.validateLitigationStatus(litigationId, "Interntional Litigation");
	}
	
	
	/**
	 * This method is for ADCVD case creation and validation
	*/
	@Test(enabled = true, priority=16)
	void Create_Adcvd_Case() throws Exception
	{
		printLog("Create_Adcvd_Case");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_016");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		String adCaseId = createNewCase(row, "A-");
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
		/*}else 
		{
			failTestSuite("Create new Case", "User is able to create a new Case",
						"Not as expected", "Step", "fail", "");
		}*/
	}
	
	/**
	 * This method is for ADCVD Petition creation and validation
	*/
	@Test(enabled = true, priority=17)
	void Create_And_Validate_Petition() throws Exception
	{
		printLog("Create_And_Validate_Petition");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_017");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		record.put("ADCVD_Case_Number__c", caseName);
		record.put("ADCVD_Case__c", caseId);
		record.put("Petition_Filed__c", row.get("Petition_Filed__c"));
		record.put("Initiation_Extension_of_days__c", row.get("Initiation_Extension_of_days__c"));
		ADCVDLib.petitionId = APITools.createObjectRecord("Petition__c", record);
		petitionId = ADCVDLib.petitionId;
		//ADCVDLib.petitionOutcome = noNullVal(jObj.getString("Petition_Outcome__c"));
		//*********************************I. VALIDATE DATES WHEN THEY FALL ON WEEKEND************************
       	//*****************************************************************************************************
		if(petitionId != null)
	    {
		    JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("petitionId", petitionId));
		   	petitionName = jObj.getString("Name");
		   	updateHtmlReport("Create Petition", "User is able to create a new Petition", 
					"Petition <span class = 'boldy'>"+" "+petitionName+"</span>", "Step", "pass", "" );
			String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> petitionDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "petition", "");
	        HtmlReport.addHtmlStepTitle("I. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	       for(LinkedHashMap<String, String> dates:petitionDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Petition_Filed__c", dates.get("Date_For_Weekend"));
			       	String code = APITools.updateRecordObject("Petition__c", petitionId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("petitionId", petitionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validatePetitionFields(jObj, dates.get("Field_Name"), "Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Petition_Filed__c", dates.get("Date_For_Holiday"));
			       	String code = APITools.updateRecordObject("Petition__c", petitionId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("petitionId", petitionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validatePetitionFields(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Petition_Filed__c", dates.get("Date_For_Tolling"));
			       	String code = APITools.updateRecordObject("Petition__c", petitionId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("petitionId", petitionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validatePetitionFields(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
	       		}
	       	}
	        //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        String actualValue, expectedValue; 
	        String sqlString = "select+Name,Next_Major_Deadline__c,Next_Office_Deadline__c,Initiation_Issues_Due_to_DAS__c,"
	        		+ "Initiation_Concurrence_Due_to_DAS__c,Next_Announcement_Date__c,Next_Due_To_DAS_Deadline__c,"
	        		+ "Calculated_Initiation_Signature__c,Initiation_Announcement_Date__c+"
	        		+ "from+petition__c+where+id='"+petitionId+"'";
	        //Next_Major_Deadline__c
	        //1
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        jObj = APITools.getRecordFromObject(sqlString);
	        String clause = "IF Actual_Initiation_Signature__c is blank OR Petition_Outcome__c is "
	        		+ "blank THEN  Calculated_Initiation_Signature__c ";
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //Next Due to DAS Deadline
	        HtmlReport.addHtmlStepTitle("1) - Next Due To DAS_ Deadline","Title");
	        //1
	        clause = "IF Actual_Initiation_Signature__c is blank OR Actual_Initiation_Issues_to_DAS__c"
	        		+ " is blank THEN Initiation_Issues_Due_to_DAS__c";
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Initiation_Concurrence_to_DAS__c is blank OR Actual_Initiation_Signature__c"
	        		+ " is blank  THEN Initiation_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	String code = APITools.updateRecordObject("petition__c", petitionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause  = "IF Actual_Initiation_Signature__c is blank OR Petition_Outcome__c is blank THEN"
	        		+ " Calculated_Initiation_Signature__c "; 
	        record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("petition__c", petitionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    //initiate
	        record.clear();
	        record.put("Actual_Initiation_Signature__c", "");
			record.put("Actual_Initiation_Issues_to_DAS__c", "");
			record.put("Actual_Initiation_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("petition__c", petitionId, record);
	  //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
	        //1
	        clause = "IF Actual_Initiation_Signature__c is blank OR Actual_Initiation_Issues_to_DAS__c"
	        		+ " is blank THEN Initiation_Issues_Due_to_DAS__c";
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Initiation_Concurrence_to_DAS__c is blank OR Actual_Initiation_Signature__c"
	        		+ " is blank  THEN Initiation_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("petition__c", petitionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause  = "IF Actual_Initiation_Signature__c is blank OR Petition_Outcome__c is blank THEN"
	        		+ " Calculated_Initiation_Signature__c "; 
	        record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("petition__c", petitionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	      //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF Initiation_Announcement_Date__c date has passed THEN clear Next_Announcement_Date__c";
			todayCal.setTime(todayDate);
			record.clear();
		    todayCal.add(Calendar.DATE, -30);
			record.put("Petition_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("petition__c", petitionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        //expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, "");
	        //2
	        clause = "IF Initiation_Announcement_Date__c date has NOT passed THEN  Initiation_Announcement_Date__c";
			todayCal.setTime(todayDate);
			record.clear();
		    todayCal.add(Calendar.DATE, 30);
			record.put("Petition_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("petition__c", petitionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();
	        record.put("Actual_Initiation_Signature__c", "");
			record.put("Actual_Initiation_Issues_to_DAS__c", "");
			record.put("Actual_Initiation_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("petition__c", petitionId, record); //Next_Office_Deadline__c
	    }
		else 
		{
			failTestSuite("Create new Petition", "User is able to create a new Petition",
						"Not as expected", "Step", "fail", "");
		}
		
	}//petition
	
	/**
	 * This method is for ADCVD Investigation creation and validation
	*/
	@Test(enabled = true, priority=18)
	void Create_And_Validate_Investigation() throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Investigation");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_018");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		//Investigation__c
		record.put("Petition__c", petitionId);
		record.put("Amend_the_Preliminary_Determination__c", "Yes");	
		record.put("Will_you_Amend_the_Final__c", "Yes");
		investigationId = APITools.createObjectRecord("Investigation__c", record);
		if(investigationId != null)
       {
	       	JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("investigationId", investigationId));
	       	investigationName = jObj.getString("Name");
	       	updateHtmlReport("Create Investigation", "User is able to create a new Investigation", 
					"investigatioon id: <span class = 'boldy'>"+" "+investigationName+"</span>", "Step", "pass", "" );
	       	
	        //*********************************I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        String actualValue, expectedValue; 
	        String sqlString = row.get("Query").replace("investigationId", investigationId);
	        //Next_Major_Deadline__c
	        //1
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String clause = "IF Actual_Preliminary_Signature__c is blank AND Investigation_Outcome__c is blank THEN  "
	        		+ "Calculated_Preliminary_Signature__c";
	        jObj = APITools.getRecordFromObject(sqlString);	        
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Amended_Prelim_Determination_Sig__c is blank AND Investigation_Outcome__c is blank "
	        		+ "AND Amend_the_Preliminary_Determination__c is 'Yes' THEN  Calc_Amended_Prelim_Determination_Sig__c";
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
    		record.put("Amend_the_Preliminary_Determination__c", "Yes");
	       	String code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);	  
	       	actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calc_Amended_Prelim_Determination_Sig__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause = "If Actual_Final_Signature__c is blank AND Investigation_Outcome__c is blank THEN Calculated_Final_Signature__c";
	        record.clear();
    		record.put("Actual_Amended_Prelim_Determination_Sig__c", todayStr);
    		//record.put("Amend_the_Preliminary_Determination__c", "Yes");
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);	  
	       	actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        
	        //4
	        clause = "IF Actual_Amended_Final_Signature__c is blank AND Investigation_Outcome__c is blank AND "
	        		+ "Will_you_Amend_the_Final__c = 'Yes' THEN Calculated_Amended_Final_Signature__c";
	        record.clear();
    		record.put("Actual_Final_Signature__c", todayStr);
    		record.put("Will_you_Amend_the_Final__c", "Yes");
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);	  
	       	actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //5
	        clause = "IF Published_Date__c (Type: Order) IS blank THEN Calculated_Order_FR_Signature__c"; 
	        record.clear();
    		record.put("Actual_Amended_Final_Signature__c", todayStr);
    		//record.put("Will_you_Amend_the_Final__c", "Yes");
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);	  
	       	actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Order_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();
	        record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Amended_Prelim_Determination_Sig__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Amended_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	        //Next Due to DAS Deadline
	       	HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline","Title");
	       	//1
	       	clause = "IF Actual_Preliminary_Signature__c is blank AND Signature_of_Prelim_Postponement_FR__c is blank AND "
	       			+ "Calculated_Postponement_of_PrelimDeterFR__c has not passed THEN Calculated_Postponement_of_PrelimDeterFR__c";
	       	record.clear();
    		record.put("Petition_Filed__c", todayStr);
	       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Postponement_of_PrelimDeterFR__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	       	//2
	       	clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  "
	        		+ "is blank THEN Prelim_Issues_Due_to_DAS__c";
	       	todayCal.setTime(todayDate);
		    todayCal.add(Calendar.MONTH, -7);
	    	record.clear();
    		record.put("Petition_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
    		//record.put("Segment_Outcome__c", "");
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	       	//5
	       	clause = "IF Amend_the_Preliminary_Determination__c is Yes AND Actual_Amended_Prelim_Determination_Sig__c "
	       			+ " is blank AND Actual_Amend_Prelim_Issues_to_DAS__c  is blank THEN Amend_Prelim_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	       	//6
	    	clause = "IF Amend_the_Preliminary_Determination__c is Yes AND Actual_Amended_Prelim_Determination_Sig__c  is blank "
	    			+ "AND Actual_Amend_Prelim_Concurrence_to_DAS__c  is blank THEN Amend_Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Prelim_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	    	//7
	    	clause = "IF Actual_Amended_Prelim_Determination_Sig__c is blank AND "
	    			+ "Amend_the_Preliminary_Determination__c is 'Yes' THEN  Calc_Amended_Prelim_Determination_Sig__c";
			record.clear();
			record.put("Actual_Amend_Prelim_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calc_Amended_Prelim_Determination_Sig__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//8
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amended_Prelim_Determination_Sig__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//10
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//11
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank"
					+ " AND Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr);	
			//record.put("Will_you_Amend_the_Final__c", "Yes");
			//record.put("Segment_Outcome__c", "Completed");
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//12
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank "
					+ "AND Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//13
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank "
					+ "THEN Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	      //14
			clause = "IF Published_Date__c (Type: Order) IS blank THEN Calculated_Order_FR_Signature__c";
			record.clear();
			record.put("Actual_Amended_Final_Signature__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Order_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        
    //initiate
	        record.clear();	        
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");
	        record.put("Actual_Amend_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Amend_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Amended_Prelim_Determination_Sig__c", "");
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Final_Signature__c", "");
	        record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Amended_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	        //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
	        //1
			/*clause = "IF Actual_Preliminary_Signature__c is blank AND Calculated_ITC_Prelim_Determination__c  "
					+ "has not passed THEN Calculated_ITC_Prelim_Determination__c";
			record.clear();
    		record.put("Petition_Filed__c", todayStr);
	       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_ITC_Prelim_Determination__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);*/
			//2
			clause = "IF Actual_Preliminary_Signature__c is blank AND Calculated_Prelim_Extension_Request_File__c  "
					+ "has not passed THEN Calculated_Prelim_Extension_Request_File__c";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.MONTH, -2);	 // make Calculated_ITC_Prelim_Determination__c passed		    
	    	record.clear();
    		record.put("Petition_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Petition__c", petitionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Prelim_Extension_Request_File__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Preliminary_Signature__c is blank AND Signature_of_Prelim_Postponement_FR__c  is blank"
					+ " AND Calculated_Postponement_of_PrelimDeterFR__c  has not passed "
					+ "THEN Calculated_Postponement_of_PrelimDeterFR__c";
			for (int days = 142; days<149; days++)
			{
				todayCal.setTime(todayDate);
				todayCal.add(Calendar.DATE, -days);	//make Calculated_Prelim_Extension_Request_File__c passed	    
				record.clear();
				record.put("Petition_Filed__c", dateFormat.format(todayCal.getTime()));
				code = APITools.updateRecordObject("Petition__c", petitionId, record);	 
				jObj = APITools.getRecordFromObject(sqlString);
				
				if (ADCVDLib.datePassed(jObj.getString("Calculated_Prelim_Extension_Request_File__c"))
						& !ADCVDLib.datePassed(jObj.getString("Calculated_Postponement_of_PrelimDeterFR__c")))
					break;
			}
			
	       	
	       	
	       	
	       	
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Postponement_of_PrelimDeterFR__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			/*//4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c has"
					+ " not passed THEN Prelim_Team_Meeting_Deadline__c";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.DATE, -195);		    
	    	record.clear();
    		record.put("Petition_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
	       	record.clear();
	       	record.put("segment__c", petitionId);
			record.put("Published_Date__c", todayStr);
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Preliminary");
			String frIdP = APITools.createObjectRecord("Federal_Register__c", record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        code = APITools.deleteRecordObject("Federal_Register__c", frIdP);*/
	        //5
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c is blank THEN "
	        		+ "Prelim_Issues_Due_to_DAS__c";
	        todayCal.setTime(todayDate);
		    todayCal.add(Calendar.DATE, -195);		    
	    	record.clear();
    		record.put("Petition_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
    		jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
			record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);
    		jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //7
	        clause = "IF Actual_Preliminary_Signature__c is blank THEN Calculated_Preliminary_Signature__c";
	        record.clear();
			record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);
    		jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	       	//8
	        clause = "IF Amend_the_Preliminary_Determination__c is Yes AND Actual_Amended_Prelim_Determination_Sig__c "
	       			+ " is blank AND Actual_Amend_Prelim_Issues_to_DAS__c  is blank THEN Amend_Prelim_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	       	//9
	    	clause = "IF Amend_the_Preliminary_Determination__c is Yes AND Actual_Amended_Prelim_Determination_Sig__c  is blank "
	    			+ "AND Actual_Amend_Prelim_Concurrence_to_DAS__c  is blank THEN Amend_Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Prelim_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	    	//10
	    	clause = "IF Actual_Amended_Prelim_Determination_Sig__c is blank AND "
	    			+ "Amend_the_Preliminary_Determination__c is 'Yes' THEN  Calc_Amended_Prelim_Determination_Sig__c";
			record.clear();
			record.put("Actual_Amend_Prelim_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calc_Amended_Prelim_Determination_Sig__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //11
			clause = "IF Actual_Final_Signature__c is"
					+ " blank AND Final_Team_Meeting_Deadline__c has not passed THEN Final_Team_Meeting_Deadline__c";
			record.clear();
			record.put("Actual_Amended_Prelim_Determination_Sig__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//12
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.MONTH, -2); // make Final_Team_Meeting_Deadline__c pass		    
	    	record.clear();
    		record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//13
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//14
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
			jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//15
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr); 
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//16
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//17
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank THEN "
					+ "Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //18
			clause = "IF Est_ITC_Notification_to_DOC_of_Final_Det__c has not pased THEN "
					+ "Est_ITC_Notification_to_DOC_of_Final_Det__c";
			record.clear();
			record.put("Actual_Amended_Final_Signature__c", todayStr);
			code = APITools.updateRecordObject("Investigation__c", investigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Est_ITC_Notification_to_DOC_of_Final_Det__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //19
			clause = "IF Published_Date__c (Type: Order) IS blank THEN Calculated_Order_FR_Signature__c ";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.MONTH, -3); // make Est_ITC_Notification_to_DOC_of_Final_Det__c passed  		    
	    	record.clear();
    		record.put("Actual_Final_Signature__c", dateFormat.format(todayCal.getTime()));
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);		
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Order_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();	        
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");
	        record.put("Actual_Amend_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Amend_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Amended_Prelim_Determination_Sig__c", "");
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Final_Signature__c", "");
	        record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Amended_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	      //Next Announcement Date
	       	/*
	       	IF Preliminary_Announcement_Date__c date has passed THEN clear Next_Announcement_Date__c, ELSE IF Investigation_Outcome__c equals Completed or blank THEN Preliminary_Announcement_Date__c
	       	IF Amended_Preliminary_Announcement_Date__c date has passed THEN clear Next_Announcement_Date__c ELSE IF Next_Announcement_Date__c is not blank THEN leave as is
	       	IF Final_Announcement_Date__c date has passed THEN clear Next_Announcement_Date__c ELSE IF Investigation_Outcome__c equals Completed or blank THEN Final_Announcement_Date__c ELSE IF Next_Announcement_Date__c is not blank THEN leave as is
	       	IF Amended_Final_Announcement_Date__c date has passed THEN clear Next_Announcement_Date__c ELSE IF Next_Announcement_Date__c is not blank THEN leave as is
	       	*/
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "if Preliminary_Announcement_Date__c is not passed Then Preliminary_Announcement_Date__c";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.DATE, 1); // make Preliminary_Announcement_Date__c not passed  		    
	    	record.clear();
    		record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);		
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "if Amended_Preliminary_Announcement_Date__c is not passed Then Amended_Preliminary_Announcement_Date__c";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.DATE, -5); // make Preliminary_Announcement_Date__c passed  		    
	    	record.clear();
    		record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);		
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amended_Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause = "if Final_Announcement_Date__c is not passed Then Final_Announcement_Date__c";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.DATE, -5); // make Amended_Preliminary_Announcement_Date__c passed  		    
	    	record.clear();
    		record.put("Actual_Amended_Prelim_Determination_Sig__c", dateFormat.format(todayCal.getTime()));
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);		
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "if Final_Announcement_Date__c is not passed Then Final_Announcement_Date__c";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.DATE, -5); // make Final_Announcement_Date__c passed  		    
	    	record.clear();
    		record.put("Actual_Final_Signature__c", dateFormat.format(todayCal.getTime()));
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);		
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amended_Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //5
	        clause = "if all of (Preliminary_Announcement_Date__c, Amended_Preliminary_Announcement_Date__c,"
	        		+ "Final_Announcement_Date__c, Amended_Final_Announcement_Date__c) are passed then "
	        		+ "clear Next_Announcement_Date";
			todayCal.setTime(todayDate);
		    todayCal.add(Calendar.DATE, -5); // make Amended_Final_Announcement_Date__c passed  		    
	    	record.clear();
    		record.put("Actual_Amended_Final_Signature__c", dateFormat.format(todayCal.getTime()));
    		code = APITools.updateRecordObject("Investigation__c", investigationId, record);		
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        //expectedValue = ADCVDLib.noNullVal(jObj.getString("Actual_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, "");
	        record.clear();	        
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");
	        record.put("Actual_Amend_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Amend_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Amended_Prelim_Determination_Sig__c", "");
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Final_Signature__c", "");
	        record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Amended_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Investigation__c", investigationId, record);
	     
	       	//*********************************II. VALIDATE DATES WHEN THEY FALL ON WEEKEND************************
		      //*****************************************************************************************************
				String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
		        ArrayList<LinkedHashMap<String, String>> petitionDates  = 
		        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "investigation", "");
		        HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
		        		+ "HOLIDAY AND TOLLING DAY","Title");
		        for(LinkedHashMap<String, String> dates:petitionDates)
		       	{
		       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
		       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
		       				&& !dates.get("Date_For_Weekend").equals(""))
		       		{
			       		record.clear();
			    		record.put("Petition_Filed__c", dates.get("Date_For_Weekend"));
				       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
				       	jObj = APITools.getRecordFromObject(row.get("Query").replace("investigationId", investigationId));
				       	testCaseStatus = testCaseStatus & 
			       		ADCVDLib.validateInvestigationFields(jObj, dates.get("Field_Name"), "Weekend", dates.get("Date_For_Weekend"));
		       		}
		       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
		       				&& !dates.get("Date_For_Holiday").equals(""))
		       		{
				       	record.clear();
			    		record.put("Petition_Filed__c", dates.get("Date_For_Holiday"));
				       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
				       	jObj = APITools.getRecordFromObject(row.get("Query").replace("investigationId", investigationId));
				       	testCaseStatus = testCaseStatus & 
			       		ADCVDLib.validateInvestigationFields(jObj, dates.get("Field_Name"),
			       				"Holiday", dates.get("Date_For_Holiday"));
		       		}
		       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
		       				&& !dates.get("Date_For_Tolling").equals(""))
		       		{
				       	record.clear();
			    		record.put("Petition_Filed__c", dates.get("Date_For_Tolling"));
				       	code = APITools.updateRecordObject("Petition__c", petitionId, record);
				       	jObj = APITools.getRecordFromObject(row.get("Query").replace("investigationId", investigationId));
				       	testCaseStatus = testCaseStatus & 
			       		ADCVDLib.validateInvestigationFields(jObj, dates.get("Field_Name"), 
			       				"Tolling Day", dates.get("Date_For_Tolling"));
		       		}
		       	}
       }
		else 
		{
			failTestSuite("Create new Investigation", "user is able to create a new investigation",
						"Not as expected", "Step", "fail", "");
		}
	}
	/**
	 * This method is for ADCVD order creation and validation
	*/
	@Test(enabled = true, priority=19)
	void Create_Order() throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Order");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_019");
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
	}
	
	/**
	 * This method is for ADCVD segment(Administrative Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=20)
	void Create_And_Validate_Segment_Administrative_Review() throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Segment - 1");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_020");
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
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
	       	adminReviewName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+adminReviewName+"</span>", "Step", "pass", "" );
	        //*********************************I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue; 
	        sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c,Calculated_Amended_Final_Signature__c+"
	        		+ "from+segment__c+where+id='"+adminReviewId+"'";
	        //Next_Major_Deadline__c
	        //1
	        String clause = "IF Actual_Preliminary_Signature__c is blank, Segment_Outcome__c is blank"
	        		+ "THEN Calculated_Preliminary_Signature__c";
	        record.clear();
    		//record.put("Actual_Preliminary_Signature__c", todayStr);
    		//record.put("Actual_Final_Signature__c", todayStr);
	       //	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Final_Signature__c is blank THEN Segment_Outcome__c is blank THEN"
	        		+ "Calculated_Final_Signature__c";
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
    		//record.put("Actual_Final_Signature__c", todayStr);
	       	String code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause  = "IF Actual_Amended_Final_Signature__c is blank AND Will_you_amended_the_final__c = Yes"
	        		+ "Segment_Outcome__c is not blank THEN Calculated_Amended_Final_Signature__c"; 
	        record.clear();
    		record.put("Actual_Final_Signature__c", todayStr);
    		record.put("Will_you_Amend_the_Final__c", "Yes");
    		record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();
	        record.put("Will_you_Amend_the_Final__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");
	        record.put("Actual_Final_Signature__c", "");
	        record.put("Segment_Outcome__c", "");
	        code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline","Title");
	        //Next Due to DAS Deadline
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
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
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
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank"
					+ " AND Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr);	
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
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank "
					+ "THEN Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);	
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
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -9);
			record.clear();
			record.put("Final_Date_of_Anniversary_Month__c", dateFormat.format(todayCal.getTime()));	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Next_Office_Deadline__c  is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -13);
			record.clear();
			record.put("Final_Date_of_Anniversary_Month__c", dateFormat.format(todayCal.getTime()));
			record.put("Actual_Preliminary_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
	       	//
	       	record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);
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
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF published_date_c (type: Final) is blank AND Actual_Final_Signaturec is not blank THEN "
					+ "Calculated_Final_FR_signature_c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr); 
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
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//12
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank THEN "
					+ "Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        record.put("Segment_Outcome__c", "");
			todayCal.setTime(todayDate);
			record.clear();
		    todayCal.add(Calendar.DATE, 10);
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
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
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
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
			record.put("Will_you_Amend_the_Final__c", "Yes");
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
	       	//*********************************II. VALIDATE DATES WHEN THEY FALL ON WEEKEND************************
	       	//*****************************************************************************************************
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title"); 
	        ArrayList<LinkedHashMap<String, String>> adminReviewDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "admin review", "");
	        for(LinkedHashMap<String, String> dates:adminReviewDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Final_Date_of_Anniversary_Month__c", dates.get("Date_For_Weekend"));
			       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAdministrativeReview(jObj, dates.get("Field_Name"), "Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Final_Date_of_Anniversary_Month__c", dates.get("Date_For_Holiday"));
			       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
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
			       	code = APITools.updateRecordObject("Segment__c", adminReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", adminReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAdministrativeReview(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
	       		}
	       	}
       }
	   else 
	   {
			failTestSuite("Create new Admin review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
	}
	/**
	 * This method is for ADCVD segment(Anti Circumvention Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=21)
	void Create_And_Validate_Segment_Anti_Circumvention_Review() throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		printLog("Create_And_Validate_Segment - Anti_Circumvention_Review");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_021");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		//record.put("Request_Filed__c", row.get("Request_Filed__c"));
		record.put("Application_Accepted__c", row.get("Application_Accepted__c"));
		record.put("Type_of_Circumvention_Inquiry__c", "Later-Developed Merchandise");
		String antiCircumventionId = APITools.createObjectRecord("Segment__c", record);
		if(antiCircumventionId != null)
       {
	       	String sqlString = "select+Name+from+segment__c+where+id='"+antiCircumventionId+"'";
			//HtmlReport.addHtmlStepTitle("Validate all dates for a happy path","Title");
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
			String antiCircumventionName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+antiCircumventionName+"</span>", "Step", "pass", "" );
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> antiCircumventionDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "anti-circumvention", "");
	       
	        //*********************************I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue;
	        sqlString = "select+Name,Next_Major_Deadline__c,Actual_Initiation_Signature__c,"
	        		+ "Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c,Calculated_Initiation_Signature__c+"
	        		+ "from+segment__c+where+id='"+antiCircumventionId+"'";
	        //Next_Major_Deadline__c
	        //1
	        String clause = "IF Actual_Initiation_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) THEN Calculated_Initiation_Signature__c";
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) THEN Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	String code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause  = "IF Actual_Preliminary_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) THEN Calculated_Final_Signature__c "; 
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();	 
	        record.put("Actual_Initiation_Signature__c", "");	        
			record.put("Actual_Preliminary_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	//Next Due to DAS Deadline
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline", "Title");        
	        //1
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Issues_to_DAS__c is "
					+ "blank THEN Initiation_Issues_Due_to_DAS__c";
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Concurrence_to_DAS__c "
					+ "is blank THEN Initiation_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Initiation_Signature__c is blank THEN Calculated_Initiation_Signature__c";
			record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  "
	        		+ "is blank THEN Prelim_Issues_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //5
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
    		record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//7
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();	        
	        record.put("Actual_Initiation_Signature__c", "");
	        record.put("Actual_Initiation_Issues_to_DAS__c", "");
	        record.put("Actual_Initiation_Concurrence_to_DAS__c", "");	        
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");	        
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
			//1
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Issues_to_DAS__c is "
					+ "blank THEN Initiation_Issues_Due_to_DAS__c";			
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Concurrence_to_DAS__c "
					+ "is blank THEN Initiation_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Application_Accepted__c", todayStr);
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Initiation_Signature__c is blank THEN Calculated_Initiation_Signature__c";
			record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);	       	
	        //4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c "
					+ "is not past THEN Prelim_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  is blank "
					+ "THEN Prelim_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -5);
			record.clear();
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Preliminary_Signature__c is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //9
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -11);
			record.clear();
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//10
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF published_date_c (type: Final) is blank AND Actual_Final_Signaturec is not blank THEN "
					+ "Calculated_Final_FR_signature_c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr); 
			record.put("Segment_Outcome__c", "Completed");	
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
			todayCal.add(Calendar.DATE, 10);
			record.clear();
		    record.put("Segment_Outcome__c", "");
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	       // code = APITools.deleteRecordObject("Federal_Register__c", frIdR);
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
			record.put("Actual_Initiation_Issues_to_DAS__c", "");
			record.put("Actual_Initiation_Concurrence_to_DAS__c", "");
			record.put("Actual_Initiation_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	        HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:antiCircumventionDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Application_Accepted__c", dates.get("Date_For_Weekend"));//Application_Accepted__c
			       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAntiCircumventionReview(jObj, dates.get("Field_Name").trim(), 
		       				"Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Application_Accepted__c", dates.get("Date_For_Holiday"));
		    		//Final_Extension_of_days__c
			       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAntiCircumventionReview(jObj, dates.get("Field_Name").trim(),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Application_Accepted__c", dates.get("Date_For_Tolling"));
		    		//
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
		    			record.put("Final_Extension_of_days__c", "1");
			       	code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", antiCircumventionId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentAntiCircumventionReview(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
			       	record.clear();
			       	record.put("Final_Extension_of_days__c", "");
			       	 code = APITools.updateRecordObject("Segment__c", antiCircumventionId, record);
	       		}
	       	}
	        
	       }
	   else 
	   {
			failTestSuite("Create new Anti-Circumvention Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
	}
	/**
	 * This method is for creation and validation ADCVD segment
	 * (Changed Circumstances Review) 
	*/
	@Test(enabled = true, priority=22)
	void Create_And_Validate_Segment_Changed_Circumstances_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 3");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_022");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));//
		record.put("Preliminary_Determination__c", "Yes");
		record.put("Request_Filed__c", row.get("Request_Filed__c"));
		record.put("Preliminary_Determination__c", row.get("Preliminary_Determination__c"));
		String changedCircumstanceId = APITools.createObjectRecord("Segment__c", record);
		if(changedCircumstanceId != null)
		{
			String sqlString = "select+Name+from+segment__c+where+id='"+changedCircumstanceId+"'";
			//HtmlReport.addHtmlStepTitle("Validate all dates for a happy path","Title");
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
			String changedCircumstanceName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+changedCircumstanceName+"</span>", "Step", "pass", "" );
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> changedCircumstanceDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "changed-circumstance", "");
	        //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue;
	       
	        sqlString = "select+Name,Next_Major_Deadline__c,Actual_Initiation_Signature__c,"
	        		+ "Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c,Calculated_Initiation_Signature__c+"
	        		+ "from+segment__c+where+id='"+changedCircumstanceId+"'";
	        //Next_Major_Deadline__c
	        //1
	        String clause = "IF Actual_Initiation_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) THEN Calculated_Initiation_Signature__c";
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) THEN Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	String code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause  = "IF Actual_Preliminary_Signature__c is blank AND Published_Date__c "
	        		+ "(Type:Rescission) THEN Calculated_Final_Signature__c "; 
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();	 
	        record.put("Actual_Initiation_Signature__c", "");	        
			record.put("Actual_Preliminary_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	//Next Due to DAS Deadline
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline", "Title");        
	        //1
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Issues_to_DAS__c is "
					+ "blank THEN Initiation_Issues_Due_to_DAS__c";
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Concurrence_to_DAS__c "
					+ "is blank THEN Initiation_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Initiation_Signature__c is blank THEN Calculated_Initiation_Signature__c";
			record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  "
	        		+ "is blank THEN Prelim_Issues_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //5
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
    		record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//7
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();	        
	        record.put("Actual_Initiation_Signature__c", "");
	        record.put("Actual_Initiation_Issues_to_DAS__c", "");
	        record.put("Actual_Initiation_Concurrence_to_DAS__c", "");	        
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");	        
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
			//1
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Issues_to_DAS__c is "
					+ "blank THEN Initiation_Issues_Due_to_DAS__c";			
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Concurrence_to_DAS__c "
					+ "is blank THEN Initiation_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Application_Accepted__c", todayStr);
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Initiation_Signature__c is blank THEN Calculated_Initiation_Signature__c";
			record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);	       	
	        //4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c "
					+ "is not past THEN Prelim_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  is blank "
					+ "THEN Prelim_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -7);
			record.clear();
			//record.put("Application_Accepted__c", dateFormat.format(todayCal.getTime()));
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Preliminary_Signature__c is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //9
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -11);
			record.clear();
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//10
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//11
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//12
			clause = "IF published_date_c (type: Final) is blank AND Actual_Final_Signaturec is not blank THEN "
					+ "Calculated_Final_FR_signature_c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr); 
			record.put("Segment_Outcome__c", "Completed");	
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
			todayCal.add(Calendar.DATE, 10);
			record.clear();
		    record.put("Segment_Outcome__c", "");
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	       // code = APITools.deleteRecordObject("Federal_Register__c", frIdR);
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
			record.put("Actual_Initiation_Issues_to_DAS__c", "");
			record.put("Actual_Initiation_Concurrence_to_DAS__c", "");
			record.put("Actual_Initiation_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       	HtmlReport.addHtmlStepTitle("II.VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:changedCircumstanceDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Request_Filed__c", dates.get("Date_For_Weekend"));//Application_Accepted__c
			       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentChangedCircumstancesReview(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Request_Filed__c", dates.get("Date_For_Holiday"));
			       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentChangedCircumstancesReview(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Request_Filed__c", dates.get("Date_For_Tolling"));
		    		//
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
		    			record.put("Final_Extension_of_days__c", "4");
			       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", changedCircumstanceId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentChangedCircumstancesReview(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
			       	record.clear();
			       	record.put("Final_Extension_of_days__c", "");
			       	code = APITools.updateRecordObject("Segment__c", changedCircumstanceId, record);
	       		}
	       	}
       }
	   else 
	   {
			failTestSuite("Create new Circumstances Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
	}
	
	/**
	 * This method is for ADCVD segment(Expedited Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=23)
	void Create_And_Validate_Segment_Expedited_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment -4");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_023");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Calculated_Initiation_Signature__c", todayStr);
		String expiditedReviewId = APITools.createObjectRecord("Segment__c", record);
		if(expiditedReviewId != null)
       {
			String sqlString = "select+Name+from+segment__c+where+id='"+expiditedReviewId+"'";
			//HtmlReport.addHtmlStepTitle("Validate all dates for a happy path","Title");
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
			String changedCircumstanceName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+changedCircumstanceName+"</span>", "Step", "pass", "" );
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> changedCircumstanceDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "Expedited_Review", "");
	      //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue; 
	        sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c,Calculated_Amended_Final_Signature__c+"
	        		+ "from+segment__c+where+id='"+expiditedReviewId+"'";
	        //Next_Major_Deadline__c
	        jObj = APITools.getRecordFromObject(sqlString);
	        String clause = "IF Actual_Preliminary_Signature__c is blank AND Published_Date__c "
	        		+ " (Type:Rescission) is blank THEN Calculated_Preliminary_Signature__c";
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	String code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause  = "IF Actual_Amended_Final_Signature__c is blank AND Published_Date__c  (Type:Rescission)"
	        		+ " is blank AND Will_you_Amend_the_Final__c = 'Yes' THEN Calculated_Amended_Final_Signature__c"; 
	        record.clear();
    		record.put("Actual_Final_Signature__c", todayStr);
    		record.put("Will_you_Amend_the_Final__c", "Yes");
    		record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);		       	
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();
	        record.put("Actual_Preliminary_Signature__c", "");
	        record.put("Actual_Final_Signature__c", "");
	        record.put("Will_you_Amend_the_Final__c", "");
    		record.put("Segment_Outcome__c", "");
	        code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline","Title");
	        //Next Due to DAS Deadline
	        //1
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  "
	        		+ "is blank THEN Prelim_Issues_Due_to_DAS__c";
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
    		record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//4
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank"
					+ " AND Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr);	
			record.put("Will_you_Amend_the_Final__c", "Yes");
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank "
					+ "AND Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank "
					+ "THEN Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();	        
			//record.put("Will_you_Amend_the_Final__c", "");
			record.put("Segment_Outcome__c", "");
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
	       	//Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
	        //1
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c "
					+ "is not past THEN Prelim_Team_Meeting_Deadline__c";
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  is blank "
					+ "THEN Prelim_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -7);
			record.clear();
			record.put("Calculated_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Next_Office_Deadline__c  is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -3);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			//record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF published_date_c (type: Final) is blank AND Actual_Final_Signaturec is not blank THEN "
					+ "Calculated_Final_FR_signature_c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr); 
			record.put("Segment_Outcome__c", "Completed");	
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//10
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
	       	record.put("segment__c", expiditedReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Final");
			String frIdR = APITools.createObjectRecord("Federal_Register__c", record);
			record.clear();
			record.put("Will_you_Amend_the_Final__c", "Yes"); 
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//11
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//12
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank THEN "
					+ "Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	      //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        record.put("Segment_Outcome__c", "");
			todayCal.setTime(todayDate);
			record.clear();
		    todayCal.add(Calendar.DATE, 10);
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        code = APITools.deleteRecordObject("Federal_Register__c", frIdR);
	        record.clear();	        
			//record.put("Will_you_Amend_the_Final__c", "");
			record.put("Segment_Outcome__c", "");
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
	       	HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:changedCircumstanceDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Calculated_Initiation_Signature__c", dates.get("Date_For_Weekend"));
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
			    	record.put("Final_Extension_of_days__c", "1");
			       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentExpiditedReview(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Calculated_Initiation_Signature__c", dates.get("Date_For_Holiday"));
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
			    	record.put("Final_Extension_of_days__c", "1");
			       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentExpiditedReview(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Calculated_Initiation_Signature__c", dates.get("Date_For_Tolling"));
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
		    		record.put("Final_Extension_of_days__c", "1");
			       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", expiditedReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentExpiditedReview(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
	       		}
	       		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
	       		{
	       			record.clear();
			       	record.put("Final_Extension_of_days__c", "");
			       	code = APITools.updateRecordObject("Segment__c", expiditedReviewId, record);
	       		}
	       	}
       }
	   else 
	   {
			failTestSuite("Create new Expidited Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
	}
	
	/**
	 * This method is for ADCVD segment(Shipper Review) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=24)
	void Create_And_Validate_Segment_New_Shipper_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 5");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_024");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		todayCal.setTime(todayDate);
		if (!ADCVDLib.isBusinessDay(todayCal))
		{
			todayCal.add(Calendar.DATE, 2);
		}
		record.put("Calculated_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
		String newShipperReviewId = APITools.createObjectRecord("Segment__c", record);
		if(newShipperReviewId != null)
       {
			String sqlString = "select+Name+from+segment__c+where+id='"+newShipperReviewId+"'";
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
			String newShipperReviewName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+newShipperReviewName+"</span>", "Step", "pass", "" );
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> changedCircumstanceDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "Shipper_Review", "");
	        //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue; 
	        sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Initiation_Signature__c,Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c,Calculated_Amended_Final_Signature__c+"
	        		+ "from+segment__c+where+id='"+newShipperReviewId+"'";
	        //Next_Major_Deadline__c
	        //1
	       /* jObj = APITools.getRecordFromObject(sqlString);
	        String clause = "IF Actual_Initiation_Signature__c is blank THEN Calculated_Initiation_Signature__c";
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);*/
	        //2
	        String clause = "IF Actual_Preliminary_Signature__c is blank AND Published_Date__c "
	        		+ " (Type:Rescission) is blank THEN Calculated_Preliminary_Signature__c ";
	        record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	String code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause  = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c"; 
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause  = "IF Actual_Amended_Final_Signature__c is blank AND Published_Date__c  "
	        		+ "(Type:Rescission) is blank AND Will_you_Amend_the_Final__c = 'Yes' THEN "
	        		+ "Calculated_Amended_Final_Signature__c"; 
	        record.clear();
    		record.put("Actual_Final_Signature__c", todayStr);
    		record.put("Will_you_Amend_the_Final__c", "Yes");
    		record.put("Segment_Outcome__c", "Completed");
    		//record.put("Calculated_Amended_Final_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);  
	        //initiate
	        record.clear();
	        record.put("Will_you_Amend_the_Final__c", "");
	        record.put("Actual_Initiation_Signature__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");
	        record.put("Actual_Final_Signature__c", "");
	        record.put("Segment_Outcome__c", "");
	        record.put("Calculated_Amended_Final_Signature__c", "");
	        code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	        
	      //Next Due to DAS Deadline
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline", "Title");        
	        //1
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Issues_to_DAS__c is "
					+ "blank THEN Initiation_Issues_Due_to_DAS__c";
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Concurrence_to_DAS__c "
					+ "is blank THEN Initiation_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Initiation_Signature__c is blank THEN Calculated_Initiation_Signature__c";
			record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  "
	        		+ "is blank THEN Prelim_Issues_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //5
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
    		record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//7
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	      //10
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Will_you_Amend_the_Final__c", "Yes"); 
			record.put("Actual_Final_Signature__c", todayStr); 
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//11
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//12
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank THEN "
					+ "Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();	        
	        record.put("Actual_Initiation_Signature__c", "");
	        record.put("Actual_Initiation_Issues_to_DAS__c", "");
	        record.put("Actual_Initiation_Concurrence_to_DAS__c", "");	        
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");	        
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Final_Signature__c", "");
	        record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	        record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
			//1
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Issues_to_DAS__c is "
					+ "blank THEN Initiation_Issues_Due_to_DAS__c";			
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Initiation_Signature__c is blank AND Actual_Initiation_Concurrence_to_DAS__c "
					+ "is blank THEN Initiation_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Application_Accepted__c", todayStr);
    		record.put("Actual_Initiation_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Initiation_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Initiation_Signature__c is blank THEN Calculated_Initiation_Signature__c";
			record.clear();
    		record.put("Actual_Initiation_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Initiation_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);	       	
	        //4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c "
					+ "is not past THEN Prelim_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));	
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  is blank "
					+ "THEN Prelim_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -7);
			record.clear();
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Preliminary_Signature__c is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //9
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -3);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//10
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//11
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//12
			clause = "IF published_date_c (type: Final) is blank AND Actual_Final_Signaturec is not blank THEN "
					+ "Calculated_Final_FR_signature_c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr); 
			record.put("Segment_Outcome__c", "Completed");	
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	      //13
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Issues_to_DAS__c is blank THEN Amend_Final_Issues_Due_to_DAS__c";
			
			record.clear();
	       	record.put("segment__c", newShipperReviewId);
			record.put("Published_Date__c", row.get("Published_Date__c"));
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Final");
			String frIdR = APITools.createObjectRecord("Federal_Register__c", record);
			record.clear();
			record.put("Will_you_Amend_the_Final__c", "Yes"); 
			record.put("Actual_Final_Signature__c", "");
			//record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//14
			clause = "IF Will_you_Amend_the_Final__c is Yes AND Actual_Amended_Final_Signature__c is blank AND "
					+ "Actual_Amend_Final_Concurrence_to_DAS__c is blank THEN Amend_Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Amend_Final_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Amend_Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//15
			clause = "IF Will_you_Amend_the_Final__c is  Yes AND Actual_Amended_Final_Signature__c is blank THEN "
					+ "Calculated_Amended_Final_Signature__c";
			record.clear();
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Amended_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	      //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        record.put("Segment_Outcome__c", "");
			todayCal.setTime(todayDate);
			record.clear();
		    todayCal.add(Calendar.DATE, 10);
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();	        
			//record.put("Will_you_Amend_the_Final__c", "");
			record.put("Segment_Outcome__c", "");
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Initiation_Issues_to_DAS__c", "");
			record.put("Actual_Initiation_Concurrence_to_DAS__c", "");
			record.put("Actual_Initiation_Signature__c", "");
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       	////////////////////////II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND///////////
	       	//////////////////////////////////////////////////////////////////////////////////////////
	       	HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:changedCircumstanceDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Calculated_Initiation_Signature__c", dates.get("Date_For_Weekend"));
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
			    	record.put("Final_Extension_of_days__c", "1");
			       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentShipperReview(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Calculated_Initiation_Signature__c", dates.get("Date_For_Holiday"));
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
			    	record.put("Final_Extension_of_days__c", "1");
			       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentShipperReview(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Calculated_Initiation_Signature__c", dates.get("Date_For_Tolling"));
		    		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
		    		record.put("Final_Extension_of_days__c", "1");
			       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", newShipperReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentShipperReview(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
	       		}
	       		if(dates.get("Field_Name").equals("Final_Announcement_Date__c"))
	       		{
	       			record.clear();
			       	record.put("Final_Extension_of_days__c", "");
			       	code = APITools.updateRecordObject("Segment__c", newShipperReviewId, record);
	       		}
	       	}
       }
	   else 
	   {
			failTestSuite("Create New Shipper Review", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
	}
	
	/**
	 * This method is for ADCVD segment(Scope Inquiry) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=25)
	void Create_And_Validate_Segment_Scope_Inquiry() throws Exception
	{
		printLog("Create_And_Validate_Segment - 6");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_025");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("ADCVD_Order__c", orderId);
		record.put("RecordTypeId", recordType.get(row.get("Segment_Type")));
		record.put("Request_Filed__c", todayStr);//row.get("Request_Filed__c")
		record.put("Actual_Date_of_Decision_on_HoP__c", todayStr);//row.get("Actual_Date_of_Decision_on_HoP__c")
		record.put("Decision_on_How_to_Proceed__c", row.get("Decision_on_How_to_Proceed__c"));
		record.put("Type_of_Scope_Ruling__c", row.get("Type_of_Scope_Ruling__c"));
		String scopeInquiryId = APITools.createObjectRecord("Segment__c", record);
		if(scopeInquiryId != null)
       {
			String sqlString = "select+Name+from+segment__c+where+id='"+scopeInquiryId+"'";
			//HtmlReport.addHtmlStepTitle("Validate all dates for a happy path","Title");
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
			String ScopeInquiryName = jObj.getString("Name");
	       	updateHtmlReport("Create segment", "User is able to create a new segment", 
					"Segment id: <span class = 'boldy'>"+" "+ScopeInquiryName+"</span>", "Step", "pass", "" );
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	      //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue; 
	        sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c,Deadline_for_Decision_on_How_to_Proceed__c+"
	        		+ "from+segment__c+where+id='"+scopeInquiryId+"'";
	        //Next_Major_Deadline__c
	        //1
	        String clause = "IF Actual_Decision_On_How_to_Proceed__c is blank THEN Decision_on_How_to_Proceed__c";
	        record.clear();
    		record.put("Actual_Date_of_Decision_on_HoP__c", "");
	       	String code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Deadline_for_Decision_on_How_to_Proceed__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Type_of_scope_ruling__c  = Formal"
	        		+ " AND Published_Date__c (Type:Rescission) THEN Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Date_of_Decision_on_HoP__c", jObj.getString("Deadline_for_Decision_on_How_to_Proceed__c"));
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause = "IF Actual_Final_Signature__c is blank AND Published_Date__c  (Type:Formal) "
	        		+ "AND Published_Date__c (Type:Rescission) THEN Calculated_Final_Signature__c";

	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", jObj.getString("Calculated_Preliminary_Signature__c"));
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", "");
    		code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
    		HtmlReport.addHtmlStepTitle("1) - Next Due to DAS Deadline","Title");
	        //Next Due to DAS Deadline
    		 //1
	        clause = "IF Actual_Date_of_Decision_on_HoP__c is blank OR Actual_Decision_on_HOP_Issues_to_DAS__c is "
	        		+ "blank THEN Decision_on_HOP_Issues_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Date_of_Decision_on_HoP__c", "");
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Decision_on_HOP_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//2
	        clause = "IF Actual_Date_of_Decision_on_HoP__c is blank AND Actual_Decision_on_HOP_Concurrence_toDAS__c "
	        		+ "is blank THEN Decision_on_HOP_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Decision_on_HOP_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Decision_on_HOP_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause = "IF Actual_Date_of_Decision_on_HoP__c  is blank THEN Deadline_for_Decision_on_How_to_Proceed__c";
	        record.clear();
    		record.put("Actual_Decision_on_HOP_Concurrence_toDAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Deadline_for_Decision_on_How_to_Proceed__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  "
	        		+ "is blank THEN Prelim_Issues_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Date_of_Decision_on_HoP__c", jObj.getString("Deadline_for_Decision_on_How_to_Proceed__c"));
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //5
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c "
	        		+ "is blank THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Segment_Outcome__c is blank THEN "
    				+ "Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
    		record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//7
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", jObj.getString("Calculated_Preliminary_Signature__c"));	
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
			clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is "
					+ "blank THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    //initiate
	        record.clear();	        
			//record.put("Will_you_Amend_the_Final__c", "");
			//record.put("Segment_Outcome__c", "");
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");			
			record.put("Actual_Decision_on_HOP_Concurrence_toDAS__c", "");
			record.put("Actual_Decision_on_HOP_Issues_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Date_of_Decision_on_HoP__c", "");
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	       	//Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
	       	//1
	        clause = "IF Actual_Date_of_Decision_on_HoP__c is blank OR Actual_Decision_on_HOP_Issues_to_DAS__c is "
	        		+ "blank THEN Decision_on_HOP_Issues_Due_to_DAS__c";
	        record.clear();
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Decision_on_HOP_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//2
	        clause = "IF Actual_Date_of_Decision_on_HoP__c is blank AND Actual_Decision_on_HOP_Concurrence_toDAS__c "
	        		+ "is blank THEN Decision_on_HOP_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Decision_on_HOP_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Decision_on_HOP_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        
	        //3
	        clause = "IF Actual_Date_of_Decision_on_HoP__c  is blank THEN Deadline_for_Decision_on_How_to_Proceed__c";
	        record.clear();
    		record.put("Actual_Decision_on_HOP_Concurrence_toDAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Deadline_for_Decision_on_How_to_Proceed__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c "
					+ "is not past THEN Prelim_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Date_of_Decision_on_HoP__c", jObj.getString("Deadline_for_Decision_on_How_to_Proceed__c"));
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  is blank "
					+ "THEN Prelim_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -2);
			record.clear();
			record.put("Actual_Date_of_Decision_on_HoP__c", dateFormat.format(todayCal.getTime()));	
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Preliminary_Signature__c is blank AND Next_Office_Deadline__c  is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", jObj.getString("Calculated_Preliminary_Signature__c"));
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //9
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -4);
			record.clear();
			record.put("Actual_Date_of_Decision_on_HoP__c", dateFormat.format(todayCal.getTime()));
			//record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//10
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//11
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	      //Next Announcement Date
	    	record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");			
			record.put("Actual_Decision_on_HOP_Concurrence_toDAS__c", "");
			record.put("Actual_Decision_on_HOP_Issues_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Signature__c", "");
			//record.put("Actual_Date_of_Decision_on_HoP__c", "");
			code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
			
			
			
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank then preliminary_Announcement_Date";
	        /*record.put("Segment_Outcome__c", "");
			todayCal.setTime(todayDate);
			record.clear();
		    todayCal.add(Calendar.DATE, 10);*/
			record.put("Actual_Preliminary_Signature__c", todayStr);
			record.put("Calculated_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed then preliminary_Announcement_Date";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is Completed then final_Announcement_Date";
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.MONTH, -3);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Calculated_Preliminary_Signature__c", "");
			record.put("Actual_Final_Signature__c", todayStr);
			record.put("Calculated_Final_Signature__c", todayStr);
			record.put("Actual_Date_of_Decision_on_HoP__c", dateFormat.format(todayCal.getTime()));
			//record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			//record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        /*clause = "IF final_Announcement_Date is not passed and segment_outcome is Completed then final_Announcement_Date";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);*/
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
	       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
	        ArrayList<LinkedHashMap<String, String>> scopeInquiryDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "Scope_Inquiry", "");
	        HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:scopeInquiryDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Actual_Date_of_Decision_on_HoP__c", dates.get("Date_For_Weekend"));//Application_Accepted__c
			       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentScopeInquiry(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"));
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Actual_Date_of_Decision_on_HoP__c", dates.get("Date_For_Holiday"));
		    		//Final_Extension_of_days__c
			       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentScopeInquiry(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"));
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Actual_Date_of_Decision_on_HoP__c", dates.get("Date_For_Tolling"));
			       	code = APITools.updateRecordObject("Segment__c", scopeInquiryId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", scopeInquiryId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateNewSegmentScopeInquiry(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"));
	       		}
	       	}
       }
	   else 
	   {
			failTestSuite("Create Scope Inquiry", "user is able to create segment", "Not as expected",
					"Step", "fail", "");
	   }
	}
	/**
	 * This method is for ADCVD segment(Sunset Inquiry) 
	 * creation and validation
	*/
	@Test(enabled = true, priority=26)
	void Create_And_Validate_Segment_Sunset_Review() throws Exception
	{
		printLog("Create_And_Validate_Segment - 7");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_026");
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
	       	record.clear();
	       	record.put("segment__c", sunsetReviewId);
			record.put("Published_Date__c", todayStr);
			record.put("Cite_Number__c", "None");
			record.put("Type__c", "Initiation");
			String fridI = APITools.createObjectRecord("Federal_Register__c", record);
			//90Days
			//HtmlReport.addHtmlStepTitle("Validate sunset 90 Day","Title");
			//jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			//testCaseStatus = testCaseStatus & ADCVDLib.validateSunSetReviewDatesByType(jObj, "90 Day", 
				//	row.get("Published_Date__c"));
			//120 Day
			record.clear();
			record.put("Domestic_Party_File_Substan_Response__c", "Yes");
			String code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
			//HtmlReport.addHtmlStepTitle("Validate sunset 120 Day","Title");
			//jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			//testCaseStatus = testCaseStatus & ADCVDLib.validateSunSetReviewDatesByType(jObj, "120 Day", 
				//	row.get("Published_Date__c"));
			//240 Day
			record.clear();
			record.put("Review_to_address_zeroing_in_Segments__c", "Yes");
			record.put("Respondent_File_Substantive_Response__c", "Yes");
			code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
			//HtmlReport.addHtmlStepTitle("Validate sunset 140 Day","Title");
			//jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			//testCaseStatus = testCaseStatus & ADCVDLib.validateSunSetReviewDatesByType(jObj, "240 Day", 
					//row.get("Published_Date__c"));
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> sunsetReviewDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "Sunset_Review", "");
	        HtmlReport.addHtmlStepTitle("I. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
			//*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue; 
	        String sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Preliminary_Signature__c,"
	        		+ "Calculated_Final_Signature__c+"
	        		+ "from+segment__c+where+id='"+sunsetReviewId+"'";
	        //Next_Major_Deadline__c
	        //1
	        String clause = "If 240:  IF Actual_Preliminary_Signature__c is blank THEN Calculated_Preliminary_Signature__c";
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", jObj.getString("Calculated_Preliminary_Signature__c"));
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", "");
    		code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
    		 //Next Due to DAS Deadline
	        //1
    		clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c "
    				+ "AND Sunset_Review_Type__c is 240 THEN Prelim_Issues_Due_to_DAS__c";
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank  "
    				+ "AND Sunset_Review_Type__c is 240 THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
	        clause = "IF Actual_Preliminary_Signature__c is blank  AND Segment_Outcome__c is blank "
	        		+ "AND Sunset_Review_Type__c is "
    				+ "240  THEN Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
    		record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//4
	        clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
	        		+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
	        clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c is "
	        		+ "blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
	        clause = "IF Actual_Final_Signature__c is blank AND Segment_Outcome__c is blank "
	        		+ "THEN Calculated_Final_Signature__c";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();	        
			record.put("Segment_Outcome__c", "");
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Amend_Final_Issues_to_DAS__c", "");
			record.put("Actual_Amend_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
	       	//Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
	        //1
	       	clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c is not "
	       			+ "past AND Sunset_Review_Type__c is 240 THEN Prelim_Team_Meeting_Deadline__c";
			jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c AND Sunset_Review_Type__c "
	       			+ "is 240 THEN Prelim_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -4); //make Prelim_Team_Meeting_Deadline__c passed
			record.clear();
			record.put("Published_Date__c", dateFormat.format(todayCal.getTime()));
			code = APITools.updateRecordObject("Federal_Register__c", fridI, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank  AND "
	       			+ "Sunset_Review_Type__c is 240 T"
	       			+ "HEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//4
	        clause = "IF Actual_Preliminary_Signature__c is blank  AND Sunset_Review_Type__c is 240 "
	       			+ "THEN Calculated_Preliminary_Signature__c";
//till here reviwed
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
	    	clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c "
	       			+ "has not passed THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
	        clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
	       			+ "THEN Final_Issues_Due_to_DAS__c";
	        todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -10);
			record.clear();
			record.put("Published_Date__c", dateFormat.format(todayCal.getTime()));
			code = APITools.updateRecordObject("Federal_Register__c", fridI, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
	        clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c "
	       			+ "is blank THEN Final_Concurrence_Due_to_DAS__c";
	       	
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
	        clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//9
	        clause = "IF published_date_c (type: Final) is blank AND Actual_Final_Signaturec is "
	       			+ "not blank THEN Calculated_Final_FR_signature_c";
			record.clear();
			record.put("Actual_Final_Signature__c", todayStr); 
			record.put("Segment_Outcome__c", "Completed");	
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_FR_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        record.clear();	        
			record.put("Actual_Prelim_Issues_to_DAS__c", "");
			record.put("Calculated_Initiation_Signature__c", "");
			record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
			record.put("Actual_Preliminary_Signature__c", "");
			record.put("Actual_Final_Issues_to_DAS__c", "");
			record.put("Actual_Final_Concurrence_to_DAS__c", "");
			record.put("Actual_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
	       	//Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date","Title");
	        //1
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        record.put("Segment_Outcome__c", "");
			todayCal.setTime(todayDate);
			record.clear();
		    todayCal.add(Calendar.DATE, 10);
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
	        record.clear();
			record.put("Published_Date__c", todayStr);
			code = APITools.updateRecordObject("Federal_Register__c", fridI, record);
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Segment_Outcome__c", "");
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
			record.put("Segment_Outcome__c", "Completed");
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
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
	       	code = APITools.updateRecordObject("Segment__c", sunsetReviewId, record);
	       	HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	       			+ "HOLIDAY AND TOLLING DAY","Title");
			for(LinkedHashMap<String, String> dates:sunsetReviewDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
	       			record.clear();
	    			record.put("Published_Date__c", dates.get("Date_For_Weekend").trim());
	    			code = APITools.updateRecordObject("Federal_Register__c", fridI, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateSunSetReviewDatesByTypeJSONObject(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"), "240 Day");
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
	       			record.clear();
	    			record.put("Published_Date__c", dates.get("Date_For_Holiday").trim());
	    			code = APITools.updateRecordObject("Federal_Register__c", fridI, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateSunSetReviewDatesByTypeJSONObject(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"), "240 Day");
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
	       			record.clear();
	    			record.put("Published_Date__c", dates.get("Date_For_Tolling").trim());
	    			code = APITools.updateRecordObject("Federal_Register__c", fridI, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("segmentId", sunsetReviewId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateSunSetReviewDatesByTypeJSONObject(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"), "240 Day");
	       		}
	       	}
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
	@Test(enabled = true, priority=27)
	void Create_And_Validate_International_Litigation() throws Exception
	{
		printLog("Create_And_Validate_litigation");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_027");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("Petition__c", petitionId);
		record.put("RecordTypeId", recordType.get("International Litigation"));
		record.put("Request_Filed__c", todayStr);
		String litigationId = APITools.createObjectRecord("Litigation__c", record);
		String sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Preliminary_Signature__c,"
        		+ "Calculated_Final_Signature__c+"
        		+ "from+Litigation__c+where+id='"+litigationId+"'";
		if(litigationId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(sqlString);
	       	String litigationName = jObj.getString("Name");
	       	updateHtmlReport("Create International Litigation", "User is able to create a new litigation", 
					"Litigation id: <span class = 'boldy'>"+" "+litigationName+"</span>", "Step", "pass", "" );
	      //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("I. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue; 
	        //Next_Major_Deadline__c
	        //1
	        String clause = "IF Actual_Preliminary_Signature__c is blank THEN Calculated_Preliminary_Signature__c";
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
	        record.clear();
    		record.put("Actual_Preliminary_Signature__c", jObj.getString("Calculated_Preliminary_Signature__c"));
	       	String code = APITools.updateRecordObject("Litigation__c", litigationId, record);	
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();	        
	        record.put("Actual_Preliminary_Signature__c", "");
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
	       	//Next Due to DAS Deadline
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline", "Title");        
	        //1
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c is "
	        		+ "blank THEN Prelim_Issues_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Initiation_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank "
	        		+ "THEN Prelim_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
    		clause = "IF Actual_Preliminary_Signature__c is blank THEN Calculated_Preliminary_Signature__c";
	        record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//4
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Preliminary_Signature__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c is "
					+ "blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c ";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();	        
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");	        
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
	       //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
			//1
			clause = "IF Actual_Preliminary_Signature__c is blank AND Prelim_Team_Meeting_Deadline__c "
					+ "is not past THEN Prelim_Team_Meeting_Deadline__c";
			jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//2
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Issues_to_DAS__c  is blank "
					+ "THEN Prelim_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -1);
			record.clear();
			record.put("Request_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//3
			clause = "IF Actual_Preliminary_Signature__c is blank AND Actual_Prelim_Concurrence_to_DAS__c is blank"
					+ " THEN Prelim_Concurrence_Due_to_DAS__c";
			record.clear();
    		record.put("Actual_Prelim_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Prelim_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//4
			clause = "IF Actual_Preliminary_Signature__c is blank THEN "
					+ "Calculated_Preliminary_Signature__c";
			record.clear();
    		record.put("Actual_Prelim_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Preliminary_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Final_Team_Meeting_Deadline__c has not passed "
					+ "THEN Final_Team_Meeting_Deadline__c";
			record.clear();
    		record.put("Actual_Preliminary_Signature__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Team_Meeting_Deadline__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //6
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -6);
			record.clear();
			record.put("Request_Filed__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//8
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c";
			record.clear();
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date - NO Requirement Given for this Date check with Paul","Title");
	        //1
	        /*clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
			todayCal.add(Calendar.DATE, 10);
			record.clear();
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);*/
	        //initiate
	        record.clear();	
	        record.put("Actual_Initiation_Signature__c", "");
	        record.put("Actual_Prelim_Issues_to_DAS__c", "");
	        record.put("Actual_Prelim_Concurrence_to_DAS__c", "");
	        record.put("Actual_Preliminary_Signature__c", "");	        
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	        record.put("Actual_Final_Signature__c", "");
	       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
	       	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	        ArrayList<LinkedHashMap<String, String>> litigationDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "Litigation", "");
	        HtmlReport.addHtmlStepTitle("II. VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:litigationDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Request_Filed__c", dates.get("Date_For_Weekend"));//Application_Accepted__c
			       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateLitigationFields(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"), "International Litigation");
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Request_Filed__c", dates.get("Date_For_Holiday"));
			       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateLitigationFields(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"), "International Litigation");
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Request_Filed__c", dates.get("Date_For_Tolling"));
			       	code = APITools.updateRecordObject("Litigation__c", litigationId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", litigationId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateLitigationFields(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"), "International Litigation");
	       		}
	       	}
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
	@Test(enabled = true, priority=28)
	void Create_And_Validate_Remand() throws Exception
	{
		printLog("Create_And_Validate_Remand");
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_028");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		record.put("Petition__c", petitionId);
		record.put("RecordTypeId", recordType.get("Remand"));
		record.put("Expected_Final_Signature_Before_Ext__c", todayStr);
		String remandId = APITools.createObjectRecord("Litigation__c", record);
		if(remandId != null)
       {
			JSONObject jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	       	String remandName = jObj.getString("Name");
	       	updateHtmlReport("Create Remand", "User is able to create a new Remand", 
					"Remand id: <span class = 'boldy'>"+" "+remandName+"</span>", "Step", "pass", "" );
	    	String datesSheet = InitTools.getInputDataFolder()+"/datapool/weekend_holiday_tolling_dates.xlsx";
	      //*********************************II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS************************
	       	//*************************************************************************************************************
	        HtmlReport.addHtmlStepTitle("II. VALIDATE NEXT DEADLINE DATES WITH ALL SCENARIOS","Title");
	        HtmlReport.addHtmlStepTitle("1) - Next Major Deadline","Title");
	        String actualValue, expectedValue; 
	        String sqlString = "select+Name,Next_Major_Deadline__c,Calculated_Draft_Remand_release_to_party__c,"
	        		+ "Calculated_Final_Signature__c+"
	        		+ "from+Litigation__c+where+id='"+remandId+"'";
	        //Next_Major_Deadline__c
	        //1
	        String clause = "IF Actual_Draft_Remand_released_to_party__c  is blank "
	        		+ "THEN Calculated_Draft_Remand_release_to_party__c";
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Draft_Remand_release_to_party__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Final_Signature__c  is blank THEN Calculated_Final_Signature__c ";
	        record.clear();
    		record.put("Actual_Draft_Remand_released_to_party__c", todayStr);
	       	String code = APITools.updateRecordObject("Litigation__c", remandId, record);	
	        jObj = APITools.getRecordFromObject(sqlString);
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Major_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //initiate
	        record.clear();	        
	        record.put("Actual_Draft_Remand_released_to_party__c", "");
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
	      //Next Due to DAS Deadline
	        HtmlReport.addHtmlStepTitle("2) - Next Due to DAS Deadline", "Title");        
	        //1
	        clause = "IF Actual_Draft_Remand_released_to_party__c is blank AND Actual_Draft_Remand_Issues_to_DAS__c "
	        		+ " is blank THEN Draft_Remand_Issues_Due_to_DAS__c";
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Draft_Remand_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Draft_Remand_released_to_party__c is blank AND Actual_Draft_Remand_Concurrence_to_DAS__c "
	        		+ " is blank THEN Draft_Remand_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Draft_Remand_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Draft_Remand_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
    		clause = "IF Actual_Draft_Remand_released_to_party__c is blank THEN "
    				+ "Calculated_Draft_Remand_release_to_party__c";
	        record.clear();
    		record.put("Actual_Draft_Remand_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Draft_Remand_release_to_party__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//4
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c"
					+ " is blank THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Draft_Remand_released_to_party__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//5
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//6
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c ";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Due_to_DAS_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    //initiate
	        record.clear();	        
	        record.put("Actual_Draft_Remand_Issues_to_DAS__c", "");
	        record.put("Actual_Draft_Remand_Concurrence_to_DAS__c", "");
	        record.put("Actual_Draft_Remand_released_to_party__c", "");	        
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
	       //Next Office Deadline
	       	HtmlReport.addHtmlStepTitle("3) - Next Office Deadline","Title");
	        //1
	        clause = "IF Actual_Draft_Remand_released_to_party__c is blank AND Actual_Draft_Remand_Issues_to_DAS__c "
	        		+ " is blank THEN Draft_Remand_Issues_Due_to_DAS__c";
	        jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Draft_Remand_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF Actual_Draft_Remand_released_to_party__c is blank AND Actual_Draft_Remand_Concurrence_to_DAS__c "
	        		+ " is blank THEN Draft_Remand_Concurrence_Due_to_DAS__c";
	        record.clear();
    		record.put("Actual_Draft_Remand_Issues_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Draft_Remand_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3
    		clause = "IF Actual_Draft_Remand_released_to_party__c is blank THEN "
    				+ "Calculated_Draft_Remand_release_to_party__c";
	        record.clear();
    		record.put("Actual_Draft_Remand_Concurrence_to_DAS__c", todayStr);
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Draft_Remand_release_to_party__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
    		//4
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c"
					+ " is blank THEN Final_Issues_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Draft_Remand_released_to_party__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //5
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Issues_to_DAS__c is blank "
					+ "THEN Final_Issues_Due_to_DAS__c";
			todayCal.setTime(todayDate);
			todayCal.add(Calendar.MONTH, -6);
			record.clear();
			record.put("Expected_Final_Signature_Before_Ext__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Issues_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        
			//6
			clause = "IF Actual_Final_Signature__c is blank AND Actual_Final_Concurrence_to_DAS__c"
					+ " is blank THEN Final_Concurrence_Due_to_DAS__c";
			record.clear();
			record.put("Actual_Final_Issues_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Concurrence_Due_to_DAS__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
			//7
			clause = "IF Actual_Final_Signature__c is blank THEN Calculated_Final_Signature__c ";
			record.clear();			
			record.put("Actual_Final_Concurrence_to_DAS__c", todayStr);	
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Office_Deadline__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Calculated_Final_Signature__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //Next Announcement Date
	        HtmlReport.addHtmlStepTitle("4) - Next Announcement Date Requirement not given Yet, Check with Paul","Title");
	        //1
	       /* clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
			todayCal.add(Calendar.DATE, 10);
			record.clear();
		    record.put("Actual_Final_Signature__c", "");
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //2
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Preliminary_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //3	        
	        clause = "IF final_Announcement_Date is not passed and segment_outcome is blank";
	        todayCal.setTime(todayDate);
	        todayCal.add(Calendar.DATE, -15);
			record.clear();
			record.put("Actual_Preliminary_Signature__c", dateFormat.format(todayCal.getTime()));
			record.put("Actual_Initiation_Signature__c", dateFormat.format(todayCal.getTime()));
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        //4
	        clause = "IF preliminary_Announcement_Date is not passed and segment_outcome is Completed";
			record.clear();
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);	       	
	       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
	        actualValue = ADCVDLib.noNullVal(jObj.getString("Next_Announcement_Date__c"));
	        expectedValue = ADCVDLib.noNullVal(jObj.getString("Final_Announcement_Date__c"));
	        testCaseStatus = testCaseStatus & ADCVDLib.validateNextDeadlineDate(clause, actualValue, expectedValue);
	        ;*/
	        //initiate
	        record.clear();	        
	        record.put("Actual_Draft_Remand_Issues_to_DAS__c", "");
	        record.put("Actual_Draft_Remand_Concurrence_to_DAS__c", "");
	        record.put("Actual_Draft_Remand_released_to_party__c", "");	        
	        record.put("Actual_Final_Issues_to_DAS__c", "");
	        record.put("Actual_Final_Concurrence_to_DAS__c", "");
	       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
	        ArrayList<LinkedHashMap<String, String>> remandDates  = 
	        		XlsxTools.readXlsxSheetAndFilter(datesSheet, "Remand", "");
	        HtmlReport.addHtmlStepTitle("VALIDATE CALCULATED DATES WHEN THEY FALL ON WEEKEND, "
	        		+ "HOLIDAY AND TOLLING DAY","Title");
	        for(LinkedHashMap<String, String> dates:remandDates)
	       	{
	       		HtmlReport.addHtmlStepTitle("Validate ["+dates.get("Field_Name")+"]","Title");
	       		if(!dates.get("Date_For_Weekend").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Weekend").equals(""))
	       		{
		       		record.clear();
		    		record.put("Expected_Final_Signature_Before_Ext__c", dates.get("Date_For_Weekend"));//Application_Accepted__c
			       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateLitigationFields(jObj, dates.get("Field_Name"), 
		       				"Weekend", dates.get("Date_For_Weekend"), "Remand");
	       		}
	       		if(!dates.get("Date_For_Holiday").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Holiday").equals(""))
	       		{
			       	record.clear();
		    		record.put("Expected_Final_Signature_Before_Ext__c", dates.get("Date_For_Holiday"));
		    		//Final_Extension_of_days__c
			       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateLitigationFields(jObj, dates.get("Field_Name"),
		       				"Holiday", dates.get("Date_For_Holiday"), "Remand");
	       		}
	       		if(!dates.get("Date_For_Tolling").equalsIgnoreCase("x")
	       				&& !dates.get("Date_For_Tolling").equals(""))
	       		{
			       	record.clear();
		    		record.put("Expected_Final_Signature_Before_Ext__c", dates.get("Date_For_Tolling"));
			       	code = APITools.updateRecordObject("Litigation__c", remandId, record);
			       	jObj = APITools.getRecordFromObject(row.get("Query").replace("litigationId", remandId));
			       	testCaseStatus = testCaseStatus & 
		       		ADCVDLib.validateLitigationFields(jObj, dates.get("Field_Name"), 
		       				"Tolling Day", dates.get("Date_For_Tolling"), "Remand");
	       		}
	       	}
	        
       }
	   else 
	   {
			failTestSuite("Create new Remand", "user is able to create Remand", "Not as expected",
					"Step", "fail", "");
	   }
	}
	
	/**
	 * This method if for getting the current test case information
	 * @param datapool, datapool data
	 * @param tcTagName, Test case name
	*/
	public LinkedHashMap<String, String> 
	getTestCaseInfo(ArrayList<LinkedHashMap<String, String>> dataPool, String tcTagName)
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
	 * @param datapoolX datapool name
	 * @return merged data pool
	*/
	static ArrayList<LinkedHashMap<String, String>> mergeDataPools(ArrayList<LinkedHashMap<String, String>> dataPool1, 
			ArrayList<LinkedHashMap<String, String>> dataPool2, ArrayList<LinkedHashMap<String, String>> dataPool3, 
			ArrayList<LinkedHashMap<String, String>> dataPool4, ArrayList<LinkedHashMap<String, String>> dataPool5,
			ArrayList<LinkedHashMap<String, String>> dataPool6, ArrayList<LinkedHashMap<String, String>> dataPool7)
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
		for(LinkedHashMap<String, String> map: dataPool6)
		{
			dataPoolMerged.add(map);
		}
		for(LinkedHashMap<String, String> map: dataPool7)
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
	 
	 
	 /**
		 * This method creates new ADCVD case
		 * @param row: map of test case's data
		 * @param type, type of case, A, CVD
		 * @return true case created correctly, false if not
		 * @exception Exception
		*/
		public static String createNewCase(LinkedHashMap<String, String> row, 
											String type) throws Exception
		{
			String cType = "";
			if(type.equals("A-")) cType = "AD ME";
			else cType = "CVD";
			LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
			record.clear();
			record.put("Name", type+ADCVDLib.getCaseName());
			record.put("Commodity__c", row.get("Commodity"));
			record.put("ADCVD_Case_Type__c", cType);
			record.put("Product__c", row.get("Product")+"_"+
					new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date()));
			record.put("Product_Short_Name__c", row.get("Product_Short_Name"));
			record.put("Country__c", row.get("Country"));
			//record.put("Record_Type__c", row.get("Record_Type"));
			String caseId = APITools.createObjectRecord("ADCVD_Case__c", record);
			if(caseId!=null)
			{
				String caseNameAd = record.get("Name");
				updateHtmlReport("Create Case", "User is able to create a new "+cType.replace(" ME", "")+" case", 
						"Case: <span class = 'boldy'>"+" "+caseNameAd+"</span>", "Step", "pass", "");
			}else
			{
				failTestCase("Create AD new Case", "User is able to create a new "+cType.replace(" ME", "")+" case",
						"Not as expected", "Step", "fail", "");
			}
			return caseId;
		}
		
		 /**
			 * This method creates new Petition
			 * @param row: map of test case's data
			 * @param caseId, case identifier
			 * @param outcome, petition outcome
			 * @return true case created correctly, false if not
			 * @exception Exception
			*/
			public static String createNewPetition(LinkedHashMap<String, String> row, 
												String caseId, String outcome) throws Exception
			{
				LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
				record.put("ADCVD_Case__c", caseId);
				record.put("Petition_Filed__c", todayStr);
				if (outcome.equals("Self-Initiated"))
				{
					record.put("Actual_Initiation_Signature__c", todayStr);
					record.put("Calculated_Initiation_Signature__c", todayStr);
					record.put("Petition_Outcome__c", "Self-Initiated");
				}
				String petitionId = APITools.createObjectRecord("Petition__c", record);
				if(petitionId!=null)
				{
					String query = "select+name+from+Petition__c+where+id='"+petitionId+"'";
					JSONObject jObj = APITools.getRecordFromObject(query);
					updateHtmlReport("Create petition", "User is able to create a new petition", 
							"Petition: <span class = 'boldy'>"+" "+jObj.getString("Name")+"</span>", "Step", "pass", "");
				}else
				{
					failTestSuite("Create petition", "User is able to create a new petition", 
							"Not As expected", "Step", "fail", "");
				}
				return petitionId;
			}
			
			/**
			 * This method creates new Investigation
			 * @param row: map of test case's data
			 * @param petitionId, petition identifier
			 * @return true case created correctly, false if not
			 * @exception Exception
			*/
			public static String createNewInvestigation(LinkedHashMap<String, String> row, 
														String petitionId) throws Exception
			{
				LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
				String investigationName = "";
				record.clear();
				record.put("Petition__c", petitionId);
				String investigationAd = APITools.createObjectRecord("Investigation__c", record);
				if(investigationAd!=null)
				{
					String query = "select+name+from+Investigation__c+where+id='"+investigationAd+"'";
					JSONObject jObj = APITools.getRecordFromObject(query);
			       	investigationName = jObj.getString("Name");
					updateHtmlReport("Create Investigation", "User is able to create a new Investigation", 
							"Investigation: <span class = 'boldy'>"+" "+investigationName+"</span>", "Step", "pass", "" );
				}else
				{
					failTestCase("Create Investigation", "User should be able to create a new Investigation", 
							"Not As expected", "Step", "fail", "");
				}
				return investigationAd+"###"+investigationName;
			}

			/**
			 * This method creates new Order
			 * @param row: map of test case's data
			 * @param petitionId, petition identifier
			 * @return created order
			 * @exception Exception
			*/
			public static String createNewOrder(String investigationId) throws Exception
			{
				LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
				record.put("Investigation__c", investigationId);
				String orderId = APITools.createObjectRecord("ADCVD_Order__c", record);
				if(orderId != null)
		       {
			       	JSONObject jObj = APITools.getRecordFromObject("Select+Name+From+ADCVD_Order__c+Where+id='"+orderId+"'");
			       	String orderName = jObj.getString("Name");
			       	updateHtmlReport("Create Order", "User is able to create a new Order", 
							"Order <span class = 'boldy'>"+" "+orderName+"</span>", "Step", "pass", "" );
		       }
				else 
				{
					failTestCase("Create new Order", "user is able to create an order", "Not as expected",
								"Step", "fail", "");
				}
				return orderId;
			}
			
			/**
			 * This method creates new Litigation/Remand
			 * @param row: map of test case's data
			 * @param petitionId, petition identifier
			 * @return created order
			 * @exception Exception
			*/
			public static String createNewLitigation(String petitionId, String litigType) throws Exception
			{
				LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
				JSONObject jObj = null;
				record.clear();
				record.put("Petition__c", petitionId);
				
				if(litigType.equalsIgnoreCase("International Litigation"))
				{
					record.put("RecordTypeId", recordType.get("International Litigation"));
					record.put("Request_Filed__c", todayStr);
				}
				else//Remand
				{
					record.put("RecordTypeId", recordType.get("Remand"));
					record.put("Expected_Final_Signature_Before_Ext__c", todayStr);
					
				}
				String litigationId = APITools.createObjectRecord("Litigation__c", record);
				String sqlString = "select+id,name+from+litigation__c+where+id='litigationId'";
				if(litigationId != null)
		        {
					jObj = APITools.getRecordFromObject(sqlString.replace("litigationId", litigationId));
			       	String litigName = jObj.getString("Name");
			       	updateHtmlReport("Create International Litigation", "User is able to create a new '"+litigType+"'", 
							"Litigation id: <span class = 'boldy'>"+" "+litigName+"</span>", "Step", "pass", "" );
		        }
			   else
			   {
					failTestCase("Create International Litigation", "User is able to create a new '"+litigType+"'", 
							"Not as expected", "Step", "fail", "" );
			   }
				return litigationId;
			}
			
			
			/**
			 * This method creates new Order
			 * @param row: map of test case's data
			 * @param petitionId, petition identifier
			 * @return created order
			 * @exception Exception
			*/
			public static String createNewSegment(String orderId, String segmentType) throws Exception
			{
				LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
				record.put("ADCVD_Order__c", orderId);
				record.put("RecordTypeId", recordType.get(segmentType));
				String segmentId = null;
				switch (segmentType)
				{
					case "Administrative Review":
					{
						record.put("Will_you_Amend_the_Final__c", "Yes");
						record.put("Final_Date_of_Anniversary_Month__c", todayStr);
						break;
					}	
					
					case "Anti-Circumvention Review":
					{
						record.put("Application_Accepted__c", todayStr);
						record.put("Type_of_Circumvention_Inquiry__c", "Later-Developed Merchandise");
						break;
					}
					
					case "Changed Circumstances Review":
					{
						record.put("Preliminary_Determination__c", "Yes");
						record.put("Request_Filed__c", todayStr);
						break;
					}
					
					case "Expedited Review":
					{
						record.put("Calculated_Initiation_Signature__c", todayStr);
						break;
					}
					
					case "New Shipper Review":
					{
						record.put("Calculated_Initiation_Signature__c", todayStr);
						break;
					}
					
					case "Scope Inquiry":
					{
						record.put("Request_Filed__c", todayStr);
						record.put("Actual_Date_of_Decision_on_HoP__c", todayStr);
						record.put("Decision_on_How_to_Proceed__c", "Formal");
						record.put("Type_of_Scope_Ruling__c", "K (1)");
						break;
					}
					
					case "Sunset Review":
					{
						record.put("Notice_of_intent_to_participate_Ips__c", "Yes");
						record.put("Domestic_Party_File_Substan_Response__c", "No");
						segmentId = APITools.createObjectRecord("Segment__c", record);
						if(segmentId == null) 
					    {
							failTestCase("Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
									"Not as expected", "Step", "fail", "");
					    }
						//90Days
						record.put("Published_Date__c", todayStr);
						record.put("Cite_Number__c", "None");
						record.put("Type__c", "Initiation");
						String fridI = APITools.createObjectRecord("Federal_Register__c", record);
						//120 Day
						record.clear();
						record.put("Domestic_Party_File_Substan_Response__c", "Yes");
						String code = APITools.updateRecordObject("Segment__c", segmentId, record);
						//240 Day
						record.clear();
						record.put("Review_to_address_zeroing_in_Segments__c", "Yes");
						record.put("Respondent_File_Substantive_Response__c", "Yes");
						code = APITools.updateRecordObject("Segment__c", segmentId, record);
						String query = "select+id,Name+from+segment__c+where+id='"+segmentId+"'";
						JSONObject jObj = APITools.getRecordFromObject(query);
				       	updateHtmlReport("Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
								"Segment id: <span class = 'boldy'>"+" "+jObj.getString("Name")+"</span>", "Step", "pass", "");
						return segmentId;
						//break;
					}
					default:
					{
						break;
					}
				}
				segmentId = APITools.createObjectRecord("Segment__c", record);
				if(segmentId != null)
		        {
					String query = "select+id,Name+from+segment__c+where+id='"+segmentId+"'";
					JSONObject jObj = APITools.getRecordFromObject(query);
			       	updateHtmlReport("Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
							"Segment id: <span class = 'boldy'>"+" "+jObj.getString("Name")+"</span>", "Step", "pass", "");
		       }
			   else 
			   {
					failTestCase("Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
							"Not as expected", "Step", "fail", "");
			   }
			   return segmentId;
			}
			
}
