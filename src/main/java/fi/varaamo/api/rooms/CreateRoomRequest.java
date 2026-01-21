package fi.varaamo.api.rooms;

import jakarta.validation.constraints.NotBlank;

public record CreateRoomRequest(
		@NotBlank String name
) {
}
