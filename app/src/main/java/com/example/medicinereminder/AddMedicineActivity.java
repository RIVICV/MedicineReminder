package com.example.medicinereminder;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddMedicineActivity extends AppCompatActivity {

    private EditText etName, etDosage, etInstruction;
    private ChipGroup chipGroupTimes;
    private Button btnAddTime, btnSave;
    private RadioGroup rgRepeatMode, rgRepeatType;
    private RadioButton rbDaily, rbWeekly, rbRepeating, rbOnce;
    private ChipGroup chipGroupWeekdays;

    private final List<String> selectedTimes = new ArrayList<>();
    private final List<Integer> selectedWeekdays = new ArrayList<>();
    private Medicine editingMedicine = null;

    private static final String[] WEEKDAYS = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        initViews();
        initWeekdayChips();

        int medicineId = getIntent().getIntExtra("medicine_id", -1);
        if (medicineId != -1) {
            loadMedicineForEdit(medicineId);
        }

        btnAddTime.setOnClickListener(v -> showTimePickerDialog());
        btnSave.setOnClickListener(v -> saveMedicine());

        rgRepeatMode.setOnCheckedChangeListener((group, checkedId) -> {
            chipGroupWeekdays.setVisibility(checkedId == R.id.rb_weekly ? View.VISIBLE : View.GONE);
        });
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etDosage = findViewById(R.id.et_dosage);
        etInstruction = findViewById(R.id.et_instruction);
        chipGroupTimes = findViewById(R.id.chip_group_times);
        btnAddTime = findViewById(R.id.btn_add_time);
        btnSave = findViewById(R.id.btn_save);
        rgRepeatMode = findViewById(R.id.rg_repeat_mode);
        rbDaily = findViewById(R.id.rb_daily);
        rbWeekly = findViewById(R.id.rb_weekly);
        rgRepeatType = findViewById(R.id.rg_repeat_type);
        rbRepeating = findViewById(R.id.rb_repeating);
        rbOnce = findViewById(R.id.rb_once);
        chipGroupWeekdays = findViewById(R.id.chip_group_weekdays);
    }

    private void initWeekdayChips() {
        for (int i = 0; i < WEEKDAYS.length; i++) {
            Chip chip = new Chip(this);
            chip.setText(WEEKDAYS[i]);
            chip.setCheckable(true);
            chip.setTag(i + 1);
            chipGroupWeekdays.addView(chip);
        }
    }

    private void loadMedicineForEdit(int id) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Medicine medicine = AppDatabase.getInstance(this).medicineDao().getMedicineById(id);
            runOnUiThread(() -> {
                if (medicine != null) {
                    editingMedicine = medicine;
                    etName.setText(medicine.getName());
                    etDosage.setText(medicine.getDosage());
                    etInstruction.setText(medicine.getInstruction());

                    String timesStr = medicine.getRemindTimes();
                    if (timesStr != null && !timesStr.isEmpty()) {
                        for (String time : timesStr.split(",")) {
                            addTimeChip(time.trim());
                        }
                    }

                    if (medicine.getRepeatMode() == 1) {
                        rbWeekly.setChecked(true);
                        chipGroupWeekdays.setVisibility(View.VISIBLE);
                        String daysStr = medicine.getRepeatDays();
                        if (daysStr != null && !daysStr.isEmpty()) {
                            for (String day : daysStr.split(",")) {
                                int weekday = Integer.parseInt(day);
                                selectedWeekdays.add(weekday);
                                for (int i = 0; i < chipGroupWeekdays.getChildCount(); i++) {
                                    Chip chip = (Chip) chipGroupWeekdays.getChildAt(i);
                                    if ((int) chip.getTag() == weekday) {
                                        chip.setChecked(true);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        rbDaily.setChecked(true);
                    }

                    if (medicine.getIsRepeating() == 0) {
                        rbOnce.setChecked(true);
                    } else {
                        rbRepeating.setChecked(true);
                    }

                    btnSave.setText("更新药品");
                }
            });
        });
    }

    private void showTimePickerDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this,
                (view, hour, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                    addTimeChip(time);
                }, 8, 0, true);
        dialog.show();
    }

    private void addTimeChip(String time) {
        for (int i = 0; i < chipGroupTimes.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupTimes.getChildAt(i);
            if (chip.getText().toString().equals(time)) {
                Toast.makeText(this, "该时间已添加", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Chip chip = new Chip(this);
        chip.setText(time);
        chip.setCloseIconVisible(true);
        chip.setCloseIconResource(android.R.drawable.ic_menu_close_clear_cancel);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupTimes.removeView(chip);
            selectedTimes.remove(time);
        });
        chipGroupTimes.addView(chip);
        selectedTimes.add(time);
    }

    private void saveMedicine() {
        String name = etName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();
        String instruction = etInstruction.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "请输入药品名称", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTimes.isEmpty()) {
            Toast.makeText(this, "请至少添加一个提醒时间", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder timesBuilder = new StringBuilder();
        for (int i = 0; i < selectedTimes.size(); i++) {
            timesBuilder.append(selectedTimes.get(i));
            if (i < selectedTimes.size() - 1) timesBuilder.append(",");
        }
        String remindTimes = timesBuilder.toString();

        int repeatMode = rbDaily.isChecked() ? 0 : 1;
        StringBuilder daysBuilder = new StringBuilder();
        if (repeatMode == 1) {
            selectedWeekdays.clear();
            for (int i = 0; i < chipGroupWeekdays.getChildCount(); i++) {
                Chip chip = (Chip) chipGroupWeekdays.getChildAt(i);
                if (chip.isChecked()) {
                    int day = (int) chip.getTag();
                    selectedWeekdays.add(day);
                    daysBuilder.append(day).append(",");
                }
            }
            if (selectedWeekdays.isEmpty()) {
                Toast.makeText(this, "请至少选择一个星期", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        String repeatDays = daysBuilder.length() > 0 ? daysBuilder.substring(0, daysBuilder.length() - 1) : "";

        int isRepeating = rbRepeating.isChecked() ? 1 : 0;

        if (editingMedicine == null) {
            Medicine medicine = new Medicine(name, dosage, instruction, remindTimes);
            medicine.setRepeatMode(repeatMode);
            medicine.setRepeatDays(repeatDays);
            medicine.setIsRepeating(isRepeating);
            AppDatabase.databaseWriteExecutor.execute(() -> {
                long id = AppDatabase.getInstance(this).medicineDao().insert(medicine);
                medicine.setId((int) id);
                ReminderScheduler.scheduleMedicine(medicine);
                runOnUiThread(() -> {
                    Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        } else {
            editingMedicine.setName(name);
            editingMedicine.setDosage(dosage);
            editingMedicine.setInstruction(instruction);
            editingMedicine.setRemindTimes(remindTimes);
            editingMedicine.setRepeatMode(repeatMode);
            editingMedicine.setRepeatDays(repeatDays);
            editingMedicine.setIsRepeating(isRepeating);

            AppDatabase.databaseWriteExecutor.execute(() -> {
                AppDatabase.getInstance(this).medicineDao().update(editingMedicine);
                ReminderScheduler.cancelMedicine(editingMedicine);
                ReminderScheduler.scheduleMedicine(editingMedicine);
                runOnUiThread(() -> {
                    Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }
}