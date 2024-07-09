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
import com.pakbloodbank.adapters.AdapterBloodDonors;
import com.pakbloodbank.items.Cities;
import com.pakbloodbank.items.Countries;
import com.pakbloodbank.items.Donor;
import com.pakbloodbank.items.DonorFilterItem;
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


public class FragmentDonorsList extends Fragment {



    ProgressBar loadMoreBar;
    Context     context;

    ProgressDialog progressDialog;

    String countryValue="";

    private Activity activity;

    private int selectedCountryPosition = 0;
    private int selectedStatePosition = 0;
    private int selectedCityPosition = 0;

    public static String bloodGroupKey = "A+";
    private       int    start_limit   = 0;
    private       int    end_limit     = 20, ADD_MORE_LIMIT = 20;
    private ArrayList<Donor>           donors_list_full = new ArrayList<>();
    private ArrayList<Donor>           donors_list      = new ArrayList<>();
    private RecyclerView               rv_donors;
    private RecyclerViewPositionHelper mRecyclerViewHelper;
    private int                        visibleItemCount = 0, totalItemCount = 0, firstVisibleItem = 0, m_PreviousTotalCount = 0;
    SwipeRefreshLayout swipeRefreshLayout;
    private AdapterBloodDonors adapter;
    int SWIPE = 1, LOAD_MORE = 2;


    private int COUNTRIES_LIST = 1, STATES_LIST = 2, CITIES_LIST = 3;

    ArrayList<Countries> countriesArrayList = new ArrayList<>();
    ArrayList<States>    statesArrayList    = new ArrayList<>();
    ArrayList<Cities>    citiesArrayList    = new ArrayList<>();
    private ArrayList<String> countriesNameList = new ArrayList<>(), statesNameList = new ArrayList<>(), citiesNameList = new ArrayList<>(), orderByList = new ArrayList<>();
//    private ArrayList<Countries> countriesNameList = new ArrayList<>();
//    private ArrayList<States> statesNameList = new ArrayList<>();
//    private ArrayList<Cities> citiesNameList = new ArrayList<>();
//    private ArrayList<String>  orderByList = new ArrayList<>();
    private SearchableSpinner  countrySpinner, stateSpinner,citySpinner,orderBySpinner;

    String user_id = "";
    private DonorFilterItem            donorFilterItem;
    private Methods                    methods;
    private RangeSeekBar               seekbar;
    private TextView                   minTextRadius;
    private CheckBox                   addedByUserCheckbox;
    private Dialog                     filterDialog;
    private WindowManager.LayoutParams filterDialogParams;
    private Boolean isRequsetFeatureCalled=false;
    private boolean isAttached;

