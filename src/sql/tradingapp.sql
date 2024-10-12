-- Create and use the trading database
CREATE DATABASE trading_db_1;
USE trading_db_1;

-- Create the users table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    pancardno VARCHAR(10) NOT NULL,
    phone VARCHAR(10) NOT NULL,
    dob DATE NOT NULL,
    password TEXT NOT NULL,
    profilePicture BLOB DEFAULT NULL,
    balance DECIMAL(10, 2) NOT NULL DEFAULT 100,
    UNIQUE KEY unique_pancardno (pancardno)
);

-- Create the Nominee table
CREATE TABLE Nominee (
    nominee_id INT PRIMARY KEY AUTO_INCREMENT,
    nominee_name VARCHAR(255) NOT NULL,
    relationship ENUM('Spouse', 'Parent', 'Child', 'Sibling', 'Other'),
    phone_no VARCHAR(15),
    user_id INT,
    is_deleted BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_nominee_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create the stocks table with additional sector column
CREATE TABLE stocks (
    stock_id INT PRIMARY KEY AUTO_INCREMENT,
    symbol VARCHAR(10) UNIQUE NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    current_stock_price DECIMAL(15, 2) NOT NULL,
    cap_category VARCHAR(10) NOT NULL,
    sector VARCHAR(50) DEFAULT NULL
);

-- Create the portfolio table with additional stop_loss_price and stop_gain_price columns
CREATE TABLE portfolio (
    portfolio_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    stock_id INT,
    quantity INT NOT NULL,
    avg_cost DECIMAL(15, 2) NOT NULL,
    total_cost DECIMAL(15, 2) NOT NULL DEFAULT 0,
    stop_loss_price DECIMAL(15, 2) DEFAULT NULL,
    stop_gain_price DECIMAL(15, 2) DEFAULT NULL,
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_portfolio_stock FOREIGN KEY (stock_id) REFERENCES stocks(stock_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create the transactions table with profit_loss column
CREATE TABLE transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    stock_id INT,
    shares INT NOT NULL,
    price DECIMAL(15, 2) NOT NULL,
    transaction_type ENUM('buy', 'sell') NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    profit_loss DECIMAL(15, 2) DEFAULT 0,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_transactions_stock FOREIGN KEY (stock_id) REFERENCES stocks(stock_id) ON DELETE CASCADE ON UPDATE CASCADE
);




--buy stock procedure 


DELIMITER //

CREATE PROCEDURE buyStockProcedure(
    IN p_userId INT,
    IN p_stockId INT,
    IN p_quantity INT,
    IN p_price DECIMAL(15, 2),
    OUT p_result INT
)
BEGIN
    DECLARE userBalance DECIMAL(10, 2);
    DECLARE totalCost DECIMAL(15, 2);
    DECLARE existingQuantity INT DEFAULT 0;
    DECLARE existingTotalCost DECIMAL(15, 2) DEFAULT 0;

    -- Get the user's current balance
    SELECT balance INTO userBalance FROM users WHERE id = p_userId;

    -- Calculate the total cost of the purchase
    SET totalCost = p_quantity * p_price;

    -- Check if the user has enough balance
    IF userBalance < totalCost THEN
        SET p_result = 0; -- Insufficient balance
    ELSE
        -- Deduct the total cost from the user's balance
        UPDATE users SET balance = balance - totalCost WHERE id = p_userId;

        -- Check if the stock already exists in the user's portfolio
        SELECT quantity, total_cost INTO existingQuantity, existingTotalCost
        FROM portfolio
        WHERE user_id = p_userId AND stock_id = p_stockId;

        -- If the stock exists, update the quantity and total cost
        IF existingQuantity > 0 THEN
            SET existingQuantity = existingQuantity + p_quantity;
            SET existingTotalCost = existingTotalCost + totalCost;
            UPDATE portfolio
            SET quantity = existingQuantity,
                total_cost = existingTotalCost,
                avg_cost = existingTotalCost / existingQuantity
            WHERE user_id = p_userId AND stock_id = p_stockId;
        ELSE
            -- If the stock does not exist, insert a new record
            INSERT INTO portfolio (user_id, stock_id, quantity, avg_cost, total_cost)
            VALUES (p_userId, p_stockId, p_quantity, p_price, totalCost);
        END IF;

        -- Record the transaction
        INSERT INTO transactions (user_id, stock_id, shares, price, transaction_type)
        VALUES (p_userId, p_stockId, p_quantity, p_price, 'buy');

        SET p_result = 1; -- Success
    END IF;
END //

DELIMITER ;


-- sell stock procedure 
DELIMITER //

CREATE PROCEDURE sellStockProcedure(
    IN p_userId INT,
    IN p_stockId INT,
    IN p_quantity INT,
    IN p_price DECIMAL(15, 2),
    OUT p_result INT,
    OUT p_profit_loss DECIMAL(15, 2)
)
BEGIN
    DECLARE totalEarning DECIMAL(15, 2);
    DECLARE existingQuantity INT DEFAULT 0;
    DECLARE existingTotalCost DECIMAL(15, 2) DEFAULT 0;
    DECLARE currentStockPrice DECIMAL(15, 2);
    DECLARE buyedStockPrice DECIMAL(15, 2);

    -- Calculate the total earning from selling the stocks
    SET totalEarning = p_quantity * p_price;

    -- Check if the user has enough stocks to sell
    SELECT quantity, total_cost / quantity INTO existingQuantity, buyedStockPrice
    FROM portfolio
    WHERE user_id = p_userId AND stock_id = p_stockId;

    IF existingQuantity >= p_quantity THEN
        -- Calculate profit or loss based on the difference between the selling price and the buyed stock price
        SET p_profit_loss = (p_price - buyedStockPrice) * p_quantity;

        -- Add the earning to the user's balance
        UPDATE users SET balance = balance + totalEarning WHERE id = p_userId;

        -- Deduct the sold stocks from the portfolio
        UPDATE portfolio
        SET quantity = quantity - p_quantity,
            total_cost = total_cost - (buyedStockPrice * p_quantity)
        WHERE user_id = p_userId AND stock_id = p_stockId;

        -- If quantity becomes zero, delete the record from portfolio
        DELETE FROM portfolio
        WHERE user_id = p_userId AND stock_id = p_stockId AND quantity = 0;

        -- Record the transaction
        INSERT INTO transactions (user_id, stock_id, shares, price, transaction_type, profit_loss)
        VALUES (p_userId, p_stockId, p_quantity, p_price, 'sell', p_profit_loss);

        SET p_result = 1; -- Success
    ELSE
        SET p_result = 0; -- Insufficient stocks to sell
    END IF;
END //

DELIMITER ;


-- trigger to change stock price when buy or sell for simplicity when stock is buy it price will increase by 10 and decrease 10 if sell.


CREATE TRIGGER adjust_stock_price AFTER INSERT ON transactions
FOR EACH ROW
BEGIN
    DECLARE price_change DECIMAL(15, 2);
    DECLARE new_price DECIMAL(15, 2);

    -- Calculate the price change based on the transaction type
    IF NEW.transaction_type = 'buy' THEN
        SET price_change = 10.00;
    ELSE
        SET price_change = -10.00;
    END IF;

    -- Compute the new price after the change
    SET new_price = (
        SELECT current_stock_price + price_change
        FROM stocks
        WHERE stock_id = NEW.stock_id
    );

    -- Ensure the new price is not less than 10
    IF new_price < 10.00 THEN
        SET new_price = 10.00;
    END IF;

    -- Update the stock price
    UPDATE stocks
    SET current_stock_price = new_price
    WHERE stock_id = NEW.stock_id;
END //

DELIMITER ;

--investment details of user by cap_category
SELECT p.portfolio_id, p.user_id, p.stock_id,s.cap_category, s.company_name, s.symbol, p.quantity, p.avg_cost, p.total_cost
FROM 
    portfolio p
JOIN 
    stocks s 
ON 
    p.stock_id = s.stock_id
WHERE p.user_id = 2;
SELECT 
    s.cap_category,
    SUM(p.quantity) AS total_quantity,
    (SELECT SUM(quantity) 
     FROM portfolio 
     WHERE user_id = 2) AS user_total_quantity
FROM 
    portfolio p
JOIN 
    stocks s ON p.stock_id = s.stock_id
WHERE 
    p.user_id = 19
GROUP BY 
    s.cap_category;


-- Procedure to check stop loss and stop gain
DELIMITER //

CREATE PROCEDURE checkStopLossAndGain()
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE p_userId, p_stockId, p_quantity INT;
    DECLARE p_currentPrice, p_stopLossPrice, p_stopGainPrice DECIMAL(15, 2);
    DECLARE cur CURSOR FOR 
        SELECT p.user_id, p.stock_id, p.quantity, s.current_stock_price, p.stop_loss_price, p.stop_gain_price 
        FROM portfolio p
        JOIN stocks s ON p.stock_id = s.stock_id;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    OPEN cur;
    read_loop: LOOP
        FETCH cur INTO p_userId, p_stockId, p_quantity, p_currentPrice, p_stopLossPrice, p_stopGainPrice;
        IF done THEN
            LEAVE read_loop;
        END IF;

        -- Check for stop loss
        IF p_stopLossPrice IS NOT NULL AND p_currentPrice <= p_stopLossPrice THEN
            CALL sellStockProcedure(p_userId, p_stockId, p_quantity, p_currentPrice, @result, @profit_loss);
        END IF;

        -- Check for stop gain
        IF p_stopGainPrice IS NOT NULL AND p_currentPrice >= p_stopGainPrice THEN
            CALL sellStockProcedure(p_userId, p_stockId, p_quantity, p_currentPrice, @result, @profit_loss);
        END IF;
    END LOOP;

    CLOSE cur;
END //

DELIMITER ;



-- Insert Indian Stocks
INSERT INTO stocks (symbol, company_name, current_stock_price, cap_category, sector) VALUES
('TCS', 'Tata Consultancy Services', 3500.50, 'Large', 'Technology'),
('INFY', 'Infosys', 1700.20, 'Large', 'Technology'),
('RELI', 'Reliance Industries', 2500.00, 'Large', 'Energy'),
('HDFCBANK', 'HDFC Bank', 1650.75, 'Large', 'Financials'),
('ICICIBANK', 'ICICI Bank', 950.80, 'Large', 'Financials'),
('SBIN', 'State Bank of India', 600.20, 'Large', 'Financials'),
('BAJAJFINSV', 'Bajaj Finserv', 1900.60, 'Large', 'Financials'),
('HINDUNILVR', 'Hindustan Unilever', 2650.50, 'Large', 'Consumer Goods'),
('ASIANPAINT', 'Asian Paints', 3100.10, 'Large', 'Materials'),
('ITC', 'ITC Limited', 450.90, 'Large', 'Consumer Goods'),
('MARUTI', 'Maruti Suzuki', 8500.80, 'Large', 'Automotive'),
('BHARTIARTL', 'Bharti Airtel', 800.70, 'Large', 'Telecom'),
('ONGC', 'Oil and Natural Gas Corporation', 135.25, 'Large', 'Energy'),
('NTPC', 'NTPC Limited', 155.60, 'Large', 'Energy'),
('ADANIGREEN', 'Adani Green Energy', 950.50, 'Large', 'Renewable Energy'),
('POWERGRID', 'Power Grid Corporation', 220.45, 'Large', 'Utilities'),
('SUNPHARMA', 'Sun Pharmaceutical', 990.75, 'Large', 'Healthcare'),
('WIPRO', 'Wipro', 420.65, 'Large', 'Technology'),
('CIPLA', 'Cipla', 1090.45, 'Large', 'Healthcare'),
('DRREDDY', 'Dr. Reddy\'s Laboratories', 5300.85, 'Large', 'Healthcare'),
('JSWSTEEL', 'JSW Steel', 720.30, 'Large', 'Materials'),
('TATAMOTORS', 'Tata Motors', 525.60, 'Large', 'Automotive'),
('VEDL', 'Vedanta Limited', 270.40, 'Large', 'Materials'),
('TATASTEEL', 'Tata Steel', 510.25, 'Large', 'Materials'),
('LT', 'Larsen & Toubro', 2500.35, 'Large', 'Industrials');

-- Insert US Stocks
INSERT INTO stocks (symbol, company_name, current_stock_price, cap_category, sector) VALUES
('AAPL', 'Apple Inc.', 175.30, 'Large', 'Technology'),
('MSFT', 'Microsoft Corporation', 310.25, 'Large', 'Technology'),
('AMZN', 'Amazon.com, Inc.', 3300.50, 'Large', 'Consumer Discretionary'),
('GOOGL', 'Alphabet Inc.', 2800.45, 'Large', 'Technology'),
('FB', 'Meta Platforms Inc.', 355.90, 'Large', 'Technology'),
('TSLA', 'Tesla Inc.', 880.65, 'Large', 'Automotive'),
('NVDA', 'NVIDIA Corporation', 700.75, 'Large', 'Technology'),
('NFLX', 'Netflix, Inc.', 590.25, 'Large', 'Consumer Discretionary'),
('PYPL', 'PayPal Holdings', 275.85, 'Large', 'Financials'),
('JPM', 'JPMorgan Chase & Co.', 155.60, 'Large', 'Financials'),
('V', 'Visa Inc.', 210.70, 'Large', 'Financials'),
('MA', 'Mastercard Inc.', 365.50, 'Large', 'Financials'),
('DIS', 'The Walt Disney Company', 155.25, 'Large', 'Consumer Discretionary'),
('PEP', 'PepsiCo, Inc.', 160.80, 'Large', 'Consumer Goods'),
('KO', 'The Coca-Cola Company', 60.50, 'Large', 'Consumer Goods'),
('XOM', 'Exxon Mobil Corporation', 95.25, 'Large', 'Energy'),
('CVX', 'Chevron Corporation', 115.65, 'Large', 'Energy'),
('WMT', 'Walmart Inc.', 145.35, 'Large', 'Consumer Staples'),
('PG', 'Procter & Gamble Co.', 145.75, 'Large', 'Consumer Goods'),
('BA', 'The Boeing Company', 225.50, 'Large', 'Industrials'),
('IBM', 'International Business Machines', 135.65, 'Large', 'Technology'),
('GE', 'General Electric', 105.45, 'Large', 'Industrials'),
('INTC', 'Intel Corporation', 52.75, 'Large', 'Technology'),
('MRK', 'Merck & Co., Inc.', 85.25, 'Large', 'Healthcare'),
('PFE', 'Pfizer Inc.', 43.20, 'Large', 'Healthcare');

-- 50 stocks in total



