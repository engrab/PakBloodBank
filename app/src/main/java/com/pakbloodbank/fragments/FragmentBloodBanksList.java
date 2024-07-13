package com.pakbloodbank.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.adapters.AdapterBloodBanks;
import com.pakbloodbank.items.BloodBankFilterItem;
import com.pakbloodbank.items.BloodBanksItem;
import com.pakbloodbank.items.Cities;
import com.pakbloodbank.items.Countries;
import com.pakbloodbank.items.States;
import com.pakbloodbank.utils.Methods;
import com.pakbloodbank.utils.RecyclerViewPositionHelper;
import com.pakbloodbank.utils.UrlHelper;
import com.pakbloodbank.utils.custom.SearchableSpinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pakbloodbank.utils.Constant.TAG;

public class FragmentBloodBanksList extends Fragment {

    ProgressBar loadMoreBar;
    Context     context;

    public static String bloodGroupKey = "A+";
    private       int    start_limit   = 0;
    private       int    end_limit     = 20, ADD_MORE_LIMIT = 20;
    private ArrayList<BloodBanksItem>  blood_banks_list_full = new ArrayList<>();
    private ArrayList<BloodBanksItem>  blood_banks_list      = new ArrayList<>();
    private RecyclerView               rv_blood_banks;
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private int                        visibleItemCount      = 0, totalItemCount = 0, firstVisibleItem = 0, m_PreviousTotalCount = 0;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterBloodBanks  adapter;
    private int                SWIPE = 1, LOAD_MORE = 2;


    private int COUNTRIES_LIST = 1, STATES_LIST = 2, CITIES_LIST = 3;

    private ArrayList<Countries> countriesArrayList = new ArrayList<>();
    private ArrayList<States>    statesArrayList    = new ArrayList<>();
    private ArrayList<Cities>    citiesArrayList    = new ArrayList<>();
    private ArrayList<String>    countriesNameList  = new ArrayList<>(), statesNameList = new ArrayList<>(), citiesNameList = new ArrayList<>(), orderByList = new ArrayList<>();
    private SearchableSpinner countrySpinner, stateSpinner, citySpinner, orderBySpinner;
    private String                     user_id = "";
    private BloodBankFilterItem        requestFilter;
    private Methods                    methods;
    private RangeSeekBar               seekbar;
    private TextView                   minTextRadius;
    private Dialog                     filterDialog;
    private WindowManager.LayoutParams filterLayoutParams;


    public static FragmentBloodBanksList createInstance(BloodBankFilterItem requestFilter) {

        FragmentBloodBanksList fragment = new FragmentBloodBanksList();
        fragment.init(requestFilter);
        return fragment;
    }

    public void init(BloodBankFilterItem requestFilter) {
        this.requestFilter = requestFilter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blood_banks_list, container, false);


        context = getActivity();

        initViews(rootView);

        getRequestsOfFilter(LOAD_MORE);

        setupSwipeRefresh();

        setupScrollListener();
        get_some_data(COUNTRIES_LIST, null);

        initFilterDialogItems();



