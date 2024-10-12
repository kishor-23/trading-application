package com.chainsys.tradingapp.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashing {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private PasswordHashing() {
        // Private constructor to prevent instantiation
    }

    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }
}
