import 'dart:io';

import 'package:vm_service_lib/vm_service_lib.dart' as vm;
import 'package:vm_service_lib/vm_service_lib_io.dart';
import 'func.dart';

vm.VmService serviceClient;



main() async{

  A.getFunc=()=>'fucx';

  print(A.getFunc());

  final String host = "127.0.0.1";
  final int port = 8181;

  serviceClient = await vmServiceConnect(host, port).catchError((e,stack) {
    print('ReloadSource:socket connected:$host:$port, err=$e, stack=$stack}');
  });

  var report = await serviceClient.getVM();

  List<String> handlerIds = <String>[];

  for (vm.IsolateRef ref in report.isolates) {
    if (ref.name.contains("handler")) {
      handlerIds.add(ref.id);
    }
  }

  List<Future> loadFutures;
  File dir = new File('/Users/haowu/IdeaProjects/xfunc/lib/hotreload/closure_reload.dart');
  dir.watch().listen((event){
    if(event.type == FileSystemEvent.delete){
      loadFutures = _reload(dir.path, handlerIds);
       Future.wait(loadFutures).then((e){
        print(e);
      });
      print(A.getFunc());
    }
    print('changed $event' );
  });

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