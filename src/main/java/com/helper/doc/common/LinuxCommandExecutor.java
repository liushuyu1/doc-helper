package com.helper.doc.common;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author liushuyu
 * @Date 2021/12/9 11:46
 * @Version
 * @Desc linux命令执行器
 */

@Slf4j
public class LinuxCommandExecutor {


    public static void main(String[] args) {
        execute("ipconfig");
    }

    /**
     * @param command linux命令
     * @return 执行结果，0是成功，否则失败
     */
    public static LinuxCommandResult execute(String command) {
        LinuxCommandResult result = new LinuxCommandResult();
        List<String> message = result.getMessage();
        List<String> errorMessage = result.getErrorMessage();
        int resultCode = 1;
        Process shellProcess = null;
        BufferedReader errorResultReader = null;
        BufferedReader resultReader = null;
        try {
            shellProcess = Runtime.getRuntime().exec(command);
            errorResultReader = new BufferedReader(new InputStreamReader(shellProcess.getErrorStream(),"UTF-8"));
            resultReader = new BufferedReader(new InputStreamReader(shellProcess.getInputStream(),"UTF-8"));
            String infoLine;
            while ((infoLine = resultReader.readLine()) != null) {
                message.add(infoLine);
            }
            String errorLine;
            while ((errorLine = errorResultReader.readLine()) != null) {
                errorMessage.add(errorLine);
            }
            // 等待程序执行结束并输出状态
            int exitCode = shellProcess.waitFor();
            if (0 == exitCode) {
                resultCode = 0;
            } else {
                resultCode = exitCode;
            }
        } catch (Exception e) {
            log.error("脚本：{}，\n执行失败：{}", command, e.getMessage());
        } finally {
            if (null != resultReader) {
                try {
                    resultReader.close();
                } catch (IOException e) {
                    log.error("流文件关闭异常：", e);
                }
            }
            if (null != errorResultReader) {
                try {
                    errorResultReader.close();
                } catch (IOException e) {
                    log.error("流文件关闭异常：", e);
                }
            }
            if (null != shellProcess) {
                shellProcess.destroy();
            }
        }
        result.setResultCode(resultCode);
        log.info("脚本：{}，\n执行结果：{}",command,result);
        return result;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LinuxCommandResult {
        private int resultCode;//0是成功，其他码是失败
        private List<String> message = new ArrayList<>();
        private List<String> errorMessage = new ArrayList<>();

        public boolean isSuccess() {
            return resultCode == 0;
        }
    }
}
