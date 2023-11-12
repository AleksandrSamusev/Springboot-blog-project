package dev.practice.blogproject.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.practice.blogproject.controllers._private.ArticlePrivateController;
import dev.practice.blogproject.services.ArticlePrivateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ArticlePrivateController.class)
public class ArticleControllerTest {
    @MockBean
    private ArticlePrivateService articleService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;


}
