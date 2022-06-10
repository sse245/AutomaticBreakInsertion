package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;

import java.util.Collection;
import java.util.HashSet;

public class NestedBlocksVisitor extends OJBaseVisitor<Boolean> {

    private final Collection<? extends OJParser.BodyContext> blocks;

    public NestedBlocksVisitor(Collection<? extends OJParser.BodyContext> blocks) {
        this.blocks = new HashSet<>(blocks);
    }

    @Override
    public Boolean visitBody(OJParser.BodyContext ctx) {
        if (blocks.contains(ctx)) {
            blocks.remove(ctx);

            this.visitChildren(ctx);

            return blocks.isEmpty();
        }

        return this.visitChildren(ctx);
    }

    @Override
    protected Boolean aggregateResult(Boolean aggregate, Boolean nextResult) {
        return aggregate && nextResult;
    }

    @Override
    protected Boolean defaultResult() {
        return true;
    }
}
