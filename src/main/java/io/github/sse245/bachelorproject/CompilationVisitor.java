package io.github.sse245.bachelorproject;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class CompilationVisitor extends OJBaseVisitor<Void> {

    private final ClassWriter writer;
    private final MethodVisitor mainVisitor;

    private final List<String> variableIndices = new ArrayList<>();

    private final Label endLabel = new Label();

    public CompilationVisitor() {
        writer = new ClassWriter(0);

        writer.visit(Opcodes.V1_6,
                Opcodes.ACC_PRIVATE,
                "Test",
                null,
                "java/lang/Object",
                null);

        MethodVisitor mv = writer.visitMethod(Opcodes.ACC_PUBLIC,
                "<init>",
                "()V",
                null,
                null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        mainVisitor = writer.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null);
    }

    @Override
    public Void visitInteger(OJParser.IntegerContext ctx) {
        mainVisitor.visitIntInsn(Opcodes.SIPUSH, Short.parseShort(ctx.getText()));

        return null;
    }

    @Override
    public Void visitFactor(OJParser.FactorContext ctx) {
        if (ctx.variable() != null) {
            this.visit(ctx.variable());
        } else if (ctx.integer() != null) {
            this.visit(ctx.integer());
        } else if (ctx.expression() != null) {
            this.visit(ctx.expression());
        } else if (ctx.input_expression() != null) {
            this.visit(ctx.input_expression());
        } else {
            this.visit(ctx.array_load());
        }

        if (ctx.variable() != null) {
            mainVisitor.visitVarInsn(Opcodes.ILOAD, variableIndices.indexOf(ctx.getText()));
        }

        return null;
    }

    @Override
    public Void visitOutput_statement(OJParser.Output_statementContext ctx) {
        System.out.println("Enter output");

        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System",
                "out",
                "Ljava/io/PrintStream;");

        if (ctx.expression() != null) {
            this.visit(ctx.expression());
        }

        System.out.println("Exit output");

        String signature = "I";

        if (ctx.STRING() != null) {
            mainVisitor.visitLdcInsn(trimDoubleQuotes(ctx.STRING().getText()));

            signature = "Ljava/lang/String;";
        }

        mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/io/PrintStream",
                "print",
                "(" + signature + ")V");

        return null;
    }

    @Override
    public Void visitExpression_ext(OJParser.Expression_extContext ctx) {
        this.visit(ctx.add_operator());
        this.visit(ctx.term());

        System.out.println("Exit expression");

        String text = ctx.add_operator().getText();

        if ("+".equals(text)) {
            mainVisitor.visitInsn(Opcodes.IADD);
        } else if ("-".equals(text)) {
            mainVisitor.visitInsn(Opcodes.ISUB);
        } else {
            throw new RuntimeException("Invalid text for add operator");
        }

        return null;
    }

    @Override
    public Void visitTerm_ext(OJParser.Term_extContext ctx) {
        this.visit(ctx.mult_operator());
        this.visit(ctx.factor());

        String text = ctx.mult_operator().getText();

        if ("*".equals(text)) {
            mainVisitor.visitInsn(Opcodes.IMUL);
        } else if ("/".equals(text)) {
            mainVisitor.visitInsn(Opcodes.IDIV);
        } else if ("%".equals(text)) {
            mainVisitor.visitInsn(Opcodes.IREM);
        } else {
            throw new RuntimeException("Invalid text for mult operator");
        }

        return null;
    }

    @Override
    public Void visitInt_declaration(OJParser.Int_declarationContext ctx) {
        this.visit(ctx.variable());

        String name = ctx.variable().getText();
        Label label = new Label();

        mainVisitor.visitLocalVariable(name,
                "I",
                null,
                label,
                endLabel,
                variableIndices.size());
        mainVisitor.visitLabel(label);
        mainVisitor.visitLdcInsn(0);
        mainVisitor.visitVarInsn(Opcodes.ISTORE, variableIndices.size());

        variableIndices.add(name);

        return null;
    }

    @Override
    public Void visitArray_declaration(OJParser.Array_declarationContext ctx) {
        System.out.println("Array declaration");

        this.visit(ctx.variable());

        String name = ctx.variable().getText();
        Label label = new Label();

        mainVisitor.visitLocalVariable(name,
                "[I",
                null,
                label,
                endLabel,
                variableIndices.size());

        System.out.println("Variable size: " + variableIndices.size());

        mainVisitor.visitLabel(label);

        this.visit(ctx.expression());

        mainVisitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT);
        mainVisitor.visitVarInsn(Opcodes.ASTORE, variableIndices.size());

        variableIndices.add(name);

        return null;
    }

    @Override
    public Void visitInt_assignment(OJParser.Int_assignmentContext ctx) {
        this.visit(ctx.variable());
        this.visit(ctx.expression());

        int index = variableIndices.indexOf(ctx.variable().getText());

        if (index == -1) {
            throw new RuntimeException("Var doesn't exist");
        }

        mainVisitor.visitVarInsn(Opcodes.ISTORE, index);

        return null;
    }

    @Override
    public Void visitArray_assignment(OJParser.Array_assignmentContext ctx) {
        System.out.println("Array assignment");

        this.visit(ctx.variable());

        int index = variableIndices.indexOf(ctx.variable().getText());

        System.out.println("Index: " + index);

        if (index == -1) {
            throw new RuntimeException("Var doesn't exist");
        }

        System.out.println("Writing aload");
        mainVisitor.visitVarInsn(Opcodes.ALOAD, index);

        this.visit(ctx.expression(0));
        this.visit(ctx.expression(1));

        mainVisitor.visitInsn(Opcodes.IASTORE);

        return null;
    }

    @Override
    public Void visitArray_load(OJParser.Array_loadContext ctx) {
        System.out.println("Array load");

        this.visit(ctx.variable());

        int index = variableIndices.indexOf(ctx.variable().getText());

        if (index == -1) {
            throw new RuntimeException("Var doesn't exist");
        }

        mainVisitor.visitVarInsn(Opcodes.ALOAD, index);

        this.visit(ctx.expression());

        mainVisitor.visitInsn(Opcodes.IALOAD);

        return null;
    }

    @Override
    public Void visitInput_expression(OJParser.Input_expressionContext ctx) {
        mainVisitor.visitTypeInsn(Opcodes.NEW, "java/util/Scanner");
        mainVisitor.visitInsn(Opcodes.DUP);
        mainVisitor.visitFieldInsn(Opcodes.GETSTATIC,
                "java/lang/System",
                "in",
                "Ljava/io/InputStream;");
        mainVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                "java/util/Scanner",
                "<init>",
                "(Ljava/io/InputStream;)V");
        mainVisitor.visitInsn(Opcodes.DUP);

        Label start = new Label();
        Label end = new Label();

        mainVisitor.visitLocalVariable("$",
                "Ljava/util/Scanner;",
                null,
                start,
                end,
                variableIndices.size());
        mainVisitor.visitLabel(start);
        mainVisitor.visitVarInsn(Opcodes.ASTORE, variableIndices.size());
        mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/util/Scanner",
                "nextInt",
                "()I");
        mainVisitor.visitVarInsn(Opcodes.ALOAD, variableIndices.size());
        mainVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/util/Scanner",
                "close",
                "()V");
        mainVisitor.visitLabel(end);

        return null;
    }

    @Override
    public Void visitWhile_statement(OJParser.While_statementContext ctx) {
        Label startLabel = new Label();

        mainVisitor.visitLabel(startLabel);

        this.visit(ctx.condition());

        int opcode = switch (ctx.condition().relation().getText()) {
            case "==" -> Opcodes.IF_ICMPNE;
            case "!=" -> Opcodes.IF_ICMPEQ;
            case "<" -> Opcodes.IF_ICMPGE;
            case "<=" -> Opcodes.IF_ICMPGT;
            case ">" -> Opcodes.IF_ICMPLE;
            case ">=" -> Opcodes.IF_ICMPLT;
            default -> throw new RuntimeException("Invalid relation");
        };

        Label endLabel = new Label();

        mainVisitor.visitJumpInsn(opcode, endLabel);

        this.visit(ctx.body());

        mainVisitor.visitJumpInsn(Opcodes.GOTO, startLabel);
        mainVisitor.visitLabel(endLabel);

        return null;
    }

    @Override
    public Void visitIf_statement(OJParser.If_statementContext ctx) {
        this.visit(ctx.condition());

        int opcode = switch (ctx.condition().relation().getText()) {
            case "==" -> Opcodes.IF_ICMPNE;
            case "!=" -> Opcodes.IF_ICMPEQ;
            case "<" -> Opcodes.IF_ICMPGE;
            case "<=" -> Opcodes.IF_ICMPGT;
            case ">" -> Opcodes.IF_ICMPLE;
            case ">=" -> Opcodes.IF_ICMPLT;
            default -> throw new RuntimeException("Invalid relation");
        };

        Label elseLabel = new Label();
        Label endLabel = new Label();

        mainVisitor.visitJumpInsn(opcode, elseLabel);

        this.visit(ctx.body(0));

        mainVisitor.visitJumpInsn(Opcodes.GOTO, endLabel);
        mainVisitor.visitLabel(elseLabel);

        this.visit(ctx.body(1));

        mainVisitor.visitLabel(endLabel);

        return null;
    }

    public void build(Path path) {
        mainVisitor.visitLabel(endLabel);
        mainVisitor.visitInsn(Opcodes.RETURN);
        mainVisitor.visitMaxs(10000, 10000);
        mainVisitor.visitEnd();

        writer.visitEnd();

        try {
            Files.write(path, writer.toByteArray(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String trimDoubleQuotes(String string) {
        if (string.length() == 0) {
            return string;
        }

        String output = string;
        char firstCharacter = output.charAt(0);

        if (firstCharacter == '"') {
            output = output.substring(1);
        }

        if (output.length() == 0) {
            return output;
        }

        char lastCharacter = output.charAt(output.length() - 1);

        if (lastCharacter == '"') {
            output = output.substring(0, output.length() - 1);
        }

        return output;
    }
}
