#include "sta_tcpclent_test.h"
#include "SysTick.h"
#include "usart.h"
#include "string.h"
#include "esp8266_drive.h"
#include "tftlcd.h"

volatile u8 TcpClosedFlag = 0;


void ESP8266_STA_TCPClient_Test(void)
{

    char str[100]= {0};

	TFTLCD_Init();			//LCD初始化
    ESP8266_CH_PD_Pin_SetH;
	ESP8266_AT_Test();
	ESP8266_Net_Mode_Choose(STA);
	while(!ESP8266_JoinAP(User_ESP8266_SSID, User_ESP8266_PWD));
	ESP8266_Enable_MultipleId ( DISABLE );
	while(!ESP8266_Link_Server(enumTCP, User_ESP8266_TCPServer_IP, User_ESP8266_TCPServer_PORT, Single_ID_0));
	
	while(!ESP8266_UnvarnishSend());
    
	//while(!ESP8266_StartOrShutServer(ENABLE, User_ESP8266_TCPServer_PORT, "1800" ));
    
	while ( 1 )
    {
		while(!ESP8266_UnvarnishSend());
		if(ESP8266_Fram_Record_Struct .InfBit .FramFinishFlag)       
		{
			USART_ITConfig ( USART2, USART_IT_RXNE, DISABLE ); //禁用串口接收中� 
			ESP8266_Fram_Record_Struct .Data_RX_BUF [ ESP8266_Fram_Record_Struct .InfBit .FramLength ]  = '\0';
            strcpy(str,ESP8266_Fram_Record_Struct .Data_RX_BUF);
            //sprintf (ESP8266_Fram_Record_Struct.Data_RX_BUF,str);			
			LCD_ShowString(10,120,tftlcd_data.width,tftlcd_data.height,24,str+3);
			ESP8266_Fram_Record_Struct .InfBit .FramLength = 0;
            ESP8266_Fram_Record_Struct .InfBit .FramFinishFlag = 0;
			USART_ITConfig ( USART2, USART_IT_RXNE, ENABLE ); //使能串口接收中断  
		} 
		delay_ms(10);
		if(TcpClosedFlag) //检测是否失去连接
        {
            ESP8266_ExitUnvarnishSend(); //退出透传模式
            do
            {
                res = ESP8266_Get_LinkStatus();     //获取连接状态
            }
            while(!res);

            if(res == 4)                     //确认失去连接后重连
            {
                printf ( "\r\n请稍等，正在重连热点和服务器...\r\n" );

                while (!ESP8266_JoinAP(User_ESP8266_SSID, User_ESP8266_PWD ) );

                while (!ESP8266_Link_Server(enumTCP, User_ESP8266_TCPServer_IP, User_ESP8266_TCPServer_PORT, Single_ID_0 ) );

                printf ( "\r\n重连热点和服务器成功\r\n" );
            }
            while(!ESP8266_UnvarnishSend());
        }
    }
}



