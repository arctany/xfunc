
String nto_date(String arg) => new DateTime.fromMicrosecondsSinceEpoch((int.parse(arg)/1000).round()).toString();

void main(List<String> args) => print(nto_date(args[0]));

