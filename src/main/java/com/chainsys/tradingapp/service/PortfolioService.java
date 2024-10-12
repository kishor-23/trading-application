package com.chainsys.tradingapp.service;

import com.chainsys.tradingapp.dao.PortfolioDAO;
import com.chainsys.tradingapp.dao.StockDAO;
import com.chainsys.tradingapp.dto.CategoryDTO;
import com.chainsys.tradingapp.dto.PortfolioDTO;
import com.chainsys.tradingapp.model.Portfolio;
import com.chainsys.tradingapp.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioService {

    private final PortfolioDAO portfolioOperations;
    private final StockDAO stockOperations;

    @Autowired
    public PortfolioService(PortfolioDAO portfolioOperations, StockDAO stockOperations) {
        this.portfolioOperations = portfolioOperations;
        this.stockOperations = stockOperations;
    }

    public PortfolioDTO getPortfolioData(User user) {
        List<Portfolio> portfolios = portfolioOperations.getPortfoliosByUserId(user.getId());

        double totalInvested = calculateTotalInvested(portfolios);
        double totalCurrent = calculateTotalCurrent(portfolios);
        Map<Integer, String> formattedStockPrices = formatStockPrices(portfolios);
        Map<Integer, Boolean> isPriceUpMap = calculatePriceDirection(portfolios);

        double pnlValue = totalCurrent - totalInvested;
        double pnlPercentage = calculatePnlPercentage(totalInvested, pnlValue);

        List<CategoryDTO> categoryQuantities = portfolioOperations.getCategoryQuantities(user.getId());
        Map<String, Integer> capCategoryPercentages = calculateCapCategoryPercentages(categoryQuantities);

        List<CategoryDTO> sectorQuantities = portfolioOperations.getSectorCategoryQuantities(user.getId());
        Map<String, Integer> sectorData = createSectorData(sectorQuantities);

        return new PortfolioDTO(
            totalInvested,
            totalCurrent,
            formatDecimal(pnlValue),
            formatDecimal(pnlPercentage),
            (pnlValue >= 0) ? "positive" : "negative",
            capCategoryPercentages.getOrDefault("Small", 0),
            capCategoryPercentages.getOrDefault("Medium", 0),
            capCategoryPercentages.getOrDefault("Large", 0),
            formatSectorLabels(sectorData),
            formatSectorValues(sectorData),
            portfolios,
            formattedStockPrices,
            isPriceUpMap
        );
    }

    private double calculateTotalInvested(List<Portfolio> portfolios) {
        return portfolios.stream()
                .mapToDouble(portfolio -> portfolio.getBuyedPrice() * portfolio.getQuantity())
                .sum();
    }

    private double calculateTotalCurrent(List<Portfolio> portfolios) {
        return portfolios.stream()
                .mapToDouble(portfolio -> stockOperations.stockPriceById(portfolio.getStockId()) * portfolio.getQuantity())
                .sum();
    }

    private Map<Integer, String> formatStockPrices(List<Portfolio> portfolios) {
        return portfolios.stream()
                .collect(Collectors.toMap(
                    Portfolio::getStockId,
                    portfolio -> String.format("%.2f", stockOperations.stockPriceById(portfolio.getStockId()))
                ));
    }

    private Map<Integer, Boolean> calculatePriceDirection(List<Portfolio> portfolios) {
        return portfolios.stream()
                .collect(Collectors.toMap(
                    Portfolio::getStockId,
                    portfolio -> stockOperations.stockPriceById(portfolio.getStockId()) > portfolio.getBuyedPrice()
                ));
    }

    private double calculatePnlPercentage(double totalInvested, double pnlValue) {
        return (totalInvested > 0) ? (pnlValue / totalInvested) * 100 : 0;
    }

    private Map<String, Integer> calculateCapCategoryPercentages(List<CategoryDTO> categoryQuantities) {
        Map<String, Integer> percentages = new HashMap<>();
        categoryQuantities.forEach(cq -> {
            int totalQuantity = cq.getTotalQuantity();
            int userTotalQuantity = cq.getUserTotalQuantity();
            int percentage = Math.round((float) totalQuantity / userTotalQuantity * 100);
            percentages.put(cq.getCapCategory(), percentage);
        });
        return percentages;
    }

    private Map<String, Integer> createSectorData(List<CategoryDTO> sectorQuantities) {
        return sectorQuantities.stream()
                .collect(Collectors.toMap(
                    CategoryDTO::getCapCategory,
                    CategoryDTO::getTotalQuantity
                ));
    }

    private String formatDecimal(double value) {
        return String.format("%.2f", value);
    }

    private String formatSectorLabels(Map<String, Integer> sectorData) {
        return sectorData.keySet().stream()
                .map(key -> "\"" + key + "\"")
                .collect(Collectors.joining(","));
    }

    private String formatSectorValues(Map<String, Integer> sectorData) {
        return sectorData.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    public List<Portfolio> getStockByIdAndUser(String category, int userId) {
        return portfolioOperations.getStockDetailsByCategory(category, userId);
    }

    public void updateStopLossAndStopGain(int stockId, BigDecimal stopLossPrice, BigDecimal stopGainPrice) {
        portfolioOperations.updateStopLossGain(stockId, stopLossPrice, stopGainPrice);
    }
}
