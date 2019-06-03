import '../interface/function.dart';

getInstance()=> new UserHandler();

class UserHandler implements XFunction{

  @override
  String initialize() {
    // TODO: implement initialize
    return null;
  }

  @override
  String handle() {
    // TODO: implement handle
    return 'Welcome to New World';
  }

}