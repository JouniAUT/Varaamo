package fi.varaamo.api.reservations;

import java.time.ZonedDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
		@NotNull Long roomId,
		@NotNull ZonedDateTime startsAt,
		@NotNull ZonedDateTime endsAt,
		@NotBlank String title,
		@Min(1) int participantCount
) {
}
