package me.dabpessoa.util;

import me.dabpessoa.service.util.MessageBundleService;
import org.primefaces.context.RequestContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class JsfUtil {

	private static final String NO_RESOURCE_FOUND = "Missing resource: ";

	public static Object resolveExpression(String expression) {
		FacesContext facesContext = getFacesContext();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
		return valueExp.getValue(elContext);
	}

	public static String resolveRemoteUser() {
		FacesContext facesContext = getFacesContext();
		ExternalContext ectx = facesContext.getExternalContext();
		return ectx.getRemoteUser();
	}

	public static String resolveUserPrincipal() {
		FacesContext facesContext = getFacesContext();
		ExternalContext ectx = facesContext.getExternalContext();
		HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
		return request.getUserPrincipal().getName();
	}

	public static Object resloveMethodExpression(String expression, @SuppressWarnings("rawtypes") Class returnType, @SuppressWarnings("rawtypes") Class[] argTypes,
			Object[] argValues) {
		FacesContext facesContext = getFacesContext();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		MethodExpression methodExpression = elFactory.createMethodExpression(elContext, expression, returnType,
				argTypes);
		return methodExpression.invoke(elContext, argValues);
	}

	public static Boolean resolveExpressionAsBoolean(String expression) {
		return (Boolean) resolveExpression(expression);
	}

	public static String resolveExpressionAsString(String expression) {
		return (String) resolveExpression(expression);
	}

	public static Object getManagedBeanValue(String beanName) {
		StringBuffer buff = new StringBuffer("#{");
		buff.append(beanName);
		buff.append("}");
		return resolveExpression(buff.toString());
	}

	public static void setExpressionValue(String expression, Object newValue) {
		FacesContext facesContext = getFacesContext();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp = elFactory.createValueExpression(elContext, expression, Object.class);
		@SuppressWarnings("rawtypes")
		Class bindClass = valueExp.getType(elContext);
		if (bindClass.isPrimitive() || bindClass.isInstance(newValue)) {
			valueExp.setValue(elContext, newValue);
		}
	}

	public static void setManagedBeanValue(String beanName, Object newValue) {
		StringBuffer buff = new StringBuffer("#{");
		buff.append(beanName);
		buff.append("}");
		setExpressionValue(buff.toString(), newValue);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void storeOnSession(String key, Object object) {
		FacesContext ctx = getFacesContext();
		Map sessionState = ctx.getExternalContext().getSessionMap();
		sessionState.put(key, object);
	}

	@SuppressWarnings("rawtypes")
	public static Object getFromSession(String key) {
		FacesContext ctx = getFacesContext();
		Map sessionState = ctx.getExternalContext().getSessionMap();
		return sessionState.get(key);
	}

	public static String getFromHeader(String key) {
		FacesContext ctx = getFacesContext();
		ExternalContext ectx = ctx.getExternalContext();
		return ectx.getRequestHeaderMap().get(key);
	}

	@SuppressWarnings("rawtypes")
	public static Object getFromRequest(String key) {
		FacesContext ctx = getFacesContext();
		Map sessionState = ctx.getExternalContext().getRequestMap();
		return sessionState.get(key);
	}

	public static String getStringFromBundle(String key) {
		ResourceBundle bundle = getBundle();
		return getStringSafely(bundle, key, null);
	}

	public static FacesMessage getMessageFromBundle(String key, FacesMessage.Severity severity) {
		ResourceBundle bundle = getBundle();
		String summary = getStringSafely(bundle, key, null);
		String detail = getStringSafely(bundle, key + "_detail", summary);
		FacesMessage message = new FacesMessage(summary, detail);
		message.setSeverity(severity);
		return message;
	}

	public static void addSucessMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_INFO, msg);
	}
	
	public static void addSucessMessageInDialog(String msg) {
		showMessageInDialog(FacesMessage.SEVERITY_INFO, msg);
	}

	public static void addErrorMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_ERROR, msg);
	}
	
	public static void addErrorMessageInDialog(String msg) {
		showMessageInDialog(FacesMessage.SEVERITY_ERROR, msg);
	}

	public static void addWarnMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_WARN, msg);
	}
	
	public static void addWarnMessageInDialog(String msg) {
		showMessageInDialog(FacesMessage.SEVERITY_WARN, msg);
	}

	private static void addMessage(FacesMessage.Severity severity, String msg) {
		FacesContext ctx = getFacesContext();
		FacesMessage fm = new FacesMessage(severity, msg, msg);
		ctx.addMessage(null, fm);
	}

	public static String getRootViewId() {
		return getFacesContext().getViewRoot().getViewId();
	}

	public static String getRootViewComponentId() {
		return getFacesContext().getViewRoot().getId();
	}

	public static FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}

	private static ResourceBundle getBundle() {
		FacesContext ctx = getFacesContext();
		UIViewRoot uiRoot = ctx.getViewRoot();
		Locale locale = uiRoot.getLocale();
		ClassLoader ldr = Thread.currentThread().getContextClassLoader();
		return ResourceBundle.getBundle(ctx.getApplication().getMessageBundle(), locale, ldr);
	}

	public static Object getRequestAttribute(String name) {
		return getFacesContext().getExternalContext().getRequestMap().get(name);
	}
	
	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) getFacesContext().getExternalContext().getRequest();
	}
	
	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) getFacesContext().getExternalContext().getResponse();
	}
	
	public static void dispatcher(String uri) throws ServletException, IOException {
		RequestDispatcher dispatcher = getRequest().getRequestDispatcher(uri);
		dispatcher.forward((ServletRequest)getRequest(), (ServletResponse)getResponse());
	}
	
	public static String getRequestParameter(String param) {
		return getRequest().getParameter(param);
	}

	public static void setRequestAttribute(String name, Object value) {
		getFacesContext().getExternalContext().getRequestMap().put(name, value);
	}

	private static String getStringSafely(ResourceBundle bundle, String key, String defaultValue) {
		String resource = null;
		try {
			resource = bundle.getString(key);
		} catch (MissingResourceException mrex) {
			if (defaultValue != null) {
				resource = defaultValue;
			} else {
				resource = NO_RESOURCE_FOUND + key;
			}
		}
		return resource;
	}

	public static UIComponent findComponentInRoot(String id) {
		UIComponent component = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			UIComponent root = facesContext.getViewRoot();
			component = findComponent(root, id);
		}
		return component;
	}

	public static UIComponent findComponent(UIComponent base, String id) {
		if (id.equals(base.getId()))
			return base;

		UIComponent children = null;
		UIComponent result = null;
		@SuppressWarnings("rawtypes")
		Iterator childrens = base.getFacetsAndChildren();
		while (childrens.hasNext() && (result == null)) {
			children = (UIComponent) childrens.next();
			if (id.equals(children.getId())) {
				result = children;
				break;
			}
			result = findComponent(children, id);
			if (result != null) {
				break;
			}
		}
		return result;
	}
	
	public static void throwMessagesToFacesContext(Collection<FacesMessage> messages){
		if(messages == null){
			return;
		}
		
		for (FacesMessage facesMessage : messages) {
			getFacesContext().addMessage(getRootViewId(), facesMessage);
		}
	}
	
	public static void cleanSubmittedValues(UIComponent component) {
		
		if (component instanceof EditableValueHolder) {
			EditableValueHolder evh = (EditableValueHolder) component;
			evh.setSubmittedValue(null);
			evh.setValue(null);
			evh.setLocalValueSet(false);
			evh.setValid(true);
		}
		
		if(component.getChildCount() > 0){
			for (UIComponent child : component.getChildren()) {
				cleanSubmittedValues(child);
			}
		}
	}
	
	public static UIComponent findComponent(String clientId){
		return FacesContext.getCurrentInstance().getViewRoot().findComponent(clientId);
	}
	
	public static void cleanSubmittedValues(String clientId){
		UIComponent component = findComponent(clientId);
		
		if(component == null){
			throw new FacesException("Componente não encontrado: "+clientId);
		}
		
		cleanSubmittedValues(component);
	}
	
	public static ApplicationContext getWebApplicationContext(){
		return  FacesContextUtils.getWebApplicationContext(FacesContext.getCurrentInstance());
	}
	
	public static void showMessageInDialog(FacesMessage.Severity severity, String message){
		MessageBundleService msgService = SpringUtils.getBean(MessageBundleService.class);
		FacesMessage msg = new FacesMessage(severity, msgService.findMessage("label.dialogmessages"), message);
		RequestContext.getCurrentInstance().showMessageInDialog(msg);
	}
	
	public static String getBaseURL() {
		return getRequest().getScheme()+"://"+getRequest().getServerName()+":"+getRequest().getServerPort()+getRequest().getContextPath();
	}
	
}