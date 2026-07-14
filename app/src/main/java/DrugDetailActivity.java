package com.example.medicinereminder;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DrugDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_detail);

        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvContent = findViewById(R.id.tv_detail_content);

        String name = getIntent().getStringExtra("drug_name");
        String fullContent = getIntent().getStringExtra("drug_full_content");

        tvName.setText(name != null ? name : "未知药品");

        if (fullContent != null && !fullContent.isEmpty()) {
            // 支持HTML格式显示（如 <br/> 换行）
            tvContent.setText(Html.fromHtml(fullContent, Html.FROM_HTML_MODE_LEGACY));
            tvContent.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            tvContent.setText("暂无详细说明");
        }
    }
}