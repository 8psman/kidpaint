package com.namnv.project.kidpaint;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.namnv.project.kidpaint.object.PaintReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 8psman on 10/27/2014.
 */
public class Application extends android.app.Application{

    /** TAG debug */
    public static final String TAG = "kidpaint";

    /** Tên thư mục chứa ảnh */
    public static final String PAINT_FOLDER_NAME = "kidpaint";

    /** Tên lưu trữ cài đặt */
    public static final String SETTING_PREF_NAME = "kidpaint.settings";

    /** Định dạng ảnh lưu trữ */
    public String paintExtension = ".jpg";

    /** Trả về đuôi mở rộng của ảnh */
    public String getCurrentPaintExtension(){
        return paintExtension;
    }

    /** Giữ thể hiện hiện tại của Application */
    private static Application instance;

    /** Lưu trữ cài đặt load ảnh thumbnail */
    static DisplayImageOptions imageDisplayOptions;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        /** Cài đặt đối tượng load ảnh ImageLoader*/
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        /** Cài đặt chính sách load ảnh thumbnail cho các bức vẽ */
        imageDisplayOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false)
                .delayBeforeLoading(500)
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new SimpleBitmapDisplayer())
                .handler(new Handler())
                .build();
    }

    /** Trả về cài đặt tải ảnh thumbnail */
    public static DisplayImageOptions getImageDisplayOptions(){
        return imageDisplayOptions;
    }

    /** Trả về thể hiện hiện tại của Application */
    public static Application getInstance(){
        return instance;
    }

    /** Trả về thiết lập của ứng dụng */
    public SharedPreferences getSetting(){
        return getSharedPreferences(SETTING_PREF_NAME, MODE_PRIVATE);
    }

    /** Trả về đường dẫn của thư mục chứa ảnh */
    public static String getLocalPaintDirectory(){
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String paintDirectory = externalStorage + File.separator + PAINT_FOLDER_NAME;
        File file = new File(paintDirectory);
        if (!file.exists()){
            boolean result = file.mkdirs();
            if (!result)
                Log.d(TAG, "Create paint folder false!");
        }
        Log.d(TAG, "Paint directory path: " + paintDirectory);
        return paintDirectory;
    }

    /** Lưu trữ dữ liệu tạm thời */

    public static final String PAINT_TO_PREVIEW = "paint_to_preview";


    private Map<String, Object> tempObject = new HashMap<String, Object>();

    /** Thêm dữ liệu tạm thời */
    public void putTempObject(String key, Object object){
        tempObject.put(key, object);
    }

    /** Lấy dữ liệu tạm thời đã lưu trữ trước đó */
    public Object getTempObject(String key){
        return tempObject.get(key);
    }
    /**
     * end for temp object--------------------------------------------------------------------------
     */

    /**
     * Trả về tên đầy đủ của bức ảnh có chứa đuôi mở rộng
     * @param name Tên của bức ảnh
     * @return Tên của ảnh có kèm theo đuôi mở rộng
     */
    public static String getFullNameForPaint(String name){
        return name + ".jpg";
    }

    /**
     * Xóa ảnh
     * @param paintRef Đối tượng chứa thông tin ảnh
     * @return Kết quả xóa ảnh (thành công hoặc thất bại)
     */
    public static boolean deletePaint(PaintReference paintRef){
        String prefix = "file://";
        String path = paintRef.path;
        if (paintRef.path.startsWith(prefix))
            path = paintRef.path.substring(prefix.length());
        File file = new File(path);
        return file.delete();
    }

    /**
     * Đổi tên ảnh
     * @param paintRef Đối tượng chứa thông tin ảnh
     * @param newName Tên mới của ảnh
     * @return Kết quả đổi tên (thành công hoặc thất bại)
     */
    public static boolean renamePaint(PaintReference paintRef, String newName){
        String paintName = Application.getFullNameForPaint(newName);
        String prefix = "file://";
        String path = paintRef.path;
        if (paintRef.path.startsWith(prefix))
            path = paintRef.path.substring(prefix.length());
        File file = new File(path);
        File newFile = new File(file.getParent(), paintName);
        return file.renameTo(newFile);
    }

    /**
     * Kiểm tra xem ảnh có tồn tại hay không
     * @param name Tên của ảnh
     * @return True nếu ảnh tồn tại và ngược lại
     */
    public static boolean isPaintExists(String name){
        String paintName = Application.getFullNameForPaint(name);
        File file = new File(getLocalPaintDirectory(), paintName);
        return file.exists();
    }

    /**
     * Trả về đường dẫn đầy đủ của của bức ảnh
     * @param nameWithoutExtension Tên của bức ảnh (không kèm theo đuôi mở rộng)
     * @return Đường dẫn tới ảnh
     */
    public static String getFilePathForPaint(String nameWithoutExtension){
        return "file://" + getLocalPaintDirectory() + File.separator + getFullNameForPaint(nameWithoutExtension);
    }
}
