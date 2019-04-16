import 'dart:async';
import 'dart:io';
import 'package:args/args.dart';
import 'package:http_server/http_server.dart' show VirtualDirectory;
import 'package:newtify/Polymorphism_BigBoss.dart';
import 'package:newtify/sample_object.dart';
import 'dart:convert';
final String PATH_TO_WEB_CONTENT = "web";

const int sampleCount = 512;
List<Citizen> cache = getRandomCitizens(sampleCount);

int counter = 0;
int counter_fail = 0;
int counter_success  = 0;


void main(List<String> args) {

  Stopwatch stopwatch = new Stopwatch();
  stopwatch.start();
  print('Begin @ ${new DateTime.now()}');
  ArgParser parser = new ArgParser();
  parser.addFlag('train',defaultsTo: false);
  parser.addOption('interval', defaultsTo:'10');
  parser.addOption('train-time', defaultsTo:'100');
  var result = parser.parse(args);
  int interval = int.parse(result['interval']);
  int tt = int.parse(result['train-time']);
  double preTimeMark = stopwatch.elapsedMilliseconds/1000.roundToDouble();

  Timer timer = Timer.periodic(new Duration(milliseconds: interval), (Timer t){
    double current  = stopwatch.elapsedMilliseconds/1000.roundToDouble();
    double failedPersec  = counter_fail / (current - preTimeMark) < 1 ? 0 : (counter_fail / (current-preTimeMark)).roundToDouble();
    double successPersec = counter_success / (current - preTimeMark) < 1 ? 0 : (counter_success / (current-preTimeMark)).roundToDouble();
    String line = '''   \t ${counter+counter_fail}            ${current}          ${successPersec}      ${failedPersec} ''';
    counter_success = 0;
    counter_fail = 0;

    print("$line");


    preTimeMark = current;
  });

  HttpServer.bind(InternetAddress.anyIPv4, 8080).then((HttpServer server) {
    print("Server up; try http://${server.address.address}:${server.port}/index.html");
    print("\n\n\n");
    print('\t --total---------tel---------succ---------fail------\t');
    server.listen((HttpRequest req) {
       if(req.method == "GET" || req.method == "POST") {
         counter++;
         processReq(req);
       }
    });

    if(result['train'] as bool){
      Future.delayed(new Duration(seconds: tt),(){
        //exit 0 or server.close will do
      server.close(force:true).then((v){
        timer.cancel();
        exit(0);
      });

      });
    }
  });
}

void processReq(HttpRequest request){

  try{
//    List<Citizen> citizens = getRandomCitizens(10);
    List<Citizen> citizens = List.from(cache.getRange(counter%sampleCount, counter%sampleCount+1 > sampleCount-1 ? sampleCount-1 : counter%sampleCount+1));
    List<Map<String,dynamic>> jsonString = <Map<String,dynamic>>[];
    BigBoss boss = new BigBoss(numOfEmployee: 1);
    citizens.forEach((c){
      jsonString.add(c.toJson());
      moke_hire(boss, c.idType, c.docId);
    });
    request.response.headers.add('bossCal','${boss.calcAll()}');
    request.response.write(jsonEncode(jsonString));
    request.response.close().then((v){
      counter_success ++;
    }).timeout(new Duration(seconds:5)).catchError((e){
      counter_fail ++;
    });
  }
  catch(e){
    request.response.statusCode = 500;
    counter_fail ++;
    request.response.close();
  }
}

void processPut(HttpRequest request) {
  final String filePath = request.uri.toFilePath();
  final File file = new File(PATH_TO_WEB_CONTENT + filePath);
  request.pipe(file.openWrite()).then((_) {
    request.response
      ..statusCode = HttpStatus.noContent
      ..close();
    print("Wrote to file '${file.path}'");
  });


}
