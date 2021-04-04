import java.util.List;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.io.*;
/*
 * Native Interface per ALPlang
 * Permban funksionet e mbrendshme te gjuhes programuese
 * autori: hashbang404 (Alixhan Basha)
 */

/*
 * TODO ( Functions to add )
 * FShkruaj( <path_string> ) -> string
 * Threads() ????
 */
class ALPNativeInterface {
    private final Environment env;
    public ALPNativeInterface( Environment e ){
        this.env = e;
    }
    public void BindFunctions(){
        this.env.define( "instanca_e" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 2; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    String type = String.valueOf( args.get(1) );
                    boolean res = false;

                    switch( type ){

                    case "tekst":
                    case "string":
                        if( args.get(0) instanceof String ) res = true;
                        break;

                    case "numer":
                        if( args.get(0)instanceof Integer || args.get(0) instanceof Double ) res = true;
                        break;

                    //case "boolean":
                    //case "Bulean":
                    //    if( String.valueOf(args.get(0)).equals("vertet") || String.valueOf(args.get(0)).equals("fals"))
                    //        res = true; // for literals !
                    //    break;

                    case "objekt":
                        if( args.get(0) instanceof ALPClass || args.get(0) instanceof ALPInstance ) res = true;
                        break;

                    case "funksion":
                        if( args.get(0) instanceof ALPCallable || args.get(0) instanceof ALPFunction ) res = true;
                        break;

                    default:
                        break;
                    }
                    return res;
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'instanca_e' >"; }
            });
        this.env.define( "Clock" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 0; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    return (double)System.currentTimeMillis()/1000.0;
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'Clock' >"; }
            });
         this.env.define( "__ShkronjaNe__" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 2; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    try{
                        if( !(args.get(0) instanceof Double || args.get(0) instanceof Integer) ) throw new RuntimeError( new Token() , "Duhet qe argumenti i pare te jete numer !" );
                        return ""+String.valueOf(args.get(1)).charAt( (int)Double.parseDouble( args.get(0).toString() ));
                    }catch(StringIndexOutOfBoundsException e){ throw new RuntimeError( new Token(), "Indeksi i dhene eshte me i madhe se gjatesia e stringut!" ); }
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'charAt' >"; }
            });
         this.env.define( "__Gjatesia__" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    if( args.get(0) instanceof String ) return args.get(0).toString().length();
                    else if( args.get(0) instanceof Double || args.get(0) instanceof Integer ) return args.get(0).toString();
                    throw new RuntimeError( new Token() , "Argumenti i dhene duhet te jete string" );
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'charAt' >"; }
            });
        this.env.define( "__SYS_EXEC__" , new ALPCallable(){
                // TODO -> Ndoshta mundeso me egzekutu ne nje thread te ri
                // dhe mundso me pas "print" opsionin
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    Process p = null;
                    int exit_code = 1;
                    try{
                        p = Runtime.getRuntime().exec( String.valueOf( args.get(0) ) );
                        p.waitFor();
                        exit_code = p.exitValue();
                    }
                    catch(IOException e){ exit_code=2; throw new RuntimeError( "Nuk mund te egzekutohet komanda e sistemit !" ); }
                    catch(InterruptedException ie){ exit_code=3;throw new RuntimeError( "Procesi i sistemit u nderpre papritmas !" ); }
                    return exit_code;
                }
                @Override
                public String toString(){ return "<Funksion nativ => '__SYS_EXEC__'>"; }
            });
        this.env.define( "Data" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 0; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    Calendar c = Calendar.getInstance();
                    String ditet[] = { "E Diel", "E Hene",
                        "E Marte", "E Merkure", "E Enjte", "E Premte", "E Shtune" };
                    String muajt[] = { "Janar", "Shkurt", "Mars", "Prill", "Maj",
                        "Qershor", "Korrik", "Gusht", "Shtator", "Tetor", "Nentor", "Dhjetor"};
                    String data =
                        ditet[ c.get(Calendar.DAY_OF_WEEK)-1 ] + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + muajt[ c.get(Calendar.MONTH) ];
                    return data;
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'Data'>"; }
            });
        this.env.define( "Integjer" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    int ret = 0;
                    try { ret = (int)Double.parseDouble( (String)args.get(0).toString() ); }
                    catch(NumberFormatException nfe){
                        throw new RuntimeError("--[ "+args.get(0).toString()+" ]-- nuk mund te konvertohet ne Integjer");
                    }
                    return ret;
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'Integjer'>"; }
            });
        this.env.define( "Dhjetor" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    double ret = 0;
                    try { ret = (double)Double.parseDouble( (String)args.get(0).toString() ); }
                    catch(NumberFormatException nfe){
                        throw new RuntimeError("--[ "+args.get(0).toString()+" ]-- nuk mund te konvertohet ne numer Dhjetor");
                    }
                    return ret;
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'Dhjetor'>"; }
            });
        this.env.define( "String" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args )
                {return (String)args.get(0).toString(); } // FIXME -> If nr is passed, it defaults to double
                @Override
                public String toString(){ return "<Funksion nativ => 'Tekst'>"; }
            });
        this.env.define( "Input" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    System.out.print( args.get(0).toString() );
                    try{
                        java.util.Scanner in = new java.util.Scanner( System.in );
                        return in.nextLine() ;
                    }
                    catch( NoSuchElementException nse ){ System.exit(0); }
                    return "";
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'Input'>"; }
            });
        this.env.define( "Dil" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    System.exit( (int)Double.parseDouble(String.valueOf(args.get(0))) );
                    return "";
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'Input'>"; }
            });
        this.env.define( "FLEXO" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 1; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    String filePath = args.get(0).toString() , str="" , res="" ;
                    try{
                        BufferedReader br = new BufferedReader( new FileReader(filePath) );
                        while( ( str = br.readLine()) != null ) res += str + "\n";
                    }
                    catch( FileNotFoundException fnf ){ throw new RuntimeError( "File-i [ " + filePath + " ] nuk u gjet" ); }
                    catch( IOException ioe ){}
                    return res;
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'FLEXO'>"; }
            });
        this.env.define( "ObjBaraz" , new ALPCallable(){
                @Override
                public int ArgumentSize(){ return 2; }
                @Override
                public Object FunctionCall( Interpreter i , List<Object> args ){
                    if( args.get(0) == args.get(1) )
                        return true;
                    return false;
                }
                @Override
                public String toString(){ return "<Funksion nativ => 'ObjBaraz'>"; }
            });
    }
}
