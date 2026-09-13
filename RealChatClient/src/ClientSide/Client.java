package ClientSide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args){
        try {
            // 创建Socket对象，指定服务器的IP地址和端口号
            Socket socket = new Socket("127.0.0.1", 12345);

            // 获取输入流和输出流，输入流和输出流通过socket对象来数据传输
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 从控制台读取用户输入
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String message;

            while(true){
                System.out.println("请输入要发送的信息（输入'exit'退出）：");
                message = reader.readLine();

                if (message.equalsIgnoreCase("exit")){
                    // 若用户输入'exit'，发送终止标志给服务器并退出循环
                    out.println("exit");
                    break;
                }

                // 将用户输入的信息发送给服务器
                out.println(message);

                // 接收服务端的响应并打印
                String response = in.readLine();
                System.out.println("服务端响应：" + response);

            }

            // 关闭连接
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
