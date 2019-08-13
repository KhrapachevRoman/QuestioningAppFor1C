package ru.belugagroup.khrapachevrv.questioningapp.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.R;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbTemplate;

public class PollsAdapter extends RecyclerView.Adapter<PollsAdapter.ViewHolder> implements Filterable {

    private final static String TAG = "PollsAdapter";

    private final List<DbTemplate> data;
    private List<DbTemplate> filteredData;
    private final OnItemClickListener listener;

    public PollsAdapter(List<DbTemplate> data, OnItemClickListener listener) {
        this.data = data;
        this.filteredData = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PollsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.polls_list_item, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PollsAdapter.ViewHolder holder, int position) {

        holder.itemClick(filteredData.get(position), listener);
        holder.tvPollText.setText(filteredData.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(DbTemplate Item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPollText;

        ViewHolder(View itemView) {
            super(itemView);
            tvPollText = itemView.findViewById(R.id.polls_list_item_text);
        }

        void itemClick(final DbTemplate item, final OnItemClickListener listener) {
            tvPollText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                filteredData = (ArrayList<DbTemplate>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                Log.d(TAG, "constraint = "+ constraint);
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredData = data;
                } else {
                    List<DbTemplate> filteredList = new ArrayList<>();
                    for (DbTemplate row : data) {

                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) ) {

                            filteredList.add(row);
                        }
                    }

                    filteredData = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredData;
                return filterResults;
            }
        };
    }

}
