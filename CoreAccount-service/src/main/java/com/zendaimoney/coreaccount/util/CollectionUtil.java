package com.zendaimoney.coreaccount.util;

import java.util.ArrayList;

/**
 * Author: kimi
 * Date: 14-5-20
 * Time: 下午3:57
 */
public abstract class CollectionUtil {
    public static <T> ArrayList<T> addAll(ArrayList<T>... list){
        if (list == null || list.length <= 0)
            return null;
        ArrayList<T> result = new ArrayList<T>(list[0]);
        for (int i = 1; i < list.length; i++) {
            result.addAll(list[i]);
        }
        return result;
    }
}
