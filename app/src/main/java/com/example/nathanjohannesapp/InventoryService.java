
package com.example.nathanjohannesapp;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class InventoryService {

    private final InventoryDataSource repository;
    private final InventoryIndex inventoryIndex;

    public InventoryService(InventoryDataSource repository) {
        this.repository = repository;
        this.inventoryIndex = new InventoryIndex();

        // Load existing database records into the HashMap.
        refreshIndex();
    }

    // Rebuild the HashMap from SQLite records.
    public void refreshIndex() {
        inventoryIndex.buildIndex(repository.getAllItems());
    }

    // Retrieve all inventory records.
    public List<InventoryItem> getAllItems() {
        return repository.getAllItems();
    }

    // Find an item using its ID.
    public InventoryItem findItemById(int id) {
        return inventoryIndex.findById(id);
    }

    // Return the number of indexed inventory items.
    public int getIndexedItemCount() {
        return inventoryIndex.size();
    }

    // Add an item, increase quantity, or bulk subtract.
    public InventoryResult addItem(String name, String quantityText) {

        String validationError =
                InputValidator.validateItem(name, quantityText);

        if (validationError != null) {
            return new InventoryResult(false, validationError);
        }

        String cleanName = name.trim();

        if (!cleanName.isEmpty()) {
            cleanName = cleanName.substring(0, 1).toUpperCase()
                    + cleanName.substring(1).toLowerCase();
        }

        int amount = Integer.parseInt(quantityText.trim());

        if (amount == 0) {
            return new InventoryResult(
                    false,
                    "Enter a quantity other than zero."
            );
        }

        // Existing item lookup by name.
        InventoryItem existing =
                repository.findItemByName(cleanName);

        if (existing != null) {

            long updatedQuantity =
                    (long) existing.getQuantity() + amount;

            if (updatedQuantity < 0) {
                return new InventoryResult(
                        false,
                        "Not enough inventory to remove that amount."
                );
            }

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

            if (updated) {
                refreshIndex();
            }

            return new InventoryResult(
                    updated,
                    updated
                            ? "Updated " + existing.getItemName()
                            : "Unable to update item."
            );
        }

        if (amount < 0) {
            return new InventoryResult(
                    false,
                    "Cannot subtract from an item that does not exist."
            );
        }

        boolean added = repository.addItem(cleanName, amount);

        if (added) {
            refreshIndex();
        }

        return new InventoryResult(
                added,
                added
                        ? "Item added successfully."
                        : "Unable to add item."
        );
    }

    // Increase or decrease an item's quantity.
    public InventoryResult adjustQuantity(int id, int change) {

        // Average O(1) HashMap lookup replaces linear search.
        InventoryItem item = inventoryIndex.findById(id);

        if (item == null) {
            return new InventoryResult(
                    false,
                    "Item not found."
            );
        }

        long newQuantity =
                (long) item.getQuantity() + change;

        if (newQuantity < 0) {
            return new InventoryResult(
                    false,
                    "Quantity cannot be negative."
            );
        }

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

        if (updated) {
            refreshIndex();
        }

        return new InventoryResult(
                updated,
                updated
                        ? "Quantity updated."
                        : "Unable to update quantity."
        );
    }

    // Delete an inventory item.
    public InventoryResult deleteItem(int id) {

        boolean deleted = repository.deleteItem(id);

        if (deleted) {
            refreshIndex();
        }

        return new InventoryResult(
                deleted,
                deleted
                        ? "Item deleted."
                        : "Unable to delete item."
        );
    }

    // Search inventory by partial item name.
    public List<InventoryItem> searchItems(String query) {

        List<InventoryItem> results = new ArrayList<>();

        String searchText = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);

        // Search the current inventory records.
        for (InventoryItem item : inventoryIndex.getAllItems()) {

            String itemName = item.getItemName()
                    .toLowerCase(Locale.ROOT);

            if (itemName.contains(searchText)) {
                results.add(item);
            }
        }

        return results;
    }

    // Sort inventory items by name or quantity.
    public List<InventoryItem> sortItems(
            List<InventoryItem> items,
            String sortOption) {

        List<InventoryItem> sortedItems =
                new ArrayList<>(items);

        Comparator<InventoryItem> byName =
                Comparator.comparing(
                        InventoryItem::getItemName,
                        String.CASE_INSENSITIVE_ORDER
                ).thenComparingInt(InventoryItem::getId);

        switch (sortOption) {

            case "QUANTITY_LOW":
                sortedItems.sort(
                        Comparator.comparingInt(
                                InventoryItem::getQuantity
                        ).thenComparing(byName)
                );
                break;

            case "QUANTITY_HIGH":
                sortedItems.sort(
                        Comparator.comparingInt(
                                InventoryItem::getQuantity
                        ).reversed().thenComparing(byName)
                );
                break;

            case "NAME_DESC":
                sortedItems.sort(byName.reversed());
                break;

            case "NAME_ASC":
            default:
                sortedItems.sort(byName);
                break;
        }

        return sortedItems;
    }

}
