package com.chainsys.tradingapp.dao;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.chainsys.tradingapp.dto.CategoryDTO;
import com.chainsys.tradingapp.model.Portfolio;
@Repository
public interface PortfolioDAO {
	 
  public  Portfolio getPortfolioById(int portfolioId);
  public  List<Portfolio> getPortfoliosByUserId(int userId);
  public List<CategoryDTO> getCategoryQuantities(int userId);
  public List<CategoryDTO> getSectorCategoryQuantities(int userId);
List<Portfolio> getStockDetailsByCategory(String category, int userId);
void updateStopLossGain(int stockId, BigDecimal stopLossPrice, BigDecimal stopGainPrice);

}