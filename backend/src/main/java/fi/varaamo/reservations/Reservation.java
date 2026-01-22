package fi.varaamo.reservations;

import java.time.ZonedDateTime;

import fi.varaamo.rooms.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservation")
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	@Column(nullable = false)
	private ZonedDateTime startsAt;

	@Column(nullable = false)
	private ZonedDateTime endsAt;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private int participantCount;

	protected Reservation() {
	}

	public Reservation(Room room, ZonedDateTime startsAt, ZonedDateTime endsAt, String title, int participantCount) {
		this.room = room;
		this.startsAt = startsAt;
		this.endsAt = endsAt;
		this.title = title;
		this.participantCount = participantCount;
	}

	public Long getId() {
		return id;
	}

	public Room getRoom() {
		return room;
	}

	public ZonedDateTime getStartsAt() {
		return startsAt;
	}

	public ZonedDateTime getEndsAt() {
		return endsAt;
	}

	public String getTitle() {
		return title;
	}

	public int getParticipantCount() {
		return participantCount;
	}
}
