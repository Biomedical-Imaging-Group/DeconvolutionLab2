package deconvolution.algorithm;

public enum Constraint {
	NO, NONNEGATIVE, CLIPPED, RESCALED, NORMALIZED;
	
	public static String[] getContraintsAsArray() {
		return new String[] {"no", "nonnegativity", "clipped"};
	}
	
	public static Constraint getByName(String c) {
		if (c.toLowerCase().equalsIgnoreCase("nonnegativity"))
			return Constraint.NONNEGATIVE;
		if (c.toLowerCase().equalsIgnoreCase("clipped"))
			return Constraint.CLIPPED;
		return Constraint.NO;
	}

}
