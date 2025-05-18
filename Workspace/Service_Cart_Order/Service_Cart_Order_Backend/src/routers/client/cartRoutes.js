const express = require('express');
const router = express.Router();
const { 
    getCartByUserId, 
    addCartItem, 
    updateCartItemQuantity, 
    deleteCartItem,
    createCart 
} = require('../../controllers/cartController');

router.get('/cart/:userId', getCartByUserId);
router.post('/cart', createCart); 
router.post('/cart-items', addCartItem);
router.put('/cart-items/:cartItemId', updateCartItemQuantity); // Đổi từ cartItemID
router.delete('/cart-items/:cartItemId', deleteCartItem); // Đổi từ cartItemID

module.exports = router;