package com.petcare.exception;

public class InvalidVaccinationDateException extends RuntimeException {
    public InvalidVaccinationDateException(String message) {
        super(message);
    }
}
