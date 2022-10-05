import java.util.ArrayList;
import java.util.List;

/* Parser class for ALP
 * created --> ( Tue Sep 15 2020 )
 * lastEdited --> ( Thu Sep 17 2020 )
 * author --> cantbebothered ( Alixhan Basha )
 */
public class Parser {
	// Like the lexer, but instead of taking characters, take the tokens

	private static class ParseError extends RuntimeException {
	}

	private final List<Token> tokens;
	private int current = 0; /* Counter intuitively points to the "next" Token */
	private boolean varflag = false;

	public Parser(List<Token> t) {
		tokens = t;
	}

	public List<Statement> parse() {
		List<Statement> statements = new ArrayList<>();
		while (!isAtFileEnd()) {
			statements.add(declaration());
		}
		return statements;
	}

	private Statement declaration() {
		try {
			if (match(TokenType.FUNKSIONI)) return funcDeclaration("funksion");
			if (match(TokenType.VAR)) return varDeclaration();
			if (match(TokenType.KLASA)) return classDeclaration();
			return statement();
		} catch (ParseError pe) {
			synchronize();
			return null;
		}
	}

	private Statement classDeclaration() {
		Token name = consume(TokenType.IDENTIFIER, "Deklarimi i klases duhet te kete nje emer !");
		Expression.Variable superclass = null;
		if (match(TokenType.TRASHEGON, TokenType.LT)) { // trashogon ose <
			consume(TokenType.IDENTIFIER, "Duhet te ceket emri i klases nga i cili klasa " + name.getLexme() + " do te trashegon !");
			superclass = new Expression.Variable(previous());
		}
		consume(TokenType.LEFT_BRACE, "Pritet qe te hapet kllapa gjarperore '{' pas deklarimit te klases '" + name.getLexme() + "' ");

		List<Statement.Function> methods = new ArrayList<>();
		while (!check(TokenType.RIGHT_BRACE) && !isAtFileEnd()) {
			methods.add(funcDeclaration("metode"));
		}
		consume(TokenType.RIGHT_BRACE, "Pritet qe te mbyllet kllapa gjarperore '}' pas deklarimit te klases '" + name.getLexme() + "' ");

		return new Statement.Class(name, superclass, methods);

	}

	private Statement.Function funcDeclaration(String type) {
		Token name = consume(TokenType.IDENTIFIER, "Pritet qe definimi i " + type + " te ket nje emer!");
		consume(TokenType.LEFT_PAREN, "Pritet qe te hapet kllapa '(' ne funksionin '\033[0;34m" + name.getLexme() + "\033[0m'");
		List<Token> params = new ArrayList<>();
		if (!check(TokenType.RIGHT_PAREN)) {
			do {
				if (params.size() >= 255)
					error(peek(), "Funksioni '" + name.getLexme() + "' nuk mund te ket me shume se 255 parametra !");
				params.add(consume(TokenType.IDENTIFIER, "Pritet emri i parametrit !"));
			} while (match(TokenType.COMMA));
		}
		consume(TokenType.RIGHT_PAREN, "Pritet qe te mbyllet kllapa ')' ne " + type + " " + name.getLexme());
		consume(TokenType.LEFT_BRACE, "Pritet qe te haper kllapa '{' per trupin e " + type + " " + name.getLexme());
		List<Statement> body = block();
		return new Statement.Function(name, params, body);
	}

	private Statement statement() {
		if (match(TokenType.NESE)) return ifStatement();
		if (match(TokenType.PERDERISA)) return whileStatement();
		if (match(TokenType.SHKRUAJ)) return printStatement(false);
		if (match(TokenType.SHKRUAJR)) return printStatement(true);
		if (match(TokenType.IMPORTO)) return importStatement();
		if (match(TokenType.LEFT_BRACE)) return new Statement.Block(block());
		if (match(TokenType.PER)) return forStatement();
		if (match(TokenType.KTHEN)) return returnStatement();
		return expressionStatement();
	}

	private Statement returnStatement() {
		Token name = previous();
		Expression value = null;
		if (!check(TokenType.SEMICOLON)) {
			value = expression();
		}
		consume(TokenType.SEMICOLON, "Pritet ';' ne fund te 'kthen' ! ");
		return new Statement.Return(name, value);
	}

