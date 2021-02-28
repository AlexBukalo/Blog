package main.service;

import main.model.Post;
import main.model.Tag2Post;
import main.model.repository.Tag2PostRepository;
import main.service.dto.TagDto;
import main.service.dto.TagForApi;
import main.model.Tag;
import main.model.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private Tag2PostRepository tag2PostRepository;

    public TagForApi getTags(String query) {
        List<Tag> tags;
        if (query == null) {
            tags = tagRepository.getTags("");
        } else tags = tagRepository.getTags(query);

        double maxCount = 1;
        Map<String, Integer> tagAndCount = new HashMap<>();
        for (Tag tag : tags) {
            if (!tagAndCount.containsKey(tag.getName())) {
                tagAndCount.put(tag.getName(), 1);
            } else {
                int count = tagAndCount.get(tag.getName()) + 1;
                if (count > maxCount) maxCount = count;
                tagAndCount.put(tag.getName(), count);
            }
        }

        List<TagDto> tagDtoList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : tagAndCount.entrySet()) {
            double weight = Math.round(entry.getValue() / maxCount * 100.0) / 100.0;
            tagDtoList.add(new TagDto(entry.getKey(), weight));
        }

        return new TagForApi(tagDtoList);
    }

    public void addTag(String tag, Post post) {
        Tag newTag = new Tag();
        if(tagRepository.getTag(tag).isEmpty()) {
            newTag.setName(tag);
            tagRepository.save(newTag);
        } else newTag = tagRepository.getTag(tag).get();

        Tag2Post tag2Post = new Tag2Post();
        tag2Post.setPostId(post.getId());
        tag2Post.setTagId(newTag.getId());
        tag2PostRepository.save(tag2Post);
    }
}
