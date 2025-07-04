package com.readforce.common.enums;

public enum ExpireTime {
	
	DEFAULT(3),
	MARK_EMAIL_AS_VERIFIED(10)
	;
	
	private final long expireTime;
	
	ExpireTime(long expireTime){
		
		this.expireTime = expireTime;
		
	}
	
	public long getTime() {
		
		return expireTime;
		
	}

}
