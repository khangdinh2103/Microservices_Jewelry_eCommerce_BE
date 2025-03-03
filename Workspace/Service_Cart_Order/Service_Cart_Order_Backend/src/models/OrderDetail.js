const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');

const OrderDetail = sequelize.define(
  "OrderDetail",
  {
    orderDetailID: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true,
    },
    orderID: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    productID: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    quantity: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    price: {
      type: DataTypes.DOUBLE,
      allowNull: false,
    },
  },
  {
    tableName: "OrderDetails",
    timestamps: false,
  }
);

module.exports = OrderDetail;
