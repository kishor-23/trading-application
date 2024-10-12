package com.chainsys.tradingapp.dto;

public class TopStockDTO extends BaseStockDTO {
    private int transactionCount;
    private Double price;

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
