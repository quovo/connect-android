package com.quovo.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.AnimRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;


import com.quovo.sdk.listeners.BroadCastManager;
import com.quovo.sdk.listeners.OnCompleteListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Builder which helps to launch webview activity with parameters
 */
public class QuovoConnectSdk {


    public static class Builder implements Serializable {

        private final transient Context context;
        protected transient List<OnCompleteListener> listeners = new ArrayList<>();

        private Integer key;
        protected Integer theme;
        private Integer animationOpenEnter = R.anim.modal_activity_open_enter;
        private Integer animationOpenExit = R.anim.modal_activity_open_exit;
        protected Integer animationCloseEnter;
        protected Integer animationCloseExit;
        protected String titleDefault;
        protected String url;
        protected boolean isAllowAutoImageLoad;
        protected boolean isAllowContentAccess;
        protected boolean isAllowFileAccess;

        public Builder setOnCompleteListener(OnCompleteListener listener) {
            listeners.clear();
            listeners.add(listener);
            return this;
        }

        public Builder addOnCompleteListener(OnCompleteListener listener) {
            listeners.add(listener);
            return this;
        }

        public Builder removeOnCompleteListener(OnCompleteListener listener) {
            listeners.remove(listener);
            return this;
        }

        public Builder(@NonNull Activity activity) {
            this.context = activity;
        }

        public Builder theme(@StyleRes int theme) {
            this.theme = theme;
            return this;
        }

        public Builder autoImageLoad(boolean loadStatus) {
            this.isAllowAutoImageLoad = loadStatus;
            return this;
        }

        public Builder allowContentAccess(boolean allowContentAccess) {
            this.isAllowContentAccess = allowContentAccess;
            return this;
        }

        public Builder allowFileAccess(boolean fileAccess) {
            this.isAllowFileAccess = fileAccess;
            return this;
        }

        public Builder customTitle(@NonNull String title) {
            this.titleDefault = title;
            return this;
        }

        public Builder titleDefaultRes(@StringRes int stringRes) {
            this.titleDefault = context.getResources().getString(stringRes);
            return this;
        }

        public Builder setCustomAnimations(@AnimRes int animationOpenEnter,
                                           @AnimRes int animationOpenExit, @AnimRes int animationCloseEnter,
                                           @AnimRes int animationCloseExit) {
            this.animationOpenEnter = animationOpenEnter;
            this.animationOpenExit = animationOpenExit;
            this.animationCloseEnter = animationCloseEnter;
            this.animationCloseExit = animationCloseExit;
            return this;
        }

        public void launch(@NonNull String token) {
            String url = generateUrl(token, null);
            show(url, null);
        }

        public void launch(@NonNull String token, HashMap<String, Object> options) {
            String url = generateUrl(token, options);
            show(url, null);
        }

        private void show(String url, String data) {
            this.url = url;
            this.key = System.identityHashCode(this);

            if (!listeners.isEmpty()) new BroadCastManager(context, key, listeners);

            Intent intent = new Intent(context, QuovoConnectSdkActivity.class);
            intent.putExtra("builder", this);
            context.startActivity(intent);

            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(animationOpenEnter, animationOpenExit);
            }
        }
    }

    private static String generateUrl(String token, HashMap<String, Object> options) {
        String optionsString = "";
        if (options != null) {
            List<String> optionArray = new ArrayList<>();
            Set<? extends Map.Entry<String, Object>> entrySet = options.entrySet();
            for (Map.Entry entry : entrySet) {
                optionArray.add(dasherize(entry.getKey().toString()) + "=" + entry.getValue());
            }
            if (optionArray.size() > 0) {
                optionsString = TextUtils.join("&", optionArray);
            }
        }
        return BuildConfig.BASE_URL + "?parent-host=" + BuildConfig.PARENT_HOST_URL + "&mobile=1&token=" + token + "&" + optionsString;
    }

    private static String dasherize(String key) {
        key = key.replaceAll("([^_])([A-Z])", "$1-$2");
        return key.toLowerCase();
    }


}
