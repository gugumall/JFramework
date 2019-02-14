package j.http;

import it.sauronsoftware.base64.Base64;
import j.Properties;
import j.common.Global;
import j.sys.AppConfig;
import j.sys.SysUtil;
import j.tool.javascript.JavaScriptBridge;
import j.util.ConcurrentMap;
import j.util.JUtilCompressor;
import j.util.JUtilInputStream;
import j.util.JUtilMD5;
import j.util.JUtilMath;
import j.util.JUtilRandom;
import j.util.JUtilString;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

/**
 * @author 肖炯
 * 
 */
public class JHttp{
	public static final String default_user_agent="Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0)";
	public static final int default_retries=1;
	public static final long default_retry_interval=3000;

	private static JHttp[] instances = new JHttp[Properties.getJHttpInstances()];
	private static ConcurrentMap configOfClients=new ConcurrentMap();
	private PoolingHttpClientConnectionManager poolingmgr;
	//private SSLConnectionSocketFactory factory;
	private HttpClient[] clients = new HttpClient[Properties.getClientsOfJHttpInstance()];

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
		CloseableHttpClient client = HttpClients.custom().setConnectionManager(poolingmgr).build();

		int retries=default_retries;
		if(JUtilMath.isInt(AppConfig.getPara("HTTP","retries"))){
			retries=Integer.parseInt(AppConfig.getPara("HTTP","retries"));
		}
		RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(retries).setSocketTimeout(timeout).setConnectTimeout(timeout).build();
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
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(username, password));
	
		CloseableHttpClient client = HttpClients.custom()
				.setConnectionManager(poolingmgr)
				.setProxy(proxy)
				.setDefaultCredentialsProvider(credsProvider).build();
		
		int retries=default_retries;
		if(JUtilMath.isInt(AppConfig.getPara("HTTP","retries"))){
			retries=Integer.parseInt(AppConfig.getPara("HTTP","retries"));
		}
		RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(retries).setSocketTimeout(timeout).setConnectTimeout(timeout).build();
		
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
	private static void getStatusAndHeaders(JHttpContext context,HttpResponse response){
		if(context==null||response==null) return;
		
		StatusLine status=response.getStatusLine();
		if(status!=null) context.setStatus(status.getStatusCode());

        Header[] headers = response.getAllHeaders();
        if(headers!=null){
            for(int i=0;i<headers.length;i++){
            	context.addResponseHeader(headers[i].getName(),headers[i].getValue());
            }
        }
	}
	
	/**
	 * 
	 * @param context
	 * @param client
	 * @param request
	 */
	private static void initRequest(JHttpContext context,HttpClient client,HttpRequestBase request){
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
				formparams.add(new BasicNameValuePair((String)key, val==null?"":val.toString()));
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
	private static void execute(JHttpContext context,HttpClient client,HttpRequestBase request,String encoding,int responseType) throws Exception{
		int retries=default_retries;
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
		
		while(retries>0){
			retries--;
			try{
				doExecute(context,client,request,encoding,responseType,retries==0?true:false);
				if(context!=null&&context.getStatus()==200) return;
				
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
	private static void doExecute(JHttpContext context,HttpClient client,HttpRequestBase request,String encoding,int responseType,boolean abort) throws Exception{
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
		context.finalize();
		context=null;
		
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
		context.finalize();
		context=null;
		
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
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		System.out.println("begin");
		
		JHttp http=JHttp.getInstance();
		HttpClient client=http.createClient();
		JHttpContext context=new JHttpContext();
		context.setAllowedErrorCodes(new String[]{"all"});
		
		String url="http://www.knlotto.kr/keno.aspx?method=kenoWinNoList";
		String response="";
		
		http.getStream(context, client, url);
		if(context.getStatus()==503){
			InputStream is=context.getResponseStream();
			response=JUtilInputStream.string(is);
			
			if(response.indexOf("jschl_vc\" value=\"")<0
					||response.indexOf("pass\" value=\"")<0){
				System.out.println("fail!");
				System.exit(0);
			}
			
			int startx=response.indexOf("jschl_vc\" value=\"")+"jschl_vc\" value=\"".length();
			int endx=response.indexOf("\"",startx);
			String jschl_vc=response.substring(startx,endx);
			
			startx=response.indexOf("pass\" value=\"")+"pass\" value=\"".length();
			endx=response.indexOf("\"",startx);
			String pass=response.substring(startx,endx);
			String domain=JUtilString.getHost(url);
			
			String _js="function _hey(){";
			
			//System.out.println("response:"+response);
			
			response=response.replaceAll(" ", "");
			startx=response.indexOf("vars,t,o,p");
			endx=response.indexOf(";",startx);
			_js+=response.substring(startx,endx)+";";
			
			startx=response.indexOf("challenge-form');")+"challenge-form');".length();
			endx=response.indexOf("+t.length",startx);
			_js+=response.substring(startx+2,endx)+";";
			
			_js=JUtilString.replaceAll(_js, "a.value=", "var hey=");
			_js=JUtilString.replaceAll(_js, "vars,t,o,p", "var s,t,o,p");
			_js=JUtilString.replaceAll(_js, ";", ";\r\n");
			_js+="return hey;}";
			//System.out.println("_js:"+_js);
			
			JavaScriptBridge js=JavaScriptBridge.getInstanceOfJsString(JUtilMD5.MD5EncodeToHex(_js), _js);
			
			Double jschl_answer=(Double) js.call("_hey");
			int _jschl_answer=jschl_answer.intValue()+domain.length();
			
			String passUrl="http://"+domain+"/cdn-cgi/l/chk_jschl?jschl_vc="+jschl_vc+"&pass="+pass+"&jschl_answer="+_jschl_answer;
			//System.out.println("passUrl:"+passUrl);
			
			context.addRequestHeader("Referer", "http://"+domain+"/");
			http.getResponse(context, client, passUrl);
			
			response=http.getResponse(context, client, url,"EUC-KR");
			//System.out.println("1:"+response);
		}else{
			InputStream is=context.getResponseStream();
			response=JUtilInputStream.string(is,"EUC-KR");
			//System.out.println("2:"+response);
		}
		System.exit(0);
	}
	
	/**
	 * 
	 * @param to
	 * @param content
	 * @param validMins
	 * @return
	 */
	private static boolean sendTemplateSMS(String to, String content,int validMins) {
		String result = "";
		try {
			String templateId="1";
			String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
			String ACCOUNT_SID="aaf98f894f402f15014f46b1316b058d";
			String ACCOUNT_TOKEN="b51578bb2e6a4d2f82c3ef4a1712c75f";
			String sid=ACCOUNT_SID + ACCOUNT_TOKEN + timestamp;
			String signature = JUtilMD5.MD5EncodeToHex(sid);
			
			//String url="https://app.cloopen.com:8883/2013-12-26/Accounts/"+ACCOUNT_SID+"/SMS/TemplateSMS?sig="+signature;
			String url="https://sandboxapp.cloopen.com:8883/2013-12-26/Accounts/"+ACCOUNT_SID+"/SMS/TemplateSMS?sig="+signature;
			String App_ID="aaf98f894f402f15014f46b1dcdb0590";
			
			String requestBody = "";
			StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
			sb.append("<appId>").append(App_ID).append("</appId>").append("<to>").append(to).append("</to>").append("<templateId>").append(templateId).append("</templateId>");
			sb.append("<datas>");
			sb.append("<data>").append(content).append("</data>");
			sb.append("<data>").append(validMins).append("</data>");
			sb.append("</datas>");
			sb.append("</TemplateSMS>").toString();
			requestBody = sb.toString();
			
			System.out.println("requestBody:"+requestBody);
			

			JHttp http=JHttp.getInstance();
			JHttpContext context=new JHttpContext();
			context.setRequestBody(requestBody);
			context.setRequestEncoding("UTF-8");
		
			context.addRequestHeader("Accept", "application/xml");
			context.addRequestHeader("Content-Type", "application/xml;charset=utf-8");
			context.addRequestHeader("Authorization", Base64.encode(ACCOUNT_SID+":"+timestamp));
			result=http.postResponse(context, null, url,null);
			
			System.out.println("result:"+result);
			
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
	}
}
