
package com.darkbooster.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OverlayService extends Service {

    WindowManager wm;
    LinearLayout leftPanel, rightPanel;
    TextView floatingButton;
    boolean panelOpen = false;

    int red = Color.rgb(255, 35, 55);
    int dark = Color.rgb(10, 12, 18);

    int dp(int value) {
        return (int)(value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        createPanels();
        createFloatingButton();
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

    TextView makeTitle(String value) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextColor(red);
        t.setTextSize(17);
        t.setTypeface(null, Typeface.BOLD);
        t.setGravity(Gravity.CENTER);
        t.setPadding(dp(5), dp(15), dp(5), dp(15));
        return t;
    }

    void addPanelButton(LinearLayout panel, String name) {
        Button b = new Button(this);
        b.setText(name);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setTextSize(12);
        b.setBackgroundColor(Color.rgb(35, 37, 48));

        panel.addView(b, new LinearLayout.LayoutParams(
                -1, dp(55)));

        b.setOnClickListener(v ->
                Toast.makeText(this,
                        name + " selected",
                        Toast.LENGTH_SHORT).show());
    }

    LinearLayout makePanel(String title) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(5), dp(10), dp(5), dp(10));
        panel.setBackgroundColor(dark);

        panel.addView(makeTitle(title));

        addPanelButton(panel, "CPU MONITOR");
        addPanelButton(panel, "RAM MONITOR");
        addPanelButton(panel, "FPS MONITOR");
        addPanelButton(panel, "TEMPERATURE");
        addPanelButton(panel, "NETWORK");
        addPanelButton(panel, "FOCUS COLOR");
        addPanelButton(panel, "TOUCH INDICATOR");

        Button close = new Button(this);
        close.setText("CLOSE PANEL");
        close.setTextColor(Color.WHITE);
        close.setBackgroundColor(red);
        panel.addView(close);

        close.setOnClickListener(v -> togglePanels());

        return panel;
    }

    void createPanels() {
        leftPanel = makePanel("DARK BOOSTER");
        rightPanel = makePanel("GAME CONTROL");

        int panelWidth = (int)(getResources()
                .getDisplayMetrics().widthPixels * 0.30f);

        WindowManager.LayoutParams leftParams =
                params(panelWidth, -1, Gravity.LEFT | Gravity.CENTER_VERTICAL);

        WindowManager.LayoutParams rightParams =
                params(panelWidth, -1, Gravity.RIGHT | Gravity.CENTER_VERTICAL);

        leftPanel.setVisibility(View.GONE);
        rightPanel.setVisibility(View.GONE);

        wm.addView(leftPanel, leftParams);
        wm.addView(rightPanel, rightParams);
    }

    void createFloatingButton() {
        floatingButton = new TextView(this);
        floatingButton.setText("DB");
        floatingButton.setTextColor(Color.WHITE);
        floatingButton.setTextSize(17);
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
            if (floatingButton != null)
                wm.removeView(floatingButton);

            if (leftPanel != null)
                wm.removeView(leftPanel);

            if (rightPanel != null)
                wm.removeView(rightPanel);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
