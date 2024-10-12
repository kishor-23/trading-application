package com.chainsys.tradingapp.mapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.chainsys.tradingapp.dto.CategoryDTO;

public class CategoryRowMapper implements RowMapper<CategoryDTO> {
    @Override
    public CategoryDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        CategoryDTO categoryQuantity = new CategoryDTO();
        categoryQuantity.setCapCategory(resultSet.getString("cap_category"));
        categoryQuantity.setTotalQuantity(resultSet.getInt("total_quantity"));
        categoryQuantity.setUserTotalQuantity(resultSet.getInt("user_total_quantity"));
        return categoryQuantity;
    }
}


