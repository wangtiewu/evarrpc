package com.eastelsoft.etos2.rpc.tool;

import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.sql.Timestamp;

/**
 * getTimestamp
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */

public class DateFormat {

	public DateFormat() {
	}

	/**
	 * 使用格式： getTimestamp("1900-01-02")
	 * 
	 * @param sTimestamp
	 * @return
	 */
	public static Timestamp getTimestamp(String sTimestamp) {
		if (sTimestamp == null)
			return null;
		if (sTimestamp.trim().equals(""))
			return null;
		try {
			java.sql.Timestamp t = java.sql.Timestamp.valueOf(sTimestamp
					+ " 00:00:00");
			return t;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据格式把字符串转换成Timestamp,如：timestamp=2006-04-26 20:00:20.0，format=yyyy-MM-dd
	 * HH:mm:ss
	 * 
	 * @param timestamp
	 * @param format
	 * @return
	 */
	public static Timestamp getTimestamp(String timestamp, String format) {
		try {
			java.text.SimpleDateFormat spformat = new java.text.SimpleDateFormat(
					format);
			Date date = (Date) spformat.parse(timestamp);
			Timestamp ts = new Timestamp(date.getTime());
			return ts;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回给定的日期，格式 getTimestamp("1900","12","1")
	 * 
	 * @param sYear
	 * @param sMonth
	 * @param sDay
	 * @return
	 */
	public static Timestamp getTimestamp(String sYear, String sMonth,
			String sDay) {
		try {
			int iYear = Integer.parseInt(sYear);
			int iMonth = Integer.parseInt(sMonth);
			int iDay = Integer.parseInt(sDay);
			return DateFormat.getTimestamp(iYear, iMonth, iDay);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回给定的日期，格式 getTimestamp(1900,12,1)<br>
	 * 
	 * @param iYear
	 * @param iMonth
	 * @param iDay
	 * @return java.sqlTimestamp
	 */
	public static Timestamp getTimestamp(int iYear, int iMonth, int iDay) {
		try {
			Timestamp ts = new Timestamp(iYear, iMonth, iDay, 0, 0, 0, 0);
			return ts;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回当前的日期
	 * 
	 * @return java.sqlTimestamp
	 */
	public static Timestamp getNow() {
		return new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 返回yyyymmdd格式时间
	 * @return
	 */
	public static String getNowByYYYYMMDD() {
		return getNowByFormatString("yyyyMMdd");
	}
	
	public static String getNowByYYYYMMDDHHMISS() {
		return getNowByFormatString("yyyyMMddHHmmss");
	}
	
	/**
	 * 返回当前的日期的字符表示
	 * 
	 * @return String 格式：yyyy.MM.dd HH:mm:ss
	 */
	public static String getNowDateString() {
		return DateFormat.getTimestampString(DateFormat.getNow());
	}

	/**
	 * 返回当前的日期时间的字符表示
	 * 
	 * @return String，格式：yyyy.MM.dd HH:mm:ss
	 */
	public static String getNowDateTimeString() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"yyyy.MM.dd HH:mm:ss");
		Date currentTime_1 = new Date();
		return formatter.format(currentTime_1);
	}

	/**
	 * 返回当前的时间的字符表示
	 * 
	 * @return String 格式：HH:mm:ss
	 */
	public static String getNowTimeString() {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
				"HH:mm:ss");
		Date currentTime_1 = new Date();
		return formatter.format(currentTime_1);
	}

	/**
	 * eg:yyyyMMddHHmmss ->
	 * 
	 * @param asTimeFormat
	 * @return
	 */
	public static String getNowByFormatString(String asTimeFormat) {
		try {
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
					asTimeFormat);
			Date currentTime_1 = new Date();
			return formatter.format(currentTime_1);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 把long格式的时间转成指定格式字符串类型的时间
	 * 
	 * @param time
	 * @param asTimeFormat
	 * @return
	 */
	public static String toFormatString(long time, String asTimeFormat) {
		try {
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
					asTimeFormat);
			Date currentTime_1 = new Date(time);
			return formatter.format(currentTime_1);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getTimestampString(Timestamp timestamp) {
		if (timestamp == null)
			return "";
		return timestamp.toString().substring(0, 10);
	}

	/**
	 * 返回
	 * 
	 * @param timestamp
	 * @return
	 */
	public static String getTimestampString(Timestamp timestamp,
			String asTimeFormat) {
		if (timestamp == null)
			return "";
		try {
			java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
					asTimeFormat);
			return formatter.format(timestamp);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回指定月数之后的月份
	 * 
	 * @param asCurMonth
	 *            月份，格式：yyyyMM
	 * @param aiInterval
	 *            月数
	 * @param asTimeFormat
	 *            时间格式
	 * @return
	 */
	public static String addMonth(String asCurMonth, int aiInterval,
			String asTimeFormat) {
		GregorianCalendar lGregorianCalendar = null;
		int liYear = 0;
		int liMonth = 0;

		try {
			liYear = Integer.parseInt(asCurMonth.substring(0, 4));
			liMonth = Integer.parseInt(asCurMonth.substring(4, 6));
			lGregorianCalendar = new GregorianCalendar(liYear, liMonth - 1, 0);
			lGregorianCalendar.add(GregorianCalendar.MONTH, aiInterval);
			lGregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH,
					lGregorianCalendar
							.getMaximum(GregorianCalendar.DAY_OF_MONTH));
			return getTimestampString(
					new java.sql.Timestamp(lGregorianCalendar.getTimeInMillis()),
					asTimeFormat);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回指定月数之后的月份
	 * 
	 * @param asCurMonth
	 *            月份，格式：yyyyMM
	 * @param aiInterval
	 *            月数
	 * @return 格式：yyyyMM
	 */
	public static String getMonth(String asCurMonth, int aiInterval) {
		GregorianCalendar lGregorianCalendar = null;
		int liYear = 0;
		int liMonth = 0;

		try {
			liYear = Integer.parseInt(asCurMonth.substring(0, 4));
			liMonth = Integer.parseInt(asCurMonth.substring(4, 6));
			lGregorianCalendar = new GregorianCalendar(liYear, liMonth - 1, 1);
			lGregorianCalendar.add(GregorianCalendar.MONTH, aiInterval);
			return (StringDeal.preFillZero(Integer.toString(lGregorianCalendar
					.get(GregorianCalendar.YEAR)), 4) + StringDeal.preFillZero(
					Integer.toString(lGregorianCalendar
							.get(GregorianCalendar.MONTH) + 1), 2));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回指定天数之后的日期
	 * 
	 * @param asCurDate
	 *            格式：yyyyMMdd
	 * @param aiInterval
	 *            天数
	 * @return
	 */
	public static String getDate(String asCurDate, int aiInterval) {
		GregorianCalendar lGregorianCalendar = null;
		int liYear = 0;
		int liMonth = 0;
		int liDay = 0;

		try {
			liYear = Integer.parseInt(asCurDate.substring(0, 4));
			liMonth = Integer.parseInt(asCurDate.substring(4, 6));
			liDay = Integer.parseInt(asCurDate.substring(6, 8));
			lGregorianCalendar = new GregorianCalendar(liYear, liMonth - 1,
					liDay);
			lGregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH, aiInterval);
			return (StringDeal.preFillZero(Integer.toString(lGregorianCalendar
					.get(GregorianCalendar.YEAR)), 4)
					+ StringDeal.preFillZero(Integer
							.toString(lGregorianCalendar
									.get(GregorianCalendar.MONTH) + 1), 2) + StringDeal
						.preFillZero(Integer.toString(lGregorianCalendar
								.get(GregorianCalendar.DAY_OF_MONTH)), 2));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回指定小时之后的日期
	 * 
	 * @param asCurHour
	 *            格式：yyyyMMddHH
	 * @param aiInterval
	 *            小时数
	 * @return
	 */
	public static String getHour(String asCurHour, int aiInterval) {
		GregorianCalendar lGregorianCalendar = null;
		int liYear = 0;
		int liMonth = 0;
		int liDay = 0;
		int liHour = 0;

		try {
			liYear = Integer.parseInt(asCurHour.substring(0, 4));
			liMonth = Integer.parseInt(asCurHour.substring(4, 6));
			liDay = Integer.parseInt(asCurHour.substring(6, 8));
			liHour = Integer.parseInt(asCurHour.substring(8, 10));
			lGregorianCalendar = new GregorianCalendar(liYear, liMonth - 1,
					liDay);
			lGregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, liHour);
			lGregorianCalendar.add(GregorianCalendar.HOUR_OF_DAY, aiInterval);
			return (StringDeal.preFillZero(Integer.toString(lGregorianCalendar
					.get(GregorianCalendar.YEAR)), 4)
					+ StringDeal.preFillZero(Integer
							.toString(lGregorianCalendar
									.get(GregorianCalendar.MONTH) + 1), 2)
					+ StringDeal.preFillZero(Integer
							.toString(lGregorianCalendar
									.get(GregorianCalendar.DAY_OF_MONTH)), 2) + StringDeal
						.preFillZero(Integer.toString(lGregorianCalendar
								.get(GregorianCalendar.HOUR_OF_DAY)), 2));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回指定分钟之后的日期
	 * 
	 * @param asCurMinute
	 *            格式：yyyyMMddHHmi
	 * @param aiInterval
	 *            分钟数
	 * @return
	 */
	public static String getMinute(String asCurMinute, int aiInterval) {
		GregorianCalendar lGregorianCalendar = null;
		int liYear = 0;
		int liMonth = 0;
		int liDay = 0;
		int liHour = 0;
		int liMinute = 0;

		try {
			liYear = Integer.parseInt(asCurMinute.substring(0, 4));
			liMonth = Integer.parseInt(asCurMinute.substring(4, 6));
			liDay = Integer.parseInt(asCurMinute.substring(6, 8));
			liHour = Integer.parseInt(asCurMinute.substring(8, 10));
			liMinute = Integer.parseInt(asCurMinute.substring(10, 12));
			lGregorianCalendar = new GregorianCalendar(liYear, liMonth - 1,
					liDay);
			lGregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, liHour);
			lGregorianCalendar.set(GregorianCalendar.MINUTE, liMinute);
			lGregorianCalendar.add(GregorianCalendar.MINUTE, aiInterval);
			return (StringDeal.preFillZero(Integer.toString(lGregorianCalendar
					.get(GregorianCalendar.YEAR)), 4)
					+ StringDeal.preFillZero(Integer
							.toString(lGregorianCalendar
									.get(GregorianCalendar.MONTH) + 1), 2)
					+ StringDeal.preFillZero(Integer
							.toString(lGregorianCalendar
									.get(GregorianCalendar.DAY_OF_MONTH)), 2)
					+ StringDeal.preFillZero(Integer
							.toString(lGregorianCalendar
									.get(GregorianCalendar.HOUR_OF_DAY)), 2) + StringDeal
						.preFillZero(Integer.toString(lGregorianCalendar
								.get(GregorianCalendar.MINUTE)), 2));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取某个月份的最后一天
	 * 
	 * @param asMonth
	 *            月份，格式：yyyyMM
	 * @return yyyyMMdd格式的时间字符串
	 */
	public static String getLastDayOfMonth(String asMonth) {
		return getLastDayOfMonth(asMonth, "yyyyMMdd");
	}

	/**
	 * 获取某个月份的最后一天
	 * 
	 * @param asMonth
	 *            月份，格式：yyyyMM
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static String getLastDayOfMonth(String asMonth, String format) {
		int liYear = Integer.parseInt(asMonth.substring(0, 4));
		int liMonth = Integer.parseInt(asMonth.substring(4, 6));
		Calendar cal = Calendar.getInstance();
		cal.set(1, liYear);
		cal.set(2, liMonth - 1);
		int lastDay = cal.getActualMaximum(5);
		cal.set(5, lastDay);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}

	/**
	 * 获取某个月份的第一天
	 * 
	 * @param asMonth
	 *            格式：yyyyMM
	 * @return yyyyMMdd格式的时间字符串
	 */
	public static String getFirstDayOfMonth(String asMonth) {
		return getFirstDayOfMonth(asMonth, "yyyyMMdd");
	}

	/**
	 * 获取某个月份的第一天
	 * 
	 * @param asMonth
	 *            格式：yyyyMM
	 * @param format
	 *            时间格式
	 * @return
	 */
	public static String getFirstDayOfMonth(String asMonth, String format) {
		int liYear = Integer.parseInt(asMonth.substring(0, 4));
		int liMonth = Integer.parseInt(asMonth.substring(4, 6));
		Calendar cal = Calendar.getInstance();
		cal.set(1, liYear);
		cal.set(2, liMonth - 1);
		int lastDay = cal.getActualMinimum(5);
		cal.set(5, lastDay);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}

	/**
	 * 判断字符串是否为时间
	 * 
	 * @param dttm
	 *            时间
	 * @param format
	 *            时间格式
	 * @return true，是时间；false，不是时间
	 */
	public static boolean isDate(String dttm, String format) {
		if (dttm != null && !dttm.isEmpty() && format != null
				&& !format.isEmpty()) {
			SimpleDateFormat formatter;
			if (format.replaceAll("\'.+?\'", "").indexOf("y") < 0) {
				format = format + "/yyyy";
				formatter = new SimpleDateFormat("/yyyy");
				dttm = dttm + formatter.format(new Date());
			}

			formatter = new SimpleDateFormat(format);
			formatter.setLenient(false);
			ParsePosition pos = new ParsePosition(0);
			Date date = formatter.parse(dttm, pos);
			return date != null && pos.getErrorIndex() <= 0 ? (pos.getIndex() != dttm
					.length() ? false : formatter.getCalendar().get(1) <= 9999)
					: false;
		} else {
			return false;
		}
	}

	/**
	 * 获取今天剩余的秒数
	 * 
	 * @return
	 */
	public static long getDayRemainingSeconds() {
		String curTime = DateFormat.getNowByFormatString("yyyy-MM-dd HH:mm:ss");
		String nextTime = DateFormat.getNowByFormatString("yyyy-MM-dd") + " "
				+ "23:59:59";
		return getDistanceSeconds(curTime, nextTime) + 1;
	}

	/**
	 * 获取两个时间的秒间隔
	 * 
	 * @param str1
	 * @param str2
	 * @return 秒
	 */
	public static long getDistanceSeconds(String str1, String str2) {
		long[] diffs = getDistanceTimes(str1, str2);
		return diffs[0] * 24 * 3600 + diffs[1] * 3600 + diffs[2] * 60
				+ diffs[3];
	}

	/**
	 * 获取两个时间的时间间隔
	 * 
	 * @param str1
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @param str2
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return 长度为4的long数组，[day,hour,min,sec]
	 */
	public static long[] getDistanceTimes(String str1, String str2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long day = 0L;
		long hour = 0L;
		long min = 0L;
		long sec = 0L;

		try {
			Date one = df.parse(str1);
			Date two = df.parse(str2);
			long times = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (times < time2) {
				diff = time2 - times;
			} else {
				diff = times - time2;
			}

			day = diff / 86400000L;
			hour = diff / 3600000L - day * 24L;
			min = diff / 60000L - day * 24L * 60L - hour * 60L;
			sec = diff / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min
					* 60L;
		} catch (ParseException var19) {
			var19.printStackTrace();
		}

		long[] times1 = new long[] { day, hour, min, sec };
		return times1;
	}

	/**
	 * 获取两个时间的时间间隔
	 * 
	 * @param str1
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @param str2
	 *            格式：yyyy-MM-dd HH:mm:ss
	 * @return xx天xx小时xx分xx秒
	 */
	public static String getDistanceTime(String str1, String str2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long day = 0L;
		long hour = 0L;
		long min = 0L;
		long sec = 0L;

		try {
			Date one = df.parse(str1);
			Date two = df.parse(str2);
			long e = one.getTime();
			long time2 = two.getTime();
			long diff;
			if (e < time2) {
				diff = time2 - e;
			} else {
				diff = e - time2;
			}

			day = diff / 86400000L;
			hour = diff / 3600000L - day * 24L;
			min = diff / 60000L - day * 24L * 60L - hour * 60L;
			sec = diff / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min
					* 60L;
		} catch (ParseException var19) {
			var19.printStackTrace();
		}

		return day + "天" + hour + "小时" + min + "分" + sec + "秒";
	}

	public static void main(String[] args) {
		new DateFormat();
		System.out.println(getNowByYYYYMMDD());
		System.out.println(getNowByYYYYMMDDHHMISS());
		System.out.println("当前小时："
				+ Integer.parseInt(DateFormat.getNowByFormatString("HH")));
		long time = System.currentTimeMillis();
		for(int i=0; i<10000000; i++) {
			Integer.parseInt(DateFormat.getNowByFormatString("HH"));
		}
		System.out.println((System.currentTimeMillis() - time)/1000);
		Timestamp t = getTimestamp("1976-1-1");
		System.out.println(getNowTimeString());
		System.out.println(getNowByFormatString("HHmmss"));
		System.out.println(getNowByFormatString("YYYYMM"));
		System.out.println("YYYYMM: " + getNowByFormatString("YYYYMM"));
		System.out.println("yyyyMM: " + getNowByFormatString("yyyyMM"));
		System.out.println("yyyyMMddHHmmss :"
				+ getNowByFormatString("yyyyMMddHHmmss"));
		System.out.println("yyyyMMddHHmmss :"
				+ toFormatString(System.currentTimeMillis(), "yyyyMMddHHmmss"));
		System.out.println("yyyy-MM-dd HH:mm:ss :"
				+ getNowByFormatString("yyyy-MM-dd HH:mm:ss"));
		String t11 = getNowByFormatString("yyyy-MM-dd HH:mm:ss");

		try {
			Thread.sleep(1230L);
		} catch (InterruptedException var11) {
			var11.printStackTrace();
		}

		String t22 = getNowByFormatString("yyyy-MM-dd HH:mm:ss");
		System.out.println(getDistanceTime(t22, t11));
		Date orderTime = new Date();
		System.out.println(orderTime.toString());
		Timestamp expireTime = getTimestamp("9999-12-31");
		System.out.println(expireTime.toString());
		expireTime = getTimestamp("2099", "12", "31");
		System.out.println(expireTime.toString());
		System.out.println(getDate(getNowByFormatString("yyyyMMdd"), -1));
		System.out.println(getDate(getNowByFormatString("yyyyMMdd"), -10));
		System.out.println(getDate(getNowByFormatString("yyyyMMdd"), 14));
		System.out.println(getDate(getNowByFormatString("yyyyMMdd"), 15));
		System.out.println(getDate(getNowByFormatString("yyyyMMdd"), 16));
		String a = "sss\tsss";
		System.out.println(a);
		a = "201311261425040001|20131126142504||62142|17360010|||SI000001|42|||9|140|140|1736001000|||15606503408|15606503408|15606503408|0|20131126142504|20131126142504|0|||1|2|10||\tdetail_10_11_2014092614_11_2459";
		String[] t1 = StringDeal.split(a, "\t");
		System.out.println(t1[t1.length - 1].trim());
		String billingMonth = getNowByFormatString("yyyyMM");
		System.out.println(getMonth(billingMonth, -1));
		Timestamp validTime = getTimestamp(getMonth(billingMonth, -1) + "01");
		System.out.println(validTime);
		System.out.println(getTimestamp("2014-10-01"));
		System.out.println(getFirstDayOfMonth(billingMonth)
				+ getNowByFormatString("HHmm"));
	}
}
