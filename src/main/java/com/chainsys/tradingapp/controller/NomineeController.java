package com.chainsys.tradingapp.controller;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.chainsys.tradingapp.dao.NomineeDAO;
import com.chainsys.tradingapp.dao.UserDAO;
import com.chainsys.tradingapp.dao.impl.NomineeImpl;
import com.chainsys.tradingapp.dao.impl.UserImpl;
import com.chainsys.tradingapp.model.Nominee;
import com.chainsys.tradingapp.model.User;
import com.chainsys.tradingapp.validation.Validation;

@Controller
@RequestMapping("/nominee")
public class NomineeController {
    private NomineeDAO nomineeOperations;
    private UserDAO userOperations;
    private static final String REDIRECT_PROFILE = "redirect:/profile";

    @Autowired
    public NomineeController(NomineeImpl nomineeImpl, UserImpl userImpl) {
        this.nomineeOperations = nomineeImpl;
        this.userOperations = userImpl;
    }

    @GetMapping("/list")
    public String listNominees(HttpSession session, Model model) throws SQLException, ClassNotFoundException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return REDIRECT_PROFILE;
        }

        User updatedUser = userOperations.getUserByEmail(user.getEmail());
        List<Nominee> listNominees = nomineeOperations.getAllNomineesByUserId(user.getId());
        session.setAttribute("user", updatedUser);
        model.addAttribute("listNominees", listNominees);
        return REDIRECT_PROFILE;
    }

    @PostMapping("/add")
    public String addNominee(@ModelAttribute Nominee nominee, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        String validationError = validateNominee(nominee);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("msg", validationError);
            return REDIRECT_PROFILE;
        }

        nominee.setUserId(user.getId());
        nomineeOperations.addNominee(nominee);
        redirectAttributes.addFlashAttribute("msg", "Nominee added successfully.");
        return REDIRECT_PROFILE;
    }

    @PostMapping("/update")
    public String updateNominee(@ModelAttribute Nominee nominee, RedirectAttributes redirectAttributes) {
        String validationError = validateNominee(nominee);
        if (validationError != null) {
            redirectAttributes.addFlashAttribute("msg", validationError);
            return REDIRECT_PROFILE;
        }

        nomineeOperations.updateNominee(nominee);
        redirectAttributes.addFlashAttribute("msg", "Nominee updated successfully.");
        return REDIRECT_PROFILE;
    }

    @PostMapping("/delete")
    public String deleteNominee(@RequestParam int nomineeId, RedirectAttributes redirectAttributes) {
        nomineeOperations.deleteNominee(nomineeId);
        redirectAttributes.addFlashAttribute("msg", "Nominee deleted successfully.");
        return REDIRECT_PROFILE;
    }

    private String validateNominee(Nominee nominee) {
        if (!Validation.isValidName(nominee.getNomineeName())) {
            return "Invalid nominee name format.";
        }
        if (!Validation.isValidPhoneNo(nominee.getPhoneNo())) {
            return "Invalid phone number format.";
        }
        return null;
    }
}
