package j.http;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class MyRedirectStrategy extends DefaultRedirectStrategy{
	/**
	 * 
	 *
	 */
	public MyRedirectStrategy() {
		super();
	}

	/*
	 *  (non-Javadoc)
	 * @see org.apache.http.client.RedirectStrategy#isRedirected(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	public boolean isRedirected(HttpRequest request, HttpResponse reponse, HttpContext context) throws ProtocolException {
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.apache.http.client.RedirectStrategy#getRedirect(org.apache.http.HttpRequest, org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
	 */
	public HttpUriRequest getRedirect(HttpRequest request, HttpResponse reponse, HttpContext context) throws ProtocolException {
		return null;
	}

}
