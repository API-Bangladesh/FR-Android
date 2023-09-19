package com.shamim.frremoteattendence.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
;import com.android.volley.AuthFailureError;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shamim.frremoteattendence.R;
import com.shamim.frremoteattendence.alert.CustomDialog;
import com.shamim.frremoteattendence.fragment.LivePreview_Camera;
import com.shamim.frremoteattendence.fragment.Employee_Data_Fragment;
import com.shamim.frremoteattendence.interfaces.InternetCheck;
import com.shamim.frremoteattendence.permission.Permission;
import com.shamim.frremoteattendence.sharedpreference.FR_sharedpreference;
import com.shamim.frremoteattendence.utils.InternetCheck_Class;

import org.json.JSONException;
import org.json.JSONObject;
public class Fragment_Changer_Activity extends AppCompatActivity implements InternetCheck {
    private static final String tokenDeleteURL = "https://frapi.apil.online/employee_permission/logout";;
    final String TAG="FullImageView";
    BottomNavigationView bottomNavigationView;
    String logoutcheckToken;
    private CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_changer);


        bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new LivePreview_Camera()).commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom_camera);
            customDialog=new CustomDialog(this);
            Permission.CheckGps(this);
        }
        bottomNavigationView.setOnItemSelectedListener(item -> {
            logoutcheckToken= FR_sharedpreference.Companion.getLoginToken(this);
            int id = item.getItemId();
            if (id == R.id.bottom_access) {
                if (logoutcheckToken !=null)
                {
                    if (InternetCheck_Class.isNetworkConnected(this))
                    {
                        DeleteToken();
                    }
                    else
                    {
                        InternetCheck_Class.openInternetDialog(this, true, this);
                    }
                }
            } else if (id == R.id.bottom_camera)
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LivePreview_Camera()).commit();
            }
            else if (id == R.id.bottom_door_lock_data) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new Employee_Data_Fragment()).commit();
            }
            return true;
        });
    }
    private void DeleteToken() {
        customDialog.startLoading("Logout Please Wait...");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("token",logoutcheckToken);
            Log.d(TAG, "Token=  " + logoutcheckToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, tokenDeleteURL, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Request Get Time:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    customDialog.dismiss();
                    if (jsonObject.has("message")) {
                        FR_sharedpreference.Companion.RemoveToken(Fragment_Changer_Activity.this);
                        FR_sharedpreference.Companion.removeAllowedLocation(Fragment_Changer_Activity.this);
                        Intent intent = new Intent(Fragment_Changer_Activity.this, LoginActivity.class);
                        FR_sharedpreference.Companion.Remove_LoginuserName(Fragment_Changer_Activity.this);
                        startActivity(intent);
                        finish();
                        Toast.makeText(Fragment_Changer_Activity.this, "Successful Logout ", Toast.LENGTH_SHORT).show();
                    } else  {
                        FR_sharedpreference.Companion.RemoveToken(Fragment_Changer_Activity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error instanceof NetworkError) {

                    Toast.makeText(Fragment_Changer_Activity.this, "Network Error", Toast.LENGTH_SHORT).show();
                } else if (error instanceof ServerError) {
                    Toast.makeText(Fragment_Changer_Activity.this, "Server Problem", Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(Fragment_Changer_Activity.this, "Network AuthFailureError", Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    Toast.makeText(Fragment_Changer_Activity.this, "Network ParseError", Toast.LENGTH_SHORT).show();

                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(Fragment_Changer_Activity.this, "Network NoConnectionError", Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    Toast.makeText(Fragment_Changer_Activity.this, "Oops. Timeout !", Toast.LENGTH_LONG).show();
                }
                customDialog.dismiss();
                Toast.makeText(Fragment_Changer_Activity.this, "Error" + error, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
        Log.d(TAG, "Request Send Time:" + requestQueue);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(2000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}