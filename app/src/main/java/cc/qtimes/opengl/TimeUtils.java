package cc.qtimes.opengl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by xutao0 on 2016/10/12.
 * 时间工具类
 */

public class TimeUtils {
    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    public final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年
    public final static String timeFormat = "yyyy-MM-ddHH:mm:ss";

    /**
     * @param @return 设定文件
     * @return Long 返回类型
     * @throws
     * @Title: getCurrentSystemTime
     * @Description: 获取当前系统时间
     */
    public static Long getCurrentSystemTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * 取出当前的日期格式yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentTimeSStr() {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(curCalendar.getTime());
    }

    /**
     * 取出当前的日期格式yyyyMMddHHmmss
     *
     * @return
     */
    public static String getCurrentTimeMSStr() {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSS");
        return df.format(curCalendar.getTime());
    }

    /**
     * 取出当前的日期格式yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTimeStr() {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(curCalendar.getTime());
    }

    /**
     * 取出当前的日期格式yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String getCurrentTimeMsStr() {
        Calendar curCalendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
        return df.format(curCalendar.getTime());
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(milliseconds));
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTimeYY(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(milliseconds));
    }


    /**
     * 返回文字描述的日期
     *
     * @param date
     * @return
     */
    public static String getTimeFormatText(long date) {
        if (date == 0) {
            return "";
        }
        date = date * 1000;
        long diff = new Date().getTime() - date;
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTimeHHMM(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(new Date(milliseconds));
    }

    /**
     * 将时间戳转成格式化时间
     *
     * @param milliseconds
     */
    public static String getFormatTime24(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
        return sdf.format(new Date(milliseconds));
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 截取Date(1418784200000-0000)的固定长度
     */
    public static String spliteTime(String dateStr) {
        String sequence = dateStr.replace("/Date(", "");
        return sequence.substring(0, 13).trim();
    }

    /**
     * 获取分秒格式化字符串
     *
     * @param duration
     * @return
     */
    public static String getFormatMiniteSecString(int duration) {
        int minutes = duration % (60 * 60) / 60;//分钟时长
        int seconds = duration % 60;//秒时长
        return String.format("%d'%d''", minutes, seconds);
    }

    /**
     * 格式化时间字符串
     * <p/>
     * 显示规则大于1天,显示天.  大于1小时,显示1=小时.   大于1分钟, 显示分钟
     * 其中,大于7天以上的均显示7天前
     *
     * @param time
     * @return
     */
    public static String getFormatTimeString(long time) {

        Long currentTime = new Date().getTime() / 1000;//获得当前时间
        Long diffTime = currentTime - time;//当前时间减去创建时间,得到时间差
        long diffDay = diffTime / (24 * 3600); //得到天数
        long diffHour = diffTime % (24 * 3600) / 3600; //得到小时数
        long diffMinute = diffTime % 3600 / 60; //得到分钟数

        String result = null;

        if (diffDay >= 1) {
            if (diffDay >= 7) {
                result = "7天前";
            } else {
                result = diffDay + "天前";
            }
        } else if (diffHour >= 1) {
            result = diffHour + "小时前";
        } else if (diffMinute >= 1) {
            result = diffMinute + "分钟前";
        } else {
            result = "";
        }
        return result;
    }

    /**
     * 获取当天0时的时间戳
     *
     * @return
     */
    public static Long getDayMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getCurrentSystemTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }


    /**
     * 将字符串时间转成时间戳
     *
     * @param time   "2013-06-01 13:24:16"
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Long getTimeMillis(String time, String format) {
        Calendar calendar = Calendar.getInstance();
        // 设置传入的时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        // 指定一个日期
        Date date = null;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 对 calendar 设置为 date 所定的日期
        calendar.setTime(date);
        return calendar.getTimeInMillis();
    }
}
