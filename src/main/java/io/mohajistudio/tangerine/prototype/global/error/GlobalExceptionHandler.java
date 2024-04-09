package io.mohajistudio.tangerine.prototype.global.error;

import io.jsonwebtoken.JwtException;
import io.mohajistudio.tangerine.prototype.global.enums.ErrorCode;
import io.mohajistudio.tangerine.prototype.global.error.exception.BusinessException;
import io.mohajistudio.tangerine.prototype.global.error.exception.UrlNotFoundException;
import io.mohajistudio.tangerine.prototype.infra.webhook.dto.DiscordWebhookDTO;
import io.mohajistudio.tangerine.prototype.infra.webhook.dto.EmbedObjectDTO;
import io.mohajistudio.tangerine.prototype.infra.webhook.dto.FieldDTO;
import io.mohajistudio.tangerine.prototype.infra.webhook.service.WebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.net.BindException;
import java.util.List;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final WebhookService webhookService;
    private final Environment environment;

    /**
     * javax.validation.Valid or @Validated 으로 binding error 발생시 발생한다.
     * HttpMessageConverter 에서 등록한 HttpMessageConverter binding 못할경우 발생
     * 주로 @RequestBody, @RequestPart 어노테이션에서 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest httpServletRequest) {
        log.error(e.getMessage());
        log.error(String.valueOf(e.getBindingResult()));
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * @ModelAttribute 으로 binding error 발생시 BindException 발생한다.
     * ref https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-modelattrib-method-args
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest httpServletRequest) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * enum type 일치하지 않아 binding 못할 경우 발생
     * 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest httpServletRequest) {
        final ErrorResponse response = ErrorResponse.of(e);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest httpServletRequest) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest httpServletRequest) {
        log.error("handleHttpMessageNotReadableException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.HTTP_MESSAGE_NOT_READABLE);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.HTTP_MESSAGE_NOT_READABLE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest httpServletRequest) {
        log.error("handleMissingServletRequestParameterException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_PARAMETER, e.getParameterName());
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.MISSING_PARAMETER);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest httpServletRequest) {
        log.error("BusinessException", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, e.getMessage());
        sendWebhook(httpServletRequest, e.getMessage(), errorCode);
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest httpServletRequest) {
        log.error("handleEntityNotFoundException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest httpServletRequest) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.URL_NOT_FOUND);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.URL_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(DataIntegrityViolationException e, HttpServletRequest httpServletRequest) {
        log.error("handleInvalidDataAccessApiUsageException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.DATA_INTEGRITY_VIOLATION);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.DATA_INTEGRITY_VIOLATION);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(IllegalArgumentException e, HttpServletRequest httpServletRequest) {
        log.error("handleIllegalArgumentException", e);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_ARGUMENT);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.ILLEGAL_ARGUMENT);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleUrlNotFoundException(UrlNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.URL_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchKeyException.class)
    protected ResponseEntity<ErrorResponse> handleNoSuchKeyException(NoSuchKeyException e, HttpServletRequest httpServletRequest) {
        log.error(e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NO_SUCH_KEY);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.ILLEGAL_ARGUMENT);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    protected ResponseEntity<ErrorResponse> handleNoSuchKeyException(HttpMessageConversionException e, HttpServletRequest httpServletRequest) {
        log.error(e.getMessage());
        log.error(String.valueOf(e.getCause()));
        final ErrorResponse response = ErrorResponse.of(ErrorCode.HTTP_MESSAGE_CONVERSION);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.HTTP_MESSAGE_CONVERSION);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(MissingServletRequestPartException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException e, HttpServletRequest httpServletRequest) {
        log.error(e.getMessage());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_SERVLET_REQUEST_PART);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.MISSING_SERVLET_REQUEST_PART);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<ErrorResponse> handleMJwtException(JwtException e, HttpServletRequest httpServletRequest) {
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NO_PERMISSION);
        sendWebhook(httpServletRequest, e.getMessage(), ErrorCode.NO_PERMISSION);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    public void sendWebhook(HttpServletRequest httpServletRequest, String errorMessage, ErrorCode errorCode) {
        if ("prod".equals(environment.getActiveProfiles()[0])) {
            FieldDTO methodField = FieldDTO.createInlineField("Method", httpServletRequest.getMethod());
            FieldDTO endpointField = FieldDTO.createInlineField("Endpoint", httpServletRequest.getRequestURI());
            FieldDTO clientIpField = FieldDTO.createInlineField("ClientIp", httpServletRequest.getRemoteAddr());

            FieldDTO errorCodeField = FieldDTO.createInlineField("ErrorCode", errorCode.getCode());
            FieldDTO statusCodeField = FieldDTO.createInlineField("StatusCode", String.valueOf(errorCode.getStatus()));
            FieldDTO errorMessageField = FieldDTO.createField("ErrorMessage", errorCode.getMessage());
            EmbedObjectDTO embedObjectDTO = EmbedObjectDTO.createErrorEmbedObject("ErrorMessage", errorMessage, List.of(methodField, endpointField, clientIpField, errorCodeField, statusCodeField, errorMessageField));
            DiscordWebhookDTO discordWebhookDTO = DiscordWebhookDTO.createErrorMessage("Spring Boot Server Error", List.of(embedObjectDTO));
            webhookService.sendMessage(discordWebhookDTO);
        }
    }
}
