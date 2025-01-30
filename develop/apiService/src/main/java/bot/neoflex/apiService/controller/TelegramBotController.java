// bot.neoflex.apiService.controller.TelegramBotController
package bot.neoflex.apiService.controller;

import bot.neoflex.apiService.service.TelegramBotService;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Контроллер для обработки сообщений от Telegram бота.
 */
@RestController
@RequiredArgsConstructor
public class TelegramBotController {

    private final TelegramBot telegramBot;
    private final TelegramBotService telegramBotService;
    private final WebClient dbWebClient;
    private static final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);
    private static final int MAX_RETRIES = 5;
    private static final Duration RETRY_DELAY = Duration.ofSeconds(5);

    /**
     * Инициализация бота и установка слушателя обновлений.
     */
    @PostConstruct
    public void init() {
        checkDbServiceAvailability()
                .doOnSuccess(aVoid -> {
                    telegramBotService.init();
                    telegramBot.setUpdatesListener(updates -> {
                        updates.forEach(this::processUpdate);
                        return UpdatesListener.CONFIRMED_UPDATES_ALL;
                    }, e -> {
                        if (e.response() != null) {
                            logger.error("Error from Telegram API: {}", e.response().description());
                        } else {
                            logger.error("Error: {}", e.getMessage());
                        }
                    });
                    logger.info("Telegram bot initialized and listening for updates.");
                })
                .doOnError(throwable -> {
                    logger.error("Failed to start Telegram bot due to db service unavailability: {}", throwable.getMessage());
                    System.exit(1);
                })
                .subscribe();

    }

    /**
     * Проверяет доступность dbService.
     *
     * @return Mono, который завершается, когда dbService доступен.
     */
    private Mono<Void> checkDbServiceAvailability() {
        logger.info("Checking db service availability...");
        return dbWebClient.get()
                .uri("/actuator/health")
                .exchange()
                .flatMap(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.OK)) {
                        logger.info("db service is available.");
                        return Mono.empty();
                    } else {
                        logger.warn("db service is not available, status code: {}", clientResponse.statusCode());
                        return Mono.error(new RuntimeException("db service is not available"));
                    }
                })
                .retryWhen(reactor.util.retry.Retry.fixedDelay(MAX_RETRIES, RETRY_DELAY)
                        .doBeforeRetry(retrySignal -> logger.warn("Retrying db service availability check, attempt: {}", retrySignal.totalRetries() + 1))
                )
                .then();

    }

    /**
     * Обрабатывает каждое обновление от Telegram.
     *
     * @param update Обновление от Telegram.
     */
    private void processUpdate(Update update) {
        logger.debug("Received update: {}", update);
        if (update.message() != null && update.message().text() != null) {
            Message message = update.message();
            String messageText = message.text();
            Long chatId = message.chat().id();
            if (messageText.equals("/start")) {
                telegramBotService.handleStartCommand(chatId);
            }  else if (telegramBotService.isWaitingForName(chatId)) {
                telegramBotService.handleNameInput(chatId, messageText);
            } else if (telegramBotService.isWaitingForLastName(chatId)) {
                telegramBotService.handleLastNameInput(chatId, messageText);
            } else if (telegramBotService.isWaitingForCity(chatId)) {
                telegramBotService.handleCityInput(chatId, messageText);
            } else if (telegramBotService.isWaitingForPhone(chatId)) {
                telegramBotService.handlePhoneInput(chatId, messageText);
            } else if (telegramBotService.isWaitingForEmail(chatId)) {
                telegramBotService.handleEmailInput(chatId, messageText);
            } else if (messageText.equals("Подать заявку")) {
                telegramBotService.handleApplyCommand(chatId);
            } else if (messageText.equals("Оставить предзаявку")) {
                telegramBotService.handlePreApplyCommand(chatId);
            } else if (telegramBotService.isWaitingForDirection(chatId)) {
                telegramBotService.handleDirectionInput(chatId, messageText);
            } else {
                telegramBotService.handleUnknownCommand(chatId);
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            if (callbackQuery.message() != null) {
                String callbackData = callbackQuery.data();
                Long chatId = callbackQuery.message().chat().id();
                if (callbackData.startsWith("direction_")) {
                    telegramBotService.handleDirectionCallback(chatId, callbackData.substring(10));
                } else if (callbackData.equals("apply")) {
                    telegramBotService.handleApplyCommand(chatId);
                } else if (callbackData.equals("pre_apply")) {
                    telegramBotService.handlePreApplyCommand(chatId);
                }
            }
        }
    }
}
