package com.chainsys.tradingapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.chainsys.tradingapp.dao.StockDAO;
import com.chainsys.tradingapp.model.Stock;

@Service
public class StockService {
    private final StockDAO stockOperations;

    @Autowired
    public StockService(StockDAO stockOperations) {
        this.stockOperations = stockOperations;
    }

    public Stock getStockDetailsById(Integer stockId) {
        return stockOperations.getStockDetailsById(stockId);
    }

    public void viewStocks(int page, int itemsPerPage, String searchQuery, String filterCategory, String sortField, String sortOrder, Model model) {
        // Fetch filtered and searched stocks directly from the database
        List<Stock> filteredStocks = stockOperations.filterAndSearchStocks(filterCategory, searchQuery);

        // Sort the filtered list based on the provided sort field and order
        List<Stock> sortedStocks = filteredStocks.stream()
            .sorted((s1, s2) -> "desc".equalsIgnoreCase(sortOrder) ?
                Double.compare(s2.getCurrentStockPrice(), s1.getCurrentStockPrice()) :
                Double.compare(s1.getCurrentStockPrice(), s2.getCurrentStockPrice()))
            .collect(Collectors.toList());

        // Pagination
        int totalItems = sortedStocks.size();
        int totalPages = (int) Math.ceil((double) totalItems / itemsPerPage);
        int startItemIndex = (page - 1) * itemsPerPage;
        int endItemIndex = Math.min(startItemIndex + itemsPerPage, totalItems);
        List<Stock> paginatedStocks = sortedStocks.subList(startItemIndex, endItemIndex);

        // Set model attributes
        model.addAttribute("listStocks", paginatedStocks);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("itemsPerPage", itemsPerPage);
        model.addAttribute("filterCategory", filterCategory);
        model.addAttribute("searchQuery", searchQuery != null ? searchQuery : "");
        model.addAttribute("sortField", sortField != null ? sortField : "currentStockPrice");
        model.addAttribute("sortOrder", sortOrder);
    }
}
