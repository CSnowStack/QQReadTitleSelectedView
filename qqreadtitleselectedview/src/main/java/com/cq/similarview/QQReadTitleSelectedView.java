package com.cq.qqreadtitleselectedview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.ArrayRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by cqll on 2016/8/10.
 */
public class QQReadTitleSelectedView extends View {
    private static final float C = 0.551915024494f;     // 一个常量，用来计算绘制圆形贝塞尔曲线控制点的位置

    private int mTextSize, mBorderWidth, mTextRectangleLength/*文本所在的框的宽度*/;
    private String[] mItemArray;//报错值
    private int mWidth, mHeight, mRadius, mRadiusChecked;
    private Paint mPaintBG/*背景*/, mPaintBGBorder/*背景边框*/, mPaintText/*文字*/, mPaintChecked/*中间选中框*/;
    private Path mPathBorder = new Path();
    private int mColorSelected, mColorNormal;
    private HorizontalPoint mHPointTopLeft = new HorizontalPoint(), mHPointTopRight = new HorizontalPoint(), mHPointBottomLeft = new HorizontalPoint(), mHPointBottomRight = new HorizontalPoint();
    private VerticalPoint mVPointLeft = new VerticalPoint(), mVPointRight = new VerticalPoint();
    private ViewPager mViewPager;
    private float mFraction;
    private Context mContext;
    private RectF mArcRectFBGLeft, mRectFBGCenter, mArcRectFBGRight;
    public QQReadTitleSelectedView(Context context) {
        this(context, null);
    }

