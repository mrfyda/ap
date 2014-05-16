#!/bin/bash

rm -rf workdir;

echo "Compiling tracer ...";
ant clean > /dev/null 2>&1;
ant > /dev/null 2>&1;

mkdir -p workdir;
cp lib/javassist.jar workdir/;
cp tracer.jar workdir/;

for TRACE in $(ls tests); do
    for TEST in $(ls tests/$TRACE); do
        echo "Executing $TEST ...";

        cp -f tests/$TRACE/$TEST/$TEST.java workdir/;
        cp -f tests/$TRACE/$TEST/$TEST.out workdir/;
        cd workdir;

        javac -cp .:javassist.jar:tracer.jar $TEST.java > /dev/null 2>&1;

        java -cp .:javassist.jar:tracer.jar ist.meic.pa.$TRACE $TEST >& $TEST.created;

        DIFF=$(diff $TEST.out $TEST.created);
        if [ "$DIFF" != "" ]; then
            echo "[FAILED] $TEST";
        else
            echo "[OK] $TEST";
        fi

        cd ..;
        echo "-------------------";
    done
done

rm -rf workdir;
