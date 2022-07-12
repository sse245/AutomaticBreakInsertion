package io.github.sse245.abi;

import io.github.sse245.abi.analysis.*;
import io.github.sse245.abi.graph.Graph;
import io.github.sse245.abi.graph.Vertex;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please provide a path to the input");
        }

        if (args.length < 2) {
            System.out.println("Please provide a path to the output");
        }

        try {
            CharStream characterStream = CharStreams.fromFileName(args[0]);
            OJLexer lexer = new OJLexer(characterStream);
            OJParser parser = new OJParser(new CommonTokenStream(lexer));

            OJParser.ProgramContext program = parser.program();

            Map<OJParser.While_statementContext, OJParser.BodyContext> breakStatements = new HashMap<>();

            //copy to mutable set
            Collection<OJParser.While_statementContext> loops = new HashSet<>(new LoopVisitor().visit(program));

            IOVisitor ioVisitor = new IOVisitor();

            loops.removeIf(ioVisitor::visit);

            loops.forEach(loop -> {
                VariableUsageVisitor variableUsageVisitor = new VariableUsageVisitor(loop);

                Collection<String> variables = variableUsageVisitor.visit(program);

                VariableDependencyVisitor variableDependencyVisitor = new VariableDependencyVisitor(variables);

                if (variableDependencyVisitor.visit(loop)) {
                    return;
                }

                VariableGraphVisitor variableGraphVisitor = new VariableGraphVisitor();

                Graph graph = variableGraphVisitor.visit(loop);

                BlockVisitor blockVisitor = new BlockVisitor();

                Collection<? extends OJParser.BodyContext> blocks = new HashSet<>(blockVisitor.visit(loop));
                blocks.remove(loop.body());

                blocks.removeIf(block -> {
                    Collection<String> dependencies = new HashSet<>();

                    for (String variable : variables) {
                        Vertex vertex = graph.getVertex(variable);

                        //in this case the variable is never changed in the loop
                        if (vertex == null) {
                            continue;
                        }

                        dependencies.addAll(graph.getAllVertices(vertex).stream()
                                .map(Vertex::getName)
                                .collect(Collectors.toUnmodifiableSet()));
                    }

                    BlockPurityVisitor blockPurityVisitor = new BlockPurityVisitor(dependencies);

                    return blockPurityVisitor.visit(block);
                });

                boolean succeededByImpure = false;

                for (OJParser.BodyContext block : blocks) {
                    PureSuccessorsVisitor pureSuccessorsVisitor = new PureSuccessorsVisitor(block, variables);

                    succeededByImpure |= !pureSuccessorsVisitor.visit(loop);
                }

                if (succeededByImpure) {
                    return;
                }

                //all these blocks must be nested
                NestedBlocksVisitor nestedBlocksVisitor = new NestedBlocksVisitor(blocks);

                if (!nestedBlocksVisitor.visit(loop)) {
                    return;
                }

                //this loop can have break insertion, inside the most nested block

                if (blocks.isEmpty()) {
                    breakStatements.put(loop, loop.body());
                } else {
                    InnerBlockVisitor innerBlockVisitor = new InnerBlockVisitor(blocks);

                    breakStatements.put(loop, innerBlockVisitor.visit(loop));
                }
            });

            breakStatements.forEach((loop, block) -> {
                System.out.println("A break can be inserted");
                System.out.println(loop.getText());
                System.out.println(block.getText());
            });

            CompilationVisitor compilationVisitor = new CompilationVisitor(breakStatements, args[1]);

            compilationVisitor.visit(program);
            compilationVisitor.build(Path.of(args[1] + ".jar"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
