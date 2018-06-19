### 2018-06-16——2018-6-19总结

```
  不多说废话，开门见山。实现功能。单片机通过轮询查询服务器端的指定文件夹
下的内容，在显示屏上显示。手机端可以发送消息到服务器端来实现更改显示屏的
内容。
  实际扩展可以实现手机端和单片机进行远程通信。理论上只需要有网络就可以实
现联系手机端发送消息到服务器，实际实现方式：服务器发送信息给单片机，单片
机返回信息给服务器，并将服务器返回的信息给手机。
  心里路程，寒假接触了stm32，esp8266。没有实际去使用，加入的嵌入式要做结
营作品。就准备摸索一下ESP8266实现远距离控制。然后就在考完4级之后去学习做
这个通信。
```

## 工具
- Android studio
- Keil5
- STM32开发板
- ESP8266WIFI模块
- 网络调试工具，串口调试工具
- Eclipse
```
遇到的坑:
  1.单片机调试的时候没能清楚理解单片机程序发送的信息在那里显示。单片机发送消
息给网络的代码是导致调试了很久。
  2.单片机和服务器通信的时候，当单片机发送AT+CIPMODE=1和AT+CIPSEND需要服务
器返回信息OK
  3.寻找单片机接受服务器的消息的代码，实际ESP8266打开了USART2串口，然后根据
返回信息进行判断，额外加了一个代码判断返回信息的标志。加了一个标志位即当读
到'\n'就认为是结束。
		if(ucCh=='\n')
		{
			ESP8266_Fram_Record_Struct .InfBit .FramFinishFlag = 1;
		}
4.服务器端的代码确保能打开端口，实现端口通信。如果不太懂可以打开防火墙，要
是被自己的防火墙给墙了也是一种极好的体验。

```


![Android发送消息](./images/Screenshot_201a_1.jpg)
```
开发过程：
  第一天：实现单片机和网络调试工具通信，通过网络调试工具发送信息给单片机。
  第二天：继续做第一天任务，寻找服务器代码实现，学习之后实现自己的功能，实
现了服务器和单片机通信。
  第三天：编写Android 移动程序实现和服务器的通信，继续编写服务器代码实现数
据通信。
  第四天：收尾工作，写代码，写历程。emm该爬去复习了。溜了
```

![数据显示](./images/IMG_20180619_232141.jpg)
```c
//回想代码：
//KEIL STM32 
int main()
{
	SysTick_Init(72);
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);  //中断优先级分组
	LED_Init();//LED灯初始化
	USART1_Init(115200);//串口1初始化
	ESP8266_Init(115200);//ESP8266初始化
	ESP8266_STA_TCPClient_Test();//主函数			
}

//主要函数
void ESP8266_STA_TCPClient_Test(void)
{
    char str[100]= {0};
	TFTLCD_Init();			//TFT屏幕初始化
    ESP8266_CH_PD_Pin_SetH;//设置高电平
	ESP8266_AT_Test();//ESP8266初始化
	ESP8266_Net_Mode_Choose(STA);//设置工作模式staion
	while(!ESP8266_JoinAP(User_ESP8266_SSID, User_ESP8266_PWD));//加入WIFI
	ESP8266_Enable_MultipleId ( DISABLE );//设置为单连接模式
	while(!ESP8266_Link_Server(enumTCP, User_ESP8266_TCPServer_IP, User_ESP8266_TCPServer_PORT, Single_ID_0));//连接通信服务器的ip和端口
	while(!ESP8266_UnvarnishSend());//发送数据测试 
	while ( 1 )
    {
		while(!ESP8266_UnvarnishSend());//发送数据测试
		if(ESP8266_Fram_Record_Struct .InfBit .FramFinishFlag)//判断是否有数据接收       
		{
			USART_ITConfig ( USART2, USART_IT_RXNE, DISABLE ); //关闭串口2通信
			ESP8266_Fram_Record_Struct .Data_RX_BUF [ ESP8266_Fram_Record_Struct .InfBit .FramLength ]  = '\0';//设置字符串结束位置
            strcpy(str,ESP8266_Fram_Record_Struct .Data_RX_BUF);//字符串赋值	
			LCD_ShowString(10,120,tftlcd_data.width,tftlcd_data.height,24,str+3);//TFT显示
			ESP8266_Fram_Record_Struct .InfBit .FramLength = 0;//长度初始化
            ESP8266_Fram_Record_Struct .InfBit .FramFinishFlag = 0;//判断标志初始化
			USART_ITConfig ( USART2, USART_IT_RXNE, ENABLE ); //打开串口2  
		} 
		delay_ms(10);//10ms延迟
}
```

