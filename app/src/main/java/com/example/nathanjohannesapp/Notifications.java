package com.example.nathanjohannesapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Notifications extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;

    private Button permissionButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        permissionButton = findViewById(R.id.btnPermission);
        backButton = findViewById(R.id.btnBack);

        // Request SMS permission
        permissionButton.setOnClickListener(v -> checkSmsPermission());

        // Return to Inventory screen
        backButton.setOnClickListener(v -> finish());
    }

    private void checkSmsPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE
            );

        } else {
            // Already granted
            sendSms();
        }
    }

    private void sendSms() {

        try {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(
                    "5551234567",
                    null,
                    "Low Inventory Alert!",
                    null,
                    null
            );

            Toast.makeText(
                    this,
                    "SMS Sent Successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "SMS Failed to Send",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_CODE) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(
                        this,
                        "Permission Granted",
                        Toast.LENGTH_SHORT
                ).show();

                sendSms();

            } else {

                Toast.makeText(
                        this,
                        "Permission Denied - App still works normally",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }
}