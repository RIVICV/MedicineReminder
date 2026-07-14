package com.example.medicinereminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class ReminderScheduler {

    private static final String TAG = "ReminderScheduler";

    public static void scheduleMedicine(Medicine medicine) {
        Context context = MyApplication.getContext();
        if (context == null) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String[] times = medicine.getRemindTimes().split(",");
        int repeatMode = medicine.getRepeatMode();
        String repeatDaysStr = medicine.getRepeatDays();

        for (String timeStr : times) {
            timeStr = timeStr.trim();
            String[] parts = timeStr.split(":");
            if (parts.length != 2) continue;
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            Calendar nextTime = calculateNextTriggerTime(hour, minute, repeatMode, repeatDaysStr);
            if (nextTime == null) continue;

            // 一次性闹钟过期处理
            if (medicine.getIsRepeating() == 0) {
                Calendar now = Calendar.getInstance();
                if (nextTime.before(now)) {
                    medicine.setIsActive(0);
                    AppDatabase.databaseWriteExecutor.execute(() -> {
                        AppDatabase.getInstance(context).medicineDao().update(medicine);
                    });
                    continue;
                }
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("medicine_id", medicine.getId());
            intent.putExtra("medicine_name", medicine.getName());
            intent.putExtra("dosage", medicine.getDosage());

            int requestCode = medicine.getId() * 100 + hour * 60 + minute;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            long triggerTime = nextTime.getTimeInMillis();
            try {
                // 仅使用系统闹钟 + 精确唤醒（双重保险，无备用）
                AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
                alarmManager.setAlarmClock(info, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }

                Log.d(TAG, "✅ 闹钟已设置: " + medicine.getName() + " 时间: " + timeStr +
                        " 触发: " + nextTime.getTime());
            } catch (Exception e) {
                Log.e(TAG, "设置闹钟失败", e);
            }
        }
    }

    private static Calendar calculateNextTriggerTime(int hour, int minute, int repeatMode, String repeatDaysStr) {
        Calendar now = Calendar.getInstance();
        Calendar target = (Calendar) now.clone();
        target.set(Calendar.HOUR_OF_DAY, hour);
        target.set(Calendar.MINUTE, minute);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);

        if (repeatMode == 0) {
            if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1);
            return target;
        } else {
            if (repeatDaysStr == null || repeatDaysStr.isEmpty()) return null;
            String[] days = repeatDaysStr.split(",");
            int[] weekdays = new int[days.length];
            for (int i = 0; i < days.length; i++) weekdays[i] = Integer.parseInt(days[i]);

            Calendar base = (Calendar) target.clone();
            if (base.before(now)) base.add(Calendar.DAY_OF_YEAR, 1);

            for (int i = 0; i < 7; i++) {
                int dayOfWeek = base.get(Calendar.DAY_OF_WEEK);
                int ourDay = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;
                for (int wd : weekdays) {
                    if (ourDay == wd) return base;
                }
                base.add(Calendar.DAY_OF_YEAR, 1);
            }
            return null;
        }
    }

    public static void cancelMedicine(Medicine medicine) {
        Context context = MyApplication.getContext();
        if (context == null) return;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        String[] times = medicine.getRemindTimes().split(",");
        for (String timeStr : times) {
            timeStr = timeStr.trim();
            String[] parts = timeStr.split(":");
            if (parts.length != 2) continue;
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            Intent intent = new Intent(context, AlarmReceiver.class);
            int requestCode = medicine.getId() * 100 + hour * 60 + minute;
            PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pi);
        }
    }
}