	private Statement forStatement() {
		consume(TokenType.LEFT_PAREN, "Pritet te hapet kllapa '(' ne bllokun 'per'!");
		Statement initializer;
		if (match(TokenType.SEMICOLON)) initializer = null;
		else if (match(TokenType.VAR)) initializer = varDeclaration();
		else initializer = expressionStatement();

		Expression condition = null;
		if (!check(TokenType.SEMICOLON)) condition = expression();
		consume(TokenType.SEMICOLON, "Pritet ';' pas deklarimit te kushtit ne bllokun 'per'");

		Expression increment = null;
		if (!check(TokenType.RIGHT_PAREN)) increment = expression();
		consume(TokenType.RIGHT_PAREN, "Pritet te mbyllet kllapa ')' ne bllokun 'per'");

		consume(TokenType.LEFT_BRACE, "Pritet qe te hapet kllapa '{' ne bllokun 'per'");
		List<Statement> body = block();

		return new Statement.For(initializer, condition, increment, body);
	}

	private Statement ifStatement() {
		List<Statement> then_ = new ArrayList<>();
		List<Statement> elif_ = new ArrayList<>();
		List<Object> elifobj_ = new ArrayList<>();
		List<Statement> else_ = new ArrayList<>();

		consume(TokenType.LEFT_PAREN, "Pritet te hapet kllapa '(' ne bllokun 'nese' !");
		Expression condition = expression();
		Expression elifCondition = null;
		consume(TokenType.RIGHT_PAREN, "Pritet te mbyllet kllapa ')' ne bllokun 'nese' !");
		consume(TokenType.LEFT_BRACE, "Pritet te hapet kllapa '{' ne bllokun 'nese' !");
		then_ = block();

		if (check(TokenType.TJETER)) {
			while (check(TokenType.TJETER)) {
				advance();
				elifCondition = expression();
				elifobj_.add(elifCondition);

				consume(TokenType.LEFT_BRACE, "Pritet te hapet kllapa '{' ne bllokun 'perndryshe' !");

				elif_ = block();
				elifobj_.add(elif_);
			}
		}
        /*
        while( !check( TokenType.RIGHT_BRACE ) && !isAtFileEnd() ){
            then_.add( declaration() ); // gjej nje menyre per me leju -- declaration(); --
        }
        consume( TokenType.RIGHT_BRACE , "Pritet te mbyllet kllapa '}' ne bllokun 'nese' !" );
        */
		if (match(TokenType.PERNDRYSHE)) {
			consume(TokenType.LEFT_BRACE, "Pritet te hapet kllapa '{' ne bllokun 'perndryshe' !");
			else_ = block();
            /*
            while( !check( TokenType.RIGHT_BRACE ) && !isAtFileEnd() ){
                else_.add( declaration() );
            }
            consume( TokenType.RIGHT_BRACE , "Pritet te hapet kllapa '}' ne bllokun 'perndryshe' !" );
            */
		}
		return new Statement.If(condition, then_, elifobj_, else_);
	}

	private Statement whileStatement() {
		List<Statement> body = new ArrayList<>();
		consume(TokenType.LEFT_PAREN, "Pritet te hapet kllapa '(' ne bllokun 'perderisa' !");
		Expression condition = expression();
		consume(TokenType.RIGHT_PAREN, "Pritet te mbyllet kllapa ')' ne bllokun 'perderisa' !");
		consume(TokenType.LEFT_BRACE, "Pritet te hapet kllapa '{' ne bllokun 'perderisa' !");
		body = block();
		return new Statement.While(condition, body);
	}

	private Statement importStatement() {
		Expression val = expression();
		consume(TokenType.SEMICOLON, "Pritet simboli ';' ne fund te komandes ");
		return new Statement.Import(val);
	}

	private Statement varDeclaration() {
		/*
		 * At the moment, if you multi-declare variables in the same line, all
		 * but the first one have global scoping !
		 */
		Token name = consume(TokenType.IDENTIFIER, "Deklarimi i variables duhet te ket nje emer !\n\tp.sh --> deklaro emri = \"EMRI\"; ");
		Expression initializer = null;
		if (match(TokenType.EQ)) initializer = expression();
		if (varflag) {
			// if blockMode insert to the new environment ; continue;
			ALP.interpreter.visitVarStatement(new Statement.Var(name, initializer));
		}
		if (match(TokenType.COMMA)) {
			varflag = true;
			varDeclaration();
			varflag = false;
		}
		if (previous().getType() != TokenType.SEMICOLON)
			consume(TokenType.SEMICOLON, "Pritet ';' pas deklarimit te variables !");
		return new Statement.Var(name, initializer);
	}

