package com.yn.figure.web;

import com.yn.figure.message.CosConfig;
import com.yn.figure.message.Request;
import com.yn.figure.message.Response;
import com.yn.figure.util.Constant;
import com.yn.figure.util.FigureProcess;
import com.yn.figure.util.ProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yinan
 */
@RestController
@RequestMapping("/api")
public class FigureController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CosConfig cosConfig;

    @Autowired
    private FigureProcess figureProcess;

    @Autowired
    private ProcessUtil processUtil;

    @PostMapping("/v1/images")
    public Response upload(Request request) {
        logger.info("Begin to generateImage ......");
        logger.info("request : {}", request);
        String fileName = request.getFileName();
        if (processUtil.validateSuffix(fileName)) {
            if (processUtil.generateImage(request.getPic(), fileName)) {
                logger.info("Begin to compress image ......");
                processUtil.commpressPicForSize(cosConfig.getPath() + fileName, cosConfig.getPath() + fileName,
                        Long.valueOf(cosConfig.getDesFileSize()), Double.valueOf(cosConfig.getAccuracy()));
                logger.info("Begin to upload image ......");
                figureProcess.upload(fileName);
                return new Response(Constant.CREATED, Constant.MSG_CREATED, processUtil.strToJSONObject(processUtil.
                        generateUrl(fileName), processUtil.
                        getImageStr(cosConfig.getPath() + fileName)));
            }
        }
        return new Response(Constant.BAD_REQUEST, Constant.MSG_BAD_REQUEST, null);
    }

    @GetMapping("/v1/urls")
    public Response getUrls() {
        return new Response(Constant.SUCCESS, Constant.MSG_SUCCESS, processUtil.getUrls());
    }

    @PostMapping("/v2/images")
    public Response uploadFiles(@RequestParam("file") MultipartFile file) {
        logger.info("[v2] Begin to generateImage ......");
        logger.info("[v2] File size: {}", file.getSize());
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            if (processUtil.validateSuffix(fileName)) {
                try {
                    if (processUtil.generateStreamToImage(file.getBytes(), fileName)) {
                        logger.info("[v2] Begin to compress image ......");
                        processUtil.commpressPicForSize(cosConfig.getPath() + fileName, cosConfig.getPath() + fileName,
                                Long.valueOf(cosConfig.getDesFileSize()), Double.valueOf(cosConfig.getAccuracy()));
                        logger.info("[v2] Begin to upload image ......");
                        figureProcess.upload(fileName);
                        return new Response(Constant.CREATED, Constant.MSG_CREATED, processUtil.strToJSONObject(processUtil.
                                generateUrl(fileName), processUtil.
                                getImageStr(cosConfig.getPath() + fileName)));
                    }
                } catch (IOException e) {
                    logger.error("[v2] upload error: {}", e.getMessage());
                }
            }
        }
        return new Response(Constant.SUCCESS, Constant.MSG_SUCCESS, null);
    }

}
