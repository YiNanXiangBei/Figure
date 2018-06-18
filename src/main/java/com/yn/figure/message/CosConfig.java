package com.yn.figure.message;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author yinan
 */
@Component
@PropertySource("classpath:config/cos-config.properties")
@ConfigurationProperties(prefix = "cos")
public class CosConfig {

    private String appId;

    private String appKey;

    private String bucketName;

    private String region;

    private String path;

    private String desFileSize;

    private String accuracy;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDesFileSize() {
        return desFileSize;
    }

    public void setDesFileSize(String desFileSize) {
        this.desFileSize = desFileSize;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
