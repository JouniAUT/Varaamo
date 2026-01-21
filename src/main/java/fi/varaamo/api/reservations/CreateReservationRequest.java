package fi.varaamo.api.reservations;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record CreateReservationRequest(
		@NotNull Long roomId,
		@NotNull ZonedDateTime startsAt,
		@NotNull ZonedDateTime endsAt,
		@NotBlank String title
) {
}
