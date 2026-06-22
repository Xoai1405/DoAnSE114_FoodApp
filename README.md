# ỨNG DỤNG ĐẶT ĐỒ ĂN ONLINE - CRAVECART 

Đây là kho lưu trữ mã nguồn của Ứng dụng đặt đồ ăn Online - CRAVECART, được phát triển trong khuôn khổ đồ án môn học Phát triển Ứng dụng Di động. Ứng dụng mang đến giải pháp đặt món ăn nhanh chóng, quản lý giỏ hàng và trải nghiệm người dùng mượt mà trên nền tảng Android.

## 1. Yêu cầu hệ thống & Môi trường phát triển

Để cài đặt và khởi chạy dự án một cách ổn định, môi trường máy tính của bạn cần đáp ứng các tiêu chuẩn sau:

* **IDE:** Android Studio (các phiên bản mới hỗ trợ Gradle 8+)
* **Java Development Kit (JDK):** Java 11 (Cấu hình đồng bộ trong `sourceCompatibility` và `targetCompatibility`)
* **Ngôn ngữ lập trình:** Java
* **Hệ thống Build:** Gradle sử dụng Kotlin DSL (`build.gradle.kts`)

**Cấu hình Android SDK**

| Thông số | Phiên bản | Ghi chú |
| :--- | :--- | :--- |
| **Compile SDK** | 36 | Phiên bản biên dịch mới nhất |
| **Target SDK** | 36 | Phiên bản tối ưu hóa mục tiêu |
| **Minimum SDK** | 24 | Hỗ trợ từ Android 7.0 (Nougat) trở lên |

## 2. Cấu hình Dịch vụ Đám mây & Bảo mật

Dự án này sử dụng hệ sinh thái Firebase và Cloudinary làm Backend-as-a-Service (BaaS). Để ứng dụng có thể kết nối với cơ sở dữ liệu và xác thực, bạn cần cấu hình các tệp tin dịch vụ:

1. **Cấu hình Google Services (Firebase):**
   * Đảm bảo bạn đã có tệp cấu hình `google-services.json` (có thể tải về từ Firebase Console của dự án).
   * Sao chép và đặt tệp `google-services.json` vào bên trong thư mục `app/` của dự án.
2. **Các dịch vụ cần được kích hoạt trên Firebase:**
   * Firebase Authentication (Đăng nhập Email/Mật khẩu & Google Sign-In)
   * Firebase Realtime Database & Cloud Firestore
   * Firebase Cloud Storage
3. **Cấu hình Cloudinary (Quản lý media):**
   * Nếu bạn cấu hình Cloudinary URL hoặc thông tin tài khoản (Cloud name, API Key, API Secret) để upload ảnh, hãy chắc chắn cập nhật các thông số này trong tệp hằng số hoặc cấu hình nội bộ của Java.

## 3. Kiến trúc & Các thư viện cốt lõi (Dependencies)

Dự án được xây dựng với giao diện thân thiện, sử dụng `ViewBinding` và tích hợp các dịch vụ đám mây mạnh mẽ:

* **User Interface (UI):** Cấu trúc giao diện bằng XML, sử dụng `ViewBinding` kết hợp hệ thống `Material Design`, `ConstraintLayout`, và `RecyclerView`.
* **Backend & Cơ sở dữ liệu:**
    * `Firebase Realtime Database` & `Firebase Firestore`: Lưu trữ và đồng bộ hóa dữ liệu trực tuyến (danh sách món ăn, chi tiết đơn hàng, người dùng).
    * `Firebase Storage` & `Cloudinary`: Tối ưu hóa việc lưu trữ hình ảnh ứng dụng.
* **Authentication (Xác thực):**
    * Tích hợp `Firebase Auth` và `Google Sign-In` (`credentials`, `play-services-auth`) cho phép xác thực người dùng một cách an toàn.
* **Image Loading:**
    * Sử dụng đồng thời hai bộ thư viện tải ảnh hiệu suất cao: `Glide` (v4.16.0) và `Fresco` (v3.2.0) giúp xử lý mượt mà danh sách hình ảnh đồ ăn.
* **Data Serialization:**
    * Sử dụng `Gson` (v2.10.1) để dễ dàng chuyển đổi dữ liệu giữa dạng Object và chuỗi JSON.

## 4. Hướng dẫn Cài đặt & Triển khai (Build & Run)

Mọi thành viên hoặc người kiểm thử có thể khởi chạy dự án theo quy trình 4 bước sau:

**Bước 1: Clone dự án và truy cập thư mục**
Mở Terminal hoặc Git Bash, thực hiện lệnh clone kho lưu trữ:
git clone https://github.com/xoai1405/doanse114_foodapp.git


**Bước 2: Tích hợp tệp cấu hình Firebase (google-services.json)

Truy cập vào liên kết Google Drive của dự án để tải xuống tệp cấu hình: https://drive.google.com/drive/folders/1NQvvbnQXdD04SAu-Rkn7_3jP1_c7poAY?usp=drive_link.

Di chuyển/Sao chép tệp google-services.json vừa tải về vào đúng thư mục mẫu sau:
DoAnSE114_FoodApp/FoodApp/
Lưu ý quan trọng về Bảo mật:

Tệp google-services.json chứa các thông tin cực kỳ nhạy cảm của dự án backend bao gồm: API Keys, Project ID, Client IDs, và Database URLs.

Nếu tệp này bị lộ công khai lên các kho lưu trữ công cộng (như GitHub Public), kẻ xấu có thể lợi dụng các thông số này để truy cập trái phép vào Database, gửi yêu cầu ảo làm cạn kiệt tài nguyên đám mây, đánh cắp dữ liệu người dùng, hoặc phát sinh chi phí dịch vụ Firebase ngoài ý muốn.

Do đó, tệp này luôn được thêm vào .gitignore để tránh bị commit lên GitHub. Thành viên trong nhóm hoặc hội đồng chấm bài bắt buộc phải tải tệp thủ công từ link Drive nội bộ bên trên và tự tích hợp vào thư mục mã nguồn trước khi biên dịch.


**Bước 3: Đồng bộ hóa dự án (Sync Gradle)

Mở thư mục gốc của dự án bằng phần mềm Android Studio.

Hệ thống sẽ tự động kích hoạt tiến trình tải thư viện, hoặc bạn có thể chủ động nhấn vào File > Sync Project with Gradle Files.

Chờ đợi trong vài phút để Gradle kết nối Internet, tải xuống toàn bộ thư viện (Firebase, Glide, Fresco,...) và hoàn tất quá trình lập chỉ mục.



**Bước 4: Khởi chạy ứng dụng

Chuẩn bị thiết bị: Kết nối một thiết bị di động Android thật (đã bật Developer Options và USB Debugging) hoặc khởi chạy một Máy ảo (Emulator) có cấu hình API từ 24 trở lên.

Thực thi: Chọn module app trên thanh công cụ và nhấn nút Run (biểu tượng hình tam giác màu xanh) hoặc nhấn tổ hợp phím Shift + F10.

Ghi chú xử lý sự cố: Trong trường hợp Gradle báo lỗi không tìm thấy class từ ViewBinding hoặc lỗi cache cũ, hãy dọn dẹp bằng cách chọn Build > Clean Project, sau đó chọn Build > Rebuild Project.
"""
