package com.test.circletimerview;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DensityUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
//        Context context = App.INSTANCE;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
//        Context context = App.INSTANCE;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取像素密度
     * @param context
     * @return
     */
    public static float getScale(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static float sp2px(Context context, float spValue) {
//        Context context = App.INSTANCE;
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (spValue * fontScale + 0.5f);
    }

    public static float parseDimension(Context context, String str) {
        Pattern pattern = Pattern.compile("^(-?\\d+(?:\\.\\d+)?)(\\w+)$");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            float value = Float.parseFloat(matcher.group(1));
            String unit = matcher.group(2);
            switch (unit.toLowerCase()) {
                case "dp":
                case "dip":
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, metrics);
                case "sp":
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, metrics);
                case "pt":
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, metrics);
                case "in":
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, value, metrics);
                case "mm":
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, metrics);
                case "px":
                default:
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, value, metrics);
            }
        }
        return 0f;
    }

    /**
     *  获取两点之间的距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

}
