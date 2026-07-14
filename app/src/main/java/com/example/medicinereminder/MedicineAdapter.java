package com.example.medicinereminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private List<Medicine> medicineList = new ArrayList<>();
    private final OnItemClickListener listener;
    private final OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Medicine medicine);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Medicine medicine);
    }

    public MedicineAdapter(OnItemClickListener listener, OnDeleteClickListener deleteListener) {
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.tvName.setText(medicine.getName());
        holder.tvDosage.setText(medicine.getDosage() != null ? medicine.getDosage() : "");
        holder.tvRemindTime.setText("⏰ " + medicine.getRemindTimes());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(medicine);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(medicine);
            }
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public void submitList(List<Medicine> newList) {
        medicineList.clear();
        if (newList != null) {
            medicineList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDosage, tvRemindTime;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_medicine_name);
            tvDosage = itemView.findViewById(R.id.tv_dosage);
            tvRemindTime = itemView.findViewById(R.id.tv_remind_time);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}