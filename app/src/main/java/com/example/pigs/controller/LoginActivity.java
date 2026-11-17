
package com.example.pigs.controller;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.media.AudioAttributes;
import android.media.SoundPool;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.widget.Toast;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pigs.network.SessionManager;



import com.example.pigs.network.ApiClient;
import com.example.pigs.MainActivity;
import com.example.pigs.R;
import com.example.pigs.model.User;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnContinue;
    private TextView tvForgotPassword, tvCreateAccount;
    private int loggedInUserId;
    private String loggedInFirstName;
    private String loggedInLastName;
    private String loggedInRole;

    private Dialog animationDialog;

    private final Handler animationHandler =
            new Handler(Looper.getMainLooper());

    private SoundPool soundPool;
    private int oinkSound;
    private boolean soundLoaded = false;

    private boolean isAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ApiClient.initialize(getApplicationContext());

        initializeViews();
        initializeSound();

        btnContinue.setOnClickListener(v -> loginUser());

        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(
                    LoginActivity.this,
                    RegisterActivity.class
            ));
        });

        tvForgotPassword.setOnClickListener(v -> {
            Toast.makeText(
                    LoginActivity.this,
                    "Forgot password feature coming soon!",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void initializeViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnContinue = findViewById(R.id.btnContinue);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
    }

    // Initialize pig sound
    private void initializeSound() {

        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setUsage(
                                AudioAttributes.USAGE_ASSISTANCE_SONIFICATION
                        )
                        .setContentType(
                                AudioAttributes.CONTENT_TYPE_SONIFICATION
                        )
                        .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener(
                (pool, sampleId, status) -> {
                    if (status == 0) {
                        soundLoaded = true;
                    }
                }
        );

        oinkSound = soundPool.load(this, R.raw.oink, 1);
    }

    private void playOink() {
        if (soundPool != null && soundLoaded) {
            soundPool.play(
                    oinkSound,
                    1f,
                    1f,
                    1,
                    0,
                    1f
            );
        }
    }

    // Login validation

    private void loginUser() {

        String emailInput = etEmail.getText() != null
                ? etEmail.getText().toString().trim() : "";

        String passwordInput = etPassword.getText() != null
                ? etPassword.getText().toString() : "";

        // Validate email
        if (emailInput.isEmpty()) {
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            etEmail.setError("Enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        // Validate password
        if (passwordInput.isEmpty()) {
            etPassword.setError("Please enter your password");
            etPassword.requestFocus();
            return;
        }

        if (passwordInput.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (isAnimating) return;

        btnContinue.setEnabled(false);
        btnContinue.setText("PLEASE WAIT...");

        // Authenticate using MySQL through PHP
        ApiClient.login(
                emailInput,
                passwordInput,
                new ApiClient.ApiCallback() {


                    @Override
                    public void onSuccess(org.json.JSONObject response) {

                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                        // Get user information from PHP
                        org.json.JSONObject user =
                                response.optJSONObject("user");

                        // Check if user information exists
                        if (user == null) {

                            btnContinue.setEnabled(true);
                            btnContinue.setText("CONTINUE");

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Unable to retrieve account information.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        // Get authentication token from PHP

                        String token = response.optString("token", "");

                        if (token.isEmpty()) {

                            btnContinue.setEnabled(true);
                            btnContinue.setText("CONTINUE");

                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login token missing. Check login.php.",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        // Save login token
                        SessionManager.save(
                                LoginActivity.this,
                                token
                        );

                        // Store user details for dashboard
                        loggedInUserId = user.optInt("id");
                        loggedInFirstName = user.optString("first_name");
                        loggedInLastName = user.optString("last_name");
                        loggedInRole = user.optString("role");

                        String userEmail = user.optString("email");

                        // Start existing PigSensor login animation
                        isAnimating = true;
                        btnContinue.setText("WELCOME!");

                        showLoginAnimation(userEmail);
                    }


                    @Override
                    public void onError(String message) {

                        if (isFinishing() || isDestroyed()) return;

                        btnContinue.setEnabled(true);
                        btnContinue.setText("CONTINUE");

                        Toast.makeText(
                                LoginActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // Create animation screen
    private void showLoginAnimation(String email) {

        animationDialog = new Dialog(this);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.WHITE);

        // Prevent the logos from being clipped by their parent
        root.setClipChildren(false);
        root.setClipToPadding(false);

        LinearLayout logoContainer = new LinearLayout(this);
        logoContainer.setOrientation(LinearLayout.VERTICAL);
        logoContainer.setGravity(Gravity.CENTER);
        logoContainer.setClipChildren(false);
        logoContainer.setClipToPadding(false);

        FrameLayout.LayoutParams containerParams =
                new FrameLayout.LayoutParams(
                        dp(300),
                        dp(310),
                        Gravity.CENTER
                );

        root.addView(logoContainer, containerParams);

        // LOGO 1 - Pig illustration
        ImageView logo1 = new ImageView(this);
        logo1.setImageResource(R.drawable.logo1);
        logo1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo1.setAdjustViewBounds(true);
        logo1.setAlpha(0f);

        LinearLayout.LayoutParams logo1Params =
                new LinearLayout.LayoutParams(
                        dp(160),
                        dp(160)
                );

        logo1Params.gravity = Gravity.CENTER_HORIZONTAL;

        logoContainer.addView(logo1, logo1Params);

        // LOGO 2 - PigSensor wordmark
        ImageView logo2 = new ImageView(this);
        logo2.setImageResource(R.drawable.logo2);
        logo2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        logo2.setAdjustViewBounds(true);
        logo2.setAlpha(0f);

        LinearLayout.LayoutParams logo2Params =
                new LinearLayout.LayoutParams(
                        dp(260),
                        dp(80)
                );

        logo2Params.gravity = Gravity.CENTER_HORIZONTAL;
        logo2Params.topMargin = dp(15);

        logoContainer.addView(logo2, logo2Params);

        animationDialog.setContentView(root);
        animationDialog.setCancelable(false);

        Window window = animationDialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
            window.setGravity(Gravity.CENTER);
        }

        animationDialog.show();

        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
            );
        }

        // Fade in the wordmark
        animateLogo2(logo2);

        // Fade in, bounce, then exit the pig
        animatePig(logo1, logo2, email);
    }

    // Fade in wordmark
    private void animateLogo2(ImageView logo2) {

        ObjectAnimator fadeIn =
                ObjectAnimator.ofFloat(
                        logo2,
                        "alpha",
                        0f,
                        1f
                );

        fadeIn.setDuration(700);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.start();
    }

    // Pig fade-in and bounce animation
    private void animatePig(
            ImageView logo1,
            ImageView logo2,
            String email
    ) {

        // Initial position
        logo1.setTranslationY(dp(-35));
        logo1.setScaleX(1f);
        logo1.setScaleY(1f);

        // 1. Fade in first
        ObjectAnimator fadeIn =
                ObjectAnimator.ofFloat(
                        logo1,
                        "alpha",
                        0f,
                        1f
                );

        fadeIn.setDuration(500);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        fadeIn.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                if (isFinishing() || isDestroyed()) return;

                startPigBounce(logo1, logo2, email);
            }
        });

        fadeIn.start();
    }

    // Actual pig bounce

    private void startPigBounce(
            ImageView logo1,
            ImageView logo2,
            String email
    ) {

        // Normal starting shape
        logo1.setTranslationY(dp(-45));
        logo1.setScaleX(1f);
        logo1.setScaleY(1f);

        // 1. FALL: Slightly stretch vertically while falling
        ObjectAnimator fall =
                ObjectAnimator.ofFloat(
                        logo1,
                        "translationY",
                        dp(-45),
                        0
                );

        ObjectAnimator fallStretchX =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleX",
                        1f,
                        0.94f
                );

        ObjectAnimator fallStretchY =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleY",
                        1f,
                        1.08f
                );

        fall.setDuration(380);
        fall.setInterpolator(new AccelerateInterpolator());

        fallStretchX.setDuration(380);
        fallStretchY.setDuration(380);

        AnimatorSet falling = new AnimatorSet();
        falling.playTogether(fall, fallStretchX, fallStretchY);

        // 2. IMPACT: Squash when touching the floor
        ObjectAnimator impactX =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleX",
                        0.94f,
                        1.18f
                );

        ObjectAnimator impactY =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleY",
                        1.08f,
                        0.78f
                );

        impactX.setDuration(100);
        impactY.setDuration(100);

        AnimatorSet impact = new AnimatorSet();
        impact.playTogether(impactX, impactY);

        // OINK 1: First floor contact
        impact.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                playOink();
            }
        });

        // 3. TAKE OFF: Stretch vertically as the pig jumps
        ObjectAnimator jumpUp =
                ObjectAnimator.ofFloat(
                        logo1,
                        "translationY",
                        0,
                        dp(-32)
                );

        ObjectAnimator jumpStretchX =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleX",
                        1.18f,
                        0.92f
                );

        ObjectAnimator jumpStretchY =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleY",
                        0.78f,
                        1.14f
                );

        jumpUp.setDuration(210);
        jumpUp.setInterpolator(new DecelerateInterpolator());

        jumpStretchX.setDuration(210);
        jumpStretchY.setDuration(210);

        AnimatorSet jumping = new AnimatorSet();
        jumping.playTogether(
                jumpUp,
                jumpStretchX,
                jumpStretchY
        );

        // 4. FALL BACK: Return toward the floor
        ObjectAnimator land =
                ObjectAnimator.ofFloat(
                        logo1,
                        "translationY",
                        dp(-32),
                        0
                );

        ObjectAnimator landStretchX =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleX",
                        0.92f,
                        0.94f
                );

        ObjectAnimator landStretchY =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleY",
                        1.14f,
                        1.08f
                );

        land.setDuration(190);
        land.setInterpolator(new AccelerateInterpolator());

        landStretchX.setDuration(190);
        landStretchY.setDuration(190);

        AnimatorSet landing = new AnimatorSet();
        landing.playTogether(
                land,
                landStretchX,
                landStretchY
        );

        // 5. SECOND IMPACT: Smaller squash
        ObjectAnimator secondImpactX =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleX",
                        0.94f,
                        1.10f
                );

        ObjectAnimator secondImpactY =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleY",
                        1.08f,
                        0.90f
                );

        secondImpactX.setDuration(90);
        secondImpactY.setDuration(90);

        AnimatorSet secondImpact = new AnimatorSet();
        secondImpact.playTogether(
                secondImpactX,
                secondImpactY
        );

        // OINK 2: Second floor contact
        secondImpact.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                playOink();
            }
        });

        // 6. SETTLE: Smoothly return to normal shape
        ObjectAnimator settleX =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleX",
                        1.10f,
                        1f
                );

        ObjectAnimator settleY =
                ObjectAnimator.ofFloat(
                        logo1,
                        "scaleY",
                        0.90f,
                        1f
                );

        settleX.setDuration(180);
        settleY.setDuration(180);

        AnimatorSet settle = new AnimatorSet();
        settle.playTogether(settleX, settleY);

        // Complete bounce sequence
        AnimatorSet bounceAnimation = new AnimatorSet();

        bounceAnimation.playSequentially(
                falling,
                impact,
                jumping,
                landing,
                secondImpact,
                settle
        );

        bounceAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {

                if (isFinishing() || isDestroyed()) return;

                animationHandler.postDelayed(() -> {

                    if (isFinishing() || isDestroyed()) return;

                    exitPig(logo1, logo2, email);

                }, 400);
            }
        });

        bounceAnimation.start();
    }


    // Pig and wordmark fade out
    private void exitPig(
            ImageView logo1,
            ImageView logo2,
            String email
    ) {

        // Move pig upward while fading
        ObjectAnimator exitMovement =
                ObjectAnimator.ofFloat(
                        logo1,
                        "translationY",
                        0,
                        dp(-65)
                );

        ObjectAnimator pigFade =
                ObjectAnimator.ofFloat(
                        logo1,
                        "alpha",
                        1f,
                        0f
                );

        ObjectAnimator wordmarkFade =
                ObjectAnimator.ofFloat(
                        logo2,
                        "alpha",
                        1f,
                        0f
                );

        exitMovement.setDuration(650);
        exitMovement.setInterpolator(
                new DecelerateInterpolator()
        );

        pigFade.setDuration(650);
        wordmarkFade.setDuration(500);

        AnimatorSet exitAnimation = new AnimatorSet();

        exitAnimation.playTogether(
                exitMovement,
                pigFade,
                wordmarkFade
        );

        exitAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {

                // OINK 3 - Pig starts exiting
                playOink();
            }


            @Override
            public void onAnimationEnd(Animator animation) {

                if (isFinishing() || isDestroyed()) return;

                Intent intent = new Intent(
                        LoginActivity.this,
                        MainActivity.class
                );

                // Pass logged-in user's information
                intent.putExtra("USER_ID", loggedInUserId);
                intent.putExtra("USER_EMAIL", email);
                intent.putExtra("USER_FIRST_NAME", loggedInFirstName);
                intent.putExtra("USER_LAST_NAME", loggedInLastName);
                intent.putExtra("USER_ROLE", loggedInRole);

                startActivity(intent);

                finish();

                overridePendingTransition(0, 0);
            }

        });

        exitAnimation.start();
    }

    private int dp(float value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density
        );
    }

    @Override
    protected void onDestroy() {

        animationHandler.removeCallbacksAndMessages(null);

        if (animationDialog != null &&
                animationDialog.isShowing()) {
            animationDialog.dismiss();
        }

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        super.onDestroy();
    }
}
