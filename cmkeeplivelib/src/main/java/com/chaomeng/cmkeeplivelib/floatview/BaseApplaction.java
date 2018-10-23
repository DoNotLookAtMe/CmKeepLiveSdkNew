package com.chaomeng.cmkeeplivelib.floatview;

import android.app.Application;
import android.content.Context;

/**
 * Author: SQSong
 * Date: 2018/9/27
 * Description:
 */
public class BaseApplaction extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
    }
}
