package com.pakbloodbank.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.pakbloodbank.adapters.AdapterBloodDonors;
import com.pakbloodbank.adapters.AdapterBloodGroups;
import com.pakbloodbank.items.Blog;
import com.pakbloodbank.items.Donor;
import com.pakbloodbank.items.DonorFilterItem;
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

import static com.pakbloodbank.utils.Constant.GRID_PADDING;
import static com.pakbloodbank.utils.Constant.TAG;

public class FragmentDonorHome extends Fragment {

    Context context;
    private TextView tv_user_city, tv_username;
    private ArrayList<Donor> recent_donors_list;
    private ArrayList<Donor> popular_donors_list;
    private ArrayList<Donor> nearby_donors_list;
    private ArrayList<Donor> donors_by_user;
    private ArrayList<Blog> blogs_list;

    private RecyclerView rv_recent_donors, rv_popular_donors, rv_nearby_donors, rv_donors_by_user, rv_blood_groups;
    private TextView view_recent_donors, view_popular_donors, view_nearby_donors, view_donors_by_user, view_all_donors;
    private String user_id;
    private Methods methods;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_donors_home, container, false);

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

        setData();
        return rootView;
    }

    private void setClickListeners(View v) {

        view_recent_donors.setOnClickListener(v15 -> {
            DonorFilterItem donorFilter = new DonorFilterItem().getFilter();

            donorFilter.setOrder_by("recent");



            FragmentDonorsList fragment = FragmentDonorsList.createInstance(donorFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.recent_donors), ((MainActivity) context).fm, false);
        });

        view_popular_donors.setOnClickListener(v14 -> {
            DonorFilterItem donorFilter = new DonorFilterItem().getFilter();
            donorFilter.setOrder_by("popular");

            FragmentDonorsList fragment = FragmentDonorsList.createInstance(donorFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.popular_donors), ((MainActivity) context).fm, false);
        });
        view_nearby_donors.setOnClickListener(v13 -> {
            DonorFilterItem donorFilter = new DonorFilterItem().getFilter();
            donorFilter.setOrder_by("nearby");

            FragmentDonorsList fragment = FragmentDonorsList.createInstance(donorFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.nearby_donors), ((MainActivity) context).fm, false);
        });

        view_donors_by_user.setOnClickListener(v12 -> {
            DonorFilterItem donorFilter = new DonorFilterItem().getFilter();
            donorFilter.setAdded_by(user_id);

            FragmentDonorsList fragment = FragmentDonorsList.createInstance(donorFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.donors_by_you), ((MainActivity) context).fm, false);
        });

        view_all_donors.setOnClickListener(v1 -> {
            DonorFilterItem donorFilter = new DonorFilterItem().getFilter();

            FragmentDonorsList fragment = FragmentDonorsList.createInstance(donorFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.donors_by_you), ((MainActivity) context).fm, false);
        });


    }

    private void getHomeData() {


        RequestQueue q = Volley.newRequestQueue(context);


        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.homeUrl, response -> {

            JSONObject object;
            try {

                object = new JSONObject(response);
                Log.d( "getHomeData: " , String.valueOf(object));
                object = object.getJSONObject("Home");

                Gson gson = new Gson();
                Type donorType = new TypeToken<List<Donor>>() {
                }.getType();
                Type blogType = new TypeToken<List<Blog>>() {
                }.getType();

                Log.d(TAG, String.valueOf(object.getJSONArray("NearByDonors")));
                recent_donors_list = gson.fromJson(object.getJSONArray("RecentDonors").toString(), donorType);
                popular_donors_list = gson.fromJson(object.getJSONArray("PopularDonors").toString(), donorType);
                nearby_donors_list = gson.fromJson(object.getJSONArray("NearByDonors").toString(), donorType);
                donors_by_user = gson.fromJson(object.getJSONArray("DonorByUser").toString(), donorType);
                blogs_list = gson.fromJson(object.getJSONArray("Blogs").toString(), blogType);


                //Log.d("donors by user", String.valueOf(donors_by_user));


                setupAdapters();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.e(TAG, "onErrorResponse: " + error, null)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("latitude", String.valueOf(Constant.curr_latitude));
                params.put("longitude", String.valueOf(Constant.curr_longitude));
                params.put("radius", "none");
                return params;
            }
        };

        q.add(request);

    }

    private void setupAdapters() {

//        rv_blood_groups.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_blood_groups.setLayoutManager(new GridLayoutManager(context, 4));
        rv_blood_groups.setAdapter(new AdapterBloodGroups(context, Constant.getBloodGroups(), "donor"));

        rv_popular_donors.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_nearby_donors.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_recent_donors.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        rv_donors_by_user.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        rv_popular_donors.setAdapter(new AdapterBloodDonors(context, popular_donors_list, false, true));
        rv_nearby_donors.setAdapter(new AdapterBloodDonors(context, nearby_donors_list, false, true));
        rv_recent_donors.setAdapter(new AdapterBloodDonors(context, recent_donors_list, false, true));
        rv_donors_by_user.setAdapter(new AdapterBloodDonors(context, donors_by_user, false, true));

    }

    private void setData() {
        try {

            Constant.asdf("userdataaaaa");
            Constant.asdf(((MainActivity) context).userData.toString());
            tv_username.setText(((MainActivity) context).userData.getString("name"));
            tv_user_city.setText(((MainActivity) context).userData.getString("city_name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initViews(View v) {


        tv_username = v.findViewById(R.id.tv_username);
        tv_user_city = v.findViewById(R.id.tv_user_city);

        rv_blood_groups = v.findViewById(R.id.rv_blood_groups);
        rv_donors_by_user = v.findViewById(R.id.rv_donors_by_user);
        rv_recent_donors = v.findViewById(R.id.rv_recent_donors);
        rv_nearby_donors = v.findViewById(R.id.rv_nearby_donors);
        rv_popular_donors = v.findViewById(R.id.rv_popular_donors);


        view_recent_donors = v.findViewById(R.id.view_recent_donors);
        view_popular_donors = v.findViewById(R.id.view_popular_donors);
        view_nearby_donors = v.findViewById(R.id.view_nearby_donors);
        view_donors_by_user = v.findViewById(R.id.view_donors_by_user);
        view_all_donors = v.findViewById(R.id.view_all_donors);

    }
}
