package bot.neoflex.apiService.client;

import bot.neoflex.dto.ApplicationDto;
import bot.neoflex.dto.ButtonActivityDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DbClient {
    private final WebClient dbWebClient;
    private static final Logger logger = LoggerFactory.getLogger(DbClient.class);

    public List<ApplicationDto> getAllApplications() {
        logger.info("db client /api/db/applications called");
        return dbWebClient
                .get()
                .uri("/api/db/applications")
                .retrieve()
                .bodyToFlux(ApplicationDto.class)
                .collectList()
                .block();
    }

    public ApplicationDto getApplicationById(UUID id) {
        logger.info("db client /api/db/application/{} called", id);
        return dbWebClient
                .get()
                .uri("/api/db/applications/" + id)
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .block();
    }

    public void updateApplicationStatus(ApplicationDto applicationDto, UUID id) {
        logger.info("db client /api/db/application/{} called with {}", id, applicationDto);
        dbWebClient.put()
                .uri("/api/db/applications/" + id)
                .bodyValue(applicationDto)
                .retrieve();
    }

    public void createButtonActivity(ButtonActivityDto buttonActivityDto) {
        dbWebClient
                .post()
                .uri("/api/db/button-activities")
                .bodyValue(buttonActivityDto)
                .retrieve();
    }

    public void setButtonActivityTime(ButtonActivityDto buttonActivityDto) {
        dbWebClient
                .put()
                .uri("/api/db/button-activities")
                .bodyValue(buttonActivityDto)
                .retrieve();
    }

    public ButtonActivityDto getButtonActivityTime(String buttonType) {
        return dbWebClient
                .get()
                .uri("/api/db/button-activities/" + buttonType)
                .retrieve()
                .bodyToMono(ButtonActivityDto.class)
                .block();
    }
}
