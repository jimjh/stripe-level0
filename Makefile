CC = gcc
CFLAGS = -Wall -Werror -O3 -std=gnu99 -pthread

%.o: %.c %.h
	$(CC) $(CFLAGS) -c $< -o $@

test_hashset: hashset.o test_hashset.o hasher.o
	$(CC) $(CFLAGS) $^ -o $@

preprocess: hashset.o preprocess.o csapp.o hasher.o
	$(CC) $(CFLAGS) $^ -o $@

level0: hashset.o level0.o csapp.o hasher.o dict.o
	$(CC) $(CFLAGS) $^ -o $@

dict.bin: preprocess
	./preprocess

dict.o: dict.bin
	objcopy -I binary -O elf64-x86-64 -B i386:x86-64 dict.bin dict.o

.PHONY: test
test: test_hashset
	./test_hashset

.PHONY: clean
clean:
	rm -f level0
	rm -f test_hashset
	rm -f preprocess
	rm -f *.o
