package com.eastelsoft.etos2.rpc.tool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;

public class VerifyHelper {
	private static Map<String, Pattern> container = new ConcurrentHashMap<String, Pattern>();

	private static Map<String, Pattern> container1 = new HashMap<String, Pattern>();

	private static final int MAX_PAN_SIZE = 10240;
	
	private static String PAT_CHINAMOILE_NUM = "^13[5-9][0-9]{8}$|^134[0-8][0-9]{7}$|^15[7-9][0-9]{8}$|^15[0-1][0-9]{8}$|^18[7-8][0-9]{8}$|^147[0-9]{8}$";

	private static String PAT_TELEMOILE_NUM = "^133[0-9]{8}$|^153[0-9]{8}$|^18[0,9][0-9]{8}$|^[(]?0[1-9][0-9]{1,3}[)]?[ -]?[2-9][0-9]{2,3}[ -]?[0-9]{4}$";

	private static String PAT_UNINMOILE_NUM = "^13[0-2][0-9]{8}$|^145[0-9]{8}$|^186[0-9]{8}$|^156[0-9]{8}$|^155[0-9]{8}$|^185[0-9]{8}$";

	{
		container1.put(PAT_CHINAMOILE_NUM, Pattern.compile(PAT_CHINAMOILE_NUM));
		container1.put(PAT_TELEMOILE_NUM, Pattern.compile(PAT_TELEMOILE_NUM));
		container1.put(PAT_UNINMOILE_NUM, Pattern.compile(PAT_UNINMOILE_NUM));
	}

	public static int isValidPasswd(String passwd) {
		return 0;
	}

	/**
	 * �Ƿ�Ϊ����
	 * 
	 * @param input
	 * @return 1��������0����������
	 */
	public static int isNumber(String input) {
		if (true) {
			if (input == null || input.equals("")) {
				return 0;
			}
			if (input.length() > 1 && input.substring(0, 1).equals("-")) {
				input = input.substring(1);
			}
			byte[] tempbyte = input.getBytes();
			for (int i = 0; i < input.length(); i++) {
				if ((tempbyte[i] < 48) || (tempbyte[i] > 57)) {
					return 0;
				}
			}
			return 1;
		}
		if (input == null || input.equals("")) {
			return 0;
		}
		for (int i = 0; i < input.length(); i++) {
			if (i == 0) {
				if (isMatch("^-{1}", input.substring(i, i + 1)) == 1
						&& input.length() < 2) {
					return 0;
				} else {
					if (isMatch("^[-|0-9]{1}", input.substring(i, i + 1)) == 0) {
						return 0;
					}
				}
			} else {
				if (isMatch("^[0-9]{1}", input.substring(i, i + 1)) == 0) {
					return 0;
				}
			}
		}
		return 1;
	}

	public static int isChinaMobileTerminalNum(String terminalNum) {
		return isMatchx(
				"^13[5-9][0-9]{8}$|^134[0-8][0-9]{7}$|^15[7-9][0-9]{8}$|^15[0-1][0-9]{8}$|^18[7-8][0-9]{8}$|^147[0-9]{8}$",
				terminalNum);
	}

	public static int isChinaUnicomTerminalNum(String terminalNum) {
		return isMatchx(
				"^13[0-2][0-9]{8}$|^145[0-9]{8}$|^186[0-9]{8}$|^156[0-9]{8}$|^155[0-9]{8}$|^185[0-9]{8}$",
				terminalNum);
	}

	public static int isChinaTelecomTerminalNum(String terminalNum) {
		return isMatchx(
				"^133[0-9]{8}$|^153[0-9]{8}$|^18[0,9][0-9]{8}$|^[(]?0[1-9][0-9]{1,3}[)]?[ -]?[2-9][0-9]{2,3}[ -]?[0-9]{4}$",
				terminalNum);
	}

	/**
	 * �����ַ��Ƿ�ƥ��������ʽ
	 * 
	 * @param pattern
	 * @param input
	 * @return 1��ƥ�䣻0����ƥ��
	 */
	public static int isMatch(String pattern, String input) {
		if (pattern == null || input == null) {
			return 0;
		}
		if (false) {
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(input);
			if (m.find()) {
				return 1;
			}
			return 0;
		}
		Pattern p = container.get(pattern);
		if (p == null) {
			if (container.size() > MAX_PAN_SIZE) {
				//System.out.println("VerifyHelper map size ����"+MAX_PAN_SIZE+", �����map");
				container.clear();
			}
			p = Pattern.compile(pattern);
			container.put(pattern, p);
		}
		Matcher m = p.matcher(input);
		if (m.find()) {
			return 1;
		}
		return 0;
	}

	private static int isMatchx(String pattern, String input) {
		Pattern p = container1.get(pattern);
		if (p == null) {
			p = Pattern.compile(pattern);
			container1.put(pattern, p);
		}
		Matcher m = p.matcher(input);
		if (m.find()) {
			return 1;
		}
		return 0;
	}
}
