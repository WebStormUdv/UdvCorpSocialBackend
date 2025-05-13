package ru.backend.UdvCorpSocialBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.backend.UdvCorpSocialBackend.auth.JwtUtil;

@SpringBootApplication
public class UdvCorpSocialBackendApplication {

	private static final Logger logger = LoggerFactory.getLogger(UdvCorpSocialBackendApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UdvCorpSocialBackendApplication.class, args);
		logger.info("http://localhost:8080/swagger-ui/index.html");
	}

}
