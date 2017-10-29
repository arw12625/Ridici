
SOURCE = ./src/
BIN = ./bin/
DRIVER_INC = ./driver/

CC = xtensa-lx106-elf-gcc
CFLAGS = -I$(SOURCE) -I$(DRIVER_INC) -D ICACHE_FLASH -mlongcalls 
#LDLIBS = -nostdlib -Wl,--start-group -lmain -lnet80211 -llwip -lwpa -lpp -lphy -lc -ldriver -Wl,--end-group -lgcc
LDLIBS = -nostdlib -Wl,--start-group -lmain -lnet80211 -llwip -lwpa -lpp -lphy -lc -Wl,--end-group -lgcc
LDFLAGS = -Teagle.app.v6.ld

ADDRESS_0 = 0x00000
ADDRESS_1 = 0x10000

PROG = udp_uart_bridge

binary: $(BIN)$(PROG)
	esptool.py elf2image $^

$(BIN)%.o: $(SOURCE)%.c
	$(CC) $(CFLAGS) -c -o $@ $<

flash: $(BIN)$(PROG)-$(ADDRESS_0).bin $(BIN)$(PROG)-$(ADDRESS_1).bin 
	esptool.py -b 115200 --port /dev/ttyUSB0 write_flash $(ADDRESS_0) $(word 1,$^) $(ADDRESS_1) $(word 2,$^)

clean:
	rm -f $(BIN)*
