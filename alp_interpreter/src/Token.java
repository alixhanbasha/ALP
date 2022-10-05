/*
 * Token class for ALPlang
 * created ( Sat Aug 8 2020 )
 * author Alixhan Basha
 * last edited ( Sat Aug 8 2020 )
 */
public class Token {
	private final TokenType type;
	private final String lexme;
	private final Object literal;
	private final int line;

	public Token() {
		this.literal = "{{{[[[ <-- IGNORE-THIS-TOKEN --> ]]]}}}";
		this.type = TokenType.ZBRAZET;
		this.lexme = "ZBRAZET";
		this.line = 0;
	}

	public Token(TokenType t, String l, Object lt, int ln) {
		this.type = t;
		this.lexme = l;
		this.literal = lt;
		this.line = ln;
	}

	public String toString() {
		return "< [" + this.type + "] [" + this.lexme + "] [" + this.literal + "] >";
	}

	public TokenType getType() {
		return this.type;
	}

	public String getLexme() {
		return this.lexme;
	}

	public int getLine() {
		return this.line;
	}

	public Object getLiteral() {
		return this.literal;
	}

}
