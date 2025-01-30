package bot.neoflex.dbService.repository;

import bot.neoflex.dbService.entity.ButtonActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью ButtonActivity.
 */
@Repository
public interface ButtonActivityRepository extends JpaRepository<ButtonActivity, Integer> {
    Optional<ButtonActivity> findByButtonType(String buttonType);
}
