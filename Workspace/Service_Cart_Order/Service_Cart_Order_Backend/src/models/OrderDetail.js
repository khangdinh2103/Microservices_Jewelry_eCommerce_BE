module.exports = (sequelize, DataTypes) => {
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
  
    OrderDetail.associate = (models) => {
      OrderDetail.belongsTo(models.Order, { foreignKey: "orderID", as: "order" });
    };
  
    return OrderDetail;
  };
  