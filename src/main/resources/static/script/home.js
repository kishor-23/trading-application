document.addEventListener('DOMContentLoaded', () => {
    const hamburger = document.querySelector(".hamburger");
    if (hamburger) {
        hamburger.addEventListener('click', () => {
            const navBar = document.querySelector(".nav-bar");
            if (navBar) {
                navBar.classList.toggle("active");
            }
        });
    }

    // TradingView Widget for Ticker Tape
    (function() {
        const config = {
            "symbols": [
                {"proName":"FOREXCOM:SPXUSD","title":"S&P 500 Index"},
                {"proName":"FOREXCOM:NSXUSD","title":"US 100 Cash CFD"},
                {"proName":"FX_IDC:EURUSD","title":"EUR to USD"},
                {"proName":"BITSTAMP:BTCUSD","title":"Bitcoin"},
                {"proName":"BITSTAMP:ETHUSD","title":"Ethereum"}
            ],
            "showSymbolLogo": true,
            "isTransparent": false,
            "displayMode": "adaptive",
            "colorTheme": "light",
            "locale": "en"
        };
        const script = document.createElement("script");
        script.type = "text/javascript";
        script.src = "https://s3.tradingview.com/external-embedding/embed-widget-ticker-tape.js";
        script.async = true;
        script.textContent = JSON.stringify(config); // Use textContent instead of innerHTML
        const container = document.querySelector(".tradingview-widget-container__widget");
        if (container) {
            container.appendChild(script);
        }
    })();



  
});
