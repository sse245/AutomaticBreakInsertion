package io.github.sse245.abi.analysis;

import io.github.sse245.abi.OJBaseVisitor;
import io.github.sse245.abi.OJParser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VariableUsageVisitor extends OJBaseVisitor<Collection<String>> {

    private final OJParser.While_statementContext loop;

    private boolean foundLoop = false;

    // When a variable is assigned to, before being used, then we don't care about it
    private final Collection<String> overridden = new HashSet<>();

    public VariableUsageVisitor(OJParser.While_statementContext loop) {
        this.loop = loop;
    }

    @Override
    public Collection<String> visitVariable(OJParser.VariableContext ctx) {
        if (foundLoop && !overridden.contains(ctx.getText())) {
            return Collections.singleton(ctx.getText());
        }

        return defaultResult();
    }

    @Override
    public Collection<String> visitInt_assignment(OJParser.Int_assignmentContext ctx) {
        Collection<String> variables = this.visit(ctx.expression());

        if (foundLoop) {
            overridden.add(ctx.variable().getText());
        }

        return variables;
    }

    @Override
    public Collection<String> visitWhile_statement(OJParser.While_statementContext ctx) {
        Collection<String> variables = this.visitChildren(ctx);

        if (this.loop == ctx) {
            foundLoop = true;
        }

        return variables;
    }

    @Override
    protected Collection<String> aggregateResult(Collection<String> aggregate, Collection<String> nextResult) {
        return Stream.concat(aggregate.stream(), nextResult.stream()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected Collection<String> defaultResult() {
        return Collections.emptySet();
    }
}
