package com.banyaibalazs.batterysaverlollipop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.navigateToAccessibilitySettings).setOnClickListener(this);

        if (BuildConfig.DEBUG) {
            ViewGroup root = (ViewGroup) findViewById(R.id.root);
            Button changeSettingButton = new Button(this);
            changeSettingButton.setText("Emulate power disconnected");
            changeSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent broadcast = new Intent(AutomaticBatterySaverService.ACTION_EMULATE_POWER_DISCONNECTED);
                    sendBroadcast(broadcast);
                }
            });
            root.addView(changeSettingButton);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.navigateToAccessibilitySettings:
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
                break;
        }
    }
}