    public QQReadTitleSelectedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQReadTitleSelectedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QQReadTitleSelectedView);
        try {
            mTextSize = typedArray.getDimensionPixelSize(R.styleable.QQReadTitleSelectedView_qrtsv_text_size, 40);
            mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.QQReadTitleSelectedView_qrtsv_border_width, 5);
            mColorSelected = typedArray.getColor(R.styleable.QQReadTitleSelectedView_qrtsv_selected_color, Color.WHITE);
            mColorNormal = typedArray.getColor(R.styleable.QQReadTitleSelectedView_qrtsv_normal_color, Color.BLUE);
            CharSequence[] items = typedArray.getTextArray(R.styleable.QQReadTitleSelectedView_qrtsv_items);
            if(items==null){
                items=mContext.getResources().getStringArray(R.array.default_item);
            }else if(items.length!=2){
                throw new IOException("");
            }
            mItemArray = new String[items.length];
            for (int i = 0; i < items.length; i++) {
                mItemArray[i] = items[i].toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }
        mPaintBG = new Paint();
        mPaintBG.setAntiAlias(true);
        mPaintBG.setStyle(Paint.Style.FILL);
        mPaintBG.setColor(mColorNormal);

        mPaintBGBorder = new Paint();
        mPaintBGBorder.setAntiAlias(true);
        mPaintBGBorder.setColor(mColorSelected);
        mPaintBGBorder.setStyle(Paint.Style.FILL);

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(mColorNormal);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setStyle(Paint.Style.FILL);

        mPaintChecked = new Paint();
        mPaintChecked.setAntiAlias(true);
        mPaintChecked.setColor(mColorSelected);//待自定义
        mPaintChecked.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);


        int minHeight = mTextSize * 3 / 2;//最小的高度
        int minWidth = mItemArray.length * (int) mPaintText.measureText(getMaxLengthStr()) * 5 / 4 + mHeight;//最小的宽度

        if (heightMode == MeasureSpec.AT_MOST && widthMode == MeasureSpec.AT_MOST) {//都为包裹则都取最小
            width = minWidth;
            height = minHeight;
        } else if (heightMode == MeasureSpec.AT_MOST) {//高度包裹
            height = minHeight;
            if (width < minWidth)
                width = minWidth;
        } else if (widthMode == MeasureSpec.AT_MOST) {//宽度包裹
            width = minWidth;
            if (height < minHeight)
                height = minHeight;

        }

        mWidth = width;
        mHeight = height;
        mRadius = height / 2;
        mTextRectangleLength = (mWidth - mHeight) / mItemArray.length;
        mRadiusChecked = mRadius - mBorderWidth;
        initBGBorderPath();
        initPoint();
        setMeasuredDimension(width, height);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mViewPager != null)
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                if (event.getX() > mWidth / 2) {
                    mViewPager.setCurrentItem(1);
                } else {
                    mViewPager.setCurrentItem(0);
                }


        return super.onTouchEvent(event);
    }

    /**
     * 初始化边框和背景的Path
     */
    private void initBGBorderPath() {

        RectF arcRectFBorderLeft = new RectF(0, 0, 2 * mRadius, mHeight);
        RectF rectFBorderCenter = new RectF(mRadius, 0, mWidth - mRadius, mHeight);
        RectF arcRectFBorderRight = new RectF(mWidth - mHeight, 0, mWidth, mHeight);

         mArcRectFBGLeft = new RectF(mBorderWidth, mBorderWidth, 2 * (mRadiusChecked) + mBorderWidth, mHeight - mBorderWidth);
         mRectFBGCenter = new RectF(mRadius, mBorderWidth, mWidth - mRadius, mHeight - mBorderWidth);
         mArcRectFBGRight = new RectF(mWidth - 2 * (mRadiusChecked) - mBorderWidth, mBorderWidth, mWidth - mBorderWidth, mHeight - mBorderWidth);

        mPathBorder.addArc(arcRectFBorderLeft, 90, 180);
        mPathBorder.addArc(arcRectFBorderRight, 270, 180);
        mPathBorder.addRect(rectFBorderCenter, Path.Direction.CCW);


    }


    /**
     * 初始化选中的点
     */
    private void initPoint() {
        mHPointTopLeft.setY(mBorderWidth);
        mHPointTopRight.setY(mBorderWidth);
        mHPointBottomLeft.setY(mBorderWidth + 2 * mRadiusChecked);
        mHPointBottomRight.setY(mBorderWidth + 2 * mRadiusChecked);
        mVPointLeft.setX(mBorderWidth);
        mVPointRight.setX(2 * mRadiusChecked + mTextRectangleLength);

        updateChecked(0);
    }


    /**
     * 修改选中图像的贝塞尔的控制点,产生移动的效果
     *
     * @param fraction 移动的比例
     */
    private void updateChecked(float fraction) {
        BigDecimal bd = new BigDecimal(fraction);
        mFraction= bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        mVPointLeft.setX(mBorderWidth + mFraction * (mTextRectangleLength + mRadiusChecked));
        mVPointRight.setX(mRadius + mTextRectangleLength + mFraction * (mTextRectangleLength + mRadius - mBorderWidth));

        mHPointTopLeft.setX(mRadius + mFraction * mTextRectangleLength, mFraction);
        mHPointBottomLeft.setX(mRadius + mFraction * mTextRectangleLength, mFraction);

        mHPointTopRight.setX(mRadius + mTextRectangleLength + mFraction * mTextRectangleLength, 1 - mFraction);
        mHPointBottomRight.setX(mRadius + mTextRectangleLength + mFraction * mTextRectangleLength, 1 - mFraction);

        mVPointLeft.setY(mRadius, mFraction);
        mVPointRight.setY(mRadius, 1 - mFraction);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBG(canvas);
        drawSelected(canvas);
        drawText(canvas);
    }


    /**
     * 画背景
     */
    private void drawBG(Canvas canvas) {

        canvas.drawPath(mPathBorder, mPaintBGBorder);

        mPaintBG.setColor(mColorNormal);
        canvas.drawRect(mRectFBGCenter, mPaintBG);


        if(mFraction==0){
            mPaintBG.setColor(mColorSelected);
        }else {
            mPaintBG.setColor(mColorNormal);
        }
        canvas.drawArc(mArcRectFBGLeft,90,180,true,mPaintBG);
        if(mFraction==1){
            mPaintBG.setColor(mColorSelected);
        }else {
            mPaintBG.setColor(mColorNormal);
        }
        canvas.drawArc(mArcRectFBGRight,270, 180,true,mPaintBG);
    }

    /**
     * 画选中的图像
     */
    private void drawSelected(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mHPointTopLeft.x, mHPointTopLeft.y);
        path.lineTo(mHPointTopRight.x, mHPointTopRight.y);
        path.cubicTo(mHPointTopRight.right.x, mHPointTopRight.right.y, mVPointRight.top.x, mVPointRight.top.y, mVPointRight.x, mVPointRight.y);
        path.cubicTo(mVPointRight.bottom.x, mVPointRight.bottom.y, mHPointBottomRight.right.x, mHPointBottomRight.right.y, mHPointBottomRight.x, mHPointBottomRight.y);
        path.lineTo(mHPointBottomLeft.x, mHPointBottomLeft.y);
        path.cubicTo(mHPointBottomLeft.left.x, mHPointBottomLeft.left.y, mVPointLeft.bottom.x, mVPointLeft.bottom.y, mVPointLeft.x, mVPointLeft.y);
        path.cubicTo(mVPointLeft.top.x, mVPointLeft.top.y, mHPointTopLeft.left.x, mHPointTopLeft.left.y, mHPointTopLeft.x, mHPointTopLeft.y);

        canvas.drawPath(path, mPaintChecked);
    }

    /**
     * 画文字
     */
    private void drawText(Canvas canvas) {

        Rect bounds = new Rect();
        for (int i = 0; i < mItemArray.length; i++) {
            mPaintText.setColor(mColorSelected);
            mPaintText.setAlpha(255);
            mPaintText.getTextBounds(mItemArray[i], 0, mItemArray[i].length(), bounds);
            Paint.FontMetricsInt fontMetrics = mPaintText.getFontMetricsInt();
            int baseLine = (mHeight / 2 + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent);
            canvas.drawText(mItemArray[i], mRadius + mTextRectangleLength * i + mTextRectangleLength / 2 - bounds.width() / 2, baseLine, mPaintText);

            mPaintText.setColor(mColorNormal);
            if(i==0){
                mPaintText.setAlpha((int) ((1-mFraction)*255));
            }else {
                mPaintText.setAlpha((int) (mFraction*255));
            }

            canvas.drawText(mItemArray[i], mRadius + mTextRectangleLength * i + mTextRectangleLength / 2 - bounds.width() / 2, baseLine, mPaintText);

        }

    }


    public void setItemArray(String[] itemArray) {
        mItemArray = itemArray;
        int minWidth = mItemArray.length * (int) mPaintText.measureText(getMaxLengthStr()) * 5 / 4 + mHeight;//最小的宽度
        if(mWidth<minWidth){
            mWidth=minWidth;
        }
        mTextRectangleLength = (mWidth - mHeight) / mItemArray.length;
        initBGBorderPath();
        initPoint();
        invalidate();
    }

    public void setItemArray(@ArrayRes int itemArray){
        setItemArray( mContext.getResources().getStringArray(itemArray));
    }

    /**
     * 获取最长字符串
     */
    private String getMaxLengthStr() {
        String maxLengthValue = "";
        for (String newStr : mItemArray) {
            maxLengthValue = maxLengthValue.length() > newStr.length() ? maxLengthValue : newStr;
        }
        return maxLengthValue;
    }

    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset > 0) {
                    updateChecked(positionOffset);
                    invalidate();
                }

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //贝塞尔曲线 点 为垂直的
    public class VerticalPoint {
        public float x, y;
        public PointF top = new PointF();
        public PointF bottom = new PointF();

        public void setX(float x) {
            this.x = x;
            top.x = x;
            bottom.x = x;
        }


        public void setY(float y, float fraction) {
            this.y = y;
            top.y = y - ((1 - fraction)) * mRadiusChecked * C;
            bottom.y = y + (1 - fraction) * mRadiusChecked * C;
        }
    }

    //贝塞尔曲线 点 为水平的
    public class HorizontalPoint {
        public float x, y;
        public PointF left = new PointF();
        public PointF right = new PointF();

        public void setX(float x, float fraction) {
            this.x = x;
            left.x = x - (1 - fraction) * mRadiusChecked * C;
            right.x = x + (1 - fraction) * mRadiusChecked * C;
        }


        public void setY(float y) {
            this.y = y;
            left.y = y;
            right.y = y;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mViewPager = null;
    }


}
