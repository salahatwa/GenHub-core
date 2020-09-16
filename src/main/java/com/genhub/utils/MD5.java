package com.genhub.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;

public class MD5 {

	/**
	 * Md5 encryption of the string
	 *
	 * @param input
	 * @return md5
	 */
	public static String md5(String input) {
		byte[] code = null;
		try {
			code = MessageDigest.getInstance("md5").digest(input.getBytes());
		} catch (NoSuchAlgorithmException e) {
			code = input.getBytes();
		}
		BigInteger bi = new BigInteger(code);
		return bi.abs().toString(32).toUpperCase();
	}

	/**
	 * 对字符串进行Md5加密
	 *
	 * @param input 原文
	 * @param salt  随机数
	 * @return string
	 */
	public static String md5(String input, String salt) {
		if (StringUtils.isEmpty(salt)) {
			salt = "";
		}
		return md5(salt + md5(input));
	}

	/**
	 * File md5 calculation
	 *
	 * @param bytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5File(byte[] bytes) {
		byte[] code = new byte[0];
		try {
			code = MessageDigest.getInstance("md5").digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
		BigInteger bi = new BigInteger(code);
		return bi.abs().toString(32).toUpperCase();
	}

}
