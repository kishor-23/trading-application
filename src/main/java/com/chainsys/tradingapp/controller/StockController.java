package com.chainsys.tradingapp.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chainsys.tradingapp.model.Stock;
import com.chainsys.tradingapp.model.User;
import com.chainsys.tradingapp.service.StockService;


@Controller
public class StockController {
    private final StockService stockService;
    private static final String SYMBOL = "symbol";
    private static final String STOCK = "stock";


    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;        
    }
    @GetMapping("/stockDetails")
    public String getStockDetail(@RequestParam("stockId") Integer stockId, Model model) {
        Stock stock = stockService.getStockDetailsById(stockId);
        String symbol = stock.getSymbol();
        model.addAttribute(SYMBOL, symbol);
        model.addAttribute(STOCK, stock);
        return "viewstocks";
    }

    @GetMapping("/stocks")
    public String viewStocks(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int itemsPerPage,
                             @RequestParam(required = false) String searchQuery,
                             @RequestParam(defaultValue = "All") String filterCategory,
                             @RequestParam(required = false) String sortField,
                             @RequestParam(defaultValue = "asc") String sortOrder,
                             Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
            model.addAttribute("user", user);
            stockService.viewStocks(page, itemsPerPage, searchQuery, filterCategory, sortField, sortOrder, model);
            return STOCK;
       
    }
    
    @GetMapping("/realtimestockdata")
    public String realtime() {
        return "realtimestockdata";
    }
    
    
}
