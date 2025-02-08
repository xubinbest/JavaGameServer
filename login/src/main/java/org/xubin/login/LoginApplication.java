package org.xubin.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;
import org.xubin.login.base.GameContext;

@SpringBootApplication
@Slf4j
public class LoginApplication {

    public static void main(String[] args) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        SpringApplication.run(LoginApplication.class, args);
        GameContext.getBean(BaseServer.class).start();

        stopWatch.stop();
        log.info("登录服启动成功，耗时[{}]毫秒", stopWatch.getTotalTimeMillis());
    }

}
