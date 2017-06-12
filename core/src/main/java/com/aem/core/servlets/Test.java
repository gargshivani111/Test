package com.aem.core.servlets;


import java.io.IOException;
import java.util.Iterator;

import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

@SlingServlet(paths="/bin/servlet")
public class Test extends SlingSafeMethodsServlet {
	
	protected void doGet(SlingHttpServletRequest request,SlingHttpServletResponse response)throws IOException
	{

		String selector = request.getRequestPathInfo().getSelectors()[0];
		Resource resource  = request.getResourceResolver().getResource("/etc/acs-commons/lists/service-keys/jcr:content/list");
		
		JSONObject jsonarray;
		Iterator<Resource> itr = resource.listChildren();
		try
		{jsonarray = new JSONObject();
		
		while(itr.hasNext())
		{
			
			Resource item = itr.next();
			ValueMap valueMap = item.adaptTo(ValueMap.class);
			String value = valueMap.get("value",String.class);
		if(value.equals(selector))
			{
			Resource sublist = item.getChild("list1");
			if(sublist !=null)
			{
				Iterator<Resource> sublistItr = sublist.listChildren();
				while(sublistItr.hasNext())
					{
						Resource res= sublistItr.next();
						ValueMap valueMaplist = res.adaptTo(ValueMap.class);
				
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("language",value);
					jsonObject.put("country",valueMaplist.get("jcr:title",String.class));
					jsonObject.put("value",valueMaplist.get("value",String.class));
					jsonarray.put(valueMaplist.get("value",String.class), jsonObject);
				
			}
				}
			}
			
		
		}
		response.getWriter().print(jsonarray.toString());
		}
		
		catch(JSONException e)
		{
			System.out.print("Exception is "+e.getMessage());
			
		}
		
	
	}

}
