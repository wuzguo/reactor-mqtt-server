package com.study.iot.mqtt.common.utils;

import com.google.common.collect.Maps;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.springframework.util.Assert;

/**
 * <B>说明：描述</B>
 *
 * @author zak.wu
 * @version 1.0.0
 * @date 2021/5/7 9:29
 */

@UtilityClass
public class DateUtil {

    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone(ZoneOffset.UTC);
    public static final ZoneId ZONE_ID = ZoneOffset.UTC;
    public static final String PATTERN_TIME_ZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    // This class is immutable and thread-safe.
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Map<String, TimeZone> mapTimeZone = Maps.newConcurrentMap();

    /**
     * @param dateStr str格式 yyyy-MM-dd
     * @return LocalDate
     */
    public static LocalDate parseDateLocal(String dateStr) {
        if (StringUtil.isBlank(dateStr)) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_TIME_FORMATTER);
    }

    public static String formatDateStr(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.format(DATE_TIME_FORMATTER);
    }

    public static String format(Date date, String pattern) {
        return format(date, pattern, TIME_ZONE, null);
    }

    public static String format(Date date, String pattern, TimeZone TIME_ZONE) {
        return format(date, pattern, TIME_ZONE, null);
    }

    public static String format(Date date, String pattern, Locale locale) {
        return format(date, pattern, TIME_ZONE, locale);
    }

    public static String format(Date date, String pattern, TimeZone TIME_ZONE, Locale locale) {
        if (date == null) {
            return null;
        }
        FastDateFormat df = FastDateFormat.getInstance(pattern, TIME_ZONE, locale);
        return df.format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        return parse(dateStr, pattern, TIME_ZONE, null);
    }

    public static Date parse(String dateStr, String pattern, TimeZone TIME_ZONE) {
        return parse(dateStr, pattern, TIME_ZONE, null);
    }

    public static Date parse(String dateStr, String pattern, Locale locale) {
        return parse(dateStr, pattern, TIME_ZONE, locale);
    }

    public static Date parse(String dateStr, String pattern, TimeZone TIME_ZONE, Locale locale) {
        if (StringUtil.isBlank(dateStr)) {
            return null;
        }
        FastDateFormat df = FastDateFormat.getInstance(pattern, TIME_ZONE, locale);
        try {
            return df.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException(dateStr, e);
        }
    }

    public static TimeZone getTimeZone(String id) {
        if (id == null) {
            return TimeZone.getDefault();
        }
        return mapTimeZone.computeIfAbsent(id, TimeZone::getTimeZone);
    }

    public static TimeZone getTimeZone(Integer timezoneOffset) {
        return getTimeZone(
            timezoneOffset == null ? null : "GMT" + (timezoneOffset < 0 ? "" : "+") + timezoneOffset + ":00");
    }


    /**
     * 添加年
     *
     * @param date       时间
     * @param yearsToAdd 添加的年数
     * @return 设置后的时间
     */
    public static Date plusYears(Date date, int yearsToAdd) {
        return DateUtil.set(date, Calendar.YEAR, yearsToAdd);
    }

    /**
     * 添加月
     *
     * @param date        时间
     * @param monthsToAdd 添加的月数
     * @return 设置后的时间
     */
    public static Date plusMonths(Date date, int monthsToAdd) {
        return DateUtil.set(date, Calendar.MONTH, monthsToAdd);
    }

    /**
     * 添加周
     *
     * @param date       时间
     * @param weeksToAdd 添加的周数
     * @return 设置后的时间
     */
    public static Date plusWeeks(Date date, int weeksToAdd) {
        return DateUtil.plus(date, Period.ofWeeks(weeksToAdd));
    }

    /**
     * 添加天
     *
     * @param date      时间
     * @param daysToAdd 添加的天数
     * @return 设置后的时间
     */
    public static Date plusDays(Date date, long daysToAdd) {
        return DateUtil.plus(date, Duration.ofDays(daysToAdd));
    }

    /**
     * 添加小时
     *
     * @param date       时间
     * @param hoursToAdd 添加的小时数
     * @return 设置后的时间
     */
    public static Date plusHours(Date date, long hoursToAdd) {
        return DateUtil.plus(date, Duration.ofHours(hoursToAdd));
    }

    /**
     * 添加分钟
     *
     * @param date         时间
     * @param minutesToAdd 添加的分钟数
     * @return 设置后的时间
     */
    public static Date plusMinutes(Date date, long minutesToAdd) {
        return DateUtil.plus(date, Duration.ofMinutes(minutesToAdd));
    }

    /**
     * 添加秒
     *
     * @param date         时间
     * @param secondsToAdd 添加的秒数
     * @return 设置后的时间
     */
    public static Date plusSeconds(Date date, long secondsToAdd) {
        return DateUtil.plus(date, Duration.ofSeconds(secondsToAdd));
    }

    /**
     * 添加毫秒
     *
     * @param date        时间
     * @param millisToAdd 添加的毫秒数
     * @return 设置后的时间
     */
    public static Date plusMillis(Date date, long millisToAdd) {
        return DateUtil.plus(date, Duration.ofMillis(millisToAdd));
    }

    /**
     * 添加纳秒
     *
     * @param date       时间
     * @param nanosToAdd 添加的纳秒数
     * @return 设置后的时间
     */
    public static Date plusNanos(Date date, long nanosToAdd) {
        return DateUtil.plus(date, Duration.ofNanos(nanosToAdd));
    }

    /**
     * 日期添加时间量
     *
     * @param date   时间
     * @param amount 时间量
     * @return 设置后的时间
     */
    public static Date plus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.plus(amount));
    }

    /**
     * 减少年
     *
     * @param date  时间
     * @param years 减少的年数
     * @return 设置后的时间
     */
    public static Date minusYears(Date date, int years) {
        return DateUtil.set(date, Calendar.YEAR, -years);
    }

    /**
     * 减少月
     *
     * @param date   时间
     * @param months 减少的月数
     * @return 设置后的时间
     */
    public static Date minusMonths(Date date, int months) {
        return DateUtil.set(date, Calendar.MONTH, -months);
    }

    /**
     * 减少周
     *
     * @param date  时间
     * @param weeks 减少的周数
     * @return 设置后的时间
     */
    public static Date minusWeeks(Date date, int weeks) {
        return DateUtil.minus(date, Period.ofWeeks(weeks));
    }

    /**
     * 减少天
     *
     * @param date 时间
     * @param days 减少的天数
     * @return 设置后的时间
     */
    public static Date minusDays(Date date, long days) {
        return DateUtil.minus(date, Duration.ofDays(days));
    }

    /**
     * 减少小时
     *
     * @param date  时间
     * @param hours 减少的小时数
     * @return 设置后的时间
     */
    public static Date minusHours(Date date, long hours) {
        return DateUtil.minus(date, Duration.ofHours(hours));
    }

    /**
     * 减少分钟
     *
     * @param date    时间
     * @param minutes 减少的分钟数
     * @return 设置后的时间
     */
    public static Date minusMinutes(Date date, long minutes) {
        return DateUtil.minus(date, Duration.ofMinutes(minutes));
    }

    /**
     * 减少秒
     *
     * @param date    时间
     * @param seconds 减少的秒数
     * @return 设置后的时间
     */
    public static Date minusSeconds(Date date, long seconds) {
        return DateUtil.minus(date, Duration.ofSeconds(seconds));
    }

    /**
     * 减少毫秒
     *
     * @param date   时间
     * @param millis 减少的毫秒数
     * @return 设置后的时间
     */
    public static Date minusMillis(Date date, long millis) {
        return DateUtil.minus(date, Duration.ofMillis(millis));
    }

    /**
     * 减少纳秒
     *
     * @param date  时间
     * @param nanos 减少的纳秒数
     * @return 设置后的时间
     */
    public static Date minusNanos(Date date, long nanos) {
        return DateUtil.minus(date, Duration.ofNanos(nanos));
    }

    /**
     * 日期减少时间量
     *
     * @param date   时间
     * @param amount 时间量
     * @return 设置后的时间
     */
    public static Date minus(Date date, TemporalAmount amount) {
        Instant instant = date.toInstant();
        return Date.from(instant.minus(amount));
    }

    /**
     * 设置日期属性
     *
     * @param date          时间
     * @param calendarField 更改的属性
     * @param amount        更改数，-1表示减少
     * @return 设置后的时间
     */
    private static Date set(Date date, int calendarField, int amount) {
        Assert.notNull(date, "The date must not be null");
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 时间转 Instant
     *
     * @param dateTime 时间
     * @return Instant
     */
    public static Instant toInstant(LocalDateTime dateTime) {
        return dateTime.atZone(ZONE_ID).toInstant();
    }

    /**
     * Instant 转 时间
     *
     * @param instant Instant
     * @return Instant
     */
    public static LocalDateTime toDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    /**
     * 转换成 date
     *
     * @param dateTime LocalDateTime
     * @return Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(DateUtil.toInstant(dateTime));
    }

    /**
     * 转换成 date
     *
     * @param localDate LocalDate
     * @return Date
     */
    public static Date toDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZONE_ID).toInstant());
    }

    /**
     * Converts local date time to Calendar.
     */
    public static Calendar toCalendar(final LocalDateTime localDateTime) {
        return GregorianCalendar.from(ZonedDateTime.of(localDateTime, ZONE_ID));
    }

    /**
     * localDateTime 转换成毫秒数
     *
     * @param localDateTime LocalDateTime
     * @return long
     */
    public static long toMilliseconds(final LocalDateTime localDateTime) {
        return localDateTime.atZone(ZONE_ID).toInstant().toEpochMilli();
    }

    /**
     * localDate 转换成毫秒数
     *
     * @param localDate LocalDate
     * @return long
     */
    public static long toMilliseconds(LocalDate localDate) {
        return toMilliseconds(localDate.atStartOfDay());
    }

    /**
     * 转换成java8 时间
     *
     * @param calendar 日历
     * @return LocalDateTime
     */
    public static LocalDateTime fromCalendar(final Calendar calendar) {
        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZONE_ID : tz.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zid);
    }

    /**
     * 转换成java8 时间
     *
     * @param instant Instant
     * @return LocalDateTime
     */
    public static LocalDateTime fromInstant(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZONE_ID);
    }

    /**
     * 转换成java8 时间
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime fromDate(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID);
    }

    /**
     * 转换成java8 时间
     *
     * @param milliseconds 毫秒数
     * @return LocalDateTime
     */
    public static LocalDateTime fromMilliseconds(final long milliseconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZONE_ID);
    }

    /**
     * 比较2个时间差，跨度比较小
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 时间间隔
     */
    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive);
    }

    /**
     * 比较2个时间差，跨度比较大，年月日为单位
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔
     */
    public static Period between(LocalDate startDate, LocalDate endDate) {
        return Period.between(startDate, endDate);
    }

    /**
     * 比较2个 时间差
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间间隔
     */
    public static Duration between(Date startDate, Date endDate) {
        return Duration.between(startDate.toInstant(), endDate.toInstant());
    }

    /**
     * @return
     */
    public static String getCurrentDateTime(DateTimeFormatter formatter) {
        return LocalDateTime.now().format(formatter);
    }
}