    private String selectedCountryid="";



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
        if(context instanceof Activity)
            this.activity= (Activity) context;
        isAttached = true;
    }

    public static FragmentDonorsList createInstance(DonorFilterItem donorFilterItem) {

        FragmentDonorsList fragment = new FragmentDonorsList();
        fragment.init(donorFilterItem);
        return fragment;
    }

    public void init(DonorFilterItem donorFilterItem) {
        this.donorFilterItem = donorFilterItem;
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_donors_list, container, false);



        //context = getActivity();



        initViews(rootView);

        methods = new Methods(context);

        getDonorsOfBloodGroup(LOAD_MORE);

        setupSwipeRefresh();

        setupScrollListener();

        get_some_data(COUNTRIES_LIST, null);

        if(!isFilterDialogShowing() && isAdded() && context != null){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initFilterDialogItems(); // Initialize spinners here
                }
            }, 500); // Adjust the delay as needed

           // initFilterDialogItems();
        }


        setHasOptionsMenu(true);
        return rootView;
    }




    private boolean isFilterDialogShowing() {
        return filterDialog!=null && filterDialog.isShowing();
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

//                    if(loadMoreBar.getVisibility()==View.VISIBLE)
//                {
//                    return true;
//                }

                        searchFilter();
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                            searchFilter();
//
//                        }
//                    }, 400);
////                    searchFilter();
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }

            return super.onOptionsItemSelected(item);
        }

        private void setupSwipeRefresh() {
            swipeRefreshLayout.setOnRefreshListener(() -> getDonorsOfBloodGroup(SWIPE));
        }

        private void getDonorsOfBloodGroup(final int type) {


        progressDialog=new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

            //Log.d("donor filter item", String.valueOf(donorFilterItem));
            if (donorFilterItem.getBlood_group().equals("")) {
                ((MainActivity) context).setActionTitle(getString(R.string.search_from_all));
            } else {
                ((MainActivity) context).setActionTitle(donorFilterItem.getBlood_group());
            }

    //        Toast.makeText(context, "bg: " + donorFilterItem.getBlood_group(), Toast.LENGTH_SHORT).show();

            if (type == SWIPE) {
                start_limit = 0;
                end_limit   = ADD_MORE_LIMIT;
            }



            loadMoreBar.setVisibility(View.VISIBLE);
            progressDialog.show();

            RequestQueue q = Volley.newRequestQueue(context);


            StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.donorsByFilter, response -> {

                loadMoreBar.setVisibility(View.GONE);
                progressDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

                Log.d(TAG, response);
                JSONObject object;
                try {
                    object = new JSONObject(response);
                   // Log.d( "Fragment donor list: " , String.valueOf(object));
                     //Toast.makeText(context, "object found", Toast.LENGTH_SHORT).show();
                    object = object.getJSONObject("List");

                   // Log.d( "Fragment donor Object: " , String.valueOf(object));

                    Gson gson = new Gson();
                    Type donorType = new TypeToken<List<Donor>>() {
                    }.getType();

                    donors_list = new ArrayList<>();

                    donors_list = gson.fromJson(object.getJSONArray("Donors").toString(), donorType);

    //                Donor donor=gson.fromJson(object.toString(), Donor.class);
    //                donors_list.add(donor);
                    //String donor_id=donor.getId();

                    Log.d( "donorslist: ",String.valueOf(donors_list));




                    if (type == LOAD_MORE) {



                        donors_list_full.addAll(donors_list);



                    } else if (type == SWIPE) {

                        donors_list_full = donors_list;

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
                    params.put("blood_group", donorFilterItem.getBlood_group());
                    params.put("start_limit", String.valueOf(start_limit));
                    params.put("end_limit", String.valueOf(end_limit));
                    params.put("latitude", donorFilterItem.getLatitude());
                    params.put("longitude", donorFilterItem.getLongitude());
                    params.put("radius", donorFilterItem.getRadius());
                    params.put("order_by", donorFilterItem.getOrder_by());

                    params.put("city", donorFilterItem.getCity());
                    params.put("state", donorFilterItem.getState());
                    params.put("country", donorFilterItem.getCountry());
                    params.put("added_by", donorFilterItem.getAdded_by());
                    params.put("donor_type", donorFilterItem.getDonor_type());
                    return params;



                }
            };

            q.add(request);
        }

        private void setupAdapters(int type) {

    //        Toast.makeText(context, "Start: " + start_limit + " End: " + end_limit + " ", Toast.LENGTH_SHORT).show();
            if (start_limit == 0) {



                Log.d("context", String.valueOf(context));
                Log.d("donor list full", String.valueOf(donors_list_full));
                Log.d("donorFiletrItem", donorFilterItem.toString());


                if(!donors_list_full.isEmpty()) {
                    rv_donors.setLayoutManager(new GridLayoutManager(context, 2));
                    adapter = new AdapterBloodDonors(context, donors_list_full, donorFilterItem.isEdit(), false);
                    rv_donors.setAdapter(adapter);
                }
                else
                {
                    Toast.makeText(context, "No Donors found", Toast.LENGTH_SHORT).show();
                }

            } else
            {
                adapter.notifyDataSetChanged();
                adapter.notifyItemRangeInserted(start_limit, donors_list_full.size());
            }

            if (donors_list_full.size() == 0) {
                start_limit = 0;
            } else {
                start_limit = donors_list_full.size();
            }

            if (type == SWIPE) {
                start_limit = 0;
                end_limit   = ADD_MORE_LIMIT;
            }

        }

        private void setupScrollListener() {
            rv_donors.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                    super.onScrollStateChanged(recyclerView, newState);

                    mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
                    visibleItemCount    = recyclerView.getChildCount();
                    totalItemCount      = mRecyclerViewHelper.getItemCount();
                    firstVisibleItem    = mRecyclerViewHelper.findFirstVisibleItemPosition();

                    if (totalItemCount == 0 || rv_donors == null)
                        return;
                    if (m_PreviousTotalCount == totalItemCount) {
                        return;
                    } else {
                        boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
                        if (loadMore) {
                            m_PreviousTotalCount = totalItemCount;

                            start_limit = m_PreviousTotalCount;
                            end_limit   = start_limit + ADD_MORE_LIMIT;

                            getDonorsOfBloodGroup(LOAD_MORE);
                        }
                    }

                }
            });
        }


        private void initViews(View v) {

            rv_donors          = v.findViewById(R.id.rv_donors_list);
            swipeRefreshLayout = v.findViewById(R.id.swipeRefresh);
            loadMoreBar        = v.findViewById(R.id.loadMoreBar);

        }


        private void searchFilter() {

            if (countriesArrayList.size() == 0) {
                get_some_data(COUNTRIES_LIST, null);
            }


            final Button    submit = filterDialog.findViewById(R.id.submit);
            final ImageView close  = filterDialog.findViewById(R.id.bt_close);


            close.setOnClickListener(v -> filterDialog.dismiss());


            submit.setOnClickListener(view -> {

                getDonorsOfBloodGroup(SWIPE);
                filterDialog.dismiss();
            });

            filterDialog.show();
            filterDialog.getWindow().setAttributes(filterDialogParams);
        }

        public void initFilterDialogItems() {

    //        Log.d( "initFilterDialogItems: ", String.valueOf(countriesArrayList));
    //        Log.d( "initFilterDialogItemsBefore: ", String.valueOf(countriesArrayList.size()));

            if (countriesArrayList.size() == 0) {
                get_some_data(COUNTRIES_LIST, null);
                new Handler().postDelayed(() -> initFilterDialogItems(), 3000);
                return;
            }

            //Log.d( "initFilterDialogItemsAfter: ", String.valueOf(countriesArrayList.size()));





                if (isAttached) {
                    Log.d("isAttached if", String.valueOf(isAttached));
                    filterDialog = new Dialog(context);
                } else
                {
    //                Log.d("isAttached else 1", String.valueOf(isAttached));
    //                new Handler().postDelayed(() -> {
    //                    if (isAttached) {
    //                        Log.d("isAttached else", String.valueOf(isAttached));
                                Toast.makeText(context, "Fragment Not Attached", Toast.LENGTH_SHORT).show();
    //                    }
    //                }, 200); // Adjust the delay as needed
                }

                //filterDialog = new Dialog(getContext());

                if (!isRequsetFeatureCalled) {
                    filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                    isRequsetFeatureCalled = true;
                }


                filterDialog.setContentView(R.layout.donors_filter_dialog);

                filterDialog.setCancelable(true);

                filterDialogParams = new WindowManager.LayoutParams();
                filterDialogParams.copyFrom(filterDialog.getWindow().getAttributes());
                filterDialogParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                filterDialogParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


                seekbar = filterDialog.findViewById(R.id.seekbar);
                minTextRadius = filterDialog.findViewById(R.id.textmin);

                addedByUserCheckbox = filterDialog.findViewById(R.id.added_by_user);


            try {
                user_id = ((MainActivity) context).userData.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            addedByUserCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    donorFilterItem.setAdded_by(user_id);
                } else {
                    donorFilterItem.setAdded_by("");
                }
            });



            countrySpinner = filterDialog.findViewById(R.id.country);
            stateSpinner   = filterDialog.findViewById(R.id.state);
            citySpinner    = filterDialog.findViewById(R.id.city);
            orderBySpinner = filterDialog.findViewById(R.id.order_by);
