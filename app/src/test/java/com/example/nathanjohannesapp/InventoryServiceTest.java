
package com.example.nathanjohannesapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class InventoryServiceTest {

    private InventoryService service;
    private FakeInventoryRepository repository;

    @Before
    public void setUp() {
        repository = new FakeInventoryRepository();
        service = new InventoryService(repository);
    }

    @Test
    public void addNewItem() {
        service.addItem("Apples", "10");

        assertEquals(1, service.getAllItems().size());
        assertEquals(10,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void addDuplicateItem() {
        service.addItem("Apples", "10");
        service.addItem("Apples", "5");

        assertEquals(1, service.getAllItems().size());
        assertEquals(15,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void duplicateNameIgnoresCapitalization() {
        service.addItem("Apples", "10");
        service.addItem("apples", "5");

        assertEquals(1, service.getAllItems().size());
        assertEquals(15,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void bulkSubtractQuantity() {
        service.addItem("Apples", "20");
        service.addItem("Apples", "-5");

        assertEquals(15,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void rejectSubtractionGreaterThanStock() {
        service.addItem("Apples", "10");

        InventoryResult result =
                service.addItem("Apples", "-15");

        assertFalse(result.isSuccess());

        assertEquals(
                "Not enough inventory to remove that amount.",
                result.getMessage()
        );

        assertEquals(10,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void rejectNegativeQuantityForNewItem() {

        InventoryResult result =
                service.addItem("Apples", "-5");

        assertFalse(result.isSuccess());

        assertEquals(
                "Cannot subtract from an item that does not exist.",
                result.getMessage()
        );

        assertTrue(service.getAllItems().isEmpty());
    }

    @Test
    public void incrementQuantity() {
        service.addItem("Apples", "10");

        int id = service.getAllItems().get(0).getId();
        service.adjustQuantity(id, 1);

        assertEquals(11,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void decrementQuantity() {
        service.addItem("Apples", "10");

        int id = service.getAllItems().get(0).getId();
        service.adjustQuantity(id, -1);

        assertEquals(9,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void preventNegativeInventory() {

        service.addItem("Apples", "1");

        int id = service.getAllItems().get(0).getId();

        service.adjustQuantity(id, -1);

        InventoryResult result =
                service.adjustQuantity(id, -1);

        assertFalse(result.isSuccess());

        assertEquals(
                "Quantity cannot be negative.",
                result.getMessage()
        );

        assertEquals(0,
                service.getAllItems().get(0).getQuantity());
    }

    @Test
    public void deleteItem() {
        service.addItem("Apples", "10");

        int id = service.getAllItems().get(0).getId();
        service.deleteItem(id);

        assertTrue(service.getAllItems().isEmpty());
    }

    @Test
    public void rejectQuantityOverflow() {

        service.addItem("Apples", "2147483647");

        InventoryResult result =
                service.addItem("Apples", "1");

        assertFalse(result.isSuccess());

        assertEquals(
                "Quantity is too large.",
                result.getMessage()
        );

        assertEquals(
                Integer.MAX_VALUE,
                service.getAllItems().get(0).getQuantity()
        );
    }


    // In-memory repository for unit testing.
    // Test 12: Find an existing item using the HashMap.
    @Test
    public void findItemByIdUsingHashMap() {
        service.addItem("Bolts", "25");

        int id = service.getAllItems().get(0).getId();

        InventoryItem found = service.findItemById(id);

        assertNotNull(found);
        assertEquals("Bolts", found.getItemName());
        assertEquals(25, found.getQuantity());
    }

    // Test 13: Search for an ID that does not exist.
    @Test
    public void findNonExistingItemById() {
        InventoryItem found = service.findItemById(999);

        assertNull(found);
    }

    // Test 14: Search using part of an item name.
    @Test
    public void searchByPartialName() {
        service.addItem("Steel Bolts", "25");
        service.addItem("Hex Bolts", "15");
        service.addItem("Washers", "50");

        List<InventoryItem> results =
                service.searchItems("Bolts");

        assertEquals(2, results.size());
    }

    // Test 15: Search should ignore capitalization.
    @Test
    public void searchIgnoresCapitalization() {
        service.addItem("Steel Bolts", "25");

        List<InventoryItem> results =
                service.searchItems("steel");

        assertEquals(1, results.size());
        assertEquals(
                "Steel bolts",
                results.get(0).getItemName()
        );
    }

    // Test 16: Searching for an unknown item.
    @Test
    public void searchReturnsEmptyForUnknownItem() {
        service.addItem("Bolts", "25");

        List<InventoryItem> results =
                service.searchItems("Hammers");

        assertTrue(results.isEmpty());
    }

    // Test 17: Sort inventory alphabetically.
    @Test
    public void sortItemsByNameAscending() {
        service.addItem("Washers", "15");
        service.addItem("Bolts", "25");
        service.addItem("Nuts", "50");

        List<InventoryItem> sorted =
                service.sortItems(
                        service.getAllItems(),
                        "NAME_ASC"
                );

        assertEquals("Bolts", sorted.get(0).getItemName());
        assertEquals("Nuts", sorted.get(1).getItemName());
        assertEquals("Washers", sorted.get(2).getItemName());
    }

    // Test 18: Sort inventory by quantity, lowest first.
    @Test
    public void sortItemsByQuantityAscending() {
        service.addItem("Bolts", "25");
        service.addItem("Nuts", "50");
        service.addItem("Washers", "15");

        List<InventoryItem> sorted =
                service.sortItems(
                        service.getAllItems(),
                        "QUANTITY_LOW"
                );

        assertEquals(15, sorted.get(0).getQuantity());
        assertEquals(25, sorted.get(1).getQuantity());
        assertEquals(50, sorted.get(2).getQuantity());
    }

    // Test 19: Sort inventory by quantity, highest first.
    @Test
    public void sortItemsByQuantityDescending() {
        service.addItem("Bolts", "25");
        service.addItem("Nuts", "50");
        service.addItem("Washers", "15");

        List<InventoryItem> sorted =
                service.sortItems(
                        service.getAllItems(),
                        "QUANTITY_HIGH"
                );

        assertEquals(50, sorted.get(0).getQuantity());
        assertEquals(25, sorted.get(1).getQuantity());
        assertEquals(15, sorted.get(2).getQuantity());
    }


    // Test 20: Verify deleting an item removes it from the HashMap.
    @Test
    public void deleteItemRemovesFromHashMap() {

        service.addItem("Bolts", "25");
        service.addItem("Nuts", "50");

        int id = service.getAllItems().get(0).getId();

        // Confirm the item exists in the index.
        assertNotNull(service.findItemById(id));
        assertEquals(2, service.getIndexedItemCount());

        // Delete the item.
        InventoryResult result = service.deleteItem(id);

        assertTrue(result.isSuccess());

        // Confirm the item is no longer indexed.
        assertNull(service.findItemById(id));
        assertEquals(1, service.getIndexedItemCount());
    }

    // Test 21: Verify quantity changes update the HashMap.
    @Test
    public void quantityUpdateRefreshesHashMap() {

        service.addItem("Bolts", "25");

        int id = service.getAllItems().get(0).getId();

        // Confirm the original indexed quantity.
        assertEquals(
                25,
                service.findItemById(id).getQuantity()
        );

        // Increase the quantity.
        InventoryResult result =
                service.adjustQuantity(id, 5);

        assertTrue(result.isSuccess());

        // Confirm the HashMap contains the updated quantity.
        InventoryItem updated = service.findItemById(id);

        assertNotNull(updated);
        assertEquals(30, updated.getQuantity());

        // Confirm the database repository agrees.
        assertEquals(
                30,
                service.getAllItems().get(0).getQuantity()
        );
    }

    // Test 22: Sort item names in descending order (Z-A).
    @Test
    public void sortItemsByNameDescending() {

        service.addItem("Bolts", "25");
        service.addItem("Washers", "15");
        service.addItem("Nuts", "50");

        List<InventoryItem> sorted =
                service.sortItems(
                        service.getAllItems(),
                        "NAME_DESC"
                );

        assertEquals(3, sorted.size());

        assertEquals(
                "Washers",
                sorted.get(0).getItemName()
        );

        assertEquals(
                "Nuts",
                sorted.get(1).getItemName()
        );

        assertEquals(
                "Bolts",
                sorted.get(2).getItemName()
        );
    }

    private static class FakeInventoryRepository
            implements InventoryDataSource {

        private final List<InventoryItem> items =
                new ArrayList<>();

        private int nextId = 1;

        @Override
        public List<InventoryItem> getAllItems() {
            return new ArrayList<>(items);
        }

        @Override
        public boolean addItem(String name, int quantity) {
            items.add(new InventoryItem(nextId++, name, quantity));
            return true;
        }

        @Override
        public boolean updateQuantity(int id, int quantity) {
            for (int i = 0; i < items.size(); i++) {
                InventoryItem item = items.get(i);

                if (item.getId() == id) {
                    items.set(i, new InventoryItem(
                            id,
                            item.getItemName(),
                            quantity
                    ));
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean deleteItem(int id) {
            return items.removeIf(item -> item.getId() == id);
        }

        @Override
        public InventoryItem findItemByName(String name) {
            for (InventoryItem item : items) {
                if (item.getItemName()
                        .trim()
                        .equalsIgnoreCase(name.trim())) {
                    return item;
                }
            }
            return null;
        }
    }
}
