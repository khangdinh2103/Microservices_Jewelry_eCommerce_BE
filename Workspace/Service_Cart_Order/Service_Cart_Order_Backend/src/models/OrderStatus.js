module.exports = (sequelize, DataTypes) => {
    const OrderStatus = {
      PENDING: "PENDING",
      SUCCESS: "SUCCESS",
      FAILED: "FAILED",
    };
    return OrderStatus;
  };
  