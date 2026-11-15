package com.example.pigs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetectionOverlayView extends View {

    public static class BoundingBox {
        public final RectF rect;
        public final String label;
        public final float confidence;

        public BoundingBox(RectF rect, String label, float confidence) {
            this.rect = rect;
            this.label = label;
            this.confidence = confidence;
        }
    }

    private final List<BoundingBox> boundingBoxes = new ArrayList<>();
    private int sourceWidth = 1;
    private int sourceHeight = 1;

    private final Paint boxPaint = new Paint();
    private final Paint textBackgroundPaint = new Paint();
    private final Paint textPaint = new Paint();

    public DetectionOverlayView(Context context) {
        super(context);
        init();
    }

    public DetectionOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetectionOverlayView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Main bounding box border paint
        boxPaint.setColor(Color.parseColor("#E85580")); // Theme primary pink
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(8f);
        boxPaint.setAntiAlias(true);

        // Background box for label text
        textBackgroundPaint.setColor(Color.parseColor("#E85580"));
        textBackgroundPaint.setStyle(Paint.Style.FILL);
        textBackgroundPaint.setAntiAlias(true);

        // Label text paint
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(36f);
        textPaint.setFakeBoldText(true);
        textPaint.setAntiAlias(true);
    }

    /**
     * Update the overlay with new detection bounding boxes and source image dimensions.
     */
    public void setResults(List<BoundingBox> boxes, int sourceWidth, int sourceHeight) {
        this.boundingBoxes.clear();
        if (boxes != null) {
            this.boundingBoxes.addAll(boxes);
        }
        this.sourceWidth = sourceWidth > 0 ? sourceWidth : 1;
        this.sourceHeight = sourceHeight > 0 ? sourceHeight : 1;
        invalidate();
    }

    /**
     * Clear all drawn bounding boxes.
     */
    public void clear() {
        this.boundingBoxes.clear();
        invalidate();
    }

    private final RectF scaledRect = new RectF();
    private final RectF textBgRect = new RectF();

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (boundingBoxes.isEmpty()) {
            return;
        }

        float viewWidth = getWidth();
        float viewHeight = getHeight();

        float scaleX = viewWidth / (float) sourceWidth;
        float scaleY = viewHeight / (float) sourceHeight;

        for (BoundingBox box : boundingBoxes) {
            // Scale rectangle coordinates from source image space to current view space
            float l = Math.max(0, box.rect.left * scaleX);
            float t = Math.max(0, box.rect.top * scaleY);
            float r = Math.min(viewWidth, box.rect.right * scaleX);
            float b = Math.min(viewHeight, box.rect.bottom * scaleY);

            scaledRect.set(l, t, r, b);

            // Draw bounding box rectangle
            canvas.drawRoundRect(scaledRect, 12f, 12f, boxPaint);

            // Prepare label string (e.g. "pig 92%")
            String labelText;
            if (box.label != null && !box.label.isEmpty()) {
                labelText = String.format(Locale.US, "%s %.0f%%", box.label, box.confidence * 100);
            } else {
                labelText = String.format(Locale.US, "pig %.0f%%", box.confidence * 100);
            }

            float textWidth = textPaint.measureText(labelText);
            float textHeight = textPaint.getTextSize();

            // Background rectangle for text tag above the box
            float bgTop = Math.max(0, t - textHeight - 16f);
            float bgBottom = Math.max(textHeight + 16f, t);
            textBgRect.set(l, bgTop, l + textWidth + 24f, bgBottom);

            canvas.drawRoundRect(textBgRect, 8f, 8f, textBackgroundPaint);

            // Draw text inside the tag
            canvas.drawText(labelText, l + 12f, bgBottom - 10f, textPaint);
        }
    }
}
