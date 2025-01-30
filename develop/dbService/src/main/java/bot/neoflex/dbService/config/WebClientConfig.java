package bot.neoflex.dbService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Конфигурация WebClient для взаимодействия с микросервисом API.
 */
@Configuration
public class WebClientConfig {

    @Value("${api.service.url}")
    private String apiServiceUrl;

    @Bean
    public WebClient apiWebClient() {
        return WebClient.builder()
                .baseUrl(apiServiceUrl)
                .build();
    }
}
