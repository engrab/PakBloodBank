package com.pakbloodbank.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.activities.AddRequestActivity;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.items.RequestsItem;
import com.pakbloodbank.utils.Methods;
import com.pakbloodbank.utils.UrlHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.pakbloodbank.utils.Constant.TAG;

public class AdapterBloodRequests extends RecyclerView.Adapter<AdapterBloodRequests.MyViewHolder> {

    private boolean isEdit;
    private ArrayList<RequestsItem> arrayListFiltered;
    private ArrayList<RequestsItem> arrayList;
    private final Context context;
    boolean horizontal;

    public AdapterBloodRequests(Context context, ArrayList<RequestsItem> arrayList, boolean isEdit, boolean horizontal) {
        this.arrayListFiltered = arrayList;
        this.context = context;
        this.arrayList = arrayList;
        this.isEdit = isEdit;
        this.horizontal = horizontal;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (horizontal)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_horizontal, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_vertical, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final RequestsItem requestsItem = arrayListFiltered.get(position);
        holder.name.setText(requestsItem.getFullName());
        holder.blood_group.setText(requestsItem.getBloodGroup());
        holder.time_ago.setText(requestsItem.getTimeAgo());
        holder.city.setText(requestsItem.getCityName());
        holder.distance.setText(requestsItem.getDistance() + " " + context.getString(R.string.kilometer));
        holder.country.setText(requestsItem.getCountryName());
        holder.no_of_bags.setText(requestsItem.getNoOfBags() + " " + context.getString(R.string.no_of_bags));
        holder.views.setText(" " + requestsItem.getViews());

        if (isEdit) {

            holder.moreBtn.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, holder.moreBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.request_edit_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            Gson gson = new Gson();
                            context.startActivity(new Intent(context, AddRequestActivity.class).putExtra("reqId", requestsItem.getId()).putExtra("request_data", gson.toJson(requestsItem)));
                            break;
                        case R.id.deactivate:
                            ((MainActivity) context).confirmRequest(requestsItem.getId(), context.getString(R.string.deactivate_this_request), "deactivate", "request");
                            break;
                        case R.id.delete:
                            ((MainActivity) context).confirmRequest(requestsItem.getId(), context.getString(R.string.delete_this_request), "delete", "request");
                            break;
                        case R.id.fulfilled:
                            ((MainActivity) context).confirmRequest(requestsItem.getId(), context.getString(R.string.set_this_request_fulfilled), "fulfilled", "request");
                            break;
                    }
                    return true;
                });

                popup.show();
            });
        } else {
            holder.moreBtn.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            showRequestsItemInformation(requestsItem);

            int prevViews = Integer.parseInt(holder.views.getText().toString().replace(" ",""));
            prevViews++;
            requestsItem.setViews(""+prevViews);
            String viewsNow = " " + prevViews;
            holder.views.setText(viewsNow);


        });

    }

    @Override
    public int getItemCount() {
        return arrayListFiltered.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView moreBtn;
        TextView name, blood_group, time_ago, city, distance, country, no_of_bags, views;

        private MyViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.requestNameText);
            blood_group = v.findViewById(R.id.requestBloodGroupText);
            time_ago = v.findViewById(R.id.timeAgoText);
            city = v.findViewById(R.id.requestCityText);
            distance = v.findViewById(R.id.requestDistanceTextView);
            country = v.findViewById(R.id.requestCountryTextView);
            no_of_bags = v.findViewById(R.id.noOfBagsTextView);
            views = v.findViewById(R.id.requestViewsCountTextView);
            moreBtn = v.findViewById(R.id.more);


        }
    }

    private void showRequestsItemInformation(RequestsItem request) {

        countView(request.getId());


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.request_information_dialog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final StringBuilder shareText = new StringBuilder();
        final ImageView call_btn = dialog.findViewById(R.id.call_btn);
        final ImageView close = dialog.findViewById(R.id.bt_close);
        final ImageView shareIt = dialog.findViewById(R.id.bt_share);
        final Button locate_in_google_maps = dialog.findViewById(R.id.locate_in_google_maps);


        TextView name = dialog.findViewById(R.id.name);
        TextView no_of_bags = dialog.findViewById(R.id.no_of_bags);
        TextView requested_by = dialog.findViewById(R.id.requested_by);
        TextView distance = dialog.findViewById(R.id.distance);
        TextView blood_group = dialog.findViewById(R.id.blood_group);
        TextView mobile = dialog.findViewById(R.id.mobile);
        TextView country = dialog.findViewById(R.id.country);
        TextView state = dialog.findViewById(R.id.state);
        TextView city = dialog.findViewById(R.id.city);
        TextView views = dialog.findViewById(R.id.views);
        TextView address = dialog.findViewById(R.id.address);
        TextView hospital = dialog.findViewById(R.id.hospital);
        TextView requested_at = dialog.findViewById(R.id.requested_at);


        name.setText(request.getFullName());
        requested_by.setText(request.getAddedBy());
        distance.setText(request.getDistance());
        blood_group.setText(request.getBloodGroup());
        mobile.setText(request.getMobile());
        country.setText(request.getCountryName());
        state.setText(request.getStateName());
        city.setText(request.getCityName());
        no_of_bags.setText(request.getNoOfBags());
        views.setText(request.getViews());
        address.setText(request.getAddress());
        hospital.setText(request.getHospitalName());
        requested_at.setText(request.getCreated());


        shareText.append(context.getString(R.string.requester)+ ":   ").append(request.getFullName()).append("\n");
        shareText.append(context.getString(R.string.blood_group)+ ": ").append(request.getBloodGroup()).append("\n");
        shareText.append(context.getString(R.string.mobile)+ ":  ").append(request.getMobile()).append("\n");
        shareText.append(context.getString(R.string.bags_needed)+ ":  ").append(request.getNoOfBags()).append("\n");
        shareText.append(context.getString(R.string.region)+ ":  ").append(request.getCityName()).append(", ").append(request.getStateName()).append(", ").append(request.getCountryName()).append("\n");
        shareText.append(context.getString(R.string.address)+ ": ").append(request.getAddress()).append("\n");
        shareText.append(context.getString(R.string.hospital)+ ": ").append(request.getHospitalName()).append("\n");
        shareText.append(context.getString(R.string.requested_at)+ ": ").append(request.getCreated()).append("\n");


        close.setOnClickListener(v -> dialog.dismiss());
        shareIt.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, context.getString(R.string.share_with));
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            context.startActivity(shareIntent);

        });

        call_btn.setOnClickListener(view -> {

            ((MainActivity) context).CallToPHone(request.getMobile());

            dialog.dismiss();


        });

        locate_in_google_maps.setOnClickListener(view -> {

            Methods.openGoogleMaps(context, request.getLatitude(), request.getLongitude());

        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void countView(final String id) {
        RequestQueue q = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST,
                UrlHelper.countViewUrl, response -> Log.d(TAG, "onResponse: " + response), error -> Log.d(TAG, "onErrorResponse: " + error.toString())) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("type", "request");
                return params;
            }
        };

        q.add(request);
    }


}