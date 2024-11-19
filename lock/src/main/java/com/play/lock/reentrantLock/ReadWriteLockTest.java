package com.play.lock.reentrantLock;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockTest {

    /**
     * 在 JVM 中，锁机制通常有两种主要实现方式：
     * synchronized 锁：这种锁是在编译阶段直接插入到字节码中，通过 monitorenter 和 monitorexit 指令控制锁的获取和释放。它依赖于对象头中的 Mark Word 来记录锁状态。
     * 显示锁 (Lock 接口)：如 ReentrantLock 和 ReentrantReadWriteLock，它们是在 Java 类库中实现的，并不是通过字节码指令来插入锁标志，而是使用 AQS 提供的机制来控制并发访问:
     * AQS 利用了 CAS (Compare-And-Swap) 操作和 volatile 变量来维护锁状态state，并使用同步队列管理线程的排队与阻塞。
     *  state: 使用变量的低16位表示写锁的重入次数；高16位表示读锁的持有次数；
     *  AQS 利用了 CAS 操作（通过 Unsafe.compareAndSwapInt 实现）来保证对 state 变量的原子更新，并通过 同步队列 来管理阻塞的线程
     */

    /**
     * 写锁的加锁与阻塞过程
     * A 线程获取写锁
     *
     * 当线程 A 调用 writeLock.lock() 时，它请求对对象 xx 加写锁。如果当前没有其他线程持有写锁或读锁，A 线程将成功获得写锁，并且可以对 xx 执行修改操作。
     * 写锁的独占性
     *
     * 一旦线程 A 获取了写锁，xx 对于其他任何线程都是“锁住的”。这时，其他线程（如线程 B）试图获取写锁或者读锁时，都将被阻塞，直到线程 A 释放写锁。
     * 这是因为 ReentrantReadWriteLock 的实现使用了 排他锁 的机制，写锁是独占的，只有持有写锁的线程可以操作对象 xx，其他线程必须等待写锁释放。
     * 线程 B 试图访问 xx
     *
     * 当线程 B 试图在写锁持有者（线程 A）操作对象 xx 时获取锁（无论是读锁还是写锁），由于线程 A 已经持有写锁，线程 B 会被阻塞。
     * 如果线程 B 试图获取写锁，它将会被阻塞，直到线程 A 释放写锁。
     * 如果线程 B 试图获取读锁，它也会被阻塞，直到写锁释放。
     * @param args
     */


    /**
     * 读读允许，读写互斥，写写互斥，写读互斥
     * 加读锁的主要目的是在多线程环境中保护共享数据的安全性；
     * 保证数据一致性：多线程并发读取共享数据，同时又有线程在修改数据，可能出现脏读或者读写冲突。
     * 提高并发性能：相对与独占锁，读锁是共享锁，多个线程可以同时持有，因此可以提高并发性能。
     */

    public static void main(String[] args) {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Map<String, String> cache = new TreeMap<>();
        cache.put("key1", "1");
        cache.put("key2", "2");
        cache.put("key3", "3");

        Lock rlock = readWriteLock.readLock();
        Lock wlock = readWriteLock.writeLock();       //写锁（排他锁），同一时刻只有一个线程能只有锁；

        Thread readerThread1 = new Thread(new ReadTask(rlock, cache), "readerThread1");
        readerThread1.start();
        Thread readerThread2 = new Thread(new ReadTask(rlock, cache), "readerThread2");
        readerThread2.start();
        Thread writerThread = new Thread(new WriteTask(wlock, cache), "writerThread");
        writerThread.start();
    }

}

class ReadTask implements Runnable {
    private Lock rlock;
    private Map<String, String> cache;

    public ReadTask(Lock readWriteLock, Map<String, String> cache) {
        this.rlock = readWriteLock;
        this.cache = cache;
    }

    @Override
    public void run() {
        readLockTest();
    }

    private void readLockTest() {
        //读锁（共享锁）,多线程可以同时持有，适合读多写少的场景；
        //如有线程持有写锁，读锁会被阻塞；

        for (int index = 0; index < 3; index++) {
            rlock.lock();
            try {
                String valueIndex = "key"+String.valueOf(index + 1);
                System.out.println(Thread.currentThread().getName() + " read key:" + valueIndex + ":" + cache.get(valueIndex));
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            } finally {
                rlock.unlock();
            }
        }
    }
}

class WriteTask implements Runnable {
    private Lock wlock;
    private Map<String, String> cache;

    public WriteTask(Lock readWriteLock, Map<String, String> cache) {
        this.wlock = readWriteLock;
        this.cache = cache;
    }

    @Override
    public void run() {
        writeLockTest();
    }

    private void writeLockTest() {
        //写锁（排他锁），同一时刻只有一个线程能只有锁；
        //如有线程持有写锁，其他线程的读锁和写锁都会被阻塞；
        //保证写操作的原子性，避免脏读。

        for (int index = 0; index < 3; index++) {
            wlock.lock();
            try {
                int newKeyValue = cache.size()+1;
                String newKeyStr = "key"+String.valueOf(newKeyValue);
                cache.put(newKeyStr, String.valueOf(newKeyValue));
                System.out.println(Thread.currentThread().getName() + " write key:" + newKeyStr + ":" + newKeyValue);
                //写操作
            } finally {
                wlock.unlock();
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

}
