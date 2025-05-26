package com.bioinfo.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 刘家雯
 * @Date 2025/5/15
 *
 * 配置专门用来分析的线程池
 */
public class AnalysisThreadPool {

    private static final ExecutorService rScriptThreadPool = new ThreadPoolExecutor(
            4, // 核心线程数
            8, // 最大线程数
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100), // 控制最大排队任务数
            new NamedThreadFactory("r-script"),
            new ThreadPoolExecutor.AbortPolicy() // 拒绝策略：默认抛出异常
    );

    public static ExecutorService getRScriptThreadPool() {
        return rScriptThreadPool;
    }

    // 自定义线程命名，便于追踪
    private static class NamedThreadFactory implements ThreadFactory {
        private final String prefix;
        private final AtomicInteger counter = new AtomicInteger(1);

        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, prefix + "-thread-" + counter.getAndIncrement());
            t.setDaemon(false);
            return t;
        }
    }
}
