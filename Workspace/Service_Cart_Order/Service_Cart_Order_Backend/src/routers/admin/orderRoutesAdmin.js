const express = require('express');
const {
 
    updateOrder,
    deleteOrder,
    
} = require('../../controllers/orderControllers');

const router = express.Router();
router.put('/orders/:orderID', updateOrder); 
router.delete('/orders/:orderID', deleteOrder); 

module.exports = router;
