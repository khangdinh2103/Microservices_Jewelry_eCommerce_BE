// server.js
const express = require('express');
const sequelize = require('./src/config/syncDatabase'); 
const cartRoutes = require('./src/routers/client/cartRoutes'); 
const cors = require("cors");

const app = express();
app.use(express.json());

app.use('/api', cartRoutes);
app.use(cors());

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