	private Statement printStatement(boolean println) {
		Expression val = expression();
		consume(TokenType.SEMICOLON, "Pritet simboli ';' ne fund te komandes shkruaj --> shkruaj \"\" ; ");
		if (println) return new Statement.Print(val, true); /* Print the value and then a new line */
		return new Statement.Print(val, false);
	}

	private Statement expressionStatement() {
		Expression val = expression();
		consume(TokenType.SEMICOLON, "Pritet simboli ';' ne fund te komandes");
		return new Statement.StmtExpression(val);
	}

	private List<Statement> block() {
		List<Statement> statements = new ArrayList<>();
		while (!check(TokenType.RIGHT_BRACE) && !isAtFileEnd()) {
			if (check(TokenType.RIGHT_BRACE)) break;
			statements.add(declaration());
		}
		consume(TokenType.RIGHT_BRACE, "Duhet qe blloku te mbyllet me '}' ! ");
		return statements;
	}

	/* Grammar Functions ! */
	private Expression expression() {
		return assignment();
	}

	private Expression assignment() {
		Expression expr = OrExpr();
		if (match(TokenType.EQ)) {
			Token equals = previous();
			Expression value = assignment();

			if (expr instanceof Expression.Variable) {
				Token name = ((Expression.Variable) expr).name;
				return new Expression.Assign(name, value);
			} else if (expr instanceof Expression.Get) {
				Expression.Get ge = (Expression.Get) expr;
				return new Expression.Set(ge.obj, ge.name, value);
			}
			error(equals, "Targeti i gabuar per caktim (barazim) !");
		}
		return expr;
	}

	private Expression OrExpr() {
		Expression expr = AndExpr();
		while (match(TokenType.OSE)) {
			Token op = previous();
			Expression right = AndExpr();
			expr = new Expression.Logical(expr, op, right);
		}
		return expr;
	}

	private Expression AndExpr() {
		Expression expr = equality();
		while (match(TokenType.DHE)) {
			Token op = previous();
			Expression right = equality();
			expr = new Expression.Logical(expr, op, right);
		}
		return expr;
	}

	private Expression equality() {
		/* equality → comparison ( ( "!=" | "==" ) comparison )* ; */
		Expression comp = comparison();
		while (match(TokenType.NOTEQ, TokenType.EQEQ)) {
			Token operator = previous();
			Expression right = comparison();
			comp = new Expression.Binary(comp, operator, right);
		}
		return comp;
	}

	//-----------------------------------------------------------------------------------------------------------
	private Expression comparison() {
		/* comparison → addition ( ( ">" | ">=" | "<" | "<=" ) addition )* ; */
		Expression add = addition();
		while (match(TokenType.GT, TokenType.GTEQ, TokenType.LT, TokenType.LTEQ)) {
			Token operator = previous();
			Expression right = addition();
			add = new Expression.Binary(add, operator, right);
		}
		return add;
	}

	//-----------------------------------------------------------------------------------------------------------
	private Expression addition() {
		Expression mult = multiplication();
		while (match(TokenType.PLUS, TokenType.MINUS)) {
			Token operator = previous();
			Expression right = multiplication();
			mult = new Expression.Binary(mult, operator, right);
		}
		return mult;
	}

	//-----------------------------------------------------------------------------------------------------------
	private Expression multiplication() {
		Expression expr = unary();
		while (match(TokenType.SLASH, TokenType.STAR, TokenType.MODULO)) {
			Token operator = previous();
			Expression right = unary();
			expr = new Expression.Binary(expr, operator, right);
		}
		return expr;
	}

	//-----------------------------------------------------------------------------------------------------------
	private Expression unary() {
		if (match(TokenType.BANG, TokenType.MINUS)) {
			Token operator = previous();
			Expression right = unary();
			return new Expression.Unary(operator, right);
		}
		return functionCall();
	}

