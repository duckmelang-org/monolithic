package umc.duckmelang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"umc.duckmelang.domain"})
@EnableScheduling
public class DuckmelangApplication {

	public static void main(String[] args) {
		SpringApplication.run(DuckmelangApplication.class, args);
	}
}
