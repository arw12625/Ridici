#include <Arduino.h>

#include "timeUtil.h"

unsigned long timer, timerOld, timerDelta;

void initTime(void) {
	
	timer=millis();

}


void updateTime(void) {
	
	timerOld = timer;
	timer=millis();
	timerDelta = timer - timerOld;

}