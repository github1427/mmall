package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @ Author     ：vain
 * @ Date       ：Created in 下午2:25 2018/6/20
 * @ Description：文件服务功能实现
 */

@Service("iFileService")
public class FileServiceImpl implements IFileService{

    Logger logger= LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile multipartFile,String path){
        String fileName=multipartFile.getOriginalFilename();
        String fileExtension=fileName.substring(fileName.lastIndexOf("."));
        String uploadFileName= UUID.randomUUID()+fileExtension;
        logger.info("开始上传文件，文件名{},上传路径{},新文件名{}",fileName,path,uploadFileName);
        File fileDir =new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile=new File(path,uploadFileName);
        try {
            multipartFile.transferTo(targetFile);//文件已经上传成功，在webapp下
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));//文件上传到FTP服务器
            targetFile.delete();
        } catch (IOException e) {
            logger.error("文件上传异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
