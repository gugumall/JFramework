package j.security;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * 
 * @author Administrator
 *
 */
public class CertificateHelper {
	/**
	 * 
	 * @param stream
	 * @return
	 */
	public static Collection<X509Certificate> getX509CertificateFromStream(InputStream stream) {
		if (stream == null) return null;
		Collection<X509Certificate> certs = null;
		try {
			// Create a Certificate Factory
			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			// Read the Trust Certs
			certs = (Collection<X509Certificate>) cf.generateCertificates(stream);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return certs;
	}
}
