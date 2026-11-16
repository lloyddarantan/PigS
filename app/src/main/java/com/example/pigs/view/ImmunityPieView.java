package com.example.pigs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ImmunityPieView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ImmunityPieView(Context context) {
        super(context);
    }

    public ImmunityPieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float size = Math.min(getWidth(), getHeight()) - 12;
        float left = (getWidth() - size) / 2;
        float top = (getHeight() - size) / 2;

        RectF rect = new RectF(
                left, top, left + size, top + size
        );

        paint.setColor(0xFFF4A0A3);
        canvas.drawArc(rect, 0, 360, true, paint);

        // Unprotected 4%
        paint.setColor(0xFFD95E68);
        canvas.drawArc(rect, 270, 14.4f, true, paint);

        paint.setColor(0xFFFFB6B8);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawCircle(
                getWidth() / 2f,
                getHeight() / 2f,
                size / 2,
                paint
        );

        paint.setStyle(Paint.Style.FILL);
    }
}
