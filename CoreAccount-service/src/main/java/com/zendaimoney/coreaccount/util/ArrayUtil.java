package com.zendaimoney.coreaccount.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数组工具类
 * 
 * @author binliu
 * 
 */
public abstract class ArrayUtil {

	/**
	 * 返回origin删除dest中所有元素后的集合
	 * 
	 * @param origin
	 * @param dests
	 * @return
	 */
	static public <T> T[] removeAll(T[] origin, T[] dests) {
		if (dests == null)
			return origin;
		List<T> result = new ArrayList<T>(origin.length);
		int found = 0;
		for (T src : origin) {
			for (T dest : dests) {
				if (src.equals(dest)) {
					continue;
				}
				if (++found == dests.length) {
					result.add(src);
				}
			}
			found = 0;
		}
		return result.toArray(Arrays.copyOf(origin, 0));
	}

}
