package com.bioinfo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.*;

/**
 * @author 刘家雯
 * @Date 2025/5/16
 */

public class MailThreadPool {

    private static final ExecutorService EMAIL_SENDER_POOL = new ThreadPoolExecutor(
            2,
            5,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactory() {
                private int count = 1;
                public Thread newThread(Runnable r) {
                    return new Thread(r, "email-sender-" + count++);
                }
            },
            new ThreadPoolExecutor.AbortPolicy()
    );

    public static ExecutorService getEmailSenderPool() {
        return EMAIL_SENDER_POOL;
    }
}

