package src;

import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import edu.jas.arith.BigComplex;
import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.arith.ModIntegerRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.structure.Power;

public class ZpFieldMultiplications {
	
	long prime;
	ModIntegerRing fact;
	GenPolynomialRing<ModInteger> ring;
	
	public ZpFieldMultiplications(long prime){
		this.prime = prime;
		fact = new ModIntegerRing(prime);
		String[] var = new String[] { "x" };
		// Polynomial factory
		ring = new GenPolynomialRing<ModInteger>( fact, 1,var);
	}

	public void main() {

		Vector<ModInteger> test = new Vector<ModInteger>();
		/*test.add(new ModInteger(fact,15));
		test.add(new ModInteger(fact,1));
		test.add(new ModInteger(fact,11));
		test.add(new ModInteger(fact,3));
		test.add(new ModInteger(fact,0));
		test.add(new ModInteger(fact,0));
		test.add(new ModInteger(fact,0));
		test.add(new ModInteger(fact,0));
		
		Vector<ModInteger> res = this.FFT(3, new ModInteger(fact,9), test);*/
	
		System.out.println("gen: " + ring.generators().toString());

		// Generate two random polynomials with the specified max degree
		GenPolynomial<ModInteger> p1 = ring.random(90);
		GenPolynomial<ModInteger> p2 = ring.random(10);
		
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
		System.out.println("fft: " + fftMultiplication.toString());
		

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
		
	public GenPolynomial<ModInteger> multiplyFFT(GenPolynomial<ModInteger> p1,
			GenPolynomial<ModInteger> p2)
	{
		GenPolynomial<ModInteger> result;
		
		// Get the minimum power of two that's greater than the sum of the degrees of the polynomials
		long m = this.getM(p1, p2);
		long maxDegree = (long) Math.pow(2, m);
		
		System.out.println("M: " + m);
		
		ModInteger w = rootOfUnity(maxDegree);
		
		System.out.println("w: " + w);
		
		// Get the dense representations of the polynomials
		Vector<ModInteger> denseP1 = denseRepresentation(p1, maxDegree);
		Vector<ModInteger> denseP2 = denseRepresentation(p2, maxDegree);
		
		Vector<ModInteger> fftP1 = FFT(m, w, denseP1);
		Vector<ModInteger> fftP2 = FFT(m, w, denseP2);
		
		Vector<ModInteger> mult = multiplyElementByElemnt(fftP1, fftP2);
		
		// Inverse FFT of the multiplication
		Vector<ModInteger> resultDense = FFT(m, w.inverse(), mult);
		ModInteger mult_inverso = new ModInteger(fact,(long)Math.pow(2, m));
		mult_inverso = mult_inverso.inverse();
		
		for(int i=0; i<resultDense.size(); ++i)
		{
			resultDense.set(i, resultDense.elementAt(i).multiply(mult_inverso));
		}
		
		result = getPolynomial(resultDense);
		
		return result;
	}
	
	/**
	 * @post denseRepresentation is reversed
	 * @param denseRepresentation
	 * @return
	 */
	private GenPolynomial<ModInteger> getPolynomial(Vector<ModInteger> denseRepresentation)
	{
		Collections.reverse(denseRepresentation);
		GenPolynomial<ModInteger> polynomial = ring.getZERO();
		
		for(int i=0; i<denseRepresentation.size(); ++i)
		{
			ExpVector exponent = ExpVector.create(1,0,denseRepresentation.size()-i-1);
			polynomial = polynomial.sum(denseRepresentation.elementAt(i),exponent);
		}
		
		return polynomial;
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
	
	public Vector<ModInteger> evenVector(Vector<ModInteger> vector)
	{
		Vector<ModInteger> result = new Vector<ModInteger>();
		for(int i = 0; i < vector.size(); i++){
			if((i+1) % 2 == 0){
				result.add(vector.elementAt(i));
			}
		}
		return result;
	}
	
	public Vector<ModInteger> oddVector(Vector<ModInteger> vector){
		Vector<ModInteger> result = new Vector<ModInteger>();
		for(int i = 0; i < vector.size(); i++){
			if((i+1) % 2 != 0){
				result.add(vector.elementAt(i));
			}
		}
		return result;
	}
	
	// Calcula la primera raiz enésima de la unidad
	/*public ModInteger rootOfUnity(long maxDegree){
		// ModInteger Factory
		ModIntegerRing mfac = new ModIntegerRing(prime);
		
		
		for(int i = 2; i < prime; i++){
			ModInteger sqrt = new ModInteger(mfac,i);
				
			sqrt = this.power(sqrt,maxDegree);
			
			if(!sqrt.equals(mfac.getONE())){
				return (new ModInteger(fact,(int)(Math.pow(i,2.0) % prime)));
			}
		}
	
		return new ModInteger(fact,-1);
	}*/
	
	// Calcula la primera raiz enésima de la unidad
	public ModInteger rootOfUnity(long m){
		// ModInteger Factory
		ModIntegerRing mfac = new ModIntegerRing(prime);
		
		
		for(int i = 2; i < prime; i++){
			ModInteger sqrt = new ModInteger(mfac,i);
			boolean aux = true;
			sqrt = this.power(sqrt,m);
			
			if(sqrt.equals(mfac.getONE())){
				for(long j = m-1; j > 0; j--){
					sqrt = new ModInteger(mfac,i);
					sqrt = this.power(sqrt,j);
					if(sqrt.equals(mfac.getONE())){
						aux = false;
					}
				}
				if(aux == true){
					return (new ModInteger(fact,i));
				}
				aux = true;
			}
		}
	
		return new ModInteger(fact,-1);
	}
	
	public Vector<ModInteger> FFT(long m, ModInteger w, Vector<ModInteger> densePoly)
	{
		Vector<ModInteger> result = new Vector<ModInteger>();
		
		if(m == 0)
		{
			result.add(densePoly.firstElement());
		}
		else
		{
			Vector<ModInteger> b = oddVector(densePoly);
			Vector<ModInteger> c = evenVector(densePoly);
			
			Vector<ModInteger> B = FFT(m-1, w.multiply(w), b);
			Vector<ModInteger> C = FFT(m-1, w.multiply(w), c);
			
			ModInteger[] temp = new ModInteger[(int) Math.pow(2, m)];
			for(int i=0; i<Math.pow(2,m-1); ++i)
			{
				temp[i] = B.elementAt(i).sum(C.elementAt(i).multiply(power(w,i)));
				temp[(int) (Math.pow(2, m-1) + i)] = B.elementAt(i).subtract(C.elementAt(i).multiply(power(w,i) ) );
			}
	
			for(int i=0; i<Math.pow(2, m); ++i)
			{
				result.add(temp[i]);
			}
		}
		
		return result;
	}
	
	/**
	 * Calculates base^exp
	 * @param base
	 * @param maxDegree
	 * @return
	 */
	private ModInteger power(ModInteger base, long maxDegree){
		ModInteger result =  base.copy();
		ModInteger partialExp = base.copy();
		
		if(maxDegree == 0){
			return (new ModInteger(fact,1));
		}
		for(int i = 2; i <= maxDegree; i++){
			result = result.multiply(partialExp);
		}
		
		return result;
	}
	
	private Vector<ModInteger> denseRepresentation(GenPolynomial<ModInteger> p, long maxDegree)
	{
		Vector<ModInteger> denseRepresentation = new Vector<ModInteger>((int)maxDegree);
		
		Monomial<ModInteger> monomial = null;
		
		long lastDegree = maxDegree;
		
		Iterator<Monomial<ModInteger>> iter = p.iterator();
      while(iter.hasNext())
      {
      	// Get an element of p.
      	monomial = iter.next();
      	if(monomial.e.degree()+1 < lastDegree)
      	{
      		// Fill with zeros
      		for(long i=monomial.e.degree()+1; i<lastDegree; ++i)
      		{
      			denseRepresentation.add(new ModInteger(fact,"0"));
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
   			denseRepresentation.add(new ModInteger(fact,"0"));
   		}
      }
		
      Collections.reverse(denseRepresentation);
	  return denseRepresentation;
	}
	
	
	// Both vectors must have the same size
	private Vector<ModInteger> multiplyElementByElemnt(Vector<ModInteger> v1, Vector<ModInteger> v2)
	{
		Vector<ModInteger>  r = new Vector<ModInteger>();
		
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
}
