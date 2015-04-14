package com.samsao.snapzi.camera;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.samsao.snapzi.R;

/**
 * Created by vlegault on 15-04-13.
 */
public class ProgressButton extends View {

    /**
     * Constants
     */
    private final String LOG_TAG = getClass().getSimpleName();
    private final int DEFAULT_BUTTON_RADIUS = 48;
    private final int DEFAULT_CENTER_BUTTON_WEIGHT = 4;
    private final int DEFAULT_PROGRESS_RING_WEIGHT = 1;
    private final int DEFAULT_CENTER_BUTTON_COLOR = 0xCCFFFFFF;
    private final int DEFAULT_RING_COLOR = 0x88FFFFFF;
    private final int DEFAULT_PROGRESS_RING_COLOR = 0x8882CC55; // Green Samsao

    /**
     * Sizes
     */
    private int mButtonRadius = DEFAULT_BUTTON_RADIUS;
    private int mRealButtonDiameter = 0;
    private int mCenterButtonWeight = DEFAULT_CENTER_BUTTON_WEIGHT;
    private int mProgressRingWeight = DEFAULT_PROGRESS_RING_WEIGHT;

    /**
     * Colors
     */
    private int mCenterButtonColor = DEFAULT_CENTER_BUTTON_COLOR;
    private int mRingColor = DEFAULT_RING_COLOR;
    private int mProgressRingColor = DEFAULT_PROGRESS_RING_COLOR;

    /**
     * Drawing
     */
    private RectF mButtonBounds = new RectF();
    private Paint mCenterButtonPaint = new Paint();
    private Paint mRingPaint = new Paint();
    private Paint mProgressRingPaint = new Paint();
    private float mProgress = 0.0f;


