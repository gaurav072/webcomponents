package org.webcomponents.resource.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jets3t.service.S3ServiceException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import org.webcomponents.resource.ResourceService;

public class GetResourceController implements ServletContextAware {

	private static final Logger logger = Logger.getLogger(ResourceController.class);
	
	private int offset = 0;
	
	private ResourceService resourceService;
	
	@Override
	public void setServletContext(ServletContext application) {
		String contextPath = application.getContextPath();
		if(!StringUtils.hasText(contextPath)) {
			return;
		}
		offset = contextPath.length();
		if(contextPath.endsWith("/")) {
			offset--;
		}
		
	}

	@RequestMapping(method = RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response) throws S3ServiceException, IOException {
		String path = request.getRequestURI();
		if(path != null) {
			URI uri = URI.create(path.substring(offset));
			URI url = resourceService.getAccessUri(uri);
			if(url.isAbsolute()) {
				logger.debug("Redirecting request path: " + path + " to " + url.toString());
				response.sendRedirect(url.toString());
			} else {
				OutputStream out = response.getOutputStream();
				try {
					resourceService.export(uri, out);
				} finally {
					out.flush();
				}
			}
		}
	}
	
	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}
	
}
