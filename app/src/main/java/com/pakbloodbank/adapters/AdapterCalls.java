package com.pakbloodbank.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.pakbloodbank.R;
import com.pakbloodbank.activities.MainActivity;
import com.pakbloodbank.items.ItemCallRate;
import com.pakbloodbank.items.ItemCalls;

import java.util.ArrayList;


public class AdapterCalls extends RecyclerView.Adapter<AdapterCalls.MyViewHolder> {

	private ArrayList<ItemCalls> arrayListFiltered;
	private final ArrayList<ItemCalls> arrayList;
	private NameFilter filter;
	private final Context context;


	public AdapterCalls(Context context, ArrayList<ItemCalls> arrayList) {
		this.arrayListFiltered = arrayList;
		this.context = context;
		this.arrayList = arrayList;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calls, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

		final ItemCalls item = arrayListFiltered.get(position);
		holder.name.setText(item.getToName());
		holder.phone.setText(item.getToPhone());
		holder.date.setText(item.getDate());

		if (item.getIsRated().equalsIgnoreCase("1"))
			holder.rateBtn.setVisibility(View.GONE);
		else
			holder.rateBtn.setVisibility(View.VISIBLE);

		holder.rateBtn.setOnClickListener(v -> showRateChooserDialog(item));


	}

	private void showRateChooserDialog(final ItemCalls item) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		View dialogView = LayoutInflater.from(context).inflate(R.layout.donor_feedback_layout, null);
		final TextView feedbackSubject = dialogView.findViewById(R.id.feedback_subject);
		final TextView feedbackContent = dialogView.findViewById(R.id.feedback_content);
		final RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);

		builder.setView(dialogView);

		AppCompatButton cancel = dialogView.findViewById(R.id.cancelBtn);
		AppCompatButton submit = dialogView.findViewById(R.id.submitBtn);

		final AlertDialog dialog = builder.create();
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		cancel.setOnClickListener(v -> dialog.dismiss());

		submit.setOnClickListener(v -> {
			dialog.dismiss();


			String donatedOrNot = "";
			int type = radioGroup.getCheckedRadioButtonId();
			if (type != -1) {
				RadioButton btn = radioGroup.findViewById(type);
				donatedOrNot = btn.getText().toString();
			}

			String name = feedbackSubject.getText().toString();
			String content = feedbackContent.getText().toString();


//			Toast.makeText(context, "Name: " + name + "\nFeedback: " + content, Toast.LENGTH_SHORT).show();

			if (donatedOrNot.equalsIgnoreCase("donated")) {
				donatedOrNot = "1";
			} else {
				donatedOrNot = "0";
			}
			ItemCallRate rate = new ItemCallRate(item.getId(), name, content, donatedOrNot);

			((MainActivity) context).rateCall(rate, "calls");
		});

		dialog.show();


	}

	@Override
	public int getItemCount() {
		return arrayListFiltered.size();
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

	class MyViewHolder extends RecyclerView.ViewHolder {


		final TextView name, phone, date;
		final ImageView rateBtn;

		private MyViewHolder(View view) {
			super(view);
			name = view.findViewById(R.id.name);
			phone = view.findViewById(R.id.phone);
			date = view.findViewById(R.id.date);
			rateBtn = view.findViewById(R.id.rateDonorBtn);


		}
	}

	private class NameFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			constraint = constraint.toString().toLowerCase();
			FilterResults result = new FilterResults();
			if (constraint.toString().length() > 0) {
				ArrayList<ItemCalls> filteredItems = new ArrayList<>();

				for (int i = 0, l = arrayList.size(); i < l; i++) {
					ItemCalls bb = arrayList.get(i);

				}
				result.count = filteredItems.size();
				result.values = filteredItems;
			} else {
				synchronized (this) {
					result.values = arrayList;
					result.count = arrayList.size();
				}
			}
			return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
		                              FilterResults results) {

			arrayListFiltered = (ArrayList<ItemCalls>) results.values;
			notifyDataSetChanged();
		}
	}

}