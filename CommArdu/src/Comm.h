/*
 * Comm.h
 *
 * Created: 8/7/2017 12:43:27 PM
 *  Author: Andrew_2
 */ 

#ifndef COMM_H_
#define COMM_H_

#define NULL_COMM 0
#define SERIAL_COMM 1
#define SOFT_SERIAL_COMM 2

#define BAUD_RATE 9600

#include "Config.h"

void initComm(void);
void updateComm(void);

int availableComm(void);
unsigned char readComm(void);
int writeComm(unsigned char *data, int len);

#endif /* COMM_H_ */