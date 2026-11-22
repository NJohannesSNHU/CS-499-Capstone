package com.example.nathanjohannesapp;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryIndex {

    private final Map<Integer, InventoryItem> itemIndex;

    public InventoryIndex() {
        itemIndex = new HashMap<>();
    }

    // Build the index from database records.
    public void buildIndex(List<InventoryItem> items) {
        itemIndex.clear();

        if (items != null) {
            for (InventoryItem item : items) {
                itemIndex.put(item.getId(), item);
            }
        }
    }

    // Find an inventory item using its unique ID.
    public InventoryItem findById(int id) {
        return itemIndex.get(id);
    }

    // Add or replace an item in the index.
    public void addOrUpdate(InventoryItem item) {
        if (item != null) {
            itemIndex.put(item.getId(), item);
        }
    }

    // Remove an item from the index.
    public void remove(int id) {
        itemIndex.remove(id);
    }

    // Return the number of indexed items.
    public int size() {
        return itemIndex.size();
    }

    // Return a snapshot of indexed inventory items.
    public Collection<InventoryItem> getAllItems() {
        return new java.util.ArrayList<>(itemIndex.values());
    }
}
