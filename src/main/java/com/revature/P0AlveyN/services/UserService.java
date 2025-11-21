package com.revature.P0AlveyN.services;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.revature.P0AlveyN.entity.User;
import com.revature.P0AlveyN.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Ensure a user exists, create or update if needed
    public User ensureUser(String name, String lastFourDigits, boolean primaryCardholder) throws SQLException {
        Optional<User> existing = userRepository.findByLastFourDigits(lastFourDigits);
        
        if (existing.isPresent()) {
            User user = existing.get();
            user.setName(name);
            user.setPrimaryCardholder(primaryCardholder);
            userRepository.update(user);
            return user;
        } else {
            User newUser = new User(name, lastFourDigits, primaryCardholder);
            return userRepository.save(newUser);
        }
    }

}
