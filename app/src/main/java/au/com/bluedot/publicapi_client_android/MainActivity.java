package au.com.bluedot.publicapi_client_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * @author Bluedot Innovation
 * Copyright (c) 2016 Bluedot Innovation. All rights reserved.
 * The code samples demonstrates the use of requests with accompanying JSON body data.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final static String DEBUG_TAG = "MainActivity";

    // This is the customer API key
    // https://www.pointaccess.bluedot.com.au/pointaccess-v1/dashboard.html -> Account Management -> Edit Profile
    private final static String CUSTOMER_API_KEY = "";

    private TextView tvLogView;
    private Button bPostApp; //post App
    private Button bPostZone; //post Zone
    private Button bPostFence; //post Fen
    private Button bPostMessageAction; //post Message Action
    private Button bDeleteFence;    //delete Fence
    private Button bPostCustomAction;   //post Custom Action with Custom Data

    private String lastApiKey; // the key returned from backend for last created app; will be required to add zones
    private String zoneId= null; // Zone Id of the create Zone
    private String fenceId = null;  //Fence Id of the created fence

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLogView = (TextView) findViewById(R.id.tvLogView);
        bPostApp = (Button) findViewById(R.id.bPostApp);
        bPostZone = (Button) findViewById(R.id.bPostZone);
        bPostFence = (Button) findViewById(R.id.bPostFence);
        bPostMessageAction = (Button) findViewById(R.id.bPostMessageAction);
        bDeleteFence = (Button) findViewById(R.id.bDeleteFence);
        bPostCustomAction = (Button) findViewById(R.id.bPostCustomAction);
        bPostApp.setOnClickListener(this);
        bPostZone.setOnClickListener(this);
        bPostFence.setOnClickListener(this);
        bPostMessageAction.setOnClickListener(this);
        bDeleteFence.setOnClickListener(this);
        bPostCustomAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bPostApp:
                doPostApplication();
                break;
            case R.id.bPostZone:
                doPostZone();
                break;
            case R.id.bPostFence:
                doPostFence();
                break;
            case R.id.bPostMessageAction:
                doPostMessageAction();
                break;
            case R.id.bDeleteFence:
                doDeleteFence();
                break;
            case R.id.bPostCustomAction:
                doPostCustomAction();
                break;

        }
    }

    /**
     * Get Application API key from success JSON response of create Application
     * @param previousRequestResult
     * @return API key value
     */
    private String getApiKeyFromPreviousRequestResult(String previousRequestResult) {
        String result = null;
        try{
            JSONObject jsonObject = new JSONObject(previousRequestResult);
            result = jsonObject.getString("apiKey");
        } catch (JSONException e){

        }
        return result;
    }

    /**
     * Get Zone ID from success JSON response of create Zone
     * @param result
     * @return Zone ID value
     */
    private String getZoneIdFromResult(String result) {
        String Id = null;
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.has("zoneId")){
                Id = jsonObject.getString("zoneId");
            }
        } catch (JSONException e){

        }
        return Id;
    }

    /**
     * Get Fence ID from success JSON response of create Fence
     * @param result
     * @return Fence ID value
     */
    private String getFenceIdFromResult(String result) {
        String Id = null;
        try{

            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.has("fencesUpdated")){
                JSONArray jsonArray = jsonObject.getJSONArray("fencesUpdated");
                Id = jsonArray.getJSONObject(0).getString("fenceId");
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return Id;
    }

    /**
     * Performs task of creating new Application
     */
    private void doPostApplication(){
        executePostApplication(CUSTOMER_API_KEY);
    }

    /**
     * Performs task of creating new Zone
     */
    private void doPostZone(){
        if (lastApiKey != null){
            executePostZone(CUSTOMER_API_KEY, lastApiKey);
        } else {
            tvLogView.setText("Error: apiKey not found");
        }
    }

    /**
     * Performs task of creating new Fence
     */
    private void doPostFence(){
        if(lastApiKey!=null){
            if(zoneId!=null){
                executePostFence(CUSTOMER_API_KEY, lastApiKey, zoneId);
            }
            else{
                tvLogView.setText("Error: zoneId not found");
            }
        }
        else{
            tvLogView.setText("Error: apiKey not found");
        }

    }

    /**
     * Performs task of creating new Message Action
     */
    private void doPostMessageAction(){
        if(lastApiKey!=null){
            if(zoneId!=null){
                executePostMessageAction(CUSTOMER_API_KEY,lastApiKey,zoneId);
            }
            else{
                tvLogView.setText("Error: zoneId not found");
            }
        }
        else{
            tvLogView.setText("Error: apiKey not found");
        }
    }

    /**
     * Performs task of creating new Custom Action with Custom Data
     */
    private void doPostCustomAction() {
        if(lastApiKey!=null){
            if(zoneId!=null){
                executePostCustomAction(CUSTOMER_API_KEY,lastApiKey,zoneId);
            }
            else{
                tvLogView.setText("Error: zoneId not found");
            }
        }
        else{
            tvLogView.setText("Error: apiKey not found");
        }
    }

    /**
     * Performs task of deleting fence
     */
    private void doDeleteFence(){
        if(zoneId!=null){
            if(fenceId!=null){
                executeDeleteFence(CUSTOMER_API_KEY,lastApiKey,zoneId,fenceId);
            }
            else{
                tvLogView.setText("Error: fenceId not found");
            }
        }else{
            tvLogView.setText("Error: zoneId not found");
        }
    }

    /**
     * Handles result responses
     * @param result
     */
    private void handleResult(String result){
        tvLogView.setText(result);
        // now try to see if any of API keys are updated
        if (lastApiKey == null){
            lastApiKey = getApiKeyFromPreviousRequestResult(result);
        }

        //try to get zone Id
        if(zoneId == null){
            zoneId = getZoneIdFromResult(result);
        }

        if(fenceId == null){
            fenceId = getFenceIdFromResult(result);
        }
    }




    //===============================================================
    //====== The part below is going to documentation examples ======
    //========== Must be modular and working in isolation ===========
    //===============================================================

    // The PUBLICAPI Urls
    private final static String PUBLICAPI_URL_APPLICATIONS = "https://api.bluedotinnovation.com/1/applications";
    private final static String PUBLICAPI_URL_ZONES = "https://api.bluedotinnovation.com/1/zones";
    private final static String PUBLICAPI_URL_FENCE = "https://api.bluedotinnovation.com/1/fences";
    private final static String PUBLICAPI_URL_MESSAGE_ACTION = "https://api.bluedotinnovation.com/1/actions";

    /**
     * The following code shows how to form the JSON request for POST Application
     * @param customerApiKey
     * @return JSON String
     */
    private String generatePOSTApplicationRequest(String customerApiKey){
        String result = null;
        String appName = "Example Application";
        String packageName = "bluedotinnovation.com.publicapi.android.examples.com";
        // form the JSON string for request
        try{
            JSONObject applicationObject = new JSONObject();
            applicationObject.put("name", appName);
            applicationObject.put("packageName", packageName);
            applicationObject.put("nextRuleUpdateIntervalFormatted", "10:00");
            JSONObject contentObject = new JSONObject();
            contentObject.put("application", applicationObject);
            JSONObject securityObject = new JSONObject();
            securityObject.put("customerApiKey", customerApiKey);
            JSONObject resultObject = new JSONObject();
            resultObject.put("security", securityObject);
            resultObject.put("content", contentObject);
            result = resultObject.toString();
        } catch (JSONException e){
            Log.e(DEBUG_TAG, "generatePOSTApplicationRequest(): " + e );
        }
        return result;
    }

    /**
     * The following code shows how to form the JSON request for POST Zone
     * @param customerApiKey
     * @param apiKey
     * @return JSON String
     */
    private String generatePOSTZoneRequest(String customerApiKey, String apiKey){
        String result = null;
        String zoneName = "Example Zone";
        // form the JSON string for request
        try{
            JSONObject fromObject = new JSONObject();
            fromObject.put("time", "09:01");
            fromObject.put("period", "am");
            JSONObject toObject = new JSONObject();
            toObject.put("time", "11:59");
            toObject.put("period", "am");
            JSONObject timeActiveObject = new JSONObject();
            timeActiveObject.put("from", fromObject);
            timeActiveObject.put("to", toObject);
            JSONObject zoneObject = new JSONObject();
            zoneObject.put("zoneName", zoneName);
            zoneObject.put("minimumRetriggerTime", "00:06");
            zoneObject.put("timeActive", timeActiveObject);
            JSONObject contentObject = new JSONObject();
            contentObject.put("zone", zoneObject);
            JSONObject securityObject = new JSONObject();
            securityObject.put("apiKey", apiKey);
            securityObject.put("customerApiKey", customerApiKey);
            JSONObject resultObject = new JSONObject();
            resultObject.put("security", securityObject);
            resultObject.put("content", contentObject);
            result = resultObject.toString();
        } catch (JSONException e){
            Log.e(DEBUG_TAG, "generatePOSTZoneRequest(): " + e );
        }
        return result;
    }

    /**
     * The following code shows how to form the JSON request for POST Fence
     * @param customerApiKey
     * @param apiKey
     * @param zoneId
     * @return JSON String
     */
    private String generatePOSTFenceRequest(String customerApiKey, String apiKey, String zoneId){
        String result = null;
        // form the JSON string for request
        try{
            JSONObject centerObject = new JSONObject();
            centerObject.put("latitude", "-37.8159544565362");
            centerObject.put("longitude", "144.9723565578461");
            JSONObject circleObject = new JSONObject();
            circleObject.put("name", "Test Circular fence with 8M radius");
            circleObject.put("color", "#000fff");
            circleObject.put("radius", "8");
            circleObject.put("center",centerObject);
            JSONArray circlesArray = new JSONArray();
            circlesArray.put(circleObject);
            JSONObject fencesObject = new JSONObject();
            fencesObject.put("circles", circlesArray);
            JSONObject zoneObject = new JSONObject();
            zoneObject.put("zoneId",zoneId);
            zoneObject.put("fences", fencesObject);
            JSONObject contentObject = new JSONObject();
            contentObject.put("zone", zoneObject);
            JSONObject securityObject = new JSONObject();
            securityObject.put("apiKey", apiKey);
            securityObject.put("customerApiKey", customerApiKey);
            JSONObject resultObject = new JSONObject();
            resultObject.put("security", securityObject);
            resultObject.put("content", contentObject);
            result = resultObject.toString();
        } catch (JSONException e){
            Log.e(DEBUG_TAG, "generatePOSTFenceRequest(): " + e );
        }
        return result;
    }

    /**
     * The following code shows how to form the JSON request for POST Message Action
     * @param customerApiKey
     * @param apiKey
     * @param zoneId
     * @return JSON String
     */
    private String generatePOSTMessageActionRequest(String customerApiKey, String apiKey, String zoneId){
        String result = null;
        // form the JSON string for request
        try{
            JSONObject messageActionObject = new JSONObject();
            messageActionObject.put("name", "A Message Action");
            messageActionObject.put("title", "This is a sample title");
            messageActionObject.put("message", "This is a sample message");
            JSONArray messageActionArray = new JSONArray();
            messageActionArray.put(messageActionObject);
            JSONObject actionObject = new JSONObject();
            actionObject.put("messageActions",messageActionArray);
            JSONObject zoneObject = new JSONObject();
            zoneObject.put("zoneId",zoneId);
            zoneObject.put("actions", actionObject);
            JSONObject contentObject = new JSONObject();
            contentObject.put("zone", zoneObject);
            JSONObject securityObject = new JSONObject();
            securityObject.put("apiKey", apiKey);
            securityObject.put("customerApiKey", customerApiKey);
            JSONObject resultObject = new JSONObject();
            resultObject.put("security", securityObject);
            resultObject.put("content", contentObject);
            result = resultObject.toString();
        } catch (JSONException e){
            Log.e(DEBUG_TAG, "generatePOSTMessageActionRequest(): " + e );
        }
        return result;
    }


    /**
     * The following code shows how to form the JSON request for POST Custom Action with Custom Data
     * @param customerApiKey
     * @param apiKey
     * @param zoneId
     * @return JSON String
     */
    private String generatePOSTCustomActionRequest(String customerApiKey, String apiKey, String zoneId){
        String result = null;
        // form the JSON string for request
        try{
            JSONObject customActionObject = new JSONObject();
            customActionObject.put("name", "A Custom Action");
            JSONArray customFieldsJsonArray = new JSONArray();
            customActionObject.put("customFields",customFieldsJsonArray);
            String[] keys = {"type", "name", "id"};
            String[] values = {"Coffee Shop", "Blue Bottle Coffee", "48707775-4991-434b-ba3a-f41ac1236c44"};
            JSONObject customFieldJsonObject;
            for(int i = 0 ; i < keys.length ; i++) {
                customFieldJsonObject = new JSONObject();
                customFieldJsonObject.put("key", keys[i]);
                customFieldJsonObject.put("value", values[i]);
                customFieldsJsonArray.put(customFieldJsonObject);
            }
            JSONArray customActionArray = new JSONArray();
            customActionArray.put(customActionObject);
            JSONObject actionObject = new JSONObject();
            actionObject.put("customActions",customActionArray);
            JSONObject zoneObject = new JSONObject();
            zoneObject.put("zoneId",zoneId);
            zoneObject.put("actions", actionObject);
            JSONObject contentObject = new JSONObject();
            contentObject.put("zone", zoneObject);
            JSONObject securityObject = new JSONObject();
            securityObject.put("apiKey", apiKey);
            securityObject.put("customerApiKey", customerApiKey);
            JSONObject resultObject = new JSONObject();
            resultObject.put("security", securityObject);
            resultObject.put("content", contentObject);
            result = resultObject.toString();
        } catch (JSONException e){
            Log.e(DEBUG_TAG, "generatePOSTCustomActionRequest(): " + e );
        }
        return result;
    }

    /**
     * The following example shows how to form the URL for Deleting a Fence
     * @param customerApiKey
     * @param apiKey
     * @param zoneId
     * @param fenceId
     * @return Url String
     */
    private String generateDELETEFenceRequest(String customerApiKey, String apiKey, String zoneId, String fenceId){
        String url = null;
        url = PUBLICAPI_URL_FENCE + "?customerApiKey=" +customerApiKey  +"&apiKey="+ apiKey +"&zoneId="+zoneId+"&fenceId=" + fenceId;
        return url;
    }

    /**
     * Starts thread for creating a new Application
     * @param customerApiKey
     */
    private void executePostApplication(String customerApiKey){
        String postData = generatePOSTApplicationRequest(customerApiKey);
        new ExecutePostRequest().execute(new String[]{PUBLICAPI_URL_APPLICATIONS, postData} );
    }

    /**
     * Starts task for creating a new Zone
     * @param customerApiKey
     * @param apiKey
     */
    private void executePostZone(String customerApiKey, String apiKey){
        String postData = generatePOSTZoneRequest(customerApiKey, apiKey);
        new ExecutePostRequest().execute(new String[]{PUBLICAPI_URL_ZONES, postData} );
    }

    /**
     * Starts task for creating a new Fence
     * @param customerApiKey
     * @param apiKey
     * @param zoneId
     */
    private void executePostFence(String customerApiKey, String apiKey, String zoneId) {
        String postData = generatePOSTFenceRequest(customerApiKey, apiKey, zoneId);
        new ExecutePostRequest().execute(new String[]{PUBLICAPI_URL_FENCE, postData} );
    }

    /**
     * Starts task for creating a new Message Action
     * @param customerApiKey
     * @param apiKey
     * @param zoneId
     */
    private void executePostMessageAction(String customerApiKey, String apiKey, String zoneId) {
        String postData = generatePOSTMessageActionRequest(customerApiKey, apiKey, zoneId);
        new ExecutePostRequest().execute(new String[]{PUBLICAPI_URL_ACTION, postData} );
    }

    /**
     * Starts task for creating a new Custom Action with Custom Data
     * @param customerApiKey
     * @param lastApiKey
     * @param zoneId
     */
    private void executePostCustomAction(String customerApiKey, String lastApiKey, String zoneId) {
        String postData = generatePOSTCustomActionRequest(customerApiKey, lastApiKey, zoneId);
        new ExecutePostRequest().execute(new String[]{PUBLICAPI_URL_ACTION, postData} );
    }

    /**
     * Starts task for deleting a Fence
     * @param customerApiKey
     * @param apiKey
     * @param zoneId
     * @param fenceId
     */
    private void executeDeleteFence(String customerApiKey, String apiKey , String zoneId, String fenceId) {
        String postDataUrl = generateDELETEFenceRequest(customerApiKey,apiKey, zoneId, fenceId);
        new ExecuteDeleteRequest().execute(new String[]{postDataUrl} );
    }

    // AsyncTask to perform the POST communication
    private class ExecutePostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return executePost(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle result if needed
            handleResult(result);
        }
    }

    /**
     * Executes a POST request for given url and post data
     * @param targetURL
     * @param postData
     * @return result string
     */
    public String executePost(String targetURL, String postData)
    {
        URL url;
        HttpURLConnection connection = null;
        Log.d(DEBUG_TAG, "executePost(): targetURL=" + targetURL);
        Log.d(DEBUG_TAG, "executePost(): postData=" + postData);
        try {
            // Get binary data
            byte[] outputBytes = postData.getBytes();
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(outputBytes.length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            OutputStream wr = connection.getOutputStream();
            wr.write(outputBytes);
            wr.flush();
            wr.close();

            int http_status = connection.getResponseCode();
            Log.d(DEBUG_TAG, "executePost(): http_status=" + http_status);

            InputStream is;

            if (http_status==200) {
                //Get Response
                is = connection.getInputStream();

            } else {
                //Get Error Response
                is = connection.getErrorStream();

            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {
            String errorMessage =  e.toString();
            Log.d(DEBUG_TAG, "executePost(): " + errorMessage);
            return errorMessage;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }


    // AsyncTask to perform the DELETE communication
    private class ExecuteDeleteRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return executeDelete(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // Handle result if needed
            handleResult(result);
        }
    }

    /**
     * Executes a DELETE request for given url
     * @param targetURL
     * @return
     */
    public String executeDelete(String targetURL)
    {
        URL url;
        HttpURLConnection connection = null;
        Log.d(DEBUG_TAG, "executePost(): targetURL=" + targetURL);

        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);


            int http_status = connection.getResponseCode();
            Log.d(DEBUG_TAG, "executeDelete(): http_status=" + http_status);

            InputStream is;

            if (http_status==200) {
                //Get Response
                is = connection.getInputStream();

            } else {
                //Get Error Response
                is = connection.getErrorStream();

            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {
            String errorMessage =  e.toString();
            Log.d(DEBUG_TAG, "executeDelete(): " + errorMessage);
            return errorMessage;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

}
