package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;
import io.github.sse245.bachelorproject.graph.Graph;
import io.github.sse245.bachelorproject.graph.Vertex;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class VariableDependencyVisitor extends OJBaseVisitor<Boolean> {

    private final Graph graph = new Graph();

    private final Collection<String> variables = new HashSet<>();

    // variables we're actually interested in
    private final Collection<String> usefulVariables;

    // variables that should no longer receive an update
    private final Collection<String> doNotUpdate = new HashSet<>();

    public VariableDependencyVisitor(Collection<String> usefulVariables) {
        this.usefulVariables = usefulVariables;
    }

    @Override
    public Boolean visitInt_assignment(OJParser.Int_assignmentContext ctx) {
        this.visit(ctx.variable());

        variables.clear();

        if (doNotUpdate.contains(ctx.variable().getText())) {
            return true;
        }

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

        if (usefulVariables.contains(ctx.variable().getText())) {
            if (this.graph.hasInfiniteWalk(source)) {
                return true;
            }

            final Vertex finalSource = source;
            this.doNotUpdate.addAll(this.graph.getAllVertices(source).stream()
                    .filter(vertex -> vertex != finalSource)
                    .map(Vertex::getName)
                    .collect(Collectors.toUnmodifiableSet()));
        }

        return defaultResult();
    }

    @Override
    public Boolean visitArray_assignment(OJParser.Array_assignmentContext ctx) {
        this.visit(ctx.variable());

        variables.clear();

        if (doNotUpdate.contains(ctx.variable().getText())) {
            return true;
        }

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

        if (usefulVariables.contains(ctx.variable().getText())) {
            if (this.graph.hasInfiniteWalk(source)) {
                return true;
            }

            final Vertex finalSource = source;
            this.doNotUpdate.addAll(this.graph.getAllVertices(source).stream()
                    .filter(vertex -> vertex != finalSource)
                    .map(Vertex::getName)
                    .collect(Collectors.toUnmodifiableSet()));
        }

        return defaultResult();
    }

    @Override
    public Boolean visitVariable(OJParser.VariableContext ctx) {
        variables.add(ctx.getText());

        return this.visitChildren(ctx);
    }

    @Override
    protected Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
        return aggregate || nextResult;
    }

    @Override
    protected Boolean defaultResult() {
        return false;
    }
}
