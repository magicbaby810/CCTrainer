package com.sk.demo_common_base;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.UMShareAPI;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.jpush.android.api.JPushInterface;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author sk
 */
public class UserApplication extends MultiDexApplication {

    private static UserApplication userApplication;
    private Context mContext;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        fixFinalizeTimeoutExce();
    }

    @Override
    public void onCreate() {
        checkXposed();
        super.onCreate();
        initSystem();
    }

    private void initSystem() {
        userApplication = this;
        mContext = getApplicationContext();
        // 初始化通知channel 兼容8.0以上的系统
        initChannel();
        //注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，
        //也需要在App代码中调用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，UMConfigure.init调用中appkey和channel参数请置为null）。
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, Constant.UMNEG_APP_KEY);

        Bugly.init(getApplicationContext(), Constant.BUGLY_APP_ID, BuildConfig.DEBUG);
        //设置Wifi下自动下载
        Beta.autoDownloadOnWifi = true;

        // 友盟初始化
        UMShareAPI.get(this);


        //初始化极光推送
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(this);

        // The exception could not be delivered to the consumer because it has already canceled/disposed the flow or the exception has nowhere to go to begin with.
        // RxJava在处理exception时订阅已经被解除，无法找到消费者去处理exception，exception无处可去，线程大量积压，栈内存无法释放
        // 而华为部分手机线程数阈值较低，很容易就导致线程爆棚，导致栈内存溢出，pthread_create (1040KB stack) failed: Out of memory
        // 从bugly可以看到此类问题都出现在华为固定的几款机型上
        RxJavaPlugins.setErrorHandler(t -> Log.e("CC", "Unhandled Rx error " + (null != t ?  ("-->> " + t.getMessage()) : "")));

    }

    /**
     * 禁用xposed
     * 防止用户通过xposed做hook操作
     */
    private void checkXposed() {
        try {

            Field v01 = ClassLoader.getSystemClassLoader().loadClass("de.robv.android.xposed.XposedBridge").getDeclaredField("disableHooks");
            // 如果加载类失败 则表示当前环境没有xposed
            if (v01 != null) {
                // 禁用xposed
                v01.setAccessible(true);
                v01.set(null, Boolean.valueOf(true));
            }
        } catch (Exception e) {}
    }


    /**
     * 解决oppo4.4到6.0系统为了流畅，熄屏、切后台后疯狂清内存，导致频繁GC
     */
    public static void fixFinalizeTimeoutExce() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");

            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);

            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);

            method.invoke(field.get(null));
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Application实例
     *
     * @return UserApplication
     */
    public static UserApplication getInstance() {
        return userApplication;
    }

    public Context getContext() {
        if (mContext == null) {
            mContext = getApplicationContext();
        }
        return mContext;
    }


    // 初始化通知channel
    public static final String CHANNEL_ID_KEY = "com.sk.cctrainer";
    public void initChannel() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && null != nm) {
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID_KEY, "欧了通知", NotificationManager.IMPORTANCE_DEFAULT));
        }
    }


}
