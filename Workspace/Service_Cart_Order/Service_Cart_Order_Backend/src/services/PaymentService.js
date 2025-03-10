const axios = require("axios");
const crypto = require('crypto');
// const { createInvoiceSvc, createInvoiceDetailSvc } = require("../invoice/invoiceService");

const paymentSvc = async (dataPayment) => {
    const { items, userInfo, amount } = dataPayment;
    const accessKey = 'F8BBA842ECF85';
    const secretKey = 'K951B6PE1waDMi640xX08PD3vg6EkVlz';
    const orderInfo = 'Thanh toán đơn hàng qua MoMo';
    const partnerCode = 'MOMO';
    const redirectUrl = 'https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b';
    const ipnUrl = 'https://fef4-58-186-29-95.ngrok-free.app/v1/api/payment/verify';
    const requestType = "payWithMethod";
    const orderId = partnerCode + new Date().getTime();
    const requestId = orderId;
    const extraData = '';
    const autoCapture = true;
    const lang = 'vi';

    const rawSignature = `accessKey=${accessKey}&amount=${amount}&extraData=${extraData}&ipnUrl=${ipnUrl}&orderId=${orderId}&orderInfo=${orderInfo}&partnerCode=${partnerCode}&redirectUrl=${redirectUrl}&requestId=${requestId}&requestType=${requestType}`;

    const signature = crypto.createHmac('sha256', secretKey)
        .update(rawSignature)
        .digest('hex');

    const requestBody = {
        partnerCode,
        storeId: "MomoTestStore",
        requestId,
        amount,
        orderId,
        orderInfo,
        redirectUrl,
        ipnUrl,
        requestType,
        extraData,
        items,
        userInfo,
        autoCapture,
        lang,
        signature,
    };

    try {
        const result = await axios.post(
            "https://test-payment.momo.vn/v2/gateway/api/create",
            requestBody,
            {
                headers: {
                    "Content-Type": "application/json",
                },
            }
        );
        return result.data;
    } catch (error) {
        throw {
            statusCode: 500,
            message: 'Internal server error: ' + error.message,
        };
    }
};

const verifyPaymentMomoSvc = (dataFromMomo) => {
    if (dataFromMomo) {
        return dataFromMomo;
    } else {
        throw {
            statusCode: 500,
            message: 'Internal server error: Momo has not responded yet',
        };
    }
}
const confirmTransactionMomoSvc = async (orderId) => {
    // const {userInfo, amount, orderId } = dataPayment;
    let secretKey = 'K951B6PE1waDMi640xX08PD3vg6EkVlz';
    let accessKey = 'F8BBA842ECF85';
    const rawSignature = `accessKey=${accessKey}&orderId=${orderId}&partnerCode=MOMO&requestId=${orderId}`;

    const signature = crypto
        .createHmac('sha256', secretKey)
        .update(rawSignature)
        .digest('hex');

    const requestBody = {
        partnerCode: 'MOMO',
        requestId: orderId,
        orderId: orderId,
        signature: signature,
        lang: 'vi',
    };
    try {
        const result = await axios.post(
            "https://test-payment.momo.vn/v2/gateway/api/query",
            requestBody,
            {
                headers: {
                    "Content-Type": "application/json",
                },
            }
        );
        console.log("RESS MOMO CONFIRM:     ", result);
        // if (result.data.resultCode === 0) {
        //     // const dataInvoice = {
        //     //     customer: userInfo.id,
        //     //     paymentStatus: "Completed",
        //     //     totalAmount: amount,
        //     //     shippingAddress: userInfo.address,
        //     //     invoiceDate: Date.now(),
        //     // };
        //     // const resultInvoice = await createInvoiceSvc(dataInvoice);
        //     // console.log("Invoice : ", resultInvoice);
        //     // let dataInvoiceDetail = items.map((product) => {
        //     //     return {
        //     //         invoice: resultInvoice._id.toString(),
        //     //         product: product.id,
        //     //         productName: product.name,
        //     //         quantity: product.quantity,
        //     //         unitPrice: product.amount,
        //     //         subtotal: product.total
        //     //     }
        //     // });
        //     // const resultInvoiceDetail = await createInvoiceDetailSvc(dataInvoiceDetail);
        //     // console.log("DETAIL : ", resultInvoiceDetail);
        // }
        return result.data;
    } catch (error) {
        throw {
            statusCode: 500,
            message: 'Internal server error: ' + error.message,
        };
    }
}

module.exports = {
    paymentSvc,
    verifyPaymentMomoSvc,
    confirmTransactionMomoSvc
};
