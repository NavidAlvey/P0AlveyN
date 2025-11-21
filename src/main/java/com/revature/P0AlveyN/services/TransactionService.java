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

}
