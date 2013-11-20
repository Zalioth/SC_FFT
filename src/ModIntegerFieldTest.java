import java.util.Iterator;

import edu.jas.arith.BigComplex;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;

public class ModIntegerFieldTest {

	public static void main() {

		//Prime
		//long prime = 41;
		long prime = 257;
		// ModInteger factory
		ModIntegerRing cfac = new ModIntegerRing(prime);
		// Using only one variable, "x"
		String[] var = new String[] { "x" };
		// Polynomial factory
		 GenPolynomialRing<ModInteger> ring = new GenPolynomialRing<ModInteger>( cfac, 1,var);

		System.out.println("gen: " + ring.generators().toString());

		// Generate two random polynomials with the specified max degree
		GenPolynomial<ModInteger> p1 = ring.random(5);
		GenPolynomial<ModInteger> p2 = ring.random(5);
		
		System.out.println("p1: " + p1.toString());
		System.out.println("p2: " + p2.toString());

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

		/*if (libraryMultiplication.toString().equals(
				fftMultiplication.toString())) {
			System.out.println("FFT multiplication is correct");
		} else {
			System.out.println("FFT multiplication is NOT correct");
		}*/
	}

	private static GenPolynomial<ModInteger> multiplyFFT(
			GenPolynomial<ModInteger> p1, GenPolynomial<ModInteger> p2) {
		// TODO Auto-generated method stub
		return null;
	}

	public static GenPolynomial<ModInteger> multiplySchool(GenPolynomial<ModInteger> p1, GenPolynomial<ModInteger> p2,long prime) {
		
		// ModInteger Factory
		ModIntegerRing cfac = new ModIntegerRing(prime);
		// Using only one variable, "x"
		String[] var = new String[] { "x" };
		// Polynomial factory
		 GenPolynomialRing<ModInteger> ring = new GenPolynomialRing<ModInteger>( cfac, 1,var);

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
				GenPolynomial<ModInteger> pPartial = ring.univariate(0,
						multiplicationOfElems.exponent().degree());
				pPartial = pPartial.multiply(multiplicationOfElems
						.coefficient());

				// Add it to the result.
				result = result.sum(pPartial);
			}
		}

		return result;
	}
}
