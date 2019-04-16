#!/bin/sh


INTV=$2

echo `date +%s%m`
if [ $1 == '--appjit' ]; then

  dart  server.snapshot $2

fi


if [$1 == '--k']; then
  dart stoppable_http_server.dart $2
fi



