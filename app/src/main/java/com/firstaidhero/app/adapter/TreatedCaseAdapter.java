package com.firstaidhero.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.R;
import com.firstaidhero.app.model.TreatedCase;
import java.util.List;

public class TreatedCaseAdapter extends RecyclerView.Adapter<TreatedCaseAdapter.ViewHolder> {

    private List<TreatedCase> caseList;

    public TreatedCaseAdapter(List<TreatedCase> caseList) {
        this.caseList = caseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_treated_case, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TreatedCase treatedCase = caseList.get(position);
        holder.tvDate.setText(treatedCase.getDate());
        holder.tvDescription.setText(treatedCase.getDescription());
        holder.tvProcedures.setText(treatedCase.getProcedures());

        if (treatedCase.getPhoto() != null && !treatedCase.getPhoto().isEmpty()) {
            holder.tvPhoto.setVisibility(View.VISIBLE);
            holder.tvPhoto.setText("📷 صورة مرفقة: " + treatedCase.getPhoto());
        } else {
            holder.tvPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return caseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvDescription, tvProcedures, tvPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvCaseDate);
            tvDescription = itemView.findViewById(R.id.tvCaseDescription);
            tvProcedures = itemView.findViewById(R.id.tvCaseProcedures);
            tvPhoto = itemView.findViewById(R.id.tvCasePhoto);
        }
    }
}