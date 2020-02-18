package cc.qtimes.opengl;

import android.text.TextUtils;
import android.util.Log;


/**
 * 一个较完善的logUtil，可以打印log所在
 * {类:方法:行数}
 * Created by lt on 2016/6/7.
 */
public class PluLog
{
    public static String tagPrefix = "PluLog";//log前缀
    public static boolean debug = true;
    public static int INDEX = 5;

    public static void d(Object o)
    {
        logger("d", o, INDEX);
    }

    public static void e(Object o)
    {
        logger("e", o, INDEX);
    }

    public static void i(Object o)
    {
        logger("i", o, INDEX);
    }

    public static void w(Object o)
    {
        logger("w", o, INDEX);
    }

    public static void d(Object o, int i)
    {
        logger("d", o, i);
    }

    public static void e(Object o, int i)
    {
        logger("e", o, i);
    }

    public static void i(Object o, int i)
    {
        logger("i", o, i);
    }

    public static void w(Object o, int i)
    {
        logger("w", o, i);
    }

    /**
     * @param type logger级别
     * @param o    logger内容
     */
    private static void logger(String type, Object o, int index)
    {
        if (!debug)
        {
            return;
        }
        String msg = o + "";
        String tag = getTag(getCallerStackTraceElement(index));
        switch (type)
        {
            case "i":
                Log.i(tag, msg);
                break;
            case "d":
                Log.d(tag, msg);
                break;
            case "e":
                Log.e(tag, msg);
                break;
            case "w":
                Log.w(tag, msg);
                break;
        }
    }


    private static String getTag(StackTraceElement element)
    {
        try
        {
            String tag = "%s.%s(Line:%d)"; // 占位符
            String callerClazzName = element.getClassName(); // 获取到类名
            callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
            tag = String.format(tag, callerClazzName, element.getMethodName(), element.getLineNumber()); // 替换
            tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":" + tag;
            return tag;
        } catch (Exception e)
        {
            return tagPrefix;
        }
    }

    /**
     * 获取线程状态
     *
     * @return
     */
    private static StackTraceElement getCallerStackTraceElement(int index)
    {
        return Thread.currentThread().getStackTrace()[index];
    }
}