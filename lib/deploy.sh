#!/usr/bin/env bash

source $DART_SDK/sdk/pkg/front_end/tool/fasta compile \
 --single-root-base=/Users/haowu/IdeaProjects/xfunc/hot \
 --single-root-scheme=hot \
 --omit-platform \
 -o lib/intermediate/handler.dill  lib/hot-ext/handler.dart