package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 上午9:26 2018/6/21
 * @ Description：前台产品处理器
 */

@Controller
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IUserService iUserService;

    @RequestMapping("/detail")
    @ResponseBody
    public ServerResponse<ProductDetailVo> productDetail(HttpSession session, Integer productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.portalProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }
    }

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<PageInfo> productList(HttpSession session,
                                                @RequestParam(value = "categoryId",required = false) Integer categoryId,
                                                @RequestParam(value = "keyword",required = false) String keyword,
                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                @RequestParam(value = "orderBy",defaultValue = "")String orderBy){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.portalProductList(categoryId,keyword,pageNum,pageSize,orderBy);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }
    }
}
