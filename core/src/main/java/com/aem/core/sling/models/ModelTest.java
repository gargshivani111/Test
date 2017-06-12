package com.aem.core.sling.models;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.*;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.wrappers.ValueMapDecorator;

import java.util.HashMap;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;

@Model(adaptables = SlingHttpServletRequest.class)
public class ModelTest {

	@Inject
	@Optional
	private String resourcePath;

	@Inject
	@Via("resource")
	private ResourceResolver resourceResolver;

	@Inject
	private SlingHttpServletRequest slingRequest;

	@PostConstruct
	protected void init() {

		Resource resource = resourceResolver.getResource(resourcePath);
		DataSource ds = new SimpleDataSource(new TransformIterator(
				resource.listChildren(), new Transformer() {
					public Object transform(Object o) {
						Resource item = (Resource) o;
						ValueMap vm = new ValueMapDecorator(
								new HashMap<String, Object>());
						ValueMap valuemap = item.getValueMap();
						vm.put("value", valuemap.get("value", String.class));
						vm.put("text", valuemap.get(JcrConstants.JCR_TITLE,
								String.class));

						return new ValueMapResource(resourceResolver,
								new ResourceMetadata(),
								JcrConstants.NT_UNSTRUCTURED, vm);
					}
				}));

		slingRequest.setAttribute(DataSource.class.getName(), ds);

	}
}
