package com.shamim.frremoteattendence.activity;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.alert.CustomDialog;
import com.shamim.frremoteattendence.interfaces.InternetCheck;
import com.shamim.frremoteattendence.utils.InternetCheck_Class;
import org.json.JSONException;
import org.json.JSONObject;

public class Forgotten_password_Activity extends AppCompatActivity implements View.OnClickListener, InternetCheck {
    private String TAG="Forgotten_password_Activity";
    EditText forgottenEmail,forgottenCode,forgotten_NewPassword;
    TextView forgotten_TimerText;
    ImageView forgotten_ReSendBtn;
    Button forgotten_submitEmailBtn,forgotten_submitCodeBtn,forgotten_submitNewPasswordBtn;
    ScrollView scrollViewEmail,scrollViewCode,scrollViewNewPassword;
    CustomDialog customDialog;
    String emailSave;
    String token;
    private String forgottenPasswordUrl="https://frapi.apil.online/employee_permission_reset/forget_password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        forgottenEmail=findViewById(R.id.forgottenEmail);
        forgottenCode=findViewById(R.id.forgottenCode);
        forgotten_NewPassword=findViewById(R.id.forgotten_Newpassword);
        forgotten_submitEmailBtn=findViewById(R.id.forgotten_submitEmailBtn);
        forgotten_submitCodeBtn=findViewById(R.id.forgotten_submitCodeBtn);
        forgotten_TimerText=findViewById(R.id.forgotten_trimerTextview);
        forgotten_ReSendBtn=findViewById(R.id.forgotten_ResendcodeBtn);
        forgotten_submitNewPasswordBtn=findViewById(R.id.forgotten_submitNewPasswordBtn);
        scrollViewEmail=findViewById(R.id.sendEmailForgottenLayout);
        scrollViewCode=findViewById(R.id.sendCodeForgottenLayout);
        scrollViewNewPassword= findViewById(R.id.sendPasswordForgottenLayout);
        customDialog=new CustomDialog(this);
        forgotten_submitEmailBtn.setOnClickListener(this);
        forgotten_submitCodeBtn.setOnClickListener(this);
        forgotten_submitNewPasswordBtn.setOnClickListener(this);
        forgotten_ReSendBtn.setOnClickListener(this);

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
                    emailSave=null;
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
                                        scrollViewNewPassword.setVisibility(View.GONE);
                                        Toast.makeText(Forgotten_password_Activity.this, "Email is not registered", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        scrollViewEmail.setVisibility(View.GONE);
                                        scrollViewNewPassword.setVisibility(View.GONE);
                                        Toast.makeText(Forgotten_password_Activity.this, "Successfully Send", Toast.LENGTH_SHORT).show();
                                        scrollViewCode.setVisibility(View.VISIBLE);
                                        emailSave=email;
                                        trimer();
                                    }


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
                        requestQueue.add(jsonObjectRequest);

                    }
                    else
                    {

                        InternetCheck_Class.openInternetDialog(this, true, this);

                    }

                }
                break;

            case R.id.forgotten_submitCodeBtn:
                String code=forgottenCode.getText().toString().trim();
                int sendCode = 0;
                try {
                    // Attempt to convert the string to a numeric type
                     sendCode= Integer.parseInt(code);

                    // If conversion succeeds, you can use 'number' here
                } catch (NumberFormatException e) {
                    // Handle the NumberFormatException gracefully
                    Toast.makeText(this, "Please Enter Number:EX:123456",Toast.LENGTH_SHORT).show();

                    // You can provide a default value or perform other error handling actions here
                }
                
                if (code.isEmpty())
                {
                    Toast.makeText(this, "Please Insert Your Code", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if (InternetCheck_Class.isNetworkConnected(this))
                    {
                        customDialog.startLoading("Verify Code...");

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", emailSave);
                            postData.put("code",sendCode);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, forgottenPasswordUrl, postData, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                customDialog.dismiss();
                                Log.d(TAG, "onResponse: "+response.toString());
                                try {
                                    JSONObject jsonObject = new JSONObject(String.valueOf(response));
                                    if (jsonObject.has("Token"))
                                    {
                                        customDialog.dismiss();
                                        token=null;
                                        Log.d(TAG,"Token"+jsonObject.has("Token"));
                                        token= jsonObject.getString("Token");
                                        Toast.makeText(Forgotten_password_Activity.this, token, Toast.LENGTH_SHORT).show();
                                        scrollViewEmail.setVisibility(View.GONE);
                                        scrollViewCode.setVisibility(View.GONE);
                                        scrollViewNewPassword.setVisibility(View.VISIBLE);
                                        Toast.makeText(Forgotten_password_Activity.this, "Successfully Send", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        customDialog.dismiss();
                                        scrollViewEmail.setVisibility(View.GONE);
                                        scrollViewCode.setVisibility(View.VISIBLE);
                                        scrollViewNewPassword.setVisibility(View.GONE);
                                        Toast.makeText(Forgotten_password_Activity.this, "Invalid Code", Toast.LENGTH_SHORT).show();

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle errors here
                                customDialog.dismiss();
                                // ...
                            }
                        });
                        requestQueue.add(jsonObjectRequest);

                    }
                    else
                    {

                        InternetCheck_Class.openInternetDialog(this, true, this);

                    }

                }
                break;


                case R.id.forgotten_submitNewPasswordBtn:

                    String newPassword=forgotten_NewPassword.getText().toString().trim();
                    if (newPassword.isEmpty())
                    {
                        Toast.makeText(this, "Please Insert Your New Password", Toast.LENGTH_SHORT).show();
                    }
                    else if (newPassword.length()<5)
                    {
                        Toast.makeText(this, "Please Insert 6 length Password", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {

                        if (InternetCheck_Class.isNetworkConnected(this))
                        {
                            customDialog.startLoading("Generate Password...");

                            RequestQueue requestQueue = Volley.newRequestQueue(this);
                            JSONObject postData = new JSONObject();
                            try {
                                postData.put("token", token);
                                postData.put("password", newPassword);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, forgottenPasswordUrl, postData, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    customDialog.dismiss();
                                    try {
                                        Log.d(TAG, "New Password onResponse: "+response.toString());
                                        JSONObject jsonObject = new JSONObject(String.valueOf(response));
                                        if (jsonObject.has("message"))
                                        {
                                            String getmessage=jsonObject.getString("message");
                                            if (getmessage.equals("Password updated successfully "))
                                            {
                                                Toast.makeText(Forgotten_password_Activity.this, "Successfully Generate Password", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(Forgotten_password_Activity.this,LoginActivity.class);
                                                startActivity(intent);

                                            }
                                            else
                                            {
                                                Toast.makeText(Forgotten_password_Activity.this, "Regenerate Password", Toast.LENGTH_SHORT).show();

                                                scrollViewEmail.setVisibility(View.VISIBLE);
                                                scrollViewCode.setVisibility(View.GONE);
                                                scrollViewNewPassword.setVisibility(View.GONE);

                                            }

                                        }
                                        else
                                        {

                                            Toast.makeText(Forgotten_password_Activity.this, "Something is Wrong", Toast.LENGTH_SHORT).show();

                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Handle errors here
                                    // ...
                                    customDialog.dismiss();

                                }
                            });
                            requestQueue.add(jsonObjectRequest);

                        }
                        else
                        {

                            InternetCheck_Class.openInternetDialog(this, true, this);

                        }

                    }

                break;

            case R.id.forgotten_ResendcodeBtn:
                    if (InternetCheck_Class.isNetworkConnected(this))
                    {
                        customDialog.startLoading("Sending Code...");

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", emailSave);

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
                                        Toast.makeText(Forgotten_password_Activity.this, "Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(Forgotten_password_Activity.this, "Successfully Resend Code", Toast.LENGTH_SHORT).show();
                                        trimer();
                                    }


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
                        requestQueue.add(jsonObjectRequest);

                    }
                    else
                    {

                        InternetCheck_Class.openInternetDialog(this, true, this);

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

    public void trimer()
    {
        new CountDownTimer(300000, 1000) { // 300,000 milliseconds = 5 minutes
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                long minutes = secondsRemaining / 60;
                long seconds = secondsRemaining % 60;
                forgotten_TimerText.setText("Time Remaining: " + minutes + " : " + seconds);
                forgotten_ReSendBtn.setVisibility(View.INVISIBLE);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                forgotten_ReSendBtn.setVisibility(View.VISIBLE);
                forgotten_TimerText.setText("Resend OTP");
            }
        }.start();

    }
}