package com.banyaibalazs.batterysaverlollipop;

import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Switch;


/**
 * Created by bbanyai on 13/06/15.
 */
public class Bot {

    private Logger logger = Logger.get(Bot.class);

    interface Listener {
        void onSuccess();
    }

    private Listener listener;

    public Bot(Listener listener) {
        this.listener = listener;
    }

    public void execute(final AccessibilityNodeInfo source) {
        final AccessibilityNodeInfo theSwitch = findSwitchRecursively(source);
        if (theSwitch != null) {
            logger.debug("Performing click...");
            theSwitch.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            logger.debug("Performed click...");
            if (listener != null) {
                listener.onSuccess();
            }
        }
    }

    private AccessibilityNodeInfo findSwitchRecursively(final AccessibilityNodeInfo source) {
        if (source.getChildCount() > 0) {
            AccessibilityNodeInfo newSource = null;
            for (int i = 0; i < source.getChildCount(); i++) {
                if (newSource != null) {
                    newSource.recycle();
                }

                newSource = source.getChild(i);

                if (newSource != null) {
                    if (newSource.getClassName().equals(Switch.class.getName())) {
                        logger.debug("Switch found");
                        return newSource;
                    } else {
                        AccessibilityNodeInfo theSwitch = findSwitchRecursively(newSource);
                        if (theSwitch != null) {
                            return theSwitch;
                        }
                    }
                }
            }
        }

        return null;
    }

}
