package com.chainsys.tradingapp.controller;

import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chainsys.tradingapp.dao.TransactionDAO;
import com.chainsys.tradingapp.dto.TopStockDTO;
import com.chainsys.tradingapp.model.Transaction;
import com.chainsys.tradingapp.model.User;
import com.chainsys.tradingapp.service.TransactionService;

@Controller
public class TransactionController {

    private final TransactionDAO transactionDAO;
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionDAO transactionDAO, TransactionService transactionService) {
        this.transactionDAO = transactionDAO;
        this.transactionService = transactionService;
    }

    private static final String ERROR_MESSAGE = "error";
    private static final String ERROR_FILE = "fail";

    @GetMapping("/ordersuccess")
    public String orderSuccess(Model model) {
        return "ordersuccess";
    }

    @PostMapping("/StockTransaction")
    public String handleTransaction(@RequestParam("transactionType") String transactionType,
                                    @RequestParam("userid") int userId,
                                    @RequestParam("stockId") int stockId,
                                    @RequestParam("quantity") int quantity,
                                    @RequestParam("price") double price,
                                    HttpSession session,
                                    Model model) throws MessagingException {
        User user = (User) session.getAttribute("user");

        try {
            Transaction transaction = transactionService.handleTransaction(userId, stockId, quantity, price, transactionType, user);
            model.addAttribute("transaction", transaction);
            String boxClass = "box";
            if ("sell".equalsIgnoreCase(transaction.getTransactionType())) {
                boxClass += " box-sell";
            } else {
                boxClass += " box-buy";
            }
            model.addAttribute("boxClass", boxClass);
            return "ordersuccess";
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute(ERROR_MESSAGE, e.getMessage());
            return ERROR_FILE;
        }
    }

    @PostMapping("/transactions")
    public String getTransactions(@RequestParam("userId") Integer userId, Model model) {
        List<Transaction> transactions = null;

        if (userId != null) {
            transactions = transactionDAO.getTransactionsByUserId(userId);
        }

        model.addAttribute("transactions", transactions);
        return "transactions";
    }
 
    @GetMapping("/trendingstocks")
    public String trending(Model model) {
    	  List<TopStockDTO> topBuyStocks = transactionDAO.getTopBuyStocks();
          List<TopStockDTO> topSellStocks = transactionDAO.getTopSellStocks();
          List<TopStockDTO> mostTradedStocks = transactionDAO.getMostTradedStocks();

          model.addAttribute("topBuyStocks", topBuyStocks);
          model.addAttribute("topSellStocks", topSellStocks);
          model.addAttribute("mostTradedStocks", mostTradedStocks);

    	return "trendingstocks";
    }
    
}
