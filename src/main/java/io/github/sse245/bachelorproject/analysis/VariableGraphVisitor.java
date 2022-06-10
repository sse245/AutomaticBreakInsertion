package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;
import io.github.sse245.bachelorproject.graph.Graph;
import io.github.sse245.bachelorproject.graph.Vertex;

import java.util.Collection;
import java.util.HashSet;

public class VariableGraphVisitor extends OJBaseVisitor<Graph> {

    private final Graph graph = new Graph();

    private final Collection<String> variables = new HashSet<>();

    @Override
    public Graph visitInt_assignment(OJParser.Int_assignmentContext ctx) {
        this.visit(ctx.variable());

        variables.clear();

        Vertex source = this.graph.getVertex(ctx.variable().getText());

        if (source == null) {
            source = this.graph.addVertex(ctx.variable().getText());
        }

        this.visit(ctx.expression());

        for (String variable : variables) {
            Vertex target = this.graph.getVertex(variable);

            if (target == null) {
                target = this.graph.addVertex(variable);
            }

            source.addEdge(target);
        }

        return defaultResult();
    }

    @Override
    public Graph visitArray_assignment(OJParser.Array_assignmentContext ctx) {
        this.visit(ctx.variable());

        variables.clear();

        Vertex source = this.graph.getVertex(ctx.variable().getText());

        if (source == null) {
            source = this.graph.addVertex(ctx.variable().getText());
        }

        this.visit(ctx.expression(0));
        this.visit(ctx.expression(1));

        for (String variable : variables) {
            Vertex target = this.graph.getVertex(variable);

            if (target == null) {
                target = this.graph.addVertex(ctx.variable().getText());
            }

            source.addEdge(target);
        }

        return defaultResult();
    }

    @Override
    public Graph visitVariable(OJParser.VariableContext ctx) {
        variables.add(ctx.getText());

        return this.visitChildren(ctx);
    }

    @Override
    protected Graph aggregateResult(Graph aggregate, Graph nextResult) {
        return defaultResult();
    }

    @Override
    protected Graph defaultResult() {
        return this.graph;
    }
}
