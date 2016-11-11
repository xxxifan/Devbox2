package com.xxxifan.devbox.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by xifan on 9/24/16.
 */

public class FieldChecker {

    protected FieldChecker() {}

    /**
     * check model all public non-static normal field is null or not.
     *
     * @return true if check success, no error found.
     */
    public static boolean checkNull(Object model) {
        Field[] fields = model.getClass().getFields();
        Field field;

        try {
            for (int i = 0, s = fields.length; i < s; i++) {
                field = fields[i];

                // check modifiers
                final int modifiers = field.getModifiers();
                if (Modifier.isStatic(modifiers)) {
                    continue;
                }

                // check string
                if (field.getType() == String.class) {
                    if (Strings.isEmpty(((String) field.get(model)))) {
                        return false;
                    }
                } else if (!field.getType().isPrimitive()) {
                    // check object
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
