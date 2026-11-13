
package com.darkbooster.app;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {

    WindowManager wm;

    LinearLayout leftPanel;
    LinearLayout rightPanel;

    TextView floatingButton;

    boolean panelOpen = false;

    final int red = Color.rgb(255, 35, 55);
    final int dark = Color.rgb(10, 12, 18);
    final int card = Color.rgb(35, 37, 48);

    final String CHANNEL_ID = "dark_booster_channel";
    final int NOTIFICATION_ID = 101;

    int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 23 &&
                !android.provider.Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();
            startForeground(NOTIFICATION_ID,
                    makeNotification());
        }

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        createPanels();
        createFloatingButton();
    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel =
                new NotificationChannel(
                    CHANNEL_ID,
                    "DARK BOOSTER",
                    NotificationManager.IMPORTANCE_LOW
                );

            NotificationManager manager =
                getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel);
        }
    }

    Notification makeNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("DARK BOOSTER")
                .setContentText("Gaming panel is running")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .build();
        }

        return new Notification.Builder(this)
            .setContentTitle("DARK BOOSTER")
            .setContentText("Gaming panel is running")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build();
    }

    int overlayType() {
        if (Build.VERSION.SDK_INT >= 26) {
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }

        return WindowManager.LayoutParams.TYPE_PHONE;
    }

    WindowManager.LayoutParams params(
            int width, int height, int gravity) {

        WindowManager.LayoutParams p =
            new WindowManager.LayoutParams(
                width,
                height,
                overlayType(),
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            );

        p.gravity = gravity;
        return p;
    }

    TextView makeTitle(String value) {
        TextView t = new TextView(this);

        t.setText(value);
        t.setTextColor(red);
        t.setTextSize(16);
        t.setTypeface(null, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(4), dp(14), dp(4), dp(14));

        return t;
    }

    void addPanelButton(LinearLayout panel, String name) {
        Button b = new Button(this);

        b.setText(name);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setTextSize(11);
        b.setBackgroundColor(card);

        LinearLayout.LayoutParams bp =
            new LinearLayout.LayoutParams(-1, dp(48));

        bp.setMargins(0, dp(3), 0, dp(3));
        panel.addView(b, bp);

        b.setOnClickListener(v ->
            Toast.makeText(this,
                name + " selected",
                Toast.LENGTH_SHORT).show()
        );
    }

    LinearLayout makePanel(String title) {
        LinearLayout panel = new LinearLayout(this);

        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(5), dp(8), dp(5), dp(8));
        panel.setBackgroundColor(dark);

        panel.addView(makeTitle(title));

        ScrollView scroll = new ScrollView(this);

        LinearLayout items = new LinearLayout(this);
        items.setOrientation(LinearLayout.VERTICAL);

        String[] features = {
            "CPU MONITOR",
            "RAM MONITOR",
            "FPS MONITOR",
            "TEMPERATURE",
            "NETWORK",
            "FOCUS COLOR",
            "TOUCH INDICATOR",
            "SOUND CONTROL",
            "ANTI DISTURB",
            "GAME SCREENSHOT"
        };

        for (String feature : features) {
            addPanelButton(items, feature);
        }

        scroll.addView(items);

        panel.addView(scroll,
            new LinearLayout.LayoutParams(-1, 0, 1));

        Button close = new Button(this);

        close.setText("CLOSE PANEL");
        close.setTextColor(Color.WHITE);
        close.setAllCaps(false);
        close.setBackgroundColor(red);

        panel.addView(close,
            new LinearLayout.LayoutParams(-1, dp(45)));

        close.setOnClickListener(v -> togglePanels());

        return panel;
    }

    void createPanels() {
        leftPanel = makePanel("DARK BOOSTER");
        rightPanel = makePanel("GAME CONTROL");

        int panelWidth = (int) (
            getResources().getDisplayMetrics().widthPixels * 0.30f
        );

        WindowManager.LayoutParams leftParams =
            params(panelWidth, -1,
                Gravity.LEFT | Gravity.CENTER_VERTICAL);

        WindowManager.LayoutParams rightParams =
            params(panelWidth, -1,
                Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        leftPanel.setVisibility(View.GONE);
        rightPanel.setVisibility(View.GONE);

        wm.addView(leftPanel, leftParams);
        wm.addView(rightPanel, rightParams);
    }

    void createFloatingButton() {
        floatingButton = new TextView(this);

        floatingButton.setText("DB");
        floatingButton.setTextColor(Color.WHITE);
        floatingButton.setTextSize(18);
        floatingButton.setTypeface(null, Typeface.BOLD);
        floatingButton.setGravity(Gravity.CENTER);
        floatingButton.setBackgroundColor(red);

        WindowManager.LayoutParams buttonParams =
            params(dp(58), dp(58),
                Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        wm.addView(floatingButton, buttonParams);

        floatingButton.setOnClickListener(v -> togglePanels());
    }

    void togglePanels() {
        panelOpen = !panelOpen;

        leftPanel.setVisibility(
            panelOpen ? View.VISIBLE : View.GONE);

        rightPanel.setVisibility(
            panelOpen ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (wm != null) {
            if (floatingButton != null &&
                    floatingButton.isAttachedToWindow()) {
                wm.removeView(floatingButton);
            }

            if (leftPanel != null &&
                    leftPanel.isAttachedToWindow()) {
                wm.removeView(leftPanel);
            }

            if (rightPanel != null &&
                    rightPanel.isAttachedToWindow()) {
                wm.removeView(rightPanel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
