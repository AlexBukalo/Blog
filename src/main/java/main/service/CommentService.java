package main.service;

import main.exceptions.PostException;
import main.model.Post;
import main.model.PostComment;
import main.model.repository.PostCommentRepository;
import main.model.repository.PostRepository;
import main.model.repository.UserRepository;
import main.request.CommentRequest;
import main.service.Response.DefaultResponse;
import main.service.dto.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class CommentService {

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public Object commentAdd(CommentRequest commentRequest, User user) {
        main.model.User userDB = userRepository.findByEmail(user.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
        PostComment postComment = new PostComment();
        if(commentRequest.getText().equals("")) {
            DefaultResponse response = new DefaultResponse();
            response.setResult(false);
            Errors errors = new Errors();
            errors.setText("Текст комментария не задан");
            response.setErrors(errors);
            return response;
        } else {
            if(commentRequest.getParentId() != null) {
                postComment.setParentId(commentRequest.getParentId());
            }
            Post post = postRepository.findById(commentRequest.getPostId()).orElseThrow(
                    () -> new PostException(commentRequest.getPostId()));
            postComment.setPost(post);
            postComment.setText(commentRequest.getText());
            postComment.setTime(new Timestamp(System.currentTimeMillis()));
            postComment.setUser(userDB);
            postCommentRepository.save(postComment);
            return postComment.getId();
        }
    }
}
