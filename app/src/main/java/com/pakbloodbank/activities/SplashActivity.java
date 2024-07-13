    package com.pakbloodbank.activities;

    import android.Manifest;
    import android.content.Intent;
    import android.content.res.Resources;
    import android.graphics.Color;
    import android.os.Build;
    import android.os.Bundle;
    import android.os.Handler;
    import android.util.TypedValue;
    import android.view.View;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.toolbox.StringRequest;
    import com.android.volley.toolbox.Volley;
    import com.pakbloodbank.R;
    import com.pakbloodbank.utils.Constant;
    import com.pakbloodbank.utils.Methods;
    import com.pakbloodbank.utils.PrefManager;
    import com.pakbloodbank.utils.UrlHelper;
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

    public class SplashActivity extends AppCompatActivity {

        private PrefManager pref;
        boolean checkRegistration = false;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }


            Methods methods = new Methods(SplashActivity.this);

            pref = new PrefManager(this);

            Resources r       = getResources();
            float     padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constant.GRID_PADDING, r.getDisplayMetrics());
            Constant.columnWidth  = (int) ((methods.getScreenWidth(this) - ((Constant.NUM_OF_COLUMNS + 1) * padding)) / Constant.NUM_OF_COLUMNS);
            Constant.columnHeight = (int) (Constant.columnWidth * 1.44);


//comment for testing purpose . in productioin we uncomment it.


//            if (methods.isNetworkAvailable()) {
//
//                if (Methods.check_internet(this)) {
//                    if (Methods.check_gps(this)) {
//                        checkPermissions(true);
//                    }
//
//
//                }
//
//            } else {
//                Constant.showNoNetwork(this);
//            }
//
//
//
//            setRegisteredHandler();


            gotoHome();
        }

        private void setRegisteredHandler() {
            final int delay = 200;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (checkRegistration) {

                        isMobileRegistered(pref.getPhoneNumber());
                    }

                }
            }, delay);
        }






        private void checkPermissions(final boolean checkNextAction) {
            Dexter.withContext(this)
                    .withPermissions(
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ).withListener(new MultiplePermissionsListener() {

                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        if (checkNextAction) {
                            checkRegistration = true;
                        } else {
                            checkRegistration = false;
                        }
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    token.continuePermissionRequest();


                }
            }).check();
        }



        private void isMobileRegistered(final String mobile) {

            if (mobile == null) {
                gotoPhoneAuthActivity(false, null);
                return;
            }
            checkUserPhoneNumber(mobile);
        }

        private void checkUserPhoneNumber(final String mobile) {
            try {

                RequestQueue q = Volley.newRequestQueue(this);

                StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.checkUserPhone,
                        response -> {


                            try {
                                JSONObject obj   = new JSONObject(response);
                                JSONArray  array = obj.getJSONArray(Constant.TAG);
                                obj = array.getJSONObject(0);


                                if (obj.getBoolean("isRegistered") && obj.getBoolean("available")) {

                                    JSONObject ob = obj.getJSONObject("user_data");
                                    pref.saveUserData(ob.toString());

                                    if (ob.getString("is_profile_saved").equalsIgnoreCase("1")) {

                                        gotoHome();

                                    } else {

                                        gotoPhoneAuthActivity(true, ob.toString());

                                    }
                                } else if (!obj.getBoolean("isRegistered") && !obj.getBoolean("available")) {

                                    Toast.makeText(SplashActivity.this, R.string.error, Toast.LENGTH_SHORT).show();

                                } else if (!obj.getBoolean("isRegistered") && obj.getBoolean("available")) {

                                    JSONObject ob = obj.getJSONObject("user_data");
                                    pref.saveUserData(ob.toString());


                                    gotoPhoneAuthActivity(true, ob.toString());

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> Toast.makeText(SplashActivity.this, R.string.try_again_after_a_while, Toast.LENGTH_SHORT).show()
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("mobile", mobile);
                        return params;
                    }
                };

                q.add(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void gotoPhoneAuthActivity(boolean toUpdate, String object) {

            Intent intent = new Intent(SplashActivity.this, PhoneAuthActivity.class);
            intent.putExtra("toUpdate", toUpdate);
            intent.putExtra("user_data", object);
            startActivity(intent);
            finish();
        }

        private void gotoHome() {
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 100);

        }
    }
