#include <assert.h>
#include <stdio.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/stat.h>
#include "hashset.h"

#define TMP_FILE "test_hashset.tmp"

typedef unsigned char byte;

static FILE *open_file() {
  FILE *file = fopen(TMP_FILE, "w+");
  if (NULL == file) perror(NULL);
  assert(NULL != file);
  return file;
}

static void close_file(FILE *file) {
  assert(!fclose(file));
}

static size_t hasher(const void *element, size_t len) {
  if (NULL ==  element) return 0;

  size_t code = 0;
  const byte *buffer = element;

  for (unsigned int i = 0; i < len; i++) {
    code  = *(buffer+i) + code * 31;
  }

  return code;
}

static set *init_set() {
  set *ptr = NULL;
  assert(!set_alloc(&ptr, 5*1024, 20*1024, hasher));

  assert(!set_add(ptr, "abc", sizeof("abc")));
  assert(!set_add(ptr, "def", sizeof("def")));
  assert(!set_add(ptr, "x", sizeof("x")));

  return ptr;
}

static void should_alloc() {
  printf("[test]\t#should_alloc\n");
  set *ptr = NULL;
  assert(!set_alloc(&ptr, 10, 1024, hasher));
  assert(NULL != ptr);
  assert(!set_free(&ptr));
}

static void should_free() {
  printf("[test]\t#should_free\n");
  set *ptr = NULL;
  assert(!set_alloc(&ptr, 10, 1024, hasher));
  assert(!set_free(&ptr));
  assert(NULL == ptr);
}

static void should_add() {
  printf("[test]\t#should_add\n");
  set *ptr = init_set();

  assert(!set_find(ptr, "x", sizeof("x")));
  assert(!set_find(ptr, "def", sizeof("def")));
  assert(ERR_NOT_FOUND == set_find(ptr, "w", sizeof("w")));
  assert(ERR_NOT_FOUND == set_find(ptr, "jumping dog", sizeof("jumping dog")));
  assert(!set_free(&ptr));
}

static void should_dump() {
  printf("[test]\t#should_dump\n");
  set *ptr = init_set();

  FILE *file = open_file();
  assert(!set_dump(ptr, file));
  close_file(file);

  assert(!set_free(&ptr));
}

static void should_load() {
  printf("[test]\t#should_load\n");

  // dump
  set *ptr = init_set();
  FILE *file = open_file();
  assert(!set_dump(ptr, file));
  close_file(file);
  assert(!set_free(&ptr));

  // check size
  int fd = open(TMP_FILE, O_RDONLY);
  struct stat statb;
  assert(!fstat(fd, &statb));
  size_t size = statb.st_size;

  // load
  assert(fd >= 0);
  void *mem = mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_PRIVATE, fd, 0);
  assert(MAP_FAILED != mem);
  assert(!set_load(&ptr, mem, hasher));

  assert(!set_find(ptr, "x", sizeof("x")));
  assert(!set_find(ptr, "def", sizeof("def")));
  assert(ERR_NOT_FOUND == set_find(ptr, "w", sizeof("w")));
  assert(ERR_NOT_FOUND == set_find(ptr, "jumping dog", sizeof("jumping dog")));

  assert(!munmap(ptr, size));
  assert(!close(fd));
}

int main() {
  printf("[test] starting\n");
  should_alloc();
  should_free();
  should_add();
  should_dump();
  should_load();
  printf("[test] success\n");
  return 0;
}
