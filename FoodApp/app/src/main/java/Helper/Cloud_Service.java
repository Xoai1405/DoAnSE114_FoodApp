package Helper;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import java.util.Map;
public class Cloud_Service
{
    public static void uploadImageToCloudinary(Uri imageUri, String firebaseFieldName) {
        MediaManager.get().upload(imageUri)
                .unsigned("food_app_unsigned")
                .option("public_id", firebaseFieldName)
                .option("folder", "Food")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Đang bắt đầu upload...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d("Cloudinary", "Upload thành công! Tên file: " + firebaseFieldName);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e("Cloudinary", "Lỗi upload: " + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    public static void loadCloudinaryImageWithGlide(String firebaseFieldName, ImageView imageView) {
        String sanitizedName = firebaseFieldName.replace(" ", "_");
        String fullPublicId =  sanitizedName;

        // 🔥 SỬA DÒNG NÀY: Thêm .format("jpg") vào trước chữ .generate()
        String imageUrl = MediaManager.get().url()
                .secure(true)
                .format("jpg")
                .generate(fullPublicId);

        // Giữ nguyên đoạn Log cũ để kiểm tra nếu muốn
        Log.d("CHECK_NAME", "Link URL cuối cùng: " + imageUrl);

        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .centerCrop()
                .into(imageView);
    }
}
