/**
 * MilCorp
 * Mouloud Hamdidouche
 * December, 2018
*/

package libs;

import static GuiLibs.GuiTools.checkElementExists;
import static GuiLibs.GuiTools.clickElement;
import static GuiLibs.GuiTools.clickElementJs;
import static GuiLibs.GuiTools.enterTextFile;
import static GuiLibs.GuiTools.elementExists;
import static GuiLibs.GuiTools.enterText;
import static GuiLibs.GuiTools.failTestCase;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.getElementAttribute;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.highlightElement;
import static GuiLibs.GuiTools.holdSeconds;
import static GuiLibs.GuiTools.uploadFile;
import static GuiLibs.GuiTools.navigateTo;
import static GuiLibs.GuiTools.pageRefresh;
import static GuiLibs.GuiTools.replaceGui;
import static GuiLibs.GuiTools.scrollByPixel;
import static GuiLibs.GuiTools.scrollToElement;
import static GuiLibs.GuiTools.scrollToTheTopOfPage;
import static GuiLibs.GuiTools.selectElementByValue;
import static GuiLibs.GuiTools.setBrowserTimeOut;
import static GuiLibs.GuiTools.switchBackFromFrame;
import static GuiLibs.GuiTools.switchToFrame;
import static GuiLibs.GuiTools.unHighlightElement;
import static GuiLibs.GuiTools.updateHtmlReport;
import static GuiLibs.GuiTools.switchToAlert;
import static GuiLibs.GuiTools.switchBackToWindow;
import static GuiLibs.GuiTools.switchToWindow;
import static ReportLibs.ReportTools.printLog;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.text.FieldView;

import org.testng.ISuiteListener;

import InitLibs.InitTools;

