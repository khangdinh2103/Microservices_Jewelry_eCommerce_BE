const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');
const User = require('./User');

const Cart = sequelize.define('Cart', {
    cartID: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    userID: {
        type: DataTypes.INTEGER,
        allowNull: false,
        // Bỏ phần reference để không kiểm tra khóa ngoại
    },
    createdAt: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW,
    },
}, {
    tableName: 'carts',
    timestamps: false,
});

// Mối quan hệ vẫn được định nghĩa ở index.js nhưng không áp dụng ràng buộc

// // Thiết lập quan hệ với User
// Cart.belongsTo(User, { foreignKey: 'userID' });
// User.hasOne(Cart, { foreignKey: 'userID' });

module.exports = Cart;