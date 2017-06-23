package com.banyaibalazs.batterysaverlollipop;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import static android.content.Intent.ACTION_POWER_DISCONNECTED;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NO_HISTORY;
import static android.content.Intent.FLAG_ACTIVITY_NO_USER_ACTION;
import static android.os.PowerManager.ACTION_POWER_SAVE_MODE_CHANGED;

/**
 * Created by bbanyai on 12/06/15.
 */
public class AutomaticBatterySaverService extends AccessibilityService implements Bot.Listener {

    static final String SETTINGS_ACTION = "android.settings.BATTERY_SAVER_SETTINGS";

    public static final String ACTION_EMULATE_POWER_DISCONNECTED = BuildConfig.APPLICATION_ID + ".EMULATE_POWER_DISCONNECTED";

    private Logger logger = Logger.get(AutomaticBatterySaverService.class);

    BroadcastReceiver disconnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            logger.debug("Handling "+ACTION_POWER_DISCONNECTED);
            doChangeSettings();
            logger.debug("Handled "+ACTION_POWER_DISCONNECTED);
        }
    };

    BroadcastReceiver powerSaveModeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (AutomaticBatterySaverService.this) {
                unregisterReceiver(this);
                logger.debug("Handling "+ACTION_POWER_SAVE_MODE_CHANGED);
                onBatterySaveModeChanged();
                logger.debug("Handled "+ACTION_POWER_SAVE_MODE_CHANGED);
            }
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
        i.setAction(SETTINGS_ACTION);
        i.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_NO_HISTORY | FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NO_USER_ACTION);
        startActivity(i);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (bot != null && !pm.isPowerSaveMode()) {
            IntentFilter filter = new IntentFilter(ACTION_POWER_SAVE_MODE_CHANGED);
            synchronized (AutomaticBatterySaverService.this) {
                registerReceiver(powerSaveModeChangeReceiver, filter);
            }
            bot.execute(source);
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        IntentFilter filter = new IntentFilter(ACTION_POWER_DISCONNECTED);
        if (BuildConfig.DEBUG) {
            filter.addAction(ACTION_EMULATE_POWER_DISCONNECTED);
        }
        registerReceiver(disconnectedReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(disconnectedReceiver);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onSuccess() {
        tearDownBot();
    }

    private void onBatterySaveModeChanged() {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm.isPowerSaveMode()) { // TODO check if battery saver is still on-screen
            performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }
}
