const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const ProductImage = sequelize.define('ProductImage', {
    id: { // Đổi từ imageID thành id
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
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
    image_url: { // Đổi từ imageURL thành image_url
        type: DataTypes.STRING,
        allowNull: false,
    },
    is_primary: { // Đổi từ isThumbnail thành is_primary
        type: DataTypes.BOOLEAN,
        allowNull: false,
        defaultValue: false,
    },
    sort_order: { // Thêm sort_order
        type: DataTypes.INTEGER,
        allowNull: true,
    }
}, {
    tableName: 'product_images',
    timestamps: false,
    underscored: true,
});

module.exports = ProductImage;