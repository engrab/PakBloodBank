package com.pakbloodbank.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.pakbloodbank.items.LanguageItem;
import com.pakbloodbank.utils.Methods;
import com.pakbloodbank.utils.UrlHelper;
import com.pakbloodbank.utils.custom.SpinnerWithImageAdapter;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.PrefManager;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.pakbloodbank.utils.MyApplication.LANGUAGE_ARABIC;
import static com.pakbloodbank.utils.MyApplication.LANGUAGE_ARABIC_COUNTRY;
import static com.pakbloodbank.utils.MyApplication.LANGUAGE_ENGLISH;
import static com.pakbloodbank.utils.MyApplication.LANGUAGE_ENGLISH_COUNTRY;
import static com.pakbloodbank.utils.MyApplication.LANGUAGE_URDU;
import static com.pakbloodbank.utils.MyApplication.LANGUAGE_URDU_COUNTRY;


public class PhoneAuthActivity extends AppCompatActivity implements
		View.OnClickListener {

	private static final String TAG = "SignUpActivity";

	private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

	private static final int STATE_INITIALIZED = 1;
	private static final int STATE_CODE_SENT = 2;
	private static final int STATE_VERIFY_FAILED = 3;
	private static final int STATE_VERIFY_SUCCESS = 4;
	private static final int STATE_SIGNIN_FAILED = 5;
	private static final int STATE_SIGNIN_SUCCESS = 6;

	private FirebaseAuth mAuth;

	private boolean mVerificationInProgress = false;
	private String mVerificationId;
	private PhoneAuthProvider.ForceResendingToken mResendToken;
	private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

	private ViewGroup mSignedInViews;

	private TextView mStatusText;
	private TextView mDetailText;

	private EditText mPhoneNumberField;
	private EditText mVerificationField;

	CountryCodePicker ccp;

	private Button mStartButton, mVerifyButton, mResendButton;
	private String phone;




	ArrayList<LanguageItem> languageItems;
	String[] langNames = {};
	int[] langFlags = {};
	boolean firstTime = false;

	PrefManager pref;

	int selectedLanguageIndex = 0;



	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phone_auth);

		if (savedInstanceState != null) {
			onRestoreInstanceState(savedInstanceState);
		}



		Methods methods = new Methods(PhoneAuthActivity.this);

		pref = new PrefManager(this);


		languageItems = new ArrayList<>();

		languageItems.add(new LanguageItem("English", R.drawable.english));


		langNames = new String[languageItems.size()];
		langFlags = new int[languageItems.size()];

		for (int i = 0; i < languageItems.size(); i++) {
			langNames[i] = languageItems.get(i).getName();
			langFlags[i] = languageItems.get(i).getImage();
		}





		mSignedInViews = findViewById(R.id.signedInButtons);

		mStatusText = findViewById(R.id.status);
		mDetailText = findViewById(R.id.detail);

		mPhoneNumberField = findViewById(R.id.fieldPhoneNumber);
		mVerificationField = findViewById(R.id.fieldVerificationCode);

		ccp = findViewById(R.id.ccp);
		ccp.registerCarrierNumberEditText(mPhoneNumberField);

		mStartButton = findViewById(R.id.buttonStartVerification);
		mVerifyButton = findViewById(R.id.buttonVerifyPhone);
		mResendButton = findViewById(R.id.buttonResend);
		Button mSignOutButton = findViewById(R.id.signOutButton);

		mStartButton.setOnClickListener(this);
		mVerifyButton.setOnClickListener(this);
		mResendButton.setOnClickListener(this);
		mSignOutButton.setOnClickListener(this);

		mAuth = FirebaseAuth.getInstance();
		mAuth.setLanguageCode("en");

		PrefManager pref = new PrefManager(this);

		if (isEmulator()) {
			phone = "03001122334";
			pref.setPhoneNumber(phone);
			gotoProfile();
		}

		mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

			@Override
			public void onVerificationCompleted(PhoneAuthCredential credential) {
				Log.d(TAG, "onVerificationCompleted:" + credential);
				mVerificationInProgress = false;

				updateUI(STATE_VERIFY_SUCCESS, credential);
				signInWithPhoneAuthCredential(credential);
			}

			@Override
			public void onVerificationFailed(FirebaseException e) {
				Log.w(TAG, "onVerificationFailed", e);
				mVerificationInProgress = false;

				if (e instanceof FirebaseAuthInvalidCredentialsException) {
					mPhoneNumberField.setError(getString(R.string.invalid_phone));
				} else if (e instanceof FirebaseTooManyRequestsException) {
					Snackbar.make(findViewById(android.R.id.content), R.string.quota_ex,
							Snackbar.LENGTH_SHORT).show();
				}

				updateUI(STATE_VERIFY_FAILED);
			}

			@Override
			public void onCodeSent(String verificationId,
			                       PhoneAuthProvider.ForceResendingToken token) {
				Log.d(TAG, "onCodeSent:" + verificationId);

				mVerificationId = verificationId;
				mResendToken = token;

				updateUI(STATE_CODE_SENT);
			}
		};




		String prevLang = pref.getAppLanguage();

		switch (prevLang) {
			case LANGUAGE_ARABIC:
				selectedLanguageIndex = 1;
				break;
			case LANGUAGE_URDU:
				selectedLanguageIndex = 2;
				break;
			default:
				selectedLanguageIndex = 0;
		}


