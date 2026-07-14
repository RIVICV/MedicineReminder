package com.example.medicinereminder;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineRepository {

    private final MedicineDao medicineDao;
    private final LiveData<List<Medicine>> allMedicines;
    private final LiveData<List<Record>> allRecords;

    public MedicineRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        medicineDao = db.medicineDao();
        allMedicines = medicineDao.getAllActiveMedicines();
        allRecords = medicineDao.getAllRecords();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }

    public LiveData<List<Record>> getAllRecords() {
        return allRecords;
    }

    public LiveData<List<Record>> getRecordsByDate(String date) {
        return medicineDao.getRecordsByDate(date);
    }

    public List<Record> getRecordsByDateSync(String date) {
        return medicineDao.getRecordsByDateSync(date);
    }

    public int getTodayTakenCountSync(String date) {
        return medicineDao.getTodayTakenCountSync(date);
    }

    public LiveData<Integer> getTodayTakenCount(String date) {
        return medicineDao.getTodayTakenCount(date);
    }

    public void insert(Medicine medicine) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long id = medicineDao.insert(medicine);
            medicine.setId((int) id);
            ReminderScheduler.scheduleMedicine(medicine);
        });
    }

    public void update(Medicine medicine) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            medicineDao.update(medicine);
            ReminderScheduler.cancelMedicine(medicine);
            ReminderScheduler.scheduleMedicine(medicine);
        });
    }

    public void delete(Medicine medicine) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            medicineDao.delete(medicine);
            ReminderScheduler.cancelMedicine(medicine);
        });
    }

    public void insertRecord(Record record) {
        AppDatabase.databaseWriteExecutor.execute(() -> medicineDao.insertRecord(record));
    }

    public void updateRecord(Record record) {
        AppDatabase.databaseWriteExecutor.execute(() -> medicineDao.updateRecord(record));
    }

    public void deleteRecord(Record record) {
        AppDatabase.databaseWriteExecutor.execute(() -> medicineDao.deleteRecord(record));
    }

    public String getTodayDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    // 计算今日应提醒总次数（在后台线程调用）
    public int calculateTodayTotalReminders() {
        List<Medicine> medicines = medicineDao.getAllMedicinesSync();
        Calendar today = Calendar.getInstance();
        int total = 0;

        for (Medicine med : medicines) {
            if (med.getIsActive() == 0) continue;

            if (med.getIsRepeating() == 0) {
                // 仅一次：只要还在活跃状态，就计入
                total += med.getRemindTimes().split(",").length;
                continue;
            }

            // 重复提醒
            if (med.getRepeatMode() == 0) {
                // 每天
                total += med.getRemindTimes().split(",").length;
            } else {
                // 指定星期
                int dayOfWeek = today.get(Calendar.DAY_OF_WEEK);
                int ourDay = (dayOfWeek == Calendar.SUNDAY) ? 7 : dayOfWeek - 1;
                String repeatDays = med.getRepeatDays();
                if (repeatDays != null && !repeatDays.isEmpty()) {
                    String[] days = repeatDays.split(",");
                    for (String d : days) {
                        if (Integer.parseInt(d) == ourDay) {
                            total += med.getRemindTimes().split(",").length;
                            break;
                        }
                    }
                }
            }
        }
        return total;
    }
}