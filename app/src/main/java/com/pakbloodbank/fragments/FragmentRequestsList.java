package com.pakbloodbank.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.pakbloodbank.adapters.AdapterBloodRequests;
import com.pakbloodbank.items.Cities;
import com.pakbloodbank.items.Countries;
import com.pakbloodbank.items.RequestFilterItem;
import com.pakbloodbank.items.RequestsItem;
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

public class FragmentRequestsList extends Fragment {

    String countryValue="",stateValue="", cityValue="";
    private ArrayAdapter<String> statesSpinnerAdapter;
    private ArrayAdapter<String> citiesSpinnerAdapter;
    ProgressBar loadMoreBar;
    Context     context;

    public static String bloodGroupKey = "A+";
    private       int    start_limit   = 0;
    private       int    end_limit     = 20, ADD_MORE_LIMIT = 20;
    private ArrayList<RequestsItem>    requests_list_full = new ArrayList<>();
    private ArrayList<RequestsItem>    requests_list      = new ArrayList<>();
    private RecyclerView               rv_requests;
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private int                        visibleItemCount   = 0, totalItemCount = 0, firstVisibleItem = 0, m_PreviousTotalCount = 0;
    private SwipeRefreshLayout   swipeRefreshLayout;
    private AdapterBloodRequests adapter;
    private int                  SWIPE = 1, LOAD_MORE = 2;


    private int COUNTRIES_LIST = 1, STATES_LIST = 2, CITIES_LIST = 3;

    private ArrayList<Countries> countriesArrayList = new ArrayList<>();
    private ArrayList<States>    statesArrayList    = new ArrayList<>();
    private ArrayList<Cities>    citiesArrayList    = new ArrayList<>();
    private ArrayList<String>    countriesNameList  = new ArrayList<>(), statesNameList = new ArrayList<>(), citiesNameList = new ArrayList<>(), orderByList = new ArrayList<>();
    private SearchableSpinner countrySpinner, stateSpinner, citySpinner, orderBySpinner;
    private String                     user_id = "";
    private RequestFilterItem          requestFilter;
    private Methods                    methods;
    private Dialog                     filterDialog;
    private WindowManager.LayoutParams layoutParams;
    private RangeSeekBar               seekbar;
    private TextView                   minTextRadius;
    private CheckBox                   addedByUser;

    private boolean isAttached;
    private Activity activity;
    ProgressDialog progressDialog;
    private int selectedCountryPosition = 0;
    private int selectedStatePosition = 0;
    private int selectedCityPosition = 0;

    ProgressBar progressBar;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
        if(context instanceof Activity)
            this.activity= (Activity) context;
        isAttached = true;
    }

    public static FragmentRequestsList createInstance(RequestFilterItem requestFilter) {

        FragmentRequestsList fragment = new FragmentRequestsList();
        fragment.init(requestFilter);
        return fragment;
    }

    public void init(RequestFilterItem requestFilter) {
        this.requestFilter = requestFilter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_requests_list, container, false);


        //context = getActivity();

        initViews(rootView);

        methods = new Methods(context);

        getRequestsOfFilter(LOAD_MORE);

        setupSwipeRefresh();

        setupScrollListener();
        get_some_data(COUNTRIES_LIST, null );


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initFilterDialogItems();
            }
        },400);



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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        searchFilter();
                    }
                },400);
