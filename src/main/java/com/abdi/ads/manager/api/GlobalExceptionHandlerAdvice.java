package com.abdi.ads.manager.api;


import java.util.List;

import com.abdi.ads.manager.dtos.FieldValidation;
import com.abdi.ads.manager.dtos.Response;
import com.abdi.ads.manager.exceptions.DataNotFoundException;
import com.abdi.ads.manager.exceptions.GlobalException;
import com.abdi.ads.manager.exceptions.NotAccessException;
import com.abdi.ads.manager.services.MessageComponent;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandlerAdvice {

	private final MessageComponent messageComponent;

	@ExceptionHandler({ GlobalException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Response<Object> internalErrException(GlobalException exception, HandlerMethod handlerMethod, ServletWebRequest request) {

		return Response.builder()
				.code(exception.getStatusCode())
				.message(exception.getStatusMessage())
				.path(getPath(request))
				.build();
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public Response<Object> methodArgumentNotValid(MethodArgumentNotValidException exception, HandlerMethod handlerMethod, ServletWebRequest request) {

		List<FieldValidation> notValidFields = exception.getBindingResult().getFieldErrors().stream()
				.map(err -> new FieldValidation(err.getField(), err.getDefaultMessage()))
				.distinct()
				.toList();

		return Response.builder()
				.code(messageComponent.getInputValidationCode())
				.message(messageComponent.getInputValidationMessage())
				.error(notValidFields)
				.path(getPath(request))
				.build();
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	Response<Object> handleConstraintViolationException(ConstraintViolationException e, ServletWebRequest request) {
		return Response.builder()
				.code(HttpStatus.BAD_REQUEST.value())
				.message(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.error("Validation error: " + e.getMessage())
				.path(getPath(request))
				.build();
	}

	@ExceptionHandler({ NotAccessException.class })
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ResponseBody
	public Response<Object> notAccessExceptionExceptionHandler(NotAccessException exception, HandlerMethod handlerMethod, ServletWebRequest request) {

		return Response.builder()
				.code(messageComponent.getForbiddenCode())
				.message(messageComponent.getForbiddenMessage())
				.path(getPath(request))
				.build();
	}

	@ExceptionHandler({ DataNotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public Response<Object> resourceNotFoundExceptionHandler(DataNotFoundException exception, HandlerMethod handlerMethod, ServletWebRequest request) {

		return Response.builder()
				.code(messageComponent.getNotfoundCode())
				.message(messageComponent.getNotfoundMessage())
				.path(getPath(request))
				.build();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public final Response<Object> handleGeneralException(Exception throwable, ServletWebRequest request) {
		log.error(messageComponent.getInternalServerErrorMessage(), throwable);
		return Response.builder()
				.code(messageComponent.getInternalServerErrorCode())
				.message(messageComponent.getInternalServerErrorMessage())
				.path(getPath(request))
				.build();
	}

	private String getPath(ServletWebRequest request) {
		return request.getRequest().getRequestURI();
	}
}