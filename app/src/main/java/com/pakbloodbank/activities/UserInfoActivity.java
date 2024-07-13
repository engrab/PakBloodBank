package com.pakbloodbank.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pakbloodbank.R;
import com.pakbloodbank.utils.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class UserInfoActivity extends AppCompatActivity {

    TextView fullName, phonenumber, address, country, states, city, blood_group, dob;
    PrefManager pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setUpSupportToolbar();
        initView();

        pref = new PrefManager(this);

        fullName.setText(pref.getFullName());
        phonenumber.setText(pref.getPhoneNumber());
        address.setText(pref.getAddress());
        country.setText(pref.getCountry());
        states.setText(pref.getState());
        city.setText(pref.getCity());
        blood_group.setText(pref.getBloodGroup());
        dob.setText(pref.getDob());

    }

    private void setUpSupportToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    private void initView() {
        fullName = findViewById(R.id.fullName);
        phonenumber = findViewById(R.id.phonenumber);
        address = findViewById(R.id.address);
        country = findViewById(R.id.country);
        states = findViewById(R.id.states);
        city = findViewById(R.id.city);
        blood_group = findViewById(R.id.blood_group);
        dob = findViewById(R.id.dob);
    }
}