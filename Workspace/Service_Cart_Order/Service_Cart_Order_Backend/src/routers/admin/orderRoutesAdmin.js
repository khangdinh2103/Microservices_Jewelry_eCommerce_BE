const express = require('express');
const {
 
    updateOrder,
    deleteOrder,
} = require('../controllers/orderController');

const router = express.Router();
router.put('/orders/:orderID', updateOrder); // Cập nhật đơn hàng
router.delete('/orders/:orderID', deleteOrder); // Xóa đơn hàng

module.exports = router;
