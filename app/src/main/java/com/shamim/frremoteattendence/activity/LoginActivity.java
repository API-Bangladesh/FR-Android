package com.shamim.frremoteattendence.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity  implements InternetCheck {
    private final String TAG="LoginActivity";
    private EditText employee_ID,userPassword;
    TextView forgottenPassword;
    private final String tokenCheck_URL="https://frapi.apil.online/employee_permission/check";
    String e_ID,pass;
    CustomDialog customDialog;
    private CheckBox checkBoxRememberMe;
    private Permission permissionHandler;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        employee_ID =findViewById(R.id.employee_ID);
        userPassword=findViewById(R.id.userpass);
        Button user_LoginBtn = findViewById(R.id.user_loginbtn);
        forgottenPassword =findViewById(R.id.forgotten_password);
        checkBoxRememberMe=findViewById(R.id.checkBoxRememberMe);

        customDialog= new CustomDialog(this);
        permissionHandler=new Permission();
        user_LoginBtn.setOnClickListener(view -> {
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

        });

        checkBoxRememberMe.setOnClickListener(view -> {
            boolean isChecked = checkBoxRememberMe.isChecked();
            e_ID = employee_ID.getText().toString().trim();
            pass=userPassword.getText().toString().trim();
            if (isChecked) {
                if (e_ID.isEmpty() )
                {
                    Toast.makeText(LoginActivity.this, "Enter User ID", Toast.LENGTH_SHORT).show();
                    checkBoxRememberMe.setChecked(false);
                }
                else
                {
                    FR_sharedpreference.Companion.setRememberData(getApplicationContext(),e_ID);
                    checkBoxRememberMe.setChecked(true);
                }
            }
            else {
                        String rememberData=FR_sharedpreference.Companion.getRememberData(getApplicationContext());
                if (rememberData !=null){
                    FR_sharedpreference.Companion.Remove_RememberData(getApplicationContext());
                }
                checkBoxRememberMe.setChecked(false);
            }

        });

        forgottenPassword.setOnClickListener(view -> {
            Intent forgottenIntent=new Intent(LoginActivity.this,Forgotten_password_Activity.class);
            startActivity(forgottenIntent);
            finish();
        });
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
        String loginURL = "https://frapi.apil.online/employee_permission/login";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, loginURL, postData, response -> {
            // Get the value of the 'token' key
            customDialog.dismiss();
            try {

                JSONObject jsonObject = new JSONObject(String.valueOf(response));
                if (jsonObject.has("token")) {

                    String token = jsonObject.getString("token");
                    Log.d(TAG, "onResponse token: "+token);
                    FR_sharedpreference.Companion.RemoveToken(LoginActivity.this);
                    FR_sharedpreference.Companion.setLoginToken(LoginActivity.this,token);

                    JSONArray allowedLocationsArray = response.getJSONArray("allowed_locations");
                    Log.d(TAG, "allowedLocationsArray: "+allowedLocationsArray.length());

                    if (jsonObject.has("allowed_locations")) {
                        String jsonArrayString = allowedLocationsArray.toString();
                        FR_sharedpreference.Companion.setallowed_locations(jsonArrayString,LoginActivity.this);
                       Intent loginIntent=new Intent(LoginActivity.this,Fragment_Changer_Activity.class);
                       startActivity(loginIntent);
                       finish();
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


        }, error -> {
            error.printStackTrace();

            if (error instanceof NetworkError) {

                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError) {
                Toast.makeText(LoginActivity.this, "Server Problem", Toast.LENGTH_SHORT).show();

            } else if (error instanceof AuthFailureError) {
                Toast.makeText(LoginActivity.this, "Network AuthFailureError", Toast.LENGTH_SHORT).show();

            } else if (error instanceof ParseError) {
                Toast.makeText(LoginActivity.this, "Network ParseError", Toast.LENGTH_SHORT).show();

            } else if (error instanceof TimeoutError) {
                Toast.makeText(LoginActivity.this, "Oops. Timeout !", Toast.LENGTH_LONG).show();
            }

            customDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
        });
        requestQueue.add(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void tokenCheck(String accesstoken)
    {

        customDialog.startLoading("please wait");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tokenCheck_URL, null, response -> {
            Log.i("onResponse", response.toString());

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(String.valueOf(response));
                boolean access;
                if (jsonObject.has("Access"))
                {
                    access = jsonObject.optBoolean("Access",false);
                    if (access)
                    {
                        Intent intent = new Intent(LoginActivity.this,Fragment_Changer_Activity.class);
                        startActivity(intent);
                        finish();
                    }
                    if (customDialog !=null)
                    {
                        customDialog.dismiss();
                    }
                } else {
                    customDialog.dismiss();

                }

            } catch (JSONException e) {
                Log.d(TAG, "RuntimeException: " +e.getMessage());

                throw new RuntimeException(e);
            }

        }, error -> {
            error.printStackTrace();

            if (error instanceof NetworkError) {

                Toast.makeText(LoginActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            } else if (error instanceof ServerError) {
                Toast.makeText(LoginActivity.this, "Server Problem", Toast.LENGTH_SHORT).show();

            } else if (error instanceof AuthFailureError) {
                Toast.makeText(LoginActivity.this, "Network AuthFailureError", Toast.LENGTH_SHORT).show();

            } else if (error instanceof ParseError) {
                Toast.makeText(LoginActivity.this, "Network ParseError", Toast.LENGTH_SHORT).show();

            } else if (error instanceof TimeoutError) {
                Toast.makeText(LoginActivity.this, "Oops. Timeout !", Toast.LENGTH_LONG).show();
            }

            customDialog.dismiss();
            Toast.makeText(LoginActivity.this, "Error:"+error, Toast.LENGTH_SHORT).show();
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // Basic Authentication
                //String auth = "Basic " + Base64.encodeToString(CONSUMER_KEY_AND_SECRET.getBytes(), Base64.NO_WRAP);

                headers.put("Authorization", "Bearer " + accesstoken);
                return headers;
            }

        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
        request.setRetryPolicy(new DefaultRetryPolicy(6000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void checkDATA()
    {
        String loginCheckToken= FR_sharedpreference.Companion.getLoginToken(this);
        String rememberData=FR_sharedpreference.Companion.getRememberData(getApplicationContext());
        if (loginCheckToken != null) {
            if (!loginCheckToken.equals(""))
            {
                Log.d(TAG, "check Token: "+loginCheckToken);
                assert rememberData != null;
                if (!rememberData.equals(""))
                {
                    checkBoxRememberMe.setChecked(true);
                    employee_ID.setText(rememberData);
                }
                tokenCheck(loginCheckToken);

            }
            else
            {
                assert rememberData != null;
                if (!rememberData.equals(""))
                {
                    employee_ID.setText(rememberData);
                    checkBoxRememberMe.setChecked(true);
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(Build.VERSION.SDK_INT <= 30)
        {
            Permission.checkCameraPermission_API_LEVEL_30(this);
            //checkStoragePermission_API_LEVEL_30();
            checkDATA();


        }
        else if (Build.VERSION.SDK_INT == 31)
        {
            Permission.checkCameraPermission_API_LEVEL_30(this);
            checkDATA();

        }
        else  if (Build.VERSION.SDK_INT >= 32)
        {
            Permission.checkCameraPermission_API_LEVEL_32(this);
            //checkStoragePermission_API_LEVEL_32();
            checkDATA();

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
}
