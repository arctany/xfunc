// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'service_component_config.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ServiceConfig _$ServiceConfigFromJson(Map<String, dynamic> json) {
  return ServiceConfig()
    ..type = json['type'] as String
    ..entries = (json['entries'] as List)
        ?.map((e) =>
            e == null ? null : ServiceEntry.fromJson(e as Map<String, dynamic>))
        ?.toList();
}

Map<String, dynamic> _$ServiceConfigToJson(ServiceConfig instance) =>
    <String, dynamic>{'type': instance.type, 'entries': instance.entries};

ServiceEntry _$ServiceEntryFromJson(Map<String, dynamic> json) {
  return ServiceEntry()
    ..name = json['name'] as String
    ..env = (json['env'] as Map<String, dynamic>)?.map(
      (k, e) => MapEntry(
          k,
          (e as Map<String, dynamic>)?.map(
            (k, e) => MapEntry(k, e as String),
          )),
    );
}

Map<String, dynamic> _$ServiceEntryToJson(ServiceEntry instance) =>
    <String, dynamic>{'name': instance.name, 'env': instance.env};
