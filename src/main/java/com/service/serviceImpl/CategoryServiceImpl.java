package com.service.serviceImpl;

import com.common.ServerResponse;
import com.dao.CategoryMapper;
import com.pojo.Category;
import com.pojo.CategoryExample;
import com.service.serviceInterface.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class CategoryServiceImpl implements ICategoryService {

//    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类错误");
        }
        Category category = new Category();
        //只添加这几项
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int count = categoryMapper.insertSelective(category);
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }

        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    @Override
    public ServerResponse updateCategory(String categoryName, Integer categoryId) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类错误");
        }
        Category category = new Category();
        //只添加这几项
        category.setName(categoryName);
        category.setId(categoryId);
        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("更新品类成功");
        }

        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    //查询一个节点下的所有子节点（广度优先）
    @Override
    public ServerResponse getChildrenParallelCategory(Integer categoryId) {
        if (categoryId == null) {
            return ServerResponse.createByErrorMessage("添加品类错误");
        }
        CategoryExample example = new CategoryExample();
        //查询当前分类的所有子分类
        example.createCriteria().andParentIdEqualTo(categoryId);
        List<Category> categoryList = categoryMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(categoryList)) {
            log.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse selectCategoryAndChilerenById(Integer categoryId) {
        List<Category> categorySet = new ArrayList<>();
        findChildCategory(categorySet, categoryId);

        List<Integer> list = new ArrayList<>();
        for (Category category : categorySet) {
            list.add(category.getId());
        }
        return ServerResponse.createBySuccess(list);

    }

    //查找一个节点下的子节点与子节点的子节点(深度优先)
    public List<Category> findChildCategory(List<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null && !categorySet.contains(category)) {
            categorySet.add(category);
        }

        //递归查找子节点，
        CategoryExample example = new CategoryExample();
        example.createCriteria().andParentIdEqualTo(categoryId);
        List<Category> categoryList = categoryMapper.selectByExample(example);
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
