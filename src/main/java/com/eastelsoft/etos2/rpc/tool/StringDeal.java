package com.eastelsoft.etos2.rpc.tool;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 字符串处理类
 * 
 * @author willie
 * @version 1.0
 */
public class StringDeal {

	/**
	 * 判断是否是标点符号
	 * 
	 * @param c
	 * @return true，是标点符号；false，不是标点符号
	 */
	public static boolean isPunctuation(char c) {
		if (true) {
			Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
			if (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
					|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
					|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
					|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
					|| ub == Character.UnicodeBlock.VERTICAL_FORMS) {
				return true;
			} else {
				return false;
			}
		}

		int r = VerifyHelper.isMatch("[\\pP‘’“”]", String.valueOf(c));
		return r == 1 ? true : false;
	}

	/**
	 * 判断是否是中文字符
	 * 
	 * @param c
	 * @return true，是中文字符；false，不是正文字符
	 */
	public static boolean isChinise(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;

	}

	/**
	 * 判断是否为数字组成的字串
	 * 
	 * @param validString
	 *            要判断的字符串
	 * @return boolen值，true或false
	 */
	public static boolean isNumber(String validString) {
		if (validString == null || validString.equals("")) {
			return false;
		}
		if (validString.length() > 1 && validString.substring(0, 1).equals("-")) {
			validString = validString.substring(1);
		}
		byte[] tempbyte = validString.getBytes();
		for (int i = 0; i < validString.length(); i++) {
			if ((tempbyte[i] < 48) || (tempbyte[i] > 57)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否为只包括字母和数字
	 * 
	 * @param validString
	 *            要判断的字符串
	 * @return boolen值，true或false
	 */
	public static boolean isChar(String validString) {
		byte[] tempbyte = validString.getBytes();
		for (int i = 0; i < validString.length(); i++) {
			if ((tempbyte[i] < 48) || ((tempbyte[i] > 57) & (tempbyte[i] < 65))
					|| (tempbyte[i] > 122)
					|| ((tempbyte[i] > 90) & (tempbyte[i] < 97))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断字符串是否只包括字母
	 * 
	 * @param validString
	 *            要判断的字符串
	 * @return boolen值，true或false
	 */
	public static boolean isLetter(String validString) {
		byte[] tempbyte = validString.getBytes();
		for (int i = 0; i < validString.length(); i++) {
			if ((tempbyte[i] < 65) || (tempbyte[i] > 122)
					|| ((tempbyte[i] > 90) & (tempbyte[i] < 97))) {
				return false;
			}
		}
		return true;
	}

	public static String preFillZero(String asValue, int aiStrLen) {
		String lsRet = "";
		int liLen = 0;

		try {
			liLen = asValue.length();
			if (liLen < aiStrLen) {
				for (int i = 0; i < (aiStrLen - liLen); i++) {
					lsRet += "0";
				}
				lsRet += asValue;
			} else {
				lsRet = asValue.substring(liLen - aiStrLen, liLen);
			}
			return lsRet;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 把字符串按分割符分割成数组
	 * 
	 * @param source
	 *            要分割的字符串
	 * @param div
	 *            分割符
	 * @return 字符串数组
	 */
	public static String[] split(String source, String div) {
		if (source == null || div == null) {
			return null;
		}
		int arynum = 0, intIdx = 0, intIdex = 0, div_length = div.length();
		if (source.compareTo("") != 0) {
			if (source.indexOf(div) != -1) {
				intIdx = source.indexOf(div);
				for (int intCount = 1;; intCount++) {
					if (source.indexOf(div, intIdx + div_length) != -1) {
						intIdx = source.indexOf(div, intIdx + div_length);
						arynum = intCount;
					} else {
						arynum += 2;
						break;
					}
				}
			} else
				arynum = 1;
		} else
			arynum = 0;

		intIdx = 0;
		intIdex = 0;
		String[] returnStr = new String[arynum];

		if (source.compareTo("") != 0) {
			if (source.indexOf(div) != -1) {
				intIdx = (int) source.indexOf(div);
				returnStr[0] = (String) source.substring(0, intIdx);
				for (int intCount = 1;; intCount++) {
					if (source.indexOf(div, intIdx + div_length) != -1) {
						intIdex = (int) source
								.indexOf(div, intIdx + div_length);
						returnStr[intCount] = (String) source.substring(intIdx
								+ div_length, intIdex);
						intIdx = (int) source.indexOf(div, intIdx + div_length);
					} else {
						returnStr[intCount] = (String) source.substring(intIdx
								+ div_length, source.length());
						break;
					}
				}
			} else {
				returnStr[0] = (String) source.substring(0, source.length());
				return returnStr;
			}
		} else {
			return returnStr;
		}
		return returnStr;
	}

	/**
	 * 处理空值
	 * 
	 * @param str
	 *            要处理的字符串
	 * @return 处理后的字符串
	 */
	public static String dealNull(String str) {
		String returnstr = null;
		if (str == null)
			returnstr = "";
		else
			returnstr = str;
		return returnstr;
	}

	/**
	 * 用指定的字符串代替子字符串
	 * 
	 * @param sRepStr
	 *            要处理的字符串
	 * @param srcSub
	 *            被代替的子字符串
	 * @param dstSub
	 *            要代替的字符串
	 * @return 处理后的字符串
	 */
	static public String replace(String sRepStr, String srcSub, String dstSub) {
		return replace(sRepStr, srcSub, dstSub, 0);
	}

	/**
	 * 从指定的位置开始用指定的字符串代替子字符串
	 * 
	 * @param sRepStr
	 *            要处理的字符串
	 * @param srcSub
	 *            被代替的子字符串
	 * @param dstSub
	 *            要代替的字符串
	 * @param fromIndex
	 *            开始搜索的字符索引位置，包括该位置
	 * @return 处理后的字符串
	 */
	static public String replace(String sRepStr, String srcSub, String dstSub,
			int fromIndex) {
		int iFindStart = 0, iFromIndex = 0;
		int iSrcLen = 0, idstLen = 0;

		if (fromIndex < 0)
			iFromIndex = 0;
		else
			iFromIndex = fromIndex;
		iSrcLen = srcSub.length();
		idstLen = dstSub.length();
		if (dstSub.equals(srcSub))
			return sRepStr;
		while (true) {
			iFindStart = sRepStr.indexOf(srcSub, iFromIndex);
			if (iFindStart < 0)
				break;
			sRepStr = sRepStr.substring(0, iFindStart) + dstSub
					+ sRepStr.substring(iFindStart + iSrcLen);
			iFromIndex = iFindStart + idstLen;
		}
		return sRepStr;
	}

	/**
	 * 获得一致显示宽度的字符串
	 * 
	 * @param len
	 *            需要显示的长度(<font color="red">注意：长度是以byte为单位的，一个汉字是2个byte</font>)
	 * @param symbol
	 *            用于表示省略的信息的字符，如“...”,“>>>”等。
	 * @return 返回处理后的字符串
	 */
	public static String getLimitLengthString(String source, int len,
			String symbol) throws UnsupportedEncodingException {
		String encode = "GBK";
		int counterOfDoubleByte = 0;
		byte[] b = source.getBytes(encode);
		if (b.length <= len)
			return source;
		for (int i = 0; i < len; i++) {
			if (b[i] < 0)
				counterOfDoubleByte++;
		}
		if (counterOfDoubleByte % 2 == 0)
			return new String(b, 0, len, encode) + symbol;
		else
			return new String(b, 0, len - 1, encode) + symbol;
	}
	/*
	 * public static String replace(String str,String substr,String restr){
	 * String[] tmp = split(str,substr); String returnstr = null;
	 * if(tmp.length!=0) { returnstr = tmp[0]; for(int i = 0 ; i < tmp.length -
	 * 1 ; i++) returnstr =dealNull(returnstr) + restr +tmp[i+1]; } return
	 * dealNull(returnstr); }
	 */

}