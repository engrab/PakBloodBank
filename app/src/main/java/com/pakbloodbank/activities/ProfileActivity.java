package com.pakbloodbank.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.items.Cities;
import com.pakbloodbank.items.CityModel;
import com.pakbloodbank.items.Countries;
import com.pakbloodbank.items.States;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.DBHelper;
import com.pakbloodbank.utils.Methods;
import com.pakbloodbank.utils.PrefManager;
import com.pakbloodbank.utils.UrlHelper;
import com.pakbloodbank.utils.custom.SearchableSpinner;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
//    ProgressDialog progressDialog;
    private String phone, name, address, cityValue, stateValue, countryValue, dob, bloodGroup, password, devId;
    Calendar myCalendar = Calendar.getInstance();
    String user_id = "";
    private EditText nameEt, dobEt, passwordEt;
    private TextView phoneEt;

    private Button saveBtn;
    private String[] cities;

    private SearchableSpinner citySpinner, stateSpinner, countrySpinner, bloodGroupSpinner;
    private Methods methods;
    private ProgressDialog progress;
    private ArrayList<CityModel> citiesArray;
    private DBHelper dbHelper;
    LinearLayout lladdress;

    PrefManager pref;
    private int DESTINATION_ID = 1;
    private TextView addressEt;
    private LatLng donorLocation;
    private Calendar dobCalendar = Calendar.getInstance();
    private int COUNTRIES_LIST = 12, STATES_LIST = 13, CITIES_LIST = 14;
    private ArrayList<Countries> countriesArrayList = new ArrayList<>();
    private ArrayList<States> statesArrayList = new ArrayList<>();
    private ArrayList<Cities> citiesArrayList = new ArrayList<>();
    private ArrayList<String> citiesNameList = new ArrayList<>(), statesNameList = new ArrayList<>(), countriesNameList = new ArrayList<>();
    String[] bloodArray;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.complete_registration));


        boolean toUpdate = getIntent().getBooleanExtra("toUpdate", false);

        pref = new PrefManager(this);

        String user_data = pref.getUserData();

        String phoneNum = "";




        dbHelper = new DBHelper(this);
        progress = new ProgressDialog(this);
        progress.setMessage("Please Wait!");
        progress.setTitle("Loading");
        progress.setCancelable(false);


        methods = new Methods(this);
        nameEt = findViewById(R.id.fullName);
        lladdress = findViewById(R.id.lladdress);
        addressEt = findViewById(R.id.address);
        passwordEt = findViewById(R.id.password);
        phoneEt = findViewById(R.id.phonenumber);
        dobEt = findViewById(R.id.dob);
        citySpinner = findViewById(R.id.city);
        stateSpinner = findViewById(R.id.states);
        countrySpinner = findViewById(R.id.country);
        bloodGroupSpinner = findViewById(R.id.blood_group);
        saveBtn = findViewById(R.id.submit);

        setUpAddressClick();

        phoneEt.setText(phoneNum);

        handleDOBEditText();
        if (methods.isNetworkAvailable()) {

            setUpSpinners();
            handleClicks();

//            progressDialog=new ProgressDialog(this);
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage("Loading...");

            get_some_data(COUNTRIES_LIST, null);

        } else {
            Constant.showNoNetwork(this);
        }

        if (user_data != null)
            try {

                JSONObject user = new JSONObject(user_data);

                user_id = user.getString("id");
                phoneNum = user.getString("mobile");
                phoneEt.setText(phoneNum);


            } catch (JSONException e) {
                e.printStackTrace();
            }

    }

    private String format_date(Calendar calendar) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        return sdf.format(calendar.getTime());
    }


    boolean error = false;

    private void getVariables() {

        error = false;
        if (nameEt.getText().toString().isEmpty()) {
            nameEt.setError(getString(R.string.please_enter_name));
            error = true;
        } else
            name = nameEt.getText().toString();


        if (addressEt.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.please_select_address), Toast.LENGTH_SHORT).show();
            error = true;
        } else
            address = addressEt.getText().toString();

        if (passwordEt.getText().toString().isEmpty()) {
            passwordEt.setError(getString(R.string.please_enter_password));
            error = true;
        } else {
            password = passwordEt.getText().toString();
        }

        if (dobEt.getText().toString().isEmpty()) {
            error = true;
            dobEt.setError(getString(R.string.please_select_date_of_birth));
        } else {
            dob = dobEt.getText().toString();
            dob = format_date(myCalendar);
        }

