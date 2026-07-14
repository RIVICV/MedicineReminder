package com.example.medicinereminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<Medicine> medicines = AppDatabase.getInstance(context).medicineDao().getAllMedicinesSync();
                for (Medicine medicine : medicines) {
                    if (medicine.getIsActive() == 1) {
                        ReminderScheduler.scheduleMedicine(medicine);
                    }
                }
            });
        }
    }
}