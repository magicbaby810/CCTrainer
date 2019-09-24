package com.sk.demo_common_base.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sk
 */
public class AppUtils {

    private static Pattern p = Pattern.compile("^[1][3,4,5,6,8,7,9][0-9]{9}$");

    /** 两次点击按钮之间的点击间隔不能少于1000毫秒 **/
    private static final int MIN_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;

    /**
     * 旋转bitmap
     * @param bitmap
     * @param angle
     * @return
     */
    public static Bitmap rotateImg(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 根据两个经纬度计算偏移角度
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getAngle(double lat1, double lng1, double lat2, double lng2) {
        double fLat = Math.PI * (lat1) / 180.0;
        double fLng = Math.PI * (lng1) / 180.0;
        double tLat = Math.PI * (lat2) / 180.0;
        double tLng = Math.PI * (lng2) / 180.0;

        double degree = (Math.atan2(Math.sin(tLng - fLng) * Math.cos(tLat), Math.cos(fLat) * Math.sin(tLat) - Math.sin(fLat) * Math.cos(tLat) * Math.cos(tLng - fLng))) * 180.0 / Math.PI;
        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 判断是不是wifi网络状态
     *
     * @param paramContext
     * @return
     */
    public static boolean isWifi(Context paramContext) {
        return "2".equals(getNetType(paramContext)[0]);
    }

    /**
     * 判断是不是2/3G网络状态
     *
     * @param paramContext
     * @return
     */
    public static boolean isMobile(Context paramContext) {
        return "1".equals(getNetType(paramContext)[0]);
    }

    /**
     * 网络是否可用
     *
     * @param paramContext
     * @return
     */
    public static boolean isNetAvailable(Context paramContext) {
        if ("1".equals(getNetType(paramContext)[0]) || "2".equals(getNetType(paramContext)[0])) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前网络状态 返回2代表wifi,1代表2G/3G
     *
     * @param paramContext
     * @return
     */
    public static String[] getNetType(Context paramContext) {
        String[] arrayOfString = {"Unknown", "Unknown"};
        PackageManager localPackageManager = paramContext.getPackageManager();
        if (localPackageManager.checkPermission(Manifest.permission.ACCESS_NETWORK_STATE, paramContext.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
            arrayOfString[0] = "Unknown";
            return arrayOfString;
        }

        ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (localConnectivityManager == null) {
            arrayOfString[0] = "Unknown";
            return arrayOfString;
        }

        NetworkInfo localNetworkInfo1 = localConnectivityManager.getNetworkInfo(1);
        if (localNetworkInfo1 != null && localNetworkInfo1.getState() == NetworkInfo.State.CONNECTED) {
            arrayOfString[0] = "2";
            return arrayOfString;
        }

        NetworkInfo localNetworkInfo2 = localConnectivityManager.getNetworkInfo(0);
        if (localNetworkInfo2 != null && localNetworkInfo2.getState() == NetworkInfo.State.CONNECTED) {
            arrayOfString[0] = "1";
            arrayOfString[1] = localNetworkInfo2.getSubtypeName();
            return arrayOfString;
        }

        return arrayOfString;
    }


    /**
     * 获取虚拟导航键的高度
     * @param context
     * @return
     */
    public static int getVirtualBarHeight(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vh;
    }


    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        // 验证手机号
        if (!validateString(str)) { return false; }
        Matcher m = null;
        boolean b = false;
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static boolean validateString(String... text) {
        for (int i = 0; i < text.length; i++) {
            if (text[i] == null || "".equals(text[i])) {
                return false;
            }
        }
        return true;
    }


    // 判断系统位置服务是否打开
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static boolean checkPermissions(final Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }






    /**
     * 隐藏虚拟键盘
     */
    public static void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            //imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏虚拟键盘
     */
    public static void hideKeyboard2(Context context) {
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&((Activity)context).getCurrentFocus()!=null){
            if (((Activity)context).getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }

    }
    /**
     * 隐藏虚拟键盘
     */
    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }



    /**
     * 字符串判空
     * @param str
     * @return
     */
    public static String checkNull(String str) {
        return TextUtils.isEmpty(str) ? "" : str;
    }

    /**
     * 字符串 int判空
     * @param str
     * @return
     */
    public static int checkIntegerNull(String str) {
        return TextUtils.isEmpty(str) ? 0 : Integer.parseInt(str);
    }

    /**
     * 字符串 double判空
     * @param str
     * @return
     */
    public static Double checkDoubleNull(String str) {
        return TextUtils.isEmpty(str) ? 0 : Double.parseDouble(str);
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static Float mul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).floatValue();
    }

    /**
     * 提供精确的乘法运算
     *
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static int mulInt(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).intValue();
    }


    //注册广播

    /**
     * 两个Float数相加
     * @param v1
     * @param v2
     * @return Float
     */
    public static Float add(Float v1,Float v2){
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.add(b2).floatValue();
    }

    /**
     * 金额相加
     * @param v1
     * @param v2
     * @return String
     */
    public static String add(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).toString();
    }

    /**
     * 金额相减
     * @param v1
     * @param v2
     * @return String
     */
    public static String minus(String v1, String v2){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).toString();
    }

