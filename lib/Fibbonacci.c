#include<stdlib.h>
#include<stdio.h>

long fibo(long n){
  if(n < 2){
    return n;
  }
  return fibo(n - 1) + fibo(n - 2);
}

int main(int argc, char *argv[]){
  long num = atoi(argv[1]);
  printf("%ld",fibo(num));
}