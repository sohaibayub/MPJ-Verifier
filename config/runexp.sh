#!/bin/bash
# usage: runexp.sh <Class> <np> <listener> [timeout]
CLS=$1; NP=$2; LST=$3; TO=${4:-170}
J8=$HOME/jpfrun/jdk8u432-b06/bin/java
cat > exp/run_${CLS}_${NP}.jpf <<EOT
target = MPJ_Express_Model
target.args = ${NP},${CLS}
classpath = $HOME/jpfrun/exp/bin
listener = gov.nasa.jpf.listener.${LST}
report.console.property_violation=error,statistics
EOT
START=$(date +%s.%N)
timeout $TO $J8 -Xmx3g -jar jc/build/RunJPF.jar exp/run_${CLS}_${NP}.jpf > exp/out_${CLS}_${NP}_${LST}.txt 2>&1
RC=$?
END=$(date +%s.%N)
echo "== $CLS np=$NP listener=$LST rc=$RC wall=$(echo "$END - $START" | bc) s"
grep -E "^(elapsed time|states|search|choice generators|max memory|instructions):" exp/out_${CLS}_${NP}_${LST}.txt
grep -E "Deadlock|Error -|no errors|ORDER|Final Average" exp/out_${CLS}_${NP}_${LST}.txt | sort | uniq -c | sort -rn | head -8
