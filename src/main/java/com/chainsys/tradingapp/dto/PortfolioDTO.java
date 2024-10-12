package com.chainsys.tradingapp.dto;

import java.util.*;

import com.chainsys.tradingapp.model.Portfolio;

public class PortfolioDTO extends BasePortfolioDTO {
    private String pnlValue;
    private String pnlPercentage;
    private String sectorLabels;
    private String sectorValues;
    private List<Portfolio> portfolioList;
    private Map<Integer, String> formattedStockPrices;
    private Map<Integer, Boolean> isPriceUpMap;

    // No-argument constructor
    public PortfolioDTO() {
        super();
    
    }

    // Parameterized constructor
    public PortfolioDTO(double totalInvested, double totalCurrent, String pnlValue, String pnlPercentage,
                        String pnlClass, int smallCapPercentage, int mediumCapPercentage, int largeCapPercentage,
                        String sectorLabels, String sectorValues, List<Portfolio> portfolioList,
                        Map<Integer, String> formattedStockPrices, Map<Integer, Boolean> isPriceUpMap) {
        super(totalInvested, totalCurrent, pnlClass, smallCapPercentage, mediumCapPercentage, largeCapPercentage);
        this.pnlValue = pnlValue;
        this.pnlPercentage = pnlPercentage;
        this.sectorLabels = sectorLabels;
        this.sectorValues = sectorValues;
        this.portfolioList = portfolioList;
        this.formattedStockPrices = formattedStockPrices;
        this.isPriceUpMap = isPriceUpMap;
    }

    // Getters and Setters
    public String getPnlValue() {
        return pnlValue;
    }

    public void setPnlValue(String pnlValue) {
        this.pnlValue = pnlValue;
    }

    public String getPnlPercentage() {
        return pnlPercentage;
    }

    public void setPnlPercentage(String pnlPercentage) {
        this.pnlPercentage = pnlPercentage;
    }

    public String getSectorLabels() {
        return sectorLabels;
    }

    public void setSectorLabels(String sectorLabels) {
        this.sectorLabels = sectorLabels;
    }

    public String getSectorValues() {
        return sectorValues;
    }

    public void setSectorValues(String sectorValues) {
        this.sectorValues = sectorValues;
    }

    public List<Portfolio> getPortfolioList() {
        return portfolioList;
    }

    public void setPortfolioList(List<Portfolio> portfolioList) {
        this.portfolioList = portfolioList;
    }

    public Map<Integer, String> getFormattedStockPrices() {
        return formattedStockPrices;
    }

    public void setFormattedStockPrices(Map<Integer, String> formattedStockPrices) {
        this.formattedStockPrices = formattedStockPrices;
    }

    public Map<Integer, Boolean> getIsPriceUpMap() {
        return isPriceUpMap;
    }

    public void setIsPriceUpMap(Map<Integer, Boolean> isPriceUpMap) {
        this.isPriceUpMap = isPriceUpMap;
    }
}
