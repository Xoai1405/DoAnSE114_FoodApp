package Helper;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class Cloud_Service
{
    public static void uploadImageToCloudinary(Uri imageUri, String firebaseFieldName) {
        uploadImageToCloudinary(imageUri, firebaseFieldName, "Food");
    }

    // cho phép chỉ định folder riêng (vd: "Avatars" cho ảnh đại diện)
    public static void uploadImageToCloudinary(Uri imageUri, String publicId, String folder) {
        MediaManager.get().upload(imageUri)
                .unsigned("food_app_unsigned")
                .option("public_id", publicId)
                .option("folder", folder)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d("Cloudinary", "Đang bắt đầu upload...");
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d("Cloudinary", "Upload thành công! Tên file: " + publicId);
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
        loadCloudinaryImageWithGlide(firebaseFieldName, "Food", imageView, null);
    }

    // cho phép chỉ định folder, không cần callback
    public static void loadCloudinaryImageWithGlide(String publicId, String folder, ImageView imageView) {
        loadCloudinaryImageWithGlide(publicId, folder, imageView, null);
    }

    //  có callback để biết load ảnh thành công hay thất bại.
    // Dùng cho Avatar — nếu user chưa từng upload, Cloudinary trả lỗi 404,
    // lúc đó callback.onError() được gọi để hiện lại chữ cái viết tắt thay vì icon lỗi.
    public static void loadCloudinaryImageWithGlide(String publicId, String folder, ImageView imageView, ImageLoadCallback callback) {
        String sanitizedName = publicId.replace(" ", "_");
        String fullPublicId =  sanitizedName;

        // 🔥 SỬA DÒNG NÀY: Thêm .format("jpg") vào trước chữ .generate()
        String imageUrl = MediaManager.get().url()
                .secure(true)
                .format("jpg")
                .generate(fullPublicId);

        // Giữ nguyên đoạn Log cũ để kiểm tra nếu muốn
        Log.d("CHECK_NAME", "Link URL cuối cùng: " + imageUrl);

        // gắn listener để bắt sự kiện thành công/thất bại,
        // trả về false ở cả 2 nhánh để Glide vẫn tự xử lý ảnh như bình thường (không đổi hành vi cũ)
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_dialog_alert)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        if (callback != null) callback.onError();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (callback != null) callback.onSuccess();
                        return false;
                    }
                })
                .into(imageView);
    }

    //interface để ProfileFragment biết khi nào nên hiện ảnh thật / hiện lại chữ viết tắt
    public interface ImageLoadCallback {
        void onSuccess();
        void onError();
    }
}