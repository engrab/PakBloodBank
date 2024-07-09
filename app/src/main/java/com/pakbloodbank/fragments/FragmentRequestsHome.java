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
import com.pakbloodbank.adapters.AdapterBloodGroups;
import com.pakbloodbank.adapters.AdapterBloodRequests;
import com.pakbloodbank.items.RequestFilterItem;
import com.pakbloodbank.items.RequestsItem;
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

public class FragmentRequestsHome extends Fragment {

    Context context;
    private TextView tv_user_city, tv_username;
    private ArrayList<RequestsItem> recent_requests_list;
    private ArrayList<RequestsItem> popular_requests_list;
    private ArrayList<RequestsItem> nearby_requests_list;
    private ArrayList<RequestsItem> requests_by_user;

    RecyclerView rv_recent_requests, rv_popular_requests, rv_nearby_requests, rv_requests_by_user, rv_blood_groups;
    TextView view_recent_requests, view_popular_requests, view_nearby_requests, view_requests_by_user, view_all_requests;
    private String user_id;
    private Methods methods;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests_home, container, false);

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

        view_recent_requests.setOnClickListener(v15 -> {
            RequestFilterItem requestFilter = new RequestFilterItem().getFilter();

            requestFilter.setOrder_by("recent");

            FragmentRequestsList fragment = FragmentRequestsList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.recent_requests), ((MainActivity) context).fm, false);
        });

        view_popular_requests.setOnClickListener(v14 -> {
            RequestFilterItem requestFilter = new RequestFilterItem().getFilter();
            requestFilter.setOrder_by("popular");

            FragmentRequestsList fragment = FragmentRequestsList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.popular_requests), ((MainActivity) context).fm, false);
        });
        view_nearby_requests.setOnClickListener(v13 -> {
            RequestFilterItem requestFilter = new RequestFilterItem().getFilter();
            requestFilter.setOrder_by("nearby");

            FragmentRequestsList fragment = FragmentRequestsList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.nearby_requests), ((MainActivity) context).fm, false);
        });

        view_requests_by_user.setOnClickListener(v12 -> {
            RequestFilterItem requestFilter = new RequestFilterItem().getFilter();
            requestFilter.setAdded_by(user_id);

            FragmentRequestsList fragment = FragmentRequestsList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.requests_by_you), ((MainActivity) context).fm, false);
        });

        view_all_requests.setOnClickListener(v1 -> {
            RequestFilterItem requestFilter = new RequestFilterItem().getFilter();

            FragmentRequestsList fragment = FragmentRequestsList.createInstance(requestFilter);
            ((MainActivity) context).loadFrag(fragment, getString(R.string.all_requests), ((MainActivity) context).fm, false);
        });


    }

    private void getHomeData() {

        String user_id = "";
        try {
            user_id = ((MainActivity) context).userData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String finalUser_id = user_id;

        RequestQueue q = Volley.newRequestQueue(context);


        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.requestsHomeUrl, response -> {

            JSONObject object;
            Log.d(TAG, response);
            List<RequestsItem> requestsItemList;

            try {



                object = new JSONObject(response);
//                JSONObject listObject=object.getJSONObject("List");
//                JSONArray requestArray=listObject.getJSONArray("Requests");
//                for (int i=0; i<requestArray.length(); i++)
//                {
//                   JSONObject requestObject=requestArray.getJSONObject(i);
//                    Log.d("Request Object", String.valueOf(requestObject));
//
//                }


                requestsItemList = new ArrayList<>();


                //RequestsItem requestsItemListFull=gson.fromJson(object.toString(), RequestsItem.class);






                Log.d("Request object", String.valueOf(object));
                object = object.getJSONObject("Home");

                Gson gson = new Gson();
                Type req_type = new TypeToken<List<RequestsItem>>() {
                }.getType();
                recent_requests_list = gson.fromJson(object.getJSONArray("RecentRequests").toString(), req_type);
                popular_requests_list = gson.fromJson(object.getJSONArray("PopularRequests").toString(), req_type);
                nearby_requests_list = gson.fromJson(object.getJSONArray("NearByRequests").toString(), req_type);
                requests_by_user = gson.fromJson(object.getJSONArray("RequestsByUser").toString(), req_type);


                setupAdapters();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> Log.e(TAG, "onErrorResponse: " + error, null)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", finalUser_id);
                params.put("latitude", String.valueOf(Constant.curr_latitude));
                params.put("longitude", String.valueOf(Constant.curr_longitude));
                params.put("radius", "none");
                return params;
            }
        };

        q.add(request);

    }

    private void setupAdapters() {

        rv_blood_groups.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_blood_groups.setAdapter(new AdapterBloodGroups(context, Constant.getBloodGroups(), "request"));

        rv_popular_requests.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_nearby_requests.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_recent_requests.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        rv_requests_by_user.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        rv_popular_requests.setAdapter(new AdapterBloodRequests(context, popular_requests_list, false, true));
        rv_nearby_requests.setAdapter(new AdapterBloodRequests(context, nearby_requests_list, false, true));
        rv_recent_requests.setAdapter(new AdapterBloodRequests(context, recent_requests_list, false, true));
        rv_requests_by_user.setAdapter(new AdapterBloodRequests(context, requests_by_user, false, true));

    }

    private void setData() {
        try {

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
        rv_requests_by_user = v.findViewById(R.id.rv_requests_by_user);
        rv_recent_requests = v.findViewById(R.id.rv_recent_requests);
        rv_nearby_requests = v.findViewById(R.id.rv_nearby_requests);
        rv_popular_requests = v.findViewById(R.id.rv_popular_requests);


        view_recent_requests = v.findViewById(R.id.view_recent_requests);
        view_popular_requests = v.findViewById(R.id.view_popular_requests);
        view_nearby_requests = v.findViewById(R.id.view_nearby_requests);
        view_requests_by_user = v.findViewById(R.id.view_requests_by_user);
        view_all_requests = v.findViewById(R.id.view_all_requests);
    }

}
