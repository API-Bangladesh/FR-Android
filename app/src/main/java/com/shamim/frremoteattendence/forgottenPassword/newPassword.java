package com.shamim.frremoteattendence.forgottenPassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.activity.LoginActivity;
import com.shamim.frremoteattendence.alert.CustomDialog;
import com.shamim.frremoteattendence.interfaces.InternetCheck;
import com.shamim.frremoteattendence.utils.InternetCheck_Class;

import org.json.JSONException;
import org.json.JSONObject;


public class newPassword extends Fragment implements InternetCheck {
    private final String TAG="newPassword";

    EditText forgotten_NewPassword;
    Button forgotten_submitNewPasswordBtn;
    private final String forgottenPasswordUrl="https://frapi.apil.online/employee_permission_reset/forget_password";
    CustomDialog customDialog;
    String token;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_new_password, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        forgotten_NewPassword=view.findViewById(R.id.forgotten_Newpassword);
        forgotten_submitNewPasswordBtn=view.findViewById(R.id.forgotten_submitNewPasswordBtn);
        customDialog=new CustomDialog(getActivity());

        Bundle receivedBundle = getArguments();
        if (receivedBundle != null) {
            token = receivedBundle.getString("token");
        }


        forgotten_submitNewPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword=forgotten_NewPassword.getText().toString().trim();
                if (newPassword.isEmpty())
                {
                    Toast.makeText(getContext(), "Please Insert Your New Password", Toast.LENGTH_SHORT).show();
                }
                else if (newPassword.length()<5)
                {
                    Toast.makeText(getContext(), "Please Insert 6 length Password", Toast.LENGTH_SHORT).show();

                }
                else
                {

                    if (InternetCheck_Class.isNetworkConnected(getContext()))
                    {
                        customDialog.startLoading("Generate Password...");

                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("token", token);
                            postData.put("password", newPassword);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, forgottenPasswordUrl, postData, response -> {
                            customDialog.dismiss();
                            try {
                                Log.d(TAG, "New Password onResponse: "+response.toString());
                                JSONObject jsonObject = new JSONObject(String.valueOf(response));
                                if (jsonObject.has("message"))
                                {
                                    String getmessage=jsonObject.getString("message");
                                    if (getmessage.equals("Password updated successfully "))
                                    {
                                        Toast.makeText(getContext(), "Successfully Generate Password", Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getContext(), LoginActivity.class);
                                        startActivity(intent);

                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(), "Regenerate Password", Toast.LENGTH_SHORT).show();

                                    }
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Something is Wrong", Toast.LENGTH_SHORT).show();
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

                        InternetCheck_Class.openInternetDialog(newPassword.this, true, getContext());

                    }
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
}