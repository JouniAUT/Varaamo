package fi.varaamo.api.reservations;

import fi.varaamo.reservations.Reservation;
import fi.varaamo.reservations.ReservationQueryService;
import fi.varaamo.reservations.ReservationService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

	private final ReservationService reservationService;
	private final ReservationQueryService reservationQueryService;

	public ReservationController(ReservationService reservationService, ReservationQueryService reservationQueryService) {
		this.reservationService = reservationService;
		this.reservationQueryService = reservationQueryService;
	}

	@PostMapping
	public ResponseEntity<ReservationResponse> create(@Valid @RequestBody CreateReservationRequest request) {
		Reservation reservation = reservationService.create(
				request.roomId(),
				request.startsAt(),
				request.endsAt(),
				request.title()
		);
		return ResponseEntity.created(URI.create("/reservations/" + reservation.getId()))
				.body(toResponse(reservation));
	}

	@GetMapping("/{id}")
	public ReservationResponse get(@PathVariable Long id) {
		return toResponse(reservationService.get(id));
	}

	@GetMapping
	public List<ReservationResponse> list(@RequestParam(required = false) Long roomId) {
		return reservationQueryService.list(roomId).stream().map(this::toResponse).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		reservationService.delete(id);
		return ResponseEntity.noContent().build();
	}

	private ReservationResponse toResponse(Reservation reservation) {
		return new ReservationResponse(
				reservation.getId(),
				reservation.getRoom().getId(),
				reservation.getStartsAt(),
				reservation.getEndsAt(),
				reservation.getTitle()
		);
	}
}