//






                orderByList.clear();
                orderByList.add(context.getString(R.string.none));
            orderByList.add(context.getString(R.string.a_to_z));
            orderByList.add(context.getString(R.string.z_to_a));
            orderByList.add(context.getString(R.string.nearby_you));
            orderByList.add(context.getString(R.string.recent));
            orderByList.add(context.getString(R.string.popular));

    //        orderByList.clear();
    //        orderByList.add(getActivity().getResources().getString(R.string.none));
    //        orderByList.add(getActivity().getResources().getString(R.string.a_to_z));
    //        orderByList.add(getActivity().getResources().getString(R.string.z_to_a));
    //        orderByList.add(getActivity().getResources().getString(R.string.nearby_you));
    //        orderByList.add(getActivity().getResources().getString(R.string.recent));
    //        orderByList.add(getActivity().getResources().getString(R.string.popular));




            ArrayAdapter<String> order_by_adapter = new ArrayAdapter<>(activity, R.layout.spinner, orderByList);
            order_by_adapter.setDropDownViewResource(R.layout.spinner);
            orderBySpinner.setAdapter(order_by_adapter);
            ArrayAdapter<String> countriesadapter = new ArrayAdapter<>(activity, R.layout.spinner, countriesNameList);
            countriesadapter.setDropDownViewResource(R.layout.spinner);
            countrySpinner.setAdapter(countriesadapter);



