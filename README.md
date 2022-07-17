# Automatic break insertion

Automatic break insertion is a compiler technique that can automatically insert break statements in loops without
altering the actual behaviour of the program. This repository contains an implementation of this optimization for an
extended version of the Orange Juice language.

## Building from source
The program can be built from source by exeucting the following commands:
```shell
mvn clean antlr4:antlr4
mvn clean package
```

A pre-built jar is available under Releases.

## Running the program
Running the program can be done with the following command:
```shell
java -jar AutomaticBreakInsertion-0.1.0.jar <input.oj> <output>
```
This will read the OJ file from `<input.oj>` and write the compiled and optimized JAR file to `<output>.jar`. The
`<output>.jar` can then be run with the following command:
```shell
java -jar <output>.jar
```

AutomaticBreakInsertion can only be run with Java 17 or higher. The generated `<output>.jar` can be run with Java 5 or
higher.
