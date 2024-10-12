package com.chainsys.tradingapp.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class MarketDataService {

    public String getNSEMarketData() throws IOException {
        // URL of the NSE India market data page
        String url = "https://www.nseindia.com/";

        // Fetch and parse the HTML from the URL
        Document document = Jsoup.connect(url).get();

        // Extract market indices
        Elements indices = document.select(".nav-item.nav-link");  // Updated selector to match the provided HTML

        StringBuilder marketData = new StringBuilder();

        for (Element index : indices) {
            Element nameElement = index.select(".tb_name").first();
            Element valueElement = index.select(".tb_val").first();
            Element percentElement = index.select(".tb_per").first();

            if (nameElement != null && valueElement != null && percentElement != null) {
                String name = nameElement.text();
                String value = valueElement.text();
                String percent = percentElement.text();

                // Clean up the output
                marketData.append(name)
                          .append(": ")
                          .append(value)
                          .append(" (")
                          .append(percent)
                          .append(")\n");
            }
        }

        return marketData.toString();
    }
}
