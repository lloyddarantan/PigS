
package com.example.pigs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ImmunityPieView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int protectedPercentage = 0;

    public ImmunityPieView(Context context) {
        super(context);
    }

    public ImmunityPieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setProtectedPercentage(int percentage) {
        protectedPercentage = Math.max(0, Math.min(100, percentage));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float size = Math.min(getWidth(), getHeight()) - 12;

        if (size <= 0) return;

        float left = (getWidth() - size) / 2f;
        float top = (getHeight() - size) / 2f;

        RectF rect = new RectF(
                left, top, left + size, top + size
        );

        float protectedSweep = protectedPercentage * 3.6f;
        float unprotectedSweep = 360f - protectedSweep;

        // Protected portion
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFF4A0A3);
        canvas.drawArc(rect, -90, protectedSweep, true, paint);

        // Unprotected portion
        paint.setColor(0xFFD95E68);
        canvas.drawArc(
                rect,
                -90 + protectedSweep,
                unprotectedSweep,
                true,
                paint
        );

        // Outer border
        paint.setColor(0xFFFFB6B8);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        canvas.drawCircle(
                getWidth() / 2f,
                getHeight() / 2f,
                size / 2f,
                paint
        );

        paint.setStyle(Paint.Style.FILL);
    }
}
