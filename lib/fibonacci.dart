//Sample 编译时间可以忽略
fibo(int n){
  if(n < 2){
    return n;
  }
  return fibo(n - 1) + fibo(n - 2);
}

main(){
  fibo(10);
}