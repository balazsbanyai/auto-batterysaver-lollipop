package com.banyaibalazs.batterysaverlollipop;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by bbanyai on 12/06/15.
 */
public class AutomaticBatterySaverService extends AccessibilityService implements Bot.Listener {

    static final String ACTION = "android.settings.BATTERY_SAVER_SETTINGS";

    public static final String ACTION_EMULATE_POWER_DISCONNECTED = BuildConfig.APPLICATION_ID + ".EMULATE_POWER_DISCONNECTED";


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            doChangeSettings();
        }
    };

    private void doChangeSettings() {
        setupBot();
        openBatterySaverSettings();
    }

    private Bot bot;

    private void setupBot() {
        bot = new Bot(this);
    }

    private void tearDownBot() {
        bot = null;
    }

    private void openBatterySaverSettings() {
        Intent i = new Intent();
        i.setAction(ACTION);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        if (bot != null) {
            bot.execute(source);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        if (BuildConfig.DEBUG) {
            filter.addAction(ACTION_EMULATE_POWER_DISCONNECTED);
        }
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onSuccess() {
        tearDownBot();
        performGlobalAction(GLOBAL_ACTION_BACK);
    }
}
