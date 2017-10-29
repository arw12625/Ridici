/*
 * Parser.cpp
 *
 * Created: 8/7/2017 3:06:43 PM
 *  Author: Andrew_2
 */ 

#include "Message.h"

extern void kill(void);

void parseMessage(unsigned char *data, int len) {
	
	unsigned char command = data[0];
	switch(command) {
		case 'i': {
			//send info
			break;
		} case 'k': {
			kill();
			break;
		}
		
	}

}
