package main.model.repository;

import main.model.Post;
import main.model.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, Long> {
    @Query("FROM PostVote WHERE post = :post AND value = :value")
    Optional<PostVote> getPostVote(@Param("post") Post post, @Param("value") int value);
}
