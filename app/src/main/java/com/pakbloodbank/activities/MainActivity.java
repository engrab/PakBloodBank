package com.pakbloodbank.activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.fragments.FragmentBloodBanksHome;
import com.pakbloodbank.fragments.FragmentCalls;
import com.pakbloodbank.fragments.FragmentDonorHome;
import com.pakbloodbank.fragments.FragmentProfile;
import com.pakbloodbank.fragments.FragmentReminder;
import com.pakbloodbank.fragments.FragmentRequestsHome;
import com.pakbloodbank.items.ItemCallRate;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.PrefManager;
import com.pakbloodbank.utils.SettingDialogs;
import com.pakbloodbank.utils.UrlHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.android.volley.VolleyLog.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  SettingDialogs.OnDialogSubmit {

    public  FragmentManager      fm;
    public  JSONObject           userData;
    private PrefManager          pref;
    private BottomNavigationView bottomNavView;

    private Button add_donor, add_blood_request;
    SettingDialogs dialog;
    private boolean widget=false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm      = getSupportFragmentManager();

        this.dialog = new SettingDialogs(this, getLayoutInflater());

        get_user_data();

        initViews();

        setUpListeners();

        bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        loadDonorHome();


    }



    private void setUpListeners() {
        add_donor.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddDonorActivity.class));


        });

        add_blood_request.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddRequestActivity.class));


        });
    }

    private void loadDonorHome() {
        loadFrag(new FragmentDonorHome(), getResources().getString(R.string.title_donors), fm, false);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.title_donors));

        add_donor.setVisibility(View.VISIBLE);
        add_blood_request.setVisibility(View.GONE);

    }



    private void loadRemindersFragment() {
        loadFrag(new FragmentReminder(), getResources().getString(R.string.reminder), fm, false);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.reminder));

        add_donor.setVisibility(View.GONE);
        add_blood_request.setVisibility(View.GONE);

    }

    private void loadCallsFragment() {
        loadFrag(new FragmentCalls(), getResources().getString(R.string.calls), fm, false);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.calls));

        add_donor.setVisibility(View.GONE);
        add_blood_request.setVisibility(View.GONE);

    }



    private void get_user_data() {
        pref = new PrefManager(this);
        try {
            userData = new JSONObject(pref.getUserData());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        bottomNavView = findViewById(R.id.bottom_nav_view);

        add_donor         = findViewById(R.id.add_donor);
        add_blood_request = findViewById(R.id.add_request);

    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm, boolean addToBackStack) {
        FragmentTransaction ft = fm.beginTransaction();
        if (name.equals(getResources().getString(R.string.title_donors))) {
            addToBackStack = false;
            fm.popBackStackImmediate();
            fm.popBackStack();
        }
        if (addToBackStack)
            ft.addToBackStack(name);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.frame_layout, f1, name);
        ft.commit();
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(name);
    }


    public void setActionTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {

        bottomNavView.setVisibility(View.VISIBLE);

        if (!getSupportActionBar().getTitle().equals(getResources().getString(R.string.title_donors))) {
            loadDonorHome();

            bottomNavView.setSelectedItemId(R.id.nav_donors_home);

        } else {


            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);

            } else {
                super.onBackPressed();
            }
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            loadDonorHome();
        } else if (id == R.id.nav_reminder) {
            loadRemindersFragment();
        } else if (id == R.id.nav_calls) {
            loadCallsFragment();
        }   else if (id == R.id.nav_sign_out) {
            signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_theme) {
            this.dialog.selectTheme(this.pref.getNightMode());
        }
        if (menuItem.getItemId() == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(menuItem);
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {



            switch (item.getItemId()) {
                case R.id.nav_donors_home:

                    loadDonorHome();

                    return true;
                case R.id.nav_blood_requests:

                    loadRequestHome();

                    return true;
//                case R.id.nav_blood_banks:
//
//                    loadBloodBanksHome();
//
//                    return true;
                case R.id.nav_profile:
                    add_donor.setVisibility(View.GONE);
                    add_blood_request.setVisibility(View.GONE);
                    loadProfileHome();
                    return true;

            }
            return false;
        }
    };

    private void loadBloodBanksHome() {
        loadFrag(new FragmentBloodBanksHome(), getResources().getString(R.string.title_blood_banks), fm, false);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.title_blood_banks));

        add_donor.setVisibility(View.GONE);
        add_blood_request.setVisibility(View.GONE);
    }

    private void loadProfileHome() {
        loadFrag(new FragmentProfile(), getResources().getString(R.string.title_profile), fm, false);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.title_profile));

        add_donor.setVisibility(View.GONE);
        add_blood_request.setVisibility(View.GONE);
    }



    private void loadRequestHome() {
        loadFrag(new FragmentRequestsHome(), getResources().getString(R.string.title_blood_requests), fm, false);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(getResources().getString(R.string.title_blood_requests));

        add_donor.setVisibility(View.GONE);
        add_blood_request.setVisibility(View.VISIBLE);
    }


    public void CallToPHone(String phone) {


        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            checkPermissions(callIntent);
            return;
        }
        startActivity(callIntent);
    }


    private void checkPermissions(final Intent callIntentAfterGrant) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ).withListener(new MultiplePermissionsListener() {

            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    startActivity(callIntentAfterGrant);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                token.continuePermissionRequest();

            }
        }).check();
    }

    public void CallToDonorPHone(String phone, String donorId) {

        String userId = "";

        try {
            JSONObject user = new JSONObject(pref.getUserData());

            userId = user.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        saveCall(phone, userId, donorId);

    }


    public void saveCall(final String mobile, final String call_from, final String call_to) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.saveCallUrl,
                response -> {
                    JSONObject jsonObject;
                    try {

                        Log.e(TAG, "onResponse: " + response);

                        CallToPHone(mobile);

                        jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG);

                        if (jsonArray.getJSONObject(0).getBoolean("success")) {

                            Toast.makeText(MainActivity.this, R.string.call_saved, Toast.LENGTH_SHORT).show();

                        }

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                },
                error -> {
                    Log.d(TAG, "onErrorResponse: error: " + error.toString());
                    CallToPHone(call_to);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("call_to", call_to);
                params.put("call_from", call_from);

                return params;
            }
        };

        queue.add(request);
    }


    public void rateCall(final ItemCallRate item, final String fragmentName) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.rateCallUrl,
                response -> {
                    JSONObject jsonObject;
                    try {

                        Log.e(TAG, "onResponse: " + response);
                        jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG);

                        if (jsonArray.getJSONObject(0).getBoolean("success")) {

                            Toast.makeText(MainActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                        } else {

                        }

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                },
                error -> Log.d(TAG, "onErrorResponse: error: " + error.toString())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", item.getId());
                params.put("subject", item.getSubject());
                params.put("feedback", item.getFeedback());
                params.put("donatedOrNot", item.getDonated());
                return params;
            }
        };

        queue.add(request);
    }


    public void confirmRequest(final String id, String shortMsg, final String what, final String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to " + shortMsg + "?");
        builder.setPositiveButton("Yes, " + what, (dialog, which) -> sendUpdateRequest(type, id, what));
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();

    }

    public void sendUpdateRequest(final String requestType, final String id, final String what) {

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.updateRequest,
                response -> {
                    JSONObject jsonObject;
                    try {

                        Constant.asdf(TAG + " onResponse: " + response);

                        jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG);

                        if (jsonArray.getJSONObject(0).getBoolean("success")) {
                            Toast.makeText(MainActivity.this, R.string.success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                },
                error -> Log.d(TAG, "onErrorResponse: error: " + error.toString())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("id", id);
                params.put("type", requestType);
                params.put("what", what);

                return params;
            }
        };

        queue.add(request);
    }

    public void signOut() {
        PrefManager pref = new PrefManager(this);
        pref.clearPref();

        FirebaseAuth.getInstance().signOut();
        finish();
        startActivity(new Intent(this, SplashActivity.class));
    }

    public void OnDialogSubmit(Intent intent2) {
        StartFeatureActivity(intent2);
    }

    private void StartFeatureActivity(Intent intent2) {
        Bundle bundle = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        intent2.putExtra(Constant.EXTRA_ACTION, this.widget);
        if (this.widget) {
            startActivityForResult(intent2, 105, bundle);
        } else {
            startActivity(intent2, bundle);
        }
    }

    public void OnThemeSubmit(int i) {
        this.pref.setNightMode(i);
        Bundle bundle = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle();
        Intent intent2 = new Intent(this, SplashActivity.class);
        finish();
        startActivity(intent2, bundle);
    }


}
