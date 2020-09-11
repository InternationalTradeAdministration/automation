package tests;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.holdSeconds;
import static GuiLibs.GuiTools.pageRefresh;
import static GuiLibs.GuiTools.testCaseStatus;
import static GuiLibs.GuiTools.updateHtmlReport;
import static ReportLibs.ReportTools.printLog;
import static XmlLibs.XmlTools.buildTestNgFromDataPool;

import java.awt.image.ReplicateScaleFilter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import static GuiLibs.GuiTools.elementExists;
import static GuiLibs.GuiTools.failTestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.ISuiteListener;
import org.testng.TestNG;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import org.testng.xml.LaunchSuite.ExistingSuite;

import GuiLibs.GuiTools;
import InitLibs.InitTools;
import OfficeLibs.XlsxTools;
import ReportLibs.HtmlReport;
import ServiceLibs.APITools;
import bsh.util.GUIConsoleInterface;
import libs.ADCVDLib;

public class TestOne {
	public static GuiTools guiTools;
	//static HtmlReport htmlReport;
	HashMap<String, String> mapConfInfos;
	String browserType;
	static XlsxTools xlsxTools;
	static LinkedHashMap<String, String> recordType = new LinkedHashMap<String, String>();
	static ArrayList<LinkedHashMap<String, String>> dataPool;
	ArrayList<LinkedHashMap<String, String>> guiPool;
	static ADCVDLib adcvdLib;
	public static String programManagerName, url, userName, password, grantService, grantType,
	grantId, clientSecret, apiUserName,  apiPassword, todayStr;
	//public static boolean testCaseStatus;
	public static Timestamp startTime, suiteStartTime;
	public static Timestamp endTime;
	public static Calendar cal = Calendar.getInstance();
	public boolean loginOn = false;
	public static void main(String[] args) throws Exception 
	{
		printLog("MainMethod()");
		Date todayDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		todayStr = dateFormat.format(todayDate);
		guiTools = new GuiTools();
		xlsxTools = new XlsxTools();
		adcvdLib = new ADCVDLib();
		TestNG testng = new TestNG();
		List<String> suites = Lists.newArrayList();
		String dataPoolPath = InitTools.getInputDataFolder()+"/datapool/Regession_TC.xlsx";
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
		url = mapConfInfos.get("url");
		userName = mapConfInfos.get("user_name");
		password = mapConfInfos.get("password");
		programManagerName = mapConfInfos.get("ProgramManagerName");
		//API
		grantService = mapConfInfos.get("GRANTSERVICE");
		grantType = mapConfInfos.get("GRANTTYPE");
		grantId = mapConfInfos.get("CLIENTID");
		clientSecret = mapConfInfos.get("CLIENTSECRET");
		apiUserName = mapConfInfos.get("APIUSERNAME");
		apiPassword = mapConfInfos.get("APIPASSWORD");
		System.out.println("dd");
		String accessToken = APITools.getAccesToken(url, grantService, grantType, grantId,
				clientSecret , apiUserName, apiPassword);
		
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
	
	
	/////////////////////////////////////////////////////////ADCVD 1 to 6////////////////////////////////////////
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_001() throws Exception
	{
		printLog("Program_Manager_001");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_001");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        clearUserPermissionSetAssignment(userIdentification);
        HtmlReport.addHtmlStepTitle("Login as [ADCVD Manager] with no permission set","Title"); 
        // Staffing manager
        String sqlStaff="select+id+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	String code = APITools.updateRecordObject("user", staffIdentification, record);	
       	row.put("Program_Manager", programManagerName);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);	
		
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
		HtmlReport.addHtmlStepTitle("Login as [ADCVD Policy Power User] with no permission set","Title");        
        //Login as ADCVD Policy Power User		
		sqlProfile="select+id+from+profile+where+name='ADCVD+Policy+PowerUser'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
	}
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_002() throws Exception
	{
		printLog("Program_Manager_002 ");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_002");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        clearUserPermissionSetAssignment(userIdentification);
        HtmlReport.addHtmlStepTitle("Login as [ADCVD Manager] with permission "
        		+ "[ADCVD App: ADCVD PolicyPowerUser Profile Permission Set]","Title"); 
     // Staffing manager
        String sqlStaff="select+id+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	String code = APITools.updateRecordObject("user", staffIdentification, record);	
       	row.put("Program_Manager", programManagerName);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_App_ADCVD_PolicyPowerUser_Profile_Permission_Set'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);
		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);		
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
		HtmlReport.addHtmlStepTitle("Login as [ADCVD Policy Power User] with permission"
				+ " [ADCVD App: ADCVD PolicyPowerUser Profile Permission Set]","Title");        
        //Login as ADCVD Policy Power User		
		sqlProfile="select+id+from+profile+where+name='ADCVD+Policy+PowerUser'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
	}
	
	
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_003() throws Exception
	{
		printLog("Program_Manager_003");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_003");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        clearUserPermissionSetAssignment(userIdentification);
        HtmlReport.addHtmlStepTitle("Login as [ADCVD Manager] with permission "
        		+ "[A0103ADCVDCasePrivateReadCreateEdit]","Title");  
     // Staffing manager
        String sqlStaff="select+id+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	String code = APITools.updateRecordObject("user", staffIdentification, record);	
       	row.put("Program_Manager", programManagerName);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='A0103ADCVDCasePrivateReadCreateEdit'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);
		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);		
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
		HtmlReport.addHtmlStepTitle("Login as [ADCVD Policy Power User] with permission"
				+ " [A0103ADCVDCasePrivateReadCreateEdit]","Title");        
        //Login as ADCVD Policy Power User		
		sqlProfile="select+id+from+profile+where+name='ADCVD+Policy+PowerUser'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
	}
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_004() throws Exception
	{
		printLog("Program_Manager_004");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_004");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        clearUserPermissionSetAssignment(userIdentification);
        HtmlReport.addHtmlStepTitle("Login as [ADCVD Manager] with permission "
        		+ "[ADCVD App: Create/Edit Case and Petition]","Title");  
     // Staffing manager
        String sqlStaff="select+id+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	String code = APITools.updateRecordObject("user", staffIdentification, record);	
       	row.put("Program_Manager", programManagerName);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_App_Create_Edit_Case_and_Petition'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);
		
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);		
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
		HtmlReport.addHtmlStepTitle("Login as [ADCVD Policy Power User] with permission"
				+ " [ADCVD App: Create/Edit Case and Petition]","Title");        
        //Login as ADCVD Policy Power User		
		sqlProfile="select+id+from+profile+where+name='ADCVD+Policy+PowerUser'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
	}
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_005() throws Exception
	{
		printLog("Program_Manager_005");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_005");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        clearUserPermissionSetAssignment(userIdentification);
        HtmlReport.addHtmlStepTitle("Login as [ADCVD Manager] with no permission set","Title"); 
        // Staffing manager
        String sqlStaff="select+id+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	String code = APITools.updateRecordObject("user", staffIdentification, record);	
       	row.put("Program_Manager", programManagerName);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);			
		testCaseStatus = testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
		
		testCaseStatus = testCaseStatus & ADCVDLib.validateProgManagerNotExist(programManagerName, true);
		
		//Login as ADCVD Front Office - Non-ADCVD Manager	
		 HtmlReport.addHtmlStepTitle("Login as [ADCVD Front Office - Non-ADCVD Manager] "
		 		+ "with no permission set","Title"); 
		sqlProfile="select+id+from+profile+where+name='ADCVD+Front+Office'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);

       	testCaseStatus = testCaseStatus & ADCVDLib.validateProgManagerNotExist(programManagerName, false);
	}
	
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_006() throws Exception
	{
		printLog("Program_Manager_006");
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_006");
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        clearUserPermissionSetAssignment(userIdentification);
        row.put("Program_Manager", programManagerName);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	String code = APITools.updateRecordObject("user", userIdentification, record);	
       	// Staffing manager --prog mngr
        String sqlStaff="select+id+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	code = APITools.updateRecordObject("user", staffIdentification, record);
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);	
		HtmlReport.addHtmlStepTitle("Login as [ADCVD Manager] and "
	        		+ "ProgramManager_Staff-Role = [Program Manager] with no permission set","Title");
		testCaseStatus =testCaseStatus & ADCVDLib.checkProgramManagerLookUp(row, true);
		HtmlReport.addHtmlStepTitle("Login as [ADCVD Manager] and "
	        		+ "ProgramManager_Staff-Role = [None] with no permission set","Title"); 
		// Staffing manager  --- none
		record.clear();
		record.put("Staffing_Role__c", "");
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	clearUserPermissionSetAssignment(staffIdentification);
       	testCaseStatus =testCaseStatus & ADCVDLib.checkProgramManagerLookUp(row, false);
       	
       	//////////////////////////////////////////////////////////////////////
        //Login as ADCVD Policy PowerUser	
  		sqlProfile="select+id+from+profile+where+name='ADCVD+Policy+PowerUser'";
  		jObj = APITools.getRecordFromObject(sqlProfile);		
        profileIdentification = jObj.getString("Id");
  		record.clear();
  		record.put("ProfileId", profileIdentification);
        code = APITools.updateRecordObject("user", userIdentification, record);	
        // Staffing manager --prog mngr
  		record.clear();
  		record.put("Staffing_Role__c", "Program Manager");
        code = APITools.updateRecordObject("user", staffIdentification, record);
  		if (! loginOn)
  		{
  			guiTools.openBrowser(browserType);
  			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
  		}
  		holdSeconds(2);	
  		HtmlReport.addHtmlStepTitle("Login as [ADCVD Policy PowerUser] and "
  	        		+ "ProgramManager_Staff-Role = [Program Manager] with no permission set","Title");
  		testCaseStatus =testCaseStatus & ADCVDLib.checkProgramManagerLookUp(row, true);
  		HtmlReport.addHtmlStepTitle("Login as [ADCVD Policy PowerUser] and "
  	        		+ "ProgramManager_Staff-Role = [None] with no permission set","Title"); 
  		// Staffing manager  --- none
  		record.clear();
  		record.put("Staffing_Role__c", "");
     	code = APITools.updateRecordObject("user", staffIdentification, record);
     	clearUserPermissionSetAssignment(staffIdentification);
     	testCaseStatus =testCaseStatus & ADCVDLib.checkProgramManagerLookUp(row, false);
	}
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_007() throws Exception
	{
		LinkedHashMap<String, String> userRoleIds= getAllObjectIds("userrole");
		printLog("Program_Manager_007"); 
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_007");
		row.put("Program_Manager", programManagerName);
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        updateHtmlReport("API - Change user", "Set user role's to [E&C DAS/AS], "
        		+ "user profile to [ADCVD Manager], user permissions to [ADCVD_Staffing]",
				"As expected", "Step", "pass", "");
    	///add this every beginning of tc
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
		String code = APITools.updateRecordObject("user", userIdentification, record);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	//set user permission
       	clearUserPermissionSetAssignment(userIdentification);
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_Staffing'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);       	
	  	// Staffing manager --prog mngr
        String sqlStaff="select+id,UserRoleId+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
        //String currentRole = jObj.getString("UserRoleId"); 
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	code = APITools.updateRecordObject("user", staffIdentification, record);       	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);
		
		
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
       	testCaseStatus =testCaseStatus & ADCVDLib.createNewPetition(row); 
       	
     /*  	row.put("Staff_Id", staffIdentification);
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "Self-Initiated");
		ADCVDLib.navigateToObject(url, adPetitionId);*/
		holdSeconds(3);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateCustomTabText("Orgs & Customs");
    	testCaseStatus =testCaseStatus & ADCVDLib.validateStaffingTitleOrder(row.get("Staff_Title_Order"));
       	testCaseStatus =testCaseStatus & ADCVDLib.validateProgramManagerTransferred(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateNewStaffCreation(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateClearProgramManagerValue(programManagerName);
    	
    	//create recusal
    	clearUserRecusal(staffIdentification);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, false);
    	SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
    	record.clear();
		record.put("user__C", staffIdentification);
		record.put("Start_Date__c", simple.format(new Date()));
		record.put("End_Date__c", simple.format(new Date()));
		record.put("Reason_for_Recusal__c", "test_"+simple.format(new Date()));
		String recusalId = APITools.createObjectRecord("recusal__c", record); 
		updateHtmlReport("API - Create recual", "Create recusal with user ["+programManagerName+"]",
				"As expected", "Step", "pass", "");
		testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, true);
		clearUserRecusal(staffIdentification);
		// change prog manager Role to non-(E&C DAS/AS , E&C TANC)
		updateHtmlReport("API - Change user role", programManagerName+ " role's set to [ADCVD Accounting]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("ADCVD Accounting"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "ADCVD Accounting", false);
     // change prog manager Role to (E&C TANC)
       	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C TANC]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C TANC"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C TANC", true);
       	//change back to original value
    	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C DAS/AS]",
				"As expected", "Step", "pass", "");
       	record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C DAS/ASC", true);
	}
	
	
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_008() throws Exception
	{
		LinkedHashMap<String, String> userRoleIds= getAllObjectIds("userrole");
		printLog("Program_Manager_008"); 
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_008");
		row.put("Program_Manager", programManagerName);
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        updateHtmlReport("API - Change user", "Set user role's to [E&C DAS/AS], "
        		+ "user profile to [ADCVD Manager], user permissions to [ADCVD_Staffing]",
				"As expected", "Step", "pass", "");
    	///add this every beginning of tc
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
		String code = APITools.updateRecordObject("user", userIdentification, record);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	//set user permission
       	clearUserPermissionSetAssignment(userIdentification);
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_Staffing'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);       	
	  	// Staffing manager --prog mngr
        String sqlStaff="select+id,UserRoleId+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
        //String currentRole = jObj.getString("UserRoleId"); 
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	code = APITools.updateRecordObject("user", staffIdentification, record);       	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		/*holdSeconds(2);
		row.put("Staff_Id", staffIdentification);
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "Self-Initiated");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		ADCVDLib.navigateToObject(url, adInvestigationIdName.split("###")[0]);*/
		holdSeconds(3);
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
       	testCaseStatus =testCaseStatus & ADCVDLib.createNewPetition(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigationFromPetition(row);
     	
       	testCaseStatus =testCaseStatus & ADCVDLib.validateCustomTabText("Orgs & Customs");
    	testCaseStatus =testCaseStatus & ADCVDLib.validateStaffingTitleOrder(row.get("Staff_Title_Order"));
       	testCaseStatus =testCaseStatus & ADCVDLib.validateProgramManagerTransferred(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateNewStaffCreation(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateClearProgramManagerValue(programManagerName);
    	
    	//create recusal
    	clearUserRecusal(staffIdentification);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, false);
    	SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
    	record.clear();
		record.put("user__C", staffIdentification);
		record.put("Start_Date__c", simple.format(new Date()));
		record.put("End_Date__c", simple.format(new Date()));
		record.put("Reason_for_Recusal__c", "test_"+simple.format(new Date()));
		String recusalId = APITools.createObjectRecord("recusal__c", record); 
		updateHtmlReport("API - Create recual", "Create recusal with user ["+programManagerName+"]",
				"As expected", "Step", "pass", "");
		testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, true);
		clearUserRecusal(staffIdentification);
		// change prog manager Role to non-(E&C DAS/AS , E&C TANC)
		updateHtmlReport("API - Change user role", programManagerName+ " role's set to [ADCVD Accounting]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("ADCVD Accounting"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "ADCVD Accounting", false);
       	// change prog manager Role to (E&C TANC)
       	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C TANC]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C TANC"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C TANC", true);
       	//change back to original value
    	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C DAS/AS]",
				"As expected", "Step", "pass", "");
       	record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C DAS/ASC", true);
	}
	
	
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_009() throws Exception
	{
		LinkedHashMap<String, String> userRoleIds= getAllObjectIds("userrole");
		printLog("Program_Manager_009"); 
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_009");
		row.put("Program_Manager", programManagerName);
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        updateHtmlReport("API - Change user", "Set user role's to [E&C DAS/AS], "
        		+ "user profile to [ADCVD Manager], user permissions to [ADCVD_Staffing]",
				"As expected", "Step", "pass", "");
    	///add this every beginning of tc
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
		String code = APITools.updateRecordObject("user", userIdentification, record);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	//set user permission
       	clearUserPermissionSetAssignment(userIdentification);
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_Staffing'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);       	
	  	// Staffing manager --prog mngr
        String sqlStaff="select+id,UserRoleId+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
        //String currentRole = jObj.getString("UserRoleId"); 
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	code = APITools.updateRecordObject("user", staffIdentification, record);       	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		/*row.put("Staff_Id", staffIdentification);
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "Self-Initiated");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String adOrderdId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//String segmentId = createNewSegment(adOrderdId, "Administrative Review");
		ADCVDLib.navigateToObject(url, adOrderdId);
		holdSeconds(3);*/
		
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
       	testCaseStatus =testCaseStatus & ADCVDLib.createNewPetition(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigationFromPetition(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewOrderFromInvestigation(row);
     	
       	testCaseStatus =testCaseStatus & ADCVDLib.validateCustomTabText("Orgs & Customs");
    	testCaseStatus =testCaseStatus & ADCVDLib.validateStaffingTitleOrder(row.get("Staff_Title_Order"));
       	testCaseStatus =testCaseStatus & ADCVDLib.validateProgramManagerTransferred(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateNewStaffCreation(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateClearProgramManagerValue(programManagerName);
    	
    	//create recusal
    	clearUserRecusal(staffIdentification);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, false);
    	SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
    	record.clear();
		record.put("user__C", staffIdentification);
		record.put("Start_Date__c", simple.format(new Date()));
		record.put("End_Date__c", simple.format(new Date()));
		record.put("Reason_for_Recusal__c", "test_"+simple.format(new Date()));
		String recusalId = APITools.createObjectRecord("recusal__c", record); 
		updateHtmlReport("API - Create recual", "Create recusal with user ["+programManagerName+"]",
				"As expected", "Step", "pass", "");
		testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, true);
		clearUserRecusal(staffIdentification);
		// change prog manager Role to non-(E&C DAS/AS , E&C TANC)
		updateHtmlReport("API - Change user role", programManagerName+ " role's set to [ADCVD Accounting]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("ADCVD Accounting"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "ADCVD Accounting", false);
       	// change prog manager Role to (E&C TANC)
       	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C TANC]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C TANC"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C TANC", true);
       	//change back to original value
    	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C DAS/AS]",
				"As expected", "Step", "pass", "");
       	record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C DAS/ASC", true);
	}
	
	
	/**
	 * This method is for ADCVD case program manager
	*/
	@Test(enabled = true, priority=1)
	void Program_Manager_010() throws Exception
	{
		LinkedHashMap<String, String> userRoleIds= getAllObjectIds("userrole");
		printLog("Program_Manager_010"); 
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_010");
		row.put("Program_Manager", programManagerName);
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        updateHtmlReport("API - Change user", "Set user role's to [E&C DAS/AS], "
        		+ "user profile to [ADCVD Manager], user permissions to [ADCVD_Staffing]",
				"As expected", "Step", "pass", "");
    	///add this every beginning of tc
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
		String code = APITools.updateRecordObject("user", userIdentification, record);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	//set user permission
       	clearUserPermissionSetAssignment(userIdentification);
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_Staffing'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);       	
	  	// Staffing manager --prog mngr
        String sqlStaff="select+id,UserRoleId+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
        //String currentRole = jObj.getString("UserRoleId"); 
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	code = APITools.updateRecordObject("user", staffIdentification, record);       	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);
		
		
		/*
		row.put("Staff_Id", staffIdentification);
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "Self-Initiated");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String adOrderdId = createNewOrder(adInvestigationIdName.split("###")[0]);
		String segmentId = createNewSegment(adOrderdId, "Administrative Review");*/
		//ADCVDLib.navigateToObject(url, segmentId);
		testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
       	testCaseStatus =testCaseStatus & ADCVDLib.createNewPetition(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigationFromPetition(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewOrderFromInvestigation(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewSegment(row);
     	
		
		holdSeconds(3);
		//https://trade--cops.my.salesforce.com
		//	https://trade--cops.my.salesforce.com/Segment__c/a3X350000005k7CEAQ/view
		//https://trade--cops.lightning.force.com/lightning/page/home
       	testCaseStatus =testCaseStatus & ADCVDLib.validateCustomTabText("Orgs & Customs");
    	testCaseStatus =testCaseStatus & ADCVDLib.validateStaffingTitleOrder(row.get("Staff_Title_Order"));
       	testCaseStatus =testCaseStatus & ADCVDLib.validateProgramManagerTransferred(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateNewStaffCreation(programManagerName);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateClearProgramManagerValue(programManagerName);
    	
    	//create recusal
    	clearUserRecusal(staffIdentification);
    	testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, false);
    	SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
    	record.clear();
		record.put("user__C", staffIdentification);
		record.put("Start_Date__c", simple.format(new Date()));
		record.put("End_Date__c", simple.format(new Date()));
		record.put("Reason_for_Recusal__c", "test_"+simple.format(new Date()));
		String recusalId = APITools.createObjectRecord("recusal__c", record); 
		updateHtmlReport("API - Create recual", "Create recusal with user ["+programManagerName+"]",
				"As expected", "Step", "pass", "");
		testCaseStatus =testCaseStatus & ADCVDLib.validateProgManagerWithRecusalEditable(programManagerName, true);
		clearUserRecusal(staffIdentification);
		// change prog manager Role to non-(E&C DAS/AS , E&C TANC)
		updateHtmlReport("API - Change user role", programManagerName+ " role's set to [ADCVD Accounting]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("ADCVD Accounting"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "ADCVD Accounting", false);
       	// change prog manager Role to (E&C TANC)
       	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C TANC]",
				"As expected", "Step", "pass", "");
		record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C TANC"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C TANC", true);
       	//change back to original value
    	updateHtmlReport("API - Change user role", programManagerName+ " role's set to [E&C DAS/AS]",
				"As expected", "Step", "pass", "");
       	record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
       	testCaseStatus =testCaseStatus & ADCVDLib.validateSearchStaffBasedOnUserRole(programManagerName, "E&C DAS/ASC", true);
	}
	
	
	
	/**
	 * This method is for Segment outcome
	*/
	@Test(enabled = true, priority=1)
	void Segment_Required_Segment_Outcome() throws Exception
	{
		LinkedHashMap<String, String> userRoleIds= getAllObjectIds("userrole");
		printLog("Segment_Required_Segment_Outcome"); 
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_011");
		row.put("Program_Manager", programManagerName);
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        updateHtmlReport("API - Change user", "Set user role's to [E&C DAS/AS], "
        		+ "user profile to [ADCVD Manager], user permissions to [ADCVD_Staffing]",
				"As expected", "Step", "pass", "");
    	///add this every beginning of tc
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
		String code = APITools.updateRecordObject("user", userIdentification, record);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	//set user permission
       	clearUserPermissionSetAssignment(userIdentification);
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_Staffing'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);       	
	  	// Staffing manager --prog mngr
        String sqlStaff="select+id,UserRoleId+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
        //String currentRole = jObj.getString("UserRoleId"); 
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	code = APITools.updateRecordObject("user", staffIdentification, record);       	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);
		
		row.put("Staff_Id", staffIdentification);
		String adCaseId = createNewCase(row, "A-");
		String adPetitionId = createNewPetition(row, adCaseId, "Self-Initiated");
		String adInvestigationIdName = createNewInvestigation(row, adPetitionId);
		String adOrderdId = createNewOrder(adInvestigationIdName.split("###")[0]);
		//String segmentId = createNewSegment(adOrderdId, "Administrative Review");
		ADCVDLib.navigateToObject(url, adOrderdId);
		
		/*testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);
       	testCaseStatus =testCaseStatus & ADCVDLib.createNewPetition(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewInvestigationFromPetition(row);
     	testCaseStatus =testCaseStatus & ADCVDLib.createNewOrderFromInvestigation(row);*/
     	
     	
     	String[] segTypeList = row.get("Segment_Type").split(",");
     	for(int i = 0; i<segTypeList.length; i++)
     	{
     		String segType = segTypeList[i].trim();
     		testCaseStatus = testCaseStatus & ADCVDLib.validateReqSegmentOutcome(segType);
     		if (segType.equalsIgnoreCase("Sunset Review"))
     		{
     			testCaseStatus = testCaseStatus & ADCVDLib.validateReqSegmentOutcomeSunset();
     		}
     	}
	}
	
	
	
	/**
	 * This method is for CBP Case Number Field
	*/
	@Test(enabled = true, priority=1)
	void CBP_Case_Number_Field() throws Exception
	{
		LinkedHashMap<String, String> userRoleIds= getAllObjectIds("userrole");
		printLog("CBP_Case_Number_Field"); 
		LinkedHashMap<String, String> row = getTestCaseInfo(dataPool, "TC_TAG_012");
		row.put("Program_Manager", programManagerName);
		GuiTools.setTestCaseName(row.get("Test_Case_Name"));
		GuiTools.setTestCaseDescription(row.get("Test_Case_Description"));
		printLog(GuiTools.getTestCaseName());
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
        String sqlUser="select+id+from+user+where+Username='"+userName.replace(" ", "+")+"'";
        JSONObject jObj = APITools.getRecordFromObject(sqlUser);		
        String  userIdentification = ADCVDLib.noNullVal(jObj.getString("Id"));
        updateHtmlReport("API - Change user", "Set user role's to [E&C DAS/AS], "
        		+ "user profile to [ADCVD Manager], user permissions to [ADCVD_Staffing]",
				"As expected", "Step", "pass", "");
    	///add this every beginning of tc
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
		String code = APITools.updateRecordObject("user", userIdentification, record);
        //Login as ADCVD Manager	
		String sqlProfile="select+id+from+profile+where+name='ADCVD+Manager'";
		jObj = APITools.getRecordFromObject(sqlProfile);		
        String  profileIdentification = jObj.getString("Id");
		record.clear();
		record.put("ProfileId", profileIdentification);
       	code = APITools.updateRecordObject("user", userIdentification, record);	
       	//set user permission
       	clearUserPermissionSetAssignment(userIdentification);
       	String sqlPermission = "select+Id,Name+from+"
       			+ "permissionset+where+Name='ADCVD_Staffing'";
       	jObj = APITools.getRecordFromObject(sqlPermission);		
        String  permissionIdentification = jObj.getString("Id");
		record.clear();
		record.put("AssigneeId", userIdentification);
		record.put("PermissionSetId", permissionIdentification);
		APITools.createObjectRecord("permissionsetassignment", record);       	
	  	// Staffing manager --prog mngr
        String sqlStaff="select+id,UserRoleId+from+user+where+Name='"+programManagerName.replace(" ", "+")+"'";
		jObj = APITools.getRecordFromObject(sqlStaff);		
        String  staffIdentification = jObj.getString("Id");
        record.clear();
		record.put("UserRoleId", userRoleIds.get("E&C DAS/AS"));
       	code = APITools.updateRecordObject("user", staffIdentification, record);
        //String currentRole = jObj.getString("UserRoleId"); 
		record.clear();
		record.put("Staffing_Role__c", "Program Manager");
       	code = APITools.updateRecordObject("user", staffIdentification, record);       	
		if (! loginOn)
		{
			guiTools.openBrowser(browserType);
			loginOn = ADCVDLib.loginToAdCvd(url, userName, password);
		}
		holdSeconds(2);
		
		row.put("Staff_Id", staffIdentification);
		String adCaseId = createNewCase(row, "A-");
		ADCVDLib.navigateToObject(url, adCaseId);
		
		/*testCaseStatus =testCaseStatus & ADCVDLib.CheckProgrmManagerCreateNewCase(row, true);*/
		testCaseStatus = testCaseStatus & ADCVDLib.validateCbpCaseNumberField();
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
	 * This method clears permission set of user
	 * @param userId, id of the user
	 * @exception JSONException
	*/
	public void clearUserPermissionSetAssignment(String userId) throws JSONException
	{
		String sqlPermissionAssg = "select+id,PermissionSetId+from+permissionsetassignment"
				+ "+where+AssigneeId='"+userId+"'";
		JSONObject jObj = APITools.getAllRecordFromObject(sqlPermissionAssg);
		JSONArray jsonArray = jObj.getJSONArray("records");
		for (int ite = 0; 
				ite< Integer.parseInt(jObj.getString("totalSize"))
				;ite++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(ite);
			String id = (String) jsonObject.get("Id");
			String PermissionSetId = (String) jsonObject.get("PermissionSetId");
			String sqlPermission="select+Name+from+permissionset+where+id='"+PermissionSetId+"'";
			JSONObject jObj2 = APITools.getRecordFromObject(sqlPermission);
			if(!ADCVDLib.noNullVal(jObj2.getString("Name")).startsWith("X"))
			{
				APITools.deleteRecordObject("permissionsetassignment", id);
			}
		}
	}
	
	/**
	 * This method clears permission set of user
	 * @param userId, id of the user
	 * @exception JSONException
	*/
	public void clearUserRecusal(String userId) throws JSONException
	{
		String sqlrec = "select+id+from+recusal__C"
				+ "+where+user__C='"+userId+"'";
		JSONObject jObj = APITools.getAllRecordFromObject(sqlrec);
		JSONArray jsonArray = jObj.getJSONArray("records");
		for (int ite = 0; 
				ite< Integer.parseInt(jObj.getString("totalSize"))
				;ite++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(ite);
			String id = (String) jsonObject.get("Id");
			APITools.deleteRecordObject("recusal__c", id);
		}
	}
	
	/**
	 * This method gets all ids of an object
	 * @param objectName, object name
	 * @exception JSONException
	*/
	public LinkedHashMap<String, String> getAllObjectIds(String objectName) throws JSONException
	{
		LinkedHashMap<String, String> allIds= new LinkedHashMap<String, String>();
		String sqlrec = "select+Name,Id+from+"+objectName;
		JSONObject jObj = APITools.getAllRecordFromObject(sqlrec);
		JSONArray jsonArray = jObj.getJSONArray("records");
		for (int ite = 0; 
				ite< Integer.parseInt(jObj.getString("totalSize"))
				;ite++)
		{
			JSONObject jsonObject = jsonArray.getJSONObject(ite);
			allIds.put((String)jsonObject.get("Name"), (String)jsonObject.get("Id"));
		}
		return allIds;
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
		record.put("Program_Manager__c", row.get("Staff_Id"));
		record.put("ADCVD_Case_Type__c", cType);
		record.put("Product__c", row.get("Product")+"_"+
				new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		
		record.put("Product_Short_Name__c", row.get("Product_Short_Name"));
		record.put("Country__c", row.get("Country"));
		//record.put("Record_Type__c", row.get("Record_Type"));
		String caseIdLocal = APITools.createObjectRecord("ADCVD_Case__c", record);
		if(caseIdLocal!=null)
		{
			String caseNameAd = record.get("Name");
			updateHtmlReport("API - Create Case", "User is able to create a new "+cType.replace(" ME", "")+" case", 
					"Case: <span class = 'boldy'>"+" "+caseNameAd+"</span>", "Step", "pass", "");
		}else
		{
			failTestCase("API - Create AD new Case", "User is able to create a new "+cType.replace(" ME", "")+" case",
					"Not as expected", "Step", "fail", "");
		}
		return caseIdLocal;
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
										String caseIdLocal, String outcome) throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		record.put("ADCVD_Case__c", caseIdLocal);
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
			updateHtmlReport("API - Create petition", "User is able to create a new petition", 
					"Petition: <span class = 'boldy'>"+" "+jObj.getString("Name")+"</span>", "Step", "pass", "");
		}else
		{
			failTestSuite("API - Create petition", "User is able to create a new petition", 
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
												String petitionIdLocal) throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		String investigationName = "";
		record.clear();
		record.put("Petition__c", petitionIdLocal);
		record.put("Period_Covered_Start_Date__c", todayStr);
		record.put("Period_Covered_End_Date__c", todayStr);
		
		String investigationIdLocal = APITools.createObjectRecord("Investigation__c", record);
		if(investigationIdLocal!=null)
		{
			String query = "select+name+from+Investigation__c+where+id='"+investigationIdLocal+"'";
			JSONObject jObj = APITools.getRecordFromObject(query);
	       	investigationName = jObj.getString("Name");
			updateHtmlReport("API - Create Investigation", "User is able to create a new Investigation", 
					"Investigation: <span class = 'boldy'>"+" "+investigationName+"</span>", "Step", "pass", "" );
		}else
		{
			failTestCase("API - Create Investigation", "User should be able to create a new Investigation", 
					"Not As expected", "Step", "fail", "");
		}
		return investigationIdLocal+"###"+investigationName;
	}

	/**
	 * This method creates new Order
	 * @param row: map of test case's data
	 * @param petitionId, petition identifier
	 * @return created order
	 * @exception Exception
	*/
	public static String createNewOrder(String investigationIdLocal) throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		record.put("Investigation__c", investigationIdLocal);
		String orderId = APITools.createObjectRecord("ADCVD_Order__c", record);
		if(orderId != null)
       {
	       	JSONObject jObj = APITools.getRecordFromObject("Select+Name+From+ADCVD_Order__c+Where+id='"+orderId+"'");
	       	String orderName = jObj.getString("Name");
	       	updateHtmlReport("API - Create Order", "User is able to create a new Order", 
					"Order <span class = 'boldy'>"+" "+orderName+"</span>", "Step", "pass", "" );
       }
		else 
		{
			failTestCase("API - Create new Order", "user is able to create an order", "Not as expected",
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
	public static String createNewLitigation(String petitionIdLocal, String litigType) throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		JSONObject jObj = null;
		record.clear();
		record.put("Petition__c", petitionIdLocal);
		
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
	       	updateHtmlReport("API - Create International Litigation", "User is able to create a new '"+litigType+"'", 
					"Litigation id: <span class = 'boldy'>"+" "+litigName+"</span>", "Step", "pass", "" );
        }
	   else
	   {
			failTestCase("API - Create International Litigation", "User is able to create a new '"+litigType+"'", 
					"Not as expected", "Step", "fail", "" );
	   }
		return litigationId;
	}
	/**
	 * This method creates new Segment
	 * @param orderIdLocal, Order identifier
	 * @param segmentType, segment type
	 * @return id of created segment
	 * @exception Exception
	*/
	public static String createNewSegment(String orderIdLocal, String segmentType) throws Exception
	{
		LinkedHashMap<String, String> record = new LinkedHashMap<String, String>();
		record.put("ADCVD_Order__c", orderIdLocal);
		record.put("RecordTypeId", recordType.get(segmentType));
		String segmentIdLocal = null;
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
				segmentIdLocal = APITools.createObjectRecord("Segment__c", record);
				if(segmentIdLocal == null) 
			    {
					failTestCase("API - Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
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
				String code = APITools.updateRecordObject("Segment__c", segmentIdLocal, record);
				//240 Day
				record.clear();
				record.put("Review_to_address_zeroing_in_Segments__c", "Yes");
				record.put("Respondent_File_Substantive_Response__c", "Yes");
				code = APITools.updateRecordObject("Segment__c", segmentIdLocal, record);
				String query = "select+id,Name+from+segment__c+where+id='"+segmentIdLocal+"'";
				JSONObject jObj = APITools.getRecordFromObject(query);
		       	updateHtmlReport("API - Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
						"Segment id: <span class = 'boldy'>"+" "+jObj.getString("Name")+"</span>", "Step", "pass", "");
				return segmentIdLocal;
				//break;
			}
			default:
			{
				break;
			}
		}
		segmentIdLocal = APITools.createObjectRecord("Segment__c", record);
		if(segmentIdLocal != null)
        {
			String query = "select+id,Name+from+segment__c+where+id='"+segmentIdLocal+"'";
			JSONObject jObj = APITools.getRecordFromObject(query);
	       	updateHtmlReport("API - Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
					"Segment id: <span class = 'boldy'>"+" "+jObj.getString("Name")+"</span>", "Step", "pass", "");
       }
	   else 
	   {
			failTestCase("API - Create segment "+segmentType, "User is able to create a new '"+segmentType+"' segment", 
					"Not as expected", "Step", "fail", "");
	   }
	   return segmentIdLocal;
	}
}
