const express = require('express');
const {
    createOrder,
    getOrders,
    getOrderById,
    deleteOrder,
    getOrderDetailById,
    getOrderByIdUser
} = require('../../controllers/orderControllers');


const router = express.Router();

router.post('/orders', createOrder); // Tạo đơn hàng
router.get('/orders', getOrders); // Lấy danh sách đơn hàng
router.get('/orders/:orderID', getOrderById); // Lấy đơn hàng theo ID
router.delete('/orders/:orderID', deleteOrder); // Xóa đơn hàng
router.get('/orders/:orderID/details', getOrderDetailById); // Lấy chi tiết đơn hàng
router.get('/orders/user/:userID', getOrderByIdUser); // Lấy danh sách đơn hàng theo userID


module.exports = router;
