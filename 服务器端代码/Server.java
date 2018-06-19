package fun;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	Socket socket;
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(8080);// 开启80端口
			System.out.println("服务器启动>>> 8080端口");
			while (true) {// 开启一个永远的循环，一直等待客户访问80端口
				socket = serverSocket.accept();

				if (socket != null) {
					Thread thread = new Thread(new Handler(socket));// 当有客户访问时，开辟一个新线程处理该请求
					thread.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
