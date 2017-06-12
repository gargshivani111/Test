package com.aem.core.workflow.process;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.mail.MessagingException;

import org.apache.commons.lang.text.StrLookup;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;
import com.day.cq.search.result.SearchResult;
import com.adobe.cq.social.commons.comments.search.SearchResultComponent;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.aem.core.services.QueryBuilderUtil;
import com.aem.core.services.ResourceResolverUtil;
import com.day.cq.search.result.Hit;
import static com.aem.core.constants.ApplicationConstants.PROPERTY;
import static com.aem.core.constants.ApplicationConstants.PROPERTY_VALUE;
import static com.aem.core.constants.ApplicationConstants.NOTICE_MANAGEMENT_RESOURCE_TYPE;
import static com.aem.core.constants.ApplicationConstants.EMAIL_NOTIFICATION_TEMPLATE_PATH;
import static com.aem.core.constants.ApplicationConstants.NOTICE_DATA;
import static com.aem.core.constants.ApplicationConstants.CONSENT_TEXT;
import static com.aem.core.constants.ApplicationConstants.PRODUCT_OWNER_EMAIL;
import static com.aem.core.constants.ApplicationConstants.EMAIL_NOTIFICATION;
import static com.aem.core.constants.ApplicationConstants.NOTICE_OPTIONS;
import static com.aem.core.constants.ApplicationConstants.NOTICE_DATA_CONTENT;
import static com.aem.core.constants.ApplicationConstants.CONSENT_DATA_CONTENT;
import static com.aem.core.constants.ApplicationConstants.DAM_ASSETS;
import static com.aem.core.constants.ApplicationConstants.EMPTY;
import static com.aem.core.constants.ApplicationConstants.CONTENT;

@Component(immediate = true, enabled = true, metatype = true)
@Service(value = WorkflowProcess.class)
@Property(name = "process.label", value = "GDPR:Send Notification to Product Owner", propertyPrivate = true)
/**
 * This workflow class is used to send email notification to the product owner
 * only if the payload has a flag to send notification.
 */
public class SendEmailNotification implements WorkflowProcess {

	/** The Constant LOGGER. */
	private static final Logger logger = LoggerFactory
			.getLogger(SendEmailNotification.class);
	/**
	 * The Resource resolver util.
	 */
	@Reference
	private ResourceResolverUtil resourceResolverUtil;

	/**
	 * The Query builder util.
	 */
	@Reference
	private QueryBuilderUtil querybuilderutil;

	/**
	 * The Message gateway service
	 */
	@Reference
	private MessageGatewayService messageGatewayService;

	@Override
	public void execute(WorkItem workItem, WorkflowSession workflowSession,
			MetaDataMap metaDataMap) {
		try {
			String payloadPath = workItem.getWorkflowData().getPayload()
					.toString();
			ResourceResolver resourceResolver = resourceResolverUtil
					.getResourceResolver();
			String componentPath = findComponent(payloadPath);
			Resource resource = resourceResolver.getResource(componentPath);
			ValueMap valuemap = resource.getValueMap();
			String emailID = valuemap.get(PRODUCT_OWNER_EMAIL, String.class);

			String emailAcceptance = valuemap.get(EMAIL_NOTIFICATION,
					String.class);
			if (emailID != null && emailAcceptance.equals("true")) {
				String option = valuemap.get(NOTICE_OPTIONS, String.class);
				Map<String, Object> mailProperty = createMap(option, valuemap);
				sendEmail(mailProperty, resourceResolver, emailID);
			}
		} catch (Exception e) {
			logger.error("REPOSITORY  EXCEPTION" + e.getMessage());
		}

	}

	/**
	 * This method is used to find the notice management component under the
	 * specific page.
	 * 
	 * @return component path
	 */
	private String findComponent(String path) {
		logger.debug("START OF findComponent METHOD");
		String componentPath = null;
		Map<String, String> map = new HashMap<String, String>();
		map.put(SearchResultComponent.PN_PATH, path);
		map.put(PROPERTY, JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
		map.put(PROPERTY_VALUE, NOTICE_MANAGEMENT_RESOURCE_TYPE);
		try {
			SearchResult result = querybuilderutil.getQueryResult(map);
			if (result.getHits().size() > 0) {
				Hit hit = result.getHits().get(0);
				componentPath = hit.getPath();
			}
		} catch (RepositoryException e) {
			logger.error("REPOSITORY  EXCEPTION" + e.getMessage());
		}
		logger.debug("END OF findComponent METHOD");
		return componentPath;
	}

	/**
	 * This method is used to send email to the product owner with the notice
	 * options.
	 */
	private void sendEmail(Map mailProperty, ResourceResolver resourceResolver,
			String emailId) {
		try {
			logger.debug("START OF sendEmail METHOD");
			MailTemplate mailTemplate = MailTemplate.create(
					EMAIL_NOTIFICATION_TEMPLATE_PATH,
					resourceResolver.adaptTo(Session.class));
			HtmlEmail email = mailTemplate.getEmail(
					StrLookup.mapLookup(mailProperty), HtmlEmail.class);
			email.addTo(emailId);
			MessageGateway<Email> messageGateway;
			messageGateway = messageGatewayService.getGateway(Email.class);
			messageGateway.send((Email) email);
			logger.debug("END OF sendEmail METHOD");
		} catch (IOException | MessagingException | EmailException e) {
			logger.error("EXCEPTION IN SEND MAIL METHOD" + e.getMessage());
		}
	}

	/**
	 * This method is used to create map on the basis of notice options in GDPR
	 * notice management
	 */
	private Map<String,Object> createMap(String option, ValueMap valuemap) {
		logger.debug("START OF createMap METHOD");
		String data = EMPTY;
		if (option.equals(NOTICE_DATA))
			data = valuemap.get(NOTICE_DATA_CONTENT, String.class);
		else if (option.equals(CONSENT_TEXT))
			data = valuemap.get(CONSENT_DATA_CONTENT, String.class);
		else
			data = valuemap.get(DAM_ASSETS, String.class);
		Map<String, Object> mailProperty = new HashMap<String, Object>();
		mailProperty.put(CONTENT, data);
		logger.debug("END OF createMap METHOD");
		return mailProperty;

	}
}