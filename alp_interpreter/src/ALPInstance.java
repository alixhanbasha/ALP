/* ALPlang klasa per reprezentimin e instancave te ALPClass
 * autori: hashbang404 (@alixhanbasha)
 * Data: 12/28/2020
 */

public class ALPInstance {

	private final ALPClass klasa;
	private final java.util.Map<String, Object> fields = new java.util.HashMap<>();

	public ALPInstance(ALPClass c) {
		this.klasa = c;
	} // kostruktor

	public Object get(Token name) {
		/*
		 * Get the fields/properties/functions of the class
		 * so basivally allow => shkruajr( someVar.objectProperty );
		 * */
		if (fields.containsKey(name.getLexme()))
			return fields.get(name.getLexme());

		ALPFunction method = klasa.findMethod(name.getLexme());
		if (method != null) return method.bind(this);

		throw new RuntimeError(name, "Prona e kerkuar nuk egziston ne instancen " + getInstanceName());
	}

	public void set(Token name, Object val) {
		fields.put(name.getLexme(), val);
	}

	public String getInstanceName() {
		return this.klasa.getClassName();
	}

	@Override
	public String toString() {
		return "<Instance => '" + klasa.getClassName() + "'>";
	}
}
