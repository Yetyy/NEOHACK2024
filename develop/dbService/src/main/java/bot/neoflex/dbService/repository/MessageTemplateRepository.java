package bot.neoflex.dbService.repository;

import bot.neoflex.dbService.entity.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью MessageTemplate.
 */
@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Integer> {
    Optional<MessageTemplate> findByMessageType(String messageType);
}
