package com.wiget.timebar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import vstc.ORLLO.bean.results.CloudTimeBean;
import vstc.ORLLO.mvp.helper.CTimeHelper;
import vstc.ORLLO.utilss.LogTools;
import vstc.ORLLO.widgets.recordsliderview.bean.RecordTimeBean;

/**
 * Created by Administrator on 2017/3/7.
 */

public class TimeStringUtils {
    public static RecordTimeBean time2string(String datename, String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = datename.replace(":", " ") + ":" + time.substring(0, 2) + ":" + time.substring(2, 4);
        Date date = null;
        try {
            date = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String sencod = time.substring(5, time.length());
        return new RecordTimeBean(date.getTime(), date.getTime() + 1000 * Integer.valueOf(sencod));
    }

    public static CloudTimeBean CloudTimeBean_time2string(String uid, String datename, String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetime = datename.replace(":", " ") + ":" + time.substring(0, 2) + ":" + time.substring(2, 4);
        Date date = null;
        try {
            date = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String sencod = time.substring(5, time.length());
        long sencodTime = date.getTime() + 1000 * Integer.valueOf(sencod);
        return new CloudTimeBean(datetime, getDateFormat(sencodTime), date.getTime(), sencodTime,uid);
    }

    public static long time2string(String datename) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(datename);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date==null){
            return 0;
        }
        return date.getTime();
    }

