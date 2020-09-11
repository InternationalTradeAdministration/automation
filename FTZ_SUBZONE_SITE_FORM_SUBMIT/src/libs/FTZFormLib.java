/**
 * MilCorp
 * Mouloud Hamdidouche
 * January, 2019
*/

package libs;

import static GuiLibs.GuiTools.checkElementExists;
import static GuiLibs.GuiTools.clickElementJs;
import static GuiLibs.GuiTools.enterText;
import static GuiLibs.GuiTools.enterTextAndClear;
import static GuiLibs.GuiTools.enterTextTextArea;
import static GuiLibs.GuiTools.failTestSuite;
import static GuiLibs.GuiTools.guiMap;
import static GuiLibs.GuiTools.holdSeconds;
import static GuiLibs.GuiTools.navigateTo;
import static GuiLibs.GuiTools.scrollToTheBottomOfPage;
import static GuiLibs.GuiTools.replaceGui;
import static GuiLibs.GuiTools.selectElementByText;
import static GuiLibs.GuiTools.setBrowserTimeOut;
import static GuiLibs.GuiTools.updateHtmlReport;
import static GuiLibs.GuiTools.updateHtmlReportOverall;
import static GuiLibs.GuiTools.scrollToElement;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

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
	 * This method login to FTZ web application
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
				clickElementJs(replaceGui(guiMap.get("TopMenu"), "OFIS Section") );
				/*Robot robot = new Robot(); 
				robot.keyPress(KeyEvent.VK_ENTER);*/
		        
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
	 * This method create and search for zone
	 * @return true if zone created and searched, false if not
	 * @exception Exception
	 */
	public static boolean createAndSearchZone() throws Exception
	{
		boolean created = true;
		LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy HHmmss");
        String zNumber = format.format(new Date()).split(" ")[1];
        row.put("Zone_ZoneNumber", zNumber);
        row.put("Zone_StatusId", "Approved");
        row.put("First Name", "fn_"+zNumber);
        row.put("Last Name", "ln_"+zNumber);
        row.put("Email Address", zNumber+"@test.com");
        created = submitFtzZone(row);
        enterText(guiMap.get("searchField"), zNumber );
        holdSeconds(1);
        clickElementJs(guiMap.get("resultSearch"));
        return created;
	}
	
	/**
	 * This method create and search for sub zone
	 * @return true if sub zone created and searched, false if not
	 * @exception Exception
	 */
	public static boolean createAndSearchSubZone() throws Exception
	{
		boolean created = true;
		LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
        DateFormat format = new SimpleDateFormat("MM-dd-yyyy HHmmss");
        String szNumber = format.format(new Date()).split(" ")[1];
        row.put("Subzone Number", szNumber);
        row.put("Status", "Approved");
        created = submitFtzSubZone(row);
       // enterText(guiMap.get("searchField"), szNumber );
        holdSeconds(1);
        clickElementJs(replaceGui(guiMap.get("subZoneTD"), szNumber));
        return created;
	}
	
	/**
	 * This method create new zone
	 * @param row: map of test zone's data
	 * @return true zone created properly, false if not
	 * @exception Exception
	*/
	public static boolean submitFtzZone(HashMap<String, String> row) throws Exception
	{
		boolean newForm = true;
		String elementName = "";
		int currentWait = setBrowserTimeOut(2); 
		if(!checkElementExists(replaceGui(guiMap.get("inputField"), "Zone_ZoneNumber")))
		{
			clickElementJs(replaceGui(guiMap.get("TopMenu"), "Add New Zone") );
		}
		setBrowserTimeOut(currentWait); 
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(elementName.equalsIgnoreCase("Forms") || elementName.equalsIgnoreCase("Active") )
			{
				continue;
			}else if (elementName.equalsIgnoreCase("Zone_StatusId") || elementName.equalsIgnoreCase("Zone_PortOfEntryId")||
					elementName.equalsIgnoreCase("Zone_StateId") || elementName.equalsIgnoreCase("Zone_Grantee_StateId"))
			{
				selectElementByText(replaceGui(guiMap.get("selectField"), elementName), entry.getValue());
			}else if (elementName.equalsIgnoreCase("Email Address"))
			{
				enterText(replaceGui(guiMap.get("inputField"), elementName), entry.getValue(),2);
			}
			else
			{
				enterText(replaceGui(guiMap.get("inputField"), elementName), entry.getValue());
			}
		}
		updateHtmlReportOverall("Fill up the Case form",  "User is able to fill up the form", "As expected", 
				"Step", "pass", "Fill up the form");
		clickElementJs(guiMap.get("CreateButton"));
		currentWait = setBrowserTimeOut(2); 
		if(!checkElementExists(guiMap.get("CreateNewFormLabel")))
		{
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new Zone form",  "User is able to submit a new zone form", "As expected", 
				"Step", "pass", "create new zone form");
		}else
		{
			newForm = false;
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new Zone form",  "User is able to submit a new zone form", "Not as expected", 
					"Step", "fail", "create new zone form");
		}
		return newForm;
	}
	

	/**
	 * This method creates subzone
	 * @param row: map of test case's data
	 * @return if subzone is created, false if not
	 * @exception Exception
	*/
	public static boolean submitFtzSubZone(HashMap<String, String> row) throws Exception
	{
		boolean newForm = true;
		String elementName = "";
		int currentWait = setBrowserTimeOut(1); 
		if(!checkElementExists(replaceGui(guiMap.get("inputField"), "Subzone Number")))
		{
			clickElementJs(guiMap.get("createSubzone") );
		}
		setBrowserTimeOut(currentWait); 
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(elementName.equalsIgnoreCase("Forms") || elementName.equalsIgnoreCase("Active") )
			{
				continue;
			}else if (elementName.equalsIgnoreCase("Status"))
			{
				selectElementByText(replaceGui(guiMap.get("selectField"), elementName), entry.getValue());
			}else if (elementName.equalsIgnoreCase("Include Acres in Activation Limit?"))
			{
				if (entry.getValue().equalsIgnoreCase("Yes"))
				clickElementJs(guiMap.get("SubZoneActLimit"));
			}else if (elementName.equalsIgnoreCase("Notes")) 
			{
				enterTextTextArea(replaceGui(guiMap.get("NoteField"), elementName), entry.getValue());
			}			
			else
			{
				enterTextAndClear(replaceGui(guiMap.get("inputField"), elementName), entry.getValue());
			}
		}
		/*updateHtmlReportOverall("Fill up the subzone form",  "User is able to fill up the form", "As expected", 
				"Step", "pass", "Fill up the form - "+ row.get("Forms"));*/
		clickElementJs(guiMap.get("CreateButton"));
		currentWait = setBrowserTimeOut(3); 
		if(checkElementExists(guiMap.get("subZoneCreated")))
		{
			scrollToTheBottomOfPage();
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new subzone form",  "User is able to submit a subzone form", "As expected", 
				"Step", "pass", "create subzone form - "+ row.get("Forms"));
		}else
		{
			newForm = false;
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new address form",  "User is able to submit a Case form", "Not as expected", 
					"Step", "fail", "create new Case form - "+ row.get("Forms"));
		}
		return newForm;
	}
	
	
	
	
	/**
	 * This method creates sub-zone site
	 * @param row: map of test case's data
	 * @return if sub-zone site is created, false if not
	 * @exception Exception
	*/
	public static boolean submitFtzSubZoneSite(HashMap<String, String> row) throws Exception
	{
		boolean newForm = true;
		String elementName = "";
		int currentWait = setBrowserTimeOut(1); 
		if(!checkElementExists(replaceGui(guiMap.get("inputField"), "Site Name")))
		{
			clickElementJs(guiMap.get("createSite") );
		}
		setBrowserTimeOut(currentWait); 
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(elementName.equalsIgnoreCase("Forms") || elementName.equalsIgnoreCase("Active") )
			{
				continue;
			}else if (elementName.equalsIgnoreCase("Status") || elementName.equalsIgnoreCase("Type")
					|| elementName.equalsIgnoreCase("State"))
			{
				selectElementByText(replaceGui(guiMap.get("selectField"), elementName), entry.getValue());
			}else if (elementName.equalsIgnoreCase("Sunset Type"))
			{
				clickElementJs(replaceGui(guiMap.get("sunsetType"), entry.getValue()));
			}else if (elementName.equalsIgnoreCase("Notes")) 
			{
				enterTextTextArea(guiMap.get("NoteField"), entry.getValue());
			}			
			else
			{
				enterTextAndClear(replaceGui(guiMap.get("inputField"), elementName), entry.getValue());
			}
		}
		/*updateHtmlReportOverall("Fill up the subzone form",  "User is able to fill up the form", "As expected", 
				"Step", "pass", "Fill up the form - "+ row.get("Forms"));*/
		clickElementJs(guiMap.get("CreateButton"));
		currentWait = setBrowserTimeOut(3); 
		if(checkElementExists(guiMap.get("subZoneCreated")))
		{
			scrollToTheBottomOfPage();
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new subzone form",  "User is able to submit a subzone form", "As expected", 
				"Step", "pass", "create subzone form - "+ row.get("Forms"));
		}else
		{
			newForm = false;
			setBrowserTimeOut(currentWait);
			updateHtmlReport("Submit new address form",  "User is able to submit a Case form", "Not as expected", 
					"Step", "fail", "create new Case form - "+ row.get("Forms"));
		}
		return newForm;
	}
	
	
	
	
}
