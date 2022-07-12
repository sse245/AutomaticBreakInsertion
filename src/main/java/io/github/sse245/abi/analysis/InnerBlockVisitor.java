package io.github.sse245.abi.analysis;

import io.github.sse245.abi.OJBaseVisitor;
import io.github.sse245.abi.OJParser;

import java.util.Collection;
import java.util.HashSet;

public class InnerBlockVisitor extends OJBaseVisitor<OJParser.BodyContext> {

    private final Collection<? extends OJParser.BodyContext> blocks;

    public InnerBlockVisitor(Collection<? extends OJParser.BodyContext> blocks) {
        this.blocks = new HashSet<>(blocks);
    }

    @Override
    public OJParser.BodyContext visitBody(OJParser.BodyContext ctx) {
        if (blocks.size() == 1 && blocks.contains(ctx)) {
            return ctx;
        }

        blocks.remove(ctx);

        return this.visitChildren(ctx);
    }

    @Override
    protected OJParser.BodyContext aggregateResult(OJParser.BodyContext aggregate, OJParser.BodyContext nextResult) {
        if (aggregate == null) {
            return nextResult;
        }

        return aggregate;
    }
}
