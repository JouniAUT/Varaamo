package fi.varaamo.reservations;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.varaamo.api.error.BadRequestException;
import fi.varaamo.api.error.ConflictException;
import fi.varaamo.api.error.NotFoundException;
import fi.varaamo.config.TimeConfig;
import fi.varaamo.rooms.Room;
import fi.varaamo.rooms.RoomRepository;

@Service
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final RoomRepository roomRepository;
	private final Clock clock;

	public ReservationService(
			ReservationRepository reservationRepository,
			RoomRepository roomRepository,
			Clock clock
	) {
		this.reservationRepository = reservationRepository;
		this.roomRepository = roomRepository;
		this.clock = clock;
	}

	@Transactional
	public Reservation create(Long roomId, ZonedDateTime startsAt, ZonedDateTime endsAt, String title, int participantCount) {
		if (startsAt == null || endsAt == null) {
			throw new BadRequestException("startsAt and endsAt are required");
		}

		ZonedDateTime startsAtHelsinki = startsAt.withZoneSameInstant(TimeConfig.HELSINKI);
		ZonedDateTime endsAtHelsinki = endsAt.withZoneSameInstant(TimeConfig.HELSINKI);

		if (!startsAtHelsinki.isBefore(endsAtHelsinki)) {
			throw new BadRequestException("startsAt must be before endsAt");
		}

		ZonedDateTime now = ZonedDateTime.now(clock).withZoneSameInstant(TimeConfig.HELSINKI);
		if (startsAtHelsinki.isBefore(now)) {
			throw new BadRequestException("Reservation can't start in the past");
		}

		Room room = roomRepository.findById(roomId)
				.orElseThrow(() -> new NotFoundException("Room not found: " + roomId));

		if (participantCount > room.getCapacity()) {
			throw new BadRequestException("Participant count exceeds room capacity");
		}

		boolean overlaps = reservationRepository.existsOverlappingInRoom(room.getId(), startsAtHelsinki, endsAtHelsinki);
		if (overlaps) {
			throw new ConflictException("Reservation overlaps an existing reservation in the room");
		}

		Reservation reservation = new Reservation(room, startsAtHelsinki, endsAtHelsinki, title, participantCount);
		return reservationRepository.save(reservation);
	}

	@Transactional(readOnly = true)
	public Reservation get(Long id) {
		return reservationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Reservation not found: " + id));
	}

	@Transactional
	public void delete(Long id) {
		if (!reservationRepository.existsById(id)) {
			throw new NotFoundException("Reservation not found: " + id);
		}
		reservationRepository.deleteById(id);
	}
}
