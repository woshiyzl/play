package com.nio.server;

import com.nio.dto.ClientHello;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ClientMessageProcessImpl implements ProcessMessageFunction<ClientHello,AsynchronousSocketChannel,String>{

    @Override
    public String apply(ClientHello clientHello, AsynchronousSocketChannel channel) {
        response2ClientMessage(channel, clientHello.getData());
        return null;
    }

    // 广播消息给所有客户端
    private static void response2ClientMessage(AsynchronousSocketChannel sender, String message) {
        ByteBuffer buffer = ByteBuffer.wrap((message + System.lineSeparator()).getBytes());
        sender.write(buffer.duplicate(), buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining()) {
                    // 如果没有写完，继续写入剩余数据
                    sender.write(attachment, attachment, this);
                } else {
                    System.out.println("响应客户端消息完成: " + message);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                System.out.println("广播消息失败: " + exc.getMessage());
            }
        });

    }
}
