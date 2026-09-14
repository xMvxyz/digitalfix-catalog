package com.digitalfix.catalog.exception;

public class CatalogAccessDeniedException extends RuntimeException {
    public CatalogAccessDeniedException(String message) {
        super(message);
    }
}
