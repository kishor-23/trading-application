package com.chainsys.tradingapp.controller;

import com.chainsys.tradingapp.dto.PortfolioDTO;
import com.chainsys.tradingapp.model.Portfolio;
import com.chainsys.tradingapp.model.User;
import com.chainsys.tradingapp.service.PortfolioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/portfolio")
    public String portfolio(HttpSession session, Model model, HttpServletResponse response) {
    	 // Set headers to prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        PortfolioDTO portfolioDTO = portfolioService.getPortfolioData(user);
        model.addAttribute(portfolioDTO);


        return "portfolio";
    }
    @GetMapping("/api/portfolio")
    public ResponseEntity<PortfolioDTO> getPortfolioData(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        PortfolioDTO portfolioDTO = portfolioService.getPortfolioData(user);
        return new ResponseEntity<>(portfolioDTO, HttpStatus.OK);
    }
    @GetMapping("/getStockDetailsByCategory")
    public ResponseEntity<List<Portfolio>> getStockDetailsByCategory(
            @RequestParam String category,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Portfolio> stockDetails = portfolioService.getStockByIdAndUser(category, user.getId());
        return ResponseEntity.ok(stockDetails);
    }
    
    
    @PostMapping("/updateStopLossAndStopGain")
    public String updateStopLossAndStopGain(
            @RequestParam int stockId,
            @RequestParam BigDecimal stopLoss,
            @RequestParam BigDecimal stopGain,
            HttpSession session) {
     

        portfolioService.updateStopLossAndStopGain(stockId, stopLoss, stopGain);
        return "redirect:/portfolio";
    }
}
