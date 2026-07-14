package com.example.medicinereminder;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicine_table")
public class Medicine {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "dosage")
    private String dosage;

    @ColumnInfo(name = "instruction")
    private String instruction;

    @NonNull
    @ColumnInfo(name = "remind_times")
    private String remindTimes;

    @ColumnInfo(name = "repeat_mode", defaultValue = "0")
    private int repeatMode;

    @ColumnInfo(name = "repeat_days", defaultValue = "")
    private String repeatDays;

    @ColumnInfo(name = "is_repeating", defaultValue = "1")
    private int isRepeating;     // 1=重复提醒，0=仅一次

    @ColumnInfo(name = "is_active", defaultValue = "1")
    private int isActive;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public Medicine(@NonNull String name, String dosage, String instruction,
                    @NonNull String remindTimes) {
        this.name = name;
        this.dosage = dosage;
        this.instruction = instruction;
        this.remindTimes = remindTimes;
        this.repeatMode = 0;
        this.repeatDays = "";
        this.isRepeating = 1;
        this.isActive = 1;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }

    @NonNull
    public String getRemindTimes() { return remindTimes; }
    public void setRemindTimes(@NonNull String remindTimes) { this.remindTimes = remindTimes; }

    public int getRepeatMode() { return repeatMode; }
    public void setRepeatMode(int repeatMode) { this.repeatMode = repeatMode; }

    public String getRepeatDays() { return repeatDays; }
    public void setRepeatDays(String repeatDays) { this.repeatDays = repeatDays; }

    public int getIsRepeating() { return isRepeating; }
    public void setIsRepeating(int isRepeating) { this.isRepeating = isRepeating; }

    public int getIsActive() { return isActive; }
    public void setIsActive(int isActive) { this.isActive = isActive; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}