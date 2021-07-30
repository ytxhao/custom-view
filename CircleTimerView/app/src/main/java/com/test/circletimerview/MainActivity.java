package com.test.circletimerview;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity";

    private CircleTimerView mCircleTimerView;
    private GrabSingRootLayout mGrabSingRootLayout;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int clickCount  = 0;
    private VocalFinishPopWindow mPopWindow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleTimerView = findViewById(R.id.circle_timer_view);
        mGrabSingRootLayout = findViewById(R.id.gsrl);
        initView();
        initView2();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showPopWindow("单击“抢”开始唱");
                setCircleTimerViewMode2(GrabSingRootLayout.MODE_COUNT_DOWN);
//                setCircleTimerViewMode2(GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_SING_DISABLE);
                showGrabSingRootLayoutAnim(null);
            }
        }, 1500);
    }
    private void initView() {

//        mCircleTimerView.setDrawMode(CircleTimerView.MODE_CLEAN);
        mCircleTimerView.setTimeDurationEnterRoom(10);
        mCircleTimerView.setTimeDurationRecord(16);//录制
        mCircleTimerView.setTimeDurationSingAnswer(7);
        mCircleTimerView.setTimeDurationTitleCard(3);//321倒计时文案
        mCircleTimerView.setTimeDurationEnterRoom(10000);
        mCircleTimerView.setTimeDurationRecord(16000);//录制
        mCircleTimerView.setTimeDurationSingAnswer(7000);
        mCircleTimerView.setTimeDurationTitleCard(3000);//321倒计时文案,单位为毫秒
        mCircleTimerView.setOnAnimationCtrlListener((animation, mode) -> {
            Log.d(TAG, "onAnimationEnd:" + " mode=" + mode);
            circleTimerAction(mode, 0);
        });
        mCircleTimerView.setOnClickCtrlListener((v, mode, animIsRunning) -> {
            Log.d(TAG, "onClick:" + " mode=" + mode + " animIsRunning=" + animIsRunning);
            if (CircleTimerView.MODE_COUNT_DOWN_NO_CIRCLE == mode
                    || CircleTimerView.MODE_CLEAN == mode
                    || CircleTimerView.MODE_COUNT_DOWN_WITH_CIRCLE == mode) {
                return;
            }
            circleTimerAction(mode, 1);
        });
        mCircleTimerView.startAnim();
    }

    /**
     * @param mode   模式
     * @param action 行为。0，动画结束，1，被点击
     */
    private void circleTimerAction(int mode, int action) {//动画结束或者点击，执行相应行为
        switch (mode) {
            case CircleTimerView.MODE_COUNT_DOWN_WITH_CIRCLE:
                setCircleTimerViewMode(CircleTimerView.MODE_COUNT_DOWN_NO_CIRCLE);
                break;
            case CircleTimerView.MODE_COUNT_DOWN_NO_CIRCLE://321倒计时
                setCircleTimerViewMode(CircleTimerView.MODE_COUNT_DOWN_WITH_RECT);
                break;
            case CircleTimerView.MODE_COUNT_DOWN_WITH_RECT://接唱结束后开始有ai识别|关闭录制
                setCircleTimerViewMode(CircleTimerView.MODE_COUNT_DOWN_WITH_IMG);
                break;
            case CircleTimerView.MODE_COUNT_DOWN_WITH_IMG://抢唱按钮被点击或者结束
                setCircleTimerViewMode(CircleTimerView.MODE_COUNT_DOWN_WITH_CIRCLE);
                break;
            case CircleTimerView.MODE_CLEAN:
                break;
        }
    }

    public void setCircleTimerViewMode(int mode) {
        if (mCircleTimerView != null) {
            mCircleTimerView.cancelAnim();
            mCircleTimerView.setDrawMode(mode);
            mCircleTimerView.startAnim();
        }
    }


    private void initView2() {
        mGrabSingRootLayout.setVisibility(View.INVISIBLE);
        mGrabSingRootLayout.setDrawMode(GrabSingRootLayout.MODE_CLEAN);
        mGrabSingRootLayout.setDurationAnimCountDown(3000);
        mGrabSingRootLayout.setDurationAnimGrabRecord(16000);
        mGrabSingRootLayout.setDurationAnimGrabSing(7000);
//        showGrabSingRootLayoutAnim(null);
        mGrabSingRootLayout.getGrabSingWrapProcessLayout().setOnAnimationCtrlListener(new GrabSingWrapProcessLayout.OnAnimationCtrlListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mGrabSingRootLayout.getDrawMode() == GrabSingRootLayout.MODE_COUNT_DOWN) {
                    circleTimerAction2(GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_SING, 0);
                } else {
                    circleTimerAction2(mGrabSingRootLayout.getDrawMode(), 0);
                }

            }
        });
