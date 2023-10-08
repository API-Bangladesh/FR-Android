package com.shamim.frremoteattendence.forgottenPassword;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shamim.frremoteattendence.R;

public class Forgotten_password_Activity extends AppCompatActivity{
    private final String TAG="Forgotten_password_Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_forgotten_container,new Email_fragment())
                .commit();

    }
    private void sendDataToEmailFragment() {
        Email_fragment emailFragment = new Email_fragment();
        Bundle bundle = new Bundle();
        bundle.putString("emailKey", "Data you want to send");
        emailFragment.setArguments(bundle);


    }

}