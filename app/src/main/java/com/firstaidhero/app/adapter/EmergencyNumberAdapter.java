package com.firstaidhero.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firstaidhero.app.R;

import java.util.List;

public class EmergencyNumberAdapter extends RecyclerView.Adapter<EmergencyNumberAdapter.ViewHolder> {

    private final List<String[]> contactList;
    private final Context context;

    public EmergencyNumberAdapter(Context context, List<String[]> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_number, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] contact = contactList.get(position);
        holder.tvContactName.setText(contact[0]);
        holder.tvPhoneNumber.setText(contact[1]);



        holder.btnCall.setOnClickListener(v -> {
            String phoneNumber = contact[1];


            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));

            try {
                context.startActivity(callIntent);
            } catch (Exception e) {
                Toast.makeText(context, "لا يوجد تطبيق هاتف متاح", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvContactName, tvPhoneNumber;
        ImageView btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }
}