const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const OrderDetail = sequelize.define('OrderDetail', {
    id: { // Đổi từ orderDetailID thành id
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
    },
    order_id: { // Đổi từ orderID thành order_id
        type: DataTypes.INTEGER,
        allowNull: false,
    },
    product_id: { // Đổi từ productID thành product_id
        type: DataTypes.INTEGER,
        allowNull: false,
    },
    quantity: {
        type: DataTypes.INTEGER,
        allowNull: false,
    },
    price: {
        type: DataTypes.DECIMAL(15, 2), // Đổi từ DOUBLE sang DECIMAL
        allowNull: false,
    },
    created_at: { // Thêm created_at
        type: DataTypes.DATE,
        defaultValue: DataTypes.NOW,
    },
    updated_at: { // Thêm updated_at
        type: DataTypes.DATE,
        allowNull: true,
    },
}, {
    tableName: 'order_details',
    timestamps: true,
    underscored: true,
});

module.exports = OrderDetail;