package com.example.pigs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import com.example.pigs.model.VaccinationLog;
import java.util.ArrayList;
import java.util.List;

public class VaccinationChartView extends View {

    private Paint paint;
    private List<Float> values = new ArrayList<>();

    private String[] dates = {
            "06-10", "06-09", "06-07"
    };

    public VaccinationChartView(Context context) {
        super(context);
        init();
    }

    public VaccinationChartView(Context context,
                                @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        values.add(1f);
        values.add(2f);
        values.add(1f);
    }
    public void setLogs(List<VaccinationLog> logs) {

        values.clear();

        dates = new String[logs.size()];

        for (int i = 0; i < logs.size(); i++) {

            values.add(logs.get(i).getDose());

            dates[i] = logs.get(i).getDate().substring(0, 5);
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float left = 65;
        float right = width - 30;
        float top = 35;
        float bottom = height - 55;

        float chartHeight = bottom - top;

        // Chart title
        paint.setColor(0xFF222222);
        paint.setTextSize(32);
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("Dose administered (g)", left, 28, paint);

        // Horizontal grid lines
        paint.setColor(0xFFE0E0E0);
        paint.setStrokeWidth(2);

        for (int i = 0; i <= 2; i++) {

            float y = bottom - (chartHeight * i / 2f);

            canvas.drawLine(left, y, right, y, paint);

            paint.setTextSize(24);
            paint.setColor(0xFF777777);

            canvas.drawText(
                    String.valueOf(i),
                    20, y + 8, paint
            );

            paint.setColor(0xFFE0E0E0);
        }

        // Bars
        float chartWidth = right - left;
        float slotWidth = chartWidth / values.size();
        float barWidth = slotWidth * 0.38f;

        for (int i = 0; i < values.size(); i++) {

            float value = values.get(i);

            float centerX = left + slotWidth * (i + 0.5f);

            float barHeight = (value / 2f) * chartHeight;

            float barTop = bottom - barHeight;

            paint.setColor(0xFFFF6268);

            canvas.drawRoundRect(
                    centerX - barWidth / 2,
                    barTop,
                    centerX + barWidth / 2,
                    bottom,
                    12, 12, paint
            );

            // Value above bar
            paint.setColor(0xFF222222);
            paint.setTextSize(26);
            paint.setTypeface(Typeface.DEFAULT_BOLD);

            String valueText = value + "g";

            canvas.drawText(
                    valueText,
                    centerX - 20,
                    barTop - 12,
                    paint
            );

            // Date below bar
            paint.setTextSize(23);
            paint.setTypeface(Typeface.DEFAULT);
            paint.setColor(0xFF555555);

            canvas.drawText(
                    dates[i],
                    centerX - 32,
                    bottom + 35,
                    paint
            );
        }

    }
}
