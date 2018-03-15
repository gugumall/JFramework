package j.app.sso;

import j.util.JUtilDom4j;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public class SSOConfigLoaderXMLImpl implements SSOConfigLoader{
	/*
	 *  (non-Javadoc)
	 * @see j.app.sso.SSOConfigLoader#loadClients()
	 */
	public List loadClients() throws Exception{
		File file = new File(j.Properties.getConfigPath()+"sso.xml");
		if (!file.exists()) {
			throw new Exception("配置文件不存在: " + file.getAbsolutePath());
		}

		//create dom document
		Document doc = JUtilDom4j.parse(file.getAbsolutePath(), "UTF-8");
		Element root = doc.getRootElement();
		//create dom document end
		
		List ssoClients=new LinkedList();
		List clients=root.elements("client");
		for(int i=0;clients!=null&&i<clients.size();i++){
			Element client=(Element)clients.get(i);
			Client c=new Client();
			
			c.setIsSsoServer("true".equalsIgnoreCase(client.attributeValue("isssoserver")));
			c.setCanLogin(!"false".equalsIgnoreCase(client.attributeValue("can-login")));
			c.setCompatible(!"false".equalsIgnoreCase(client.attributeValue("compatible")));
			c.setId(client.elementText("id"));
			c.setName(client.elementText("name"));
			
			List domains=client.elements("domain");
			for(int j=0;j<domains.size();j++){
				Element domainE=(Element)domains.get(j);
			
				c.addDomain(domainE.getTextTrim());
			}
			
			List urls=client.elements("url");
			for(int j=0;j<urls.size();j++){
				Element urlE=(Element)urls.get(j);
			
				String url=urlE.getTextTrim();
				if(!url.endsWith("/")){
					url+="/";
				}
				
				if("true".equalsIgnoreCase(urlE.attributeValue("default"))){
					c.setUrlDefault(url);
				}
				
				c.addUrl(url);
			}
			
			c.setLoginPage(client.elementText("login-page"));
			c.setHomePage(client.elementText("home-page"));
			c.setPassport(client.elementText("passport"));
			c.setLoginInterface(client.elementText("login-interface"));
			c.setLogoutInterface(client.elementText("logout-interface"));
			
			Element loginAgentEle=client.element("login-agent");
			LoginAgent la=new LoginAgent(c.getId(),
					loginAgentEle.attributeValue("avail"),
					loginAgentEle.attributeValue("for-other-clients"),
					loginAgentEle.attributeValue("authenticator"),
					loginAgentEle.attributeValue("interface"));			
			c.setLoginAgent(la);
			
			c.setUserClass(client.elementText("user-class"));
			
			List props=client.elements("property");
			for(int j=0;props!=null&&j<props.size();j++){
				Element prop=(Element)props.get(j);
				c.setProperty(prop.attributeValue("key"),prop.attributeValue("value"));
			}
			
			ssoClients.add(c);
		}
		
		//clear
		root=null;
		doc=null;
		
		return ssoClients;
	}
}
