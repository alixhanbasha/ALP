class Return extends RuntimeException {
	public final Object value;

	public Return(Object v) {
		super(null, null, false, false);
		value = v;
	}
}
