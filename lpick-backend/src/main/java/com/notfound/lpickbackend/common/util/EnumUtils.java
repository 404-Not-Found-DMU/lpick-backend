package com.notfound.lpickbackend.common.util;

/** 프론트엔드에서 전달받은 String이 Enum 내의 name과 동일한지 DB READ 들어가기 전에 선 확인 하기위한 목적의 Util */
public class EnumUtils {

    /** isValidEnum(GearClass.class, str.upper()) */
    public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String name) {
        if (enumClass == null || name == null) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, name);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
