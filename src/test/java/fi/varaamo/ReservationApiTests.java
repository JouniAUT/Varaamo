package fi.varaamo;

import fi.varaamo.config.TimeConfig;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
		String roomId = createRoom("Neuvotteluhuone A");

		createReservation(roomId, "2026-01-21T11:00:00+02:00", "2026-01-21T12:00:00+02:00", "First");

		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"2026-01-21T11:30:00+02:00\"," +
							"\"endsAt\":\"2026-01-21T12:30:00+02:00\"," +
							"\"title\":\"Overlapping\"" +
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
							"\"title\":\"Touching\"" +
							"}"
					))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.roomId").value(Integer.parseInt(roomId)))
				.andExpect(jsonPath("$.title").value("Touching"));
	}

	@Test
	void rejectsPastReservations_andStartAfterEnd() throws Exception {
		String roomId = createRoom("Neuvotteluhuone B");

		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"2026-01-21T09:00:00+02:00\"," +
							"\"endsAt\":\"2026-01-21T10:00:00+02:00\"," +
							"\"title\":\"Past\"" +
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
							"\"title\":\"InvalidOrder\"" +
							"}"
					))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value("BAD_REQUEST"));
	}

	private String createRoom(String name) throws Exception {
		String response = mockMvc.perform(post("/rooms")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"name\":\"" + name + "\"}"))
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

	private void createReservation(String roomId, String startsAt, String endsAt, String title) throws Exception {
		mockMvc.perform(post("/reservations")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{" +
							"\"roomId\":" + roomId + "," +
							"\"startsAt\":\"" + startsAt + "\"," +
							"\"endsAt\":\"" + endsAt + "\"," +
							"\"title\":\"" + title + "\"" +
							"}"
					))
				.andExpect(status().isCreated());
	}
}
