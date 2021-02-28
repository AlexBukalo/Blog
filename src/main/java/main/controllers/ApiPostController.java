package main.controllers;


import main.request.PostRequest;
import main.request.VoteRequest;
import main.service.Response.DefaultResponse;
import main.service.dto.CountPosts;
import main.service.Mode;
import main.service.PostService;
import main.service.dto.PostForId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public CountPosts getPosts(@RequestParam("offset") int offset,
                               @RequestParam("limit") int limit,
                               @RequestParam("mode") Mode mode) {
        return postService.getPosts(offset, limit, mode);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public CountPosts getPostsQuery(@RequestParam("offset") int offset,
                                    @RequestParam("limit") int limit,
                                    @RequestParam("query") String query) {
        return postService.getPostsByQuery(offset, limit, query);
    }

    @RequestMapping(value = "/byDate", method = RequestMethod.GET)
    public CountPosts getPostsByDate(@RequestParam("offset") int offset,
                                     @RequestParam("limit") int limit,
                                     @RequestParam("date") String date) {
        return postService.getPostsByDate(offset, limit, date);
    }

    @RequestMapping(value = "/byTag", method = RequestMethod.GET)
    public CountPosts getPostsByTag(@RequestParam("offset") int offset,
                                    @RequestParam("limit") int limit,
                                    @RequestParam("tag") String tag) {
        return postService.getPostsByTag(offset, limit, tag);
    }

    @RequestMapping(value = "/my", method = RequestMethod.GET)
    public CountPosts getMyPosts(@RequestParam("offset") int offset,
                        @RequestParam("limit") int limit,
                        @RequestParam("status") String status,
                        Principal principal) {
       return postService.getMyPosts(offset, limit, status, principal);
    }

    @RequestMapping(value = "/{ID}", method = RequestMethod.GET)
    public PostForId getMyPostsById(@PathVariable("ID") long id, @AuthenticationPrincipal User user) {
        return postService.getPostById(id, user);
    }

    @RequestMapping(value = "moderation", method = RequestMethod.GET)
    public CountPosts getPostsForModeration(@RequestParam("offset") int offset,
                                            @RequestParam("limit") int limit,
                                            @RequestParam("status") String status,
                                            @AuthenticationPrincipal User user) {
        return postService.getPostsForModeration(offset, limit, status ,user);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public DefaultResponse addPost(@RequestBody PostRequest postRequest,
                                   @AuthenticationPrincipal User user) {
        return postService.addPost(postRequest, user);
    }

    @RequestMapping(value = "/{ID}", method = RequestMethod.PUT)
    public DefaultResponse redPost(@PathVariable("ID") long id,
                                   @RequestBody PostRequest postRequest,
                                   @AuthenticationPrincipal User user) {
        return postService.redPost(id, postRequest, user);
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public DefaultResponse like(@RequestBody VoteRequest request, @AuthenticationPrincipal User user) {
        return postService.like(request, user, 1);
    }

    @RequestMapping(value = "/dislike", method = RequestMethod.POST)
    public DefaultResponse dislike(@RequestBody VoteRequest request, @AuthenticationPrincipal User user) {
        return postService.like(request, user, -1);
    }
}
