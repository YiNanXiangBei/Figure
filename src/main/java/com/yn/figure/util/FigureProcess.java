package com.yn.figure.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.yn.figure.message.CosConfig;
import com.yn.figure.message.Request;
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

    public void upload(Request request) {
        try {
            File localFile = new File(cosConfig.getPath() + request.getFileName());
            cosClient = factory.getClient();
            PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucketName(),
                    request.getFileName(), localFile);
            cosClient.putObject(putObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
