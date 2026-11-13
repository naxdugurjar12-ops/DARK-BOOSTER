
package com.darkbooster.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    LinearLayout main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(20, 30, 20, 20);
        main.setBackgroundColor(Color.rgb(10, 12, 20));

        TextView title = new TextView(this);
        title.setText("DARK BOOSTER");
        title.setTextSize(28);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        title.setPadding(10, 25, 10, 25);
        main.addView(title);

        addCard("CPU MONITOR", "CPU Status: Ready");
        addCard("GPU MONITOR", "GPU Status: Device dependent");
        addCard("RAM MONITOR", "RAM Monitor");
        addCard("FPS MONITOR", "FPS Monitor: Not available yet");

        TextView ultra = new TextView(this);
        ultra.setText("ULTRA MODE");
        ultra.setTextColor(Color.CYAN);
        ultra.setTextSize(20);
        ultra.setGravity(Gravity.CENTER);
        ultra.setPadding(15, 25, 15, 25);
        main.addView(ultra);

        setContentView(main);
    }

    void addCard(String heading, String detail) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(20, 18, 20, 18);
        card.setBackgroundColor(Color.rgb(28, 32, 48));

        TextView title = new TextView(this);
        title.setText(heading);
        title.setTextColor(Color.CYAN);
        title.setTextSize(19);

        TextView info = new TextView(this);
        info.setText(detail);
        info.setTextColor(Color.WHITE);
        info.setTextSize(15);
        info.setPadding(0, 10, 0, 0);

        card.addView(title);
        card.addView(info);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                -1, -2
            );
        params.setMargins(0, 10, 0, 10);

        main.addView(card, params);
    }
}
