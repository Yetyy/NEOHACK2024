package bot.neoflex.dbService.repository;

import bot.neoflex.dbService.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Application.
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    Optional<Application> findByUserId(Integer userId);

}
