package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;

import java.util.Collection;

public class BlockPurityVisitor extends OJBaseVisitor<Boolean> {

    private final Collection<String> noUpdates;

    public BlockPurityVisitor(Collection<String> noUpdates) {
        this.noUpdates = noUpdates;
    }

    @Override
    public Boolean visitInt_assignment(OJParser.Int_assignmentContext ctx) {
        this.visitChildren(ctx);

        return !this.noUpdates.contains(ctx.variable().getText());
    }

    @Override
    public Boolean visitArray_assignment(OJParser.Array_assignmentContext ctx) {
        this.visitChildren(ctx);

        return !this.noUpdates.contains(ctx.variable().getText());
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
