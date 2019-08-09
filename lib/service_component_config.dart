import 'dart:convert';
import 'dart:io';

import 'package:json_annotation/json_annotation.dart';
part 'service_component_config.g.dart';


@JsonSerializable()
class ServiceConfig{

  String _type;
  List<ServiceEntry> _entries;

  factory ServiceConfig.fromJson(Map<String, dynamic> json) => _$ServiceConfigFromJson(json);

  Map<String,dynamic> toJson()=> _$ServiceConfigToJson(this);

  ServiceConfig(){}

  String get type => _type;

  set type(String value) {
    _type = value;
  }

  List<ServiceEntry> get entries => _entries;

  set entries(List<ServiceEntry> value) {
    _entries = value;
  }

  @override
  String toString()=> jsonEncode(toJson());


}

@JsonSerializable()
class ServiceEntry{
  static const String defaultEnv = 'default';
  String _name;
  Map<String,Map<String,String>> _env;
  Map<String,String> getDefault() => _env?? _env[defaultEnv];
  String get name => _name;

  ServiceEntry(){}

  factory ServiceEntry.fromJson(Map<String, dynamic> json) => _$ServiceEntryFromJson(json);

  Map<String,dynamic> toJson()=> _$ServiceEntryToJson(this);

  set name(String value) {
    _name = value;
  }

  Map<String, Map<String, String>> get env => _env;

  set env(Map<String, Map<String, String>> value) {
    _env = value;
  }

  @override
  String toString() => jsonEncode(toJson());

}

main(){

  Map<String,dynamic> json = jsonDecode(new File('/Users/haowu/IdeaProjects/xfunc/lib/middleware.json').readAsStringSync());

//  json.forEach((key,value){
//    config[key] =  new ServiceConfig.fromJson(value);
//
//  });
//
//  print((config['kvstore'].entries[0] as ServiceEntry));

}
