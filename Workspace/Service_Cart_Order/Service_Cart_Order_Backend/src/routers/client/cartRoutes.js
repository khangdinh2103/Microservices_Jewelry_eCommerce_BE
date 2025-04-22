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
router.post('/cart', createCart); // Thêm route tạo giỏ hàng mới
router.post('/cart-items', addCartItem);
router.put('/cart-items/:cartItemID', updateCartItemQuantity);
router.delete('/cart-items/:cartItemID', deleteCartItem);

module.exports = router;