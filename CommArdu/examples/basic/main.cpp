#include <Arduino.h>

#include "Config.h"
#include "TimeUtil.h"
#include "Comm.h"
#include "Message.h"

void setup() {
	initTime();
	initComm();
	initMessage();
}

void loop() {
	if((millis()-timer) >= MAIN_LOOP_TIME) {
		updateTime();
		updateComm();
		updateMessage();
	}
}

void kill() {
}

