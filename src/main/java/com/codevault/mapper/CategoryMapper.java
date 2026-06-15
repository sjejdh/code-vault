package com.codevault.mapper;

import com.codevault.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类数据访问层接口
 * 提供分类的增删改查基本操作
 */
@Mapper
public interface CategoryMapper {

    /**
     * 查询所有分类
     * @return 分类列表
     */
    List<Category> findAll();

    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类信息
     */
    Category findById(Long id);

    /**
     * 新增分类
     * @param category 分类实体
     * @return 影响行数
     */
    int insert(Category category);

    /**
     * 更新分类信息
     * @param category 分类实体
     * @return 影响行数
     */
    int update(Category category);

    /**
     * 根据ID删除分类
     * @param id 分类ID
     * @return 影响行数
     */
    int deleteById(Long id);
}
