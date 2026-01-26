package fi.varaamo;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import fi.varaamo.config.TimeConfig;

@SpringBootTest
@AutoConfigureMockMvc
class ReservationApiTests {

	@TestConfiguration
	static class FixedClockConfig {
		@Bean
		@Primary
		Clock fixedClock() {
			ZoneId helsinki = TimeConfig.HELSINKI;
			Instant fixedInstant = Instant.parse("2026-01-21T08:00:00Z"); // 10:00 in Helsinki (EET)
			return Clock.fixed(fixedInstant, helsinki);
		}
	}

	@org.springframework.beans.factory.annotation.Autowired
	private MockMvc mockMvc;

	@Test
	void createsReservation_andRejectsOverlap_butAllowsTouching() throws Exception {
		String roomId = createRoom("Neuvotteluhuone A", 10);

		createReservation(roomId, "2026-01-21T11:00:00+02:00", "2026-01-21T12:00:00+02:00", "First", 5);

		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"2026-01-21T11:30:00+02:00\"," +
							"\"endsAt\":\"2026-01-21T12:30:00+02:00\"," +
							"\"title\":\"Overlapping\"," +
							"\"participantCount\":5" +
							"}"
					))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.code").value("CONFLICT"));

		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"2026-01-21T12:00:00+02:00\"," +
							"\"endsAt\":\"2026-01-21T13:00:00+02:00\"," +
							"\"title\":\"Touching\"," +
							"\"participantCount\":5" +
							"}"
					))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.roomId").value(Integer.parseInt(roomId)))
				.andExpect(jsonPath("$.title").value("Touching"));
	}

	@Test
	void rejectsPastReservations_andStartAfterEnd() throws Exception {
		String roomId = createRoom("Neuvotteluhuone B", 10);

		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"2026-01-21T09:00:00+02:00\"," +
							"\"endsAt\":\"2026-01-21T10:00:00+02:00\"," +
							"\"title\":\"Past\"," +
							"\"participantCount\":5" +
							"}"
					))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", anyOf(is("BAD_REQUEST"), is("VALIDATION_FAILED"))));

		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"2026-01-21T12:00:00+02:00\"," +
							"\"endsAt\":\"2026-01-21T11:00:00+02:00\"," +
							"\"title\":\"InvalidOrder\"," +
							"\"participantCount\":5" +
							"}"
					))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"));
	}

	@Test
	void rejectsReservationWhenParticipantCountExceedsCapacity() throws Exception {
		String roomId = createRoom("Neuvotteluhuone C", 2);

		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"2026-01-21T11:00:00+02:00\"," +
							"\"endsAt\":\"2026-01-21T12:00:00+02:00\"," +
							"\"title\":\"TooManyParticipants\"," +
							"\"participantCount\":3" +
						"}"
					))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"))
				.andExpect(jsonPath("$.message", containsString("capacity")));
	}

	private String createRoom(String name, int capacity) throws Exception {
		String response = mockMvc.perform(post("/rooms")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"name\":\"" + name + "\"," +
							"\"capacity\":" + capacity +
					"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.name").value(name))
				.andReturn()
				.getResponse()
				.getContentAsString();

		// Extract id in a minimal way to avoid pulling extra test deps
		int idIndex = response.indexOf("\"id\":");
		int commaIndex = response.indexOf(',', idIndex);
		String idValue = response.substring(idIndex + 5, commaIndex).trim();
		return idValue;
	}

	private void createReservation(String roomId, String startsAt, String endsAt, String title, int participantCount) throws Exception {
		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"" + startsAt + "\"," +
							"\"endsAt\":\"" + endsAt + "\"," +
							"\"title\":\"" + title + "\"," +
							"\"participantCount\":" + participantCount +
							"}"
					))
				.andExpect(status().isCreated());
	}
}
