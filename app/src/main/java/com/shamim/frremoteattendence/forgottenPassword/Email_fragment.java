package com.shamim.frremoteattendence.forgottenPassword;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Patterns;
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
import com.shamim.frremoteattendence.alert.CustomDialog;
import com.shamim.frremoteattendence.alert.CustomDialog_notification;
import com.shamim.frremoteattendence.interfaces.InternetCheck;
import com.shamim.frremoteattendence.utils.InternetCheck_Class;

import org.json.JSONException;
import org.json.JSONObject;


public class Email_fragment extends Fragment implements InternetCheck {

    EditText forgottenEmail;
    Button forgotten_submitEmailBtn;
    private final String forgottenPasswordUrl="https://frapi.apil.online/employee_permission_reset/forget_password";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_email_fragment, container, false);

        forgottenEmail=view.findViewById(R.id.forgottenEmail);
        forgotten_submitEmailBtn=view.findViewById(R.id.forgotten_submitEmailBtn);


        forgotten_submitEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog customDialog=new CustomDialog(getActivity());
                String email=forgottenEmail.getText().toString().trim();
                if (email.isEmpty())
                {
                    Toast.makeText(getContext(), "Please Enter Email", Toast.LENGTH_SHORT).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    Toast.makeText(getContext(), "Please Give Valid Email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (InternetCheck_Class.isNetworkConnected(getContext()))
                    {
                        customDialog.startLoading("Sending Code...");

                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        JSONObject postData = new JSONObject();
                        try {
                            postData.put("email", email);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, forgottenPasswordUrl, postData, response -> {
                            customDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(String.valueOf(response));

                                if (jsonObject.has("Access"))
                                {

                                    Toast.makeText(getContext(), "Email is not registered", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email",email);

                                    CodeSent codeSentFragment = new CodeSent();
                                    codeSentFragment.setArguments(bundle);

                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_forgotten_container,codeSentFragment);
                                    transaction.addToBackStack(null); // Optional: Add to back stack if needed
                                    transaction.commit();
                                    Toast.makeText(getContext(), "Successfully Send", Toast.LENGTH_SHORT).show();

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
                        InternetCheck_Class.openInternetDialog(Email_fragment.this, true, getContext());
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
        if (InternetCheck_Class.isNetworkConnected(getContext()))
        {
        }
        else
        {
            InternetCheck_Class.openInternetDialog(this, true, getContext());
        }
    }
}