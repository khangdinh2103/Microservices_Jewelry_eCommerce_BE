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

const getDistance = async (req, res) => {
    const { startLat, startLng, endLat, endLng } = req.query;
  
    if (!startLat || !startLng || !endLat || !endLng) {
      return res.status(400).json({ error: 'Vui lòng cung cấp đủ tọa độ!' });
    }
  
    const apiKey = '0c5748d0-e17f-4e95-a409-37d6b1edc231';  // Thay thế bằng GraphHopper API Key
    const graphHopperUrl = `https://graphhopper.com/api/1/route?point=${startLat},${startLng}&point=${endLat},${endLng}&vehicle=car&key=${apiKey}`;
  
    try {
      const response = await axios.get(graphHopperUrl);
      const result = response.data;
  
      const distanceMeters = result.paths[0].distance;  // Đo khoảng cách (mét)
      const durationSeconds = result.paths[0].time;    // Thời gian (giây)
  
      res.json({
        message: 'Tính khoảng cách thành công!',
        from: { lat: startLat, lng: startLng },
        to: { lat: endLat, lng: endLng },
        distance_km: (distanceMeters / 1000).toFixed(2),  // Chuyển từ mét sang km
        duration_minutes: (durationSeconds / 60000).toFixed(1),  // Chuyển từ giây sang phút
      });
    } catch (error) {
      console.error('Lỗi gọi GraphHopper API:', error.message);
      res.status(500).json({ error: 'Không thể tính khoảng cách từ GraphHopper' });
    }
  };

module.exports = { getLocation, getDistance };
