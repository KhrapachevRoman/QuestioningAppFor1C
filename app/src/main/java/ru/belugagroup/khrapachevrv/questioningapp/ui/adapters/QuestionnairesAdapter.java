package ru.belugagroup.khrapachevrv.questioningapp.ui.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.belugagroup.khrapachevrv.questioningapp.R;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionnaire;
import ru.belugagroup.khrapachevrv.questioningapp.models.prefs.PreferenceUtils;

public class QuestionnairesAdapter extends RecyclerView.Adapter<QuestionnairesAdapter.ViewHolder> implements Filterable {

    private final static String TAG = "QuestionnairesAdapter";

    private final List<DbQuestionnaire> data;
    private List<DbQuestionnaire> filteredData;
    private final OnItemClickListener listener;

    public QuestionnairesAdapter(List<DbQuestionnaire> data, OnItemClickListener listener) {
        this.data = data;
        this.filteredData = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuestionnairesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questionnaire_list_item, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionnairesAdapter.ViewHolder holder, int position) {

        holder.itemClick(filteredData.get(position), listener);
        holder.tvPartner.setText(filteredData.get(position).getRespondent().getName());
        holder.tvTemplates.setText(filteredData.get(position).getTemplate().getTitle());
        String dateString = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(filteredData.get(position).getDateInMillis());
        holder.tvDate.setText(dateString);
        if (filteredData.get(position).getDateInMillis() > PreferenceUtils.getLastSyncTime()) {
            //holder.tvStatus.setText("(｡╯︵╰｡)");
            holder.tvStatus.setText("-");
            holder.tvStatus.setTextColor(Color.parseColor("#ef5350"));
        } else {
            //holder.tvStatus.setText("٩(｡•́‿•̀｡)۶");
            holder.tvStatus.setText("+");
            holder.tvStatus.setTextColor(Color.parseColor("#43a047"));
        }

    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public interface OnItemClickListener {
        void onItemClick(DbQuestionnaire Item);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPartner;
        final TextView tvTemplates;
        final TextView tvDate;
        final TextView tvStatus;
        final CardView container;

        ViewHolder(View itemView) {
            super(itemView);
            tvPartner = itemView.findViewById(R.id.questionnaire_list_item_partner);
            tvTemplates = itemView.findViewById(R.id.questionnaire_list_item_templates);
            tvDate = itemView.findViewById(R.id.questionnaire_list_item_date);
            container = itemView.findViewById(R.id.questionnaire_list_item_container);
            tvStatus = itemView.findViewById(R.id.questionnaire_list_item_status);
        }

        void itemClick(final DbQuestionnaire item, final OnItemClickListener listener) {
            container.setOnClickListener(new View.OnClickListener() {
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
            protected void publishResults(CharSequence constraint, FilterResults results) {

                filteredData = (ArrayList<DbQuestionnaire>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                Log.d(TAG, "constraint = " + constraint);
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filteredData = data;
                } else {
                    List<DbQuestionnaire> filteredList = new ArrayList<>();
                    for (DbQuestionnaire row : data) {

                        if (row.getRespondent().getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getTemplate().getTitle().toLowerCase().contains(charString.toLowerCase())) {

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
