
var msg = [2,1.5,0.3,1.99];

class BigBoss{

  int numOfEmployee = 100;

  BigBoss({int numOfEmployee : 100}){
    this.numOfEmployee = numOfEmployee;
  }

  Set<Employee> employees = new Set<Employee>();

  Set<Employee> getEmployee() => employees;

  void hire(List<Employee> employee){

    if(employee!=null && getEmployee().length >= numOfEmployee){
      fire();
    }
    employees.addAll(employee);
  }

  void fire(){
//    print(
//      'fire cause total ${getEmployee().length} of  ${numOfEmployee}'
//    );
    employees.remove(employees.first);

  }

  num calcAll() {
    return cal(getEmployee());
  }

  num cal(Set<Employee> employee){

    num total = 0;
    employee.forEach((e){
      if(employees.contains(e)){
        total = total + e.inc();
      }
    });
    return total;
  }
}

abstract class Employee{

  int id;

  num sallary;

  inc();
}

class P8 extends Employee{

  @override
  dynamic inc() {
    // TODO: implement spank
    return msg[0];
  }

}

class P9 extends Employee{
  @override
  dynamic inc() {
    // TODO: implement spank
    return msg[1];
  }

}

class PZero extends Employee{

  @override
  dynamic inc() {
    // TODO: implement spank
    return msg[2]* msg[0];
  }

}


class P7 extends PZero{

  @override
  dynamic inc() {
    // TODO: implement spank
    return msg[1]* msg[0] * 0.9 * msg[2];
  }

}


class P6 extends Employee{

  @override
  dynamic inc() {
    // TODO: implement spank
    return msg[1] * msg[2];
  }

}


num moke_hire(BigBoss boss, int type, int id){

  switch (type){
    case 1 :  boss.hire([new P9()..id  = id .. sallary = 3.5 * msg[1]]); break;
    case 2 :  boss.hire([new P8()..id  = id .. sallary = 2 * msg[2]]); break;
    case 3 :  boss.hire([new PZero()..id  = id .. sallary = 0.3 * msg[3]]); break;
    case 4 :  boss.hire([new P6()..id  = id .. sallary = msg[1] * msg[3]]); break;
    default : boss.hire([new P7()..id  = id .. sallary = msg[3]]); break;
    break;
  }
}


void main(){

  BigBoss boss = new BigBoss();

  for(int i=0 ;  i< 1000 ; i++){
    moke_hire(boss, i%5 , i);
  }

  print(boss.employees.length);
  print(boss.calcAll());

//  BigBoss boss = new BigBoss();
//
//  for(int i=0; i< 1000; i++){
//    if(i % 100 == 0){
//      boss.hire([new P9()..id  = i .. sallary = 3.5]);
//    }
//    else if(i % 11 == 0){
//      boss.hire([new P8()..id = i ..sallary = 2]);
//    }else{
//      boss.hire([new PZero()..id = i .. sallary = 0.3]);
//    }
//  }

//  print(boss.calcAll());
}

