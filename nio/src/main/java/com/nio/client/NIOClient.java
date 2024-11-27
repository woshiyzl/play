package com.nio.client;

import com.alibaba.fastjson2.JSONObject;
import com.nio.dto.ClientHello;
import com.nio.dto.NIOConst;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class NIOClient {
    public static void main(String[] args)
    {
        try (Socket socket = new Socket(NIOConst.serverIp, NIOConst.serverPort)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            ClientHello clientHello = new ClientHello();
            clientHello.setName("test");
            clientHello.setData("hello");
            clientHello.setHostId(UUID.randomUUID().toString());
            writer.println(JSONObject.toJSONString(clientHello)); // 发送消息给服务端
            System.out.println("服务端回应: " + reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
