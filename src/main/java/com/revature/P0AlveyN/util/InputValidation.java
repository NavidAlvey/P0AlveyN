package com.revature.P0AlveyN.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

public final class InputValidation {

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("M/d/yyyy"),
            DateTimeFormatter.ofPattern("M/d/yy")
    );

    private InputValidation() {
    }

    public static String requireNonBlank(String input, String fieldName) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
        return input.trim();
    }

    public static LocalDate parseDate(String rawDate) {
        String cleaned = requireNonBlank(rawDate, "Date");
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(cleaned, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Unsupported date format: " + rawDate);
    }

    public static BigDecimal parseAmount(String rawAmount) {
        String cleaned = requireNonBlank(rawAmount, "Amount")
                .replace("$", "")
                .replace(",", "")
                .replace("+", "")
                .trim();
        BigDecimal value = new BigDecimal(cleaned);
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static String extractLastFour(String cardNumber) {
        String digitsOnly = requireNonBlank(cardNumber, "Card number")
                .chars()
                .filter(Character::isDigit)
                .mapToObj(c -> String.valueOf((char) c))
                .reduce("", String::concat);
        if (digitsOnly.length() < 4) {
            throw new IllegalArgumentException("Card number must contain at least four digits");
        }
        return digitsOnly.substring(digitsOnly.length() - 4);
    }

    public static Path requireReadableFile(String filePath) {
        Objects.requireNonNull(filePath, "File path required");
        Path path = Path.of(filePath.trim());
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException("File is not readable: " + filePath);
        }
        return path;
    }
}
