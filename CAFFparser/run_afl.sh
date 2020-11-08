#!/bin/sh
MAKEFILE=Makefile_afl

make -f ${MAKEFILE} clean
make -f ${MAKEFILE}

echo core | sudo tee -a /proc/sys/kernel/core_pattern

# place CAFF file in inputs folder
afl-fuzz -i inputs -o out ./CAFFparserBin @@ out