![服务器处理数据](./images/IMG_20180619_232202_1.jpg)
```java
//服务器代码
public class Server {
	Socket socket;
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();//开启服务器
    }
	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(8080);// 开启8080端口
			System.out.println("服务器启动>>> 8080端口");
			while (true) {// 开启一个永远的循环，一直等待客户访问8080端口
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

public class Handler implements Runnable {

	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private BufferedReader reader;
	private PrintWriter writer;
	private static String WEB_ROOT = "D:\\KEIL IDE";// 服务器储存文件目录
	private static long times = 0;
	private String ans = "";

	public Handler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			inputStream = socket.getInputStream();// 服务器到客户的输出流
			outputStream = socket.getOutputStream();// 客户到服务器的输入流
			reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));// 包装后的输入缓冲字符流，注意编码格式
			writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"));// 包装后的输出缓冲字符流
			String msg = "";// 接收客户端请求的临时字符串
			StringBuffer request = new StringBuffer();// 将请求拼接成完整的请求
			if ((msg = reader.readLine()) != null) {

				System.out.println(msg);//输出查看接收到的消息
				if (msg.equals("AT+CIPMODE=1") || msg.equals("AT+CIPSTO=1800") || msg.equals("AT+CIPSERVER=1,8080")) {//如果是单片机发送的消息就返回OK
					writer.write("OK\n");
					writer.flush();
				} else if (msg.equals("AT+CIPSEND")) {//单片机发送的消息
					writer.write("OK\n");
					writer.flush();
					{
						FileInputStream fileInputStream = new FileInputStream(new File(WEB_ROOT + "\\main.txt"));
						InputStreamReader reader = new InputStreamReader(fileInputStream, "UTF-8");
						BufferedReader br = new BufferedReader(reader);
						String tmp = br.readLine();//打开文件读取，返回给单片机
						{
							writer.write(tmp);
							System.out.println(tmp);
							writer.flush();
						}
					}
				} else {//返回Android端的信息
					FileOutputStream fileOutputStream = new FileOutputStream(new File(WEB_ROOT + "\\main.txt"));//Android信息处理
					OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
					BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
					bufferedWriter.write(msg);//写入到单片机读取的文件
					bufferedWriter.close();
					outputStreamWriter.close();
					fileOutputStream.close();//数据流关闭
					writer.write(msg);//返回接收的Android数据信息
					writer.flush();
				}
			}
			outputStream.close();
			inputStream.close();
			reader.close();
			socket.close();
			System.out.println("---6----");//判断标志
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
```

```java
//注意声明internet权限
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_send;//按钮
    private EditText et_send,ip,so;//文本框
    private RecyclerView recyclerView;//中间的消息
    private MainRecyAdapter recyAdapter;//消息适配器
    private Toolbar toolbar;
    private ArrayList<String> data = new ArrayList<>();//数据
    private Socket socket = null;//socket
    private String TAG = "main";
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {//处理其他线程的UI信息
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String socket = msg.obj.toString();
                    data.add("服务器说：" + socket);//处理返回的信息
                    recyAdapter.notifyDataSetChanged();//刷新
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init_view();//view初始化
        con_socket();//socket连接
    }
    private void init_view() {
        btn_send = findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
        et_send = findViewById(R.id.ed_send);
        recyclerView = findViewById(R.id.main_recycler);
        recyAdapter = new MainRecyAdapter(R.layout.recycle_item, data);//适配器
        toolbar=findViewById(R.id.toolbar2);
        so=toolbar.findViewById(R.id.main_so);//需要在对应view下面去找控件
        ip=toolbar.findViewById(R.id.main_ip);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置垂直方向
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyAdapter);//设置适配器
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {//按键处理
            case R.id.btn_send:
                if (socket == null || socket.isClosed()) {
                    con_socket();
                }
                final String tmp = et_send.getText().toString();//获取编辑文本框
                if (!tmp.isEmpty()) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            PrintWriter wirter = null;
                            try {
                                wirter = new PrintWriter(new OutputStreamWriter(new DataOutputStream(socket.getOutputStream()),"UTF-8"));//注意编码格式
                                wirter.write(tmp);//写信息
                                wirter.flush();
                                socket.shutdownOutput();
                                read(socket);//读取返回信息
                                con_socket();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    data.add(tmp);
                    recyAdapter.notifyDataSetChanged();
                    Log.e(TAG, "onClick: " + "i am click");
                }
                break;
            default:
                break;
        }
    }

    public void con_socket() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Log.e(TAG, "run: "+ ip.getText().toString()+Integer.parseInt(so.getText().toString()));
                    socket = new Socket(ip.getText().toString(),Integer.parseInt(so.getText().toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public void read(Socket socket) {//读取返回信息
        try {
            InputStream is = socket.getInputStream();//获取输入数据流
            BufferedReader br = new BufferedReader(new InputStreamReader(is));//读取数据buffer
            String info = null;
            StringBuilder result = new StringBuilder("");//拼string
            while ((info = br.readLine()) != null) {
                result.append(info);
            }
            Message message=new Message();
            message.what=1;
            message.obj=result.toString();
            handler.sendMessage(message);//更新ui，不能在非主线程更新ui
            br.close();
            is.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//适配器
public class MainRecyAdapter extends RecyclerView.Adapter<MainRecyAdapter.ViewHoder>{
    private int resourceId;//布局id
    private ArrayList<String> data;//数据
    public MainRecyAdapter(int resourceId, ArrayList<String> list)
    {
        this.resourceId=resourceId;
        data=  list;
    }
    @Override
    public ViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {//创建viewholder
        View view = LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false);
        ViewHoder viewHoder=new ViewHoder(view);
        return viewHoder;
    }

    @Override
    public void onBindViewHolder(ViewHoder holder, int position) {//显示的时候调用
        holder.textView.setText(data.get(position));
    }

    @Override
    public int getItemCount() {//获取数量
        return data.size();
    }

    class ViewHoder extends RecyclerView.ViewHolder {//view的持有者
        protected TextView textView;
        public ViewHoder(View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.item_text);//找控件
        }
    }
}
```