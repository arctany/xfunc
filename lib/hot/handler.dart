import 'dart:io';
import 'package:random_string/random_string.dart';
import '../interface/function.dart';


execute(List<String> s) {
  HttpServer
      .bind(InternetAddress.anyIPv6, 8080,shared: true)
      .then((server) {
    server.listen((HttpRequest request) {
      if(request.uri.path.contains('hello')){
//        request.response.write('10');
        request.response.write(getInstance().handle());
      }
      request.response.close();
    });
  });
}

//String handle() => '${test()} Hello New World';

//String test() => 'one ${randomAlpha(10)}' ;

//String test() => 'test';

getInstance()=> new EmptyHandler();


class EmptyHandler implements XFunction{

  @override
  String handle() {
    // TODO: implement handle
    return  'Hello Old World @ ${randomAlphaNumeric(10)}';
  }

  @override
  String initialize() {
    // TODO: implement initialize

    return null;
  }

}

void main(List<String> args){
  execute(args);
}