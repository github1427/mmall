package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午3:38 2018/6/19
 * @ Description：
 */

@Controller
@RequestMapping("/manage/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("/save")
    @ResponseBody
    public ServerResponse save(HttpServletRequest httpServletRequest, Product product) {
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.productInsertOrUpdate(product);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/set_sale_status")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest httpServletRequest, Integer productId, Integer status) {
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/detail")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getProductDetail(HttpServletRequest httpServletRequest, Integer productId) {
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/list")
    @ResponseBody
    public ServerResponse<PageInfo> getList(HttpServletRequest httpServletRequest,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping("/search")
    @ResponseBody
    public ServerResponse<PageInfo> productSearch(HttpServletRequest httpServletRequest,String productName,Integer productId,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.productSearch(productName,productId,pageNum,pageSize);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<Map<String,String>> fileUpload(HttpServletRequest httpServletRequest, @RequestParam(value ="upload_file",required = false)MultipartFile file, HttpServletRequest request) {
        String loginToken= CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆，无法获取当前用户信息");
        }
        String loginUserJson= RedisPoolUtil.get(loginToken);
        User user= JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请登录管理员");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            if (StringUtils.isBlank(targetFileName)){
                return ServerResponse.createByErrorMessage("上传失败");
            }
            Map<String,String> map= Maps.newHashMap();
            map.put("uri",targetFileName);
            map.put("url",url);
            return ServerResponse.createBySuccess(map);
        } else {
            return ServerResponse.createByErrorMessage("用户无权限");
        }

    }

    @RequestMapping(value = "/richtext_img_upload",method = RequestMethod.POST)
    @ResponseBody
    public Map richtextUpload(HttpServletRequest httpServletRequest, @RequestParam(value ="upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map map=Maps.newHashMap();
        String loginToken=CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)){
            map.put("success",false);
            map.put("msg","用户未登陆，请登录管理员");
            return map;
        }
        String loginUserJson=RedisPoolUtil.get(loginToken);
        User user=JsonUtil.stringToObj(loginUserJson,User.class);
        if (user == null) {
            map.put("success",false);
            map.put("msg","用户未登陆，请登录管理员");
            return map;
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url= PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            if (StringUtils.isBlank(targetFileName)){
                map.put("success",false);
                map.put("msg","上传失败");
                return map;
            }
            map.put("success",false);
            map.put("msg","上传成功");
            map.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return map;
        } else {
            map.put("success",false);
            map.put("msg","用户无权限");
            return map;
        }

    }

}
