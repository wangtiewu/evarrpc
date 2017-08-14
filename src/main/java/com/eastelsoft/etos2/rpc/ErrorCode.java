package com.eastelsoft.etos2.rpc;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ErrorCode {
	public final static String ECODE_SUCCESS = "0";// 成功
	public final static String ECODE_SYSBUSI = "-1";// 系统繁忙
	public final static String ECODE_SYSFAIL = "-2";// 系统内部错误
	public final static String ECODE_INTERF_LIMIT = "-3";// 接口调用次数超过限制
	public final static String ECODE_DAO_EXCEPTION = "-10";// 数据异常
	public final static String ECODE_SERVICE_EXCEPTION = "-11";// 业务异常
	public final static String ECODE_PARAM_MISSING = "100";// 缺少参数 {0}
	public final static String ECODE_PARAM_FORMAT_ERR = "101";// 参数 {0}
																// 格式错误，输入值为{1}，合法值为
																// {2}
	public final static String ECODE_PARAM_NOTEXIST = "102";// {0} {1} 不存在
	public final static String ECODE_TIMEOUT = "110";// 操作 {0} 超时，超时时间 {1}{2}
	public final static String ECODE_VERSION_INVALID = "120";// 版本 {0} 非法（或已下线）
	public final static String ECODE_QUOTA_INSUFFICIENT = "130";// 配额不足
	public final static String ECODE_IP_LIMITED = "200";// ip地址非法
	public final static String ECODE_SIGN_ERROR = "400";// sign签名错误
	public final static String ECODE_INTF_ERROR = "500";// 接口调用失败，连接异常或者接口响应异常
	public final static String ECODE_TOKEN_INVALID = "1001";// token{}不合法
	public final static String ECODE_APP_INVALID = "1002";// 拒绝服务，无法识别的客户端或者网关
	public final static String ECODE_PASSWD_CONTAIN_INVALID_LETTER = "1003";// 密码中包含非法字符
	public final static String ECODE_PASSWD_LEN_INVALID = "1004";// 密码长度不符合要求，密码太短或超长
	public final static String ECODE_PASSWD_SO_EASY = "1005";// 密码强度不达要求
	public final static String ECODE_ACCOUNT_ALREADY_EXISTS = "1006";// 用户帐号已经存在
	public final static String ECODE_ACCOUNT_OR_PASSWD_ERR = "1007";// 帐号或密码错误
	public final static String ECODE_VCODE_INVALID = "1008";// 验证码{0} 不存在或已过期
	public final static String ECODE_ACCOUNT_NOT_VERIFIED = "1009";// 帐号未激活
	public final static String ECODE_ACCOUNT_NOT_EXIST = "1010";// 帐号不存在
	public final static String ECODE_ACCOUNT_CANCELED = "1011";// 帐号已注销
	public final static String ECODE_ACCOUNT_LEN_INVALID = "1400";// 用户帐号长度太短或超长
	public final static String ECODE_ACCOUNT_CONTAIN_INVALID_LETTER = "1401";// 用户帐号含有非法字
	public final static String ECODE_APP_NOT_EXIST = "1450";// app {0} 不存在
	public final static String ECODE_APP_OR_KEY_INVALID = "1451";// appid或appkey错误
	public final static String ECODE_CODE_INVALID = "1452";// code {0} 非法或已过期
	public final static String ECODE_REFRESS_TOKEN_INVALID = "1453";// refresh_token{0}非法或已过期
	public final static String ECODE_ACCESS_TOKEN_INVALID = "1454";// access_token{0}非法或已过期
	public final static String ECODE_OPENID_INVALID = "1455";// openId{0}非法
	protected final static Map<String, String> errorCodeMap = new HashMap<String, String>();
	static {
		// 0
		errorCodeMap.put(ECODE_SUCCESS, "成功");
		// -1
		errorCodeMap.put(ECODE_SYSBUSI, "系统繁忙");
		// -2
		errorCodeMap.put(ECODE_SYSFAIL, "系统内部错误");
		// -3
		errorCodeMap.put(ECODE_INTERF_LIMIT, "接口调用次数超过限制");
		// -10
		errorCodeMap.put(ECODE_DAO_EXCEPTION, "数据异常（数据不存在、重复或者值错误）");
		// -11
		errorCodeMap.put(ECODE_SERVICE_EXCEPTION, "业务异常");
		// 100
		errorCodeMap.put(ECODE_PARAM_MISSING, "缺少参数 {0}");
		// 101
		errorCodeMap.put(ECODE_PARAM_FORMAT_ERR,
				"参数 {0} 格式错误，输入值为 {1}，合法值为 {2}");
		// 102
		errorCodeMap.put(ECODE_PARAM_NOTEXIST, "{0} {1} 不存在");
		// 110
		errorCodeMap.put(ECODE_TIMEOUT, "操作 {0} 超时，超时时间 {1}{2}");
		// 120
		errorCodeMap.put(ECODE_VERSION_INVALID, "当前版本 {0} 过低，支持的最低版本是 {1}");
		// 130
		errorCodeMap.put(ECODE_QUOTA_INSUFFICIENT, "配额不足");
		// 200
		errorCodeMap.put(ECODE_IP_LIMITED, "ip地址非法");
		// 400
		errorCodeMap.put(ECODE_SIGN_ERROR, "sign签名错误");
		// 500
		errorCodeMap.put(ECODE_INTF_ERROR, "接口调用失败");
		// 1001
		errorCodeMap.put(ECODE_TOKEN_INVALID, "Token {0} 非法");
		// 1002
		errorCodeMap.put(ECODE_APP_INVALID, "拒绝服务，无法识别的客户端或者网关");
		// 1003
		errorCodeMap.put(ECODE_PASSWD_CONTAIN_INVALID_LETTER, "密码中包含非法字符");
		// 1004
		errorCodeMap.put(ECODE_PASSWD_LEN_INVALID, "密码长度不符合要求，密码太短或超长");
		// 1005
		errorCodeMap.put(ECODE_PASSWD_SO_EASY, "密码强度不达要求");
		// 1006
		errorCodeMap.put(ECODE_ACCOUNT_ALREADY_EXISTS, "帐号已经存在");
		// 1007
		errorCodeMap.put(ECODE_ACCOUNT_OR_PASSWD_ERR, "帐号或密码错误");
		// 1008
		errorCodeMap.put(ECODE_VCODE_INVALID, "验证码{0} 不存在或已过期");
		// 1009
		errorCodeMap.put(ECODE_ACCOUNT_NOT_VERIFIED, "帐号未激活");
		// 1010
		errorCodeMap.put(ECODE_ACCOUNT_NOT_EXIST, "帐号不存在");
		// 1011
		errorCodeMap.put(ECODE_ACCOUNT_CANCELED, "帐号已注销");
		// 1400
		errorCodeMap.put(ECODE_ACCOUNT_LEN_INVALID, "用户帐号长度太短或超长");
		// 1401
		errorCodeMap.put(ECODE_ACCOUNT_CONTAIN_INVALID_LETTER, "用户帐号含有非法字");
		// 1450
		errorCodeMap.put(ECODE_APP_NOT_EXIST, "app {0} 不存在");
		// 1451
		errorCodeMap.put(ECODE_APP_OR_KEY_INVALID, "appid或appkey错误");
		// 1452
		errorCodeMap.put(ECODE_CODE_INVALID, "code {0} 非法或已过期");
		// 1453
		errorCodeMap.put(ECODE_REFRESS_TOKEN_INVALID,
				"refresh_token {0} 非法或已过期");
		// 1454
		errorCodeMap.put(ECODE_ACCESS_TOKEN_INVALID, "access_token {0} 非法或已过期");
		// 1455
		errorCodeMap.put(ECODE_OPENID_INVALID, "openId {0} 非法");
	}

	public static String getEmsg(String ecode, String... arguments) {
		String emsg = errorCodeMap.get(ecode);
		if (emsg == null) {
			emsg = "错误描述信息未定义";
			return emsg;
		}
		if (arguments.length > 0) {
			return MessageFormat.format(emsg, arguments);
		} else {
			return emsg;
		}
	}

	public static String getEmsg(String ecode) {
		String emsg = errorCodeMap.get(ecode);
		if (emsg == null) {
			emsg = "错误描述信息未定义";
		}
		return emsg;
	}

	public static <T> T setErrorInfo(RpcResponse rpcResponse,
			String ecode, String... arguments) {
		rpcResponse.setEcode(ecode);
		rpcResponse.setEmsg(getEmsg(ecode, arguments));
		return (T) rpcResponse;
	}
}
