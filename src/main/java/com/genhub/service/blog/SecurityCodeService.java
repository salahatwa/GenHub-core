package com.genhub.service.blog;

public interface SecurityCodeService {
	String generateCode(String key, int type, String target);

	boolean verify(String key, int type, String code);
}
