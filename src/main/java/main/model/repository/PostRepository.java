package main.model.repository;

import main.model.Post;
import main.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface
PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "GROUP BY p"
    )
    Page<Post> findAllPosts(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "GROUP BY p.id"
    )
    Page<Post> findPosts(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(pc) DESC"
    )
    Page<Post> findPostsOrderByComments(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostVote pvl on pvl.post = p.id and pvl.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(pvl.value) DESC"
    )
    Page<Post> findPostsOrderByLikes(Pageable pageable);


    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "AND (p.text LIKE %:query% OR p.title LIKE %:query% OR p.announce LIKE %:query%) " +
            "GROUP BY p.id"
    )
    Page<Post> findPostsByQuery(@Param("query") String query, Pageable pageable);


    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' " +
            "AND cast(p.time as date) = date(:date) " +
            "GROUP BY p.id "
    )
    Page<Post> findPostsByDate(@Param("date") String date, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "LEFT JOIN Tag2Post tg on tg.postId = p.id " +
            "LEFT JOIN Tag t on t.id = tg.tagId " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "AND t.name LIKE :tag " +
            "GROUP BY p.id "
    )
    Page<Post> findPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "WHERE p.status != 'ACCEPTED' AND p.time <= current_time()")
    List<Post> postsForModeration();

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN User u on u.id = p.user AND u.id = :id " +
            "WHERE p.isActive = 1 AND p.status = 'NEW'")
    Page<Post> getMyPostsPending(@Param("id") int id, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN User u on u.id = p.user AND u.id = :id " +
            "WHERE p.isActive = 0")
    Page<Post> getMyPostsNoneActive(@Param("id") int id, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN User u on u.id = p.user AND u.id = :id " +
            "WHERE p.isActive = 1 AND p.status = 'DECLINED'")
    Page<Post> getMyPostsDeclined(@Param("id") int id, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN User u on u.id = p.user AND u.id = :id " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED'")
    Page<Post> getMyPostsPublished(@Param("id") int id, Pageable pageable);

    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    @Modifying
    @Transactional
    void updateViews(@Param("id") long id);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() " +
            "GROUP BY p"
    )
    List<Post> findAllPosts();

    @Query(value = "SELECT DATE_FORMAT(`time` , '%Y') as new_date FROM posts group by new_date", nativeQuery = true)
    List<Integer> years();

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'NEW' AND p.time <= current_time()" +
            "GROUP BY p")
    Page<Post> getPostsForModerationNew(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'DECLINED' AND p.time <= current_time()" +
            "GROUP BY p")
    Page<Post> getPostsForModerationDeclined(Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.moderatorId = :moderator AND p.time <= current_time()" +
            "GROUP BY p")
    Page<Post> getPostsForMyModeration(@Param("moderator") User user, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "LEFT JOIN User u ON u.id = p.user " +
            "LEFT JOIN PostComment pc ON pc.post = p.id " +
            "LEFT JOIN PostVote pv on pv.post = p.id and pv.value = 1 " +
            "WHERE p.isActive = 1 AND p.status = 'ACCEPTED' AND p.time <= current_time() AND u.id = :id " +
            "GROUP BY p"
    )
    List<Post> findAllPostsByUser(@Param("id") int id);
}