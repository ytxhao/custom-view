package com.test.circletimerview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * 抢唱finish popWindow
 */
public class VocalFinishPopWindow {

    private PopupWindow mWindow;

    private FrameLayout mFrameLayout,mRootFrameLayout;

    public VocalFinishPopWindow(Context context, String popTextTips) {
        getTextView(context, popTextTips);
        mWindow = new PopupWindow(mRootFrameLayout);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        View contentView = mWindow.getContentView();
        contentView.measure(makeDropDownMeasureSpec(mWindow.getWidth()),
                makeDropDownMeasureSpec(mWindow.getHeight()));
//        mWindow.setBackgroundDrawable(ResourceUtils.getDrawable(R.drawable.vocal_finish_pop));
        mWindow.setTouchable(false);
        mWindow.setFocusable(false);
        mWindow.setOutsideTouchable(false);
    }

    private void getTextView(Context context, String popTextTips) {
        mRootFrameLayout =  new FrameLayout(context);
        mFrameLayout = new FrameLayout(context);
        mFrameLayout.setClipToPadding(false);
        mFrameLayout.setClipChildren(false);
        mRootFrameLayout.setClipToPadding(false);
        mRootFrameLayout.setClipToPadding(false);
        mRootFrameLayout.setPadding( DensityUtils.dip2px(context,1), DensityUtils.dip2px(context,5),
                DensityUtils.dip2px(context, 1), DensityUtils.dip2px(context, 5));
        TextView mTextView = new TextView(context);

        mTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
        mTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTextView.setPadding(DensityUtils.dip2px(context, 12), DensityUtils.dip2px(context,12),
                DensityUtils.dip2px(context,12), DensityUtils.dip2px(context,12));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f);
        mTextView.setText(popTextTips);
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.vocal_finish_pop);
        mFrameLayout.setBackground(drawable);
        mFrameLayout.addView(mTextView);
        mRootFrameLayout.addView(mFrameLayout);
    }

    public void showPopWindow(Context context, View anchor) {
        if (mWindow != null) {
            int offsetX = Math.abs(anchor.getWidth() - mWindow.getContentView().getMeasuredWidth()) / 2;
            int offsetY = mWindow.getContentView().getMeasuredHeight() + anchor.getHeight() + DensityUtils.dip2px(context,5);
//            if (ResourceUtils.isLayoutRtl()) {
            Configuration mConfig = context.getResources().getConfiguration();
            if(mConfig.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
                offsetX = -anchor.getWidth() + offsetX;
            }
            mWindow.showAsDropDown(anchor, offsetX, -offsetY);
        }
    }

    public void hidePopWindow() {
        mWindow.dismiss();
    }

    public void setWidth(int width) {
        mWindow.setWidth(width);
    }

    public void setHeight(int height) {
        mWindow.setHeight(height);
    }

    public View getContentView() {
        if (mWindow != null) {
            return mWindow.getContentView();
        }
        return null;
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

}
