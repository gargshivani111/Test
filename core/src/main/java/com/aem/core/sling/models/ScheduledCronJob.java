package com.aem.core.sling.models;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import static com.aem.core.constants.ApplicationConstants.EMPTY;
import com.aem.core.services.ResourceResolverUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This scheduler is used to fetch the services keys and product identifiers from the API after a periodic time and save it on a node
 */

@Component(metatype = true)
@Service(value = Runnable.class)
@Properties({
		@Property(label="Scheduler Expression" ,name = "scheduler.expression", value = "0 * * * * ?"),
		@Property(label = "API Endpoint", description = "This is the Rest API Endpoint to fetch service-keys and product-identifiers", name = "api.endpoint", value = ""),
		@Property(label = "PATH To store response", description = "This is the path where the JSON Response got saved as string", name = "json.path", value = "/etc/designs/gdpr/serviceKeysData") })
public class ScheduledCronJob implements Runnable {
	private static final String API_ENDPOINT = "api.endpoint";
	private static final String JSON_PATH = "json.path";
	private String endpoint;
	private String jsonPath;

	/**
	 * @return API Endpoint
	 */
	public String getEndpoint() {
		return endpoint;
	}

	/**
	 * @return Json path to store resource
	 */
	public String getJsonPath() {
		return jsonPath;
	}

	protected final Logger log = LoggerFactory.getLogger(this.getClass());
	@Reference
	ResourceResolverUtil resourceResolverUtil;

	@Activate
	protected void activate(Map context) {
		this.endpoint = PropertiesUtil.toString(context.get(API_ENDPOINT),
				EMPTY);
		this.jsonPath = PropertiesUtil.toString(context.get(JSON_PATH), EMPTY);
	}

	@Modified
	protected void modified(ComponentContext context) {
		Dictionary dictionary = context.getProperties();
		this.endpoint = PropertiesUtil.toString(dictionary.get(API_ENDPOINT),
				EMPTY);
		this.jsonPath = PropertiesUtil.toString(dictionary.get(JSON_PATH),
				EMPTY);
	}

	public void run() {
		ResourceResolver resourceResolver = resourceResolverUtil
				.getResourceResolver();
		HttpClient client = new HttpClient();
		GetMethod get = new GetMethod(endpoint);

		try {
			int status = client.executeMethod(get);
			if (status == 200) {
				Resource jsonNodePath = ResourceUtil.getOrCreateResource(
						resourceResolver, jsonPath, " ", " ", true);
				Node node = jsonNodePath.adaptTo(Node.class);
				try {
					node.setProperty("data", get.getResponseBodyAsString());
					Session session = resourceResolver.adaptTo(Session.class);
					session.save();
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (HttpException e) {
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

	}

}