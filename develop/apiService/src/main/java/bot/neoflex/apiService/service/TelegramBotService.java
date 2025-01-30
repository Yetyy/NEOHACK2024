package bot.neoflex.apiService.service;

import bot.neoflex.dto.*;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.GetMe;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetMeResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.http.HttpStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final TelegramBot telegramBot;
    private final WebClient dbWebClient;
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);
    private final Map<Long, UserDto> userRegistration = new HashMap<>();
    private final Map<Long, String> userState = new HashMap<>();
    private final Map<Long, Integer> applicationRegistration = new HashMap<>();
    private final String APPLY_BUTTON_TYPE = "apply";
    private final String PRE_APPLY_BUTTON_TYPE = "pre_apply";
    private final String VISITOR_ROLE = "visitor";
    private final String CANDIDATE_ROLE = "candidate";
    private final String EXTERNAL_USER_ROLE = "external_user";
    private final String APPLY_TYPE = "apply";
    private final String PRE_APPLY_TYPE = "pre_apply";
    private final String REGISTRATION_EVENT = "registration";
    private final String APPLICATION_EVENT = "application";
    private final String PENDING_STATUS = "На рассмотрении";
    private Long telegramBotId;
    private boolean notificationsSent = false;
    private String previousButtonStatus = null;

    private static final Pattern CYRILLIC_PATTERN = Pattern.compile("^[а-яА-Я]+$");
    private static final Pattern CITY_PATTERN = Pattern.compile("^[а-яА-Я0-9\\-\\.]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9\\-\\s\\+]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");


    public void init() {
        GetMeResponse getMeResponse = telegramBot.execute(new GetMe());
        if (getMeResponse.isOk()) {
            telegramBotId = getMeResponse.user().id();
            logger.info("Telegram bot ID: {}", telegramBotId);
        } else {
            logger.error("Failed to get Telegram bot ID: {}", getMeResponse.description());
        }
    }

    public void handleStartCommand(Long chatId) {
        logger.debug("Handling /start command for chat: {}", chatId);
        getMessageTemplate("welcome")
                .flatMap(welcomeTemplate -> {
                    if (welcomeTemplate != null) {
                        sendMessage(chatId, welcomeTemplate.getMessageContent());
                        userState.put(chatId, "visitor");
                    } else {
                        sendMessage(chatId, "Извините, произошла ошибка при обработке команды /start.");
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    public void handleNameInput(Long chatId, String firstName) {
        logger.debug("Handling name input for chat: {}, name: {}", chatId, firstName);
        if (!isValidCyrillic(firstName)) {
            sendMessage(chatId, "Имя должно содержать только кириллические символы. Пожалуйста, введите имя еще раз.");
            return;
        }
        UserDto userDto = userRegistration.getOrDefault(chatId, new UserDto());
        userDto.setFirstName(firstName);
        userRegistration.put(chatId, userDto);
        userState.put(chatId, "waiting_for_last_name");
        getMessageTemplate("last_name_request")
                .flatMap(lastNameTemplate -> {
                    if (lastNameTemplate != null) {
                        sendMessage(chatId, lastNameTemplate.getMessageContent());
                    } else {
                        sendMessage(chatId, "Извините, произошла ошибка при запросе фамилии.");
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    public void handleLastNameInput(Long chatId, String lastName) {
        logger.debug("Handling last name input for chat: {}, last name: {}", chatId, lastName);
        if (!isValidCyrillic(lastName)) {
            sendMessage(chatId, "Фамилия должна содержать только кириллические символы. Пожалуйста, введите фамилию еще раз.");
            return; // Не сбрасываем состояние, просто остаемся на текущем шаге
        }
        UserDto userDto = userRegistration.get(chatId);
        userDto.setLastName(lastName);
        userRegistration.put(chatId, userDto);
        userState.put(chatId, "waiting_for_city");
        getMessageTemplate("city_request")
                .flatMap(cityTemplate -> {
                    if (cityTemplate != null) {
                        sendMessage(chatId, cityTemplate.getMessageContent());
                    } else {
                        sendMessage(chatId, "Извините, произошла ошибка при запросе города.");
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    public void handleCityInput(Long chatId, String city) {
        logger.debug("Handling city input for chat: {}, city: {}", chatId, city);
        if (!isValidCity(city)) {
            sendMessage(chatId, "Город должен содержать только кириллические символы, цифры и символ '-'. Пожалуйста, введите город еще раз.");
            return; // Не сбрасываем состояние, просто остаемся на текущем шаге
        }
        UserDto userDto = userRegistration.get(chatId);
        userDto.setCity(city);
        userRegistration.put(chatId, userDto);
        userState.put(chatId, "waiting_for_phone");
        getMessageTemplate("phone_request")
                .flatMap(phoneTemplate -> {
                    if (phoneTemplate != null) {
                        sendMessage(chatId, phoneTemplate.getMessageContent());
                    } else {
                        sendMessage(chatId, "Извините, произошла ошибка при запросе номера телефона.");
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    public void handlePhoneInput(Long chatId, String phoneNumber) {
        logger.debug("Handling phone input for chat: {}, phone: {}", chatId, phoneNumber);
        if (!isValidPhone(phoneNumber)) {
            sendMessage(chatId, "Номер телефона должен содержать только цифры, символы '-', ' ' и '+'. Пожалуйста, введите номер телефона еще раз.");
            return; // Не сбрасываем состояние, просто остаемся на текущем шаге
        }
        UserDto userDto = userRegistration.get(chatId);
        userDto.setPhoneNumber(phoneNumber);
        userRegistration.put(chatId, userDto);
        userState.put(chatId, "waiting_for_email");
        getMessageTemplate("email_request")
                .flatMap(emailTemplate -> {
                    if (emailTemplate != null) {
                        sendMessage(chatId, emailTemplate.getMessageContent());
                    } else {
                        sendMessage(chatId, "Извините, произошла ошибка при запросе электронной почты.");
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    public void handleEmailInput(Long chatId, String email) {
        logger.debug("Handling email input for chat: {}, email: {}", chatId, email);
        if (!isValidEmail(email)) {
            sendMessage(chatId, "Некорректный формат электронной почты. Пожалуйста, введите email еще раз.");
            return; // Не сбрасываем состояние, просто остаемся на текущем шаге
        }
        UserDto userDto = userRegistration.getOrDefault(chatId, new UserDto());
        userDto.setEmail(email);
        userRegistration.put(chatId, userDto);
        userState.put(chatId, "email_received");
        createUser(chatId, userDto)
                .flatMap(newUser -> {
                    userRegistration.put(chatId, newUser);
                    getMessageTemplate("apply_or_pre_apply_request")
                            .flatMap(applyOrPreApplyTemplate -> {
                                if (applyOrPreApplyTemplate != null) {
                                    sendMessageWithButtons(chatId, applyOrPreApplyTemplate.getMessageContent());
                                } else {
                                    sendMessage(chatId, "Извините, произошла ошибка при запросе выбора типа заявки.");
                                }
                                return Mono.empty();
                            })
                            .subscribe();
                    return Mono.empty();
                })
                .subscribe();
    }

    private Mono<UserDto> createUser(Long chatId, UserDto userDto) {
        logger.debug("Creating user with email {}", userDto.getEmail());
        userDto.setRole(EXTERNAL_USER_ROLE);
        userDto.setTelegramId(telegramBotId);
        userDto.setChatId(chatId);
        return dbWebClient.post()
                .uri("/api/db/users")
                .body(Mono.just(userDto), UserDto.class)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    logger.error("Error creating new user: {}, status code: {}", userDto.getEmail(), clientResponse.statusCode());
                    return Mono.error(new HttpServerErrorException(clientResponse.statusCode(), "Error creating user"));
                })
                .bodyToMono(UserDto.class)
                .flatMap(newUser -> {
                    createEvent(newUser.getId(), REGISTRATION_EVENT, "User registered");
                    return Mono.just(newUser);
                })
                .onErrorResume(e -> {
                    if (e instanceof HttpServerErrorException && ((HttpServerErrorException) e).getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                        logger.error("Error creating new user: {}, error: {}", userDto.getEmail(), e.getMessage());
                        sendMessage(chatId, "Извините, пользователь с таким chatId уже существует.");
                        return Mono.empty();
                    }
                    logger.error("Error creating new user: {}, error: {}", userDto.getEmail(), e.getMessage());
                    sendMessage(chatId, "Извините, произошла ошибка при регистрации пользователя.");
                    return Mono.empty();
                });
    }

    public void handleApplyCommand(Long chatId) {
        logger.debug("Handling apply command for chat: {}", chatId);
        isButtonActive(APPLY_BUTTON_TYPE)
                .flatMap(isActive -> {
                    if (!isActive) {
                        return getMessageTemplate("apply_not_active")
                                .flatMap(applyNotActiveTemplate -> {
                                    if (applyNotActiveTemplate != null) {
                                        sendMessage(chatId, applyNotActiveTemplate.getMessageContent());
                                    } else {
                                        sendMessage(chatId, "Извините, кнопка подачи заявки не активна.");
                                    }
                                    return Mono.empty();
                                });
                    }
                    UserDto userDto = userRegistration.get(chatId);
                    if (userDto == null || userDto.getId() == null) {
                        logger.error("User data not found or user ID is null for chat: {}", chatId);
                        sendMessage(chatId, "Извините, произошла ошибка при обработке заявки. Пожалуйста, начните регистрацию заново.");
                        return Mono.empty();
                    }
                    userDto.setRole(CANDIDATE_ROLE);
                    userRegistration.put(chatId, userDto);
                    return createApplicationWithoutDirection(chatId, userDto);
                })
                .subscribe();
    }

    private Mono<Void> createApplicationWithoutDirection(Long chatId, UserDto userDto) {
        logger.debug("Creating application without direction for user: {}", userDto.getId());
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setUserId(userDto.getId());
        applicationDto.setType(APPLY_TYPE);
        applicationDto.setStatus(PENDING_STATUS);
        return dbWebClient.post()
                .uri("/api/db/applications")
                .body(Mono.just(applicationDto), ApplicationDto.class)
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .flatMap(application -> {
                    createEvent(userDto.getId(), APPLICATION_EVENT, "User created application without direction");
                    userState.put(chatId, "waiting_for_direction");
                    applicationRegistration.put(chatId, application.getId());
                    return sendDirectionSelectionMessage(chatId);
                })
                .onErrorResume(e -> {
                    logger.error("Error creating application without direction for user: {}, error: {}", userDto.getId(), e.getMessage());
                    sendMessage(chatId, "Извините, произошла ошибка при создании заявки. Пожалуйста, попробуйте позже.");
                    return Mono.empty();
                });
    }

    public void handlePreApplyCommand(Long chatId) {
        logger.debug("Handling pre-apply command for chat: {}", chatId);
        isButtonActive(PRE_APPLY_BUTTON_TYPE)
                .flatMap(isActive -> {
                    if (!isActive) {
                        return getMessageTemplate("pre_apply_not_active")
                                .flatMap(preApplyNotActiveTemplate -> {
                                    if (preApplyNotActiveTemplate != null) {
                                        sendMessage(chatId, preApplyNotActiveTemplate.getMessageContent());
                                    } else {
                                        sendMessage(chatId, "Извините, кнопка предзаявки не активна.");
                                    }
                                    return Mono.empty();
                                });
                    }
                    UserDto userDto = userRegistration.get(chatId);
                    if (userDto == null || userDto.getId() == null) {
                        logger.error("User data not found or user ID is null for chat: {}", chatId);
                        sendMessage(chatId, "Извините, произошла ошибка при обработке предзаявки. Пожалуйста, начните регистрацию заново.");
                        return Mono.empty();
                    }
                    userDto.setRole(CANDIDATE_ROLE);
                    userRegistration.put(chatId, userDto);
                    return createPreApplication(chatId, userDto);
                })
                .subscribe();
    }

    public void handleDirectionInput(Long chatId, String directionName) {
        logger.debug("Handling direction input for chat: {}, direction: {}", chatId, directionName);
        UserDto userDto = userRegistration.get(chatId);
        getDirectionByName(directionName)
                .flatMap(directionDto -> {
                    if (directionDto == null) {
                        return getMessageTemplate("direction_not_found")
                                .flatMap(directionNotFoundTemplate -> {
                                    if (directionNotFoundTemplate != null) {
                                        sendMessage(chatId, directionNotFoundTemplate.getMessageContent());
                                    } else {
                                        sendMessage(chatId, "Извините, направление не найдено.");
                                    }
                                    return Mono.empty();
                                });
                    }
                    registerApplication(chatId, directionDto, userDto);
                    return Mono.empty();
                })
                .subscribe();
    }

    public void handleDirectionCallback(Long chatId, String directionName) {
        logger.debug("Handling direction callback for chat: {}, direction: {}", chatId, directionName);
        UserDto userDto = userRegistration.get(chatId);
        getDirectionByName(directionName)
                .flatMap(directionDto -> {
                    if (directionDto == null) {
                        return getMessageTemplate("direction_not_found")
                                .flatMap(directionNotFoundTemplate -> {
                                    if (directionNotFoundTemplate != null) {
                                        sendMessage(chatId, directionNotFoundTemplate.getMessageContent());
                                    } else {
                                        sendMessage(chatId, "Извините, направление не найдено.");
                                    }
                                    return Mono.empty();
                                });
                    }
                    registerApplication(chatId, directionDto, userDto);
                    return Mono.empty();
                })
                .subscribe();
    }

    public void handleUnknownCommand(Long chatId) {
        logger.warn("Handling unknown command for chat: {}", chatId);
        getMessageTemplate("unknown_command")
                .flatMap(unknownCommandTemplate -> {
                    if (unknownCommandTemplate != null) {
                        sendMessage(chatId, unknownCommandTemplate.getMessageContent());
                    } else {
                        sendMessage(chatId, "Извините, я не понимаю эту команду.");
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    private Mono<Void> createPreApplication(Long chatId, UserDto userDto) {
        logger.debug("Creating pre-application for user: {}", userDto.getId());
        ApplicationDto applicationDto = new ApplicationDto();
        applicationDto.setUserId(userDto.getId());
        applicationDto.setType(PRE_APPLY_TYPE);
        applicationDto.setStatus(PENDING_STATUS);
        return dbWebClient.post()
                .uri("/api/db/applications")
                .body(Mono.just(applicationDto), ApplicationDto.class)
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .flatMap(application -> {
                    createEvent(userDto.getId(), APPLICATION_EVENT, "User created pre-application");
                    return sendPreApplicationSuccessMessage(chatId);
                })
                .onErrorResume(e -> {
                    logger.error("Error creating pre-application for user: {}, error: {}", userDto.getId(), e.getMessage());
                    sendMessage(chatId, "Извините, произошла ошибка при создании предзаявки. Пожалуйста, попробуйте позже.");
                    return Mono.empty();
                });
    }

    private void registerApplication(Long chatId, DirectionDto directionDto, UserDto userDto) {
        logger.debug("Registering application for chat: {}, direction: {}, user: {}", chatId, directionDto, userDto);
        if (userDto.getId() == null) {
            logger.error("User ID is null, cannot register application for chat: {}", chatId);
            sendMessage(chatId, "Извините, произошла ошибка при подаче заявки. Пожалуйста, попробуйте позже.");
            return;
        }
        Integer applicationId = applicationRegistration.get(chatId);
        if (applicationId == null) {
            logger.error("Application ID is null, cannot register application for chat: {}", chatId);
            sendMessage(chatId, "Извините, произошла ошибка при подаче заявки. Пожалуйста, попробуйте позже.");
            return;
        }
        dbWebClient.get()
                .uri("/api/db/applications/" + applicationId)
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .flatMap(existingApplication -> {
                    logger.debug("Application for user {} found, updating direction to {}", userDto.getId(), directionDto.getId());
                    existingApplication.setDirectionId(directionDto.getId());
                    return updateApplication(chatId, existingApplication, directionDto, userDto);
                })
                .onErrorResume(e -> {
                    logger.error("Error checking existing application for user: {}, error: {}", userDto.getId(), e.getMessage());
                    sendMessage(chatId, "Извините, произошла ошибка при подаче заявки. Пожалуйста, попробуйте позже.");
                    return Mono.empty();
                })
                .subscribe();
    }

    private Mono<Void> updateApplication(Long chatId, ApplicationDto existingApplication, DirectionDto directionDto, UserDto userDto) {
        existingApplication.setDirectionId(directionDto.getId());
        return dbWebClient.put()
                .uri("/api/db/applications/" + existingApplication.getId())
                .body(Mono.just(existingApplication), ApplicationDto.class)
                .retrieve()
                .bodyToMono(ApplicationDto.class)
                .flatMap(updatedApplication -> {
                    createEvent(userDto.getId(), APPLICATION_EVENT, "User updated application for direction: " + directionDto.getName());
                    return sendDirectionInfoMessage(chatId, directionDto)
                            .then(sendApplicationSuccessMessage(chatId));
                })
                .onErrorResume(e -> {
                    logger.error("Error updating application for user: {}, error: {}", userDto.getId(), e.getMessage());
                    sendMessage(chatId, "Извините, произошла ошибка при обновлении заявки. Пожалуйста, попробуйте позже.");
                    return Mono.empty();
                });
    }

    private Mono<Void> sendDirectionInfoMessage(Long chatId, DirectionDto directionDto) {
        String directionMessage = String.format("Выбрано направление: %s\nПодробнее: %s",
                directionDto.getName(), directionDto.getDescription());
        sendMessage(chatId, directionMessage);
        return Mono.empty();
    }

    private Mono<Void> updateUserRole(UserDto userDto) {
        return dbWebClient.put()
                .uri("/api/db/users")
                .body(Mono.just(userDto), UserDto.class)
                .retrieve()
                .bodyToMono(UserDto.class)
                .then();
    }

    private Mono<Void> sendApplicationSuccessMessage(Long chatId) {
        return getMessageTemplate("application_success")
                .flatMap(successTemplate -> {
                    if (successTemplate != null) {
                        sendMessage(chatId, successTemplate.getMessageContent());
                    } else {
                        sendMessage(chatId, "Заявка успешно подана, но произошла ошибка при отправке сообщения.");
                    }
                    userState.remove(chatId);
                    userRegistration.remove(chatId);
                    applicationRegistration.remove(chatId);
                    return Mono.empty();
                });
    }

    private Mono<Void> sendPreApplicationSuccessMessage(Long chatId) {
        return getMessageTemplate("pre_application_success")
                .flatMap(successTemplate -> {
                    if (successTemplate != null) {
                        sendMessage(chatId, successTemplate.getMessageContent());
                    } else {
                        sendMessage(chatId, "Предзаявка успешно создана, но произошла ошибка при отправке сообщения.");
                    }
                    userState.remove(chatId);
                    userRegistration.remove(chatId);
                    applicationRegistration.remove(chatId);
                    return Mono.empty();
                });
    }

    private void sendMessageWithButtons(Long chatId, String message) {
        logger.debug("Sending message with buttons to chat: {}, message: {}", chatId, message);
        SendMessage sendMessage = new SendMessage(chatId, message);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();

        isButtonActive(APPLY_BUTTON_TYPE)
                .flatMap(isApplyActive -> {
                    if (isApplyActive) {
                        markup.addRow(new InlineKeyboardButton("Подать заявку").callbackData("apply"));
                        return Mono.just(true); // Если apply активна, не проверяем pre_apply
                    }
                    return isButtonActive(PRE_APPLY_BUTTON_TYPE)
                            .map(isPreApplyActive -> {
                                if (isPreApplyActive) {
                                    markup.addRow(new InlineKeyboardButton("Оставить предзаявку").callbackData("pre_apply"));
                                }
                                return false; // Возвращаем false, если pre_apply была добавлена или не активна
                            });
                })
                .doOnSuccess(applyButtonAdded -> {
                    sendMessage.replyMarkup(markup);
                    SendResponse response = telegramBot.execute(sendMessage);
                    if (!response.isOk()) {
                        logger.error("Failed to send message with buttons to chat: {}, error: {}", chatId, response.description());
                    }
                })
                .subscribe();
    }

    private Mono<Void> sendDirectionSelectionMessage(Long chatId) {
        logger.debug("Sending direction selection message to chat: {}", chatId);
        return getMessageTemplate("direction_request")
                .flatMap(directionRequestTemplate -> {
                    if (directionRequestTemplate == null) {
                        sendMessage(chatId, "Извините, произошла ошибка при запросе выбора направления.");
                        return Mono.empty();
                    }
                    String directionRequestMessage = directionRequestTemplate.getMessageContent();
                    return getAllDirections()
                            .flatMap(directions -> {
                                List<InlineKeyboardButton> buttons = directions.stream()
                                        .filter(DirectionDto::isActive)
                                        .map(direction -> new InlineKeyboardButton(direction.getName()).callbackData("direction_" + direction.getName()))
                                        .toList();

                                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                                List<InlineKeyboardButton> row = new ArrayList<>();
                                for (InlineKeyboardButton button : buttons) {
                                    row.add(button);
                                    if (row.size() == 2) {
                                        markup.addRow(row.toArray(new InlineKeyboardButton[0]));
                                        row = new ArrayList<>();
                                    }
                                }
                                if (!row.isEmpty()) {
                                    markup.addRow(row.toArray(new InlineKeyboardButton[0]));
                                }


                                SendMessage sendMessage = new SendMessage(chatId, directionRequestMessage).replyMarkup(markup);
                                SendResponse response = telegramBot.execute(sendMessage);
                                if (!response.isOk()) {
                                    logger.error("Failed to send direction selection message to chat: {}, error: {}", chatId, response.description());
                                }
                                return Mono.empty();
                            });
                });
    }

    private void sendMessage(Long chatId, String message) {
        logger.debug("Sending message to chat: {}, message: {}", chatId, message);
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse response = telegramBot.execute(sendMessage);
        if (!response.isOk()) {
            logger.error("Failed to send message to chat: {}, error: {}", chatId, response.description());
        }
    }

    private Mono<MessageTemplateDto> getMessageTemplate(String messageType) {
        logger.debug("Getting message template by type: {}", messageType);
        return dbWebClient.get()
                .uri("/api/db/message-templates/" + messageType)
                .retrieve()
                .bodyToMono(MessageTemplateDto.class)
                .onErrorResume(e -> {
                    logger.error("Error getting message template with type: {}, error: {}", messageType, e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<Boolean> isButtonActive(String buttonType) {
        logger.debug("Checking if button is active, type: {}", buttonType);
        return dbWebClient.get()
                .uri("/api/db/button-activities/" + buttonType)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), clientResponse -> Mono.error(new RuntimeException("Client error")))
                .bodyToMono(ButtonActivityDto.class)
                .map(buttonActivity -> {
                    if (buttonActivity == null) {
                        return false;
                    }
                    LocalDate today = LocalDate.now();
                    return !today.isBefore(buttonActivity.getStartDate()) && !today.isAfter(buttonActivity.getEndDate());
                })
                .onErrorResume(e -> {
                    logger.error("Error getting button activity with type: {}, error: {}", buttonType, e.getMessage());
                    return Mono.just(false);
                })
                .switchIfEmpty(Mono.just(false));
    }

    private Mono<List<DirectionDto>> getAllDirections() {
        logger.debug("Getting all directions");
        return dbWebClient.get()
                .uri("/api/db/directions")
                .retrieve()
                .bodyToFlux(DirectionDto.class)
                .collectList()
                .onErrorResume(e -> {
                    logger.error("Error getting all directions, error: {}", e.getMessage());
                    return Mono.just(List.of());
                });
    }

    private Mono<DirectionDto> getDirectionByName(String directionName) {
        logger.debug("Getting direction by name: {}", directionName);
        return getAllDirections()
                .flatMap(directions -> Mono.justOrEmpty(directions.stream()
                        .filter(direction -> direction.getName().equals(directionName))
                        .findFirst()));
    }

    private void createEvent(Integer userId, String eventType, String details) {
        logger.debug("Creating event for user: {}, type: {}, details: {}", userId, eventType, details);
        EventDto eventDto = new EventDto();
        eventDto.setUserId(userId);
        eventDto.setEventType(eventType);
        Map<String, Object> detailsMap = new HashMap<>();
        detailsMap.put("details", details);
        eventDto.setDetails(detailsMap);
        dbWebClient.post()
                .uri("/api/db/events")
                .body(Mono.just(eventDto), EventDto.class)
                .retrieve()
                .bodyToMono(EventDto.class)
                .subscribe();
    }

    private void sendApplicationStatusMessage(Long chatId, Integer applicationId, String status) {
        logger.debug("Sending application status message to chat: {}, application id: {}, status: {}", chatId, applicationId, status);
        getMessageTemplate("application_status")
                .flatMap(statusTemplate -> {
                    if (statusTemplate != null) {
                        String statusMessage = statusTemplate.getMessageContent()
                                .replace("{status}", status);
                        sendMessage(chatId, statusMessage);
                    } else {
                        sendMessage(chatId, "Извините, произошла ошибка при отправке статуса заявки.");
                    }
                    return Mono.empty();
                })
                .subscribe();
    }

    public boolean isWaitingForName(Long chatId) {
        return "visitor".equals(userState.get(chatId));
    }

    public boolean isWaitingForLastName(Long chatId) {
        return "waiting_for_last_name".equals(userState.get(chatId));
    }

    public boolean isWaitingForCity(Long chatId) {
        return "waiting_for_city".equals(userState.get(chatId));
    }

    public boolean isWaitingForPhone(Long chatId) {
        return "waiting_for_phone".equals(userState.get(chatId));
    }

    public boolean isWaitingForEmail(Long chatId) {
        return "waiting_for_email".equals(userState.get(chatId));
    }

    public boolean isWaitingForDirection(Long chatId) {
        return "waiting_for_direction".equals(userState.get(chatId));
    }

    public void resetUserState(Long chatId) {
        logger.debug("Resetting user state for chat: {}", chatId);
        userState.remove(chatId);
        userRegistration.remove(chatId);
        applicationRegistration.remove(chatId);
    }

    @Scheduled(fixedRate = 180000) // Проверка каждые 3 минуты (для тестов для прода хватит суток/недели/месяца)
    public void checkButtonStatusAndSendNotifications() {
        logger.info("Checking button status and sending notifications if needed");
        isButtonActive(APPLY_BUTTON_TYPE)
                .flatMap(isApplyActive -> {
                    if (isApplyActive) {
                        return isButtonActive(PRE_APPLY_BUTTON_TYPE)
                                .flatMap(isPreApplyActive -> {
                                    String currentButtonStatus = isPreApplyActive ? PRE_APPLY_BUTTON_TYPE : APPLY_BUTTON_TYPE;
                                    if (!notificationsSent && previousButtonStatus != null && !previousButtonStatus.equals(currentButtonStatus) && currentButtonStatus.equals(APPLY_BUTTON_TYPE)) {
                                        logger.info("Button status changed from pre_apply to apply, sending notifications");
                                        notificationsSent = true;
                                        return sendOpenRecruitmentMessage();
                                    }
                                    previousButtonStatus = currentButtonStatus;
                                    return Mono.empty();
                                });
                    } else {
                        notificationsSent = false;
                        previousButtonStatus = null;
                        return Mono.empty();
                    }
                })
                .subscribe();
    }

    private Mono<Void> sendOpenRecruitmentMessage() {
        return dbWebClient.get()
                .uri("/api/db/users")
                .retrieve()
                .bodyToFlux(UserDto.class)
                .flatMap(userDto -> {
                    return dbWebClient.get()
                            .uri("/api/db/applications/user/" + userDto.getId())
                            .retrieve()
                            .bodyToMono(ApplicationDto.class)
                            .flatMap(applicationDto -> {
                                if (applicationDto.getType().equals(PRE_APPLY_TYPE)) {
                                    return getMessageTemplate("open_recruitment")
                                            .flatMap(openRecruitmentTemplate -> {
                                                if (openRecruitmentTemplate != null) {
                                                    sendMessage(userDto.getChatId(), openRecruitmentTemplate.getMessageContent());
                                                }
                                                return Mono.empty();
                                            });
                                }
                                return Mono.empty();
                            })
                            .onErrorResume(e -> {
                                logger.warn("User {} has no application", userDto.getId());
                                return Mono.empty();
                            });
                })
                .then();
    }

    private boolean isValidCyrillic(String text) {
        return text != null && CYRILLIC_PATTERN.matcher(text).matches();
    }

    private boolean isValidCity(String text) {
        return text != null && CITY_PATTERN.matcher(text).matches();
    }

    private boolean isValidPhone(String text) {
        return text != null && PHONE_PATTERN.matcher(text).matches();
    }

    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
