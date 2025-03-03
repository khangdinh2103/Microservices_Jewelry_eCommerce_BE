const { DataTypes } = require('sequelize');
const sequelize = require('../config/Database');
const OrderStatus = require('./OrderStatus'); 


const Order = sequelize.define(
  "Order",
  {
    orderID: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true,
    },
    userID: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    createAt: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW,
    },
    address: {
      type: DataTypes.STRING,
      allowNull: false,
    },
    status: {
        type: DataTypes.ENUM(...Object.values(OrderStatus)), 
        defaultValue: OrderStatus.PENDING,
      },
  },
  {
    tableName: "Orders",
    timestamps: false,
  }
);

module.exports = Order;
