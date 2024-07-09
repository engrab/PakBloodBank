package com.pakbloodbank.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.items.ItemBloodDonor;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.PrefManager;
import com.pakbloodbank.utils.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class FragmentReminder extends Fragment {

    private AppCompatButton submit;
    private EditText lastDonatedDateEt;
    private TextView lastDonatedTv;
    private TextView nextDonationTv;
    private Calendar myCalendar;
    private ArrayList<ItemBloodDonor> arrayList;
    private String[] donorItems = new String[]{};


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminder, container, false);

        myCalendar = Calendar.getInstance();

        initViews(rootView);

        handleDateEdittext();

        return rootView;
    }

    private void handleSubmit(final Calendar myCalendar) {
        submit.setOnClickListener(v -> askForAddEventInCalender(myCalendar));
    }

    private void askForAddEventInCalender(final Calendar cal) {
        try {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.create();
            alertDialog.setTitle(R.string.add_event);
            alertDialog.setMessage(R.string.do_you_want_to_set_reminder);
            alertDialog.setIcon(android.R.drawable.ic_lock_idle_alarm);
            alertDialog.setCancelable(false);

            alertDialog.setPositiveButton(R.string.yes_sure, (dialog, which) -> {

                askForDonorId();

                addToDeviceCalendar(cal.getTime(), getResources().getString(R.string.app_name) + getString(R.string.blood_donation_reminder_title),
                        getString(R.string.blood_donation_reminder_description),
                        getString(R.string.blood_donation_location_to_remind)
                );


//                    sendReminderRequest();
            });
            alertDialog.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        } catch (Exception e) {
            Log.d(Constant.TAG, "Show Dialog: " + e.getMessage());
        }
    }

    private void askForDonorId() {
        try {

            if (getActivity() != null) {

                String userId = "";
                PrefManager pref = new PrefManager(getActivity());
                // = pref.getUserId();

                try {
                    JSONObject user = new JSONObject(pref.getUserData());

                    userId = user.getString("id");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                final String android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);

                RequestQueue queue = Volley.newRequestQueue(getActivity());
                final String finalUserId = userId;
                StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.donorsByUserId,
                        response -> {

                            JSONObject jsonObject;
                            try {

                                jsonObject = new JSONObject(response);

                                //Log.d( "askForDonorId: ", String.valueOf(jsonObject));
                                arrayList = new ArrayList<>();

                                jsonObject = jsonObject.getJSONObject("List");

//                                    Gson gson = new Gson();
//                                    Type donorType = new TypeToken<List<ItemBloodDonor>>() {
//                                    }.getType();
//
                                arrayList = new ArrayList<>();

//                                    arrayList = gson.fromJson(jsonObject.getJSONArray("Donors").toString(), donorType);

                                JSONArray jsonArray = jsonObject.getJSONArray("Donors");
                                JSONObject c;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    c = jsonArray.getJSONObject(i);
                                    arrayList.add(new ItemBloodDonor(
                                            c.getString("id"),
                                            c.getString("full_name"),
                                            c.getString("mobile"),
                                            c.getString("city"),
                                            c.getString("address"),
                                            c.getString("date_of_birth"),
                                            c.getString("blood_group"),
                                            c.getString("country"),
                                            c.getString("state"),
                                            c.getString("lastDonationDate"),
                                            c.getString("habits")
                                    ));
//                                        donorItems[i] = c.getString("full_name");
                                }
//
                                showDialogOfDonors();

//                                    JSONArray cities = jsonObject.getJSONArray("Cities");
//                                    JSONObject cc;
//                                    donorsList = new String[cities.length()];
//                                    for (int i = 0; i < cities.length(); i++) {
//                                        cc = cities.getJSONObject(i);
//                                        donorsList[i] = cc.getString("city_name");
//                                    }
//
//                                    progressBar.setVisibility(View.GONE);
//                                    setAdapter();


                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        },
                        error -> {

                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
//                        params.put("devId", android_id);
                        params.put("addedBy", finalUserId);
                        return params;
                    }
                };

                queue.add(request);

            }


        } catch (Exception e) {
            Log.d(Constant.TAG, "Show Dialog: " + e.getMessage());
        }
    }

    private void showDialogOfDonors() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setCancelable(false);

        mBuilder.setTitle(R.string.choose_a_donor);

        Log.e(TAG, "showDialogOfDonors: " + arrayList.size());
        donorItems = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            donorItems[i] = arrayList.get(i).getName();
        }

        mBuilder.setSingleChoiceItems(donorItems, -1, (dialog, position) -> {

            sendReminderRequest(arrayList.get(position).getId(), lastDonatedDateEt.getText().toString());

            dialog.dismiss();
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    private void sendReminderRequest(final String id, final String lastDonationDate) {

        if (getActivity() != null) {
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.reminderUrl,
                    response -> {
                        JSONObject jsonObject;
                        try {

                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG);

                            Toast.makeText(getActivity(), R.string.information_saved, Toast.LENGTH_SHORT).show();

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
                    params.put("value", "1");
                    params.put("lastDonationDate", lastDonationDate);

                    return params;
                }
            };

            queue.add(request);

        }
    }

    private void handleDateEdittext() {

        lastDonatedDateEt.setInputType(InputType.TYPE_NULL);

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            updateLabel(myCalendar);

            myCalendar.add(Calendar.MONTH, 3);

            nextDonationTv.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(myCalendar.getTime()));

            handleSubmit(myCalendar);
            Log.e("asdfg", "onDateSet: called: " + new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(myCalendar.getTime()));

        };

        lastDonatedDateEt.setOnClickListener(v -> openDatePicker(date, myCalendar));

        lastDonatedDateEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                openDatePicker(date, myCalendar);
            }
        });
    }

    private void openDatePicker(DatePickerDialog.OnDateSetListener date, Calendar myCalendar) {
        new DatePickerDialog(getActivity(),
                date,
                myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(Calendar calendar) {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        lastDonatedDateEt.setText(sdf.format(calendar.getTime()));
        lastDonatedTv.setText(sdf.format(calendar.getTime()));
    }

    private void initViews(View v) {
        submit = v.findViewById(R.id.submitBtn);
        lastDonatedDateEt = v.findViewById(R.id.lastDonatedDateEt);
        lastDonatedTv = v.findViewById(R.id.lastDonatedDateTv);
        nextDonationTv = v.findViewById(R.id.nextDonationDateTv);
    }

    private void addToDeviceCalendar(Date startDate, String title, String description, String location) {

        String stDate;

        GregorianCalendar calDate = new GregorianCalendar();

        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy,MM,dd,HH,mm", Locale.getDefault());

        stDate = targetFormat.format(startDate);

        long startMillis;
        String dates[] = stDate.split(",");

        String SD_YeaR = dates[0];
        String SD_MontH = dates[1];
        String SD_DaY = dates[2];
        String SD_HouR = dates[3];
        String SD_MinutE = dates[4];

        calDate.set(
                (Integer.parseInt(SD_YeaR)),
                (Integer.parseInt(SD_MontH) - 1),
                (Integer.parseInt(SD_DaY)),
                (Integer.parseInt(SD_HouR)),
                (Integer.parseInt(SD_MinutE))
        );

        startMillis = calDate.getTimeInMillis();

        try {
            ContentResolver cr = getActivity().getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, calDate.getTimeInMillis() + 60 * 60 * 1000);
            values.put(CalendarContract.Events.TITLE, title);
            values.put(CalendarContract.Events.DESCRIPTION, description);
            values.put(CalendarContract.Events.EVENT_LOCATION, location);
            values.put(CalendarContract.Events.HAS_ALARM, 1);
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance()
                    .getTimeZone().getID());
            System.out.println(Calendar.getInstance().getTimeZone().getID());
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            long eventId = Long.parseLong(uri.getLastPathSegment());
            Log.d("Ketan_Event_Id", String.valueOf(eventId));

            Toast.makeText(getActivity(), R.string.reminder_saved, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
