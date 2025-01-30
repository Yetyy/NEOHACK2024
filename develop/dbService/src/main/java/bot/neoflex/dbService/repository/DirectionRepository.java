package bot.neoflex.dbService.repository;

import bot.neoflex.dbService.entity.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью Direction.
 */
@Repository
public interface DirectionRepository extends JpaRepository<Direction, Integer> {
}
