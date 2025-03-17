const {getLocation} = require('../../controllers/locationControllers');
const express = require('express');
const router = express.Router();

router.get('/', getLocation);

module.exports = router;

