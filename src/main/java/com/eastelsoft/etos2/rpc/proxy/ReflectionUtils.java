package com.eastelsoft.etos2.rpc.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ReflectionUtils {
	private static Map<Class, Map<String, Field>> fieldMap = new HashMap<Class, Map<String, Field>>();

	/**
	 * 获取对象的指定字段，Field.setAccessible(true);
	 * 
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Object obj, String fieldName) {
		return getField(obj.getClass(), fieldName);
	}

	/**
	 * 获取类的指定字段，Field.setAccessible(true);
	 * 
	 * @param cls
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class cls, String fieldName) {
		Field f = null;
		Map<String, Field> inner = fieldMap.get(cls);
		if (inner != null) {
			f = inner.get(fieldName);
			if (f != null)
				return f;
		} else {
			inner = new HashMap<String, Field>();
			Map<String, Field> oldInner = fieldMap.putIfAbsent(cls, inner);
			if (oldInner != null) {
				inner = oldInner;
			}
		}
		try {
			while (cls != null) {
				try {
					f = cls.getDeclaredField(fieldName);
					f.setAccessible(true);
				} catch (NoSuchFieldException e) {
					f = null;
				}
				if (f != null)
					break;
				cls = cls.getSuperclass();
			}
		} catch (SecurityException e) {
			throw new RuntimeException("SecurityException", e);
		}
		if (f == null)
			throw new RuntimeException("NoSuchFieldException");
		inner.put(fieldName, f);
		return f;
	}

	/**
	 * 获取对象指定字段的值
	 * 
	 * @param obj
	 * @param field
	 * @return
	 */
	public static Object getFieldValue(Object obj, Field field) {
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 设置对象指定字段的值
	 * 
	 * @param obj
	 * @param field
	 * @param value
	 */
	public static void setFieldValue(Object obj, Field field, Object value) {
		field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("IllegalArgumentException", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("IllegalAccessExceptione", e);
		}
	}

	/**
	 * 获取类的所有字段（包括父类）
	 * 
	 * @param clz
	 * @return
	 */
	public static Field[] getAllFields(Class clz) {
		ArrayList<Field> fieldList = new ArrayList<Field>();
		getAllFieldsRecursive(clz, fieldList);
		if (fieldList.isEmpty())
			return new Field[0];
		else
			return fieldList.toArray(new Field[0]);
	}

	/**
	 * 获取类的所有非静态字段（包括父类）
	 * 
	 * @param clz
	 * @return
	 */
	public static Field[] getAllNonStaticFields(Class clz) {
		List<Field> fList = new ArrayList<Field>();
		getAllFieldsRecursive(clz, fList);
		List<Field> nonStaticFields = new LinkedList<Field>();
		for (Field f : fList) {
			if ((f.getModifiers() & Modifier.STATIC) == 0) {
				nonStaticFields.add(f);
			}
		}
		if (nonStaticFields.isEmpty())
			return new Field[0];
		else
			return nonStaticFields.toArray(new Field[0]);
	}

	/**
	 * 获取类的所有非静态字段，并把字段的Accessible设置为true
	 * 
	 * @param clz
	 * @return
	 */
	public static Field[] getDeclaredNonStaticFields(Class clz) {
		List<Field> nonStaticFields = new LinkedList<Field>();
		for (Field f : clz.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				nonStaticFields.add(f);
			}
		}
		if (nonStaticFields.isEmpty())
			return new Field[0];
		else
			return nonStaticFields.toArray(new Field[0]);
	}

	/**
	 * 获取类及所有父类的方法，如果找不到则抛出异常NoSuchMethodException
	 * 
	 * @param clz
	 *            - declaring class of the method
	 * @param methodName
	 *            - name of the method
	 * @param paramTypes
	 *            - method paramTypes
	 * @return requested method
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Class<? extends Object> clz,
			String methodName, Class[] paramTypes) throws NoSuchMethodException {
		if (clz == null)
			throw new NoSuchMethodException(methodName + "(" + paramTypes
					+ ") method does not exist ");
		try {
			return clz.getDeclaredMethod(methodName, paramTypes);
		} catch (NoSuchMethodException e) {
			return getMethod(clz.getSuperclass(), methodName, paramTypes);
		}
	}

	/**
	 * <p>
	 * Invokes method of a given obj and set of parameters
	 * </p>
	 * 
	 * @param obj
	 *            - Object for which the method is going to be executed. Special
	 *            if obj is of type java.lang.Class, method is executed as
	 *            static method of class given as obj parameter
	 * @param method
	 *            - name of the object
	 * @param params
	 *            - actual parameters for the method
	 * @return - result of the method execution
	 * 
	 *         <p>
	 *         If there is any problem with method invocation, a
	 *         RuntimeException is thrown
	 *         </p>
	 */
	public static Object invoke(Object obj, String method, Class[] params,
			Object[] args) {
		try {
			Class<?> clazz = null;
			if (obj instanceof Class) {
				clazz = (Class) obj;
			} else {
				clazz = obj.getClass();
			}
			Method methods = clazz.getMethod(method, params);
			if (obj instanceof Class) {
				return methods.invoke(null, args);
			} else {
				return methods.invoke(obj, args);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 是否基础类型、或基础类型包装类、或String类
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isPrimitiveOrString(Class<? extends Object> clz) {
		// TODO Auto-generated method stub
		if (clz.equals(String.class)) {
			return true;
		}
		if (clz.isPrimitive()) {
			return true;
		}
		// 判断是否是Wrap
		return clz.equals(Boolean.class) || clz.equals(Integer.class)
				|| clz.equals(Character.class) || clz.equals(Byte.class)
				|| clz.equals(Short.class) || clz.equals(Double.class)
				|| clz.equals(Long.class) || clz.equals(Float.class);
	}

	/**
	 * 是否自定义类
	 * 
	 * @param clz
	 * @return
	 */
	public static boolean isCustomizedModel(Class<? extends Object> clz) {
		// TODO Auto-generated method stub
		return clz != null && clz.getClassLoader() != null;
	}

	private static void getAllFieldsRecursive(Class clz, List<Field> fldLst) {
		if (clz.isPrimitive())
			return;
		for (Field f : clz.getDeclaredFields()) {
			f.setAccessible(true);
			fldLst.add(f);
		}
		if (clz.getSuperclass() == Object.class)
			return;
		else
			getAllFieldsRecursive(clz.getSuperclass(), fldLst);
	}
}
