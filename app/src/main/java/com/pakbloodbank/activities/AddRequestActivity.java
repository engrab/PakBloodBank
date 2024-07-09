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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.items.Cities;
import com.pakbloodbank.items.Countries;
import com.pakbloodbank.items.RequestsItem;
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


public class AddRequestActivity extends AppCompatActivity {
    private final int DESTINATION_ID = 1;

    private int COUNTRIES_LIST = 1, STATES_LIST = 2, CITIES_LIST = 3;
    ArrayList<Countries> countriesArrayList = new ArrayList<>();
    ArrayList<States> statesArrayList = new ArrayList<>();
    ArrayList<Cities> citiesArrayList = new ArrayList<>();
    private ArrayList<String> countriesNameList = new ArrayList<>(), statesNameList = new ArrayList<>(), citiesNameList = new ArrayList<>();
    private SearchableSpinner countrySpinner, stateSpinner, citySpinner, bloodGroupSpinner;
    ;

    String reqId, request_data;
    TextView addressEt;
    EditText messageEt;

    private LatLng donorLocation;

    String full_name = "", mobile = "", noOfBags = "", addressValue = "", date_of_birth = "", blood_group = "", countryValue = "", stateValue = "", cityValue = "", message = "", latitude = "", longitude = "";


    LinearLayout lladdress;
    EditText nameEt;
    Button submit;
    private EditText dobEt, lastDonatedDateEt, mobileEt;
    private Methods methods;
    private ProgressDialog progress;
    private PrefManager pref;
    private JSONObject userData;
    private String user_id;
    Calendar dobCalendar = Calendar.getInstance();
    private EditText noOfBagsEt;
    private RequestsItem request_item;

