package com.nnoob;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.ZooDefs;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * @CreateTime: 2022/5/6 11:31 上午
 * @Author: nnoobnie
 * @Description: CUratorClient
 */
public class CuratorClient {

    private CuratorFramework curatorFramework;


    @Before
    public void initConfig() throws IOException {
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
    }

    @Test
    public void testCreate() throws Exception {

        curatorFramework.start();
        curatorFramework.create().creatingParentsIfNeeded().withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE).forPath("/zk-curat2/zk-curat-sub", "cura".getBytes(StandardCharsets.UTF_8));

    }


    @Test
    public void testGeneNOV1() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat sim = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sim.format(new Date());
                    System.err.println("orderNo:" + orderNo);
                }
            }).start();
        }
        countDownLatch.countDown();
    }

    @Test
    public void testGeneNOV2() throws Exception {
        String lockPath = "/curator_recipes_path";

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



    /*分布式计数器*/


    @Test
    public void testDisAtomicInt() throws Exception {

        String path = "/curator_recipes_dis_path";
        curatorFramework.start();
        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(curatorFramework, path, new RetryNTimes(3, 1000));

        AtomicValue<Integer> add = atomicInteger.add(8);

        System.err.println("Result:" + add.succeeded());
        System.err.println("Result:" + add.postValue());

    }

    static DistributedBarrier barrier;

    /*分布式屏障*/
    @Test
    public void testDisCyclicBarrier() throws Exception {
        final String path = "/curator_recipes_barrier_path";


        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    CuratorFramework build = CuratorFrameworkFactory.builder()
                            .connectString("127.0.0.1:2181")
                            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                            .build();

                    build.start();
                    barrier = new DistributedBarrier(build, path);
                    System.err.println(Thread.currentThread().getName()+"号barrier设置");
                    try {
                        barrier.setBarrier();
                        barrier.waitOnBarrier();
                        System.err.println("启动...");


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        Thread.sleep(2000);

        barrier.removeBarrier();

    }


}
