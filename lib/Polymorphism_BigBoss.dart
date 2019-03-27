
var msg = [2,1.5,0.3];

class BigBoss{

  Set<Employee> employees = new Set<Employee>();

  Set<Employee> getEmployee() => employees;

  void hire(List<Employee> employee){
    if(employee!=null){
      employees.addAll(employee);
    }
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
    return msg[2];
  }

}

void main(){

  BigBoss boss = new BigBoss();

  for(int i=0; i< 100000; i++){
    if(i % 100 == 0){
      boss.hire([new P9()..id  = i .. sallary = 3.5]);
    }
    else if(i % 11 == 0){
      boss.hire([new P8()..id = i ..sallary = 2]);
    }else{
      boss.hire([new PZero()..id = i .. sallary = 0.3]);
    }
  }

  print(boss.calcAll());
}

