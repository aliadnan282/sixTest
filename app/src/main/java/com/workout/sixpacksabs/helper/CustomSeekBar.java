package com.workout.sixpacksabs.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.SeekBar;


import com.workout.sixpacksabs.model.ProgressItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

@SuppressLint("AppCompatCustomView")
public class CustomSeekBar extends SeekBar {

    float mBmiValue;
    int lineWidth = 10;
    private ArrayList<ProgressItem> mProgressItemsList = new ArrayList<>();
    private int mThumbSize;
    private TextPaint mTextPaint;
    private TextPaint mProgressPaint;
    private TextPaint mLinePaint;

    public CustomSeekBar(Context context) {
        super(context);
        mProgressItemsList = new ArrayList<ProgressItem>();
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

   /* public static Bitmap getBitmapFromDrawable(Context context, @DrawableRes int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawableCompat || drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }*/

    public void initData(ArrayList<ProgressItem> progressItemsList, float bmi) {
        this.mProgressItemsList.clear();
        this.mProgressItemsList = progressItemsList;
        this.mBmiValue = bmi;
        //   mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(pxFromDp(getContext(), 14));//getResources().getDimensionPixelSize(R.dimen.thumb_text_size));
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);

        mProgressPaint = new TextPaint();
        mProgressPaint.setColor(Color.BLACK);
        mProgressPaint.setTextSize(pxFromDp(getContext(), 11));//getResources().getDimensionPixelSize(R.dimen.thumb_text_size));
        mProgressPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mProgressPaint.setTextAlign(Paint.Align.CENTER);
        mProgressPaint.setAntiAlias(true);

        mLinePaint = new TextPaint();
        mLinePaint.setColor(Color.BLACK);
        mLinePaint.setStrokeWidth(pxFromDp(getContext(), 2.5f));//getResources().getDimensionPixelSize(R.dimen.thumb_text_size));
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setAntiAlias(true);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        if (mProgressItemsList.size() > 0) {
            int progressBarWidth = getWidth();
            int progressBarHeight = getHeight();

            int thumboffset = getThumbOffset();
            int lastProgressX = 0;
            double progressSum = 10;
            int progressItemWidth, progressItemRight;
            canvas.drawText(new DecimalFormat("##.#").format(progressSum).toString(), lastProgressX + pxFromDp(getContext(), 5), getHeight() - pxFromDp(getContext(), 2), mProgressPaint);

            for (int i = 0; i < mProgressItemsList.size(); i++) {
                ProgressItem progressItem = mProgressItemsList.get(i);

                Paint progressPaint = new Paint();
                progressPaint.setColor(progressItem.getColor());
                progressItemWidth = (int) (progressItem.getProgressItemPercentage() * progressBarWidth / 40);

                progressItemRight = lastProgressX + progressItemWidth;

                // for last item give right to progress item to the width
                if (i == mProgressItemsList.size() - 1 && progressItemRight != progressBarWidth) {
                    progressItemRight = progressBarWidth - (int) pxFromDp(getContext(), 4);
                }
                Rect progressRect = new Rect();
                progressRect.set(lastProgressX, getHeight() / 2, progressItemRight, progressBarHeight - thumboffset / 2 - (int) pxFromDp(getContext(), 4));
                canvas.drawRect(progressRect, progressPaint);

                // tempCurrent+=progressItem.getProgressItemPercentage();
                progressSum += progressItem.getProgressItemPercentage();
                lastProgressX = progressItemRight;
                if (i == mProgressItemsList.size() - 1)
                    canvas.drawText(new DecimalFormat("##.#").format(Math.ceil(progressSum - 10)).toString(), lastProgressX - pxFromDp(getContext(), 7), getHeight(), mProgressPaint);
                else {
                    if (i >= 2)
                        canvas.drawText(new DecimalFormat("##.#").format(Math.ceil(progressSum)).toString(), lastProgressX, getHeight() + pxFromDp(getContext(), 0), mProgressPaint);
                    else
                        canvas.drawText(new DecimalFormat("##.#").format(progressSum).toString(), lastProgressX, getHeight() + pxFromDp(getContext(), 0), mProgressPaint);

                    //  progressSum += progressItem.getProgressItemPercentage();
                }
            }

            String progressText = getBmiTitle();//"Under weight";//String.valueOf(getProgress()/1000);
            Rect bounds = new Rect();
            mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);

            int leftPadding = getPaddingLeft() - getThumbOffset();
            int rightPadding = getPaddingRight() - getThumbOffset();
            int width = getWidth() - leftPadding - rightPadding;
            float progressRatio = (float) getProgress() / getMax();
            //float thumbOffset = mThumbSize * (.5f - progressRatio);
            float thumbX = progressRatio * width + leftPadding + (int) pxFromDp(getContext(), 2);//+ thumbOffset;
            float thumbY = getHeight() / 2f - bounds.height();

            if (mBmiValue <= 10) {
                setProgress((int) (2));
                canvas.drawLine(thumbX + (int) pxFromDp(getContext(), 8), thumbY + pxFromDp(getContext(), 8), thumbX + (int) pxFromDp(getContext(), 8), getHeight() - bounds.height() / 2 - pxFromDp(getContext(), 8), mLinePaint);
                canvas.drawText(progressText, thumbX + bounds.width() / 2, thumbY - pxFromDp(getContext(), 2), mTextPaint);
                return;
            }
           else if (mBmiValue <= 18) {
                setProgress((int) (mBmiValue - 10)<2?2:(int)(mBmiValue - 10));

                canvas.drawLine(thumbX, thumbY + pxFromDp(getContext(), 8), thumbX, getHeight() - bounds.height() / 2 - pxFromDp(getContext(), 8), mLinePaint);
                canvas.drawText(progressText, thumbX + bounds.width() / 2, thumbY - pxFromDp(getContext(), 2), mTextPaint);

            } else if (mBmiValue > 50) {
                setProgress(38);

                canvas.drawLine(thumbX - (int) pxFromDp(getContext(), 8), thumbY + pxFromDp(getContext(), 8), thumbX - (int) pxFromDp(getContext(), 8), getHeight() - bounds.height() / 2 - pxFromDp(getContext(), 8), mLinePaint);
                canvas.drawText(progressText, thumbX - bounds.width() / 2, thumbY - (int) pxFromDp(getContext(), 8) - pxFromDp(getContext(), 2), mTextPaint);

            } else {
                setProgress((int) (mBmiValue - 10));

                canvas.drawLine(thumbX, thumbY + pxFromDp(getContext(), 8), thumbX, getHeight() - bounds.height() / 2 - pxFromDp(getContext(), 8), mLinePaint);
                canvas.drawText(progressText, thumbX - bounds.width() / 10, thumbY - pxFromDp(getContext(), 2), mTextPaint);
            }

            super.onDraw(canvas);
        }

    }

    private String getBmiTitle() {

        if (mBmiValue <= 15) {
            return mProgressItemsList.get(0).getTitle();
        } else if (mBmiValue <= 18.5) {
            return mProgressItemsList.get(1).getTitle();
        } else if (mBmiValue <= 25) {
            return mProgressItemsList.get(2).getTitle();
        } else if (mBmiValue <= 30) {
            return mProgressItemsList.get(3).getTitle();
        } else if (mBmiValue <= 35) {
            return mProgressItemsList.get(4).getTitle();
        } else {
            return mProgressItemsList.get(5).getTitle();
        }

    }
}
