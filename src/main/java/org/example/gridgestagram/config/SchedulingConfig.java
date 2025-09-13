package org.example.gridgestagram.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulingConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();

        log.info("TaskScheduler 설정 완료 - 풀 크기: {}", scheduler.getPoolSize());
        return scheduler;
    }
    
    @PostConstruct
    public void logSchedulingStatus() {
        log.info("=== 스케줄링 설정 완료 ===");
        log.info("@EnableScheduling이 활성화되었습니다.");
        log.info("스케줄러가 곧 시작됩니다.");
    }
}
