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
        if (processUtil.validateSuffix(request.getFileName())) {
            if (processUtil.generateImage(request.getPic(), request.getFileName())) {
                logger.info("Begin to compress image ......");
                processUtil.commpressPicForSize(cosConfig.getPath() + request.getFileName(), cosConfig.getPath() + request.getFileName(),
                        Long.valueOf(cosConfig.getDesFileSize()), Double.valueOf(cosConfig.getAccuracy()));
                logger.info("Begin to upload image ......");
                figureProcess.upload(request);
                return new Response(Constant.CREATED, Constant.MSG_CREATED, processUtil.strToJSONObject(processUtil.
                        generateUrl(request.getFileName()), processUtil.
                        getImageStr(cosConfig.getPath() + request.getFileName())));
            }
        }
        return new Response(Constant.BAD_REQUEST, Constant.MSG_BAD_REQUEST, null);
    }

    @GetMapping("/v1/urls")
    public Response getUrls() {
        return new Response(Constant.SUCCESS, Constant.MSG_SUCCESS, processUtil.getUrls());
    }

}
