package com.chaomeng.cmkeeplivelib.floatview.floatwindow;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.TimeUnit;

/**
 * Author: SQSong
 * Date: 2018/9/28
 * Description:
 */
@android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class KeepLiveJobService extends JobService {
    private static final String TAG = "KeepLiveJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i(TAG, "onStartJob: params =  ");

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onStopJob: ");
        return false;
    }

    @Override
    public void onCreate() {
        startJobSheduler();
        Log.i(TAG, "onCreate: ");
    }

    private void startJobSheduler() {
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(getPackageName(), KeepLiveJobService.class.getName()));
        builder.setMinimumLatency(TimeUnit.MILLISECONDS.toMillis(10)); //执行的最小延迟时间
        builder.setOverrideDeadline(TimeUnit.MILLISECONDS.toMillis(15));  //执行的最长延时时间
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_NOT_ROAMING);  //非漫游网络状态
        builder.setBackoffCriteria(TimeUnit.MINUTES.toMillis(10), JobInfo.BACKOFF_POLICY_LINEAR);  //线性重试方案
//        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(true);
//        builder.setPeriodic(5);
        builder.setPersisted(true);

        JobScheduler systemService = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//        systemService.cancel(1);
        systemService.schedule(builder.build());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }
}
