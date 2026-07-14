package com.example.medicinereminder;

import android.Manifest;
import android.app.AlarmManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMedicines;
    private FloatingActionButton fabAdd;
    private MedicineAdapter adapter;
    private MedicineViewModel viewModel;
    private TextView tvGreeting;
    private TextView tvDate;
    private TextView tvTakenCount;
    private TextView tvTotalCount;

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMedicines = findViewById(R.id.rv_medicines);
        fabAdd = findViewById(R.id.fab_add);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvDate = findViewById(R.id.tv_date);
        tvTakenCount = findViewById(R.id.tv_taken_count);
        tvTotalCount = findViewById(R.id.tv_total_count);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        setupHeader();

        rvMedicines.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicineAdapter(
                medicine -> {
                    Intent intent = new Intent(this, AddMedicineActivity.class);
                    intent.putExtra("medicine_id", medicine.getId());
                    startActivity(intent);
                },
                medicine -> {
                    new AlertDialog.Builder(this)
                            .setTitle("删除药品")
                            .setMessage("确定删除 " + medicine.getName() + " 吗？")
                            .setPositiveButton("删除", (dialog, which) -> viewModel.delete(medicine))
                            .setNegativeButton("取消", null)
                            .show();
                }
        );
        rvMedicines.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(MedicineViewModel.class);
        MedicineRepository repository = new MedicineRepository(getApplication());
        viewModel.setRepository(repository);

        viewModel.getAllMedicines().observe(this, medicines -> {
            adapter.submitList(medicines);
            updateTodayStats();
        });

        String today = viewModel.getTodayDate();
        viewModel.getRecordsByDate(today).observe(this, records -> updateTodayStats());

        fabAdd.setOnClickListener(v -> startActivity(new Intent(this, AddMedicineActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            } else if (id == R.id.nav_search) {
                startActivity(new Intent(this, DrugSearchActivity.class));
                return true;
            }
            return false;
        });

        checkAndRequestPermissions();
    }

    private void setupHeader() {
        int hour = new Date().getHours();
        String greeting;
        if (hour < 12) greeting = "早上好，";
        else if (hour < 18) greeting = "下午好，";
        else greeting = "晚上好，";
        tvGreeting.setText(greeting);

        String date = new SimpleDateFormat("yyyy年MM月dd日 EEEE", Locale.CHINESE).format(new Date());
        tvDate.setText("✨ " + date);
    }

    private void updateTodayStats() {
        String today = viewModel.getTodayDate();

        AppDatabase.databaseWriteExecutor.execute(() -> {
            int totalReminders = viewModel.calculateTodayTotalReminders();
            int takenCount = AppDatabase.getInstance(this).medicineDao().getTodayTakenCountSync(today);

            final int finalTaken = takenCount;
            final int finalPending = totalReminders - takenCount;

            runOnUiThread(() -> {
                tvTotalCount.setText(String.valueOf(Math.max(finalPending, 0)));
                tvTakenCount.setText(String.valueOf(finalTaken));
            });
        });
    }

    private void checkAndRequestPermissions() {
        // 1. 精确闹钟
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                new AlertDialog.Builder(this)
                        .setTitle("需要精确闹钟权限")
                        .setMessage("为了准时提醒您服药，请开启精确闹钟权限")
                        .setPositiveButton("去设置", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        })
                        .setNegativeButton("稍后", null)
                        .show();
            }
        }

        // 2. 通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }

        // 3. 电池优化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                new AlertDialog.Builder(this)
                        .setTitle("关闭电池优化")
                        .setMessage("为保证后台准时提醒，请关闭电池优化")
                        .setPositiveButton("去设置", (d, w) -> {
                            Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            i.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(i);
                        })
                        .setNegativeButton("稍后", null)
                        .show();
            }
        }

        // 4. 自启动引导（仅展示一次或按需）
        showAutoStartGuideIfNeeded();
    }

    private void showManualGuideIfNeeded() {
        new AlertDialog.Builder(this)
                .setTitle("🔋 保证准时提醒最后一步")
                .setMessage("为避免系统延迟提醒，请务必在「设置-应用-药不能停-电池」中关闭省电策略或设为「无限制」。")
                .setPositiveButton("去设置", (d, w) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                })
                .setNegativeButton("稍后", null)
                .show();
    }
    private void showAutoStartGuideIfNeeded() {
        // 可增加 SharedPreferences 记录是否已展示过，避免每次启动都弹
        new AlertDialog.Builder(this)
                .setTitle("重要：确保后台提醒")
                .setMessage("为了锁屏和后台时能准时提醒，请前往系统设置开启「自启动」权限。\n(通常在「应用管理」->「应用启动管理」中)")
                .setPositiveButton("去设置", (d, w) -> openAutoStartSetting())
                .setNegativeButton("稍后", null)
                .show();
    }

    private void openAutoStartSetting() {
        try {
            Intent intent = new Intent();
            String mf = Build.MANUFACTURER.toLowerCase();
            if (mf.contains("xiaomi") || mf.contains("redmi")) {
                intent.setComponent(new ComponentName("com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if (mf.contains("huawei") || mf.contains("honor")) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager",
                        "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"));
            } else if (mf.contains("oppo")) {
                intent.setComponent(new ComponentName("com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if (mf.contains("vivo")) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
            }
            startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private void showAutoStartGuide() {
        new AlertDialog.Builder(this)
                .setTitle("确保闹钟正常工作")
                .setMessage("如果您使用华为/小米/OPPO/vivo等手机，请在系统设置中开启「自启动」权限。")
                .setPositiveButton("去设置", (dialog, which) -> openAutoStartSetting())
                .setNegativeButton("知道了", null)
                .show();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "通知权限已开启", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTodayStats();
    }
}