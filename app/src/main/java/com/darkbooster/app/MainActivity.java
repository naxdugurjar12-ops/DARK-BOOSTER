
package com.darkbooster.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    LinearLayout main;
    String selectedMode = "STANDARD";
    int red = Color.rgb(255, 35, 55);
    int dark = Color.rgb(10, 12, 18);
    int cardColor = Color.rgb(28, 30, 40);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDashboard();
    }

    int dp(int value) {
        return (int)(value * getResources()
                .getDisplayMetrics().density);
    }

    TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(dp(8), dp(8), dp(8), dp(8));
        return t;
    }

    Button makeButton(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setAllCaps(false);
        b.setBackgroundColor(cardColor);
        return b;
    }

    void setupPage(String title) {
        ScrollView scroll = new ScrollView(this);
        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(dp(16), dp(20), dp(16), dp(20));
        main.setBackgroundColor(dark);

        TextView heading = text(title, 25, red);
        heading.setGravity(Gravity.CENTER);
        main.addView(heading);

        scroll.addView(main);
        setContentView(scroll);
    }

    void addButton(String name, Runnable action) {
        Button b = makeButton(name);
        LinearLayout.LayoutParams p =
            new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, dp(5), 0, dp(5));
        main.addView(b, p);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        });
    }

    void showDashboard() {
        setupPage("DARK BOOSTER");

        TextView sub = text(
            "ULTIMATE GAMING CONTROL",
            13, Color.LTGRAY);
        sub.setGravity(Gravity.CENTER);
        main.addView(sub);

        main.addView(text(
            "SELECT GAMING MODE", 18, Color.WHITE));

        addButton("STANDARD MODE", new Runnable() {
            public void run() {
                selectMode("STANDARD");
            }
        });

        addButton("BASIC MODE", new Runnable() {
            public void run() {
                selectMode("BASIC");
            }
        });

        addButton("ULTRA MODE", new Runnable() {
            public void run() {
                selectMode("ULTRA");
            }
        });

        TextView current = text(
            "CURRENT MODE: " + selectedMode,
            17, red);
        current.setGravity(Gravity.CENTER);
        main.addView(current);

        main.addView(text(
            "PERFORMANCE MONITORS", 18, Color.WHITE));

        String[] monitors = {
            "CPU MONITOR",
            "GPU MONITOR",
            "RAM MONITOR",
            "FPS MONITOR",
            "TEMPERATURE MONITOR",
            "NETWORK MONITOR",
            "STORAGE MONITOR"
        };

        for (final String monitor : monitors) {
            addButton(monitor, new Runnable() {
                public void run() {
                    showMonitor(monitor);
                }
            });
        }

        addButton("DEVICE HEALTH", new Runnable() {
            public void run() {
                showHealth();
            }
        });

        addButton("GAMING PANEL", new Runnable() {
            public void run() {
                showGamingPanel();
            }
        });
    }

    void selectMode(String mode) {
        selectedMode = mode;
        Toast.makeText(this,
            mode + " MODE SELECTED",
            Toast.LENGTH_SHORT).show();
        showDashboard();
    }

    void showMonitor(String name) {
        setupPage(name);

        GaugeView gauge = new GaugeView(this, name);
        main.addView(gauge,
            new LinearLayout.LayoutParams(-1, dp(230)));

        TextView info = text(
            "Live device data is not connected yet.",
            15, Color.LTGRAY);
        info.setGravity(Gravity.CENTER);
        main.addView(info);

        addButton("BACK TO DASHBOARD", new Runnable() {
            public void run() {
                showDashboard();
            }
        });
    }

    void showHealth() {
        new AlertDialog.Builder(this)
            .setTitle("DEVICE HEALTH")
            .setMessage(
                "Battery: Data not connected\n\n" +
                "Temperature: Data not connected\n\n" +
                "RAM: Data not connected\n\n" +
                "Storage: Data not connected\n\n" +
                "Network: Data not connected")
            .setPositiveButton("OK", null)
            .show();
    }

    void showGamingPanel() {
        new AlertDialog.Builder(this)
            .setTitle("DARK BOOSTER GAMING PANEL")
            .setMessage(
                "STANDARD / BASIC / ULTRA\n\n" +
                "Performance Monitor\n" +
                "Device Health\n" +
                "Battery Saver\n" +
                "Touch Indicator\n" +
                "Focus Color\n" +
                "Network Monitor\n" +
                "Edge Lighting\n\n" +
                "Panel preview only. " +
                "Floating overlay implementation is pending.")
            .setPositiveButton("CLOSE", null)
            .show();
    }

    class GaugeView extends View {

        Paint paint = new Paint(3);
        String label;

        GaugeView(Activity context, String name) {
            super(context);
            label = name;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            float radius = dp(75);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(12));
            paint.setColor(cardColor);

            RectF oval = new RectF(
                cx-radius, cy-radius,
                cx+radius, cy+radius);

            canvas.drawArc(oval, 135, 270,
                false, paint);

            paint.setColor(red);
            canvas.drawArc(oval, 135, 0,
                false, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            paint.setTextSize(dp(23));
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("--", cx, cy, paint);

            paint.setTextSize(dp(13));
            paint.setColor(Color.LTGRAY);
            canvas.drawText(label, cx, cy+dp(27), paint);
        }
    }
}
