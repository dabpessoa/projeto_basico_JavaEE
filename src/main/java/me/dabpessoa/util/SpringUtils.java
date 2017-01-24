package me.dabpessoa.util;

import me.dabpessoa.service.util.ApplicationContextProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.faces.context.FacesContext;
import java.io.IOException;

public class SpringUtils {

	public static final String contextPath = "classpath:applicationContext.xml";

	private static ApplicationContext context;

	private SpringUtils() {}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) getContext().getBean(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<?> clazz) { 
		return (T) getContext().getBean(clazz);
	}
	
	public static void init(final ApplicationContext contextApp) {
		context = contextApp;
	}

	public synchronized static ApplicationContext getContext() {
		if (context == null) {
			if (FacesContext.getCurrentInstance() != null) {
				context = JsfUtil.getWebApplicationContext();
			}
			
			if (context == null) {
				context = ApplicationContextProvider.getApplicationContext();
			}
			
			if (context == null) {
				context = new FileSystemXmlApplicationContext(contextPath);
			}
		}
		return context;
	}
	
	public static Resource[] findResources(String locationPattern) throws IOException {
		return new PathMatchingResourcePatternResolver().getResources(locationPattern);
	}

}