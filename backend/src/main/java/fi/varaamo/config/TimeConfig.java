package fi.varaamo.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

	public static final ZoneId HELSINKI = ZoneId.of("Europe/Helsinki");

	@Bean
	public Clock clock() {
		return Clock.system(HELSINKI);
	}
}
