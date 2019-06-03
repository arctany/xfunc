import 'dart:async';

import 'package:vm_service_lib/vm_service_lib.dart';
import 'package:vm_service_lib/vm_service_lib_io.dart';

Future<bool> reloadSource(String isolateId, String dillFile) async {
  final String host = "120.0.0.1";
  final int port = 8181;

  var serviceClient = await vmServiceConnect(host, port).catchError((e) {
  });

  ReloadReport report = await serviceClient.reloadSources(isolateId, rootLibUri: dillFile).catchError((e) {
  });
  if (report.success != true) {
    print('ReloadSource:error, err=${report.json}');
  }
  if (report == null) {
    return false;
  }
  return report.success;
}

Future<String> findIsolateIdByName(String isolateName) async {
  final String host = "120.0.0.1";
  final int port = 8181;

  var serviceClient = await vmServiceConnect(host, port).catchError((e) {
    print('findIsolateIdByName:socket connected:$host:$port, err=$e');
  });
  var report = await serviceClient.getVM();
  for (IsolateRef ref in report.isolates) {
    if (ref.name.contains(isolateName)) {
      return ref.id;
    }
  }
  return null;
}

Future<List<String>> findAllHttpIsolateIds() async {
  final String host = "120.0.0.1";
  final int port = 8181;

  var serviceClient = await vmServiceConnect(host, port).catchError((e) {
    print('findAllHttpIsolateIds:socket connected:$host:$port, err=$e');
  });
  var report = await serviceClient.getVM();
  return report.isolates
      .where((IsolateRef ref)=>ref.name.contains('handle'))
      .map((IsolateRef ref)=>ref.id)
      .toList();
}