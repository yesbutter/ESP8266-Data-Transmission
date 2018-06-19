package fun;

import java.io.*;  
import java.net.Socket;  
  
//请求处理程序  
public class Handler implements  Runnable {  
  
    private Socket socket;  
    private OutputStream outputStream;  
    private InputStream inputStream;  
    private BufferedReader reader;  
    private PrintWriter writer;  
    private static String WEB_ROOT = "D:\\KEIL IDE";//服务器储存文件目录  
    private static long times=0;
    private String ans="";
    public Handler(Socket socket){  
        this.socket = socket;  
    }  

	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();// 服务器到客户的输出流
			outputStream = socket.getOutputStream();// 客户到服务器的输入流
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));// 包装后的输入缓冲字符流
			writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));// 包装后的输出缓冲字符流
			String msg = "";// 接收客户端请求的临时字符串
			StringBuffer request = new StringBuffer();// 将请求拼接成完整的请求
			// while((msg = reader.readLine()) != null && msg.length() > 0)
			// {
			// System.out.println(msg);
			// writer.write(msg);
			// //writer.flush();
			// //request.append("\n");//HTTP协议的格式
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
							//outputStream.write(fileBuffer, 0, length);// 向客户端浏览器发送文件
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
				// request.append("\n");//HTTP协议的格式
			}
			
			// String[] msgs = request.toString().split(" ");//HTTP协议以空格为分隔符
			// msgs[1]代表了HTTP协议中的第二个字符串，是浏览器请求的文件名
			/*
			 * if(msgs[1].endsWith(".ico")){//.ico文件是浏览器页面的图标文件，是浏览器默认会向服务器发送的请求
			 * writer.println("HTTP/1.1 200 OK");//如果服务器不打算对客户端发送.ico结尾的文件，可以这一写，然后直接返回，
			 * 跳过发送该文件的过程 writer.println("Content-Type: text/html;charset=UTF-8");
			 * writer.close(); //如果不发送，就直接返回 return; }
			 */
			// 将服务器目录下被请求的文件读入程序
//			FileInputStream fileInputStream = new FileInputStream(new File(WEB_ROOT + "\\main.txt"));
//			byte[] fileBuffer = new byte[1024];// 可能是二进制文件，所以需要用byte数组做缓冲
//			int length = 0;
//			while ((length = fileInputStream.read(fileBuffer)) != -1) {
//				outputStream.write(fileBuffer, 0, length);// 向客户端浏览器发送文件
//			}
			outputStream.close();
			inputStream.close();
			reader.close();
			socket.close();
//			fileInputStream.close();
			System.out.println("---6----");
		} catch (IOException e) {
			// 如果抛出异常，在这里基本上是未找到被请求文件，所以返回404
			e.printStackTrace();
			writer.write("HTTP/1.1 404 ERROR:FILE NOT FINDED");
			writer.close();
		}
	}  
}  