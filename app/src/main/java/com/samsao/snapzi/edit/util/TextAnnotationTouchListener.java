package com.samsao.snapzi.edit.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * @author jfcartier
 * @since 15-03-24
 */
public class TextAnnotationTouchListener implements View.OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;
    private float mCurrentX;
    private float mCurrentY;
    private float mLastX;
    private float mLastY;
    private float mTouchSlop;
    private long mDeBounce;  // to avoid double clicks
    private int mActivePointerId; // The active pointer is the one currently moving our object.
    private View mView;
    private Callback mCallback;

    public TextAnnotationTouchListener(View view, Callback callback) {
        super();
        mView = view;
        mCallback = callback;
        mTouchSlop = ViewConfiguration.get(view.getContext()).getScaledTouchSlop();
        mDeBounce = 0;
        mActivePointerId = INVALID_POINTER_ID;
    }

    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mCallback.hideOverlays();
                // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Save the ID of this pointer
                mActivePointerId = event.getPointerId(0);
                final float x = event.getX(mActivePointerId);
                final float y = event.getY(mActivePointerId);
                // Remember where we started
                mLastX = x;
                mLastY = y;
                // to prevent an initial jump of the magnifier, aposX and mCurrentY must
                // have the values from the magnifier frame
                if (mCurrentX == 0) {
                    mCurrentX = mView.getX();
                }
                if (mCurrentY == 0) {
                    mCurrentY = mView.getY();
                }
                break;

            case MotionEvent.ACTION_UP:
                reset();
                // check for click event
                int intCurrentY = Math.round(event.getY(mActivePointerId));
                int intCurrentX = Math.round(event.getX(mActivePointerId));

                if (Math.abs(intCurrentX - Math.round(mLastX)) < 3 && Math.abs(intCurrentY - Math.round(mLastY)) < 3 && mDeBounce <= event.getDownTime()) {
                    mCallback.unlockText();
                    mDeBounce = event.getEventTime();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // Extract the index of the pointer that left the touch sensor
                final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // Find the index of the active pointer and fetch its position
                final int pointerIndexMove = event.findPointerIndex(mActivePointerId);
                float xMove = event.getX(pointerIndexMove);
                float yMove = event.getY(pointerIndexMove);

                // from http://android-developers.blogspot.com/2010/06/making-sense-of-multitouch.html
                // Calculate the distance moved
                final float dx = xMove - mLastX;
                final float dy = yMove - mLastY;

                if (Math.abs(dx) > mTouchSlop || Math.abs(dy) > mTouchSlop) {
                    // Move the frame
                    mCurrentX += dx;
                    mCurrentY += dy;

                    // Remember this touch position for the next move event
                    // no! see http://stackoverflow.com/questions/17530589/jumping-imageview-while-dragging-getx-and-gety-values-are-jumping?rq=1 and
                    // last comment in http://stackoverflow.com/questions/16676097/android-getx-gety-interleaves-relative-absolute-coordinates?rq=1
                    // mLastX = xMove;
                    // mLastY = yMove;

                    // in this area would be code for doing something with the magnified view as the frame moves.
                    mView.setX(mCurrentX);
                    mView.setY(mCurrentY);
                }
                break;

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }
        }

        return true;
    }

    private void reset() {
        mCallback.showOverlays();
        mCurrentX = 0;
        mCurrentY = 0;
        mLastX = 0;
        mLastY = 0;
    }

    public interface Callback {
        void hideOverlays();
        void showOverlays();
        void unlockText();
    }
}
