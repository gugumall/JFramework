package j.blockchain.bitcoin;

import j.security.SHA256;
import j.util.JUtilBytes;
import j.util.JUtilRandom;

import org.bitcoinj.core.Base58;

public class Key implements j.blockchain.Key{
	private String privateKeyOriginal;
	private String privateKey;
	private String publicKeyOriginal;
	private String publicKey;

	@Override
	public void gen() throws Exception{
		privateKeyOriginal=genPrivateKeyOriginal();

		privateKey=privateKeyOriginal;
		privateKey="80"+privateKeyOriginal+"01";
		
		String temp=SHA256.encode(privateKey);
		temp=SHA256.encode(temp);
		privateKey=privateKey+temp.substring(0,8);
		privateKey=privateKey.toUpperCase();
		
		System.out.println(privateKey);

		privateKey=Base58.encode(privateKey.getBytes());
		System.out.println(privateKey);
		

		System.out.println(JUtilBytes.byte2Hex(Base58.decode(privateKey)));
	}

	/**
	 * 
	 * @return
	 */
	private static String genPrivateKeyOriginal(){
		StringBuffer buffer=new StringBuffer();
		while(buffer.length()<32){
			String temp="";
			for(int i=0;i<4;i++){
				int r=JUtilRandom.nextInt(2);
				temp+=r;
			}
			temp=Long.toString(Long.parseLong(temp,2),16);
			buffer.append(temp);
		}

		return buffer.toString().toUpperCase();
	}

	@Override
	public String getPrivateKeyOriginal(){
		return this.privateKeyOriginal;
	}

	@Override
	public String getPrivateKey(){
		return this.privateKey;
	}

	@Override
	public String getPublicKeyOriginal(){
		return this.publicKeyOriginal;
	}

	@Override
	public String getPublicKey(){
		return this.publicKey;
	}

	public static void main(String[] args) throws Exception{
		Key k=new Key();
		k.gen();
	}
}
