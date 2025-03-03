const express = require('express');
const sequelize = require('./src/config/syncDatabase'); 
const cartRoutes = require('./src/routers/client/cartRoutes'); 
const orderRoutesAdmin = require('./src/routers/admin/orderRoutesAdmin');
const orderRoutesClient = require('./src/routers/client/orderRoutersClient');
const cors = require("cors");

const app = express();
app.use(cors()); 
app.use(express.json());
app.use('/api', cartRoutes);
app.use('/api', orderRoutesAdmin);
app.use('/api', orderRoutesClient);

// Chỉ gọi `sequelize.sync()` tại đây
sequelize.sync({ force: false }) 
    .then(() => {
        console.log('Database synchronized...');
        
        app.listen(3000, () => {
            console.log('Server running at http://localhost:3000');
        });
    })
    .catch((error) => {
        console.error('Failed to synchronize database:', error);
    });  
