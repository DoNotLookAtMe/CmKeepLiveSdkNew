package com.chaomeng.cmkeeplivelib.floatview.floatwindow;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;


/**
 * 保活bind的临时activity
 * 该进程只是起到拉起进程,并且bind服务
 */
public class ThreeBindActivity extends Activity {
    private static final String TAG = "ThreeBindActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        getWindow().addFlags(flags);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;


        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        startService(new Intent(this, AidlService.class));
        //延时关闭当前activity
        Log.i(TAG, "onCreate: ");
//        finishActivity();
    }

    private void finishActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(600);
                finish();
            }
        }).start();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in);
    }
}
