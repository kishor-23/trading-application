package com.chainsys.tradingapp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.chainsys.tradingapp.dao.NomineeDAO;
import com.chainsys.tradingapp.mapper.NomineeRowMapper;
import com.chainsys.tradingapp.model.Nominee;

import java.util.List;

@Repository
public class NomineeImpl implements NomineeDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_NOMINEE_SQL = 
        "INSERT INTO Nominee (nominee_name, relationship, user_id, phone_no) VALUES (?, ?, ?, ?)";
    private static final String SELECT_NOMINEE_BY_ID_SQL = 
        "SELECT nominee_id, nominee_name, relationship, user_id, phone_no FROM Nominee WHERE nominee_id = ? AND is_deleted = FALSE";
    private static final String SELECT_ALL_NOMINEES_BY_USER_ID_SQL = 
        "SELECT nominee_id, nominee_name, relationship, phone_no, user_id FROM Nominee WHERE user_id = ? AND is_deleted = FALSE";
    private static final String UPDATE_NOMINEE_SQL = 
        "UPDATE Nominee SET nominee_name = ?, relationship = ?, phone_no = ? WHERE nominee_id = ? AND is_deleted = FALSE";
    private static final String DELETE_NOMINEE_SQL = 
        "UPDATE Nominee SET is_deleted = TRUE WHERE nominee_id = ?";

    @Autowired
    public NomineeImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addNominee(Nominee nominee) {
       
            jdbcTemplate.update(INSERT_NOMINEE_SQL, 
                nominee.getNomineeName(), nominee.getRelationship(), 
                nominee.getUserId(), nominee.getPhoneNo());
       
    }

    @Override
    public Nominee getNomineeById(int nomineeId) {
      
            return jdbcTemplate.queryForObject(SELECT_NOMINEE_BY_ID_SQL, new NomineeRowMapper(), nomineeId);
            
    }

    @Override
    public List<Nominee> getAllNomineesByUserId(int userId) {
      
            return jdbcTemplate.query(SELECT_ALL_NOMINEES_BY_USER_ID_SQL, new NomineeRowMapper(), userId);
      
    }

    @Override
    public void updateNominee(Nominee nominee) {
      
            jdbcTemplate.update(UPDATE_NOMINEE_SQL, 
                nominee.getNomineeName(), nominee.getRelationship(), 
                nominee.getPhoneNo(), nominee.getNomineeId());
       
    }

    @Override
    public void deleteNominee(int nomineeId) {
     
            jdbcTemplate.update(DELETE_NOMINEE_SQL, nomineeId);
       
    }
}