//                searchFilter();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> getRequestsOfFilter(SWIPE));
    }

    private void getRequestsOfFilter(final int type) {

        if (requestFilter.getBlood_group().equals("")) {
            ((MainActivity) context).setActionTitle(getString(R.string.search_from_all));
        } else {
            ((MainActivity) context).setActionTitle(requestFilter.getBlood_group());
        }

//        Toast.makeText(context, "bg: " + requestFilter.getBlood_group(), Toast.LENGTH_SHORT).show();

        if (type == SWIPE) {
            start_limit = 0;
            end_limit   = ADD_MORE_LIMIT;
        }

        progressDialog=new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        loadMoreBar.setVisibility(View.VISIBLE);
        RequestQueue q = Volley.newRequestQueue(context);


        StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.requestsByFilter, response -> {

            loadMoreBar.setVisibility(View.GONE);
            progressDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);

            Log.d(TAG, response);
            JSONObject object;
            try {
                object = new JSONObject(response);
                Log.d("object", String.valueOf(object));
                object = object.getJSONObject("List");
                Log.d("list", String.valueOf(object));

                Gson gson = new Gson();
                Type donorType = new TypeToken<List<RequestsItem>>() {
                }.getType();

                requests_list = new ArrayList<>();

                requests_list = gson.fromJson(object.getJSONArray("Requests").toString(), donorType);

                if (type == LOAD_MORE) {

                    requests_list_full.addAll(requests_list);

                } else if (type == SWIPE) {

                    requests_list_full = requests_list;

                }

                setupAdapters(type);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> {
            swipeRefreshLayout.setRefreshing(false);
            loadMoreBar.setVisibility(View.GONE);
            progressDialog.dismiss();
            Log.e(TAG, "onErrorResponse: " + error, null);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("blood_group", requestFilter.getBlood_group());
                params.put("start_limit", String.valueOf(start_limit));
                params.put("end_limit", String.valueOf(end_limit));
                params.put("latitude", requestFilter.getLatitude());
                params.put("longitude", requestFilter.getLongitude());
                params.put("radius", requestFilter.getRadius());
                params.put("order_by", requestFilter.getOrder_by());
                params.put("city", requestFilter.getCity());
                params.put("state", requestFilter.getState());
                params.put("country", requestFilter.getCountry());
                params.put("added_by", requestFilter.getAdded_by());
                return params;
            }
        };

        q.add(request);
    }


    private void setupAdapters(int type) {

//        Toast.makeText(context, "Start: " + start_limit + " End: " + end_limit + " ", Toast.LENGTH_SHORT).show();
        if (start_limit == 0) {


            if(!requests_list_full.isEmpty()) {
                rv_requests.setLayoutManager(new GridLayoutManager(context, 2));
                adapter = new AdapterBloodRequests(context, requests_list_full, requestFilter.isEdit(), false);
                rv_requests.setAdapter(adapter);
            }
            else
            {
                Toast.makeText(activity,"No data found", Toast.LENGTH_SHORT);
            }

        } else {
            adapter.notifyDataSetChanged();
            adapter.notifyItemRangeInserted(start_limit, requests_list_full.size());
        }

        if (requests_list_full.size() == 0) {
            start_limit = 0;
        } else {
            start_limit = requests_list_full.size();
        }

        if (type == SWIPE) {
            start_limit = 0;
            end_limit   = ADD_MORE_LIMIT;
        }

    }

    private void setupScrollListener() {
        rv_requests.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);

                mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                visibleItemCount    = recyclerView.getChildCount();
                totalItemCount      = mRecyclerViewHelper.getItemCount();
                firstVisibleItem    = mRecyclerViewHelper.findFirstVisibleItemPosition();

                if (totalItemCount == 0 || rv_requests == null)
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

    public void initFilterDialogItems() {

        if (countriesArrayList.size() == 0) {
            get_some_data(COUNTRIES_LIST, null);
            new Handler().postDelayed(() -> initFilterDialogItems(), 3000);
            return;
        }

        filterDialog = new Dialog(context);
        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        filterDialog.setContentView(R.layout.requests_filter_dialog);
        filterDialog.setCancelable(true);

        layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(filterDialog.getWindow().getAttributes());
        layoutParams.width  = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        seekbar       = filterDialog.findViewById(R.id.seekbar);
        minTextRadius = filterDialog.findViewById(R.id.textmin);

        addedByUser = filterDialog.findViewById(R.id.added_by_user);


        try {
            user_id = ((MainActivity) context).userData.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addedByUser.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                requestFilter.setAdded_by(user_id);
            } else {
                requestFilter.setAdded_by("");
            }
        });

        countrySpinner = filterDialog.findViewById(R.id.country);
        stateSpinner   = filterDialog.findViewById(R.id.state);
        citySpinner    = filterDialog.findViewById(R.id.city);
        orderBySpinner = filterDialog.findViewById(R.id.order_by);