public class AccessLib{
	public static String filedDate,
	actualInitiationSignature, calculatedInitiationSignature, petitionOutcome;
	public static int petitionInitiationExtension;
	static DateFormat format;
	static Calendar calendar;
	static String caseType;
	static String investigationId, aInvestigation, cInvestigation;
	static String orderId, adCaseId, cvdCaseId, arSegmentId;
	public AccessLib() throws IOException {
		//super();
		this.format = new SimpleDateFormat("M/d/yyyy");
		this.calendar = Calendar.getInstance();
	}
	/**
	 * This method login to ADCVD web application
	 * @param url: url for the application
	 * @param user: user
	 * @param password: password
	 * @exception Exception
	 */
	public static boolean loginToAccess(String url, 
									   String user, 
									   String password) throws Exception
	{
		boolean loginStatus = true;
		navigateTo(url);
		int currentTimeOut = setBrowserTimeOut(3);
		setBrowserTimeOut(currentTimeOut);
		enterText(guiMap.get("userName"), user);
		enterText(guiMap.get("password"), password);
		clickElementJs(guiMap.get("Agreement"));
		JOptionPane.showMessageDialog(null,
			    "Please Do the CAPTCHA manually and click 'OK'");
		holdSeconds(1);
		//clickElementJs(guiMap.get("loginTo"));
	//	holdSeconds(5);
		if(! checkElementExists(guiMap.get("AccessLink")))
		{
			failTestSuite("Login to ACCESS App with user "+user, 
					"User is able to login", 
				"Not as expected", "Step", "fail", "Login failed");
			loginStatus = false;
		}else
		{
			highlightElement(guiMap.get("AccessLink"), "green");
			holdSeconds(2);
			updateHtmlReport("Login to ACCESS App with user "+user,  "User is able to login", "As expected", 
					"Step", "pass", "Login to ACCESS");
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
		clickElementJs(guiMap.get("EFileDocument"));
		enterText(guiMap.get("txtCaseNumber"), row.get("Case_Number"));
		selectElementByValue(guiMap.get("Segment"), row.get("Segment"));
		selectElementByValue(guiMap.get("SecurityClassification"), row.get("Security_Classification"));
		selectElementByValue(guiMap.get("DocumentType"), row.get("Document_Type"));
		enterText(guiMap.get("FiledOnBehalfOf"), row.get("Filed_On_Behalf_Of"));
		//String fileName="";
		int nbrUpload=0;
		//File_2	File_Title_3
		addFiles(row, 0, 0);
		try{
			nbrUpload = Integer.parseInt(row.get("Number_of_uploads"));
			for(int j = 1; j<=nbrUpload; j++)
			{
				clickElementJs(guiMap.get("addMoreFiles"));
				addFiles(row, j, nbrUpload);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			failTestCase("Number_of_uploads = "+row.get("Number_of_uploads"), 
					"Number_of_uploads should have a number",
					"Not as expected", "Step", "fail", "");
		}
		return create;
	}
	
	/**
	 * This method validates fields help messages
	 * @param row: map of test case's data
	 * @return true case created correctly, false if not
	 * @exception Exception
	*/
	public static boolean ValidateFieldsHelpMessages(LinkedHashMap<String, String> row) throws Exception
	{
		boolean validate = true;
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
			//clickElementJs(guiMap.get("helpMessage"));
			String fieldActualMessage = getElementAttribute(guiMap.get("helpMessage"), "text");
			if (fieldActualMessage.equalsIgnoreCase(fieldExpectedMessage))
			{
				highlightElement(guiMap.get("helpMessage"), "green");
				updateHtmlReport("validate field ["+fieldName+"]", "expected message is ["+fieldExpectedMessage+"]",
						"expected message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+fieldName);
			}
			else
			{
				validate = false;
				highlightElement(guiMap.get("helpMessage"), "red");
				updateHtmlReport("validate field ["+fieldName+"]", "expected message is ["+fieldExpectedMessage+"]",
						"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+fieldName);
			}
			switchBackToWindow(originalHadle);
		}
		return validate;
	}
	/**
	 * This method validates fields help messages
	 * @param row: map of test case's data
	 * @return true case created correctly, false if not
	 * @exception Exception
	*/
	public static boolean ValidateFieldsErrorMessages(ArrayList<LinkedHashMap<String, String>> list) throws Exception
	{
		boolean validate = true;
		int i=0;
		clickElementJs(guiMap.get("EFileDocument"));
		for (LinkedHashMap<String, String> row: list) 
		{
			i++;
			System.out.println("");
			//invalidCaseNumber
			//fieldError_1
			int currentWait = 0;
			String fieldName = row.get("Filed Name").trim();
			String givenValue = row.get("Given Value").trim();
			//String expectedDisplayedError = row.get("Displayed Error");
			String errorValue;
			switch (fieldName)
			{
			
				case "Case Number": 
				{ 	
					enterText(guiMap.get("txtCaseNumber"), givenValue);
					clickElementJs(guiMap.get("submitButton"));
					holdSeconds(1);
					if(givenValue.equals(""))
					{
						errorValue = getElementAttribute(replaceGui(guiMap.get("fieldError_1"),
								fieldName), "Style");
						if( errorValue.contains("display: inline") )
						{
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "green");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName));
							
						}else
						{
							highlightElement(guiMap.get("txtCaseNumber"), "red");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+fieldName + "-" + i);
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
								updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should not be displayed",
										"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
								unHighlightElement(guiMap.get("txtCaseNumber"));
								
							}else
							{
								highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "red");
								updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should not be displayed",
										"Not as expected", "VP", "fail", "Screen shot - "+fieldName + "-" + i);
								unHighlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName));
							}
						}else // not right format
						{
							currentWait = setBrowserTimeOut(2);
							if( elementExists(guiMap.get("invalidCaseNumber")) )
							{
								highlightElement(guiMap.get("invalidCaseNumber"), "green");
								updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
										"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
								unHighlightElement(guiMap.get("invalidCaseNumber"));
								
							}else
							{
								highlightElement(guiMap.get("txtCaseNumber"), "red");
								updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
										"Not as expected", "VP", "fail", "Screen shot - "+fieldName + "-" + i);
								unHighlightElement(guiMap.get("txtCaseNumber"));
							}
						}
					}
					break;
				}
				case "Segment": case "Security Classification": 
				case "Document Type": case "Segment Specific Information":
				{
					selectElementByValue(replaceGui(guiMap.get("filedSelect_1"), fieldName), givenValue);
					clickElementJs(guiMap.get("submitButton"));
					holdSeconds(1);
					errorValue = getElementAttribute(replaceGui(guiMap.get("fieldError_1"),
							fieldName), "Style");
					if(!givenValue.equals(""))
					{
						if( errorValue.contains("display: none") )
						{
							highlightElement(replaceGui(guiMap.get("filedSelect_1"), fieldName), "green");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should not be displayed",
									"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(replaceGui(guiMap.get("filedSelect_1"), fieldName));
							
						}else
						{
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "red");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should not be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(guiMap.get("fieldError_1"));
						}
					}else//empty
					{
						if( errorValue.contains("display: inline") )
						{
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "green");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName));
							
						}else
						{
							highlightElement(replaceGui(guiMap.get("filedSelect_1"), fieldName), "red");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
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
					errorValue = getElementAttribute(replaceGui(guiMap.get("fieldError_1"),
							fieldName), "Style");
					if(!givenValue.equals(""))
					{
						if( errorValue.contains("display: none") )
						{
							highlightElement(guiMap.get("FiledOnBehalfOf"), "green");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should not be displayed",
									"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(guiMap.get("FiledOnBehalfOf"));
							
						}else
						{
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "red");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should not be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(guiMap.get("fieldError_1"));
						}
					}else//empty
					{
						if( errorValue.contains("display: inline") )
						{
							highlightElement(replaceGui(guiMap.get("fieldError_1"),	fieldName), "green");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
									"As expected", "VP", "pass", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(guiMap.get("fieldError_1"));
							
						}else
						{
							highlightElement(guiMap.get("FiledOnBehalfOf"), "red");
							updateHtmlReport("validate field ["+fieldName+" = "+givenValue+"]", "Error message should be displayed",
									"Not as expected", "VP", "fail", "Screen shot - "+fieldName + "-" + i);
							unHighlightElement(guiMap.get("FiledOnBehalfOf"));
						}
					}
					break;
				}
				case "Title":
				{
					break;
				}
				case "Upload File(s)":
				{
					break;
				}
				default:
				{
					break;
				}
			}//switch
		}//for
		return validate;
	}
	
	//selectElementByValue(guiMap.get("Segment"), row.get("Segment"));
	//selectElementByValue(guiMap.get("SecurityClassification"), row.get("Security_Classification"));
	//selectElementByValue(guiMap.get("DocumentType"), row.get("Document_Type"));
	//enterText(guiMap.get("FiledOnBehalfOf"), row.get("Filed_On_Behalf_Of"));
	/**
	 * This method creates new ADCVD case
	 * @param row: map of test case's data
	 * @return true case created correctly, false if not
	 * @exception Exception
	*/
	public static void addFiles(LinkedHashMap<String, String> row, int iteration, int total) throws Exception
	{
	
		for(int i=1; i<=5; i++)
		{
			if(!row.get("File_"+i).equals("") && !row.get("File_"+i).equals("N/A"))
			{
				String fileName = InitTools.getInputDataFolder()+"/input_files/"+row.get("File_"+i);
				enterText(guiMap.get("fileUploadText"+i), "Iteration: "+iteration+" _ File: "+i);
				enterTextFile(guiMap.get("fileUploadButton"+i), fileName);
			}
		}
		clickElementJs(guiMap.get("submitButton"));
		holdSeconds(2);
		Robot robot = new Robot();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		holdSeconds(10);
		if(! checkElementExists(guiMap.get("barCode")))
		{
			failTestSuite("submitting File", 
					"User is able to submit E-File Document", 
				"Not as expected", "Step", "fail", "submitting File");
		}else
		{
			if (iteration==0)
			{
				highlightElement(guiMap.get("barCode"), "green");
				holdSeconds(2);
				String barCode = getElementAttribute(guiMap.get("barCode"), "text");
				updateHtmlReport("submitting File", 
						"User is able to submit E-File Document", 
					"Bar Code: "+barCode, "Step", "pass", "Submitting File");
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
	 * This method validate fields value and report
	 * @param row: map of test case's data
	 * @return true case created correctly, false if not
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
			updateHtmlReport("validate field ["+fieldName+"]", "expected message is ["+fieldExpectedMessage+"]",
					"expected message is ["+fieldActualMessage+"]", "VP", "pass", "Screen shot - "+fieldName);
		}
		else
		{
			validate = false;
			highlightElement(guiMap.get("helpMessage"), "red");
			updateHtmlReport("validate field ["+fieldName+"]", "expected message is ["+fieldExpectedMessage+"]",
					"Actual message is ["+fieldActualMessage+"]", "VP", "fail", "Screen shot - "+fieldName);
		}
		return validate;
	}
	
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
	
	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	
}
