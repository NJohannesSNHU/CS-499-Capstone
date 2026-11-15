
package com.example.nathanjohannesapp;

import android.content.Context;

import java.util.List;

public class InventoryRepository implements InventoryDataSource {

    private final DatabaseHelper db;

    public InventoryRepository(Context context) {
        db = new DatabaseHelper(
                context.getApplicationContext()
        );
    }

    // Retrieve all inventory items.
    public List<InventoryItem> getAllItems() {
        return db.getAllItems();
    }

    // Add a new inventory item.
    public boolean addItem(String name, int quantity) {
        return db.insertItem(name, quantity);
    }

    // Update an existing item's quantity.
    public boolean updateQuantity(int id, int quantity) {
        return db.updateItem(id, quantity) > 0;
    }

    // Delete an inventory item.
    public boolean deleteItem(int id) {
        return db.deleteItem(id) > 0;
    }

    // Find an existing item by its name.
    public InventoryItem findItemByName(String name) {

        for (InventoryItem item : getAllItems()) {

            if (item.getItemName()
                    .trim()
                    .equalsIgnoreCase(name.trim())) {

                return item;
            }
        }

        return null;
    }
}
