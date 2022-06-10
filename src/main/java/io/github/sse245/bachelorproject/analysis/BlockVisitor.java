package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlockVisitor extends OJBaseVisitor<Collection<? extends OJParser.BodyContext>> {

    @Override
    public Collection<? extends OJParser.BodyContext> visitBody(OJParser.BodyContext ctx) {
        return aggregateResult(this.visitChildren(ctx), Collections.singleton(ctx));
    }

    @Override
    protected Collection<? extends OJParser.BodyContext> aggregateResult(Collection<? extends OJParser.BodyContext> aggregate, Collection<? extends OJParser.BodyContext> nextResult) {
        return Stream.concat(aggregate.stream(), nextResult.stream()).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected Collection<? extends OJParser.BodyContext> defaultResult() {
        return new HashSet<>();
    }
}
