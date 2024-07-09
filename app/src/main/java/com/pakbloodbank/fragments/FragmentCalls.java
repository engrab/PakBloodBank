package com.pakbloodbank.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.adapters.AdapterCalls;
import com.pakbloodbank.items.ItemCalls;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.Methods;
import com.pakbloodbank.utils.PrefManager;
import com.pakbloodbank.utils.UrlHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

import static com.pakbloodbank.utils.Constant.TAG;

public class FragmentCalls extends Fragment {

    private RecyclerView recyclerView;
    private AdapterCalls adapterCalls;
    private ArrayList<ItemCalls> arrayList;
    private ProgressBar progressBar;
    private TextView textView_empty;
    private SearchView searchView;
    private String userId;

    private final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {

            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (!searchView.isIconified()) {
                adapterCalls.getFilter().filter(s);
                adapterCalls.notifyDataSetChanged();
            }
            return false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calls, container, false);

        Methods methods = new Methods(getActivity());

        arrayList = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.pb_cat);
        textView_empty = rootView.findViewById(R.id.tv_empty_cat);
        recyclerView = rootView.findViewById(R.id.rv_calls);

        PrefManager pref = new PrefManager(getActivity());

        try {
            JSONObject user = new JSONObject(pref.getUserData());

            userId = user.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayoutManager linear = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linear);
        recyclerView.setAdapter(adapterCalls);

        if (methods.isNetworkAvailable()) {

            if (getActivity() != null) {

                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.callsUrl,
                        response -> {
                            JSONObject jsonObject;
                            try {

                                Log.d(TAG, "onResponse: " + response);
                                jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray(TAG);
                                JSONObject c;

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    c = jsonArray.getJSONObject(i);

                                    arrayList.add(new ItemCalls(
                                            c.getString("id"),
                                            c.getString("to_id"),
                                            c.getString("from_id"),
                                            c.getString("to_name"),
                                            c.getString("to_phone"),
                                            c.getString("date"),
                                            c.getString("isRated")
                                    ));
                                }

                                progressBar.setVisibility(View.GONE);
                                setAdapter();
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
                        params.put("user_id", userId);
                        return params;
                    }
                };

                queue.add(request);
            } else {
                if (arrayList != null) {
                    setAdapter();
                }
                progressBar.setVisibility(View.GONE);
            }

        } else {
            Constant.showNoNetwork(getActivity());
        }

        setHasOptionsMenu(true);
        return rootView;
    }

    private void setAdapter() {
        adapterCalls = new AdapterCalls(getActivity(), arrayList);
        AnimationAdapter adapterAnim = new ScaleInAnimationAdapter(adapterCalls);
        adapterAnim.setFirstOnly(true);
        adapterAnim.setDuration(500);
        adapterAnim.setInterpolator(new OvershootInterpolator(.9f));
        recyclerView.setAdapter(adapterAnim);

        setExmptTextView();
    }

    private void setExmptTextView() {
        if (arrayList.size() == 0) {
            textView_empty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            textView_empty.setVisibility(View.GONE);
        }
    }
}
