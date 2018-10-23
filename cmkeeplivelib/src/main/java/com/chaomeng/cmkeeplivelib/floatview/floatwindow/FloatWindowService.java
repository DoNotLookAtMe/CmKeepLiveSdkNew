package com.chaomeng.cmkeeplivelib.floatview.floatwindow;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Author: SQSong
 * Date: 2018/9/25
 * Description:
 */
public class FloatWindowService extends Service {
    boolean flag = true;
    private static final String TAG = "FloatWindowService";
    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler handler = new Handler();

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;
    private UsageStatsManager sUsageStatsManager;
    private int NOTICE_ID = 100;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 开启定时器，每隔0.5秒刷新一次
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("KeepAppAlive");
//        builder.setContentText("DaemonService is runing...");
//        startForeground(NOTICE_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Service被终止的同时也停止定时器继续运行
        timer.cancel();
        timer = null;
    }

    class RefreshTask extends TimerTask {

        @Override
        public void run() {


            long elapsedCpuTime = Process.getElapsedCpuTime();

            Log.i(TAG, "run: " + elapsedCpuTime);

//             当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
            if (isHome(FloatWindowService.this) && !FloatWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FloatWindowManager.createSmallWindow(FloatWindowService.this);
                    }
                });
            }
            // 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗。
            else if (!isHome(FloatWindowService.this) && FloatWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        FloatWindowManager.removeSmallWindow(getApplicationContext());
                    }
                });
            }

        }

    }

    /**
     * 判断当前界面是否是桌面
     */
    private boolean isHome(Context context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
            boolean contains = getHomes().contains(rti.get(0).topActivity.getPackageName());
            return contains;
        } else if (isUseGranted()) {
            long endTime = System.currentTimeMillis();
            long beginTime = endTime - 1000 * 60 * 60 * 60;
            if (sUsageStatsManager == null) {
                sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            }
            String result = "";
            UsageEvents.Event event = new UsageEvents.Event();
            List<UsageStats> stats = sUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
            String topActivity = "";                     //取得最近运行的一个app，即当前运行的app
            if ((stats != null) && (!stats.isEmpty())) {
                int j = 0;
                for (int i = 0; i < stats.size(); i++) {
                    if (stats.get(i).getLastTimeUsed() > stats.get(j).getLastTimeUsed()) {
                        j = i;
                    }
                }
                topActivity = stats.get(j).getPackageName();
            }

            if (!android.text.TextUtils.isEmpty(topActivity)) {

                return getHomes().contains(topActivity);
            }
        }
        return false;

    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
            Log.i(TAG, "getHomes:  packageName = " + ri.activityInfo.packageName);
        }
        return names;
    }


    /**
     * 判断  用户查看使用情况的权利是否给予app
     *
     * @return
     */
    private boolean isUseGranted() {

        AppOpsManager appOps = (AppOpsManager) this
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                Process.myUid(), this.getPackageName());

        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }
}

