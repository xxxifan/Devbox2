package com.xxxifan.devbox.library.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xifan on 6/8/16.
 */
public class Strings {
    public static final String EMPTY = "";

    private final static Pattern PHONE_PATTERN = Pattern.compile("^(1(3|4|5|7|8))\\d{9}$");

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
        return !isEmpty(source) && source.contains(key);
    }

    public static boolean equals(String source, String key) {
        return !isEmpty(source) && source.equals(key);
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

    /**
     * MD5 encode string
     */
    public static String encodeMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5")
                    .digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
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
}
