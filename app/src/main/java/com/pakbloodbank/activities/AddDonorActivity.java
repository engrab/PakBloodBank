package com.pakbloodbank.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.items.Cities;
import com.pakbloodbank.items.Countries;
import com.pakbloodbank.items.Donor;
import com.pakbloodbank.items.States;
import com.pakbloodbank.utils.Constant;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.pakbloodbank.utils.Constant.TAG;


public class AddDonorActivity extends AppCompatActivity {
    private final int DESTINATION_ID = 1;

    private int COUNTRIES_LIST = 1, STATES_LIST = 2, CITIES_LIST = 3;
    ArrayList<Countries> countriesArrayList = new ArrayList<>();
    ArrayList<States>    statesArrayList    = new ArrayList<>();
    ArrayList<Cities>    citiesArrayList    = new ArrayList<>();
    private ArrayList<String> countriesNameList = new ArrayList<>(), statesNameList = new ArrayList<>(), citiesNameList = new ArrayList<>();
    private SearchableSpinner countrySpinner, stateSpinner, citySpinner, donorTypeSpinner, bloodGroupSpinner;

    TextView addressEt;
    EditText habitsEt;

    private LatLng donorLocation;

    String full_name = "", mobile = "", addressValue = "", date_of_birth = "", blood_group = "", countryValue = "", stateValue = "", cityValue = "", habits = "", type = "", last_donation = "", latitude = "", longitude = "";


