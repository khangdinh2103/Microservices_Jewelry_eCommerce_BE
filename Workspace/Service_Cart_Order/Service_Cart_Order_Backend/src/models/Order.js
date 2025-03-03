module.exports = (sequelize, DataTypes) => {
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
          type: DataTypes.ENUM("PENDING", "SUCCESS", "FAILED"),
          defaultValue: "PENDING",
        },
      },
      {
        tableName: "Orders",
        timestamps: false,
      }
    );
  
    Order.associate = (models) => {
      Order.hasMany(models.OrderDetail, { foreignKey: "orderID", as: "details" });
    };
  
    return Order;
  };
  