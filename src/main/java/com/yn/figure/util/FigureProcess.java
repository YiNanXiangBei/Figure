package com.yn.figure.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.yn.figure.message.CosConfig;
import com.yn.figure.message.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author yinan
 */
@Component
public class FigureProcess {
    @Autowired
    private CosConfig cosConfig;

    private COSClient cosClient;

    @Autowired
    private ClientFactory factory;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void upload(String fileName) {
        try {
            File localFile = new File(cosConfig.getPath() + fileName);
            cosClient = factory.getClient();
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(),
                    fileName, localFile);
            cosClient.putObject(putObjectRequest);
            logger.info("upload image success!");
        } catch (Exception e) {
            logger.error("upload image error: {}", e.getMessage());
        }

    }
}
