package com.chainsys.tradingapp.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.chainsys.tradingapp.dao.TransactionDAO;
import com.chainsys.tradingapp.mapper.TransactionRowMapper;
import com.chainsys.tradingapp.model.Transaction;
import com.chainsys.tradingapp.dto.TopStockDTO;

@Repository
public class TransactionImpl implements TransactionDAO {

    private static final String COMMON_STOCK_SELECT = 
            "SELECT s.stock_id, s.symbol, s.company_name, s.current_stock_price, COUNT(t.transaction_id) AS transaction_count ";

    private static final String GROUP_BY_CLAUSE =
            "GROUP BY s.stock_id, s.symbol, s.company_name, s.current_stock_price ";
    private static final String ORDER_BY_AND_LIMIT =
            "ORDER BY transaction_count DESC LIMIT 5";

    private static final String GET_TRANSACTIONS_BY_USER_ID_QUERY =
            "SELECT t.transaction_id, t.user_id, t.stock_id, t.shares, t.price, t.transaction_type,t.profit_loss, t.timestamp, s.symbol, s.company_name " +
            "FROM transactions t JOIN stocks s ON t.stock_id = s.stock_id " +
            "WHERE t.user_id = ?";

    private static final String GET_LAST_FIVE_TRANSACTIONS_BY_USER_ID_QUERY =
            "SELECT t.transaction_id, t.user_id, t.stock_id, t.shares, t.price, t.transaction_type,t.profit_loss, t.timestamp, s.symbol, s.company_name " +
            "FROM transactions t JOIN stocks s ON t.stock_id = s.stock_id " +
            "WHERE t.user_id = ? " +
            "ORDER BY t.timestamp DESC LIMIT 5";

    private static final String GET_TOP_BUY_STOCKS_QUERY =
            COMMON_STOCK_SELECT +
            "FROM transactions t " +
            "JOIN stocks s ON t.stock_id = s.stock_id " +
            "WHERE t.transaction_type = 'buy' " +
            GROUP_BY_CLAUSE +
            ORDER_BY_AND_LIMIT;

    private static final String GET_TOP_SELL_STOCKS_QUERY =
            COMMON_STOCK_SELECT +
            "FROM transactions t " +
            "JOIN stocks s ON t.stock_id = s.stock_id " +
            "WHERE t.transaction_type = 'sell' " +
            GROUP_BY_CLAUSE +
             ORDER_BY_AND_LIMIT;

    private static final String GET_MOST_TRADED_STOCKS_QUERY =
            COMMON_STOCK_SELECT +
            "FROM transactions t " +
            "JOIN stocks s ON t.stock_id = s.stock_id " +
            GROUP_BY_CLAUSE +
            ORDER_BY_AND_LIMIT;

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transaction> getTransactionsByUserId(int userId) {
        return jdbcTemplate.query(GET_TRANSACTIONS_BY_USER_ID_QUERY, new TransactionRowMapper(), userId);
    }

    @Override
    public List<Transaction> getLastFiveTransactionsByUserId(int userId) {
        return jdbcTemplate.query(GET_LAST_FIVE_TRANSACTIONS_BY_USER_ID_QUERY, new TransactionRowMapper(), userId);
    }

    @Override
    public List<TopStockDTO> getTopBuyStocks() {
        return jdbcTemplate.query(GET_TOP_BUY_STOCKS_QUERY, new TopStockRowMapper());
    }

    @Override
    public List<TopStockDTO> getTopSellStocks() {
        return jdbcTemplate.query(GET_TOP_SELL_STOCKS_QUERY, new TopStockRowMapper());
    }

    @Override
    public List<TopStockDTO> getMostTradedStocks() {
        return jdbcTemplate.query(GET_MOST_TRADED_STOCKS_QUERY, new TopStockRowMapper());
    }

    private static class TopStockRowMapper implements RowMapper<TopStockDTO> {
        @Override
        public TopStockDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            TopStockDTO topStock = new TopStockDTO();
            topStock.setStockId(rs.getInt("stock_id"));
            topStock.setSymbol(rs.getString("symbol"));
            topStock.setCompanyName(rs.getString("company_name"));
            topStock.setTransactionCount(rs.getInt("transaction_count"));
            topStock.setPrice(rs.getDouble("current_stock_price"));
            return topStock;
        }
    }
}
