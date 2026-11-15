
package com.example.nathanjohannesapp;

public class InputValidator {

    private static final int MAX_NAME_LENGTH = 50;

    // Check whether an inventory item name is valid.
    public static boolean isValidItemName(String name) {
        return name != null
                && !name.trim().isEmpty()
                && name.trim().length() <= MAX_NAME_LENGTH;
    }

    // Check whether the quantity is a valid
    // nonnegative whole number.
    public static boolean isValidQuantity(String quantity) {

        if (quantity == null || quantity.trim().isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(quantity.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    // Return a helpful validation message.
    public static String validateItem(
            String name,
            String quantity) {

        if (!isValidItemName(name)) {
            return "Enter an item name (1-50 characters).";
        }

        if (!isValidQuantity(quantity)) {
            return "Enter a valid whole number.";
        }

        return null;
    }
}
