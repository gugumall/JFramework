package j.blockchain;

/**
 * 
 * @author ceo
 *
 */
public interface Key{
	public void gen() throws Exception;
	
	public String getPrivateKeyOriginal();
	public String getPrivateKey();
	
	public String getPublicKeyOriginal();
	public String getPublicKey();
}
