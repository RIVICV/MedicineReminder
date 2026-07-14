package com.example.medicinereminder;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MedicineViewModel extends AndroidViewModel {

    private MedicineRepository repository;
    private LiveData<List<Medicine>> allMedicines;
    private LiveData<List<Record>> allRecords;

    public MedicineViewModel(Application application) {
        super(application);
    }

    public void setRepository(MedicineRepository repo) {
        this.repository = repo;
        this.allMedicines = repo.getAllMedicines();
        this.allRecords = repo.getAllRecords();
    }

    public LiveData<List<Medicine>> getAllMedicines() {
        return allMedicines;
    }

    public LiveData<List<Record>> getAllRecords() {
        return allRecords;
    }

    public LiveData<List<Record>> getRecordsByDate(String date) {
        return repository.getRecordsByDate(date);
    }

    public LiveData<Integer> getTodayTakenCount(String date) {
        return repository.getTodayTakenCount(date);
    }

    public void insert(Medicine medicine) {
        repository.insert(medicine);
    }

    public void update(Medicine medicine) {
        repository.update(medicine);
    }

    public void delete(Medicine medicine) {
        repository.delete(medicine);
    }

    public void insertRecord(Record record) {
        repository.insertRecord(record);
    }

    public void updateRecord(Record record) {
        repository.updateRecord(record);
    }

    public void deleteRecord(Record record) {
        repository.deleteRecord(record);
    }

    public String getTodayDate() {
        return repository.getTodayDate();
    }

    public int calculateTodayTotalReminders() {
        return repository.calculateTodayTotalReminders();
    }
}