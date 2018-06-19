package com.yn.figure.util;

import com.alibaba.fastjson.JSONObject;
import com.yn.figure.message.CosConfig;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;

/**
 * @author yinan
 */
@Component
public class ProcessUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ProcessUtil() {

    }

    @Autowired
    private  CosConfig cosConfig;



    /**
     * base64字符串转化成图片
     * @param imgStr
     * @return
     */
    public boolean generateImage(String imgStr, String fileName)
    {   //对字节数组字符串进行Base64解码并生成图片
        //图像数据为空
        logger.info("Generate image ......");
        if (imgStr == null) {
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //Base64解码
            logger.info("decode the byte ......");
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            String imgFilePath = cosConfig.getPath() + fileName;
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();
            logger.info("Generate image finished ......");
            return true;
        } catch (Exception e) {
            logger.error("generate image error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 根据指定大小和指定精度压缩图片
     *
     * @param srcPath
     *            源图片地址
     * @param desPath
     *            目标图片地址
     * @param desFileSize
     *            指定图片大小，单位kb
     * @param accuracy
     *            精度，递归压缩的比率，建议小于0.9
     * @return
     */
    public String commpressPicForSize(String srcPath, String desPath,
                                             long desFileSize, double accuracy) {
        if (StringUtils.isEmpty(srcPath) || StringUtils.isEmpty(srcPath)) {
            return null;
        }
        if (!new File(srcPath).exists()) {
            return null;
        }
        try {
            File srcFile = new File(srcPath);
            long srcFileSize = srcFile.length();
            System.out.println("源图片：" + srcPath + "，大小：" + srcFileSize / 1024
                    + "kb");

            // 1、先转换成jpg
            logger.info("read the image ......");
            Thumbnails.of(srcPath).scale(1f).toFile(desPath);
            // 递归压缩，直到目标文件大小小于desFileSize
            logger.info("compress image recursive ......");
            commpressPicCycle(desPath, desFileSize, accuracy);

            File desFile = new File(desPath);
            logger.info("目标图片：" + desPath + "，大小" + desFile.length()
                    / 1024 + "kb");
        } catch (Exception e) {
            logger.error("compress image error: {}", e.getMessage());
            return null;
        }
        return desPath;
    }
    /**
     * 图片压缩:按指定大小把图片进行缩放（会遵循原图高宽比例）
     * 并设置图片文件大小
     */
    private void commpressPicCycle(String desPath, long desFileSize,
                                          double accuracy) throws IOException {
        File srcFileJPG = new File(desPath);
        long srcFileSizeJPG = srcFileJPG.length();
        // 2、判断大小，如果小于指定大小，不压缩；如果大于等于指定大小，压缩
        if (srcFileSizeJPG <= desFileSize * 1024) {
            return;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(srcFileJPG);
        int srcWdith = bim.getWidth();
        int srcHeigth = bim.getHeight();
        int desWidth = new BigDecimal(srcWdith).multiply(
                new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(srcHeigth).multiply(
                new BigDecimal(accuracy)).intValue();

        Thumbnails.of(desPath).size(desWidth, desHeight)
                .outputQuality(accuracy).toFile(desPath);
        commpressPicCycle(desPath, desFileSize, accuracy);
    }

    public boolean validateSuffix(String filename) {
        if (StringUtils.isEmpty(filename)) {
            return false;
        } else {
            String []strings = filename.split("\\.");
            return strings.length > 1;
        }
    }

    public JSONObject strToJSONObject(String str) {

        str = "{ \"" + "url" + "\"" +  ": \"" +  str + "\" }";
        logger.info("get image url: {}", str);
        return JSONObject.parseObject(str);
    }

    public String generateUrl(String fileName) {
        StringBuilder stringBuffer = new StringBuilder("http://");
        stringBuffer.append(cosConfig.getBucketName()).append(".").
                append(cosConfig.getUrlRegion()).append(".")
                .append("myqcloud.com/").append(fileName);
        return stringBuffer.toString();
    }

}
