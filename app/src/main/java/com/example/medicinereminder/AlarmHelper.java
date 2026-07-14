package com.example.medicinereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class AlarmHelper {

    public static void scheduleReminder(Context context, Medicine medicine) {
        String remindTimes = medicine.getRemindTimes();
        if (remindTimes == null || remindTimes.isEmpty()) return;

        String[] times = remindTimes.split(",");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (String timeStr : times) {
            timeStr = timeStr.trim();
            String[] parts = timeStr.split(":");
            if (parts.length != 2) continue;

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // 如果时间已过，设为明天
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("medicine_id", medicine.getId());
            intent.putExtra("medicine_name", medicine.getName());
            intent.putExtra("dosage", medicine.getDosage());

            int requestCode = generateRequestCode(medicine.getId(), hour, minute);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // 设置精确闹钟
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    // Android 12+ 需要检查权限
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        // 降级为非精确闹钟
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    public static void cancelReminder(Context context, Medicine medicine) {
        String remindTimes = medicine.getRemindTimes();
        if (remindTimes == null || remindTimes.isEmpty()) return;

        String[] times = remindTimes.split(",");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (String timeStr : times) {
            timeStr = timeStr.trim();
            String[] parts = timeStr.split(":");
            if (parts.length != 2) continue;

            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Intent intent = new Intent(context, AlarmReceiver.class);
            int requestCode = generateRequestCode(medicine.getId(), hour, minute);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    private static int generateRequestCode(int medicineId, int hour, int minute) {
        return medicineId * 10000 + hour * 100 + minute;
    }
}