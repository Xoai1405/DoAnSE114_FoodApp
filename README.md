![alt text](https://www.uit.edu.vn/sites/vi/files/banner.png)
   
# MÔN HỌC: NHẬP MÔN ỨNG DỤNG DI ĐỘNG- SE114.Q22 
# TÊN ĐỀ TÀI: ỨNG DỤNG ĐẶT ĐỒ ĂN ONLINE-CRAVECART
## 1. THÔNG TIN NHÓM:
### Tên nhóm: Nhóm 8
### Thành viên nhóm:
- Hà Gia Bảo – 24520150
- Hà Tuấn Hùng – 24520584
- Bùi Bá Bổng – 24520203
- Phạm Đan Trường – 24521898


### Giảng viên hướng dẫn: Nguyễn Tấn Toàn

---

## 2. ĐẶT VẤN ĐỀ
- Trong nhịp sống hiện đại, nhu cầu đặt đồ ăn trực tuyến ngày càng phổ biến. Nhằm mang lại sự tiện lợi, tiết kiệm thời gian và tối ưu hóa trải nghiệm ẩm thực cho người dùng, chúng tôi đã phát triển ứng dụng FoodApp.
- Ứng dụng giúp người dùng dễ dàng tìm kiếm các món ăn ngon, xem chi tiết đánh giá, quản lý giỏ hàng và thực hiện thanh toán một cách nhanh chóng ngay trên thiết bị di động.

---

## 3. TÍNH NĂNG CHÍNH CỦA ỨNG DỤNG
*(Dưới đây là các tính năng cốt lõi được xây dựng trong hệ thống)*

### 🔑 Xác thực & Quản lý người dùng
- Đăng nhập, Đăng ký tài khoản (Tích hợp xác thực).
- Khôi phục mật khẩu khi quên.
- Quản lý thông tin hồ sơ cá nhân (Profile).

### 🍔 Khám phá & Tìm kiếm món ăn
- **Màn hình chính (Dashboard/Home):** Hiển thị các món ăn nổi bật (Best Foods), phân loại món ăn theo danh mục (Categories).
- **Món ăn yêu thích:** Cho phép người dùng thả tim và lưu lại các món ăn yêu thích.
- **Khuyến mãi (Deals):** Cập nhật các ưu đãi và combo giảm giá.

### 🛒 Giỏ hàng & Thanh toán
- Xem thông tin chi tiết từng món ăn (thành phần, giá cả, thời gian giao hàng).
- Thêm món ăn vào giỏ hàng, tùy chỉnh số lượng.
- Quản lý mã giảm giá (Voucher) áp dụng cho đơn hàng.
- Màn hình Checkout xác nhận thông tin địa chỉ giao hàng và tổng tiền thanh toán.

---

## 4. GIAO DIỆN ỨNG DỤNG (SCREENSHOTS)
Dưới đây là một số hình ảnh thực tế từ ứng dụng:

### Màn hình chính và Danh mục món ăn

<p align="center">
  <img width="372" height="781" alt="Home Screen" src="https://github.com/user-attachments/assets/31f28ddb-036f-4c08-81f3-2ffd206e5b79" />
</p>

---

### Quản lý Giỏ hàng và Thanh toán

<p align="center">
  <img width="368" height="778" alt="Cart and Checkout" src="https://github.com/user-attachments/assets/03566d29-5f33-459d-9a30-149a079873a0" />
</p>

---

### Quản lý Profile người dùng 

<p align="center">
  <img width="372" height="782" alt="Profile Screen" src="https://github.com/user-attachments/assets/f44528a8-ddeb-4bdb-bf15-b6823ec1bef8" />
</p>

---

### Quản lý món ăn yêu thích

<p align="center">
  <img width="366" height="775" alt="Favorite Screen" src="https://github.com/user-attachments/assets/e3708360-4a03-4538-87e4-6a1c6a43612e" />
</p>

---

## 5. KIẾN TRÚC & CÔNG NGHỆ SỬ DỤNG
- **Nền tảng:** Android (Ngôn ngữ Java).
- **Môi trường phát triển:** Android Studio.
- **Cấu trúc UI:** Sử dụng `RecyclerView` kết hợp với các `Adapter` (CartAdapter, CategoryAdapter, FoodListAdapter...) để tối ưu hóa việc hiển thị danh sách.
- **Lưu trữ dữ liệu:** - Sử dụng `TinyDB` để lưu trữ dữ liệu giỏ hàng cục bộ (Local Storage).
  - Tích hợp đọc dữ liệu định dạng JSON.

---
