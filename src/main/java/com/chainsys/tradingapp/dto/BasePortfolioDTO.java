package com.chainsys.tradingapp.dto;

public abstract class BasePortfolioDTO {
    private double totalInvested;
    private double totalCurrent;
    private String pnlClass;
    private int smallCapPercentage;
    private int mediumCapPercentage;
    private int largeCapPercentage;
    // No-argument constructor
    protected BasePortfolioDTO() {
       
    }
 protected  BasePortfolioDTO(double totalInvested, double totalCurrent, String pnlClass, int smallCapPercentage,
			int mediumCapPercentage, int largeCapPercentage) {
		this.totalInvested = totalInvested;
		this.totalCurrent = totalCurrent;
		this.pnlClass = pnlClass;
		this.smallCapPercentage = smallCapPercentage;
		this.mediumCapPercentage = mediumCapPercentage;
		this.largeCapPercentage = largeCapPercentage;
	}

	// Getters and Setters
    public double getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(double totalInvested) {
        this.totalInvested = totalInvested;
    }

    public double getTotalCurrent() {
        return totalCurrent;
    }

    public void setTotalCurrent(double totalCurrent) {
        this.totalCurrent = totalCurrent;
    }

    public String getPnlClass() {
        return pnlClass;
    }

    public void setPnlClass(String pnlClass) {
        this.pnlClass = pnlClass;
    }

    public int getSmallCapPercentage() {
        return smallCapPercentage;
    }

    public void setSmallCapPercentage(int smallCapPercentage) {
        this.smallCapPercentage = smallCapPercentage;
    }

    public int getMediumCapPercentage() {
        return mediumCapPercentage;
    }

    public void setMediumCapPercentage(int mediumCapPercentage) {
        this.mediumCapPercentage = mediumCapPercentage;
    }

    public int getLargeCapPercentage() {
        return largeCapPercentage;
    }

    public void setLargeCapPercentage(int largeCapPercentage) {
        this.largeCapPercentage = largeCapPercentage;
    }
}
