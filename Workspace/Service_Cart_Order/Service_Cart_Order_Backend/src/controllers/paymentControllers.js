const { paymentSvc, verifyPaymentMomoSvc, confirmTransactionMomoSvc } = require("../services/PaymentService");

const paymentCtrl = async (req, res) => {
    try {
        const result = await paymentSvc(req.body);
        return res.status(200).json({
            success: true,
            message: "Payment is successful!",
            data: result
        });
    } catch (error) {
        return res.status(error.statusCode || 500).json({
            success: false,
            message: "Thanh toán không thành công",
            error: error.message
        });
    }
};

const verifyPaymentMomoCtrl = (req, res) => {
    try {
        const result = verifyPaymentMomoSvc(req.body);
        return res.status(200).json({
            success: true,
            message: "Payment verify is successful!",
            data: result
        });
    } catch (error) {
        return res.status(error.statusCode || 500).json({
            success: false,
            message: "Payment verify is failed!",
            error: error.message
        });
    }
};

const confirmTransactionMomoCtrl = async (req, res) => {
    try {
        console.log("DATAPAYMENT:", req.body);
        const result = await confirmTransactionMomoSvc(req.body.id);
        return res.status(200).json({
            success: true,
            message: "Confirm transaction is successful!",
            data: result
        });
    } catch (error) {
        return res.status(error.statusCode || 500).json({
            success: false,
            message: "Confirm transaction is failed!",
            error: error.message
        });
    }
};

module.exports = {
    paymentCtrl,
    verifyPaymentMomoCtrl,
    confirmTransactionMomoCtrl
};