//        spinner.post(new Runnable() {
//            @Override
//            public void run() {
		firstTime = false;




	}

	public static boolean isEmulator() {

//		Log.d("EMULATOR", "FINGERPRINT: " + Build.FINGERPRINT);
//		Log.d("EMULATOR", "MODEL: " +Build.MODEL);
//		Log.d("EMULATOR", "MANUFACTURER: " +Build.MANUFACTURER);
//		Log.d("EMULATOR", "BRAND: " +Build.BRAND);
//		Log.d("EMULATOR", "PRODUCT: " +Build.PRODUCT);
//		Log.d("EMULATOR", "DEVICE: " +Build.DEVICE);

		return Build.FINGERPRINT.startsWith("generic")
				|| Build.FINGERPRINT.startsWith("unknown")
				|| Build.MODEL.contains("google_sdk")
				|| Build.MODEL.contains("sdk_gphone_x86_arm")
				|| Build.MODEL.contains("Emulator")
				|| Build.MODEL.contains("Android SDK built for x86")
				|| Build.MANUFACTURER.contains("Genymotion")
				|| (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
				|| "google_sdk".equals(Build.PRODUCT);
	}

	@Override
	public void onStart() {
		super.onStart();
		FirebaseUser currentUser = mAuth.getCurrentUser();
		updateUI(currentUser);

		if (mVerificationInProgress && validatePhoneNumber()) {
			startPhoneNumberVerification(ccp.getFullNumberWithPlus());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
	}


	private void startPhoneNumberVerification(String phoneNumber) {
		phone = phoneNumber;
		PhoneAuthProvider.getInstance().verifyPhoneNumber(
				phoneNumber,        // Phone number to verify
				60,                 // Timeout duration
				TimeUnit.SECONDS,   // Unit of timeout
				this,               // Activity (for callback binding)
				mCallbacks);        // OnVerificationStateChangedCallbacks

		mVerificationInProgress = true;
	}

	private void verifyPhoneNumberWithCode(String verificationId, String code) {
		PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
		signInWithPhoneAuthCredential(credential);
	}

	private void resendVerificationCode(String phoneNumber,
	                                    PhoneAuthProvider.ForceResendingToken token) {
		PhoneAuthProvider.getInstance().verifyPhoneNumber(
				phoneNumber,        // Phone number to verify
				60,                 // Timeout duration
				TimeUnit.SECONDS,   // Unit of timeout
				this,               // Activity (for callback binding)
				mCallbacks,         // OnVerificationStateChangedCallbacks
				token);             // ForceResendingToken from callbacks
	}

	private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, task -> {
					if (task.isSuccessful()) {
						Log.d(TAG, "signInWithCredential:success");

						FirebaseUser user = null;
						user = task.getResult().getUser();
						updateUI(STATE_SIGNIN_SUCCESS, user);
					} else {
						Log.w(TAG, "signInWithCredential:failure", task.getException());
						if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
							mVerificationField.setError(getString(R.string.invalid_code));
						}
						updateUI(STATE_SIGNIN_FAILED);
					}
				});
	}

	private void signOut() {
		mAuth.signOut();
		updateUI(STATE_INITIALIZED);
	}

	private void updateUI(int uiState) {
		updateUI(uiState, mAuth.getCurrentUser(), null);
	}

	private void updateUI(FirebaseUser user) {
		if (user != null) {
			updateUI(STATE_SIGNIN_SUCCESS, user);
		} else {
			updateUI(STATE_INITIALIZED);
		}
	}

	private void updateUI(int uiState, FirebaseUser user) {
		updateUI(uiState, user, null);
	}

	private void updateUI(int uiState, PhoneAuthCredential cred) {
		updateUI(uiState, null, cred);
	}

	private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
		switch (uiState) {
			case STATE_INITIALIZED:
				enableViews(mStartButton, mPhoneNumberField);
				disableViews(mVerifyButton, mResendButton, mVerificationField);
				mDetailText.setText(null);
				break;
			case STATE_CODE_SENT:
				enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
				disableViews(mStartButton);
				mDetailText.setText(getString(R.string.code_sent));
				break;
			case STATE_VERIFY_FAILED:
				enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
				mDetailText.setText(getString(R.string.verification_failed));
				break;
			case STATE_VERIFY_SUCCESS:
				disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
				mDetailText.setText(getString(R.string.verification_success));

				if (cred != null) {
					if (cred.getSmsCode() != null) {
						mVerificationField.setText(cred.getSmsCode());
					} else {
						mVerificationField.setText(getString(R.string.instant_validation));
					}
				}

				break;
			case STATE_SIGNIN_FAILED:
				mDetailText.setText(getString(R.string.signin_failed));
				break;
			case STATE_SIGNIN_SUCCESS:
				break;
		}

		if (user == null) {
			mSignedInViews.setVisibility(View.GONE);

			mStatusText.setText(getString(R.string.signed_out));
		} else {
			finalizeLogin();
		}
	}

	private void finalizeLogin() {
		Log.e(TAG, "finalizeLogin: called");

		mSignedInViews.setVisibility(View.VISIBLE);

		PrefManager p = new PrefManager(this);
		if (p.isFirstRun()) {
			if (phone != null) {
				checkUserPhoneNumber(phone);
			} else {
				phone = p.getPhoneNumber();
				if (phone != null) {
					checkUserPhoneNumber(phone);
				}
			}
		} else
			gotoProfile();
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

								Toast.makeText(PhoneAuthActivity.this, R.string.errorr, Toast.LENGTH_SHORT).show();

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

							Toast.makeText(PhoneAuthActivity.this, R.string.success, Toast.LENGTH_SHORT).show();

						} catch (JSONException e) {
							e.printStackTrace();
						}
					},
					error -> {

						Toast.makeText(PhoneAuthActivity.this, "Error! Please try again after a while", Toast.LENGTH_SHORT).show();
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
		startActivity(new Intent(PhoneAuthActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
	}

	private void gotoProfile() {

		boolean toUpdate = getIntent().getBooleanExtra("toUpdate", false);
		String user_data = getIntent().getStringExtra("user_data");

		Intent profile = new Intent(PhoneAuthActivity.this, CompleteProfileActivity.class);
		profile.putExtra("toUpdate", toUpdate);
		profile.putExtra("user_data", user_data);
		profile.putExtra("mobile", phone);
		profile.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(profile);
	}

	private boolean validatePhoneNumber() {
		String phoneNumber = ccp.getFullNumberWithPlus();
		if (TextUtils.isEmpty(phoneNumber)) {
			mPhoneNumberField.setError(getString(R.string.invalid_phone));
			return false;
		}

		return true;
	}

	private void enableViews(View... views) {
		for (View v : views) {
			v.setEnabled(true);
		}
	}

	private void showViews(View... views) {
		for (View v : views) {
			v.setVisibility(View.VISIBLE);
		}
	}

	private void disableViews(View... views) {
		for (View v : views) {
			v.setEnabled(false);
		}
	}

	private void hideViews(View... views) {
		for (View v : views) {
			v.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.buttonStartVerification:
				if (!validatePhoneNumber()) {
					return;
				}

				startPhoneNumberVerification(ccp.getFullNumberWithPlus());
				break;
			case R.id.buttonVerifyPhone:
				String code = mVerificationField.getText().toString();
				if (TextUtils.isEmpty(code)) {
					mVerificationField.setError(getString(R.string.cannot_be_empty));
					return;
				}

				verifyPhoneNumberWithCode(mVerificationId, code);
				break;
			case R.id.buttonResend:
				resendVerificationCode(ccp.getFullNumberWithPlus(), mResendToken);
				break;
			case R.id.signOutButton:
				signOut();
				break;
		}
	}

	private void initSpinnerAdapters(String[] array, Spinner spinner) {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(PhoneAuthActivity.this, android.R.layout.simple_spinner_item, array);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
}