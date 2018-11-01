package com.controller.backend;

import com.common.Const;
import com.common.ServerResponse;
import com.dao.CategoryMapper;
import com.pojo.User;
import com.service.serviceInterface.ICategoryService;
import com.service.serviceInterface.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    @ResponseBody
    @RequestMapping("/add_category.do")
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        //1、先检查用户是否存在
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录，请先登录");
        }
        //2、校验用户是否是管理源,并数据插入数据库中
        if (iUserService.isAdmin(user)){
            //是管理员
            return iCategoryService.addCategory(categoryName,parentId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @ResponseBody
    @RequestMapping("/set_category.do")
    public ServerResponse setCategoryName(HttpSession session, String categoryName,  Integer categoryId){
        //1、先检查用户是否存在
        User user=(User) session.getAttribute(Const.CURRENT_USER);
        if (user==null){
            return ServerResponse.createByErrorMessage("用户未登录，请先登录");
        }
        if (iUserService.isAdmin(user)){
            //是管理员
            return iCategoryService.updateCategory(categoryName,categoryId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @ResponseBody
    @RequestMapping("/get_category.do")
    public ServerResponse getChildrenParallelCategory(HttpServletRequest request, Integer categoryId) {
        //1、先检查用户是否存在
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，请先登录");
        }
        if (iUserService.isAdmin(user)) {
            //是管理员
            return iCategoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    public ServerResponse getCategoryAndDeepChildRenCategory(HttpServletRequest request,@RequestParam(value = "categoryId" ,defaultValue = "0") Integer categoryId){
        //1、先检查用户是否存在
        User user = iUserService.getUserformRedis(request);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，请先登录");
        }
        if (iUserService.isAdmin(user)){
            //0->100->1000...
            return iCategoryService.selectCategoryAndChilerenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

}
