package com.example.pigtech;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TechnicalSupportActivity extends AppCompatActivity {

    private static final String TAG = "TechSupport";

    // The form has no pen field yet, so every ticket is sent with this pen.
    private static final String DEFAULT_PEN = "Pen A";

    // TODO: replace with your real support numbers
    private static final String EMERGENCY_NUMBER = "09123456789";
    private static final String FARM_NUMBER      = "09123456780";
    private static final String HARDWARE_NUMBER  = "09123456781";

    private LinearLayout content, ticketContainer;
    private View btnEmergency, btnCallFarm, btnCallHardware, btnSubmit;
    private EditText etIssue, etDesc;
    private ImageView ivScan, ivEmergencyPhone;
    private final ImageView[] tabs = new ImageView[4];

    private ObjectAnimator emergencyPulse, phoneRing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_support);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        bindViews();
        setupTabs();
        setupCallButtons();
        setupSubmit();
        loadTickets();

        playEntranceAnimations();
        startEmergencyPulse();
    }

    private void bindViews() {
        content          = findViewById(R.id.content);
        ticketContainer  = findViewById(R.id.ticketContainer);
        btnEmergency     = findViewById(R.id.btnEmergency);
        btnCallFarm      = findViewById(R.id.btnCallFarm);
        btnCallHardware  = findViewById(R.id.btnCallHardware);
        btnSubmit        = findViewById(R.id.btnSubmit);
        etIssue          = findViewById(R.id.etIssue);
        etDesc           = findViewById(R.id.etDesc);
        ivScan           = findViewById(R.id.ivScan);
        ivEmergencyPhone = findViewById(R.id.ivEmergencyPhone);
        tabs[0] = findViewById(R.id.tab0);
        tabs[1] = findViewById(R.id.tab1);
        tabs[2] = findViewById(R.id.tab2);
        tabs[3] = findViewById(R.id.tab3);
    }

    // ------------------------------------------------------------------
    // TABS
    // ------------------------------------------------------------------
    private void setupTabs() {
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            tabs[i].setOnClickListener(v -> selectTab(index));
        }
        ivScan.setOnClickListener(v -> {
            ivScan.animate().rotationBy(360f).setDuration(500).start();
            Toast.makeText(this, "Scanner coming soon", Toast.LENGTH_SHORT).show();
        });
        selectTab(2); // same selected tab as the design
    }

    private void selectTab(int selected) {
        for (int i = 0; i < tabs.length; i++) {
            boolean isSelected = (i == selected);
            tabs[i].setBackgroundResource(isSelected ? R.drawable.bg_tab_selected : R.drawable.bg_tab);
            // the pig icon (tab 0) is multi-colored, so we don't tint it
            if (i != 0) {
                tabs[i].setColorFilter(Color.parseColor(isSelected ? "#E8607A" : "#FFFFFF"));
            }
            tabs[i].animate()
                    .scaleX(isSelected ? 1.1f : 1f)
                    .scaleY(isSelected ? 1.1f : 1f)
                    .translationZ(isSelected ? 10f : 0f)
                    .setDuration(250)
                    .setInterpolator(new OvershootInterpolator(2f))
                    .start();
        }
    }

    // ------------------------------------------------------------------
    // CALL BUTTONS
    // ------------------------------------------------------------------
    private void setupCallButtons() {
        addPressEffect(btnEmergency);
        addPressEffect(btnCallFarm);
        addPressEffect(btnCallHardware);

        btnEmergency.setOnClickListener(v -> dial(EMERGENCY_NUMBER));
        btnCallFarm.setOnClickListener(v -> dial(FARM_NUMBER));
        btnCallHardware.setOnClickListener(v -> dial(HARDWARE_NUMBER));
    }

    /** Opens the phone dialer (no permission needed). */
    private void dial(String number) {
        try {
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No phone app found", Toast.LENGTH_SHORT).show();
        }
    }

    // ------------------------------------------------------------------
    // TICKETS (talks to the PHP API through TicketApi)
    // ------------------------------------------------------------------
    private void setupSubmit() {
        addPressEffect(btnSubmit);
        btnSubmit.setOnClickListener(v -> submitTicket());
    }

    private void submitTicket() {
        String issue = etIssue.getText().toString().trim();
        String desc  = etDesc.getText().toString().trim();

        if (issue.isEmpty()) {
            etIssue.setError("Please enter the issue type");
            shake(etIssue);
            return;
        }
        if (desc.isEmpty()) {
            etDesc.setError("Please describe the problem");
            shake(etDesc);
            return;
        }

        if (ApiConfig.getToken(this).isEmpty()) {
            Toast.makeText(this, "You're not logged in. Please log in again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("issue_type", issue);
            body.put("description", desc);
            body.put("pen_number", DEFAULT_PEN);
        } catch (JSONException e) {
            Log.e(TAG, "bad json", e);
            return;
        }

        btnSubmit.setEnabled(false);

        TicketApi.post(this, "submit_ticket.php", body, new TicketApi.Callback() {
            @Override
            public void onSuccess(JSONObject json) {
                if (isFinishing() || isDestroyed()) return;
                btnSubmit.setEnabled(true);
                Toast.makeText(TechnicalSupportActivity.this,
                        json.optString("message", "Ticket submitted"),
                        Toast.LENGTH_SHORT).show();
                etIssue.setText("");
                etDesc.setText("");
                etIssue.clearFocus();
                etDesc.clearFocus();
                hideKeyboard();
                loadTickets(); // refresh My Tickets from the database
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) return;
                btnSubmit.setEnabled(true);
                Toast.makeText(TechnicalSupportActivity.this, message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Loads the logged-in user's tickets from the database. */
    private void loadTickets() {
        if (ApiConfig.getToken(this).isEmpty()) return;

        TicketApi.get(this, "get_tickets.php", new TicketApi.Callback() {
            @Override
            public void onSuccess(JSONObject json) {
                if (isFinishing() || isDestroyed()) return;

                JSONArray tickets = json.optJSONArray("tickets");
                if (tickets == null) tickets = new JSONArray();

                ticketContainer.removeAllViews();
                for (int i = 0; i < tickets.length(); i++) {
                    JSONObject t = tickets.optJSONObject(i);
                    if (t == null) continue;

                    String ticketId = String.format("TK-%03d", t.optInt("ticket_id", 0));
                    View item = addTicket(-1, R.drawable.ic_wrench_dark,
                            t.optString("issue_type", "Issue"),
                            ticketId + " . " + t.optString("pen_number", "")
                                    + " . " + t.optString("created_at", ""),
                            t.optString("status", "Open"));

                    if (i == 0) { // little drop-in on the newest ticket
                        item.setAlpha(0f);
                        item.setTranslationX(-200f);
                        item.animate().alpha(1f).translationX(0f).setDuration(500)
                                .setInterpolator(new OvershootInterpolator(1.2f)).start();
                    }
                }
            }

            @Override
            public void onError(String message) {
                Log.w(TAG, "loadTickets: " + message);
            }
        });
    }

    /** index = -1 adds at the bottom, 0 adds at the top. */
    private View addTicket(int index, int iconRes, String title, String sub, String status) {
        View item = getLayoutInflater().inflate(R.layout.item_ticket, ticketContainer, false);

        ((ImageView) item.findViewById(R.id.ivTicketIcon)).setImageResource(iconRes);
        ((TextView) item.findViewById(R.id.tvTicketTitle)).setText(title);
        ((TextView) item.findViewById(R.id.tvTicketSub)).setText(sub);

        TextView tvStatus = item.findViewById(R.id.tvStatus);
        tvStatus.setText(status);
        switch (status) {
            case "In Progress":
                tvStatus.setBackgroundResource(R.drawable.bg_status_progress);
                tvStatus.setTextColor(Color.parseColor("#3B6FA0"));
                break;
            case "Open":
                tvStatus.setBackgroundResource(R.drawable.bg_status_open);
                tvStatus.setTextColor(Color.parseColor("#2E9E4F"));
                break;
            default: // Resolved
                tvStatus.setBackgroundResource(R.drawable.bg_status_resolved);
                tvStatus.setTextColor(Color.parseColor("#555555"));
                break;
        }

        ticketContainer.addView(item, index);
        return item;
    }

    // ------------------------------------------------------------------
    // ANIMATIONS
    // ------------------------------------------------------------------

    /** Every section of the page slides up one after the other. */
    private void playEntranceAnimations() {
        for (int i = 0; i < content.getChildCount(); i++) {
            View child = content.getChildAt(i);
            child.setAlpha(0f);
            child.setTranslationY(60f);
            child.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setStartDelay(100L + i * 70L)
                    .setDuration(450)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }
    }

    /** Emergency Call button gently pulses and the phone icon "rings". */
    private void startEmergencyPulse() {
        emergencyPulse = ObjectAnimator.ofPropertyValuesHolder(btnEmergency,
                PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f));
        emergencyPulse.setDuration(700);
        emergencyPulse.setInterpolator(new AccelerateDecelerateInterpolator());
        emergencyPulse.setRepeatCount(ValueAnimator.INFINITE);
        emergencyPulse.setRepeatMode(ValueAnimator.REVERSE);
        emergencyPulse.start();

        phoneRing = ObjectAnimator.ofFloat(ivEmergencyPhone, "rotation", -15f, 15f);
        phoneRing.setDuration(250);
        phoneRing.setRepeatCount(ValueAnimator.INFINITE);
        phoneRing.setRepeatMode(ValueAnimator.REVERSE);
        phoneRing.start();
    }

    private void shake(View v) {
        ObjectAnimator.ofFloat(v, "translationX", 0f, -16f, 16f, -12f, 12f, -6f, 6f, 0f)
                .setDuration(400).start();
    }

    private void addPressEffect(View v) {
        v.setOnTouchListener((view, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    view.animate().scaleX(1f).scaleY(1f).setDuration(200)
                            .setInterpolator(new OvershootInterpolator(3f)).start();
                    break;
            }
            return false;
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
        }
    }

    // ------------------------------------------------------------------
    // LIFECYCLE
    // ------------------------------------------------------------------

    /** Slide the screen out to the right when going back. */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onPause() {
        super.onPause();
        emergencyPulse.pause();
        phoneRing.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        emergencyPulse.resume();
        phoneRing.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emergencyPulse.cancel();
        phoneRing.cancel();
    }
}