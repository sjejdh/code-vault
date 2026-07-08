package com.codevault.service;

import com.codevault.common.exception.BusinessException;
import com.codevault.config.HotSnippetCache;
import com.codevault.dto.SnippetDTO;
import com.codevault.entity.Snippet;
import com.codevault.mapper.SnippetMapper;
import com.codevault.mapper.TagMapper;
import com.codevault.service.impl.SnippetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 代码片段服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("代码片段服务测试")
class SnippetServiceTest {

    @Mock
    private SnippetMapper snippetMapper;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private HotSnippetCache hotSnippetCache;

    @InjectMocks
    private SnippetServiceImpl snippetService;

    private SnippetDTO snippetDTO;
    private Snippet existingSnippet;

    @BeforeEach
    void setUp() {
        snippetDTO = new SnippetDTO();
        snippetDTO.setTitle("测试片段");
        snippetDTO.setDescription("测试描述");
        snippetDTO.setContent("public class Test {}");
        snippetDTO.setLanguage("Java");
        snippetDTO.setCategoryId(1L);
        snippetDTO.setIsPublic(1);
        snippetDTO.setTags(Arrays.asList("Spring Boot", "Java"));

        existingSnippet = new Snippet();
        existingSnippet.setId(1L);
        existingSnippet.setUserId(1L);
        existingSnippet.setTitle("已有片段");
        existingSnippet.setStatus(1);
        existingSnippet.setIsPublic(1);
    }

    @Test
    @DisplayName("创建片段成功")
    void createSnippet_success() {
        when(snippetMapper.insert(any(Snippet.class))).thenAnswer(invocation -> {
            Snippet snippet = invocation.getArgument(0);
            snippet.setId(1L); // 模拟自增ID
            return 1;
        });
        when(tagMapper.findByName(anyString())).thenReturn(null);
        when(tagMapper.insert(any())).thenReturn(1);

        Snippet result = snippetService.createSnippet(1L, snippetDTO);

        assertNotNull(result.getId());
        assertEquals("测试片段", result.getTitle());
        assertEquals(1, result.getIsPublic());
        verify(hotSnippetCache).clearCache();
    }

    @Test
    @DisplayName("创建片段失败 - 数据库插入返回0")
    void createSnippet_fail_whenInsertFails() {
        when(snippetMapper.insert(any(Snippet.class))).thenReturn(0);

        assertThrows(BusinessException.class,
                () -> snippetService.createSnippet(1L, snippetDTO));

        verify(tagMapper, never()).insertSnippetTag(anyLong(), anyLong());
    }

    @Test
    @DisplayName("更新片段成功 - 只能更新自己的片段")
    void updateSnippet_success() {
        when(snippetMapper.findById(1L)).thenReturn(existingSnippet);
        when(snippetMapper.update(any(Snippet.class))).thenReturn(1);

        assertDoesNotThrow(() -> snippetService.updateSnippet(1L, 1L, snippetDTO));
        verify(hotSnippetCache).clearCache();
    }

    @Test
    @DisplayName("更新片段失败 - 无权修改他人片段")
    void updateSnippet_fail_whenNotOwner() {
        existingSnippet.setUserId(2L);
        when(snippetMapper.findById(1L)).thenReturn(existingSnippet);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> snippetService.updateSnippet(1L, 1L, snippetDTO));
        assertEquals("无权修改他人的代码片段", exception.getMessage());

        verify(snippetMapper, never()).update(any());
    }

    @Test
    @DisplayName("删除片段成功")
    void deleteSnippet_success() {
        when(snippetMapper.findById(1L)).thenReturn(existingSnippet);
        when(snippetMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> snippetService.deleteSnippet(1L, 1L));
        verify(tagMapper).deleteBySnippetId(1L);
        verify(hotSnippetCache).clearCache();
    }

    @Test
    @DisplayName("删除片段失败 - 片段不存在")
    void deleteSnippet_fail_whenNotFound() {
        when(snippetMapper.findById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> snippetService.deleteSnippet(1L, 1L));
        assertEquals("代码片段不存在", exception.getMessage());
    }

    @Test
    @DisplayName("获取公开片段详情 - 私密片段不可查看")
    void getSnippetDetail_fail_whenPrivate() {
        existingSnippet.setIsPublic(0);
        when(snippetMapper.findById(1L)).thenReturn(existingSnippet);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> snippetService.getSnippetDetail(1L));
        assertEquals("该代码片段为私密片段，无法查看", exception.getMessage());
    }

    @Test
    @DisplayName("分页查询公开片段 - 返回正确分页结构")
    void getPublicSnippets_success() {
        List<Snippet> mockList = Collections.singletonList(existingSnippet);
        when(snippetMapper.findPublicSnippets(any(), any(), any(), anyInt(), anyInt()))
                .thenReturn(mockList);
        when(snippetMapper.countPublicSnippets(any(), any(), any())).thenReturn(1);
        when(tagMapper.findBySnippetIds(anyList())).thenReturn(new ArrayList<>());

        Map<String, Object> result = snippetService.getPublicSnippets(null, null, null, 1, 10);

        assertEquals(1, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(10, result.get("pageSize"));
        assertNotNull(result.get("list"));
    }
}
