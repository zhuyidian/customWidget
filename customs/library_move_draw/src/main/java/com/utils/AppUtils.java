package com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * App（Android 工具类）
 */
public final class AppUtils {

    /**
     * Don't let anyone instantiate this class.
     */
    private AppUtils() {
        throw new Error("Do not need instantiate!");
    }

    // == ----------------------------------------- ==

    /**
     * 通过上下文获取 WindowManager
     *
     * @param mContext
     * @return
     */
    public static WindowManager getWindowManager(Context mContext) {
        try {
            return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 通过上下文 获取Activity
     *
     * @param mContext
     * @return
     */
    public static Activity getActivity(Context mContext) {
        try {
            Activity activity = (Activity) mContext;
            return activity;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 通过上下文获取 DisplayMetrics (获取关于显示的通用信息，如显示大小，分辨率和字体)
     *
     * @param mContext
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context mContext) {
        try {
            WindowManager wManager = getWindowManager(mContext);
            if (wManager != null) {
                DisplayMetrics dMetrics = new DisplayMetrics();
                wManager.getDefaultDisplay().getMetrics(dMetrics);
                return dMetrics;
            }
        } catch (Exception e) {
        }
        return null;
    }

    // == ----------------------------------------- ==

    /**
     * 获取app版本信息
     *
     * @param mContext
     * @return 0 = versionName , 1 = versionCode
     */
    public static String[] getAppVersion(Context mContext) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";

                return new String[]{versionName, versionCode};
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取app版本号
     *
     * @param mContext 上下文
     * @return 当前版本Code
     */
    public static int getVerCode(Context mContext) {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                return pi.versionCode;
            }
        } catch (Exception e) {
        }
        return -1;
    }

    // 获取当前软件的版本号
    public static String getVersionName(Context mContext) {
        // 获取packagemanager的
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是您当前类的包名�?代表是获取版本信�?
        PackageInfo packInfo;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            version = packInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;

    }

    // == ----------------------------------------- ==

    /**
     * 对内设置指定语言 (app 多语言,单独改变app语言)
     *
     * @param mContext
     * @param locale
     */
    public static void setLanguage(Context mContext, Locale locale) {
        try {
            // 获得res资源对象
            Resources resources = mContext.getResources();
            // 获得设置对象
            Configuration config = resources.getConfiguration();
            // 获得屏幕参数：主要是分辨率，像素等。
            DisplayMetrics dm = resources.getDisplayMetrics();
            // 语言
            config.locale = locale;
            // 更新语言
            resources.updateConfiguration(config, dm);
        } catch (Exception e) {
        }
    }

    /**
     * 重启app
     *
     * @param mContext
     */
    public static void restartApplication(Context mContext) {
        try {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        } catch (Exception e) {
        }
    }

    // == ----------------------------------------- ==


    /**
     * 获取设备信息
     *
     * @param dInfoMaps 传入设备信息传出HashMap
     */
    public static void getDeviceInfo(HashMap<String, String> dInfoMaps) {
        // 获取设备信息类的所有申明的字段,即包括public、private和proteced， 但是不包括父类的申明字段。
        Field[] fields = Build.class.getDeclaredFields();
        // 遍历字段
        for (Field field : fields) {
            try {
                // 取消java的权限控制检查
                field.setAccessible(true);
                // 获取类型对应字段的数据，并保存
                dInfoMaps.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

    /**
     * 处理设备信息
     *
     * @param dInfoMaps 设备信息
     * @param eHint     错误提示，如获取设备信息失败
     */
    public static String handleDeviceInfo(HashMap<String, String> dInfoMaps, String eHint) {
        try {
            // 初始化StringBuilder，拼接字符串
            StringBuilder sBuilder = new StringBuilder();
            // 获取设备信息
            Iterator<Map.Entry<String, String>> mapIter = dInfoMaps.entrySet().iterator();
            // 遍历设备信息
            while (mapIter.hasNext()) {
                // 获取对应的key-value
                Map.Entry<String, String> rnEntry = (Map.Entry<String, String>) mapIter.next();
                String rnKey = (String) rnEntry.getKey(); // key
                String rnValue = (String) rnEntry.getValue(); // value
                // 保存设备信息
                sBuilder.append(rnKey + " = " + rnValue + "\r\n");
            }
            return sBuilder.toString();
        } catch (Exception e) {
        }
        return eHint;
    }

    /**
     * 获取错误信息(有换行)
     *
     * @param eHint 获取失败提示
     * @param ex    错误信息
     * @return
     */
    public static String getThrowableNewLinesMsg(String eHint, Throwable ex) {
        try {
            if (ex != null) {
                // 初始化Writer，PrintWriter打印流
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                // 获取错误栈信息
                StackTraceElement[] stElement = ex.getStackTrace();
                // 标题，提示属于什么异常
                printWriter.append(ex.toString() + "\r\n");
                // 遍历错误栈信息,并且进行换行,缩进
                for (StackTraceElement st : stElement) {
                    printWriter.append("\tat " + st.toString());
                    printWriter.append("\r\n");
                }
                // 关闭流
                printWriter.close();
                return writer.toString();
            }
        } catch (Exception e) {
        }
        return eHint;
    }

    /**
     * 获取屏幕信息
     *
     * @param mContext 上下文
     * @return
     */
    public static int[] getScreen(Context mContext) {
        int[] screen = null;
        try {
            WindowManager wManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wManager.getDefaultDisplay();
            // 获取宽度高度
            int width = display.getWidth();
            ;
            int height = display.getHeight();
            if (width != 0 && height != 0) {
                screen = new int[]{width, height};
            }
        } catch (Exception e) {
            screen = null;
        }
        return screen;
    }

    /**
     * 获取屏幕宽度（对外公布）
     *
     * @param mContext 上下文
     * @return
     */
    public static int getWidth(Context mContext) {
        int[] screen = getScreen(mContext);
        if (screen != null) {
            return screen[0];
        }
        return 0;
    }

    /**
     * 获取屏幕高度（对外公布）
     *
     * @param mContext 上下文
     * @return
     */
    public static int getHeight(Context mContext) {
        int[] screen = getScreen(mContext);
        if (screen != null) {
            return screen[1];
        }
        return 0;
    }

    /**
     * 获得状态栏的高度(无关 android:theme 获取状态栏高度)
     *
     * @param mContext
     * @return
     */
    public static int getStatusHeight(Context mContext) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            return mContext.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
        }
        return -1;
    }


    // == ----------------------------------------- ==

    public static boolean checkApkExist(Context mContext, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = mContext.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
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

    public static boolean getAppSystemModel() {
        if (Build.MODEL.contains("MX4")
                || Build.MODEL.contains("vivo")
                || Build.MODEL.contains("MX3")
                || Build.MODEL.contains("M355")
                || Build.MODEL.contains("EVA-AL00")
                || Build.MODEL.contains("TIT-AL00")
                || Build.MODEL.contains("SM-C7000")
                || Build.MODEL.contains("STF-AL")
                || Build.MODEL.contains("LG-L70")
                || Build.MODEL.contains("VKY-AL0")
                || Build.MODEL.contains("AL00")
                || Build.MODEL.contains("E5823")
                || Build.MODEL.contains("MIX 2")
                || Build.MODEL.contains("SM-G9")
                || Build.MODEL.contains("SM-A")
                || Build.MODEL.contains("NRD90M.G950FXXU1AQI7")
                || Build.MODEL.contains("E2")
                || Build.MODEL.contains("HTC M8t")
                || Build.MODEL.contains("HTC U11 plus")
                || Build.MODEL.contains("OPPO R11")
                || Build.MODEL.contains("BKL-AL20")
                || Build.MODEL.contains("COL-AL10")
                || Build.MODEL.contains("PIXEL")
                || Build.MODEL.contains("ASUS_X00QD")
                || Build.MODEL.contains("OS105")
                || Build.MODEL.contains("Galaxy J7 Pro")
                || Build.MODEL.contains("ONEPLUS A5010")
                || Build.MODEL.contains("ZTE BV0800")
                || Build.MODEL.contains("ZTE BV0850")
                || Build.MODEL.contains("XT1773")
                || Build.MODEL.contains("XT168")
                || Build.MODEL.contains("Moto G")
                || Build.MODEL.contains("SM-J730")
                || Build.MODEL.contains("CMR-AL09")
                || Build.MODEL.contains("COL-AL10")
                || Build.MODEL.contains("TA-1005")
                || Build.MODEL.contains("TA-1055")
                || Build.MODEL.contains("TA-1062")
                || Build.MODEL.contains("TA-10")
                || Build.MODEL.toLowerCase().contains("redmi note 5")
                || Build.MODEL.contains("Nexus 6P")
                || Build.MODEL.contains("Aquaris")
                || Build.MODEL.toUpperCase().contains("ONEPLUS")) {
            return true;
        } else {
            return false;
        }

    }

    public static String get4GOperator(String operator) {
        if(TextUtils.isEmpty(operator)){
            return "";
        } else if (operator.equals("1")) {
            return "CMCC";
        } else if (operator.equals("2")) {
            return "CUCC";
        } else if (operator.equals("3")) {
            return "CTCC";
        } else {
            return "0";
        }
    }


    public static String get4GOperatorStringToInt(String operator) {
        if (operator.equals("CMCC")) {
            return "1";
        } else if (operator.equals("CUCC")) {
            return "2";
        } else if (operator.equals("CTCC")) {
            return "3";
        } else {
            return "";
        }
    }


}
