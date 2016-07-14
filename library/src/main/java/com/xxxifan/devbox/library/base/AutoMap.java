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

package com.xxxifan.devbox.library.base;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xifan on 15-7-26.
 */
public class AutoMap extends HashMap<String, Object> {
    public AutoMap() {
        super();
    }

    /**
     * AutoMap
     *
     * @param serializable
     */
    @SuppressWarnings("unchecked")
    public AutoMap(Map<String, Object> serializable) {
        super(serializable);
    }

    public AutoMap(int capacity) {
        super(capacity);
    }

    /**
     * putBoolean
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putBoolean(String key, boolean value) {
        put(key, value);
        return this;
    }

    /**
     * putInt
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putInt(String key, int value) {
        put(key, value);
        return this;
    }

    /**
     * putLong
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putLong(String key, long value) {
        put(key, value);
        return this;
    }

    /**
     * putFloat
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putFloat(String key, float value) {
        put(key, value);
        return this;
    }

    /**
     * AutoMap
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putDouble(String key, double value) {
        put(key, value);
        return this;
    }

    /**
     * putString
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putString(String key, String value) {
        put(key, value);
        return this;
    }

    /**
     * putCharSequence
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putCharSequence(String key, CharSequence value) {
        put(key, value);
        return this;
    }

    /**
     * putObject
     *
     * @param key
     * @param value
     * @return
     */
    public AutoMap putObject(String key, Object value) {
        put(key, value);
        return this;
    }

    public AutoMap putObjectList(String key, List value) {
        put(key, value);
        return this;
    }

    /**
     * getBoolean
     *
     * @param key
     * @return
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * getBoolean
     *
     * @param key
     * @return
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object o = get(key);
        if (o == null) {
            return defaultValue;
        }

        if (o instanceof Boolean) {
            return (Boolean) o;
        }

        String s = o.toString();
        return TextUtils.isEmpty(s) ? defaultValue : Boolean.parseBoolean(s);
    }

    /**
     * getInt
     *
     * @param key
     * @return
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * getInt
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getInt(String key, int defaultValue) {
        Object o = get(key);
        if (o == null) {
            return defaultValue;
        }

        if (o instanceof Integer) {
            return (Integer) o;
        }

        String s = o.toString().trim();
        if (TextUtils.isEmpty(s)) {
            return defaultValue;
        }

        int floatNumber = s.indexOf(".");
        if (floatNumber > 0) {
            s = s.substring(0, floatNumber);
        }

        return Integer.parseInt(s);
    }

    /**
     * getLong
     *
     * @param key
     * @return
     */
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * getLong
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getLong(String key, long defaultValue) {
        Object o = get(key);
        if (o == null) {
            return defaultValue;
        }

        if (o instanceof Long) {
            return (Long) o;
        }

        String s = o.toString().trim();
        if (TextUtils.isEmpty(s)) {
            return defaultValue;
        }

        if (s.endsWith(".0")) {
            s = s.substring(0, s.length() - 2);
        }

        return Long.parseLong((new BigDecimal(s)).toPlainString());
    }

    /**
     * getFloat
     *
     * @param key
     * @return
     */
    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    /**
     * getFloat
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getFloat(String key, float defaultValue) {
        Object o = get(key);
        if (o == null) {
            return defaultValue;
        }

        if (o instanceof Float) {
            return (Float) o;
        }

        String s = o.toString();
        return TextUtils.isEmpty(s) ? defaultValue : Float.parseFloat(s);
    }

    /**
     * getDouble
     *
     * @param key
     * @return
     */
    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    /**
     * getDouble
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public double getDouble(String key, double defaultValue) {
        Object o = get(key);
        if (o == null) {
            return defaultValue;
        }

        if (o instanceof Double) {
            return (Double) o;
        }

        String s = o.toString();
        return TextUtils.isEmpty(s) ? defaultValue : Double.parseDouble(s);
    }

    /**
     * getString
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * getString
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        Object o = get(key);
        return o == null || TextUtils.isEmpty(o.toString()) ? defaultValue : o.toString();
    }

    /**
     * getStringList
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> getStringList(String key) {
        Object o = get(key);
        if (o == null)
            return null;
        try {
            return (ArrayList<String>) o;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * ArrayList
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<? extends AutoMap> getList(String key) {
        Object o = get(key);
        return o == null ? null : (List<? extends AutoMap>) o;
    }

    /**
     * getMap
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String key) {
        Object o = get(key);
        return o == null ? null : (Map<String, Object>) o;
    }
}
