package src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.jas.arith.BigInteger;

public class FindRoots {
	public static void main(String[] args) throws IOException 
	{
		ZpFieldMultiplications z41 = new ZpFieldMultiplications(41);
		ZpFieldMultiplications z257 = new ZpFieldMultiplications(257);
		ZpFieldMultiplications zp = new ZpFieldMultiplications(65537);
		
		FileWriter rootFich = new FileWriter("./roots.txt");
        PrintWriter pw = new PrintWriter(rootFich);
        
        BigInteger root = new BigInteger(0);
		
		for(int m = 2; m < 16; m = m*2){
			root = z41.rootOfUnity(m, pw).getInteger();
			pw.println(z41.prime + "\t" + m + "\t" + root + "\t");
		}
		
		for(int m = 2; m < z257.prime; m = m*2){
			root = z257.rootOfUnity(m, pw).getInteger();
			pw.println(z257.prime + "\t" + m + "\t" + root + "\t");
		}
		
		for(int m = 2; m < zp.prime; m = m*2){
			root = zp.rootOfUnityZp(m, pw).getInteger();
			pw.println(zp.prime + "\t" + m + "\t" + root + "\t");
			System.out.println(m);
		}
		
		pw.close();
	}
}
