// ==========================================
// CẤU HÌNH NHÀ CUNG CẤP VIETQR
// ==========================================
const BANK_CONFIG = {
    BANK_ID: "MB",               // Mã ngân hàng (VD: MB, VCB, TCB, ACB, VPB,...)
    ACCOUNT_NO: "0351234567",    // Thay bằng Số tài khoản của bạn
    ACCOUNT_NAME: "DUNG DORYS"  // Tên chủ tài khoản
};

const token = "pk.eyJ1IjoibnZ0MjkwNjIwMDUiLCJhIjoiY21zMzE0bHdhMWNvNTJ3cG1seHhwd3VoeiJ9.3ymS4xHPOHOT2etqRtT39Q";
mapboxgl.accessToken = token;

const studioAddress = "43 Trung Kính, Trung Hòa, Cầu Giấy, Hà Nội";
const studioLng = 105.8008;
const studioLat = 21.0207;

// Lấy giá dịch vụ được truyền từ file HTML
const servicePrice = Number(window.SERVICE_PRICE || 0);

const studioRadio = document.getElementById("studio");
const homeRadio = document.getElementById("home");
const addressBox = document.getElementById("addressBox");

// Khởi tạo Map
const map = new mapboxgl.Map({
    container: "map",
    style: "mapbox://styles/mapbox/streets-v12",
    center: [studioLng, studioLat],
    zoom: 13
});

new mapboxgl.Marker({ color: "red" })
    .setLngLat([studioLng, studioLat])
    .setPopup(new mapboxgl.Popup().setHTML("<b>Studio Makeup</b><br>43 Trung Kính"))
    .addTo(map);

let customerMarker = new mapboxgl.Marker({ color: "blue", draggable: true });

customerMarker.on('dragend', async () => {
    const lngLat = customerMarker.getLngLat();
    await updateAddressFromCoordinate(lngLat.lng, lngLat.lat);
    calculateDistanceByCoordinate(lngLat.lng, lngLat.lat);
});

function toggleAddress() {
    if (homeRadio && homeRadio.checked) {
        addressBox.style.display = "block";
        setTimeout(() => map.resize(), 200);
    } else if (addressBox) {
        addressBox.style.display = "none";
        updateFee(0);
    }
    updatePricingAndQR();
}

if (studioRadio && homeRadio) {
    studioRadio.addEventListener("change", toggleAddress);
    homeRadio.addEventListener("change", toggleAddress);
    toggleAddress();
}

map.on("click", async function (e) {
    if (!homeRadio || !homeRadio.checked) return;
    const { lng, lat } = e.lngLat;
    customerMarker.setLngLat([lng, lat]).addTo(map);
    await updateAddressFromCoordinate(lng, lat);
    calculateDistanceByCoordinate(lng, lat);
});

async function updateAddressFromCoordinate(lng, lat) {
    const url = `https://api.mapbox.com/geocoding/v5/mapbox.places/${lng},${lat}.json?access_token=${token}&language=vi`;
    try {
        const res = await fetch(url);
        const data = await res.json();
        if (data.features && data.features.length > 0) {
            const addressInput = document.querySelector("[name='address']");
            if (addressInput) addressInput.value = data.features[0].place_name;
        }
    } catch (err) {
        console.error("Lỗi lấy địa chỉ từ tọa độ:", err);
    }
}

async function calculateDistanceByCoordinate(lng, lat) {
    const url = `https://api.mapbox.com/directions/v5/mapbox/driving/${studioLng},${studioLat};${lng},${lat}?access_token=${token}`;
    try {
        const res = await fetch(url);
        const data = await res.json();
        if (!data.routes || data.routes.length === 0) {
            alert("Không tính được đường đi đến vị trí này.");
            return;
        }
        const distanceKm = data.routes[0].distance / 1000;
        updateFee(distanceKm);
    } catch (err) {
        console.error("Lỗi tính khoảng cách:", err);
    }
}

