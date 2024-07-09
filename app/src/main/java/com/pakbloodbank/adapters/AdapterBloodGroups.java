package com.pakbloodbank.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pakbloodbank.R;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.fragments.FragmentDonorsList;
import com.pakbloodbank.fragments.FragmentRequestsList;
import com.pakbloodbank.items.DonorFilterItem;
import com.pakbloodbank.items.RequestFilterItem;
import com.pakbloodbank.utils.Constant;

import java.util.ArrayList;


public class AdapterBloodGroups extends RecyclerView.Adapter<AdapterBloodGroups.MyViewHolder> {

    private ArrayList<String> arrayListFiltered;
    private final Context context;
    String from;


    public AdapterBloodGroups(Context context, ArrayList<String> arrayList, String from) {
        this.arrayListFiltered = arrayList;
        this.context = context;
        this.from = from;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blood_group, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final String item = arrayListFiltered.get(position);
        holder.name.setText(item);



        holder.itemView.setOnClickListener(v -> {


            if (from.equals("donor")) {
                DonorFilterItem donorFilterItem = new DonorFilterItem().getFilterWith(item, String.valueOf(Constant.curr_latitude), String.valueOf(Constant.curr_longitude), "none", "none", null, null, null, null, null);
                FragmentDonorsList fragment = FragmentDonorsList.createInstance(donorFilterItem);
                ((MainActivity) context).loadFrag(fragment, item, ((MainActivity) context).fm, false);
            } else if (from.equals("request")) {
                RequestFilterItem requestFilterItem = new RequestFilterItem().getFilterWith(item, String.valueOf(Constant.curr_latitude), String.valueOf(Constant.curr_longitude), "none", "none", null, null, null, null);
                FragmentRequestsList fragment = FragmentRequestsList.createInstance(requestFilterItem);
                ((MainActivity) context).loadFrag(fragment, item, ((MainActivity) context).fm, false);
            }
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

    class MyViewHolder extends RecyclerView.ViewHolder {


        final TextView name;

        private MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.bloodGroup);

        }
    }


}