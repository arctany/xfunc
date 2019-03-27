#!/usr/bin/env bash

begin=`gdate +%s%N`

dart server.snapshot.kernel


end=`gdate +%s%N`


diff1=`expr $end - $begin`

echo $delta1

begin=`gdate +%s%N`

dart server.snapshot

end=`gdate +%s%N`

diff2=`expr $end - $begin`

echo $delta2

echo 'delta' `expr $diff2 - $diff1` 'ns'