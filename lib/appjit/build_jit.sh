#!/usr/bin/env bash
cd ..
echo `pwd`
dart --snapshot-kind=app-jit --snapshot=appjit/snapshot/server.snapshot appjit/stoppable_http_server.dart