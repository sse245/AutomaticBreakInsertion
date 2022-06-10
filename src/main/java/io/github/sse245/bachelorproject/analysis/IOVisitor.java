package io.github.sse245.bachelorproject.analysis;

import io.github.sse245.bachelorproject.OJBaseVisitor;
import io.github.sse245.bachelorproject.OJParser;

public class IOVisitor extends OJBaseVisitor<Boolean> {

    @Override
    public Boolean visitInput_expression(OJParser.Input_expressionContext ctx) {
        return true;
    }

    @Override
    public Boolean visitOutput_statement(OJParser.Output_statementContext ctx) {
        return true;
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
