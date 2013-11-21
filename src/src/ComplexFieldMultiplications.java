package src;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigRational;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;

/*
 * Creado el 14/11/2013
 */

public class ComplexFieldMultiplications
{
	private BigComplex coefficientFactory;
   private String[] variables;
   GenPolynomialRing<BigComplex> polynomialFactory;
	
	public ComplexFieldMultiplications(BigComplex coefficientFactory,
			String[] variables, GenPolynomialRing<BigComplex> polynomialFactory)
	{
		this.coefficientFactory = coefficientFactory;
		this.variables = variables;
		this.polynomialFactory = polynomialFactory;
		if(variables.length > 1)
		{
			System.out.println("Multiplication of polynomials on several variables is not implementated yet.");
		}
		else if(variables.length <= 0)
		{
			System.out.println("A valid polynomial must have at least one variable.");
		}
	}
	
	public GenPolynomial<BigComplex> multiplySchool(GenPolynomial<BigComplex> p1,
			GenPolynomial<BigComplex> p2)
	{
      // Initialize the result as zero.
		GenPolynomial<BigComplex> result = polynomialFactory.getZERO();
		
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
         	GenPolynomial<BigComplex> pPartial = polynomialFactory.univariate(0,multiplicationOfElems.exponent().degree());
         	pPartial = pPartial.multiply(multiplicationOfElems.coefficient());
         	
         	// Add it to the result.
         	result = result.sum(pPartial);
         }
      }
		
		return result;
	}
	
	public GenPolynomial<BigComplex> multiplyFFT(GenPolynomial<BigComplex> p1,
			GenPolynomial<BigComplex> p2)
	{
		GenPolynomial<BigComplex> result = p1;
		
		// Get the minimum power of two that's greater than the product of the degrees of the polynomials
		long maxDegree = (long) Math.pow( 2, Math.ceil( Math.log(p1.degree() * p2.degree())/Math.log(2) ) );
		
		// Get the dense representations of the polynomials
		Vector<BigComplex> denseP1 = denseRepresentation(p1, maxDegree);
		Vector<BigComplex> denseP2 = denseRepresentation(p2, maxDegree);
		
		// dftP1 = DFT(p1)
		// dftP2 = DFT(p2)
		// mult = dftP1 * dftP2 (element by element)
		// resultDense = pow(pow(2,maxDegree),-1) * IDFT(mult)
		// result = getPolynomial(resultDense)
		
		return result;
	}
	
	private Vector<BigComplex> rootsOfUnity(int n)
	{
		Vector<BigComplex> roots = new Vector<BigComplex>(n);
		
		// Divide the complex vector space (whose modulus are equal to 1)
		// in n sectors. Each of the points obtained is an nth root of unity.
		final double modulus = 1.0;
		final double fullVectorSpace = 2*Math.PI;
		final double sectorSize = fullVectorSpace / n;
		
		for(int i=0; i < n; ++i)
		{
			double real = modulus * Math.cos(i * sectorSize);
			double imaginary = modulus * Math.sin(i * sectorSize);
			
			roots.add(new BigComplex(new BigRational(""+real), new BigRational(""+imaginary)));
		}
		
		return roots;
	}
	
	private Vector<BigComplex> denseRepresentation(GenPolynomial<BigComplex> p, long maxDegree)
	{
		Vector<BigComplex> denseRepresentation = new Vector<BigComplex>((int)maxDegree);
		
		Monomial<BigComplex> monomial = null;
		
		long lastDegree = maxDegree+1;
		
		Iterator<Monomial<BigComplex>> iter = p.iterator();
      while(iter.hasNext())
      {
      	// Get an element of p.
      	monomial = iter.next();
      	//001023004
      	//(0,8)(0,7)(1,6)(0,5)(2,4)(3,3)(0,2)(0,1)(4,0)
      	if(monomial.e.degree()+1 < lastDegree)
      	{
      		// Fill with zeros
      		for(long i=monomial.e.degree()+1; i<lastDegree; ++i)
      		{
      			denseRepresentation.add(new BigComplex("0"));
      		}
      	}
      	denseRepresentation.add(monomial.coefficient());
      	lastDegree = monomial.e.degree();
      }
      
      if(monomial.e.degree() > 0)
      {
      	// Fill with zeros
   		for(long i=0; i<monomial.e.degree(); ++i)
   		{
   			denseRepresentation.add(new BigComplex("0"));
   		}
      }
		
		return denseRepresentation;
	}
}
