package com.chainsys.tradingapp.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chainsys.tradingapp.service.MarketDataService;


@RestController
@RequestMapping("/market")
public class MarketDataController {

    
    private MarketDataService marketDataService;
    @Autowired
    public MarketDataController(MarketDataService marketDataService) {
    	this.marketDataService=marketDataService;
    }

    @GetMapping("/nse")
    public String getNSEMarketData() throws IOException {
        return marketDataService.getNSEMarketData();
    }
}

