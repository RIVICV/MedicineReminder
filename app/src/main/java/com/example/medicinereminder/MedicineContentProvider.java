package com.example.medicinereminder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.medicinereminder.provider";
    private static final String PATH_RECORDS = "records";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH_RECORDS);

    private static final int RECORDS = 1;
    private static final int RECORD_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, PATH_RECORDS, RECORDS);
        uriMatcher.addURI(AUTHORITY, PATH_RECORDS + "/#", RECORD_ID);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (getContext() == null) return null;

        int match = uriMatcher.match(uri);
        if (match == RECORDS || match == RECORD_ID) {
            // 获取今日所有记录（演示用，实际可扩展）
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            List<Record> records = AppDatabase.getInstance(getContext()).medicineDao().getRecordsByDateSync(today);

            MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "medicine_name", "scheduled_time", "status"});
            for (Record r : records) {
                cursor.addRow(new Object[]{r.getId(), r.getMedicineName(), r.getScheduledTime(), r.getStatus()});
            }
            return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        if (match == RECORDS) {
            return "vnd.android.cursor.dir/vnd.com.example.medicinereminder.record";
        } else if (match == RECORD_ID) {
            return "vnd.android.cursor.item/vnd.com.example.medicinereminder.record";
        }
        return null;
    }

    // 以下方法非必须，但必须实现（直接返回0或null）
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}