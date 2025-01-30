package bot.neoflex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи данных заявки.
 */
@Data
@NoArgsConstructor
public class ApplicationDto {
    private Integer id;
    @NotNull(message = "User ID cannot be null")
    private Integer userId;
    private Integer directionId;
    @NotBlank(message = "Application type cannot be blank")
    private String type;
    private String status;
}
