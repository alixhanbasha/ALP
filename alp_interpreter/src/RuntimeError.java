/* RuntimeError handler for ALPlang
 * Created (  )
 * Last Edited (  )
 * Author --> cantbebothered ( Alixhan Basha )
 */

public class RuntimeError extends RuntimeException {
    final Token token;
    public RuntimeError( Token t , String message ){
        super(message);
        this.token = t;
    }
    public RuntimeError( String msg ){
        super( msg );
        this.token = new Token();
    }
}
