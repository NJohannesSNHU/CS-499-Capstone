package com.example.nathanjohannesapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class InventoryIndexTest {

    private InventoryIndex inventoryIndex;

    @Before
    public void setUp() {
        inventoryIndex = new InventoryIndex();

        List<InventoryItem> items = Arrays.asList(
                new InventoryItem(1, "Bolts", 25),
                new InventoryItem(2, "Nuts", 50),
                new InventoryItem(3, "Washers", 15)
        );

        inventoryIndex.buildIndex(items);
    }

    // Test 1: Find an existing item by ID.
    @Test
    public void testFindExistingItem() {
        InventoryItem item = inventoryIndex.findById(2);

        assertNotNull(item);
        assertEquals("Nuts", item.getItemName());
        assertEquals(50, item.getQuantity());
    }

    // Test 2: Search for an ID that does not exist.
    @Test
    public void testFindNonExistingItem() {
        InventoryItem item = inventoryIndex.findById(99);

        assertNull(item);
    }

    // Test 3: Verify the number of indexed items.
    @Test
    public void testIndexSize() {
        assertEquals(3, inventoryIndex.size());
    }

    // Test 4: Add a new item to the HashMap.
    @Test
    public void testAddItem() {
        InventoryItem newItem =
                new InventoryItem(4, "Screws", 100);

        inventoryIndex.addOrUpdate(newItem);

        assertEquals(4, inventoryIndex.size());
        assertNotNull(inventoryIndex.findById(4));
    }

    // Test 5: Update an existing item.
    @Test
    public void testUpdateItem() {
        InventoryItem updated =
                new InventoryItem(1, "Bolts", 75);

        inventoryIndex.addOrUpdate(updated);

        assertEquals(
                75,
                inventoryIndex.findById(1).getQuantity()
        );

        assertEquals(3, inventoryIndex.size());
    }

    // Test 6: Remove an existing item.
    @Test
    public void testRemoveItem() {
        inventoryIndex.remove(3);

        assertNull(inventoryIndex.findById(3));
        assertEquals(2, inventoryIndex.size());
    }

    // Test 7: Rebuild the index.
    @Test
    public void testRebuildIndex() {
        List<InventoryItem> newItems = new ArrayList<>();

        newItems.add(
                new InventoryItem(10, "Hammers", 5)
        );

        inventoryIndex.buildIndex(newItems);

        assertEquals(1, inventoryIndex.size());
        assertNull(inventoryIndex.findById(1));
        assertNotNull(inventoryIndex.findById(10));
    }

    // Test 8: Build an empty index.
    @Test
    public void testEmptyIndex() {
        inventoryIndex.buildIndex(new ArrayList<>());

        assertEquals(0, inventoryIndex.size());
        assertNull(inventoryIndex.findById(1));
    }
}