//        mGrabSingRootLayout.setOnAnimationCtrlListener(new GrabSingFrameLayout.OnAnimationCtrlListener() {
////            @Override
////            public void onAnimationEnd(Animator animation, int mode) {
////                circleTimerAction(mode, 0);
////            }
////        });
        mGrabSingRootLayout.setOnClickCtrlListener(new GrabSingRootLayout.OnClickCtrlListener() {
            @Override
            public void onClick(View v, int mode) {
                if (mode == GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_SING ||
                        mode == GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_RECORD) {
                    mGrabSingRootLayout.feedBackAnim(null);
//                    hiddenPopWindow();
                    clickCount++;
                    if (clickCount == 4) {
                        clickCount = 0;
                        circleTimerAction2(mode, 1);
                    }
//                    MediaPlayerManager.INSTANCE.playEffect(MediaPlayerManager.AudioEffect.VOCAL_CLICK_FEEDBACK);
                }
            }
        });
    }

    public void showPopWindow(String popTextTips) {
        mPopWindow = new VocalFinishPopWindow(this, popTextTips);
        mPopWindow.showPopWindow(this, mGrabSingRootLayout);
        floatAnim(mPopWindow.getContentView(), 0);
//        timer();
    }

    public void hiddenPopWindow() {
        if (mPopWindow != null) {
            mPopWindow.hidePopWindow();
        }
    }

    /**
     * @param mode   模式
     * @param action 行为。0，动画结束，1，被点击
     */
    private boolean hasAutoRecordEnd = false;
    private boolean firstGrabDisable = true;
    private boolean isStartGrabClean = false;
    private void resetAnimFlags(){
        hasAutoRecordEnd = false;
        isStartGrabClean = false;
    }

    public void setCircleTimerViewMode2(int mode) {
        if (mGrabSingRootLayout != null) {
            mGrabSingRootLayout.cancelCountDown();
            mGrabSingRootLayout.setDrawMode(mode);
            mGrabSingRootLayout.startCountDown();
        }
    }

    private void circleTimerAction2(int mode, int action) {//动画结束或者点击，执行相应行为
        switch (mode) {
            case GrabSingRootLayout.MODE_COUNT_DOWN://321倒计时
//                hasChallenged = false;
//                showPopWindow("单击“抢”开始唱");
//                setCircleTimerViewMode2(GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_SING);
                break;
            case GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_RECORD://接唱结束后开始有ai识别|关闭录制
                if (action == 1) {//被点击
                    showPopWindow("单击“抢”开始唱");
                    setCircleTimerViewMode2(GrabSingRootLayout.MODE_COUNT_DOWN);
                    showGrabSingRootLayoutAnim(null);
                } else {
                    vocalScaleAnim(mGrabSingRootLayout, false, new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
//                            hiddenPopWindow();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            showPopWindow("单击“抢”开始唱");
                            setCircleTimerViewMode2(GrabSingRootLayout.MODE_COUNT_DOWN);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                break;
            case GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_SING://抢唱按钮被点击或者结束
                if (action == 1) {//被点击
                    setCircleTimerViewMode2(GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_RECORD);
                    showGrabSingRootLayoutAnim(null);

//                    setCircleTimerViewMode2(CircleTimerView.MODE_CLEAN);
                } else if (action == 0) {//抢唱环节结束隐藏按钮,并且没有人抢
                    hideGrabSingRootLayoutAnim(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                                hiddenPopWindow();
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            setCircleTimerViewMode2(GrabSingRootLayout.MODE_CLEAN);
                            showGrabSingRootLayoutAnim(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    setCircleTimerViewMode2(GrabSingRootLayout.MODE_COUNT_DOWN_GRAB_SING);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                break;
            case GrabSingRootLayout.MODE_CLEAN:
//                hiddenPopWindow();
                break;
        }
    }

    private void showGrabSingRootLayoutAnim(Animation.AnimationListener listener) {
        if (mGrabSingRootLayout != null) {
            vocalScaleAnim(mGrabSingRootLayout, true, listener);
        }
    }

    private void hideGrabSingRootLayoutAnim(Animation.AnimationListener listener) {
        if (mGrabSingRootLayout != null) {
            vocalScaleAnim(mGrabSingRootLayout, false, listener);
        }
    }

    private void vocalScaleAnim(View view, boolean show, Animation.AnimationListener animatorListener) {
        Configuration mConfig = getApplicationContext().getResources().getConfiguration();
        if (view != null) {
            int viewX = view.getWidth();
            if (viewX <= 0) {
                viewX = DensityUtils.dip2px(getApplicationContext(),115f);
            }
            TranslateAnimation scaleAnimation;
            if (show) {
                if (mConfig.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    scaleAnimation = new TranslateAnimation(-viewX, 0f, 0f, 0f);
                } else {
                    scaleAnimation = new TranslateAnimation(viewX, 0f, 0f, 0f);
                }
                scaleAnimation.setDuration(200);
            } else {
                if (mConfig.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    scaleAnimation = new TranslateAnimation(0f, -viewX, 0f, 0f);
                } else {
                    scaleAnimation = new TranslateAnimation(0f, viewX, 0f, 0f);
                }
                scaleAnimation.setDuration(200);
            }
            if (animatorListener != null) {
                scaleAnimation.setAnimationListener(animatorListener);
            }
            view.startAnimation(scaleAnimation);
        }
    }

    void floatAnim(View view, long delay) {
        if (view == null) {
            return;
        }
        ArrayList<Animator> animators = new  ArrayList<Animator>();
        ObjectAnimator translationXAnim = ObjectAnimator.ofFloat(view, "translationX", -3.0f, 3.0f, -3.0f);
        translationXAnim.setDuration(3000);
        translationXAnim.setRepeatCount(ValueAnimator.INFINITE);
        translationXAnim.setRepeatMode(ValueAnimator.REVERSE);
        translationXAnim.start();
        animators.add(translationXAnim);
        ObjectAnimator translationYAnim = ObjectAnimator.ofFloat(view, "translationY", -6.0f, 5.0f, -6.0f);
        translationYAnim.setDuration(2000);
        translationYAnim.setRepeatCount(ValueAnimator.INFINITE);
        translationYAnim.setRepeatMode(ValueAnimator.REVERSE);
        translationYAnim.start();
        animators.add(translationYAnim);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.setStartDelay(delay);
        animatorSet.start();
    }
}