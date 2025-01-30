package bot.neoflex.dbService.repository;

import bot.neoflex.dbService.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью Event.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
}
