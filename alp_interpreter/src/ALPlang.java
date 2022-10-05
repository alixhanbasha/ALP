/*
 * ALPlang interpreter made with java
 * created ( Sat Aug 8 2020 )
 * author Alixhan Basha
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

class ALP {
	/* Class for all the flags & logic of the program */
	public static String PROMPT = "\033[1;34malp: \033[0m"; // PS1
	public static String PS2_PROMPT = "  |\t"; // PS2
	public static boolean VERBOSE = false;
	public static boolean TIME = false;
	public static boolean DEBUG = false;
	public static boolean RUNNING = false;
	public static boolean HAD_ERROR = false, COMMANDER = false;

	public static String VERSION = "j_0.1.21   (Thu Dec 31 2020)";
	public static boolean HAD_RUNTIME_ERROR = false;

	public static Interpreter interpreter = new Interpreter();
	public static ArrayList<String> importedModules = new ArrayList<>();

	public static void printHelp() {
		System.out.println("Me jep opcione te sakta !");
	}

	public static void printUsage() {
		System.out.println("This is the usage");
	}

	public static void dprint(String toPrint) {
		if (VERBOSE) {
			System.out.println("\033[0;33m" + toPrint + "\033[0m");
		}
	}

	// ---------------------------------------------------------------------------------------------
	/* Interpreter parts */

	public static void throwError(int line, String where, String message) {
		System.err.println("[line " + line + "] Error: " + where + ": " + message);
		HAD_ERROR = true;
	}

	public static void error(Token t, String message) {
		if (t.getType() == TokenType.EOF) {
			report(t.getLine() + 2, "", message);
		} else {
			report(t.getLine() + 2, "", message);
		}
	}

	public static void report(int line, String f, String message) {
		System.err.println("Ne rreshtin " + line + "\n\t-> " + f + " " + message);
	}

	public static void throwFatalLexerError(int line, String where, String message) {
		dprint("Lexer threw a fatal error !");
		System.err.println("[line " + line + "] Syntax-Error: " + where + ": " + message);
		HAD_ERROR = true;
		System.exit(1);
	}

	public static void throwFatalParserError(int line, String where, String message) {
		dprint("Parser threw a fatal error !");
		System.err.println("[line " + line + "] Parse-Error: " + where + ": " + message);
		HAD_ERROR = true;
		System.exit(1);
	}

	public static void giveWarning(int line, String where, String message) {
		System.err.println("[line " + line + "] Warning" + where + ": " + message);
	}

	public static void runtimeError(RuntimeError error) {
		System.err.println(error.getMessage() + "\n[ rreshti >> " + error.token.getLine() + " ]");
		HAD_RUNTIME_ERROR = true;
	}

	// ---------------------------------------------------------------------------------------------
	public static void run(String source) {
		try {
			Lexer scanner = new Lexer(source);
			List<Token> tokens = scanner.scanTokens();
			Parser p = new Parser(tokens);
			//Expression ex = p.parse();
			List<Statement> statements = p.parse();
			if (ALP.DEBUG) {
				System.out.print("\033[0;32mTokenat e krijuar nga Skaneri/Lekseri\n");
				for (Token token : tokens) {
					if (token.getType() == TokenType.EOF) break;
					System.out.println(token);
				}

				if (HAD_ERROR) return;

				System.out.println("\nRezultati:");
				System.out.print("\033[0m");
			}

			//Resolver r = new Resolver( interpreter );
			//r.resolve( statements );

			interpreter.interpret(statements);
			//System.out.println( "run() method called\n" + source );
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			runtimeError(new RuntimeError(new Token(), "\033[0;31mU shkaktua nje error fatal.\033[0m"));
			System.exit(1);
		}
	}

	// ---------------------------------------------------------------------------------------------
    /*    public static String readFile( String path ) throws IOException{
        byte bytes[] = Files.readAllBytes( Paths.get( path ) );

        }*/
	// ---------------------------------------------------------------------------------------------
	private static boolean isImported(String module) {
		boolean is_imported = false;
		for (String s : importedModules) {
			if (s.equals(module)) {
				is_imported = true;
				break;
			}
		}
		return is_imported;
	}

	public static void runFile(String path) throws IOException {
		long clock = System.currentTimeMillis();

		dprint("Reading file '" + path + "'");
		String DefaultLibPath = "/opt/alp/alplib/";

		Path p = Paths.get(path);
		String fileName = p.getFileName().toString();
		byte bytes[] = new byte[1024];

		if (fileName.charAt(0) == '<' && fileName.charAt(fileName.length() - 1) == '>') {
			fileName = fileName.replaceAll("<", "");
			fileName = fileName.replaceAll(">", "");

			if (!fileName.endsWith(".alp")) fileName += ".alp";

			DefaultLibPath += fileName;
			if (!isImported(DefaultLibPath)) {
				dprint("Importimi i modulit: " + DefaultLibPath);
				importedModules.add(DefaultLibPath);
				bytes = Files.readAllBytes(Paths.get(DefaultLibPath));
				run(new String(bytes, Charset.defaultCharset()));
			}
		} else {
			if (!isImported(path)) {
				dprint("Importimi i modulit: " + path);
				importedModules.add(DefaultLibPath);
				bytes = Files.readAllBytes(p);
				run(new String(bytes, Charset.defaultCharset()));
			}
		}

		clock = System.currentTimeMillis() - clock;

		if (TIME) System.out.println("\n\n\033[0;31mFile executed in " + clock + "ms\033[0m\n");
		if (HAD_ERROR) System.exit(65);
		if (HAD_RUNTIME_ERROR) System.exit(70);
	}

	// ---------------------------------------------------------------------------------------------
	public static void runREPL() throws IOException {
		System.out.println("ALP (AlbanianProgramming Language) repl -- " + new GregorianCalendar().getTime() + "\nVersion " + VERSION);
		if (ALP.COMMANDER) System.out.println("COMMANDER Mode !");

		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		String command = "";
		long clock = 0;
		while (true) {
			System.out.print("\n" + PROMPT);
			command = reader.readLine();
			if (command == null) break;
			clock = System.currentTimeMillis();

			if (ALP.COMMANDER && (command.equalsIgnoreCase(".memorja") || command.equalsIgnoreCase(".mem"))) {
				CommandLineTable tbl = new CommandLineTable();
				tbl.setShowVerticalLines(true);
				tbl.setHeaders("Emri variables", "Vlera variables");

				if (interpreter.getEnvironment().enclosing != null) {
					for (String s : interpreter.getEnvironment().enclosing.getVariableTable().keySet()) {
						//tbl.addRow( "\033[0m\033[1;34m" + s , "\033[0m\033[1;33m" + interpreter.getEnvironment().getVariableTable().get(s) + "\033[0m" );
						tbl.addRow(s, interpreter.getEnvironment().getVariableTable().get(s) + "");
					}
					tbl.addRow("", "");
				}
				for (String s : interpreter.getEnvironment().getVariableTable().keySet()) {
					//tbl.addRow( "\033[0m\033[1;34m" + s , "\033[0m\033[1;33m" + interpreter.getEnvironment().getVariableTable().get(s) + "\033[0m" );
					tbl.addRow(s, interpreter.getEnvironment().getVariableTable().get(s) + "");
				}
				tbl.print();
				continue;
			} else if (ALP.COMMANDER && command.equalsIgnoreCase(".cls")) {
				System.out.print("\033[2J\033[H");
				continue;
			} else if (ALP.COMMANDER && (command.equalsIgnoreCase(".commander_ndihme") || command.equalsIgnoreCase(".ndihme") || command.equalsIgnoreCase(".n"))) {
				System.out.print("\n\tNdihme per ALP Commander\n\t    .mod | .modulet => Printo modulet e importuara\n\t    .mem | .memorja => Printo statusin e memorjes\n\t    .cls => Pastro ekranin\n");
				continue;
			} else if (ALP.COMMANDER && (command.equalsIgnoreCase(".mod") || command.equalsIgnoreCase(".modulet"))) {
				CommandLineTable tbl = new CommandLineTable();
				tbl.setShowVerticalLines(true);
				tbl.setHeaders("Modulet e importuara");
				for (int i = 0; i < importedModules.size(); i++) {
					tbl.addRow(importedModules.get(i));
				}
				tbl.print();
				continue;
			}
			run(command);

			System.out.print("\033[0m");
			clock = System.currentTimeMillis() - clock;
			dprint("Executed in " + clock + "ms");
			HAD_ERROR = false;
		}
	}
	/* End Interpreter parts */
	// ---------------------------------------------------------------------------------------------
}

