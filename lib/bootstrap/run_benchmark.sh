#!/bin/sh

echo 'Default Setting up'
dart  ../json_conversion.dart

echo 'Tuned Young GC to 1024mb'
dart  --new_gen_semi_initial_size=1024 --new_gen_semi_max_size=1024  json_conversion.dart


#echo 'Tuned Young GC to 1024mb'
#dart  --new_gen_semi_initial_size=1024 --new_gen_semi_max_size=1024  json_conversion.dart