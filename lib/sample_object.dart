import 'dart:convert';
import 'package:json_annotation/json_annotation.dart';
import 'dart:math';
import 'package:random_string/random_string.dart';

part 'sample_object.g.dart';

@JsonSerializable()
class Citizen{
  String _id;
  String _name;
  String _name1;
  int _gener;
  int _age;
  DateTime _birthday;
  Address _placeOfBorn;
  List<Housing> _housing = <Housing>[];
  Education _education;
  String   _nationality;
  String   _religion;
  String   _occupation;
  Address  _address;
  bool    _isMarried;
  double _income;
  double _height;
  double _weight;
  String _bioTag;
  bool   _isFiltered;
  int    _socialCreditIdx;
  int    _overallBAIndx;
  double _overallSPCIndx;
  double _provenId;
  String _hobby;
  String _insurranceId;
  String _licenceId;
  String _traceId;
  DateTime _registerTime;
  DateTime _lastLoginTime;
  bool _hasCrimeRecord;
  String _latestActId;
  Map<String,String> _financeIds;
  String _sToken;
  String _genKey;
  DateTime _gmtCreated;
  DateTime _gmtModified;
  int _status;
  Map<String,String> _features;
  CommunicationDetail _communicationDetail;
  int _idType;
  String _sign;
  DateTime _validDate;
  int _docId;

  Citizen(){}


  int get idType => _idType;

  set idType(int value) {
    _idType = value;
  }

  String get name => _name;
  String get bioTag => _bioTag;

  set bioTag(String value) {
    _bioTag = value;
  }

  set name(String value) {
    _name = value;
  }


  CommunicationDetail get communicationDetail => _communicationDetail;

  set communicationDetail(CommunicationDetail value) {
    _communicationDetail = value;
  }

  int get gener => _gener;

  String get licenceId => _licenceId;

  set licenceId(String value) {
    _licenceId = value;
  }

  String get insurranceId => _insurranceId;

  set insurranceId(String value) {
    _insurranceId = value;
  }

  String get id => _id;

  set id(String value) {
    _id = value;
  }

  String get hobby => _hobby;

  set hobby(String value) {
    _hobby = value;
  }

  double get weight => _weight;

  set weight(double value) {
    _weight = value;
  }

  double get height => _height;

  set height(double value) {
    _height = value;
  }

  double get income => _income;

  set income(double value) {
    _income = value;
  }

  bool get isMarried => _isMarried;

  set isMarried(bool value) {
    _isMarried = value;
  }

  Address get address => _address;

  set address(Address value) {
    _address = value;
  }

  String get occupation => _occupation;

  set occupation(String value) {
    _occupation = value;
  }

  String get religion => _religion;

  set religion(String value) {
    _religion = value;
  }

  String get nationality => _nationality;

  set nationality(String value) {
    _nationality = value;
  }

  Education get education => _education;

  set education(Education value) {
    _education = value;
  }

  List<Housing> get housing => _housing;

  set housing(List<Housing> value) {
    _housing = value;
  }

  Address get placeOfBorn => _placeOfBorn;

  set placeOfBorn(Address value) {
    _placeOfBorn = value;
  }

  DateTime get birthday => _birthday;

  set birthday(DateTime value) {
    _birthday = value;
  }

  int get age => _age;

  set age(int value) {
    _age = value;
  }

  set gener(int value) {
    _gener = value;
  }

  String get name1 => _name1;

  set name1(String value) {
    _name1 = value;
  }

  factory Citizen.fromJson(Map<String, dynamic> json) => _$CitizenFromJson(json);
//
  Map<String, dynamic> toJson() => _$CitizenToJson(this);

  bool get isFiltered => _isFiltered;

  set isFiltered(bool value) {
    _isFiltered = value;
  }

  int get socialCreditIdx => _socialCreditIdx;

  set socialCreditIdx(int value) {
    _socialCreditIdx = value;
  }

  int get overallBAIndx => _overallBAIndx;

  set overallBAIndx(int value) {
    _overallBAIndx = value;
  }

  double get overallSPCIndx => _overallSPCIndx;

  set overallSPCIndx(double value) {
    _overallSPCIndx = value;
  }

  double get provenId => _provenId;

  set provenId(double value) {
    _provenId = value;
  }

  String get traceId => _traceId;

  set traceId(String value) {
    _traceId = value;
  }

  DateTime get registerTime => _registerTime;

  set registerTime(DateTime value) {
    _registerTime = value;
  }

  DateTime get lastLoginTime => _lastLoginTime;

  set lastLoginTime(DateTime value) {
    _lastLoginTime = value;
  }

