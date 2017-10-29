/*
 * TimeUtil.h
 *
 * Created: 8/7/2017 12:17:52 PM
 *  Author: Andrew_2
 */ 


#ifndef TIMEUTIL_H_
#define TIMEUTIL_H_

#define TIMER_RES 1000

extern unsigned long timer;
extern unsigned long timerDelta;

void initTime(void);
void updateTime(void);




#endif /* TIMEUTIL_H_ */