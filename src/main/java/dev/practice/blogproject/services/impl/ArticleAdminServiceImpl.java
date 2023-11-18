package dev.practice.blogproject.services.impl;

import dev.practice.blogproject.dtos.article.ArticleFullDto;
import dev.practice.blogproject.exceptions.ActionForbiddenException;
import dev.practice.blogproject.models.Role;
import dev.practice.blogproject.repositories.ArticleRepository;
import dev.practice.blogproject.repositories.UserRepository;
import dev.practice.blogproject.services.ArticleAdminService;
import dev.practice.blogproject.services.ArticlePrivateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleAdminServiceImpl implements ArticleAdminService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticlePrivateService articleService;

    @Override
    public List<ArticleFullDto> getAllArticlesByUserId(Long userId, Long authorId, Integer from,
                                                       Integer size, String status) {
        checkUserIsAdmin(userId);
        return articleService.getAllArticlesByUserId(authorId, from, size, status);
    }

    private void checkUserIsAdmin(Long userId) {
        if (userRepository.findById(userId).get().getRole() != Role.ADMIN) {
            log.error("User with id {} is not ADMIN", userId);
            throw new ActionForbiddenException(String.format(
                    "User with id %d is not ADMIN. Access is forbidden", userId));
        }
    }

}
