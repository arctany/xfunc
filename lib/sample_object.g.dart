// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'sample_object.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Citizen _$CitizenFromJson(Map<String, dynamic> json) {
  return Citizen()
    ..idType = json['idType'] as int
    ..name = json['name'] as String
    ..bioTag = json['bioTag'] as String
    ..communicationDetail = json['communicationDetail'] == null
        ? null
        : CommunicationDetail.fromJson(
            json['communicationDetail'] as Map<String, dynamic>)
    ..gener = json['gener'] as int
    ..licenceId = json['licenceId'] as String
    ..insurranceId = json['insurranceId'] as String
    ..id = json['id'] as String
    ..hobby = json['hobby'] as String
    ..weight = (json['weight'] as num)?.toDouble()
    ..height = (json['height'] as num)?.toDouble()
    ..income = (json['income'] as num)?.toDouble()
    ..isMarried = json['isMarried'] as bool
    ..address = json['address'] == null
        ? null
        : Address.fromJson(json['address'] as Map<String, dynamic>)
    ..occupation = json['occupation'] as String
    ..religion = json['religion'] as String
    ..nationality = json['nationality'] as String
    ..education = json['education'] == null
        ? null
        : Education.fromJson(json['education'] as Map<String, dynamic>)
    ..housing = (json['housing'] as List)
        ?.map((e) =>
            e == null ? null : Housing.fromJson(e as Map<String, dynamic>))
        ?.toList()
    ..placeOfBorn = json['placeOfBorn'] == null
        ? null
        : Address.fromJson(json['placeOfBorn'] as Map<String, dynamic>)
    ..birthday = json['birthday'] == null
        ? null
        : DateTime.parse(json['birthday'] as String)
    ..age = json['age'] as int
    ..name1 = json['name1'] as String
    ..isFiltered = json['isFiltered'] as bool
    ..socialCreditIdx = json['socialCreditIdx'] as int
    ..overallBAIndx = json['overallBAIndx'] as int
    ..overallSPCIndx = (json['overallSPCIndx'] as num)?.toDouble()
    ..provenId = (json['provenId'] as num)?.toDouble()
    ..traceId = json['traceId'] as String
    ..registerTime = json['registerTime'] == null
        ? null
        : DateTime.parse(json['registerTime'] as String)
    ..lastLoginTime = json['lastLoginTime'] == null
        ? null
        : DateTime.parse(json['lastLoginTime'] as String)
    ..hasCrimeRecord = json['hasCrimeRecord'] as bool
    ..latestActId = json['latestActId'] as String
    ..financeIds = (json['financeIds'] as Map<String, dynamic>)?.map(
      (k, e) => MapEntry(k, e as String),
    )
    ..sToken = json['sToken'] as String
    ..genKey = json['genKey'] as String
    ..gmtCreated = json['gmtCreated'] == null
        ? null
        : DateTime.parse(json['gmtCreated'] as String)
    ..gmtModified = json['gmtModified'] == null
        ? null
        : DateTime.parse(json['gmtModified'] as String)
    ..status = json['status'] as int
    ..features = (json['features'] as Map<String, dynamic>)?.map(
      (k, e) => MapEntry(k, e as String),
    )
    ..sign = json['sign'] as String
    ..validDate = json['validDate'] == null
        ? null
        : DateTime.parse(json['validDate'] as String)
    ..docId = json['docId'] as int;
}

