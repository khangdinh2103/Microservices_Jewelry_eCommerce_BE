const { Sequelize } = require('sequelize');

const sequelize = new Sequelize('JEC', 'jec_admin', 'jec_admin', {
    host: 'localhost',
    port: 6543, 
    dialect: 'postgres',
    dialectOptions: {
        ssl: false,
    },
    logging: false,
    pool: {
        max: 5,
        min: 0,
        acquire: 30000,
        idle: 10000
    }
});

sequelize.authenticate()
    .then(() => console.log('PostgreSQL database connected...'))
    .catch(err => console.error('PostgreSQL connection error:', err));

module.exports = sequelize;