package com.nnoob;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @CreateTime: 2022/5/6 3:53 下午
 * @Author: nnoobnie
 * @Description: 分布式订单号、分布式锁
 */
public class App2 {

    static String lockPath = "/curator_recipes_path";

    static CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181")
            .retryPolicy(new ExponentialBackoffRetry(1000,3))
            .build();

    public static void main(String[] args) {

        curatorFramework.start();

        // curatorFramework.create().forPath("/test","test".getBytes(StandardCharsets.UTF_8));
        final InterProcessMutex lock = new InterProcessMutex(curatorFramework, lockPath);

        final CountDownLatch down = new CountDownLatch(1);
        for (int i = 0; i < 30; i++) {
            Thread thread = new Thread(() -> {


                try {
                    down.await();
                    lock.acquire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                SimpleDateFormat sim = new SimpleDateFormat("HH:mm:ss|SSS");
                String orderNo = sim.format(new Date());
                System.err.println("orderNo:" + orderNo);

                try {
                    lock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        down.countDown();
    }
}
