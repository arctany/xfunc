import 'dart:io';

int main(){

    ProcessSignal.sigint.watch().listen((signal) {
//    if (signal != ProcessSignal.SIGUSR1) exit(1);
//    usr1Count--;
//    check();
         print('killed');


    });

}