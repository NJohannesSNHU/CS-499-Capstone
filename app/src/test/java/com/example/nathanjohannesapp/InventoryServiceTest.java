
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
