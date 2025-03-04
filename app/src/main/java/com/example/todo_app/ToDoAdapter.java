package com.example.todo_app;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private DatabaseHandler db;
    private MainActivity activity;
    private List<ToDoModel> todoList;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
        this.todoList = new ArrayList<>();
        db.openDatabase(); // Opening the database once
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));

        holder.task.setOnCheckedChangeListener((buttonView, isChecked) -> db.updateStatus(item.getId(), isChecked ? 1 : 0));

        holder.deleteButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Delete Tasks");
            builder.setMessage("Are you sure you want to delete this Task?");
            builder.setPositiveButton("Confirm", (dialog, which) -> deleteItem(position));
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });

        holder.editButton.setOnClickListener(view -> editItem(position));

        // Display priority as text
        holder.priorityChip.setText(getPriorityText(item.getPriority()));
        int priorityColor = getPriorityColor(item.getPriority());
        holder.priorityChip.setChipBackgroundColor(ContextCompat.getColorStateList(activity, priorityColor));

        // Retrieve and format date
        String formattedDate = formatDateTime(item.getDateTime());
        holder.datetime.setText(formattedDate);
    }

    private static String formatDateTime(String inputDateTime) {
        if (inputDateTime == null || inputDateTime.isEmpty()) {
            return "No Date"; // Handle empty date
        }
        try {
            // Define input format as "dd/MM/yyyy HH:mm"
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
            Date date = inputFormat.parse(inputDateTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String dayWithSuffix = getDayWithSuffix(day);

            // Define output format as "28th February 2025, 01:46 PM"
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy, hh:mm a", Locale.ENGLISH);
            return dayWithSuffix + " " + outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date"; // Handle parsing errors
        }
    }


    private static String getDayWithSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1: return day + "st";
            case 2: return day + "nd";
            case 3: return day + "rd";
            default: return day + "th";
        }
    }


    private int getPriorityColor(int priority) {
        switch (priority) {
            case 2:
                return R.color.colorHigh; // High priority
            case 1:
                return R.color.colorMedium; // Medium priority
            default:
                return R.color.colorLow; // Low priority
        }
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = new ArrayList<>(todoList);
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.cancelNotification(item.getId(),activity);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("taskId", item.getId()); // Use "taskId" instead of "id"
        bundle.putString("task", item.getTask());
        bundle.putInt("priority", item.getPriority());
        bundle.putString("datetime", item.getDateTime());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    private String getPriorityText(int priority) {
        switch (priority) {
            case 2:
                return "High";
            case 1:
                return "Medium";
            default:
                return "Low";
        }
    }
    public Context getContext() {
        return activity;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        ImageButton deleteButton;
        ImageButton editButton;
        Chip priorityChip;
        TextView datetime;

        public ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.tocheckbox);
            deleteButton = view.findViewById(R.id.btndelete);
            editButton = view.findViewById(R.id.btnedit);
            priorityChip = view.findViewById(R.id.priority_chip);
            datetime = view.findViewById(R.id.datetxt);
        }
    }
}
