package com.pakbloodbank.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import com.pakbloodbank.activities.AddDonorActivity;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.items.Donor;
import com.pakbloodbank.utils.Methods;
import com.pakbloodbank.utils.UrlHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.pakbloodbank.utils.Constant.TAG;

public class AdapterBloodDonors extends RecyclerView.Adapter<AdapterBloodDonors.MyViewHolder> {

    private boolean isEdit;
    private ArrayList<Donor> arrayListFiltered;
    private ArrayList<Donor> arrayList;
    private final Context context;
    boolean horizontal;

    public AdapterBloodDonors(Context context, ArrayList<Donor> arrayList, boolean isEdit, boolean horizontal) {
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
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donor_horizontal_bloodgroup, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donor_vertical_bloodgroup, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Donor donor = arrayListFiltered.get(position);


        //Log.d("ArrayListDonor", arrayList.toString());
        //VALUE OF DONORS AT HOME PAGE
//        Log.d("Donor filter data", String.valueOf(donor));
//      Log.d("Donor filter data", String.valueOf(donor.getFullName()));



        holder.name.setText(donor.getFullName());
        holder.blood_group.setText(donor.getBloodGroup());
        holder.time_ago.setText(donor.getTimeAgo());
        holder.city.setText(donor.getCityName());
        holder.distance.setText(donor.getDistance() + " " + context.getString(R.string.kilometer));
        holder.country.setText(donor.getCountryName());
        holder.type.setText(donor.getType());
        holder.points.setText(donor.getPoints() + " " + context.getString(R.string.points));
        holder.views.setText(" " + donor.getViews());


        if (isEdit) {

            holder.moreBtn.setVisibility(View.VISIBLE);
            holder.moreBtn.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, holder.moreBtn);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.donor_edit_popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.edit:

                            Gson gson = new Gson();
                            context.startActivity(new Intent(context, AddDonorActivity.class).putExtra("donorId", donor.getId()).putExtra("donorData", gson.toJson(donor)));

                            break;
                        case R.id.deactivate:
                            ((MainActivity) context).confirmRequest(donor.getId(), context.getString(R.string.deactivate_this_donor), "deactivate", "donor");

                            break;
                        case R.id.delete:
                            ((MainActivity) context).confirmRequest(donor.getId(), context.getString(R.string.delete_this_donor), "delete", "donor");

                            break;
                    }

                    new Handler().postDelayed(this::notifyDataSetChanged, 3000);
                    return true;
                });

                popup.show();
            });
        } else {
            holder.moreBtn.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            showDonorInformation(donor);

            int prevViews = Integer.parseInt(holder.views.getText().toString().replace(" ", ""));
            prevViews++;
            donor.setViews("" + prevViews);
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, blood_group, time_ago, city, distance, country, type, points, views;
        ImageView moreBtn;

        private MyViewHolder(View v) {
            super(v);

            name = v.findViewById(R.id.donorNameText);
            blood_group = v.findViewById(R.id.donorBloodGroupText);
            time_ago = v.findViewById(R.id.timeAgoText);
            city = v.findViewById(R.id.donorCityText);
            distance = v.findViewById(R.id.donorDistanceTextView);
            country = v.findViewById(R.id.donorCountryTextView);
            type = v.findViewById(R.id.donorTypeTextView);
            points = v.findViewById(R.id.donorPointsTextView);
            views = v.findViewById(R.id.donorViewsCountTextView);
            moreBtn = v.findViewById(R.id.more);


        }
    }


    private void showDonorInformation(final Donor donor) {

        countView(donor.getId());


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.donors_information_dialog);
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

        TextView donor_name = dialog.findViewById(R.id.donor_name);
        TextView donor_type = dialog.findViewById(R.id.donor_type);
        TextView registered_by = dialog.findViewById(R.id.registered_by);
        TextView distance = dialog.findViewById(R.id.distance);
        TextView blood_group = dialog.findViewById(R.id.blood_group);
        TextView donor_mobile = dialog.findViewById(R.id.donor_mobile);
        TextView donor_country = dialog.findViewById(R.id.donor_country);
        TextView donor_state = dialog.findViewById(R.id.donor_state);
        TextView donor_city = dialog.findViewById(R.id.donor_city);
        TextView donor_last_donation = dialog.findViewById(R.id.last_donated_date);
        TextView donor_points = dialog.findViewById(R.id.donor_points);
        TextView donor_views = dialog.findViewById(R.id.donor_views);
        TextView donor_habits = dialog.findViewById(R.id.donor_habits);
        TextView donor_address = dialog.findViewById(R.id.donor_address);
        TextView donor_member_since = dialog.findViewById(R.id.member_since);


        donor_name.setText(donor.getFullName());
        donor_type.setText(donor.getType());
        registered_by.setText(donor.getAddedBy());
        distance.setText(String.format("%s %s", donor.getDistance(), context.getString(R.string.kilometer)));
        blood_group.setText(donor.getBloodGroup());
        donor_mobile.setText(donor.getMobile());
        donor_country.setText(donor.getCountryName());
        donor_state.setText(donor.getStateName());
        donor_city.setText(donor.getCityName());
        donor_last_donation.setText(donor.getLastDonationDate());
        donor_points.setText(donor.getPoints());
        donor_views.setText(donor.getViews());
        donor_habits.setText(donor.getHabits());
        donor_address.setText(donor.getAddress());
        donor_member_since.setText(donor.getCreated());


        shareText.append(context.getString(R.string.donor) + ": ").append(donor.getFullName()).append("\n");
        shareText.append(context.getString(R.string.type) + ": ").append(donor.getType()).append("\n");
        shareText.append(context.getString(R.string.blood_group) + ": ").append(donor.getBloodGroup()).append("\n");
        shareText.append(context.getString(R.string.mobile) + ": ").append(donor.getMobile()).append("\n");
        shareText.append(context.getString(R.string.region) + ": ").append(donor.getCityName()).append(", ").append(donor.getStateName()).append(", ").append(donor.getCountryName()).append("\n");
        shareText.append(context.getString(R.string.address) + ": ").append(donor.getAddress()).append("\n");
        shareText.append(context.getString(R.string.last_donation) + ": ").append(donor.getLastDonationDate()).append("\n");

        close.setOnClickListener(v -> dialog.dismiss());


        shareIt.setOnClickListener(v -> {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Share with");
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            context.startActivity(shareIntent);
        });

        call_btn.setOnClickListener(view -> {

            ((MainActivity) context).CallToDonorPHone(donor.getMobile(), donor.getId());
            dialog.dismiss();
        });

        locate_in_google_maps.setOnClickListener(view -> {

            Methods.openGoogleMaps(context, donor.getLatitude(), donor.getLongitude());

        });

        dialog.show();
        dialog.getWindow().

                setAttributes(lp);
    }

    private void countView(final String id) {
        RequestQueue q = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST,
                UrlHelper.countViewUrl, response -> Log.d(TAG, "onResponse: " + response), error -> Log.d(TAG, "onErrorResponse: " + error.toString())) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("type", "donor");
                return params;
            }
        };

        q.add(request);
    }


}