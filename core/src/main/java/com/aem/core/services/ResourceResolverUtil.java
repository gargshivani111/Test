package com.aem.core.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import static com.aem.core.constants.ApplicationConstants.RESOURCE_RESOLVER_SUBSERVICE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* This Service will provide utility methods related to resource Resolver.
*/
@Component(label = "GDPR - Resource Resolver Utility Service", enabled = true)
@Service(ResourceResolverUtil.class)
public class ResourceResolverUtil {

	/** The resource resolver factory. */
	@Reference
	private transient ResourceResolverFactory resourceResolverFactory;

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory
			.getLogger(ResourceResolverUtil.class);

	/**
	 * Gets the resource resolver.
	 *
	 * @return the resource resolver
	 */
	public ResourceResolver getResourceResolver() {
		logger.debug("START OF getResourceResolver METHOD");
		ResourceResolver resourceResolver = null;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put(ResourceResolverFactory.SUBSERVICE, RESOURCE_RESOLVER_SUBSERVICE);
		try {
			resourceResolver = resourceResolverFactory
					.getServiceResourceResolver(param);
			logger.debug("END OF getResourceResolver METHOD");
		} catch (LoginException e) {
			logger.error("LOGIN EXCEPTION"+e.getMessage());
		}
		return resourceResolver;
	}
}