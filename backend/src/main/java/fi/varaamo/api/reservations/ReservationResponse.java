package fi.varaamo.api.reservations;

import java.time.ZonedDateTime;

public record ReservationResponse(
		Long id,
		Long roomId,
		ZonedDateTime startsAt,
		ZonedDateTime endsAt,
		String title,
		int participantCount
) {
}
