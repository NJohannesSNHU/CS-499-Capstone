
package com.example.nathanjohannesapp;

import java.util.List;

public class InventoryService {

    private final InventoryDataSource repository;

    public InventoryService(InventoryDataSource repository) {
        this.repository = repository;
    }

    // Retrieve all inventory records.
    public List<InventoryItem> getAllItems() {
        return repository.getAllItems();
    }

    // Add a new item, increase quantity, or bulk subtract.
    public InventoryResult addItem(String name, String quantityText) {

        String validationError =
                InputValidator.validateItem(name, quantityText);

        if (validationError != null) {
            return new InventoryResult(false, validationError);
        }

        String cleanName = name.trim();
        int amount = Integer.parseInt(quantityText.trim());

        // Zero does not change inventory.
        if (amount == 0) {
            return new InventoryResult(
                    false,
                    "Enter a quantity other than zero."
            );
        }

        // Check whether the item already exists.
        InventoryItem existing =
                repository.findItemByName(cleanName);

        if (existing != null) {

            long updatedQuantity =
                    (long) existing.getQuantity() + amount;

            // Prevent negative inventory.
            if (updatedQuantity < 0) {
                return new InventoryResult(
                        false,
                        "Not enough inventory to remove that amount."
                );
            }

            // Prevent integer overflow.
            if (updatedQuantity > Integer.MAX_VALUE) {
                return new InventoryResult(
                        false,
                        "Quantity is too large."
                );
            }

            boolean updated = repository.updateQuantity(
                    existing.getId(),
                    (int) updatedQuantity
            );

            return new InventoryResult(
                    updated,
                    updated
                            ? "Updated " + existing.getItemName()
                            : "Unable to update item."
            );
        }

        // Do not create a new item with negative stock.
        if (amount < 0) {
            return new InventoryResult(
                    false,
                    "Cannot subtract from an item that does not exist."
            );
        }

        // Add the new item to the repository.
        boolean added = repository.addItem(cleanName, amount);

        return new InventoryResult(
                added,
                added
                        ? "Item added successfully."
                        : "Unable to add item."
        );
    }

    // Increase or decrease an item's quantity.
    public InventoryResult adjustQuantity(int id, int change) {

        for (InventoryItem item : repository.getAllItems()) {

            if (item.getId() == id) {

                long newQuantity =
                        (long) item.getQuantity() + change;

                // Prevent negative quantities.
                if (newQuantity < 0) {
                    return new InventoryResult(
                            false,
                            "Quantity cannot be negative."
                    );
                }

                // Prevent integer overflow.
                if (newQuantity > Integer.MAX_VALUE) {
                    return new InventoryResult(
                            false,
                            "Quantity is too large."
                    );
                }

                boolean updated = repository.updateQuantity(
                        id,
                        (int) newQuantity
                );

                return new InventoryResult(
                        updated,
                        updated
                                ? "Quantity updated."
                                : "Unable to update quantity."
                );
            }
        }

        // Item ID was not found.
        return new InventoryResult(
                false,
                "Item not found."
        );
    }

    // Delete an inventory item.
    public InventoryResult deleteItem(int id) {

        boolean deleted = repository.deleteItem(id);

        return new InventoryResult(
                deleted,
                deleted
                        ? "Item deleted."
                        : "Unable to delete item."
        );
    }
}