
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Polymorphism_BigBoss{

    Set<Employee> employees = new HashSet<Employee>();

    Set<Employee> getEmployee() {
        return employees;
    }

    void hire(List<Employee> employee){
        if(employee!=null){
            employees.addAll(employee);
        }
    }

    double calcAll() {
        return cal(getEmployee());
    }

    double cal(Set<Employee> employee){
        double total = 0;
        for(Employee e : employee){
            total = total + e.inc();
        }
        return total;
    }

    public static void main(String[] args){

        Polymorphism_BigBoss boss = new Polymorphism_BigBoss();

        for(int i=0; i< 100000; i++){
            if(i % 100 == 0){
                P9 p9 = new P9();
                p9.id = i;
                List<Employee> l = new ArrayList<Employee>();
                l.add(p9);
                boss.hire(l);
            }
            else if(i % 11 == 0){
                P8 p8 = new P8();
                p8.id = i;
                List<Employee> l = new ArrayList<Employee>();
                l.add(p8);
                boss.hire(l);
            }else{
                PZero pZero = new PZero();
                pZero.id = i;
                List<Employee> l = new ArrayList<Employee>();
                l.add(pZero);
                boss.hire(l);
            }
        }
        System.out.println(boss.calcAll());
    }

}

abstract class Employee{
    int id;

    double sallary;

    abstract double inc();
}

class P8 extends Employee{

    @Override
    double inc() {
        // TODO: implement spank
        return 2;
    }

}

class P9 extends Employee{
    @Override
    double inc() {
        // TODO: implement spank
        return 3.5;
    }

}

class PZero extends Employee{

    @Override
    double inc() {
        // TODO: implement spank
        return 0.3;
    }
}





