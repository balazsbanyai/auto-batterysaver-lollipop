package com.banyaibalazs.batterysaverlollipop;

import android.util.Log;

/**
 * Created by bbanyai on 18/06/17.
 */
public abstract class Logger {

    public abstract void debug(String s);

    public static Logger get(Class<?> clazz) {
        if (BuildConfig.DEBUG) {
            return new LogcatLogger(clazz.getSimpleName());
        } else {
            return new SilentLogger();
        }

    }

    private static class LogcatLogger extends Logger {
        private final String tag;

        LogcatLogger(String clazzName) {
            this.tag = "AutoBatterySaver." + clazzName;
        }

        public void debug(String s) {
            Log.d(tag, s);
        }
    }

    private static class SilentLogger extends Logger {
        @Override
        public void debug(String s) {

        }
    }
}
