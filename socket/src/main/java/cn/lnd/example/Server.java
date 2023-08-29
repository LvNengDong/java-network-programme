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

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        try {
            // bind
            serverSocket = new ServerSocket(DEFAULT_PORT);
            log.info("启动服务器，监听端口：" + DEFAULT_PORT);

            while (true) {
                // accept
                // 1、阻塞式调用  2、当有客户端请求连接时，返回一个socket对象，作为服务器与客户端进行数据交换的端点
                // 等待客户端连接
                Socket socket = serverSocket.accept();
                log.info("客户端[" + socket.getInetAddress() + ":" + socket.getPort() + "]连接成功");

                // I/O：处理输入输出流
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //取出客户端端点的数据流
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // 向客户端端点写的数据流

                // 读取客户端发送的消息
                String msg = reader.readLine();
                if (StringUtils.isNotEmpty(msg)) { //如果在读取数据期间客户端Socket关闭了，那么服务器读到的数据就是null
                    log.info("客户端[" + socket.getInetAddress() + ":" + socket.getPort() + "]>>" + msg);
                    // 回复客户端消息
                    writer.write("服务器：" + msg + "\n");
                    writer.flush();
                }
            }
        } catch (Exception e) {
            log.error("服务端异常", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    log.error("服务器关闭异常", e);
                }
            }
        }
    }
}
