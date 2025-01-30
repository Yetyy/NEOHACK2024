package bot.neoflex.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * DTO для передачи данных события.
 */
@Data
@NoArgsConstructor
public class EventDto {
    private Integer id;
    @NotNull(message = "User ID cannot be null")
    private Integer userId;
    @NotBlank(message = "Event type cannot be blank")
    private String eventType;
    private Map<String, Object> details;
}
