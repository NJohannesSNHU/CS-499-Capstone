
package com.example.nathanjohannesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class Inventory extends AppCompatActivity {

    private EditText itemName;
    private EditText quantity;
    private TableLayout tableInventory;
    private TextView emptyInventory;
    private TextView statusMessage;

    private InventoryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Connect views to the XML layout.
        itemName = findViewById(R.id.etItemName);
        quantity = findViewById(R.id.etQuantity);
        tableInventory = findViewById(R.id.tableInventory);
        emptyInventory = findViewById(R.id.tvEmptyInventory);
        statusMessage = findViewById(R.id.tvStatusMessage);

        Button addButton = findViewById(R.id.btnAddItem);
        Button logoutButton = findViewById(R.id.btnLogout);
        Button notificationButton =
                findViewById(R.id.btnNotifications);

        // Set up the application's layers.
        InventoryRepository repository =
                new InventoryRepository(this);

        InventoryService service =
                new InventoryService(repository);

        InventoryViewModelFactory factory =
                new InventoryViewModelFactory(service);

        viewModel = new ViewModelProvider(
                this, factory
        ).get(InventoryViewModel.class);

        // Observe changes to inventory records.
        viewModel.getItems().observe(this, items -> {
            displayItems(items);
        });

        // Observe status and validation messages.
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                statusMessage.setText(message);
                statusMessage.setVisibility(View.VISIBLE);

                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Add a new item or update an existing one.
        addButton.setOnClickListener(v -> {

            String name =
                    itemName.getText().toString().trim();

            String qty =
                    quantity.getText().toString().trim();

            String error =
                    InputValidator.validateItem(name, qty);

            if (error != null) {
                statusMessage.setText(error);
                statusMessage.setVisibility(View.VISIBLE);
                return;
            }

            boolean success = viewModel.addItem(name, qty);

            if (success) {
                itemName.setText("");
                quantity.setText("");
            }
        });

        // Log out.
        logoutButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Inventory.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });

        // Open the notifications screen.
        if (notificationButton != null) {
            notificationButton.setOnClickListener(v -> {

                Intent intent = new Intent(
                        Inventory.this,
                        Notifications.class
                );

                startActivity(intent);
            });
        }
    }

    // Display the current inventory.
    private void displayItems(List<InventoryItem> items) {

        // Keep the table header.
        if (tableInventory.getChildCount() > 1) {
            tableInventory.removeViews(
                    1,
                    tableInventory.getChildCount() - 1
            );
        }

        // Show an empty inventory message.
        if (items == null || items.isEmpty()) {
            emptyInventory.setVisibility(View.VISIBLE);
            return;
        }

        emptyInventory.setVisibility(View.GONE);

        for (InventoryItem item : items) {

            TableRow row = new TableRow(this);

            // Item name.
            TextView name = new TextView(this);
            name.setText(item.getItemName());

            name.setLayoutParams(
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1f
                    )
            );

            // Quantity.
            TextView qty = new TextView(this);
            qty.setText(
                    String.valueOf(item.getQuantity())
            );

            qty.setLayoutParams(
                    new TableRow.LayoutParams(
                            0,
                            TableRow.LayoutParams.WRAP_CONTENT,
                            1f
                    )
            );

            // Decrease quantity.
            Button minusButton = new Button(this);
            minusButton.setText("−");
            minusButton.setTextSize(14);
            minusButton.setMinWidth(0);
            minusButton.setMinimumWidth(0);
            minusButton.setPadding(0, 0, 0, 0);

            minusButton.setOnClickListener(v ->
                    viewModel.adjustQuantity(item.getId(), -1)
            );

            // Increase quantity.
            Button plusButton = new Button(this);
            plusButton.setText("+");
            plusButton.setTextSize(14);
            plusButton.setMinWidth(0);
            plusButton.setMinimumWidth(0);
            plusButton.setPadding(0, 0, 0, 0);

            plusButton.setOnClickListener(v ->
                    viewModel.adjustQuantity(item.getId(), 1)
            );

            // Delete item.
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setTextSize(12);
            deleteButton.setAllCaps(false);
            deleteButton.setMinWidth(0);
            deleteButton.setMinimumWidth(0);
            deleteButton.setPadding(4, 0, 4, 0);

            deleteButton.setOnClickListener(v ->
                    viewModel.deleteItem(item.getId())
            );

            // Add views to the table row.
            row.addView(name);
            row.addView(qty);

            TableRow.LayoutParams minusParams =
                    new TableRow.LayoutParams(dp(48), dp(48));
            minusParams.setMargins(dp(2), dp(2), dp(2), dp(2));

            TableRow.LayoutParams plusParams =
                    new TableRow.LayoutParams(dp(48), dp(48));
            plusParams.setMargins(dp(2), dp(2), dp(2), dp(2));

            TableRow.LayoutParams deleteParams =
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            dp(48));
            deleteParams.setMargins(dp(2), dp(2), dp(2), dp(2));

            row.addView(minusButton, minusParams);
            row.addView(plusButton, plusParams);
            row.addView(deleteButton, deleteParams);

            tableInventory.addView(row);
        }
    }
    private int dp(int value) {
        return Math.round(
                value * getResources().getDisplayMetrics().density
        );
    }
}
