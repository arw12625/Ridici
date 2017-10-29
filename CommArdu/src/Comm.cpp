/*
 * Comm.cpp
 *
 * Created: 8/7/2017 1:23:40 PM
 *  Author: Andrew_2
 */

#include "Comm.h"
#include <Arduino.h>


#if COMM_MODE == SOFT_SERIAL_COMM
#include "SoftwareSerial.h"
SoftwareSerial softSerial(2,3);
#endif

void initComm(void) {
	
	#if COMM_MODE == SERIAL_COMM
		Serial.begin(BAUD_RATE);
	#elif COMM_MODE == SOFT_SERIAL_COMM
		softSerial.begin(BAUD_RATE);
	#endif
	
	
}


void updateComm(void) {
	
}

int availableComm(void) {
	#if COMM_MODE == SERIAL_COMM
		return Serial.available();
	#elif COMM_MODE == SOFT_SERIAL_COMM
		return softSerial.available();
	#elif COMM_MODE == NULL_COMM
		return 0;
	#endif
}

unsigned char readComm(void) {
	#if COMM_MODE == SERIAL_COMM
		return Serial.read();
	#elif COMM_MODE == SOFT_SERIAL_COMM
		return softSerial.read();
	#elif COMM_MODE == NULL_COMM
		return 0;
	#endif
}

int writeComm(unsigned char *data, int len) {
	#if COMM_MODE == SERIAL_COMM
		return Serial.write(data, len);
	#elif COMM_MODE == SOFT_SERIAL_COMM
		return softSerial.write(data, len);
	#elif COMM_MODE == NULL_COMM
		return 0;
	#endif
}