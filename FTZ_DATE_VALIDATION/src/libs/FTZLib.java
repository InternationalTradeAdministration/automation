/**
 * MilCorp
 * Mouloud Hamdidouche
 * January, 2019
*/

package libs;

import static GuiLibs.GuiTools.checkElementExists;
import static GuiLibs.GuiTools.clickElementJs;
import static GuiLibs.GuiTools.enterText;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.getElementAttribute;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.highlightElement;
import static GuiLibs.GuiTools.holdMilliSeconds;
import static GuiLibs.GuiTools.enterTextAndClear;
import static GuiLibs.GuiTools.getNiemElementAttribute;
import static GuiLibs.GuiTools.clickElement;
import static GuiLibs.GuiTools.switchToWindow;
import static GuiLibs.GuiTools.holdSeconds;
import static GuiLibs.GuiTools.navigateTo;
import static GuiLibs.GuiTools.replaceGui;
import static GuiLibs.GuiTools.scrollByPixel;
import static GuiLibs.GuiTools.scrollToElement;
import static GuiLibs.GuiTools.selectElementByText;
import static GuiLibs.GuiTools.setBrowserTimeOut;
import static GuiLibs.GuiTools.unHighlightElement;
import static GuiLibs.GuiTools.updateHtmlReport;
import static GuiLibs.GuiTools.updateHtmlReportOverall;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import javax.swing.text.Highlighter.Highlight;

import org.testng.ISuiteListener;

import ReportLibs.HtmlReport;
import bsh.util.GUIConsoleInterface;

public class FTZLib{
	static DateFormat format, format2;
	static Calendar calendar = (Calendar)Calendar.getInstance();
	static Date todayDate;
	static String todayDateStr;
	
