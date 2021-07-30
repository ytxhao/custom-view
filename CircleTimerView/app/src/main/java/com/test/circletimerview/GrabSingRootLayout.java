package com.test.circletimerview;

import android.animation.Animator;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GrabSingRootLayout extends FrameLayout implements View.OnClickListener{

    public static final int MODE_COUNT_DOWN_GRAB_SING = 1;//有图片的环形--抢唱
    public static final int MODE_COUNT_DOWN_GRAB_RECORD = 2;//录制
    public static final int MODE_COUNT_DOWN = 3; //抢唱前的321倒计时
    public static final int MODE_COUNT_DOWN_GRAB_SING_DISABLE = 4;
    public static final int MODE_CLEAN = 5;//清屏

    private static final int PROCESS_COUNT = 1000;

    private ImageView ivGrabRecord;
    private ImageView ivGrabSing;
    private ImageView ivCountDown;
    private ImageView ivGrabDisable;

    private TextView tvGrabSing;
    private TextView tvCountDown;

    private RelativeLayout rlGrabRoot;
    private RelativeLayout rlWrapBtn;
    private FrameLayout flCountDown;
    private FrameLayout flGrabSing;

    private GrabSingWrapProcessLayout gswpLayout;


    //private long durationAnim = 5000;


    //单位为毫秒
    private long durationAnimGrabSing = 5000;//321倒计时文案
    private long durationAnimCountDown = 5000;//抢唱时间
    private long durationAnimGrabRecord = 5000;//录制时间

    private int drawMode = 1;


    private OnAnimationCtrlListener mOnAnimationCtrlListener;

    private OnClickCtrlListener mOnClickCtrlListener;

    public GrabSingRootLayout(@NonNull Context context) {
        this(context,null);
    }

    public GrabSingRootLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GrabSingRootLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.grab_root_layout, this, true);
        ivGrabRecord = findViewById(R.id.iv_grab_record);
        ivGrabSing = findViewById(R.id.iv_grab_sing);
        ivCountDown = findViewById(R.id.iv_count_down);
        ivGrabDisable = findViewById(R.id.iv_grab_disable);

        tvGrabSing = findViewById(R.id.tv_grab_sing);
        tvCountDown = findViewById(R.id.tv_count_down);

        rlGrabRoot = findViewById(R.id.rl_grab_root);
        gswpLayout = findViewById(R.id.gswp_layout);
        flGrabSing = findViewById(R.id.fl_grab_sing);
        flCountDown = findViewById(R.id.fl_count_down);
        rlWrapBtn = findViewById(R.id.rl_wrap_btn);

        setOnClickListener(this);
        gswpLayout.setOnProgressListener(new GrabSingWrapProcessLayout.OnProgressListener() {
            @Override
            public void onProgress(float progress) {
                switch (getDrawMode()) {
                    case MODE_COUNT_DOWN_GRAB_SING:

                        break;
                    case MODE_COUNT_DOWN_GRAB_RECORD:

                        break;
                    case MODE_COUNT_DOWN:
                        long process = (long)((progress * getDurationAnimCountDown())/1000)+1;
                        tvCountDown.setText("" + process);
                        break;
                }
            }
        });


    }


    public int getDrawMode() {
        return drawMode;
    }

    public boolean setDrawMode(int mMode) {
        this.drawMode = mMode;
        return true;
    }


    public OnClickCtrlListener getOnClickCtrlListener() {
        return mOnClickCtrlListener;
    }

    public void setOnClickCtrlListener(OnClickCtrlListener mOnClickCtrlListener) {
        this.mOnClickCtrlListener = mOnClickCtrlListener;
    }

    public OnAnimationCtrlListener getOnAnimationCtrlListener() {
        return mOnAnimationCtrlListener;
    }

    public void setOnAnimationCtrlListener(OnAnimationCtrlListener mOnAnimationCtrlListener) {
        this.mOnAnimationCtrlListener = mOnAnimationCtrlListener;
    }

    public long getDurationAnimGrabSing() {
        return durationAnimGrabSing;
    }

    public void setDurationAnimGrabSing(long durationAnimGrabSing) {
        this.durationAnimGrabSing = durationAnimGrabSing;
    }

    public long getDurationAnimCountDown() {
        return durationAnimCountDown;
    }

    public void setDurationAnimCountDown(long durationAnimCountDown) {
        this.durationAnimCountDown = durationAnimCountDown;
    }

    public long getDurationAnimGrabRecord() {
        return durationAnimGrabRecord;
    }

    public void setDurationAnimGrabRecord(long durationAnimGrabRecord) {
        this.durationAnimGrabRecord = durationAnimGrabRecord;
    }


    public RelativeLayout getRlWrapBtn(){
        return rlWrapBtn;
    }

    public GrabSingWrapProcessLayout getGrabSingWrapProcessLayout(){
        return gswpLayout;
    }

    public void startCountDown(){
        switch (getDrawMode()) {
            case MODE_COUNT_DOWN_GRAB_SING:
                setVisibility(VISIBLE);
                tvCountDown.setVisibility(VISIBLE);
                tvGrabSing.setVisibility(VISIBLE);
                ivCountDown.setVisibility(INVISIBLE);
                ivGrabSing.setVisibility(VISIBLE);
                ivGrabRecord.setVisibility(INVISIBLE);
                ivGrabDisable.setVisibility(INVISIBLE);
                flCountDown.setVisibility(INVISIBLE);
                flGrabSing.setVisibility(VISIBLE);
                tvGrabSing.setTextColor(0xffffffff);
                AlphaAnimation mAlphaAnimGrabSing = new AlphaAnimation(0.0f, 1.0f);
                mAlphaAnimGrabSing.setDuration(500);
                mAlphaAnimGrabSing.setFillAfter(true);
                gswpLayout.startAnimation(mAlphaAnimGrabSing);
                gswpLayout.showProcess(true);
                gswpLayout.setDurationAnim(getDurationAnimGrabSing());
                gswpLayout.startAnim();
                break;
            case MODE_COUNT_DOWN_GRAB_RECORD:
               // animGrabRecord = startAnim(MODE_COUNT_DOWN_GRAB_RECORD, getDurationAnimGrabRecord());
                setVisibility(VISIBLE);
                ivCountDown.setVisibility(INVISIBLE);
                tvCountDown.setVisibility(INVISIBLE);
                tvGrabSing.setVisibility(INVISIBLE);
                ivGrabSing.setVisibility(INVISIBLE);
                ivGrabRecord.setVisibility(VISIBLE);
                ivGrabDisable.setVisibility(INVISIBLE);
                flCountDown.setVisibility(INVISIBLE);
                flGrabSing.setVisibility(INVISIBLE);
                AlphaAnimation mAlphaAnimGrabRecord = new AlphaAnimation(0.0f, 1.0f);
                mAlphaAnimGrabRecord.setDuration(500);
                mAlphaAnimGrabRecord.setFillAfter(true);
                gswpLayout.startAnimation(mAlphaAnimGrabRecord);
                gswpLayout.showProcess(true);
                gswpLayout.setDurationAnim(getDurationAnimGrabRecord());
                gswpLayout.startAnim();
                break;
            case MODE_COUNT_DOWN:
              //  animCountDown = startAnim(MODE_COUNT_DOWN, getDurationAnimCountDown());
                setVisibility(VISIBLE);
                ivCountDown.setVisibility(VISIBLE);
                tvCountDown.setVisibility(VISIBLE);
                tvGrabSing.setVisibility(INVISIBLE);
                ivGrabSing.setVisibility(INVISIBLE);
                ivGrabRecord.setVisibility(INVISIBLE);
                ivGrabDisable.setVisibility(INVISIBLE);
                flCountDown.setVisibility(VISIBLE);
                flGrabSing.setVisibility(INVISIBLE);
                gswpLayout.showProcess(false);
                gswpLayout.setDurationAnim(getDurationAnimCountDown());
                gswpLayout.startAnim();
                break;
            case MODE_CLEAN:
                setVisibility(INVISIBLE);
                ivCountDown.setVisibility(INVISIBLE);
                tvCountDown.setVisibility(INVISIBLE);
                tvGrabSing.setVisibility(INVISIBLE);
                ivGrabSing.setVisibility(INVISIBLE);
                ivGrabRecord.setVisibility(INVISIBLE);
                ivGrabDisable.setVisibility(INVISIBLE);
                flCountDown.setVisibility(INVISIBLE);
                flGrabSing.setVisibility(INVISIBLE);
                gswpLayout.showProcess(false);
                cancelCountDown();
                break;
            case MODE_COUNT_DOWN_GRAB_SING_DISABLE:
                setVisibility(VISIBLE);
                ivCountDown.setVisibility(INVISIBLE);
                tvCountDown.setVisibility(INVISIBLE);
                tvGrabSing.setVisibility(VISIBLE);
                ivGrabSing.setVisibility(INVISIBLE);
                ivGrabRecord.setVisibility(INVISIBLE);
                ivGrabDisable.setVisibility(VISIBLE);
                flCountDown.setVisibility(INVISIBLE);
                flGrabSing.setVisibility(VISIBLE);
                tvGrabSing.setTextColor(0x66FFFFFF);
                gswpLayout.showProcess(false);
                cancelCountDown();
                break;
            default:
                setVisibility(INVISIBLE);
                ivCountDown.setVisibility(INVISIBLE);
                tvCountDown.setVisibility(INVISIBLE);
                tvGrabSing.setVisibility(INVISIBLE);
                ivGrabSing.setVisibility(INVISIBLE);
                ivGrabRecord.setVisibility(INVISIBLE);
                ivGrabDisable.setVisibility(INVISIBLE);
                flCountDown.setVisibility(INVISIBLE);
                flGrabSing.setVisibility(INVISIBLE);
                gswpLayout.showProcess(false);
                cancelCountDown();
                break;
        }
    }

    public void cancelCountDown() {
        gswpLayout.cancelAnim();
    }

    public void feedBackAnim(Animation.AnimationListener listener){
        ScaleAnimation mScaleAnimation = new ScaleAnimation(0.9f, 1f, 0.9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mScaleAnimation.setDuration(200);
        if (listener != null) {
            mScaleAnimation.setAnimationListener(listener);
        }
        rlWrapBtn.startAnimation(mScaleAnimation);
    }

    @Override
    public void onClick(View v) {
        //feedBackAnim();
        if(mOnClickCtrlListener != null){
            mOnClickCtrlListener.onClick(v,getDrawMode());
        }
    }

    public interface OnAnimationCtrlListener {
        void onAnimationEnd(Animator animation, int mode);
    }

    public interface OnClickCtrlListener{
        void onClick(View v, int mode);
    }
}
