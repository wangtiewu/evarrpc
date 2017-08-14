package com.eastelsoft.etos2.rpc.tool;

public class ClassUtils {
	/**
	 * �ж����Ƿ���Primitive Wrap Class
	 * @param clz
	 * @return
	 */
	public static boolean isWrapClass(Class clz) {
		return clz.equals(Boolean.class) || clz.equals(Integer.class)
				|| clz.equals(Character.class) || clz.equals(Byte.class)
				|| clz.equals(Short.class) || clz.equals(Double.class)
				|| clz.equals(Long.class) || clz.equals(Float.class);
	}
}
