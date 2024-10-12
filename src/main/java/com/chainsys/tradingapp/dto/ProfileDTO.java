package com.chainsys.tradingapp.dto;


import java.util.List;
import java.util.Map;

import com.chainsys.tradingapp.model.Nominee;
import com.chainsys.tradingapp.model.Portfolio;
import com.chainsys.tradingapp.model.Transaction;
import com.chainsys.tradingapp.model.User;

public class ProfileDTO extends BasePortfolioDTO {
    private User user;
    private List<CategoryDTO> categoryQuantities;
    private List<Transaction> lastFiveTransactions;
    private List<Nominee> nominees;
    private List<Portfolio> portfolios;
    private Map<String, Integer> sectorData;
    private String sectorDataJson;
    private double pnlValue;
    private double pnlPercentage;
    // No-argument constructor
    public ProfileDTO() {
        super();
       
    }

    public double getPnlValue() {
		return pnlValue;
	}

	public void setPnlValue(double pnlValue) {
		this.pnlValue = pnlValue;
	}

	public double getPnlPercentage() {
		return pnlPercentage;
	}

	public void setPnlPercentage(double pnlPercentage) {
		this.pnlPercentage = pnlPercentage;
	}

	// Parameterized constructor
    public ProfileDTO(User user, List<CategoryDTO> categoryQuantities, List<Transaction> lastFiveTransactions,
                      List<Nominee> nominees, List<Portfolio> portfolios, Map<String, Integer> sectorData,
                      int smallCapPercentage, int mediumCapPercentage, int largeCapPercentage, 
                      double totalInvested, double totalCurrent, double pnlValue, double pnlPercentage,
                      String pnlClass, String sectorDataJson) {
        super(totalInvested, totalCurrent, pnlClass, smallCapPercentage, mediumCapPercentage, largeCapPercentage);
        this.user = user;
        this.categoryQuantities = categoryQuantities;
        this.lastFiveTransactions = lastFiveTransactions;
        this.nominees = nominees;
        this.portfolios = portfolios;
        this.sectorData = sectorData;
        this.sectorDataJson = sectorDataJson;
        this.pnlValue = pnlValue;
        this.pnlPercentage = pnlPercentage;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CategoryDTO> getCategoryQuantities() {
        return categoryQuantities;
    }

    public void setCategoryQuantities(List<CategoryDTO> categoryQuantities) {
        this.categoryQuantities = categoryQuantities;
    }

    public List<Transaction> getLastFiveTransactions() {
        return lastFiveTransactions;
    }

    public void setLastFiveTransactions(List<Transaction> lastFiveTransactions) {
        this.lastFiveTransactions = lastFiveTransactions;
    }

    public List<Nominee> getNominees() {
        return nominees;
    }

    public void setNominees(List<Nominee> nominees) {
        this.nominees = nominees;
    }

    public List<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public Map<String, Integer> getSectorData() {
        return sectorData;
    }

    public void setSectorData(Map<String, Integer> sectorData) {
        this.sectorData = sectorData;
    }

    public String getSectorDataJson() {
        return sectorDataJson;
    }

    public void setSectorDataJson(String sectorDataJson) {
        this.sectorDataJson = sectorDataJson;
    }
}
