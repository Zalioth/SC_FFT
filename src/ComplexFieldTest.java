import edu.jas.arith.BigComplex;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;

/*
 * Creado el 14/11/2013
 */

public class ComplexFieldTest
{
	public static void main()
	{
		// Big Complex factory
		BigComplex fac = new BigComplex();
		// Using only one variable, "x"
      String[] var = new String[]{ "x" };
      // Polynomial factory
      GenPolynomialRing<BigComplex> ring = new GenPolynomialRing<BigComplex>(fac,1,var);
      
      // Generate two random polynomials with the specified max degree
      GenPolynomial<BigComplex> p1 = ring.random(5);
      GenPolynomial<BigComplex> p2 = ring.random(5);
      
      // Multiply using the library (to check if my implementation is correct)
      GenPolynomial<BigComplex> libraryMultiplication = p1.multiply(p2);
      
      // Multiply using the school algorithm (implemented by me)
      GenPolynomial<BigComplex> schoolMultiplication = multiplySchool(p1, p2);
      
      if(libraryMultiplication.toString() == schoolMultiplication.toString())
      {
      	System.out.println("School multiplication is correct");
      }
      else
      {
      	System.out.println("School multiplication is NOT correct");
      	System.out.println("library: "+libraryMultiplication);
      	System.out.println("mine: "+schoolMultiplication);
      }
      
      // Multiply using the FFT algorithm (implemented by me)
      GenPolynomial<BigComplex> fftMultiplication = multiplyFFT(p1, p2);
      
      if(libraryMultiplication.toString() == fftMultiplication.toString())
      {
      	System.out.println("FFT multiplication is correct");
      }
      else
      {
      	System.out.println("FFT multiplication is NOT correct");
      	System.out.println("library: "+libraryMultiplication);
      	System.out.println("mine: "+fftMultiplication);
      }
	}
	
	
	public static GenPolynomial<BigComplex> multiplySchool(GenPolynomial<BigComplex> p1,
			GenPolynomial<BigComplex> p2)
	{
		GenPolynomial<BigComplex> result = p1;
		
		// TODO
		
		return result;
	}
	
	public static GenPolynomial<BigComplex> multiplyFFT(GenPolynomial<BigComplex> p1,
			GenPolynomial<BigComplex> p2)
	{
		GenPolynomial<BigComplex> result = p1;
		
		// TODO
		
		return result;
	}
}
