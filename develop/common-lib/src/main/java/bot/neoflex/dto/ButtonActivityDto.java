package bot.neoflex.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO для передачи данных активности кнопки.
 */
@Data
@NoArgsConstructor
public class ButtonActivityDto {
    private Integer id;
    @NotBlank(message = "Button type cannot be blank")
    private String buttonType;
    @NotNull(message = "End date cannot be null")
    private LocalDate endDate;
    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;
}
