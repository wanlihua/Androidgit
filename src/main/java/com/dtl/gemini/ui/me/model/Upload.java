package com.dtl.gemini.ui.me.model;

import lombok.Data;

/**
 * @author DTL
 * @date 2020/4/28
 **/
@Data
public class Upload {
    /**
     * name : file
     * url : http://127.0.0.1:10300/user/user_file/download/user/id_card/196738c0-3787-43c4-91ae-ffce2d6c6a78.jpg
     * fileResourceId : null
     * originalName : head.jpg
     * fileType : null
     * fileName : 196738c0-3787-43c4-91ae-ffce2d6c6a78.jpg
     * fileMd5 : e6f90dca7de9ad30a10ec952f93d253f
     */

    private String name;
    private String url;
    private Object fileResourceId;
    private String originalName;
    private Object fileType;
    private String fileName;
    private String fileMd5;
}
