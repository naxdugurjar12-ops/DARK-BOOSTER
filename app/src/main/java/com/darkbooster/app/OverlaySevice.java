package com.darkbooster.app;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {

    WindowManager wm;
    LinearLayout leftPanel, rightPanel;
    TextView floatingButton;
    boolean panelOpen = false;

    final int red = Color.rgb(255, 35, 55);
    final int dark = Color.rgb(10, 12, 18);
    final int card = Color.rgb(35, 37, 48);

    final String CHANNEL_ID = "dark_booster_channel";
    final int NOTIFICATION_ID = 101;

    int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        if (Build.VERSION.SDK_INT >= 26) {
            createNotificationChannel();

            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(
                    NOTIFICATION_ID,
                    makeNotification(),
                    android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                );
            } else {
                startForeground(NOTIFICATION_ID, makeNotification());
            }
        }

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        createPanels();
        createFloatingButton();
    }

    void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Dark Booster",
                NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);
        }
    }

    Notification makeNotification() {
        if (Build.VERSION.SDK_INT >= 26) {
            return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("DARK BOOSTER")
                .setContentText("Gaming panel is running")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setOngoing(true)
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

    WindowManager.LayoutParams params(int width, int height, int gravity) {
        return new WindowManager.LayoutParams(
            width,
            height,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ) {{
            this.gravity = gravity;
        }};
    }

    TextView makeTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(red);
        title.setTextSize(17);
        title.setGravity(Gravity.CENTER);
        title.setPadding(dp(4), dp(12), dp(4), dp(12));
        title.setTypeface(null, 1);
        return title;
    }

    void addPanelButton(LinearLayout layout, String name) {
        Button button = new Button(this);
        button.setText(name);
        button.setTextColor(Color.WHITE);
        button.setTextSize(11);
        button.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(card)
        );

        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(
            -1, dp(48)
        );
        bp.setMargins(dp(4), dp(4), dp(4), dp(4));

        layout.addView(button, bp);

        button.setOnClickListener(v ->
            Toast.makeText(this, name + " selected", Toast.LENGTH_SHORT).show()
        );
    }

    LinearLayout makePanel(String side) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundColor(dark);
        panel.setPadding(dp(5), dp(5), dp(5), dp(5));

        panel.addView(makeTitle("DARK BOOSTER"));

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
        panel.addView(scroll);

        return panel;
    }

    void createPanels() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int panelWidth = (int) (screenWidth * 0.30f);

        leftPanel = makePanel("LEFT");
        rightPanel = makePanel("RIGHT");

        WindowManager.LayoutParams leftParams =
            params(panelWidth, -1, Gravity.LEFT | Gravity.CENTER_VERTICAL);

        WindowManager.LayoutParams rightParams =
            params(panelWidth, -1, Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        leftParams.x = 0;
        rightParams.x = 0;

        wm.addView(leftPanel, leftParams);
        wm.addView(rightPanel, rightParams);

        leftPanel.setVisibility(View.GONE);
        rightPanel.setVisibility(View.GONE);
    }

    void createFloatingButton() {
        floatingButton = new TextView(this);
        floatingButton.setText("DB");
        floatingButton.setTextColor(Color.WHITE);
        floatingButton.setTextSize(17);
        floatingButton.setTypeface(null, 1);
        floatingButton.setGravity(Gravity.CENTER);
        floatingButton.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(red)
        );
        floatingButton.setBackgroundColor(red);

        WindowManager.LayoutParams buttonParams =
            params(dp(55), dp(55), Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        buttonParams.x = dp(8);

        wm.addView(floatingButton, buttonParams);

        floatingButton.setOnClickListener(v -> togglePanels());
    }

    void togglePanels() {
        panelOpen = !panelOpen;

        if (panelOpen) {
            leftPanel.setVisibility(View.VISIBLE);
            rightPanel.setVisibility(View.VISIBLE);
        } else {
            leftPanel.setVisibility(View.GONE);
            rightPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (wm != null) {
            if (leftPanel != null) wm.removeView(leftPanel);
            if (rightPanel != null) wm.removeView(rightPanel);
            if (floatingButton != null) wm.removeView(floatingButton);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
