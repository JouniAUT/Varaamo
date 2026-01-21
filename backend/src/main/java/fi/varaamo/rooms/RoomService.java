package fi.varaamo.rooms;

import fi.varaamo.api.error.BadRequestException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

	private final RoomRepository roomRepository;

	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	@Transactional
	public Room create(String name) {
		if (name == null || name.isBlank()) {
			throw new BadRequestException("name is required");
		}
		String trimmedName = name.trim();
		if (roomRepository.findByName(trimmedName).isPresent()) {
			throw new BadRequestException("Room name already exists");
		}
		return roomRepository.save(new Room(trimmedName));
	}

	@Transactional(readOnly = true)
	public List<Room> list() {
		return roomRepository.findAll();
	}
}
