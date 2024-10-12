package com.chainsys.tradingapp.service;

import java.sql.Timestamp;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chainsys.tradingapp.dao.StockDAO;
import com.chainsys.tradingapp.model.Stock;
import com.chainsys.tradingapp.model.Transaction;
import com.chainsys.tradingapp.model.User;

@Service
public class TransactionService {

    private final StockDAO stockDAO;
    private final EmailService emailService;

    @Autowired
    public TransactionService(StockDAO stockDAO, EmailService emailService) {
        this.stockDAO = stockDAO;
        this.emailService = emailService;
    }

    public Transaction handleTransaction(int userId, int stockId, int quantity, double price, String transactionType, User user) throws MessagingException {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setStockId(stockId);
        transaction.setShares(quantity);
        transaction.setPrice(price);
        transaction.setTransactionType(transactionType);
        transaction.setTimestamp(new Timestamp(System.currentTimeMillis()));

        if ("buy".equalsIgnoreCase(transactionType)) {
            int result = stockDAO.buyStock(userId, stockId, quantity, price);
            if (result == 1) {
                Stock stock = stockDAO.getStockDetailsById(stockId);
                emailService.sendOrderConfirmation(user.getEmail(), user.getName(), stock.getCompanyName(), "buy", quantity);
                transaction.setCompanyName(stock.getCompanyName());
            } else {
                throw new IllegalStateException("insufficientBalance");
            }
        } else if ("sell".equalsIgnoreCase(transactionType)) {
            int result = stockDAO.sellStock(userId, stockId, quantity, price);
            if (result == 1) {
                Stock stock = stockDAO.getStockDetailsById(stockId);
                emailService.sendOrderConfirmation(user.getEmail(), user.getName(), stock.getCompanyName(), "sell", quantity);
                transaction.setCompanyName(stock.getCompanyName());
            } else {
                throw new IllegalStateException("You don't have that stock to sell");
            }
        } else {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        return transaction;
    }
}
