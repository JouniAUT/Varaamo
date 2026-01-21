package fi.varaamo.reservations;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationQueryService {

	private final ReservationRepository reservationRepository;

	public ReservationQueryService(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@Transactional(readOnly = true)
	public List<Reservation> list(Long roomId) {
		if (roomId == null) {
			return reservationRepository.findAllByOrderByStartsAtAsc();
		}
		return reservationRepository.findAllByRoomIdOrderByStartsAtAsc(roomId);
	}
}
