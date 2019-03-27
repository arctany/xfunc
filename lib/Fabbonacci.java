public class Fabbonacci{

    public static long fibo(long n){
        if(n < 2){
            return n;
        }
        return fibo(n - 1) + fibo(n - 2);
    }

    public static void main(String[] args){
        fibo(Long.parseLong(args[0]));
    }

}



