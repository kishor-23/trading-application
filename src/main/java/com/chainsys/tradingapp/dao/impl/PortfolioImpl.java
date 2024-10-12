package com.chainsys.tradingapp.dao.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chainsys.tradingapp.dao.PortfolioDAO;
import com.chainsys.tradingapp.dto.CategoryDTO;
import com.chainsys.tradingapp.model.Portfolio;
import com.chainsys.tradingapp.mapper.CategoryRowMapper;
import com.chainsys.tradingapp.mapper.PortfolioRowMapper;

@Repository
public class PortfolioImpl implements PortfolioDAO {

    private final JdbcTemplate jdbcTemplate;

    // SQL Query Constants
    private static final String GET_PORTFOLIO_BY_ID_SQL =
        "SELECT portfolio_id, user_id, stock_id, quantity, buyed_price, stop_loss_price, stop_gain_price FROM portfolio WHERE portfolio_id = ?";
    private static final String GET_PORTFOLIOS_BY_USER_ID_SQL =
        "SELECT p.portfolio_id, p.user_id, p.stock_id, s.company_name as company, s.symbol as symbol, p.quantity, p.avg_cost, p.total_cost,stop_loss_price, stop_gain_price " +
        "FROM portfolio p JOIN stocks s ON p.stock_id = s.stock_id WHERE p.user_id = ? ORDER BY total_cost DESC";
    private static final String GET_CATEGORY_QUANTITIES_SQL =
        "SELECT s.cap_category, SUM(p.quantity) AS total_quantity, " +
        "(SELECT SUM(quantity) FROM portfolio WHERE user_id = ?) AS user_total_quantity " +
        "FROM portfolio p JOIN stocks s ON p.stock_id = s.stock_id " +
        "WHERE p.user_id = ? GROUP BY s.cap_category";
    private static final String GET_SECTOR_CATEGORY_QUANTITIES_SQL =
        "SELECT s.sector as cap_category, SUM(p.quantity) AS total_quantity, " +
        "(SELECT SUM(quantity) FROM portfolio WHERE user_id = ?) AS user_total_quantity " +
        "FROM portfolio p JOIN stocks s ON p.stock_id = s.stock_id " +
        "WHERE p.user_id = ? GROUP BY s.sector";
    private static final String GET_STOCK_DETAILS_BY_CATEGORY_SQL =
        "SELECT p.portfolio_id, p.user_id, p.stock_id, s.company_name AS company, s.symbol AS symbol, p.quantity, p.avg_cost, p.total_cost ,p.stop_loss_price, p.stop_gain_price " +
        "FROM portfolio p JOIN stocks s ON p.stock_id = s.stock_id WHERE p.user_id = ? AND s.cap_category = ? ORDER BY p.total_cost DESC";
    private static final String UPDATE_STOP_LOSS_GAIN_SQL =
        "UPDATE portfolio SET stop_loss_price = ?, stop_gain_price = ? WHERE stock_id = ?";

    @Autowired
    public PortfolioImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Portfolio getPortfolioById(int portfolioId) {
            return jdbcTemplate.queryForObject(GET_PORTFOLIO_BY_ID_SQL, new PortfolioRowMapper(), portfolioId);
      }

    @Override
    public List<Portfolio> getPortfoliosByUserId(int userId) {
            return jdbcTemplate.query(GET_PORTFOLIOS_BY_USER_ID_SQL, new PortfolioRowMapper(), userId);
     
    }

    @Override
    public List<CategoryDTO> getCategoryQuantities(int userId) {
            return jdbcTemplate.query(GET_CATEGORY_QUANTITIES_SQL, new CategoryRowMapper(), userId, userId);
      
    }

    @Override
    public List<CategoryDTO> getSectorCategoryQuantities(int userId) {
            return jdbcTemplate.query(GET_SECTOR_CATEGORY_QUANTITIES_SQL, new CategoryRowMapper(), userId, userId);
      
    }

    @Override
    public List<Portfolio> getStockDetailsByCategory(String category, int userId) {
            return jdbcTemplate.query(GET_STOCK_DETAILS_BY_CATEGORY_SQL, new PortfolioRowMapper(), userId, category);
       
    }

    @Override
    public void updateStopLossGain(int stockId, BigDecimal stopLossPrice, BigDecimal stopGainPrice) {
            jdbcTemplate.update(UPDATE_STOP_LOSS_GAIN_SQL, stopLossPrice, stopGainPrice, stockId);
       
    }
}
