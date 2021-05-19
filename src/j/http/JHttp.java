package j.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.DefaultCookieSpecProvider;
import org.apache.http.impl.cookie.RFC6265CookieSpecProvider;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.ocr.OtherDemo.Tess4J;

import j.Properties;
import j.common.Global;
import j.common.JArray;
import j.common.JObject;
import j.common.JProperties;
import j.fs.JDFSFile;
import j.sys.AppConfig;
import j.sys.SysUtil;
import j.util.ConcurrentMap;
import j.util.JUtilCompressor;
import j.util.JUtilInputStream;
import j.util.JUtilJSON;
import j.util.JUtilMath;
import j.util.JUtilRandom;
import j.util.JUtilString;
import j.util.JUtilTimestamp;

/**
 * @author 肖炯
 * 
 */
public class JHttp{
	public static final String default_user_agent="Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0)";
	public static final int default_redirects=1;
	public static final int default_retries=1;
	public static final long default_retry_interval=500;

	private static JHttp[] instances = new JHttp[JProperties.getJHttpInstances()];
	private static ConcurrentMap configOfClients=new ConcurrentMap();
	private PoolingHttpClientConnectionManager poolingmgr;
	//private SSLConnectionSocketFactory factory;
	private HttpClient[] clients = new HttpClient[JProperties.getClientsOfJHttpInstance()];
	private CookieStore cookieStore = new BasicCookieStore();
	private String cookieSpec=CookieSpecs.DEFAULT;
	private boolean redirectsEnabled=true;
	
	
	static {
//		System.setProperty("jdk.tls.allowUnsafeServerCertChange", "true");
//		System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
//		System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");
	}

	/**
	 * 
	 *
	 */
	private JHttp() {
	}

	/**
	 * 
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	public static JHttp getInstance(){
		synchronized(default_user_agent){			
			int random=instances.length==1?0:JUtilRandom.nextInt(instances.length);
			JHttp jhttp =instances[random];
			if(jhttp==null){
				try{
					jhttp = createSelfSigned(Properties.getConfigPath()+"/server.jks","20081016",new String[] {"SSLv3","TLSv1","TLSv1.1","TLSv1.2"});
					
					instances[random]=jhttp;
				}catch(Exception e){
					e.printStackTrace();
					try{
						jhttp.destroy();
					}catch(Exception ex){}
					return null;
				}
			}
			

			for(int i=0;i<jhttp.clients.length;i++){
				jhttp.clients[i]=jhttp.createClient();
			}
			
			return jhttp;
		}
	}

	/**
	 * 
	 * @param certFilePath
	 * @param certFilePassword
	 * @return
	 */
	public static JHttp getInstance(String certFilePath, String certFilePassword){
		synchronized(default_user_agent){			
			int random=instances.length==1?0:JUtilRandom.nextInt(instances.length);
			JHttp jhttp =instances[random];
			if(jhttp==null){
				try{
					jhttp = createSelfSigned(certFilePath,certFilePassword,new String[] {"SSLv3","TLSv1","TLSv1.1","TLSv1.2"});
					
					instances[random]=jhttp;
				}catch(Exception e){
					e.printStackTrace();
					try{
						jhttp.destroy();
					}catch(Exception ex){}
					return null;
				}
			}
			

			for(int i=0;i<jhttp.clients.length;i++){
				jhttp.clients[i]=jhttp.createClient();
			}
			
			return jhttp;
		}
	}
	
	/**
	 * 
	 * @param certFilePath
	 * @param password
	 * @param keyStoreType
	 * @return
	 * @throws Exception
	 */
	public static JHttp createSelfSigned(String certFilePath,String password,String[] protocols) throws Exception{
		JHttp jhttp=new JHttp();

        SSLContext ctx = SSLContexts.custom().loadTrustMaterial(new File(certFilePath),password.toCharArray(), new TrustSelfSignedStrategy()).build();
        ctx.init(null, new TrustManager[] { new MyTrustManager() }, null);
        
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
                ctx,
                protocols,
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        
        jhttp.poolingmgr = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", factory)
                    .build(),
                null,
                null,
                null,
                5000,
                TimeUnit.MILLISECONDS);
		
        jhttp.poolingmgr.setDefaultMaxPerRoute(100);
        jhttp.poolingmgr.setMaxTotal(1000);
        
