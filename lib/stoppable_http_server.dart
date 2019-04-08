import 'dart:io';
import 'package:args/args.dart';
import 'package:http_server/http_server.dart' show VirtualDirectory;
import 'package:newtify/sample_object.dart';
import 'dart:convert';
final String PATH_TO_WEB_CONTENT = "web";


List<Citizen> cache = getRandomCitizens(100);

int counter = 0;

void main(List<String> args) {

  HttpServer.bind(InternetAddress.loopbackIPv4, 8080).then((HttpServer server) {
    print("Server up; try http://${server.address.address}:${server.port}/index.html");
//    final String root = Platform.script.resolve(PATH_TO_WEB_CONTENT).toFilePath();
//    final virDir = new VirtualDirectory(root)
//      ..followLinks = true
//      ..jailRoot = false;
    server.listen((HttpRequest req) {
//      print("${req.method} ${req.uri};\tcached ${req.headers.ifModifiedSince}");
//      (req.method == "PUT" ? processPut : virDir.serveRequest)(req);
       if(req.method == "GET" || req.method == "POST") {
         processReq(req);
         counter ++;
       }

    });

    ArgParser parser = new ArgParser();
    parser.addOption('training',defaultsTo: "false");
    var result = parser.parse(args);
    if(result["training"] == "true"){
      Future.delayed(new Duration(seconds: 100),(){
        //exit 0 or server.close will do
      server.close(force:true);
//        exit(0);
      });
    }
  });
}

void processReq(HttpRequest request) async{
  Citizen citizen = cache[counter % 100];
//  citizen.features.forEach((k,v){
//    request.headers.add(k, v);
//  });
  request.response.write(jsonEncode(citizen.toJson()));
//  request.headers.add('uri', value);
  request.response.close();
}


void processPut(HttpRequest request) {
  final String filePath = request.uri.toFilePath();
  final File file = new File(PATH_TO_WEB_CONTENT + filePath);
  request.pipe(file.openWrite()).then((_) {
    request.response
      ..statusCode = HttpStatus.NO_CONTENT
      ..close();
    print("Wrote to file '${file.path}'");
  });
}

