package main.model.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("SELECT t " +
            "FROM Tag t " +
            "LEFT JOIN Tag2Post tp on t.id = tp.tagId " +
            "LEFT JOIN Post p on p.id = tp.postId " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "AND t.name LIKE :query%")
    List<Tag> getTags(@Param("query") String query);

    @Query("SELECT t " +
            "FROM Tag t " +
            "LEFT JOIN Tag2Post tp on t.id = tp.tagId " +
            "LEFT JOIN Post p on p.id = tp.postId " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "AND t.name LIKE %:query%")
    Optional<Tag> getTag(@Param("query") String query);
}
