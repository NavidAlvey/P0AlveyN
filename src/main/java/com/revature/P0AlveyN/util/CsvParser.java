package com.revature.P0AlveyN.util;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.revature.P0AlveyN.entity.TransactionType;

public class CsvParser {

    private static final List<String> DATE_HEADERS =
            List.of("transaction date", "date", "posted date");

    private static final List<String> CARD_HEADERS =
            List.of("card", "card no", "card number", "last four", "account number");

    private static final List<String> DESCRIPTION_HEADERS =
            List.of("description", "merchant", "details", "memo", "name");

    private static final List<String> CATEGORY_HEADERS =
            List.of("category", "categories");

    private static final List<String> DEBIT_HEADERS =
            List.of("debit", "amount debit", "purchase amount");

    private static final List<String> CREDIT_HEADERS =
            List.of("credit", "amount credit", "payment");

    private static final List<String> PAYMENT_KEYWORDS =
            List.of("pymt", "payment", "pmt", "mobile pymt", "online pymt");

    public List<CsvRecord> parse(Path csvPath) throws IOException, CsvValidationException {
        List<CsvRecord> records = new ArrayList<>();

        try (Reader reader = Files.newBufferedReader(csvPath);
             CSVReader csvReader = new CSVReader(reader)) {

            // Read the header of each column
            String[] header = csvReader.readNext();
            if (header == null) {
                throw new IllegalArgumentException("CSV is empty: " + csvPath);
            }

            Map<String, Integer> indexMap = buildIndexMap(header);
            validateRequiredColumns(indexMap, csvPath);

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                if (isBlankRow(row)) continue;

                // Parse through each field
                LocalDate date = InputValidation.parseDate(
                        fetch(row, indexMap, DATE_HEADERS).orElseThrow()
                );

                String cardRaw = fetch(row, indexMap, CARD_HEADERS).orElse("");
                String lastFour = InputValidation.extractLastFour(cardRaw);

                String description = fetch(row, indexMap, DESCRIPTION_HEADERS)
                        .orElse("Unknown Vendor")
                        .trim();

                String categoryRaw = fetch(row, indexMap, CATEGORY_HEADERS).orElse("Uncategorized");
                List<String> categories = parseCategories(categoryRaw);

                BigDecimal debit = parseAmount(fetch(row, indexMap, DEBIT_HEADERS).orElse(""));
                BigDecimal credit = parseAmount(fetch(row, indexMap, CREDIT_HEADERS).orElse(""));

                BigDecimal amount = debit.signum() > 0 ? debit : credit;
                if (amount.signum() == 0) continue; // ignore zero-amount rows

                int type = determineType(description, debit, credit);

                records.add(new CsvRecord(
                        date,
                        lastFour,
                        null,      // cardholder name (not in file)
                        amount.abs(),             // ALWAYS positive
                        description,              // vendor
                        categories,
                        description,
                        type
                ));
            }
        }

        return records;
    }

    // Turn the header row into a map of normalized header to index
    private Map<String, Integer> buildIndexMap(String[] header) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < header.length; i++) {
            String normalized = header[i] == null ? "" : header[i].trim().toLowerCase(Locale.US);
            map.put(normalized, i);
        }
        return map;
    }


    // Validate required fields for this CSV format
    private void validateRequiredColumns(Map<String, Integer> indexMap, Path csvPath) {
        if (findIndex(indexMap, DATE_HEADERS).isEmpty()
                || findIndex(indexMap, CARD_HEADERS).isEmpty()
                || (findIndex(indexMap, DEBIT_HEADERS).isEmpty()
                && findIndex(indexMap, CREDIT_HEADERS).isEmpty())
                || findIndex(indexMap, DESCRIPTION_HEADERS).isEmpty()) {

            throw new IllegalArgumentException(
                    "CSV missing required fields (date, card, description, debit/credit): " + csvPath
            );
        }
    }


    // Flexible header matching
    private Optional<Integer> findIndex(Map<String, Integer> indexMap, List<String> candidates) {
        for (String candidate : candidates) {
            for (String header : indexMap.keySet()) {
                if (header.contains(candidate.toLowerCase(Locale.US))) {
                    return Optional.of(indexMap.get(header));
                }
            }
        }
        return Optional.empty();
    }

    private Optional<String> fetch(String[] row, Map<String, Integer> indexMap, List<String> headers) {
        return findIndex(indexMap, headers)
                .filter(idx -> idx < row.length)
                .map(idx -> row[idx])
                .filter(val -> val != null && !val.isBlank());
    }

    private boolean isBlankRow(String[] row) {
        for (String cell : row) {
            if (cell != null && !cell.isBlank()) return false;
        }
        return true;
    }

    private BigDecimal parseAmount(String raw) {
        if (raw == null || raw.isBlank()) return BigDecimal.ZERO;
        try {
            return new BigDecimal(raw.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    // Category splitting by slash/semicolon/pipe
    private List<String> parseCategories(String raw) {
        String[] parts = raw.split("[|;/]");
        List<String> cats = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) cats.add(t);
        }
        return cats.isEmpty() ? List.of("Uncategorized") : cats;
    }

    // Determine transaction type 
    private int determineType(String description, BigDecimal debit, BigDecimal credit) {
        String descLower = description.toLowerCase(Locale.US);

        if (debit.signum() > 0) {
            return TransactionType.PURCHASE;
        }

        if (credit.signum() > 0) {
            boolean isPayment = PAYMENT_KEYWORDS.stream().anyMatch(descLower::contains);
            return isPayment ? TransactionType.PAYMENT : TransactionType.REFUND;
        }

        return TransactionType.PURCHASE;
    }

    public record CsvRecord(
            LocalDate transactionDate,
            String cardLastFour,
            String cardholderName,
            BigDecimal amount,
            String vendor,
            List<String> categories,
            String description,
            int type
    ) { }
}
