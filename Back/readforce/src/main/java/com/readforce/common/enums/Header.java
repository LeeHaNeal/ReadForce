package com.readforce.common.enums;

public enum Header {

	X_FORWARDED_FOR("X-Forwarded-For"),
	AUTHORIZATION("Authorization")
	;
	
	private final String header;
	
	Header(String header){
		
		this.header = header;
		
	}
	
	public String getContent() {
		
		return header;
		
	}
	
}
