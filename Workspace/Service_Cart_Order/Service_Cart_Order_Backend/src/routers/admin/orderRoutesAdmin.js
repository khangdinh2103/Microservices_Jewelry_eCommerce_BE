const express = require('express');
const {
    getOrders,
    updateOrder,
    deleteOrder,
    getAllOrdersForAdmin,
} = require('../../controllers/orderControllers');

const router = express.Router();
router.get('/orders', getAllOrdersForAdmin); // Add this route to get all orders
router.put('/orders/:orderID', updateOrder); 
router.delete('/orders/:orderID', deleteOrder); 

module.exports = router;