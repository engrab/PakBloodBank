package com.pakbloodbank.adding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pakbloodbank.R;
import com.pakbloodbank.activities.CompleteProfileActivity;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.activities.PhoneAuthActivity;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.PrefManager;
import com.pakbloodbank.utils.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {
    String mobile;
    private PrefManager pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        pref = new PrefManager(this);

        mobile = pref.getPhoneNumber();

        if (mobile == null) {
            // Choose authentication providers
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.PhoneBuilder().build());

// Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();
            signInLauncher.launch(signInIntent);
        }else {

            checkUserPhoneNumber(mobile);
        }

    }
    private void checkUserPhoneNumber(final String mobile) {
        try {

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Authenticating!");
            dialog.setTitle("Verification");
            dialog.setCancelable(false);

            dialog.show();

            final PrefManager pref = new PrefManager(this);


            RequestQueue q = Volley.newRequestQueue(this);

            StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.checkUserPhone,
                    response -> {

                        Log.e(Constant.TAG, "onResponse: " + response);

                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray array = obj.getJSONArray(Constant.TAG);
                            obj = array.getJSONObject(0);


                            if (obj.getBoolean("isRegistered") && obj.getBoolean("available")) {

                                JSONObject ob = obj.getJSONObject("user_data");
                                pref.saveUserData(ob.toString());

                                if (ob.getString("is_profile_saved").equalsIgnoreCase("1")) {

                                    gotoMain();

                                } else {

                                    gotoProfile();

                                }

                            } else if (!obj.getBoolean("isRegistered") && !obj.getBoolean("available")) {

                                Toast.makeText(SignInActivity.this, R.string.errorr, Toast.LENGTH_SHORT).show();

                            } else if (!obj.getBoolean("isRegistered") && obj.getBoolean("available")) {

                                JSONObject ob = obj.getJSONObject("user_data");
                                pref.saveUserData(ob.toString());

                                gotoProfile();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject userData = new JSONObject(pref.getUserData());

                            Toast.makeText(SignInActivity.this, R.string.success, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {

                        Toast.makeText(SignInActivity.this, "Error! Please try again after a while", Toast.LENGTH_SHORT).show();
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
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

    private void gotoMain() {
        startActivity(new Intent(SignInActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void gotoProfile() {

        boolean toUpdate = getIntent().getBooleanExtra("toUpdate", false);
        String user_data = getIntent().getStringExtra("user_data");

        Intent profile = new Intent(SignInActivity.this, CompleteProfileActivity.class);
        profile.putExtra("toUpdate", toUpdate);
        profile.putExtra("user_data", user_data);
        profile.putExtra("mobile", mobile);
        profile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profile);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String phone_number = user.getPhoneNumber().toString();

            pref.setPhoneNumber(phone_number);



            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }


    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );
}