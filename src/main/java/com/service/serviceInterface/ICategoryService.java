package com.service.serviceInterface;


import com.common.ServerResponse;
import com.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategory(String categoryName, Integer categoryId);

    ServerResponse getChildrenParallelCategory(Integer categoryId);

     ServerResponse selectCategoryAndChilerenById(Integer categoryId);

    }
