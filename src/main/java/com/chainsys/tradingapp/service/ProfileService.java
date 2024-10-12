package com.chainsys.tradingapp.service;

import com.chainsys.tradingapp.dao.*;
import com.chainsys.tradingapp.dto.CategoryDTO;
import com.chainsys.tradingapp.dto.ProfileDTO;
import com.chainsys.tradingapp.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProfileService {

    private final UserDAO userOperations;
    private final PortfolioDAO portfolioOperations;
    private final TransactionDAO transactionOperations;
    private final NomineeDAO nomineeOperations;
    private final StockDAO stockOperations;

    @Autowired
    public ProfileService(UserDAO userOperations,
                          PortfolioDAO portfolioOperations,
                          TransactionDAO transactionOperations,
                          NomineeDAO nomineeOperations,
                          StockDAO stockOperations) {
        this.userOperations = userOperations;
        this.portfolioOperations = portfolioOperations;
        this.transactionOperations = transactionOperations;
        this.nomineeOperations = nomineeOperations;
        this.stockOperations = stockOperations;
    }

    public ProfileDTO getProfileData(String emailId) throws ClassNotFoundException, SQLException, JsonProcessingException {
        ProfileDTO profileDTO = new ProfileDTO();

        User updatedUser = userOperations.getUserByEmail(emailId);
        profileDTO.setUser(updatedUser);

        List<CategoryDTO> categoryQuantities = portfolioOperations.getCategoryQuantities(updatedUser.getId());
        List<Transaction> transList = transactionOperations.getLastFiveTransactionsByUserId(updatedUser.getId());
        List<Nominee> nominees = nomineeOperations.getAllNomineesByUserId(updatedUser.getId());
        List<Portfolio> portfoliolist = portfolioOperations.getPortfoliosByUserId(updatedUser.getId());
        List<CategoryDTO> sectorQuantities = portfolioOperations.getSectorCategoryQuantities(updatedUser.getId());

        profileDTO.setCategoryQuantities(categoryQuantities);
        profileDTO.setLastFiveTransactions(transList);
        profileDTO.setNominees(nominees);
        profileDTO.setPortfolios(portfoliolist);
        profileDTO.setSectorData(createSectorData(sectorQuantities));
        profileDTO.setSectorDataJson(convertToJson(profileDTO.getSectorData()));

        profileDTO.setSmallCapPercentage(calculatePercentage(categoryQuantities, "Small"));
        profileDTO.setMediumCapPercentage(calculatePercentage(categoryQuantities, "Medium"));
        profileDTO.setLargeCapPercentage(calculatePercentage(categoryQuantities, "Large"));

        calculateInvestments(profileDTO, portfoliolist);

        return profileDTO;
    }

    private Map<String, Integer> createSectorData(List<CategoryDTO> sectorQuantities) {
        Map<String, Integer> sectorData = new HashMap<>();
        for (CategoryDTO category : sectorQuantities) {
            sectorData.put(category.getCapCategory(), category.getTotalQuantity());
        }
        return sectorData;
    }

    private String convertToJson(Object data) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(data);
    }

    private int calculatePercentage(List<CategoryDTO> categoryQuantities, String capCategory) {
        for (CategoryDTO cq : categoryQuantities) {
            if (cq.getCapCategory().equals(capCategory)) {
                int totalQuantity = cq.getTotalQuantity();
                int userTotalQuantity = cq.getUserTotalQuantity();
                return  Math.round((float) totalQuantity / userTotalQuantity * 100);
            }
        }
        return 0;
    }

    private void calculateInvestments(ProfileDTO profileDTO, List<Portfolio> portfolios) {
        double totalInvested = 0.0;
        double totalCurrent = 0.0;

        for (Portfolio portfolio : portfolios) {
            totalInvested += portfolio.getBuyedPrice() * portfolio.getQuantity();
            double currentPrice = stockOperations.stockPriceById(portfolio.getStockId());
            totalCurrent += currentPrice * portfolio.getQuantity();
        }

        double pnlValue = totalCurrent - totalInvested;
        double pnlPercentage = (totalInvested > 0) ? (pnlValue / totalInvested) * 100 : 0;

        profileDTO.setTotalInvested(totalInvested);
        profileDTO.setTotalCurrent(totalCurrent);
        profileDTO.setPnlValue(pnlValue);
        profileDTO.setPnlPercentage(pnlPercentage);
        profileDTO.setPnlClass((pnlValue >= 0) ? "positive" : "negative");
    }
}
