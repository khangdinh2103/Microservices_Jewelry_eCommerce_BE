const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const ProductImage = sequelize.define('ProductImage', {
    imageID: {
        type: DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true,
    },
    productID: {
        type: DataTypes.INTEGER,
        allowNull: false,
        references: {
            model: 'products',
            key: 'productID',
        },
        onDelete: 'CASCADE',
    },
    imageURL: {
        type: DataTypes.STRING,
        allowNull: false,
    },
    isThumbnail: {
        type: DataTypes.BOOLEAN,
        allowNull: false,
        defaultValue: false,
    },
}, {
    tableName: 'product_images',
    timestamps: false,
});

module.exports = ProductImage;
