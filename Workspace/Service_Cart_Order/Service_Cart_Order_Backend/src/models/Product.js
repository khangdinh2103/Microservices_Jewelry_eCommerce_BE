const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');
const ProductImage = require('./ProductImage'); // Import model ProductImage

const Product = sequelize.define('Product', {
    productID: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    name: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    description: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    stock: {
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 0,
    },
    price: {
        type: DataTypes.DOUBLE,
        allowNull: false,
    },
    gender: {
        type: DataTypes.INTEGER,
        allowNull: true,
    },
    material: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    gold_karat: {
        type: DataTypes.INTEGER,
        allowNull: true,
    },
    color: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    brand: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    category: {
        type: DataTypes.STRING,
        allowNull: true,
    },
    createdAt: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW,
    },
    updatedAt: {
        type: DataTypes.DATE,
        allowNull: true,
    }
}, {
    tableName: 'products',
    timestamps: true,
});

// Thiết lập quan hệ 1-N giữa Product và ProductImage
// Product.hasMany(ProductImage, { foreignKey: 'productID', as: 'imageSet' });
// ProductImage.belongsTo(Product, { foreignKey: 'productID' });

module.exports = Product;
