package com.gac.checktextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Administrator on 2015/12/29.
 */
public class CheckTextView extends TextView {
    private String mText;
    private int mColor;
    private int mSize;
    private Rect mBound;//绘制文本的范围
    private Paint mPaint;

    private List<PointX> points;//产生直线点的集合
    public CheckTextView(Context context) {
       this(context,null);
    }

    public CheckTextView(Context context, AttributeSet attrs) {
       this(context,attrs,0);
    }

    public CheckTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,R.styleable.CheckTextView,defStyleAttr,0);
        int n = ta.getIndexCount();
        for(int i = 0; i < n ;i++){
            int attr = ta.getIndex(i);
            switch (attr){
                case R.styleable.CheckTextView_checkText:
                    mText = ta.getString(attr);
                    break;
                case R.styleable.CheckTextView_checkTextColor:
                    mColor = ta.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CheckTextView_checkTextSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mSize = ta.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics()));

                    break;
            }
        }
        ta.recycle();
        //获得绘制文本的宽高
        mPaint = new Paint();
        mPaint.setTextSize(mSize);
        mBound = new Rect();
        Log.d("gac","textsize:"+mSize);
        //获得文本的宽高
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);
        createLinePoints();
        Log.d("gac", "mBound:" + mBound.width() + "," + mBound.height());
        //初始化设置点击事件
        setTextClickListener();
    }


    //初始化时候 调用一次 每次点击textview时候调用 产生新的点的集合
    private void createLinePoints(){
        points = new ArrayList<PointX>();

        for(int i = 0; i < 6; i++){
             Random rand = new Random();
            int startX = rand.nextInt( mBound.width()-1)+1;
            int startY = rand.nextInt(mBound.height()-1)+1;
            int stopX =  rand.nextInt( mBound.width() - 1)+1;
            int stopY = rand.nextInt(mBound.height()-1)+1;
            points.add(new PointX(startX,startY,stopX,stopY));
        }

    }
    private void setTextClickListener(){
        this.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                randomText();
                createLinePoints();
                postInvalidate();

            }
        });
    }

    private void  randomText(){
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4)
        {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set)
        {
            sb.append("" + i);
        }

         mText = sb.toString();
    }
    public String getCheckText(){
        return mText;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("gac", "widthSize:" + widthSize);
        Log.d("gac","heightSize:"+heightSize);
        int width;
        int height;
        if(widthMode == MeasureSpec.EXACTLY){
            width = widthSize;
        }else{
            mPaint.setTextSize(mSize);
            mPaint.getTextBounds(mText,0,mText.length(),mBound);
            float textWidth = mBound.width();
            Log.d("gac","mBound1:"+mBound.width()+","+mBound.height());
            int desired = (int)(getPaddingLeft() + textWidth+getPaddingRight());
            width = desired;
        }

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else{
            mPaint.setTextSize(mSize);
            mPaint.getTextBounds(mText, 0, mText.length(), mBound);
            float textHeight = mBound.height();

            int desired = (int)(getPaddingTop()+textHeight+getPaddingBottom());
            height = desired;
        }
        Log.d("gac", "width:" + width + ", height:" + height);
        //设置textview的宽高
        setMeasuredDimension(width, height);
    }

    private void randomLines(Canvas canvas){

            for(int i = 0; i < 6; i++){
                randomLine(canvas,points.get(i));
            }


    }
    private void randomLine(Canvas canvas,PointX p){

            mPaint.setColor(Color.RED);
            float old = mPaint.getStrokeWidth();
            //设置线条宽度
            mPaint.setStrokeWidth(4.0f);

          //  Log.d("gac", startX + " " + " " + startY + " " + stopX + " " + stopY);
            canvas.drawLine(p.startX, p.startY, p.stopX, p.stopY, mPaint);
            // canvas.drawLine(10, 10, 100, 100,mPaint);
            //设置回原来线条宽度
            mPaint.setStrokeWidth(old);


    }
    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        Log.d("gac", "getMeasuredWidth:" + getMeasuredWidth() + " getMeasuredHeight:" + getMeasuredHeight());
        randomLines(canvas);
        Log.d("gac", "getWidth:" + getWidth() + " getHeight:" + getHeight());
        // mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(mColor);
        //292 147 屏幕宽高   30 117 textsize 120
        //232 87 字体宽高
        // x y 参数 字体的最左上角x坐标 左下角y坐标
        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, (getHeight() / 2 + mBound.height() / 2), mPaint);
    }


    //产生噪点线条的坐标
    private class PointX{
        private int startX;
        private int startY;
        private int stopX;
        private int stopY;
        public PointX(int startX, int startY, int stopX,int stopY){
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }

    }
}
