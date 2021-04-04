/* Environment class for ALPlang
 * Variable , Function and Class declarations are memorized here !
 * Created --> ( Sun Sep 20 2020 )
 * Last Edited --> ( Sun Sep 20 2020 )
 * Author: hashbang404
 */
import java.util.Map;
import java.util.HashMap;

public class Environment {

    public final Environment enclosing;
    private HashMap<String, Object> values = new HashMap<>();

    public Environment(){ enclosing = null; }
    public Environment( Environment enc ){ enclosing = enc; }

    public void define( String name , Object value ){
        if( values.get(name)!=null ) { ALP.error( new Token(), "\033[0;32mVaraiabla '" + name + "' vetem eshte e definuar.\033[0m" ); return; }
        values.put( name , value );
    }
    public Object getAt( int distance, String name ){
        return getAncestor( distance ).getVariableTable().get( name );
    }
    public void assignAt( int distance, Token name, Object value ){
        getAncestor(distance).getVariableTable().put( name.getLexme(), value);
    }
    private Environment getAncestor( int d ){
        Environment env = this;
        for( int i=0; i<d ; i++ ){
            env = env.enclosing;
        }
        return env;
    }
    public Object get( Token name ){

        if( values.containsKey( name.getLiteral() ) ) return values.get(name.getLexme());

        if( this.enclosing != null ){
            return this.enclosing.get( name );
        }

        throw new RuntimeError(name , "\033[0;31mVariable e padefinuar -> '" + name.getLexme() + "'.\033[0m");
    }
    public Object get( String name ){

        if( values.containsKey( name )) return values.get(name);

        if( this.enclosing != null ){
            return this.enclosing.get( name );
        }

        throw new RuntimeError(new Token() , "\033[0;31mVariable e padefinuar -> '" + name + "'.\033[0m");
    }
    public void assign(Token name, Object value) {
        if (values.containsKey( name.getLexme() )) {
            values.put( name.getLexme() , value);
            return;
        }
        if( this.enclosing != null ){ this.enclosing.assign( name, value ); return; }
        throw new RuntimeError(name,"\033[0;31mVariabla e padefinuar -> '" + name.getLexme() + "'.\033[0m");
  }
    public HashMap<String , Object> getVariableTable(){ return this.values; }
    public void setVariableTable( HashMap<String , Object> values2 ){ this.values = values2; }
}
