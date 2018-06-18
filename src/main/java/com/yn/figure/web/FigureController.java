package com.yn.figure.web;

import com.yn.figure.message.CosConfig;
import com.yn.figure.message.Request;
import com.yn.figure.util.FigureProcess;
import com.yn.figure.util.ProcessUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yinan
 */
@RestController
public class FigureController {

    @Autowired
    private CosConfig cosConfig;

    @Autowired
    private FigureProcess figureProcess;

    @Autowired
    private ProcessUtil processUtil;

    @PostMapping("/upload")
    public String upload(Request request) {
        processUtil.generateImage(request.getPic(), request.getFileName());
        processUtil.commpressPicForSize(cosConfig.getPath() + request.getFileName(), cosConfig.getPath() + request.getFileName(),
                Long.valueOf(cosConfig.getDesFileSize()), Double.valueOf(cosConfig.getAccuracy()));
        figureProcess.upload(request);
        return "success";
    }

}
