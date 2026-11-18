
package com.example.pigs;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainSupportActivity extends AppCompatActivity {

    private View headerRow, titleRow, cardTech, cardVet;
    private TextView tvHelpLabel, tvTitle, tvBack;
    private ImageView ivGear, ivPig, ivVetPig, ivArrowTech, ivArrowVet, ivLogo;
    private LinearLayout chipsTech, chipsVet;

    private final List<ObjectAnimator> loopAnimators = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_support);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();
        styleTitle();
        setupClicks();
        playEntranceAnimations();
        startLoopingAnimations();
    }

    private void bindViews() {

        headerRow = findViewById(R.id.headerRow);
        titleRow = findViewById(R.id.titleRow);
        cardTech = findViewById(R.id.cardTech);
        cardVet = findViewById(R.id.cardVet);
        tvHelpLabel = findViewById(R.id.tvHelpLabel);
        tvTitle = findViewById(R.id.tvTitle);
        tvBack = findViewById(R.id.tvBack);

        ivGear = findViewById(R.id.ivGear);
        ivPig = findViewById(R.id.ivPig);
        ivVetPig = findViewById(R.id.ivVetPig);
        ivArrowTech = findViewById(R.id.ivArrowTech);
        ivArrowVet = findViewById(R.id.ivArrowVet);
        ivLogo = findViewById(R.id.ivLogo);

        chipsTech = findViewById(R.id.chipsTech);
        chipsVet = findViewById(R.id.chipsVet);
    }

    private void styleTitle() {

        if (tvTitle == null) {
            return;
        }

        String text = "What kind of\nsupport do\nyou need?";

        SpannableString span = new SpannableString(text);

        span.setSpan(
                new ForegroundColorSpan(Color.parseColor("#111111")),
                0,
                text.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        int start = text.indexOf("support");

        span.setSpan(
                new ForegroundColorSpan(Color.parseColor("#F08CA0")),
                start,
                start + "support".length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvTitle.setTextColor(Color.parseColor("#111111"));
        tvTitle.setText(span);
    }

    private void setupClicks() {

        if (cardTech != null) {

            addPressEffect(cardTech);

            cardTech.setOnClickListener(v -> {
                Intent intent = new Intent(
                        MainSupportActivity.this,
                        TechnicalSupportActivity.class
                );

                startActivity(intent);

                overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                );
            });
        }

        if (cardVet != null) {

            addPressEffect(cardVet);

            cardVet.setOnClickListener(v ->
                    Toast.makeText(
                            this,
                            "Veterinarian page coming soon",
                            Toast.LENGTH_SHORT
                    ).show()
            );
        }

        if (tvBack != null) {

            addPressEffect(tvBack);

            tvBack.setOnClickListener(v -> {

                Intent intent = new Intent(
                        MainSupportActivity.this,
                        MainActivity.class
                );

                intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                );

                startActivity(intent);

                overridePendingTransition(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                );

                finish();
            });
        }
    }

    private void playEntranceAnimations() {

        slideUp(headerRow, 0);
        slideUp(tvHelpLabel, 100);
        slideUp(titleRow, 200);
        slideUp(cardTech, 350);
        slideUp(cardVet, 500);
        slideUp(tvBack, 650);

        if (ivLogo != null) {
            ObjectAnimator.ofFloat(
                    ivLogo,
                    "rotation",
                    0f, -30f, 30f, -15f, 15f, 0f
            ).setDuration(900).start();
        }

        popInChips(chipsTech, 700);
        popInChips(chipsVet, 850);
    }

    private void slideUp(View v, long delay) {

        if (v == null) {
            return;
        }

        v.setAlpha(0f);
        v.setTranslationY(80f);

        v.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(500)
                .setInterpolator(new OvershootInterpolator(0.8f))
                .start();
    }

    private void popInChips(LinearLayout row, long baseDelay) {

        if (row == null) {
            return;
        }

        for (int i = 0; i < row.getChildCount(); i++) {

            View chip = row.getChildAt(i);

            chip.setScaleX(0f);
            chip.setScaleY(0f);

            chip.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(baseDelay + i * 90L)
                    .setDuration(350)
                    .setInterpolator(new OvershootInterpolator(2f))
                    .start();
        }
    }

    private void startLoopingAnimations() {

        if (ivGear != null) {

            ObjectAnimator gearSpin = ObjectAnimator.ofFloat(
                    ivGear, "rotation", 0f, 360f
            );

            gearSpin.setDuration(8000);
            gearSpin.setInterpolator(new LinearInterpolator());
            gearSpin.setRepeatCount(ValueAnimator.INFINITE);
            gearSpin.start();

            loopAnimators.add(gearSpin);
        }

        if (ivPig != null) {

            ObjectAnimator pigFloat = ObjectAnimator.ofFloat(
                    ivPig, "translationY", 0f, -16f
            );

            pigFloat.setDuration(1200);
            pigFloat.setInterpolator(new AccelerateDecelerateInterpolator());
            pigFloat.setRepeatCount(ValueAnimator.INFINITE);
            pigFloat.setRepeatMode(ValueAnimator.REVERSE);
            pigFloat.start();

            loopAnimators.add(pigFloat);

            ObjectAnimator pigTilt = ObjectAnimator.ofFloat(
                    ivPig, "rotation", -3f, 3f
            );

            pigTilt.setDuration(1800);
            pigTilt.setInterpolator(new AccelerateDecelerateInterpolator());
            pigTilt.setRepeatCount(ValueAnimator.INFINITE);
            pigTilt.setRepeatMode(ValueAnimator.REVERSE);
            pigTilt.start();

            loopAnimators.add(pigTilt);
        }

        if (ivVetPig != null) {

            ObjectAnimator vetWobble = ObjectAnimator.ofFloat(
                    ivVetPig, "rotation", -6f, 6f
            );

            vetWobble.setDuration(1000);
            vetWobble.setInterpolator(new AccelerateDecelerateInterpolator());
            vetWobble.setRepeatCount(ValueAnimator.INFINITE);
            vetWobble.setRepeatMode(ValueAnimator.REVERSE);
            vetWobble.start();

            loopAnimators.add(vetWobble);
        }

        if (ivArrowTech != null) {
            loopAnimators.add(nudge(ivArrowTech));
        }

        if (ivArrowVet != null) {
            loopAnimators.add(nudge(ivArrowVet));
        }
    }

    private ObjectAnimator nudge(View v) {

        ObjectAnimator a = ObjectAnimator.ofFloat(
                v, "translationX", 0f, 6f
        );

        a.setDuration(700);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.start();

        return a;
    }

    private void addPressEffect(View v) {

        v.setOnTouchListener((view, event) -> {

            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    view.animate()
                            .scaleX(0.96f)
                            .scaleY(0.96f)
                            .setDuration(100)
                            .start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    view.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .setInterpolator(new OvershootInterpolator(3f))
                            .start();
                    break;
            }

            return false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        for (ObjectAnimator a : loopAnimators) {
            if (a.isStarted()) {
                a.pause();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        for (ObjectAnimator a : loopAnimators) {
            if (a.isPaused()) {
                a.resume();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (ObjectAnimator a : loopAnimators) {
            a.cancel();
        }

        loopAnimators.clear();
    }
}