//
//        orderByList.clear();
//        orderByList.add(getString(R.string.none));
//        orderByList.add(getString(R.string.a_to_z));
//        orderByList.add(getString(R.string.z_to_a));
//        orderByList.add(getString(R.string.nearby_you));
//        orderByList.add(getString(R.string.recent));
//        orderByList.add(getString(R.string.popular));

        orderByList.clear();
        orderByList.add(context.getString(R.string.none));
        orderByList.add(context.getString(R.string.a_to_z));
        orderByList.add(context.getString(R.string.z_to_a));
        orderByList.add(context.getString(R.string.nearby_you));
        orderByList.add(context.getString(R.string.recent));
        orderByList.add(context.getString(R.string.popular));




        progressBar=new ProgressBar(activity);

        ArrayAdapter<String> order_by_adapter = new ArrayAdapter<>(activity, R.layout.spinner, orderByList);
        order_by_adapter.setDropDownViewResource(R.layout.spinner);
        orderBySpinner.setAdapter(order_by_adapter);

        ArrayAdapter<String> countries = new ArrayAdapter<>(activity, R.layout.spinner, countriesNameList);
        countries.setDropDownViewResource(R.layout.spinner);
        countrySpinner.setAdapter(countries);

        stateSpinner.setEnabled(false);
        citySpinner.setEnabled(false);












        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCountry = (String) countrySpinner.getSelectedItem();
                Log.d("Position before if ", "Selected Position: " + countrySpinner.getSelectedItemPosition());
                Log.d( "position before if", String.valueOf(position));

                if (position == 0) {

                        Log.d("Position ", "Selected Position: " + countrySpinner.getSelectedItemPosition());

                        //                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                        //                    ((TextView) parent.getChildAt(0)).setTextSize(14);

                        // donorFilterItem.setState(statesArrayList.get(stateSpinner.getSelectedItemPosition()).getId());
                        //String selectedState= statesArrayList.get(position).getId();
                    Log.d("Position before if", "Selected Position: " + countrySpinner.getSelectedItemPosition());

                    int selectedPosition=countrySpinner.getSelectedItemPosition();



                    if (selectedCountry != null) {
                            int selectedCountryPosition = countrySpinner.getSelectedItemPosition();

                            if (selectedCountryPosition >= 0) {
                                requestFilter.setCountry(countriesArrayList.get(selectedCountryPosition).getId());
                            } else {
                                requestFilter.setCountry("");
                            }
                        }
                        else {
                            requestFilter.setCountry("");
                        }
//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//                    requestFilter.setCountry(countriesArrayList.get((countrySpinner.getSelectedItemPosition())).getId());
//
////                    requestFilter.setCountry("");
                    requestFilter.setState("");
                    requestFilter.setCity("");
                    get_some_data(STATES_LIST, requestFilter.getCountry());


                        }
                else
                {
//                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                    ((TextView) parent.getChildAt(0)).setTextSize(14);


                    if (selectedCountry != null) {
                        int selectedCountryPosition = countrySpinner.getSelectedItemPosition();

                        if (selectedCountryPosition >= 0) {
                            requestFilter.setCountry(countriesArrayList.get(selectedCountryPosition-1).getId());
                        } else {
                            requestFilter.setCountry("");
                        }
                    }
                    else {
                        requestFilter.setCountry("");
                    }
                   // requestFilter.setCountry(countriesArrayList.get((countrySpinner.getSelectedItemPosition() - 1)).getId());
                    requestFilter.setState("");
                    requestFilter.setCity("");
                    get_some_data(STATES_LIST, requestFilter.getCountry());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Position before if", "Selected Position: " + position);

                if (position >= 0) {
                    // Check if the selected position is valid (not -1)
                    String selectedCountryId = "";
                    if (position==0)
                    {
                        selectedCountryId = countriesArrayList.get(position).getId();
                        requestFilter.setCountry(selectedCountryId);
                    }
                    if (position > 0) {
                        // Position is greater than 0, use position - 1 logic
                        selectedCountryId = countriesArrayList.get(position - 1).getId();
                        requestFilter.setCountry(selectedCountryId);

                    }
                    Log.d("Selected Country ID: ", selectedCountryId);

                    // Reset state and city when changing the country
                    requestFilter.setState("");
                    requestFilter.setCity("");

                    if (!selectedCountryId.isEmpty()) {
                        // Trigger data retrieval for states based on the selected country

                        progressBar.setVisibility(View.VISIBLE);
                        get_some_data(STATES_LIST, selectedCountryId);
                    }
                } else {
                    // Handle the case when no valid country is selected
                    Log.d("Selected Position", "No country selected");
                    requestFilter.setCountry("");
                    requestFilter.setState("");
                    requestFilter.setCity("");
                }

                Log.d("DonorFilterItem: ", String.valueOf(requests_list_full));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
                Log.d("Nothing selected", "No country selected");
                requestFilter.setCountry("");
                requestFilter.setState("");
                requestFilter.setCity("");
            }
        });


        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected state
                String selectedStateId = "";
                if (position >= 0) {
                    // Check if the selected position is valid (not -1)
                    String selectedState = (String) stateSpinner.getSelectedItem();

                    if (selectedState != null) {
                        int selectedStatePosition = stateSpinner.getSelectedItemPosition();
                        int selectedCountryPosition = countrySpinner.getSelectedItemPosition();

                        if (selectedCountryPosition >= 0 && selectedStatePosition > 0) {
                            // Adjust the state position based on the selected country
                            requestFilter.setState(statesArrayList.get(selectedStatePosition - 1).getId());
                        } else {
                            requestFilter.setState("");
                        }
                    } else {
                        requestFilter.setState("");
                    }
                    // Reset city when changing the state
                    requestFilter.setCity("");
                    // Trigger data retrieval for cities based on the selected state
                    citySpinner.setEnabled(false);
                    get_some_data(CITIES_LIST, requestFilter.getState());
                } else {
                    // Handle the case when no valid state is selected
                    Log.d("Selected Position", "No state selected");
                    requestFilter.setState("");
                    requestFilter.setCity("");
                }

                Log.d("RequestFilterItem: ", String.valueOf(requestFilter));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
                Log.d("Nothing selected", "No state selected");
                requestFilter.setState("");
                requestFilter.setCity("");
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedCity = (String) citySpinner.getSelectedItem();
//                int selectedCountryPosition = countrySpinner.getSelectedItemPosition(); // Get the selected country position
//                int selectedStatePosition = stateSpinner.getSelectedItemPosition(); // Get the selected state position

                if (citiesArrayList != null && !citiesArrayList.isEmpty()) {
                    if (position >= 0 && position < citiesArrayList.size()) {
                        // Access the selected city
                        String selectedCity = citiesArrayList.get(position).getId();
                        int selectedCountryPosition = countrySpinner.getSelectedItemPosition();
                        int selectedStatePosition = stateSpinner.getSelectedItemPosition();

                        if (position == 0) {
                            // Handle the case when no city is selected
                            if (selectedCity != null) {
                                if (selectedCountryPosition >= 0 && selectedStatePosition > 0) {
                                    requestFilter.setCity(selectedCity);
                                } else {
                                    requestFilter.setCity("");
                                }
                            } else {
                                requestFilter.setCity("");
                            }
                        } else {
                            // Handle the case when a city is selected
                            if (selectedCity != null) {
                                if (selectedCountryPosition >= 0 && selectedStatePosition > 0) {
                                    requestFilter.setCity(selectedCity);
                                } else {
                                    requestFilter.setCity("");
                                }
                            } else {
                                requestFilter.setCity("");
                            }
                        }

                        // Log the current state of the requestFilter (for debugging purposes)
                        Log.d("RequestFilter: ", String.valueOf(requestFilter));
                    } else {
                        // Handle the case where the selected position is out of bounds
                        // You can set appropriate values or handle this case as needed
                    }
                } else {
                    // Handle the case where citiesArrayList is null or empty
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when nothing is selected
                Log.d("Nothing selected", "No city selected");
                requestFilter.setCity("");
            }
        });




        //previous spinners
