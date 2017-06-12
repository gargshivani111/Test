package com.aem.core.sling.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.inject.Inject;
import javax.inject.Named;

@Model(adaptables = Resource.class, resourceType ={"weretail/components/content/heroimage", "weretail/components/content/articleslist"}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", selector="test",extensions = "json", options = { 
		@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true"),
		@ExporterOption(name = "MapperFeature.WRITE_DATES_AS_TIMESTAMPS", value = "true"),
		@ExporterOption(name = "MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME", value = "true")})
public class ModelComponent {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Inject
	String title;
	
	
	@Inject
	String heading;

	@Inject
	String buttonLabel;

	@Inject
	String buttonLinkTo;

	@Inject
	String fileReference;

	
	@Inject 
	@Named("sling:resourceType")
	String slingResourceType;

	public String getHeading() {
		return heading;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public String getButtonLinkTo() {
		return buttonLinkTo;
	}

	public String getSlingResourceType() {
		return slingResourceType;
	}

	public String getTitle() {
		return title;
	}
	
	public String getFileReference() {
		return fileReference;
	}

}