    /**
     * The constructor for the ProgressButton
     *
     * @param context
     * @param attrs
     */
    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.ProgressButton));
    }

    /**
     * The constructor for the ProgressButton
     *
     * @param activity
     */
    public ProgressButton(Activity activity) {
        super(activity);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int viewWidth = 2 * mButtonRadius + this.getPaddingLeft() + this.getPaddingRight();
        int viewHeight = 2 * mButtonRadius + this.getPaddingTop() + this.getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(viewWidth, widthSize);
        } else {
            //Be whatever you want
            width = viewWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(viewHeight, heightSize);
        } else {
            //Be whatever you want
            height = viewHeight;
        }

        setMeasuredDimension(width, height);
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        // Get real button radius
        int minValue = Math.min(w - paddingLeft - paddingRight,
                h - paddingBottom - paddingTop);
        mRealButtonDiameter = Math.min(minValue, 2 * mButtonRadius);

        setupBounds(w, h);
        setupPaints();
        invalidate();
    }

    /**
     * Set the properties of the paints we're using to
     * draw the progress wheel
     */
    private void setupPaints() {
        float realButtonRadius = (float) mRealButtonDiameter / 2.0f;

        // Get center button radius and progress ring width
        float centerButtonRadius = realButtonRadius * ((float) mCenterButtonWeight / (float) (mCenterButtonWeight + mProgressRingWeight));
        float progressRingWidth = realButtonRadius - centerButtonRadius;

        mCenterButtonPaint.setColor(mCenterButtonColor);
        mCenterButtonPaint.setStyle(Paint.Style.FILL);
        mCenterButtonPaint.setAntiAlias(true);

        mRingPaint.setColor(mRingColor);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(progressRingWidth);
        mRingPaint.setAntiAlias(true);

        mProgressRingPaint.setColor(mProgressRingColor);
        mProgressRingPaint.setStyle(Paint.Style.STROKE);
        mProgressRingPaint.setStrokeWidth(progressRingWidth);
        mProgressRingPaint.setAntiAlias(true);
    }

    /**
     * Set the bounds of the component
     */
    private void setupBounds(int layoutWidth, int layoutHeight) {
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        float realButtonRadius = (float) mRealButtonDiameter / 2.0f;
        // Get center button radius and progress ring width
        float centerButtonRadius = realButtonRadius * ((float) mCenterButtonWeight / (float) (mCenterButtonWeight + mProgressRingWeight));
        float progressRingHalfWidth = (realButtonRadius - centerButtonRadius) / 2.0f;

        // Calc the Offset if needed for centering the wheel in the available space
        int xOffset = (layoutWidth - paddingLeft - paddingRight - mRealButtonDiameter) / 2 + paddingLeft;
        int yOffset = (layoutHeight - paddingTop - paddingBottom - mRealButtonDiameter) / 2 + paddingTop;

        mButtonBounds = new RectF(xOffset + progressRingHalfWidth,
                yOffset + progressRingHalfWidth,
                xOffset + mRealButtonDiameter - progressRingHalfWidth,
                yOffset + mRealButtonDiameter - progressRingHalfWidth);
    }

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param attributes the attributes to parse
     */
    private void parseAttributes(TypedArray attributes) {
        // We transform the default values from DIP to pixels
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mButtonRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mButtonRadius, metrics);

        // Get sizes
        mButtonRadius = (int) attributes.getDimension(R.styleable.ProgressButton_radius, mButtonRadius); // Get custom button radius if available
        mCenterButtonWeight = (int) attributes.getDimension(R.styleable.ProgressButton_center_button_weight, mCenterButtonWeight); // Get custom center button weight if available
        mProgressRingWeight = (int) attributes.getDimension(R.styleable.ProgressButton_progress_ring_weight, mProgressRingWeight); // Get custom progress ring weight if available

        // Get colors
        mCenterButtonColor = attributes.getColor(R.styleable.ProgressButton_center_button_color, mCenterButtonColor); // Get custom color if available
        mRingColor = attributes.getColor(R.styleable.ProgressButton_ring_color, mRingColor); // Get custom color if available
        mProgressRingColor = attributes.getColor(R.styleable.ProgressButton_progress_ring_color, mProgressRingColor); // Get custom color if available

        // Recycle
        attributes.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float progress = mProgress;

        // A View is usually in edit mode when displayed within a developer tool.
        if (isInEditMode()) {
            progress = 360.0f;
        }


        // Draw rings
        canvas.drawArc(mButtonBounds, progress - 90.0f, 360.0f, false, mRingPaint);
        canvas.drawArc(mButtonBounds, -90.0f, progress, false, mProgressRingPaint);

        // Get center button radius
        float realButtonRadius = (float) mRealButtonDiameter / 2.0f;
        float centerButtonRadius = realButtonRadius * ((float) mCenterButtonWeight / (float) (mCenterButtonWeight + mProgressRingWeight));

        // Draw center button
        canvas.drawCircle((float) (canvas.getWidth() >> 1), (float) (canvas.getHeight() >> 1), centerButtonRadius, mCenterButtonPaint);
    }

    /**
     * Reset the count (in increment mode)
     */
    public void reset() {
        mProgress = 0.0f;
        invalidate();
    }

    /**
     * @return the current progress between 0.0 and 1.0
     */
    public float getProgress() {
        return mProgress / 360.0f;
    }

    /**
     * Set the progress to a specific value,
     * the bar will be set instantly to that value
     *
     * @param progress the progress between 0 and 1
     */
    public void setProgress(float progress) {
        if (progress > 1.0f) {
            progress = 360.0f;
        } else if (progress < 0.0f) {
            progress = 0.0f;
        } else {
            progress *= 360.0f;
        }

        if (progress == mProgress) {
            return;
        }

        mProgress = progress;
        invalidate();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        ProgressButtonSavedState ss = new ProgressButtonSavedState(superState);

        // We save everything that can be changed at runtime
        ss.mButtonRadius = this.mButtonRadius;
        ss.mCenterButtonWeight = this.mCenterButtonWeight;
        ss.mProgressRingWeight = this.mProgressRingWeight;
        ss.mCenterButtonColor = this.mCenterButtonColor;
        ss.mRingColor = this.mRingColor;
        ss.mProgressRingColor = this.mProgressRingColor;
        ss.mProgress = this.mProgress;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof ProgressButtonSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        ProgressButtonSavedState ss = (ProgressButtonSavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.mButtonRadius = ss.mButtonRadius;
        this.mCenterButtonWeight = ss.mCenterButtonWeight;
        this.mProgressRingWeight = ss.mProgressRingWeight;
        this.mCenterButtonColor = ss.mCenterButtonColor;
        this.mRingColor = ss.mRingColor;
        this.mProgressRingColor = ss.mProgressRingColor;
        this.mProgress = ss.mProgress;
    }

    /**
     * Sets the center button color
     *
     * @param centerButtonColor the color for the center button
     */
    public void setCenterButtonColor(int centerButtonColor) {
        mCenterButtonColor = centerButtonColor;
        setupPaints();
        invalidate();
    }

    /**
     * Sets the ring color
     *
     * @param ringColor the color for ring
     */
    public void setRingColor(int ringColor) {
        mRingColor = ringColor;
        setupPaints();
        invalidate();
    }

    /**
     * Sets the center button color
     *
     * @param progressRingColor the color for the progress ring
     */
    public void setProgressRingColor(int progressRingColor) {
        mProgressRingColor = progressRingColor;
        setupPaints();
        invalidate();
    }

    static class ProgressButtonSavedState extends BaseSavedState {
        int mButtonRadius;
        int mCenterButtonWeight;
        int mProgressRingWeight;
        int mCenterButtonColor;
        int mRingColor;
        int mProgressRingColor;
        float mProgress;
        float mTargetProgress;

        ProgressButtonSavedState(Parcelable superState) {
            super(superState);
        }

        private ProgressButtonSavedState(Parcel in) {
            super(in);
            this.mButtonRadius = in.readInt();
            this.mCenterButtonWeight = in.readInt();
            this.mProgressRingWeight = in.readInt();
            this.mCenterButtonColor = in.readInt();
            this.mRingColor = in.readInt();
            this.mProgressRingColor = in.readInt();
            this.mProgress = in.readFloat();
            this.mTargetProgress = in.readFloat();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(this.mButtonRadius);
            out.writeInt(this.mCenterButtonWeight);
            out.writeInt(this.mProgressRingWeight);
            out.writeInt(this.mCenterButtonColor);
            out.writeInt(this.mRingColor);
            out.writeInt(this.mProgressRingColor);
            out.writeFloat(this.mProgress);
            out.writeFloat(this.mTargetProgress);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<ProgressButtonSavedState> CREATOR =
                new Parcelable.Creator<ProgressButtonSavedState>() {
                    public ProgressButtonSavedState createFromParcel(Parcel in) {
                        return new ProgressButtonSavedState(in);
                    }

                    public ProgressButtonSavedState[] newArray(int size) {
                        return new ProgressButtonSavedState[size];
                    }
                };
    }
}