    /**
     * 金额相除
     * @param v1
     * @param v2
     * @return String
     */
    public static String divide(String v1, String v2, int pre){
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, pre, BigDecimal.ROUND_HALF_UP).toString();
    }

    /**
     * 金额相除
     * @param v1
     * @param v2
     * @return String
     */
    public static String divide(String v1, String v2){
        return divide(v1, v2, 2);
    }

    /**
     * @desc 分转化为元，字符串操作，防止丢失精度
     * @param money
     * @return
     */
    public static String getMoney(Object money){
        String returnMoney="";
        if(money==null) {
            return returnMoney;
        }
        String moneyStr=money.toString();
        if(moneyStr.length()==1){
            returnMoney= "0.0"+money;
        }else if(moneyStr.length()==2) {
            returnMoney= "0."+money;
        }else if(moneyStr.length()>2){
            returnMoney=moneyStr.substring(0,moneyStr.length()-2)+"."+moneyStr.substring(moneyStr.length()-2,moneyStr.length());
        }

        return returnMoney;
    }

    /**
     * @desc 分转化为元，字符串操作，防止丢失精度,并把尾部0移除
     * @param money
     * @return
     */
    public static String getMoneyCutZero(Object money){
        String returnMoney="";
        if(money==null) {
            return returnMoney;
        }
        String moneyStr=money.toString();
        if(moneyStr.length()==1){
            returnMoney= "0.0"+money;
        }else if(moneyStr.length()==2) {

            // 去除尾部保留小数留下的0情况
            if (moneyStr.substring(moneyStr.length() - 1).equals("0")) {
                money = moneyStr.substring(0, moneyStr.length() - 1);
            }

            returnMoney= "0."+money;

        }else if(moneyStr.length()>2){

            // 去除尾部保留小数留下的 .00 .20 .30 情况,把后面的0去掉
            if (moneyStr.substring(moneyStr.length() - 2).equals("00")) {
                returnMoney = moneyStr.substring(0, moneyStr.length() - 2);
            } else if (moneyStr.substring(moneyStr.length() - 1).equals("0")) {
                returnMoney=moneyStr.substring(0,moneyStr.length()-2)+"."+moneyStr.substring(moneyStr.length()-2, moneyStr.length() - 1);
            } else {
                returnMoney=moneyStr.substring(0,moneyStr.length()-2)+"."+moneyStr.substring(moneyStr.length()-2);
            }
        }

        return returnMoney;
    }


    /**
     * @desc 精确到小数点两位
     */
    public static String intRoundingTwo(int one) {

        DecimalFormat df = new DecimalFormat("0.00");
        String two = df.format((float) one / 1000);
        return two;
    }

    /**
     * 检查小数点后几位
     * @return
     */
    public static int checkRoundingLen(String str) {
        if (!TextUtils.isEmpty(str) && str.contains(".") && !str.startsWith(".")) {
            return str.substring(str.indexOf(".")).length() - 1;
        }
        return 0;
    }


    /**
     * 版本名
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 版本号
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * 获取应用包信息
     * @param context
     * @return
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 获取埋点需要的屏幕分辨率
     * @return
     */
    public static String getDisplayMetricsStr(Context context) {
        DisplayMetrics displayMetrics = getDpi(context);
        String width = String.valueOf(displayMetrics.widthPixels);
        String height = String.valueOf(displayMetrics.heightPixels);
        return width + "*" + height;
    }

    public static String getIMEI(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                ? ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()
                : "";
    }

    /**
     *  Pseudo-Unique ID, 这个在任何Android手机中都有效 解决手机中IMEI获取不到情况，兼容所有手机
     */
    public static String getIMEINew(Context context) {
        //we make this look like a valid IMEI
        String m_szDevIDShort = "35" +
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
        return m_szDevIDShort;
    }

    /**
     * 邮箱验证
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    /**
     * @desc :防止频繁触(间隔1S)
     * @return
     */
    public static boolean isFastClick() {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * @desc :防止频繁触(根据参数动态控制触发间隔时间)
     * @return
     */
    public static boolean isFastClickContral(int countTime) {
        boolean flag = true;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= countTime) {
            flag = false;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     */
    public static DisplayMetrics getDpi(Context context) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            //dpi=displayMetrics.heightPixels;
        }catch(Exception e){
            e.printStackTrace();
        }
        return displayMetrics;
    }


    /**
     * 按图片的高度设置ratingbar高度
     * @param context
     * @param resourceId
     * @param ratingBar
     */
    public static void setRatingBarHeightByOriginImgHeight(Context context, int resourceId, RatingBar ratingBar) {
        try {
            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), resourceId);

            //将获取的图片高度设置给RatingBar
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) ratingBar.getLayoutParams();
            lp.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            lp.height = bmp.getHeight();
            ratingBar.setLayoutParams(lp);

            if (null != bmp && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前IP
     * @return
     */
    public static String getLocalIpAddress()
    {
        try
        {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
            {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
                {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException ex)
        {
            Log.e("Wifi IpAddress", ex.toString());
        }
        return null;
    }


    public static String[] getPhoneContacts(Context context, Uri uri){
        String[] contact = new String[2];
        //得到ContentResolver对象**
        ContentResolver cr = context.getContentResolver();
        //取得电话本中开始一项的光标**
        Cursor cursor = cr.query(uri,null,null,null,null);
        if (cursor != null) {
            cursor.moveToFirst();
            //取得联系人姓名**
            int nameFieldColumnIndex=cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            //取得电话号码**
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            if (phone != null) {
                phone.moveToFirst();
                contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
            if (null != phone){
                phone.close();
            }
            cursor.close();
        }
        else {
            return null;
        }
        return contact;
    }



    /**
     * 精确返回保留1位
     * @param price
     * @return
     */
    public static String  getfoloatSavaTwo(float price){
        //构造方法的字符格式保留1位.
        DecimalFormat decimalFormat=new DecimalFormat(".0");
        //format 返回的是字符串
        String p=decimalFormat.format(price);
        return  p;
    }


    /**
     * 判断当前手机是否被root
     *
     * @return
     */
    public synchronized static boolean checkRoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            return exitValue == 0;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 跳转到应用设置
     * @param context
     */
    public static void toSelfSetting(Context context){
        Intent mIntent=new Intent();
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            mIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            mIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            mIntent.setAction(Intent.ACTION_VIEW);
            mIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            mIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(mIntent);

    }



    /**
     * view 转bitmap
     * @param view
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.TRANSPARENT);
        }
        view.draw(canvas);
        return bitmap;
    }


    /**
     * 检测网络连通性（是否能访问网络）
     * 例如连上wifi 但是路由器没网
     */
    public static boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 1 223.5.5.5");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    //获取是否存在NavigationBar
    public static boolean checkHuaWeiDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        try {
            Resources rs = context.getResources();
            int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
            if (id > 0) {
                hasNavigationBar = rs.getBoolean(id);
            }
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }


    //NavigationBar状态是否是显示
    public static boolean isNavigationBarShow(Context context) {
        Activity mContext = (Activity) context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = mContext.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }


    /**
     * 虚拟按键是否可见
     *
     * @param activity activity
     * @return
     */
    public static boolean isNavigationBarShow(Activity activity) {

        //虚拟键的view,为空或者不可见时是隐藏状态
        View view = activity.findViewById(android.R.id.navigationBarBackground);
        if (view == null) {
            return false;
        }
        int visible = view.getVisibility();
        if (visible == View.GONE || visible == View.INVISIBLE) {
            return false;
        } else {
            return true;
        }
    }




    /**
     * STT 需要的权限
     */
    public static void requestPermissions(Activity constant) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(constant, Manifest.permission.RECORD_AUDIO);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(constant, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO}, 0x0010);
                }

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(constant, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * STT 判断是否有语音权限
     */
    public static Boolean getPermissionsVoice(Context context){
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        boolean isRequest=false;
        if (permission != PackageManager.PERMISSION_GRANTED) {
            isRequest=true;
        }else{
            isRequest=false;
        }
        return isRequest;
    }


    static float sNoncompatDensity = 0, sNoncompatScaledDensity = 0;
    public static void setCustomDensity(@NonNull Activity activity, @NonNull Application application) {

        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();

        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScaledDensity = appDisplayMetrics.scaledDensity;

            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(@NonNull Configuration configuration) {
                    if (configuration != null && configuration.fontScale > 0) {
                        sNoncompatScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        final float targetDensity = appDisplayMetrics.widthPixels / 360;
        final float targetScaledDensity = targetDensity * (sNoncompatScaledDensity / sNoncompatDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }



    /**
     * 获取手机分辨率
     */
    public static String getAPPDisplay(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.i("AppUtils-",screenWidth+"x"+screenHeight);
        return screenWidth+"x"+screenHeight;
    }


}