//        if (phoneEt.getText().toString().isEmpty()) {
//            error = true;
//            phoneEt.setError(getString(R.string.please_enter_mobile_number));
//        } else {
//            phone = phoneEt.getText().toString();
//        }

    }

    private void handleClicks() {
        saveBtn.setOnClickListener(v -> {
            getVariables();
            if (!error) {
                sendRegistrationRequest();
            }
        });
    }


    private void sendRegistrationRequest() {
        if (methods.isNetworkAvailable()) {

            if (!progress.isShowing()) {
                progress.show();
            }


         //   final String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            final String android_id =  UUID.randomUUID().toString();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.addUserUrl,
                    response -> {

                        Constant.asdf("add_user");
                        Constant.asdf(response);

                        if (progress.isShowing()) {
                            progress.dismiss();
                        }

                        JSONObject jsonObject;
                        try {

                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG);

                            JSONObject ob = jsonArray.getJSONObject(0);
                            if (ob.getBoolean("success")) {

                                Toast.makeText(ProfileActivity.this, "" + ob.getString("msg"), Toast.LENGTH_SHORT).show();
                                pref.saveUserData(ob.getJSONObject("user_data").toString());

                                pref.setFullName(name);
                                pref.setAddress(address);



                                gotoMain();

                            } else {
                                Toast.makeText(ProfileActivity.this, "" + ob.getString("msg"), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    },
                    error -> {
                        Log.d(TAG, "onErrorResponse: error: " + error.toString());
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    //if (updateOrNot)
                    params.put("id", user_id);

                    params.put("name", name);
                    params.put("city", cityValue);
                    params.put("state", stateValue);
                    params.put("country", countryValue);
                    params.put("address", address);
                    params.put("latitude", String.valueOf(donorLocation.latitude));
                    params.put("longitude", String.valueOf(donorLocation.longitude));
                    params.put("date_of_birth", dob);
                    params.put("blood_group", bloodGroup);
                    params.put("password", password);
                    params.put("deviceId", android_id);


                    return params;
                }
            };

            queue.add(request);

        } else {
            Constant.showNoNetwork(this);
        }
    }


    private void get_some_data(final int what_data, final String id) {


        RequestQueue q = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.getSomeData, response -> {

            Log.d(TAG, response);
            JSONObject object;
            try {
                object = new JSONObject(response);
                object = object.getJSONObject("List");


                Gson gson = new Gson();


                if (what_data == COUNTRIES_LIST) {

                    countriesArrayList.clear();
                    countriesNameList.clear();

//                    showProgressDialog();

                    Type country_type = new TypeToken<List<Countries>>() {
                    }.getType();
                    JSONArray countries = object.getJSONArray("countries");

                    countriesArrayList = gson.fromJson(countries.toString(), country_type);

                    for (Countries c : countriesArrayList) {
                        countriesNameList.add(c.getName());
                    }

                    setupCountrySpinner();
                } else if (what_data == STATES_LIST) {
                    statesNameList.clear();
                    statesArrayList.clear();

                    Type states_type = new TypeToken<List<States>>() {
                    }.getType();
                    JSONArray states = object.getJSONArray("states");

                    statesArrayList = gson.fromJson(states.toString(), states_type);

                    for (States s : statesArrayList) {
                        statesNameList.add(s.getName());
                    }

                    setupStateSpinner();



                } else if (what_data == CITIES_LIST) {

                    citiesArrayList.clear();
                    citiesNameList.clear();

                    Type cities_type = new TypeToken<List<Cities>>() {
                    }.getType();

                    JSONArray cities = object.getJSONArray("cities");

                    citiesArrayList = gson.fromJson(cities.toString(), cities_type);

                    for (Cities c : citiesArrayList) {
                        citiesNameList.add(c.getName());
                    }
                    Log.d(TAG, "get_some_data: "+citiesNameList.size());


                    setupCitySpinner();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> Log.e(TAG, "onErrorResponse: " + error, null))

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                if (what_data == COUNTRIES_LIST) {
                    params.put("what", "countries");
                } else if (what_data == STATES_LIST) {
                    params.put("what", "states");
                    params.put("country_id", id);
                } else if (what_data == CITIES_LIST) {
                    params.put("what", "cities");
                    params.put("state_id", id);
                }
                return params;
            }
        };

        q.add(request);
    }


    private void handleDOBEditText() {

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            dobCalendar.set(Calendar.YEAR, year);
            dobCalendar.set(Calendar.MONTH, monthOfYear);
            dobCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(dobCalendar, dobEt);
        };

        dobEt.setInputType(InputType.TYPE_NULL);

        dobEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (dobEt.hasFocus()) {
                showDateDialog(date, dobCalendar, 1995);
            }
        });

        dobEt.setOnClickListener(v -> showDateDialog(date, dobCalendar, 1995));

    }

    private void setupCountrySpinner() {
        ArrayAdapter<String> countries = new ArrayAdapter<>(ProfileActivity.this, R.layout.spinner, countriesNameList);
        countries.setDropDownViewResource(R.layout.spinner);
        countrySpinner.setAdapter(countries);

        stateSpinner.setEnabled(false);
        citySpinner.setEnabled(false);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    countryValue = countriesArrayList.get((countrySpinner.getSelectedItemPosition())).getId();
                    pref.setCountry(countriesArrayList.get((countrySpinner.getSelectedItemPosition())).getName());

                    get_some_data(STATES_LIST, countryValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    private void setupStateSpinner() {
        ArrayAdapter<String> countries = new ArrayAdapter<>(ProfileActivity.this, R.layout.spinner, statesNameList);
        countries.setDropDownViewResource(R.layout.spinner);
        stateSpinner.setAdapter(countries);


        stateSpinner.setEnabled(true);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                stateValue = statesArrayList.get((stateSpinner.getSelectedItemPosition())).getId();
                pref.setState(statesArrayList.get((stateSpinner.getSelectedItemPosition())).getName());

                get_some_data(CITIES_LIST, stateValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setupCitySpinner() {
        ArrayAdapter<String> countries = new ArrayAdapter<>(ProfileActivity.this, R.layout.spinner, citiesNameList);
        countries.setDropDownViewResource(R.layout.spinner);
        citySpinner.setAdapter(countries);

        citySpinner.setEnabled(true);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cityValue = citiesArrayList.get((citySpinner.getSelectedItemPosition())).getId();
                pref.setCity(citiesArrayList.get((citySpinner.getSelectedItemPosition())).getName());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void updateLabel(Calendar calendar, EditText et) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et.setText(sdf.format(calendar.getTime()));
        pref.setDob(et.getText().toString());
    }

    private void showDateDialog(DatePickerDialog.OnDateSetListener ldate, Calendar dobCalendar, int last_year) {

        new DatePickerDialog(this,
                ldate,
                last_year,
                dobCalendar.get(Calendar.MONTH),
                dobCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }


    private void setUpAddressClick() {
        lladdress.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, PicklocationActivity.class);
            intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
            startActivityForResult(intent, 78);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 78 && resultCode == RESULT_OK) {
            LatLng latLng = data.getParcelableExtra(PicklocationActivity.LOCATION_LATLNG);
            this.addressEt.setText(data.getStringExtra(PicklocationActivity.LOCATION_NAME));
            this.donorLocation = latLng;
        }
    }

    private void gotoMain() {
        this.finish();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }



    private void showViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }



    private void hideViews(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    private void setUpSpinners() {


        bloodArray = new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        initSpinnerAdapters(bloodArray, bloodGroupSpinner);

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                bloodGroup = String.valueOf(parent.getItemAtPosition(position));
                pref.setBloodGroup(bloodGroup);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerAdapters(String[] array, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    private String countryMatch(String country){

        for(Countries c : countriesArrayList){
            if(c.getId().equals(country)){
                return c.getName();
            }
        }
        return null;

    }

    private String stateMatch(String state){

        for(States c : statesArrayList){
            if(c.getId().equals(state)){
                return c.getName();
            }
        }
        return null;

    }

    private String cityMatch(String city){

        for(Cities c : citiesArrayList){
            if(c.getId().equals(city)){
                return c.getName();
            }
        }
        return null;

    }


}