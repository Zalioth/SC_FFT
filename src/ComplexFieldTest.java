import java.util.Iterator;

import edu.jas.arith.BigComplex;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;

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
      
      System.out.println("gen: "+fac.generators().toString());
      
      
      // Generate two random polynomials with the specified max degree
      GenPolynomial<BigComplex> p1 = ring.random(5);
      GenPolynomial<BigComplex> p2 = ring.random(5);
      System.out.println("p1: "+p1.toString());
      System.out.println("p2: "+p2.toString());
      
      // Multiply using the library (to check if my implementation is correct)
      GenPolynomial<BigComplex> libraryMultiplication = p1.multiply(p2);
      
      // Multiply using the school algorithm (implemented by me)
      GenPolynomial<BigComplex> schoolMultiplication = multiplySchool(p1, p2);
      
      // Multiply using the FFT algorithm (implemented by me)
      GenPolynomial<BigComplex> fftMultiplication = multiplyFFT(p1, p2);
      
      System.out.println("library: "+libraryMultiplication.toString());
   	System.out.println("school: "+schoolMultiplication.toString());
   	System.out.println("fft: "+fftMultiplication.toString());
      
      if(libraryMultiplication.toString().equals(schoolMultiplication.toString()))
      {
      	System.out.println("School multiplication is correct");
      }
      else
      {
      	System.out.println("School multiplication is NOT correct");
      }
      
      if(libraryMultiplication.toString().equals(fftMultiplication.toString()))
      {
      	System.out.println("FFT multiplication is correct");
      }
      else
      {
      	System.out.println("FFT multiplication is NOT correct");
      }
	}
	
	
	public static GenPolynomial<BigComplex> multiplySchool(GenPolynomial<BigComplex> p1,
			GenPolynomial<BigComplex> p2)
	{
		// Big Complex factory
		BigComplex fac = new BigComplex();
		// Using only one variable, "x"
      String[] var = new String[]{ "x" };
      // Polynomial factory
      GenPolynomialRing<BigComplex> ring = new GenPolynomialRing<BigComplex>(fac,1,var);
	   
      // Initialize the result as zero.
		GenPolynomial<BigComplex> result = ring.getZERO();
		
		// For each element of p1, multiply said element of p1 by each element of p2, add each product to the result.
		Iterator<Monomial<BigComplex>> iter1 = p1.iterator();
      while(iter1.hasNext())
      {
      	// Get an element of p1.
      	Monomial<BigComplex> elemOfP1 = iter1.next();
      	
      	// Restart the iterator of p2.
      	Iterator<Monomial<BigComplex>> iter2 = p2.iterator();
      	while(iter2.hasNext())
         {
      		// Get an element of p2.
         	Monomial<BigComplex> elemOfP2 = iter2.next();
         	
         	// Multiply the elements.
         	Monomial<BigComplex> multiplicationOfElems = new Monomial<BigComplex>
         	(
         			// Exponents are added.
         			elemOfP1.exponent().sum(elemOfP2.exponent()),
         			// Coefficients are multiplied.
         			elemOfP1.coefficient().multiply(elemOfP2.coefficient())
         	);
         	
         	// Create a polynomial from the calculated monomial.
         	GenPolynomial<BigComplex> pPartial = ring.univariate(0,multiplicationOfElems.exponent().degree());
         	pPartial = pPartial.multiply(multiplicationOfElems.coefficient());
         	
         	// Add it to the result.
         	result = result.sum(pPartial);
         }
      }
		
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
