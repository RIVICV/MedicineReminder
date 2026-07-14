package com.example.medicinereminder;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MedicineDao {

    // ========== 药品操作 ==========
    @Insert
    long insert(Medicine medicine);

    @Update
    void update(Medicine medicine);

    @Delete
    void delete(Medicine medicine);

    @Query("SELECT * FROM medicine_table WHERE is_active = 1 ORDER BY created_at DESC")
    LiveData<List<Medicine>> getAllActiveMedicines();

    @Query("SELECT * FROM medicine_table")
    List<Medicine> getAllMedicinesSync();

    @Query("SELECT * FROM medicine_table WHERE id = :id")
    Medicine getMedicineById(int id);

    // ========== 服药记录操作 ==========
    @Insert
    void insertRecord(Record record);

    @Update
    void updateRecord(Record record);

    @Delete
    void deleteRecord(Record record);

    @Query("SELECT * FROM record_table ORDER BY created_at DESC")
    LiveData<List<Record>> getAllRecords();

    @Query("SELECT * FROM record_table WHERE medicine_id = :medicineId ORDER BY created_at DESC")
    LiveData<List<Record>> getRecordsByMedicine(int medicineId);

    @Query("SELECT * FROM record_table WHERE date(scheduled_time/1000, 'unixepoch') = date(:date) ORDER BY created_at DESC")
    LiveData<List<Record>> getRecordsByDate(String date);

    @Query("SELECT * FROM record_table WHERE date(scheduled_time/1000, 'unixepoch') = date(:date)")
    List<Record> getRecordsByDateSync(String date);

    // 今日已服次数
    @Query("SELECT COUNT(*) FROM record_table WHERE date(scheduled_time/1000, 'unixepoch') = date(:date) AND status = 1")
    LiveData<Integer> getTodayTakenCount(String date);

    @Query("SELECT COUNT(*) FROM record_table WHERE date(scheduled_time/1000, 'unixepoch') = date(:date) AND status = 1")
    int getTodayTakenCountSync(String date);
}