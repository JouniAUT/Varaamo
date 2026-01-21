package fi.varaamo.api.rooms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(
		@NotBlank String name,
		@NotNull Integer capacity
) {
}
