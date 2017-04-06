package com.gzlk.android.isp.helper;

import android.text.TextUtils;

import java.util.Locale;

/**
 * <b>功能：</b>常用的string相关方法集合<br />
 * <b>作者：</b>Hsiang Leekwok <br />
 * <b>时间：</b>2016/01/14 09:39 <br />
 * <b>邮箱：</b>xiang.l.g@gmail.com <br />
 */
public class StringHelper {

    public static String format(String fmt, Object... args) {
        // 参数列表为空时直接返回待格式化的字符串
        if (null == args || args.length < 1) return fmt;
        return String.format(Locale.getDefault(), fmt, args);
    }

    /**
     * 判断字符串是否为空，"null"也当作空
     */
    public static boolean isEmpty(String string) {
        return TextUtils.isEmpty(string) || string.toLowerCase().equals("null");
    }

    /**
     * 判断字符串是否为空的Json Array，如"[]"
     */
    public static boolean isEmptyJsonArray(String string) {
        return isEmpty(string) || string.equals("[]");
    }


    /**
     * 将文本中的空格、换行替换成html代码
     */
    public static String escapeToHtml(String text) {
        // 先替换空格，再替换换行
        return replaceAll(replaceAll(text, " ", "&nbsp;"), "\n", "<br/>");
    }

    /**
     * 将html中的空格、换行替换成文本
     */
    public static String escapeFromHtml(String html) {
        return replaceAll(replaceAll(html, "&nbsp;", " "), "<br/>", "\n");
    }

    /**
     * 将文本中的json字符串进行转义
     */
    public static String escapeJson(String text) {
        return replaceAll(replaceAll(text, "\\\\", "\\\\\\\\"), "\"", "\\\\\"");
    }

    /**
     * 进行文本替换
     *
     * @param text 文本内容
     * @param from 被替换的文本
     * @param to   替换成的文本
     */
    public static String replaceAll(String text, String from, String to) {
        return text.replaceAll(from, to);
    }
}
