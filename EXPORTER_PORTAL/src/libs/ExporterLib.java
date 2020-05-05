/**
 * MilCorp
 * Mouloud Hamdidouche
 * December, 2018
*/
package libs;
import static GuiLibs.GuiTools.checkElementExists;
import static GuiLibs.GuiTools.checkElementVisible;
import static GuiLibs.GuiTools.clickElement;
import static GuiLibs.GuiTools.clickElementJs;
import static GuiLibs.GuiTools.clickNiemElementJs;
import static GuiLibs.GuiTools.elementExists;
import static GuiLibs.GuiTools.enterText;
import static GuiLibs.GuiTools.enterTextAndClear;
import static GuiLibs.GuiTools.enterTextFile;
import static GuiLibs.GuiTools.failTestCase;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.getElementAttribute;
import static GuiLibs.GuiTools.getElementValuesIntoArray;
import static GuiLibs.GuiTools.getSelectValues;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.highlightElement;
import static GuiLibs.GuiTools.highlightNiemElement;
import static GuiLibs.GuiTools.holdMilliSeconds;
import static GuiLibs.GuiTools.holdSeconds;
import static GuiLibs.GuiTools.navigateTo;
import static GuiLibs.GuiTools.pageRefresh;
import static GuiLibs.GuiTools.removeSpecialChar;
import static GuiLibs.GuiTools.replaceGui;
import static GuiLibs.GuiTools.scrollToElement;
import static GuiLibs.GuiTools.scrollToNiemElement;
import static GuiLibs.GuiTools.selectElementByText;
import static GuiLibs.GuiTools.selectElementByValue;
import static GuiLibs.GuiTools.selectNiemElementByText;
import static GuiLibs.GuiTools.setBrowserTimeOut;
import static GuiLibs.GuiTools.switchBackFromFrame;
import static GuiLibs.GuiTools.switchBackToWindow;
import static GuiLibs.GuiTools.switchToFrame;
import static GuiLibs.GuiTools.switchToWindow;
import static GuiLibs.GuiTools.unHighlightElement;
import static GuiLibs.GuiTools.updateHtmlReport;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import InitLibs.InitTools;
import ReportLibs.HtmlReport;
public class ExporterLib{
	public static String filedDate,
	actualInitiationSignature, calculatedInitiationSignature, petitionOutcome;
	public static int petitionInitiationExtension;
	static DateFormat format;
	static Calendar calendar;
	static String caseType;
	static String investigationId, aInvestigation, cInvestigation;
	static String orderId, adCaseId, cvdCaseId, arSegmentId;
	public ExporterLib() throws IOException {
		//super();
		this.format = new SimpleDateFormat("M/d/yyyy");
		this.calendar = Calendar.getInstance();
	}
	
	/**
	 * This method creates new Content
	 * @param row: map of test case's data
	 * @return true content created correctly, false if not
	 * @exception Exception
	*/
	public static boolean createContent(LinkedHashMap<String, String> row) throws Exception
	{
		String type = row.get("Content_Type");
		String timeStamp = "_"+ new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());
		String internalTitle = "Aut_"+type+"_"+timeStamp;
		System.out.println("dd");
		boolean created = true;
		int currentWait = setBrowserTimeOut(2);
		if(!elementExists(guiMap.get("ContentMenu")))
		{
			clickElementJs(guiMap.get("MainMenu"));
		}
		clickNiemElementJs(guiMap.get("ContentMenu"), 1);
		clickElementJs(guiMap.get("AddContentButton"));
		clickElementJs(replaceGui(guiMap.get("ContentType"),type));
		if(elementExists(guiMap.get("ContentName")))
		{
			enterText(guiMap.get("ContentName") ,internalTitle);
		}
		if(!"N/A".equalsIgnoreCase(row.get("World_Region")))
		{
			selectElementByText(guiMap.get("tradeRegion"), row.get("World_Region"));
		}
		if(elementExists(guiMap.get("Industries")))
		{
			selectElementByText(guiMap.get("Industries"), "Industries");
		}
		
		if(!"N/A".equalsIgnoreCase(row.get("Content_Public_Title")) && !"".equalsIgnoreCase(row.get("Content_Public_Title")))
		{
			enterText(guiMap.get("ContentPublicTitle"), row.get("Content_Public_Title"));
		}
		if("Yes".equalsIgnoreCase(row.get("ITA_Employee")))
		{
			clickElementJs(guiMap.get("ITAEmployeeCheckBox"));
			enterText(replaceGui(guiMap.get("ContentText"),"Business Unit/Office"), 
					row.get("Content_Business_Office_Unit"));
		}
		else if(!"N/A".equalsIgnoreCase(row.get("Content_Category")) && !"".equalsIgnoreCase(row.get("Content_Category")))
		{
			selectElementByText(guiMap.get("SelectTypeCategory"), row.get("Content_Category"));
		}
		if(!"N/A".equalsIgnoreCase(row.get("Content_Title")) && !"".equalsIgnoreCase(row.get("Content_Title")))
		{
			enterText(guiMap.get("ContentTitle"), internalTitle);
		}
		if(!"N/A".equalsIgnoreCase(row.get("Trade_Lead_Title")) && !"".equalsIgnoreCase(row.get("Trade_Lead_Title")))
		{
			enterText(guiMap.get("TradeLeadContent"), row.get("Trade_Lead_Title"));
		}
		//TradeLeadContent
		setBrowserTimeOut(currentWait);
		
