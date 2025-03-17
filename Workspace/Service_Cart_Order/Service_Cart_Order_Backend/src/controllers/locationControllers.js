const axios = require("axios");

const getLocation = async (req, res) => {
    const { lat, lng } = req.query;

    if (!lat || !lng) {
        return res.status(400).json({ error: "Vui lòng cung cấp tọa độ lat và lng!" });
    }

    const latitude = parseFloat(lat);
    const longitude = parseFloat(lng);

    if (isNaN(latitude) || isNaN(longitude)) {
        return res.status(400).json({ error: "Tọa độ không hợp lệ!" });
    }

    const geocodeUrl = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${latitude}&lon=${longitude}`;

    try {
        const response = await axios.get(geocodeUrl);
        const data = response.data;

        if (!data || !data.address) {
            return res.status(500).json({ error: "Không thể lấy địa chỉ từ tọa độ!" });
        }

        const address = data.display_name || "Không tìm thấy địa chỉ";

        res.json({
            message: "Lấy vị trí thành công!",
            location: { latitude, longitude },
            address: address
        });
    } catch (error) {
        console.error("Lỗi gọi OpenStreetMap API:", error.message);
        res.status(500).json({ error: "Lỗi khi lấy địa chỉ từ OpenStreetMap API" });
    }
};

module.exports = { getLocation };
