
package com.example.nathanjohannesapp;

import java.util.List;

public interface InventoryDataSource {

    List<InventoryItem> getAllItems();

    boolean addItem(String name, int quantity);

    boolean updateQuantity(int id, int quantity);

    boolean deleteItem(int id);

    InventoryItem findItemByName(String name);
}
