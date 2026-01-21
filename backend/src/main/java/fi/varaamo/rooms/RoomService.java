package fi.varaamo.rooms;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.varaamo.api.error.BadRequestException;

@Service
public class RoomService {

	private final RoomRepository roomRepository;

	public RoomService(RoomRepository roomRepository) {
		this.roomRepository = roomRepository;
	}

	@Transactional
	public Room create(String name, int capacity) {
		if (name == null || name.isBlank()) {
			throw new BadRequestException("name is required");
		}
		if (capacity <= 0) {
			throw new BadRequestException("capacity must be greater than zero");
		}
		String trimmedName = name.trim();
		if (roomRepository.findByName(trimmedName).isPresent()) {
			throw new BadRequestException("Room name already exists");
		}
		return roomRepository.save(new Room(trimmedName, capacity));
	}

	@Transactional(readOnly = true)
	public List<Room> list() {
		return roomRepository.findAll();
	}
}
