
package com.example.nathanjohannesapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class InventoryViewModel extends ViewModel {

    private final InventoryService service;

    private String searchQuery = "";
    private String sortOption = "NAME_ASC";

    private final MutableLiveData<List<InventoryItem>> items =
            new MutableLiveData<>(new ArrayList<>());

    private final MutableLiveData<String> message =
            new MutableLiveData<>();

    public InventoryViewModel(InventoryService service) {
        this.service = service;
        refreshItems();
    }

    public LiveData<List<InventoryItem>> getItems() {
        return items;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    // Reload inventory and apply current search/sort options.
    public void refreshItems() {

        if (service != null) {

            // Synchronize the in-memory index with SQLite.
            service.refreshIndex();

            List<InventoryItem> filteredItems =
                    service.searchItems(searchQuery);

            List<InventoryItem> sortedItems =
                    service.sortItems(filteredItems, sortOption);

            items.setValue(sortedItems);
        }
    }


    // Add an item or update an existing quantity.
    public boolean addItem(String name, String quantity) {
        InventoryResult result = service.addItem(name, quantity);

        message.setValue(result.getMessage());

        if (result.isSuccess()) {
            refreshItems();
        }

        return result.isSuccess();
    }

    // Increase or decrease an item's quantity.
    public void adjustQuantity(int id, int change) {
        InventoryResult result = service.adjustQuantity(id, change);

        message.setValue(result.getMessage());

        if (result.isSuccess()) {
            refreshItems();
        }
    }

    // Delete an inventory item.
    public void deleteItem(int id) {
        InventoryResult result = service.deleteItem(id);

        message.setValue(result.getMessage());

        if (result.isSuccess()) {
            refreshItems();
        }
    }

    // NEW: Search inventory items.
    public void searchItems(String query) {
        searchQuery = query == null ? "" : query;
        refreshItems();
    }

    // NEW: Change the sorting option.
    public void setSortOption(String option) {
        sortOption = option;
        refreshItems();
    }
}
