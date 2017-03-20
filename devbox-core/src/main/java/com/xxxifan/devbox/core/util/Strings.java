/*
 * Copyright(c) 2016 xxxifan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xxxifan.devbox.core.util;

import android.util.Base64;

import com.xxxifan.devbox.core.Devbox;
import com.xxxifan.devbox.core.R;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xifan on 6/8/16.
 */
public class Strings {
    public static final String EMPTY = "";

    private static final Pattern PHONE_PATTERN = Pattern.compile("^(1(3|4|5|7|8))\\d{9}$");
    private static final int[] TIME_VALUE = {
            1, //s
            60, //m
            60 * 60, //h
            24 * 60 * 60, //d
    };
    private static String[] sTimeUnits;

    protected Strings() {}

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0 || str.toString().trim().isEmpty();
    }

    /**
     * @return true if every item is empty
     */
    public static boolean isEmpty(CharSequence... str) {
        for (int i = 0, s = str.length; i < s; i++) {
            if (!isEmpty(str[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse timestamp to a down count timer like '34 hours ago'
     *
     * @param timestamp specified time
     * @param unitLimit displayed unit limit, normally it will return full units of time like
     *                  3 days 2 hours 34 minutes 23 seconds, this will limit to largest unit,
     *                  if you passing 2, then it will return only 3 days 2 hours, negative number for no limits.
     * @return parsed time format like '3 days 2 hours 34 minutes 23 seconds'
     */
    public static String downTimer(long timestamp, int unitLimit) {
        if (sTimeUnits == null) {
            sTimeUnits = Devbox.getAppDelegate().getResources().getStringArray(R.array.time_unit);
        }
        String value = EMPTY;
        long interval = (System.currentTimeMillis() - timestamp) / 1000; // start with seconds
        if (interval < 0) {
            interval = -interval;
        }

        int count = 0;
        for (int i = sTimeUnits.length - 1; i >= 0; i--) {
            if (count == unitLimit) { // limit reached
                break;
            }

            long result = interval / TIME_VALUE[i];
            if (result > 0) {
                value += result + sTimeUnits[i];
                interval %= TIME_VALUE[i];
                count++;
            }
        }
        return value;
    }

    public static String escapeExprSpecialWord(String keyword) {
        String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
        for (String key : fbsArr) {
            if (keyword.contains(key)) {
                keyword = keyword.replace(key, "\\" + key);
            }
        }
        return keyword;
    }

    /**
     * @return true if every item is NOT empty
     */
    public static boolean hasEmpty(CharSequence... str) {
        for (int i = 0, s = str.length; i < s; i++) {
            if (isEmpty(str[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(String source, String key) {
        return source != null && source.contains(key);
    }

    public static boolean equals(String source, String key) {
        return source != null && source.equals(key);
    }

    /**
     * Check is phone number
     */
    public static boolean isPhoneNum(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        Matcher m = PHONE_PATTERN.matcher(phone);
        return m.matches();
    }

    public static String encodeMD5(String string) {
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));

            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY;
    }

    public static String encodeBase64(String encodeStr) {
        return encodeBase64(encodeStr.getBytes());
    }

    public static String encodeBase64(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public static byte[] decodeBase64(String decodeStr) {
        return Base64.decode(decodeStr, Base64.NO_WRAP);
    }

    public static String decodeBase64ToString(String decodeStr) {
        return new String(decodeBase64(decodeStr), Charset.forName("utf-8"));
    }


    public static String encodeSHA1ToString(String string) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            byte hash[] = digest.digest(string.getBytes("UTF-8"));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                if ((b & 0xFF) < 0x10) hex.append("0");
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return EMPTY;
    }

    public static byte[] encodeSHA1(String string) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            return digest.digest(string.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
