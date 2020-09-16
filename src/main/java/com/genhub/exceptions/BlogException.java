
package com.genhub.exceptions;

public class BlogException extends RuntimeException {
	private static final long serialVersionUID = -7443213283905815106L;
	private int code;

	public BlogException() {
	}

	public BlogException(int code) {
		super("code=" + code);
		this.code = code;
	}

	public BlogException(String message) {
		super(message);
	}

	public BlogException(Throwable cause) {
		super(cause);
	}

	public BlogException(String message, Throwable cause) {
		super(message, cause);
	}

	public BlogException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
