package ru.belugagroup.khrapachevrv.questioningapp.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import ru.belugagroup.khrapachevrv.questioningapp.R;
import ru.belugagroup.khrapachevrv.questioningapp.models.db.DbQuestionForQuestionnaire;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private final static String TAG = "QuestionsAdapter";

    private final List<DbQuestionForQuestionnaire> data;
    private final OnItemClickListener listener;
    private final Boolean editable;

    public QuestionsAdapter(List<DbQuestionForQuestionnaire> data, OnItemClickListener listener, boolean editable) {
        this.data = data;
        this.listener = listener;
        this.editable = editable;
    }

    @NonNull
    @Override
    public QuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questionnaires_list_item, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsAdapter.ViewHolder holder, int position) {

        holder.itemClick(position, listener);
        holder.tvSectionName.setText(data.get(position).getQuestion().getText());
        holder.tvHint.setText(data.get(position).getQuestion().getHint());
        holder.checkBox.setText(data.get(position).getQuestion().getText());
        holder.checkBox.setChecked(data.get(position).getChecked());

        holder.tvHint.setVisibility(View.GONE);

        if (!data.get(position).getQuestion().getIsGroup()){
            holder.tvSectionName.setVisibility(View.GONE);
            if (!data.get(position).getQuestion().getHint().isEmpty()){
                holder.tvHint.setVisibility(View.VISIBLE);
            }
            holder.checkBox.setVisibility(View.VISIBLE);
        }else{
            holder.tvSectionName.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.GONE);
        }


        holder.checkBox.setEnabled(editable);


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, boolean isChecked);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSectionName;
        final TextView tvHint;
        final CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            tvSectionName = itemView.findViewById(R.id.questionnaires_list_item_tv_section_name);
            tvHint  =   itemView.findViewById(R.id.questionnaires_list_item_tv_section_hint);
            checkBox = itemView.findViewById(R.id.questionnaires_list_item_tv_question_check_box);
        }

        void itemClick(final int position, final OnItemClickListener listener) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.onItemClick(position,isChecked);
                }
            });
           /* checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });*/
        }

    }


}
