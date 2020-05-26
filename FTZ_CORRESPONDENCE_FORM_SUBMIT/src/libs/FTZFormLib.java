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
import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import bsh.util.GUIConsoleInterface;

public class FTZFormLib{
	public static String filedDate,
	actualInitiationSignature, calculatedInitiationSignature, petitionOutcome;
	public static int petitionInitiationExtension;
	static DateFormat format;
	static Calendar calendar;
	static String caseType;
	static String investigationId;
	static String orderId;
	
	static String[] ElementsInRange = {"Aluminum","Antimony","Bismuth","Boron","Carbon","Chromium",
			"Cobalt","Copper","Iron","Lead","Magnesium","Manganese","Molybdenum","Nickel",
			"Niobium","Nitrogen","Phosphorus","Selenium","Silicon","Sulfur","Tellurium",
			"Titanium",	"Tungsten",	"Vanadium",	"Zinc",	"Zirconium",	"Other Chemical",
			"Elogation",	"Reduction in Area",	"Hole Expansion"};
	
	public FTZFormLib() throws IOException {
		//super();
		//this.format = new SimpleDateFormat("M/d/yyyy");
		//this.calendar = Calendar.getInstance();
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
									 String password) throws Exception
	{
		boolean loginStatus = true;
		navigateTo(url);
		
		if(!checkElementExists(replaceGui(guiMap.get("TopMenu"), "FTZ")))
		{
			failTestSuite("navigate to Free Trade Zone url", "User is able to navigate", 
				"Not as expected", "Step", "fail", "navigation failed");
			loginStatus = false;
		}else
		{
			int currentWait = setBrowserTimeOut(2);
			if(checkElementExists(replaceGui(guiMap.get("TopMenu"), "Sign out")))
			{
				clickElementJs(replaceGui(guiMap.get("TopMenu"), "Sign out") );
			}
			clickElementJs(guiMap.get("SignInLink")); //replaceGui(guiMap.get("TopMenu"), "Sign out")
			enterText(guiMap.get("UserName"), user);
			enterText(guiMap.get("Password"), password);
			clickElementJs(guiMap.get("SignInButton"));			
			if(checkElementExists(replaceGui(guiMap.get("TopMenu"), "Sign out")))
			{
				setBrowserTimeOut(currentWait);
				updateHtmlReport("Login to FTZ App",  "User is able to login", "As expected", 
					"Step", "pass", "Login to FTZ");
				clickElementJs(replaceGui(guiMap.get("TopMenu"), "Correspondence") );
			}else
			{
				loginStatus = false;
				setBrowserTimeOut(currentWait);
				updateHtmlReport("Login to FTZ App",  "User is able to login", "Not as expected", 
						"Step", "fail", "Login to FTZ");
			}
		}
		return loginStatus;
	}
	/**
	 * This method fills up step 1
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean submitFtzCorrespondence(HashMap<String, String> row) throws Exception
	{
		boolean newForm = true;
		String elementName = "";
		clickElementJs(guiMap.get("CreateNew"));
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(elementName.equalsIgnoreCase("Forms") || elementName.equalsIgnoreCase("Active") )
			{
				continue;
			}else if (elementName.equalsIgnoreCase("Type") || elementName.equalsIgnoreCase("Direction")||
					elementName.equalsIgnoreCase("Rcvd Via") || elementName.equalsIgnoreCase("AddressId"))
			{
				selectElementByText(replaceGui(guiMap.get("selectField"), elementName), entry.getValue());
			}
			else
			{
				enterText(replaceGui(guiMap.get("inputField"), elementName), entry.getValue());
			}
		}
		updateHtmlReportOverall("Fill up the address form",  
				"User is able to fill up the form", "As expected", 
				"Step", "pass", "Fill up the form");
		clickElementJs(guiMap.get("CreateButton"));
		int currentWait = setBrowserTimeOut(3); 
		if(!checkElementExists(guiMap.get("CreateNewFormLabel")))
		{
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new address form",  
					"User is able to submit address form", "As expected", 
				"Step", "pass", "create new address form");
		}else
		{
			newForm = false;
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new address form", 
					"User is able to submit address form", "Not as expected", 
					"Step", "fail", "create new address form");
		}
		return newForm;
	}
}
