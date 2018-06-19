package com.yn.figure.message;

import java.io.Serializable;

/**
 * @author yinan
 */
public class Request implements Serializable{

    private static final long serialVersionUID = 461450873709294807L;

    private String pic;

    private String fileName;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
