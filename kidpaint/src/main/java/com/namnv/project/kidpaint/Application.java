package com.namnv.project.kidpaint;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
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

    public static final String PAINT_FOLDER_NAME = "kidpaint";

    public static final String SETTING_PREF_NAME = "kidpaint.settings";

    public static final String KEY_APP_THEME = "kidpaint.apptheme";


    public static final String TAG = "kidpaint";

    public String paintExtension = ".jpg";

    public String getCurrentPaintExtension(){
        return paintExtension;
    }

    private static Application instance;
    DisplayImageOptions options;

    int appTheme;
    public ThemeInfo themeInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // load settings
        String themeName = getSetting().getString(KEY_APP_THEME, getThemeNameFromId(R.style.KidPaint_Aqua));
        appTheme = getThemeResourceFromName(themeName);
        themeInfo = new ThemeInfo();
        applyTheme(appTheme);


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
            .build();
        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // resource or drawable
                .resetViewBeforeLoading(false)  // default
                .delayBeforeLoading(1000)
                .cacheInMemory(true) // default
                .cacheOnDisk(false) // default
//                .preProcessor(...)
//        .postProcessor(...)
//        .extraForDownloader(...)
//        .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
//                .decodingOptions(...)
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();
    }

    public ThemeInfo getThemeInfo(){
        return themeInfo;
    }

    public void applyTheme(int appTheme){
        Resources.Theme theme = getResources().newTheme();
        theme.applyStyle(appTheme, true);
        TypedArray values = theme.obtainStyledAttributes(
                new int[]{
                        android.R.attr.windowBackground
                });
        themeInfo.background = values.getResourceId(0, R.color.theme_default);
    }

    public int getAppTheme(){
        return appTheme;
    }

    public void setAppTheme(int appTheme){
        this.appTheme = appTheme;
        getSetting().edit().putString(KEY_APP_THEME, getThemeNameFromId(appTheme)).commit();
        applyTheme(appTheme);
    }

    public static Application getInstance(){
        return instance;
    }

    public SharedPreferences getSetting(){
        return getSharedPreferences(SETTING_PREF_NAME, MODE_PRIVATE);
    }

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

    /**
     * for temp object, used to transfer object between activity------------------------------------
     */
    // keys for object
    public static final String PAINT_TO_PREVIEW = "paint_to_preview";
    public static final String PAINT_REFERENCES = "paint_references";

    private Map<String, Object> tempObject = new HashMap<String, Object>();
    public void putTempObject(String key, Object object){
        tempObject.put(key, object);
    }

    public Object getTempObject(String key){
        return tempObject.get(key);
    }
    /**
     * end for temp object--------------------------------------------------------------------------
     */


    public int getThemeResourceFromName(String name){
        if (name.equals(getString(R.string.theme_1))){
            return R.style.KidPaint_Blue;
        }else
        if (name.equals(getString(R.string.theme_2))){
            return R.style.KidPaint_Green;
        }else
        if (name.equals(getString(R.string.theme_3))){
            return R.style.KidPaint_Red;
        }else
        if (name.equals(getString(R.string.theme_4))){
            return R.style.KidPaint_Aqua;
        }else
        if (name.equals(getString(R.string.theme_5))){
            return R.style.KidPaint_Magenta;
        }else
        if (name.equals(getString(R.string.theme_6))){
            return R.style.KidPaint_Yellow;
        }
        return R.style.KidPaint_Blue;
    }

    public String getThemeNameFromId(int theme){
        switch (theme){
            case R.style.KidPaint_Blue      : return getString(R.string.theme_1);
            case R.style.KidPaint_Green     : return getString(R.string.theme_2);
            case R.style.KidPaint_Red       : return getString(R.string.theme_3);
            case R.style.KidPaint_Aqua      : return getString(R.string.theme_4);
            case R.style.KidPaint_Magenta   : return getString(R.string.theme_5);
            case R.style.KidPaint_Yellow    : return getString(R.string.theme_6);
        }
        return getString(R.string.theme_1);
    }

    public class ThemeInfo{
        public int background;
    }

    public static String getFullNameForPaint(String name){
        return name + ".jpg";
    }

    public static boolean deletePaint(PaintReference paintRef){
        String prefix = "file://";
        String path = paintRef.path;
        if (paintRef.path.startsWith(prefix))
            path = paintRef.path.substring(prefix.length());
        File file = new File(path);
        return file.delete();
    }

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

    public static boolean isPaintExists(String name){
        String paintName = Application.getFullNameForPaint(name);
        File file = new File(getLocalPaintDirectory(), paintName);
        return file.exists();
    }

    public static String getFilePathForPaint(String nameWithoutExtension){
        return "file://" + getLocalPaintDirectory() + File.separator + getFullNameForPaint(nameWithoutExtension);
    }
}
