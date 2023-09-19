package com.shamim.frremoteattendence.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.alert.CustomDialog;
import com.shamim.frremoteattendence.interfaces.InternetCheck;
import com.shamim.frremoteattendence.permission.Permission;
import com.shamim.frremoteattendence.sharedpreference.FR_sharedpreference;
import com.shamim.frremoteattendence.utils.InternetCheck_Class;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity  implements InternetCheck {
    private final String TAG="LoginActivity";
    private EditText employee_ID,userPassword;
    private final String loginURL="https://frapi.apil.online/employee_permission/login";
    private final String tokenCheck_URL="https://frapi.apil.online/employee_permission/check";
    private Button user_LoginBtn;
    String e_ID,pass;
    CustomDialog customDialog;
    private TextView user_SignupText;
    private CheckBox checkBoxRememberMe;
    private Permission permissionHandler;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        employee_ID =findViewById(R.id.employee_ID);
        userPassword=findViewById(R.id.userpass);
        user_LoginBtn=findViewById(R.id.user_loginbtn);
        user_SignupText=findViewById(R.id.user_signupText);
        checkBoxRememberMe=findViewById(R.id.checkBoxRememberMe);
        customDialog= new CustomDialog(this);
        permissionHandler=new Permission();

        String loginCheckToken= FR_sharedpreference.Companion.getLoginToken(this);
        if (!loginCheckToken.equals(""))
        {
          tokenCheck(loginCheckToken);
        }
        else
        {
            checkRememberDataSaveOrNot();
        }

        user_LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                e_ID = employee_ID.getText().toString().trim();
                pass=userPassword.getText().toString().trim();

                if (e_ID.isEmpty() )
                {
                    Toast.makeText(LoginActivity.this, "Please Enter Your ID", Toast.LENGTH_SHORT).show();
                }
                else if (pass.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    if (InternetCheck_Class.isNetworkConnected(LoginActivity.this))
                    {
                        customDialog.startLoading("Please Wait");
                        readLoginData();
                    }
                    else
                    {

                        InternetCheck_Class.openInternetDialog(LoginActivity.this, true, LoginActivity.this);

                    }
                }

            }
        });


        checkBoxRememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = checkBoxRememberMe.isChecked();
                e_ID = employee_ID.getText().toString().trim();
                pass=userPassword.getText().toString().trim();

                if (isChecked) {
                    if (e_ID.isEmpty() )
                    {
                        Toast.makeText(LoginActivity.this, "Enter UserName", Toast.LENGTH_SHORT).show();
                        checkBoxRememberMe.setChecked(false);
                    }
                    else
                    {
                        FR_sharedpreference.Companion.setRememberData(getApplicationContext(), e_ID,pass);
                        FR_sharedpreference.Companion.setCheckRememberData(getApplicationContext(),true);
                    }
                }
                else {

                    FR_sharedpreference.Companion.Remove_RememberData(getApplicationContext());
                    FR_sharedpreference.Companion.setCheckRememberData(getApplicationContext(),false);
                }

            }
        });
    }

    private void checkRememberDataSaveOrNot()
    {
        boolean data=  FR_sharedpreference.Companion.getCheckRememberData(getApplicationContext());

        if (data)
        {
            checkBoxRememberMe.setChecked(true);
            employee_ID.setText(FR_sharedpreference.Companion.getRememberData(getApplicationContext()));
        }

    }

    private void readLoginData()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("E_ID", e_ID);
            postData.put("password", pass);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginURL, postData, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Get the value of the 'token' key
                customDialog.dismiss();
                try {

                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                    if (jsonObject.has("token")) {
                        String token = jsonObject.getString("token");
                        FR_sharedpreference.Companion.setLoginToken(LoginActivity.this,token);

                        JSONArray allowedLocationsArray = response.getJSONArray("allowed_locations");
                        Log.d(TAG, "allowedLocationsArray: "+allowedLocationsArray.length());

                            if (jsonObject.has("allowed_locations")) {
                                String jsonArrayString = allowedLocationsArray.toString();
                                FR_sharedpreference.Companion.setallowed_locations(jsonArrayString,LoginActivity.this);

                                Log.d(TAG, "valueArray: "+jsonArrayString);

                            }

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Wrong userName or Password", Toast.LENGTH_SHORT).show();
                    }
                }

                catch (Exception e) {
                    Log.d(TAG, "Response Error  =" + e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                if (error instanceof NetworkError) {

                    Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(LoginActivity.this, "Server Problem", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(LoginActivity.this, "Network AuthFailureError", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(LoginActivity.this, "Network ParseError", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(LoginActivity.this, "Network NoConnectionError", Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, "Oops. Timeout !", Toast.LENGTH_LONG).show();
                }

                customDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
        Log.d(TAG, "Request Send Time:" + requestQueue);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void tokenCheck(String accesstoken)
    {
        customDialog.startLoading("please wait");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tokenCheck_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                Log.i("onResponse", response.toString());

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(String.valueOf(response));
                    String access = null;

                    if (jsonObject.has("Access")) {
                        access = jsonObject.getString("Access");
                        Intent intent = new Intent(LoginActivity.this,Fragment_Changer_Activity.class);
                        startActivity(intent);
                        customDialog.dismiss();
                        finish();
                        Log.d(TAG, "access: " + access);
                    } else {
                        Log.d(TAG, "not access: " + access);
                        customDialog.dismiss();

                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                Log.e("onErrorResponse", error.toString());
                customDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + accesstoken);
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT <= 30)
        {
            Permission.checkCameraPermission_API_LEVEL_30(this);
            //checkStoragePermission_API_LEVEL_30();

        }
        else if (Build.VERSION.SDK_INT == 31)
        {
            Permission.checkCameraPermission_API_LEVEL_30(this);

        }
        else  if (Build.VERSION.SDK_INT >= 32)
        {
            Permission.checkCameraPermission_API_LEVEL_32(this);
            //checkStoragePermission_API_LEVEL_32();

        }
        else
        {
            Toast.makeText(this, "Can Not Open\n Need API LEVEL at least 29", Toast.LENGTH_SHORT).show();
            // do something for phones running an SDK before lollipop
        }
        Permission.gpsPermission(this);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHandler.handlePermissionResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onSuccess() {

    }

    @Override
    public void onCancel() {
        if (InternetCheck_Class.isNetworkConnected(this))
        {

        }
        else
        {

            InternetCheck_Class.openInternetDialog(this, true, this);

        }
    }

    @Override
    public void onRetry()
    {
        if (InternetCheck_Class.isNetworkConnected(this))
        {

        }
        else
        {

            InternetCheck_Class.openInternetDialog(this, true, this);

        }
    }
    private static class FetchDataFromUrlTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            if (urls.length == 0) {
                return null;
            }
            String urlString = urls[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseData = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    responseData = responseBuilder.toString();
                } else {
                    Log.e("HTTP GET", "Error response code: " + responseCode);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return responseData;
        }

        @Override
        protected void onPostExecute(String responseData) {
            super.onPostExecute(responseData);

            if (responseData != null) {
                try {
                    JSONArray jsonArray = new JSONArray(responseData);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject locationObject = jsonArray.getJSONObject(i);

                        // Retrieve individual data fields
                        int id = locationObject.getInt("ID");
                        String latitude = locationObject.getString("latitude");
                        String longitude = locationObject.getString("longitude");
                        String locationName = locationObject.getString("location_name");
                        String user = locationObject.getString("User");
                        String eId = locationObject.getString("E_ID");

                        // Now you have the data for each location
                        // You can use this data as needed
                        Log.d("Location Data", "ID: " + id);
                        Log.d("Location Data", "Latitude: " + latitude);
                        Log.d("Location Data", "Longitude: " + longitude);
                        Log.d("Location Data", "Location Name: " + locationName);
                        Log.d("Location Data", "User: " + user);
                        Log.d("Location Data", "E_ID: " + eId);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the case where there was no response or an error occurred
                Log.e("HTTP GET", "No response or error occurred");
            }
        }
    }

}