//            CustomAdapterSpinner countryAdapter=new CustomAdapterSpinner(countriesNameList,activity);
//            countrySpinner.setAdapter(countryAdapter);
//            countrySpinner.setEnabled(true);



            Log.d("CountriesNameList", String.valueOf(countriesNameList));


            stateSpinner.setEnabled(false);
            citySpinner.setEnabled(false);


////
//            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            Log.d("Position before if", "Selected Position: " + countrySpinner.getSelectedItemPosition());
//
//
//                            //String selectedCountry = parent.getItemAtPosition(position).toString();
//
//                            String selectedCountry = (String) countrySpinner.getSelectedItem();
//
//                            if (position == 0) {
//                                Log.d("Position 0 selected", "Selected Position: " + countrySpinner.getSelectedItemPosition());
//                                Log.d("Position 0 selected ", countriesArrayList.get(position).getId());
//                                //((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                                ////                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//                                //donorFilterItem.setCountry(countriesArrayList.get((countrySpinner.getSelectedItemPosition() )).getId());
//                                //donorFilterItem.setCountry(countriesArrayList.get(position).getId());
//                                //donorFilterItem.setCountry(selectedCountry);
//
//
//                                selectedCountryid="";
//                                if (selectedCountry != null) {
//                                    int selectedCountryPosition = countrySpinner.getSelectedItemPosition();
//
//                                    if (selectedCountryPosition >= 0) {
//                                        donorFilterItem.setCountry(countriesArrayList.get(selectedCountryPosition).getId());
//                                    } else {
//                                        donorFilterItem.setCountry("");
//                                    }
//                                } else {
//                                    donorFilterItem.setCountry("");
//                                }
//                                Log.d("DonorFilterItem: ", String.valueOf(donorFilterItem));
//                                //Log.d("CountriesArrayList", String.valueOf(countriesArrayList));
//
//
//
//
//                                //donorFilterItem.setCountry("");
//                                donorFilterItem.setState("");
//                                donorFilterItem.setCity("");
//
//
//                                get_some_data(STATES_LIST, donorFilterItem.getCountry());
//
//                            }
//                            //
//
//                            else {
//
//
//                                Log.d("Position ", "Selected Position: " + countrySpinner.getSelectedItemPosition());
//                                //                    Log.d("Position ", "Selected Position: " + countrySpinner.getSelectedItem());
//                                Log.d("Position ", String.valueOf(position));
//                                //donorFilterItem.setCountry(countriesArrayList.get((countrySpinner.getSelectedItemPosition() )).getId());
//
//                                // donorFilterItem.setCountry(countriesArrayList.get((position - 1)).getId());
//                                if (selectedCountry != null) {
//                                    int selectedCountryPosition = countrySpinner.getSelectedItemPosition();
//
//                                    if (selectedCountryPosition >= 0) {
//                                        donorFilterItem.setCountry(countriesArrayList.get(selectedCountryPosition - 1).getId());
//                                    } else {
//                                        donorFilterItem.setCountry("");
//                                    }
//                                } else {
//                                    donorFilterItem.setCountry("");
//                                }
//                                Log.d("DonorFilterItem: ", String.valueOf(donorFilterItem));
//
//                                donorFilterItem.setState("");
//                                donorFilterItem.setCity("");
//                                get_some_data(STATES_LIST, donorFilterItem.getCountry());
//                            }
//
//
//
//
//
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });


            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Position before if", "Selected Position: " + position);
                    Log.d("Position before if", "Selected Position: " + countrySpinner.getSelectedItemPosition());

                    int selectedPosition=countrySpinner.getSelectedItemPosition();

                    if(selectedPosition==-1)
                    {
                       // initFilterDialogItems();
                        ArrayAdapter<String> countries = new ArrayAdapter<>(activity, R.layout.spinner, countriesNameList);
                        countries.setDropDownViewResource(R.layout.spinner);
                        countrySpinner.setAdapter(countries);


                    }

                    if (position >= 0) {
                        Log.d("position", String.valueOf(countrySpinner.getSelectedItemPosition()));
                        // Check if the selected position is valid (not -1)
                        String selectedCountryId = "";
                        if (position==0)
                        {
                            selectedCountryId = countriesArrayList.get(position).getId();
                            Log.d("SelectedcountryID", selectedCountryId);
                            donorFilterItem.setCountry(selectedCountryId);
                        }
                        else
                        {
                            donorFilterItem.setCountry("");
                        }
                        if (position > 0) {
                            // Position is greater than 0, use position - 1 logic
                            selectedCountryId = countriesArrayList.get(position - 1).getId();
                            Log.d("SelectedcountryID", selectedCountryId);
                            donorFilterItem.setCountry(selectedCountryId);

                        }
                        else
                        {
                            donorFilterItem.setCountry("");
                        }
                        Log.d("Selected Country ID: ", selectedCountryId);

                        // Reset state and city when changing the country
                        donorFilterItem.setState("");
                        donorFilterItem.setCity("");

                        if (!selectedCountryId.isEmpty()) {
                            // Trigger data retrieval for states based on the selected country


                            stateSpinner.setEnabled(false);
                            get_some_data(STATES_LIST, selectedCountryId);
                        }
                    } else {
                        // Handle the case when no valid country is selected
                        Log.d("Selected Position", "No country selected");
                        donorFilterItem.setCountry("");
                        donorFilterItem.setState("");
                        donorFilterItem.setCity("");
                    }

                    Log.d("DonorFilterItem: ", String.valueOf(donorFilterItem));


                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle case when nothing is selected
                    Log.d("Nothing selected", "No country selected");
                    donorFilterItem.setCountry("");
                    donorFilterItem.setState("");
                    donorFilterItem.setCity("");
                }
            });


            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Get the selected state
                    Log.d("position", String.valueOf(stateSpinner.getSelectedItemPosition()));

                    String selectedStateId = "";
                    if (position >= 0 && position<statesArrayList.size()) {
                        // Check if the selected position is valid (not -1)
                        String selectedState = (String) stateSpinner.getSelectedItem();

                        if (selectedState != null) {
                            int selectedStatePosition = stateSpinner.getSelectedItemPosition();
                            int selectedCountryPosition = countrySpinner.getSelectedItemPosition();

                            if (selectedCountryPosition >= 0 && selectedStatePosition > 0) {
                                // Adjust the state position based on the selected country
                                donorFilterItem.setState(statesArrayList.get(selectedStatePosition - 1).getId());
                            } else {
                                donorFilterItem.setState("");
                            }
                        } else {
                            donorFilterItem.setState("");
                        }
                        // Reset city when changing the state
                        donorFilterItem.setCity("");
                        // Trigger data retrieval for cities based on the selected state

                        citySpinner.setEnabled(false);
                        get_some_data(CITIES_LIST, donorFilterItem.getState());
                    } else {
                        // Handle the case when no valid state is selected
                        Log.d("Selected Position", "No state selected");
                        donorFilterItem.setState("");
                        donorFilterItem.setCity("");
                    }

                    Log.d("DonorFilterItem: ", String.valueOf(donorFilterItem));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle case when nothing is selected
                    Log.d("Nothing selected", "No state selected");
                    donorFilterItem.setState("");
                    donorFilterItem.setCity("");
                }
            });

            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCity = (String) citySpinner.getSelectedItem();
                    int selectedCountryPosition = countrySpinner.getSelectedItemPosition(); // Get the selected country position
                    int selectedStatePosition = stateSpinner.getSelectedItemPosition(); // Get the selected state position

                    if (position == 0 && position<citiesArrayList.size()) {
                        // Handle the case when no city is selected
                        if (selectedCity != null) {
                            if (selectedCountryPosition >= 0 && selectedStatePosition > 0) {
                                donorFilterItem.setCity(citiesArrayList.get(position).getId());
                            } else {
                                donorFilterItem.setCity("");
                            }
                        } else {
                            donorFilterItem.setCity("");
                        }
                    } else {
                        // Handle the case when a city is selected
                        if (position < citiesArrayList.size()) {


                            if (selectedCity != null) {
                                if (selectedCountryPosition >= 0 && selectedStatePosition > 0) {
                                    donorFilterItem.setCity(citiesArrayList.get(position - 1).getId());
                                } else {
                                    donorFilterItem.setCity("");
                                }
                            } else {
                                donorFilterItem.setCity("");
                            }
                        }

                    }
                    // Log the current state of the donorFilterItem (for debugging purposes)
                    Log.d("DonorFilterItem: ", String.valueOf(donorFilterItem));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Handle case when nothing is selected
                    Log.d("Nothing selected", "No city selected");
                    donorFilterItem.setCity("");
                }
            });
