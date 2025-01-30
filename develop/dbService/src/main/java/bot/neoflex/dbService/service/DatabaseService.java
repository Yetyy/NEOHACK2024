package bot.neoflex.dbService.service;

import bot.neoflex.dto.*;
import bot.neoflex.dbService.entity.*;
import bot.neoflex.dbService.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
//import java.util.Integer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final UserRepository userRepository;
    private final DirectionRepository directionRepository;
    private final ApplicationRepository applicationRepository;
    private final MessageTemplateRepository messageTemplateRepository;
    private final ButtonActivityRepository buttonActivityRepository;
    private final EventRepository eventRepository;
    private final AdminUserRepository adminUserRepository;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    public UserDto createUser(UserDto userDto) {
        logger.debug("Creating user: {}", userDto);

        // Проверяем, существует ли пользователь с таким telegramId
        Optional<User> existingUser = userRepository.findByTelegramId(userDto.getTelegramId());

        if (existingUser.isPresent()) {
            // Если пользователь существует, обновляем его данные
            User userToUpdate = existingUser.get();
            userToUpdate.setChatId(userDto.getChatId());
            userToUpdate.setCity(userDto.getCity());
            userToUpdate.setEmail(userDto.getEmail());
            userToUpdate.setFirstName(userDto.getFirstName());
            userToUpdate.setLastName(userDto.getLastName());
            userToUpdate.setPhoneNumber(userDto.getPhoneNumber());
            userToUpdate.setRole(userDto.getRole());
            userToUpdate.setUpdatedAt(LocalDateTime.now()); // Обновляем время обновления

            User updatedUser = userRepository.save(userToUpdate);
            logger.info("User updated with id: {}", updatedUser.getId());
            return modelMapper.map(updatedUser, UserDto.class);
        } else {
            // Если пользователь не существует, создаем нового
            User user = modelMapper.map(userDto, User.class);
            User savedUser = userRepository.save(user);
            logger.info("User created with id: {}", savedUser.getId());
            userDto.setId(savedUser.getId());
            return userDto;
        }
    }


    public Optional<UserDto> findUserByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(value -> modelMapper.map(value, UserDto.class));
    }

    public Optional<UserDto> findUserByPhoneNumber(String phoneNumber) {
        logger.debug("Finding user by phone number: {}", phoneNumber);
        Optional<User> user = userRepository.findByPhoneNumber(phoneNumber);
        return user.map(value -> modelMapper.map(value, UserDto.class));
    }

    public UserDto updateUser(UserDto userDto) {
        logger.debug("Updating user: {}", userDto);
        User user = modelMapper.map(userDto, User.class);
        User updatedUser = userRepository.save(user);
        logger.info("User updated with id: {}", updatedUser.getId());
        return modelMapper.map(updatedUser, UserDto.class);
    }

    public List<UserDto> getAllUsers() {
        logger.debug("Getting all users");
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }


    public DirectionDto createDirection(DirectionDto directionDto) {
        logger.debug("Creating direction: {}", directionDto);
        Direction direction = modelMapper.map(directionDto, Direction.class);
        Direction savedDirection = directionRepository.save(direction);
        logger.info("Direction created with id: {}", savedDirection.getId());
        return modelMapper.map(savedDirection, DirectionDto.class);
    }


    public Optional<DirectionDto> findDirectionById(Integer id) {
        logger.debug("Finding direction by id: {}", id);
        Optional<Direction> direction = directionRepository.findById(id);
        return direction.map(value -> modelMapper.map(value, DirectionDto.class));
    }

    public List<DirectionDto> getAllDirections() {
        logger.debug("Getting all directions");
        return directionRepository.findAll().stream()
                .map(direction -> modelMapper.map(direction, DirectionDto.class))
                .collect(Collectors.toList());
    }

    private User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });
    }

    private Direction getDirectionById(Integer directionId) {
        return directionRepository.findById(directionId)
                .orElseThrow(() -> {
                    logger.warn("Direction not found with id: {}", directionId);
                    return new RuntimeException("Direction not found with id: " + directionId);
                });
    }

    public ApplicationDto createApplication(ApplicationDto applicationDto) {
        logger.debug("Creating application: {}", applicationDto);
        Application application = modelMapper.map(applicationDto, Application.class);
        User user = getUserById(applicationDto.getUserId());
        if (applicationDto.getDirectionId() != null) {
            Direction direction = getDirectionById(applicationDto.getDirectionId());
            application.setDirection(direction);
        }
        application.setUser(user);
        Application savedApplication = applicationRepository.save(application);
        logger.info("Application created with id: {}", savedApplication.getId());
        return modelMapper.map(savedApplication, ApplicationDto.class);
    }

    public Optional<ApplicationDto> findApplicationById(Integer id) {
        logger.debug("Finding application by id: {}", id);
        Optional<Application> application = applicationRepository.findById(id);
        return application.map(value -> modelMapper.map(value, ApplicationDto.class));
    }

    public List<ApplicationDto> getAllApplications() {
        logger.debug("Getting all applications");
        return applicationRepository.findAll().stream()
                .map(application -> modelMapper.map(application, ApplicationDto.class))
                .collect(Collectors.toList());
    }

    public ApplicationDto updateApplication(ApplicationDto applicationDto) {
        logger.debug("Updating application: {}", applicationDto);
        Application application = modelMapper.map(applicationDto, Application.class);
        User user = getUserById(applicationDto.getUserId());
        if (applicationDto.getDirectionId() != null) {
            Direction direction = getDirectionById(applicationDto.getDirectionId());
            application.setDirection(direction);
        }
        application.setUser(user);
        Application updatedApplication = applicationRepository.save(application);
        logger.info("Application updated with id: {}", updatedApplication.getId());
        return modelMapper.map(updatedApplication, ApplicationDto.class);
    }

    public MessageTemplateDto createMessageTemplate(MessageTemplateDto messageTemplateDto) {
        logger.debug("Creating message template: {}", messageTemplateDto);
        MessageTemplate messageTemplate = modelMapper.map(messageTemplateDto, MessageTemplate.class);
        MessageTemplate savedMessageTemplate = messageTemplateRepository.save(messageTemplate);
        logger.info("Message template created with id: {}", savedMessageTemplate.getId());
        return modelMapper.map(savedMessageTemplate, MessageTemplateDto.class);
    }

    public Optional<MessageTemplateDto> findMessageTemplateByType(String messageType) {
        logger.debug("Finding message template by type: {}", messageType);
        Optional<MessageTemplate> messageTemplate = messageTemplateRepository.findByMessageType(messageType);
        if (messageTemplate.isEmpty()) {
            logger.warn("Message template with type {} not found", messageType);
            return Optional.empty();
        }
        return messageTemplate.map(value -> modelMapper.map(value, MessageTemplateDto.class));
    }

    public MessageTemplateDto updateMessageTemplate(MessageTemplateDto messageTemplateDto) {
        logger.debug("Updating message template: {}", messageTemplateDto);
        MessageTemplate messageTemplate = modelMapper.map(messageTemplateDto, MessageTemplate.class);
        MessageTemplate updatedMessageTemplate = messageTemplateRepository.save(messageTemplate);
        logger.info("Message template updated with id: {}", updatedMessageTemplate.getId());
        return modelMapper.map(updatedMessageTemplate, MessageTemplateDto.class);
    }

    public ButtonActivityDto createButtonActivity(ButtonActivityDto buttonActivityDto) {
        logger.debug("Creating button activity: {}", buttonActivityDto);
        ButtonActivity buttonActivity = modelMapper.map(buttonActivityDto, ButtonActivity.class);
        ButtonActivity savedButtonActivity = buttonActivityRepository.save(buttonActivity);
        logger.info("Button activity created with id: {}", savedButtonActivity.getId());
        return modelMapper.map(savedButtonActivity, ButtonActivityDto.class);
    }

    public Optional<ButtonActivityDto> findButtonActivityByType(String buttonType) {
        logger.debug("Finding button activity by type: {}", buttonType);
        Optional<ButtonActivity> buttonActivity = buttonActivityRepository.findByButtonType(buttonType);
        if (buttonActivity.isEmpty()) {
            logger.warn("Button activity with type {} not found", buttonType);
            return Optional.empty();
        }
        return buttonActivity.map(value -> modelMapper.map(value, ButtonActivityDto.class));
    }

    public ButtonActivityDto updateButtonActivity(ButtonActivityDto buttonActivityDto) {
        logger.debug("Updating button activity: {}", buttonActivityDto);
        ButtonActivity buttonActivity = modelMapper.map(buttonActivityDto, ButtonActivity.class);
        ButtonActivity updatedButtonActivity = buttonActivityRepository.save(buttonActivity);
        logger.info("Button activity updated with id: {}", updatedButtonActivity.getId());
        return modelMapper.map(updatedButtonActivity, ButtonActivityDto.class);
    }

    public EventDto createEvent(EventDto eventDto) {
        logger.debug("Creating event: {}", eventDto);
        Event event = modelMapper.map(eventDto, Event.class);
        User user = getUserById(eventDto.getUserId());
        event.setUser(user);
        Event savedEvent = eventRepository.save(event);
        logger.info("Event created with id: {}", savedEvent.getId());
        return modelMapper.map(savedEvent, EventDto.class);
    }

    public List<EventDto> getAllEvents() {
        logger.debug("Getting all events");
        return eventRepository.findAll().stream()
                .map(event -> modelMapper.map(event, EventDto.class))
                .collect(Collectors.toList());
    }

    public AdminUserDto createAdminUser(AdminUserDto adminUserDto) {
        logger.debug("Creating admin user: {}", adminUserDto);
        AdminUser adminUser = modelMapper.map(adminUserDto, AdminUser.class);
        AdminUser savedAdminUser = adminUserRepository.save(adminUser);
        logger.info("Admin user created with id: {}", savedAdminUser.getId());
        return modelMapper.map(savedAdminUser, AdminUserDto.class);
    }

    public Optional<AdminUserDto> findAdminUserByUsername(String username) {
        logger.debug("Finding admin user by username: {}", username);
        Optional<AdminUser> adminUser = adminUserRepository.findByUsername(username);
        return adminUser.map(value -> modelMapper.map(value, AdminUserDto.class));
    }

    public AdminUserDto updateAdminUser(AdminUserDto adminUserDto) {
        logger.debug("Updating admin user: {}", adminUserDto);
        AdminUser adminUser = modelMapper.map(adminUserDto, AdminUser.class);
        AdminUser updatedAdminUser = adminUserRepository.save(adminUser);
        logger.info("Admin user updated with id: {}", updatedAdminUser.getId());
        return modelMapper.map(updatedAdminUser, AdminUserDto.class);
    }
}
