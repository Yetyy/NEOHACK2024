package bot.neoflex.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для передачи данных направления обучения.
 */
@Data
@NoArgsConstructor
public class DirectionDto {
    private Integer id;
    @NotBlank(message = "Direction name cannot be blank")
    private String name;
    private String description;
    private boolean isActive;
}
