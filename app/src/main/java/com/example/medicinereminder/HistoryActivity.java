package com.example.medicinereminder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvRecords;
    private RecordAdapter adapter;
    private MedicineViewModel viewModel;
    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvRecords = findViewById(R.id.rv_records);
        tvDate = findViewById(R.id.tv_date);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(today);

        rvRecords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordAdapter(record -> {
            int newStatus = record.getStatus() == 1 ? 0 : 1;
            record.setStatus(newStatus);
            viewModel.updateRecord(record);
            Toast.makeText(this, newStatus == 1 ? "已标记为已服" : "已标记为未服", Toast.LENGTH_SHORT).show();
            // 无需手动刷新，LiveData 会自动触发
        }, record -> {
            new AlertDialog.Builder(this)
                    .setTitle("删除记录")
                    .setMessage("确定删除这条服药记录吗？")
                    .setPositiveButton("删除", (dialog, which) -> viewModel.deleteRecord(record))
                    .setNegativeButton("取消", null)
                    .show();
        });
        rvRecords.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        MedicineRepository repository = new MedicineRepository(getApplication());
        viewModel.setRepository(repository);

        viewModel.getRecordsByDate(today).observe(this, records -> {
            adapter.submitList(records);
        });
    }

    private static class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.VH> {
        private List<Record> list;
        private final OnItemClickListener clickListener;
        private final OnItemLongClickListener longClickListener;

        interface OnItemClickListener { void onClick(Record record); }
        interface OnItemLongClickListener { void onLongClick(Record record); }

        RecordAdapter(OnItemClickListener click, OnItemLongClickListener longClick) {
            this.clickListener = click;
            this.longClickListener = longClick;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            Record record = list.get(position);
            holder.text1.setText(record.getMedicineName());
            String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(record.getScheduledTime()));
            String status = record.getStatus() == 1 ? "✅ 已服" : "⏳ 未服";
            holder.text2.setText(time + "  " + status);
            holder.itemView.setOnClickListener(v -> clickListener.onClick(record));
            holder.itemView.setOnLongClickListener(v -> {
                longClickListener.onLongClick(record);
                return true;
            });
        }

        @Override
        public int getItemCount() { return list == null ? 0 : list.size(); }

        public void submitList(List<Record> newList) {
            list = newList;
            notifyDataSetChanged();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView text1, text2;
            VH(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}