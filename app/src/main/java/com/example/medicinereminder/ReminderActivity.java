package com.example.medicinereminder;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReminderActivity extends AppCompatActivity {

    private ShakeDetector shakeDetector;
    private int medicineId;
    private String medicineName;
    private String dosage;
    private final AtomicBoolean isConfirming = new AtomicBoolean(false);
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // 设置窗口属性（锁屏唤醒）
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                android.view.WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // 获取数据
        handleIntent(getIntent());

        // 初始化UI
        Button btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(v -> confirmTaken());

        shakeDetector = new ShakeDetector(this, this::confirmTaken);
        acquireWakeLock();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 如果已经确认过，不再重新处理
        if (!isConfirming.get()) {
            setIntent(intent);
            handleIntent(intent);
        }
    }

    private void handleIntent(Intent intent) {
        medicineId = intent.getIntExtra("medicine_id", -1);
        medicineName = intent.getStringExtra("medicine_name");
        dosage = intent.getStringExtra("dosage");

        TextView tvName = findViewById(R.id.tv_medicine_name);
        TextView tvDosage = findViewById(R.id.tv_dosage);
        if (tvName != null) tvName.setText(medicineName);
        if (tvDosage != null) tvDosage.setText(dosage != null ? dosage : "请按时服药");
    }

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "MedicineReminder::ScreenWakeLock");
            wakeLock.acquire(2 * 60 * 1000L);
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private void confirmTaken() {
        if (!isConfirming.compareAndSet(false, true)) return;
        // 立即停止摇一摇
        if (shakeDetector != null) shakeDetector.stop();

        // 插入服药记录
        Record record = new Record(medicineId, medicineName, System.currentTimeMillis());
        record.setActualTime(System.currentTimeMillis());
        record.setStatus(1);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getInstance(this).medicineDao().insertRecord(record);
        });

        // 停止前台服务
        Intent serviceIntent = new Intent(this, ReminderForegroundService.class);
        stopService(serviceIntent);

        // 取消所有通知
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(medicineId);
            nm.cancel(2001); // 前台服务通知ID
        }

        releaseWakeLock();
        Toast.makeText(this, "✅ 已记录服药", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shakeDetector != null) shakeDetector.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shakeDetector != null) shakeDetector.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, ReminderForegroundService.class);
        stopService(serviceIntent);
        releaseWakeLock();
    }
}