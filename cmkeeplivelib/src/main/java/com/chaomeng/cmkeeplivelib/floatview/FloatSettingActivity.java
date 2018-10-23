package com.chaomeng.cmkeeplivelib.floatview;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.chaomeng.cmkeeplivelib.R;
import com.chaomeng.cmkeeplivelib.floatview.floatwindow.FloatWindowService;


public class FloatSettingActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private int OVERLAY_PERMISSION_REQ_CODE = 10;
    private int _USAGE_ACCESS_CODE = 11;
    private int NOTICE_ID = 100;
    private Switch storagePower;
    private Switch floatPower;
    private Switch playMusic;
    private boolean storagePowerHave;
    private boolean floatPowerHave;
    private LibConfig libInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floatview_main);
        //获取开关状态
        getSwitchState();
        //初始化view
        initView();
        //设置监听
        initListen();

    }

    /**
     * 初始化开关状态
     */
    private void getSwitchState() {
        libInstance = LibConfig.getInstance();
        storagePowerHave = libInstance.getBoolean(SpConstans.STORAGE_POWER_SWITCH, false); //内存状态权限获取状态
        floatPowerHave = libInstance.getBoolean(SpConstans.FLOAT_POWER_SWITCH, false);   //悬浮窗权限获取状态
    }

    /**
     * 设置监听
     */
    private void initListen() {
        storagePower.setOnCheckedChangeListener(this);
        floatPower.setOnCheckedChangeListener(this);
        playMusic.setOnCheckedChangeListener(this);

        storagePower.setChecked(storagePowerHave);
        floatPower.setChecked(floatPowerHave);
    }

    /**
     * 初始化view
     */
    private void initView() {
        storagePower = findViewById(R.id.requst_power);
        floatPower = findViewById(R.id.requst_float_view);
        playMusic = findViewById(R.id.play_music);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setupToolbar(toolbar);
        if (Build.VERSION.SDK_INT <= 21) {
            storagePower.setVisibility(View.GONE);
        } else {
            storagePower.setVisibility(View.VISIBLE);
        }

    }

//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (i == R.id.requst_power) {

//
//        } else if (i == R.id.requst_float_view) {
//            if (Build.VERSION.SDK_INT >= 23) {
//                if (Settings.canDrawOverlays(this)) {
//                    //有悬浮窗权限开启服务绑定 绑定权限
//                    // 如果觉得常驻通知栏体验不好            // 可以通过启动CancelNoticeService，将通知移除，oom_adj值不变            Intent intent = new Intent(this,CancelNoticeService.class);            startService(intent);
//                    Intent intent = new Intent(FloatSettingActivity.this, FloatWindowService.class);
//                    startService(intent);
//
//                } else {