//        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                String selectedState = (String) stateSpinner.getSelectedItem();
//
//                if (position == 0) {
////                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
////                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//
//
//
//
//                    if (selectedState != null) {
//                        int selectedStatePosition = stateSpinner.getSelectedItemPosition();
//
//                        if (selectedStatePosition >= 0) {
//                            requestFilter.setState(statesArrayList.get(selectedStatePosition).getId());
//                        } else {
//                            requestFilter.setState("");
//                        }
//                    } else {
//                        requestFilter.setState("");
//                    }
//                    //requestFilter.setState(statesArrayList.get((stateSpinner.getSelectedItemPosition())).getId());
////                    requestFilter.setState("");
//                    requestFilter.setCity("");
//                    get_some_data(CITIES_LIST, requestFilter.getState());
//
//                } else {
////                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
////                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//
//
//                    if (selectedState != null) {
//                        int selectedStatePosition = stateSpinner.getSelectedItemPosition();
//
//                        if (selectedStatePosition >= 0) {
//                            requestFilter.setState(statesArrayList.get(selectedStatePosition-1).getId());
//                        } else {
//                            requestFilter.setState("");
//                        }
//                    } else {
//                        requestFilter.setState("");
//                    }
//                   // requestFilter.setState(statesArrayList.get((stateSpinner.getSelectedItemPosition() - 1)).getId());
//                    requestFilter.setCity("");
//                    get_some_data(CITIES_LIST, requestFilter.getState());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                String selectedCity = (String) citySpinner.getSelectedItem();
//
//                if (position == 0) {
//
//                    if (selectedCity != null) {
//                        int selectedCityPosition = citySpinner.getSelectedItemPosition();
//
//                        if (selectedCityPosition >= 0) {
//                            requestFilter.setCity(citiesArrayList.get(selectedCityPosition).getId());
//                        } else {
//                            requestFilter.setCity("");
//                        }
//                    } else {
//                        requestFilter.setCity("");
//                    }
////                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
////                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//                   // requestFilter.setCity(citiesArrayList.get((citySpinner.getSelectedItemPosition())).getId());
////                    requestFilter.setCity("");
////
//                } else {
//
//                    if (selectedCity != null) {
//                        int selectedCityPosition = citySpinner.getSelectedItemPosition();
//
//                        if (selectedCityPosition >= 0) {
//                            requestFilter.setCity(citiesArrayList.get(selectedCityPosition).getId());
//                        } else {
//                            requestFilter.setCity("");
//                        }
//                    } else {
//                        requestFilter.setCity("");
//                    }
////                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
////                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//                    //requestFilter.setCity(citiesArrayList.get((citySpinner.getSelectedItemPosition() - 1)).getId());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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

        final Button all_blood_groups = filterDialog.findViewById(R.id.all_blood_group);
        final Button a_positive       = filterDialog.findViewById(R.id.btn_a_positive);
        final Button a_negative       = filterDialog.findViewById(R.id.btn_a_negative);
        final Button b_positive       = filterDialog.findViewById(R.id.btn_b_positive);
        final Button b_negative       = filterDialog.findViewById(R.id.btn_b_negative);
        final Button ab_positive      = filterDialog.findViewById(R.id.btn_ab_positive);
        final Button ab_negative      = filterDialog.findViewById(R.id.btn_ab_negative);
        final Button o_positive       = filterDialog.findViewById(R.id.btn_o_positive);
        final Button o_negative       = filterDialog.findViewById(R.id.btn_o_negative);
        all_blood_groups.setSelected(true);

        minTextRadius.setText("");

        seekbar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue,
                                       boolean isFromUser) {
                DecimalFormat df = new DecimalFormat("0");
                minTextRadius.setText("" + df.format(leftValue) + " "+getString(R.string.kilometer));
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


        all_blood_groups.setOnClickListener(v -> {
            requestFilter.setBlood_group("");
            all_blood_groups.setSelected(true);
            a_positive.setSelected(false);
            a_negative.setSelected(false);
            b_positive.setSelected(false);
            b_negative.setSelected(false);
            ab_positive.setSelected(false);
            ab_negative.setSelected(false);
            o_positive.setSelected(false);
            o_negative.setSelected(false);
        });

        a_positive.setOnClickListener(v -> {
            requestFilter.setBlood_group("A+");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(true);
            a_negative.setSelected(false);
            b_positive.setSelected(false);
            b_negative.setSelected(false);
            ab_positive.setSelected(false);
            ab_negative.setSelected(false);
            o_positive.setSelected(false);
            o_negative.setSelected(false);
        });

        a_negative.setOnClickListener(v -> {

            requestFilter.setBlood_group("A-");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(false);
            a_negative.setSelected(true);
            b_positive.setSelected(false);
            b_negative.setSelected(false);
            ab_positive.setSelected(false);
            ab_negative.setSelected(false);
            o_positive.setSelected(false);
            o_negative.setSelected(false);
        });

        b_positive.setOnClickListener(v -> {
            requestFilter.setBlood_group("B+");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(false);
            a_negative.setSelected(false);
            b_positive.setSelected(true);
            b_negative.setSelected(false);
            ab_positive.setSelected(false);
            ab_negative.setSelected(false);
            o_positive.setSelected(false);
            o_negative.setSelected(false);
        });

        b_negative.setOnClickListener(v -> {
            requestFilter.setBlood_group("B-");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(false);
            a_negative.setSelected(false);
            b_positive.setSelected(false);
            b_negative.setSelected(true);
            ab_positive.setSelected(false);
            ab_negative.setSelected(false);
            o_positive.setSelected(false);
            o_negative.setSelected(false);
        });

        ab_positive.setOnClickListener(v -> {
            requestFilter.setBlood_group("AB+");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(false);
            a_negative.setSelected(false);
            b_positive.setSelected(false);
            b_negative.setSelected(false);
            ab_positive.setSelected(true);
            ab_negative.setSelected(false);
            o_positive.setSelected(false);
            o_negative.setSelected(false);
        });

        ab_negative.setOnClickListener(v -> {
            requestFilter.setBlood_group("AB-");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(false);
            a_negative.setSelected(false);
            b_positive.setSelected(false);
            b_negative.setSelected(false);
            ab_positive.setSelected(false);
            ab_negative.setSelected(true);
            o_positive.setSelected(false);
            o_negative.setSelected(false);
        });

        o_positive.setOnClickListener(v -> {
            requestFilter.setBlood_group("O+");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(false);
            a_negative.setSelected(false);
            b_positive.setSelected(false);
            b_negative.setSelected(false);
            ab_positive.setSelected(false);
            ab_negative.setSelected(false);
            o_positive.setSelected(true);
            o_negative.setSelected(false);
        });

        o_negative.setOnClickListener(v -> {
            requestFilter.setBlood_group("O-");
            all_blood_groups.setSelected(false);
            a_positive.setSelected(false);
            a_negative.setSelected(false);
            b_positive.setSelected(false);
            b_negative.setSelected(false);
            ab_positive.setSelected(false);
            ab_negative.setSelected(false);
            o_positive.setSelected(false);
            o_negative.setSelected(true);
        });


    }


    private void initViews(View v) {

        rv_requests        = v.findViewById(R.id.rv_requests_list);
        swipeRefreshLayout = v.findViewById(R.id.swipeRefresh);
        loadMoreBar        = v.findViewById(R.id.loadMoreBar);

    }


    private void searchFilter() {


        final Button    submit = filterDialog.findViewById(R.id.submit);
        final ImageView close  = filterDialog.findViewById(R.id.bt_close);

        close.setOnClickListener(v -> filterDialog.dismiss());

        submit.setOnClickListener(view -> {

            getRequestsOfFilter(SWIPE);
            filterDialog.dismiss();

        });

        filterDialog.show();
        filterDialog.getWindow().setAttributes(layoutParams);
    }

    private void resetValues() {
        requestFilter = new RequestFilterItem().getFilter();
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