		if("Yes".equalsIgnoreCase(row.get("Content_Media")))
		{
			clickElementJs(guiMap.get("SelectAttachment"));
			switchToFrame(guiMap.get("iFrameMedia"));
			enterText(guiMap.get("MediaName"), 
					row.get("Content_Title"));
			//String originalHadle = switchToWindow();
			clickNiemElementJs(guiMap.get("mediaCheckBox"), 1);
			clickElementJs(guiMap.get("SelectMedia"));
			switchBackFromFrame();
			//switchBackToWindow(originalHadle);
		}
		if(type.equalsIgnoreCase("Series Aggregator"))
		{
			String startDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MONTH, 1);
			String endDate = new SimpleDateFormat("MM/dd/yyyy").format(cal.getTime());
			enterText(guiMap.get("valueDate"), startDate, 1);
			enterText(guiMap.get("valueDate"), endDate, 2);
		}
		if(!"N/A".equalsIgnoreCase(row.get("Content_Summary")) && !"".equalsIgnoreCase(row.get("Content_Summary")))
		{
			switchToFrame(guiMap.get("iFrameSummary"));
			enterText(guiMap.get("summaryFiled"),  row.get("Content_Summary"));
			switchBackFromFrame();
		}
		if(!"N/A".equalsIgnoreCase(row.get("Content_Body")) && !"".equalsIgnoreCase(row.get("Content_Body")))
		{
			switchToFrame(guiMap.get("iFrameBody"));
			enterText(guiMap.get("bodyField"),  row.get("Content_Body"));
			switchBackFromFrame();
		}
		String pageContributor = row.get("Page_Contributor");
		if(!"".equalsIgnoreCase(pageContributor) && !"N/A".equalsIgnoreCase(pageContributor))
		{
			String[] pageContList = pageContributor.split("\\#");
			for (int i=0; i<pageContList.length; i++)
			{
				if(i!=0) 
				{
					clickElement(guiMap.get("AddAnotherItem"));
					holdSeconds(3);
				}
				String[] contributors = pageContList[i].split("\\|");
				for(int j=0; j<contributors.length;j++)
				{
					selectNiemElementByText(replaceGui(guiMap.get("SelectPageContributor"),(i+1)+""), contributors[j], j+1);
				}
			}
		}
		selectElementByText(guiMap.get("SaveAs"), row.get("Save_As"));
		updateHtmlReport("Fill up a form for ["+type+"]",  "User is able to fill up a form for ["+type+"]", "As expected", 
				"Step", "pass", "Fill up a form _"+type, true);
		clickElementJs(guiMap.get("ButtonSave"));
		if(checkElementExists(guiMap.get("ContentCreated")))
		{
			highlightElement(guiMap.get("ContentCreated"), "green");
			updateHtmlReport("Create content ["+type+"]",  "User is able to create ["+type+"]", "As expected", 
					"Step", "pass", "Create content "+type);
			created = true;
		}else
		{
			highlightElement(guiMap.get("ContentCreated"), "red");
			updateHtmlReport("Create content ["+type+"]",  "User is able to create ["+type+"]", "Not as expected", 
					"Step", "fail", "Create content "+type);
			created =  false;
		}
		return created;
	}
	
	
	
	/**
	 * This method login to ADCVD web application
	 * @param url: url for the application
	 * @param user: user
	 * @param password: password
	 * @exception Exception
	 */
	public static boolean loginToExporter(String url, 
									   String user, 
									   String password) throws Exception
	{
		boolean loginStatus = true;
		navigateTo(url);
		int currentWait = setBrowserTimeOut(2);
		if(checkElementExists(guiMap.get("LogOut")))
		{
			clickElement(guiMap.get("LogOut"));
			navigateTo(url);
		}
		setBrowserTimeOut(currentWait);
		enterTextAndClear(guiMap.get("UserName"), user);
		enterTextAndClear(guiMap.get("Password"), password);
		clickElementJs(guiMap.get("SubmitButton"));
		if(! checkElementExists(guiMap.get("HomePage")))
		{
			failTestSuite("Login to Exporter portal with user "+user, 
					"User is able to login", 
				"Not as expected", "Step", "fail", "Login failed");
			loginStatus = false;
		}else
		{
			highlightElement(guiMap.get("HomePage"), "green");
			updateHtmlReport("Login to Exporter portal with user "+user,  "User is able to login", "As expected", 
					"Step", "pass", "Login to exporter");
		}
		return loginStatus;
	}
	/**
	 * This method creates new E-File document
	 * @param row: map of test case's data
	 * @return true case created correctly, false if not
	 * @exception Exception
	*/
	public static boolean createEFileDocument(LinkedHashMap<String, String> row) throws Exception
	{
		boolean create = true;
		create = true;
		String type = row.get("type")+"";
		clickElementJs(guiMap.get("EFileDocument"));
		enterTextAndClear(guiMap.get("txtCaseNumber"), row.get("Case_Number"));
		selectElementByValue(guiMap.get("Segment"), row.get("Segment"));
		selectElementByValue(guiMap.get("SecurityClassification"), row.get("Security_Classification"));
		selectElementByValue(guiMap.get("DocumentType"), row.get("Document_Type"));
		enterText(guiMap.get("FiledOnBehalfOf"), row.get("Filed_On_Behalf_Of"));
		int nbrUpload=0;
		if (type.equalsIgnoreCase("manual submission"))
		{
			clickElementJs(guiMap.get("manualSubmission"));
			holdSeconds(1);
			enterText(guiMap.get("txtTitle"), row.get("Title")); 	
			enterText(guiMap.get("txtPageCount"), row.get("Page_Count"));
			clickElementJs(guiMap.get("submitButton"));
			holdSeconds(2);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			holdSeconds(2);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			int currentWait = setBrowserTimeOut(40);
			if(!checkElementExists(guiMap.get("barCode")))
			{
				setBrowserTimeOut(currentWait);
				failTestSuite("Manual submission", 
						"User is able to perform manual submission", 
					"Not as expected", "Step", "fail", "manual submission");
			}else
			{
				setBrowserTimeOut(currentWait);
				highlightElement(guiMap.get("barCode"), "green");
				holdSeconds(2);
				String barCode = getElementAttribute(guiMap.get("barCode"), "text");
				updateHtmlReport("Manual submission", 
						"User is able to perform manual submission", 
					"As expected", "Step", "pass", "manual submission");
			}
		}
		else
		{
			addFiles(row, 0, 0, "");
			try{
				nbrUpload = Integer.parseInt(row.get("Number_of_uploads"));
				for(int j = 1; j<=nbrUpload; j++)
				{
					if (type.equalsIgnoreCase("add more files"))
					{
						clickElementJs(guiMap.get("addMoreFiles"));
						addFiles(row, j, nbrUpload, "");
					}
					else
					{
						clickElementJs(guiMap.get("similarSubmissionButton"));
						addFiles(row, j, nbrUpload, row.get("Security_Classification"));
					}
				}
			}catch(Exception e)
			{
				create = false;
				e.printStackTrace();
				failTestCase("Number_of_uploads = "+row.get("Number_of_uploads"), 
						"Number_of_uploads should have a number",
						"Not as expected", "Step", "fail", "");
			}
		}
		return create;
	}
	/**
	 * This method creates new entry of appearance
	 * @param row: map of test case's data
	 * @return true if EOA is created, false if not
	 * @exception Exception
	*/
	public static boolean createEntryOfAppearance(LinkedHashMap<String, String> row) throws Exception
	{
		boolean create = true;
		int i = 0;
		create = true;
		clickElementJs(guiMap.get("manageEntryOfAppearance"));		
		clickElementJs(guiMap.get("entryOfAppearanceLink"));
		String caseNumber = row.get("Case_Number");
		enterTextAndClear(guiMap.get("txtCaseNumber"), caseNumber);
		selectElementByValue(guiMap.get("SelectIsparticipantrepresentedcounsel"), "Yes");
		clickElementJs(guiMap.get("checkParticipant"));
		enterText(guiMap.get("txtParticipant"), row.get("Text_Participant"));
		enterTextAndClear(guiMap.get("FullAddress"), row.get("Full_Address"));
		String [] segmentOption = getSelectValues(guiMap.get("CaseSegment"));
		for(; i<segmentOption.length;i++)
		{
			String opt = segmentOption[i];
			scrollToElement(guiMap.get("CaseSegment"));
			selectElementByText(guiMap.get("CaseSegment"), opt);
			clickElementJs(guiMap.get("submitButton"));
			int currentWait = setBrowserTimeOut(3);
			if(checkElementExists(guiMap.get("barCode")))
			{
				highlightElement(guiMap.get("barCode"), "green");
				holdSeconds(2);
				String barCode = getElementAttribute(guiMap.get("barCode"), "text");
				updateHtmlReport("submitting EOA with case segment = ["+opt+"]", 
						"User is able to submit entry of appearance", 
					"Bar Code: <span class = 'boldy'>"+barCode +"</span>", "VP", "pass", "Submitting EOA - "+removeSpecialChar(opt));
				setBrowserTimeOut(currentWait);
				break;
			}
			else
			{
				if(checkElementExists(guiMap.get("EntryofAppearanceExistMsg")))
				{
					updateHtmlReport("Submitting EOA with case segment = ["+opt+"]", 
							"Already submitted error message has shown", 
						"As expected", "VP", "pass", "Submitting EOA - " +removeSpecialChar(opt));
					setBrowserTimeOut(currentWait);
				}
				else
				{
					updateHtmlReport("Submitting EOA with case segment = ["+opt+"]",
							"Either barcode or enty of appearance error message should be displayed", 
						"Not as expected", "VP", "fail", "Submitting EOA - "+ removeSpecialChar(opt));
					setBrowserTimeOut(currentWait);
					break;
				}
			}
		}
		if(i==segmentOption.length)
		{
			HtmlReport.addHtmlStep("<span class = 'Warning'>Submitting EOA with All existing case segments</span>", 
					"<span class = 'Warning'>Submit with the available case segment</span>" ,
					"<span class = 'Warning'>All case segments are already submitted for this case number "
					+ " ... ["+caseNumber+"]</span>" , 
					"<span class = 'Warning'>VP</span>",
					"Warning", "Submitting EOA - All options");
		}
		return create;
	}
	
	/**
	 * This method checks fields for other EOA for my firm 
	 * @param row: map of test case's data
	 * @return true if case number and firm name are populated, false if not
	 * @exception Exception
	*/
	public static boolean otherEntryOfAppearanceCheckFields(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true;
		String ExpectedCaseNumber = row.get("Case_Number");
		clickElementJs(guiMap.get("manageEntryOfAppearance"));
		if(elementExists(replaceGui(guiMap.get("EoaCaseNumberLink_2"), ExpectedCaseNumber)))
		{
			scrollToNiemElement(replaceGui(guiMap.get("EoaCaseNumberLink_2"), ExpectedCaseNumber), 1);
			highlightNiemElement(replaceGui(guiMap.get("EoaCaseNumberLink_2"), ExpectedCaseNumber), "blue", 1);
			updateHtmlReport("Find a case number in the list", "Case number should be available",
					"As expected", "Step", "pass", "find case number in the lis - other EOA");
			clickNiemElementJs(replaceGui(guiMap.get("EoaCaseNumberLink_2"), ExpectedCaseNumber),1);
		}
		else
		{
			failTestCase("Validate entry of appearance fields (for my firm)", ExpectedCaseNumber+ " should be displayed", 
					"Not as expected", "Step", "fail", "Validate entry of appearance");
		}
		String actualCaseNumber = getElementAttribute(guiMap.get("txtCaseNumber"), "value");
		
		if (actualCaseNumber.equalsIgnoreCase(ExpectedCaseNumber))
		{
			highlightElement(guiMap.get("txtCaseNumber"), "green");
			updateHtmlReport("Validate case number", "The case number should be populated and have the same clicked case number value",
					"As expected", "VP", "pass", "Screen shot - case number verify");
		}
		else
		{
			validate = false;
			highlightElement(guiMap.get("txtCaseNumber"), "red");
			updateHtmlReport("Validate case number", "The Case number should be populated"
					+ " and have the same clicked case number",
					"Case number is either Empty or doesn't match", "VP", "pass", "Screen shot - case number verify");
		}
		scrollToElement(guiMap.get("txtFirmName"));
		String actualfirmName = getElementAttribute(guiMap.get("txtFirmName"), "value");
		if (!"".equalsIgnoreCase(actualfirmName))
		{
			highlightElement(guiMap.get("txtFirmName"), "green");
			updateHtmlReport("validate firm name", "The firm name should be populated",
					"As expected", "VP", "pass", "Screen shot - firm name verify");
		}
		else
		{
			validate = false;
			highlightElement(guiMap.get("txtFirmName"), "green");
			updateHtmlReport("validate firm name", "The firm name should be populated",
					"Not as expected", "VP", "pass", "Screen shot - firm name verify");
		}
		return validate;
	}
	
	/**
	 * This method checks fields for other APO for my firm 
	 * @param row: map of test case's data
	 * @return true if case number and firm name are populated, false if not
	 * @exception Exception
	*/
	public static boolean otherApoCheckFields(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true;
		String ExpectedCaseNumber = row.get("Case_Number");
		clickElementJs(guiMap.get("manageAPOApplication"));
		if(elementExists(replaceGui(guiMap.get("ApoCaseNumberLink_2"), ExpectedCaseNumber)))
		{
			scrollToNiemElement(replaceGui(guiMap.get("ApoCaseNumberLink_2"), ExpectedCaseNumber), 1);
			highlightNiemElement(replaceGui(guiMap.get("ApoCaseNumberLink_2"), ExpectedCaseNumber), "blue", 1);
			updateHtmlReport("Find a case number in the list", "Case number should be available",
					"As expected", "Step", "pass", "find case number in the lis - other APO");
			clickNiemElementJs(replaceGui(guiMap.get("ApoCaseNumberLink_2"), ExpectedCaseNumber),1);
		}
		else
		{
			failTestCase("Validate APO fields (for my firm)", ExpectedCaseNumber+ " should be displayed", 
					"Not as expected", "Step", "fail", "Validate entry of appearance");
		}
		String actualCaseNumber = getElementAttribute(guiMap.get("APOtxtCaseNumber"), "value");
		if (actualCaseNumber.equalsIgnoreCase(ExpectedCaseNumber))
		{
			highlightElement(guiMap.get("APOtxtCaseNumber"), "green");
			updateHtmlReport("Validate case number", "The case number should be populated and have"
					+ " the same clicked case number value",
					"As expected", "VP", "pass", "Screen shot - case number verify");
		}
		else
		{
			validate = false;
			highlightElement(guiMap.get("APOtxtCaseNumber"), "red");
			updateHtmlReport("Validate case number", "The Case number should be populated"
					+ " and have the same clicked case number",
					"Case number is either Empty or doesn't match", "VP", "pass", "Screen shot - case number verify");
		}
		scrollToElement(guiMap.get("txtFirmName"));
		String actualfirmName = getElementAttribute(guiMap.get("txtFirmName"), "value");
		if (!"".equalsIgnoreCase(actualfirmName))
		{
			highlightElement(guiMap.get("txtFirmName"), "green");
			updateHtmlReport("validate firm name", "The firm name should be populated",
					"As expected", "VP", "pass", "Screen shot - firm name verify");
		}
		else
		{
			validate = false;
			highlightElement(guiMap.get("txtFirmName"), "green");
			updateHtmlReport("validate firm name", "The firm name should be populated",
					"Not as expected", "VP", "pass", "Screen shot - firm name verify");
		}
		return validate;
	}
	/**
	 * This method creates new APO application
	 * @param row: map of test case's data
	 * @return true if APO created, false if not
	 * @exception Exception
	*/
	public static boolean createApoApplication(LinkedHashMap<String, String> row) throws Exception
	{
		boolean create = true;
		create = true;
		int i=0;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date todayDate = new Date();
		String todayStr = dateFormat.format(todayDate);
		clickElementJs(guiMap.get("manageAPOApplication"));		
		clickElementJs(guiMap.get("NewAPOLink"));
		String caseNumber = row.get("Case_Number");
		enterTextAndClear(guiMap.get("APOtxtCaseNumber"), caseNumber);
		holdSeconds(1);
		enterTextAndClear(guiMap.get("FullAddress"), row.get("Full_Address"));
		holdSeconds(1);
		clickElementJs(guiMap.get("checkParticipant"));
		holdSeconds(1);		
		scrollToElement(guiMap.get("txtParticipant"));
		enterText(guiMap.get("txtParticipant"), row.get("Text_Participant"));
		String [] segmentOption = getSelectValues(guiMap.get("CaseSegmentAPO"));
		scrollToElement(guiMap.get("CaseSegmentAPO"));
		for(;i<segmentOption.length;i++)
		{
			String opt = segmentOption[i];
			selectElementByText(guiMap.get("CaseSegmentAPO"), opt);
			int currentWait = setBrowserTimeOut(2);
			if(checkElementExists(guiMap.get("requestAdminReviewDate")))
			{
				enterTextAndClear(guiMap.get("requestAdminReviewDate"), todayStr);
			}
			setBrowserTimeOut(currentWait);
			clickElementJs(guiMap.get("submitButton"));
			currentWait = setBrowserTimeOut(3);
			if(checkElementExists(guiMap.get("barCode")))
			{
				highlightElement(guiMap.get("barCode"), "green");
				holdSeconds(2);
				String barCode = getElementAttribute(guiMap.get("barCode"), "text");
				updateHtmlReport("Submitting APO with case segment = ["+opt+"]", 
						"User is able to submit an APO", 
					"Bar Code:  <span class = 'boldy'>"+barCode+"</span>", "VP", "pass", "Submitting APO - "+removeSpecialChar(opt));
				setBrowserTimeOut(currentWait);
				break;
			}
			else
			{
				if(checkElementExists(guiMap.get("APOExistMsg")))
				{
					updateHtmlReport("Submitting APO with case segment = ["+opt+"]", 
							"Already submitted error message has shown", 
						"As expected", "VP", "pass", "Submitting APO - " +removeSpecialChar(opt));
					setBrowserTimeOut(currentWait);
				}
				else
				{
					updateHtmlReport("Submitting APO with case segment = ["+opt+"]",
							"Either barcode or enty of appearance error message should be displayed", 
						"Not as expected", "VP", "fail", "Submitting APO - "+ removeSpecialChar(opt));
					setBrowserTimeOut(currentWait);
					break;
				}
			}
		}
		if(i==segmentOption.length)
		{
			HtmlReport.addHtmlStep("<span class = 'Warning'>Submitting APO with All existing case segments</span>", 
					"<span class = 'Warning'>Submit with the available case segment</span>" ,
					"<span class = 'Warning'>All case segments are already submitted for this case number "
					+ " ... ["+caseNumber+"]</span>" , 
					"<span class = 'Warning'>VP</span>",
					"Warning", "Submitting APO - All options");
		}
		return create;
	}
	
	/**
	 * This method validates fields help messages of e file
	 * @param row: map of test case's data
	 * @return true if all help messages match, false if not
	 * @exception Exception
	*/
	public static boolean validateFieldsEFileHelpMessages(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true, scl=true;;
		clickElementJs(guiMap.get("EFileDocument"));
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			String fieldName = entry.getKey().trim();
			String fieldExpectedMessage = entry.getValue().trim();
			switch (entry.getKey())
			{
				case "Quick Search":
				{ 	
					highlightElement(guiMap.get("fieldHelpLink_4"), "blue");
					clickElementJs(guiMap.get("fieldHelpLink_4"));
					break;
				}
				case "Title": case "Upload File(s)":
				{
					scrollToElement(replaceGui(guiMap.get("fieldHelpLink_2"),fieldName));
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_2"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_2"),fieldName));
					//
					break;
				}
				case "Submit": case "Reset": case "Cancel":
				{
					if(scl)	scrollToElement(replaceGui(guiMap.get("fieldHelpLink_3"),fieldName));
					scl=false;
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_3"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_3"),fieldName));
					break;
				}
				default:
				{
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName));
					break;
				}
			}//switch
			String originalHadle = switchToWindow();
			String fieldActualMessage = getElementAttribute(guiMap.get("helpMessage"), "text");
			if (fieldActualMessage.equalsIgnoreCase(fieldExpectedMessage))
			{
				highlightElement(guiMap.get("helpMessage"), "green");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+fieldName);
			}
			else
			{
				validate = false;
				highlightElement(guiMap.get("helpMessage"), "red");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+fieldName);
			}
			switchBackToWindow(originalHadle);
		}
		return validate;
	}
	
	/**
	 * This method validates fields of update profile help messages
	 * @param row: map of test case's data
	 * @return true if all help messages match, false if not
	 * @exception Exception
	*/
	public static boolean validateFieldsUpdateProfileHelpMessages(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true;
		clickElementJs(guiMap.get("updateProfile"));
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			String fieldName = entry.getKey().trim();
			String fieldExpectedMessage = entry.getValue().trim();
			switch (entry.getKey())
			{
				case "Quick Search":
				{ 	
					highlightElement(guiMap.get("fieldHelpLink_4"), "blue");
					clickElementJs(guiMap.get("fieldHelpLink_4"));
					break;
				}
				case "Security Question 1": 
				{
					scrollToElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")));
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), "blue", 1);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), 1);
					//
					break;
				}
				case "Answer 1":
				{
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), "blue", 1);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), 1);
					break;
				}
				case "Security Question 2": 
				{
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), "blue", 2);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), 2);
					//
					break;
				}
				case "Answer 2":
				{
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), "blue", 2);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), 2);
					break;
				}
				case "Submit": case "Reset": case "Cancel":
				{
					if (entry.getKey().equalsIgnoreCase("Submit")) 
						scrollToElement(replaceGui(guiMap.get("fieldHelpLink_6"),fieldName));
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_3"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_3"),fieldName));
					break;
				}
				default:
				{
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName));
					break;
				}
			}//switch
			String originalHadle = switchToWindow();
			String fieldActualMessage = getElementAttribute(guiMap.get("helpMessage"), "text");
			if (fieldActualMessage.equalsIgnoreCase(fieldExpectedMessage))
			{
				highlightElement(guiMap.get("helpMessage"), "green");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+fieldName.replace(":", ""));
			}
			else
			{
				validate = false;
				highlightElement(guiMap.get("helpMessage"), "red");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+fieldName);
			}
			switchBackToWindow(originalHadle);
		}
		return validate;
	}
	
	/**
	 * This method validates fields of register page help messages
	 * @param row: map of test case's data
	 * @return true if all messages match, false if not
	 * @exception Exception
	*/
	public static boolean validateFieldsRegisterHelpMessages(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true;
		int currentWait = setBrowserTimeOut(3);
		if(elementExists(guiMap.get("linkLogout")))
		{
			clickElementJs(guiMap.get("linkLogout"));
		}
		clickElementJs(guiMap.get("linkEfileRegister"));
		setBrowserTimeOut(currentWait);
		clickElementJs(guiMap.get("buttonAccept"));
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			String fieldName = entry.getKey().trim();
			String fieldExpectedMessage = entry.getValue().trim();
			switch (entry.getKey())
			{
				case "Quick Search":
				{ 	
					highlightElement(guiMap.get("fieldHelpLink_4"), "blue");
					clickElementJs(guiMap.get("fieldHelpLink_4"));
					break;
				}
				case "Security Question 1": 
				{
					scrollToElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")));
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), "blue", 1);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), 1);
					break;
				}
				case "Answer 1":
				{
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), "blue", 1);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 1", "")), 1);
					break;
				}
				case "Security Question 2": 
				{
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), "blue", 2);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), 2);
					break;
				}
				case "Answer 2":
				{
					highlightNiemElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), "blue", 2);
					clickNiemElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName.replace(" 2", "")), 2);
					break;
				}
				case "Submit": case "Reset": case "Cancel":
				{
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_3"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_3"),fieldName));
					break;
				}
				default:
				{
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_1"),fieldName));
					break;
				}
			}//switch
			String originalHadle = switchToWindow();
			String fieldActualMessage = getElementAttribute(guiMap.get("helpMessage"), "text");
			if (fieldActualMessage.equalsIgnoreCase(fieldExpectedMessage))
			{
				highlightElement(guiMap.get("helpMessage"), "green");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName));
			}
			else
			{
				validate = false;
				highlightElement(guiMap.get("helpMessage"), "red");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+fieldName);
			}
			switchBackToWindow(originalHadle);
		}
		return validate;
	}
	
	/**
	 * This method check messages for for e-files uploads
	 * @param listOfFiles: list of files to be uploaded
	 * @return true if the the displayed message is as expected, false if not
	 * @exception Exception
	*/
	public static boolean checkFilesEfile(ArrayList<LinkedHashMap<String, String>> listOfFiles) throws Exception
	{
		/*IE: "\"" + "C:\\Selenium\\TestData\\Flexy - BigFile1.txt"+"\"" +"\""+"C:\\Selenium\\TestData\\Flexy - BigFile2.txt" + "\""
		CHROME:	"C:\\Selenium\\TestData\\Flexy - BigFile1.txt"+"\n"+"C:\\Selenium\\TestData\\Flexy - BigFile2.txt".*/
		boolean validate = true;
		int iterator = 0;
		String fileName, allFiles = "";
		clickElementJs(guiMap.get("checkEfiles"));
		for (LinkedHashMap<String, String> row : listOfFiles) 
		{
			iterator++;
			String expectedMessage = row.get("Expected_Message");
			String[] fileList = row.get("File_Name").split("\\|");
			for (int j=0; j<fileList.length; j++)
			{
				fileName = InitTools.getInputDataFolder()+"/input_files/"+fileList[j];
				if (j==0) allFiles = fileName;
				else allFiles = allFiles+"\n"+fileName;
			}
			enterTextFile(guiMap.get("uploadCheckFilesButton"), allFiles);
			clickElementJs(guiMap.get("checkFilesButton"));
			holdSeconds(2);
			int currentWait = setBrowserTimeOut(6);
			while(checkElementVisible(guiMap.get("fileProgress")))
			{
				holdSeconds(2);
				System.out.println("progress, waiting...");
			}
			setBrowserTimeOut(currentWait);
			String displayedMessage = getElementAttribute(guiMap.get("uploadDisplayedMessage"), "text");
			if (expectedMessage.equalsIgnoreCase(displayedMessage))
			{
				highlightElement(guiMap.get("uploadDisplayedMessage"), "green");
				updateHtmlReport("Upload files["+row.get("File_Name").replace("|", ", ")+"]", 
						"Expected message is ["+expectedMessage+"]",
						"Actual message is ["+displayedMessage+"]", "VP", "pass", 
						"Screen shot - file upload "+iterator
				);
				unHighlightElement(guiMap.get("uploadDisplayedMessage"));
			}
			else
			{
				validate = false;
				highlightElement(guiMap.get("uploadDisplayedMessage"), "red");
				updateHtmlReport("Upload files["+row.get("File_Name").replace("|", ", ")+"]",
						"Expected message is ["+expectedMessage+"]",
						"Actual message is ["+displayedMessage+"]", "VP", "fail", 
						"Screen shot - file upload "+iterator
				);
				unHighlightElement(guiMap.get("uploadDisplayedMessage"));
			}
		}
		return validate;
	}
	
	/**
	 * This method validates fields of entry of appearance help messages of e file
	* @param row: map of test case's data
	* @param caseNumber, case number
	* @return true if all messages are matching, false if not
	* @exception Exception
	*/
	public static boolean validateFieldsManageEntryOfAppearanceHelpMessages(LinkedHashMap<String, String> row,
																			String caseNumber) throws Exception
	{
		boolean validate = true;
		int currentWait = setBrowserTimeOut(10);
		clickElementJs(guiMap.get("manageEntryOfAppearance"));
		if(elementExists(replaceGui(guiMap.get("EoaCaseNumberLink"), caseNumber)))
		{
			scrollToNiemElement(replaceGui(guiMap.get("EoaCaseNumberLink"), caseNumber), 1);
			highlightNiemElement(replaceGui(guiMap.get("EoaCaseNumberLink"), caseNumber), "blue", 1);
			updateHtmlReport("Find a case number in the list", "Case number should be available",
					"As expected", "Step", "pass", "find case number in the list EOA");
			clickNiemElementJs(replaceGui(guiMap.get("EoaCaseNumberLink"), caseNumber),1);
		}
		else
		{
			failTestCase("Validate entry of appearance", caseNumber+ " should be displayed", 
					"Not as expected", "Step", "fail", "Validate entry of appearance");
		}
		setBrowserTimeOut(currentWait);
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			String fieldName = entry.getKey().trim();
			String fieldExpectedMessage = entry.getValue().trim();
			switch (entry.getKey())
			{
				case "Quick Search":
				{ 	
					highlightElement(guiMap.get("fieldHelpLink_4"), "blue");
					clickElementJs(guiMap.get("fieldHelpLink_4"));
					break;
				}
				case "Submit": case "Reset": case "Cancel":
				{
					if (entry.getKey().equalsIgnoreCase("Submit")) 
					scrollToElement(replaceGui(guiMap.get("fieldHelpLink_6"),fieldName));
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_6"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_6"),fieldName));
					break;
				}
				
				default:
				{
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_5"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_5"),fieldName));
					break;
				}
			}//switch
			String originalHadle = switchToWindow();
			String fieldActualMessage = getElementAttribute(guiMap.get("helpMessage"), "text");
			if (fieldActualMessage.equalsIgnoreCase(fieldExpectedMessage))
			{
				highlightElement(guiMap.get("helpMessage"), "green");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName));
			}
			else
			{
				validate = false;
				highlightElement(guiMap.get("helpMessage"), "red");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName));
			}
			switchBackToWindow(originalHadle);
		}
		return validate;
	}

	/**
	 * This method validates fields of APO help messages of e file
	 * @param row: map of test case's data
	 * @param caseNumber, case number
	 * @return true if all messages are matching, false if not
	 * @exception Exception
	*/
	public static boolean validateFieldsManageApoApplicationHelpMessages(LinkedHashMap<String, String> row,
																		 String caseNumber) throws Exception
	{
		boolean validate = true;
		int currentWait = setBrowserTimeOut(10);
		clickElementJs(guiMap.get("manageAPOApplication"));
		if(elementExists(replaceGui(guiMap.get("ApoCaseNumberLink"), caseNumber)))
		{
			scrollToNiemElement(replaceGui(guiMap.get("ApoCaseNumberLink"), caseNumber), 1);
			highlightNiemElement(replaceGui(guiMap.get("ApoCaseNumberLink"), caseNumber), "blue", 1);
			updateHtmlReport("Find a case number in the list", "Case number should be available",
					"As expected", "Step", "pass", "find case number in the list APO");
			clickNiemElementJs(replaceGui(guiMap.get("ApoCaseNumberLink"), caseNumber),1);
		}else
		{
			failTestCase("Validate manage APO Application", "case number "+ caseNumber+ " should be displayed", 
					"Not as expected", "Step", "fail", "manage APO Application");
		}
		setBrowserTimeOut(currentWait);
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			String fieldName = entry.getKey().trim();
			String fieldExpectedMessage = entry.getValue().trim();
			switch (entry.getKey())
			{
				case "Quick Search":
				{ 	
					highlightElement(guiMap.get("fieldHelpLink_4"), "blue");
					clickElementJs(guiMap.get("fieldHelpLink_4"));
					break;
				}
				case "Submit": case "Reset": case "Cancel":
				{
					if (entry.getKey().equalsIgnoreCase("Submit")) 
					scrollToElement(replaceGui(guiMap.get("fieldHelpLink_6"),fieldName));
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_6"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_6"),fieldName));
					break;
				}
				
				default:
				{
					highlightElement(replaceGui(guiMap.get("fieldHelpLink_5"),fieldName), "blue");
					clickElementJs(replaceGui(guiMap.get("fieldHelpLink_5"),fieldName));
					break;
				}
			}//switch
			String originalHadle = switchToWindow();
			String fieldActualMessage = getElementAttribute(guiMap.get("helpMessage"), "text");
			if (fieldActualMessage.equalsIgnoreCase(fieldExpectedMessage))
			{
				highlightElement(guiMap.get("helpMessage"), "green");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+
				removeSpecialChar(fieldName));
			}
			else
			{
				validate = false;
				highlightElement(guiMap.get("helpMessage"), "red");
				updateHtmlReport(fieldName + " (?)", "Expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName));
			}
			switchBackToWindow(originalHadle);
		}
		return validate;
	}
	/**
	 * This method validates fields error messages
	 * @param list: list of scenarios to be tested
	 * @return true if all scenarios passed, false if not
	 * @exception Exception
	*/
	public static boolean ValidateFieldsErrorMessages(
			ArrayList<LinkedHashMap<String, String>> list) throws Exception
	{
		boolean validate = true;
		int i=0;
		String previousFiledName= "";
		pageRefresh(); 
		clickElementJs(guiMap.get("EFileDocument"));
		for (LinkedHashMap<String, String> row: list) 
		{
			holdSeconds(1);
			i++;
			String fieldName = row.get("Filed Name").trim();
			String givenValue = row.get("Given Value").trim();
			String errorValue, errorValue2;
			if(!previousFiledName.equalsIgnoreCase(fieldName))
			{
				HtmlReport.addHtmlStepTitle("VALIDATE FIELD: "+fieldName.toUpperCase(),"Title");
			}
			switch (fieldName)
			{
				case "Case Number": 
				{ 	
					
					enterTextAndClear(guiMap.get("txtCaseNumber"), givenValue);
					holdSeconds(1);
					clickElementJs(guiMap.get("submitButton"));
					if(givenValue.equals(""))
					{
						errorValue = getElementAttribute(replaceGui(guiMap.get("fieldError_1"),
								fieldName), "Style");
						if( errorValue.contains("display: inline") )
						{
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "green");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName));
							
						}else
						{
							validate = false;
							highlightElement(guiMap.get("txtCaseNumber"), "red");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("txtCaseNumber"));
						}
					}else//not empty
					{
						if(caseNumberFormat(givenValue))//not right format
						{
							errorValue = getElementAttribute(replaceGui(guiMap.get("fieldError_1"),
									fieldName), "Style");
							if( errorValue.contains("display: none") )
							{
								highlightElement(guiMap.get("txtCaseNumber"), "green");
								updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should not be displayed",
										"As expected", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
								unHighlightElement(guiMap.get("txtCaseNumber"));
								
							}else
							{
								validate = false;
								highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "red");
								updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should not be displayed",
										"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
								unHighlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName));
							}
						}else // not right format
						{
							if( elementExists(guiMap.get("invalidCaseNumber")) )
							{
								highlightElement(guiMap.get("invalidCaseNumber"), "green");
								updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
										"As expected", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
								unHighlightElement(guiMap.get("invalidCaseNumber"));
								
							}else
							{
								validate = false;
								highlightElement(guiMap.get("txtCaseNumber"), "red");
								updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
										"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
								unHighlightElement(guiMap.get("txtCaseNumber"));
							}
						}
					}
					break;
				}
				case "Segment": case "Security Classification": 
				case "Document Type": case "Segment Specific Information":
				{
					if(!givenValue.equals(""))
					{
						selectElementByText(replaceGui(guiMap.get("filedSelect_1"), fieldName), givenValue);
						clickElementJs(guiMap.get("submitButton"));
						errorValue = getElementAttribute(replaceGui(guiMap.get("fieldError_1"),
								fieldName), "Style");
						if( errorValue.contains("display: none") )
						{
							highlightElement(replaceGui(guiMap.get("filedSelect_1"), fieldName), "green");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should not be displayed",
									"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(replaceGui(guiMap.get("filedSelect_1"), fieldName));
							
						}else
						{
							validate = false;
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "red");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should not be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("fieldError_1"));
						}
					}else//empty
					{
						clickElementJs(guiMap.get("submitButton"));
						errorValue = getElementAttribute(replaceGui(guiMap.get("fieldError_1"),
								fieldName), "Style");
						if( errorValue.contains("display: inline") )
						{
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "green");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName));
							
						}else
						{
							validate = false;
							highlightElement(replaceGui(guiMap.get("filedSelect_1"), fieldName), "red");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(guiMap.get("filedSelect_1"));
						}
					}
					break;
				}
				case "Filed On Behalf Of (collective entity)":
				{
					enterText(guiMap.get("FiledOnBehalfOf"), givenValue);
					clickElementJs(guiMap.get("submitButton"));	
					holdSeconds(1);
					errorValue = getElementAttribute(guiMap.get("FiledOnBehalfOfError"), "Style");
					if(!givenValue.equals(""))
					{
						if( errorValue.contains("display: none") )
						{
							highlightElement(guiMap.get("FiledOnBehalfOf"), "green");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should not be displayed",
									"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(guiMap.get("FiledOnBehalfOf"));
							
						}else
						{
							validate = false;
							highlightElement(guiMap.get("FiledOnBehalfOfError"), "red");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should not be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("FiledOnBehalfOfError"));
						}
					}else//empty
					{
						if( errorValue.contains("display: inline") )
						{
							highlightElement(guiMap.get("FiledOnBehalfOfError"), "green");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("FiledOnBehalfOfError"));
							
						}else
						{
							validate = false;
							highlightElement(guiMap.get("FiledOnBehalfOf"), "red");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("FiledOnBehalfOf"));
						}
					}
					break;
				}
				case "Title":
				{
					scrollToElement(replaceGui(guiMap.get("fieldHelpLink_2"), "Title"));
					if(!givenValue.equals(""))
					{
						String fileName = InitTools.getInputDataFolder()+"/input_files/test_file_1.xlsx";
						enterTextAndClear(guiMap.get("txtCaseNumber"), "");
						enterText(guiMap.get("fileUploadText1"), givenValue);
						enterTextFile(guiMap.get("fileUploadButton1"), fileName);
						clickElementJs(guiMap.get("submitButton"));
						errorValue = getElementAttribute(guiMap.get("fieldError_title"), "Style");
						errorValue2 = getElementAttribute(guiMap.get("fieldError_fileUpload"), "Style");
						scrollToElement(replaceGui(guiMap.get("fieldHelpLink_2"), "Title"));
						if( errorValue.contains("display: none") )
						{
							highlightElement(guiMap.get("fileUploadText1"), "green");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("fileUploadText1"));
							
						}else
						{
							validate = false;
							highlightElement(guiMap.get("fieldError_title"), "red");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("fieldError_title"));
						}
						if( errorValue2.contains("display: none") )
						{
							highlightElement(guiMap.get("fileUploadButton1"), "green");
							updateHtmlReport("file upload = [test_file_1.xlsx]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - file upload -" + i);
							unHighlightElement(guiMap.get("fileUploadButton1"));
							
						}else
						{
							validate = false;
							highlightElement(guiMap.get("fieldError_fileUpload"), "red");
							updateHtmlReport("file upload = [test_file_1.xlsx]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - file upload -" + i);
							unHighlightElement(guiMap.get("fieldError_fileUpload"));
						}
					}
					else
					{
						enterText(guiMap.get("fileUploadText1"), givenValue);
						clickElementJs(guiMap.get("submitButton"));					
						errorValue = getElementAttribute(guiMap.get("fieldError_title"), "Style");
						errorValue2 = getElementAttribute(guiMap.get("fieldError_fileUpload"), "Style");
						if( errorValue.contains("display: inline") )
						{
							highlightElement(guiMap.get("fieldError_title"), "green");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("fieldError_title"));
							
						}else
						{
							validate = false;
							highlightElement(guiMap.get("fileUploadText1"), "red");
							updateHtmlReport(fieldName+" = ["+givenValue+"]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+removeSpecialChar(fieldName) + "-" + i);
							unHighlightElement(guiMap.get("fileUploadText1"));
						}
						if( errorValue2.contains("display: inline") )
						{
							highlightElement(guiMap.get("fieldError_fileUpload"), "green");
							updateHtmlReport("file upload = []", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - file upload -" + i);
							unHighlightElement(guiMap.get("fieldError_fileUpload"));
							
						}else
						{
							validate = false;
							highlightElement(guiMap.get("fileUploadButton1"), "red");
							updateHtmlReport("file upload = []", "Error message should be displayed",
									"Not as expected", "VP", "pass", "Screen shot - file upload -" + i);
							unHighlightElement(guiMap.get("fileUploadButton1"));
						}
					}
					break;
				}
				default:
				{
					break;
				}
			}//switch
			previousFiledName = fieldName;
		}//for
		return validate;
	}
	/**
	 * This method browse file for upload
	 * @param row: map of test case's data
	 * @param iteration, iteration number
	 * @param total, total of uploads
	 * @param securityClass, security class
	 * @exception Exception
	*/
	public static void addFiles(LinkedHashMap<String, String> row,
								int iteration,							
								int total,
								String securityClass) throws Exception
	{
		if (!securityClass.equalsIgnoreCase(""))
		{
			selectElementByValue(guiMap.get("SecurityClassification"), securityClass);
		}
		for(int i=1; i<=5; i++)
		{
			if(!row.get("File_"+i).equals("") && !row.get("File_"+i).equals("N/A"))
			{
				String fileName = InitTools.getInputDataFolder()+"/input_files/"+row.get("File_"+i);
				holdMilliSeconds(200);
				enterText(guiMap.get("fileUploadText"+i), "Iteration: "+iteration+" _ File: "+i);
				enterTextFile(guiMap.get("fileUploadButton"+i), fileName);
			}
		}
		clickElementJs(guiMap.get("submitButton"));
		holdSeconds(2);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		int currentWait = setBrowserTimeOut(40);
		if(! checkElementExists(guiMap.get("barCode")))
		{
			setBrowserTimeOut(currentWait);
			failTestSuite("Submitting File", 
					"User is able to submit E-File Document", 
				"Not as expected", "Step", "fail", "Submitting File");
		}else
		{
			setBrowserTimeOut(currentWait);
			if (iteration==0)
			{
				highlightElement(guiMap.get("barCode"), "green");
				holdSeconds(2);
				String barCode = getElementAttribute(guiMap.get("barCode"), "text");
				updateHtmlReport("Submitting File", 
						"User is able to submit E-File Document", 
					"Bar Code:  <span class = 'boldy'>"+barCode+"</span>", "Step", "pass", "Submitting File");
			}
			
			else
			{
				updateHtmlReport("Adding Files... " +iteration+ " out of "+total, 
						"User is able to add more files", 
					"As Expected", "Step", "pass", "Adding Files "+iteration);
			}
		}
	}
	
	/**
	 * This method resubmit an EOA 
	 * @param row: map of test case's data 
	 * @return true if resubmit worked, false if not
	 * @exception Exception
	*/
	public static boolean resubmitEoa(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true;
		boolean foundOne = false;
		int j=0;
		clickElementJs(guiMap.get("manageEntryOfAppearance"));
		String[] submittedEoa = getElementValuesIntoArray(guiMap.get("EoaCaseNumberLinkAll"),"text");
		String[] toBeSubmittedEoa = getElementValuesIntoArray(guiMap.get("EoaCaseNumberLinkAll_2"),"text");
		if(toBeSubmittedEoa==null)
		{
			failTestCase("Resubmit EOA", "The list shouldn't be empty", "Not as expected", "VP", 
					"fail", "Sc-Shot - Resubmit EOA");
		}
		for(; j < toBeSubmittedEoa.length; j++)
		{
			if (!Arrays.asList(submittedEoa).contains(toBeSubmittedEoa[j]))
			{
				foundOne = true ;
				break;
			}
		}
		if(!foundOne)
		{
			failTestCase("Resubmit EOA", "At least one entry should be available for resubmission'", 
					"All the entries are already resubmitted", "VP", 
					"fail", "Sc-Shot - Resubmit EOA");
		}
		scrollToNiemElement(guiMap.get("EoaActionLink"), j+1);
		highlightNiemElement(guiMap.get("EoaActionLink"), "blue", j+1);
		updateHtmlReport("resubmit existing case number", "Case number should be available",
				"As expected", "Step", "pass", "find case nuber for resubmit");
		clickNiemElementJs(guiMap.get("EoaActionLink"), j+1);
		selectElementByValue(guiMap.get("SelectIsparticipantrepresentedcounsel"), "Yes");
		holdSeconds(2);
		clickElementJs(guiMap.get("submitButton"));
		holdSeconds(2);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		if(! checkElementExists(guiMap.get("barCode")))
		{
			failTestSuite("Submitting File", 
					"User is able to resubmit EOA Document", 
				"Not as expected", "Step", "fail", "Resubmitting EOA");
		}else
		{
			highlightElement(guiMap.get("barCode"), "green");
			holdSeconds(2);
			String barCode = getElementAttribute(guiMap.get("barCode"), "text");
			updateHtmlReport("Resubmitting EOA", 
					"User is able to submit E-File Document", 
				"Bar Code:  <span class = 'boldy'>"+barCode+"</span>", "Step", "pass", "Resubmitting EOA");
		}
		return validate;
	}
	
	/**
	 * This method resubmit and APO
	 * @param row: map of test case's data
	 * @return true the resubmit worked, false if not
	 * @exception Exception
	*/
	public static boolean resubmitApo(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true;
		boolean foundOne = false;
		int j=0;
		clickElementJs(guiMap.get("manageAPOApplication"));
		String[] submittedApo = getElementValuesIntoArray(guiMap.get("ApoCaseNumberLinkAll"),"text");
		String[] toBeSubmittedApo = getElementValuesIntoArray(guiMap.get("ApoCaseNumberLinkAll_2"),"text");
		if(toBeSubmittedApo==null)
		{
			failTestCase("Resubmit APO", "The list shouldn't be empty", "Not as expected", "VP", 
					"fail", "Sc-Shot - Resubmit APO");
		}
		for(; j < toBeSubmittedApo.length; j++)
		{
			if (!Arrays.asList(submittedApo).contains(toBeSubmittedApo[j]))
			{
				foundOne = true ;
				break;
			}
		}
		if(!foundOne)
		{
			failTestCase("Resubmit APO", "At least one entry should be available for resubmission'", 
					"All the entries are already resubmitted", "VP", 
					"fail", "Sc-Shot - Resubmit APO");
		}
		scrollToNiemElement(guiMap.get("ApoActionLink"), j+1);
		highlightNiemElement(guiMap.get("ApoActionLink"), "blue", j+1);
		updateHtmlReport("resubmit existing case number", "Case number should be available",
				"As expected", "Step", "pass", "find case nuber for resubmit");
		clickNiemElementJs(guiMap.get("ApoActionLink"), j+1);
		holdSeconds(2);
		clickElementJs(guiMap.get("submitButton"));
		holdSeconds(2);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		if(! checkElementExists(guiMap.get("barCode")))
		{
			failTestSuite("Submitting File", 
					"User is able to resubmit APO Document", 
				"Not as expected", "Step", "fail", "Resubmitting APO");
		}else
		{
			highlightElement(guiMap.get("barCode"), "green");
			holdSeconds(2);
			String barCode = getElementAttribute(guiMap.get("barCode"), "text");
			updateHtmlReport("Resubmitting EOA", 
					"User is able to submit E-File Document", 
				"Bar Code:  <span class = 'boldy'>"+barCode+"</span>", "Step", "pass", "Resubmitting APO");
		}
		return validate;
	}
	
	/**
	 * This method validate fields value and report
	 * @param fieldName: field name
	 * @param fieldExpectedMessage, field expected message
	 * @param fieldActualMessage, field actual message
	 * @return true if expected msg equal to actual msg
	 * @exception Exception
	*/
	public static boolean validateAndReport(String fieldName, 
											String fieldExpectedMessage, 
											String fieldActualMessage) throws Exception
	{
		boolean validate = true;
		if (fieldActualMessage.equalsIgnoreCase(fieldExpectedMessage))
		{
			highlightElement(guiMap.get("helpMessage"), "green");
			
			updateHtmlReport("validate field ["+fieldName+"]", "Expected message is ["+fieldExpectedMessage+"]",
					"Actual message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+fieldName);
		}
		else
		{
			validate = false;
			highlightElement(guiMap.get("helpMessage"), "red");
			updateHtmlReport("validate field ["+fieldName+"]", "Expected message is ["+fieldExpectedMessage+"]",
					"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+fieldName);
		}
		return validate;
	}
	
	/**
	 * This method checks case number format
	 * @param val: case number
	 * @return true if case number is in the format A-xxx-xxx, false if not
	 * @exception Exception
	*/
	public static boolean caseNumberFormat(String val)
	{
		if (!val.contains("-")) return false;
		String[] tab = val.split("-");
		if (tab.length !=3) return false;
		if(!tab[0].equalsIgnoreCase("A")) return false;
		if(tab[1].length()!=3 ||!isNumeric(tab[1]) || 
				tab[2].length()!=3 || !isNumeric(tab[2])) return false;
		return true;
	}
	/**
	 * This method checks if a given char is numeric
	 * @param str: given string
	 * @return true if str is numeric, false if not
	 * @exception Exception
	*/
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	
}
