package ServiceLibs;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.io.BufferedReader;
 
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;
import org.json.JSONException;

public class APITools {
	
	//static HttpPost httpPost;
	private static String REST_ENDPOINT = "/services/data" ;
	private static String API_VERSION = "/v45.0" ;
	private static String baseUri;
	private static Header oauthHeader;
	private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint", "1");
	private static String OrderStatus,OrderName,OrderNumber,OrderID,OrderBillingCity;
	private static LinkedHashMap  <String, String> recordTpe = new LinkedHashMap<String, String>();
	//static HttpClient httpclient=  HttpClientBuilder.create().build();
	
	/**
	 * This function is used to fetch access Token from salesForce application
	 * @param url: base url
	 * @param grantService, granted service
	 * @param clientId, client id
	 * @param clientSecret, client secret
	 * @param userName user name
	 * @param password,password
	 * @return access token access token from salesforce
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static String getAccesToken(  String url,
										 String grantService,
										 String grantType,
										 String clientId,
										 String clientSecret,
										 String userName,
										 String password) throws Exception
	{
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = null;
		int statusCode;
		String loginURL = url +	grantService +"="+ grantType+
		"&client_id=" + clientId +
		"&client_secret=" + clientSecret +
		"&username=" + userName +
		"&password=" + password;
		System.out.print(loginURL);
		httpPost = new HttpPost();
		httpPost = new HttpPost(loginURL);
		HttpResponse response = null;
		try {
			// Execute the login POST request
			response = httpClient.execute(httpPost);
		}
		catch (ClientProtocolException cpException)
		{
			cpException.printStackTrace();
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
		statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK)
		{
			System.out.println("Error authenticating to Force.com: "+statusCode);
		// Error is in EntityUtils.toString(response.getEntity())
			return "Nothing";
		}
		String getResult = null;
		try
		{
			getResult = EntityUtils.toString(response.getEntity());
		} catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
		JSONObject jsonObject = null;
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		try
		{
		jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
		loginAccessToken = jsonObject.getString("access_token");
		loginInstanceUrl = jsonObject.getString("instance_url");
		}
		catch (JSONException jsonException)
		{
		jsonException.printStackTrace();
		}
		oauthHeader = new BasicHeader("Authorization", "OAuth " + loginAccessToken) ;
		System.out.println(response.getStatusLine());
		System.out.println("Successful login");
		System.out.println("instance URL: "+loginInstanceUrl);
		System.out.println("access token/session ID: "+loginAccessToken);
		baseUri = loginInstanceUrl + REST_ENDPOINT + API_VERSION ;
		System.out.println("baseUri: "+ baseUri); 
		// release connection
		if(httpPost!=null)
        httpPost.releaseConnection();
        if(httpClient!=null)
        httpClient.getConnectionManager().shutdown();
		return loginAccessToken;
	
	}
	
	/**
	 * This function is used to fetch access Token from salesForce application
	 * @param query: query
	 * @return JSON object
	 * @throws Exception
	 */
     public static JSONObject getRecordObject(String query) 
     {
        System.out.println("\n_______________ Query to return record from Object _______________");
        JSONObject jsonObject = null;
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        try {
        	//query = "Select+Name,+Petition_Outcome__c+From+Petition__c+Where+Name='P-05375'";
        	//query = "SELECT+Name+Petition_Outcome__c+From+Petition__c+Where+Name='P-05375'";
            //Set up the HTTP objects needed to make the request.
            httpClient = HttpClientBuilder.create().build();
            String uri = baseUri + "/query?q="+query;
            System.out.println("Query URL: " + uri);
            httpGet = new HttpGet(uri);
            System.out.println("oauthHeader2: " + oauthHeader);
            httpGet.addHeader(oauthHeader);
            httpGet.addHeader(prettyPrintHeader);
            // Make the request.
            HttpResponse response = httpClient.execute(httpGet);
            // Process the result
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                String responseString = EntityUtils.toString(response.getEntity());
                try {
                    JSONObject json = new JSONObject(responseString);
                    System.out.println("JSON result of Query:\n" + json.toString(1));
                    JSONArray jsonArray = json.getJSONArray("records");
                    jsonObject = jsonArray.getJSONObject(0);
                    System.out.println(jsonObject.get("Name"));
                    
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            } else {
                System.out.println("Query was unsuccessful. Status code returned is " + statusCode);
                System.out.println("An error has occured. Http status: " + response.getStatusLine().getStatusCode());
                System.out.println(getBody(response.getEntity().getContent()));
                return jsonObject;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        if(httpGet!=null)
        httpGet.releaseConnection();
        if(httpClient!=null)
        httpClient.getConnectionManager().shutdown();
        return jsonObject;
	 }
  	/**
  	 * This function is used to create record in any object
  	 * @param apiObjectName: API Object Name
  	 * @param idToReturn: the id of the created record
  	 * @param rowRec, Map containing fields name/value of the record
  	 * to be created
  	 * @return  id of the record created.
  	 * @return JSON object
  	 * @throws Exception
  	 */
 	 public static String createRecordObject(String apiObjectName, 
 											 LinkedHashMap<String, String> rowRec) 
 	 {
         System.out.println("\n_______________ INSERT Record to "+apiObjectName+" _______________");
         String uri = baseUri + "/sobjects/"+apiObjectName+"/";
         System.out.println("uri" +uri);
         String returnId = null;
         HttpClient httpClient = null;
         HttpPost httpPost = null;
         try {
             //create the JSON object containing the new lead details. "A-209-464"
             JSONObject jsonObject = new JSONObject();
            for (Entry<String, String> entry : rowRec.entrySet())  
             {
                 System.out.println("Key =  " + entry.getKey() + 
                                  ", Value = " + entry.getValue()); 
                 jsonObject.put(entry.getKey(), entry.getValue());
             }
              /*jsonObject.put("Segment_Outcome__c", "Deficient");//"ADCVD_Case__c":"A-070-707"
            jsonObject.put("ADCVD_Case__c", "a3fr00000009NzLAAU");
             jsonObject.put("Initiation_Extension_of_days__c", "3");//"ADCVD_Case__c":"A-070-707"
             jsonObject.put("Petition_Filed__c", "2019-03-16");*/
             System.out.println("JSON for record to be inserted:\n" + jsonObject.toString(1));
             //Construct the objects needed for the request
             httpClient = HttpClientBuilder.create().build();
             httpPost = new HttpPost(uri);
             httpPost.addHeader(oauthHeader);
             httpPost.addHeader(prettyPrintHeader);
             // The message we are going to post
             StringEntity body = new StringEntity(jsonObject.toString(1));
             body.setContentType("application/json");
             httpPost.setEntity(body);
             //Make the request
             HttpResponse response = httpClient.execute(httpPost);
             //Process the results
             int statusCode = response.getStatusLine().getStatusCode();
             if (statusCode == 201) {
                 String response_string = EntityUtils.toString(response.getEntity());
                 JSONObject json = new JSONObject(response_string);
                 // Store the retrieved lead id to use when we update the lead.
                 returnId = json.getString("id");
             } else {
                 System.out.println("Insertion unsuccessful. Status code returned is " + statusCode);
             }
         } catch (JSONException e) {
             System.out.println("Issue creating JSON or processing results");
             e.printStackTrace();
         } catch (IOException ioe) {
             ioe.printStackTrace();
         } catch (NullPointerException npe) {
             npe.printStackTrace();
         }
         if(httpPost!=null)
         httpPost.releaseConnection();
         if(httpClient!=null)
         httpClient.getConnectionManager().shutdown();
         return returnId;
     }
 	/**
 	 * This function is used to create record in any object
 	 * @param apiObjectName: API Object Name
 	 * @param idToReturn: the id of the created record
 	 * @param rowRec, Map containing fields name/value of the record
 	 * to be created
 	 * @return  id of the record created.
 	 * @return JSON object
 	 * @throws Exception
 	 */
	 public static String updateRecordObject(String apiObjectName,
			 								 String recordId, 
											 LinkedHashMap<String, String> rowRec) 
	 {
        System.out.println("\n_______________ INSERT Record to "+apiObjectName+" _______________");
        String uri = baseUri + "/sobjects/"+apiObjectName+"/"+recordId;
        System.out.println("uri" +uri);
        String returnId = null;
        HttpClient httpClient = null;
        HttpPatch httpPatch = null;
        try {
            //create the JSON object containing the new lead details. "A-209-464"
            JSONObject jsonObject = new JSONObject();
           for (Entry<String, String> entry : rowRec.entrySet())  
            {
                System.out.println("Key =  " + entry.getKey() + 
                                 ", Value = " + entry.getValue()); 
                jsonObject.put(entry.getKey(), entry.getValue());
            }
           // jsonObject.put("Segment_Outcome__c", "Deficient");//"ADCVD_Case__c":"A-070-707"
           /* jsonObject.put("ADCVD_Case__c", "a3fr00000009NzLAAU");
            jsonObject.put("Initiation_Extension_of_days__c", "3");//"ADCVD_Case__c":"A-070-707"
            jsonObject.put("Petition_Filed__c", "2019-03-16");*/
            System.out.println("JSON for record to be inserted:\n" + jsonObject.toString(1));
            //Construct the objects needed for the request
            httpClient = HttpClientBuilder.create().build();
            httpPatch = new HttpPatch(uri);
            httpPatch.addHeader(oauthHeader);
            httpPatch.addHeader(prettyPrintHeader);
            // The message we are going to post
            StringEntity body = new StringEntity(jsonObject.toString(1));
            body.setContentType("application/json");
            httpPatch.setEntity(body);
            //Make the request
            HttpResponse response = httpClient.execute(httpPatch);
            //Process the results
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 204) {
            	returnId = statusCode+"";
                /*String response_string = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(response_string);
                // Store the retrieved lead id to use when we update the lead.
                returnId = json.getString("id");*/
            } else {
                System.out.println("Insertion unsuccessful. Status code returned is " + statusCode);
            }
        } catch (JSONException e) {
            System.out.println("Issue creating JSON or processing results");
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        if(httpPatch!=null)
        httpPatch.releaseConnection();
        if(httpClient!=null)
        httpClient.getConnectionManager().shutdown();
        return returnId;
    } 
	 
	 
	     
    private static String getBody(InputStream inputStream) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream)
            );
            String inputLine;
            while ( (inputLine = in.readLine() ) != null ) {
                result += inputLine;
                result += "\n";
            }
            in.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }
    
   

   
}