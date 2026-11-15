package com.example.nathanjohannesapp;

public class InventoryResult {

    private final boolean success;
    private final String message;

    public InventoryResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
