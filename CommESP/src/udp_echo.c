



/******************************************************************************
 * Copyright 2013-2014 Espressif Systems
 *
*******************************************************************************/
#include "ets_sys.h"
#include "os_type.h"
#include "osapi.h"
#include "mem.h"
#include "gpio.h"
#include "user_interface.h"
#include <ip_addr.h>

#include "espconn.h"
 
#define UDP_PORT 1234

LOCAL struct espconn ptrespconn;
 
 
/******************************************************************************
 * FunctionName : user_devicefind_recv
 * Description  : Processing the received udp packet
 * Parameters   : arg -- Additional argument to pass to the callback function
 *                pusrdata -- The received data (or NULL when the connection has been closed!)
 *                length -- The length of received data
 * Returns      : none
*******************************************************************************/
LOCAL void ICACHE_FLASH_ATTR
 user_udp_recv_cb(void *arg, char *pusrdata, unsigned short length)
 {
       
     os_printf("recv udp data: %s\n", pusrdata);
     struct espconn *pesp_conn = arg;
      
       remot_info *premot = NULL;
       sint8 value = ESPCONN_OK;
       if (espconn_get_connection_info(pesp_conn,&premot,0) == ESPCONN_OK){
             pesp_conn->proto.tcp->remote_port = premot->remote_port;
             pesp_conn->proto.tcp->remote_ip[0] = premot->remote_ip[0];
             pesp_conn->proto.tcp->remote_ip[1] = premot->remote_ip[1];
             pesp_conn->proto.tcp->remote_ip[2] = premot->remote_ip[2];
             pesp_conn->proto.tcp->remote_ip[3] = premot->remote_ip[3];
             espconn_sent(pesp_conn, pusrdata, length);
       }
 }
 
/******************************************************************************
 * FunctionName : user_devicefind_init
 * Description  : create a udp listening
 * Parameters   : none
 * Returns      : none
*******************************************************************************/
void ICACHE_FLASH_ATTR
user_udp_init(void)
{
    ptrespconn.type = ESPCONN_UDP;
    ptrespconn.proto.udp = (esp_udp *)os_zalloc(sizeof(esp_udp));
    ptrespconn.proto.udp->local_port = UDP_PORT;  // ESP8266 udp port
    espconn_regist_recvcb(&ptrespconn, user_udp_recv_cb); // register a udp packet receiving callback
    espconn_create(&ptrespconn);   // create udp
}
 
/******************************************************************************
 * FunctionName : user_init
 * Description  : entry of user application, init user function here
 * Parameters   : none
 * Returns      : none
*******************************************************************************/
void user_init(void)
{
 
// init gpio subsytem
  gpio_init();
  uart_div_modify(0, UART_CLK_FREQ / 115200);
  
       os_printf("SDK version:%s\n", system_get_sdk_version());

 
   //Set softAP + station mode 
   wifi_set_opmode(STATIONAP_MODE); 
 
   // Create udp listening.
   user_udp_init();
 
}
