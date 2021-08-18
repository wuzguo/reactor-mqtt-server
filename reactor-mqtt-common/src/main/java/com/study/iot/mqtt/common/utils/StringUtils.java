package com.study.iot.mqtt.common.utils;

import com.google.common.base.Charsets;
import com.study.iot.mqtt.common.enums.RandomEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;


/**
 * <B>说明：描述</B>
 *
 * @author L.cm
 * @version 1.0.0
 * @date 2021/5/7 9:29
 */

@UtilityClass
public class StringUtils extends org.springframework.util.StringUtils {

    /**
     * 随机字符串因子
     */
    private static final String INT_STR = "0123456789";
    private static final String STR_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String ALL_STR = INT_STR + STR_STR;

    /**
     * 首字母变小写
     *
     * @param str 字符串
     * @return {String}
     */
    public static String firstCharToLower(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= CharPools.UPPER_A && firstChar <= CharPools.UPPER_Z) {
            char[] arr = str.toCharArray();
            arr[0] += (CharPools.LOWER_A - CharPools.UPPER_A);
            return new String(arr);
        }
        return str;
    }

    /**
     * 首字母变大写
     *
     * @param str 字符串
     * @return {String}
     */
    public static String firstCharToUpper(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= CharPools.LOWER_A && firstChar <= CharPools.LOWER_Z) {
            char[] arr = str.toCharArray();
            arr[0] -= (CharPools.LOWER_A - CharPools.UPPER_A);
            return new String(arr);
        }
        return str;
    }

    /**
     * 驼峰转下划线
     *
     * @param camelCaseList 需要转换的字符串列表
     * @return 转换好的字符串列表
     */
    public static List<String> toUnderline(Collection<String> camelCaseList) {
        List<String> resultUnderlineList = new ArrayList<>();
        for (String item : camelCaseList) {
            item = toUnderline(item);
            resultUnderlineList.add(item);
        }
        return resultUnderlineList;
    }

    /**
     * 字符串驼峰转下划线格式
     *
     * @param stringWithCamelCase 需要转换的字符串
     * @return 转换好的字符串
     */
    public static String toUnderline(String stringWithCamelCase) {
        if (StringUtils.isEmpty(stringWithCamelCase)) {
            return StringPools.EMPTY;
        }
        int len = stringWithCamelCase.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = stringWithCamelCase.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append(CharPools.UNDERSCORE);
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param stringWithUnderline 下划线名
     * @return 转换之后的驼峰
     */
    public static String toCamelCase(String stringWithUnderline) {
        if (StringUtils.isEmpty(stringWithUnderline)) {
            return StringPools.EMPTY;
        }
        if (stringWithUnderline.indexOf(CharPools.UNDERSCORE) == -1) {
            return stringWithUnderline;
        }
        stringWithUnderline = stringWithUnderline.toLowerCase();
        char[] fromArray = stringWithUnderline.toCharArray();
        char[] toArray = new char[fromArray.length];
        int j = 0;
        for (int i = 0; i < fromArray.length; i++) {
            if (CharPools.UNDERSCORE == fromArray[i]) {
                // 当前字符为下划线时，将指针后移一位，将紧随下划线后面一个字符转成大写并存放
                i++;
                if (i < fromArray.length) {
                    toArray[j++] = Character.toUpperCase(fromArray[i]);
                }
            } else {
                toArray[j++] = fromArray[i];
            }
        }
        return new String(toArray, 0, j);
    }

    /**
     * Check whether the given {@code CharSequence} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code CharSequence} is not {@code null}, its length is greater than 0, and it contains at least one
     * non-whitespace character.
     * <pre class="code">
     * StringUtil.isBlank(null) = true
     * StringUtil.isBlank("") = true
     * StringUtil.isBlank(" ") = true
     * StringUtil.isBlank("12345") = false
     * StringUtil.isBlank(" 12345 ") = false
     * </pre>
     *
     * @param cs the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null}, its length is greater than 0, and it does
     * not contain whitespace only
     * @see Character#isWhitespace
     */
    public static boolean isBlank(@Nullable final CharSequence cs) {
        return !StringUtils.hasText(cs);
    }

    /**
     * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
     * <pre>
     * StringUtil.isNotBlank(null)		= false
     * StringUtil.isNotBlank("")		= false
     * StringUtil.isNotBlank(" ")		= false
     * StringUtil.isNotBlank("bob")		= true
     * StringUtil.isNotBlank("  bob  ")	= true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is not empty and not null and not whitespace
     * @see Character#isWhitespace
     */
    public static boolean isNotBlank(@Nullable final CharSequence cs) {
        return StringUtils.hasText(cs);
    }

    /**
     * 有 任意 一个 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isAnyBlank(final CharSequence... css) {
        if (ObjectUtils.isEmpty(css)) {
            return true;
        }
        return Stream.of(css).anyMatch(StringUtils::isBlank);
    }

    /**
     * 有 任意 一个 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isAnyNotBlank(final CharSequence... css) {
        if (CollectionUtils.isEmpty(css)) {
            return false;
        }
        return Arrays.stream(css).anyMatch(StringUtils::isNoneBlank);
    }

    /**
     * 有 任意 一个 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isAnyNotBlank(Collection<String> css) {
        if (CollectionUtils.isEmpty(css)) {
            return false;
        }
        return css.stream().anyMatch(StringUtils::isNoneBlank);
    }

    /**
     * 是否全非 Blank
     *
     * @param css CharSequence
     * @return boolean
     */
    public static boolean isNoneBlank(final CharSequence... css) {
        if (ObjectUtils.isEmpty(css)) {
            return false;
        }
        return Stream.of(css).allMatch(StringUtils::isNotBlank);
    }

    /**
     * 判断一个字符串是否是数字
     *
     * @param cs the CharSequence to check, may be null
     * @return {boolean}
     */
    public static boolean isNumeric(final CharSequence cs) {
        if (StringUtils.isBlank(cs)) {
            return false;
        }
        for (int i = cs.length(); --i >= 0; ) {
            int chr = cs.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将字符串中特定模式的字符转换成map中对应的值
     * <p>
     * use: format("my name is ${name}, and i like ${like}!", {"name":"L.cm", "like": "Java"})
     *
     * @param message 需要转换的字符串
     * @param params  转换所需的键值对集合
     * @return 转换后的字符串
     */
    public static String format(@Nullable String message, @Nullable Map<String, Object> params) {
        // message 为 null 返回空字符串
        if (message == null) {
            return StringPools.EMPTY;
        }
        // 参数为 null 或者为空
        if (params == null || params.isEmpty()) {
            return message;
        }
        // 替换变量
        StringBuilder sb = new StringBuilder((int) (message.length() * 1.5));
        int cursor = 0;
        for (int start, end; (start = message.indexOf(StringPools.DOLLAR_LEFT_BRACE, cursor)) != -1
            && (end = message.indexOf(CharPools.RIGHT_BRACE, start)) != -1; ) {
            sb.append(message, cursor, start);
            String key = message.substring(start + 2, end);
            Object value = params.get(StringUtils.trimWhitespace(key));
            sb.append(value == null ? StringPools.EMPTY : value);
            cursor = end + 1;
        }
        sb.append(message.substring(cursor));
        return sb.toString();
    }

    /**
     * 同 log 格式的 format 规则
     * <p>
     * use: format("my name is {}, and i like {}!", "L.cm", "Java")
     *
     * @param message   需要转换的字符串
     * @param arguments 需要替换的变量
     * @return 转换后的字符串
     */
    public static String format(@Nullable String message, @Nullable Object... arguments) {
        // message 为 null 返回空字符串
        if (message == null) {
            return StringPools.EMPTY;
        }
        // 参数为 null 或者为空
        if (arguments == null || arguments.length == 0) {
            return message;
        }
        StringBuilder sb = new StringBuilder((int) (message.length() * 1.5));
        int cursor = 0;
        int index = 0;
        int argsLength = arguments.length;
        for (int start, end; (start = message.indexOf(CharPools.LEFT_BRACE, cursor)) != -1
            && (end = message.indexOf(CharPools.RIGHT_BRACE, start)) != -1 && index < argsLength; ) {
            sb.append(message, cursor, start);
            sb.append(arguments[index]);
            cursor = end + 1;
            index++;
        }
        sb.append(message.substring(cursor));
        return sb.toString();
    }

    /**
     * Convert a {@code Collection} into a delimited {@code String} (e.g., CSV).
     * <p>Useful for {@code toString()} implementations.
     *
     * @param coll the {@code Collection} to convert
     * @return the delimited {@code String}
     */
    public static String join(Collection<?> coll) {
        return StringUtils.collectionToCommaDelimitedString(coll);
    }

    /**
     * Convert a {@code Collection} into a delimited {@code String} (e.g. CSV).
     * <p>Useful for {@code toString()} implementations.
     *
     * @param coll  the {@code Collection} to convert
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited {@code String}
     */
    public static String join(Collection<?> coll, String delim) {
        return StringUtils.collectionToDelimitedString(coll, delim);
    }

    /**
     * Convert a {@code String} array into a comma delimited {@code String} (i.e., CSV).
     * <p>Useful for {@code toString()} implementations.
     *
     * @param arr the array to display
     * @return the delimited {@code String}
     */
    public static String join(Object[] arr) {
        return StringUtils.arrayToCommaDelimitedString(arr);
    }

    /**
     * Convert a {@code String} array into a delimited {@code String} (e.g. CSV).
     * <p>Useful for {@code toString()} implementations.
     *
     * @param arr   the array to display
     * @param delim the delimiter to use (typically a ",")
     * @return the delimited {@code String}
     */
    public static String join(Object[] arr, String delim) {
        return StringUtils.arrayToDelimitedString(arr, delim);
    }

    /**
     * 分割 字符串
     *
     * @param str       字符串
     * @param delimiter 分割符
     * @return 字符串数组
     */
    public static String[] split(@Nullable String str, @Nullable String delimiter) {
        return StringUtils.delimitedListToStringArray(str, delimiter);
    }

    /**
     * 分割 字符串 删除常见 空白符
     *
     * @param str       字符串
     * @param delimiter 分割符
     * @return 字符串数组
     */
    public static String[] splitTrim(@Nullable String str, @Nullable String delimiter) {
        return StringUtils.delimitedListToStringArray(str, delimiter, " \t\n\n\f");
    }

    /**
     * 字符串是否符合指定的 表达式
     *
     * <p>
     * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy"
     * </p>
     *
     * @param pattern 表达式
     * @param str     字符串
     * @return 是否匹配
     */
    public static boolean simpleMatch(@Nullable String pattern, @Nullable String str) {
        return PatternMatchUtils.simpleMatch(pattern, str);
    }

    /**
     * 字符串是否符合指定的 表达式
     *
     * <p>
     * pattern styles: "xxx*", "*xxx", "*xxx*" and "xxx*yyy"
     * </p>
     *
     * @param patterns 表达式 数组
     * @param str      字符串
     * @return 是否匹配
     */
    public static boolean simpleMatch(@Nullable String[] patterns, String str) {
        return PatternMatchUtils.simpleMatch(patterns, str);
    }

    /**
     * 生成uuid，采用 jdk 9 的形式，优化性能
     *
     * @return UUID
     */
    public static String getUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        long lsb = random.nextLong();
        long msb = random.nextLong();
        byte[] buf = new byte[32];
        formatUnsignedLong(lsb, buf, 20, 12);
        formatUnsignedLong(lsb >>> 48, buf, 16, 4);
        formatUnsignedLong(msb, buf, 12, 4);
        formatUnsignedLong(msb >>> 16, buf, 8, 4);
        formatUnsignedLong(msb >>> 32, buf, 0, 8);
        return new String(buf, Charsets.UTF_8);
    }

    private static void formatUnsignedLong(long val, byte[] buf, int offset, int len) {
        int charPos = offset + len;
        int radix = 1 << 4;
        int mask = radix - 1;
        do {
            buf[--charPos] = NumberUtils.DIGITS[((int) val) & mask];
            val >>>= 4;
        } while (charPos > offset);
    }

    /**
     * 随机数生成
     *
     * @param count 字符长度
     * @return 随机数
     */
    public static String random(int count) {
        return StringUtils.random(count, RandomEnum.ALL);
    }

    /**
     * 随机数生成
     *
     * @param count      字符长度
     * @param randomEnum 随机数类别
     * @return 随机数
     */
    public static String random(int count, RandomEnum randomEnum) {
        if (count == 0) {
            return "";
        }
        Assert.isTrue(count > 0, "Requested random string length " + count + " is less than 0.");
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            if (RandomEnum.INT == randomEnum) {
                buffer[i] = INT_STR.charAt(random.nextInt(INT_STR.length()));
            } else if (RandomEnum.STRING == randomEnum) {
                buffer[i] = STR_STR.charAt(random.nextInt(STR_STR.length()));
            } else {
                buffer[i] = ALL_STR.charAt(random.nextInt(ALL_STR.length()));
            }
        }
        return new String(buffer);
    }

    public static String getDefaultIfBlank(String str, String defaultStr) {
        return StringUtils.isBlank(str) ? defaultStr : str;
    }

    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        } else {
            int pads = size - str.length();
            if (pads <= 0) {
                return str;
            } else {
                return pads > 8192 ? leftPad(str, size, String.valueOf(padChar)) : repeat(padChar, pads).concat(str);
            }
        }
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        } else {
            if (isEmpty(padStr)) {
                padStr = " ";
            }

            int padLen = padStr.length();
            int strLen = str.length();
            int pads = size - strLen;
            if (pads <= 0) {
                return str;
            } else if (padLen == 1 && pads <= 8192) {
                return leftPad(str, size, padStr.charAt(0));
            } else if (pads == padLen) {
                return padStr.concat(str);
            } else if (pads < padLen) {
                return padStr.substring(0, pads).concat(str);
            } else {
                char[] padding = new char[pads];
                char[] padChars = padStr.toCharArray();

                for (int i = 0; i < pads; ++i) {
                    padding[i] = padChars[i % padLen];
                }

                return (new String(padding)).concat(str);
            }
        }
    }

    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        } else {
            char[] buf = new char[repeat];

            for (int i = repeat - 1; i >= 0; --i) {
                buf[i] = ch;
            }

            return new String(buf);
        }
    }

    public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }
}