  bool get hasCrimeRecord => _hasCrimeRecord;

  set hasCrimeRecord(bool value) {
    _hasCrimeRecord = value;
  }

  String get latestActId => _latestActId;

  set latestActId(String value) {
    _latestActId = value;
  }

  Map<String, String> get financeIds => _financeIds;

  set financeIds(Map<String, String> value) {
    _financeIds = value;
  }

  String get sToken => _sToken;

  set sToken(String value) {
    _sToken = value;
  }

  String get genKey => _genKey;

  set genKey(String value) {
    _genKey = value;
  }

  DateTime get gmtCreated => _gmtCreated;

  set gmtCreated(DateTime value) {
    _gmtCreated = value;
  }

  DateTime get gmtModified => _gmtModified;

  set gmtModified(DateTime value) {
    _gmtModified = value;
  }

  int get status => _status;

  set status(int value) {
    _status = value;
  }

  Map<String, String> get features => _features;

  set features(Map<String, String> value) {
    _features = value;
  }

  String get sign => _sign;

  set sign(String value) {
    _sign = value;
  }

  DateTime get validDate => _validDate;

  set validDate(DateTime value) {
    _validDate = value;
  }

  int get docId => _docId;

  set docId(int value) {
    _docId = value;
  }
}

@JsonSerializable()
class Address{
  String _city;
  String _province;
  Map<String,String> _detail = <String,String>{};

  Address(){}

  String get province => _province;

  set province(String value) {
    _province = value;
  }

  Map<String, String> get detail => _detail;

  set detail(Map<String, String> value) {
    _detail = value;
  }

  String get city => _city;

  set city(String value) {
    _city = value;
  }

  factory Address.fromJson(Map<String, dynamic> json) => _$AddressFromJson(json);
//
  Map<String, dynamic> toJson() => _$AddressToJson(this);


}

@JsonSerializable()
class CommunicationDetail{
  String _phoneNum;
  String _phoneNum2;
  String _primaryEmailAddress;
  String _secEmailAddress;
  List<String> _emergencyContacts = <String>[];
  Map<String,String> __socialNetworkAccountIds;

  CommunicationDetail(){}

  String get phoneNum => _phoneNum;

  set phoneNum(String value) {
    _phoneNum = value;
  }

  String get phoneNum2 => _phoneNum2;

  set phoneNum2(String value) {
    _phoneNum2 = value;
  }

  String get primaryEmailAddress => _primaryEmailAddress;

  set primaryEmailAddress(String value) {
    _primaryEmailAddress = value;
  }

  String get secEmailAddress => _secEmailAddress;

  set secEmailAddress(String value) {
    _secEmailAddress = value;
  }

  List<String> get emergencyContacts => _emergencyContacts;

  set emergencyContacts(List<String> value) {
    _emergencyContacts = value;
  }

  Map<String, String> get _socialNetworkAccountIds => __socialNetworkAccountIds;

  set _socialNetworkAccountIds(Map<String, String> value) {
    __socialNetworkAccountIds = value;
  }

  factory CommunicationDetail.fromJson(Map<String, dynamic> json) => _$CommunicationDetailFromJson(json);
////
  Map<String, dynamic> toJson() => _$CommunicationDetailToJson(this);
}

@JsonSerializable()
class Housing{
  int _rId;
  Address _location;
  double  _price;
  Address get location => _location;
  Map<String,Map<String,Map<String,Map<String,Map<String,String>>>>> _complexDetail;
  Housing(){

  }

  set location(Address value) {
    _location = value;
  }

  Map<String,
      Map<String,
          Map<String, Map<String, Map<String, String>>>>> get complexDetail =>
      _complexDetail;

  set complexDetail(Map<String,
      Map<String, Map<String, Map<String, Map<String, String>>>>> value) {
    _complexDetail = value;
  }

  int get rId => _rId;

  set rId(int value) {
    _rId = value;
  }

  double get price => _price;

  set price(double value) {
    _price = value;
  }

  factory Housing.fromJson(Map<String, dynamic> json) => _$HousingFromJson(json);

  Map<String, dynamic> toJson() => _$HousingToJson(this);
}

@JsonSerializable()
class Education{
  String _degree;
  DateTime _graduateDate;
  String get degree => _degree;
  Education(){}

  set degree(String value) {
    _degree = value;
  }

  DateTime get graduateDate => _graduateDate;

  set graduateDate(DateTime value) {
    _graduateDate = value;
  }

  factory Education.fromJson(Map<String, dynamic> json) => _$EducationFromJson(json);
//
  Map<String, dynamic> toJson() => _$EducationToJson(this);
}


