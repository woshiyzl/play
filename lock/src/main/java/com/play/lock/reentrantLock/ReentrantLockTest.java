package com.play.lock.reentrantLock;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    // 可重入锁含义：同一个线程可以多次获取锁，但是必须释放多次；但是不会因为多次获取阻塞；
    // java中，synchronized、ReentrantLock是可重入锁。

    /**
     * 测试可重入锁
     *
     * @param args
     */
    public static void main(String[] args){

        synchronizedLockTest();

        reentrantLockTest();
    }

    /**
     * ReentrantLock 内部维护了一个计数器，用于跟踪锁的重入次数
     * 当线程获取第一次锁时，计数器值为1，同一线程再次获取锁，计数器加1，不会被阻塞；
     * 每次调用unlock()，计数器减1，当计数器为0，锁才被释放；其他线程才有机会获取锁；
     * 使用reentrantLock 必须手动释放；
     * 性能考虑：ReentrantLock 在高并发场景下性能由于synchronized,特别是在大量锁竞争时；
     */
    private static void reentrantLockTest() {
        ReentrantLock lock = new ReentrantLock(); // 默认是非公平锁 NonfairSyn：内部类Sync是包装类，继承与AbstractQueuedSynchronizer(AQS):
                                                  // AQS 提供了可重入、可中断、公平与非公平锁的灵活控制
                                                  // 在非公平锁中，线程不会关心等待队列的顺序，而是直接尝试抢占锁
                                                  // 在公平锁中，线程会按照FIFO的顺序获取锁，公平锁会考虑等待队列中的线程顺序，避免插队现象

        ReentrantLockTest reentrantLockTest = new ReentrantLockTest();
        new Thread(()->reentrantLockTest.methodC(lock)).start();
    }

    public void methodC(ReentrantLock lock){
        lock.lock();    // 第一次获取锁，首次获取锁成功后，将线程状态的值修改为1，这里使用的CAS保证原子性；
                       // 后续再次获取锁时，发现线程已经加锁，只需要对锁进行CAS操作，判断是否为当前线程，是则成功，否则返回false，不会阻塞；

        try{
            System.out.println("begin methodC");
            System.out.println("methodD lock num "+ lock.getHoldCount());
            methodD(lock);  //同一线程再次获取锁
        }finally{
            System.out.println("end methodC");
            System.out.println("methodD lock num "+ lock.getHoldCount());
            lock.unlock();  //释放第一次获取的锁
        }
        System.out.println("methodD lock num "+ lock.getHoldCount());
    }

    public void methodD(ReentrantLock lock){
        lock.lock();     // 第二次获取锁
        try{
            System.out.println("begin methodD");
            System.out.println("methodD lock num "+ lock.getHoldCount());
        }finally{
            System.out.println("end methodD");
            System.out.println("methodD lock num "+ lock.getHoldCount());
            lock.unlock();  //释放第二次获取的锁
        }
        System.out.println("methodD lock num "+ lock.getHoldCount());
    }
    /**
     * 测试synchronized可重入锁
     * 同一个线程中，synchronized 是可重入的，不会被阻塞；
     *  @code
     *    public synchronized void methodA(){
     *     methodB();   //同一线程调用，会再次尝试获取锁；
     *  }
     *  public synchronized void methodB(){
     *     method3();
     *  }
     */
    private static void synchronizedLockTest() {
        ReentrantLockTest reentrantLockTest = new ReentrantLockTest();
        new Thread(() -> reentrantLockTest.methodA()).start();
    }

    public synchronized void methodA (){
        System.out.println("begin methodA");
        methodB();
        System.out.println("end methodA");
    }
    public synchronized void methodB (){
        System.out.println("begin methodB");
        System.out.println("end methodB");
    }
}
