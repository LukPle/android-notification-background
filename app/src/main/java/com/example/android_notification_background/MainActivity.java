package com.example.android_notification_background;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * This Activity should resemble a Reminder App.
 * It is possible to write down the content of the reminder and set a specific time.
 * Setting the reminder shows an Android Notification with the content to the anticipated time.
 * It is also possible to delete an ongoing reminder.
 *
 * The documentation focuses on the AlertReceiver and the AlarmManager.
 * For Javadoc information about the TimePicker and Notification check out the specific project.
 *
 * Layout File: activity_main.xml
 *
 * @author Lukas Plenk
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // This is the code for the extra to the AlertReceiver class
    public static final String EXTRA_CONTENT = "com.example.android_notification_background.EXTRA_CONTENT";

    private EditText content, time;
    private Button setButton, deleteButton;

    Calendar calendar;
    int hour;
    int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        content = findViewById(R.id.edit_content);

        time = findViewById(R.id.edit_time);
        time.setOnClickListener(this);
        time.setFocusable(false);

        setButton = findViewById(R.id.button_setReminder);
        setButton.setOnClickListener(this);

        deleteButton = findViewById(R.id.button_deleteReminder);
        deleteButton.setOnClickListener(this);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel
                    ("Remind", "Reminder", NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Channel for Reminder Notifications");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.edit_time:
                setTime();
                break;

            case R.id.button_setReminder:
                setReminder();
                break;

            case R.id.button_deleteReminder:
                deleteReminder();
                break;
        }
    }

    private void setTime() {

        calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                String timeInTxt = hour + ":" + minute;
                time.setText(timeInTxt);
            }
        }, hour, minute, DateFormat.is24HourFormat(MainActivity.this));
        timePickerDialog.show();
    }

    /**
     * Method for setting the reminder.
     * The reminder should only work if all EditText fields have values.
     * An AlarmManger calls the AlertReceiver class with the Notification to the right time.
     * This is been done trough the values set in the TimePicker and a PendingIntent.
     */
    private void setReminder() {

        if (content.getText().toString().trim().isEmpty() || time.getText().toString().trim().isEmpty()) {

            Toast.makeText(MainActivity.this, "Please insert all information", Toast.LENGTH_LONG).show();
        }
        else {

            Toast.makeText(MainActivity.this, "Reminder set for " +time.getText().toString(), Toast.LENGTH_LONG).show();

            // The AlarmManager is used for background activity that calls a Notification at an exact time
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(MainActivity.this, AlertReceiver.class);
            intent.putExtra(EXTRA_CONTENT, content.getText().toString());

            // PendingIntent.FLAG_UPDATE_CURRENT is important for giving an extra
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // If the value from the TimePicker is a time set before the current time, it should be called the next day
            if (calendar.before(Calendar.getInstance())) {

                calendar.add(Calendar.DATE, 1);
            }

            // Statement for executing the AlarmManager with the PendingIntent to the right time
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    /**
     * Method for deleting an ongoing reminder.
     * This method creates an AlarmManager that cancels all current PendingIntents.
     */
    private void deleteReminder() {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(MainActivity.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        Toast.makeText(MainActivity.this, "Reminder deleted", Toast.LENGTH_LONG).show();
    }
}