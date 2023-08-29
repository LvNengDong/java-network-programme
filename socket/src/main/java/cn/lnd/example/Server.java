package cn.lnd.example;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author lnd
 * @Description
 * @Date 2023/8/29 11:18
 */
@Slf4j
public class Server {
    static final int DEFAULT_PORT = 8888;
    static final String QUIT = "quit";
    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            // bind
            serverSocket = new ServerSocket(DEFAULT_PORT);
            log.info("服务器：启动成功，{}:{}", serverSocket.getInetAddress(), serverSocket.getLocalPort());

            while (true) {
                // accept
                // 1、阻塞式调用  2、当有客户端请求连接时，返回一个socket对象，作为服务器与客户端进行数据交换的端点
                // 等待客户端连接
                Socket socket = serverSocket.accept();
                log.info("服务器：获取客户端Socket成功，{}:{}", socket.getInetAddress(), socket.getLocalPort());

                // I/O：处理输入输出流
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //取出客户端端点的数据流
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 向客户端端点写的数据流

                // 读取客户端发送的消息
                String msg = null;
                while ((msg = reader.readLine()) != null) { //如果在读取数据期间客户端Socket关闭了，那么服务器读到的数据就是null
                    log.info("服务器：获取客户端请求成功，{}:{}，msg:{}", socket.getInetAddress(), socket.getLocalPort(), msg);
                    // 回复客户端消息
                    writer.write("服务器：" + msg + "\n");
                    writer.flush();

                    // 检查客户端是否退出
                    if (StringUtils.equalsIgnoreCase(QUIT, msg)) {
                        log.info("服务器：客户端请求断开连接，{}:{}", socket.getInetAddress(), socket.getPort());
                        break;
                        /* 实际上，即便不在这里手动退出循环，关闭ServerSocket，如果客户端主动断开了Socket，在while循环进行到下一个循环的时候，
                        * 也会由于 reader.readLine() 读不到任何内容而导致跳出循环，关闭ServerSocket */
                    }
                }
            }
        } catch (Exception e) {
            log.error("服务端异常", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    log.info("服务器：关闭ServerSocket");
                } catch (IOException e) {
                    log.error("服务器关闭异常", e);
                }
            }
        }
    }
}
