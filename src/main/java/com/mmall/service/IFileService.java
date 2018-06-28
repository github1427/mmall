package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午2:24 2018/6/20
 * @ Description：文件服务接口
 */
public interface IFileService {
    String upload(MultipartFile multipartFile, String path);
}
