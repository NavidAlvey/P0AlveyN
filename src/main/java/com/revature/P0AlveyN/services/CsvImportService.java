package com.revature.P0AlveyN.services;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.exceptions.CsvValidationException;
import com.revature.P0AlveyN.entity.TransactionType;
import com.revature.P0AlveyN.entity.User;
import java.util.Optional;
import com.revature.P0AlveyN.services.TransactionService.SplitRequest;
import com.revature.P0AlveyN.services.TransactionService.TransactionRequest;
import com.revature.P0AlveyN.util.CsvParser;
import com.revature.P0AlveyN.util.InputValidation;

public class CsvImportService {

    private final CsvParser parser;
    private final TransactionService transactionService;
    private final UserService userService;

    public CsvImportService(CsvParser parser,
                            TransactionService transactionService,
                            UserService userService) {
        this.parser = parser;
        this.transactionService = transactionService;
        this.userService = userService;
    }

   
     //Imports transactions from a CSV file
    public ImportResult importFile(String filePath) throws IOException, CsvValidationException {
        Path path = InputValidation.requireReadableFile(filePath);
        List<CsvParser.CsvRecord> records = parser.parse(path);
        List<String> errors = new ArrayList<>();
        int imported = 0;

        for (CsvParser.CsvRecord record : records) {
            try {
                TransactionRequest request = buildRequest(record);
                transactionService.recordTransaction(request);
                imported++;
            } catch (SQLException e) {
                errors.add("Row dated " + record.transactionDate() + " failed: " + e.getMessage());
            } catch (Exception ex) {
                errors.add("Row dated " + record.transactionDate() + " failed: " + ex.getMessage());
            }
        }

        return new ImportResult(imported, records.size() - imported, errors);
    }

    private TransactionRequest buildRequest(CsvParser.CsvRecord record) throws SQLException {
        String lastFour = record.cardLastFour();
        String name = record.cardholderName();
        if (name == null || name.isBlank()) {
            Optional<User> userOpt = userService.findByLastFour(lastFour);
            if (userOpt.isPresent()) {
                name = userOpt.get().getName();
            } else {
                name = "Card " + lastFour;
            }
        }

        SplitRequest split = new SplitRequest(
                name,
                lastFour,
                true,
                record.amount()
        );

        return new TransactionRequest(
                record.transactionDate(),
                record.vendor(),
                record.amount(),
                lastFour,
                name,
                record.type() == 0 ? TransactionType.PURCHASE : record.type(),
                record.description(),
                record.categories(),
                List.of(split)
        );
    }

    public record ImportResult(int imported, int failed, List<String> errors) {
    }
}
