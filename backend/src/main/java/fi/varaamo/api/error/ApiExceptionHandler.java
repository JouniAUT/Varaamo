package fi.varaamo.api.error;

import fi.varaamo.config.TimeConfig;
import jakarta.validation.ConstraintViolationException;
import java.time.Clock;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	private final Clock clock;

	public ApiExceptionHandler(Clock clock) {
		this.clock = clock;
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
		return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
	}

	@ExceptionHandler(ConflictException.class)
	public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
		return build(HttpStatus.CONFLICT, "CONFLICT", ex.getMessage());
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex) {
		return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
		FieldError firstError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
		String message = firstError == null
				? "Validation failed"
				: (firstError.getField() + ": " + firstError.getDefaultMessage());
		return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", message);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
		return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", ex.getMessage());
	}

	private ResponseEntity<ApiError> build(HttpStatus status, String code, String message) {
		ZonedDateTime timestamp = ZonedDateTime.now(clock).withZoneSameInstant(TimeConfig.HELSINKI);
		return ResponseEntity.status(status).body(new ApiError(code, message, timestamp));
	}
}
