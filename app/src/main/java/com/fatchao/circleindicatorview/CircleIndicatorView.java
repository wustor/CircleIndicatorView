package com.fatchao.circleindicatorview;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.text.DecimalFormat;

public class CircleIndicatorView extends View {

    private Paint outPaint = new Paint();
    private Paint inPaint = new Paint();
    private Paint dashPaint = new Paint();
    private Paint middleTextPaint = new Paint();
    private float sweepAngle = 240;//圆结束的弧度
    int mCenter = 0;
    int mRadius = 0;

    private float indexValue;
    private float inSweepAngle;
    private int mLargeSize;
    private int mSmallSize;
    private int mGrayColor;
    private int mWhiteColor;

    public void setIndexValue(float indexValue) {
        this.indexValue = indexValue;
    }


    public float getInSweepAngle() {
        return inSweepAngle;
    }

    public void setInSweepAngle(float inSweepAngle) {
        this.inSweepAngle = inSweepAngle;
    }


    public CircleIndicatorView(Context context) {
        this(context, null);
    }

    public CircleIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CircleIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicatorView);
        mLargeSize = (int) ta.getDimension(R.styleable.CircleIndicatorView_largeSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics()));
        mSmallSize = (int) ta.getDimension(R.styleable.CircleIndicatorView_smallSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, context.getResources().getDisplayMetrics()));
        mGrayColor = ta.getColor(R.styleable.CircleIndicatorView_grayColor, Color.GRAY);
        mWhiteColor = ta.getColor(R.styleable.CircleIndicatorView_whiteColor, Color.WHITE);
        ta.recycle();
        initPaint();
    }


    /**
     * 初始化画笔
     */
    public void initPaint() {
        outPaint.setColor(mGrayColor);
        outPaint.setAntiAlias(true);
        outPaint.setStyle(Paint.Style.STROKE);
        outPaint.setStrokeWidth(5.0f);
        inPaint.setAntiAlias(true);
        inPaint.setStyle(Paint.Style.STROKE);
        inPaint.setStrokeWidth(5.0f);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setAntiAlias(true);
        dashPaint.setColor(Color.WHITE);
        dashPaint.setStrokeWidth(3);
        setLayerType(View.LAYER_TYPE_SOFTWARE, dashPaint);
        PathEffect effects = new DashPathEffect(new float[]{20, 6}, 0);
        dashPaint.setPathEffect(effects);
        middleTextPaint = new Paint();
        middleTextPaint.setTextAlign(Paint.Align.CENTER);
        middleTextPaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCenter = getMeasuredWidth() / 2;
        mRadius = getMeasuredHeight() / 2;
        RectF rectF = new RectF(mCenter - mRadius + getPaddingLeft(), mCenter - mRadius + getPaddingTop(), mCenter + mRadius - getPaddingRight(), mCenter + mRadius - getPaddingBottom());
        //内虚线圆
        RectF dashedRectF = new RectF(mCenter - mRadius + 20 + getPaddingLeft(), mCenter - mRadius + 20 + getPaddingTop(), mCenter + mRadius - 20 - getPaddingRight(), mCenter + mRadius - 20 - getPaddingBottom());
        float startAngle = 150;
        canvas.drawArc(dashedRectF, startAngle, sweepAngle, false, dashPaint);

        //进度椭圆
        canvas.drawArc(rectF, startAngle, getInSweepAngle(), false, inPaint);
        //外实线椭圆
        canvas.drawArc(rectF, startAngle, sweepAngle, false, outPaint);
        //中心数字
        middleTextPaint.setColor(mWhiteColor);
        middleTextPaint.setTextSize(mLargeSize);
        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String value = decimalFormat.format(indexValue);//format 返回的是字符串
        Log.d("value---->", value);
        String[] split = value.split("\\.");
        String text = split[0];
        int mTextWidth = (int) middleTextPaint.measureText(text);
        Paint.FontMetrics fontMetrics = middleTextPaint.getFontMetrics();
        int textHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        canvas.drawText(text, getMeasuredWidth() / 2, getMeasuredHeight() / 2 + textHeight / 2 - fontMetrics.bottom, middleTextPaint);

        //左边符号
        String text0 = "￥";
        middleTextPaint.setTextSize(mSmallSize);
        int mTextWidth0 = (int) middleTextPaint.measureText(text0);
        canvas.drawText(text0, getMeasuredWidth() / 2 - mTextWidth / 2 - mTextWidth0 / 2, getMeasuredHeight() / 2 + textHeight / 2 - fontMetrics.bottom, middleTextPaint);

        //右边数字
        String text2 = "." + split[1];
        middleTextPaint.setTextSize(mSmallSize);
        int mTextWidth1 = (int) middleTextPaint.measureText(text2);
        canvas.drawText(text2, getMeasuredWidth() / 2 + mTextWidth / 2 + mTextWidth1 / 2, getMeasuredHeight() / 2 + textHeight / 2 - fontMetrics.bottom, middleTextPaint);


        middleTextPaint.setTextSize(mSmallSize);
        Paint.FontMetrics fontMetrics1 = middleTextPaint.getFontMetrics();
        textHeight = (int) (fontMetrics1.bottom - fontMetrics1.top);
        canvas.drawText("可用额度", getMeasuredWidth() / 2, getMeasuredHeight() / 2 + textHeight / 2 - fontMetrics1.bottom - 60, middleTextPaint);

        //绘制发光的小圆点
        Paint paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setAntiAlias(true);//抗锯齿功能
        canvas.translate(getWidth() / 2, getHeight() / 2);//这时候的画布已经移动到了中心位置
        canvas.rotate(getInSweepAngle() + 60);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.iv_picture);
        canvas.translate(0, getHeight() / 2 - getPaddingLeft());

        // 获得图片的宽高
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        // 设置想要的大小
        int newWidth = 30;
        int newHeight = 30;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap dotBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
        canvas.drawBitmap(dotBitmap, -15, -15, paintCircle);
        canvas.rotate(-(getInSweepAngle() + 60));
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                break;
        }
        return result;
    }

    public void goToPoint(float value) {
        if (value < 20)
            inPaint.setColor(ContextCompat.getColor(getContext(), R.color.red));
        else
            inPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        float inSweepAngle = sweepAngle * value / 100;
        ValueAnimator angleAnim = ValueAnimator.ofFloat(0f, inSweepAngle);
        float inValue = value * 8888 / 100;
        ValueAnimator valueAnim = ValueAnimator.ofFloat(0, inValue);
        angleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float currentValue = (float) valueAnimator.getAnimatedValue();
                setInSweepAngle(currentValue);
                invalidate();
            }
        });
        valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float currentValue = (float) valueAnimator.getAnimatedValue();
                setIndexValue(currentValue);
                invalidate();
            }
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(3000);
        animatorSet.playTogether(angleAnim, valueAnim);
        animatorSet.start();
    }
}
