package bot.neoflex.dbService.service;

import bot.neoflex.dto.*;
import bot.neoflex.dbService.entity.*;
import bot.neoflex.dbService.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatabaseServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private DirectionRepository directionRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private MessageTemplateRepository messageTemplateRepository;
    @Mock
    private ButtonActivityRepository buttonActivityRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private AdminUserRepository adminUserRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private WebClient apiWebClient;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;
    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;
    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @InjectMocks
    private DatabaseService databaseService;

    private User user;
    private UserDto userDto;
    private Direction direction;
    private DirectionDto directionDto;
    private Application application;
    private ApplicationDto applicationDto;
    private MessageTemplate messageTemplate;
    private MessageTemplateDto messageTemplateDto;
    private ButtonActivity buttonActivity;
    private ButtonActivityDto buttonActivityDto;
    private Event event;
    private EventDto eventDto;
    private AdminUser adminUser;
    private AdminUserDto adminUserDto;

    @BeforeEach
    void setUp() {
        Integer userId = 1;
        Integer directionId = 1;
        Integer applicationId = 1;
        Integer messageTemplateId = 1;
        Integer buttonActivityId = 1;
        Integer eventId = 1;
        Integer adminUserId = 1;

        user = new User();
        user.setId(userId);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setCity("Test City");
        user.setPhoneNumber("1234567890");
        user.setEmail("test@example.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRole("new");

        userDto = new UserDto();
        userDto.setId(userId);
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setCity("Test City");
        userDto.setPhoneNumber("1234567890");
        userDto.setEmail("test@example.com");
        userDto.setRole("new");

        direction = new Direction();
        direction.setId(directionId);
        direction.setName("Test Direction");
        direction.setDescription("Test Description");
        direction.setActive(true);

        directionDto = new DirectionDto();
        directionDto.setId(directionId);
        directionDto.setName("Test Direction");
        directionDto.setDescription("Test Description");
        directionDto.setActive(true);

        application = new Application();
        application.setId(applicationId);
        application.setUser(user);
        application.setDirection(direction);
        application.setType("50");
        application.setSubmissionDate(LocalDateTime.now());
        application.setStatus("На рассмотрении");

        applicationDto = new ApplicationDto();
        applicationDto.setId(applicationId);
        applicationDto.setUserId(user.getId());
        applicationDto.setDirectionId(direction.getId());
        applicationDto.setType("50");
        applicationDto.setStatus("На рассмотрении");

        messageTemplate = new MessageTemplate();
        messageTemplate.setId(messageTemplateId);
        messageTemplate.setMessageType("test_message");
        messageTemplate.setMessageContent("Test message content");

        messageTemplateDto = new MessageTemplateDto();
        messageTemplateDto.setId(messageTemplateId);
        messageTemplateDto.setMessageType("test_message");
        messageTemplateDto.setMessageContent("Test message content");

        buttonActivity = new ButtonActivity();
        buttonActivity.setId(buttonActivityId);
        buttonActivity.setButtonType("apply");
        buttonActivity.setStartDate(LocalDate.now());
        buttonActivity.setEndDate(LocalDate.now().plusDays(7));

        buttonActivityDto = new ButtonActivityDto();
        buttonActivityDto.setId(buttonActivityId);
        buttonActivityDto.setButtonType("apply");
        buttonActivityDto.setStartDate(LocalDate.now());
        buttonActivityDto.setEndDate(LocalDate.now().plusDays(7));

        event = new Event();
        event.setId(eventId);
        event.setUser(user);
        event.setEventType("test_event");
        event.setTimestamp(LocalDateTime.now());
        event.setDetails(Map.of("key", "value"));

        eventDto = new EventDto();
        eventDto.setId(eventId);
        eventDto.setUserId(user.getId());
        eventDto.setEventType("test_event");
        eventDto.setDetails(Map.of("key", "value"));

        adminUser = new AdminUser();
        adminUser.setId(adminUserId);
        adminUser.setUsername("test_admin");
        adminUser.setPasswordHash("hashed_password");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole("admin");

        adminUserDto = new AdminUserDto();
        adminUserDto.setId(adminUserId);
        adminUserDto.setUsername("test_admin");
        adminUserDto.setPasswordHash("hashed_password");
        adminUserDto.setEmail("admin@example.com");
        adminUserDto.setRole("admin");
    }

    @Test
    void findUserByEmailTest() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Optional<UserDto> result = databaseService.findUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals(userDto, result.get());
    }

    @Test
    void findUserByPhoneNumberTest() {
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        Optional<UserDto> result = databaseService.findUserByPhoneNumber("1234567890");

        assertTrue(result.isPresent());
        assertEquals(userDto, result.get());
    }

    @Test
    void updateUserTest() {
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = databaseService.updateUser(userDto);

        assertEquals(userDto, result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getAllUsersTest() {
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);
        when(userRepository.findAll()).thenReturn(users);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        List<UserDto> result = databaseService.getAllUsers();

        assertEquals(userDtos, result);
    }

    @Test
    void createDirectionTest() {
        when(modelMapper.map(directionDto, Direction.class)).thenReturn(direction);
        when(directionRepository.save(direction)).thenReturn(direction);
        when(modelMapper.map(direction, DirectionDto.class)).thenReturn(directionDto);

        DirectionDto result = databaseService.createDirection(directionDto);

        assertEquals(directionDto, result);
        verify(directionRepository, times(1)).save(direction);
    }

    @Test
    void findDirectionByIdTest() {
        when(directionRepository.findById(direction.getId())).thenReturn(Optional.of(direction));
        when(modelMapper.map(direction, DirectionDto.class)).thenReturn(directionDto);

        Optional<DirectionDto> result = databaseService.findDirectionById(direction.getId());

        assertTrue(result.isPresent());
        assertEquals(directionDto, result.get());
    }

    @Test
    void getAllDirectionsTest() {
        List<Direction> directions = List.of(direction);
        List<DirectionDto> directionDtos = List.of(directionDto);
        when(directionRepository.findAll()).thenReturn(directions);
        when(modelMapper.map(direction, DirectionDto.class)).thenReturn(directionDto);

        List<DirectionDto> result = databaseService.getAllDirections();

        assertEquals(directionDtos, result);
    }

    @Test
    void createApplicationTest() {
        when(modelMapper.map(applicationDto, Application.class)).thenReturn(application);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(directionRepository.findById(direction.getId())).thenReturn(Optional.of(direction));
        when(applicationRepository.save(application)).thenReturn(application);
        when(modelMapper.map(application, ApplicationDto.class)).thenReturn(applicationDto);

        ApplicationDto result = databaseService.createApplication(applicationDto);

        assertEquals(applicationDto, result);
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    void findApplicationByIdTest() {
        when(applicationRepository.findById(application.getId())).thenReturn(Optional.of(application));
        when(modelMapper.map(application, ApplicationDto.class)).thenReturn(applicationDto);

        Optional<ApplicationDto> result = databaseService.findApplicationById(application.getId());

        assertTrue(result.isPresent());
        assertEquals(applicationDto, result.get());
    }

    @Test
    void getAllApplicationsTest() {
        List<Application> applications = List.of(application);
        List<ApplicationDto> applicationDtos = List.of(applicationDto);
        when(applicationRepository.findAll()).thenReturn(applications);
        when(modelMapper.map(application, ApplicationDto.class)).thenReturn(applicationDto);

        List<ApplicationDto> result = databaseService.getAllApplications();

        assertEquals(applicationDtos, result);
    }

    @Test
    void updateApplicationTest() {
        when(modelMapper.map(applicationDto, Application.class)).thenReturn(application);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(directionRepository.findById(direction.getId())).thenReturn(Optional.of(direction));
        when(applicationRepository.save(application)).thenReturn(application);
        when(modelMapper.map(application, ApplicationDto.class)).thenReturn(applicationDto);

        ApplicationDto result = databaseService.updateApplication(applicationDto);

        assertEquals(applicationDto, result);
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    void createMessageTemplateTest() {
        when(modelMapper.map(messageTemplateDto, MessageTemplate.class)).thenReturn(messageTemplate);
        when(messageTemplateRepository.save(messageTemplate)).thenReturn(messageTemplate);
        when(modelMapper.map(messageTemplate, MessageTemplateDto.class)).thenReturn(messageTemplateDto);

        MessageTemplateDto result = databaseService.createMessageTemplate(messageTemplateDto);

        assertEquals(messageTemplateDto, result);
        verify(messageTemplateRepository, times(1)).save(messageTemplate);
    }

    @Test
    void findMessageTemplateByTypeTest() {
        when(messageTemplateRepository.findByMessageType("test_message")).thenReturn(Optional.of(messageTemplate));
        when(modelMapper.map(messageTemplate, MessageTemplateDto.class)).thenReturn(messageTemplateDto);

        Optional<MessageTemplateDto> result = databaseService.findMessageTemplateByType("test_message");

        assertTrue(result.isPresent());
        assertEquals(messageTemplateDto, result.get());
    }

    @Test
    void updateMessageTemplateTest() {
        when(modelMapper.map(messageTemplateDto, MessageTemplate.class)).thenReturn(messageTemplate);
        when(messageTemplateRepository.save(messageTemplate)).thenReturn(messageTemplate);
        when(modelMapper.map(messageTemplate, MessageTemplateDto.class)).thenReturn(messageTemplateDto);

        MessageTemplateDto result = databaseService.updateMessageTemplate(messageTemplateDto);

        assertEquals(messageTemplateDto, result);
        verify(messageTemplateRepository, times(1)).save(messageTemplate);
    }

    @Test
    void createButtonActivityTest() {
        when(modelMapper.map(buttonActivityDto, ButtonActivity.class)).thenReturn(buttonActivity);
        when(buttonActivityRepository.save(buttonActivity)).thenReturn(buttonActivity);
        when(modelMapper.map(buttonActivity, ButtonActivityDto.class)).thenReturn(buttonActivityDto);

        ButtonActivityDto result = databaseService.createButtonActivity(buttonActivityDto);

        assertEquals(buttonActivityDto, result);
        verify(buttonActivityRepository, times(1)).save(buttonActivity);
    }

    @Test
    void findButtonActivityByTypeTest() {
        when(buttonActivityRepository.findByButtonType("apply")).thenReturn(Optional.of(buttonActivity));
        when(modelMapper.map(buttonActivity, ButtonActivityDto.class)).thenReturn(buttonActivityDto);

        Optional<ButtonActivityDto> result = databaseService.findButtonActivityByType("apply");

        assertTrue(result.isPresent());
        assertEquals(buttonActivityDto, result.get());
    }

    @Test
    void updateButtonActivityTest() {
        when(modelMapper.map(buttonActivityDto, ButtonActivity.class)).thenReturn(buttonActivity);
        when(buttonActivityRepository.save(buttonActivity)).thenReturn(buttonActivity);
        when(modelMapper.map(buttonActivity, ButtonActivityDto.class)).thenReturn(buttonActivityDto);

        ButtonActivityDto result = databaseService.updateButtonActivity(buttonActivityDto);

        assertEquals(buttonActivityDto, result);
        verify(buttonActivityRepository, times(1)).save(buttonActivity);
    }

    @Test
    void createEventTest() {
        when(modelMapper.map(eventDto, Event.class)).thenReturn(event);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.save(event)).thenReturn(event);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);

        EventDto result = databaseService.createEvent(eventDto);

        assertEquals(eventDto, result);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void getAllEventsTest() {
        List<Event> events = List.of(event);
        List<EventDto> eventDtos = List.of(eventDto);
        when(eventRepository.findAll()).thenReturn(events);
        when(modelMapper.map(event, EventDto.class)).thenReturn(eventDto);

        List<EventDto> result = databaseService.getAllEvents();

        assertEquals(eventDtos, result);
    }

    @Test
    void createAdminUserTest() {
        when(modelMapper.map(adminUserDto, AdminUser.class)).thenReturn(adminUser);
        when(adminUserRepository.save(adminUser)).thenReturn(adminUser);
        when(modelMapper.map(adminUser, AdminUserDto.class)).thenReturn(adminUserDto);

        AdminUserDto result = databaseService.createAdminUser(adminUserDto);

        assertEquals(adminUserDto, result);
        verify(adminUserRepository, times(1)).save(adminUser);
    }

    @Test
    void findAdminUserByUsernameTest() {
        when(adminUserRepository.findByUsername("test_admin")).thenReturn(Optional.of(adminUser));
        when(modelMapper.map(adminUser, AdminUserDto.class)).thenReturn(adminUserDto);

        Optional<AdminUserDto> result = databaseService.findAdminUserByUsername("test_admin");

        assertTrue(result.isPresent());
        assertEquals(adminUserDto, result.get());
    }

    @Test
    void updateAdminUserTest() {
        when(modelMapper.map(adminUserDto, AdminUser.class)).thenReturn(adminUser);
        when(adminUserRepository.save(adminUser)).thenReturn(adminUser);
        when(modelMapper.map(adminUser, AdminUserDto.class)).thenReturn(adminUserDto);

        AdminUserDto result = databaseService.updateAdminUser(adminUserDto);

        assertEquals(adminUserDto, result);
        verify(adminUserRepository, times(1)).save(adminUser);
    }
}
