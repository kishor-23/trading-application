package com.chainsys.tradingapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chainsys.tradingapp.dao.UserDAO;
import com.chainsys.tradingapp.dao.impl.UserImpl;
import com.chainsys.tradingapp.exception.PanCardDuplicateException;
import com.chainsys.tradingapp.model.User;
import com.chainsys.tradingapp.validation.Validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserDAO userDAO;

    @Autowired
    public UserService(UserImpl userImpl) {
        this.userDAO = userImpl;
    }

    public User loginUser(String email, String password) throws SQLException, ClassNotFoundException {
        User user = userDAO.getUserByEmail(email);
        if (user != null && PasswordHashing.checkPassword(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public Boolean userAlreadyExists(String email) {
        return userDAO.checkUserAlreadyExists(email);
    }

    public void registerUser(User user, MultipartFile filePart, HttpServletRequest request) throws SQLException {
        Date dob = parseDateOfBirth(request.getParameter("dob"));
        Blob profilePicture = processProfilePicture(filePart);
        setUserDetails(user, dob, profilePicture);

        try {
            userDAO.addUser(user);
        } catch (DuplicateKeyException e) {
            // Check if the exception is caused by a duplicate entry
                throw new PanCardDuplicateException("User with this PAN card already exists.");
        }
    }

    public void addMoneyToUser(int userId, double amount) {
        userDAO.addMoneyToUser(userId, amount);
    }

    public User getUserByEmail(String email) throws SQLException, ClassNotFoundException {
        return userDAO.getUserByEmail(email);
    }

    public void updateUserProfilePicture(int userId, MultipartFile filePart) throws SQLException {
        Blob profilePicture = processProfilePicture(filePart);
        userDAO.updateUserProfilePicture(userId, profilePicture);
    }
    public boolean hasValidationErrors(User user) {
        return Validation.isValidEmail(user.getEmail()) && Validation.isValidPhoneNo(user.getPhone()) &&
               Validation.isValidPanCard(user.getPancardno()) && Validation.isValidPassword(user.getPassword());
    }


    public void getProfilePicture(int userId, HttpServletResponse response) {
        try {
            Blob profilePicture = userDAO.getUserProfilePicture(userId);
            if (profilePicture != null) {
                try (InputStream inputStream = profilePicture.getBinaryStream()) {
                    response.setContentType("image/jpeg");
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        response.getOutputStream().write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Error while fetching profile picture for user ID: {}", userId, e);
        }
    }

    private Date parseDateOfBirth(String dobString) {
        if (dobString != null && !dobString.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return new Date(sdf.parse(dobString).getTime());
            } catch (ParseException e) {
                logger.error("Error parsing date of birth: {}", dobString, e);
            }
        }
        return null;
    }

    private Blob processProfilePicture(MultipartFile filePart) throws SQLException {
        if (filePart != null && !filePart.isEmpty()) {
            try (InputStream inputStream = filePart.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                return new SerialBlob(bytes);
            } catch (IOException e) {
                logger.error("Error processing profile picture", e);
            }
        }
        return null;
    }

 
    private void setUserDetails(User user, Date dob, Blob profilePicture) {
        user.setDob(dob);
        user.setProfilePicture(profilePicture);
        String hashedPassword = PasswordHashing.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
    }
}
