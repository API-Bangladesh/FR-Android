package com.shamim.frremoteattendence.forgottenPassword;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.alert.CustomDialog;
import com.shamim.frremoteattendence.interfaces.InternetCheck;
import com.shamim.frremoteattendence.utils.InternetCheck_Class;

import org.json.JSONException;
import org.json.JSONObject;


public class CodeSent extends Fragment implements InternetCheck {
    private final String TAG="CodeSent";

    EditText forgottenCode;
    Button forgotten_submitCodeBtn;
    CustomDialog customDialog;
    TextView forgotten_TimerText;
    ImageView forgotten_ReSendBtn;
    String emailData;
    private final String forgottenPasswordUrl="https://frapi.apil.online/employee_permission_reset/forget_password";


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_code_sent, container, false);
        forgottenCode=view.findViewById(R.id.forgottenCode);
        forgotten_submitCodeBtn=view.findViewById(R.id.forgotten_submitCodeBtn);
        forgotten_ReSendBtn=view.findViewById(R.id.forgotten_ResendcodeBtn);
        forgotten_TimerText=view.findViewById(R.id.forgotten_trimerTextview);

        customDialog=new CustomDialog(getActivity());
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
             emailData = receivedBundle.getString("email");
            Toast.makeText(getContext(), ""+emailData, Toast.LENGTH_SHORT).show();
            // Now you have the emailData in CodeSent_fragment, and you can use it as needed
        }


        forgotten_submitCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=forgottenCode.getText().toString().trim();
                int sendCode = 0;
                try {
                    sendCode= Integer.parseInt(code);

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please Enter Number:EX:123456",Toast.LENGTH_SHORT).show();
                }

                if (code.isEmpty())
                {
                    Toast.makeText(getContext(), "Please Insert Your Code", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if (InternetCheck_Class.isNetworkConnected(getContext()))
                    {
                        customDialog.startLoading("Verify Code...");

                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", emailData);
                            postData.put("code",sendCode);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, forgottenPasswordUrl, postData, response -> {
                            customDialog.dismiss();
                            Log.d(TAG, "onResponse: "+response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(String.valueOf(response));
                                if (jsonObject.has("Token"))
                                {
                                    String token=jsonObject.getString("Token");

                                    Bundle bundle = new Bundle();
                                    bundle.putString("token",token);

                                    newPassword newPassword = new newPassword();
                                    newPassword.setArguments(bundle);

                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_forgotten_container, newPassword);
                                    transaction.addToBackStack(null); // Optional: Add to back stack if needed
                                    transaction.commit();
                                    Toast.makeText(getContext(), "Successfully Send", Toast.LENGTH_SHORT).show();
                                    customDialog.dismiss();
                                }
                                else
                                {
                                    customDialog.dismiss();
                                    Toast.makeText(getContext(), "Invalid Code", Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            // Handle errors here
                            customDialog.dismiss();
                            Toast.makeText(getContext(), "error:"+ error.getMessage(), Toast.LENGTH_SHORT).show();
                            // ...
                        });
                        requestQueue.add(jsonObjectRequest);

                    }
                    else
                    {
                        InternetCheck_Class.openInternetDialog(CodeSent.this, true, getContext());
                    }

                }

            }
        });

        forgotten_ReSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InternetCheck_Class.isNetworkConnected(getContext()))
                {
                    customDialog.startLoading("Sending Code...");
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    JSONObject postData = new JSONObject();
                    try {
                        postData.put("email", emailData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, forgottenPasswordUrl, postData, response -> {
                        customDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(String.valueOf(response));
                            if (jsonObject.has("Access"))
                            {
                                Toast.makeText(getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getContext(), "Successfully Resend Code", Toast.LENGTH_SHORT).show();
                                trimer();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        customDialog.dismiss();
                        Toast.makeText(getContext(), "error:"+ error.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                    requestQueue.add(jsonObjectRequest);
                }
                else
                {

                    InternetCheck_Class.openInternetDialog(CodeSent.this, true, getContext());
                }
            }
        });

        return view;
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onRetry() {

    }


    public void trimer()
    {
        new CountDownTimer(20000, 1000) { // 300,000 milliseconds = 5 minutes
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