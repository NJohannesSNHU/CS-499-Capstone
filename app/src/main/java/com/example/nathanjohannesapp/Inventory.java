package com.example.nathanjohannesapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class Inventory extends AppCompatActivity {

    private EditText itemName;
    private EditText quantity;
    private Button addButton;
    private Button notificationButton;
    private TableLayout tableInventory;
    private DatabaseHelper db;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize database
        db = new DatabaseHelper(this);

        // Connect views to XML
        itemName = findViewById(R.id.etItemName);
        quantity = findViewById(R.id.etQuantity);
        addButton = findViewById(R.id.btnAddItem);
        tableInventory = findViewById(R.id.tableInventory);

        // Only include this if you added the button to XML
        notificationButton = findViewById(R.id.btnNotifications);

        // Display saved items
        displayItems();

        logoutButton = findViewById(R.id.btnLogout);

        logoutButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            Inventory.this,
                            MainActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });

        // Add item button
        addButton.setOnClickListener(v -> {

            String item =
                    itemName.getText().toString().trim();

            String qtyText =
                    quantity.getText().toString().trim();

            if (item.isEmpty() || qtyText.isEmpty()) {
                Toast.makeText(
                        this,
                        "Please enter an item and quantity.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            int qty = Integer.parseInt(qtyText);

            boolean success =
                    db.insertItem(item, qty);

            if (success) {

                Toast.makeText(
                        this,
                        "Item Added",
                        Toast.LENGTH_SHORT
                ).show();

                itemName.setText("");
                quantity.setText("");

                displayItems();

            } else {

                Toast.makeText(
                        this,
                        "Unable to add item.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Open Notifications screen
        if (notificationButton != null) {
            notificationButton.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                Inventory.this,
                                Notifications.class
                        );

                startActivity(intent);
            });
        }
    }

    private void displayItems() {

        // Keep the header row
        if (tableInventory.getChildCount() > 1) {
            tableInventory.removeViews(
                    1,
                    tableInventory.getChildCount() - 1
            );
        }

        List<InventoryItem> items =
                db.getAllItems();

        for (InventoryItem item : items) {

            TableRow row = new TableRow(this);

            // ITEM NAME COLUMN
            TextView name = new TextView(this);
            TableRow.LayoutParams nameParams =
                    new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            name.setLayoutParams(nameParams);

            // QTY COLUMN
            TextView qty = new TextView(this);
            TableRow.LayoutParams qtyParams =
                    new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            qty.setLayoutParams(qtyParams);

            // ACTION COLUMN (DELETE BUTTON)
            Button delete = new Button(this);
            TableRow.LayoutParams deleteParams =
                    new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            delete.setLayoutParams(deleteParams);

            name.setText(item.getItemName());
            qty.setText(String.valueOf(item.getQuantity()));
            delete.setText("Delete");

            delete.setOnClickListener(v -> {

                db.deleteItem(item.getId());

                Toast.makeText(this,
                        "Item Deleted",
                        Toast.LENGTH_SHORT).show();

                displayItems();
            });

            row.addView(name);
            row.addView(qty);
            row.addView(delete);

            tableInventory.addView(row);
        }
    }
}