package com.shamim.frremoteattendence.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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
import com.shamim.frremoteattendence.sharedpreference.FR_sharedpreference;
import com.shamim.frremoteattendence.utils.InternetCheck_Class;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Forgotten_password_Activity extends AppCompatActivity implements View.OnClickListener, InternetCheck {
    private String TAG="Forgotten_password_Activity";
    EditText forgottenEmail,forgottenCode;
    Button forgotten_submitEmailBtn,forgotten_submitCodeBtn;
    ScrollView scrollViewEmail,scrollViewCode;
    CustomDialog customDialog;
    private String forgottenPasswordUrl="https://frapi.apil.online/employee_permission_reset/forget_password";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        forgottenEmail=findViewById(R.id.forgottenEmail);
        forgottenCode=findViewById(R.id.forgottenCode);
        forgotten_submitEmailBtn=findViewById(R.id.forgotten_submitEmailBtn);
        forgotten_submitCodeBtn=findViewById(R.id.forgotten_submitCodeBtn);

        scrollViewEmail=findViewById(R.id.sendEmailForgottenLayout);
        scrollViewCode=findViewById(R.id.sendCodeForgottenLayout);
        customDialog=new CustomDialog(this);

        forgotten_submitEmailBtn.setOnClickListener(this);
        forgotten_submitCodeBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.forgotten_submitEmailBtn:
                String email=forgottenEmail.getText().toString().trim();
                if (email.isEmpty())
                {
                    Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
               else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(this, "Please Give Valid Email", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if (InternetCheck_Class.isNetworkConnected(this))
                    {
                        customDialog.startLoading("Sending Code...");

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", email);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, forgottenPasswordUrl, postData, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                customDialog.dismiss();
                                Log.d(TAG, "onResponse: "+String.valueOf(response.toString()));
                                try {
                                    JSONObject jsonObject = new JSONObject(String.valueOf(response));

                                    boolean access = false;
                                    Log.d(TAG,"Access"+jsonObject.has("Access"));

                                    if (jsonObject.has("Access"))
                                    {
                                        scrollViewEmail.setVisibility(View.VISIBLE);
                                        scrollViewCode.setVisibility(View.GONE);
                                        Toast.makeText(Forgotten_password_Activity.this, "Email is not registered", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        scrollViewEmail.setVisibility(View.GONE);
                                        Toast.makeText(Forgotten_password_Activity.this, "Successfully Send", Toast.LENGTH_SHORT).show();
                                        scrollViewCode.setVisibility(View.VISIBLE);


                                    }




//                                    if (jsonObject.has("message"))
//                                    {
//                                        String message=jsonObject.getString("message");
//
//                                        Log.d(TAG,"message"+message);
//
//                                        if (!access) {
//                                            // "Access" is false in the JSON response
//                                            Log.d(TAG, "onResponse: Access is false");
//                                            scrollViewEmail.setVisibility(View.VISIBLE);
//                                            scrollViewCode.setVisibility(View.GONE);
//                                            Toast.makeText(Forgotten_password_Activity.this, "Email is not registered", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            // "Access" is true in the JSON response
//                                            Log.d(TAG, "onResponse: Access is true");
//                                            scrollViewEmail.setVisibility(View.GONE);
//                                            Toast.makeText(Forgotten_password_Activity.this, "Successfully Send", Toast.LENGTH_SHORT).show();
//                                            scrollViewCode.setVisibility(View.VISIBLE);
//                                        }
//                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle errors here
                                // ...
                            }
                        });

// Add the request to the request queue
                        requestQueue.add(jsonObjectRequest);


                    }
                    else
                    {

                        InternetCheck_Class.openInternetDialog(this, true, this);

                    }



                }
                break;
        }
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onRetry() {

        if (InternetCheck_Class.isNetworkConnected(this))
        {

        }
        else
        {

            InternetCheck_Class.openInternetDialog(this, true, this);

        }
    }
}