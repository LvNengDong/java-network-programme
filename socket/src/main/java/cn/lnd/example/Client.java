package cn.lnd.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

/**
 * @Author lnd
 * @Description
 * @Date 2023/8/29 11:18
 */
@Slf4j
public class Client {
    static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    static final int DEFAULT_SERVER_PORT = 8888;
    static final String QUIT = "quit";
    static Socket socket = null;

    public static void main(String[] args) {
        BufferedWriter writer = null;
        try {
            // create & bind & connect
            log.info("客户端：开始启动");
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
            log.info("客户端：启动完成，{}:{}", socket.getInetAddress(), socket.getPort());
            // 创建IO流
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //读取服务器端发送的信息
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//写要发送给服务器端的数据
            // 等待用户输入信息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = consoleReader.readLine();
                // 发送消息给服务器
                writer.write(input + "\n");
                writer.flush();
                log.info("客户端：发送消息，msg:{}", input);
                // 读取服务器返回的消息
                String msg = reader.readLine();
                log.info("客户端：接收到服务器返回的消息，msg:{}", msg);
                // 检查用户是否需要退出
                if (StringUtils.equalsIgnoreCase(QUIT, msg)) {
                    log.info("客户端：用户请求退出，开始退出...");
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != socket) {
                try {
                    /* 为什么不直接关闭Socket而是关闭writer？
                    *   1、关闭writer时会顺带着关闭Socket
                    *   2、关闭writer前会执行一遍flush函数，确保缓冲区中没有数据
                    * */
                    writer.close();
                    log.info("客户端：退出完成，关闭Socket");
                } catch (IOException e) {
                    log.error("关闭Socket异常", e);
                }
            }
        }
    }
}
