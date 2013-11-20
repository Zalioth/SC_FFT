package src;

import java.util.Iterator;

import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;

public class ZpFieldMultiplications {
	
	long prime;
	ModIntegerRing fact;
	GenPolynomialRing<ModInteger> ring;
	
	public ZpFieldMultiplications(long prime){
		this.prime = prime;
		ModIntegerRing fact = new ModIntegerRing(prime);
		String[] var = new String[] { "x" };
		// Polynomial factory
		ring = new GenPolynomialRing<ModInteger>( fact, 1,var);
	}

	public void main() {

	
		System.out.println("gen: " + ring.generators().toString());

		// Generate two random polynomials with the specified max degree
		GenPolynomial<ModInteger> p1 = ring.random(5);
		GenPolynomial<ModInteger> p2 = ring.random(5);
		
		System.out.println("p1: " + p1.toString());
		System.out.println("p2: " + p2.toString());

		System.out.println("M: " + getM(p1,p2));
		//add(p1,prime);
		
		   // Multiply using the library (to check if my implementation is correct)
	      GenPolynomial<ModInteger> libraryMultiplication = p1.multiply(p2);
	      
	      // Multiply using the school algorithm (implemented by me [the other me])
	     GenPolynomial<ModInteger> schoolMultiplication = multiplySchool(p1, p2,prime);
	      
	      // Multiply using the FFT algorithm (implemented by me [the other me])
	      GenPolynomial<ModInteger> fftMultiplication = multiplyFFT(p1, p2);

		System.out.println("library: " + libraryMultiplication.toString());
		System.out.println("school: " + schoolMultiplication.toString());
		//System.out.println("fft: " + fftMultiplication.toString());
		

		if (libraryMultiplication.toString().equals(
				schoolMultiplication.toString())) {
			System.out.println("School multiplication is correct");
		} else {
			System.out.println("School multiplication is NOT correct");
		}

		if (libraryMultiplication.toString().equals(
				fftMultiplication.toString())) {
			System.out.println("FFT multiplication is correct");
		} else {
			System.out.println("FFT multiplication is NOT correct");
		}
	}

	private static GenPolynomial<ModInteger> multiplyFFT(GenPolynomial<ModInteger> p1, GenPolynomial<ModInteger> p2) {
		// ModInteger Factory
				ModIntegerRing cfac = new ModIntegerRing(7);
				// Using only one variable, "x"
				String[] var = new String[] { "x" };
				// Polynomial factory
				 GenPolynomialRing<ModInteger> ring = new GenPolynomialRing<ModInteger>( cfac, 1,var);
		return ring.getZERO();
	}
	
	private GenPolynomial<ModInteger> multiplySchool(GenPolynomial<ModInteger> p1, GenPolynomial<ModInteger> p2,long prime) {
		
		// Initialize the result as zero.
		GenPolynomial<ModInteger> result = ring.getZERO();

		// For each element of p1, multiply said element of p1 by each element
		// of p2, add each product to the result.
		Iterator<Monomial<ModInteger>> iter1 = p1.iterator();
		while (iter1.hasNext()) {
			// Get an element of p1.
			Monomial<ModInteger> elemOfP1 = iter1.next();

			// Restart the iterator of p2.
			Iterator<Monomial<ModInteger>> iter2 = p2.iterator();
			while (iter2.hasNext()) {
				// Get an element of p2.
				Monomial<ModInteger> elemOfP2 = iter2.next();

				// Multiply the elements.
				Monomial<ModInteger> multiplicationOfElems = new Monomial<ModInteger>(
				// Exponents are added.
						elemOfP1.exponent().sum(elemOfP2.exponent()),
						// Coefficients are multiplied.
						elemOfP1.coefficient().multiply(elemOfP2.coefficient()));

				// Create a polynomial from the calculated monomial.
				GenPolynomial<ModInteger> pPartial = ring.univariate(0,multiplicationOfElems.exponent().degree());
				pPartial = pPartial.multiply(multiplicationOfElems .coefficient());

				// Add it to the result.
				result = result.sum(pPartial);
			}
		}

		return result;
	}
	
	// Calcula la primera raiz enésima de la unidad
	public static int unitSquare(long base, int degree){
		// ModInteger Factory
		ModIntegerRing mfac = new ModIntegerRing(base);
		
		
		for(int i = 2; i < base; i++){
			ModInteger orig = new ModInteger(mfac,i);
			ModInteger sqrt = new ModInteger(mfac,i);
			
			for(int j = 2; j <= degree; j++){
				sqrt = sqrt.multiply(orig);
			}
			
			if(!sqrt.equals(mfac.getONE())){
				return (int)(Math.pow(i,2.0) % base);
			}
		}
	
		return -1;
	}
	
	// Calcula la M para FFT
	public int getM(GenPolynomial<ModInteger> p1, GenPolynomial<ModInteger> p2){
		long degree1 = p1.degree();
		long degree2 = p2.degree();
		double result = 0;
		double i = 1;
		
		for(i = 1; result <= degree1+degree2; i++){
			result = Math.pow(2.0,i);
		}
		return (int)(i-1);
	}	
}
