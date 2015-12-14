package com.project.estevao.chatbluetooth;

import android.content.Context;

public final class ApplicationUtil {

    private static Context context;

    private static Context applicationContext;

    private ApplicationUtil() {
    }
    public static void setContext(Context context) {
        ApplicationUtil.context = context;
    }

    public static Context getContext() {
        return ApplicationUtil.context;
    }
}
