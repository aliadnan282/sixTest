package com.workout.sixpacksabs.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.workout.sixpacksabs.helper.AppConstant;

public class TextProgressBar extends ProgressBar {
    private static final int STROKE_WIDTH = 30;
    private String text;
    private Paint textPaint;

    public TextProgressBar(Context context) {
        super(context);
        initPaintObject();
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaintObject();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaintObject();
    }

    private void initPaintObject() {
        text = "";
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(STROKE_WIDTH);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setTextSize(80);
        textPaint.setAntiAlias(true);
        //Typeface tf =Typeface.createFromAsset(AppConstant.mContext.getAssets(),"fonts/MyriadPro_Bold.otf");
        textPaint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        // First draw the regular progress bar, then custom draw our text
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, textPaint);
    }

    public synchronized void setText(String text) {
        this.text = text;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }
}