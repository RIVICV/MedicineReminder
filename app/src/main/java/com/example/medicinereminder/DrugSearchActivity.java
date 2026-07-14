package com.example.medicinereminder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrugSearchActivity extends AppCompatActivity {

    // 重要：请填入你在天聚数行申请的ApiKey
    private static final String TIANAPI_KEY = "f0f52a915fec882b0e6732433b5d54a4";

    private EditText etDrugName;
    private Button btnSearch;
    private RecyclerView rvDrugResults;
    private ProgressBar progressBar;
    private TextView tvResultCount;
    private TextView tvEmpty;

    private DrugResultAdapter adapter;
    private final List<DrugInfo> drugList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_search);

        etDrugName = findViewById(R.id.et_drug_name);
        btnSearch = findViewById(R.id.btn_search);
        rvDrugResults = findViewById(R.id.rv_drug_results);
        progressBar = findViewById(R.id.progress_bar);
        tvResultCount = findViewById(R.id.tv_result_count);
        tvEmpty = findViewById(R.id.tv_empty);

        rvDrugResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DrugResultAdapter(drugList, drug -> {
            Intent intent = new Intent(this, DrugDetailActivity.class);
            intent.putExtra("drug_name", drug.getName());
            intent.putExtra("drug_manufacturer", drug.getManufacturer());
            intent.putExtra("drug_ingredient", drug.getIngredient());
            intent.putExtra("drug_indication", drug.getIndication());
            intent.putExtra("drug_usage", drug.getUsage());
            intent.putExtra("drug_adverse", drug.getAdverse());
            intent.putExtra("drug_contraindication", drug.getContraindication());
            // 将完整说明书内容也传递过去，便于详情页展示
            intent.putExtra("drug_full_content", drug.getFullContent());
            startActivity(intent);
        });
        rvDrugResults.setAdapter(adapter);

        btnSearch.setOnClickListener(v -> performSearch());
        etDrugName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String keyword = etDrugName.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入药品名称", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);
        drugList.clear();
        adapter.notifyDataSetChanged();

        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String apiUrl = "https://apis.tianapi.com/yaopin/index?key=" + TIANAPI_KEY
                        + "&word=" + URLEncoder.encode(keyword, "UTF-8");
                connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.setRequestProperty("User-Agent", "MedicineReminder/1.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    parseTianApiResponse(response.toString());
                } else {
                    runOnUiThread(() -> {
                        showLoading(false);
                        Toast.makeText(this, "服务器错误: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "网络请求失败，请稍后重试", Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    private void parseTianApiResponse(String json) {
        Log.d("DrugSearch", "原始返回数据: " + json);
        try {
            JSONObject root = new JSONObject(json);
            int code = root.optInt("code", -1);
            if (code != 200) {
                String msg = root.optString("msg", "请求失败");
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(this, "请求失败: " + msg, Toast.LENGTH_SHORT).show();
                });
                return;
            }

            JSONObject result = root.optJSONObject("result");
            if (result == null) {
                runOnUiThread(() -> {
                    showLoading(false);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvResultCount.setVisibility(View.GONE);
                    rvDrugResults.setVisibility(View.GONE);
                });
                return;
            }

            // 根据实际返回结构：result.list 是一个数组
            JSONArray listArray = result.optJSONArray("list");
            List<DrugInfo> tempList = new ArrayList<>();

            if (listArray != null && listArray.length() > 0) {
                for (int i = 0; i < listArray.length(); i++) {
                    JSONObject item = listArray.getJSONObject(i);
                    DrugInfo drug = new DrugInfo();

                    // 关键字段：title 为药品名称，content 为说明书全文
                    String name = item.optString("title", "未知名称");
                    String content = item.optString("content", "暂无详细说明");

                    drug.setName(name);
                    drug.setFullContent(content);
                    // 由于该接口未提供结构化字段，将 content 作为适应症展示，其他留空
                    drug.setManufacturer("");
                    drug.setIngredient("");
                    drug.setIndication(content); // 将全文作为适应症显示
                    drug.setUsage("");
                    drug.setAdverse("");
                    drug.setContraindication("");

                    tempList.add(drug);
                }
            }

            runOnUiThread(() -> {
                drugList.clear();
                drugList.addAll(tempList);
                adapter.notifyDataSetChanged();

                if (drugList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvResultCount.setVisibility(View.GONE);
                    rvDrugResults.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    tvResultCount.setVisibility(View.VISIBLE);
                    tvResultCount.setText("共找到 " + drugList.size() + " 条结果");
                    rvDrugResults.setVisibility(View.VISIBLE);
                }
                showLoading(false);
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> {
                showLoading(false);
                Toast.makeText(this, "数据解析失败", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSearch.setEnabled(!show);
        etDrugName.setEnabled(!show);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    public static class DrugInfo {
        private String name;
        private String manufacturer;
        private String ingredient;
        private String indication;
        private String usage;
        private String adverse;
        private String contraindication;
        private String fullContent;  // 新增字段，保存完整说明书

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getManufacturer() { return manufacturer; }
        public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
        public String getIngredient() { return ingredient; }
        public void setIngredient(String ingredient) { this.ingredient = ingredient; }
        public String getIndication() { return indication; }
        public void setIndication(String indication) { this.indication = indication; }
        public String getUsage() { return usage; }
        public void setUsage(String usage) { this.usage = usage; }
        public String getAdverse() { return adverse; }
        public void setAdverse(String adverse) { this.adverse = adverse; }
        public String getContraindication() { return contraindication; }
        public void setContraindication(String contraindication) { this.contraindication = contraindication; }
        public String getFullContent() { return fullContent; }
        public void setFullContent(String fullContent) { this.fullContent = fullContent; }
    }

    private static class DrugResultAdapter extends RecyclerView.Adapter<DrugResultAdapter.VH> {
        private final List<DrugInfo> list;
        private final OnItemClickListener listener;

        interface OnItemClickListener { void onClick(DrugInfo drug); }

        DrugResultAdapter(List<DrugInfo> list, OnItemClickListener listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drug_result, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            DrugInfo drug = list.get(position);
            holder.tvName.setText(drug.getName());
            holder.tvManufacturer.setText(drug.getManufacturer());
            holder.itemView.setOnClickListener(v -> listener.onClick(drug));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvManufacturer;
            VH(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_drug_name);
                tvManufacturer = itemView.findViewById(R.id.tv_drug_manufacturer);
            }
        }
    }
}