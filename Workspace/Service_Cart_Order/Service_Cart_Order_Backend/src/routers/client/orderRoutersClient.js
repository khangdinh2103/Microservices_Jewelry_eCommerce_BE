const express = require('express');
const {
    createOrder,
    getOrders,
    getOrderById,
    deleteOrder,
    getOrderDetailById,
    getOrderByIdUser,
    getUserById
} = require('../../controllers/orderControllers');


const router = express.Router();

router.post('/orders', createOrder); // Tạo đơn hàng
router.get('/orders', getOrders); // Lấy danh sách đơn hàng
router.get('/orders/:orderID', getOrderById); // Lấy đơn hàng theo ID
router.delete('/orders/:orderID', deleteOrder); // Xóa đơn hàng
router.get('/orders/:orderID/details', getOrderDetailById); 
router.get('/orders/user/:userID', getOrderByIdUser); 
router.get('/user/:userID', getUserById); 

module.exports = router;
