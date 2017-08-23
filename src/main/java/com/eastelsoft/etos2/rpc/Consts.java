package com.eastelsoft.etos2.rpc;

/**
 * 定义相关常量
 * 
 * @author eastelsoft
 *
 */
public class Consts {
	public enum SerializeType {
		JDK_NATIVE("jdknative"), PROTOSTUFF("protostuff"), PROTOBUF("protobuf");

		private String value;

		public String value() {
			return value;
		}

		private SerializeType(String value) {
			this.value = value;
		}

		public static SerializeType from(String value) {
			if ("jdknative".equals(value)) {
				return JDK_NATIVE;
			}
			if ("protostuff".equals(value)) {
				return PROTOSTUFF;
			}
			if ("protobuf".equals(value)) {
				return PROTOBUF;
			}
			return null;
		}

		public static String enumsToString() {
			StringBuffer sb = new StringBuffer();
			for (SerializeType enum2 : SerializeType.values()) {
				sb.append(enum2.value).append("|");
			}
			return sb.toString().length() > 0 ? sb.toString().substring(0,
					sb.toString().length() - 1) : sb.toString();
		}
	};

}
