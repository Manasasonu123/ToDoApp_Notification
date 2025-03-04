package com.example.todo_app;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";

    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;
    private ChipGroup chipGroupPriority;
    private EditText setdatetime;
    private String selectedPriority = "Low";
    private Calendar selectedCalendar;
    private int taskId=-1;
    //private Button setalarm;


    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newTaskText = view.findViewById(R.id.newTaskText);
        newTaskSaveButton = view.findViewById(R.id.newtaskbtn);
        chipGroupPriority = view.findViewById(R.id.chip_group_priority);
        setdatetime=view.findViewById(R.id.settime);
        selectedCalendar = Calendar.getInstance();
        //setalarm=view.findViewById(R.id.setalarm);

        // Make EditText act like a button (disable keyboard input in XML)
        setdatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        db = new DatabaseHandler(requireActivity());
        db.openDatabase();

        Bundle bundle = getArguments();
        boolean isUpdate = bundle != null;

        if (isUpdate) {
            taskId = bundle.getInt("taskId", -1);
            String task = bundle.getString("task");
            String datetime=bundle.getString("datetime");
            int priority = bundle.getInt("priority", 0);
            newTaskText.setText(task);
            setdatetime.setText(datetime);
            int chipId = (priority == 2) ? R.id.chip_high : (priority == 1) ? R.id.chip_medium : R.id.chip_low;
            chipGroupPriority.check(chipId);
            selectedPriority = getPriorityLabel(priority);
        }

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupPriority.setOnCheckedStateChangeListener((group, checkedId) -> {
            if (!checkedId.isEmpty()) {
                Chip selectedChip = view.findViewById(checkedId.get(0));
                if (selectedChip != null) {
                    selectedPriority = selectedChip.getText().toString();
                    Log.d("PrioritySelection", "Selected Priority: " + selectedPriority);
                }
            }
        });

        newTaskSaveButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
            }

            long alarmtime=selectedCalendar.getTimeInMillis();
            String text = newTaskText.getText().toString();
            String datetimetxt = setdatetime.getText().toString();
            if (!text.isEmpty()) {
                int priorityValue = getPriorityValue(selectedPriority);
                if (isUpdate && bundle != null && taskId!=-1) {
//                    int taskId = bundle.getInt("id", -1);
                    db.cancelNotification(taskId,requireContext());
                    db.updateTask(taskId, text);
                    db.updatePriority(taskId, priorityValue);
                    db.updateDateTime(taskId, datetimetxt);
                    scheduleNotification(taskId, text, alarmtime);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    task.setPriority(priorityValue);
                    task.setDatetime(datetimetxt);
                    taskId= Math.toIntExact(db.insertTask(task));
                    scheduleNotification(taskId, text, alarmtime);
                }
            }
            dismiss();
        });
    }
    private void showDateTimePicker() {
        final Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        selectedCalendar.set(Calendar.MINUTE, minute);
                                        selectedCalendar.set(Calendar.SECOND, 0);

                                        // Format the selected date/time for display
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                        setdatetime.setText(sdf.format(selectedCalendar.getTime()));
                                    }
                                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false);
                        timePickerDialog.show();
                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void scheduleNotification(int taskId, String taskText, long time) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("taskText", taskText);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        Log.d("AlarmSet", "Setting alarm with requestCode: " + taskId);
        Log.d("AlarmSet", "Alarm scheduled for: " + new Date(time).toString());
        Toast.makeText(requireContext(),"AlarmSet for"+time,Toast.LENGTH_SHORT).show();
    }

//    // Cancel existing notification before updating
//    void cancelNotification(int taskId) {
//        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                requireContext(), taskId, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
//        );
//        if (pendingIntent != null) {
//            alarmManager.cancel(pendingIntent);
//            pendingIntent.cancel();
//            Log.d("NotificationCancel", "Notification canceled for taskId: " + taskId);
//        } else {
//            Log.d("NotificationCancel", "No active notification found for taskId: " + taskId);
//        }
//    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener) {
            ((DialogCloseListener) activity).handleDialogClose(dialog);
        }
    }

    private int getPriorityValue(String priority) {
        return priority.equalsIgnoreCase("High") ? 2 : priority.equalsIgnoreCase("Medium") ? 1 : 0;
    }

    private String getPriorityLabel(int priority) {
        return priority == 2 ? "High" : priority == 1 ? "Medium" : "Low";
    }
}
