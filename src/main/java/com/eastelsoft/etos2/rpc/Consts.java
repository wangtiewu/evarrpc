package com.eastelsoft.etos2.rpc;

/**
 * 定义相关常量
 * 
 * @author eastelsoft
 *
 */
public class Consts {
	public enum SerializeType {
		JDK_NATIVE("jdknative"), PROTOSTUFF("protostuff");

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

	public enum VOSInterfaceName {
		PAY("/etos/vos/pay"), ADDCALLEEWHITELIST(
				"/etos/vos/pay/addcalleewhitelist");
		private String value;

		public String value() {
			return value;
		}

		private VOSInterfaceName(String value) {
			this.value = value;
		}

		public static VOSInterfaceName from(String value) {
			if ("/etos/vos/pay".equals(value)) {
				return PAY;
			}
			if ("/etos/vos/pay/addcalleewhitelist".endsWith(value)) {
				return ADDCALLEEWHITELIST;
			}
			return null;
		}

		public static String enumsToString() {
			StringBuffer sb = new StringBuffer();
			for (VOSInterfaceName enum2 : VOSInterfaceName.values()) {
				sb.append(enum2).append("(").append(enum2.value).append(")")
						.append(",");
			}
			return sb.toString().length() > 0 ? sb.toString().substring(0,
					sb.toString().length() - 1) : sb.toString();
		}
	};

}
