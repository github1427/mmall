package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 上午9:54 2018/6/20
 * @ Description：日期转换工具类
 */
public class DateTimeUtil {

    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String str,String format){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(format);
        DateTime dateTime=dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String dateToString(Date date,String format){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(format);
    }

    public static Date strToDate(String str){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static String dateToString(Date date){
        if (date==null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }
}
