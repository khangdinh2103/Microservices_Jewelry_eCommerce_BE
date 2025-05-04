const { Order, OrderDetail, Product, User } = require('../models');
const ProductImage = require('../models/ProductImage');


const createOrder = async (req, res) => {
    try {
        const { userID, address, status, orderDetails, paymentStatus } = req.body;

        const user = await User.findByPk(userID);
        if (!user) {
            return res.status(404).json({ message: "Người dùng không tồn tại" });
        }

        const order = await Order.create({ userID, address, status, paymentStatus });

        // Tạo chi tiết đơn hàng
        const orderItems = orderDetails.map(detail => ({
            orderID: order.orderID,
            productID: detail.productID,
            quantity: detail.quantity,
            price: detail.price
        }));
        await OrderDetail.bulkCreate(orderItems);

        return res.status(201).json({ message: "Đơn hàng được tạo thành công", order });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Lỗi khi tạo đơn hàng" });
    }
};


const getOrders = async (req, res) => {
    try {
        const orders = await Order.findAll({
            include: [{ model: User, as: "user" }]
        });
        return res.status(200).json(orders);
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Lỗi khi lấy danh sách đơn hàng" });
    }
};


const getOrderById = async (req, res) => {
    try {
        const { orderID } = req.params;
        const order = await Order.findByPk(orderID, {
            include: [{ model: User, as: "user" }]
        });

        if (!order) {
            return res.status(404).json({ message: "Không tìm thấy đơn hàng" });
        }
        return res.status(200).json(order);
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Lỗi khi lấy đơn hàng" });
    }
};

const updateOrder = async (req, res) => {
    try {
        const { orderID } = req.params;
        const { address, status, paymentStatus } = req.body;

        const order = await Order.findByPk(orderID);
        if (!order) {
            return res.status(404).json({ message: "Không tìm thấy đơn hàng" });
        }

        // Create an update object with only the fields that need to be updated
        const updateData = {};
        
        // Add address to update data if provided
        if (address) {
            updateData.address = address;
        }
        
        // Add paymentStatus to update data if provided and valid
        if (paymentStatus !== undefined) {
            if (!["PENDING", "PAID", "CANCELED"].includes(paymentStatus)) {
                return res.status(400).json({ message: 'Trạng thái thanh toán không hợp lệ' });
            }
            updateData.paymentStatus = paymentStatus;
            console.log("Payment status to update:", paymentStatus);
        }
        
        // Update non-status fields first if there are any
        if (Object.keys(updateData).length > 0) {
            await Order.update(updateData, {
                where: { orderID: orderID }
            });
        }
        
        // Handle status update separately
        if (status !== undefined) {
            if (!["PENDING", "PROCESSING", "DELIVERED", "CANCELLED"].includes(status)) {
                return res.status(400).json({ message: 'Trạng thái đơn hàng không hợp lệ' });
            }
            
            console.log("Status to update:", status);
            
            // Try a different approach - update the instance directly and save
            order.status = status;
            await order.save({ fields: ['status'] });
            
            // Verify the status was updated
            const checkOrder = await Order.findByPk(orderID);
            console.log("Status after direct save:", checkOrder.status);
            
            // If status is still empty, try raw SQL with quotes
            if (!checkOrder.status) {
                console.log("Status is empty after save, trying raw SQL with quotes");
                await Order.sequelize.query(
                    `UPDATE Orders SET status = "${status}" WHERE orderID = ?`,
                    {
                        replacements: [orderID],
                        type: Order.sequelize.QueryTypes.UPDATE
                    }
                );
            }
        }
        
        // Fetch the updated order to return in response
        const updatedOrder = await Order.findByPk(orderID);
        console.log("Final updated order:", JSON.stringify(updatedOrder, null, 2));
        
        return res.status(200).json({ 
            message: 'Cập nhật đơn hàng thành công',
            order: updatedOrder
        });
        
    } catch (error) {
        console.error('Error updating order:', error);
        return res.status(500).json({ message: 'Lỗi server khi cập nhật đơn hàng' });
    }
}

const deleteOrder = async (req, res) => {
    try {
        const { orderID } = req.params;

        const order = await Order.findByPk(orderID);
        if (!order) {
            return res.status(404).json({ message: "Không tìm thấy đơn hàng" });
        }

        await order.destroy();
        return res.status(200).json({ message: "Xóa đơn hàng thành công" });
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Lỗi khi xóa đơn hàng" });
    }
};

const getOrderDetailById = async (req, res) => {
    try {
        const { orderID } = req.params;

        const orderDetails = await OrderDetail.findAll({
            where: { orderID },
            include: [{ model: Product, as: "product" }]
        });

        if (orderDetails.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy chi tiết đơn hàng" });
        }

        return res.status(200).json(orderDetails);
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Lỗi khi lấy chi tiết đơn hàng" });
    }
};
const getOrderByIdUser = async (req, res) => {
    try {
        const { userID } = req.params;

        // Kiểm tra xem user có tồn tại không
        const user = await User.findByPk(userID);
        if (!user) {
            return res.status(404).json({ message: "Người dùng không tồn tại" });
        }

        // Lấy danh sách đơn hàng của user
        const orders = await Order.findAll({
            where: { userID },
            include: [{ model: OrderDetail, as: "orderDetails", include: [{ model: Product, as: "product", include:[{model: ProductImage, as: "imageSet", attributes: ['imageURL'], limit: 1,}] }] }]
        });

        if (orders.length === 0) {
            return res.status(404).json({ message: "Không tìm thấy đơn hàng nào" });
        }

        return res.status(200).json(orders);
    } catch (error) {
        console.error(error);
        return res.status(500).json({ message: "Lỗi khi lấy danh sách đơn hàng của người dùng" });
    }
};

//Tạm thời lấy user
const getUserById = async (userID) => {
    try {
        const user = await User.findOne({ where: { userID } });

        if (!user) {
            return { error: 'Người dùng không tồn tại' };
        }

        return user;
    } catch (error) {
        console.error('Lỗi khi lấy thông tin người dùng:', error);
        return { error: 'Lỗi hệ thống' };
    }
};
const getAllOrdersForAdmin = async (req, res) => {
    try {
      const orders = await Order.findAll({
        include: [
          {
            model: OrderDetail,
            as: 'orderDetails',
            include: [
              {
                model: Product,
                as: 'product',
                include: [
                  {
                    model: ProductImage,
                    as: 'imageSet'
                  }
                ]
              }
            ]
          }
        ],
        order: [['createAt', 'DESC']]
      });
      
      return res.status(200).json(orders);
    } catch (error) {
      console.error(error);
      return res.status(500).json({ message: "Error fetching orders" });
    }
  };

module.exports = {
    createOrder,
    getOrders,
    getOrderById,
    updateOrder,
    deleteOrder,
    getOrderDetailById,
    getOrderByIdUser,
    getUserById,
    getAllOrdersForAdmin,
    
};

