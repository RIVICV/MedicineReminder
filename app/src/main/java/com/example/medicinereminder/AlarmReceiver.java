package com.example.medicinereminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "medicine_reminder";
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int medicineId = intent.getIntExtra("medicine_id", -1);
        String medicineName = intent.getStringExtra("medicine_name");
        String dosage = intent.getStringExtra("dosage");

        Log.d(TAG, "闹钟触发: " + medicineName + " ID: " + medicineId);
        if (medicineId == -1) return;

        // 启动前台服务（播放铃声）
        Intent serviceIntent = new Intent(context, ReminderForegroundService.class);
        serviceIntent.putExtra("medicine_id", medicineId);
        serviceIntent.putExtra("medicine_name", medicineName);
        serviceIntent.putExtra("dosage", dosage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }

        // 发送一条简洁通知（可选）
        showMinimalNotification(context, medicineId, medicineName, dosage);

        // 启动全屏提醒Activity（singleTop，不会重复创建）
        Intent reminderIntent = new Intent(context, ReminderActivity.class);
        reminderIntent.putExtra("medicine_id", medicineId);
        reminderIntent.putExtra("medicine_name", medicineName);
        reminderIntent.putExtra("dosage", dosage);
        reminderIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(reminderIntent);

        // 重新调度下一次闹钟
        rescheduleNextAlarm(context, medicineId);
    }

    private void rescheduleNextAlarm(Context context, int medicineId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Medicine medicine = AppDatabase.getInstance(context).medicineDao().getMedicineById(medicineId);
            if (medicine != null && medicine.getIsActive() == 1) {
                if (medicine.getIsRepeating() == 1) {
                    ReminderScheduler.scheduleMedicine(medicine);
                } else {
                    medicine.setIsActive(0);
                    AppDatabase.getInstance(context).medicineDao().update(medicine);
                }
            }
        });
    }

    private void showMinimalNotification(Context context, int id, String name, String dosage) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "服药提醒", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM), null);
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, ReminderActivity.class);
        intent.putExtra("medicine_id", id);
        intent.putExtra("medicine_name", name);
        intent.putExtra("dosage", dosage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("⏰ 该吃药了")
                .setContentText(name + (dosage != null ? " " + dosage : ""))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setContentIntent(pi)
                .setFullScreenIntent(pi, true)
                .setAutoCancel(true);
        manager.notify(id, builder.build());
    }
}