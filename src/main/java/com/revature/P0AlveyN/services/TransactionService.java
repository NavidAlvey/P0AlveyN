package com.revature.P0AlveyN.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.revature.P0AlveyN.entity.Category;
import com.revature.P0AlveyN.entity.Transaction;
import com.revature.P0AlveyN.entity.TransactionSplit;
import com.revature.P0AlveyN.entity.TransactionType;
import com.revature.P0AlveyN.entity.User;
import com.revature.P0AlveyN.repository.TransactionCategoryRepository;
import com.revature.P0AlveyN.repository.TransactionRepository;
import com.revature.P0AlveyN.repository.TransactionSplitRepository;

public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionSplitRepository splitRepository;
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final CategoryService categoryService;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionSplitRepository splitRepository,
                              TransactionCategoryRepository transactionCategoryRepository,
                              CategoryService categoryService,
                              UserService userService) {
        this.transactionRepository = transactionRepository;
        this.splitRepository = splitRepository;
        this.transactionCategoryRepository = transactionCategoryRepository;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    // Record a new transaction with splits and categories
    public Transaction recordTransaction(TransactionRequest request) throws SQLException {
        validateRequest(request);
        BigDecimal normalizedAmount = request.amount().abs();

        Transaction transaction = new Transaction(
                request.transactionDate(),
                request.vendor(),
                normalizedAmount,
                request.cardLastFour(),
                request.type(),
                request.description());

        // Save transaction first to get ID
        transaction = transactionRepository.save(transaction);

        // Add categories
        if (request.categoryNames() != null && !request.categoryNames().isEmpty()) {
            for (String categoryName : request.categoryNames()) {
                if (categoryName != null && !categoryName.trim().isEmpty()) {
                    Category category = categoryService.findOrCreate(categoryName.trim());
                    transactionCategoryRepository.addCategoryToTransaction(transaction.getId(), category.getId());
                }
            }
        }

        // Handle splits
        List<SplitRequest> splits;
        if (request.splits().isEmpty()) {
            splits = new ArrayList<>();
            splits.add(new SplitRequest(
                    request.cardholderName(),
                    request.cardLastFour(),
                    true,
                    normalizedAmount));
        } else {
            splits = request.splits();
        }

        BigDecimal splitTotal = BigDecimal.ZERO;
        for (SplitRequest split : splits) {
            splitTotal = splitTotal.add(split.amount());
        }

        if (splitTotal.compareTo(normalizedAmount) != 0) {
            throw new IllegalArgumentException("Split amounts must total the transaction amount");
        }

        // Save splits
        for (SplitRequest split : splits) {
            User user = userService.ensureUser(
                    Optional.ofNullable(split.cardholderName()).orElse(split.lastFourDigits()),
                    split.lastFourDigits(),
                    split.primaryCardholder());
            TransactionSplit transactionSplit = new TransactionSplit(
                    transaction.getId(),
                    user.getId(),
                    split.amount());
            splitRepository.save(transactionSplit);
        }

        return transaction;
    }


    // Get transactions for a user by last four digits
    public List<Transaction> getTransactionsForUser(String lastFourDigits) throws SQLException {
        List<TransactionSplit> splits = splitRepository.findByUserLastFourDigits(lastFourDigits);
        List<Transaction> transactions = new ArrayList<>();
        
        for (TransactionSplit split : splits) {
            Optional<Transaction> transaction = transactionRepository.findById(split.getTransactionId());
            if (transaction.isPresent()) {
                transactions.add(transaction.get());
            }
        }
        
        return transactions;
    }

    
    // Get transactions between two dates
    public List<Transaction> getTransactionsBetween(LocalDate start, LocalDate end) throws SQLException {
        return transactionRepository.findByTransactionDateBetween(start, end);
    }


    // Get a transaction by ID
    public Transaction getTransaction(Long id) throws SQLException {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);
        if (transactionOpt.isPresent()) {
            return transactionOpt.get();
        } else {
            throw new IllegalArgumentException("Transaction not found: " + id);
        }
    }

    
    // Calculate totals by user for a date range
    public Map<User, BigDecimal> totalsByUser(LocalDate start, LocalDate end) throws SQLException {
        List<TransactionSplit> splits = splitRepository.findByTransactionDateBetween(start, end);
        Map<User, BigDecimal> totals = new HashMap<>();
        
        for (TransactionSplit split : splits) {
            Optional<Transaction> transactionOpt = transactionRepository.findById(split.getTransactionId());
            
            // Get user by ID from split
            Optional<User> userOpt = userService.findById(split.getUserId());
            
            if (transactionOpt.isPresent() && userOpt.isPresent()) {
                Transaction transaction = transactionOpt.get();
                User user = userOpt.get();
                int type = transaction.getType();
                BigDecimal signedShare = split.getShareAmount()
                        .multiply(BigDecimal.valueOf(TransactionType.getMultiplier(type)));
                BigDecimal existingTotal = totals.get(user);
                if (existingTotal == null) {
                    totals.put(user, signedShare);
                } else {
                    totals.put(user, existingTotal.add(signedShare));
                }
            }
        }
        
        return totals;
    }

    
    // Calculate totals by category for a date range
    public Map<String, BigDecimal> totalsByCategory(LocalDate start, LocalDate end) throws SQLException {
        List<Transaction> transactions = transactionRepository.findByTransactionDateBetween(start, end);
        Map<String, BigDecimal> totals = new HashMap<>();
        
        for (Transaction tx : transactions) {
            int type = tx.getType();
            BigDecimal signedAmount = tx.getAmount().multiply(BigDecimal.valueOf(TransactionType.getMultiplier(type)));
            List<Category> categories = transactionCategoryRepository.findCategoriesByTransactionId(tx.getId());
            
            if (categories.isEmpty()) {
                BigDecimal existingTotal = totals.get("Uncategorized");
                if (existingTotal == null) {
                    totals.put("Uncategorized", signedAmount);
                } else {
                    totals.put("Uncategorized", existingTotal.add(signedAmount));
                }
            } else {
                for (Category cat : categories) {
                    String categoryName = cat.getName();
                    BigDecimal existingTotal = totals.get(categoryName);
                    if (existingTotal == null) {
                        totals.put(categoryName, signedAmount);
                    } else {
                        totals.put(categoryName, existingTotal.add(signedAmount));
                    }
                }
            }
        }
        
        return totals;
    }

    // Get categories for a transaction
    public List<Category> getCategoriesForTransaction(Long transactionId) throws SQLException {
        return transactionCategoryRepository.findCategoriesByTransactionId(transactionId);
    }


    // Update splits for a transaction
    public Transaction updateSplits(Long transactionId, List<SplitRequest> splits) throws SQLException {
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (!transactionOpt.isPresent()) {
            throw new IllegalArgumentException("Transaction not found: " + transactionId);
        }
        Transaction transaction = transactionOpt.get();

        BigDecimal normalizedAmount = transaction.getAmount();
        BigDecimal total = BigDecimal.ZERO;
        for (SplitRequest split : splits) {
            total = total.add(split.amount());
        }

        if (total.compareTo(normalizedAmount) != 0) {
            throw new IllegalArgumentException("Split amount total must equal transaction amount");
        }

        // Delete existing splits
        splitRepository.deleteByTransactionId(transactionId);

        // Create new splits
        for (SplitRequest split : splits) {
            User user = userService.ensureUser(
                    Optional.ofNullable(split.cardholderName()).orElse(split.lastFourDigits()),
                    split.lastFourDigits(),
                    split.primaryCardholder());
            TransactionSplit transactionSplit = new TransactionSplit(
                    transactionId,
                    user.getId(),
                    split.amount());
            splitRepository.save(transactionSplit);
        }

        return transaction;
    }

    private void validateRequest(TransactionRequest request) {
        Objects.requireNonNull(request.transactionDate(), "Transaction date required");
        Objects.requireNonNull(request.vendor(), "Vendor required");
        Objects.requireNonNull(request.amount(), "Amount required");
        Objects.requireNonNull(request.cardLastFour(), "Card last four required");
        // Type is an int, so we just validate it's a valid value
        int type = request.type();
        if (type != TransactionType.PURCHASE && type != TransactionType.REFUND && type != TransactionType.PAYMENT) {
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        }
    }
// Represent a transaction request and uses a compact constructor to replace null lists with empty ones before the record is created
    public record TransactionRequest(
        LocalDate transactionDate,
        String vendor,
        BigDecimal amount,
        String cardLastFour,
        String cardholderName,
        int type,
        String description,
        List<String> categoryNames,
        List<SplitRequest> splits) {
    public TransactionRequest {
        if (categoryNames == null) categoryNames = new ArrayList<>();
        if (splits == null) splits = new ArrayList<>();
    }
    }

// Represent one portion of a shared transaction assigned to a specific cardholder
public record SplitRequest(
        String cardholderName,
        String lastFourDigits,
        boolean primaryCardholder,
        BigDecimal amount) {}

}
