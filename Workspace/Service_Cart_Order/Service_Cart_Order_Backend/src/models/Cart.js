const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const Cart = sequelize.define('Cart', {
    id: { // Đổi từ cartID thành id
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    user_id: { // Đổi từ userID thành user_id
        type: DataTypes.BIGINT, // Đổi sang BIGINT để phù hợp với id user
        allowNull: false,
    },
    created_at: { // Đổi từ createdAt thành created_at
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW,
    },
    updated_at: { // Thêm updated_at
        type: DataTypes.DATE,
        allowNull: true,
    },
}, {
    tableName: 'carts',
    timestamps: true,
    underscored: true,
});

module.exports = Cart;