package src;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import util.Time;
import edu.jas.arith.BigComplex;
import edu.jas.arith.BigRational;
import edu.jas.poly.ExpVector;
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
   private GenPolynomialRing<BigComplex> polynomialFactory;
	
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
			GenPolynomial<BigComplex> p2, Time time, PrintWriter pw)
	{
		time.start();
		GenPolynomial<BigComplex> result;
	
		
		// Get the minimum power of two that's greater than the sum of the degrees of the polynomials
		long m = (long) Math.ceil( Math.log(p1.degree()+1 + p2.degree()+1) / Math.log(2) );
		long maxDegree = (long) Math.pow(2, m);
		
		Vector<BigComplex> w = primitiveRootsOfUnity(maxDegree);
		
		// Get the dense representations of the polynomials
		Vector<BigComplex> denseP1 = denseRepresentation(p1, maxDegree);
		Vector<BigComplex> denseP2 = denseRepresentation(p2, maxDegree);
		
		Vector<BigComplex> fftP1 = FFT(m, w.firstElement(), denseP1);
		Vector<BigComplex> fftP2 = FFT(m, w.firstElement(), denseP2);
		
		Vector<BigComplex> mult = multiplyElementByElemnt(fftP1, fftP2);
		
		// Inverse FFT of the multiplication
		Vector<BigComplex> resultDense = FFT(m, w.firstElement().inverse(), mult);
		for(int i=0; i<resultDense.size(); ++i)
		{
			resultDense.set(i, resultDense.elementAt(i).divide(new BigComplex(new BigRational(maxDegree))));
		}
		
		result = getPolynomial(resultDense);
		time.stop();
		pw.println((p1.degree() + p1.degree()) + "\t" + time.getTime());
		//System.out.println((p1.degree() + p1.degree()) + "\t" + time.getTime());
		return result;
	}
	
	public Vector<BigComplex> FFT(long m, BigComplex w, Vector<BigComplex> densePoly)
	{
		Vector<BigComplex> result = new Vector<BigComplex>();
		
		if(m == 0)
		{
			result.add(densePoly.firstElement());
		}
		else
		{
			Vector<BigComplex> b = oddVector(densePoly);
			Vector<BigComplex> c = evenVector(densePoly);
			
			Vector<BigComplex> B = FFT(m-1, w.multiply(w), b);
			Vector<BigComplex> C = FFT(m-1, w.multiply(w), c);
			
			BigComplex[] temp = new BigComplex[(int) Math.pow(2, m)];
			for(int i=0; i<Math.pow(2,m-1); ++i)
			{
				temp[i] = B.elementAt(i).sum( C.elementAt(i).multiply( power(w,i) ) );
				temp[(int) (Math.pow(2, m-1) + i)] = B.elementAt(i).subtract( C.elementAt(i).multiply( power(w,i) ) );
			}

			for(int i=0; i<Math.pow(2, m); ++i)
			{
				result.add(temp[i]);
			}
		}
		
		return result;
	}
	
	private Vector<BigComplex> primitiveRootsOfUnity(long n)
	{
		// @pre Implemented to work only with n being a power of two.
		Vector<BigComplex> primitiveRootsOfUnity = new Vector<BigComplex>();
		Vector<BigComplex> rootsOfUnity = rootsOfUnity(n);
		
		if(n<=0)
		{
			// Empty set
		}
		else if(n==1)
		{
			primitiveRootsOfUnity.add(new BigComplex(1));
		}
		else if(n==2)
		{
			primitiveRootsOfUnity.add(new BigComplex(-1));
		}
		else if(n==4)
		{
			primitiveRootsOfUnity.add(new BigComplex(new BigRational(0), new BigRational(1)));
			primitiveRootsOfUnity.add(new BigComplex(new BigRational(0), new BigRational(-1)));
		}
		else
		{
			for(int i=0; i<rootsOfUnity.size(); ++i)
			{
				if(i == 0 
						|| i == ((1/4) * rootsOfUnity.size())-1 
						|| i == ((2/4) * rootsOfUnity.size())-1
						|| i == ((3/4) * rootsOfUnity.size())-1
						)
				{
					// These special points (can't be just calculated due to precision errors) are not primitive.
					continue;
				}
				// For each root of unity, check if it's primitive.
				Vector<BigComplex> previousPowers = new Vector<BigComplex>();
				// Calculate it's powers between 0 and n
				for(int j=0; j<n; ++j)
				{
					previousPowers.add(power(rootsOfUnity.elementAt(i), j));
				}
				
				boolean isPrimitive = true;
				// Check that there is no two equal powers
				for(int j=0; j<previousPowers.size(); ++j)
				{
					for(int k=j+1; k<previousPowers.size(); ++k)
					{
						if(previousPowers.elementAt(j) == previousPowers.elementAt(k))
						{
							isPrimitive = false;
						}
					}
				}
				if(isPrimitive)
				{
					primitiveRootsOfUnity.add(rootsOfUnity.elementAt(i));
				}
			}
		}

		return primitiveRootsOfUnity;
	}
	
	private Vector<BigComplex> rootsOfUnity(long n)
	{
		Vector<BigComplex> roots = new Vector<BigComplex>();
		
		// Divide the complex vector space (whose modulus are equal to 1)
		// in n sectors. Each of the points obtained is an nth root of unity.
		final double modulus = 1.0;
		final double fullVectorSpace = 2*Math.PI;
		final double sectorSize = fullVectorSpace / n;
		
		for(int i=0; i < n; ++i)
		{
			double real = modulus * StrictMath.cos(i * sectorSize);
			double imaginary = modulus * StrictMath.sin(i * sectorSize);
			
			DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
			otherSymbols.setDecimalSeparator('.');
			DecimalFormat df = new DecimalFormat("0.000", otherSymbols);
			df.setMaximumFractionDigits(64);
			df.setGroupingUsed(false);
			String strReal = df.format(real);
			String strImaginary = df.format(imaginary);
			
			roots.add(new BigComplex(new BigRational(strReal), new BigRational(strImaginary)));
		}
		
		return roots;
	}
	
	private Vector<BigComplex> denseRepresentation(GenPolynomial<BigComplex> p, long maxDegree)
	{
		Vector<BigComplex> denseRepresentation = new Vector<BigComplex>((int)maxDegree);
		
		Monomial<BigComplex> monomial = null;
		
		long lastDegree = maxDegree;
		
		Iterator<Monomial<BigComplex>> iter = p.iterator();
      while(iter.hasNext())
      {
      	// Get an element of p.
      	monomial = iter.next();
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
		
      Collections.reverse(denseRepresentation);
	  return denseRepresentation;
	}
	

	private Vector<BigComplex> evenVector(Vector<BigComplex> vector)
	{
		Vector<BigComplex> result = new Vector<BigComplex>();
		for(int i = 0; i < vector.size(); i++)
		{
			//is an even position
			if((i+1) % 2 == 0)
			{
				result.add(vector.elementAt(i));
			}
		}
		return result;
	}
	
	private Vector<BigComplex> oddVector(Vector<BigComplex> vector)
	{
		Vector<BigComplex> result = new Vector<BigComplex>();
		for(int i = 0; i < vector.size(); i++)
		{
			//is and odd position
			if((i+1) % 2 != 0)
			{
				result.add(vector.elementAt(i));
			}
		}
		return result;
	}
	
	// Both vectors must have the same size
	private Vector<BigComplex> multiplyElementByElemnt(Vector<BigComplex> v1, Vector<BigComplex> v2)
	{
		Vector<BigComplex>  r = new Vector<BigComplex>();
		
		if(v1.size() != v2.size())
		{
			System.out.println("multiplyElementByElemnt: error, different vector sizes.");
			return null;
		}
		
		for(int i=0; i<v1.size(); ++i)
		{
			r.add(v1.elementAt(i).multiply(v2.elementAt(i)));
		}
		
		return r;
	}
	
	private BigComplex power(BigComplex base, long exponent)
	{
		BigComplex power = null;
		if(exponent == 0)
		{
			return new BigComplex(1);
		}
		else if(exponent == 1)
		{
			return base;
		}
		else
		{
			for(long i=0; i<exponent; ++i)
			{
				power = base.multiply(base);
			}
			return power;
		}
	}
	
	/**
	 * @post denseRepresentation is reversed
	 * @param denseRepresentation
	 * @return
	 */
	private GenPolynomial<BigComplex> getPolynomial(Vector<BigComplex> denseRepresentation)
	{
		Collections.reverse(denseRepresentation);
		GenPolynomial<BigComplex> polynomial = polynomialFactory.getZERO();
		
		for(int i=0; i<denseRepresentation.size(); ++i)
		{
			ExpVector exponent = ExpVector.create(1,0,denseRepresentation.size()-i-1);
			polynomial = polynomial.sum(denseRepresentation.elementAt(i),exponent);
		}
		
		return polynomial;
	}
}
