package com.test.circletimerview;

/**
 * Created by admin on 16/4/19.
 * 圆形 imageview  可设置边缘 边框\
 * app:civ_border_color="#000" 边框颜色
 * app:civ_border_width="3dp"  边框宽度
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;


public class CircleTimerView extends View implements View.OnClickListener {


    private static final String TAG = "CircleTimerView";

    private static final int DEFAULT_BORDER_WIDTH = 1;
    private static final int DEFAULT_BORDER_COLOR = Color.WHITE;
    private static final int DEFAULT_FILL_COLOR = Color.TRANSPARENT;
    private static final int DEFAULT_RECT_COLOR = Color.GREEN;


    public static final int MODE_COUNT_DOWN_WITH_CIRCLE = 0;//有环形
    public static final int MODE_COUNT_DOWN_NO_CIRCLE = 1;//无环形--抢唱前的321倒计时
    public static final int MODE_COUNT_DOWN_WITH_RECT = 2;//方形--录制
    public static final int MODE_COUNT_DOWN_WITH_IMG = 3;//有图片的环形--抢唱
    public static final int MODE_CLEAN = 4;//清屏

    private static final float FONT_SIZE = 24f;

    private int timeDurationTitleCard = 5;//321倒计时文案
    private int timeDurationSingAnswer = 5;//抢唱时间
    private int timeDurationRecord = 5;//录制时间
    private int timeDurationEnterRoom = 5;


    private final Paint mBitmapPaint = new Paint();//绘制图片笔
    private final Paint mUpBorderPaint = new Paint();//顶部边距笔
    private final Paint mDownBorderPaint = new Paint();//底部部边距笔
    private final Paint mFillPaint = new Paint();//填充笔
    private final Paint mRectPaint = new Paint();//矩形笔

    private final Paint mPaintClear = new Paint();
    private final Paint mPaintText = new Paint();

    private final Paint mPaintEnterRoomFill = new Paint();
    private final Paint mPaintEnterRoomText = new Paint();
    private final Paint mPaintEnterRoomDownBorder= new Paint();

    private final Paint mPaintEraser = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

    private final Paint mPaintRecordCyc = new Paint();
    private final Paint mPaintRecordCycPressces = new Paint();


    private ValueAnimator animatorTitleCard;
    private ValueAnimator animatorSingAnswer;
    private ValueAnimator animatorRecord;
    private ValueAnimator animatorEnterRoom;

    private final PorterDuffXfermode mPorterDuffXfermodeClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

    private int mUpBorderColor = DEFAULT_BORDER_COLOR;
    private int mDownBorderColor = DEFAULT_BORDER_COLOR;
    private int mRectColor = DEFAULT_RECT_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private int mFillColor = DEFAULT_FILL_COLOR;
    private int drawMode = 1;

    private int lastDrawMode = 1;

    private int remainTimeTitleCard = 0;

    private int timeDurationSingAnswerProcess = 1;
    private int timeDurationRecordProcess = 1;
    private int timeDurationEnterRoomProcess = 1;
    private int remainTimeEnterRoom = 0;

    private Bitmap mBitMap = null;
    private Bitmap mBitMapScale = null;
//    private Bitmap fgBitmap = null;
    private Matrix matrix = new Matrix();

    private Context context;




    private OnClickCtrlListener mOnClickCtrlListener;
    private OnAnimationCtrlListener mOnAnimationCtrlListener;

    public void setOnClickCtrlListener(OnClickCtrlListener mOnClickCtrlListener) {
        this.mOnClickCtrlListener = mOnClickCtrlListener;
    }
    public void setOnAnimationCtrlListener(OnAnimationCtrlListener listener) {
        this.mOnAnimationCtrlListener = listener;
    }

    public CircleTimerView(Context context) {
        this(context,null);

    }

    public CircleTimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public int getDrawMode() {
        return drawMode;
    }

    public boolean setDrawMode(int mMode) {
        //TODO
    //        if(animIsRunning()){
//            return false;
//        }else{
            setLastDrawMode(drawMode);
            this.drawMode = mMode;
            return true;
//        }
}

    public int getTimeDurationTitleCard() {
        return timeDurationTitleCard;
    }

    public void setTimeDurationTitleCard(int timeDurationTitleCard) {
        this.timeDurationTitleCard = timeDurationTitleCard;
    }

    public int getTimeDurationSingAnswer() {
        return timeDurationSingAnswer;
    }

    public void setTimeDurationSingAnswer(int timeDurationSingAnswer) {
        this.timeDurationSingAnswer = timeDurationSingAnswer;
    }

    public int getTimeDurationRecord() {
        return timeDurationRecord;
    }

    public void setTimeDurationRecord(int timeDurationRecord) {
        this.timeDurationRecord = timeDurationRecord;
    }

    public int getTimeDurationEnterRoom() {
        return timeDurationEnterRoom;
    }

    public void setTimeDurationEnterRoom(int timeDurationEnterRoom) {
        this.timeDurationEnterRoom = timeDurationEnterRoom;
    }

    public int getLastDrawMode() {
        return lastDrawMode;
    }

    public void setLastDrawMode(int lastDrawMode) {
        this.lastDrawMode = lastDrawMode;
    }

    public CircleTimerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTimerView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleTimerView_ctv_border_width, DEFAULT_BORDER_WIDTH);
        mUpBorderColor = a.getColor(R.styleable.CircleTimerView_ctv_up_border_color, DEFAULT_BORDER_COLOR);
        mDownBorderColor = a.getColor(R.styleable.CircleTimerView_ctv_down_border_color, DEFAULT_BORDER_COLOR);
        mRectColor = a.getColor(R.styleable.CircleTimerView_ctv_rect_color, DEFAULT_RECT_COLOR);
        mFillColor = a.getColor(R.styleable.CircleTimerView_ctv_fill_color, DEFAULT_FILL_COLOR);
        int resourceId = a.getResourceId(R.styleable.CircleTimerView_ctv_img_src, 0);
        setDrawMode(a.getInt(R.styleable.CircleTimerView_ctv_style_mode, 0));
        setLastDrawMode(getDrawMode());
        a.recycle();
        if (resourceId != 0) {
            Drawable drawable = ContextCompat.getDrawable(context, resourceId);
//            Drawable drawable = ResourcesCompat.getDrawable(getResources(), resourceId, null);
            if (drawable instanceof BitmapDrawable) {
                mBitMap = ((BitmapDrawable) drawable).getBitmap();
            }
        }
        init();
        setWillNotDraw(false);
        initOnClickListener();
    }

    private void initOnClickListener() {
        setOnClickListener(this);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if ((mBitMapScale == null || w != oldw || h != oldh) && mBitMap != null) {
            int bitmapWidth = mBitMap.getWidth();
            int bitmapHeight = mBitMap.getHeight();
//            int R = w < h ? w : h;
//            double ScaleWidth = Math.sqrt(2) * R /2 ;
            int wantWidth = DensityUtils.dip2px(context, 46);
            float ratio = (float) (wantWidth / bitmapWidth);
            matrix.preScale(ratio, ratio);
            mBitMapScale = Bitmap.createBitmap(mBitMap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // 生成前景图Bitmap
//            fgBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }

    }

    private void init() {
        initData();
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        initPaint();
    }

    private void initData() {
        remainTimeTitleCard = timeDurationTitleCard;
    }

    private void initPaint() {
        mPaintClear.setXfermode(mPorterDuffXfermodeClear);

        //mBitmapPaint.setColor(Color.TRANSPARENT);
        // mBitmapPaint.setAntiAlias(true);//取消锯齿
        mBitmapPaint.setStrokeWidth(1);
        // mBitmapPaint.setStyle(Paint.Style.FILL);

        mUpBorderPaint.setColor(mUpBorderColor);
        mUpBorderPaint.setAntiAlias(true);
        mUpBorderPaint.setStyle(Paint.Style.STROKE);//设置画圆弧的画笔的属性为描边(空心)
        mUpBorderPaint.setStrokeWidth(mBorderWidth);

        mDownBorderPaint.setColor(mDownBorderColor);
        mDownBorderPaint.setAntiAlias(true);
        mDownBorderPaint.setStyle(Paint.Style.STROKE);
        mDownBorderPaint.setStrokeWidth(mBorderWidth);

        mFillPaint.setColor(mFillColor);
        mFillPaint.setAntiAlias(true);//取消锯齿
        mFillPaint.setStyle(Paint.Style.FILL);

        mRectPaint.setColor(mRectColor);
        mRectPaint.setAntiAlias(true);//取消锯齿
        mRectPaint.setStyle(Paint.Style.FILL);

        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(Color.WHITE);
        mPaintText.setAntiAlias(true);
        mPaintText.setTextSize(DensityUtils.sp2px(context, FONT_SIZE));
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
//        mPaintText.setTextAlign(Paint.Align.CENTER);

        mPaintEraser.setARGB(128, 255, 0, 0);
        // 设置混合模式为DST_IN
        mPaintEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaintEraser.setStyle(Paint.Style.STROKE);
        mPaintEraser.setStrokeWidth(mBorderWidth);
        mPaintEraser.setAntiAlias(true);

//        context.getResources().getColor(R.color.white_40);
        mPaintRecordCyc.setColor(ContextCompat.getColor(context, R.color.white_40));
        mPaintRecordCyc.setStyle(Paint.Style.STROKE);
        mPaintRecordCyc.setStrokeWidth(DensityUtils.dip2px(context, 6));
        mPaintRecordCyc.setAntiAlias(true);


        mPaintRecordCycPressces.setColor(ContextCompat.getColor(context, R.color.gift_common_red));
        mPaintRecordCycPressces.setStyle(Paint.Style.STROKE);
        mPaintRecordCycPressces.setStrokeWidth(DensityUtils.dip2px(context, 6));
        mPaintRecordCycPressces.setAntiAlias(true);



        mPaintEnterRoomFill.setColor(Color.BLACK);
        mPaintEnterRoomFill.setAntiAlias(true);//取消锯齿
        mPaintEnterRoomFill.setStyle(Paint.Style.FILL);


        mPaintEnterRoomText.setStyle(Paint.Style.FILL);
        mPaintEnterRoomText.setColor(ContextCompat.getColor(context, R.color.gift_common_red));
        mPaintEnterRoomText.setAntiAlias(true);
        mPaintEnterRoomText.setTextSize(DensityUtils.sp2px(context, FONT_SIZE));
        mPaintEnterRoomText.setTypeface(Typeface.DEFAULT_BOLD);


        mPaintEnterRoomDownBorder.setColor(ContextCompat.getColor(context, R.color.gift_common_red));
        mPaintEnterRoomDownBorder.setAntiAlias(true);
        mPaintEnterRoomDownBorder.setStyle(Paint.Style.STROKE);
        mPaintEnterRoomDownBorder.setStrokeWidth(DensityUtils.dip2px(context,2));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        clearCanvas(canvas);
//        onDrawModeWithRect(canvas);
        //判断当前模式根据模式进行绘制不同画面
        switch (getDrawMode()){
            case MODE_COUNT_DOWN_WITH_CIRCLE :
                onDrawModeWithCircle(canvas);
                break;
            case MODE_COUNT_DOWN_NO_CIRCLE :
                onDrawModeNoCircle(canvas);
                break;
            case MODE_COUNT_DOWN_WITH_RECT :
                onDrawModeWithRect(canvas);
                break;
            case MODE_COUNT_DOWN_WITH_IMG :
                onDrawModeWithImg(canvas);
                break;
            case MODE_CLEAN:
                if (canvas!=null){
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                }
        }


    }


    private void clearCanvas(Canvas canvas){
        if (canvas!=null){
            canvas.drawPaint(mPaintClear);
        }
//        if(fgBitmap != null){
//            fgBitmap.eraseColor(Color.TRANSPARENT);
//        }
    }

    private void onDrawModeWithCircle(Canvas canvas){

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int cycleR = viewWidth < viewHeight ? viewWidth/2:viewHeight/2;
        int cycleRInner = cycleR - DensityUtils.dip2px(context, 4);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, cycleRInner, mPaintEnterRoomFill);

        //绘制倒计时数字
        String remainTime  = String.valueOf(this.remainTimeEnterRoom);

        Paint.FontMetricsInt fm = mPaintEnterRoomText.getFontMetricsInt();
        float baseLineX = viewWidth / 2 - mPaintEnterRoomText.measureText(remainTime) / 2 ;
        float baseLineY = viewHeight/2 - fm.bottom/2 - fm.top/2 ;
        canvas.drawText(remainTime, baseLineX, baseLineY, mPaintEnterRoomText);


        //绘制倒计时圆环
        RectF oval = new RectF();
        cycleR = cycleR - DensityUtils.dip2px(context, 2);
        oval.left = (viewWidth/2 - cycleR);
        oval.top = (viewHeight/2 - cycleR);
        oval.right = cycleR * 2 + (viewWidth/2 - cycleR);
        oval.bottom = cycleR * 2 + (viewWidth/2 - cycleR);

        canvas.drawArc(oval, -90, -((float)timeDurationEnterRoomProcess / (getTimeDurationEnterRoom()/10)) * 360, false, mPaintEnterRoomDownBorder);

    }


    private void onDrawModeNoCircle(Canvas canvas){


        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int cycleR = viewWidth < viewHeight ? viewWidth/2:viewHeight/2;
        int cycleRInner = cycleR - DensityUtils.dip2px(context, 3);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, cycleRInner, mFillPaint);

        //绘制倒计时数字
        String remainTime  = String.valueOf(this.remainTimeTitleCard);

        Paint.FontMetricsInt fm = mPaintText.getFontMetricsInt();
        float baseLineX = viewWidth / 2 - mPaintText.measureText(remainTime) / 2 ;
        float baseLineY = viewHeight/2 - fm.bottom/2 - fm.top/2 ;
        canvas.drawText(remainTime, baseLineX, baseLineY, mPaintText);



    }

    private void onDrawModeWithRect(Canvas canvas){
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int cycleR = viewWidth < viewHeight ? viewWidth/2:viewHeight/2;
        RectF oval = new RectF();
        cycleR = cycleR - DensityUtils.dip2px(context, 4);
        oval.left = (viewWidth/2 - cycleR);
        oval.top = (viewHeight/2 - cycleR);
        oval.right = cycleR * 2 + (viewWidth/2 - cycleR);
        oval.bottom = cycleR * 2 + (viewWidth/2 - cycleR);

        canvas.drawArc(oval, -90, - 360, false, mPaintRecordCyc);

        canvas.drawArc(oval, -90, -((float)timeDurationRecordProcess / (getTimeDurationRecord()/10)) * 360, false, mPaintRecordCycPressces);

        RectF mRectF = new RectF();
        mRectF.left = viewWidth/2 - DensityUtils.dip2px(context, 14);
        mRectF.top = viewHeight/2 - DensityUtils.dip2px(context, 14);

        mRectF.right = viewWidth/2 + DensityUtils.dip2px(context, 14);
        mRectF.bottom = viewHeight/2 + DensityUtils.dip2px(context, 14);
        canvas.drawRoundRect(mRectF,DensityUtils.dip2px(context, 4),DensityUtils.dip2px(context, 4),mRectPaint);


    }

    private void onDrawModeWithImg(Canvas canvas){

        if(mBitMapScale != null){
            int bitmapWidth = mBitMapScale.getWidth();
            int bitmapHeight = mBitMapScale.getHeight();
            int viewWidth = getWidth();
            int viewHeight = getHeight();
            float left = viewWidth/2 - bitmapWidth/2;
            float top = viewHeight/2 - bitmapHeight/2;
            int cycleR = viewWidth < viewHeight ? viewWidth/2:viewHeight/2;
            int cycleRInner = cycleR - DensityUtils.dip2px(context, 3);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, cycleRInner, mFillPaint);
            canvas.drawBitmap(mBitMapScale,left,top,mBitmapPaint);

            //绘制倒计时圆环
            RectF oval = new RectF();
            cycleR = cycleR - DensityUtils.dip2px(context, 1);
            oval.left = (viewWidth/2 - cycleR);
            oval.top = (viewHeight/2 - cycleR);
            oval.right = cycleR * 2 + (viewWidth/2 - cycleR);
            oval.bottom = cycleR * 2 + (viewWidth/2 - cycleR);

            canvas.drawArc(oval, -90, -((float)timeDurationSingAnswerProcess / (getTimeDurationSingAnswer()/10)) * 360, false, mDownBorderPaint);

        }


    }


    public void startAnim(){
        switch (getDrawMode()){
            case MODE_COUNT_DOWN_WITH_CIRCLE :
                startEnterRoomAnim();
                break;
            case MODE_COUNT_DOWN_NO_CIRCLE :
                startTitleCardAnim();
                break;
            case MODE_COUNT_DOWN_WITH_RECT :
                startRecordAnim();
                break;
            case MODE_COUNT_DOWN_WITH_IMG :
                startSingAnswerAnim();
                break;
            case MODE_CLEAN:
                postInvalidate();
                    break;
        }
    }


    public void cancelAnim(){
        switch (getDrawMode()){
            case MODE_COUNT_DOWN_WITH_CIRCLE :
                if(animatorEnterRoom != null){
                    animatorEnterRoom.cancel();
                }
                break;
            case MODE_COUNT_DOWN_NO_CIRCLE :
                if(animatorTitleCard != null){
                    animatorTitleCard.cancel();
                }
                break;
            case MODE_COUNT_DOWN_WITH_RECT :

                if(animatorRecord != null){
                    animatorRecord.cancel();
                }
                break;
            case MODE_COUNT_DOWN_WITH_IMG :
                if(animatorSingAnswer != null){
                    animatorSingAnswer.cancel();
                }
                break;
        }
    }

    private boolean animIsRunning() {
        boolean ret = false;
        switch (getDrawMode()) {
            case MODE_COUNT_DOWN_WITH_CIRCLE:
                if(animatorEnterRoom != null){
                    ret = animatorEnterRoom.isRunning();
                }
                break;
            case MODE_COUNT_DOWN_NO_CIRCLE:
                if (animatorTitleCard != null) {
                    ret = animatorTitleCard.isRunning();
                }
                break;
            case MODE_COUNT_DOWN_WITH_RECT:

                if(animatorRecord != null){
                    ret = animatorRecord.isRunning();
                }
                break;
            case MODE_COUNT_DOWN_WITH_IMG:
                if (animatorSingAnswer != null) {
                    ret = animatorSingAnswer.isRunning();
                }

                break;
        }

        return ret;
    }


    private void startRecordAnim(){

        if(animatorRecord != null && animatorRecord.isRunning()){
            return;
        }

        animatorRecord = ValueAnimator.ofInt(getTimeDurationRecord()/10,0);
        animatorRecord.setDuration(getTimeDurationRecord()); //毫秒
        animatorRecord.setRepeatCount(0);
        animatorRecord.setInterpolator(new LinearInterpolator());
        animatorRecord.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                timeDurationRecordProcess = (int)animation.getAnimatedValue();
                postInvalidate();


            }
        });

        animatorRecord.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart:");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd:");
                animation.cancel();
                if (mOnAnimationCtrlListener!=null){
                    mOnAnimationCtrlListener.onAnimationEnd(animation,getDrawMode());
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel:");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat:");
            }
        });
        animatorRecord.start();
    }

    private void startSingAnswerAnim(){
        if(animatorSingAnswer != null && animatorSingAnswer.isRunning()){
            return;
        }

        animatorSingAnswer = ValueAnimator.ofInt(getTimeDurationSingAnswer()/10,0);
        animatorSingAnswer.setDuration(getTimeDurationSingAnswer()); //毫秒
        animatorSingAnswer.setRepeatCount(0);
        animatorSingAnswer.setInterpolator(new LinearInterpolator());
        animatorSingAnswer.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                timeDurationSingAnswerProcess = (int)animation.getAnimatedValue();
                Log.d(TAG, "onAnimationUpdate: remainTime = "+timeDurationSingAnswerProcess);
                postInvalidate();


            }
        });

        animatorSingAnswer.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart:");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(TAG, "onAnimationEnd:");
                if (mOnAnimationCtrlListener!=null){
                    mOnAnimationCtrlListener.onAnimationEnd(animation,getDrawMode());
                }
                animation.cancel();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel:");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat:");
            }
        });
        animatorSingAnswer.start();
    }

    private void startTitleCardAnim(){
        if(animatorTitleCard != null && animatorTitleCard.isRunning()){
            return;
        }

        animatorTitleCard = ValueAnimator.ofInt(getTimeDurationTitleCard()/10,0);
        animatorTitleCard.setDuration(getTimeDurationTitleCard());
        animatorTitleCard.setRepeatCount(0);
        animatorTitleCard.setInterpolator(new LinearInterpolator());
        animatorTitleCard.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (int)animation.getAnimatedValue();
                // Log.d(TAG, "onAnimationUpdate: remainTime = "+remainTime +" val="+val + " val%100="+val%100);
                if(val%100 >= 90){
                    remainTimeTitleCard = val/100+1;
                    //     Log.d(TAG, "onAnimationUpdate: remainTime = "+remainTime);
                    postInvalidate();
                }


            }
        });

        animatorTitleCard.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart: remainTimeTitleCard = "+remainTimeTitleCard);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnAnimationCtrlListener!=null){
                    mOnAnimationCtrlListener.onAnimationEnd(animation,getDrawMode());
                }
                animation.cancel();
                Log.d(TAG, "onAnimationEnd: remainTimeTitleCard = "+remainTimeTitleCard);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel: remainTimeTitleCard = "+remainTimeTitleCard);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat: remainTimeTitleCard = "+remainTimeTitleCard);
            }
        });
        animatorTitleCard.start();
    }


    private void startEnterRoomAnim(){
        if(animatorEnterRoom != null && animatorEnterRoom.isRunning()){
            return;
        }

        animatorEnterRoom = ValueAnimator.ofInt(getTimeDurationEnterRoom()/10,0);
        animatorEnterRoom.setDuration(getTimeDurationEnterRoom());
        animatorEnterRoom.setRepeatCount(0);
        animatorEnterRoom.setInterpolator(new LinearInterpolator());
        animatorEnterRoom.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                timeDurationEnterRoomProcess = (int)animation.getAnimatedValue();

                Log.d(TAG, "onAnimationUpdate:  val/100="+ timeDurationEnterRoomProcess/100 +" val="+timeDurationEnterRoomProcess + " val%100="+timeDurationEnterRoomProcess%100);
                remainTimeEnterRoom = timeDurationEnterRoomProcess/100;
                postInvalidate();

            }
        });

        animatorEnterRoom.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(TAG, "onAnimationStart: remainTime = "+remainTimeEnterRoom);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnAnimationCtrlListener!=null){
                    mOnAnimationCtrlListener.onAnimationEnd(animation,getDrawMode());
                }
                animation.cancel();
                Log.d(TAG, "onAnimationEnd: remainTime = "+remainTimeEnterRoom);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "onAnimationCancel: remainTime = "+remainTimeEnterRoom);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "onAnimationRepeat: remainTime = "+remainTimeEnterRoom);
            }
        });
        animatorEnterRoom.start();
    }

    @Override
    public void onClick(View v) {
        if(mOnClickCtrlListener != null){
            mOnClickCtrlListener.onClick(v,getDrawMode(), animIsRunning());
        }
    }


    public interface OnClickCtrlListener{
        void onClick(View v, int mode, boolean animIsRunning);
    }

    public interface OnAnimationCtrlListener{
        void onAnimationEnd(Animator animation,int mode);
    }

}
