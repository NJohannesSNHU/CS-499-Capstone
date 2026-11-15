
package com.example.nathanjohannesapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class InventoryViewModel extends ViewModel {

    private final InventoryService service;

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

    // Reload inventory records.
    public void refreshItems() {
        if (service != null) {
            items.setValue(service.getAllItems());
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
}