public class ALPlang {

	/* ----------------------------- Main class ----------------------------- */
	public static void main(String[] args) {
		try { /* Parse the CLI arguments ! */
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {

					switch (args[i]) {
						case "-V":
						case "--version":
							System.out.println(ALP.VERSION);
							break;
						case "-t":
						case "--time":
							ALP.TIME = true;
							break;
						case "-v":
						case "--verbose":
							ALP.VERBOSE = true;
							//ALP.dprint("Verbose mode active!");
							break;
						case "-d":
						case "--debug":
							ALP.DEBUG = true;
							break;
						case "--PS1":
							try {
								ALP.dprint("Setting prompt to: '" + args[i + 1] + "'");
								ALP.PROMPT = args[i + 1];
								i++;
							} catch (Exception e) {
								System.out.println("Could not set the prompt");
							}
							break;

						case "--file":
						case "-f":
							try {
								//                      ALP.importedModules.add( (String)args[i+1] );
								ALP.runFile(args[i + 1]);
								ALP.dprint(ALP.importedModules + "");
								i++;
							} catch (FileNotFoundException e) {
								System.out.println("File-i qe shenuat nuk u gjet -> '" + args[i + 1] + "'");
                      /*
                      if( ALP.VERBOSE ){
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            String exception = sw.toString();
                            ALP.dprint( exception );
                      }
                      */
								ALP.dprint(e.toString());
								i++;
							} catch (NoSuchFileException e) {
								System.out.println("File-i qe shenuat nuk u gjet -> '" + args[i + 1] + "'");
                      /*
                        if( ALP.VERBOSE ){
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exception = sw.toString();
                        ALP.dprint( exception );
                        }
                      */
								ALP.dprint(e.toString());
								i++;
							}
							break;

						case "--repl":
						case "-r":
							ALP.runREPL();
							break;
						case "--commander":
							ALP.COMMANDER = true;
							break;
						default:
							ALP.printUsage();
							System.exit(1);
							break;
					}
				}
			} else {
				ALP.printHelp();
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		System.out.println("");
	}
}
