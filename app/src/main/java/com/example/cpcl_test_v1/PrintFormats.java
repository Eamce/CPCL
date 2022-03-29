package com.example.cpcl_test_v1;

/**
 * Created by DTYUNLU on 14.02.2018.
 */

public enum PrintFormats {

    BARCODE("Barcode");
    private final String text;

    private PrintFormats(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