        methods = new Methods(context);

        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                searchFilter();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> getRequestsOfFilter(SWIPE));
    }

    private void getRequestsOfFilter(final int type) {


        if (type == SWIPE) {
            start_limit = 0;
            end_limit   = ADD_MORE_LIMIT;
        }

        loadMoreBar.setVisibility(View.VISIBLE);
        RequestQueue q = Volley.newRequestQueue(context);


        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.bloodBanksByFilter, response -> {

            loadMoreBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);

            Log.d(TAG, response);
            JSONObject object;
            try {
                object = new JSONObject(response);
                object = object.getJSONObject("List");

                Gson gson = new Gson();
                Type donorType = new TypeToken<List<BloodBanksItem>>() {
                }.getType();

                blood_banks_list = new ArrayList<>();

                blood_banks_list = gson.fromJson(object.getJSONArray("BloodBanks").toString(), donorType);

                if (type == LOAD_MORE) {


                    blood_banks_list_full.addAll(blood_banks_list);

                } else if (type == SWIPE) {

                    blood_banks_list_full = blood_banks_list;

                }

                setupAdapters(type);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> {
            swipeRefreshLayout.setRefreshing(false);
            loadMoreBar.setVisibility(View.GONE);
            Log.e(TAG, "onErrorResponse: " + error, null);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("start_limit", String.valueOf(start_limit));
                params.put("end_limit", String.valueOf(end_limit));
                params.put("latitude", requestFilter.getLatitude());
                params.put("longitude", requestFilter.getLongitude());
                params.put("radius", requestFilter.getRadius());
                params.put("order_by", requestFilter.getOrder_by());
                params.put("city", requestFilter.getCity());
                params.put("state", requestFilter.getState());
                params.put("country", requestFilter.getCountry());
                return params;
            }
        };

        q.add(request);
    }


    private void setupAdapters(int type) {

//        Toast.makeText(context, "Start: " + start_limit + " End: " + end_limit + " ", Toast.LENGTH_SHORT).show();
        if (start_limit == 0) {

            rv_blood_banks.setLayoutManager(new GridLayoutManager(context, 2));
            adapter = new AdapterBloodBanks(context, blood_banks_list_full, false, false);
            rv_blood_banks.setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
            adapter.notifyItemRangeInserted(start_limit, blood_banks_list_full.size());
        }

        if (blood_banks_list_full.size() == 0) {
            start_limit = 0;
        } else {
            start_limit = blood_banks_list_full.size();
        }

        if (type == SWIPE) {
            start_limit = 0;
            end_limit   = ADD_MORE_LIMIT;
        }

    }

    private void setupScrollListener() {
        rv_blood_banks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                visibleItemCount    = recyclerView.getChildCount();
                totalItemCount      = mRecyclerViewHelper.getItemCount();
                firstVisibleItem    = mRecyclerViewHelper.findFirstVisibleItemPosition();

                if (totalItemCount == 0 || rv_blood_banks == null)
                    return;
                if (m_PreviousTotalCount == totalItemCount) {
                    return;
                } else {
                    boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                    if (loadMore) {
                        m_PreviousTotalCount = totalItemCount;

                        start_limit = m_PreviousTotalCount;
                        end_limit   = start_limit + ADD_MORE_LIMIT;

                        getRequestsOfFilter(LOAD_MORE);
                    }
                }

            }
        });
    }


    private void initViews(View v) {

        rv_blood_banks     = v.findViewById(R.id.rv_blood_banks_list);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefresh);
        loadMoreBar        = v.findViewById(R.id.loadMoreBar);

    }

    public void initFilterDialogItems() {
        filterDialog = new Dialog(getActivity());
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        filterDialog.setContentView(R.layout.blood_banks_filter_dialog);
        filterDialog.setCancelable(true);


        filterLayoutParams = new WindowManager.LayoutParams();
        filterLayoutParams.copyFrom(filterDialog.getWindow().getAttributes());
        filterLayoutParams.width  = WindowManager.LayoutParams.MATCH_PARENT;
        filterLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


        seekbar       = filterDialog.findViewById(R.id.seekbar);
        minTextRadius = filterDialog.findViewById(R.id.textmin);

        countrySpinner = filterDialog.findViewById(R.id.country);
        stateSpinner   = filterDialog.findViewById(R.id.state);
        citySpinner    = filterDialog.findViewById(R.id.city);
        orderBySpinner = filterDialog.findViewById(R.id.order_by);

        try {
            user_id = ((MainActivity) context).userData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        orderByList.clear();
        orderByList.add(getString(R.string.none));
        orderByList.add(getString(R.string.a_to_z));
        orderByList.add(getString(R.string.z_to_a));
        orderByList.add(getString(R.string.nearby_you));
        orderByList.add(getString(R.string.recent));
        orderByList.add(getString(R.string.popular));


        ArrayAdapter<String> order_by_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner, orderByList);
        order_by_adapter.setDropDownViewResource(R.layout.spinner);
        orderBySpinner.setAdapter(order_by_adapter);

        orderBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    requestFilter.setOrder_by("");
                } else if (position == 1) {
                    requestFilter.setOrder_by("a-z");
                } else if (position == 2) {
                    requestFilter.setOrder_by("z-a");
                } else if (position == 3) {
                    requestFilter.setOrder_by("nearby");
                } else if (position == 4) {
                    requestFilter.setOrder_by("recent");
                } else if (position == 5) {
                    requestFilter.setOrder_by("popular");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        minTextRadius.setText(R.string.search_everywhere);


        ArrayAdapter<String> countries = new ArrayAdapter<>(getActivity(), R.layout.spinner, countriesNameList);
        countries.setDropDownViewResource(R.layout.spinner);
        countrySpinner.setAdapter(countries);

        stateSpinner.setEnabled(false);
        citySpinner.setEnabled(false);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    requestFilter.setCountry(countriesArrayList.get((countrySpinner.getSelectedItemPosition())).getId());
                    get_some_data(STATES_LIST, requestFilter.getCountry());

//                    requestFilter.setCountry("");
                    requestFilter.setState("");
                    requestFilter.setCity("");


                } else {
                    requestFilter.setState("");
                    requestFilter.setCity("");

//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    requestFilter.setCountry(countriesArrayList.get((countrySpinner.getSelectedItemPosition() - 1)).getId());
                    get_some_data(STATES_LIST, requestFilter.getCountry());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                    requestFilter.setState(statesArrayList.get((stateSpinner.getSelectedItemPosition())).getId());
//                    requestFilter.setState("");
                    requestFilter.setCity("");
                    get_some_data(CITIES_LIST, requestFilter.getState());

                } else {
                    requestFilter.setCity("");

//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);
                    requestFilter.setState(statesArrayList.get((stateSpinner.getSelectedItemPosition() - 1)).getId());

                    get_some_data(CITIES_LIST, requestFilter.getState());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                    requestFilter.setCity(citiesArrayList.get((citySpinner.getSelectedItemPosition())).getId());


                } else {

                    requestFilter.setCity(citiesArrayList.get((citySpinner.getSelectedItemPosition() - 1)).getId());

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seekbar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue,
                                       boolean isFromUser) {
                DecimalFormat df = new DecimalFormat("0");
                minTextRadius.setText("" + df.format(leftValue) + " " + getString(R.string.kilometer));
                requestFilter.setRadius(df.format(leftValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //do what you want!!
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //do what you want!!
            }
        });


    }


    private void searchFilter() {



        final Button    submit = filterDialog.findViewById(R.id.submit);



        submit.setOnClickListener(view -> {


            getRequestsOfFilter(SWIPE);
            filterDialog.dismiss();

        });

        filterDialog.show();
        filterDialog.getWindow().setAttributes(filterLayoutParams);
    }



    private void get_some_data(final int what_data, final String id) {

        RequestQueue q = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.getSomeData, response -> {

            Log.d(TAG, response);
            JSONObject object;
            try {
                object = new JSONObject(response);
                object = object.getJSONObject("List");


                Gson gson = new Gson();


                if (what_data == COUNTRIES_LIST) {

                    countriesArrayList.clear();
                    countriesNameList.clear();

                    Type country_type = new TypeToken<List<Countries>>() {
                    }.getType();
                    JSONArray countries = object.getJSONArray("countries");

                    countriesArrayList = gson.fromJson(countries.toString(), country_type);


                    countriesNameList.add(getString(R.string.all_countries));
                    for (Countries c :
                            countriesArrayList) {
                        countriesNameList.add(c.getName());
                    }

                    initFilterDialogItems();

                } else if (what_data == STATES_LIST) {
                    statesNameList.clear();
                    statesArrayList.clear();

                    Type states_type = new TypeToken<List<States>>() {
                    }.getType();
                    JSONArray states = object.getJSONArray("states");

                    statesArrayList = gson.fromJson(states.toString(), states_type);

                    statesNameList.add(getString(R.string.all_states));
                    for (States s :
                            statesArrayList) {
                        statesNameList.add(s.getName());
                    }


                    ArrayAdapter<String> states_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner, statesNameList);
                    states_adapter.setDropDownViewResource(R.layout.spinner);
                    stateSpinner.setAdapter(states_adapter);
                    stateSpinner.setEnabled(true);

                } else if (what_data == CITIES_LIST) {

                    citiesArrayList.clear();
                    citiesNameList.clear();

                    Type cities_type = new TypeToken<List<Cities>>() {
                    }.getType();

                    JSONArray cities = object.getJSONArray("cities");

                    citiesArrayList = gson.fromJson(cities.toString(), cities_type);

                    citiesNameList.add(getString(R.string.all_cities));
                    for (Cities c :
                            citiesArrayList) {
                        citiesNameList.add(c.getName());
                    }


                    ArrayAdapter<String> cities_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner, citiesNameList);
                    cities_adapter.setDropDownViewResource(R.layout.spinner);
                    citySpinner.setAdapter(cities_adapter);
                    citySpinner.setEnabled(true);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> Log.e(TAG, "onErrorResponse: " + error, null)) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                if (what_data == COUNTRIES_LIST) {
                    params.put("what", "countries");
                } else if (what_data == STATES_LIST) {
                    params.put("what", "states");
                    params.put("country_id", id);
                } else if (what_data == CITIES_LIST) {
                    params.put("what", "cities");
                    params.put("state_id", id);
                }
                return params;
            }
        };

        q.add(request);
    }
}
