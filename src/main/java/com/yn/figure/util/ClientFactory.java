package com.yn.figure.util;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.yn.figure.message.CosConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yinan
 */
@Component
public class ClientFactory {

    @Autowired
    private  CosConfig cosConfig;

    private COSClient cosClient;

    private ClientFactory() {

    }

    public COSClient getClient() {
        System.out.println(cosConfig);
        if(cosClient == null) {
            COSCredentials  cred = new BasicCOSCredentials(cosConfig.getAppId(), cosConfig.getAppKey());
            ClientConfig clientConfig = new ClientConfig(new Region(cosConfig.getRegion()));
            cosClient = new COSClient(cred, clientConfig);
        }
        return cosClient;
    }

}
