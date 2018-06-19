package fun;

import java.io.*;  
import java.net.Socket;  
  
//���������  
public class Handler implements  Runnable {  
  
    private Socket socket;  
    private OutputStream outputStream;  
    private InputStream inputStream;  
    private BufferedReader reader;  
    private PrintWriter writer;  
    private static String WEB_ROOT = "D:\\KEIL IDE";//�����������ļ�Ŀ¼  
    private static long times=0;
    private String ans="";
    public Handler(Socket socket){  
        this.socket = socket;  
    }  

	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();// ���������ͻ��������
			outputStream = socket.getOutputStream();// �ͻ�����������������
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));// ��װ������뻺���ַ���
			writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));// ��װ�����������ַ���
			String msg = "";// ���տͻ����������ʱ�ַ���
			StringBuffer request = new StringBuffer();// ������ƴ�ӳ�����������
			// while((msg = reader.readLine()) != null && msg.length() > 0)
			// {
			// System.out.println(msg);
			// writer.write(msg);
			// //writer.flush();
			// //request.append("\n");//HTTPЭ��ĸ�ʽ
			// }
			if ((msg=reader.readLine())!=null) {

				System.out.println(msg);
				if (msg.equals("AT+CIPMODE=1")||msg.equals("AT+CIPSTO=1800")||msg.equals("AT+CIPSERVER=1,8080")) {
					writer.write("OK\n");
					writer.flush();
				}
				else if(msg .equals( "AT+CIPSEND"))
				{
					writer.write("OK\n");
					writer.flush();
					//if(socket.getInetAddress().toString().equals("/192.168.1.107")) 
					{
						FileInputStream fileInputStream = new FileInputStream(new File(WEB_ROOT + "\\main.txt"));
						InputStreamReader reader = new InputStreamReader(fileInputStream,"UTF-8");
						BufferedReader br = new BufferedReader(reader);
						String tmp=br.readLine();
//						if ((length = fileInputStream.read(fileBuffer)) != -1) 	
						{
							//outputStream.write(fileBuffer, 0, length);// ��ͻ�������������ļ�
							writer.write(tmp);
							System.out.println(tmp);
							writer.flush();
						}
					}
				}
				else {
					FileOutputStream fileOutputStream = new FileOutputStream(new File(WEB_ROOT + "\\main.txt"));
					OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream,"UTF-8");
					BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
					bufferedWriter.write(msg);
					bufferedWriter.close();
					outputStreamWriter.close();
					fileOutputStream.close();
					writer.write(msg);
					writer.flush();
					ans=new String(msg);
				}
				// writer.flush();
				// request.append("\n");//HTTPЭ��ĸ�ʽ
			}
			
			// String[] msgs = request.toString().split(" ");//HTTPЭ���Կո�Ϊ�ָ���
			// msgs[1]������HTTPЭ���еĵڶ����ַ������������������ļ���
			/*
			 * if(msgs[1].endsWith(".ico")){//.ico�ļ��������ҳ���ͼ���ļ����������Ĭ�ϻ�����������͵�����
			 * writer.println("HTTP/1.1 200 OK");//���������������Կͻ��˷���.ico��β���ļ���������һд��Ȼ��ֱ�ӷ��أ�
			 * �������͸��ļ��Ĺ��� writer.println("Content-Type: text/html;charset=UTF-8");
			 * writer.close(); //��������ͣ���ֱ�ӷ��� return; }
			 */
			// ��������Ŀ¼�±�������ļ��������
//			FileInputStream fileInputStream = new FileInputStream(new File(WEB_ROOT + "\\main.txt"));
//			byte[] fileBuffer = new byte[1024];// �����Ƕ������ļ���������Ҫ��byte����������
//			int length = 0;
//			while ((length = fileInputStream.read(fileBuffer)) != -1) {
//				outputStream.write(fileBuffer, 0, length);// ��ͻ�������������ļ�
//			}
			outputStream.close();
			inputStream.close();
			reader.close();
			socket.close();
//			fileInputStream.close();
			System.out.println("---6----");
		} catch (IOException e) {
			// ����׳��쳣���������������δ�ҵ��������ļ������Է���404
			e.printStackTrace();
			writer.write("HTTP/1.1 404 ERROR:FILE NOT FINDED");
			writer.close();
		}
	}  
}  