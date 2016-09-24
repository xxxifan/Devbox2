package com.xxxifan.devbox.library.util;

import java.lang.reflect.Field;

/**
 * Created by xifan on 9/24/16.
 */

public class FieldChecker {

    protected FieldChecker() {}

    /**
     * @return true if check success, no error found.
     */
    public static boolean checkNull(Object model) {
        Field[] fields = model.getClass().getFields();
        Field field;

        try {
            for (int i = 0, s = fields.length; i < s; i++) {
                field = fields[i];
                // check string
                if (field.getType() == String.class) {
                    if (Strings.isEmpty(((String) field.get(model)))) {
                        return false;
                    }
                }

                // check object
                if (!field.getType().isPrimitive()) {
                    if (field.get(model) == null) {
                        return false;
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
