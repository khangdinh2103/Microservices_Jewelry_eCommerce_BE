const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const Product = sequelize.define('Product', {
    id: { // Đổi từ productID thành id để đồng bộ
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    name: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    code: { // Thêm code để đồng bộ với Catalog Service
        type: DataTypes.STRING,
        allowNull: true,
    },
    description: {
        type: DataTypes.TEXT, // Đổi từ STRING sang TEXT
        allowNull: true,
    },
    quantity: { // Đổi từ stock thành quantity để đồng bộ
        type: DataTypes.INTEGER,
        allowNull: false,
        defaultValue: 0,
    },
    price: {
        type: DataTypes.DECIMAL(15, 2), // Đổi từ DOUBLE sang DECIMAL
        allowNull: false,
    },
    status: {
        type: DataTypes.STRING,
        allowNull: true,
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
    size: { // Thêm size để đồng bộ
        type: DataTypes.STRING,
        allowNull: true,
    },
    view_count: { // Thêm view_count để đồng bộ
        type: DataTypes.INTEGER,
        allowNull: true,
    },
    category_id: { // Đổi từ category thành category_id
        type: DataTypes.INTEGER,
        allowNull: true,
    },
    collection_id: { // Thêm collection_id để đồng bộ
        type: DataTypes.INTEGER,
        allowNull: true,
    },
    created_at: {
        type: DataTypes.DATE,
        allowNull: false,
        defaultValue: DataTypes.NOW,
    },
    updated_at: {
        type: DataTypes.DATE,
        allowNull: true,
    }
}, {
    tableName: 'products',
    timestamps: true,
    underscored: true,
});

module.exports = Product;