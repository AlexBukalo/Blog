package main.model.repository;

import main.model.GlobalSettings;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends CrudRepository<GlobalSettings, Long> {

    @Query("FROM GlobalSettings WHERE code = 'STATISTICS_IS_PUBLIC'")
    GlobalSettings getStatistics();

    @Query("FROM GlobalSettings WHERE code = 'POST_PREMODERATION'")
    GlobalSettings getModeration();
}
