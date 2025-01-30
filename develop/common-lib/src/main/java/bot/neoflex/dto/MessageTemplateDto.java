package bot.neoflex.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для передачи данных шаблона сообщения.
 */
@Data
@NoArgsConstructor
public class MessageTemplateDto {
    private Integer id;
    @NotBlank(message = "Message type cannot be blank")
    private String messageType;
    @NotBlank(message = "Message content cannot be blank")
    private String messageContent;
}
