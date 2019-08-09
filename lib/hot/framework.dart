import 'dart:async';
import 'dart:io';

import 'package:vm_service_lib/vm_service_lib.dart' as vm;
import 'package:vm_service_lib/vm_service_lib_io.dart';
import 'dart:isolate';
import 'package:args/args.dart';
import 'handler.dart';
//spawn 4 empty isolates
//loading 4 isolate implementations dynamically

vm.VmService serviceClient;

File dir = new File('/Users/haowu/Downloads/workspace/code_repos/dart-new/ext-module/lib');
//File dir = new File('/Users/haowu/IdeaProjects/xfunc/lib/hot-ext');

void main(List<String> args) async {

  ArgParser parser = new ArgParser();
  parser.addFlag('src',defaultsTo: false);

  var arr = parser.parse(args);

  List<String> handlerIds = <String>[];
  List<Future<Isolate>> futureList = <Future<Isolate>>[];

  Stopwatch stopwatch = new Stopwatch();
  stopwatch.start();

  bool src = arr['src'];

  for (int i = 0; i < 4; i++) {

    if(!src){
      futureList.add(Isolate.spawnUri(
          Uri.file('/Users/haowu/IdeaProjects/xfunc/lib/hot/handler.dill'),
          ['a', 'c'], 'a', paused: false, debugName: 'handler'));
    }else{
      futureList.add(Isolate.spawn(execute, null, debugName: 'handler'));
    }
  }

  await Future.wait(futureList).whenComplete(() => print('finished'));

  final String host = "127.0.0.1";
  final int port = 8181;

  serviceClient = await vmServiceConnect(host, port).catchError((e,stack) {
    print('ReloadSource:socket connected:$host:$port, err=$e, stack=$stack}');
  });

  var report = await serviceClient.getVM();
  for (vm.IsolateRef ref in report.isolates) {
    if (ref.name.contains("handler")) {
      handlerIds.add(ref.id);
    }
  }
  print('Start user handler: ${handlerIds}');
  print('Framework prepareation cast   ${stopwatch.elapsedMilliseconds}  ms'  );
  List<Future> loadFutures;
//  File dir = new File('/Users/haowu/Downloads/workspace/code_repos/dart-new/ext-module/lib');
  String reloadFilePath = dir.path+'/handler.dill';
  dir.watch().listen((event){
    if(event.path.contains('handler.dill') && event.type == FileSystemEvent.modify){

      if(!new File(reloadFilePath).existsSync()){
        return;
      }
      stopwatch.reset();
      loadFutures = _reload(reloadFilePath, handlerIds);
      Future.wait(loadFutures).then((e){
        print('Hot reload took ${stopwatch.elapsedMilliseconds} ms');
      });
    }
    print('changed $event' );
  });

  stopwatch.reset();
}

List<Future> _reload(String uri, List<String> isolateIds){
  List<Future> reports = <Future>[];
  isolateIds.forEach((i) {
    reports.add(serviceClient.reloadSources(i,
        pause: false,
        force: true,
        rootLibUri: uri).then((r){
      print(r.toJson()
      );
    }).catchError((es){print(es);}));
  });
  return reports;
}


