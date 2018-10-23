package com.chaomeng.cmkeeplivelib.floatview.bindserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

/**
 * Author: SQSong
 * Date: 2018/9/30
 * Description:
 */
public class BindServerManager {
    private Context context;
    private static final String TAG = "BindServerManager";


    public BindServerManager(Context context) {
        this.context = context;
    }

    //绑定保活进程
    public void attmtoBindService() {
        Intent intent = new Intent();
        intent.setAction("test.library.chaomeng.com.keeplive.keep.service");
        intent.setPackage("test.library.chaomeng.com.keeplive");
        intent.setClassName("test.library.chaomeng.com.keeplive", "test.library.chaomeng.com.service.KeepLiveService");
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: ==");
            //收到bind成功后 通知主页面关闭


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            /**
             * 收到解绑消息
             * 1.先开启保护进程的首页面
             * 2.继续bind服务,等待收取下次解绑消息
             *
             */

            try {
                String url = "www.chaomeng://com.android.example";
                Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
                //                startActivity(in);
//                Intent intent = new Intent();
//                ComponentName cmp = new ComponentName("test.library.chaomeng.com.keeplive", "test.library.chaomeng.com.keeplive.FloatSettingActivity");
//                intent.setAction(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setComponent(cmp);
            } catch (Exception e) {
            }
            attmtoBindService();
        }
    };

    public void unbindServer() {
        context.unbindService(serviceConnection);
    }

}
