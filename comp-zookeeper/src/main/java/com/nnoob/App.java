package com.nnoob;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * Hello world!
 *
 */
public class App 
{

    static final String path = "/curator_recipes_barrier_path";
    static  DistributedBarrier barrier ;

    public static void main( String[] args ) throws Exception {



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
