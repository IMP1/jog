package test;

public class Vector {

	/** The size of the vector. */
	public final int size;
	/** The contents of the vector. */
	private double[] data;
	
	/**
	 * Contructor for Vector objects.
	 * @param data initial contents of the vector.
	 */
	public Vector(double... data) {
		if (data.length == 1 && data[0] == 1) {
			throw new ArithmeticException("Vector of size 1 is just a number.");
		} else if (data.length == 1) {
			size = (int)data[0];
			this.data = new double[size];
			// Set default values to 0
			for (int i = 0; i < size; i ++) { this.data[i] = 0; }
		} else {
			size = data.length;
			this.data = data;
		}
	}
	
	public boolean equals(Vector v) {
		if (v.size != size) return false;
		for (int i = 0; i < size; i ++) {
			if (data[i] != v.data[i]) return false;
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() != getClass()) return false;
		return equals((Vector)(obj));
	}
	
	public Vector copy() {
		return new Vector(data.clone());
	}
	
	public double x() { return get(0); } public double i() { return get(0); }
	public double y() {	return get(1); } public double j() {	return get(1); }
	public double z() { return get(2); } public double k() { return get(2); }
	
	public double get(int index) {
		if (size <= index) {
			throw new IndexOutOfBoundsException("" + index);
		} else {
			return data[index];
		}
	}
	
	public Vector inverse() {
		Vector outcome = copy();
		for (int i = 0; i < size; i ++) {
			outcome.data[i] = -outcome.data[i];
		}
		return outcome;
	}
	
	public Vector add(Vector v) {
		if (v.size != size) {
			throw new ArithmeticException("Vectors are of different sizes.");
		}
		Vector outcome = copy();
		for (int i = 0; i < size; i ++) {
			outcome.data[i] += v.data[i];
		}
		return outcome;
	}
	
	public Vector sub(Vector v) {
		if (v.size != size) {
			throw new ArithmeticException("Vectors are of different sizes.");
		}
		Vector outcome = copy();
		for (int i = 0; i < size; i ++) {
			outcome.data[i] -= v.data[i];
		}
		return outcome;
	}
	
	public Vector scale(double n) {
		Vector outcome = copy();
		for (int i = 0; i < size; i ++) {
			outcome.data[i] *= n;
		}
		return outcome;
	}
	
	public double dotProduct(Vector v) {
		double outcome = 0;
		for (int i = 0; i < size; i ++) {
			outcome += data[i] * v.data[i];
		}
		return outcome;
	}
	
	public Vector crossProduct3(Vector v) {
		return new Vector(data[2] * v.data[3] - data[3] * v.data[2],
						  data[3] * v.data[1] - data[1] * v.data[3],
						  data[1] * v.data[2] - data[2] * v.data[1]);
	}
	
	public double crossProduct2(Vector v) {
		return data[1] * v.data[2] - data[2] * v.data[1];
	}
	
	public double magnitude() {
		double outcome = 0;
		for (int i = 0; i < size; i ++) {
			outcome += data[i] * data[i]; 
		}
		return Math.sqrt(outcome);
	}
	
	public Vector normalise() {
		return scale(1 / magnitude());
	}
	
	public Vector rotate(double r) {
		if (size > 2) {
			throw new ArithmeticException("Vectors of size 3 need to use Vector.rotate(i, j, k)");
		}
		Vector outcome = copy();
		outcome.data[0] = x() * Math.cos(r) - y() * Math.sin(r);
		outcome.data[1] = y() * Math.cos(r) - x() * Math.sin(r);
		return outcome;
	}
	
	public Vector rotate(double i, double j, double k) {
		if (size <= 2) {
			throw new ArithmeticException("Vectors of size 2 need to use Vector.rotate(r)");
		}
		Vector outcome = copy();
		outcome.data[0] = x() * Math.cos(i) - y() * Math.sin(i);
		outcome.data[1] = y() * Math.cos(i) - x() * Math.sin(i);
		
		outcome.data[1] = y() * Math.cos(i) - z() * Math.sin(i);
		outcome.data[2] = z() * Math.cos(i) - y() * Math.sin(i);
		
		outcome.data[2] = z() * Math.cos(i) - x() * Math.sin(i);
		outcome.data[0] = x() * Math.cos(i) - z() * Math.sin(i);
		return outcome;
	}
	
	public static double angleBetween(Vector v1, Vector v2) {
		return Math.acos( v1.dotProduct(v2) / (v1.magnitude() * v2.magnitude()) );
	}
	
	public static Vector normal(Vector v1, Vector v2) {
		return v1.crossProduct3(v2).normalise();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int i = 0; i < size; i ++) {
			sb.append(data[i]);
			if (i < size - 1) {
				sb.append(", ");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
}
