#!/bin/bash

rm -rf workdir;
mkdir -p workdir;
cp lib/javassist.jar workdir/;
cp tracer.jar workdir/;

for TEST in $(ls tests); do
    echo "Executing $TEST ...";

    cp tests/$TEST/$TEST.java workdir/;
    cp tests/$TEST/$TEST.out workdir/;
    cd workdir;

    javac -cp .:javassist.jar:tracer.jar $TEST.java > /dev/null 2>&1;

    java -cp .:javassist.jar:tracer.jar ist.meic.pa.TraceVM $TEST >& $TEST.created;

    DIFF=$(diff $TEST.out $TEST.created);
    if [ -n "$DIFF" ]; then
        echo "[FAILED] $TEST";
    else
        echo "[OK] $TEST";
    fi

    cd ..;
    echo "-------------------";
done

rm -rf workdir;