//            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                    //String selectedState = parent.getItemAtPosition(position).toString();
//                    String selectedState = (String) stateSpinner.getSelectedItem();
//
//                    int selectedCountryPosition = countrySpinner.getSelectedItemPosition(); // Get the selected country position
//
//                    Log.d("CountrySpinner", "Selected Position: " + position);
//
//                        if (position == 0) {
//                            Log.d("Position ", "Selected Position: " + stateSpinner.getSelectedItemPosition());
//
//
//                            if (selectedState != null) {
//                                int selectedStatePosition = stateSpinner.getSelectedItemPosition();
//
//                                if (selectedCountryPosition >= 0) {
//                                    donorFilterItem.setState(statesArrayList.get(selectedStatePosition).getId());
//                                } else {
//                                    donorFilterItem.setState("");
//                                }
//                            } else {
//                                donorFilterItem.setState("");
//                            }
//                            //donorFilterItem.setState(selectedState);
//                            //                    donorFilterItem.setState("");
//                            donorFilterItem.setCity("");
//                            get_some_data(CITIES_LIST, donorFilterItem.getState());
//
//                        } else {
//                            Log.d("Position ", "Selected Position: " + stateSpinner.getSelectedItemPosition());
//
//                            //                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                            //                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//                            // donorFilterItem.setState(statesArrayList.get((stateSpinner.getSelectedItemPosition() - 1)).getId());
//                            //String selectedState= statesArrayList.get(position - 1).getId();
//
//
//                            if (selectedState != null) {
//                                int selectedStatePosition = stateSpinner.getSelectedItemPosition();
//
//                                if (selectedCountryPosition >= 0) {
//                                    donorFilterItem.setState(statesArrayList.get(selectedStatePosition - 1).getId());
//                                } else {
//                                    donorFilterItem.setState("");
//                                }
//                            } else {
//                                donorFilterItem.setState("");
//                            }
//                            // donorFilterItem.setState(selectedState);
//                            donorFilterItem.setCity("");
//
//                            get_some_data(CITIES_LIST, donorFilterItem.getState());
//                        }
//
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//
//            citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    //String selectedCity = parent.getItemAtPosition(position).toString();
//                    String selectedCity = (String) countrySpinner.getSelectedItem();
//
//                    int selectedCountryPosition = countrySpinner.getSelectedItemPosition(); // Get the selected country position
//
//
//
//
//                        if (position == 0) {
//                            //                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                            //                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//                            //String selectedCity=citiesArrayList.get(position).getId() ;
//                            //donorFilterItem.setCity(selectedCity);
//
//
//                            if (selectedCity != null) {
//                                int selectedCityPosition = citySpinner.getSelectedItemPosition();
//
//                                if (selectedCountryPosition >= 0) {
//                                    donorFilterItem.setCity(citiesArrayList.get(selectedCityPosition).getId());
//                                } else {
//                                    donorFilterItem.setCity("");
//                                }
//                            } else {
//                                donorFilterItem.setCity("");
//                            }
//                            //donorFilterItem.setCity(citiesArrayList.get((position )).getId());
//                            //                    donorFilterItem.setCity("");
//
//                        } else {
//                            //                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
//                            //                    ((TextView) parent.getChildAt(0)).setTextSize(14);
//                            //String selectedCity=citiesArrayList.get(position -1).getId() ;
//                            //donorFilterItem.setCity(selectedCity);
//                            if (selectedCity != null) {
//                                int selectedCityPosition = citySpinner.getSelectedItemPosition();
//
//                                if (selectedCountryPosition >= 0) {
//                                    donorFilterItem.setCity(citiesArrayList.get(selectedCityPosition - 1).getId());
//                                } else {
//                                    donorFilterItem.setCity("");
//                                }
//                            } else {
//                                donorFilterItem.setCity("");
//                            }
//
//                            //donorFilterItem.setCity(citiesArrayList.get((position - 1)).getId());
//                        }
//
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });




            orderBySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        donorFilterItem.setOrder_by("");
                    } else if (position == 1) {
                        donorFilterItem.setOrder_by("a-z");
                    } else if (position == 2) {
                        donorFilterItem.setOrder_by("z-a");
                    } else if (position == 3) {
                        donorFilterItem.setOrder_by("nearby");
                    } else if (position == 4) {
                        donorFilterItem.setOrder_by("recent");
                    } else if (position == 5) {
                        donorFilterItem.setOrder_by("popular");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
    
            minTextRadius.setText("");
    
            seekbar.setOnRangeChangedListener(new OnRangeChangedListener() {
                @Override
                public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue,
                                           boolean isFromUser) {
                    DecimalFormat df = new DecimalFormat("0");
                    minTextRadius.setText("" + df.format(leftValue) + " " +getString(R.string.kilometer));
                    donorFilterItem.setRadius(df.format(leftValue));
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
    
    
            final Button btn_free_donor = filterDialog.findViewById(R.id.bt_free_donor);
            final Button btn_paid_donor = filterDialog.findViewById(R.id.bt_paid_donor);
            final Button btn_all_donor  = filterDialog.findViewById(R.id.bt_all_donor);
            btn_all_donor.setSelected(true);
    
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
    
    
            btn_free_donor.setOnClickListener(v -> {
                donorFilterItem.setDonor_type("free");
                btn_free_donor.setSelected(true);
                btn_paid_donor.setSelected(false);
                btn_all_donor.setSelected(false);
            });
    
            btn_paid_donor.setOnClickListener(v -> {
                donorFilterItem.setDonor_type("paid");
                btn_paid_donor.setSelected(true);
                btn_all_donor.setSelected(false);
                btn_free_donor.setSelected(false);
            });
            btn_all_donor.setOnClickListener(v -> {
                donorFilterItem.setDonor_type("");
                btn_paid_donor.setSelected(false);
                btn_all_donor.setSelected(true);
                btn_free_donor.setSelected(false);
            });
    
            all_blood_groups.setOnClickListener(v -> {
                donorFilterItem.setBlood_group("");
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
                donorFilterItem.setBlood_group("A+");
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
    
                donorFilterItem.setBlood_group("A-");
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
                donorFilterItem.setBlood_group("B+");
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
                donorFilterItem.setBlood_group("B-");
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
                donorFilterItem.setBlood_group("AB+");
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
                donorFilterItem.setBlood_group("AB-");
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
                donorFilterItem.setBlood_group("O+");
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
                donorFilterItem.setBlood_group("O-");
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
    
        private void resetValues() {
            donorFilterItem = new DonorFilterItem().getFilter();
        }
    
    
        private void get_some_data(final int what_data, final String id) {


    
            RequestQueue q = Volley.newRequestQueue(context);
    
            StringRequest request = new StringRequest(Request.Method.POST, UrlHelper.getSomeData, response -> {
    
                Log.d(TAG, response);
                JSONObject object;
                try {
                    object = new JSONObject(response);
                    Log.d( "get_some_data: ", String.valueOf(object));
                    object = object.getJSONObject("List");
    
                    //Log.d( "get_some_data: ", String.valueOf(object));
    
    
                    Gson gson = new Gson();
    
    
                    if (what_data == COUNTRIES_LIST) {
    
                        countriesArrayList.clear();
                        countriesNameList.clear();
    
                        Type country_type = new TypeToken<List<Countries>>() {
                        }.getType();
                        JSONArray countries = object.getJSONArray("countries");
    
                        countriesArrayList = gson.fromJson(countries.toString(), country_type);
                        selectedCountryPosition = 0;
    
    
                        countriesNameList.add(getString(R.string.all_countries));
                        for (Countries c :
                                countriesArrayList) {
                            countriesNameList.add(c.getName());
                        }


                            initFilterDialogItems();


//
//                        new Handler().postDelayed(() -> {
//                            if (countryId != null) {
//                                countrySpinner.setSelectionM(selectedCountryPosition);
//                            }
//                        }, 1000);
//
//
//                        if (countryId != null) {
//                            get_some_data(STATES_LIST, countryId, countryId, stateId, cityId);
//                        }





                    } else if (what_data == STATES_LIST) {
                        statesNameList.clear();
                        statesArrayList.clear();

    
                        Type states_type = new TypeToken<List<States>>() {
                        }.getType();
                        JSONArray states = object.getJSONArray("states");
    
                        statesArrayList = gson.fromJson(states.toString(), states_type);
    
                       // Log.d("StatesNameList", String.valueOf(statesNameList));

                        statesNameList.add(getString(R.string.all_states));
                        for (States s :
                                statesArrayList) {
                            statesNameList.add(s.getName());
                        }
//                        statesArrayList = gson.fromJson(states.toString(), states_type);
//                        selectedStatePosition = 0;
//                        for (int i = 0; i < statesArrayList.size(); i++) {
//                            States s = statesArrayList.get(i);
//                            statesNameList.add(s.getName());
//
//                            if (stateId != null) {
//                                if (s.getId().equalsIgnoreCase(stateId)) {
//                                    selectedStatePosition = i;
//                                }
//                            }
//                        }
    
                        //Log.d("StatesNameListAfter", String.valueOf(statesNameList));


                                ArrayAdapter<String> states_adapter = new ArrayAdapter<>(activity, R.layout.spinner, statesNameList);
                                states_adapter.setDropDownViewResource(R.layout.spinner);
                                stateSpinner.setAdapter(states_adapter);


                        stateSpinner.setEnabled(true);

//                        new Handler().postDelayed(() -> {
//                            if (stateId != null) {
//                                stateSpinner.setSelectionM(selectedStatePosition);
//                            }
//                        }, 1000);
//
//
//                        if (stateId != null) {
//                            get_some_data(CITIES_LIST, stateId, countryId, stateId, cityId);
//                        }

//
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
////
//                        selectedCityPosition = 0;
//                        for (int i = 0; i < citiesArrayList.size(); i++) {
//                            Cities c = citiesArrayList.get(i);
//                            citiesNameList.add(c.getName());
//                            if (cityId != null) {
//                                if (c.getId().equalsIgnoreCase(cityId)) {
//                                    selectedCityPosition = i;
//                                }
//                            }
//                        }

                                ArrayAdapter<String> cities_adapter = new ArrayAdapter<>(activity, R.layout.spinner, citiesNameList);
                                cities_adapter.setDropDownViewResource(R.layout.spinner);
                                citySpinner.setAdapter(cities_adapter);


                        citySpinner.setEnabled(true);


//                        new Handler().postDelayed(() -> {
//                            if (cityId != null) {
//                                citySpinner.setSelectionM(selectedCityPosition);
//                            }
//                        }, 1000);
//
//                        if (citiesNameList.size() == 1) {
//                            citySpinner.setSelectionM(0);
//                        }
//
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



    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Loading states...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    // Method to dismiss the progress dialog
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
  }


