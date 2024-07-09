package com.pakbloodbank.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pakbloodbank.R;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.items.ItemBlog;
import com.pakbloodbank.utils.Constant;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;


public class AdapterBlog extends RecyclerView.Adapter<AdapterBlog.MyViewHolder> {

    private ArrayList<ItemBlog> arrayList;
    private ArrayList<ItemBlog> filteredArrayList;
    private NameFilter filter;
    private final Context context;

    public AdapterBlog(Context context, ArrayList<ItemBlog> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
        this.filteredArrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_blog, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ItemBlog item = filteredArrayList.get(position);

        holder.id.setText(item.getId());
        holder.title.setText(item.getS_title());
        holder.desc.setText(Html.fromHtml(item.getS_content()).toString());
        holder.postedAt.setText(String.format(context.getString(R.string.posted_at)+ ": %s", item.getPosted_at()));

        Glide.with(context).load(item.getBlog_image())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.blogImage.setImageResource(R.mipmap.ic_launcher);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(holder.blogImage);

        handleClick(holder, item);

    }

    private void handleClick(final MyViewHolder holder, final ItemBlog item) {
        holder.itemView.setOnClickListener(v -> {

            Constant.itemBlog = item;

            ((MainActivity) context).loadBlogDetails();
        });
    }

    @Override
    public int getItemCount() {
        return filteredArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView id, title, desc, postedAt;
        ImageView blogImage;

        private MyViewHolder(View v) {
            super(v);
            id = v.findViewById(R.id.pid);
            title = v.findViewById(R.id.blogTitle);
            postedAt = v.findViewById(R.id.postedAt);
            desc = v.findViewById(R.id.blogDesc);
            blogImage = v.findViewById(R.id.blogThumb);
        }
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();


            FilterResults result = new FilterResults();

            if (constraint.toString().length() > 0) {
                ArrayList<ItemBlog> filteredItems = new ArrayList<>();

                for (int i = 0, l = arrayList.size(); i < l; i++) {

                    ItemBlog dd = arrayList.get(i);

                    if (
                            dd.getBlog_title().toLowerCase().contains(constraint)
                                    || dd.getPosted_at().toLowerCase().contains(constraint)
                                    || dd.getS_content().toLowerCase().contains(constraint)
                    ) {
                        filteredItems.add(dd);
                    }
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else if (((String) constraint).isEmpty()){
                filteredArrayList = arrayList;
                result.values = filteredArrayList;
                result.count = filteredArrayList.size();
            }
            else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            filteredArrayList = (ArrayList<ItemBlog>) results.values;
            notifyDataSetChanged();
        }
    }
}