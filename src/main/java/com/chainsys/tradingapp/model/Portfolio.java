package com.chainsys.tradingapp.model;


public class Portfolio {
    private int portfolioId;
    private int userId;
    private int stockId;
    private int quantity;
    private double stopLoss;
    private double stopGain;
    public int getPortfolioId() {
		return portfolioId;
	}
	public double getStopLoss() {
		return stopLoss;
	}
	public void setStopLoss(double stopLoss) {
		this.stopLoss = stopLoss;
	}
	public double getStopGain() {
		return stopGain;
	}
	public void setStopGain(double stopGain) {
		this.stopGain = stopGain;
	}
	public void setPortfolioId(int portfolioId) {
		this.portfolioId = portfolioId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getStockId() {
		return stockId;
	}
	public void setStockId(int stockId) {
		this.stockId = stockId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public double getBuyedPrice() {
		return buyedPrice;
	}
	public void setBuyedPrice(double buyedPrice) {
		this.buyedPrice = buyedPrice;
	}
	private double total;
    private String symbol;
	private String company;
	private double buyedPrice;


}
