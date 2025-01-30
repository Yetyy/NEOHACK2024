package bot.neoflex.dbService.controller;

import bot.neoflex.dto.*;
import bot.neoflex.dbService.service.DatabaseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/db")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseService databaseService;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Received request to create user: {}", userDto);
        Optional<UserDto> existingUser = databaseService.findUserByEmail(userDto.getEmail());
        if (existingUser.isPresent()) {
            logger.info("User with email {} already exists, returning existing user", userDto.getEmail());
            return new ResponseEntity<>(existingUser.get(), HttpStatus.OK);
        }
        UserDto createdUser = databaseService.createUser(userDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        logger.info("Received request to get user by email: {}", email);
        Optional<UserDto> userDto = databaseService.findUserByEmail(email);
        return userDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> {
                    logger.warn("User with email {} not found", email);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping("/users/phone/{phoneNumber}")
    public ResponseEntity<UserDto> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        logger.info("Received request to get user by phone number: {}", phoneNumber);
        Optional<UserDto> userDto = databaseService.findUserByPhoneNumber(phoneNumber);
        return userDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> {
                    logger.warn("User with phone number {} not found", phoneNumber);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PutMapping("/users")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto) {
        logger.info("Received request to update user: {}", userDto);
        UserDto updatedUser = databaseService.updateUser(userDto);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("Received request to get all users");
        List<UserDto> users = databaseService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/directions")
    public ResponseEntity<DirectionDto> createDirection(@Valid @RequestBody DirectionDto directionDto) {
        logger.info("Received request to create direction: {}", directionDto);
        DirectionDto createdDirection = databaseService.createDirection(directionDto);
        return new ResponseEntity<>(createdDirection, HttpStatus.CREATED);
    }

    @GetMapping("/directions/{id}")
    public ResponseEntity<DirectionDto> getDirectionById(@PathVariable Integer id) {
        logger.info("Received request to get direction by id: {}", id);
        Optional<DirectionDto> directionDto = databaseService.findDirectionById(id);
        return directionDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> {
                    logger.warn("Direction with id {} not found", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping("/directions")
    public ResponseEntity<List<DirectionDto>> getAllDirections() {
        logger.info("Received request to get all directions");
        List<DirectionDto> directions = databaseService.getAllDirections();
        return new ResponseEntity<>(directions, HttpStatus.OK);
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationDto> createApplication(@Valid @RequestBody ApplicationDto applicationDto) {
        logger.info("Received request to create application: {}", applicationDto);
        ApplicationDto createdApplication = databaseService.createApplication(applicationDto);
        return new ResponseEntity<>(createdApplication, HttpStatus.CREATED);
    }

    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable Integer id) {
        logger.info("Received request to get application by id: {}", id);
        Optional<ApplicationDto> applicationDto = databaseService.findApplicationById(id);
        return applicationDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> {
                    logger.warn("Application with id {} not found", id);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationDto>> getAllApplications() {
        logger.info("Received request to get all applications");
        List<ApplicationDto> applications = databaseService.getAllApplications();
        return new ResponseEntity<>(applications, HttpStatus.OK);
    }

    @PutMapping("/applications/{id}")
    public ResponseEntity<ApplicationDto> updateApplication(@PathVariable Integer id, @Valid @RequestBody ApplicationDto applicationDto) {
        logger.info("Received request to update application with id {}: {}", id, applicationDto);
        if (!id.equals(applicationDto.getId())) {
            logger.warn("Application id in path {} does not match id in body {}", id, applicationDto.getId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ApplicationDto updatedApplication = databaseService.updateApplication(applicationDto);
        return new ResponseEntity<>(updatedApplication, HttpStatus.OK);
    }

    @PostMapping("/message-templates")
    public ResponseEntity<MessageTemplateDto> createMessageTemplate(@Valid @RequestBody MessageTemplateDto messageTemplateDto) {
        logger.info("Received request to create message template: {}", messageTemplateDto);
        MessageTemplateDto createdMessageTemplate = databaseService.createMessageTemplate(messageTemplateDto);
        return new ResponseEntity<>(createdMessageTemplate, HttpStatus.CREATED);
    }

    @GetMapping("/message-templates/{messageType}")
    public ResponseEntity<MessageTemplateDto> getMessageTemplateByType(@PathVariable String messageType) {
        logger.info("Received request to get message template by type: {}", messageType);
        Optional<MessageTemplateDto> messageTemplateDto = databaseService.findMessageTemplateByType(messageType);
        return messageTemplateDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> {
                    logger.warn("Message template with type {} not found", messageType);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PutMapping("/message-templates")
    public ResponseEntity<MessageTemplateDto> updateMessageTemplate(@Valid @RequestBody MessageTemplateDto messageTemplateDto) {
        logger.info("Received request to update message template: {}", messageTemplateDto);
        MessageTemplateDto updatedMessageTemplate = databaseService.updateMessageTemplate(messageTemplateDto);
        return new ResponseEntity<>(updatedMessageTemplate, HttpStatus.OK);
    }

    @PostMapping("/button-activities")
    public ResponseEntity<ButtonActivityDto> createButtonActivity(@Valid @RequestBody ButtonActivityDto buttonActivityDto) {
        logger.info("Received request to create button activity: {}", buttonActivityDto);
        ButtonActivityDto createdButtonActivity = databaseService.createButtonActivity(buttonActivityDto);
        return new ResponseEntity<>(createdButtonActivity, HttpStatus.CREATED);
    }

    @GetMapping("/button-activities/{buttonType}")
    public ResponseEntity<ButtonActivityDto> getButtonActivityByType(@PathVariable String buttonType) {
        logger.info("Received request to get button activity by type: {}", buttonType);
        Optional<ButtonActivityDto> buttonActivityDto = databaseService.findButtonActivityByType(buttonType);
        return buttonActivityDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> {
                    logger.warn("Button activity with type {} not found", buttonType);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PutMapping("/button-activities")
    public ResponseEntity<ButtonActivityDto> updateButtonActivity(@Valid @RequestBody ButtonActivityDto buttonActivityDto) {
        logger.info("Received request to update button activity: {}", buttonActivityDto);
        ButtonActivityDto updatedButtonActivity = databaseService.updateButtonActivity(buttonActivityDto);
        return new ResponseEntity<>(updatedButtonActivity, HttpStatus.OK);
    }

    @PostMapping("/events")
    public ResponseEntity<EventDto> createEvent(@Valid @RequestBody EventDto eventDto) {
        logger.info("Received request to create event: {}", eventDto);
        EventDto createdEvent = databaseService.createEvent(eventDto);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventDto>> getAllEvents() {
        logger.info("Received request to get all events");
        List<EventDto> events = databaseService.getAllEvents();
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PostMapping("/admin-users")
    public ResponseEntity<AdminUserDto> createAdminUser(@Valid @RequestBody AdminUserDto adminUserDto) {
        logger.info("Received request to create admin user: {}", adminUserDto);
        AdminUserDto createdAdminUser = databaseService.createAdminUser(adminUserDto);
        return new ResponseEntity<>(createdAdminUser, HttpStatus.CREATED);
    }

    @GetMapping("/admin-users/{username}")
    public ResponseEntity<AdminUserDto> getAdminUserByUsername(@PathVariable String username) {
        logger.info("Received request to get admin user by username: {}", username);
        Optional<AdminUserDto> adminUserDto = databaseService.findAdminUserByUsername(username);
        return adminUserDto.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> {
                    logger.warn("Admin user with username {} not found", username);
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                });
    }

    @PutMapping("/admin-users")
    public ResponseEntity<AdminUserDto> updateAdminUser(@Valid @RequestBody AdminUserDto adminUserDto) {
        logger.info("Received request to update admin user: {}", adminUserDto);
        AdminUserDto updatedAdminUser = databaseService.updateAdminUser(adminUserDto);
        return new ResponseEntity<>(updatedAdminUser, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));
        logger.warn("Validation error: {}", errors);
        return new ResponseEntity<>("Validation error: " + errors, HttpStatus.BAD_REQUEST);
    }
}
