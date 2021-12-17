package com.helper.doc.nginx;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.helper.doc.common.LinuxCommandExecutor;
import com.helper.doc.os.OSinfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.helper.doc.nginx.NginxConstant.*;

/**
 * @Author liushuyu
 * @Date 2021/12/9 11:24
 * @Version
 * @Desc
 */

@Slf4j
@Component
public class LinuxNginxTask {


    /**
     * NGINX_CONF_SLINK_DIR {@link com.helper.doc.nginx.NginxConstant#NGINX_CONF_SLINK_DIR}
     * 1.收集nginx配置文件，然后将nginx配置文件的软连接创建到 NGINX_CONF_SLINK_DIR中
     */
    @Scheduled(fixedDelay = 10000)
    public void createNginxConfSoftLink() {
        if (!OSinfo.isLinux()) {
            return;
        }
        //1.收集nginx相关的配置文件
        List<File> fileList = collect();
        //2.先创建目录/opt/doc/nginx-conf/
        if (!FileUtil.exist(NGINX_CONF_SLINK_DIR)) {
            LinuxCommandExecutor.execute(CMD_NGINX_MKDIR);
        }
        //3.在/opt/doc/nginx-conf/目录下创建nginx配置文件的软连接
        fileList.forEach(file -> {
            String filePath = FileUtil.getAbsolutePath(file);
            String fileName = FileUtil.getName(file);
            String cmd = CMD_NGINX_LN_S.replace("{filePath}", filePath).replace("{fileName}", fileName);
            //创建软连接
            LinuxCommandExecutor.execute(cmd);
        });
    }


    /**
     * NGINX_CONF_SLINK_DIR {@link com.helper.doc.nginx.NginxConstant#NGINX_CONF_SLINK_DIR}
     * 2.将nginx的软连接读取到一个文件中
     */
    @Scheduled(fixedDelay = 10000)
    public void recordNginxConfSoftLink() {
        if (!OSinfo.isLinux()) {
            return;
        }
        //1.先创建目录/opt/doc/nginx-conf/
        if (!FileUtil.exist(NGINX_CONF_SLINK_DIR)) {
            LinuxCommandExecutor.execute(CMD_NGINX_MKDIR);
        }
        //2.给/opt/doc/nginx-conf/目录下的软连接创建索引文件/opt/doc/nginx-conf/nginxConfIndex
        File nginxConfIndex = new File(NGINX_CONF_SLINK_DIR + NGINX_CONF_SLINK_INDEX);
        if (!FileUtil.exist(nginxConfIndex)) {
            try {
                nginxConfIndex.createNewFile();
            } catch (IOException e) {
                log.error("创建文件失败：" + nginxConfIndex.getName(), e);
                return;
            }
        }
        //3.搜集/opt/doc/nginx-conf/目录下所有nginx配置文件软连接
        List<File> nginxConfSoftLinkFiles = FileUtil.loopFiles(NGINX_CONF_SLINK_DIR, file -> FileUtil.getName(file).endsWith(NGINX_CONF_SLINK_SUFFIX));
        List<String> nginxConfSoftLinkFileNames = nginxConfSoftLinkFiles.stream().map(File::getName).collect(Collectors.toList());
        //4.将/opt/doc/nginx-conf/目录下软连接名称写入到nginxConfIndex索引文件中
        FileUtil.writeUtf8Lines(nginxConfSoftLinkFileNames, nginxConfIndex);
    }

    public static void main(String[] args) {
        String line = "    include ../conf.d/*.conf;";
        String nginxConf = StrUtil.trim(line, 0);
        System.out.println(nginxConf);
    }

    /**
     * nginx -t
     * nginx: the configuration file /www/nginx-1.12.0/conf/nginx.conf syntax is ok
     * nginx: configuration file /www/nginx-1.12.0/conf/nginx.conf test is successful
     * 收集nginx相关的配置文件
     */
    private List<File> collect() {
        //获取nginx配置文件路劲
        String nginxConfPath = getNginxConf();
        //TODO 解析nginx配置文件
        File nginxConfFile = new File(nginxConfPath);
        List<String> includeFilePath = getIncludeFilePath(nginxConfFile);
        //配置文件列表
        List<File> confFileList = new ArrayList<>();
        confFileList.add(nginxConfFile);
        includeFilePath.forEach(path -> {
            String name = FileUtil.getName(path);
            List<File> confFiles = new ArrayList<>();
            //表示通配符,则遍历该目录
            if (name.startsWith("*")) {
                String dir = path.substring(0, path.lastIndexOf(name));
                String suffix = FileUtil.getSuffix(name);
                confFiles = FileUtil.loopFiles(new File(dir), (file) -> FileUtil.getSuffix(file.getName()).equalsIgnoreCase(suffix));
            } else {
                confFiles = FileUtil.loopFiles(new File(path), (file) -> name.equals(file.getName()));
            }
            confFileList.addAll(confFiles);
        });
        return confFileList;
    }

    private List<String> getIncludeFilePath(File nginxConfFile) {
        String nginxConfDir = nginxConfFile.getParent();
        log.info("nginxConf目录：" + nginxConfDir);
        String nginxConfParentDir = nginxConfFile.getParentFile().getParent();
        log.info("nginxConf上级目录：" + nginxConfParentDir);
        List<String> lines = FileUtil.readUtf8Lines(nginxConfFile);
        log.debug("nginx配置文件：" + lines);
        List<String> includeLine = lines.stream()
                .map(line -> StrUtil.trim(line, 0))//去除配置中的前后空白
                .filter(line -> line.startsWith("include"))//过滤出include配置
                .map(line -> StrUtil.removeSuffix(StrUtil.removePrefix(line, "include"), ";"))//去除include配置的前缀和后缀
                .map(line -> StrUtil.trim(line, 0))//去除配置中的前后空白
                .map(includePath -> {
                    if (includePath.startsWith("/")) {
                        return includePath;
                    }
                    if (includePath.startsWith("..")) {
                        String s = StrUtil.removePrefix(includePath, "..");
                        return nginxConfParentDir + s;
                    }
                    if (includePath.startsWith(".")) {//同一个目录
                        String s = StrUtil.removePrefix(includePath, ".");
                        return nginxConfDir + s;
                    }
                    return nginxConfDir + "/" + includePath;
                })
                .collect(Collectors.toList());
        log.info("nginx配置文件中includeLine：" + includeLine);
        return includeLine;
    }

    private String getNginxConf() {
        LinuxCommandExecutor.LinuxCommandResult result = LinuxCommandExecutor.execute(CMD_NGINX_T);
        if (result.isSuccess() && !result.getErrorMessage().isEmpty()) {
            String message = result.getErrorMessage().get(1);
            String startStr = "nginx: configuration file ";
            String endStr = " test is successful";
            message = StrUtil.trim(message, 0);
            String nginxConf = message.replace(startStr, "").replace(endStr, "");
            return nginxConf;
        } else {
            throw new RuntimeException("获取nginx配置文件失败");
        }
    }

}
