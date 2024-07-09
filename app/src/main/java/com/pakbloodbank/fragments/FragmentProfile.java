package com.pakbloodbank.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.adapters.AdapterBloodDonors;
import com.pakbloodbank.adapters.AdapterBloodRequests;
import com.pakbloodbank.items.Donor;
import com.pakbloodbank.items.DonorFilterItem;
import com.pakbloodbank.items.RequestFilterItem;
import com.pakbloodbank.items.RequestsItem;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.UrlHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pakbloodbank.utils.Constant.TAG;

public class FragmentProfile extends Fragment {

    Context context;
    private ArrayList<Donor> donors_by_user_list;
    private ArrayList<RequestsItem> requests_by_user_list;

    RecyclerView rv_donors_by_user, rv_requests_by_user;
    TextView view_donors_by_user, view_requests_by_user;
    private String user_id;


    private boolean isEdit;


    public static FragmentProfile createInstance(boolean isEdit) {

        FragmentProfile fragment = new FragmentProfile();
        fragment.init(isEdit);
        return fragment;
    }

    public void init(boolean isEdit) {
        this.isEdit = isEdit;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        context = getActivity();

        user_id = "";
        try {
            assert context != null;
            user_id = ((MainActivity) context).userData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        initViews(rootView);

        setClickListeners(rootView);

        getHomeData();
        return rootView;
    }


    private void setClickListeners(View v) {

//        Toast.makeText(context, "Implement it!!!", Toast.LENGTH_SHORT).show();
        view_donors_by_user.setOnClickListener(v12 -> {
            DonorFilterItem donorFilter = new DonorFilterItem().getFilter();

            donorFilter.setEdit(true);
            donorFilter.setOrder_by("recent");
            donorFilter.setAdded_by(user_id);

            FragmentDonorsList fragment = FragmentDonorsList.createInstance(donorFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.recent_blood_banks), ((MainActivity) context).fm, false);
        });

        view_requests_by_user.setOnClickListener(v1 -> {
            RequestFilterItem reqItem = new RequestFilterItem().getFilter();
            reqItem.setEdit(true);
            reqItem.setOrder_by("recent");
            reqItem.setAdded_by(user_id);
            FragmentRequestsList fragment = FragmentRequestsList.createInstance(reqItem);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.popular_blood_banks), ((MainActivity) context).fm, false);
        });

    }

    private void getHomeData() {

        RequestQueue q = Volley.newRequestQueue(context);


        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.profileUrl, response -> {

            JSONObject object;
            try {

                object = new JSONObject(response);
                object = object.getJSONObject("List");

                Gson gson = new Gson();
                Type req_type = new TypeToken<List<RequestsItem>>() {
                }.getType();
                Type donor_type = new TypeToken<List<Donor>>() {
                }.getType();

                requests_by_user_list = gson.fromJson(object.getJSONArray("Requests").toString(), req_type);
                donors_by_user_list = gson.fromJson(object.getJSONArray("Donors").toString(), donor_type);


                setupAdapters();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.e(TAG, "onErrorResponse: " + error, null)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(Constant.curr_latitude));
                params.put("longitude", String.valueOf(Constant.curr_longitude));
                params.put("addedBy", user_id);
                return params;
            }
        };

        q.add(request);

    }

    private void setupAdapters() {

        rv_requests_by_user.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_donors_by_user.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        rv_requests_by_user.setAdapter(new AdapterBloodRequests(context, requests_by_user_list, true, true));
        rv_donors_by_user.setAdapter(new AdapterBloodDonors(context, donors_by_user_list, true, true));

    }

    private void initViews(View v) {


        rv_donors_by_user = v.findViewById(R.id.rv_donors_by_user);
        rv_requests_by_user = v.findViewById(R.id.rv_requests_by_user);


        view_donors_by_user = v.findViewById(R.id.view_donors_by_user);
        view_requests_by_user = v.findViewById(R.id.view_requests_by_user);
    }
}
