package com.aem.core.sling.models;


import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.servlets.ServletResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;

@Model(adaptables = SlingHttpServletRequest.class, resourceType = {
    "weretail/components/structure/page"}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = "jackson", extensions = "json", options = {
    @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value = "false")
})

@JsonIgnoreProperties("title")
public class Test {
	
  @Inject
  @Via("resource")
  private boolean navRoot;

  public boolean isNavRoot() {
    return navRoot;
  }

  @Inject
  @Via("resource")
  @Named("jcr:title")
  private String title;

  @Inject
  @Via("resource")
  @Named("jcr:createdBy")
  private String createdBy;

  @JsonProperty(index=1)
  @Inject
  @Via("resource")
  @Named("sling:resourceType")
  private String resourceType;

 
  public String getResourceType() {
    return resourceType;
  }

  @JsonGetter("shivani")
  public String getCreatedBy() {
    return createdBy;
  }

  public String getTitle() {
    return title;
  }
  public List<TestModel> getList()
  {
    List <TestModel>list = new ArrayList<TestModel>();
    list.add(new TestModel("test1","Test2"));
    return list;
  }
}
