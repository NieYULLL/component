package com.nnoob;

import org.apache.zookeeper.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @CreateTime: 2022/3/7 10:00 上午
 * @Author: nnoobnie
 * @Description: ZookeeperTest
 */
public class ZookeeperTest {


    private ZooKeeper zooKeeper;


    @Before
    public void initConfig() throws IOException {
        zooKeeper = new ZooKeeper("127.0.0.1:2181", 2000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.err.println(event);
            }
        });

    }


    @Test
    public void testConnect() throws KeeperException, InterruptedException {


        zooKeeper.exists("/dubbo", watchedEvent -> {
            Watcher.Event.KeeperState state = watchedEvent.getState();
            System.err.println(state);

        });
    }

    /*创建节点*/

    @Test
    public void testCreate() {

        String hello = "您好";

        try {
            String s = zooKeeper.create("/zk-book", hello.getBytes(StandardCharsets.ISO_8859_1), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL, null);
            System.err.println(s);

        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCreateAsync() {

        String hello = "您好";
        zooKeeper.create("/zk-book-async", hello.getBytes(StandardCharsets.ISO_8859_1),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, (rc, path, ctx, name) -> System.err.printf("rc:%s,path:%s,ctx:%s,name:%s%n", rc, path, ctx, name), "im ctx");

    }

    @Test
    public void testDelete(){
        try {
            zooKeeper.delete("/zk-book0000000009", 1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }


    }

    @Test
    public void testGetChildren(){
        try {
            List<String> children = zooKeeper.getChildren("/", new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.err.println(event.getType());
                }
            });
            children.forEach(System.err::println);
            testCreate();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }


    }

}
