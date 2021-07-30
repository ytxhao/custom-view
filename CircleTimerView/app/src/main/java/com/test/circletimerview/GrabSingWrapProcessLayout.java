package com.test.circletimerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class GrabSingWrapProcessLayout extends FrameLayout{

    private static final String TAG = "GrabWrapProcess";

    private static final int DEFAULT_PROCESS_FIRST_COLOR = Color.RED;
    private static final int DEFAULT_PROCESS_SECOND_COLOR = Color.TRANSPARENT;


    private static final int ANIM_STATUS_IDLE = 0;
    private static final int ANIM_STATUS_START = 1;
    private static final int ANIM_STATUS_CANCEL = 2;
    private static final int ANIM_STATUS_END = 3;
    private static final int ANIM_STATUS_UPDATE = 4;

    private static final int PROCESS_COUNT = 1000;

    private float mHeight;
    private float mWidth;
    private Paint reachedPaint;
    private Paint unreachedPaint;
    private Path reachedPath;
    private Path unreachedPath;
    private int mColorProcessFirst = DEFAULT_PROCESS_FIRST_COLOR;
    private int mColorProcessSecond = DEFAULT_PROCESS_SECOND_COLOR;
    private float mProgress = 0.0f;

    private PathMeasure measureReachedPath;
    private float measureReachedLength;
    private float lineWidth = dp2px(4);

    private ValueAnimatorGrab anim;
    private int animCount = 1;
    private int animStatus = ANIM_STATUS_IDLE;
    private final PorterDuffXfermode mPorterDuffXfermodeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    private final Paint mPaintClear = new Paint();


    private ArrayList<ValueAnimatorGrab> animLists = new ArrayList();

    //单位为毫秒
    private long durationAnim = 5000;

    private boolean isShowProcess = true;

    private OnAnimationCtrlListener mOnAnimationCtrlListener;

    private OnProgressListener mOnProgressListener;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

                if (msg.obj != null && msg.obj instanceof ProcessHolder) {
                    ProcessHolder holder = (ProcessHolder) msg.obj;
                    int status = holder.getStatus();

                    if (animCount == msg.what) {
                        if (status == ANIM_STATUS_UPDATE) {
                            setProgress(holder.getProcess());
                            if(mOnProgressListener != null){
                                mOnProgressListener.onProgress(holder.getProcess());
                            }
                        } else if (status == ANIM_STATUS_CANCEL || status == ANIM_STATUS_END) {
                            setProgress(0);
                            anim = null;
                        }
                    }
                }


        }
    };


    public GrabSingWrapProcessLayout(@NonNull Context context) {
        this(context, null);
    }

    public GrabSingWrapProcessLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GrabSingWrapProcessLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GrabSingWrapProcessLayout, defStyleAttr, 0);
        mColorProcessFirst = a.getColor(R.styleable.GrabSingWrapProcessLayout_process_first_color, DEFAULT_PROCESS_FIRST_COLOR);
        mColorProcessSecond = a.getColor(R.styleable.GrabSingWrapProcessLayout_process_second_color, DEFAULT_PROCESS_SECOND_COLOR);
        a.recycle();
        initView();
    }

    public void setOnAnimationCtrlListener(OnAnimationCtrlListener mOnAnimationCtrlListener) {
        this.mOnAnimationCtrlListener = mOnAnimationCtrlListener;
    }


    public OnProgressListener getOnProgressListener() {
        return mOnProgressListener;
    }

    public void setOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }


    public long getDurationAnim() {
        return durationAnim;
    }

    public void setDurationAnim(long durationAnim) {
        this.durationAnim = durationAnim;
    }

    public void startAnim() {
        mProgress = 0f;
        anim = startAnim(getDurationAnim());
        animLists.add(anim);
    }


    private void initView() {
        reachedPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        reachedPaint.setStyle(Paint.Style.STROKE);
        reachedPaint.setStrokeWidth(lineWidth);
//        reachedPaint.setTextAlign(Paint.Align.RIGHT);
        reachedPaint.setColor(mColorProcessFirst);
        reachedPaint.setStrokeJoin(Paint.Join.ROUND);
        reachedPaint.setStrokeCap(Paint.Cap.ROUND);
        unreachedPaint = new Paint(reachedPaint);
//        unreachedPaint.setTextAlign(Paint.Align.RIGHT);
        unreachedPaint.setColor(mColorProcessSecond);
        mPaintClear.setXfermode(mPorterDuffXfermodeClear);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (unreachedPath == null) {
            unreachedPath = new Path();
        }

        unreachedPath.addOval(new RectF(lineWidth/2, lineWidth/2, w - lineWidth/2, h - lineWidth/2), Path.Direction.CCW);

        if (reachedPath == null) {
            reachedPath = new Path();
        }
        Path.Direction dir;
        Configuration mConfig = getContext().getResources().getConfiguration();
        if(mConfig.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
             dir = Path.Direction.CW;
        }else{
            dir = Path.Direction.CCW;
        }
        reachedPath.addOval(new RectF(lineWidth/2, lineWidth/2, w - lineWidth/2, h - lineWidth/2), dir);

        measureReachedPath = new PathMeasure(reachedPath, false);

        measureReachedLength = measureReachedPath.getLength();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT);
        if(isShowProcess){
            canvas.save();
            Configuration mConfig = getContext().getResources().getConfiguration();
            if(mConfig.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL){
                canvas.rotate(-180, mWidth / 2, mHeight / 2);
            }
            // canvas.rotate(-90, mWidth / 2, mHeight / 2);
            drawReachedRect(canvas);
            canvas.restore();
        }


    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    private void clearCanvas(Canvas canvas) {
        if (canvas != null) {
            canvas.drawPaint(mPaintClear);
        }
    }

    Path pathProcess = new Path();

    private void drawReachedRect(Canvas canvas) {
        unreachedPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(unreachedPath, unreachedPaint);
        float currLength = measureReachedLength * mProgress;
        pathProcess.reset();
        // 避免硬件加速的Bug
//        pathProcess.lineTo(0, 0);
        measureReachedPath.getSegment(0, currLength, pathProcess, true);
        canvas.drawPath(pathProcess, reachedPaint);

    }

    public float dp2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    public void setProgress(float progress) {
        this.mProgress = progress;
        postInvalidate();
    }

    public void showProcess(boolean isShow){
        isShowProcess = isShow;
    }

    private ValueAnimatorGrab startAnim(long duration) {
        ValueAnimatorGrab valueAnimator = new ValueAnimatorGrab();
        valueAnimator.setIntValues(PROCESS_COUNT,0);
        valueAnimator.setDrawMode(++animCount);
        valueAnimator.setDuration(duration);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimatorGrab.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.d(TAG, "onAnimationUpdate:");
                animStatus = ANIM_STATUS_UPDATE;
                int curProcess = (int) animation.getAnimatedValue();
                if (animation instanceof ValueAnimatorGrab) {
                    ValueAnimatorGrab animGrab = (ValueAnimatorGrab) animation;
                    int curAnimMode = animGrab.getDrawMode();
                    if (curAnimMode == animCount) {
                        Message message = handler.obtainMessage();
                        ProcessHolder mProcessHolder = new ProcessHolder();
                        mProcessHolder.setStatus(animStatus);
                        mProcessHolder.setProcess(((float) curProcess) / PROCESS_COUNT);
                        message.what = curAnimMode;
                        message.obj = mProcessHolder;
                        handler.sendMessage(message);
                    } else {
                        animation.cancel();
                    }
                } else {
                    Message message = handler.obtainMessage();
                    ProcessHolder mProcessHolder = new ProcessHolder();
                    mProcessHolder.setStatus(animStatus);
                    mProcessHolder.setProcess(((float) curProcess) / PROCESS_COUNT);
                    message.what = animCount;
                    message.obj = mProcessHolder;
                    handler.sendMessage(message);
                }

            }
        });


        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart:");
                animStatus = ANIM_STATUS_START;
               // Message message = handler.obtainMessage();
               // ProcessHolder mProcessHolder = new ProcessHolder();
               // mProcessHolder.setStatus(animStatus);
               // message.obj = mProcessHolder;
               // handler.sendMessage(message);

                if (animation instanceof ValueAnimatorGrab) {
                    ValueAnimatorGrab animGrab = (ValueAnimatorGrab) animation;
                    int curAnimMode = animGrab.getDrawMode();
                    Message message = handler.obtainMessage();
                    ProcessHolder mProcessHolder = new ProcessHolder();
                    mProcessHolder.setStatus(animStatus);
                    message.what = curAnimMode;
                    message.obj = mProcessHolder;
                    handler.sendMessage(message);

                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd:");
                animation.cancel();
                animStatus = ANIM_STATUS_END;
//                Message message = handler.obtainMessage();
//                ProcessHolder mProcessHolder = new ProcessHolder();
//                mProcessHolder.setStatus(animStatus);
//                message.obj = mProcessHolder;
//                handler.sendMessage(message);

                if (animation instanceof ValueAnimatorGrab) {
                    ValueAnimatorGrab animGrab = (ValueAnimatorGrab) animation;
                    int curAnimMode = animGrab.getDrawMode();
                    Message message = handler.obtainMessage();
                    ProcessHolder mProcessHolder = new ProcessHolder();
                    mProcessHolder.setStatus(animStatus);
                    message.what = curAnimMode;
                    message.obj = mProcessHolder;
                    handler.sendMessage(message);

                }

                if (mOnAnimationCtrlListener != null) {
                    mOnAnimationCtrlListener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel:");
                animStatus = ANIM_STATUS_CANCEL;
//                Message message = handler.obtainMessage();
//                ProcessHolder mProcessHolder = new ProcessHolder();
//                mProcessHolder.setStatus(animStatus);
//                message.obj = mProcessHolder;
//                handler.sendMessage(message);

                if (animation instanceof ValueAnimatorGrab) {
                    ValueAnimatorGrab animGrab = (ValueAnimatorGrab) animation;
                    int curAnimMode = animGrab.getDrawMode();
                    Message message = handler.obtainMessage();
                    ProcessHolder mProcessHolder = new ProcessHolder();
                    mProcessHolder.setStatus(animStatus);
                    message.what = curAnimMode;
                    message.obj = mProcessHolder;
                    handler.sendMessage(message);

                }

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat:");
            }
        });
        valueAnimator.start();
        return valueAnimator;
    }

    public void cancelAnim() {
        if(animLists.size() != 0){
            for(ValueAnimatorGrab animatorGrab : animLists){
                if(animatorGrab != null){
                    animatorGrab.cancel();
                    animatorGrab = null;
                }
            }
        }
    }

    public void pauseAnim(){
        if(anim != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                anim.pause();
            }
        }
    }

    public void resumeAnim(){
        if(anim != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                anim.resume();
            }
        }
    }


    public interface OnAnimationCtrlListener {
        void onAnimationEnd(Animator animation);
    }

    public interface OnProgressListener{
        void onProgress(float progress);
    }

    private class ProcessHolder {


        private int status = 0;

        private float process = 0f;

        public float getProcess() {
            return process;
        }

        public void setProcess(float process) {
            this.process = process;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    private class ValueAnimatorGrab extends ValueAnimator {
        public int getDrawMode() {
            return drawMode;
        }

        public void setDrawMode(int drawMode) {
            this.drawMode = drawMode;
        }

        private int drawMode;
    }
}
