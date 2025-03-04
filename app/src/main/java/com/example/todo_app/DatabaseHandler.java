package com.example.todo_app;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "toDoListDatabase";
    private static final String TABLE_TODO = "todo";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TASK = "task";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_PRIORITY = "priority";
    private static final String COLUMN_DATETIME = "datetime";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TASK + " TEXT, " +
            COLUMN_STATUS + " INTEGER, " +
            COLUMN_PRIORITY + " INTEGER DEFAULT 1," +
            COLUMN_DATETIME + " TEXT)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COLUMN_PRIORITY + " INTEGER DEFAULT 0");
        }
        if (oldVersion < 3) { // Add datetime column if upgrading from version < 3
            db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COLUMN_DATETIME + " TEXT");
        }
    }

    public void openDatabase() throws SQLException {
        db = this.getWritableDatabase();
    }

    public long insertTask(ToDoModel task) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TASK, task.getTask());
        cv.put(COLUMN_STATUS, task.getStatus());
        cv.put(COLUMN_PRIORITY, task.getPriority());
        cv.put(COLUMN_DATETIME,task.getDateTime());
        long taskId=db.insert(TABLE_TODO, null, cv);
        return taskId;
    }

    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks() {
    List<ToDoModel> taskList = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = null;

    try {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_TODO, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int taskIndex = cursor.getColumnIndex(COLUMN_TASK);
            int statusIndex = cursor.getColumnIndex(COLUMN_STATUS);
            int priorityIndex = cursor.getColumnIndex(COLUMN_PRIORITY);
            int datetimeIndex = cursor.getColumnIndex(COLUMN_DATETIME);

            if (idIndex != -1 && taskIndex != -1 && statusIndex != -1 && priorityIndex != -1 && datetimeIndex !=-1) {

                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cursor.getInt(idIndex));
                    task.setTask(cursor.getString(taskIndex));
                    task.setStatus(cursor.getInt(statusIndex));
                    task.setPriority(cursor.getInt(priorityIndex));
                    task.setDatetime(cursor.getString(datetimeIndex));
                    taskList.add(task);
                } while (cursor.moveToNext());
            } else {
                Log.e("DatabaseHandler", "One or more columns not found in the cursor.");
            }
        }
    } catch (Exception e) {
        Log.e("DatabaseHandler", "Error getting tasks: " + e.getMessage());
    } finally {
        if (cursor != null) {
            cursor.close();
        }
//        db.close();
    }
    return taskList;
    }


    public boolean getTaskExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT 1 FROM " + TABLE_TODO + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            if (cursor != null && cursor.getCount() > 0) {
                return true; // Task exists
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
//            db.close();
        }
        return false; // Task does not exist
    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);
        db.update(TABLE_TODO, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TASK, task);
        db.update(TABLE_TODO, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updatePriority(int id, int priority) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PRIORITY, priority);
        db.update(TABLE_TODO, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
    public void updateDateTime(int id, String datetime) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DATETIME, datetime);
        db.update(TABLE_TODO, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db.delete(TABLE_TODO, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }
    // Cancel existing notification before updating
    public void cancelNotification(int taskId,Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, taskId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d("NotificationCancel", "Notification canceled for taskId: " + taskId);
        } else {
            Log.d("NotificationCancel", "No active notification found for taskId: " + taskId);
        }
    }
}
