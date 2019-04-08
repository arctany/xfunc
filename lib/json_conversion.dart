import 'package:benchmark_harness/benchmark_harness.dart';
import 'sample_object.dart';
import 'dart:convert';


Citizen citizens;

class JsonSerializationBenchmark extends BenchmarkBase{


  const JsonSerializationBenchmark() : super("JsonSerializationBenchmark");

  // The benchmark code.
  void run() {
    //Object to Json string
    jsonEncode(citizens.toJson());
  }


  // Not measured: setup code executed before the benchmark runs.
  void setup() {
    citizens = getRandomCitizens(1)[0];
  }

  // Not measured: teardown code executed after the benchmark runs.
  void teardown() {
    citizens = null;
  }

  static void main() {
    new JsonSerializationBenchmark().report();
  }

}

//Des
class JsonDeSerializationBenchmark extends BenchmarkBase{
  const JsonDeSerializationBenchmark() : super("JsonDeSerializationBenchmark");
}



main(){
  JsonSerializationBenchmark.main();
}



