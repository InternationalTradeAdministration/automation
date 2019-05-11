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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ReportLibs.HtmlReport;
import bsh.ParseException;

public class BisFormLib{
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
	
	public BisFormLib() throws IOException {
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
	public static boolean loginToBis(String url, 
									 String user, 
									 String password) throws Exception
	{
		boolean loginStatus = true;
		navigateTo(url);
		if(! checkElementExists(guiMap.get("HomePage")))
		{
			failTestSuite("Login to BIS 232 App", "User is able to login", 
				"Not as expected", "Step", "fail", "Login failed");
			loginStatus = false;
		}else
		{
			highlightElement(guiMap.get("HomePage"), "green");
			holdSeconds(2);
			updateHtmlReport("Login to BIS232 App",  "User able to login", "As expected", 
					"Step", "pass", "Login to BIS 232");
			clickElementJs(guiMap.get("CreateNewExclusionRequest"));
		}
		return loginStatus;
	}
	/**
	 * This method fills up step 1
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static void fillUpStepOne(HashMap<String, String> row) throws Exception
	{
		String elementName = "";
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 1") );
		holdSeconds(2);
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(elementName.equalsIgnoreCase("Scenarios") || elementName.equalsIgnoreCase("Active") )
			{
				continue;
			}else if (elementName.equalsIgnoreCase("Requesting Organization State") || 
					  elementName.equalsIgnoreCase("Importer State")||elementName.equalsIgnoreCase("Metal Type")
					  ||elementName.equalsIgnoreCase("Metal Class"))
			{
				selectElementByText(replaceGui(guiMap.get("StepOneElementSelect"), elementName), entry.getValue());
			}
			else
			{
				enterText(replaceGui(guiMap.get("StepOneElement"), elementName), entry.getValue());
			}
		}
		updateHtmlReportOverall("Fillup the form of step 1",  "User fill up the form of step 1", "As expected", 
				"Step", "pass", "Fill up the form step 1");
	}
	
	/**
	 * This method fills up step 2
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static void fillUpStepTwo(HashMap<String, String> row) throws Exception
	{
		String elementName = "";
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 2") );
		holdSeconds(2);
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(elementName.equalsIgnoreCase("Ownership Activity")) clickElementJs(guiMap.get("OwnershipAnswer_true"));
			if(elementName.equalsIgnoreCase("Non US Producer Name")) clickElementJs(guiMap.get("BehalfOf_true"));
			if(elementName.equalsIgnoreCase("Scenarios") || elementName.equalsIgnoreCase("Active") )
			{
				continue;
			}else if (elementName.equalsIgnoreCase("Ownership Activity") || 
					  elementName.equalsIgnoreCase("Ownership Headquarters Country")||
					  elementName.equalsIgnoreCase("Exclusion Explanation")
					  ||elementName.equalsIgnoreCase("Non US Producer Headquarters Country"))
			{
				selectElementByText(replaceGui(guiMap.get("StepOneElementSelect"), elementName), entry.getValue());
			}
			else
			{
				enterText(replaceGui(guiMap.get("StepOneElement"), elementName), entry.getValue());
			}
		}
		updateHtmlReportOverall("Fillup the form of step 2",  "User fill up the form of step 2", "As expected", 
				"Step", "pass", "Fill up the form step 2");
	}
	
	/**
	 * This method fills up step 3 form
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static void fillUpStepThree(HashMap<String, String> row) throws Exception
	{
		String minVal="", maxVal="", elementName = "";
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 3"));
		holdSeconds(2);
		enterText(guiMap.get("ProductDescription"), row.get("Product Description"));
		scrollToElement(replaceGui(guiMap.get("ElementMinValue"), "Aluminum"));
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(!elementName.equalsIgnoreCase("Scenarios") && !elementName.equalsIgnoreCase("Active")
					&& !elementName.equalsIgnoreCase("Product Description"))
			{
				minVal = entry.getValue().substring(0,entry.getValue().indexOf("|"));
				maxVal = entry.getValue().substring(entry.getValue().indexOf("|")+1,entry.getValue().length());
				enterText(replaceGui(guiMap.get("ElementMinValue"), elementName), minVal);
				enterText(replaceGui(guiMap.get("ElementMaxValue"), elementName), maxVal);
			}
		}
		updateHtmlReportOverall("Fillup the form of step 3",  "User fill up the form of step 3", "As expected", 
				"Step", "pass", "Fill up the form step 3");
	}
	
	/**
	 * This method fills up step 4 form
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static void fillUpStepFour(HashMap<String, String> row) throws Exception
	{
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 4"));
		holdSeconds(2);
		enterText(guiMap.get("ApplicationSuitability"), row.get("Application Suitability"));
		scrollToElement(guiMap.get("OriginCountry"));
		selectElementByText(guiMap.get("OriginCountry"), row.get("Origin Country"));
		selectElementByText(guiMap.get("ExportCountry"), row.get("Export Country"));
		enterText(guiMap.get("CBPDistinguishComments"), row.get("CBP Distinguish Comments"));
		updateHtmlReportOverall("Fillup the form of step 4",  "User fill up the form of step 4", "As expected", 
				"Step", "pass", "Fill up the form step 4");
	}
	
	/**
	 * This method fills up step 5
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static void fillUpStepFive(HashMap<String, String> row) throws Exception
	{
		String elementName = "";
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 5") );
		holdSeconds(2);
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(elementName.equalsIgnoreCase("Scenarios") || elementName.equalsIgnoreCase("Active") )
			{
				continue;
			}
			else
			{
				enterText(replaceGui(guiMap.get("StepFiveElement"), elementName), entry.getValue());
			}
		}
		updateHtmlReportOverall("Fillup the form of step 5",  "User fill up the form of step 5", "As expected", 
				"Step", "pass", "Fill up the form step 5");
	}
	/**
	 * This method Validates Step 1
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean ValidateStepOne(HashMap<String, String> row) throws Exception
	{
		String  elementName = "", errorMsg="", htmlToValidate="";
		String mType = row.get("Metal Type");
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 1") );
		holdSeconds(2);
		boolean matches = true;
		//Validate HTSUS Here
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value :  " + entry.getValue());
			elementName = entry.getKey().trim();
			String elementValue = entry.getValue();
			if(!elementName.equalsIgnoreCase("Scenarios") && !elementName.equalsIgnoreCase("Active") &&
					!elementName.equalsIgnoreCase("Metal Type") && !elementName.equalsIgnoreCase("Metal Class")	)
			{
				if (elementName.equalsIgnoreCase("Organization Legal Name"))
				{
					scrollToElement(replaceGui(guiMap.get("StepOneElement"), "Requesting Organization Zipcode"));
				}
				else if (elementName.equalsIgnoreCase("Importer Legal Name"))
				{
					scrollToElement(replaceGui(guiMap.get("StepOneElement"), "Importer Zipcode"));
				}
				if (elementName.equalsIgnoreCase("HTSUS Code") && !elementValue.trim().equals(""))
				{
					errorMsg = getElementAttribute(replaceGui(guiMap.get("StepOneElementMessage"), elementName, "2"), "text");	
					htmlToValidate = "StepOneElementDiv";
				}
				else if (elementName.equalsIgnoreCase("Requesting Organization State") || elementName.equalsIgnoreCase("Importer State"))
				{
					errorMsg = getElementAttribute(replaceGui(guiMap.get("StepOneElementSelectMessage"), elementName), "text");
					htmlToValidate ="StepOneElementSelectDiv";
				}
				else
				{
					errorMsg = getElementAttribute(replaceGui(guiMap.get("StepOneElementMessage"), elementName, "1"), "text");	
					htmlToValidate = "StepOneElementDiv";
				}
				switch(elementName)
				{
					case "HTSUS Code":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The HTSUSCode field is required.", errorMsg);
						}
						else if(
								(mType.equals("Steel") && ! Arrays.toString(Materials.materialSteels).contains(elementValue))
							||  (mType.equals("Aluminum") && (! Arrays.toString(Materials.materialAluminum).contains(elementValue)))
								)
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"HTSUSCode is not valid", errorMsg);
						}else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
						break;
					}
					case "Organization Legal Name": case "Importer Legal Name":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The Full Organization Legal Name field is required.", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
						break;
					}
					case "Requesting Organization Street Address": case "Importer Street Address":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The Street Address field is required.", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
						break;
					}
					case "Requesting Organization City": case "Importer City":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The City field is required.", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
						break;
					}
					case "Requesting Organization State":  case "Importer State":
					{
						if(elementValue.trim().equals("Please Select"))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The State field is required.", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
						break;
					}
					case "Requesting Organization Zipcode":  case "Importer Zipcode":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The Zip Code field is required.", errorMsg);
						}
						else
						{
							if(!elementValue.matches("^[0-9]{5}$"))
							{
								matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"Zip Code can be 5 characters only and contain only digits", errorMsg);
							}
							else
							{
								matches = matches & validateElementAndReport(elementName, htmlToValidate, 
										"", errorMsg);
							}
						}
						break;
					}
					case "Requesting Organization POC Name": case "Importer POC Name":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The Point of Contact Name field is required.", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
						break;
					}
					case "Requesting Organization Phone Number":  case "Importer Phone Number":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The Phone Number field is required.", errorMsg);
						}
						else
						{
							if(!elementValue.matches("^[0-9]*$"))
							{
								matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The Phone Number field is not a valid phone number.", errorMsg);
							}
							else
							{
								matches = matches & validateElementAndReport(elementName, htmlToValidate, 
										"", errorMsg);
							}
						}
						break;
					}
					case "Requesting Organization Email Address": case "Importer Email Address":
					{
						if(elementValue.trim().equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The E-mail Address field is required.", errorMsg);
						}
						else
						{
							if(!elementValue.matches("^(.+)@(.+)$"))
							{
								matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"The E-mail Address field is not a valid e-mail address.", errorMsg);
							}
							else
							{
								matches = matches & validateElementAndReport(elementName, htmlToValidate, 
										"", errorMsg);
							}
						}
						break;
					}
					case "Requesting Organization Website Address": case "Importer Website Address":
					{
						if(!elementValue.contains("http://") && !elementValue.contains("https://") &&
								!elementValue.contains("ftp://") && !elementValue.equals(""))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"The Web site Address field is not a valid fully-qualified http, https, or ftp URL.", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
						break;
					}
					default:
					{
						break;
					}
				}
			}
		}
		return matches;
	}
	
	/**
	 * This method Validates Step 3
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean ValidateStepTwo(HashMap<String, String> row) throws Exception
	{
		String  elementName = "", errorMsg="", htmlToValidate="";
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 2") );
		holdSeconds(2);
		boolean matches = true;
		//Validate HTSUS Here
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			String elementValue = entry.getValue();
			if(!elementName.equalsIgnoreCase("Scenarios") && !elementName.equalsIgnoreCase("Active"))
			{
				if (elementName.equalsIgnoreCase("Ownership Activity"))
				{
					scrollToElement(replaceGui(guiMap.get("StepTwoElement"), "Ownership Organization Name"));
				}
				else if (elementName.equalsIgnoreCase("Total Requested Annual Exclusion Quantity"))
				{
					scrollToElement(replaceGui(guiMap.get("StepTwoElement"), "Total Requested Annual Exclusion Quantity"));
				}else if (elementName.equalsIgnoreCase("Percentage Not Available"))
				{
					scrollToElement(replaceGui(guiMap.get("StepTwoElement"), "Manufacture Estimate"));
				}
				else if (elementName.equalsIgnoreCase("Non US Producer Name"))
				{
					scrollToElement(replaceGui(guiMap.get("StepTwoElement"), "Non US Producer Name"));
				}
				else if (elementName.equalsIgnoreCase("Authorized Representative Phone Number"))
				{
					scrollToElement(replaceGui(guiMap.get("StepTwoElement"), "Authorized Representative Website Address"));
				}
				if (elementName.equalsIgnoreCase("Ownership Activity") || 
						  elementName.equalsIgnoreCase("Ownership Headquarters Country")||
						  elementName.equalsIgnoreCase("Exclusion Explanation")
						  ||elementName.equalsIgnoreCase("Non US Producer Headquarters Country"))
				{
					errorMsg = getElementAttribute(replaceGui(guiMap.get("StepTwoElementSelectMessage"), elementName), "text");
					htmlToValidate ="StepTwoElementSelectDiv";
				}
				else
				{
					errorMsg = getElementAttribute(replaceGui(guiMap.get("StepTwoElementMessage"), elementName), "text");	
					htmlToValidate = "StepTwoElementDiv";
				}
				switch(elementName)
				{
				case "Authorized Representative Phone Number":  
				{
					if(!elementValue.matches("[0-9]*"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
							"The Phone Number field is not a valid phone number.", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
						
					break;
				}
				case "Authorized Representative Email Address": 
				{
					if(!elementValue.matches("^(.+)@(.+)$"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
							"The E-Mail Address field is not a valid e-mail address.", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Authorized Representative Website Address": 
				{
					if(!elementValue.contains("http://") && !elementValue.contains("https://") &&
							!elementValue.contains("ftp://") && !elementValue.equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
							"The Website Address field is not a valid fully-qualified http, https, or ftp URL.", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Ownership Activity":
				{
					if(elementValue.trim().equals("Please Select"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Activity is required if Ownership is selected", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Ownership Organization Name":
				{
					if(elementValue.trim().equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Organization is required if Ownership is selected", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Ownership Headquarters Country":
				{
					if(elementValue.trim().equals("Please Select"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Headquarters Country is required if Ownership is selected", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Total Requested Annual Exclusion Quantity":  
				{
					if(elementValue.trim().equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"The TotalRequestedAnnualExclusionQuantity field is required.", errorMsg);
					}
					else
					{
						if(!elementValue.matches("^[0-9]*$"))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Invalid Value", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
					}
					break;
				}
				case "Total Requested Average Annual Consumption":  
				{
					if(elementValue.trim().equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"The AvgAnnualConsumption field is required.", errorMsg);
					}
					else
					{
						if(!elementValue.matches("^[0-9]*$"))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Invalid Value", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
					}
					break;
				}
				case "Exclusion Explanation":
				{
					if(elementValue.trim().equals("Please Select"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Please select a value from dropdown", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Percentage Not Available":
				{
					if(!elementValue.matches("[0-9]+|[0-9]+\\.?[0-9]+") && !elementValue.equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Invalid Value", errorMsg);
					}
					else if(elementValue.matches("[0-9]+|[0-9]+\\.?[0-9]+") 
							&& (Float.parseFloat(elementValue)<0 
							|| Float.parseFloat(elementValue)>100))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Allowed range (0-100)", errorMsg);
					}else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				 case "Delivery Estimate": 
				{
					if(!elementValue.matches("[0-9]*"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
							"Invalid Value", errorMsg);
					}
					else if(elementValue.equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"The DeliveryEstimate field is required.", errorMsg);
					}else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				 case "Manufacture Estimate": 
				{
					if(!elementValue.matches("[0-9]*"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
							"Invalid Value", errorMsg);
					}
					else if(elementValue.equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"The ManufactureEstimate field is required.", errorMsg);
					}else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				 case "Shipment Days Estimate": 
				{
					if(!elementValue.matches("[0-9]*"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
							"Invalid Value", errorMsg);
					}
					else if(elementValue.equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"The ShipmentDaysEstimate field is required.", errorMsg);
					}else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Shipment Quantity Estimate":  
				{
					if(elementValue.trim().equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"The ShipmentQtyEstimate field is required.", errorMsg);
					}
					else
					{
						if(!elementValue.matches("^[0-9]*$"))
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Invalid Value", errorMsg);
						}
						else
						{
							matches = matches & validateElementAndReport(elementName, htmlToValidate, 
									"", errorMsg);
						}
					}
					break;
				}
				case "Non US Producer Name":
				{
					if(elementValue.trim().equals(""))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Producer Name is required if On-Behalf field is selected", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				case "Non US Producer Headquarters Country":
				{
					if(elementValue.trim().equals("Please Select"))
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"Headquarter Country is required if On-Behalf Field is selected", errorMsg);
					}
					else
					{
						matches = matches & validateElementAndReport(elementName, htmlToValidate, 
								"", errorMsg);
					}
					break;
				}
				default:
				{
					break;
				}
				}
			}
		}
		return matches;
	}
	
	/**
	 * This method Validates Step 3
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean ValidateStepThree(HashMap<String, String> row) throws Exception
	{
		boolean matches = true;
		String minVal="", maxVal="", elementName = "";
		String minMessage, maxMessage;
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 3"));
		holdSeconds(2);
		int movePixel = 1;
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 3"));
		holdSeconds(1);
		scrollToElement(guiMap.get("ProductDescription"));
		String descMessage = getElementAttribute(guiMap.get("ProductDescriptionMessage"), "text");
		if (row.get("Product Description").trim().equals("") )
		{
			matches = matches & validateElementAndReport("Product Description", "ProductDescription", 
					"The Description field is required.",descMessage);
		}
		else
		{
			matches = matches & validateElementAndReport("Product Description", "ProductDescription", 
					"",descMessage);
		}
		scrollToElement(replaceGui(guiMap.get("ElementMinValue"), "Aluminum"));
		int currentWait = setBrowserTimeOut(2);
		for (HashMap.Entry<String, String> entry : row.entrySet()) 
		{
			movePixel++;
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
			elementName = entry.getKey().trim();
			if(!elementName.equalsIgnoreCase("Scenarios") && !elementName.equalsIgnoreCase("Active") 
					&& !elementName.equalsIgnoreCase("Product Description"))
			{
				if (elementName.equalsIgnoreCase("Thickness") )
				{
					scrollToElement(replaceGui(guiMap.get("ElementMinValue"), "Thickness"));
					movePixel=1;
				}else if (elementName.equalsIgnoreCase("Tensile Strength"))
				{
					scrollToElement(replaceGui(guiMap.get("ElementMinValue"), "Tensile Strength"));
					movePixel=1;
				}else if (elementName.equalsIgnoreCase("Tensile Strength"))
				{
					scrollToElement(replaceGui(guiMap.get("ElementMinValue"), "Tensile Strength"));
					movePixel=1;
				}else if (elementName.equalsIgnoreCase("Elogation"))
				{
					scrollToElement(replaceGui(guiMap.get("ElementMinValue"), "Elogation"));
					movePixel=1;
				}
				else if (elementName.equalsIgnoreCase("Epstein"))
				{
					scrollToElement(replaceGui(guiMap.get("ElementMinValue"), "Epstein"));
					movePixel=1;
				}else
				{
					if (movePixel % 5 ==0) scrollByPixel(215);
				}
				minVal = entry.getValue().substring(0,entry.getValue().indexOf("|"));
				maxVal = entry.getValue().substring(entry.getValue().indexOf("|")+1,entry.getValue().length());
				minMessage = getElementAttribute(replaceGui(guiMap.get("ElementMinValueMessage"), elementName), "text");
				maxMessage = getElementAttribute(replaceGui(guiMap.get("ElementMaxValueMessage"), elementName), "text");
				/////minimum
				//minimum is not a number
				String htmlToValidate = "ElementMinValueMessageTd";
				if(!minVal.matches("[0-9]+|[0-9]+\\.?[0-9]+"))
				{
					if (minVal.equals(""))
					{
						matches = matches &  validateMinMaxElementAndReport(elementName, htmlToValidate, "The Minimum field is required.", 
								minMessage, minVal, "Minimum");
					}
					else 
					{
						matches = matches & validateMinMaxElementAndReport(elementName, htmlToValidate, "Invalid Value", 
								minMessage, minVal, "Minimum");
					}
				}
				else //minimum is a number
				{ 
					if(Arrays.asList(ElementsInRange).contains(elementName)&&(Float.parseFloat(minVal)<0 
							|| Float.parseFloat(minVal)>100) )// min out of range
					{
						matches = matches & validateMinMaxElementAndReport(elementName, htmlToValidate, "Allowed range (0-100)", 
								minMessage, minVal, "Minimum");
						
					}
					else // min in range range
					{
						matches = matches & validateMinMaxElementAndReport(elementName, htmlToValidate, "", 
								minMessage, minVal, "Minimum");
					}
				}
				htmlToValidate = "ElementMaxValueMessageTd";
				/////maximum is not a number
				if(!maxVal.matches("[0-9]+|[0-9]+\\.?[0-9]+"))
				{
					if (maxVal.equals(""))
					{
						matches = matches & validateMinMaxElementAndReport(elementName, htmlToValidate, 
								"The Maximum field is required.", maxMessage, maxVal, "Maximum");
					}
					else 
					{
						matches = matches & validateMinMaxElementAndReport(elementName, htmlToValidate, "Invalid Value", 
								maxMessage, maxVal, "Maximum");
					}
				}
				else
				{ // maximum is a number
					if(Arrays.asList(ElementsInRange).contains(elementName)&&(Float.parseFloat(maxVal)<0 
							|| Float.parseFloat(maxVal)>100) )// min out of range
					{
						matches = matches & validateMinMaxElementAndReport(elementName, htmlToValidate, "Allowed range (0-100)", 
								maxMessage, maxVal, "Maximum");
						
					}
					else // min in range
					{
						matches = matches & validateMinMaxElementAndReport(elementName, htmlToValidate, "", 
								maxMessage, maxVal, "Maximum");
					}
				}
			}
		}
		setBrowserTimeOut(currentWait);
		return matches;
	}
	/**
	 * This method Validates Step 5
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean ValidateStepFour(HashMap<String, String> row) throws Exception
	{
		boolean matches = true;
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 4") );
		holdSeconds(2);
		String errorMsg, elementValue;
		scrollToElement(guiMap.get("ApplicationSuitability"));
		elementValue = row.get("Application Suitability");
		errorMsg = getElementAttribute(guiMap.get("ApplicationSuitabilityMessage"), "text");
		if(elementValue.trim().equals(""))
		{
			matches = matches & validateElementAndReport("Application Suitability", "ApplicationSuitability", 
					"The ApplicationSuitability field is required.", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Application Suitability", "ApplicationSuitability", 
					"", errorMsg);
		}
		scrollToElement(guiMap.get("OriginCountry"));
		String originCountry = row.get("Origin Country");
		String exportCountry = row.get("Export Country");
		errorMsg = getElementAttribute(guiMap.get("OriginExportCountryMessage"), "text");
		if(originCountry.equals("Please Select") || exportCountry.equals("Please Select"))
		{
			matches = matches & validateElementAndReport("Application Suitability", "OriginExportCountryTable", 
					"At least one country should be provided.", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Application Suitability", "OriginExportCountryTable", 
					"", errorMsg);
		}
		scrollToElement(guiMap.get("CBPDistinguishComments"));
		elementValue = row.get("CBP Distinguish Comments");
		errorMsg = getElementAttribute(guiMap.get("CBPDistinguishCommentsMessage"), "text");
		if(elementValue.trim().equals(""))
		{
			matches = matches & validateElementAndReport("CBP Distinguish Comments", "CBPDistinguishComments", 
					"The Provide a detailed explanation as to how U.S. Customs and Border Protection (CBP) will "
					+ "be able to reasonably distinguish the product subject to the Exclusion Request at time of entry, without adding "
					+ "undue burden to their current entry system and procedures. field is required.", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Application Suitability", "CBPDistinguishComments", 
					"", errorMsg);
		}
		return matches;
	}
	
	/**
	 * This method Validates Step 5
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean ValidateStepFive(HashMap<String, String> row) throws Exception
	{
		boolean matches = true;
		holdSeconds(1);
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 5") );
		holdSeconds(2);
		String errorMsg, elementValue;
		scrollToElement(replaceGui(guiMap.get("StepFiveElement"),
				"Submission Certification Authorizing Official Phone Number"));
		elementValue = row.get("Submission Certification Authorizing Official Company Name");
		errorMsg = getElementAttribute(replaceGui(guiMap.get("StepFiveElementMessage"),
				"Submission Certification Authorizing Official Company Name"), "text");
		if(elementValue.trim().equals(""))
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Company Name", 
					"StepFiveElementDiv", "The Company Name field is required.", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Company Name", 
					"StepFiveElementDiv", "", errorMsg);
		}
		elementValue = row.get("Submission Certification Authorizing Official Name");
		errorMsg = getElementAttribute(replaceGui(guiMap.get("StepFiveElementMessage"),
				"Submission Certification Authorizing Official Name"), "text");
		if(elementValue.trim().equals(""))
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Name", 
					"StepFiveElementDiv", "The Name of Authorizing Official field is required.", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Name", 
					"StepFiveElementDiv", "", errorMsg);
		}
		elementValue = row.get("Submission Certification Authorizing Official Title");
		errorMsg = getElementAttribute(replaceGui(guiMap.get("StepFiveElementMessage"),
				"Submission Certification Authorizing Official Title"), "text");
		if(elementValue.trim().equals(""))
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Title", 
					"StepFiveElementDiv", "The Title of Authorizing Official field is required.", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Title", 
					"StepFiveElementDiv", "", errorMsg);
		}
		elementValue = row.get("Submission Certification Authorizing Official Phone Number");
		errorMsg = getElementAttribute(replaceGui(guiMap.get("StepFiveElementMessage"),
				"Submission Certification Authorizing Official Phone Number"), "text");
		
		if(elementValue.equals(""))
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Phone Number", 
					"StepFiveElementDiv", "The Phone Number field is required.", errorMsg);
		}
		else if(!elementValue.matches("[0-9]+"))
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Phone Number", 
					"StepFiveElementDiv", "The Phone Number field is not a valid phone number.", errorMsg);
		}else
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Phone Number", 
					"StepFiveElementDiv", "", errorMsg);
		}
		elementValue = row.get("Submission Certification Authorizing Official Email");
		errorMsg = getElementAttribute(replaceGui(guiMap.get("StepFiveElementMessage"),
				"Submission Certification Authorizing Official Email"), "text");
		if(elementValue.matches(""))
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Email", 
					"StepFiveElementDiv", "The Email of Authorizing Official field is required.", errorMsg);
		}
		else if(!elementValue.matches("^(.+)@(.+)$"))
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Email", 
					"StepFiveElementDiv", "The Email of Authorizing Official field is not a valid e-mail address.", errorMsg);
		}else
		{
			matches = matches & validateElementAndReport("Submission Certification Authorizing Official Email", 
					"StepFiveElementDiv", "", errorMsg);
		}
		elementValue = row.get("Submission Certification POC Email");
		errorMsg = getElementAttribute(replaceGui(guiMap.get("StepFiveElementMessage"),
				"Submission Certification POC Email"), "text");
		if(elementValue.matches("^(.+)@(.+)$") || elementValue.equals(""))
		{
			matches = matches & validateElementAndReport("Submission Certification POC Email", 
					"StepFiveElementDiv", "", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Submission Certification POC Email", 
					"StepFiveElementDiv", "The E-mail Address field is not a valid e-mail address.", errorMsg);
		}
		elementValue = row.get("Submission Certification POC Phone Number");
		errorMsg = getElementAttribute(replaceGui(guiMap.get("StepFiveElementMessage"),
				"Submission Certification POC Phone Number"), "text");
		
		if(!elementValue.matches("[0-9]*"))
		{
			matches = matches & validateElementAndReport("Submission Certification POC Phone Number", 
					"StepFiveElementDiv", "The Phone Number field is not a valid phone number.", errorMsg);
		}
		else
		{
			matches = matches & validateElementAndReport("Submission Certification POC Phone Number", 
					"StepFiveElementDiv", "", errorMsg);
		}
		return matches;
	}
	
	/**
	 * This method submit the form after all steps are 
	 * filled 
	 * @exception Exception
	*/
	public static void submitBisForm() throws Exception
	{
		clickElementJs(replaceGui(guiMap.get("tabName"), "Step 5"));
		holdSeconds(2);
		scrollToElement(guiMap.get("submitForm"));
		clickElementJs(guiMap.get("submitForm"));
	}
	
	/**
	 * This method validates and reports min/max values
	 * @param elementName: element name
	 * @param htmlElment: HTML name on GUI
	 * @param expectedMsg: expected message
	 * @param actualMsg: actual message
	 * @return true if the expected value is as actual, false if not
	 * @exception Exception
	*/
	public static boolean validateElementAndReport(String elementName,
													String htmlElment,
													String expectedMsg,
													String actualMsg) throws Exception
	{
		String displayedMessage = expectedMsg.equals("")? "No error message":expectedMsg;
		boolean match = true;
		if(expectedMsg.equalsIgnoreCase(actualMsg))
		{
			highlightElement(replaceGui(guiMap.get(htmlElment), elementName), "green");
			updateHtmlReport("validate ["+elementName+"]",  
			"'"+displayedMessage+"' should display", "As expected", 
			"VP", "pass", elementName);
			unHighlightElement(replaceGui(guiMap.get(htmlElment), elementName));
		}
		else
		{
			highlightElement(replaceGui(guiMap.get(htmlElment), elementName), "red");
			updateHtmlReport("validate ["+elementName+"]",  
			"'"+displayedMessage+"' should display", "Not as expected", 
			"VP", "fail", elementName);
			unHighlightElement(replaceGui(guiMap.get(htmlElment), elementName));
			match = false;
		}
		return match;
	}
	/**
	 * This method validates and reports min/max values
	 * @param elementName: element name
	 * @param htmlElment: HTML name on GUI
	 * @param expectedMsg: expected message
	 * @param actualMsg: actual message
	 * @param minMaxVal element's value
	 * @param MinOrMax min or max
	 * @return  true if the expected value is as actual, false if not
	 * @exception Exception
	*/
	public static boolean validateMinMaxElementAndReport(String elementName,
														 String htmlElment,
														 String expectedMsg,
														 String actualMsg,
														 String minMaxVal,
														 String MinOrMax) throws Exception
	{
		String displayedMessage = expectedMsg.equals("")? "No error message":expectedMsg;
		boolean match = true;
		if(expectedMsg.equalsIgnoreCase(actualMsg))
		{
			highlightElement(replaceGui(guiMap.get(htmlElment), elementName), "green");
			updateHtmlReport("validate ["+elementName+ " - " + MinOrMax +" = "+minMaxVal+"]",  
					"'"+displayedMessage+"' should display", "As expected", 
					"VP", "pass", elementName+ " - " + MinOrMax);
			unHighlightElement(replaceGui(guiMap.get(htmlElment), elementName));
		}
		else
		{
			highlightElement(replaceGui(guiMap.get(htmlElment), elementName), "red");
			updateHtmlReport("validate ["+elementName+ " - " + MinOrMax +" = "+minMaxVal+"]",  
					"'"+displayedMessage+"' should display", "Not as expected", 
					"VP", "fail", elementName+ " - " + MinOrMax);
			unHighlightElement(replaceGui(guiMap.get(htmlElment), elementName));
			match = false;
		}
		return match;
	}
	/**
	 * This method reads number from the screen
	 * @param strNumber: number is string format
	 * @return number in integer format
	 * @exception Exception
	*/
	public static int readNumberFromScreen(String strNumber)
	{
		if (strNumber.equals("") || strNumber.equals("0"))
		{
			return 0;
		}
		else
		{
			return Integer.parseInt(strNumber);
		}
	}
	
	
	/**
	 * This method reads number from the screen
	 * @param strNumber: number is string format
	 * @return number in integer format
	 * @throws IOException 
	 * @exception Exception
	*/
	public static boolean ValidateConditions(JSONObject jObj, 
											 String productType, 
											 String conditions) throws IOException
	{
		boolean ovralResult=true;
		String conditionDetails;
		if (conditions.equals("")) return true;
		String[] listConditions = conditions.split(",");
		for(int i=0; i<listConditions.length; i++)
		{ 
			String condition = listConditions[i];
			if (productType.equalsIgnoreCase("Steel"))
			{
				
				
				
				
				switch(condition.toLowerCase())
				{
				case "condition 1":
				{
					
					break;
				}
				case "condition 2":
				{
					conditionDetails ="Stainless ...then the maximum percentage of carbon must be 1.2 percent or "
							+ "less and the minimum percentage of chromium must be 10.5 percent or more.";
					String carbonMax = getProdValue(jObj, "ChemicalComposition", "Carbon", "Maximum");
					String chromiumMin = getProdValue(jObj, "ChemicalComposition", "Chromium", "Minimum");
					if (Float.parseFloat(carbonMax)<=1.2 && Float.parseFloat(chromiumMin)>=10.5)
					{
						HtmlReport.addHtmlStep("Condition 2", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 2", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 3":{
		
				break;
				}
				case "condition 4":
				{
					conditionDetails = "...then the minimum width must be 600 mm or more.";
					String widthMin = getProdValue(jObj, "ProductDimensions", "Width", "Minimum");
					if (Float.parseFloat(widthMin)>=600)
					{
						HtmlReport.addHtmlStep("Condition 4", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 4", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 5":
				{
					conditionDetails = "...then the maximum width must be less than 600 mm.";
					String widthMax = getProdValue(jObj, "ProductDimensions", "Width", "Minimum");
					if (Float.parseFloat(widthMax)<600)
					{
						HtmlReport.addHtmlStep("Condition 5", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 5", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 6":{
		
				break;
				}
				case "condition 7":{
		
				break;
				}
				case "condition 8":{
		
				break;
				}
				case "condition 9":{
		
				break;
				}
				case "condition 10":{
		
				break;
				}
				case "condition 11":{
		
				break;
				}
				case "condition 12":
				{
					conditionDetails = "Silicon electrical steel ...then the maximum carbon must be equal to or less than 0.08 percent, "
							+ "and the minimum silicon must be equal to or greater than 0.6 percent, and the maximum silicon "
							+ "must be equal to or less than 6 percent, and the maximum aluminum must be equal to or less than 1 percent.";
					String carbonMax = getProdValue(jObj, "ChemicalComposition", "Carbon", "Maximum");
					String siliconMin = getProdValue(jObj, "ChemicalComposition", "Silicon", "Minimum");
					String siliconMax = getProdValue(jObj, "ChemicalComposition", "Silicon", "Maximum");
					String aluminumMax = getProdValue(jObj, "ChemicalComposition", "Aluminum", "Maximum");
					if (Float.parseFloat(carbonMax)<=0.08 && Float.parseFloat(siliconMin)>=0.6
							&& Float.parseFloat(siliconMax) <=6 && Float.parseFloat(aluminumMax) <=1 )
					{
						HtmlReport.addHtmlStep("Condition 12", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 12", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 13":
				{
					conditionDetails = "Silico-manganese steel ...then the maximum carbon must be equal to or less than 0.7 "
							+ "percent, and the minimum manganese must be equal to or greater than 0.5 percent, and the maximum "
							+ "manganese must be equal to or less than 1.9 percent, and the minimum silicon must be equal to or "
							+ "greater than 0.6 percent, and the maximum silicon must be equal to or less than 2.3 percent.";
					String carbonMax = getProdValue(jObj, "ChemicalComposition", "Carbon", "Maximum");
					String manganeseMin = getProdValue(jObj, "ChemicalComposition", "Magnesium", "Minimum");
					String manganeseMax = getProdValue(jObj, "ChemicalComposition", "Magnesium", "Maximum");
					String siliconMin = getProdValue(jObj, "ChemicalComposition", "Silicon", "Minimum");
					String siliconMax = getProdValue(jObj, "ChemicalComposition", "Silicon", "Maximum");
					if (Float.parseFloat(carbonMax)<=0.7 && Float.parseFloat(manganeseMin)>=0.5
							&& Float.parseFloat(manganeseMax) <=1.9 && Float.parseFloat(siliconMin)>=0.6 
							&& Float.parseFloat(siliconMax)<=2.3)
					{
						HtmlReport.addHtmlStep("Condition 13", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 13", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 14":{
		
				break;
				}
				case "condition 15":
				{
					conditionDetails = "Razor blade steel ...then the maximum thickness must be equal to or less than "
							+ "0.25 mm, and the maximum width must be equal to or less than 23 mm, and the maximum percent "
							+ "chromium must be equal to or less than 14.7.";
					String cthicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					String widthMax = getProdValue(jObj, "ProductDimensions", "Width", "Maximum");
					String chromiumMax = getProdValue(jObj, "ChemicalComposition", "Chromium", "Maximum");
					if (Float.parseFloat(cthicknessMax)<=0.25 && Float.parseFloat(widthMax)<=23 
							&& Float.parseFloat(chromiumMax)<=14.7)
					{
						HtmlReport.addHtmlStep("Condition 15", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 15", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 16":
				{
					conditionDetails = "Universal mill plate ...then the minimum width must be greater than 150 mm,"
							+ " and the maximum width must be equal to or less than 1250 mm, and theminimum thickness must"
							+ " be equal to or greater than 4 mm.";
					String widthMin = getProdValue(jObj, "ProductDimensions", "Width", "Minimum");
					String widthMax = getProdValue(jObj, "ProductDimensions", "Width", "Maximum");
					String cthicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					
					if (Float.parseFloat(widthMin)>150 && Float.parseFloat(widthMax)<=1250 
							&& Float.parseFloat(cthicknessMin)>=4)
					{
						HtmlReport.addHtmlStep("Condition 16", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 16", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 17":
				{
					conditionDetails = "Heat-resisting steel ...then the maximum carbon must be less than 0.3 percent,"
							+ " and the minimum chromium must be equal to or greater than 4 percent, and the maximum "
							+ "chromium must be less than 10.5 percent.";
					
					String carbonMax = getProdValue(jObj, "ChemicalComposition", "Carbon", "Maximum");
					String chromiumMin = getProdValue(jObj, "ChemicalComposition", "Chromium", "Minimum");
					String chromiumMax = getProdValue(jObj, "ChemicalComposition", "Chromium", "Maximum");
					if (Float.parseFloat(carbonMax)<0.3 && Float.parseFloat(chromiumMin)>=4 
							&& Float.parseFloat(chromiumMax)<10.5)
					{
						HtmlReport.addHtmlStep("Condition 17", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 17", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					
					break;
				}
				case "condition 18":{
		
				break;
				}
				case "condition 19":
				{
					conditionDetails = "Welding quality wire rod ...then the maximum percent carbon must be less than 0.2, and the maximum "
							+ "percent sulfur must be less than 0.04, and the maximum percentphosphorus must be less than 0.04, and the "
							+ "maximum outside diameter must be less than 10.0 mm.";
					String carbonMax = getProdValue(jObj, "ChemicalComposition", "Carbon", "Maximum");
					String sulfurMax = getProdValue(jObj, "ChemicalComposition", "Sulfur", "Maximum");
					String phosphorusMax = getProdValue(jObj, "ChemicalComposition", "Phosphorus", "Maximum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(carbonMax)<0.2 && Float.parseFloat(sulfurMax)<0.04 
							&& Float.parseFloat(phosphorusMax)<0.04 && Float.parseFloat(outsideDiameterMax)<10)
					{
						HtmlReport.addHtmlStep("Condition 17", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 17", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 20":{
		
				break;
				}
				case "condition 21":{
		
				break;
				}
				case "condition 22":
				{
					conditionDetails = "High nickel alloy steel ...then the minimum percent nickel must be "
							+ "equal to or greater than 24.";
					String nickelMin = getProdValue(jObj, "ChemicalComposition", "Nickel", "Minimum");
					if (Float.parseFloat(nickelMin)>=24)
					{
						HtmlReport.addHtmlStep("Condition 22", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 22", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 23":{
		
				break;
				}
				case "condition 24":{
		
				break;
				}
				case "condition 25":
				{
					conditionDetails = "Chapter 73 stainless ...then the maximum percentage of carbon must be 1.2 percent or "
							+ "less and the minimum percentage of chromium must be 10.5 percent or more.";
					String carbonMax = getProdValue(jObj, "ChemicalComposition", "Carbon", "Maximum");
					String chromiumMin = getProdValue(jObj, "ChemicalComposition", "Chromium", "Minimum");
					if (Float.parseFloat(carbonMax)<=1.2 && Float.parseFloat(chromiumMin)>=10.5 )
					{
						HtmlReport.addHtmlStep("Condition 25", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 25", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 26":
				{
					conditionDetails = "...then the maximum outside diameter and the minimum outside"
							+ " diameter must be equal to or less than 114.3 mm.";
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					if (Float.parseFloat(outsideDiameterMax)<=114.3 && Float.parseFloat(outsideDiameterMin)<=114.3)
					{
						HtmlReport.addHtmlStep("Condition 26", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 26", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 27":
				{
					conditionDetails = "...then the minimum outside diameter must be equal to or greater than 114.3 mm and"
							+ " less than 406.4 mm, and the maximum outside diameter must be equal to or greater than 114.3 "
							+ "mm and less than 406.4 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					
					if (Float.parseFloat(outsideDiameterMin)>=114.3 && Float.parseFloat(outsideDiameterMin)<406.4
							&& Float.parseFloat(outsideDiameterMax)>=114.3 && Float.parseFloat(outsideDiameterMax)<406.4 )
					{
						HtmlReport.addHtmlStep("Condition 27", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 27", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 28":
				{
					conditionDetails = "...then the maximum outside diameter and the minimum outside "
							+ "diameter must be greater than 406.4 mm.";
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					if (Float.parseFloat(outsideDiameterMax)>406.4 && Float.parseFloat(outsideDiameterMin)>406.4)
					{
						HtmlReport.addHtmlStep("Condition 28", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 28", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 29":
				{
					conditionDetails = "...then the minimum outside diameter must be equal to or greater than 114.3 mm"
							+ " and less than 215.9 mm, and the maximum outside diameter must be equal to or greater "
							+ "than 114.3 mm and less than 215.9 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					
					if (Float.parseFloat(outsideDiameterMin)>=114.3 && Float.parseFloat(outsideDiameterMin)<215.9
							&& Float.parseFloat(outsideDiameterMax)>=114.3 && Float.parseFloat(outsideDiameterMax)<215.9 )
					{
						HtmlReport.addHtmlStep("Condition 29", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 29", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 30":
				{
					conditionDetails = "...then the maximum thickness must be less than 12.7 mm.";
					String cthicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(cthicknessMax)<12.7 )
					{
						HtmlReport.addHtmlStep("Condition 30", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 30", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 31":
				{
					conditionDetails = "...then the minimum thickness must be greater than or equal to 12.7 mm.";
					String thicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					if (Float.parseFloat(thicknessMin)>=12.7 )
					{
						HtmlReport.addHtmlStep("Condition 31", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 31", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 32":
				{
					conditionDetails = "...then the outside diameter must be equal to or less than 168.3 mm.";
					break;
				}
				case "condition 33":
				{
					conditionDetails = "...then the minimum thickness must be greater than 9.5 mm.";
					String thicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					if (Float.parseFloat(thicknessMin)>9.5 )
					{
						HtmlReport.addHtmlStep("Condition 33", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 33", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 34":
				{
					conditionDetails = "...then the maximum thickness must be equal to or less than 9.5 mm.";
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMax)<=9.5 )
					{
						HtmlReport.addHtmlStep("Condition 34", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 34", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 35":
				{
					conditionDetails = "...then the outside diameter must be greater than 168.3 mm.";
					break;
				}
				case "condition 36":{
		
				break;
				}
				case "condition 37":
				{
					conditionDetails = "...then the minimum outside diameter must be equal to or greater than 215.9 mm "
							+ "and equal to or less than 406.4 mm, and the maximum outside diameter must be equal to or"
							+ " greater than 215.9 mm and equal to or less than 406.4 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin)>=215.9 && Float.parseFloat(outsideDiameterMin)<=406.4
							&& Float.parseFloat(outsideDiameterMax)>=215.9 && Float.parseFloat(outsideDiameterMax)<=406.4 )
					{
						HtmlReport.addHtmlStep("Condition 37", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 37", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 38":
				{
					conditionDetails = "...then the minimum outside diameter must be equal to or greater than 215.9 mm and equal "
							+ "to or less than 285.8 mm, and the maximum outside diameter must be equal to or greater than 285.8 mm "
							+ "and equal to or less than 406.4 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin)>=215.9 && Float.parseFloat(outsideDiameterMin)<=285.8
							&& Float.parseFloat(outsideDiameterMax)>=285.8 && Float.parseFloat(outsideDiameterMax)<=406.4 )
					{
						HtmlReport.addHtmlStep("Condition 38", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 38", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 39":
				{
					conditionDetails = "...then the minimum outside diameter must be greater than 285.8 mm and equal to or "
							+ "less than 406.4 mm, and the maximum outside diameter must be greater than 285.8 mm and equal "
							+ "to or less than 406.4 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) > 285.8 && Float.parseFloat(outsideDiameterMin)<=406.4
							&& Float.parseFloat(outsideDiameterMax)> 285.8 && Float.parseFloat(outsideDiameterMax)<=406.4 )
					{
						HtmlReport.addHtmlStep("Condition 39", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 39", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 40":
				{
					conditionDetails = "...then the minimum outside diameter must be greater than 406.4 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					if (Float.parseFloat(outsideDiameterMin) > 406.4)
					{
						HtmlReport.addHtmlStep("Condition 40", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 40", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 41":
				{
					conditionDetails = "...then the minimum outside diameter and the maximum outside diameter must be less than 38.1 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) < 38.1 && Float.parseFloat(outsideDiameterMax) < 38.1)
					{
						HtmlReport.addHtmlStep("Condition 41", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 41", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 42":
				{
					conditionDetails = "...then the minimum outside diameter must be equal to or greater than 38.1 mm and less"
							+ " than 190.5 mm, and the maximum outside diameter must be greater than 38.1 mm and less than 190.5 mm";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) >= 38.1 && Float.parseFloat(outsideDiameterMin) < 190.5
							&& Float.parseFloat(outsideDiameterMax)> 38.1 && Float.parseFloat(outsideDiameterMax) < 190.5 )
					{
						HtmlReport.addHtmlStep("Condition 42", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 42", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 43":
				{
					conditionDetails = "...then the minimum outside diameter and the maximum outside diameter must be greater than 285.8 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) > 285.8 && Float.parseFloat(outsideDiameterMax) > 285.8)
					{
						HtmlReport.addHtmlStep("Condition 43", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 43", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 44":
				{
					conditionDetails = "...then the maximum thickness must be less than 6.4 mm.";
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMax)< 6.4 )
					{
						HtmlReport.addHtmlStep("Condition 44", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 44", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 45":
				{
					conditionDetails = "...then the minimum thickness must be equal to or greater than 6.4 mm, "
							+ "and the maximum thickness must be equal to or less than 12.7 mm.";
					String thicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMin)>= 6.4 && Float.parseFloat(thicknessMax) <=12.7)
					{
						HtmlReport.addHtmlStep("Condition 45", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 45", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 46":
				{
					conditionDetails = "...then the minimum outside diameter must be greater than 114.3 mm and less than 190.5 mm, "
							+ "and the maximum outside diameter must be greater than 114.3 mm and less than 190.5 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) > 114.3 && Float.parseFloat(outsideDiameterMin) < 190.5 &&
							Float.parseFloat(outsideDiameterMax) > 114.3 && Float.parseFloat(outsideDiameterMax) < 190.5)
					{
						HtmlReport.addHtmlStep("Condition 46", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 46", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 47":
				{
					conditionDetails = "...then the minimum thickness must be equal to or greater than 12.7 mm, "
							+ "and the maximum thickness must be less than 19 mm.";
					String thicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMin)>= 12.7 && Float.parseFloat(thicknessMax) < 19)
					{
						HtmlReport.addHtmlStep("Condition 47", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 47", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 48":
				{
					conditionDetails = "...then the minimum thickness must be equal to or greater than 19 mm.";
					String thicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					if (Float.parseFloat(thicknessMin)>= 19 )
					{
						HtmlReport.addHtmlStep("Condition 48", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 48", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 49":
				{
					conditionDetails = "...then the minimum outside diameter must be equal to or greater than 38.1 mm and less than 114.3 mm, "
							+ "and the maximum outside diameter must be greater than 38.1 mm and less than 114.3 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) >=38.1 && Float.parseFloat(outsideDiameterMin) < 114.3 &&
							Float.parseFloat(outsideDiameterMax) > 38.1 && Float.parseFloat(outsideDiameterMax) < 114.3)
					{
						HtmlReport.addHtmlStep("Condition 49", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 49", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 50":
				{
					conditionDetails = "...then the minimum outside diameter must be equal to or "
							+ "greater than 190.5 mm and equal to or less than 285.8 mm, "
							+ "and the maximum outside diameter must be equal to or greater than 190.5 mm "
							+ "and less than 285.8 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) >=190.5 && Float.parseFloat(outsideDiameterMin) <= 285.8 &&
							Float.parseFloat(outsideDiameterMax) >= 190.5 && Float.parseFloat(outsideDiameterMax) < 285.8)
					{
						HtmlReport.addHtmlStep("Condition 50", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 50", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 51":
				{
					
				break;
				}
				case "condition 52":
				{
					conditionDetails = "...then the outside diameter must be greater than 609.6 mm.";
					break;
				}
				case "condition 53":
				{
					conditionDetails = "...then the minimum outside diameter must be greater than 406.4 mm and equal to or less than 609.6 mm,"
							+ " and the maximum outside diameter must be equal to or greater than 406.4 mm and less than 609.6 mm.";
					String outsideDiameterMin = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Minimum");
					String outsideDiameterMax = getProdValue(jObj, "ProductDimensions", "OutsideDiameter", "Maximum");
					if (Float.parseFloat(outsideDiameterMin) > 406.4 && Float.parseFloat(outsideDiameterMin) <= 609.6 &&
							Float.parseFloat(outsideDiameterMax) >= 406.4&& Float.parseFloat(outsideDiameterMax) < 609.6)
					{
						HtmlReport.addHtmlStep("Condition 53", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 53", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
				
					break;
				}
				case "condition 54":
				{
					conditionDetails = "...then the maximum thickness must be less than 1.65 mm.";
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMax) < 1.65 )
					{
						HtmlReport.addHtmlStep("Condition 54", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 54", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 55":
				{
					conditionDetails = "...then the maximum thickness must be equal to or less than 2.54 mm.";
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMax) <= 2.54 )
					{
						HtmlReport.addHtmlStep("Condition 55", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 55", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 56":
				{
					conditionDetails = "...then the maximum thickness must be less than 6.4 mm.";
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMax) < 6.4 )
					{
						HtmlReport.addHtmlStep("Condition 56", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 56", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 57":
				{
					conditionDetails = "...then the minimum percent nickel must be greater than 0.5.";
					String nickelMin = getProdValue(jObj, "ChemicalComposition", "Nickel", "Minimum");
					if (Float.parseFloat(nickelMin)> 0.5)
					{
						HtmlReport.addHtmlStep("Condition 57", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 57", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 58":
				{
					conditionDetails = "...then the maximum percent nickel must be equal to or less than 0.5.";
					String nickelMax = getProdValue(jObj, "ChemicalComposition", "Nickel", "Maximum");
					if (Float.parseFloat(nickelMax)<= 0.5)
					{
						HtmlReport.addHtmlStep("Condition 58", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 58", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 59":
				{
					conditionDetails = "...then the minimum percent molybdenum must be greater than 1.5 and the "
							+ "maximum percent molybdenum must be less than 5.";
					String molybdenumMin = getProdValue(jObj, "ChemicalComposition", "Molybdenum", "Minimum");
					String molybdenumMax = getProdValue(jObj, "ChemicalComposition", "Molybdenum", "Maximum");
					if (Float.parseFloat(molybdenumMin)> 1.5 && Float.parseFloat(molybdenumMax) < 5)
					{
						HtmlReport.addHtmlStep("Condition 59", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 59", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 60":
				{
					conditionDetails = "...then the minimum percent molybdenum must be equal to or less than 1.5 "
							+ "or the maximum percent molybdenum must be equal to or greater than 5.";
					String molybdenumMin = getProdValue(jObj, "ChemicalComposition", "Molybdenum", "Minimum");
					String molybdenumMax = getProdValue(jObj, "ChemicalComposition", "Molybdenum", "Maximum");
					if (Float.parseFloat(molybdenumMin) <= 1.5 && Float.parseFloat(molybdenumMax) >= 5)
					{
						HtmlReport.addHtmlStep("Condition 60", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 60", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
				break;
				}
				case "condition 61":
				{
					conditionDetails = "...then the minimum percent nickel must be greater than 0.5 and the "
							+ "maximum percent nickel must be less than 24.";
					String nickelMin = getProdValue(jObj, "ChemicalComposition", "Nickel", "Minimum");
					String nickelMax = getProdValue(jObj, "ChemicalComposition", "Nickel", "Maximum");
					if (Float.parseFloat(nickelMin)> 0.5 && Float.parseFloat(nickelMax) < 24)
					{
						HtmlReport.addHtmlStep("Condition 61", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 61", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 62":
				{
					conditionDetails = "...then the minimum percent nickel must be equal to or less"
							+ " than 0.5 or the maximum percent nickel must be equal to or greater than 24.";
					String nickelMin = getProdValue(jObj, "ChemicalComposition", "Nickel", "Minimum");
					String nickelMax = getProdValue(jObj, "ChemicalComposition", "Nickel", "Maximum");
					if (Float.parseFloat(nickelMin) <= 0.5 || Float.parseFloat(nickelMax) >= 24)
					{
						HtmlReport.addHtmlStep("Condition 62", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 62", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 63":
				{
					conditionDetails = "...then the maximum percent chromium must be less than 15.";
					String chromiumMax = getProdValue(jObj, "ChemicalComposition", "Chromium", "Maximum");
					if (Float.parseFloat(chromiumMax)<15)
					{
						HtmlReport.addHtmlStep("Condition 63", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 63", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 64":
				{
					conditionDetails = "...then the maximum percent chromium must be equal to or greater than 15.";
					String chromiumMax = getProdValue(jObj, "ChemicalComposition", "Chromium", "Maximum");
					if (Float.parseFloat(chromiumMax) >= 15)
					{
						HtmlReport.addHtmlStep("Condition 64", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 64", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 65":
				{
					conditionDetails = "...then the minimum thickness must be equal to or greater than 1.65 mm.";
					String thicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					if (Float.parseFloat(thicknessMin) >= 1.65 )
					{
						HtmlReport.addHtmlStep("Condition 65", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 65", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 66":
				{
					conditionDetails = "...then the maximum thickness must be less than 4 mm.";
					String thicknessMax = getProdValue(jObj, "ProductDimensions", "Thickness", "Maximum");
					if (Float.parseFloat(thicknessMax) < 4)
					{
						HtmlReport.addHtmlStep("Condition 66", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 66", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
				break;
				}
				case "condition 67":
				{
					conditionDetails = "...then the minimum thickness must be equal to or greater than 4 mm.";
					String thicknessMin = getProdValue(jObj, "ProductDimensions", "Thickness", "Minimum");
					if (Float.parseFloat(thicknessMin) >= 4 )
					{
						HtmlReport.addHtmlStep("Condition 67", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 67", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 68":
				{
					conditionDetails = "...then the maximum carbon must be less than 0.25 percent.";
					String carbonMax = getProdValue(jObj, "ChemicalComposition", "Carbon", "Maximum");
					if (Float.parseFloat(carbonMax) < 0.25)
					{
						HtmlReport.addHtmlStep("Condition 68", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 68", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 69":
				{
					conditionDetails = "...then the minimum carbon must be equal to or greater than 0.25 percent.";
					String carbonMin = getProdValue(jObj, "ChemicalComposition", "Carbon", "Minimum");
					if (Float.parseFloat(carbonMin) >= 0.25)
					{
						HtmlReport.addHtmlStep("Condition 69", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>", 
								condition+" is met", "VP", "pass", "");
					}
					else
					{
						ovralResult = false;
						HtmlReport.addHtmlStep("Condition 69", "<abbr title='"+conditionDetails+"'>Hover here for "+condition+"'s details ...</abbr>",
								condition+" is not met", "VP", "fail", "");
					}
					break;
				}
				case "condition 70":{
		
				break;
				}
				case "condition 71":{
		
				break;
				}
				case "condition 72":{
		
				break;
				}
				case "condition 73":{
		
				break;
				}
				case "condition 74":{
		
				break;
				}
				case "condition 75":{
		
				break;
				}
				case "condition 76":{
		
				break;
				}
				case "condition 77":{
		
				break;
				}
				case "condition 78":{
		
				break;
				}
				case "condition 79":{
		
				break;
				}
				case "condition 80":{
		
				break;
				}
				case "condition 81":{
		
				break;
				}
				case "condition 82":{
		
				break;
				}
				case "condition 83":{
		
				break;
				}
				case "condition 84":{
		
				break;
				}
				case "condition 85":{
		
				break;
				}
				case "condition 86":{
		
				break;
				}
				case "condition 87":{
		
				break;
				}
				case "condition 88":{
		
				break;
				}
				case "condition 89":{
		
				break;
				}
				case "condition 90":{
		
				break;
				}
				case "condition 91":{
		
				break;
				}
				case "condition 92":{
		
				break;
				}
				case "condition 93":{
		
				break;
				}
				case "condition 94":{
		
				break;
				}
				case "condition 95":{
		
				break;
				}
				case "condition 96":{
		
				break;
				}
				case "condition 97":{
		
				break;
				}
				case "condition 98":{
		
				break;
				}
				case "condition 99":{
		
				break;
				}
				case "condition 100":{
		
				break;
				}
				case "condition 101":{
		
				break;
				}
				case "condition 102":{
		
				break;
				}
				case "condition 103":{
		
				break;
				}
				case "condition 104":{
		
				break;
				}
				case "condition 105":{
		
				break;
				}
				case "condition 106":{
		
				break;
				}
				case "condition 107":{
		
				break;
				}
				case "condition 108":{
		
				break;
				}
				case "condition 109":{
		
				break;
				}
				case "condition 110":{
		
				break;
				}
				case "condition 111":{
		
				break;
				}
				case "condition 112":{
		
				break;
				}
				case "condition 113":{
		
				break;
				}
				case "condition 114":{
		
				break;
				}
				case "condition 115":{
		
				break;
				}
				case "condition 116":{
		
				break;
				}
				case "condition 117":{
		
				break;
				}
				case "condition 118":{
		
				break;
				}
				case "condition 119":{
		
				break;
				}
				case "condition 120":{
		
				break;
				}
				case "condition 121":{
		
				break;
				}
				case "condition 122":{
		
				break;
				}
				case "condition 123":{
		
				break;
				}
				case "condition 124":{
		
				break;
				}
				case "condition 125":{
		
				break;
				}
				case "condition 126":{
		
				break;
				}
				case "condition 127":{
		
				break;
				}
				case "condition 128":{
		
				break;
				}
				case "condition 129":{
		
				break;
				}
				case "condition 130":{
		
				break;
				}
				case "condition 131":{
		
				break;
				}
				case "condition 132":{
		
				break;
				}
				case "condition 133":{
		
				break;
				}
				case "condition 134":{
		
				break;
				}
				case "condition 135":{
		
				break;
				}
				case "condition 136":{
		
				break;
				}
				case "condition 137":{
		
				break;
				}
				case "condition 138":{
		
				break;
				}
				case "condition 139":{
		
				break;
				}
				case "condition 140":{
		
				break;
				}
				default :
				{
					failTestSuite("Validate condition", "All conditions are valid", 
							condition+" is not a valid condition", "Step", "fail", "");
					break;
				}
	
	
				}//switch
			}//if product type
			else //Auminium
			{
				
			}
			
		}
		
		//HtmlReport.addHtmlStep("Validate "+jObj.get("HTSUSCode"), conditions, "stepActualResult", "VP", "pass", "");
		return ovralResult;
	}
	
	public static String getProdValue(JSONObject obj, String blockName, String productName, String minMax)
	{
		String prodVal = "";
		String key;
		JSONArray blockArray = (JSONArray) obj.get(blockName);
		for (Object block : blockArray) 
		{
		    JSONObject blockItem = (JSONObject) block;
		    key = (String) blockItem.get("Key");
		    if(key.equalsIgnoreCase(productName))
		    {
		    	Object valueObject = blockItem.get("Value");
		    	JSONObject jValueObject = (JSONObject) valueObject;
		    	prodVal = (String) jValueObject.get(minMax);
		    	break;
		    }
		}
		
		return prodVal;
	}
	
	
}
