# MPJ-Verifier

Model checking of unmodified MPI Java programs with Java Pathfinder (JPF).

The verifier replaces the MPJ Express communication library with a proxy model in which every MPI process runs as a thread of one Java program and the network is a shared message queue. A JPF listener controls the scheduling of these threads so that every feasible matching of receives to sends is explored, and reports

- message orders that lead to different program results,
- sends that are never consumed and receives that can never be matched,
- deadlocks: circular waits on receives, deadlocks caused by wildcard receives, and cycles of unmatched blocking sends (unsafe sends).

The companion paper is "Verifying Non-Deterministic Behavior and Detecting Errors in MPI Java Programs using Software Model Checking" by Muhammad Sohaib Ayub.

## Layout

| Folder | Content |
|---|---|
| `proxy/mpi` | the proxy model of the mpiJava API (`MPI`, `Intracomm`, `Status`, `Datatype`, message queue classes) |
| `launcher` | `MPJ_Express_Model` (creates one thread per rank) and `Threads` (loads the program by reflection) |
| `listener` | `MPJ_Verifier`, the JPF listener with scheduling, message counting and deadlock checks; `MPJ_Verifier_Deadlock`, the stand alone send cycle check |
| `supportingClasses` | state tree and helper classes used by the listener |
| `examples` | every program used in the evaluation |
| `config` | an example `.jpf` file and the shell script used to run the experiments |
| `results` | the JPF output of every run reported in the paper (listener trace lines removed) |

## Requirements

- JPF core version 8 (the 2014 release, `v8.0 rev 2+`)
- Java 8 (JPF 8 does not run on later Java versions)

## Building

1. Copy `listener/*.java` into `jpf-core/src/main/gov/nasa/jpf/listener/` and `supportingClasses/*.java` into `jpf-core/src/main/gov/nasa/jpf/supportingClasses/`, then build jpf-core (`ant build`).
2. Compile the proxy, the launcher and the program under test together, with the proxy first on the classpath so that `import mpi.*` resolves to the model:

   ```
   javac -d bin proxy/mpi/*.java launcher/*.java examples/RunningAverage.java
   ```

## Running

Create a `.jpf` file such as `config/example.jpf`:

```
target = MPJ_Express_Model
target.args = 9,RunningAverage      # number of processes, class name of the program
classpath = /path/to/bin
listener = gov.nasa.jpf.listener.MPJ_Verifier
```

and run

```
java -jar jpf-core/build/RunJPF.jar config/example.jpf
```

JPF prints the program output for every explored order, followed by its own statistics (states, end states, time, memory). Deadlocks are reported as `Deadlock` break transitions or as `Deadlock (unsafe send): cycle of blocked sends ...`; an end state with unconsumed messages is reported with their number.

`config/runexp.sh <Class> <processes> <listener> [timeout]` runs one configuration and prints the summary lines used in the paper.

## Supported subset of the API

Blocking point to point operations (`Send`, `Recv`) with `ANY_SOURCE`, tags, and the primitive and object data types. Collective operations, non blocking operations and probes are not modeled.

## License

Apache License 2.0, the same as JPF.
