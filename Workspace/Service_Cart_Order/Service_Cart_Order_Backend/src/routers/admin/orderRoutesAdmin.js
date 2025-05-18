const express = require('express');
const {
    updateOrder,
    deleteOrder,
} = require('../../controllers/orderControllers');

const router = express.Router();
router.put('/orders/:orderId', updateOrder); // Đổi từ orderID
router.delete('/orders/:orderId', deleteOrder); // Đổi từ orderID

module.exports = router;