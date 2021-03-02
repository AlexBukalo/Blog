package main.service;

import main.exceptions.ForbiddenException;
import main.exceptions.PostException;
import main.model.*;
import main.model.repository.PostVoteRepository;
import main.model.repository.SettingsRepository;
import main.model.repository.UserRepository;
import main.request.ModerationRequest;
import main.request.PostRequest;
import main.request.VoteRequest;
import main.service.Response.DefaultResponse;
import main.service.Response.Statistics;
import main.service.dto.*;
import main.model.repository.PostRepository;
import main.service.dto.Calendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostService {


    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagService tagService;
    private final SettingsRepository settingsRepository;
    private final PostVoteRepository postVoteRepository;

    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, TagService tagService,
                       SettingsRepository settingsRepository, PostVoteRepository postVoteRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.tagService = tagService;
        this.settingsRepository = settingsRepository;
        this.postVoteRepository = postVoteRepository;
    }


    public CountPosts getPosts(int offset, int limit, Mode mode) {
        Pageable pageable;
        Page<Post> posts = null;
        switch (mode) {
            case recent:
                pageable = PageRequest.of(offset / 10, limit, JpaSort.unsafe(Sort.Direction.DESC, "time"));
                posts = postRepository.findPosts(pageable);
                break;
            case popular:
                pageable = PageRequest.of(offset / 10, limit);
                posts = postRepository.findPostsOrderByComments(pageable);
                break;
            case best:
                pageable = PageRequest.of(offset / 10, limit);
                posts = postRepository.findPostsOrderByLikes(pageable);
                break;
            case early:
                pageable = PageRequest.of(offset / 10, limit, JpaSort.unsafe(Sort.Direction.ASC, "time"));
                posts = postRepository.findPosts(pageable);
                break;
        }

        return mapPosts(posts);
    }

    public CountPosts getPostsByQuery(int offset, int limit, String query) {
        Pageable pageable = PageRequest.of(offset / 10, limit);
        if (query.equals("")) {
            return mapPosts(postRepository.findAllPosts(pageable));
        } else
            return mapPosts(postRepository.findPostsByQuery(query, pageable));
    }

    public CountPosts getPostsByTag(int offset, int limit, String tag) {
        Pageable pageable = PageRequest.of(offset / 10, limit);
        return mapPosts(postRepository.findPostsByTag(tag, pageable));
    }

    public CountPosts getPostsByDate(int offset, int limit, String date) {
        Pageable pageable = PageRequest.of(offset / 10, limit);
        return mapPosts(postRepository.findPostsByDate(date, pageable));
    }

    public CountPosts getMyPosts(int offset, int limit, String status, Principal principal) {
        Pageable pageable = PageRequest.of(offset / 10, limit);
        Page<Post> posts = null;

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        switch (status) {
            case "inactive":
                posts = postRepository.getMyPostsNoneActive(user.getId(), pageable);
                break;
            case "pending":
                posts = postRepository.getMyPostsPending(user.getId(), pageable);
                break;
            case "declined":
                posts = postRepository.getMyPostsDeclined(user.getId(), pageable);
                break;
            case "published":
                posts = postRepository.getMyPostsPublished(user.getId(), pageable);
                break;
        }

        return mapPosts(posts);
    }

    public PostForId getPostById(long id, org.springframework.security.core.userdetails.User user) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(id));
        List<PostVote> postVotes = post.getPostVote();
        int likeCount = 0;
        int dislikeCount = 0;
        if (postVotes != null) {
            for (PostVote postVote : postVotes) {
                if (postVote.getValue() == 1) likeCount++;
                if (postVote.getValue() == -1) dislikeCount++;
            }
        }

        List<PostComment> commentsPost = post.getPostComment();
        List<CommentDto> comments = new ArrayList<>();
        commentsPost.forEach(postComment -> comments.add(new CommentDto(postComment)));

        List<Tag> tagsPost = post.getTag();
        String[] tags = new String[tagsPost.size()];
        for (int i = 0; i < tags.length; i++) {
            tags[i] = tagsPost.get(i).getName();
        }

        if (user == null) {
            postRepository.updateViews(post.getId());
        } else {
            User userDB = userRepository.findByEmail(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("not found"));
            if (userDB.getId() != post.getUser().getId() && userDB.getIsModerator() != 1) {
                postRepository.updateViews(post.getId());
            }
        }

        return new PostForId(post, likeCount, dislikeCount, comments, tags);
    }

    public Calendar calendar() {
        List<Post> posts = postRepository.findAllPosts();
        List<Integer> yearsFromPost = postRepository.years();
        int[] years = new int[yearsFromPost.size()];
        for (int i = 0; i < yearsFromPost.size(); i++) {
            years[i] = yearsFromPost.get(i);
        }
        Map<String, Integer> postsMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        posts.forEach(post -> {
            String date = dateFormat.format(post.getTime());

            if (!postsMap.containsKey(date)) {
                postsMap.put(date, 1);
            } else {
                int count = postsMap.get(date) + 1;
                postsMap.put(date, count);
            }
        });
        return new Calendar(years, postsMap);
    }

    public CountPosts getPostsForModeration(int offset, int limit, String status, org.springframework.security.core.userdetails.User user) {
        Pageable pageable = PageRequest.of(offset, limit);
        User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
        Page<Post> posts = null;

        switch (status) {
            case "new":
                posts = postRepository.getPostsForModerationNew(pageable);
                break;
            case "declined":
                posts = postRepository.getPostsForModerationDeclined(pageable);
                break;
            case "accepted":
                posts = postRepository.getPostsForMyModeration(userDB, pageable);
                break;
        }

        return mapPosts(posts);
    }

    public DefaultResponse addPost(PostRequest postRequest, org.springframework.security.core.userdetails.User user) {
        User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        DefaultResponse defaultResponse = new DefaultResponse();
        defaultResponse.setResult(true);

        Errors errors = new Errors();

        if (postRequest.getTitle().length() >= 3 && postRequest.getText().length() >= 50) {
            Post post = new Post();
            post.setIsActive(postRequest.getActive());
            post.setText(postRequest.getText());
            post.setAnnounce(postRequest.getText().replaceAll("<[^>]*>","").substring(0, 25).replace("&nbsp;"," ")
                    + "...");
            post.setUser(userDB);
            if(settingsRepository.getModeration().getValue().equals("NO")) {
                post.setStatus(Post.ModerationStatus.ACCEPTED);
            } else post.setStatus(Post.ModerationStatus.NEW);
            post.setTitle(postRequest.getTitle());
            post.setViewCount(0);
            if (postRequest.getTimestamp() < System.currentTimeMillis()) {
                post.setTime(new Timestamp(System.currentTimeMillis()));
            } else post.setTime(new Timestamp(postRequest.getTimestamp()));
            postRepository.save(post);

            String[] tags = postRequest.getTags();
            for (String tag : tags) {
                tagService.addTag(tag, post);
            }

        } else {
            if (postRequest.getText().length() < 50) {
                errors.setText("Текст публикации слишком короткий");
                defaultResponse.setResult(false);
            }
            if (postRequest.getTitle().length() < 3) {
                errors.setTitle("Заголовок не установлен");
                defaultResponse.setResult(false);
            }
            defaultResponse.setErrors(errors);
        }


        return defaultResponse;
    }

    public DefaultResponse redPost(long id, PostRequest postRequest, org.springframework.security.core.userdetails.User user) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(id));
        User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
        DefaultResponse defaultResponse = new DefaultResponse();
        defaultResponse.setResult(true);

        Errors errors = new Errors();

        if (postRequest.getTitle().length() >= 3 && postRequest.getText().length() >= 50) {
            post.setText(postRequest.getText());
            post.setAnnounce(postRequest.getText().substring(0, 25).replaceAll("<img.*?nbsp;", "")
                    + "...");
            post.setTitle(postRequest.getTitle());
            post.setViewCount(0);
            if (postRequest.getTimestamp() < System.currentTimeMillis()) {
                post.setTime(new Timestamp(System.currentTimeMillis()));
            } else post.setTime(new Timestamp(postRequest.getTimestamp()));
            if (userDB.getIsModerator() != 1) {
                post.setStatus(Post.ModerationStatus.NEW);
            }
            postRepository.save(post);

            String[] tags = postRequest.getTags();
            for (String tag : tags) {
                tagService.addTag(tag, post);
            }

        } else {
            if (postRequest.getText().length() < 50) {
                errors.setText("Текст публикации слишком короткий");
                defaultResponse.setResult(false);
            }
            if (postRequest.getTitle().length() < 3) {
                errors.setTitle("Заголовок не установлен");
                defaultResponse.setResult(false);
            }
            defaultResponse.setErrors(errors);
        }

        return defaultResponse;
    }

    public DefaultResponse moderationDecision(ModerationRequest request, org.springframework.security.core.userdetails.User user) {
        Post post = postRepository.findById(request.getPostId()).orElseThrow(() -> new PostException(request.getPostId()));
        User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
        DefaultResponse defaultResponse = new DefaultResponse();
        post.setModeratorId(userDB);
        if (request.getDecision().equals("accept")) {
            post.setStatus(Post.ModerationStatus.ACCEPTED);
        } else post.setStatus(Post.ModerationStatus.DECLINED);
        post.setModeratorId(userDB);
        postRepository.save(post);
        defaultResponse.setResult(true);
        return defaultResponse;
    }

    public Statistics statisticsMy(org.springframework.security.core.userdetails.User user) {
        if(user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } else {
            User userDB = userRepository.findByEmail(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("not found"));
            List<Post> posts = postRepository.findAllPostsByUser(userDB.getId());

            int postsCount = 0;
            int likesCount = 0;
            int dislikesCount = 0;
            int viewsCount = 0;
            long firstPublication = Long.MAX_VALUE;

            for (Post post : posts) {
                postsCount++;
                viewsCount += post.getViewCount();
                List<PostVote> postVotes = post.getPostVote();
                if (postVotes != null) {
                    for (PostVote postVote : postVotes) {
                        if (postVote.getValue() == 1) likesCount++;
                        if (postVote.getValue() == -1) dislikesCount++;
                    }
                }
                if (firstPublication > post.getTime().getTime() / 1000) {
                    firstPublication = post.getTime().getTime() / 1000;
                }
            }
            return new Statistics(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);
        }
    }

    public Object statisticsAll(org.springframework.security.core.userdetails.User user) {
        User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));

        GlobalSettings settings = settingsRepository.getStatistics();

        int postsCount = 0;
        int likesCount = 0;
        int dislikesCount = 0;
        int viewsCount = 0;
        long firstPublication = Long.MAX_VALUE;

        if (userDB.getIsModerator() == 0 && settings.getValue().equals("NO")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else {
            List<Post> posts = postRepository.findAllPosts();

            for (Post post : posts) {
                postsCount++;
                viewsCount += post.getViewCount();
                List<PostVote> postVotes = post.getPostVote();
                if (postVotes != null) {
                    for (PostVote postVote : postVotes) {
                        if (postVote.getValue() == 1) likesCount++;
                        if (postVote.getValue() == -1) dislikesCount++;
                    }
                }
                if (firstPublication > post.getTime().getTime() / 1000) {
                    firstPublication = post.getTime().getTime() / 1000;
                }
            }
        }


        return new Statistics(postsCount, likesCount, dislikesCount, viewsCount, firstPublication);
    }

    public DefaultResponse like(VoteRequest request, org.springframework.security.core.userdetails.User user, int value) {
        DefaultResponse defaultResponse = new DefaultResponse();

        if(user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        } else {
            User userDB = userRepository.findByEmail(user.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("not found"));

            if (postVoteRepository.getPostVote(postRepository.findById(request.getPostId()).get(), value, userDB).isPresent()) {
                defaultResponse.setResult(false);
            } else {
                if (postVoteRepository.getPostVote(postRepository.findById(request.getPostId()).get(), -value, userDB).isPresent()) {
                    postVoteRepository.delete(postVoteRepository.getPostVote(postRepository.findById(request.getPostId()).get(), -value, userDB).get());
                }
                PostVote postVote = new PostVote();
                postVote.setPost(postRepository.findById(request.getPostId()).get());
                postVote.setTime(new Timestamp(System.currentTimeMillis()));
                postVote.setUser(userDB);
                postVote.setValue(value);
                defaultResponse.setResult(true);
                postVoteRepository.save(postVote);
            }
        }

        return defaultResponse;
    }


    private CountPosts mapPosts(Page<Post> posts) {
        List<PostsForMainPage> postsForMainPageList = new ArrayList<>();

        for (Post post : posts) {
            int likeCount = 0;
            int dislikeCount = 0;
            List<PostVote> postVotes = post.getPostVote();
            if (postVotes != null) {
                for (PostVote postVote : postVotes) {
                    if (postVote.getValue() == 1) likeCount++;
                    if (postVote.getValue() == -1) dislikeCount++;
                }
            }

            int commentCount = 0;
            if (post.getPostComment() != null) commentCount = post.getPostComment().size();

            postsForMainPageList.add(new PostsForMainPage(post, likeCount, dislikeCount, commentCount));
        }
        return new CountPosts(posts.getTotalElements(), postsForMainPageList);
    }
}
