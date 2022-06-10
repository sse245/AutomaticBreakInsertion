package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoopVisitor extends OJBaseVisitor<Collection<OJParser.While_statementContext>> {

    @Override
    public Collection<OJParser.While_statementContext> visitWhile_statement(OJParser.While_statementContext ctx) {
        return aggregateResult(this.visitChildren(ctx), Collections.singleton(ctx));
    }

    @Override
    protected Collection<OJParser.While_statementContext> aggregateResult(Collection<OJParser.While_statementContext> aggregate, Collection<OJParser.While_statementContext> nextResult) {
        return Stream.concat(aggregate.stream(), nextResult.stream()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected Collection<OJParser.While_statementContext> defaultResult() {
        return Collections.emptySet();
    }
}
