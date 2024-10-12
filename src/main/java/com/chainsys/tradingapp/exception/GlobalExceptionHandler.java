package com.chainsys.tradingapp.exception;

import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String ERROR_VIEW = "error";


    @ExceptionHandler(PanCardDuplicateException.class)
    public String handlePanCardDuplicateException(PanCardDuplicateException ex, Model model) {
        model.addAttribute("msg", "Registration failed. User with this PAN card already exists.");
        return "register"; 
    }

    @ExceptionHandler(StockNotFoundException.class)
    public String handleStockNotFoundException(StockNotFoundException ex, Model model) {
        model.addAttribute("message", "Stock not found.");
        return ERROR_VIEW;  
    }

    @ExceptionHandler(SQLException.class)
    public String handleSQLException(SQLException ex, Model model) {
        model.addAttribute("msg", "Database error occurred. Please try again.");
        return ERROR_VIEW;  
    }

    @ExceptionHandler(MessagingException.class)
    public String handleMessagingException(MessagingException ex, Model model) {
        model.addAttribute("msg", "Failed to send email. Please try again.");
        return ERROR_VIEW;  
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException ex, Model model) {
        model.addAttribute("msg", "I/O error occurred. Please try again.");
        return ERROR_VIEW;  
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        model.addAttribute("msg", "An unexpected error occurred. Please try again.");
        return ERROR_VIEW;  
    }
}
