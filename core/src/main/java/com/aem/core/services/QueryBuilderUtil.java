package com.aem.core.services;

import java.util.Map;

import javax.jcr.Session;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

/**
 * This Service will provide utility methods related to query builder API.
 */
@Component(label = "BHF - QueryBuilder Utility Service", enabled = true, immediate = true)
@Service(QueryBuilderUtil.class)
public class QueryBuilderUtil {

	/**
	 * The Resource resolver util.
	 */
	@Reference
	ResourceResolverUtil resourceResolverUtil;

	/**
	 * Gets query result.
	 *
	 * @param map the map
	 *            
	 * @return the query result
	 */
	public SearchResult getQueryResult(Map map) {
		ResourceResolver resourceResolver = resourceResolverUtil
				.getResourceResolver();
		QueryBuilder builder = resourceResolver.adaptTo(QueryBuilder.class);
		SearchResult result = null;
		if (builder != null) {
			Query query = builder.createQuery(PredicateGroup.create(map),
					resourceResolver.adaptTo(Session.class));
			result = query.getResult();
		}
		return result;
	}
}
