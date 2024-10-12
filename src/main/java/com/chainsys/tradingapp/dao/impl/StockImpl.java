package com.chainsys.tradingapp.dao.impl;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.chainsys.tradingapp.dao.StockDAO;
import com.chainsys.tradingapp.exception.StockNotFoundException;
import com.chainsys.tradingapp.model.Stock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Repository
public class StockImpl implements StockDAO {
    private static final Logger logger = LoggerFactory.getLogger(StockImpl.class);
    public static final String STOCK_ID_NOT_FOUND_MESSAGE = "Stock not found for ID : ";
    private static final String CALL_BUY_STOCK_PROCEDURE = "{CALL buyStockProcedure(?, ?, ?, ?, ?)}";
    private static final String CALL_SELL_STOCK_PROCEDURE = "{CALL sellStockProcedure(?, ?, ?, ?, ?, ?)}";

  
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public StockImpl(JdbcTemplate jdbcTemplate) {
    	this.jdbcTemplate=jdbcTemplate;
    	
    }
    

    private RowMapper<Stock> stockRowMapper = (rs, rowNum) -> new Stock(
            rs.getInt("stock_id"),
            rs.getString("symbol"),
            rs.getString("company_name"),
            rs.getDouble("current_stock_price"),
            rs.getString("cap_category")
    );

    @Override
    public List<Stock> selectAllStocks() {
        String sql = "SELECT stock_id, symbol, company_name, current_stock_price, cap_category FROM stocks";
        return jdbcTemplate.query(sql, stockRowMapper);
    }
    

    @Override
    public Stock getStockDetailsById(int id) {
        String sql = "SELECT stock_id, symbol, company_name, current_stock_price, cap_category FROM stocks WHERE stock_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, stockRowMapper, id);
        } catch (Exception e) {
            logger.error("Error retrieving stock details for ID: {}", id);
            throw new StockNotFoundException(STOCK_ID_NOT_FOUND_MESSAGE + id );
        }
    }
    @Override
    public int buyStock(int userId, int stockId, int quantity, double price) {
        Integer result = jdbcTemplate.execute(CALL_BUY_STOCK_PROCEDURE, (CallableStatementCallback<Integer>) cs -> {
            cs.setInt(1, userId);
            cs.setInt(2, stockId);
            cs.setInt(3, quantity);
            cs.setDouble(4, price);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        });
        return result != null ? result : -1;
    }

    @Override
    public int sellStock(int userId, int stockId, int quantity, double price) {
        Integer result = jdbcTemplate.execute(CALL_SELL_STOCK_PROCEDURE, (CallableStatementCallback<Integer>) cs -> {
            cs.setInt(1, userId);
            cs.setInt(2, stockId);
            cs.setInt(3, quantity);
            cs.setDouble(4, price);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.registerOutParameter(6, Types.DECIMAL);
            cs.execute();
            return cs.getInt(5);
        });
        return result != null ? result : -1;
    }

    @Override
    public double stockPriceById(int stockId) {
        String sql = "SELECT current_stock_price FROM stocks WHERE stock_id = ?";
        try {
            Double stockPrice = jdbcTemplate.queryForObject(sql, Double.class, stockId);
            if (stockPrice == null) {
                throw new StockNotFoundException(STOCK_ID_NOT_FOUND_MESSAGE + stockId );
            }
            return stockPrice;
        } catch (Exception e) {
            logger.error("Error retrieving stock price for ID: {}", stockId, e);
            throw new StockNotFoundException(STOCK_ID_NOT_FOUND_MESSAGE + stockId );
        }
    }
    
    @Override
    public List<Stock> filterAndSearchStocks(String filterCategory, String searchQuery) {
        // Start building the SQL query
        StringBuilder sql = new StringBuilder("SELECT stock_id, symbol, company_name, current_stock_price, cap_category FROM stocks WHERE 1=1");
        
        List<Object> params = new ArrayList<>();
        
        // Add filter category if it is not "All"
        if (filterCategory != null && !filterCategory.equalsIgnoreCase("All") && !filterCategory.isEmpty()) {
            sql.append(" AND cap_category = ?");
            params.add(filterCategory);
        }
        
        // Add search query if provided
        if (searchQuery != null && !searchQuery.isEmpty()) {
            sql.append(" AND (symbol LIKE ? OR company_name LIKE ?)");
            String likeQuery = "%" + searchQuery + "%";
            params.add(likeQuery);
            params.add(likeQuery);
        }

        // Convert List<Object> to Object[] for JdbcTemplate
        Object[] paramArray = params.toArray();

        return jdbcTemplate.query(sql.toString(), stockRowMapper, paramArray);
    }


 
  
}
