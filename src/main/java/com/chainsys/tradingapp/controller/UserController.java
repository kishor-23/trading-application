package com.chainsys.tradingapp.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chainsys.tradingapp.dto.ProfileDTO;
import com.chainsys.tradingapp.model.User;
import com.chainsys.tradingapp.service.EmailService;
import com.chainsys.tradingapp.service.ProfileService;
import com.chainsys.tradingapp.service.UserService;
import com.chainsys.tradingapp.validation.Validation;



@Controller
public class UserController {

    private static final String REGISTER_PAGE = "register";
    private static final String LOGIN_PAGE = "login";
    private static final String PROFILE_PAGE = "redirect:/profile";
    private static final String REDIRECT_LOGIN = "redirect:/login";

    private static final String MSG = "msg";

    private final UserService userService;
    private final ProfileService profileService;
    private final EmailService emailService;

    @Autowired
    public UserController(UserService userService, ProfileService profileService, EmailService emailService) {
        this.userService = userService;
        this.profileService = profileService;
        this.emailService = emailService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model, HttpServletResponse response) throws ClassNotFoundException, SQLException, IOException {
        // Set headers to prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        // Retrieve the current user from session
        User currentUser = (User) session.getAttribute("user");
        // Redirect to login if user is not logged in
        if (currentUser == null) {
            return REDIRECT_LOGIN;
        }
        // Fetch profile data and add it to the model
        ProfileDTO profileDTO = profileService.getProfileData(currentUser.getEmail());
        model.addAttribute("profile", profileDTO);
        // Return the profile view
        return "profile";
    }



    @GetMapping("/chatbot")
    public String getChatPage() {
        return "chatbot";
    }
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/liveMarket")
    public String liveMarket() {
        return "LiveMarket";
    }

    @GetMapping("/learnToTrade")
    public String liveToTrade() {
        return "learn_to_trade";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return LOGIN_PAGE;
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) throws ClassNotFoundException, SQLException {
        	if(Boolean.FALSE.equals(userService.userAlreadyExists(email))) {
                model.addAttribute(MSG, "User does not exists. Please register.");
        		return LOGIN_PAGE;
        	}
            User user = userService.loginUser(email, password);
            if (user != null) {
                session.setAttribute("user", user);
                return PROFILE_PAGE;
            } else {
                model.addAttribute("msg", "Incorrect password");
                return LOGIN_PAGE;
            }
       
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return REGISTER_PAGE;
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                               @RequestParam("profile") MultipartFile filePart, 
                               HttpServletRequest request, 
                               Model model, 
                               RedirectAttributes redirectAttributes) throws SQLException, MessagingException, IOException {
        if (Boolean.TRUE.equals(userService.userAlreadyExists(user.getEmail()))) {
            model.addAttribute("msg", "Registration failed. User already exists. Please register.");
            return REGISTER_PAGE;
        }
        if (!userService.hasValidationErrors(user)) {
            model.addAttribute("msg", "Not a valid input");
            return REGISTER_PAGE;
        }
        userService.registerUser(user, filePart, request);
        emailService.sendWelcomeEmail(user.getEmail(), "Welcome to ChainTrade!");
        redirectAttributes.addFlashAttribute("alert", "Successfully registered!");
        return LOGIN_PAGE;
    }


    @PostMapping("/addMoney")
    public String addMoney(@RequestParam int userId, @RequestParam double amount, HttpSession session, Model model,RedirectAttributes redirectAttributes) throws ClassNotFoundException, SQLException {
            userService.addMoneyToUser(userId, amount);
            User updatedUser = userService.getUserByEmail(((User) session.getAttribute("user")).getEmail());
            session.setAttribute("user", updatedUser);
            if(!Validation.isValidAmount(amount)) {
                redirectAttributes.addFlashAttribute("msg", "Money can not be negative");           
                return PROFILE_PAGE; // Redirects to the profile page
            }
            redirectAttributes.addFlashAttribute("msg", "Money has been added successfully.");        
            return PROFILE_PAGE; // Redirects to the profile page
       
    }


    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        if (session != null) {
            session.invalidate();
        }
        // Set headers to prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        return REDIRECT_LOGIN;
    }


    @PostMapping("/profilePicture")
    public String updateProfilePicture(@RequestParam("userId") int userId, @RequestParam("profilePicture") MultipartFile filePart, HttpServletResponse response) throws SQLException {
        userService.updateUserProfilePicture(userId, filePart);
        return PROFILE_PAGE;
    }

    @GetMapping("/profilePicture")
    public void getProfilePicture(@RequestParam("userId") int userId, HttpServletResponse response) {
        userService.getProfilePicture(userId, response);
    }
}
