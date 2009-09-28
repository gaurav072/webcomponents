package org.webcomponents.resource.web;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.webcomponents.content.Content;
import org.webcomponents.content.ContentNotFoundException;
import org.webcomponents.content.ContentService;
import org.webcomponents.resource.ResourceException;
import org.webcomponents.resource.ResourceMetaData;
import org.webcomponents.resource.ResourceService;

@Controller
public class ResourceController {
	
	private ResourceService resourceService;
	private ContentService contentService;
	private long maxUploadSize;
	private int pageSize;
	
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> list(@RequestParam("id")String id, @RequestParam(value="offset", required=false)Integer offset, @RequestParam(value="size", required=false)Integer size) throws IOException {
		int o = offset == null || offset < 0 ? 0 : offset;
		int s = size == null || size < 0 ? pageSize : size;
		List<? extends ResourceMetaData> resources = resourceService.list(id, o, s);
		Long count = resourceService.count(id);
		
		Map<String, Object> model = new HashMap<String, Object>(3);
		model.put("resources", resources);
		model.put("count", count);
		model.put("offset", o);
		model.put("pageSize", s);
		
		Content content = this.contentService.retrieveMetadata(id);
		if(content != null) {
			model.put("maxUploadCount", content.getMaxResourcesCount());
			model.put("minUploadCount", content.getMinResourcesCount());
		}
		
		model.put("maxUploadSize", maxUploadSize);
		return model;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void add(@RequestParam("id")String id, @RequestParam("file")MultipartFile file, HttpServletResponse response) throws ContentNotFoundException, IOException, ResourceException  {
		resourceService.add(id, file);
		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public void remove(@RequestParam("resource")String resource, HttpServletResponse response) throws IOException {
		this.resourceService.remove(URI.create(resource));
		response.setStatus(HttpServletResponse.SC_OK);
	}
		
	public void setMaxUploadSize(long maxUploadSize) {
		this.maxUploadSize = maxUploadSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	
}

