/*
* This file is auto generated ASTgen script , dont mess with it.
* created ( Mon Aug 10 2020 )
* last edited ( Sat Sep 19 2020 )
* author Alixhan Basha
*/
import java.util.List;
public abstract class Statement{
    public interface Visitor<R> {
        public R visitStmtExpressionStatement(StmtExpression statement);
        public R visitBlockStatement(Block statement);
        public R visitClassStatement(Class statement);
        public R visitFunctionStatement(Function statement);
        public R visitIfStatement(If statement);
        public R visitReturnStatement(Return statement);
        public R visitPrintStatement(Print statement);
        public R visitImportStatement(Import statement);
        public R visitVarStatement(Var statement);
        public R visitWhileStatement(While statement);
        public R visitForStatement(For statement);
    }

    public abstract <R> R accept( Visitor<R> visitor );

    public static class StmtExpression extends Statement {
        public StmtExpression ( Expression expr ) {
            this.expr = expr;
        }
        public final Expression expr;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitStmtExpressionStatement(this);
        }
    }


    public static class Block extends Statement {
        public Block ( List<Statement> statements ) {
            this.statements = statements;
        }
        public final List<Statement> statements;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitBlockStatement(this);
        }
    }


    public static class Class extends Statement {
        public Class ( Token name, Expression.Variable superclass, List<Statement.Function> methods ) {
            this.name = name;
            this.superclass = superclass;
            this.methods = methods;
        }
        public final Token name;
        public final Expression.Variable superclass;
        public final List<Statement.Function> methods;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitClassStatement(this);
        }
    }


    public static class Function extends Statement {
        public Function ( Token name, List<Token> params, List<Statement> body ) {
            this.name = name;
            this.params = params;
            this.body = body;
        }
        public final Token name;
        public final List<Token> params;
        public final List<Statement> body;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitFunctionStatement(this);
        }
    }


    public static class If extends Statement {
        public If ( Expression condition, List<Statement> thenBranch, List<Object> elif, List<Statement> elseBranch ) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elif = elif;
            this.elseBranch = elseBranch;
        }
        public final Expression condition;
        public final List<Statement> thenBranch;
        public final List<Object> elif;
        public final List<Statement> elseBranch;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitIfStatement(this);
        }
    }


    public static class Return extends Statement {
        public Return ( Token keyword, Expression value ) {
            this.keyword = keyword;
            this.value = value;
        }
        public final Token keyword;
        public final Expression value;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitReturnStatement(this);
        }
    }


    public static class Print extends Statement {
        public Print ( Expression expr, boolean println ) {
            this.expr = expr;
            this.println = println;
        }
        public final Expression expr;
        public final boolean println;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitPrintStatement(this);
        }
    }


    public static class Import extends Statement {
        public Import ( Expression expr ) {
            this.expr = expr;
        }
        public final Expression expr;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitImportStatement(this);
        }
    }


    public static class Var extends Statement {
        public Var ( Token name, Expression initializer ) {
            this.name = name;
            this.initializer = initializer;
        }
        public final Token name;
        public final Expression initializer;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitVarStatement(this);
        }
    }


    public static class While extends Statement {
        public While ( Expression condition, List<Statement> body ) {
            this.condition = condition;
            this.body = body;
        }
        public final Expression condition;
        public final List<Statement> body;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitWhileStatement(this);
        }
    }


    public static class For extends Statement {
        public For ( Statement initializer, Expression condition, Expression increment, List<Statement> body ) {
            this.initializer = initializer;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
        }
        public final Statement initializer;
        public final Expression condition;
        public final Expression increment;
        public final List<Statement> body;

        @Override
        public <R> R accept( Visitor<R> visitor ){
            return visitor.visitForStatement(this);
        }
    }


}
