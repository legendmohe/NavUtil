package com.legendmohe.app;

import android.app.Application;

import com.legendmohe.navutil.NavUtil;

/**
 * Created by legendmohe on 2017/1/23.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NavUtil.init(this);
    }
}
