package com.helper.doc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author liushuyu
 * @Date 2021/12/9 10:56
 * @Version
 * @Desc
 */


@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.helper.doc")
public class DocHelperApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocHelperApplication.class,args);
    }
}
