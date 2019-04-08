#!/usr/bin/env bash
cd ..
echo `pwd`

dart --snapshot-kind=app-jit --snapshot=appjit/snapshot/server.snapshot --optimization-counter-threshold=1 appjit/int2double.dart
dart --snapshot-kind=kernel --snapshot=appjit/snapshot/server.snapshot.kernel appjit/int2double.dart
dart --optimization-counter-threshold=10 --no-background-compilation --snapshot-kind=app-jit --snapshot=appjit/snapshot/server.snapshot appjit/stoppable_http_server.dart --training=true
