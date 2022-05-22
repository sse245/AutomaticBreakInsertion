package io.github.sse245.bachelorproject;

import org.antlr.v4.runtime.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a path to the input");
        }

        try {
            CharStream characterStream = CharStreams.fromFileName(args[0]);
            OJLexer lexer = new OJLexer(characterStream);
            OJParser parser = new OJParser(new CommonTokenStream(lexer));

            OJParser.ProgramContext context = parser.program();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
