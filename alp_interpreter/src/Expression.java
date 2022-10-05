/*
 * This file is auto generated ASTgen script , dont mess with it.
 * created ( Mon Aug 10 2020 )
 * last edited ( Sat Sep 19 2020 )
 * author Alixhan Basha
 */

import java.util.List;

public abstract class Expression {
	public interface Visitor<R> {
		public R visitAssignExpression(Assign expression);

		public R visitBinaryExpression(Binary expression);

		public R visitUnaryExpression(Unary expression);

		public R visitLogicalExpression(Logical expression);

		public R visitLiteralExpression(Literal expression);

		public R visitCallExpression(Call expression);

		public R visitGetExpression(Get expression);

		public R visitSetExpression(Set expression);

		public R visitSuperExpression(Super expression);

		public R visitThisExpression(This expression);

		public R visitGroupingExpression(Grouping expression);

		public R visitVariableExpression(Variable expression);
	}

	public abstract <R> R accept(Visitor<R> visitor);

	public static class Assign extends Expression {
		public Assign(Token name, Expression value) {
			this.name = name;
			this.value = value;
		}

		public final Token name;
		public final Expression value;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignExpression(this);
		}
	}


	public static class Binary extends Expression {
		public Binary(Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		public final Expression left;
		public final Token operator;
		public final Expression right;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpression(this);
		}
	}


	public static class Unary extends Expression {
		public Unary(Token operator, Expression right) {
			this.operator = operator;
			this.right = right;
		}

		public final Token operator;
		public final Expression right;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpression(this);
		}
	}


	public static class Logical extends Expression {
		public Logical(Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		public final Expression left;
		public final Token operator;
		public final Expression right;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLogicalExpression(this);
		}
	}


	public static class Literal extends Expression {
		public Literal(Object value) {
			this.value = value;
		}

		public final Object value;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpression(this);
		}
	}


	public static class Call extends Expression {
		public Call(Expression callee, Token paren, List<Expression> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		public final Expression callee;
		public final Token paren;
		public final List<Expression> arguments;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitCallExpression(this);
		}
	}


	public static class Get extends Expression {
		public Get(Expression obj, Token name) {
			this.obj = obj;
			this.name = name;
		}

		public final Expression obj;
		public final Token name;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitGetExpression(this);
		}
	}


	public static class Set extends Expression {
		public Set(Expression obj, Token name, Expression value) {
			this.obj = obj;
			this.name = name;
			this.value = value;
		}

		public final Expression obj;
		public final Token name;
		public final Expression value;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitSetExpression(this);
		}
	}


	public static class Super extends Expression {
		public Super(Token keyword, Token method) {
			this.keyword = keyword;
			this.method = method;
		}

		public final Token keyword;
		public final Token method;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitSuperExpression(this);
		}
	}


	public static class This extends Expression {
		public This(Token keyword) {
			this.keyword = keyword;
		}

		public final Token keyword;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitThisExpression(this);
		}
	}


	public static class Grouping extends Expression {
		public Grouping(Expression expr) {
			this.expr = expr;
		}

		public final Expression expr;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpression(this);
		}
	}


	public static class Variable extends Expression {
		public Variable(Token name) {
			this.name = name;
		}

		public final Token name;

		@Override
		public <R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableExpression(this);
		}
	}


}
