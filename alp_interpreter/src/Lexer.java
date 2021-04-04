/*
* Scanner ( Lexer ) class for ALPlang
* created ( Sat Aug 8 2020 )
* last edited ( Mon Aug 10 2020 )
* author Alixhan Basha
*/
import java.util.ArrayList;
import java.util.HashMap  ;
import java.util.List     ;
import java.util.Map      ;

class Lexer {
    private final String source ;
    private final List<Token> tokens = new ArrayList<>();
    private static final Map<String , TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put( "funksion" , TokenType.FUNKSIONI );
        keywords.put( "funksioni" , TokenType.FUNKSIONI );
        //keywords.put( "function" , TokenType.FUNKSIONI );
        keywords.put( "dhe" , TokenType.DHE );
        //keywords.put( "and" , TokenType.DHE );
        keywords.put( "ose" , TokenType.OSE );
        //keywords.put( "or" , TokenType.OSE );
        keywords.put( "klasa" , TokenType.KLASA );
        //keywords.put( "class" , TokenType.KLASA );
        keywords.put( "perndryshe" , TokenType.PERNDRYSHE );
        keywords.put( "trashegon" , TokenType.TRASHEGON );
        //keywords.put( "else" , TokenType.PERNDRYSHE );
        keywords.put( "tjeter" , TokenType.TJETER );
        //keywords.put( "elif" , TokenType.TJETER );
        keywords.put( "nese" , TokenType.NESE );
        //keywords.put( "if" , TokenType.NESE );
        keywords.put( "ZBRAZET" , TokenType.ZBRAZET );
        keywords.put( "NULL" , TokenType.ZBRAZET );
        keywords.put( "shkruaj" , TokenType.SHKRUAJ );
        //keywords.put( "print" , TokenType.SHKRUAJ );
        keywords.put( "shkruajr" , TokenType.SHKRUAJR );
        //keywords.put( "println" , TokenType.SHKRUAJR );
        keywords.put( "kthen" , TokenType.KTHEN );
        keywords.put( "kthehu" , TokenType.KTHEN );
        //keywords.put( "return" , TokenType.KTHEN );
        keywords.put( "super" , TokenType.SUPER );
        keywords.put( "ky" , TokenType.AKTUAL );
        keywords.put( "kjo" , TokenType.AKTUAL );
        keywords.put( "this" , TokenType.AKTUAL );
        //keywords.put( "this" , TokenType.AKTUAL );
        keywords.put( "vertet" , TokenType.VERTET );
        //keywords.put( "true" , TokenType.VERTET );
        keywords.put( "fals" , TokenType.FALSE );
        //keywords.put( "false" , TokenType.FALSE );
        keywords.put( "deklaro" , TokenType.VAR );
        //keywords.put( "var" , TokenType.VAR );
        keywords.put( "importo" , TokenType.IMPORTO );
        keywords.put( "import" , TokenType.IMPORTO );
        keywords.put( "perderisa" , TokenType.PERDERISA );
        //keywords.put( "while" , TokenType.PERDERISA );
        keywords.put( "per" , TokenType.PER );
        //keywords.put( "for" , TokenType.PER );
    }
    private int position ;
    private int start    ;
    private int line     ;

    private char currentCharacter(){
        if( position < source.length() ){
            return source.charAt( position );
        }else{
            return '\0'; /* Return END-OF-FILE */
        }
    }

    public Lexer( String source ){
        this.source = source ;
        this.position = 0    ;
        this.start = position;
        this.line = 1        ;
    }

    public char next(){
        position++;
        return source.charAt( position-1 );
    }

    public void addToken( TokenType type , Object literal ){
        String text = source.substring( start , position );
        tokens.add( new Token( type , text , literal , line ) );
    }
    public void addToken( TokenType type ){
        addToken( type , null );
    }

    private boolean match( char expected ){
        if( currentCharacter() == '\0' ){ return false; }
        if( currentCharacter() != expected ){ return false; }
        position++;
        return true;
    }

    private char peek(){
        if( currentCharacter() == '\0' ){
            return '\0';
        }
        return source.charAt( position );
    }

    private boolean isDigit( char c ){ return c >= '0' && c <= '9'; } /* Custom isDigit method */
    private boolean isAlpha( char c ){ return ( c >= 'a' && c <= 'z' ) || ( c >= 'A' && c <= 'Z' ) || ( c == '_' ); } /* Return true if char c is alphabetical */
    private boolean isAlphaNum( char c ){ return isAlpha( c ) || isDigit( c ); }

    public void scanToken(){
        char c = next();
        switch( c ){
            case ' ' :
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '$': addToken( TokenType.SUPER ); break;
            case '(': addToken( TokenType.LEFT_PAREN ); break;
            case ')': addToken( TokenType.RIGHT_PAREN );break;
            case '{': addToken( TokenType.LEFT_BRACE ); break;
            case '}': addToken( TokenType.RIGHT_BRACE );break;
            case ',': addToken( TokenType.COMMA );      break;
            case '+': addToken( TokenType.PLUS );       break;
            case '%': addToken( TokenType.MODULO );     break;
            case '-': addToken( TokenType.MINUS );      break;
            case '*': addToken( TokenType.STAR );       break;
            case '.': addToken( TokenType.DOT );        break;
            case ';': addToken( TokenType.SEMICOLON );  break;
            case '!': addToken( match('=') ? TokenType.NOTEQ : TokenType.BANG );break;
            case '=': addToken( match('=') ? TokenType.EQEQ : TokenType.EQ );   break;
            case '<': addToken( match('=') ? TokenType.LTEQ : TokenType.LT );   break;
            case '>': addToken( match('=') ? TokenType.GTEQ : TokenType.GT );   break;
            case '/':
                if( match('/') ){
                    while( peek() != '\n' && currentCharacter() != '\0' ){ next(); } /* Single line comments */
                }else if( match('*') ){
                    while( currentCharacter() != '*' && peek() != '/' ){
                        if( currentCharacter() == '\0' ){
                            ALP.runtimeError( new RuntimeError("Multiline komenti nuk perfundoi, por file-i po !") );
                        }
                        next();
                    } /* Multiline comment */
                    next();next();
                } else{ addToken( TokenType.SLASH ); }
                break;
            case '"': /* String */
                try {
                    String res = "";
                    while( peek() != '"' && currentCharacter() != '\0' ){
                        if( peek() == '\n' ) line++;
                        if( currentCharacter() == '\\' ){
                            next();
                            if( currentCharacter() == 'n' ){ res += "\n"; next(); }
                            else if( currentCharacter() == 't' ){ res += "\t"; next(); }
                            else if( currentCharacter() == 'r' ){res += "\033[0;31m"; next(); }
                            else if( currentCharacter() == 'g' ){res += "\033[0;32m"; next(); }
                            else if( currentCharacter() == 'b' ){res += "\033[0;34m"; next(); }
                            else if( currentCharacter() == 'd' ){ res += "\033[0m"; next(); }
                            else if( currentCharacter() == '"' ){ res += "\""; next(); }
                        }else{
                            res += currentCharacter();
                            next();
                        }
                    }
                    if( currentCharacter() == '\0' ){
                        ALP.error( tokens.get( tokens.size()-1 ) , "File-i perfundoi pa terminim te stingut ! " );
                    }

                    next();
                    addToken( TokenType.TEXT , res );
                }catch(StringIndexOutOfBoundsException e){ System.out.println();System.exit(1); }
                break;
            default:
                if( isDigit( c ) ){ /* Numbers */
                    while( isDigit( peek())){ next(); }
                    if( peek() == '.' && isDigit( peekNext() ) ){ next()/* once */; while( isDigit(peek()) ) next(); }
                    addToken( TokenType.NUMBER , Double.parseDouble( source.substring( start , position ) ) );
                }
                else if( isAlpha( c ) ){ /* Identifiers */
                    while( isAlphaNum( peek() ) ) next();
                    String text = source.substring( start , position );
                    TokenType type = keywords.get( text );
                    if( type == null ) type = TokenType.IDENTIFIER; /* If keyword was not found in the map, then its an identifier */
                    addToken( type , text );
                }
                else{ ALP.throwError( line , "" , "Unexpected character -+> " + c  ); }
                break;
        }
    }
    private char peekNext(){
        return ( position+1 > source.length() ) ? '\0' : source.charAt( position+1 );
    }
    public List<Token> scanTokens(){
        while( currentCharacter() != '\0' ){
            start = position ;
            scanToken();
        }
        tokens.add( new Token( TokenType.EOF , "END_OF_FILE reached !" , null , line ) );
        return tokens;
    }
}
