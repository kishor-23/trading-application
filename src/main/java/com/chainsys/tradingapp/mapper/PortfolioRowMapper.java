package com.chainsys.tradingapp.mapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.chainsys.tradingapp.model.Portfolio;

public class PortfolioRowMapper implements RowMapper<Portfolio> {
    @Override
    public Portfolio mapRow(ResultSet resultSet, int i) throws SQLException {
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(resultSet.getInt("portfolio_id"));
        portfolio.setUserId(resultSet.getInt("user_id"));
        portfolio.setSymbol(resultSet.getString("symbol"));
        portfolio.setCompany(resultSet.getString("company"));
        portfolio.setStockId(resultSet.getInt("stock_id"));
        portfolio.setQuantity(resultSet.getInt("quantity"));
        portfolio.setBuyedPrice(resultSet.getDouble("avg_cost"));
        portfolio.setTotal(resultSet.getDouble("total_cost"));
        portfolio.setStopLoss(resultSet.getDouble("stop_loss_price"));
        portfolio.setStopGain(resultSet.getDouble("stop_gain_price"));
        return portfolio;
    }
}
