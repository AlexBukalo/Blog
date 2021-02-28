package main.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private short isActive;

    @Enumerated(EnumType.STRING)
    private ModerationStatus status;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "moderator_id")
    private User moderatorId;

    @NotNull
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Basic
    @Column(columnDefinition = "DATETIME")
    private Timestamp time;

    @NotNull
    private String title;

    @NotNull
    private String announce;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String text;

    @NotNull
    private int viewCount;

    @ManyToMany(mappedBy = "post")
    private List<Tag> tag = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostVote> postVote = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostComment> postComment = new ArrayList<>();


    public enum ModerationStatus {
        NEW, ACCEPTED, DECLINED
    }
}
