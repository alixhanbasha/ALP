/* Function & Class calls interface for ALPlang
 * author: hashbang404 (@alixhanbasha)
 * lastEditet: (Sat Dec 5 2020)
 */
interface ALPCallable {
    public int ArgumentSize();
    public Object FunctionCall( Interpreter i , java.util.List<Object> args );
}
