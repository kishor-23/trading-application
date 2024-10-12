function updateModal(modal, symbol, price, stockId, quantityId, totalPriceId, stockSymbolId, stockPriceId, stockIdInput) {
    modal.find(`.modal-body #${stockSymbolId}`).val(symbol);
    modal.find(`.modal-body #${stockPriceId}`).val(price);
    modal.find(`.modal-body #${quantityId}`).val(1);
    modal.find(`.modal-body #${totalPriceId}`).val(price);
    modal.find(`.modal-body #${stockIdInput}`).val(stockId);
}

function updateTotalPrice(quantityInput, priceInput, totalPriceInput) {
    let price = parseFloat(document.getElementById(priceInput).value) || 0;
    let quantity = parseInt(document.getElementById(quantityInput).value) || 1;
    document.getElementById(totalPriceInput).value = (price * quantity).toFixed(2);
}

// Handle Buy Button Click
$('#buyModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    updateModal(
        $(this), 
        button.data('symbol'), 
        button.data('price'), 
        button.data('stock-id'),
        'quantity', 
        'totalPrice', 
        'stockSymbol', 
        'stockPrice', 
        'stockId'
    );
});

// Handle Sell Button Click
$('#sellModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    updateModal(
        $(this), 
        button.data('symbol'), 
        button.data('price'), 
        button.data('stock-id'),
        'sellQuantity', 
        'sellTotalPrice', 
        'sellStockSymbol', 
        'sellStockPrice', 
        'sellStockId'
    );
});

// Update Total Price when Buy Quantity Changes
document.getElementById('quantity').addEventListener('input', function() {
    updateTotalPrice('quantity', 'stockPrice', 'totalPrice');
});

// Update Total Price when Sell Quantity Changes
document.getElementById('sellQuantity').addEventListener('input', function() {
    updateTotalPrice('sellQuantity', 'sellStockPrice', 'sellTotalPrice');
});
