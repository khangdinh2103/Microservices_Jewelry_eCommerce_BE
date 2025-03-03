const User = require('./User');
const Cart = require('./Cart');
const Product = require('./Product');
const CartItem = require('./CartItem');
const Order = require('./Order');
const OrderDetail = require('./OrderDetail');


Cart.hasMany(CartItem, { foreignKey: 'cartID', as: 'cartItems', onDelete: 'CASCADE' });
CartItem.belongsTo(Cart, { foreignKey: 'cartID', as: 'cart' });

Product.hasMany(CartItem, { foreignKey: 'productID', as: 'productItems', onDelete: 'CASCADE' });
CartItem.belongsTo(Product, { foreignKey: 'productID', as: 'product' });

User.hasOne(Cart, { foreignKey: 'userID', as: 'userCart', onDelete: 'CASCADE' });
Cart.belongsTo(User, { foreignKey: 'userID', as: 'user' });

User.hasMany(Order, { foreignKey: 'userID', as: 'orders', onDelete: 'CASCADE' });
Order.belongsTo(User, { foreignKey: 'userID', as: 'user' });

// 📝 Quan hệ giữa Order và OrderDetail
Order.hasMany(OrderDetail, { foreignKey: 'orderID', as: 'orderDetails', onDelete: 'CASCADE' });
OrderDetail.belongsTo(Order, { foreignKey: 'orderID', as: 'order' });

// 📦 Quan hệ giữa OrderDetail và Product
Product.hasMany(OrderDetail, { foreignKey: 'productID', as: 'productOrders', onDelete: 'CASCADE' });
OrderDetail.belongsTo(Product, { foreignKey: 'productID', as: 'product' });

module.exports = {
    User,
    Cart,
    Product,
    CartItem,
    Order,
    OrderDetail,
};
