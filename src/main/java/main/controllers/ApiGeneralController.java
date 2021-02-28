package main.controllers;

import main.request.CommentRequest;
import main.request.ModerationRequest;
import main.request.ProfileRequest;
import main.request.SettingsRequest;
import main.service.*;
import main.service.Response.DefaultResponse;
import main.service.Response.Statistics;
import main.service.dto.Calendar;
import main.service.dto.Settings;
import main.service.dto.TagForApi;
import main.model.BlogInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class ApiGeneralController {
    @Autowired
    private BlogInfo blogInfo;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostService postService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CommentService commentService;

    @Autowired
    LoginService loginService;

    @GetMapping("/api/init")
    public BlogInfo init() {
        return blogInfo;
    }

    @GetMapping("/api/settings")
    public Settings settings() {
        return settingsService.getSettings();
    }

    @GetMapping("/api/tag")
    public TagForApi tag(@RequestParam(value = "query", required = false) String query) {
        return tagService.getTags(query);
    }

    @GetMapping("/api/calendar")
    public Calendar calendar() {
        return postService.calendar();
    }

    @PostMapping("/api/image")
    public Object uploadImage(@RequestParam("image") MultipartFile image,
                              HttpServletRequest request) {
        return imageService.uploadImage(image, request);
    }

    @PostMapping("/api/comment")
    public Object addComment(@RequestBody CommentRequest commentRequest,
                             @AuthenticationPrincipal User user) {
        return commentService.commentAdd(commentRequest, user);
    }

    @RequestMapping(value = "/api/moderation", method = RequestMethod.POST)
    public DefaultResponse moderationPost(@RequestBody ModerationRequest request,
                                          @AuthenticationPrincipal User user) {
        return postService.moderationDecision(request, user);
    }

    @RequestMapping(value = "/api/profile/my", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public DefaultResponse redProfilePhoto(@AuthenticationPrincipal User user,
                                           @RequestParam(name = "name") String name,
                                           @RequestParam(name = "email") String email,
                                           @RequestParam(name = "password", required = false) String password,
                                           @RequestParam(name = "removePhoto", required = false) Integer removePhoto,
                                           @RequestParam(name = "photo", required = false) final MultipartFile photo,
                                           HttpServletRequest request) {
        return loginService.redProfilePhoto(name, email, password, photo, removePhoto, user, request);
    }

    @RequestMapping(value = "/api/profile/my", method = RequestMethod.POST)
    public DefaultResponse redProfile(@RequestBody ProfileRequest profileRequest,
                                      @AuthenticationPrincipal User user) {
        return loginService.redProfile(profileRequest, user);
    }

    @RequestMapping(value = "/api/statistics/my", method = RequestMethod.GET)
    public Statistics statisticsMy(@AuthenticationPrincipal User user) {
        return postService.statisticsMy(user);
    }

    @RequestMapping(value = "/api/statistics/all", method = RequestMethod.GET)
    public Object statisticsAll(@AuthenticationPrincipal User user) {
        return postService.statisticsAll(user);
    }

    @RequestMapping(value = "/api/settings", method = RequestMethod.PUT)
    public void setSettings(@RequestBody SettingsRequest request, @AuthenticationPrincipal User user) {
        settingsService.setSettings(request, user);
    }
}