//
//                }
//            } else {
//                //默认有悬浮窗权限  但是 华为, 小米,oppo等手机会有自己的一套Android6.0以下  会有自己的一套悬浮窗权限管理 也需要做适配
//                Intent intent = new Intent(FloatSettingActivity.this, FloatWindowService.class);
//                startService(intent);
//            }
//
//
//        } else if (i == R.id.play_music) {//                startActivity(new Intent(this,Liabrary.class));
//            startService(new Intent(this, VoicePlayService.class));
//
//        } else if (i == R.id.start_jobserver) {
//            startService(new Intent(this, KeepLiveJobService.class));
//
//        }
//
//    }

    /**
     * 判断  用户查看使用情况的权利是否给予app
     * api 21之后  android:get_usage_stats需要   需要申请权限
     * api 21之前,不需要申请权限,直接获取进程信息  所以else里的方法基本无效
     *
     * @return
     */
    private boolean isUseGranted() {
        boolean granted = false;
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                AppOpsManager appOps = (AppOpsManager) this
                        .getSystemService(Context.APP_OPS_SERVICE);
                int mode = -1;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    mode = appOps.checkOpNoThrow("android:get_usage_stats",
                            android.os.Process.myUid(), this.getPackageName());
                }
                granted = mode == AppOpsManager.MODE_ALLOWED;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            PackageManager packageManager = getPackageManager();
            int i = packageManager.checkPermission(Settings.ACTION_USAGE_ACCESS_SETTINGS, this.getPackageName());
            granted = PackageManager.PERMISSION_GRANTED == i;
        }
        return granted;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                    //关闭开关状态
                    floatPowerHave = false;
                    floatPower.setChecked(false);
                } else {
                    floatPowerHave = true;
                    Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
                    LibConfig.getInstance().putBoolean(true, SpConstans.FLOAT_POWER_SWITCH);
                    //有悬浮窗权限开启服务绑定 绑定权限
                    Intent intent = new Intent(FloatSettingActivity.this, FloatWindowService.class);
                    startService(intent);
                }
            }
        }
        if (requestCode == _USAGE_ACCESS_CODE) {
            if (isUseGranted()) {
                storagePowerHave = true;
                Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
                LibConfig.getInstance().putBoolean(true, SpConstans.STORAGE_POWER_SWITCH);
            } else {
                Toast.makeText(FloatSettingActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
                //关闭开关状态
                storagePowerHave = false;
                storagePower.setChecked(false);
            }

        }

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        //内存权限获取
        if (id == R.id.requst_power) {
            if (isChecked) {
                //开关状态人为改变,需要去申请权限
                if (!storagePowerHave) {
                    handlerStoragePower();
                }

            } else if (!isChecked) {
                LibConfig.getInstance().putBoolean(false, SpConstans.STORAGE_POWER_SWITCH);
            }
        }
        //悬浮窗权限获取
        if (id == R.id.requst_float_view) {
            if (isChecked) {
                //开关人为改变  需要从新申请权限
                if (!floatPowerHave) {
                    handlerFloatPower();
                } else {
                    LibConfig.getInstance().putBoolean(true, SpConstans.FLOAT_POWER_SWITCH);
                    Intent intent = new Intent(FloatSettingActivity.this, FloatWindowService.class);
                    startService(intent);
                }

            } else {
                LibConfig.getInstance().putBoolean(false, SpConstans.FLOAT_POWER_SWITCH);
            }
        }
        //后台播放音乐
        if (id == R.id.play_music) {
            if (isChecked) {
                startService(new Intent(this, VoicePlayService.class));
                LibConfig.getInstance().putBoolean(true, SpConstans.VOICE_MUSIC_SWITCH_STATE);
            } else {
                LibConfig.getInstance().putBoolean(false, SpConstans.VOICE_MUSIC_SWITCH_STATE);
            }

        }
    }

    /**
     * 申请权限查看内存信息
     */
    public void handlerStoragePower() {

        if (isUseGranted()) {
            storagePowerHave = true;
            LibConfig.getInstance().putBoolean(true, SpConstans.STORAGE_POWER_SWITCH);
            Toast.makeText(FloatSettingActivity.this, "已经申请权限", Toast.LENGTH_SHORT).show();
        } else {
            //开启应用授权界面
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, _USAGE_ACCESS_CODE);
        }
    }

    /**
     * 悬浮窗权限申请
     */
    public void handlerFloatPower() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                floatPowerHave = true;
                LibConfig.getInstance().putBoolean(true, SpConstans.FLOAT_POWER_SWITCH);
                Toast.makeText(FloatSettingActivity.this, "权限申请成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FloatSettingActivity.this, FloatWindowService.class);
                startService(intent);
            } else {
                //没有悬浮窗权限m,去开启悬浮窗权限
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            floatPowerHave = true;
            LibConfig.getInstance().putBoolean(true, SpConstans.FLOAT_POWER_SWITCH);
            Intent intent = new Intent(FloatSettingActivity.this, FloatWindowService.class);
            startService(intent);
        }
    }

    protected void setupToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.libColorWhite));
        toolbar.setNavigationIcon(R.drawable.ic_lib_back);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
