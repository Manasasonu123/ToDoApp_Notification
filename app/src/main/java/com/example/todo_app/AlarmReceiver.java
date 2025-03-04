//package com.example.todo_app;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//
//public class AlarmReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        int requestCode = intent.getIntExtra("requestCode", 0);
//        Log.d("AlarmReceiver", "Received alarm with requestCode: " + requestCode);
//        //Toast.makeText(context, "Event is triggered", Toast.LENGTH_SHORT).show();
//        if (requestCode == 0 || requestCode == 1) {
//            showNotification(context, requestCode);
//        } else {
//            Log.e("AlarmReceiver", "Invalid requestCode received: " + requestCode);
//        }
//    }
//
//    private void showNotification(Context context, int requestCode) {
//        String channelId = "alarm_channel";
//        String channelName = "Alarm Notifications";
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Toast.makeText(context, "show notification entered", Toast.LENGTH_SHORT).show();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Alarm Channel for Notifications");
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        Intent mainIntent = new Intent(context, MainActivity.class);
//        // Use FLAG_IMMUTABLE for security as we don't plan to modify this PendingIntent
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        String message = (requestCode == 1) ? "5 min prior alert!" : "It's time!";
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                .setSmallIcon(R.drawable.baseline_access_alarm_24)
//                .setContentTitle("Reminder")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//
//        if (notificationManager != null) {
//            notificationManager.notify(requestCode, builder.build());
//        }
//        Log.d("AlarmReceiver", "Notification displayed for requestCode: " + requestCode);
//    }
//}

//package com.example.todo_app;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//
//public class AlarmReceiver extends BroadcastReceiver {
//
//@Override
//public void onReceive(Context context, Intent intent) {
//    int requestCode = intent.getIntExtra("requestCode", 0);
//    Log.d("AlarmReceiver", "Received alarm with requestCode: " + requestCode);
//
//    // Extract taskId from requestCode
//    int taskId = (requestCode >= 2000) ? requestCode - 2000 : (requestCode >= 1000) ? requestCode - 1000 : -1;
//
//    if (taskId >= 0) {
//        DatabaseHandler db = new DatabaseHandler(context);
//        db.openDatabase();
//        if (db.getTaskExists(taskId)) {
//            showNotification(context, requestCode);
//        } else {
//            Log.d("AlarmReceiver", "Task deleted. Notification not shown.");
//        }
//        db.close();
//    } else {
//        Log.e("AlarmReceiver", "Invalid requestCode received: " + requestCode);
//    }
//}
//
//    private void showNotification(Context context, int requestCode) {
//        String channelId = "alarm_channel";
//        String channelName = "Alarm Notifications";
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Toast.makeText(context, "show notification entered", Toast.LENGTH_SHORT).show();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Alarm Channel for Notifications");
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        Intent mainIntent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        String message = (requestCode == 1) ? "5 min prior alert!" : "It's time!"; // Use requestCode here
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                .setSmallIcon(R.drawable.baseline_access_alarm_24)
//                .setContentTitle("Reminder")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent);
//
//        if (notificationManager != null) {
//            notificationManager.notify(requestCode, builder.build()); // Use requestCode as notification ID
//        }
//        Log.d("AlarmReceiver", "Notification displayed for requestCode: " + requestCode);
//    }
//}
//package com.example.todo_app;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//
//public class AlarmReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        int requestCode = intent.getIntExtra("requestCode", -1); // Default to -1 if not found
//        Log.d("AlarmReceiver", "Received alarm with requestCode: " + requestCode);
//
//        if (requestCode == 0 || requestCode == 1) {
//            showNotification(context, requestCode);
//        } else {
//            Log.e("AlarmReceiver", "Invalid requestCode received: " + requestCode);
//        }
//    }
//
//    private void showNotification(Context context, int requestCode) {
//        String channelId = "alarm_channel";
//        String channelName = "Alarm Notifications";
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Toast.makeText(context, "show notification entered", Toast.LENGTH_SHORT).show();
//
//        // Create notification channel for Android O and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("Alarm Channel for Notifications");
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Create an intent to open the app when the notification is clicked
//        Intent mainIntent = new Intent(context, MainActivity.class);
//        mainIntent.putExtra("requestCode",0);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        // Set the notification message based on the requestCode
//        String message = (requestCode == 1) ? "3 min prior alert!" : "It's time!";
//
//        // Build the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                .setSmallIcon(R.drawable.baseline_access_alarm_24) // Set your notification icon
//                .setContentTitle("Reminder")
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent);
//
//        // Display the notification
//        if (notificationManager != null) {
//            notificationManager.notify(requestCode, builder.build()); // Use requestCode as notification ID
//            Log.d("AlarmReceiver", "Notification displayed for requestCode: " + requestCode);
//        } else {
//            Log.e("AlarmReceiver", "NotificationManager is null");
//        }
//    }
//}
package com.example.todo_app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "task_notifications";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve task details
        int taskId = intent.getIntExtra("taskId", 0);
        String taskText = intent.getStringExtra("taskText");

        // Open MainActivity when the notification is clicked
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, taskId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Get notification sound
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_access_alarm_24) // Ensure you have an icon in res/drawable
                .setContentTitle("Task Reminder")
                .setContentText(taskText)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Get NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel (for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Task Notifications", NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Show notification
        notificationManager.notify(taskId, builder.build());
    }
}
