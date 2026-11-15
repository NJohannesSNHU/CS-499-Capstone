
package com.example.nathanjohannesapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class InputValidatorTest {

    @Test
    public void validItemName() {
        assertTrue(InputValidator.isValidItemName("Apples"));
    }

    @Test
    public void emptyItemName() {
        assertFalse(InputValidator.isValidItemName(""));
    }

    @Test
    public void whitespaceItemName() {
        assertFalse(InputValidator.isValidItemName("   "));
    }

    @Test
    public void nameTooLong() {
        String name = "A".repeat(51);
        assertFalse(InputValidator.isValidItemName(name));
    }

    @Test
    public void validPositiveQuantity() {
        assertTrue(InputValidator.isValidQuantity("10"));
    }

    @Test
    public void validNegativeQuantity() {
        assertTrue(InputValidator.isValidQuantity("-5"));
    }

    @Test
    public void validZeroQuantity() {
        assertTrue(InputValidator.isValidQuantity("0"));
    }

    @Test
    public void invalidTextQuantity() {
        assertFalse(InputValidator.isValidQuantity("abc"));
    }

    @Test
    public void emptyQuantity() {
        assertFalse(InputValidator.isValidQuantity(""));
    }

    @Test
    public void quantityTooLarge() {
        assertFalse(
                InputValidator.isValidQuantity("999999999999")
        );
    }
}
