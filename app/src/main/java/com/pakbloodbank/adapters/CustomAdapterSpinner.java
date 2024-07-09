package com.pakbloodbank.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pakbloodbank.R;

import java.util.List;

public class CustomAdapterSpinner extends BaseAdapter {

    private List<String> dataItems;
    private Context context;

    public CustomAdapterSpinner(List<String> dataItems, Context context) {
        this.dataItems = dataItems;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataItems.size();
    }

    @Override
    public Object getItem(int position) {
        return dataItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view==null)
        {
            LayoutInflater inflater=LayoutInflater.from(context);
            view=inflater.inflate(R.layout.spinner,parent, false );
        }

        TextView textView=view.findViewById(R.id.item_spinner_text);
        String item=dataItems.get(position);
        textView.setText(item);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        //return super.getDropDownView(position, convertView, parent);
        return getView(position,convertView,parent);
    }
}
