//Sample 编译时间可以忽略
int fibo(int n){
  if(n < 2){
    return n;
  }
  return fibo(n - 1) + fibo(n - 2);
}

void main(List<String> args){
  fibo(int.parse(args[0]));
}