        return jhttp;
	}
	
	/**
	 * 
	 * @param certFilePath
	 * @param password
	 * @param keyStoreType
	 * @return
	 * @throws Exception
	 */
	public static JHttp createSelfSignedX(URL certFilePath,String password,String[] protocols) throws Exception{
		JHttp jhttp=new JHttp();

        SSLContext ctx = SSLContexts.custom().loadTrustMaterial(certFilePath,password.toCharArray(), new TrustSelfSignedStrategy()).build();
        ctx.init(null, new TrustManager[] { new MyTrustManager() }, null);
        
        SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
                ctx,
                protocols,
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        
        jhttp.poolingmgr = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", factory)
                    .build(),
                null,
                null,
                null,
                5000,
                TimeUnit.MILLISECONDS);
		
        jhttp.poolingmgr.setDefaultMaxPerRoute(100);
        jhttp.poolingmgr.setMaxTotal(1000);
        
        return jhttp;
	}
	
	/**
	 * 
	 * @param certFilePath
	 * @param password
	 * @param keyStoreType
	 * @param protocal
	 * @return
	 * @throws Exception
	 */
	public static JHttp create(String certFilePath,String password,String keyStoreType,String protocal) throws Exception{
		JHttp jhttp=new JHttp();

        KeyStore trustStore = KeyStore.getInstance(keyStoreType); 
        FileInputStream fis=new FileInputStream(new File(certFilePath));
        try{
        	trustStore.load(fis, password.toCharArray());
        }finally {
        	fis.close();
        }
        
        KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyFactory.init(trustStore, password.toCharArray()); 

        SSLContext ctx = SSLContext.getInstance(protocal);
		ctx.init(keyFactory.getKeyManagers(), new TrustManager[]{new MyTrustManager()}, null);
		
		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
	                ctx,
	                new String[] {"SSLv3","TLSv1","TLSv1.1","TLSv1.2"},
	                null,
	                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
		
		jhttp.poolingmgr = new PoolingHttpClientConnectionManager(
	                RegistryBuilder.<ConnectionSocketFactory>create()
	                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
	                    .register("https", factory)
	                    .build(),
	                null,
	                null,
	                null,
	                5000,
	                TimeUnit.MILLISECONDS);
			
		jhttp.poolingmgr.setDefaultMaxPerRoute(10);
		jhttp.poolingmgr.setMaxTotal(100);
		
		jhttp.clients = new HttpClient[1];
		for(int i=0;i<jhttp.clients.length;i++){
			jhttp.clients[i]=jhttp.createClient();
		}
        
        return jhttp;
	}
	
	/**
	 * 
	 * @param cookieSpec
	 */
	public void setCookieSpec(String cookieSpec) {
		this.cookieSpec=cookieSpec;
	}
	
	/**
	 * 
	 * @param enabled
	 */
	public void setRedirectsEnabled(boolean enabled) {
		this.redirectsEnabled=enabled;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getRedirectsEnabled() {
		return this.redirectsEnabled;
	}
	
	/**
	 * 
	 *
	 */
	public void destroy(){
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		destroy();
	}
	
	/**
	 * 
	 * @param client
	 * @return
	 */
	public static RequestConfig getConfigOfClient(HttpClient client) {
		if(configOfClients==null) return null;
		return (RequestConfig)configOfClients.get(client.toString());
	}
	
	/**
	 * 
	 * @param client
	 * @param config
	 */
	public static void setConfigOfClient(HttpClient client, RequestConfig config) {
		if(configOfClients==null) return;
		configOfClients.put(client.toString(), config);
	}

	/**
	 * 
	 * @return
	 */
	public HttpClient createClient() {
		return createClient(15000);
	}

	/**
	 * 
	 * @param timeout in milliseconds
	 * @return
	 */
	public HttpClient createClient(int timeout) {		
		int redirects=default_redirects;
		if(JUtilMath.isInt(AppConfig.getPara("HTTP","redirects"))){
			redirects=Integer.parseInt(AppConfig.getPara("HTTP","redirects"));
		}
		
		return createClient(timeout, redirects);
	}

	/**
	 * 
	 * @param timeout
	 * @param redirects
	 * @return
	 */
	public HttpClient createClient(int timeout, int redirects) {
		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec(this.cookieSpec)
				.setMaxRedirects(redirects)
				.setRedirectsEnabled(getRedirectsEnabled())
				.setRelativeRedirectsAllowed(getRedirectsEnabled())
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout).build();
		
		PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
		Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
		        .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider(publicSuffixMatcher))
		        .register(CookieSpecs.STANDARD, new RFC6265CookieSpecProvider(publicSuffixMatcher))
		        .build();
		
		CloseableHttpClient client = HttpClients.custom()
				.setConnectionManager(poolingmgr)
		        .setDefaultCookieSpecRegistry(r)
				.setDefaultCookieStore(cookieStore)
				.setDefaultRequestConfig(requestConfig)
				.setRedirectStrategy(new NoRedirectStrategy())
				.build();
		
		configOfClients.put(client.toString(), requestConfig);
		
		return client;
	}
	
	/**
	 * 
	 * @return
	 */
	public HttpClient createClient(String host,int port,String scheme,String username, String password) {
		return createClient(15000,host,port,scheme,username,password);
	}

	/**
	 * 
	 * @param timeout in milliseconds
	 * @return
	 */
	public HttpClient createClient(int timeout,String host,int port,String scheme,String username, String password) {
		HttpHost proxy=new HttpHost(host,port,scheme==null?"http":scheme);
		
		CredentialsProvider credsProvider=null;
		if(username!=null && !"".equals(username)) {
			credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username, password));
		}
		
		int redirects=default_redirects;
		if(JUtilMath.isInt(AppConfig.getPara("HTTP","redirects"))){
			redirects=Integer.parseInt(AppConfig.getPara("HTTP","redirects"));
		}
		
		RequestConfig requestConfig = RequestConfig.custom()
				.setCookieSpec(this.cookieSpec)
				.setMaxRedirects(redirects)
				.setRedirectsEnabled(getRedirectsEnabled())
				.setSocketTimeout(timeout)
				.setConnectTimeout(timeout)
				.setProxy(proxy)
				.build();
		
		PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.getDefault();
		Registry<CookieSpecProvider> r = RegistryBuilder.<CookieSpecProvider>create()
		        .register(CookieSpecs.DEFAULT, new DefaultCookieSpecProvider(publicSuffixMatcher))
		        .register(CookieSpecs.STANDARD, new RFC6265CookieSpecProvider(publicSuffixMatcher))
		        .build();
	
		CloseableHttpClient client = null;
		if(credsProvider!=null) {
			client=HttpClients.custom()
			.setConnectionManager(poolingmgr)
			.setDefaultCookieSpecRegistry(r)
			.setDefaultCookieStore(cookieStore)
			.setDefaultRequestConfig(requestConfig)
			.setProxy(proxy)
			.setDefaultCredentialsProvider(credsProvider)
			.build();
		}else {
			client=HttpClients.custom()
			.setConnectionManager(poolingmgr)
			.setDefaultCookieSpecRegistry(r)
			.setDefaultCookieStore(cookieStore)
			.setDefaultRequestConfig(requestConfig)
			.setProxy(proxy)
			.build();
		}
		
		configOfClients.put(client.toString(), requestConfig);

		return client;
	}

	/**
	 * 
	 * @return
	 */
	public HttpClient getDefaultClient() {
		int selector=JUtilRandom.nextInt(this.clients.length);
		return this.clients[selector];
	}

	/**
	 * @deprecated
	 * @param client
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public static void setProxy(HttpClient client,String host,int port,String username, String password) {
	
	}

	/**
	 * @deprecated
	 * @param client
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */
	public static void setProxyHttps(HttpClient client,String host,int port,String username, String password) {

	}

	/**
	 * @deprecated
	 * @param client
	 */
	public static void clearProxy(HttpClient client) {
	}
	
	/**
	 * 
	 * @param context
	 * @param response
	 */
	private void getStatusAndHeaders(JHttpContext context,HttpResponse response){
		if(context==null||response==null) return;
		
		StatusLine status=response.getStatusLine();
		if(status!=null) {
			context.setStatus(status.getStatusCode());
			if(HttpStatus.SC_MOVED_TEMPORARILY==status.getStatusCode()) {
				Header header=response.getFirstHeader("Location");
				if(header!=null) {
	            	context.addResponseHeader(header.getName(),header.getValue());
				}
			}
		}

        Header[] headers = response.getAllHeaders();
        if(headers!=null){
            for(int i=0;i<headers.length;i++){
            	context.addResponseHeader(headers[i].getName(),headers[i].getValue());
            }
        }
        
        List<Cookie> cookies=cookieStore.getCookies();
        if(cookies!=null) {
        	for(int i=0; i<cookies.size(); i++) {
        		Cookie c=cookies.get(i);
        		context.addCookie(c.getName(), 
        				c.getValue(),
        				c.getVersion(),
        				c.getDomain(),
        				c.getPath());
        	}
        }
	}
	
	/**
	 * 
	 * @param context
	 * @param client
	 * @param request
	 */
	private void initRequest(JHttpContext context,HttpClient client,HttpRequestBase request){
		if(context==null||request==null) return;
		context.setRequest(request);
		
		if (context.getRequestHeader("User-Agent") == null) {
			String agent = AppConfig.getPara("HTTP", "User-Agent");
			if (agent == null) {
				request.addHeader("User-Agent",default_user_agent);
			} else {
				request.addHeader("User-Agent", agent);
			}
		}

		
		String sCookies="";
		Map cookies=context.getCookies();
		if(cookies!=null&&!cookies.isEmpty()){
			for(Iterator it=cookies.keySet().iterator();it.hasNext();){
				String name=(String)it.next();
				JHttpCookie c=(JHttpCookie)cookies.get(name);
				
				if(!"".equals(sCookies)) sCookies+="; ";
				sCookies+=name+"="+c.getValue();
				
				BasicClientCookie cookie = new BasicClientCookie(name, c.getValue());
				cookie.setVersion(c.getVersion());
				cookie.setDomain(c.getDomain());
				cookie.setPath(c.getPath());
				cookieStore.addCookie(cookie);
			}
		}
		
		if(!"".equals(sCookies)) {
			context.addRequestHeader("Cookie", sCookies);
		}
		
		Map headers=context.getRequestHeaders();
		if(headers!=null&&!headers.isEmpty()){
			for(Iterator it=headers.keySet().iterator();it.hasNext();){
				String name=(String)it.next();
				String value=(String)headers.get(name);
				
				if(request.containsHeader(name)){
					request.removeHeaders(name);					
				}
				
				request.addHeader(name,value);
			}
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param request
	 * @param params
	 * @throws Exception
	 */
	private static void addParams(JHttpContext context,HttpPost request,Map params) throws Exception{
		if (params != null && !params.isEmpty()) {
			List formparams = new ArrayList();
			Iterator keys = params.keySet().iterator();
			while (keys.hasNext()) {
				Object key = keys.next();
				Object val = params.get(key);
				if(val!=null && val instanceof String[]) {
					String[] vals=(String[])val;
					for(int i=0; i<vals.length;i++) {
						formparams.add(new BasicNameValuePair((String)key, vals[i]));
					}
				}else if(val!=null && val instanceof List) {
					List vals=(List)val;
					for(int i=0; i<vals.size();i++) {
						formparams.add(new BasicNameValuePair((String)key, (String)vals.get(i)));
					}
				}else {
					formparams.add(new BasicNameValuePair((String)key, val==null?"":val.toString()));
				}
			}
			if(context.getRequestEncoding()==null) request.setEntity(new UrlEncodedFormEntity(formparams));
			else request.setEntity(new UrlEncodedFormEntity(formparams,context.getRequestEncoding()));
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param request
	 * @param strings
	 * @throws Exception
	 */
	private static void addParams(JHttpContext context,HttpPost request,Map parts,Map strings) throws Exception{
		HttpEntity reqEntity=null;
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		if(parts!=null&&!parts.isEmpty()){
			Iterator keys = parts.keySet().iterator();
			while (keys.hasNext()) {
				Object key = keys.next();
				Object val = parts.get(key);
				if(val instanceof File){					
					builder=builder.addPart((String)key, new FileBody((File)val));
				}else if(val instanceof byte[]){					
					builder=builder.addPart((String)key, new ByteArrayBody((byte[])val,(String)key));
				}else if(val instanceof InputStream){					
					builder=builder.addPart((String)key, new InputStreamBody((InputStream)val,(String)key));
				}else if(val instanceof String[]){	
					String[] array=(String[])val;
					for(int i=0; i<array.length; i++) {
						if(context.getRequestEncoding()!=null&&!"".equals(context.getRequestEncoding())){
							builder=builder.addPart((String)key, new StringBody(array[i],ContentType.create(context.getContentType()==null?"text/plain":context.getContentType(),Charset.forName(context.getRequestEncoding()))));
						}else{
							builder=builder.addPart((String)key, new StringBody(array[i],ContentType.TEXT_PLAIN));
						}
					}
				}else if(val!=null && val instanceof List) {
					List vals=(List)val;
					for(int i=0; i<vals.size();i++) {
						if(context.getRequestEncoding()!=null&&!"".equals(context.getRequestEncoding())){
							builder=builder.addPart((String)key, new StringBody((String)vals.get(i),ContentType.create(context.getContentType()==null?"text/plain":context.getContentType(),Charset.forName(context.getRequestEncoding()))));
						}else{
							builder=builder.addPart((String)key, new StringBody((String)vals.get(i),ContentType.TEXT_PLAIN));
						}
					}
				}else if(val instanceof String){	
					if(context.getRequestEncoding()!=null&&!"".equals(context.getRequestEncoding())){
						builder=builder.addPart((String)key, new StringBody((String)val,ContentType.create(context.getContentType()==null?"text/plain":context.getContentType(),Charset.forName(context.getRequestEncoding()))));
					}else{
						builder=builder.addPart((String)key, new StringBody((String)val,ContentType.TEXT_PLAIN));
					}
				}
			}
		}
		
		
		if(strings!=null&&!strings.isEmpty()){
			Iterator keys = strings.keySet().iterator();
			while (keys.hasNext()) {
				Object key = keys.next();
				Object val = strings.get(key);
				if(context.getRequestEncoding()!=null&&!"".equals(context.getRequestEncoding())){
					builder=builder.addPart((String)key, new StringBody((String)val,ContentType.create(context.getContentType()==null?"text/plain":context.getContentType(),Charset.forName(context.getRequestEncoding()))));
				}else{
					builder=builder.addPart((String)key, new StringBody((String)val,ContentType.TEXT_PLAIN));
				}
			}
		}
		
		reqEntity=builder.build();
		
		request.setEntity(reqEntity);
	}
	
	/**
	 * 
	 * @param context
	 * @param client
	 * @param request
	 * @param encoding
	 * @param responseType 0 - String, 1 - InputStream
	 * @throws Exception
	 */
	private void execute(JHttpContext context,HttpClient client,HttpRequestBase request,String encoding,int responseType) throws Exception{
		int retries=1;
		
		if(context!=null&&context.getRetries()>0){
			retries=context.getRetries();
		}else if(JUtilMath.isInt(AppConfig.getPara("HTTP","retries"))){
			retries=Integer.parseInt(AppConfig.getPara("HTTP","retries"));
		}
		
		long interval=default_retry_interval;
		if(context!=null&&context.getRetryInterval()>0){
			interval=context.getRetryInterval();
		}else if(JUtilMath.isLong(AppConfig.getPara("HTTP","retry-interval"))){
			interval=Long.parseLong(AppConfig.getPara("HTTP","retry-interval"));
		}
		
		if(retries<=0) retries=1;
		while(retries>0){
			retries--;
			try{
				doExecute(context,client,request,encoding,responseType,retries==0?true:false);
				if(context!=null
						&&(context.getStatus()==200 || context.isErrorCodeAllowed(context.getStatus()))) return;
				
				Thread.sleep(interval);
			}catch(Exception e){
				if(retries==0) throw e;
			}
		}
		
		if(context!=null
				&&!context.isErrorCodeAllowed(context.getStatus())){
			throw new Exception("get "+context.getStatus()+" error while request "+request.getURI());
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param client
	 * @param request
	 * @param encoding
	 * @param responseType 0 - String, 1 - InputStream
	 * @throws Exception
	 */
	private void doExecute(JHttpContext context,HttpClient client,HttpRequestBase request,String encoding,int responseType,boolean abort) throws Exception{
		try {			
			RequestConfig config=(RequestConfig)configOfClients.get(client.toString());
			if(config!=null) request.setConfig(config);
			
			HttpResponse response = client.execute(request);
			getStatusAndHeaders(context,response);
		
			HttpEntity entity = response.getEntity();			
			if (entity != null) {		
				if(responseType==0){
		            boolean isGzip=isGzip(entity);         
		            String responseText=null;
		            if(isGzip){
		            	if(encoding!=null) responseText = JUtilCompressor.readGZipStream2String(entity.getContent(),encoding);
		            	else responseText = JUtilCompressor.readGZipStream2String(entity.getContent());
		            }else{
		            	if(encoding!=null) responseText = JUtilInputStream.string(entity.getContent(),encoding);
		            	else responseText = JUtilInputStream.string(entity.getContent());
		            }
		            context.setResponseText(responseText);
					EntityUtils.consume(entity);
					if(abort){
						try{
							request.releaseConnection();
							request.abort();
						}catch(Exception e){}
					}
				}else if(responseType==1){
					context.setResponseStream(entity.getContent());
				}	     
			}
		} catch (Exception e) {
			try{
				if(request!=null&&abort) request.abort();
			}catch(Exception ex){}
			throw e;
		}
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	private static boolean isGzip(HttpEntity entity){
		if(entity==null) return false;
		Header header = entity.getContentEncoding();
        return (header != null&&"gzip".equalsIgnoreCase(header.getValue()));
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public JHttpContext get(JHttpContext context,HttpClient client, String url) throws Exception {
		return get(context,client,url,(String)null);
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public JHttpContext get(JHttpContext context,HttpClient client, String url, String encoding)throws Exception {
		if(context == null) context = new JHttpContext(); 
		if(client == null) client = getDefaultClient();
		
		url=JUtilString.replaceAll(url," ","%20");
		HttpGet request = new HttpGet(url);
		
		initRequest(context,client,request);
		
		execute(context,client,request,encoding,0);
		
		return context;
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public JHttpContext post(JHttpContext context,HttpClient client, String url, Map params)throws Exception {
		return post(context,client,url,params,null);
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public JHttpContext post(JHttpContext context,HttpClient client, String url,Map params,String encoding) throws Exception {
		if(context == null) context = new JHttpContext(); 
		if(client == null) client = getDefaultClient();

		url=JUtilString.replaceAll(url," ","%20");	
		HttpPost request = new HttpPost(url);
	
		initRequest(context,client,request);
		
		if(context.getRequestBody()!=null){
			StringEntity se=null;
			if(context.getRequestEncoding()==null) {
				se=new StringEntity(context.getRequestBody());
			}else{
				se=new StringEntity(context.getRequestBody(),context.getRequestEncoding());
			}
			request.setEntity(se);
		}else{
			addParams(context,request,params);
		}
		
		execute(context,client,request,encoding,0);
		
		return context;
	}
	
	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param parts
	 * @param strings
	 * @return
	 * @throws Exception
	 */
	public JHttpContext postMultipartData(JHttpContext context,HttpClient client, String url,Map parts,Map strings)throws Exception {
		return postMultipartData(context,client,url,parts,strings,null);
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param parts
	 * @param strings
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public JHttpContext postMultipartData(JHttpContext context,HttpClient client, String url,Map parts,Map strings, String encoding) throws Exception {
		if(context == null) context = new JHttpContext(); 
		if(client == null) client = getDefaultClient();

		url=JUtilString.replaceAll(url," ","%20");
		HttpPost request = new HttpPost(url);
		
		initRequest(context,client,request);

		addParams(context,request,parts,strings);
		
		execute(context,client,request,encoding,0);
		
		return context;
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @throws Exception
	 */
	public JHttpContext getStream(JHttpContext context,HttpClient client, String url) throws Exception {
		if(context == null) context = new JHttpContext(); 
		if(client == null) client = getDefaultClient();

		url=JUtilString.replaceAll(url," ","%20");
		HttpGet request = new HttpGet(url);
		
		initRequest(context,client,request);
		
		execute(context,client,request,null,1);
		
		return context;
	}


	/**
	 * 
	 * @param client
	 * @param url
	 * @param params
	 * @return
	 */
	public JHttpContext postStream(JHttpContext context,HttpClient client, String url, Map params)throws Exception {
		if(context == null) context = new JHttpContext(); 
		if(client == null) client = getDefaultClient();
		
		url=JUtilString.replaceAll(url," ","%20");
		HttpPost request = new HttpPost(url);
		
		initRequest(context,client,request);

		if(context.getRequestBody()!=null){
			StringEntity se=null;
			if(context.getRequestEncoding()==null) {
				se=new StringEntity(context.getRequestBody());
			}else{
				se=new StringEntity(context.getRequestBody(),context.getRequestEncoding());
			}
			request.setEntity(se);
		}else{
			addParams(context,request,params);
		}
		
		execute(context,client,request,null,1);
		
		return context;
	}

	
	/**
	 * 
	 * @param request 原始请求
	 * @param httMethod POST 或者 GET
	 * @param host
	 * @param port
	 * @param requestUri
	 * @throws Exception
	 */
	public String pipe(HttpServletRequest request,String httMethod,String host,int port,String requestUri) throws Exception{
		Socket socket = new Socket(host,port);
        try {
    		OutputStream out=socket.getOutputStream();
            out.write((httMethod+" "+requestUri+" HTTP/1.1\r\n").getBytes());
            Enumeration hns=request.getHeaderNames();
    		while(hns.hasMoreElements()){
    			String n=hns.nextElement().toString();
                out.write((n+":  "+request.getHeader(n)+"\r\n").getBytes());
    		}
            out.write("\r\n".getBytes());
            
            InputStream in=request.getInputStream();
            byte[] buffer=new byte[1024];
    		int readed=in.read(buffer);
    		while(readed>-1){
    			out.write(buffer,0,readed);
    			readed=in.read(buffer);
    		}
    		out.flush();

    		BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		String  line=reader.readLine();
    		String  responseContent="";
    		boolean responseContentStarted=false;
    		while(line!=null){	      		
    			if(responseContentStarted) responseContent+=line+Global.lineSeparator;
    			if(line.equals("")) responseContentStarted=true;
    			
        		line=reader.readLine();
        	}
    		
    		if(responseContent.endsWith(Global.lineSeparator)){
    			responseContent=responseContent.substring(0,responseContent.length()-Global.lineSeparator.length());
    		}
    		
    		return responseContent;
        } finally {
            socket.close();
        }
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public String getResponse(JHttpContext context,HttpClient client, String url) throws Exception {
		return getResponse(context,client,url,(String)null);
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public String getResponse(JHttpContext context,HttpClient client, String url, String encoding)throws Exception {
		context=get(context,client,url,encoding);

		if(context==null||!context.isErrorCodeAllowed(context.getStatus())){
			throw new Exception("获取网页出错（get） - "+url+" - context - "+context+" status - "+(context==null?"unknown":context.getStatus()));
		}
		String response=context.getResponseText();
		context.finish();
		
		return response;
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String postResponse(JHttpContext context,HttpClient client, String url, Map params)throws Exception {
		return postResponse(context,client,url,params,null);
	}

	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @param params
	 * @param encoding
	 * @return
	 * @throws Exception
	 */
	public String postResponse(JHttpContext context,HttpClient client, String url,Map params,String encoding) throws Exception {
		context=post(context,client,url,params,encoding);
		
		if(context==null||!context.isErrorCodeAllowed(context.getStatus())){
			throw new Exception("获取网页出错（post） - "+url+" - context - "+context+" status - "+(context==null?"unknown":context.getStatus()));
		}
		String response=context.getResponseText();
		context.finish();
		
		return response;
	}
	


	/**
	 * 
	 * @param context
	 * @param client
	 * @param url
	 * @throws Exception
	 */
	public InputStream getStreamResponse(JHttpContext context,HttpClient client, String url) throws Exception {
		context=getStream(context,client,url);

		if(context==null||!context.isErrorCodeAllowed(context.getStatus())){
			throw new Exception("获取网页输入流出错（get） - "+url+" - context - "+context+" status - "+(context==null?"unknown":context.getStatus()));
		}
		
		InputStream is=context.getResponseStream();
		
		return is;
	}


	/**
	 * 
	 * @param client
	 * @param url
	 * @param params
	 * @return
	 */
	public InputStream postStreamResponse(JHttpContext context,HttpClient client, String url, Map params)throws Exception {
		context=postStream(context,client,url,params);

		if(context==null||!context.isErrorCodeAllowed(context.getStatus())){
			throw new Exception("获取网页输入流出错（post） - "+url+" - context - "+context+" status - "+(context==null?"unknown":context.getStatus()));
		}
		
		InputStream is=context.getResponseStream();
		
		return is;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////	
	/**
	 * 得到访问者IP
	 * @param request
	 * @return
	 */
	public static String getRemoteIp(HttpServletRequest request){
		String[] ips=getRemoteIps(request);
		return ips.length==0?"":ips[0];
	}
	
	/**
	 * 访问者的多个IP信息
	 * @param request
	 * @return
	 */
	public static String[] getRemoteIps(HttpServletRequest request){
		if(request==null) return new String[] {"127.0.0.1"};
		String ip=SysUtil.getCookie(request,"ROAR_STATIC_IP");
		if(ip!=null&&ip.indexOf("127.0.0.1")>-1) ip=null;
		if(ip==null) ip=request.getHeader("x-forwarded-for");
		if(ip==null) ip=request.getHeader("x-real-ip");
		if(ip==null) ip=request.getHeader("remote-host");
		if(ip==null) ip=request.getHeader("remote-addr");
		if(ip==null) ip=request.getHeader("PROXY_FORWARDED_FOR");
		if(ip==null) ip=request.getRemoteHost();
		
		if(ip.indexOf(",")>0){
			ip=ip.replaceAll(" ","");
			return ip.split(",");
		}else{
			return new String[]{ip.trim()};
		}
	}
	
	
	/**
	 * 
	 * @param sellerId
	 * @param data
	 * @return
	 */
	private static String getDatadigest(String sellerId, String data) {
		System.out.println(data);
		data+="31ada638f569948c3799b7ea5266f922";
		
		byte[] bytes=null;
		try {
			bytes=data.getBytes("UTF-8");
		}catch(Exception e) {}
		
		return Base64.encodeBase64String(DigestUtils.md5(bytes));
	}
	
	public static String getAccessToken(String mainDomain,String loginVia)throws Exception{
		String key="WEIXIN.AccessTokens."+mainDomain+"."+loginVia;
		synchronized(key.intern()) {
			
			
			//参数配置文件中，参数AccessTokenUrl2表示基础接口地址：
			//https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET
			String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=SECRET";
			url=url.replaceAll("APPID", "wxd381a7a449e1f7f7");
			url=url.replaceAll("SECRET", "b0ace945a397b737b5ba3217b64432a0");
	
			String response=JHttp.getInstance().getResponse(null, null, url);
			//System.out.println("getAccessToken:"+response);
			
			JSONObject resp=JUtilJSON.parse(response);
			long expires_in=3600000;
			String acessToken=JUtilJSON.string(resp, "access_token");
			String _expires_in=JUtilJSON.string(resp, "expires_in");
			if(JUtilMath.isLong(_expires_in) && Long.parseLong(_expires_in)>0) {
				expires_in=Long.parseLong(_expires_in)*1000;
			}
			
			return acessToken;
		}
	}
	
	public static String getGameDate(Timestamp time) {
		if(time==null) time=new Timestamp(System.currentTimeMillis());
		Timestamp line=Timestamp.valueOf(time.toString().substring(0,10)+" 07:00:00");
		
		if(time.getTime()>=line.getTime()) return time.toString().substring(0,10);//>=7点算当天
		else return JUtilTimestamp.addToTime(time, -1).toString().substring(0,10);//<7点算前一天
	}
	
	public static final String[] animals=new String[] {
			"鼠",
			"牛",
			"虎",
			"兔",
			"龙",
			"蛇",
			"马",
			"羊",
			"猴",
			"鸡",
			"狗",
			"猪"	};
	
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args){
		try {
			JHttp http=JHttp.getInstance();
			http.setRedirectsEnabled(false);
			JHttpContext context=new JHttpContext();
			context.setAllowedErrorCodes(new String[] {"200","301","302"});
			context.setClearRequestHeadersOnFinish(false);
			HttpClient client=http.createClient();

			String uid="kk6355c1";
			String url="http://lqb.ck67890.com/";
			if(!url.endsWith("/")) url+="/";
			System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 尝试登录X2，使用线路：\r\n"+url);
			
			String dir=System.getProperty("user.dir");
			http=JHttp.getInstance("F:\\work\\JFramework_v2.0\\config\\server.jks","20081016");

			
			context.addRequestHeader("User-Agent", "mozilla/5.0 (windows nt 10.0; win64; x64) applewebkit/537.36 (khtml, like gecko) chrome/90.0.4430.93 safari/537.36");
			context.addRequestHeader("Upgrade-Insecure-Request", "1");

			String resp=http.getResponse(context, client, url+"member/login");
			if(resp.indexOf("id=\"userAuth\" value=\"")<0) {
				System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 未获得userAuth -> \r\n"+resp);
				System.exit(0);
			}
			
			int start=resp.indexOf("id=\"userAuth\" value=\"")+"id=\"userAuth\" value=\"".length();
			int end=resp.indexOf("\"", start);
			String userAuth=resp.substring(start, end);
			
			String captcha=null;
			if(resp.indexOf("name=\"captcha\"")>0) {//需要验证码
				System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 破解验证码......");
				String code="";
				String codeSavedPath="f:/temp/cache/"+uid+"_code.jpg";
				for(int i=0; i<10; i++) {
					context.addRequestHeader("Referer", url+"member/login");
					http.getStream(context, client, url+"captcha?tz="+(new Random()).nextDouble());
					//System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 破解验证码 -> "+resp);
					
					JDFSFile.saveStream(context.getResponseStream(), codeSavedPath);
					
					try {Thread.sleep(100);}catch(Exception e) {}

			        //OCR识别
			        code = Tess4J.doOCR(codeSavedPath);
					System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 破解验证码 -> "+code);
			        
			        code=JUtilString.replaceAll(code, " ", "");
			        code=JUtilString.replaceAll(code, "\t", "");
			        code=JUtilString.replaceAll(code, "\r", "");
			        code=JUtilString.replaceAll(code, "\n", "");
			        code=JUtilString.replaceAll(code, "\b", "");
			        code=JUtilString.replaceAll(code, ",", "");
			        code=JUtilString.replaceAll(code, ".", "");
			        code=JUtilString.replaceAll(code, ")", "");
			        code=JUtilString.replaceAll(code, "(", "");
			        code=JUtilString.replaceAll(code, "[", "");
			        code=JUtilString.replaceAll(code, "]", "");
			        code=JUtilString.replaceAll(code, "‘", "");
			        
			        //成功识别
			        if(code.length()==4&&JUtilMath.isInt(code)) {
						System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 成功识别验证码："+code);
			        }else {
			        	captcha=null;
			        }
				}
				
				if(captcha==null) {
					System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 成功破解失败！");
					System.exit(0);
				}
			}
			
			Map paras=new HashMap();
			paras.put("userAuth",userAuth);
			paras.put("username",uid);
			paras.put("password","As222222");
			paras.put("x","26");
			paras.put("y","36");
	
			resp=http.postResponse(context, client, url+"member/login", paras, "UTF-8");
			String Location=context.getResponseHeader("Location");
			if(Location==null || "".equals(Location)) Location=url+"member/welcome";
			
			resp=http.getResponse(context, client, Location, "UTF-8");			
			if(resp.indexOf("同意")<0) {
				System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 未正确获取用户条款页面 -> \r\n"+resp);
				System.exit(0);
			}

			resp=http.getResponse(context, client, url+"member/", "UTF-8");
			if(resp.indexOf("src=\"/member/top\"")<0) {
				System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 登入失败 -> \r\n"+resp);
				System.exit(0);
			}else {
				System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 登入成功！");
			}
			
			//赔率
			Map<String, String> odds=new HashMap();
			for(int i=2; i<=5; i++) {
				resp=http.getResponse(context, client, url+"realtime/shengxiaolian/"+(i-1)+"?_="+System.currentTimeMillis(), "UTF-8");
				resp=JUtilString.decodeUnicode(resp);
				
				JSONObject _json=JUtilJSON.parse(resp);
				JSONObject _data=JUtilJSON.object(_json, "data");
				JSONObject _odds=JUtilJSON.object(_data, "odds");
				if(_odds==null) {
					System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+"["+uid+"] 获取赔率"+i+"失败 -> \r\n"+resp);
					System.exit(0);
				}
				
				System.out.println((new java.sql.Timestamp(System.currentTimeMillis()))+" -> "+i+"连肖赔率 -> "+_odds);
				odds.put("string-"+i,_odds.toString());
			}
			
			
			//模拟下注件注单
			List<Betting> bettings=new ArrayList();
			Betting b1=new Betting();
			b1.typeCode="MultiAnimals_2In7";
			b1.selectUuid="MultiAnimals_2In7_Gou";
			b1.sContent="jis:3f,y,2r,33,32,38,2t,32,38,y,1m,y,mon,18,mqy,y,18,y,2r,33,39,32,38,y,1m,y,1d,y,18,y,2x,32,3a,2p,30,2x,2s,1v,33,2s,2t,y,1m,y,32,39,30,30,y,18,y,2x,32,3a,2p,30,2x,2s,25,37,2v,y,1m,y,32,39,30,30,y,18,y,31,39,30,38,2x,28,33,37,37,2x,2q,2x,30,2x,38,2x,2t,37,y,1m,y,1y,y,18,y,37,2t,30,2t,2r,38,37,y,1m,2j,2l,3h";
			
			Betting b2=new Betting();
			b2.typeCode="MultiAnimals_2In7";
			b2.selectUuid="MultiAnimals_2In7_Zhu";
			b2.sContent="jis:3f,y,2r,33,32,38,2t,32,38,y,1m,y,mqy,18,v8h,y,18,y,2r,33,39,32,38,y,1m,y,1d,y,18,y,2x,32,3a,2p,30,2x,2s,1v,33,2s,2t,y,1m,y,32,39,30,30,y,18,y,2x,32,3a,2p,30,2x,2s,25,37,2v,y,1m,y,32,39,30,30,y,18,y,31,39,30,38,2x,28,33,37,37,2x,2q,2x,30,2x,38,2x,2t,37,y,1m,y,1y,y,18,y,37,2t,30,2t,2r,38,37,y,1m,2j,2l,3h";
			
			Betting b3=new Betting();
			b3.typeCode="MultiAnimals_2In7";
			b3.selectUuid="MultiAnimals_2In7_Gou";
			b3.sContent="jis:3f,y,2r,33,32,38,2t,32,38,y,1m,y,mon,18,v8h,y,18,y,2r,33,39,32,38,y,1m,y,1d,y,18,y,2x,32,3a,2p,30,2x,2s,1v,33,2s,2t,y,1m,y,32,39,30,30,y,18,y,2x,32,3a,2p,30,2x,2s,25,37,2v,y,1m,y,32,39,30,30,y,18,y,31,39,30,38,2x,28,33,37,37,2x,2q,2x,30,2x,38,2x,2t,37,y,1m,y,1y,y,18,y,37,2t,30,2t,2r,38,37,y,1m,2j,2l,3h";
			
			b1.betMoney=3;
			b2.betMoney=3;
			b3.betMoney=3;
			
			bettings.add(b1);
			bettings.add(b2);
			bettings.add(b3);
			
			Betting b0=null;
			double amount=0;
			List<String> groups=new ArrayList();
			List<String> balls=new ArrayList();
			List<String> _odds=new ArrayList();
			
			for(int i=0; i<bettings.size(); i++) {
				Betting b=bettings.get(i);
				if(!b.typeCode.startsWith("MultiAnimals_")) {
					continue;
				}
				
				long betMoney=Math.round(b.betMoney);
				if(betMoney < 2) {
					//return new TransferResult(true, "success", "小于最小单注，忽略该注单。","");
					continue;
				}
				
				JSONObject sContent=JUtilJSON.parse(JObject.intSequence2String(b.sContent));
				String content=JUtilJSON.string(sContent, "content");
				if(content==null) continue;
				
				b0=b;
				
				break;
			}
			
			JSONObject _oddsString=null;
			if(b0.typeCode.equals("MultiAnimals_2In7")) _oddsString=JUtilJSON.parse(odds.get("string-2"));
			if(b0.typeCode.equals("MultiAnimals_3In7")) _oddsString=JUtilJSON.parse(odds.get("string-3"));
			if(b0.typeCode.equals("MultiAnimals_4In7")) _oddsString=JUtilJSON.parse(odds.get("string-4"));
			if(b0.typeCode.equals("MultiAnimals_5In7")) _oddsString=JUtilJSON.parse(odds.get("string-5"));
			
			for(int i=0; i<bettings.size(); i++) {
				Betting b=bettings.get(i);
				if(!b.typeCode.startsWith("MultiAnimals_")) {
					continue;
				}
				
				long betMoney=Math.round(b.betMoney);
				if(betMoney < 2) {
					//return new TransferResult(true, "success", "小于最小单注，忽略该注单。","");
					continue;
				}
				
				JSONObject sContent=JUtilJSON.parse(JObject.intSequence2String(b.sContent));
				String content=JUtilJSON.string(sContent, "content");
				if(content==null) continue;
				
				String[] choices=content.split(",");
				List<String> _choices=new ArrayList();//按生肖顺序排列
				for(int j=0; j<animals.length; j++) {
					if(JUtilString.contain(choices, animals[j])) {
						_choices.add(animals[j]);
						if(!balls.contains(animals[j])) {
							balls.add(animals[j]);
						}
					}
				}
				
				//转繁体
				for(int c=0; c<_choices.size(); c++) {
					_choices.set(c, JUtilString.toZhTw(_choices.get(c)));
				}
				
				String __choices=JArray.toString(_choices,",");
				System.out.println("__choices -> "+__choices);
				
				String minOdds="0";
				for(int j=0; j<_choices.size(); j++) {
					String thisOdds=JUtilJSON.string(_oddsString, _choices.get(j));
					if(Double.parseDouble(minOdds)<0.01
							||Double.parseDouble(minOdds)>Double.parseDouble(thisOdds)) {
						minOdds=thisOdds;
					}
				}
				
				groups.add(__choices+"|"+minOdds);
				_odds.add(minOdds);
				
				amount+=betMoney;
			}
			for(int c=0; c<balls.size(); c++) {
				balls.set(c, JUtilString.toZhTw(balls.get(c)));
			}
			
			System.out.println("groups -> "+JArray.toString(groups," * "));
			System.out.println("count -> "+groups.size());
			System.out.println("amount -> "+Math.round(amount));
			System.out.println("single_amount -> "+Math.round(b0.betMoney));
			System.out.println("balls -> "+JArray.toString(balls,","));
			System.out.println("odds -> "+JArray.toString(_odds,","));
			
			context.setRequestEncoding("UTF-8");
			Map params=new HashMap();
			params.put("groups[]",groups);
			params.put("count",groups.size()+"");	
			params.put("amount", ""+Math.round(amount));
			params.put("single_amount", ""+Math.round(b0.betMoney));
			params.put("balls",JArray.toString(balls,","));
			params.put("odds",JArray.toString(_odds,","));
			
			resp=http.postResponse(context, client, url+"member/shengxiaolian/fushi", params, "UTF-8");
			
			System.out.println("模拟下注 -> "+resp);
			
			System.exit(0);
		}catch(Exception e) {
			System.out.println(SysUtil.getException(e));
			e.printStackTrace();
			System.exit(0);
		}
	}
}