package com.kolich.bolt.exceptions;

import com.kolich.common.KolichCommonException;

public class KolichBoltException extends KolichCommonException {

	private static final long serialVersionUID = -5789690611856086365L;
	
	public KolichBoltException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public KolichBoltException(Throwable cause) {
		super(cause);
	}
	
	public KolichBoltException(String message) {
		super(message);
	}

}
