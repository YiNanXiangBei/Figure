package com.yn.figure.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yn.figure.message.CosConfig;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author yinan
 */
@Component
public class ProcessUtil {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>(Constant.NUMBER);

    private Set<String> set = new HashSet<>(Constant.NUMBER);

    private ProcessUtil() {

    }

    @Autowired
    private  CosConfig cosConfig;



    /**
     * base64字符串转化成图片
     * @param imgStr
     * @return
     */
    public boolean generateImage(String imgStr, String fileName) {   //对字节数组字符串进行Base64解码并生成图片
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

    public String getImageStr(String imgName) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        File localFile = new File(imgName);

        InputStream in = null;
        byte[] data = null;
        // 读取图片字节数组
        try {
            in = new FileInputStream(imgName);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            logger.error("image to base64 error: {}", e.getMessage());
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        //删除文件
        if (localFile.exists() && localFile.isFile()) {
            localFile.delete();
        }
        // 返回Base64编码过的字节数组字符串
        return data != null ? encoder.encode(data) : null;

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
        logger.info("validate filename: {}", filename);
        if (StringUtils.isEmpty(filename)) {
            return false;
        } else {
            String []strings = filename.split("\\.");
            return strings.length > 1;
        }
    }

    /**
     * 生成一个base64和一个最新的url
     * @param str 最新的url
     * @param base64 图片的base64
     * @return 封装的json格式
     */
    public JSONObject strToJSONObject(String str, String base64) {
        if (set != null && set.size() == Constant.NUMBER) {
            set.remove(str);
            if (set.size() == Constant.NUMBER) {
                String url = blockingQueue.poll();
                set.remove(url);
                set.add(str);
                blockingQueue.offer(str);
            }
        } else if (set != null){
            int size = set.size();
            set.add(str);
            if (size != set.size()) {
                if (blockingQueue.size() == Constant.NUMBER) {
                    blockingQueue.poll();
                }
                blockingQueue.offer(str);
            }
        }
        base64 = base64.replaceAll("[\\r\\n]", "");
        logger.info("get image url: {}", str);
        str = "{ \"" + "url" + "\"" +  ": \"" +  str + "\"" + "," + "\"" + "base64" + "\"" + ": \"" + base64 + "\"" +" }";
        return JSONObject.parseObject(str);
    }

    /**
     * 生成四个最近的url和一个最近的base64
     * @param str 最新图片url
     * @param base64 图片的base64形式
     * @param number 需要的url数量
     * @return 封装的json形式
     */
    public JSONObject objectToJSONObject(String str, String base64, int number) {
        logger.info("begin to generate jsonobject ......");
        base64 = base64.replaceAll("[\\r\\n]", "");
        JSONArray array = new JSONArray();
        JSONObject dataObject = new JSONObject();
        if (blockingQueue != null && blockingQueue.size() == number) {
            blockingQueue.poll();
        }
        if (blockingQueue != null) {
            blockingQueue.offer(str);
            array.addAll(blockingQueue);
            dataObject.put("base64", base64);
            dataObject.put("urls", array);
        }
        return dataObject;
    }

    /**
     * 按照一定格式生成url
     * @param fileName  文件名
     * @return 文件url
     */
    public String generateUrl(String fileName) {
        StringBuilder stringBuffer = new StringBuilder("http://");
        stringBuffer.append(cosConfig.getBucketName()).append(".").
                append(cosConfig.getUrlRegion()).append(".")
                .append("myqcloud.com/").append(fileName);
        return stringBuffer.toString();
    }

    /**
     * 获取最近上传的四张图片
     * @return 最新上传的json格式url
     */
    public JSONObject getUrls() {
        JSONArray array = new JSONArray();
        JSONObject dataObject = new JSONObject();
        array.addAll(blockingQueue);
        dataObject.put("urls", array);
        return dataObject;
    }

    public boolean generateStreamToImage(byte []bytes, String fileName) {
        Path path = Paths.get(cosConfig.getPath() + fileName);
        try {
            Files.write(path, bytes);
            return true;
        } catch (IOException e) {
            logger.error("File transfer error: {}", e.getMessage());
        }
        return false;
    }
}
