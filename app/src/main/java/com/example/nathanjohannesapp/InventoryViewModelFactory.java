
package com.example.nathanjohannesapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class InventoryViewModelFactory
        implements ViewModelProvider.Factory {

    private final InventoryService service;

    public InventoryViewModelFactory(InventoryService service) {
        this.service = service;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(
            @NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(
                InventoryViewModel.class)) {

            return (T) new InventoryViewModel(service);
        }

        throw new IllegalArgumentException(
                "Unknown ViewModel class"
        );
    }
}
