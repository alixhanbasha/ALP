/*
 * Resolver Class for ALPlang
 * author hashbang404 (@alixhanbasha)
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


public class Resolver implements Statement.Visitor<Void>, Expression.Visitor<Void> {

	private enum FunctionType {NONE, FUNCTION, METHOD}

	private enum ClassType {NONE, CLASS}

	private final Interpreter interpreter;

	private FunctionType currentFunction = FunctionType.NONE;
	private ClassType currentClass = ClassType.NONE;

	private final Stack<Map<String, Boolean>> scopes = new Stack<>();

	public Resolver(Interpreter i) {
		this.interpreter = i;
	}

	/* Helpers */
	private void beginScope() {
		scopes.push(new HashMap<String, Boolean>());
	}

	private void endScope() {
		scopes.pop();
	}

	public void resolve(List<Statement> statements) {
		for (Statement s : statements) {
			resolve(s);
		}
	}

	public void resolveObjects(List<Object> obj) {
		for (Object s : obj) {
			resolve((Statement) s);
		}
	} // for elseif

	private void resolve(Statement s) {
		s.accept(this);
	}

	private void resolve(Expression e) {
		e.accept(this);
	}

	private void declare(Token name) {
		if (scopes.isEmpty()) return;
		Map<String, Boolean> scope = scopes.peek();
		scope.put(name.getLexme(), false);
	}

	private void define(Token name) {
		if (scopes.isEmpty()) return;
		scopes.peek().put(name.getLexme(), true);
	}

	private void resolveLocal(Expression expr, Token name) {
		for (int i = scopes.size() - 1; i >= 0; i--) {
			if (scopes.get(i).containsKey(name.getLexme())) {
				interpreter.resolve(expr, scopes.size() - 1 - i);
				return;
			}
		}
	}

	private void resolveFunction(Statement.Function fun, FunctionType ft) {
		FunctionType type = currentFunction;
		currentFunction = ft;
		beginScope();
		for (Token param : fun.params) {
			declare(param);
			define(param);
		}
		resolve(fun.body);
		endScope();
		currentFunction = type;
	}

	/* Statement Visitor implementation */
	@Override
	public Void visitStmtExpressionStatement(Statement.StmtExpression s) {
		resolve(s.expr);
		return null;
	}

	@Override
	public Void visitBlockStatement(Statement.Block s) {
		beginScope();
		resolve(s.statements);
		endScope();
		return null;
	}

	@Override
	public Void visitFunctionStatement(Statement.Function s) {
		declare(s.name);
		define(s.name);
		resolveFunction(s, FunctionType.FUNCTION);
		return null;
	}

	@Override
	public Void visitClassStatement(Statement.Class cs) {
		ClassType thisClass = currentClass;
		currentClass = ClassType.CLASS;

		define(cs.name);
		declare(cs.name);

		if (cs.superclass != null &&
				cs.name.getLexme().equals(cs.superclass.name.getLexme())) {
			ALP.error(cs.superclass.name, "Nje klase nuk mund te trashegoj veteveten !");
		}

		if (cs.superclass != null) resolve(cs.superclass);
		if (cs.superclass != null) {
			beginScope();
			scopes.peek().put("super", true);
		}

		beginScope();
		scopes.peek().put("this", true);

		for (Statement.Function method : cs.methods) {
			FunctionType declaration = FunctionType.METHOD;
			resolveFunction(method, declaration);
		}

		endScope();
		if (cs.superclass != null) endScope();
		currentClass = thisClass;
		return null;
	}

	@Override
	public Void visitIfStatement(Statement.If s) {

		beginScope();
		resolve(s.condition);
		resolve(s.thenBranch);
		endScope();

		if (s.elif != null) {
			beginScope();
			resolveObjects(s.elif);
			endScope();
		}
		if (s.elseBranch != null) {
			beginScope();
			resolve(s.elseBranch);
			endScope();
		}
		return null;
	}

	@Override
	public Void visitReturnStatement(Statement.Return s) {
		if (currentFunction == FunctionType.NONE) {
			ALP.error(s.keyword, "Kthimi i rezultatit nuk ka kuptim ne kete faze !");
		}
		if (s.value != null) resolve(s.value);
		return null;
	}

	@Override
	public Void visitPrintStatement(Statement.Print s) {
		resolve(s.expr);
		return null;
	}

	@Override
	public Void visitImportStatement(Statement.Import s) {
		resolve(s.expr);
		return null;
	}

	@Override
	public Void visitVarStatement(Statement.Var s) {
		declare(s.name);
		if (s.initializer != null) resolve(s.initializer);
		define(s.name);
		return null;
	}

	@Override
	public Void visitWhileStatement(Statement.While s) {
		resolve(s.condition);
		beginScope();
		resolve(s.body);
		endScope();
		return null;
	}

	@Override
	public Void visitForStatement(Statement.For s) {
		beginScope();
		resolve(s.initializer);
		resolve(s.condition);
		resolve(s.increment);

		beginScope();
		resolve(s.body);
		endScope();

		endScope();
		return null;
	}


	/* Expression Visitor implementation */
	@Override
	public Void visitSuperExpression(Expression.Super se) {
		resolveLocal(se, se.keyword);
		return null;
	}

	@Override
	public Void visitThisExpression(Expression.This t) {
		if (currentClass == ClassType.NONE) {
			ALP.error(t.keyword, t.keyword.getLexme() + " nuk mund te perdoret jashte metodave te klasave !");
			return null;
		}
		resolveLocal(t, t.keyword);
		return null;
	}

	@Override
	public Void visitAssignExpression(Expression.Assign e) {
		resolve(e.value);
		resolveLocal(e, e.name);
		return null;
	}

	@Override
	public Void visitGetExpression(Expression.Get ge) {
		resolve(ge.obj);
		return null;
	}

	@Override
	public Void visitSetExpression(Expression.Set se) {
		resolve(se.value);
		resolve(se.obj);
		return null;
	}

	@Override
	public Void visitBinaryExpression(Expression.Binary e) {
		resolve(e.left);
		resolve(e.right);
		return null;
	}

	@Override
	public Void visitUnaryExpression(Expression.Unary e) {
		resolve(e.right);
		return null;
	}

	@Override
	public Void visitLogicalExpression(Expression.Logical e) {
		resolve(e.left);
		resolve(e.right);
		return null;
	}

	@Override
	public Void visitLiteralExpression(Expression.Literal e) {
		return null;
	}

	@Override
	public Void visitCallExpression(Expression.Call e) {
		resolve(e.callee);
		for (Expression arg : e.arguments) {
			resolve(arg);
		}
		return null;
	}

	@Override
	public Void visitGroupingExpression(Expression.Grouping e) {
		resolve(e.expr);
		return null;
	}

	@Override
	public Void visitVariableExpression(Expression.Variable e) {
		if (!scopes.isEmpty() && scopes.peek().get(e.name.getLexme()) == Boolean.FALSE)
			ALP.error(e.name, "Nuk mund te lexohet variabla lokale ne inicializuesin e vete! ");
		resolveLocal(e, e.name);
		return null;
	}

}
