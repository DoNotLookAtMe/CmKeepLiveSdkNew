package com.chaomeng.cmkeeplivelib.floatview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.chaomeng.cmkeeplivelib.floatview.floatwindow.FloatWindowService;



/**
 * Author: SQSong
 * Date: 2018/10/15
 * Description:
 */
public class LibConfig {
    private Context mAppcontext;
    private String CHAO_MENG_PREFERENCE = "chao_meng_prefs";
    private SharedPreferences preferences;
    private SharedPreferences.Editor edit;


    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean debug) {
        isDebug = debug;
    }

    private boolean isDebug = true;

    public Context getmAppcontext() {
        return mAppcontext;
    }

    public LibConfig setmAppcontext(Context mAppcontext) {
        this.mAppcontext = mAppcontext;
        return Singleton.INSTANCE;
    }

    public void startKeepLife() {
        boolean storagePower = getBoolean(SpConstans.STORAGE_POWER_SWITCH, false);
        boolean floatPower = getBoolean(SpConstans.FLOAT_POWER_SWITCH, false);
        boolean musicSwitch = getBoolean(SpConstans.VOICE_MUSIC_SWITCH_STATE, false);
        if (musicSwitch) {
            mAppcontext.startService(new Intent(mAppcontext, VoicePlayService.class));
        }
        if (storagePower && floatPower) {
            Intent intent = new Intent(mAppcontext, FloatWindowService.class);
            mAppcontext.startService(intent);
        } else if (Build.VERSION.SDK_INT <= 23 && floatPower) {
            Intent intent = new Intent(mAppcontext, FloatWindowService.class);
            mAppcontext.startService(intent);
        }

    }

    private LibConfig() {
    }

    /**
     * 仅仅是为了单例
     */
    private static final class Singleton {
        private static final LibConfig INSTANCE = new LibConfig();
    }

    /**
     * 获取到libconfig对象
     *
     * @return
     */
    public static LibConfig getInstance() {

        return Singleton.INSTANCE;
    }

    public void putBoolean(boolean state, String key) {
        getSpEidter();
        edit.putBoolean(key, state);
        edit.apply();
    }

    public boolean getBoolean(String key, boolean defalt) {
        getSpEidter();
        return preferences.getBoolean(key, defalt);
    }

    public void getSpEidter() {
        if (preferences == null || edit == null) {
            preferences = mAppcontext.getSharedPreferences(CHAO_MENG_PREFERENCE, Context.MODE_PRIVATE);
            edit = preferences.edit();
        }
    }
}
