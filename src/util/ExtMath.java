/*
 * Creado el 27/10/2013
 */
package util;


//Non-instantiable class
public class ExtMath
{
	// Suppress default constructor for non-instantiability
	private ExtMath() 
	{
		throw new AssertionError();
	}

	public static Integer mod(Integer x, Integer m)
	{
		int rest = x % m;
		
		if(x < 0)
		{
			return new Integer(m + rest);
		}
		else
		{
			return new Integer(rest);
		}
	}
	
	public static Integer mod(Long value, Integer modulo)
	{
		long m = modulo;
		long x = value;
		long rest = x % m;
		
		if(x < 0)
		{
			return new Integer((int)(m + rest));
		}
		else
		{
			return new Integer((int)rest);
		}
	}
	
	public static class ResultExtendedEuclidean
	{
		public Integer d, u, v;
		
		public ResultExtendedEuclidean(Integer d, Integer u, Integer v)
		{
			this.d = d;
			this.u = u;
			this.v = v;
		}
	}
	
	public static ResultExtendedEuclidean extendedEuclidean(Integer x, Integer y)
	{
		int u, v, rest, quotant;
		
		int a = x.intValue();
		int b = y.intValue();
		
		int u1 = 1;
		int v1 = 0;
		int u2 = 0;
		int v2 = 1;
		
		while( b != 0 )
		{
			quotant = a / b;
			rest = a % b;
			a = b;
			b = rest;
			u = u1 - (quotant*u2);
			v = v1 - (quotant*v2);
			u1 = u2;
			u2 = u;
			v1 = v2;
			v2 = v;
		}
		
		return new ResultExtendedEuclidean(new Integer(a), new Integer(u1), new Integer(v1));
	}
	
	public static ResultExtendedEuclidean extendedEuclidean(Long x, Integer y)
	{
		long u, v, rest, quotant;
		
		long a = x.intValue();
		long b = y.intValue();
		
		long u1 = 1;
		long v1 = 0;
		long u2 = 0;
		long v2 = 1;
		
		while( b != 0 )
		{
			quotant = a / b;
			rest = a % b;
			a = b;
			b = rest;
			u = u1 - (quotant*u2);
			v = v1 - (quotant*v2);
			u1 = u2;
			u2 = u;
			v1 = v2;
			v2 = v;
		}
		
		return new ResultExtendedEuclidean(new Integer((int)a), new Integer((int)u1), new Integer((int)v1));
	}
}
