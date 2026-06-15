package com.codevault.mapper;

import com.codevault.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签数据访问层接口
 * 提供标签的增删改查以及片段-标签关联操作
 */
@Mapper
public interface TagMapper {

    /**
     * 查询所有标签
     * @return 标签列表
     */
    List<Tag> findAll();

    /**
     * 根据ID查询标签
     * @param id 标签ID
     * @return 标签信息
     */
    Tag findById(Long id);

    /**
     * 根据名称查询标签
     * @param name 标签名称
     * @return 标签信息
     */
    Tag findByName(String name);

    /**
     * 新增标签
     * @param tag 标签实体
     * @return 影响行数
     */
    int insert(Tag tag);

    /**
     * 根据片段ID查询关联的标签列表
     * @param snippetId 代码片段ID
     * @return 标签列表
     */
    List<Tag> findBySnippetId(Long snippetId);

    /**
     * 插入片段-标签关联记录
     * @param snippetId 代码片段ID
     * @param tagId     标签ID
     * @return 影响行数
     */
    int insertSnippetTag(@Param("snippetId") Long snippetId, @Param("tagId") Long tagId);

    /**
     * 根据片段ID删除所有关联标签
     * @param snippetId 代码片段ID
     * @return 影响行数
     */
    int deleteBySnippetId(Long snippetId);
}
