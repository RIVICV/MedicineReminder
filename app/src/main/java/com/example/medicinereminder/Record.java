package com.example.medicinereminder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_table")
public class Record {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "medicine_id")
    private int medicineId;

    @ColumnInfo(name = "medicine_name")
    private String medicineName;

    @ColumnInfo(name = "scheduled_time")
    private long scheduledTime;

    @ColumnInfo(name = "actual_time")
    private long actualTime;

    @ColumnInfo(name = "status", defaultValue = "0")
    private int status;  // 0=未服，1=已服，2=跳过

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public Record(int medicineId, String medicineName, long scheduledTime) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.scheduledTime = scheduledTime;
        this.status = 0;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMedicineId() { return medicineId; }
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public long getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(long scheduledTime) { this.scheduledTime = scheduledTime; }

    public long getActualTime() { return actualTime; }
    public void setActualTime(long actualTime) { this.actualTime = actualTime; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}