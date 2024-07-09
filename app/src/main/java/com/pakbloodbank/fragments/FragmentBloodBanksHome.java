package com.pakbloodbank.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.adapters.AdapterBloodBanks;
import com.pakbloodbank.items.BloodBanksItem;
import com.pakbloodbank.items.BloodBankFilterItem;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.Methods;
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

public class FragmentBloodBanksHome extends Fragment {

    Context context;
    private TextView tv_user_city, tv_username;
    private ArrayList<BloodBanksItem> recent_blood_banks_list;
    private ArrayList<BloodBanksItem> popular_blood_banks_list;
    private ArrayList<BloodBanksItem> nearby_blood_banks_list;

    RecyclerView rv_recent_blood_banks, rv_popular_blood_banks, rv_nearby_blood_banks;
    TextView view_recent_blood_banks, view_popular_blood_banks, view_nearby_blood_banks;
    private String user_id;
    private Methods methods;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blood_banks_home, container, false);

        context = getActivity();

        user_id = "";
        try {
            assert context != null;
            user_id = ((MainActivity) context).userData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        methods = new Methods(context);


        initViews(rootView);

        setClickListeners(rootView);

        getHomeData();

        return rootView;
    }


    private void setClickListeners(View v) {

//        Toast.makeText(context, "Implement it!!!", Toast.LENGTH_SHORT).show();
        view_recent_blood_banks.setOnClickListener(v13 -> {
            BloodBankFilterItem requestFilter = new BloodBankFilterItem().getFilter();

            requestFilter.setOrder_by("recent");

            FragmentBloodBanksList fragment = FragmentBloodBanksList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.recent_blood_banks), ((MainActivity) context).fm, false);
        });

        view_popular_blood_banks.setOnClickListener(v12 -> {
            BloodBankFilterItem requestFilter = new BloodBankFilterItem().getFilter();
            requestFilter.setOrder_by("popular");

            FragmentBloodBanksList fragment = FragmentBloodBanksList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.popular_blood_banks), ((MainActivity) context).fm, false);
        });
        view_nearby_blood_banks.setOnClickListener(v1 -> {
            BloodBankFilterItem requestFilter = new BloodBankFilterItem().getFilter();
            requestFilter.setOrder_by("nearby");

            FragmentBloodBanksList fragment = FragmentBloodBanksList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.nearby_blood_banks), ((MainActivity) context).fm, false);
        });


    }

    private void getHomeData() {

        RequestQueue q = Volley.newRequestQueue(context);


        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.bloodBanksHomeUrl, response -> {

            JSONObject object;
            try {

                object = new JSONObject(response);
                object = object.getJSONObject("Home");

                Gson gson = new Gson();
                Type req_type = new TypeToken<List<BloodBanksItem>>() {
                }.getType();

                recent_blood_banks_list = gson.fromJson(object.getJSONArray("RecentBloodBanks").toString(), req_type);
                popular_blood_banks_list = gson.fromJson(object.getJSONArray("PopularBloodBanks").toString(), req_type);
                nearby_blood_banks_list = gson.fromJson(object.getJSONArray("NearByBloodBanks").toString(), req_type);


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
                params.put("radius", "none");
                return params;
            }
        };

        q.add(request);

    }

    private void setupAdapters() {

        rv_popular_blood_banks.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_nearby_blood_banks.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_recent_blood_banks.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        rv_popular_blood_banks.setAdapter(new AdapterBloodBanks(context, popular_blood_banks_list, false, true));
        rv_nearby_blood_banks.setAdapter(new AdapterBloodBanks(context, nearby_blood_banks_list, false, true));
        rv_recent_blood_banks.setAdapter(new AdapterBloodBanks(context, recent_blood_banks_list, false, true));

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews(View v) {


        tv_username = v.findViewById(R.id.tv_username);
        tv_user_city = v.findViewById(R.id.tv_user_city);

        rv_recent_blood_banks = v.findViewById(R.id.rv_recent_blood_banks);
        rv_nearby_blood_banks = v.findViewById(R.id.rv_nearby_blood_banks);
        rv_popular_blood_banks = v.findViewById(R.id.rv_popular_blood_banks);


        view_recent_blood_banks = v.findViewById(R.id.view_recent_blood_banks);
        view_popular_blood_banks = v.findViewById(R.id.view_popular_blood_banks);
        view_nearby_blood_banks = v.findViewById(R.id.view_nearby_blood_banks);
    }

}
