package com.pakbloodbank.items;

import android.content.Context;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pakbloodbank.R;
import com.google.android.gms.common.api.ApiException;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.AutocompletePrediction;
//import com.google.android.gms.location.places.AutocompletePredictionBuffer;
//import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.AutocompletePrediction;
//import com.google.android.gms.location.places.AutocompletePredictionBuffer;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.GeoDataApi;
//import com.google.android.libraries.places.AutocompleteFilter;
//import com.google.android.libraries.places.compat.AutocompletePrediction;
//import com.google.android.libraries.places.compat.AutocompletePredictionBuffer;

//import android.widget.Filter.FilterResults;

public class PlaceAutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(1);
    private static final String TAG = "PlaceAutocomplete";
    private LatLngBounds mBounds;
    PlacesClient placesClient;
    //    private GoogleApiClient mGoogleApiClient;
//    private AutocompleteFilter mPlaceFilter;
    public List<AutocompletePrediction> mResultList;

    public PlaceAutoCompleteAdapter(Context context, PlacesClient placesClient, LatLngBounds latLngBounds) {
//        super(context, 17367047, 16908308);
        super(context, R.layout.place_autocomplete_item_prediction, R.id.place_autocomplete_prediction_primary_text);
        this.placesClient = placesClient;
        this.mBounds = latLngBounds;
//        this.mPlaceFilter = autocompleteFilter;
    }

//    private static List<AutocompletePrediction> onSuccess(FindAutocompletePredictionsResponse response) {
//
////        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//////                mResult.append(" ").append(prediction.getFullText(null) + "\n");
////
////            Log.i(TAG, prediction.getPlaceId());
////            Log.i(TAG, prediction.getPrimaryText(null).toString());
////
////
//////                Toast.makeText(PlacePredictionProgrammatically.this, prediction.getPrimaryText(null) + "-" + prediction.getSecondaryText(null), Toast.LENGTH_SHORT).show();
////        }
////            mSearchResult.setText(String.valueOf(mResult));
//
////        return DataBufferUtils.freezeAndClose(predictions);
//        return new ArrayList<>(response.getAutocompletePredictions());
//    }

    public void setBounds(LatLngBounds latLngBounds) {
        this.mBounds = latLngBounds;
    }

    public int getCount() {
        return this.mResultList.size();
    }

    public AutocompletePrediction getItem(int i) {
        return this.mResultList.get(i);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2 = super.getView(i, view, viewGroup);
        AutocompletePrediction item = getItem(i);
        TextView textView = view2.findViewById(R.id.place_autocomplete_prediction_secondary_text);
        ((TextView) view2.findViewById(R.id.place_autocomplete_prediction_primary_text)).setText(item.getPrimaryText(STYLE_BOLD));
        textView.setText(item.getSecondaryText(STYLE_BOLD));
        return view2;
    }

    public Filter getFilter() {
        return new Filter() {
            public FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if (charSequence != null) {
                    getAutocomplete(charSequence, success -> {
                        mResultList = success.getAutocompletePredictions();

                        Log.e(TAG, "size of results: " + mResultList.size());

                        if (mResultList != null) {
                            filterResults.values = mResultList;
                            filterResults.count = mResultList.size();
                        }

                    });

                }
                return filterResults;
            }

            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults == null || filterResults.count <= 0) {
                    notifyDataSetInvalidated();
                } else {
                    notifyDataSetChanged();
                }
            }

            public CharSequence convertResultToString(Object obj) {
                if (obj instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) obj).getFullText(null);
                }
                return super.convertResultToString(obj);
            }
        };
    }

    public void getAutocomplete(CharSequence charSequence, OnSuccessListener<FindAutocompletePredictionsResponse> successListener) {
//        if (this.mGoogleApiClient.isConnected()) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Query for: ");
        sb.append(charSequence);
        Log.i(str, sb.toString());
//            AutocompletePredictionBuffer autocompletePredictionBuffer = (AutocompletePredictionBuffer) Places.GeoDataApi.getAutocompletePredictions(this.mGoogleApiClient, charSequence.toString(), this.mBounds, this.mPlaceFilter).await(60, TimeUnit.SECONDS);
//            AutocompletePredictionBuffer autocompletePredictionBuffer = Places.GeoDataApi.getAutocompletePredictions(this.mGoogleApiClient, charSequence.toString(), this.mBounds, this.mPlaceFilter).await(60, TimeUnit.SECONDS);
//            AutocompletePredictionBuffer autocompletePredictionBuffer = GeodataApi.getAutocompletePredictions(this.mGoogleApiClient, charSequence.toString(), this.mBounds, this.mPlaceFilter).await(60, TimeUnit.SECONDS);


//            PendingResult<AutocompletePredictionBuffer> results = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, charSequence.toString(), this.mBounds, this.mPlaceFilter);//.await(60, TimeUnit.SECONDS);
//            AutocompletePredictionBuffer autocompletePredictionBuffer = results.await(60, TimeUnit.SECONDS);
////            AutocompletePredictionBuffer autocompletePredictionBuffer = (AutocompletePredictionBuffer) Places.getGeoDataClient(getContext()).getAutocompletePredictions(this.mGoogleApiClient, charSequence.toString(), this.mBounds, this.mPlaceFilter).await(60, TimeUnit.SECONDS);
//            Status status = autocompletePredictionBuffer.getStatus();
//            if (!status.isSuccess()) {
//                Context context = getContext();
//                StringBuilder sb2 = new StringBuilder();
//                sb2.append("Error contacting API: ");
//                sb2.append(status.toString());
//                Toast.makeText(context, sb2.toString(), Toast.LENGTH_SHORT).show();
//                String str2 = TAG;
//                StringBuilder sb3 = new StringBuilder();
//                sb3.append("Error getting autocomplete prediction API call: ");
//                sb3.append(status.toString());
//                Log.e(str2, sb3.toString());
//                autocompletePredictionBuffer.release();
//                return null;
//            }
//            String str3 = TAG;
//            StringBuilder sb4 = new StringBuilder();
//            sb4.append("Query completed. Received ");
//            sb4.append(autocompletePredictionBuffer.getCount());
//            sb4.append(" predictions.");
//            Log.i(str3, sb4.toString());
//            return DataBufferUtils.freezeAndClose(autocompletePredictionBuffer);


        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(mBounds);
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
//                .setCountry("ng")//Nigeria
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(charSequence.toString())
                .build();


        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(successListener)
//                .addOnSuccessListener(PlaceAutoCompleteItem::onSuccess)
                .addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                    }
                });
//
//        }
//        Log.e(TAG, "Google API client is not connected for autocomplete query.");
//        return null;
    }
}
