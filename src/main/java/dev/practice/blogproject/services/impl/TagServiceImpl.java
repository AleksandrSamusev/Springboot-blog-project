package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.tag.TagFullDto;
import dev.practice.blogproject.dtos.tag.TagNewDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.exceptions.InvalidParameterException;
import dev.practice.blogproject.exceptions.ResourceNotFoundException;
import dev.practice.blogproject.mappers.TagMapper;
import dev.practice.blogproject.models.Article;
import dev.practice.blogproject.models.Tag;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.TagRepository;
import dev.practice.blogproject.services.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;

    @Override
    public TagFullDto createTag(TagNewDto dto, Long articleId, Long userId) {
        if(dto == null || articleId == null || userId == null) {
            throw new InvalidParameterException("Invalid parameter");
        }
        Article article = articleRepository.findById(articleId).orElseThrow(()->
                new ResourceNotFoundException("article with given ID = " + articleId + " not found"));
        if(!article.getAuthor().getUserId().equals(userId)) {
            throw new ActionForbiddenException("Action forbidden for current user");
        } else {
            Tag tag = new Tag();
            tag.setName(dto.getName());
            tag.getArticles().add(article);
            Tag savedTag = tagRepository.save(tag);
            return TagMapper.toTagDto(savedTag);
        }
    }
}
