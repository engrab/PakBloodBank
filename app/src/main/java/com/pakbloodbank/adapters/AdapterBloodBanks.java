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
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pakbloodbank.R;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.items.BloodBanksItem;
import com.pakbloodbank.utils.Methods;
import com.pakbloodbank.utils.UrlHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.pakbloodbank.utils.Constant.TAG;

public class AdapterBloodBanks extends RecyclerView.Adapter<AdapterBloodBanks.MyViewHolder> {

    private       boolean                   isEdit;
    private       ArrayList<BloodBanksItem> arrayListFiltered;
    private       ArrayList<BloodBanksItem> arrayList;
    private final Context                   context;
    boolean horizontal;

    public AdapterBloodBanks(Context context, ArrayList<BloodBanksItem> arrayList, boolean isEdit, boolean horizontal) {
        this.arrayListFiltered = arrayList;
        this.context           = context;
        this.arrayList         = arrayList;
        this.isEdit            = isEdit;
        this.horizontal        = horizontal;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (horizontal)
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_bank_horizontal, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_bank_vertical, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final BloodBanksItem item = arrayListFiltered.get(position);
        holder.name.setText(item.getName());
        holder.time_ago.setText(item.getTimeAgo());
        holder.city.setText(item.getCityName());
        holder.distance.setText(item.getDistance() + context.getString(R.string.kilometer));
        holder.country.setText(item.getCountryName());
        holder.address.setText(item.getAddress());
        holder.contact.setText(item.getContact());
        holder.views.setText(" " + item.getViews());

        holder.itemView.setOnClickListener(v -> {
            showBloodBanksItemInformation(item);

            int prevViews = Integer.parseInt(holder.views.getText().toString().replace(" ", ""));
            prevViews++;
            item.setViews("" + prevViews);
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

        TextView name, address, time_ago, city, distance, country, contact, views;

        private MyViewHolder(View v) {
            super(v);

            name     = v.findViewById(R.id.bbank_name);
            address  = v.findViewById(R.id.address);
            time_ago = v.findViewById(R.id.timeAgoText);
            city     = v.findViewById(R.id.city);
            distance = v.findViewById(R.id.distance);
            country  = v.findViewById(R.id.country);
            contact  = v.findViewById(R.id.bbank_contact);
            views    = v.findViewById(R.id.views);


        }
    }

    private void showBloodBanksItemInformation(final BloodBanksItem bbank) {

        countView(bbank.getId());


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.blood_bank_information_dialog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width  = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final StringBuilder shareText = new StringBuilder();
        final ImageView        call_btn  = dialog.findViewById(R.id.call_btn);
        final ImageView     close     = dialog.findViewById(R.id.bt_close);
        final ImageView     shareIt   = dialog.findViewById(R.id.bt_share);
        final Button locate_in_google_maps = dialog.findViewById(R.id.locate_in_google_maps);


        TextView name         = dialog.findViewById(R.id.name);
        TextView mobile       = dialog.findViewById(R.id.mobile);
        TextView distance     = dialog.findViewById(R.id.distance);
        TextView country      = dialog.findViewById(R.id.country);
        TextView state        = dialog.findViewById(R.id.state);
        TextView city         = dialog.findViewById(R.id.city);
        TextView views        = dialog.findViewById(R.id.views);
        TextView address      = dialog.findViewById(R.id.address);
        TextView member_since = dialog.findViewById(R.id.member_since);

        name.setText(bbank.getName());
        distance.setText(String.format("%s %s", bbank.getDistance(), context.getString(R.string.kilometer)));
        country.setText(bbank.getCountryName());
        mobile.setText(bbank.getContact());
        state.setText(bbank.getStateName());
        city.setText(bbank.getCityName());
        views.setText(bbank.getViews());
        address.setText(bbank.getAddress());
        member_since.setText(bbank.getCreated());

        shareText.append(context.getString(R.string.blood_bank_name)).append(bbank.getName()).append("\n");
        shareText.append(context.getString(R.string.region)).append(bbank.getCityName()).append(", ").append(bbank.getStateName()).append(", ").append(bbank.getCountryName()).append("\n");
        shareText.append(context.getString(R.string.contact)).append(bbank.getContact()).append("\n");
        shareText.append(context.getString(R.string.address)).append(bbank.getAddress()).append("\n");


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

            ((MainActivity) context).CallToPHone(bbank.getContact());

            dialog.dismiss();


        });

        locate_in_google_maps.setOnClickListener(view -> {

            Methods.openGoogleMaps(context, bbank.getLatitude(), bbank.getLongitude());

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
                params.put("type", "blood_bank");
                return params;
            }
        };

        q.add(request);
    }


}