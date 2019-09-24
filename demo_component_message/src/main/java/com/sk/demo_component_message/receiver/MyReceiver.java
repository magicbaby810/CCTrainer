package com.sk.demo_component_message.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.billy.cc.core.component.CC;
import com.sk.demo_common_base.event.MessageEvent;
import com.sk.demo_common_base.utils.RxBus;
import com.sk.demo_component_message.MessageActivity;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * @author sk
 * @极光广播接收器
 *
 */
public class MyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();

        if (null != bundle) {

            int type = -1;
            String orderNo = "";
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                type =jsonObject.getInt("type");
                if(!TextUtils.isEmpty((CharSequence) jsonObject.opt("orderNo"))) {
                    orderNo = jsonObject.getString("orderNo");
                }
            } catch (Exception e) {
                Log.d(TAG,"异常"+e);
            }

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                Log.d(TAG, "JPush用户注册成功");

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "接受到推送下来的自定义消息");
                Log.e(TAG, bundle.getString(JPushInterface.EXTRA_MESSAGE));
                receivingNotification(context,bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                Log.d(TAG, "接受到推送下来的通知");
                //增加系统推送消息展示
                String msg=bundle.getString(JPushInterface.EXTRA_ALERT);
                if(!TextUtils.isEmpty(msg)) {
                    Log.d(TAG,"type="+type+"message="+msg);
                    RxBus.getDefault().post(new MessageEvent(msg));
                }
                receivingNotification(context,bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                Log.d(TAG, "用户点击打开了通知");

                openNotification(context, bundle, type, orderNo);

            } else {
                Log.d(TAG, "Unhandled intent - " + intent.getAction());
            }

        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void receivingNotification(Context context, Bundle bundle){
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }

    /**
     * 点击收到的通知跳转
     * @param context
     * @param bundle
     */
    private void openNotification(Context context, Bundle bundle, int type, String orderNo) {

        if (type == 1 || type == 2) {
            // 1：活动通知  2：系统通知
            Intent mIntent = new Intent(context, MessageActivity.class);
            mIntent.putExtra("type", Integer.valueOf(type));
            mIntent.putExtras(bundle);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(mIntent);
        } else if (type == 3) {
            // 3：订单通知
            CC.obtainBuilder("ComponentMain")
                    .setActionName("showMainActivity")
                    .addParam("order_no", orderNo)
                    .build()
                    .call();
        } else {
            CC.obtainBuilder("ComponentMain")
                    .setActionName("showMainActivity")
                    .build()
                    .call();
        }

    }
}