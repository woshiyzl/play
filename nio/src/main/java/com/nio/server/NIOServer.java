package com.nio.server;

import com.alibaba.fastjson2.JSONObject;
import com.nio.dto.ClientHello;
import com.nio.dto.NIOConst;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class NIOServer {
    private static final Map<String,AsynchronousSocketChannel> clients = new ConcurrentHashMap<>();
    public static void main(String[] args) throws Exception{
        // 创建 CountDownLatch，初始化为 1，主线程会阻塞直到 latch.countDown() 被调用
        CountDownLatch latch = new CountDownLatch(1);

        // 创建异步 ServerSocketChannel
        AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(NIOConst.serverPort));
        System.out.printf("AIO 服务端启动，监听端口 %d",NIOConst.serverPort);
        // 开始接受客户端连接
        serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                try {
                    System.out.println("客户端连接: " + clientChannel.getRemoteAddress());
                    // 处理客户端的读写事件
                    handleClient(clientChannel,new ClientMessageProcessImpl());
                    // 接收下一个连接
                    serverChannel.accept(null, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                System.out.println("客户端连接失败: " + exc.getMessage());
            }
        });
        // 阻塞主线程，直到 latch.countDown() 被调用
        latch.await();
    }
    // 处理客户端的读写逻辑
    private static void handleClient(AsynchronousSocketChannel clientChannel, ProcessMessageFunction processMessage) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 异步读取数据

        StringBuilder sb = new StringBuilder();
        clientChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (result == -1) {
                    // 客户端断开连接
                    try {
                        System.out.println("客户端断开: " + clientChannel.getRemoteAddress());
                        removeClient(clientChannel);
                        clientChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // 读取数据
                attachment.flip();
                String message = new String(attachment.array(), 0, attachment.limit());

                sb.append(message);
                System.out.println("收到客户端消息: " + sb.toString());
                ClientHello clientHello = JSONObject.parseObject(sb.toString(), ClientHello.class);
                processMessage.apply(clientHello, clientChannel);

                clients.put(clientHello.getHostId(), clientChannel); // 保存客户端连接
                // 清空 buffer，继续读取
                attachment.clear();
                clientChannel.read(attachment, attachment, this);
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("读取失败: " + exc.getMessage());
                try {
                    removeClient(clientChannel);
                    clientChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void removeClient(AsynchronousSocketChannel clientChannel) {
        for(Map.Entry<String,AsynchronousSocketChannel> entry:clients.entrySet()){
            if(entry.getValue()==clientChannel){
                clients.remove(entry.getKey());
            }
        }
    }

}
