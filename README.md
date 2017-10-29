# Ridici
*A simple framework for control of embedded systems*

**Overview**
The goal of this project is to provide a framework for communication and control of embedded
systems and personal computers, as well as . Hopefully, this will enable more rapid
development of my other embedded projects. The framework abstracts the communication
channel so that different methods of communication can be swapped easily. In addition it
provides multiple methods of user input to control the device.

The communication scheme uses consistent overhead byte stuffing to handle errors in
transmission and to synchronize the sender and receiver. Other coding schemes may be added
in the future.

Currently, development is focused on three components of the framework: an embedded codebase,
a pc client, and a wireless communication bridge.

***Embedded Codebase***
A template for embedded projects for communicating with other compatible devices using the
framework. For now, the focus is on Arduino microcontrollers and more broadly AVR chips.

***PC Client***
A collection of Java programs enabling the use of various communication channels and
providing utilities for controlling embedded systems. The current focus is on supporting
communication over USB using Rxtx and through a serial bridge over UDP and TCP. The
client also provides a serial console and supports input from a gamepad through the
JInput library.

***Wireless Communication Bridge***
A program for the ESP8266 Wifi module enabling serial communication with an embedded
device over Wifi. The ESP8266 acts as an access point and a UDP or TCP server for receiving
and responding to messages over the web. The goal is to provide a link with a low enough
latency to enable control of real time systems like robots.