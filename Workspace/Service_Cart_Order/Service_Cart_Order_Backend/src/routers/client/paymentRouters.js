const express = require("express");
const { paymentCtrl, verifyPaymentMomoCtrl, confirmTransactionMomoCtrl } = require("../../controllers/paymentControllers");
const router = express.Router();

router.post("/", paymentCtrl);
router.post("/verify", verifyPaymentMomoCtrl);
router.post("/confirmTransaction", confirmTransactionMomoCtrl);

module.exports = router;