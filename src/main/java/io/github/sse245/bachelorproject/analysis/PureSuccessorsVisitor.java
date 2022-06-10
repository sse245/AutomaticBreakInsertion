package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;

import java.util.Collection;

public class PureSuccessorsVisitor extends OJBaseVisitor<Boolean> {

    private final OJParser.BodyContext body;

    private final Collection<String> variables;

    private boolean afterBody = false;

    public PureSuccessorsVisitor(OJParser.BodyContext body, Collection<String> variables) {
        this.body = body;
        this.variables = variables;
    }

    @Override
    public Boolean visitBody(OJParser.BodyContext ctx) {
        this.visitChildren(ctx);

        if (this.body == ctx) {
            this.afterBody = true;
        }

        return true;
    }

    @Override
    public Boolean visitInt_assignment(OJParser.Int_assignmentContext ctx) {
        this.visitChildren(ctx);

        return this.afterBody && this.variables.contains(ctx.variable().getText());
    }

    @Override
    public Boolean visitArray_assignment(OJParser.Array_assignmentContext ctx) {
        this.visitChildren(ctx);

        return this.afterBody && this.variables.contains(ctx.variable().getText());
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
