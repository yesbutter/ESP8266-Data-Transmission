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

	TFTLCD_Init();			//LCD≥ı ºªØ
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
			USART_ITConfig ( USART2, USART_IT_RXNE, DISABLE ); //Ω˚”√¥Æø⁄Ω” ’÷–∂ 
			ESP8266_Fram_Record_Struct .Data_RX_BUF [ ESP8266_Fram_Record_Struct .InfBit .FramLength ]  = '\0';
            strcpy(str,ESP8266_Fram_Record_Struct .Data_RX_BUF);
            //sprintf (ESP8266_Fram_Record_Struct.Data_RX_BUF,str);			
			LCD_ShowString(10,120,tftlcd_data.width,tftlcd_data.height,24,str+3);
			ESP8266_Fram_Record_Struct .InfBit .FramLength = 0;
            ESP8266_Fram_Record_Struct .InfBit .FramFinishFlag = 0;
			USART_ITConfig ( USART2, USART_IT_RXNE, ENABLE ); // πƒ‹¥Æø⁄Ω” ’÷–∂œ  
		} 
		delay_ms(10);
		if(TcpClosedFlag) //ºÏ≤‚ «∑Ò ß»•¡¨Ω”
        {
            ESP8266_ExitUnvarnishSend(); //ÕÀ≥ˆÕ∏¥´ƒ£ Ω
            do
            {
                res = ESP8266_Get_LinkStatus();     //ªÒ»°¡¨Ω”◊¥Ã¨
            }
            while(!res);

            if(res == 4)                     //»∑»œ ß»•¡¨Ω”∫Û÷ÿ¡¨
            {
                printf ( "\r\n«Î…‘µ»£¨’˝‘⁄÷ÿ¡¨»»µ„∫Õ∑˛ŒÒ∆˜...\r\n" );

                while (!ESP8266_JoinAP(User_ESP8266_SSID, User_ESP8266_PWD ) );

                while (!ESP8266_Link_Server(enumTCP, User_ESP8266_TCPServer_IP, User_ESP8266_TCPServer_PORT, Single_ID_0 ) );

                printf ( "\r\n÷ÿ¡¨»»µ„∫Õ∑˛ŒÒ∆˜≥…π¶\r\n" );
            }
            while(!ESP8266_UnvarnishSend());
        }
    }
}



