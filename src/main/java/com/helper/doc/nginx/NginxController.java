package com.helper.doc.nginx;

import cn.hutool.core.io.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.helper.doc.nginx.NginxConstant.*;

/**
 * @Author liushuyu
 * @Date 2021/12/9 11:19
 * @Version
 * @Desc
 */


@Controller("/opt/doc/nginx")
public class NginxController {


    /**
     * 展示nginx配置文件列表
     * @return
     */
    public List<String> list() {
        return null;
    }

    /**
     * 展示指定的conf文件
     */
    public void detail(){

    }

    /**
     * 合并所有配置文件并展示
     */

}
