package fi.varaamo.reservations;

import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	List<Reservation> findAllByOrderByStartsAtAsc();

	List<Reservation> findAllByRoomIdOrderByStartsAtAsc(Long roomId);

	@Query("select (count(r) > 0) from Reservation r where r.room.id = :roomId and r.startsAt < :endsAt and :startsAt < r.endsAt")
	boolean existsOverlappingInRoom(
			@Param("roomId") Long roomId,
			@Param("startsAt") ZonedDateTime startsAt,
			@Param("endsAt") ZonedDateTime endsAt
	);
}