List<String> degrees = <String>['master','bachelor','doctor','elem','highschool','etc'];


genRandomMap(int entries){
  Map<String,String> map = <String,String>{};
  for(int i=0; i<entries; i++){
    map.putIfAbsent(randomAlpha(randomBetween(1, 5)), ()=>randomAlpha(20));
  }
  return map;
}

List<Citizen> getRandomCitizens(int a){

  List<Citizen> r = <Citizen>[];

  for(int i=0; i<a ; i++){
    Citizen c = new Citizen();
    Education e = new Education();
    e.degree = degrees[a%5];
    e.graduateDate = new DateTime.now();

    var ran = new Random(100);
    c.income= ran.nextDouble();
    c.education=e;
    c.address=new Address()
      ..city=randomAlpha(3)..detail=genRandomMap(10);
    r.add(c);

    c.age=ran.nextInt(90);
    c.bioTag=randomAlphaNumeric(30);
    c.height = ran.nextDouble();
    c.weight = ran.nextDouble();
    c.hobby = randomAlpha(20);
    c.birthday = new DateTime.now();
    c.features=genRandomMap(5);
    c.financeIds = genRandomMap(10);
    c.traceId = randomAlpha(30);
    c.status = 1;
    c.socialCreditIdx = ran.nextInt(10000);
    c.sToken = randomAlpha(20);
    c.religion = randomAlpha(2);
    c.registerTime = new DateTime.now();
    c.provenId = ran.nextDouble();
    c.placeOfBorn = new Address()
      ..city=randomAlpha(3)..detail=genRandomMap(10)..province=randomAlpha(3);
    c.overallSPCIndx= ran.nextDouble();
    c.overallBAIndx = randomBetween(10, 100000);
    c.occupation = randomString(5);
    c.nationality = randomAlpha(3);
    c.name1 = randomAlpha(10);
    c.name = randomAlpha(10);
    c.licenceId = randomAlpha(20);
    c.latestActId = randomAlpha(20);
    c.lastLoginTime = new DateTime.now();
    c.isMarried = ran.nextBool();
    c.isFiltered = ran.nextBool();
    c.insurranceId = randomAlpha(20);
    c.id = randomAlpha(20);
    c.gmtModified = new DateTime.now();
    c.gmtCreated = new DateTime.now();
    c.genKey = randomAlpha(50);
    c.gener = i%1;
    c.sign = randomAlpha(30);
    c.idType = randomBetween(1, 5);
    c.communicationDetail =  new CommunicationDetail()..__socialNetworkAccountIds={
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
      randomAlpha(6):randomAlphaNumeric(20),
    }..emergencyContacts=[randomAlpha(20),randomAlpha(20),randomAlpha(20),randomAlpha(20)]
      ..phoneNum = randomAlphaNumeric(11).._phoneNum2=randomAlphaNumeric(20)
      .._primaryEmailAddress = '${randomString(6)}@${randomAlpha(9)}'..secEmailAddress
      ='${randomString(6)}@${randomAlpha(9)}';
    c.validDate = new DateTime.now();
    c.docId = ran.nextInt(999999999);
    List<Housing> hs = <Housing>[];
    for(int j=0; j<3; j++){
      hs.add(new Housing()..location=new Address()..rId= i%3..price=ran.nextDouble()
      ..complexDetail = {
        randomAlpha(3): {randomAlpha(3):{randomAlpha(3):{randomAlpha(3):genRandomMap(10)}}},
        randomAlpha(3): {randomAlpha(3):{randomAlpha(3):{randomAlpha(3):genRandomMap(10)}}},
        randomAlpha(3): {randomAlpha(3):{randomAlpha(3):{randomAlpha(3):genRandomMap(10)}}},
        randomAlpha(3): {randomAlpha(3):{randomAlpha(3):{randomAlpha(3):genRandomMap(10)}}},
        randomAlpha(3): {randomAlpha(3):{randomAlpha(3):{randomAlpha(3):genRandomMap(10)}}},
        randomAlpha(3): {randomAlpha(3):{randomAlpha(3):{randomAlpha(3):genRandomMap(10)}}},
        randomAlpha(3): {randomAlpha(3):{randomAlpha(3):{randomAlpha(3):genRandomMap(10)}}}
      }
      );
    }
    c.housing = hs;
  }
  return r;

}



main(){
  print(jsonEncode(getRandomCitizens(1)[0].toJson()));
  Citizen c= Citizen.fromJson(jsonDecode(jsonEncode(getRandomCitizens(1)[0].toJson())));
  print(c.housing);

}