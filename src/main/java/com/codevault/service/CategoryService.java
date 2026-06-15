package com.codevault.service;

import com.codevault.common.result.Result;
import com.codevault.entity.Category;

import java.util.List;

/**
 * 分类业务逻辑接口
 * 定义分类模块的核心业务方法
 */
public interface CategoryService {

    /**
     * 查询所有分类（按排序序号升序）
     * @return 分类列表
     */
    Result findAll();

    /**
     * 创建新分类
     * @param name  分类名称
     * @param icon  分类图标
     * @return 操作结果
     */
    Result create(String name, String icon);

    /**
     * 更新分类信息
     * @param id    分类ID
     * @param name  新的分类名称
     * @param icon  新的分类图标
     * @return 操作结果
     */
    Result update(Long id, String name, String icon);

    /**
     * 删除分类
     * @param id 分类ID
     * @return 操作结果
     */
    Result delete(Long id);
}
