const express = require('express');
const sequelize = require('./src/config/syncDatabase'); 
const cartRoutes = require('./src/routers/client/cartRoutes'); 
const orderRoutesAdmin = require('./src/routers/admin/orderRoutesAdmin');
const orderRoutesClient = require('./src/routers/client/orderRoutersClient');
const paymentRoutes = require('./src/routers/client/paymentRouters');
const locationRoutes = require('./src/routers/client/locationRoutes');
const cors = require("cors");

const app = express();

app.use(cors()); 
app.use(express.urlencoded({ extended: true }));

app.use(express.json());
app.use('/api', cartRoutes);
app.use('/api/admin', orderRoutesAdmin);
app.use('/api', orderRoutesClient);
app.use('/api/payment', paymentRoutes);
app.use('/api/location', locationRoutes);

// Chỉ gọi `sequelize.sync()` tại đây
sequelize.sync({ force: false }) 
    .then(() => {
        console.log('Database synchronized...');
        
        app.listen(8106, () => {
            console.log('Server running at http://localhost:8106');
        });
    })
    .catch((error) => {
        console.error('Failed to synchronize database:', error);
    });
