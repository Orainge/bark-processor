package com.orainge.bark_processor.server.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularMatchUtils {
    /**
     * 只要命中一次，就算命中
     *
     * @param str   字符串
     * @param regex 正则表达式
     * @return true: 命中; false: 未命中
     */
    public static boolean match(String str, String regex) {
        if (str == null) {
            return false;
        }

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        return matcher.find();

//        while (matcher.find()) {
//            return true;
//        }
//        return false;
    }
}
