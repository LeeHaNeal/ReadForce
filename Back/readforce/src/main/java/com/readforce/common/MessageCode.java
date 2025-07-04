package com.readforce.common;

public final class MessageCode {

	public static final String MESSAGE_CODE = "MESSAGE_CODE";
	
	
	
	
	public static final String EMAIL_NOT_BLANK = "NOT0001";
	public static final String PASSWORD_NOT_BLANK = "NOT0002";
	public static final String NICKNAME_NOT_BLANK = "NOT0003";
	public static final String BIRTHDAY_NOT_NULL = "NOT0004";
	public static final String FILE_NOT_NULL = "NOT0005";
	public static final String VERIFICATION_CODE_NOT_BLANK = "NOT0006";
	public static final String TEMPORAL_TOKEN_NOT_BLANK = "NOT0007";
	public static final String REFRESH_TOKEN_NOT_BLANK = "NOT0008";
	
	
	
	
	
	public static final String NICKNAME_SIZE_INVALID = "INV0001";
	public static final String NICKNAME_PATTERN_INVALID = "INV0002";
	public static final String PASSWORD_PATTERN_INVALID = "INV0003";
	public static final String EMAIL_PATTERN_INVALID = "INV0004";
	public static final String JWT_SECRET_KEY_INVALID = "INV0005";
	public static final String NEWS_LANGUAGE_INVALID = "INV0006";
	public static final String NOVEL_LANGUAGE_INVALID = "INV0007";
	public static final String FAIRY_TALE_LANGUAGE_INVALID = "INV0008";
	public static final String PASSWORD_SIZE_INVALID = "INV0009";
	public static final String BIRTHDAY_RANGE_INVALID = "INV0010";
	public static final String FILE_SIZE_INVALID = "INV0011";
	public static final String FILE_TYPE_INVALID = "INV0012";
	public static final String FILE_PATTERN_INVALID = "INV0013";

	
	
	
	
	public static final String SIGN_UP_SUCCESS = "SUC0001";
	public static final String SIGN_IN_SUCCESS = "SUC0002";
	public static final String SIGN_OUT_SUCCESS = "SUC0003";
	public static final String EMAIL_VERIFICATION_SUCCESS = "SUC0004";
	public static final String MEMBER_MODIFY_SUCCESS = "SUC0005";
	public static final String MEMBER_WITHDRAW_SUCCESS = "SUC0006";
	public static final String VERIFICATION_CODE_SEND_SUCCESS = "SUC0007";
	public static final String VERIFICATION_CODE_VERIFY_SUCCESS = "SUC0008";
	public static final String SEND_PASSWORD_RESET_LINK_SUCCESS = "SUC0009";
	public static final String PASSWORD_RESET_SUCCESS = "SUC0010";
	public static final String PROFILE_IMAGE_UPLOAD_SUCCESS = "SUC0011";
	public static final String PROFILE_IMAGE_DELETE_SUCCESS = "SUC0012";
	public static final String REISSUE_ACCESS_TOKEN_SUCCESS = "SUC0013";
	public static final String GET_TOKENS_SUCCESS = "SUC0014";
	
	
	
	
	public static final String AUTHENTICATION_FAIL = "FAI0001";
	public static final String JSON_PROCESSING_FAIL = "FAI0002";
	public static final String DIRECTORY_CREATION_FAIL = "FAI0003";
	public static final String FILE_STORE_FAIL = "FAI0004";
	public static final String FILE_LOAD_FAIL = "FAI0005";
	public static final String FILE_DELETE_FAIL = "FAI0006";
	public static final String FILE_DELETE_IO_FAIL = "FAI0007";
	public static final String VERIFY_VERIFICATION_CODE_FAIL = "FAI0008";
	public static final String JSON_MAPPING_FAIL = "FAI0009";
	
	
	
	public static final String MEMBER_NOT_FOUND = "NOF0001";
	public static final String FILE_NOT_FOUND = "NOF0002";
	public static final String PROFILE_IMAGE_NOT_FOUND = "NOF0003";
	public static final String MEMBER_RESULT_NOT_FOUND = "NOF0004";
	
	
	

	
	
	
	
	
	public static final String SOCIAL_EMAIL_ALREADY_CONNECTED = "DUP0001";
	public static final String SOCIAL_EMAIL_ALREADY_USED = "DUP0002";
	public static final String TODAY_ALREADY_ATTENDANCE = "DUP0003";
	public static final String NEWS_ENGLISH_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0004";
	public static final String NEWS_KOREAN_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0005";
	public static final String NEWS_JAPANESE_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0006";
	public static final String NOVEL_ENGLISH_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0007";
	public static final String NOVEL_KOREAN_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0008";
	public static final String NOVEL_JAPANESE_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0009";
	public static final String FAIRY_TALE_ENGLISH_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0010";
	public static final String FAIRY_TALE_KOREAN_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0011";
	public static final String FAIRY_TALE_JAPANESE_CHALLENGE_ALREDY_ATTEMPTED_TODAY = "DUP0012";
	public static final String EMAIL_ALREADY_USED = "DUP0013";
	public static final String NICKNAME_ALREADY_USED = "DUP0014";
	
	
	
	public static final String ACCESS_TOKEN_EXPIRED = "SPE0001";
	public static final String REQUEST_ATTRIBUTES_IS_NULL_AND_CANNOT_CHECK_STATE = "SPE0002";
	public static final String EMAIL_REQUEST_LIMIT_EXCEEDED = "SPE0003";
	public static final String IP_ADDRESS_REQUEST_LIMIT_EXCEEDED = "SPE0004";
	public static final String EMAIL_CAN_USE = "SPE0005";
	public static final String NICKNAME_CAN_USE = "SPE0006";
	public static final String EMAIL_VERIFICATION_REQUIRED = "SPE0007";
	public static final String FILE_CATEGORY_REQUIRED = "SPE0008";
	
	
	
	
}
