import java.util.List;

class ALPFunction implements ALPCallable {

	private final Statement.Function declaration;
	private final Environment closure;

	public ALPFunction(Statement.Function dec, Environment cl) {
		declaration = dec;
		closure = cl;
	}

	public ALPFunction bind(ALPInstance instance) {
		Environment env = new Environment(closure);
		env.define("this", instance);
		env.define("kjo", instance);
		env.define("ky", instance);
		return new ALPFunction(declaration, env);
	}

	@Override
	public int ArgumentSize() {
		return (int) declaration.params.size();
	}

	@Override
	public Object FunctionCall(Interpreter i, List<Object> args) {
		Environment current = new Environment(closure);

		for (int j = 0; j < declaration.params.size(); j++)
			current.define(declaration.params.get(j).getLexme(), args.get(j)); // defino argumentet e funksionit
		//
		try {
			i.executeBlock(declaration.body, current);
		} catch (Return r) {
			return r.value;
		}

		return null;
	}

	public String getFunctionName() {
		return declaration.name.getLexme();
	}

	@Override
	public String toString() {
		return "<Funksioni => '" + declaration.name.getLexme() + "'>";
	}
}