	private Expression functionCall() {
		Expression expr = primary();
		while (true) {
			if (match(TokenType.LEFT_PAREN)) expr = finishCall(expr);
			else if (match(TokenType.DOT)) {
				Token name = consume(TokenType.IDENTIFIER, "Pritet te emertohet prona e instances pas '.'");
				expr = new Expression.Get(expr, name);
			} else break;
		}
		return expr;
	}

	private Expression finishCall(Expression ex) {
		List<Expression> arguments = new ArrayList<>();
		if (!check(TokenType.RIGHT_PAREN)) {
			do {
				if (arguments.size() > 255)
					error(peek(), "Funksioni nuk mund te pranoj me shume se 255 argumenteQ");
				arguments.add(expression());
			} while (match(TokenType.COMMA));
		}
		Token rparen = consume(TokenType.RIGHT_PAREN, "Pritet te mbyllet kllapa ')' ne thirje te funksionit!");
		return new Expression.Call(ex, rparen, arguments);
	}

	//-----------------------------------------------------------------------------------------------------------
	private Expression primary() {
		if (match(TokenType.FALSE)) return new Expression.Literal(false);
		if (match(TokenType.VERTET)) return new Expression.Literal(previous().getLiteral());
		if (match(TokenType.ZBRAZET)) return new Expression.Literal(null);

		if (match(TokenType.SUPER)) {
			Token keyword = previous();
			consume(TokenType.DOT, "Pritet '.' pas 'super'");
			Token method = consume(TokenType.IDENTIFIER, "Duhet te ceket emri i metodes apo variables pas 'super' !");
			return new Expression.Super(keyword, method);
		}
		if (match(TokenType.AKTUAL)) return new Expression.This(previous());

		if (match(TokenType.NUMBER, TokenType.TEXT)) {
			return new Expression.Literal(previous().getLiteral());
		}
		if (match(TokenType.IDENTIFIER)) {
			return new Expression.Variable(previous());
		}
		// To add functions and classes, also make something similar to the above code for IDENTIFIER
		if (match(TokenType.LEFT_PAREN)) {
			Expression expr = expression();
			consume(TokenType.RIGHT_PAREN, "Pritet qe kllapa ')' te mbyllet.");
			return new Expression.Grouping(expr);
		}
		throw error(peek(), "Pritet qe te jete nje expression ( shpreheje ) valide ! ");
	}

	/* Parser Helper Functions ! */
	private Token consume(TokenType t, String message) { /* Could be named "expect" because it expects a token, or it failes */
		if (check(t)) return advance();
		throw error(peek(), "[Parse Error] => " + message);
	}

	//-----------------------------------------------------------------------------------------------------------
	private ParseError error(Token t, String message) {
		ALP.error(t, message);
		return new ParseError();
	}

	//-----------------------------------------------------------------------------------------------------------
	private void synchronize() {
		advance();
		while (!isAtFileEnd()) {
			if (previous().getType() == TokenType.SEMICOLON) return;
			switch (peek().getType()) {
				case KLASA:
				case FUNKSIONI:
				case IMPORTO:
				case PER:
				case NESE:
				case PERDERISA:
				case SHKRUAJ:
				case SHKRUAJR:
				case KTHEN:
				case LEXO:
				case LEXOF:
				case NET:
					return;
			}
			advance();
		}
	}

	//-----------------------------------------------------------------------------------------------------------
	private boolean match(TokenType... t) { /* Match a tokentype.  */
		for (TokenType tt : t) {
			if (check(tt)) {
				advance();
				return true;
			}
		}
		return false;
	}

	//-----------------------------------------------------------------------------------------------------------
	private boolean check(TokenType type) { /* Same as match, but only for one tokentype, used as helper */
		if (isAtFileEnd()) return false;
		return peek().getType() == type;
	}

	//-----------------------------------------------------------------------------------------------------------
	private Token advance() { /*  */
		if (!isAtFileEnd()) current++;
		return previous();
	}

	//-----------------------------------------------------------------------------------------------------------
	private boolean isAtFileEnd() {
		return peek().getType() == TokenType.EOF;
	}

	private Token peek() {
		return tokens.get(current);
	}

	private Token previous() {
		return tokens.get(current - 1);
	}
}
