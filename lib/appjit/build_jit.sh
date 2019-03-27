#!/usr/bin/env bash
cd ..
echo `pwd`
dart --optimization-counter-threshold=10 --no-background-compilation --snapshot-kind=app-jit --snapshot=appjit/snapshot/server.snapshot appjit/stoppable_http_server.dart --training=true
