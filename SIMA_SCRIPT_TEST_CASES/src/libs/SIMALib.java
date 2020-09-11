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
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

import ReportLibs.HtmlReport;
import bsh.util.GUIConsoleInterface;

public class SIMALib{
	public static String filedDate,
	actualInitiationSignature, calculatedInitiationSignature, petitionOutcome;
	public static int petitionInitiationExtension;
	static DateFormat format;
	static Calendar calendar;
	static String caseType;
	static String investigationId;
	static String orderId;
	
	
	public SIMALib() throws IOException {
		//super();
		//this.format = new SimpleDateFormat("M/d/yyyy");
		//this.calendar = Calendar.getInstance();
	}
	/**
	 * This method login to ADCVD web application
	 * @param url: url for the application
	 * @param user: user
	 * @param password: password
	 * @param modCreate, modifaction or creation validation
	 * @exception Exception
	 */
	public static boolean loginToSima(String url, 
									 String user, 
									 String password,
									 String modCreate) throws Exception
	{
		boolean loginStatus = true;
		navigateTo(url);
		
		if(!checkElementExists(guiMap.get("Home")))
		{
			failTestSuite("navigate to SIMA url", "User is able to navigate", 
				"Not as expected", "Step", "fail", "navigation failed");
			loginStatus = false;
		}else
		{ 	holdSeconds(1);
			int currentWait = setBrowserTimeOut(2);
			if(checkElementExists(replaceGui(guiMap.get("TopMenu"), "Sign out")))
			{
				clickElementJs(replaceGui(guiMap.get("TopMenu"), "Sign out") );
				holdSeconds(1);
				clickElementJs(guiMap.get("SignInLink")); 
			}else
			{
				holdSeconds(2);
				clickElementJs(guiMap.get("SignInLink")); 
			}
			//replaceGui(guiMap.get("TopMenu"), "Sign out")
			enterText(guiMap.get("UserName"), user);
			enterText(guiMap.get("Password"), password);
			clickElementJs(guiMap.get("SignInButton"));			
			if(checkElementExists(replaceGui(guiMap.get("TopMenu"), "Sign out")))
			{
				updateHtmlReport("Login to SIMA App",  "User is able to login", "As expected", 
					"Step", "pass", "Login to SIMA");
				if(checkElementExists(replaceGui(guiMap.get("TopMenu"), "See Licensing")))
				{
					holdSeconds(1);
					clickElementJs(replaceGui(guiMap.get("TopMenu"), "See Licensing") );
					switchToWindow();
				}
				setBrowserTimeOut(currentWait);
			}else
			{
				loginStatus = false;
				setBrowserTimeOut(currentWait);
				updateHtmlReport("Login to SIMA App",  "User is able to login", "Not as expected", 
						"Step", "fail", "Login to SIMA");
			}
			
		}
		if(modCreate.equalsIgnoreCase("modification"))
		{
			clickElementJs(guiMap.get("licenseItem"));
		}else
		{
			clickElementJs(replaceGui(guiMap.get("TopMenu"), "Standard License") );
			//Standard License
			//url = "https://content.trade.gov/ita-sima-web/License/Standard/Createddd";
			//navigateTo(url);
		}
		
		return loginStatus;
	}
	/**
	 * This method fills up step 1
	 * @param row: map of test case's data
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean CheckHtsUnitValue(ArrayList<LinkedHashMap<String, String>> scenarios) throws Exception
	{
		String url = "https://content.trade.gov/ita-sima-web/License/Standard/Createddd";
		//navigateTo(url);
		BigDecimal customerValue;
		BigDecimal volume;
		int i=0;
		BigDecimal unitValue;		
		Random rand = new Random();	
		boolean checked = true;
		//scrollToElement(guiMap.get("ProdInfoSection"));
		scrollToElement(replaceGui(guiMap.get("inputField"), "Product Description"));
		selectElementByText(replaceGui(guiMap.get("selectField"), "Country of Melt & Pour"), "United States of America");
		for(LinkedHashMap<String, String> row : scenarios)
		{
			i++;
			String highAuv = row.get("HighAUV").trim().replace("$", "").replace(" ", "");
			String lowAuv = row.get("LowAUV").trim().replace("$", "").replace(" ", "");
			BigDecimal floatLowAuv = new BigDecimal(lowAuv);
			BigDecimal floatHighAuv = new BigDecimal(highAuv);
			String htsCode = row.get("HTS #").trim();
			HtmlReport.addHtmlStepTitle(i+". Validate unit value field for ["+htsCode+"]", "Title");
			enterTextAndClear(replaceGui(guiMap.get("inputField"), "HTS Number"), htsCode);
			//less
			volume=new BigDecimal(rand.nextInt(2)+2);
			customerValue =new BigDecimal(floatLowAuv.doubleValue()/2);
			//customerValue = new BigDecimal(floatLowAuv.doubleValue() * volume.doubleValue() - floatLowAuv.doubleValue()) ;		
			unitValue = new BigDecimal(customerValue.doubleValue()/volume.intValue()).setScale(2, RoundingMode.DOWN);
			checked = checked & compareAndReport(floatLowAuv, floatHighAuv, unitValue, volume, customerValue, htsCode);
			//=min
			volume=new BigDecimal(rand.nextInt(100)+1);
			customerValue = new BigDecimal(floatLowAuv.doubleValue() * volume.doubleValue()) ;		
			unitValue = new BigDecimal(customerValue.doubleValue()/volume.intValue()).setScale(2, RoundingMode.HALF_EVEN);
			checked = checked & compareAndReport(floatLowAuv, floatHighAuv, unitValue, volume, customerValue, htsCode);
			//between
			volume=new BigDecimal(rand.nextInt(100)+1);
			customerValue = new BigDecimal((floatLowAuv.doubleValue()+1) * volume.doubleValue() ) ;		
			unitValue = new BigDecimal(customerValue.doubleValue()/volume.intValue());
			checked = checked & compareAndReport(floatLowAuv, floatHighAuv, unitValue, volume, customerValue, htsCode);
			//=max
			
			
			volume=new BigDecimal(rand.nextInt(100)+1);
			customerValue = new BigDecimal(floatHighAuv.doubleValue() * volume.doubleValue()) ;		
			unitValue = new BigDecimal(customerValue.doubleValue()/volume.intValue()).setScale(2, RoundingMode.HALF_EVEN);
			checked = checked & compareAndReport(floatLowAuv, floatHighAuv, unitValue, volume, customerValue, htsCode);
			//greater thAN MAX
			volume=new BigDecimal(rand.nextInt(100)+1);
			customerValue = new BigDecimal((floatHighAuv.doubleValue() +1) * volume.doubleValue()) ;		
			unitValue = new BigDecimal(customerValue.doubleValue()/volume.intValue()).setScale(2, RoundingMode.HALF_UP);
			checked = checked & compareAndReport(floatLowAuv, floatHighAuv, unitValue, volume, customerValue, htsCode);
		}//for
		return checked;
	}
	
	public static boolean compareAndReport( BigDecimal lowAuv,
											BigDecimal highAuv,
											BigDecimal unitValue,
											BigDecimal volume,
											BigDecimal customerValue,
											String htsCode) throws Exception
	{
		enterTextAndClear(replaceGui(guiMap.get("inputField"), "Volume (Quantity - KG)"), volume+"");
		enterTextAndClear(replaceGui(guiMap.get("inputField"), "Customs Value (U.S. $)"), customerValue+"");		
		enterTextAndClear(replaceGui(guiMap.get("inputField"), "Volume (Quantity - KG)"), volume+"");
		//BigDecimal val = new BigDecimal (77777);
		unitValue = unitValue.setScale(2, RoundingMode.DOWN);
		String outsideErrorStyle = getElementAttribute(guiMap.get("outsideError"), "style");
		if( (lowAuv.doubleValue() <= unitValue.doubleValue() && unitValue.doubleValue() <= highAuv.doubleValue()) && (outsideErrorStyle.contains("none;")) )
		{
			updateHtmlReport("Scenario: [Volume (Quantity - KG) = "+volume.intValue()+"], [Customs Value (U.S. $) = "+customerValue.intValue()+"], "
					+ "[LowAuv="+lowAuv + "], [HighAuv=" +highAuv +"], [valparunit=" + unitValue.doubleValue()+"]",
					"message 'Unit Value is outside of the range of HTS Number' should not be displaying", "As expected", 
					"Step", "pass", "");
			return true;
		}
		
		else if( (lowAuv.doubleValue() <= unitValue.doubleValue() && unitValue.doubleValue() <= highAuv.doubleValue()) && (!outsideErrorStyle.contains("none;")) )
		{
			updateHtmlReport("Scenario: [Volume (Quantity - KG) = "+volume.intValue()+"], [Customs Value (U.S. $) = "+customerValue.intValue()+"], "
					+ "[LowAuv="+lowAuv + "], [HighAuv=" +highAuv +"], [valparunit=" + unitValue.doubleValue()+"]",
					"message 'Unit Value is outside of the range of HTS Number' should not be displaying", "Not as expected", 
					"Step", "fail", htsCode + "_" + unitValue.doubleValue());
			return false;
		}
		else if( (lowAuv.doubleValue() > unitValue.doubleValue() || unitValue.doubleValue() > highAuv.doubleValue()) && (!outsideErrorStyle.contains("none;")))
		{
			updateHtmlReport("Scenario: [Volume (Quantity - KG) = "+volume.intValue()+"], [Customs Value (U.S. $) = "+customerValue.intValue()+"], "
					+ "[LowAuv="+lowAuv + "], [HighAuv=" +highAuv +"], [valparunit=" + unitValue.doubleValue()+"]",
					"message 'Unit Value is outside of the range of HTS Number' should be displaying", "As expected", 
					"Step", "pass", "");
			return true;
		}
		else
		{
			updateHtmlReport("Scenario: [Volume (Quantity - KG) = "+volume.intValue()+"], [Customs Value (U.S. $) = "+customerValue.intValue()+"], "
					+ "[LowAuv="+lowAuv + "], [HighAuv=" +highAuv +"], [valparunit=" + unitValue.doubleValue()+"]",
					"message 'Unit Value is outside of the range of HTS Number' should be displaying", "not as expected", 
					"Step", "fail", htsCode + "_" + unitValue.doubleValue());
			return false;
		}			
	}
	
	
	/**
	 * This method checks unusual countries
	 * @param scenarios: list of countries
	 * @param col: column name
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean checkUnusualCountries(ArrayList<LinkedHashMap<String, String>> scenarios,
			String fieldName) throws Exception
	{
		boolean checked = true;
		String colName = "Unusal Country Of Melt and Pour";
		String xpath = "unusualCtryOfSmelt";
		
		if (fieldName.equalsIgnoreCase("Country in which the product(s) were manufactured (Country of Orgin)"))
		{
			 colName = "Unusual Country Of Origin";
			 xpath = "unusualCtryOfOrgn";
			
		}else if(fieldName.equalsIgnoreCase("Country from which the product(s) were exported"))
		{
			 colName = "Unusual Country Of Export";
			 xpath = "unusualCtryOfExport";
		}else
		{
			
		}
		scrollToElement(replaceGui(guiMap.get("selectField"), fieldName));
		for(LinkedHashMap<String, String> row : scenarios)
		{
			String country = row.get("Country Name").trim();
			String isUnusual = row.get(colName).trim();
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName), country);
			String outsideErrorStyle = getElementAttribute(guiMap.get(xpath), "style");
			if (isUnusual.equalsIgnoreCase("TRUE"))
			{
				if(!outsideErrorStyle.contains("none;"))
				{
					updateHtmlReport("Country = " + country, "The message ["+colName+"] should display",
							"As expected", "VP", "pass", "validate for country "+ country);
				}
				else
				{
					checked = false;
					updateHtmlReport("Country = " + country, "The message ["+colName+"] should display",
							"Not as expected", "VP", "fail", "validate for country "+ country);
				}
			}
			else
			{
				if(outsideErrorStyle.contains("none;"))
				{
					updateHtmlReport("Country = " + country, "The message ["+colName+"] should not display",
							"As expected", "VP", "pass", "validate for country "+ country);
				}
				else
				{
					checked = false;
					updateHtmlReport("Country = " + country, "The message ["+colName+"] should not display",
							"Not as expected", "VP", "fail", "validate for country "+ country);
				}
			}
		}
		return checked;
	}
	
	
	

	/**
	 * This method validate mismatched countries
	 * @param scenarios: list of countries
	 * @param col: column name
	 * @return true if all displayed messages are correct, false if not
	 * @exception Exception
	*/
	public static boolean checkMismatchCountries(List<String> otherCountries,
			List<String> naftaCountries, List<String> euCountries) throws Exception
	{
		boolean checked = true;
		Random rand = new Random();
		String fieldName1="Country in which the product(s) were manufactured (Country of Orgin)";
		String fieldName2="Country from which the product(s) were exported";
		String colName = "Unusal Country Of Melt and Pour";
		String xpath = "unusualCtryOfSmelt";
		String originC, exportC;
		int scenariosGroup = 15;
		int naftaSize = naftaCountries.size();
		int euSize = euCountries.size();
		int otherSize = otherCountries.size();
		scrollToElement(replaceGui(guiMap.get("selectField"), fieldName1));
		for(int i=0; i<naftaSize;i++)
		{
			originC = naftaCountries.get(i);
			exportC = naftaCountries.get((i+1)%2);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			
			if(originMismatch.contains("none;") && exportMismatch.contains("none;"))
			{
				updateHtmlReport("NAFTA Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should not display",
						"As expected", "VP", "pass", "NAFTA - "+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("NAFTA Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should not display",
						"Not as expected", "VP", "fail", "NAFTA - "+ originC+"_"+ exportC);
			}
		}
		
		for(int i=0; i<naftaSize;i++)
		{
			originC = naftaCountries.get(i);
			exportC = euCountries.get(rand.nextInt(euSize-1));
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			
			if(!originMismatch.contains("none;") && !exportMismatch.contains("none;"))
			{
				updateHtmlReport("NAFTA-EU Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"As expected", "VP", "pass", "NAFTA-EU - "+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("NAFTA-EU Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"Not as expected", "VP", "fail", "NAFTA-EU - "+ originC+"_"+ exportC);
			}
		}
		
		for(int i=0; i<naftaSize;i++)
		{
			originC = naftaCountries.get(i);
			exportC = otherCountries.get(rand.nextInt(otherSize-1));
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			
			if(!originMismatch.contains("none;") && !exportMismatch.contains("none;"))
			{
				updateHtmlReport("NAFTA-Other Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"As expected", "VP", "pass", "NAFTA-Other - "+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("NAFTA-Other Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"Not as expected", "VP", "fail", "NAFTA-other - "+ originC+"_"+ exportC);
			}
		}
		
		////eu
		
		for(int i=0; i<scenariosGroup;i++)
		{
			originC = euCountries.get(rand.nextInt(euSize-1));
			exportC = euCountries.get(rand.nextInt(euSize-1));
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			
			if(originMismatch.contains("none;") && exportMismatch.contains("none;"))
			{
				updateHtmlReport("EU Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should not display",
						"As expected", "VP", "pass", "EU - "+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("EU Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should not display",
						"Not as expected", "VP", "fail", "EU - "+ originC+"_"+ exportC);
			}
		}
		for(int i=0; i<naftaSize;i++)
		{
			originC = euCountries.get(rand.nextInt(euSize-1));
			exportC = naftaCountries.get(i);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			
			if(!originMismatch.contains("none;") && !exportMismatch.contains("none;"))
			{
				updateHtmlReport("EU-NAFTA Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"As expected", "VP", "pass", "EU - NAFTA"+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("EU-NAFTA Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"Not as expected", "VP", "fail", "EU - NAFTA"+ originC+"_"+ exportC);
			}
		}
		for(int i=0; i<=scenariosGroup;i++)
		{
			originC = euCountries.get(rand.nextInt(euSize-1));
			exportC = otherCountries.get(rand.nextInt(otherSize-1));
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			if(!originMismatch.contains("none;") && !exportMismatch.contains("none;"))
			{
				updateHtmlReport("EU-Other Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"As expected", "VP", "pass", "EU - other - "+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("EU-Other Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"Not as expected", "VP", "fail", "EU - other - "+ originC+"_"+ exportC);
			}
		}
		
		//other
		
		for(int i=0; i<scenariosGroup;i++)
		{
			originC = exportC = otherCountries.get(rand.nextInt(otherSize-1));
			//exportC = otherCountries.get((i+1)%2);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			
			if(originMismatch.contains("none;") && exportMismatch.contains("none;"))
			{
				updateHtmlReport("SAME Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should not display",
						"As expected", "VP", "pass", "SAME- "+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("SAME Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should not display",
						"Not as expected", "VP", "fail", "SAME - "+ originC+"_"+ exportC);
			}
		}
		
		for(int i=0; i<scenariosGroup;i++)
		{
			int x= rand.nextInt(100);
			originC = otherCountries.get(x);
			exportC = otherCountries.get(x+1);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName1), originC);
			selectElementByText(replaceGui(guiMap.get("selectField"), fieldName2), exportC);
			String originMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 1);
			String exportMismatch = getNiemElementAttribute(guiMap.get("cntryMismatch"), "style", 2);
			
			if(!originMismatch.contains("none;") && !exportMismatch.contains("none;"))
			{
				updateHtmlReport("Different Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"As expected", "VP", "pass", "Diff- "+ originC+"_"+ exportC);
			}
			else
			{
				checked = false;
				updateHtmlReport("Different Countries: Country of origin = " + originC+" and country of export = "+exportC, 
						"The message [Country of Origin/Exportation Mismatch] should display",
						"Not as expected", "VP", "fail", "Diff - "+ originC+"_"+ exportC);
			}
		}
		
		return checked;
	}

}
