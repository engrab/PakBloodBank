package com.pakbloodbank.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.pakbloodbank.R;
import com.pakbloodbank.items.ItemBlog;
import com.pakbloodbank.utils.Constant;
import com.pakbloodbank.utils.Methods;
import com.bumptech.glide.Glide;

public class FragmentBlogDesc extends Fragment {

    private Methods methods;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blog_detail, container, false);

        methods = new Methods(getActivity());

        ItemBlog itemBlog = Constant.itemBlog;

        WebView webView = rootView.findViewById(R.id.webview);
        ImageView imageView = rootView.findViewById(R.id.blogImage);
        TextView posted = rootView.findViewById(R.id.postedAt);
        TextView title = rootView.findViewById(R.id.postTitle);


        Glide.with(this).load(itemBlog.getBlog_image()).into(imageView);


        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
                "<html><head>" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                "</head><body>";

        content += itemBlog.getBlog_content() + "</body></html>";

        webView.loadData(content, "text/html; charset=utf-8", "UTF-8");

        title.setText(itemBlog.getBlog_title());
        posted.setText(String.format("Posted at: %s", itemBlog.getPosted_at()));


        return rootView;
    }
}
