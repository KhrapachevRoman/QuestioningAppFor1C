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
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbPerson;

public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.ViewHolder> implements Filterable {

    private final static String TAG = "PersonsAdapter";

    private final List<DbPerson> data;
    private List<DbPerson> filteredData;
    private final OnItemClickListener listener;

    public PersonsAdapter(List<DbPerson> data, OnItemClickListener listener) {
        this.data = data;
        this.filteredData = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PersonsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_list_item, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonsAdapter.ViewHolder holder, int position) {

      //  if (filteredData != null && !filteredData.isEmpty()){
            holder.itemClick(filteredData.get(position), listener);
            holder.tvPartnerText.setText(filteredData.get(position).getName());
      //  }

    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(DbPerson Item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPartnerText;

        ViewHolder(View itemView) {
            super(itemView);
            tvPartnerText = itemView.findViewById(R.id.common_list_item_text);
        }

        void itemClick(final DbPerson item, final OnItemClickListener listener) {
            tvPartnerText.setOnClickListener(new View.OnClickListener() {
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

                filteredData = (ArrayList<DbPerson>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                Log.d(TAG, "constraint = "+ constraint);
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    Log.d(TAG, "charString.isEmpty() = true");
                    filteredData = data;
                } else {
                    Log.d(TAG, "charString.isEmpty() = false");
                    List<DbPerson> filteredList = new ArrayList<>();
                    for (DbPerson row : data) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) ) {

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
