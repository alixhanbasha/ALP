/* Interpreter class for ALP
 * Last Edited --> ( Sun Apr 4 2021 )
 * Author --> hashbang404 (Alixhan Basha)
 */
import java.util.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Interpreter implements Statement.Visitor<Void> , Expression.Visitor<Object>{

    private boolean blockMode = false;
    public final Environment globals = new Environment();
    private final Map<Expression, Integer> locals = new HashMap<>();

    private Environment environment = globals;
    public Environment getEnvironment(){ return this.environment; }

    public Interpreter(){ new ALPNativeInterface(globals).BindFunctions(); }

    public void interpret( List<Statement> stats ){
        try{
            for( Statement st : stats ){ execute( st ); }
        }catch( RuntimeError re ){ ALP.runtimeError( re ); }
    }
    public void resolve( Expression e , int depth ){ locals.put( e, depth ); }
    private Object lookUpVariable( Token name, Expression expr ){
        Integer distance = locals.get( expr );
        if( distance != null ) return this.environment.getAt(distance, name.getLexme() );
        else return globals.get( name );
    }

    private void execute( Statement st ){ st.accept(this); }

    @Override
    public Void visitClassStatement( Statement.Class cs ){
        if (cs.superclass != null && cs.name.getLexme().equals(cs.superclass.name.getLexme()))
            throw new RuntimeError(cs.superclass.name, "Nje klase nuk mund te trashegoje veteveten !");

        Object superclass = null;
        if( cs.superclass != null  ){
            superclass = evaluate( cs.superclass );
            if( !(superclass instanceof ALPClass) )
                throw new RuntimeError(
                    cs.superclass.name ,
                    "'" + cs.superclass.name.getLexme() + "' duhet te jete nje superklase !\n" +
                    "Klasa " + cs.name.getLexme() + " mund te trashegon vetem nga klasat!"
                );
        }

        this.environment.define( cs.name.getLexme(), null );

        if( cs.superclass != null ){
            this.environment = new Environment( this.environment );
            environment.define( "super", superclass );
        }

        Map<String, ALPFunction> methods = new HashMap<>();

        for( Statement.Function method : cs.methods ){
            ALPFunction fun = new ALPFunction( method, environment );
            methods.put( method.name.getLexme(), fun );
        }
        ALPClass klasa = new ALPClass( cs.name.getLexme(), (ALPClass)superclass , methods );

        if( cs.superclass != null ) this.environment = this.environment.enclosing;

        this.environment.assign( cs.name, klasa );
        return null;
    }
    @Override
    public Void visitReturnStatement( Statement.Return ret ){
        Object value = null;
        if( ret.value != null ) value = evaluate(ret.value);
        throw new Return( value );
    }
    @Override
    public Void visitFunctionStatement(Statement.Function function){
        ALPFunction f = new ALPFunction( function, this.environment );
        this.environment.define( function.name.getLexme() , f );
        return null;
    }
    @Override
    public Void visitForStatement( Statement.For forStatement ){
        Environment current = new Environment(this.environment);

        this.environment = new Environment( this.environment );
        execute( forStatement.initializer );

        while( isTruthy(evaluate( forStatement.condition )) ){
            executeBlock( forStatement.body , this.environment );
            evaluate( forStatement.increment );
        }
        this.environment = current;
        return null;
    }
    @Override
    public Void visitWhileStatement( Statement.While whileStatement ){
        while( isTruthy(evaluate(whileStatement.condition)) ){
            executeBlock( whileStatement.body , this.environment );
        }
        return null;
    }
    @Override
    public Void visitIfStatement( Statement.If ifstatement ){
        boolean execElse = true;
        if( isTruthy( evaluate(ifstatement.condition) ) ){
            execElse = false;
            executeBlock( ifstatement.thenBranch , this.environment );
        }

        else if( ifstatement.elif.size() > 1 ){
            for( int i=0 ; i<ifstatement.elif.size() ; i+=2 ){
                if( isTruthy( evaluate( (Expression)ifstatement.elif.get(i) ) ) ){
                    execElse = false;
                    executeBlock( (List<Statement>)ifstatement.elif.get(i+1) , this.environment );
                }
            }
        }

        if( !isTruthy(evaluate(ifstatement.condition)) && !ifstatement.elseBranch.isEmpty() && execElse ){
            executeBlock( ifstatement.elseBranch , this.environment );
        }
        return null;
    }
    @Override
    public Void visitBlockStatement( Statement.Block blc ){
        executeBlock( blc.statements , new Environment(this.environment) );
        return null;
    }
    @Override
    public Void visitStmtExpressionStatement( Statement.StmtExpression se ){
        evaluate( se.expr );
        return null;
    }
    @Override
    public Void visitPrintStatement( Statement.Print ps ){
        Object val = evaluate( ps.expr );
        if( ps.println ){ System.out.println( stringify(val) ); }
        else{ System.out.print( stringify(val) ); }
        return null;
    }
    @Override
    public Void visitVarStatement( Statement.Var var ){
        Object value = null;
        if( var.initializer != null ) value = evaluate( var.initializer );
        this.environment.define( var.name.getLexme() , value );
        return null;
    }
    @Override
    public Void visitImportStatement( Statement.Import importer ){
        Object value = evaluate( importer.expr );
        try{
            ALP.runFile( value.toString() );
        }catch(Exception io){ System.out.println( "Moduli per importim nuk u gjet " + io );}
        return null;
    }
    @Override
    public Object visitSuperExpression( Expression.Super se ){
        ALPClass superclass = (ALPClass) this.environment.get( se.keyword );
        ALPInstance obj = (ALPInstance)this.environment.get( "this" );
        ALPFunction method = superclass.findMethod( se.method.getLexme() );
        if( method == null ){
            throw new RuntimeError( se.method , "Metoda" + method.getFunctionName() + "' e padefinuar per superklasen " + superclass.getClassName() );
        }
        return method.bind( obj );
    }
    @Override
    public Object visitThisExpression( Expression.This expr ){
        return this.environment.get( expr.keyword ); // pa resolver
        // return lookUpVariable( expr.keyword, expr ); // me resolver
    }
    @Override
    public Object visitAssignExpression( Expression.Assign ae ){
        Object value = evaluate( ae.value );
        this.environment.assign( ae.name, value );

        //Integer d = locals.get( ae );
        //if( d != null ) this.environment.assignAt( d, ae.name, value );
        //else globals.assign( ae.name, value );

        return value;
    }
    @Override
    public Object visitVariableExpression( Expression.Variable ex ){
        return this.environment.get( ex.name ); // old way ( without resolver / slower )
        //return lookUpVariable( ex.name, ex );
    }
    @Override
    public Object visitLiteralExpression( Expression.Literal le ){
        return le.value;
    }

    @Override
    public Object visitGroupingExpression( Expression.Grouping ge ){
        return evaluate( ge.expr );
    }

    @Override
    public Object visitUnaryExpression( Expression.Unary ue ){
        Object right = evaluate( ue.right );

        switch( ue.operator.getType() ){
        case BANG:
            return !isTruthy( right );
        case MINUS:
            checkNumberOperand( ue.operator , right );
            return -(double)right;
        }

        return null;
    }
    @Override
    public Object visitCallExpression( Expression.Call function ){
        Object callee = evaluate( function.callee );
        List<Object> arguments = new ArrayList<>();

        for( Expression arg : function.arguments ){
            arguments.add(evaluate(arg));
        }

        if( !(callee instanceof ALPCallable) )
            throw new RuntimeError(
                function.paren , "Mund te egzekutohen/thiren vetem klasat dhe funksionet"
            );

        ALPCallable func = (ALPCallable)callee;

        if( arguments.size() != func.ArgumentSize() )
            throw new RuntimeError(
                "Priten "+func.ArgumentSize()+" argumente por jane dhene"+arguments.size()
            );

        return func.FunctionCall(this , arguments);
    }
    @Override
    public Object visitGetExpression( Expression.Get ge ){
        Object obj = evaluate( ge.obj );
        if( obj instanceof ALPInstance )
            return ((ALPInstance) obj).get( ge.name );
        throw new RuntimeError( ge.name , "Vetem instancat e objekteve/klasave kane prona !" );
    }
    @Override
    public Object visitSetExpression( Expression.Set se ){
        Object o = evaluate( se.obj );
        if( !(o instanceof ALPInstance) )
            throw new RuntimeError( se.name , "Vetem instancat e objekteve/klasave mund te kontrollojne pronat !" );

        Object val = evaluate( se.value );
        try{
            Object co = ALPInstance.class.cast(o);
            ((ALPInstance)co).set( se.name, val );
        }catch(ClassCastException ce){ System.err.println( "U shfaq nje problem gjat percaktimit te prones !\t" + ce ); }
        return val;
    }
    @Override
    public Object visitLogicalExpression( Expression.Logical le ){
        Object left = evaluate( le.left );
        if( le.operator.getType() == TokenType.OSE ){
            if( isTruthy(left) )
                return left;
        }else{
            if( !isTruthy(left) )
                return left;
        }
        return evaluate( le.right );
    }
    @Override
    public Object visitBinaryExpression( Expression.Binary be ){
        Object right = evaluate( be.right );
        Object left  = evaluate( be.left  );

        switch( be.operator.getType() ){
        case PLUS:
            if( left instanceof Double && right instanceof Double ) {
                double res = (double)left + (double)right;
                String tst = String.valueOf( res );
                if( tst.endsWith(".0") ){
                    return (int)res;
                }
                return res;
            }
            if( (left instanceof Integer && right instanceof Double) || ( left instanceof Double && right instanceof Integer ) )
                return Double.parseDouble(String.valueOf(left)) + Double.parseDouble(String.valueOf(right));

            if( left instanceof Integer && right instanceof Integer ) return (int)left + (int)right;

            if( left instanceof String && right instanceof String ) return (String)left + (String)right;

            if( left instanceof String && ( right instanceof Double || right instanceof Integer ) ) {
                return (String)left + stringify( right );
            }
            if( (left instanceof Double || left instanceof Integer ) && right instanceof String ) {
                return stringify(left) + (String)right;
            }

            if( (left instanceof Boolean || left instanceof String) && ( right instanceof Boolean || right instanceof String ) ) {
                //String lft = String.valueOf(left.toString());
                //String rgt = String.valueOf(right.toString());
                return (String)stringify(left) + (String)stringify(right);
            }
            throw new RuntimeError( be.operator , "\n\n\033[0;33mOperatoret >> " + left.toString() + ", " + right.toString() + " << duhet te jene numra ose stringje! + \033[0m" );

        case MINUS:
            if( left instanceof Double && right instanceof Double ) {
                double res = Double.parseDouble(String.valueOf(left)) - Double.parseDouble(String.valueOf(right));
                if( String.valueOf(res).endsWith(".0") ){
                    return (int)res;
                }
                return res;
            }
            if( (left instanceof Integer && right instanceof Double) || ( left instanceof Double && right instanceof Integer ) )
                return Double.parseDouble(String.valueOf(left)) - Double.parseDouble(String.valueOf(right));
            if( left instanceof Integer && right instanceof Integer )
                return (int)left - (int)right;

            throw new RuntimeError( be.operator , "\n\n\033[0;33mOperatoret >> " + left.toString() + ", " + right.toString() + " << duhet te jene numra qe te zbriten !\033[0m" );

        case STAR:
            if( left instanceof Double && right instanceof Double ) {
                double res = Double.parseDouble(String.valueOf(left)) * Double.parseDouble(String.valueOf(right));
                if( String.valueOf(res).endsWith(".0") ){
                    return (int)res;
                }
                return res;
            }
            if( (left instanceof Integer && right instanceof Double) || ( left instanceof Double && right instanceof Integer ) )
                return Double.parseDouble(String.valueOf(left)) * Double.parseDouble(String.valueOf(right));
            if( left instanceof Integer && right instanceof Integer )
                return (int)left * (int)right;

            throw new RuntimeError( be.operator , "\n\n\033[0;33mOperatoret >> " + left.toString() + ", " + right.toString() + " << duhet te jene numra qe te shumzohen!\033[0m" );
        case MODULO:
             if( left instanceof Double && right instanceof Double ) {
                double res = Double.parseDouble(String.valueOf(left)) % Double.parseDouble(String.valueOf(right));
                if( String.valueOf(res).endsWith(".0") ){
                    return (int)res;
                }
                return res;
            }
            if( (left instanceof Integer && right instanceof Double) || ( left instanceof Double && right instanceof Integer ) )
                return Double.parseDouble(String.valueOf(left)) % Double.parseDouble(String.valueOf(right));
            if( left instanceof Integer && right instanceof Integer )
                return (int)left % (int)right;

            throw new RuntimeError( be.operator , "\n\n\033[0;33mOperatoret >> " + left.toString() + ", " + right.toString() + " << duhet te jene numra eq te kompletohet modulo!\033[0m" );

        case SLASH:
            checkNumberOperators( be.operator , left , right );
            if( Double.parseDouble( String.valueOf(right) ) == 0 ) throw new RuntimeError( be.operator , "Pjestimi me 0 nuk mund te kalkulohet!" );
            return Double.parseDouble( String.valueOf(left) ) / Double.parseDouble( String.valueOf(right) );
        case GT:
            checkNumberOperators( be.operator , left , right );
            return Double.parseDouble( String.valueOf(left) ) > Double.parseDouble( String.valueOf(right) );
        case GTEQ:
            checkNumberOperators( be.operator , left , right );
            return Double.parseDouble( String.valueOf(left) ) >= Double.parseDouble( String.valueOf(right) );
        case LT:
            checkNumberOperators( be.operator , left , right );
            return Double.parseDouble( String.valueOf(left) ) < Double.parseDouble( String.valueOf(right) );
        case LTEQ:
            checkNumberOperators( be.operator , left , right );
            return Double.parseDouble( String.valueOf(left) ) <= Double.parseDouble( String.valueOf(right) );
        case NOTEQ:
            if( (left instanceof Double && right instanceof Double) || (left instanceof Integer && right instanceof Integer) ){return Double.parseDouble(String.valueOf(left)) != Double.parseDouble(String.valueOf(right));}
            else if( left instanceof String && right instanceof String ){ return !String.valueOf(left).equals( String.valueOf(right) ); }
            else if( left instanceof Boolean && right instanceof Boolean ){ return (boolean)left != (boolean)right; }
            else if( left instanceof Boolean && String.valueOf( right ).equals("vertet") ){ return stringify((boolean)left == (boolean)Boolean.parseBoolean( String.valueOf(right) )); }
            else if( left == null && right == null ){ return stringify( false ); }
            else if( left == null && ( right instanceof String || right instanceof Integer || right instanceof Double || right instanceof Boolean || right instanceof Object ) ){
                return stringify(true);
            }
            else if( (left instanceof String || left instanceof Integer || left instanceof Double || left instanceof Boolean || left instanceof Object ) && right == null ){
                return stringify( left != right );
            }
            else {
                checkNumberOperators( be.operator , left , right );
                return !isEqual( stringify(left) , stringify(right) );
            }
        case EQEQ:
            if( (left instanceof Double && right instanceof Double) || (left instanceof Integer && right instanceof Integer) ){return Double.parseDouble(String.valueOf(left)) == Double.parseDouble(String.valueOf(right));}
            else if( left instanceof String && right instanceof String ){ return String.valueOf(left).equals( String.valueOf(right) ); }
            else if( left instanceof Boolean && right instanceof Boolean ){ return (boolean)left == (boolean)right; }
            else if( left instanceof Boolean && String.valueOf( right ).equals("vertet") ){ return stringify((boolean)left != (boolean)Boolean.parseBoolean( String.valueOf(right) )); }
            else if( left == null && right == null ){ return stringify( true ); }
            else if( left == null && ( right instanceof String || right instanceof Integer || right instanceof Double || right instanceof Boolean || right instanceof Object ) ){
                return stringify(false);
            }
            else if( (left instanceof String || left instanceof Integer || left instanceof Double || left instanceof Boolean || left instanceof Object ) && right == null ){
                return stringify( left == right );
            }
            else {
                checkNumberOperators( be.operator , left , right );
                return isEqual( stringify(left) , stringify(right) );
            }
            //case OSE:
            //return ( (boolean)left || (boolean)right );
        }
        return null;
    }

    /* Helper functions */
    public void executeBlock( List<Statement> statements , Environment env ){
        Environment current = this.environment;
        this.blockMode = true;
        try {
            this.environment = new Environment(env);
            for( Statement s : statements ){
                execute( s );
            }
        } finally {
            this.environment = current;
            this.blockMode = false;
        }
    }

    private Object evaluate( Expression ex ){ return ex.accept(this); }
    private boolean isTruthy( Object obj ){
        if( obj == null ) return false;
        if( obj instanceof Boolean ) return (boolean)obj;
        return true;
    }
    private boolean isEqual( Object a , Object b ){
        if( a==null && b==null ) return false;
        if ( a==null ) return false;
        return a.equals(b);
    }
    private void checkNumberOperand( Token operator , Object operand ){
        if( operand instanceof Double ) return; /* Everything is OK */
        throw new RuntimeError( operator , "\033[0;33" + operand.toString() + " -> duhet te jet numer !\033[0m"  ); /* Everything is NOT OK */
    }
    private void checkNumberOperators( Token operator , Object left , Object right ){
        /* TODO => Give more information about types and why the error occurs  */
        if( (left instanceof Double || left instanceof Integer) && (right instanceof Double || right instanceof Integer) ) return;
        throw new RuntimeError( operator , "\033[0;33mOperatoret >> " + left.toString() + ", " + right.toString() + " << duhet te jene te tipit te njejte !\033[0m" );
    }
    private String stringify( Object obj ){
        if( obj == null ) return "ZBRAZET";
        if( obj instanceof Boolean && (boolean)obj == true ) return "vertet" ;
        if( obj instanceof Boolean && (boolean)obj == false) return "fals"  ;
        if( obj instanceof Double ){
            String txt = obj.toString();
            if( txt.endsWith(".0") ){
                return txt.substring(0 , txt.length()-2);
            }
        }
        return obj.toString();
    }
}