    public static int isRecord(List<RecordAlarmTimeSegment> list, long currentTime) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStartTimeInMillisecond() <= currentTime && list.get(i).getEndTimeInMillisecond() >= currentTime) {
                return i;
            }
        }
        return 10000;
    }



    public static String getDateFormat(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time);
    }


    public static String getDataString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time).replace(":", "_").replace(" ", ":") + "_10";
    }

    public static String getDataString(RecordAlarmTimeSegment temptime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startT=0;
        if (temptime.getTempStartTimeMillisecond()!=0){
            startT=temptime.getTempStartTimeMillisecond();
        }else {
            startT=temptime.getStartTimeInMillisecond();
        }
        long endT=temptime.getEndTimeInMillisecond();
        long temp=(endT-startT) / 1000;
        return format.format(startT- CTimeHelper.GreemTime).replace(":", "_").replace(" ", ":")
                + "_" +(temp>=10?temp+"":"0"+temp) ;
    }


    public static String toJSONArray(List<String> stringlist) {
        JSONArray array = new JSONArray(stringlist);
        return array.toString();
    }

    public static ArrayList<String> get7DayList() {
        String sencodtime = getlong2StringDate(System.currentTimeMillis());
        ArrayList<String> list = new ArrayList<String>();
        list.add(sencodtime + ":00");
        for (int i = 0; i < 6; i++) {
            sencodtime = getSpecifiedDayBefore(sencodtime);
            list.add(sencodtime + ":00");
        }
        return list;
    }

    public static long string2longDate(String string) {
        return Long.parseLong(string);
    }

    public static String getlong2StringDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time).split(" ")[0];
    }

    public static String getlong2StringTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(time).split(" ")[1];
    }

    public static String getSpecifiedDayBefore(String specifiedDay) {
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = new SimpleDateFormat("yy-MM-dd").parse(specifiedDay);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);

        String dayBefore = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        return dayBefore;
    }

    public static String getDownLoadKey(String pathname) {
        return "VSTA004952VTDSE_" + getDataString(Long.valueOf(pathname.split("-")[0]).longValue());
    }

    public static String getPathName(String params) {
        String date = params.replace("VSTA004952VTDSE_", "").replace(":", " ").replace("_", ":");
        date.substring(0, date.length() - 2);
        long time = time2string(date.substring(0, date.length() - 3));
        return time + "-" + (time + 10000);
    }

    //时分秒转换
    public static String getHMS_String(long time) {
        // time 毫秒数
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
        return formatter.format(time);
        // 可得hms 的值为  00：05：00. 即0时5分0秒。
    }

    public static long getHMS_long(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse("0000-00-00 " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }


    public static List<String> getJSONObjectKey(String json) {
        List<String> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                list.add(iterator.next().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getDateString(String string) {
        return string.substring(0, 4) + "-" + string.substring(4, 6) + "-" + string.substring(6, 8);
    }

    public static String getDateString2Sceond(String time) {

        if (time == null) {
            return "";
        }

        return time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8) +
                " " + time.substring(8, 10) + ":" + time.substring(10, 12) + ":" + time.substring(12, 14);

    }


    public static String TimeStamp2Date(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new java.util.Date(timestamp));
        return date;
    }

    public static String getDate(String timestampString) {
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestampString));
        return date;
    }
    /**
     * 获取上个月的月份 201706
     */
    public static String getthisMonth(String time) {
        int YearInt = Integer.parseInt(time.substring(0, 4));
        String YearStr;
        String MonthStr = String.valueOf(Integer.parseInt(time.substring(4, 6)) );
        if (MonthStr.equals("00") || MonthStr.equals("0")) {
            YearStr = String.valueOf(YearInt - 1);
        } else {
            YearStr = String.valueOf(YearInt);
        }
        if (MonthStr.equals("00") || MonthStr.equals("0")) {
            MonthStr = "12";
        }
        if (MonthStr.length() == 1) {
            MonthStr = "0" + MonthStr;
        }
        return YearStr + MonthStr;
    }

    /**
     * 获取上个月的月份 201706
     */
    public static String getlastMonth(String time) {
        int YearInt = Integer.parseInt(time.substring(0, 4));
        String YearStr;
        String MonthStr = String.valueOf(Integer.parseInt(time.substring(4, 6)) - 1);

        if (MonthStr.equals("00") || MonthStr.equals("0")) {
            YearStr = String.valueOf(YearInt - 1);
        } else {
            YearStr = String.valueOf(YearInt);
        }

        if (MonthStr.equals("00") || MonthStr.equals("0")) {
            MonthStr = "12";
        }
        if (MonthStr.length() == 1) {
            MonthStr = "0" + MonthStr;
        }
        return YearStr + MonthStr;
    }

    //  判断字符串是否为数字
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static String get6FormDate(String time) {
        return time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8);
    }


    public static String get6FormMonth(String time) {
        return time.substring(0, 4) + "-" + time.substring(4, 6);
    }


    public static String getLastSecond(long time) {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        return format.format(time);
    }

    //想要获取日志的日期 格式：201703
    public static String getMonthString() {
        Calendar cal = Calendar.getInstance();
//        Date now = ca.getTime();
//        SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
//        return sf.format(now);
        int day = cal.get(Calendar.DATE);       //日
        int month = cal.get(Calendar.MONTH) + 1;//月
        int year = cal.get(Calendar.YEAR);      //年
        if (month>9){
            return year+""+month;
        }else {
            return year+"0"+month;
        }
    }

    //想要获取日志的日期 格式：20170303
    public static String getDayhString() {
        Calendar ca = Calendar.getInstance();
        Date now = ca.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        return sf.format(now);
    }

    //想要获取日志的日期 格式：20170303
    public static String getDayStringWithLine() {
        Calendar ca = Calendar.getInstance();
        Date now = ca.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(now);
    }

    //想要获取日志的日期 格式：20170303
    public static String getMonthStringWithLine() {
        Calendar ca = Calendar.getInstance();
        Date now = ca.getTime();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
        return sf.format(now);
    }

    public static ArrayList getMapTimeArr(String date, int timeSegment) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = timeSegment*6; i < (timeSegment+1)*6; i++) {
            String j = i + "";
            if (j.length() == 1) {
                j = "0" + j;
            }
            list.add(date + ":" + j);

        }
        for (int i = 0; i < list.size(); i++) {
            LogTools.print("小时集合="+list.get(i));
        }

        return list;

    }
    public static long TimeX_Value=0;
    private static long timeX=0;
    public static long getTimeX() {
            String charT = "";
            String nub = "";
            TimeZone tz = TimeZone.getDefault();//GMT+08:00
            String s = tz.getDisplayName(false, TimeZone.SHORT);
           // LogTools.print("getTimeX.TimeZone1:    "+s);
            if (!s.contains("GMT")) {
                s="GMT"+getGMT(s);
            }
          //   LogTools.print("getTimeX.TimeZone2:    "+s);
            s = s.replace("GMT", "");
            charT = s.substring(0, 1);
            nub = s.substring(1, 3);
            if (nub.startsWith("0")) {
                nub = nub.substring(1, 2);
            }
           // System.out.println("===" + charT + nub);
            timeX= Long.valueOf(nub);
            if (charT.equals("-")) {
                timeX=0-timeX;
            }
        return timeX*3600000;
    }
    public static String getTimeByHour2(long time, int hour){
        //time-1000*60*60;
        return getRightString(getDateFormat(time-1000*60*60*hour-CTimeHelper.GreemTime));
    }

    private static String getRightString(String string){
        //2017-06-20 17:46:34
        return string.split(" ")[0]+":"+string.split(" ")[1].substring(0,2);
    }

    private static String getGMT(String content){
        if (content.equals("KLT")){
            return "+14:00";
        }else if (content.equals("NZDT")){
            return "+13:00";
        }else if (content.equals("IDLW")){
            return "+12:00";
        } else if (content.equals("NZST")){
            return "+12:00";
        }  else if (content.equals("NZT")){
            return "+12:00";
        } else if (content.equals("AESST")){
            return "+11:00";
        }else if (content.equals("ACSST")){
            return "+10:30";
        }else if (content.equals("CADT")){
            return "+10:30";
        }else if (content.equals("SADT")){
            return "+10:30";
        }else if (content.equals("AEST")){
            return "+10:00";
        }else if (content.equals("EAST")){
            return "+10:00";
        }else if (content.equals("GST")){
            return "+10:00";
        }else if (content.equals("LIGT")){
            return "+10:00";
        }else if (content.equals("SAST")){
            return "+09:30";
        }else if (content.equals("CAST")){
            return "+09:30";
        }else if (content.equals("MHT")){
            return "+09:00";
        }else if (content.equals("JST")){
            return "+09:00";
        }else if (content.equals("KST")){
            return "+09:00";
        }else if (content.equals("AWST")){
            return "+08:00";
        }else if (content.equals("CCT")){
            return "+08:00";
        }else if (content.equals("WST")){
            return "+08:00";
        }else if (content.equals("JT")){
            return "+07:30";
        }else if (content.equals("ALMST")){
            return "+07:00";
        }else if (content.equals("CXT")){
            return "+07:00";
        }else if (content.equals("MMT")){
            return "+06:30";
        }else if (content.equals("ALMT")){
            return "+06:00";
        }else if (content.equals("IOT")){
            return "+05:00";
        }else if (content.equals("MVT")){
            return "+05:00";
        }else if (content.equals("TFT")){
            return "+05:00";
        }else if (content.equals("AFT")){
            return "+04:30 ";
        }else if (content.equals("EAST")){
            return "+04:00";
        }else if (content.equals("MUT")){
            return "+04:00";
        }else if (content.equals("RET")){
            return "+04:00";
        }else if (content.equals("SCT")){
            return "+04:00";
        }else if (content.equals("IRT")){
            return "+03:30";
        }else if (content.equals("IRT,IT")){
            return "+03:30";
        }else if (content.equals("EAT")){
            return "+03:00";
        }else if (content.equals("BT")){
            return "+03:00";
        }else if (content.equals("EETDST")){
            return "+03:00";
        }else if (content.equals("HMT")){
            return "+03:00";
        }else if (content.equals("BDST")){
            return "+02:00";
        }else if (content.equals("CEST")){
            return "+02:00";
        }else if (content.equals("CETDST")){
            return "+02:00";
        }else if (content.equals("EET")){
            return "+02:00";
        }else if (content.equals("FWT")){
            return "+02:00";
        }else if (content.equals("IST")){
            return "+02:00";
        }else if (content.equals("MEST")){
            return "+02:00";
        }else if (content.equals("METDST")){
            return "+02:00";
        }else if (content.equals("SST")){
            return "+02:00";
        }else if (content.equals("BST")){
            return "+01:00";
        }else if (content.equals("CET")){
            return "+01:00";
        }else if (content.equals("DNT")){
            return "+01:00";
        }else if (content.equals("FST")){
            return "+01:00";
        }else if (content.equals("MET")){
            return "+01:00";
        }else if (content.equals("NOR")){
            return "+01:00";
        }else if (content.equals("SWT")){
            return "+01:00";
        }else if (content.equals("WETDST")){
            return "+01:00";
        }else if (content.equals("GMT")){
            return "-0:00";
        }else if (content.equals("UT")){
            return "+00:00";
        }else if (content.equals("UTC")){
            return "+00:00";
        }else if (content.equals("ZULU")){
            return "+00:00";
        }else if (content.equals("WET")){
            return " +00:00";
        }else if (content.equals("WAT")){
            return "-01:00";
        }else if (content.equals("FNST")){
            return "-01:00";
        }else if (content.equals("FNT")){
            return "-02:00";
        }else if (content.equals("BRST")){
            return "-02:00";
        }else if (content.equals("NDT")){
            return "-02:30";
        }else if (content.equals("ADT")){
            return "-03:00";
        }else if (content.equals("BRT")){
            return "-03:00";
        }else if (content.equals("NST")){
            return "-03:30";
        }else if (content.equals("NFT")){
            return "-03:30";
        }else if (content.equals("NST,NFT")){
            return "-03:30";
        }else if (content.equals("AST")){
            return "-04:00";
        }else if (content.equals("ACST")){
            return "-04:00";
        }else if (content.equals("ACT")){
            return "-05:00";
        }else if (content.equals("EDT")){
            return "-04:00";
        }else if (content.equals("CDT")){
            return "-05:00";
        }else if (content.equals("EST")){
            return "-05:00";
        }else if (content.equals("CST")){
            return "-06:00";
        }else if (content.equals("MDT")){
            return "-06:00";
        }else if (content.equals("MST")){
            return "-07:00";
        }else if (content.equals("PDT")){
            return "-07:00";
        }else if (content.equals("AKDT")){
            return "-08:00";
        }else if (content.equals("PST")){
            return "-08:00";
        }else if (content.equals("YST")){
            return "-08:00";
        }else if (content.equals("AKST")){
            return "-09:00";
        }else if (content.equals("HDT")){
            return "-09:00";
        }else if (content.equals("MART")){
            return "-09:30";
        }else if (content.equals("AHST")){
            return "-10:00";
        }else if (content.equals("HST")){
            return "-10:00";
        }else if (content.equals("CAT")){
            return "-11:00";
        }else if (content.equals("NT")){
            return "-11:00";
        }else if (content.equals("IDLE")){
            return "-12:00";
        }
        return "+00:00";
    }
}