	public FTZLib() throws IOException {
	}
	/**
	 * This method login to ADCVD web application
	 * @param url: url for the application
	 * @param user: user
	 * @param password: password
	 * @exception Exception
	 */
	public static boolean loginToFtz(String url, 
									 String user, 
									 String password,
									 String caseId) throws Exception
	{
		
		boolean loginStatus = true;
		navigateTo(url);
		/*JOptionPane.showMessageDialog(null,
			    "Please Do the CAPTCHA manually and click 'OK'");*/
		holdSeconds(1);
		int currentWait = setBrowserTimeOut(2);
		if(checkElementExists(guiMap.get("signOutLink")))
		{
			clickElementJs(guiMap.get("signOutLink"));
			holdMilliSeconds(500);
		}
		clickElementJs(guiMap.get("signInLink"));
		if(checkElementExists(guiMap.get("ftzHome")))
		{
			updateHtmlReport("Login to FTZ App",  "User is able to login", "As expected", 
				"Step", "pass", "Login to FTZ");
			setBrowserTimeOut(currentWait);
		}else
		{
			enterText(guiMap.get("UserName"), user);
			enterText(guiMap.get("Password"), password);
			clickElementJs(guiMap.get("SignInButton"));			
			if(checkElementExists(guiMap.get("ftzHome")))
			{
				updateHtmlReport("Login to FTZ App",  "User is able to login", "As expected", 
					"Step", "pass", "Login to FTZ");
				setBrowserTimeOut(currentWait);
			}else
			{
				loginStatus = false;
				setBrowserTimeOut(currentWait);
				failTestSuite("Login to FTZ App",  "User is able to login", "Not as expected", 
						"Step", "fail", "Login to FTZ");
			}
		}
		//replaceGui(guiMap.get("TopMenu"), "Sign out")
		clickElementJs(guiMap.get("caseSection"));
		enterText(guiMap.get("searchInput"), caseId);
		holdSeconds(1);
		clickElementJs(guiMap.get("editPencil"));
		openCloseHeaders();
		return loginStatus;
	}
	/**
	 * This method login to ADCVD web application
	 * @param map, date info
	 * @return true if date is calculated correctly, false if not
	 * @exception Exception
	 */
	public static boolean validateFtzDate(LinkedHashMap<String, String> map,
										  ArrayList<LinkedHashMap<String, String>> caseType) throws Exception
	{
		
		format = new SimpleDateFormat("yyyy-MM-dd");
		format2 = new SimpleDateFormat("MM/dd/yyyy");
		todayDate = new Date();
		calendar.setTime(todayDate);
		todayDateStr = format2.format(todayDate);
		String htmlCalDate = "dateInput";
		
		ArrayList<LinkedHashMap<String, String>> reqArrayList =
				new ArrayList<LinkedHashMap<String, String>>();
		LinkedHashMap<String, String> req = new LinkedHashMap<String, String>();
		boolean status = true;
		
		String formula = map.get("Formula");
		String requirements = map.get("Requirement");
		String refDateLabel = map.get("Reference_Date_Label").trim();
		String calcDateLabel = map.get("Calculated_Date_Label").trim();
		
		String [] reqsList = formula.split("#");
		for(int i=0; i<reqsList.length;i++)
		{
			req.put("type", reqsList[i].trim().substring(1, reqsList[i].trim().
					indexOf("]")).trim().replace(" ", ""));
			req.put("days", reqsList[i].trim().substring(reqsList[i].trim().
					indexOf("+")+1, reqsList[i].trim().length()).replace(" ", ""));
			reqArrayList.add(new LinkedHashMap<String, String>(req));
			req.clear();
		}
		if (calcDateLabel.equalsIgnoreCase("Preliminary Examiner's Report/Memo Response Due"))
		{
			htmlCalDate = "dateInputExaminer";
		}

		////////////////////////////////////////
		boolean pass = true;
		String expectedDateValue = "", actualDateValue="";
		String caseTypeGroup, typeOfCase, typeOfCaseNumber;
		for (LinkedHashMap<String, String> oneType: caseType)
		{
			boolean found=false;
			expectedDateValue = "";
			actualDateValue = "";
			caseTypeGroup = oneType.get("Case_Type_Group");
			typeOfCase = oneType.get("Type_Of_Case");
			typeOfCaseNumber = oneType.get("Number");
			if (caseTypeGroup.equalsIgnoreCase("Staff Cases"))
			clickElementJs(replaceGui(guiMap.get("caseTypeGroup"), "S"));	
			else 
			clickElementJs(replaceGui(guiMap.get("caseTypeGroup"), "B"));			
			selectElementByText(guiMap.get("selectCaseType"), typeOfCase);
			
			for(LinkedHashMap <String, String> row:reqArrayList)
			{
				List<String> list = Arrays.asList(row.get("type").trim().split(","));
				if (list.contains(typeOfCaseNumber) || list.contains(" "+typeOfCaseNumber))
				{
					found = true;
					calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(row.get("days").trim()));
					expectedDateValue = format.format(calendar.getTime());
					calendar.add(Calendar.DAY_OF_MONTH, -1*Integer.parseInt(row.get("days").trim()));
					break;
				} 
			}
			int currentWait = setBrowserTimeOut(3);
			if(!checkElementExists(replaceGui(guiMap.get(htmlCalDate),calcDateLabel))
					|| !checkElementExists(replaceGui(guiMap.get("dateInput"),refDateLabel))) 
			{
				if (found)
				{
					pass = false;
					updateHtmlReportOverall("Case Type = ("+ typeOfCaseNumber +") "+typeOfCase, "For this case type, both"
							+ " dates " +refDateLabel+ " AND "+calcDateLabel+" should be visible on GUI", "not as expected",
							"VP", "fail", calcDateLabel + "_" + typeOfCaseNumber);
				}else
				{
					pass = pass & compareAndReport(calcDateLabel, actualDateValue, expectedDateValue, typeOfCase, 
							refDateLabel, todayDateStr,typeOfCaseNumber, requirements);
				}
			}
			//f e, nf e
			else if(checkElementExists(replaceGui(guiMap.get(htmlCalDate),calcDateLabel))
							&& checkElementExists(replaceGui(guiMap.get("dateInput"),refDateLabel)))
			{  
				enterText(replaceGui(guiMap.get("dateInput"), refDateLabel),todayDateStr);
				highlightElement(replaceGui(guiMap.get("dateInput"), refDateLabel), "blue");
				scrollToElement(replaceGui(guiMap.get(htmlCalDate), calcDateLabel));
				highlightElement(replaceGui(guiMap.get(htmlCalDate), calcDateLabel), "blue");
				actualDateValue = getElementAttribute(replaceGui(guiMap.get(htmlCalDate),
						calcDateLabel), "value");
				pass = pass & compareAndReport(calcDateLabel, actualDateValue, expectedDateValue, typeOfCase, 
						refDateLabel, todayDateStr,typeOfCaseNumber, requirements);
			}			
			setBrowserTimeOut(currentWait);
		}//for
		return pass;
	}
	

	
	/**
	 * This method used to compare and report dates
	 * @param actualValue: url for the application
	 * @param expectedValue: user
	 * @param caseType: password
	 * @param referenceDate
	 * @param caseTypeNumber
	 * @param reqs
	 * @return pass if dates are matching false if not.
	 * @exception Exception
	 */
	public static boolean compareAndReport( String calcDateLabel,
											String actualValue,
											String expectedValue,
											String caseType,
											String referenceDate,
											String todayDateStr,
											String caseTypeNumber,
											String requirement) throws Exception
	{
		String scShot = actualValue.equals("")?"":calcDateLabel + "_" + caseTypeNumber;
			
		String scenario = "Case Type = ("+caseTypeNumber +") "+ caseType+"<br>" + 
				referenceDate + " = " + todayDateStr;
		if( actualValue.equalsIgnoreCase(expectedValue))
		{
			updateHtmlReport("<abbr title='"+requirement+"'>"+scenario+"</abbr>",
					"Expected date value = "+expectedValue, 
					"Actual Date value = "+actualValue, "VP", "pass", scShot);
			return true;
		}
		else 
		{
			updateHtmlReport("<abbr title='"+requirement+"'>"+scenario+"</abbr>",
					"Expected date value = "+expectedValue, 
					"Actual Date value = "+actualValue, "VP", "fail", scShot);
			return false;
		}
	}
	
	/**
	 * This method used to compare and report dates
	 * @param header: header name
	 * @exception Exception
	 */
	public static void openCloseHeaders() throws Exception
	{
		String[] headers = new String[] {"headingPreDocketingApplication", "headingCommentRebuttalPeriod", 
				"headingPostDocketingApplication", "headingCaseFinalAction"
				};//"headingCaseCorrespondences", "headingFRNotices"
		for(int i=0; i<headers.length; i++)
		{
			clickElementJs(replaceGui(guiMap.get("headingName"), headers[i]));
		}
		
		
	}
	

}