const addressInput = document.querySelector("[name='address']");
if (addressInput) {
    addressInput.addEventListener("blur", async function () {
        const address = this.value.trim();
        if (!address) return;

        const url = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(address)}.json?access_token=${token}&language=vi`;
        try {
            const res = await fetch(url);
            const data = await res.json();
            if (data.features && data.features.length > 0) {
                const [lng, lat] = data.features[0].center;
                customerMarker.setLngLat([lng, lat]).addTo(map);
                map.flyTo({ center: [lng, lat], zoom: 14 });
                calculateDistanceByCoordinate(lng, lat);
            }
        } catch (err) {
            console.error("Lỗi chuyển đổi địa chỉ:", err);
        }
    });
}

let searchTimeout;
const searchInput = document.getElementById("searchAddress");
if (searchInput) {
    searchInput.addEventListener("input", function () {
        clearTimeout(searchTimeout);
        const keyword = this.value.trim();
        if (keyword.length < 3) {
            document.getElementById("searchResult").innerHTML = "";
            return;
        }
        searchTimeout = setTimeout(() => searchPlace(keyword), 300);
    });
}

async function searchPlace(keyword) {
    const url = `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(keyword)}.json?access_token=${token}&language=vi&country=vn&limit=5`;
    try {
        const res = await fetch(url);
        const data = await res.json();
        showResult(data.features || []);
    } catch (err) {
        console.error("Lỗi tìm kiếm địa chỉ:", err);
    }
}

function showResult(features) {
    const box = document.getElementById("searchResult");
    if (!box) return;
    box.innerHTML = "";
    features.forEach(place => {
        const item = document.createElement("button");
        item.type = "button";
        item.className = "list-group-item list-group-item-action text-start";
        item.innerHTML = place.place_name;
        item.onclick = function () {
            selectPlace(place);
        };
        box.appendChild(item);
    });
}

function selectPlace(place) {
    const addressInput = document.querySelector("[name='address']");
    if (addressInput) addressInput.value = place.place_name;
    
    document.getElementById("searchAddress").value = "";
    document.getElementById("searchResult").innerHTML = "";

    const [lng, lat] = place.center;
    customerMarker.setLngLat([lng, lat]).addTo(map);
    map.flyTo({ center: [lng, lat], zoom: 14 });
    calculateDistanceByCoordinate(lng, lat);
}

function updateFee(distance) {
    const distanceText = document.getElementById("distanceText");
    if (distanceText) distanceText.innerText = distance.toFixed(1) + " km";

    let fee = 0;
    if (distance === 0 || distance <= 10) {
        fee = 0;
    } else if (distance <= 20) {
        fee = 100000;
    } else if (distance <= 100) {
        fee = 200000;
    } else if (distance <= 200) {
        fee = 300000;
    } else {
        alert("Xin lỗi, studio chỉ hỗ trợ phục vụ tận nơi trong phạm vi 200 km.");
        document.getElementById("feeText").innerText = "Không hỗ trợ";
        document.getElementById("totalPrice").innerText = "---";
        return;
    }

    const feeText = document.getElementById("feeText");
    const distanceKmInput = document.getElementById("distanceKm");
    const extraFeeInput = document.getElementById("extraFee");

    if (feeText) feeText.innerText = fee.toLocaleString("vi-VN") + " VNĐ";
    if (distanceKmInput) distanceKmInput.value = distance.toFixed(2);
    if (extraFeeInput) extraFeeInput.value = fee;

    // Cập nhật lại tổng tiền dịch vụ & Mã QR
    updatePricingAndQR();
}

// ==========================================
// LÝ GIẢI & TỰ ĐỘNG TÍNH TOÁN CỌC + GEN VIETQR
// ==========================================
function updatePricingAndQR() {
    let extraFee = 0;
    if (homeRadio && homeRadio.checked) {
        const extraFeeInput = document.getElementById("extraFee");
        extraFee = extraFeeInput ? (parseFloat(extraFeeInput.value) || 0) : 0;
    }

    // 1. Tổng chi phí thực tế = Giá dịch vụ + Phụ phí di chuyển
    const totalPrice = servicePrice + extraFee;

    // Hiển thị tổng giá lên giao diện
    const totalPriceText = document.getElementById("totalPrice");
    if (totalPriceText) {
        totalPriceText.innerText = totalPrice.toLocaleString("vi-VN") + " VNĐ";
    }

    // 2. Xử lý tính cọc / thanh toán full
    const paymentTypeChecked = document.querySelector('input[name="paymentType"]:checked');
    const paymentType = paymentTypeChecked ? paymentTypeChecked.value : 'DEPOSIT';

    let amountToPay = totalPrice;
    let notePrefix = "Chuyen khoan full";

    if (paymentType === 'DEPOSIT') {
        amountToPay = Math.round(totalPrice * 0.3); // Tính tròn 30%
        notePrefix = "Coc 30pct";
    }

    // 3. Cập nhật mã QR Code VietQR
    const phoneInput = document.querySelector('input[name="phone"]');
    const userPhone = phoneInput ? (phoneInput.value.trim() || "KHACH") : "KHACH";
    const transferNote = `${notePrefix} ${userPhone}`;

    const vietQRImg = document.getElementById("vietQRImg");
    const qrAmountText = document.getElementById("qrAmountText");
    const qrNoteText = document.getElementById("qrNoteText");

    if (qrAmountText) qrAmountText.innerText = amountToPay.toLocaleString("vi-VN") + " VNĐ";
    if (qrNoteText) qrNoteText.innerText = transferNote;

    if (vietQRImg) {
        // Link API sinh QR chuẩn từ VietQR.io
        const qrUrl = `https://img.vietqr.io/image/${BANK_CONFIG.BANK_ID}-${BANK_CONFIG.ACCOUNT_NO}-compact2.png?amount=${amountToPay}&addInfo=${encodeURIComponent(transferNote)}&accountName=${encodeURIComponent(BANK_CONFIG.ACCOUNT_NAME)}`;
        vietQRImg.src = qrUrl;
    }
}

// Lắng nghe các sự kiện thay đổi trên giao diện để tính toán lại ngay lập tức
document.addEventListener("DOMContentLoaded", () => {
    // Gọi tính ban đầu
    updatePricingAndQR();

    // Sự kiện khi chọn chuyển đổi Cọc / Thanh toán Full
    const paymentRadios = document.querySelectorAll('input[name="paymentType"]');
    paymentRadios.forEach(radio => {
        radio.addEventListener("change", updatePricingAndQR);
    });

    // Sự kiện khi gõ số điện thoại (để cập nhật vào nội dung QR)
    const phoneInput = document.querySelector('input[name="phone"]');
    if (phoneInput) {
        phoneInput.addEventListener("input", updatePricingAndQR);
    }
});