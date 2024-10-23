package com.abdi.ads.manager.api.web.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
	@Override
	public boolean isValid(MultipartFile multipartFile,
			ConstraintValidatorContext constraintValidatorContext) {
		if (multipartFile == null) {
			return false;
		}

		String contentType = multipartFile.getContentType();
		log.debug("Validating file {}", contentType);
		return isSupportedContentType(contentType);
	}

	private boolean isSupportedContentType(String contentType) {
		return contentType.equals("application/json");
	}
}