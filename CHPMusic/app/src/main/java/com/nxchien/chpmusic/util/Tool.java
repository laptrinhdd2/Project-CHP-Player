package com.nxchien.chpmusic.util;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.nxchien.chpmusic.model.Rectangle;

import java.util.ArrayList;

public class Tool {

    private static Tool tool;
    private Context context;

    public static void init(Context context) {
        if (tool == null) tool = new Tool();
        tool.context = context;
        Tool.getScreenSize(context);
        tool.resumeWallpaperTracking();
    }

    public static Tool getInstance() {
        return tool;
    }

    private ArrayList<WallpaperChangedNotifier> notifiers = new ArrayList<>();
    private ArrayList<Boolean> CallFirstTime = new ArrayList<>();

    public void clear() {
        notifiers.clear();
    }

    public void remove(WallpaperChangedNotifier notifier) {
        notifiers.remove(notifier);
    }

    public interface WallpaperChangedNotifier {
        void onWallpaperChanged(Bitmap original, Bitmap blur);
    }

    private Bitmap originalWallPaper;
    private Bitmap blurWallPaper;

    private Bitmap getActiveWallPaper() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        Bitmap bmp = ((BitmapDrawable) wallpaperDrawable).getBitmap();
        if (bmp.getWidth() > 0) return bmp.copy(bmp.getConfig(), true);
        return Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888);
    }

    private Bitmap blurWallBitmap() {
        return BitmapEditor.getBlurredWithGoodPerformance(context, originalWallPaper, 1, 12, 1.6f);
    }

    private Bitmap getCropCenterScreenBitmap(Bitmap source_bitmap) {
        Rectangle rect_parent_in_bitmap = new Rectangle();
        float parentWidth = screenSize[0];
        float parentHeight = screenSize[1];
        float ratio_parent = parentWidth / (parentHeight + 0.0f);
        float ratio_source = source_bitmap.getWidth() / (source_bitmap.getHeight() + 0.0f);

        if (ratio_parent > ratio_source) {
            rect_parent_in_bitmap.Width = source_bitmap.getWidth();
            rect_parent_in_bitmap.Height = (int) (rect_parent_in_bitmap.Width * parentHeight / parentWidth);

            rect_parent_in_bitmap.Left = 0;
            rect_parent_in_bitmap.Top = source_bitmap.getHeight() / 2 - rect_parent_in_bitmap.Height / 2;
        } else {
            rect_parent_in_bitmap.Height = source_bitmap.getHeight();
            rect_parent_in_bitmap.Width = (int) (rect_parent_in_bitmap.Height * parentWidth / parentHeight);

            rect_parent_in_bitmap.Top = 0;
            rect_parent_in_bitmap.Left = source_bitmap.getWidth() / 2 - rect_parent_in_bitmap.Width / 2;
        }
        Bitmap ret = Bitmap.createBitmap((int) parentWidth, (int) parentHeight, Bitmap.Config.ARGB_8888);
        return ret;
    }

    private boolean status = false;

    boolean first_run = true;

    public static int lighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public void resumeWallpaperTracking() {
        if (first_run) return;
        if (!status) {
            status = true;
            mHandlerTask.run();
        }
    }

    private final static int INTERVAL = 1000 * 2; //2 minutes
    private Handler mHandler = new Handler();
    private boolean runningAsyncTask = false;

    private Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            if (!runningAsyncTask) {
                runningAsyncTask = true;
                new WallpaperLoadAndCompare().execute(Tool.this);
            }
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    private static class WallpaperLoadAndCompare extends AsyncTask<Tool, Void, Boolean> {
        Tool tool;

        @Override
        protected void onPostExecute(Boolean result) {
            if (tool.status) {
                if (result) {
                    // nếu có thay đổi
                    for (int i = 0; i < tool.notifiers.size(); i++) {
                        WallpaperChangedNotifier item = tool.notifiers.get(i);
                        item.onWallpaperChanged(tool.originalWallPaper, tool.blurWallPaper);
                        tool.CallFirstTime.set(i, true);
                    }

                } else {
                    // ngược lại duyệt mảng xem phần tử nào chưa dc gọi lần đầu thì gọi
                    for (int i = tool.notifiers.size() - 1; i != -1; i--) {
                        if (!tool.CallFirstTime.get(i)) {
                            tool.CallFirstTime.set(i, true);
                            tool.notifiers.get(i).onWallpaperChanged(tool.originalWallPaper, tool.blurWallPaper);
                        } else break;
                    }

                }
            }
            tool.runningAsyncTask = false;
        }

        @Override
        protected Boolean doInBackground(Tool... t) {
            this.tool = t[0];
            Bitmap origin = tool.originalWallPaper;

            // nếu ảnh gốc chưa được load lần đầu
            if (origin == null) {
                tool.originalWallPaper = tool.getCropCenterScreenBitmap(tool.getActiveWallPaper());
                makeWallPaper();
                return true;
            }
            // ngược lại ta so sánh ảnh mới và ảnh gốc
            Bitmap newOrigin = tool.getCropCenterScreenBitmap(tool.getActiveWallPaper());
            if (!origin.sameAs(newOrigin)) {
                tool.originalWallPaper = newOrigin;
                makeWallPaper();
                return true;
            } else return false;
        }

        private void makeWallPaper() {
            Bitmap tmpBlur = tool.blurWallBitmap();
            int[] c = BitmapEditor.getAverageColorRGB(tmpBlur);
            tool.mAverageColor = Color.rgb(c[0], c[1], c[2]);
            Tool.setSurfaceColor(tool.mAverageColor);
            tool.mDarkWallpaper = BitmapEditor.PerceivedBrightness(160, c);
            tool.blurWallPaper = tool.getCropCenterScreenBitmap(tmpBlur);
            tmpBlur.recycle();
        }
    }

    private boolean mDarkWallpaper = false;
    private int mAverageColor = Color.WHITE;

    private static int GlobalColor = 0xffff4081;
    private static int SurfaceColor = 0xff007AFF;

    public static void setSurfaceColor(int globalColor) {
        SurfaceColor = ColorReferTo(globalColor);

    }

    public static int getBaseColor() {
        return SurfaceColor;
    }

    public static void setMostCommonColor(int globalColor) {
        GlobalColor = globalColor;
    }

    public static int getMostCommonColor() {
        return GlobalColor;
    }

    public static void setOneDps(float width) {
        oneDPs = width;
    }

    public static int getHeavyColor() {
        switch (SurfaceColor) {
            case 0xffFF3B30:
                return 0xff770000;
            case 0xffFF9500:
                return 0xff923C00;
            case 0xffFFCC00:
                return 0xffAF8700;
            case 0xff4CD964:
                return 0xff005800;
            case 0xff5AC8FA:
                return 0xff0058AA;
            case 0xff007AFF:
                return 0xff00218B;
            case 0xff5855D6:
                return 0xff162EA6;
            default: //0xffFB2C57
                return 0xffb60024;
        }
    }

    public static int ColorReferTo(int cmc) {
        float[] hsv = new float[3];
        Color.colorToHSV(cmc, hsv);
        float toEight = hsv[0] / 45 + 0.5f;
        if (toEight > 8 | toEight <= 1) return 0xffFF3B30;
        if (toEight <= 2) return 0xffFF9500;
        if (toEight <= 3) return 0xffFFCC00;
        if (toEight <= 4) return 0xff4CD964;
        if (toEight <= 5) return 0xff5AC8FA;
        if (toEight <= 6) return 0xff007AFF;
        if (toEight <= 7) return 0xff5855D6;
        return 0xffFF2D55;
    }

    public static int StatusHeight = -1;

    public static int getStatusHeight(Resources myR) {
        if (StatusHeight != -1) return StatusHeight;
        int height;
        int idSbHeight = myR.getIdentifier("status_bar_height", "dimen", "android");
        if (idSbHeight > 0) {
            height = myR.getDimensionPixelOffset(idSbHeight);
        } else {
            height = 0;
        }
        StatusHeight = height;
        return StatusHeight;
    }

    public static float getPixelsFromDPs(Context activity, int dps) {
        Resources r = activity.getResources();
        return (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
    }

    public static float getOneDps(Context context) {
        if (oneDPs != -1) return oneDPs;
        oneDPs = getPixelsFromDPs(context, 1);
        return oneDPs;
    }

    public static float oneDPs = -1;

    static int[] screenSize;
    static float[] screenSizeInDp;
    public static boolean HAD_GOT_SCREEN_SIZE = false;

    public static int[] getScreenSize(Context context) {
        if (!HAD_GOT_SCREEN_SIZE) {
            Point p = new Point();
            Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); // this will get the view of screen
            d.getRealSize(p);
            int width = p.x;
            int height = p.y;
            screenSize = new int[]{width, height};
            screenSizeInDp = new float[]{(width + 0.0f) / getOneDps(context), (height + 0.0f) / getOneDps(context)};
            HAD_GOT_SCREEN_SIZE = true;
        }
        return screenSize;
    }

    public static int[] getScreenSize(boolean sure) {
        return screenSize;
    }


    public static boolean hasSoftKeys(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getNavigationHeight(Activity activity) {

        int navigationBarHeight = 0;
        int resourceId = activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        if (!hasSoftKeys(activity.getWindowManager())) return 0;
        return navigationBarHeight;
    }

    public static void showToast(Context context, String text, int time) {

        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();

            }
        }, time);
    }

    private static boolean drawn = false;

    public static boolean isDrawn() {
        return drawn;
    }


    private static boolean splashGone = false;

    public static void setSplashGone(boolean splashGone) {
        Tool.splashGone = splashGone;
    }

    public static String getStringTagForView(View v) {
        Object tagObject = v.getTag();
        return String.valueOf(tagObject);
    }

}