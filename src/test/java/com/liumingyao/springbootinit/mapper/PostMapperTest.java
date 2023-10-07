package com.liumingyao.springbootinit.mapper;

import com.liumingyao.springbootinit.mapper.PostMapper;
import com.liumingyao.springbootinit.model.entity.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作测试
 *
 * @author <a href="https://github.com/Liumingyao666">刘铭垚</a>
 *   
 */
@SpringBootTest
class PostMapperTest {

    @Resource
    private PostMapper postMapper;

    @Test
    void listPostWithDelete() {
        List<Post> postList = postMapper.listPostWithDelete(new Date());
        Assertions.assertNotNull(postList);
    }
}