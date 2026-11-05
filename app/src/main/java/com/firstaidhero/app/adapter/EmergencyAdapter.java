package com.firstaidhero.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.R;
import com.firstaidhero.app.model.Emergency;
import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.ViewHolder> {

    private final List<Emergency> emergencyList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Emergency emergency);
    }

    public EmergencyAdapter(List<Emergency> emergencyList, OnItemClickListener listener) {
        this.emergencyList = emergencyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Emergency emergency = emergencyList.get(position);
        holder.tvTitle.setText(emergency.getTitle());
        holder.tvDangerLevel.setText(emergency.getDangerLevel());
        holder.tvSymptoms.setText(emergency.getSymptoms());

        String dangerLevel = emergency.getDangerLevel();
        if (dangerLevel.equals("حرجة")) {
            holder.tvDangerLevel.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.red_emergency));
        } else if (dangerLevel.equals("متوسطة")) {
            holder.tvDangerLevel.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.orange_warning));
        } else {
            holder.tvDangerLevel.setBackgroundColor(
                    holder.itemView.getContext().getColor(R.color.green_success));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(emergency));
    }

    @Override
    public int getItemCount() {
        return emergencyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDangerLevel, tvSymptoms;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDangerLevel = itemView.findViewById(R.id.tvDangerLevel);
            tvSymptoms = itemView.findViewById(R.id.tvSymptoms);
        }
    }
}