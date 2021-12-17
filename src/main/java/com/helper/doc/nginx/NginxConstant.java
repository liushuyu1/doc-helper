package com.helper.doc.nginx;

/**
 * @Author liushuyu
 * @Date 2021/12/9 20:04
 * @Version
 * @Desc
 */
public class NginxConstant {

    static final String NGINX_CONF_SLINK_DIR = "/opt/doc/nginx-conf/";
    static final String NGINX_CONF_SLINK_SUFFIX = ".txt";
    static final String NGINX_CONF_SLINK_INDEX = "nginxConfIndex.json";


    static final String CMD_NGINX_T = "nginx -t";
    static final String CMD_NGINX_MKDIR = "mkdir -p " + NGINX_CONF_SLINK_DIR;
    static final String CMD_NGINX_LN_S = "ln -s {filePath} " + NGINX_CONF_SLINK_DIR + "{fileName}" + NGINX_CONF_SLINK_SUFFIX;
}



//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/mime.types.txt">mime.types</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/poss-juweb-api.conf.txt">poss-juweb-api.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/aton-config-api.conf.txt">aton-config-api.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/aton-admin-api-download-nginx.conf.txt">aton-admin-api-download-nginx.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/aton-admin-api-nginx.conf.txt">aton-admin-api-nginx.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/token-juweb-admin-nginx.conf.txt">token-juweb-admin-nginx.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/poss-api-admin.conf.txt">poss-api-admin.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/poss-api-admin-download.conf.txt">poss-api-admin-download.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/aton-admin-juweb-nginx.conf.txt">aton-admin-juweb-nginx.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/doc-nginx.conf.txt">doc-nginx.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/platon-aton-api.conf.txt">platon-aton-api.conf</a></td></tr>
//<tr><td><a href="http://192.168.9.190:16666/nginx-conf/alaya-aton-api.conf.txt">alaya-aton-api.conf</a></td></tr>