    LinearLayout lladdress;
    EditText     nameEt;
    String[]     donor_types_arr = {"Free", "Paid"};
    Button       submit;
    private EditText dobEt, lastDonatedDateEt, mobileEt;
    private Methods        methods;
    private ProgressDialog progress;
    private PrefManager    pref;
    private JSONObject     userData;
    private String         user_id;
    private String         donorId;
    private String         donorData;
    Calendar dobCalendar          = Calendar.getInstance();
    Calendar lastDonationCalendar = Calendar.getInstance();
    private Donor                donorItem;
    private ArrayAdapter<String> donorTypeAdapter;
    private ArrayAdapter<String> bloodGroupAdapter;
    private ArrayAdapter<String> countriesSpinnerAdapter;
    private ArrayAdapter<String> statesSpinnerAdapter;
    private ArrayAdapter<String> citiesSpinnerAdapter;
    private int                  SELECT_ADDRESS_REQUEST    = 87;
    private int                  selectedCountryPosition   = 0;
    private int                  selectedStatePosition     = 0;
    private int                  selectedCityPosition      = 0;
    private int                  selectedBloodGroup        = 0;
    private int                  selectedDonorType         = 0;
    private int                  AUTOCOMPLETE_REQUEST_CODE = 23948;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_add_donor);

        setUpSupportToolbar();

        initViews();

        get_user_data();

        Intent data = getIntent();
        try {
            donorId = data.getStringExtra("donorId");

            donorData = data.getStringExtra("donorData");

            if (donorData != null) {

                Gson gson = new Gson();
                Type type = new TypeToken<Donor>() {
                }.getType();

                donorItem = gson.fromJson(donorData, type);

                nameEt.setText(donorItem.getFullName());
                mobileEt.setText(donorItem.getMobile());
                mobileEt.setEnabled(false);
                addressEt.setText(donorItem.getAddress());

                latitude  = donorItem.getLatitude();
                longitude = donorItem.getLongitude();


                dobEt.setText(donorItem.getDateOfBirthSimple());
                lastDonatedDateEt.setText(donorItem.getLastDonationDateSimple());
                habitsEt.setText(donorItem.getHabits());

                get_some_data(COUNTRIES_LIST, null, donorItem.getCountry(), donorItem.getState(), donorItem.getCity());

                setUpSpinnersAdapter(donorItem.getType(), donorItem.getBloodGroup());


            } else {
                get_some_data(COUNTRIES_LIST, null, null, null, null);
                setUpSpinnersAdapter(null, null);

            }


            if (donorId == null) {
                setTitle(getString(R.string.add_donor));
            } else {
                setTitle(getString(R.string.edit_donor));

            }

        } catch (Exception e) {
            e.getLocalizedMessage();
        }

        setUpAddressClick();


        this.submit.setOnClickListener(view -> {
            full_name    = nameEt.getText().toString();
            mobile       = mobileEt.getText().toString();
            addressValue = addressEt.getText().toString();
            if (donorLocation != null) {
                latitude  = String.valueOf(donorLocation.latitude);
                longitude = String.valueOf(donorLocation.longitude);
            }

            date_of_birth = format_date(dobCalendar);
            last_donation = format_date(lastDonationCalendar);
            habits        = habitsEt.getText().toString();

            if (TextUtils.isEmpty(full_name)) {
                Toast.makeText(AddDonorActivity.this, AddDonorActivity.this.getString(R.string.please_enter_donor_name), Toast.LENGTH_LONG).show();
            } else if (full_name.length() < 5) {
                Toast.makeText(AddDonorActivity.this, "Name Cannot be less than 5 characters", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mobile)) {
                Toast.makeText(AddDonorActivity.this, "Please Enter Mobile Number", Toast.LENGTH_LONG).show();
            } else if (mobile.length() < 11) {
                Toast.makeText(AddDonorActivity.this, "Mobile number Cannot be less than 11 characters", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(addressValue)) {
                Toast.makeText(AddDonorActivity.this, "Please Select Your location", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(blood_group)) {
                Toast.makeText(AddDonorActivity.this, "Please Select Blood Group", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(countryValue)) {
                Toast.makeText(AddDonorActivity.this, "Please Select Country", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(stateValue)) {
                Toast.makeText(AddDonorActivity.this, "Please Select State", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(cityValue)) {
                Toast.makeText(AddDonorActivity.this, "Please Select City", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(type)) {
                Toast.makeText(AddDonorActivity.this, "Please Select Donor Type", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(date_of_birth)) {
                Toast.makeText(AddDonorActivity.this, "Please Select Date of birth", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(last_donation)) {
                Toast.makeText(AddDonorActivity.this, "Please Select Last Donation date", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddDonorActivity.this, "Send request", Toast.LENGTH_SHORT).show();
                if (donorId != null) {
                    sendRegistrationRequest(false);
                } else
                    sendRegistrationRequest(true);
            }
        });

        handleDOBEditText();
    }


    private void setUpSupportToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setUpSpinnersAdapter(final String donorType, final String bloodGroup) {

        donorTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner, this.donor_types_arr);
        donorTypeAdapter.setDropDownViewResource(R.layout.spinner);
        donorTypeSpinner.setAdapter(donorTypeAdapter);
        selectedDonorType = 0;
        if (donorType != null) {
            for (int i = 0; i < donor_types_arr.length; i++) {
                if (donorType.equalsIgnoreCase(donor_types_arr[i])) {
                    selectedDonorType = i;
                }
            }
        }

        new Handler().postDelayed(() -> {
            if (donorType != null) {
                donorTypeSpinner.setSelectionM(selectedDonorType);
            }
        }, 1000);


        donorTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {

                type = donor_types_arr[donorTypeSpinner.getSelectedItemPosition()];
                ((TextView) adapterView.getChildAt(0)).setTextColor(AddDonorActivity.this.getResources().getColor(R.color.black));
                ((TextView) adapterView.getChildAt(0)).setTextSize(14.0f);
            }
        });


        bloodGroupAdapter = new ArrayAdapter<>(this, R.layout.spinner, Constant.getBloodGroups());
        bloodGroupAdapter.setDropDownViewResource(R.layout.spinner);
        bloodGroupSpinner.setAdapter(bloodGroupAdapter);

        selectedBloodGroup = 0;
        if (bloodGroup != null)
            for (int i = 0; i < Constant.getBloodGroups().size(); i++) {
                if (bloodGroup.equalsIgnoreCase(Constant.getBloodGroups().get(i))) {
                    selectedBloodGroup = i;
                }
            }
        new Handler().postDelayed(() -> {
            if (bloodGroup != null) {
                bloodGroupSpinner.setSelectionM(selectedBloodGroup);
            }
        }, 1000);


        bloodGroupSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                blood_group = Constant.getBloodGroups().get(bloodGroupSpinner.getSelectedItemPosition());
                ((TextView) adapterView.getChildAt(0)).setTextColor(AddDonorActivity.this.getResources().getColor(R.color.black));
                ((TextView) adapterView.getChildAt(0)).setTextSize(14.0f);
            }
        });
    }

    private void setUpAddressClick() {
        lladdress.setOnClickListener(view -> {
            Intent intent = new Intent(AddDonorActivity.this, PicklocationActivity.class);
            intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
            AddDonorActivity.this.startActivityForResult(intent, 78);
        });
    }

    private void initViews() {

        nameEt            = findViewById(R.id.name);
        mobileEt          = findViewById(R.id.mobile);
        addressEt         = findViewById(R.id.address);
        donorTypeSpinner  = findViewById(R.id.donor_type);
        bloodGroupSpinner = findViewById(R.id.blood_group);
        dobEt             = findViewById(R.id.date_of_birth);
        lastDonatedDateEt = findViewById(R.id.last_donated_date);
        habitsEt          = findViewById(R.id.habits);
        submit            = findViewById(R.id.submit);
        lladdress         = findViewById(R.id.lladdress);
        countrySpinner    = findViewById(R.id.country);
        stateSpinner      = findViewById(R.id.states);
        citySpinner       = findViewById(R.id.city);

        methods = new Methods(this);

        progress = new ProgressDialog(this);
        progress.setMessage(AddDonorActivity.this.getString(R.string.please_wait));
        progress.setCancelable(false);
    }

    private void setupSpinners() {
        final ArrayAdapter<String> countries = new ArrayAdapter<>(AddDonorActivity.this, R.layout.spinner, countriesNameList);
        countries.setDropDownViewResource(R.layout.spinner);
        countrySpinner.setAdapter(countries);

        stateSpinner.setEnabled(false);
        citySpinner.setEnabled(false);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    countryValue = countriesArrayList.get((countrySpinner.getSelectedItemPosition())).getId();

                    if (selectedCountryPosition > 0) {

                    } else
                    {
                        get_some_data(STATES_LIST, countryValue, null, null, null);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    stateValue = statesArrayList.get((stateSpinner.getSelectedItemPosition())).getId();

                    if (selectedStatePosition > 0) {

                    } else {
                        get_some_data(CITIES_LIST, stateValue, null, null, null);

                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                    cityValue = citiesArrayList.get((citySpinner.getSelectedItemPosition())).getId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
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


//    private void get_some_data(final int what_data, final String id) {
//
//
//        RequestQueue q = Volley.newRequestQueue(this);
//
//        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.getSomeData, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                Log.d(TAG, response);
//                JSONObject object;
//                try {
//                    object = new JSONObject(response);
//                    object = object.getJSONObject("List");
//
//
//                    Gson gson = new Gson();
//
//
//                    if (what_data == COUNTRIES_LIST) {
//
//                        countriesArrayList.clear();
//                        countriesNameList.clear();
//
//                        Type country_type = new TypeToken<List<Countries>>() {
//                        }.getType();
//                        JSONArray countries = object.getJSONArray("countries");
//
//                        countriesArrayList = gson.fromJson(countries.toString(), country_type);
//
//
//                        for (Countries c : countriesArrayList) {
//                            countriesNameList.add(c.getName());
//                        }
//
//                        setupSpinners();
//
//                    } else if (what_data == STATES_LIST) {
//                        statesNameList.clear();
//                        statesArrayList.clear();
//
//                        Type states_type = new TypeToken<List<States>>() {
//                        }.getType();
//                        JSONArray states = object.getJSONArray("states");
//
//                        statesArrayList = gson.fromJson(states.toString(), states_type);
//                        for (States s : statesArrayList) {
//                            statesNameList.add(s.getName());
//                        }
//
//                        ArrayAdapter<String> states_adapter = new ArrayAdapter<>(AddDonorActivity.this, R.layout.spinner, statesNameList);
//                        states_adapter.setDropDownViewResource(R.layout.spinner);
//                        stateSpinner.setAdapter(states_adapter);
//                        stateSpinner.setEnabled(true);
//
//                    } else if (what_data == CITIES_LIST) {
//
//                        citiesArrayList.clear();
//                        citiesNameList.clear();
//
//                        Type cities_type = new TypeToken<List<Cities>>() {
//                        }.getType();
//
//                        JSONArray cities = object.getJSONArray("cities");
//
//                        citiesArrayList = gson.fromJson(cities.toString(), cities_type);
//
//                        for (Cities c : citiesArrayList) {
//                            citiesNameList.add(c.getName());
//                        }
//
//
//                        ArrayAdapter<String> cities_adapter = new ArrayAdapter<>(AddDonorActivity.this, R.layout.spinner, citiesNameList);
//                        cities_adapter.setDropDownViewResource(R.layout.spinner);
//                        citySpinner.setAdapter(cities_adapter);
//                        citySpinner.setEnabled(true);
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e(TAG, "onErrorResponse: " + error, null);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//
//                if (what_data == COUNTRIES_LIST) {
//                    params.put("what", "countries");
//                } else if (what_data == STATES_LIST) {
//                    params.put("what", "states");
//                    params.put("country_id", id);
//                } else if (what_data == CITIES_LIST) {
//                    params.put("what", "cities");
//                    params.put("state_id", id);
//                }
//                return params;
//            }
//        };
//
//        q.add(request);
//    }

    private void get_some_data(final int what_to_get, final String id, final String countryId, final String stateId, final String cityId) {

        Log.e(TAG, "what: " + what_to_get + " id:  " + id + " countryId: " + countryId + " stateId: " + stateId + " cityID: " + cityId);

        RequestQueue q = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.getSomeData, response -> {

            Log.d(TAG, response);
            JSONObject object;
            try {
                object = new JSONObject(response);
                object = object.getJSONObject("List");

                Gson gson = new Gson();

                if (what_to_get == COUNTRIES_LIST) {

                    countriesArrayList.clear();
                    countriesNameList.clear();

                    Type country_type = new TypeToken<List<Countries>>() {
                    }.getType();
                    JSONArray countries = object.getJSONArray("countries");


                    Log.e("Countries", countries.toString());

                    countriesArrayList = gson.fromJson(countries.toString(), country_type);

                    selectedCountryPosition = 0;

                    for (int i = 0; i < countriesArrayList.size(); i++) {

                        Countries c = countriesArrayList.get(i);
                        countriesNameList.add(c.getName());

                        if (countryId != null) {
                            if (c.getId().equalsIgnoreCase(countryId)) {
                                selectedCountryPosition = i;
                            }
                        }
                    }

                    setupSpinners();

                    new Handler().postDelayed(() -> {
                        if (countryId != null) {
                            countrySpinner.setSelectionM(selectedCountryPosition);
                        }
                    }, 1000);


                    if (countryId != null) {
                        get_some_data(STATES_LIST, countryId, countryId, stateId, cityId);
                    }

                } else if (what_to_get == STATES_LIST) {
                    statesNameList.clear();
                    statesArrayList.clear();

                    Type states_type = new TypeToken<List<States>>() {
                    }.getType();
                    JSONArray states = object.getJSONArray("states");

                    statesArrayList       = gson.fromJson(states.toString(), states_type);
                    selectedStatePosition = 0;
                    for (int i = 0; i < statesArrayList.size(); i++) {
                        States s = statesArrayList.get(i);
                        statesNameList.add(s.getName());

                        if (stateId != null) {
                            if (s.getId().equalsIgnoreCase(stateId)) {
                                selectedStatePosition = i;
                            }
                        }
                    }

                    statesSpinnerAdapter = new ArrayAdapter<>(AddDonorActivity.this, R.layout.spinner, statesNameList);
                    statesSpinnerAdapter.setDropDownViewResource(R.layout.spinner);
                    stateSpinner.setAdapter(statesSpinnerAdapter);
                    stateSpinner.setEnabled(true);

                    new Handler().postDelayed(() -> {
                        if (stateId != null) {
                            stateSpinner.setSelectionM(selectedStatePosition);
                        }
                    }, 1000);

//                    if (statesNameList.size() == 1) {
//                        stateSpinner.setSelectionM(0);
//                    }


                    if (stateId != null) {
                        get_some_data(CITIES_LIST, stateId, countryId, stateId, cityId);
                    }

                } else if (what_to_get == CITIES_LIST) {

                    citiesArrayList.clear();
                    citiesNameList.clear();

                    Type cities_type = new TypeToken<List<Cities>>() {
                    }.getType();

                    JSONArray cities = object.getJSONArray("cities");

                    citiesArrayList = gson.fromJson(cities.toString(), cities_type);

                    selectedCityPosition = 0;
                    for (int i = 0; i < citiesArrayList.size(); i++) {
                        Cities c = citiesArrayList.get(i);
                        citiesNameList.add(c.getName());
                        if (cityId != null) {
                            if (c.getId().equalsIgnoreCase(cityId)) {
                                selectedCityPosition = i;
                            }
                        }
                    }


                    citiesSpinnerAdapter = new ArrayAdapter<>(AddDonorActivity.this, R.layout.spinner, citiesNameList);
                    citiesSpinnerAdapter.setDropDownViewResource(R.layout.spinner);
                    citySpinner.setAdapter(citiesSpinnerAdapter);
                    citySpinner.setEnabled(true);

                    new Handler().postDelayed(() -> {
                        if (cityId != null) {
                            citySpinner.setSelectionM(selectedCityPosition);
                        }
                    }, 1000);

                    if (citiesNameList.size() == 1) {
                        citySpinner.setSelectionM(0);
                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> Log.e(TAG, "onErrorResponse: " + error, null)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                if (what_to_get == COUNTRIES_LIST) {
                    params.put("what", "countries");
                } else if (what_to_get == STATES_LIST) {
                    params.put("what", "states");
                    params.put("country_id", id);
                } else if (what_to_get == CITIES_LIST) {
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

        final DatePickerDialog.OnDateSetListener ldate = (view, year, monthOfYear, dayOfMonth) -> {
            lastDonationCalendar.set(Calendar.YEAR, year);
            lastDonationCalendar.set(Calendar.MONTH, monthOfYear);
            lastDonationCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(lastDonationCalendar, lastDonatedDateEt);
        };

        lastDonatedDateEt.setInputType(InputType.TYPE_NULL);

        lastDonatedDateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (lastDonatedDateEt.hasFocus()) {
                showDateDialog(ldate, lastDonationCalendar, lastDonationCalendar.get(Calendar.YEAR));
            }
        });

        lastDonatedDateEt.setOnClickListener(v -> showDateDialog(ldate, lastDonationCalendar, lastDonationCalendar.get(Calendar.YEAR)));
    }

    private void showDateDialog(DatePickerDialog.OnDateSetListener ldate, Calendar dobCalendar, int last_year) {

        new DatePickerDialog(this,
                ldate,
                last_year,
                dobCalendar.get(Calendar.MONTH),
                dobCalendar.get(Calendar.DAY_OF_MONTH)
        ).show();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLabel(Calendar calendar, EditText et) {
        String           myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf      = new SimpleDateFormat(myFormat, Locale.US);

        et.setText(sdf.format(calendar.getTime()));
    }

    private String format_date(Calendar calendar) {
        String           myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf      = new SimpleDateFormat(myFormat, Locale.US);

        return sdf.format(calendar.getTime());
    }


    private void get_user_data() {
        pref = new PrefManager(this);
        try {
            userData = new JSONObject(pref.getUserData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void sendRegistrationRequest(final boolean isNewUser) {
        if (methods.isNetworkAvailable()) {

            if (!progress.isShowing()) {
                progress.show();
            }
//            final String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            String url;

            if (isNewUser) {
                url = UrlHelper.addDonorUrl;
            } else {
                url = UrlHelper.editDonorUrl;
            }

            try {
                user_id = userData.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }

                        JSONObject jsonObject;
                        try {

                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG);

                            if (jsonArray.getJSONObject(0).getBoolean("success")) {
                                if (donorId == null)
                                    Toast.makeText(AddDonorActivity.this, AddDonorActivity.this.getString(R.string.donor_successfully_added), Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(AddDonorActivity.this, AddDonorActivity.this.getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(AddDonorActivity.this, AddDonorActivity.this.getString(R.string.operation_failed), Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    },
                    error -> {
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                        Log.d(VolleyLog.TAG, "onErrorResponse: error: " + error.toString());
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("full_name", full_name);
                    params.put("mobile", mobile);
                    params.put("address", addressValue);
                    params.put("city", cityValue);
                    params.put("state", stateValue);
                    params.put("country", countryValue);
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                    params.put("donor_type", type);
                    params.put("habits", habits);
                    params.put("date_of_birth", date_of_birth);
                    params.put("lastDonationDate", last_donation);
                    params.put("blood_group", blood_group);
                    params.put("addedBy", user_id);

                    if (!isNewUser) {
                        params.put("id", donorId);
                    }
                    return params;
                }
            };

            queue.add(request);

        } else {

            if (progress.isShowing()) {
                progress.dismiss();
            }

            Constant.showNoNetwork(this);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}