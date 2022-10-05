/*  Data e krijimit 12/28/2020
 *  ALPlang reprezentimi i klasave
 *  Autori: hashbang404 (@alixhanbasha)
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ALPClass implements ALPCallable {
	private final String name;
	private final ALPClass superclass;
	private final Map<String, ALPFunction> methods;

	public ALPClass(String n, ALPClass superclass, Map<String, ALPFunction> m) {
		this.name = n;
		this.methods = m;
		this.superclass = superclass;
	}

	public ALPFunction findMethod(String name) {
		if (methods.containsKey(name)) {
			return methods.get(name);
		}
		if (superclass != null) return superclass.findMethod(name);
		return null;
	}

	public ALPFunction findMethod(String... names) {
		for (String name : names) {
			if (methods.containsKey(name)) {
				return methods.get(name);
			}
		}
		return null;
	}

	@Override
	public Object FunctionCall(Interpreter i, java.util.List<Object> args) {
		ALPInstance instance = new ALPInstance(this);
		ALPFunction constructor = findMethod("konstruktor", "__init__");
		if (constructor != null) {
			constructor.bind(instance).FunctionCall(i, args);
		}
		return instance;
	}

	@Override
	public int ArgumentSize() {
		ALPFunction constructor = findMethod("konstruktor", "__init__");
		if (constructor == null) return 0;
		return constructor.ArgumentSize();
	}

	public String getClassName() {
		return this.name;
	}

	@Override
	public String toString() {
		return "<Klasa => '" + this.name + "'>";
	}

}
