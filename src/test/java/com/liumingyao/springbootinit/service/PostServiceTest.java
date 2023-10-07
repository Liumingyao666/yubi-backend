package com.liumingyao.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liumingyao.springbootinit.model.dto.post.PostQueryRequest;
import com.liumingyao.springbootinit.model.entity.Post;
import com.liumingyao.springbootinit.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 帖子服务测试
 *
 * @author <a href="https://github.com/Liumingyao666">刘铭垚</a>
 *   
 */
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostService postService;

    @Test
    void searchFromEs() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setUserId(1L);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        Assertions.assertNotNull(postPage);
    }

}