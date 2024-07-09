package com.pakbloodbank.utils.custom;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pakbloodbank.R;

public class SpinnerWithImageAdapter extends ArrayAdapter<String> {

    String[] names;
    int[] resources;
    Context context;


    public SpinnerWithImageAdapter(Context context, String[] names, int[] resources) {
        super(context, R.layout.lang_spinner_item, names);
        this.context = context;
        this.names = names;
        this.resources = resources;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lang_spinner_item, null);

        TextView textView = view.findViewById(R.id.languageName);
        ImageView imageView = view.findViewById(R.id.languageFlag);

        textView.setText(names[position]);
        imageView.setImageResource(resources[position]);

        return view;
    }

    @Nullable
    @Override
    public Resources.Theme getDropDownViewTheme() {
        return super.getDropDownViewTheme();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lang_spinner_item, null);

        TextView textView = view.findViewById(R.id.languageName);
        ImageView imageView = view.findViewById(R.id.languageFlag);

        textView.setText(names[position]);
        imageView.setImageResource(resources[position]);

        return view;
    }


}
