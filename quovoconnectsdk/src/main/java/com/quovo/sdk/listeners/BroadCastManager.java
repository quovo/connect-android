package com.quovo.sdk.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

/**
 * Used to receive callbacks from webview and pass it to application
 */
public class BroadCastManager {

    private static final String WEBVIEW_EVENT = "WEBVIEW_EVENT";
    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    private static final String EXTRA_CALLBACK = "EXTRA_CALLBACK";
    private static final String EXTRA_RESPONSE = "EXTRA_RESPONSE";
    private int key;
    private List<OnCompleteListener> listeners;
    private LocalBroadcastManager manager;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (context == null || intent == null) return;
            handleIntent(intent);
        }
    };

    public BroadCastManager(Context context, int key, @NonNull List<OnCompleteListener> listeners) {
        this.key = key;
        this.listeners = listeners;
        manager = LocalBroadcastManager.getInstance(context);
        manager.registerReceiver(receiver, new IntentFilter(WEBVIEW_EVENT));
    }

    // Base Static Methods
    private static Intent getBaseIntent(Type type) {
        return new Intent(BroadCastManager.WEBVIEW_EVENT)
                .putExtra(EXTRA_TYPE, type);
    }

    private static void sendBroadCast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // Handle Each Event Type
    public static void onComplete(Context context, String callback, String response) {
        Intent intent = getBaseIntent(Type.ON_COMPLETE).putExtra(EXTRA_CALLBACK, callback).putExtra(EXTRA_RESPONSE, response);
        sendBroadCast(context, intent);
    }

    public static void unregister(Context context) {
        Intent intent = getBaseIntent(Type.UNREGISTER);
        sendBroadCast(context, intent);
    }

    private void handleIntent(@NonNull Intent intent) {
        Type type = (Type) intent.getSerializableExtra(EXTRA_TYPE);
        switch (type) {
            case ON_COMPLETE:
                onComplete(intent);
                break;

            case UNREGISTER:
                unregister();
                break;
        }
    }

    private void onComplete(Intent intent) {
        for (OnCompleteListener listener : listeners)
            listener.onComplete(intent.getStringExtra(EXTRA_CALLBACK), intent.getStringExtra(EXTRA_RESPONSE));
    }


    private void unregister() {
        if (manager != null && receiver != null) manager.unregisterReceiver(receiver);
    }

    public enum Type {
        ON_COMPLETE, UNREGISTER
    }
}
