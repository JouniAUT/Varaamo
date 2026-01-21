package fi.varaamo.api.error;

import java.time.ZonedDateTime;

public record ApiError(
		String code,
		String message,
		ZonedDateTime timestamp
) {
}
