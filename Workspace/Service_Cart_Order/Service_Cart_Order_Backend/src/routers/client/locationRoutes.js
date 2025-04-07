const {getLocation, getDistance} = require('../../controllers/locationControllers');
const express = require('express');
const router = express.Router();

router.get('/', getLocation);
router.get('/distance', getDistance);


module.exports = router;

