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
			ServerSocket serverSocket = new ServerSocket(8080);// ����80�˿�
			System.out.println("����������>>> 8080�˿�");
			while (true) {// ����һ����Զ��ѭ����һֱ�ȴ��ͻ�����80�˿�
				socket = serverSocket.accept();

				if (socket != null) {
					Thread thread = new Thread(new Handler(socket));// ���пͻ�����ʱ������һ�����̴߳��������
					thread.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
