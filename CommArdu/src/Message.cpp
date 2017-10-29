/*
 * Message.cpp
 *
 * Created: 8/7/2017 2:06:39 PM
 *  Author: Andrew_2
 */
#include "Message.h"
#include "Comm.h"
#include <Arduino.h>

#define READ_MESSAGE_BUFFER_SIZE 254
#define READ_BUFFER_SIZE 256
#define WRITE_MESSAGE_BUFFER_SIZE 254
#define WRITE_BUFFER_SIZE 256

#define SHORT_TO_BYTES(shortVal, byteArray, index) \
do { \
	byteArray[index] = (shortVal >> 8) & 0xFF; \
	byteArray[index + 1] = shortVal & 0xFF; \
} while(0)


unsigned char inMessageBuffer [READ_MESSAGE_BUFFER_SIZE];
unsigned char readBuffer [READ_BUFFER_SIZE];
unsigned char readLen;

unsigned char outMessageBuffer [WRITE_MESSAGE_BUFFER_SIZE];
unsigned char writeBuffer [WRITE_BUFFER_SIZE];

void unStuffData(const unsigned char *ptr, unsigned char length, unsigned char *dst);
void stuffData(const unsigned char *ptr, unsigned char length, unsigned char *dst);


void initMessage(void) {
	readLen = 0;
}

void readMessage(void);

void updateMessage(void) {
	readMessage();
}


extern void parseMessage(unsigned char *data, int len);

void readMessage(void) {
	while(availableComm() > 0) {
		readBuffer[readLen++] = readComm();
		if(readBuffer[readLen - 1] == 0) {
			unStuffData(readBuffer, readLen, inMessageBuffer);

			parseMessage(inMessageBuffer, readLen - 2);

			readLen = 0;
		}
		digitalWrite(A5, LOW);
	}
}

void writeMessage(unsigned char *data, int len) {

	
	unsigned char writeLen = len + 1;
	outMessageBuffer[0] = writeLen;
	for(int j = 0; j < len; j++) {
		outMessageBuffer[1 + j] = data[j];
	}
	
	
	stuffData(outMessageBuffer, writeLen, writeBuffer);

	writeComm(writeBuffer, writeLen + 2);

}

void unStuffData(const unsigned char *ptr, unsigned char length, unsigned char *dst)
{
	const unsigned char *end = ptr + length - 1;
	while (ptr < end) {
		int i, code = *ptr++;
		for (i=1; i<code; i++) {
			*dst++ = *ptr++;
		}
		*dst++ = 0;
	}
}

#define FinishBlock(X) (*code_ptr = (X), code_ptr = dst++, code = 0x01)

void stuffData(const unsigned char *ptr, unsigned char length, unsigned char *dst) {
	const unsigned char *end = ptr + length;
	unsigned char *code_ptr = dst++;
	unsigned char code = 0x01;

	while (ptr < end) {
		if (*ptr == 0) {
			FinishBlock(code);
			} else {
			*dst++ = *ptr;
			code++;
		}
		ptr++;
	}

	*dst = 0;
	FinishBlock(code);
}
