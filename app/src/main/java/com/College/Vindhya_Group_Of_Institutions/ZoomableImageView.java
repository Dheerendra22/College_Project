package com.College.Vindhya_Group_Of_Institutions;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

public class ZoomableImageView extends AppCompatImageView {

    private static final float MIN_SCALE_FACTOR = 0.1f;
    private static final float MAX_SCALE_FACTOR = 5.0f;

    private Matrix matrix = new Matrix();
    private ScaleGestureDetector scaleGestureDetector;

    private PointF lastFocus = new PointF();
    private PointF startTouch = new PointF();

    private boolean isPanning = false;

    private float scaleFactor = 1.0f;

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        setScaleType(ScaleType.MATRIX);  // Set ImageView scale type to MATRIX
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (!scaleGestureDetector.isInProgress()) {
                    startTouch.set(event.getX(), event.getY());
                    isPanning = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isPanning && event.getPointerCount() == 1) {
                    float deltaX = event.getX() - startTouch.x;
                    float deltaY = event.getY() - startTouch.y;

                    matrix.postTranslate(deltaX, deltaY);
                    setImageMatrix(matrix);

                    startTouch.set(event.getX(), event.getY());
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isPanning) {
                    // Only perform a click if not panning
                    performClick();
                }
            case MotionEvent.ACTION_POINTER_UP:
                isPanning = false;
                break;
        }

        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min(scaleFactor, MAX_SCALE_FACTOR));

            lastFocus.set(detector.getFocusX(), detector.getFocusY());

            matrix.setScale(scaleFactor, scaleFactor, lastFocus.x, lastFocus.y);
            setImageMatrix(matrix);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            lastFocus.set(detector.getFocusX(), detector.getFocusY());
            return true;
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
        }
    }

    @Override
    public boolean performClick() {
        // Handle click logic here
        return super.performClick();
    }
}
