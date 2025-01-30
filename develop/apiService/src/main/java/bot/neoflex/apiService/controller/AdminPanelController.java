package bot.neoflex.apiService.controller;

import bot.neoflex.apiService.client.DbClient;
import bot.neoflex.dto.ApplicationDto;
import bot.neoflex.dto.ButtonActivityDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminPanelController {
    private final DbClient dbClient;
    private static final Logger logger = LoggerFactory.getLogger(AdminPanelController.class);

    @GetMapping("/applications")
    public List<ApplicationDto> getAllApplications() {
        logger.info("/admin/applications called");
        return dbClient.getAllApplications();
    }

    @GetMapping("/applications/{id}")
    public ApplicationDto getApplication(@PathVariable UUID id) {
        logger.info("/admin/applications/{} called", id);
        return dbClient.getApplicationById(id);
    }

    @PutMapping("/applications/{id}")
    public void updateApplicationStatus(@RequestBody ApplicationDto applicationDto, @PathVariable UUID id) {
        logger.info("/admin/applications/{} called with {}", id, applicationDto);
        dbClient.updateApplicationStatus(applicationDto, id);
    }

    @PostMapping("/button")
    public void createButtonActivityTime(@RequestBody ButtonActivityDto buttonActivityDto) {
        dbClient.createButtonActivity(buttonActivityDto);
    }

    @PutMapping("/button")
    public void setButtonActivityTime(@RequestBody ButtonActivityDto buttonActivityDto) {
        dbClient.setButtonActivityTime(buttonActivityDto);
    }

    @GetMapping("/button/{buttonType}")
    public ButtonActivityDto getButtonActivityTime(@PathVariable String buttonType) {
        return dbClient.getButtonActivityTime(buttonType);
    }
}
