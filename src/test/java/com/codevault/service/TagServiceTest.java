package com.codevault.service;

import com.codevault.common.exception.BusinessException;
import com.codevault.entity.TagEntity;
import com.codevault.mapper.TagMapper;
import com.codevault.service.impl.TagServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 标签服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("标签服务测试")
class TagServiceTest {

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    @Test
    @DisplayName("查询所有标签")
    void findAll_success() {
        TagEntity tag1 = new TagEntity(1L, "Java", null);
        TagEntity tag2 = new TagEntity(2L, "Python", null);
        when(tagMapper.findAll()).thenReturn(Arrays.asList(tag1, tag2));

        List<TagEntity> result = tagService.findAll();

        assertEquals(2, result.size());
        verify(tagMapper).findAll();
    }

    @Test
    @DisplayName("创建标签 - 新标签")
    void create_newTag_success() {
        when(tagMapper.findByName("Docker")).thenReturn(null);
        when(tagMapper.insert(any(TagEntity.class))).thenAnswer(invocation -> {
            TagEntity tag = invocation.getArgument(0);
            tag.setId(3L);
            return 1;
        });

        TagEntity result = tagService.create("Docker");

        assertEquals("Docker", result.getName());
        verify(tagMapper).insert(argThat(tag -> tag.getName().equals("Docker")));
    }

    @Test
    @DisplayName("创建标签 - 已存在则直接返回")
    void create_existingTag_returnExisting() {
        TagEntity existing = new TagEntity(1L, "Java", null);
        when(tagMapper.findByName("Java")).thenReturn(existing);

        TagEntity result = tagService.create("Java");

        assertEquals(1L, result.getId());
        verify(tagMapper, never()).insert(any());
    }

    @Test
    @DisplayName("创建标签 - 名称为空抛异常")
    void create_fail_whenNameEmpty() {
        assertThrows(BusinessException.class, () -> tagService.create(""));
        assertThrows(BusinessException.class, () -> tagService.create(null));
        assertThrows(BusinessException.class, () -> tagService.create("   "));
    }
}
