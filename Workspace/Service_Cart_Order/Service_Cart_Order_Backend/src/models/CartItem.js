const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const CartItem = sequelize.define('CartItem', {
    id: { // Đổi từ cartItemID thành id
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    price: {
        type: DataTypes.DECIMAL(15, 2), // Đổi từ DOUBLE sang DECIMAL
        allowNull: false,
    },
    quantity: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 1,
    },
    cart_id: { // Đổi từ cartID thành cart_id
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
            model: 'carts',
            key: 'id',
        },
        onDelete: 'CASCADE',
    },
    product_id: { // Đổi từ productID thành product_id
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
            model: 'products',
            key: 'id',
        },
        onDelete: 'CASCADE',
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
    tableName: 'cart_items',
    timestamps: true,
    underscored: true,
});

module.exports = CartItem;