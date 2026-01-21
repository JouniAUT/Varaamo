package fi.varaamo.api.rooms;

import fi.varaamo.rooms.Room;
import fi.varaamo.rooms.RoomService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
public class RoomController {

	private final RoomService roomService;

	public RoomController(RoomService roomService) {
		this.roomService = roomService;
	}

	@PostMapping
	public ResponseEntity<RoomResponse> create(@Valid @RequestBody CreateRoomRequest request) {
		Room room = roomService.create(request.name());
		return ResponseEntity.created(URI.create("/rooms/" + room.getId()))
				.body(new RoomResponse(room.getId(), room.getName()));
	}

	@GetMapping
	public List<RoomResponse> list() {
		return roomService.list().stream()
				.map(room -> new RoomResponse(room.getId(), room.getName()))
				.toList();
	}
}
