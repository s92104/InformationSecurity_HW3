import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class InformationSecurity_HW3 {
	//測試互質
	public static boolean isRelativelyPrime(BigInteger a,BigInteger b)
	{
		if(a.gcd(b).equals(BigInteger.ONE))
			return true;
		return false;
	}
	//找反元素
	public static BigInteger findInverseElement(BigInteger t, BigInteger n)
	{
		BigInteger a=n;
		BigInteger b=t;
		BigInteger x=BigInteger.ONE;
		BigInteger x_plum=BigInteger.ZERO;
		BigInteger q;
		BigInteger r=BigInteger.ONE;
		while(!r.equals(BigInteger.ZERO))
		{
			q=a.divide(b);
			r=a.mod(b);
			if(!r.equals(BigInteger.ZERO))
			{
				a=b;
				b=r;
				BigInteger recX=x;		
				x=x_plum.subtract(q.multiply(x));
				x_plum=recX;
			}		
		}
		if(x.compareTo(BigInteger.ZERO)==-1)
			return x.add(n);
		return x;
	}
	public static void main(String[] args)
	{	
		Scanner scanner=new Scanner(System.in);
		String cmd;
		do {
			BigInteger p = generateBigPrime();
			BigInteger q = generateBigPrime();
			BigInteger n = p.multiply(q);
			BigInteger fi_n = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
			BigInteger e = BigInteger.ZERO;
			//找與fi(n)互質的數
			for (BigInteger i = new BigInteger("3"); i.compareTo(fi_n)==-1; i=i.add(BigInteger.ONE))
			{
				if (isRelativelyPrime(i, fi_n))
				{
					e = i;
					break;
				}
			}	
			BigInteger d=e.modInverse(fi_n);
			//輸出相關參數
			System.out.println("p:"+p);
			System.out.println("q:"+q);
			System.out.println("e:"+e);
			System.out.println("d:"+d);	
			
			System.out.println("0.重找大質數\t1.加密\t2.解密");
			cmd=scanner.nextLine();
			while(!cmd.equals("0"))
			{
				String dataString;
				BigInteger data;
				//加密
				if(cmd.equals("1"))
				{
					System.out.print("PlainText:");
					dataString=scanner.nextLine();
					data=new BigInteger(dataString);
					System.out.println("CipherText:"+power(data, e, n));
				}
				//解密
				else
				{
					System.out.print("CipherText:");
					dataString=scanner.nextLine();
					data=new BigInteger(dataString);
					System.out.println("PlainText:"+CRT(data,d,p,q));
				}
				System.out.println("0.重找大質數\t1.加密\t2.解密");
				cmd=scanner.nextLine();
			}
		} while (cmd.equals("0"));
		
		
	}
	//產生大質數
	public static BigInteger generateBigPrime()
	{
		String prime;
		Random random=new Random();
		do {
			prime="1";
			for(int i=0;i<510;i++)
				prime+=random.nextInt(2);
			prime+="1";
		} while (!MillerRabinTest(prime));
		return new BigInteger(prime,2);
	}
	//Miller-Rabin Test
	public static boolean MillerRabinTest(String prime)
	{
		BigInteger n=new BigInteger(prime,2);
		BigInteger m =n.subtract(BigInteger.ONE);
		BigInteger k =BigInteger.ZERO;
		while(m.mod(new BigInteger("2")).equals(BigInteger.ZERO))
		{
			m=m.divide(new BigInteger("2"));
			k=k.add(BigInteger.ONE);
		}
		
		BigInteger a=new BigInteger("2");
		BigInteger b=power(a, m, n);
		if(!b.equals(BigInteger.ONE) && !b.equals(n.subtract(BigInteger.ONE)))
		{
			BigInteger i=BigInteger.ONE;
			while(i.compareTo(k)==-1 && !b.equals(n.subtract(BigInteger.ONE)))
			{
				b=power(b, new BigInteger("2"), n);
				if(b.equals(BigInteger.ONE))
					return false;
				i=i.add(BigInteger.ONE);
			}
			if(!b.equals(n.subtract(BigInteger.ONE)))
				return false;
		}
		return true;
	}
	//Square&multiply
	public static BigInteger power(BigInteger base,BigInteger pow,BigInteger mod)
	{
		BigInteger result=BigInteger.ONE;
		String powString=pow.toString(2);
		for(int i=0;i<powString.length();i++)
		{
			if(i!=0)
				result=result.modPow(new BigInteger("2"), mod);
			if(powString.charAt(i)=='1')
			{
				result=result.multiply(base);
				result=result.mod(mod);
			}
		}
		return result;
	}
	//Chinese Remainder Theorem	
	public static BigInteger CRT(BigInteger c,BigInteger d,BigInteger p,BigInteger q)
	{
		BigInteger d_p=d.mod(p.subtract(BigInteger.ONE));
		BigInteger d_q=d.mod(q.subtract(BigInteger.ONE));
		BigInteger q_p=q.modInverse(p);
		BigInteger m_p=c.mod(p).modPow(d_p, p);
		BigInteger m_q=c.mod(q).modPow(d_q, q);
		BigInteger v=q_p.multiply(m_p.subtract(m_q)).mod(p);
		return m_q.add(q.multiply(v));
	}
}