    private ArrayAdapter<String> bloodGroupAdapter;
    private ArrayAdapter<String> countriesSpinnerAdapter;
    private ArrayAdapter<String> statesSpinnerAdapter;
    private ArrayAdapter<String> citiesSpinnerAdapter;
    private int SELECT_ADDRESS_REQUEST = 87;
    private int selectedCountryPosition = 0;
    private int selectedStatePosition = 0;
    private int selectedCityPosition = 0;
    private int selectedBloodGroup = 0;
    private int selectedDonorType = 0;
    private int AUTOCOMPLETE_REQUEST_CODE = 23948;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_add_request);


        setUpSupportToolbar();


        initViews();

        get_user_data();

        Intent data = getIntent();
        try {
            reqId = data.getStringExtra("reqId");

            request_data = data.getStringExtra("request_data");

            if (request_data != null) {

                Gson gson = new Gson();
                Type type = new TypeToken<RequestsItem>() {
                }.getType();

                request_item = gson.fromJson(request_data, type);

                nameEt.setText(request_item.getFullName());
                mobileEt.setText(request_item.getMobile());
                mobileEt.setEnabled(false);
                addressEt.setText(request_item.getAddress());

                latitude = request_item.getLatitude();
                longitude = request_item.getLongitude();

                addressEt.setText(request_item.getHospitalName());
                noOfBagsEt.setText(request_item.getNoOfBags());
                messageEt.setText(request_item.getMessage());


                dobEt.setText(request_item.getDateOfBirthSimple());
                setUpSpinnersAdapter(request_item.getBloodGroup());

                get_some_data(COUNTRIES_LIST, null, request_item.getCountry(), request_item.getState(), request_item.getCity());


            } else {
                setUpSpinnersAdapter(null);
                get_some_data(COUNTRIES_LIST, null, null, null, null);
            }


            if (reqId == null) {
                setTitle(getString(R.string.add_request));
            } else {
                setTitle(getString(R.string.edit_request));

            }

        } catch (Exception e) {
            e.getLocalizedMessage();
        }


        setUpAddressClick();


        this.submit.setOnClickListener(view -> {
            full_name = nameEt.getText().toString();
            mobile = mobileEt.getText().toString();
            noOfBags = noOfBagsEt.getText().toString();
            addressValue = addressEt.getText().toString();
            if (donorLocation != null) {
                latitude = String.valueOf(donorLocation.latitude);
                longitude = String.valueOf(donorLocation.longitude);
            }

            date_of_birth = format_date(dobCalendar);
            message = messageEt.getText().toString();

            if (TextUtils.isEmpty(full_name)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_enter_name, Toast.LENGTH_LONG).show();
            } else if (full_name.length() < 5) {
                Toast.makeText(AddRequestActivity.this, R.string.name_cannot_be_less_than, Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mobile)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_enter_mobile_number, Toast.LENGTH_LONG).show();
            } else if (mobile.length() < 11) {
                Toast.makeText(AddRequestActivity.this, R.string.mobile_no_cannot_less_than, Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(noOfBags)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_enter_no_of_bags, Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(addressValue)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_select_your_location, Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(blood_group)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_select_blood_group, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(countryValue)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_select_country, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(stateValue)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_select_state, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(cityValue)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_select_city, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(date_of_birth)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_select_date_of_birth, Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(message)) {
                Toast.makeText(AddRequestActivity.this, R.string.please_enter_some_message, Toast.LENGTH_SHORT).show();
            } else {
                if (reqId == null)
                    sendBloodRequest(true);
                else
                    sendBloodRequest(false);
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

    private void setUpSpinnersAdapter(String bloodGroup) {

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
                ((TextView) adapterView.getChildAt(0)).setTextColor(AddRequestActivity.this.getResources().getColor(R.color.black));
                ((TextView) adapterView.getChildAt(0)).setTextSize(14.0f);
            }
        });
    }


    private void setUpAddressClick() {
        lladdress.setOnClickListener(view -> AddRequestActivity.this.finish());
        lladdress.setOnClickListener(view -> {
            Intent intent = new Intent(AddRequestActivity.this, PicklocationActivity.class);
            intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
            AddRequestActivity.this.startActivityForResult(intent, 78);
        });
    }

    private void initViews() {

        nameEt = findViewById(R.id.name);
        mobileEt = findViewById(R.id.mobile);
        addressEt = findViewById(R.id.address);
        bloodGroupSpinner = findViewById(R.id.blood_group);
        dobEt = findViewById(R.id.date_of_birth);
        lastDonatedDateEt = findViewById(R.id.last_donated_date);
        messageEt = findViewById(R.id.message);
        submit = findViewById(R.id.submit);
        lladdress = findViewById(R.id.lladdress);
        noOfBagsEt = findViewById(R.id.no_of_bags);
        countrySpinner = findViewById(R.id.country);
        stateSpinner = findViewById(R.id.states);
        citySpinner = findViewById(R.id.city);

        methods = new Methods(this);

        progress = new ProgressDialog(this);
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
    }

    private void setupSpinners() {
        ArrayAdapter<String> countries = new ArrayAdapter<>(AddRequestActivity.this, R.layout.spinner, countriesNameList);
        countries.setDropDownViewResource(R.layout.spinner);
        countrySpinner.setAdapter(countries);

        stateSpinner.setEnabled(false);
        citySpinner.setEnabled(false);

        countrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                ((TextView) parent.getChildAt(0)).setTextSize(14);
                countryValue = countriesArrayList.get((countrySpinner.getSelectedItemPosition())).getId();

                if (selectedCountryPosition > 0) {
                } else {
                    get_some_data(STATES_LIST, countryValue, null, null, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                ((TextView) parent.getChildAt(0)).setTextSize(14);
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

        citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                ((TextView) parent.getChildAt(0)).setTextSize(14);
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

                    statesArrayList = gson.fromJson(states.toString(), states_type);
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

                    statesSpinnerAdapter = new ArrayAdapter<>(AddRequestActivity.this, R.layout.spinner, statesNameList);
                    statesSpinnerAdapter.setDropDownViewResource(R.layout.spinner);
                    stateSpinner.setAdapter(statesSpinnerAdapter);
                    stateSpinner.setEnabled(true);

                    new Handler().postDelayed(() -> {
                        if (stateId != null) {
                            stateSpinner.setSelectionM(selectedStatePosition);
                        }
                    }, 1000);


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


                    citiesSpinnerAdapter = new ArrayAdapter<>(AddRequestActivity.this, R.layout.spinner, citiesNameList);
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
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et.setText(sdf.format(calendar.getTime()));
    }

    private String format_date(Calendar calendar) {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

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


    private void sendBloodRequest(final boolean isNewRequest) {
        if (methods.isNetworkAvailable()) {

            if (!progress.isShowing()) {
                progress.show();
            }

            String url;

            if (isNewRequest) {
                url = UrlHelper.addBloodRequestUrl;
            } else {
                url = UrlHelper.editBloodRequestUrl;
            }

            try {
                user_id = userData.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            RequestQueue queue = Volley.newRequestQueue(AddRequestActivity.this);
            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {

                        if (progress.isShowing()) {
                            progress.dismiss();
                        }
                        Log.d(TAG, "onResponse: " + response);
                        JSONObject jsonObject;
                        try {

                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG);
                            JSONObject c;

                            if (jsonArray.getJSONObject(0).getBoolean("success")) {
                                if (reqId == null)
                                    Toast.makeText(AddRequestActivity.this, R.string.request_successfully_added, Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(AddRequestActivity.this, R.string.updated_successfully, Toast.LENGTH_SHORT).show();
                                AddRequestActivity.this.onBackPressed();
                            } else {
                                Toast.makeText(AddRequestActivity.this, R.string.operation_failed, Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    },
                    error -> {

                        Log.e(TAG, "onErrorResponse: " + error.toString());
                        if (progress.isShowing()) {
                            progress.dismiss();
                        }

                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("full_name", full_name);
                    params.put("mobile", mobile);
                    params.put("city", cityValue);
                    params.put("state", stateValue);
                    params.put("country", countryValue);
                    params.put("latitude", latitude);
                    params.put("longitude", longitude);
                    params.put("hospitalAddress", addressValue);
                    params.put("no_of_bags", noOfBags);
                    params.put("date_of_birth", date_of_birth);
                    params.put("blood_group", blood_group);
                    params.put("message", message);
                    params.put("addedBy", user_id);

                    if (!isNewRequest) {
                        params.put("id", reqId);
                    }

                    return params;
                }
            };

            queue.add(request);

        } else {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}