package com.abdi.ads.manager.services;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MessageComponent {

	@Value("${success.code}")
	private int successCode;

	@Value("${success.message}")
	private String successMessage;

	@Value("${failed.code}")
	private int failedCode;

	@Value("${failed.message}")
	private String failedMessage;

	@Value("${input-validation.code}")
	private int inputValidationCode;

	@Value("${input-validation.message}")
	private String inputValidationMessage;

	@Value("${notfound.code}")
	private int notfoundCode;

	@Value("${notfound.message}")
	private String notfoundMessage;

	@Value("${internal-server-error.code}")
	private int internalServerErrorCode;

	@Value("${internal-server-error.message}")
	private String internalServerErrorMessage;

	@Value("${request_is_duplicated.code}")
	private int requestIsDuplicatedCode;

	@Value("${request_is_duplicated.message}")
	private String requestIsDuplicatedMessage;

	@Value("${forbidden.code}")
	private int forbiddenCode;

	@Value("${forbidden.message}")
	private String forbiddenMessage;
}