Map<String, dynamic> _$CitizenToJson(Citizen instance) => <String, dynamic>{
      'idType': instance.idType,
      'name': instance.name,
      'bioTag': instance.bioTag,
      'communicationDetail': instance.communicationDetail,
      'gener': instance.gener,
      'licenceId': instance.licenceId,
      'insurranceId': instance.insurranceId,
      'id': instance.id,
      'hobby': instance.hobby,
      'weight': instance.weight,
      'height': instance.height,
      'income': instance.income,
      'isMarried': instance.isMarried,
      'address': instance.address,
      'occupation': instance.occupation,
      'religion': instance.religion,
      'nationality': instance.nationality,
      'education': instance.education,
      'housing': instance.housing,
      'placeOfBorn': instance.placeOfBorn,
      'birthday': instance.birthday?.toIso8601String(),
      'age': instance.age,
      'name1': instance.name1,
      'isFiltered': instance.isFiltered,
      'socialCreditIdx': instance.socialCreditIdx,
      'overallBAIndx': instance.overallBAIndx,
      'overallSPCIndx': instance.overallSPCIndx,
      'provenId': instance.provenId,
      'traceId': instance.traceId,
      'registerTime': instance.registerTime?.toIso8601String(),
      'lastLoginTime': instance.lastLoginTime?.toIso8601String(),
      'hasCrimeRecord': instance.hasCrimeRecord,
      'latestActId': instance.latestActId,
      'financeIds': instance.financeIds,
      'sToken': instance.sToken,
      'genKey': instance.genKey,
      'gmtCreated': instance.gmtCreated?.toIso8601String(),
      'gmtModified': instance.gmtModified?.toIso8601String(),
      'status': instance.status,
      'features': instance.features,
      'sign': instance.sign,
      'validDate': instance.validDate?.toIso8601String(),
      'docId': instance.docId
    };

Address _$AddressFromJson(Map<String, dynamic> json) {
  return Address()
    ..province = json['province'] as String
    ..detail = (json['detail'] as Map<String, dynamic>)?.map(
      (k, e) => MapEntry(k, e as String),
    )
    ..city = json['city'] as String;
}

Map<String, dynamic> _$AddressToJson(Address instance) => <String, dynamic>{
      'province': instance.province,
      'detail': instance.detail,
      'city': instance.city
    };

CommunicationDetail _$CommunicationDetailFromJson(Map<String, dynamic> json) {
  return CommunicationDetail()
    ..phoneNum = json['phoneNum'] as String
    ..phoneNum2 = json['phoneNum2'] as String
    ..primaryEmailAddress = json['primaryEmailAddress'] as String
    ..secEmailAddress = json['secEmailAddress'] as String
    ..emergencyContacts =
        (json['emergencyContacts'] as List)?.map((e) => e as String)?.toList();
}

Map<String, dynamic> _$CommunicationDetailToJson(
        CommunicationDetail instance) =>
    <String, dynamic>{
      'phoneNum': instance.phoneNum,
      'phoneNum2': instance.phoneNum2,
      'primaryEmailAddress': instance.primaryEmailAddress,
      'secEmailAddress': instance.secEmailAddress,
      'emergencyContacts': instance.emergencyContacts
    };

Housing _$HousingFromJson(Map<String, dynamic> json) {
  return Housing()
    ..location = json['location'] == null
        ? null
        : Address.fromJson(json['location'] as Map<String, dynamic>)
    ..complexDetail = (json['complexDetail'] as Map<String, dynamic>)?.map(
      (k, e) => MapEntry(
          k,
          (e as Map<String, dynamic>)?.map(
            (k, e) => MapEntry(
                k,
                (e as Map<String, dynamic>)?.map(
                  (k, e) => MapEntry(
                      k,
                      (e as Map<String, dynamic>)?.map(
                        (k, e) => MapEntry(
                            k,
                            (e as Map<String, dynamic>)?.map(
                              (k, e) => MapEntry(k, e as String),
                            )),
                      )),
                )),
          )),
    )
    ..rId = json['rId'] as int
    ..price = (json['price'] as num)?.toDouble();
}

Map<String, dynamic> _$HousingToJson(Housing instance) => <String, dynamic>{
      'location': instance.location,
      'complexDetail': instance.complexDetail,
      'rId': instance.rId,
      'price': instance.price
    };

Education _$EducationFromJson(Map<String, dynamic> json) {
  return Education()
    ..degree = json['degree'] as String
    ..graduateDate = json['graduateDate'] == null
        ? null
        : DateTime.parse(json['graduateDate'] as String);
}

Map<String, dynamic> _$EducationToJson(Education instance) => <String, dynamic>{
      'degree': instance.degree,
      'graduateDate': instance.graduateDate?.toIso8601String()
    };
