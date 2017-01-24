package me.dabpessoa.service.util;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Locale;

@Service
public class MessageBundleService {

	@Resource(name = "messageSource")
	private MessageSource messageSource;
	
	private Locale locale;
	
	public MessageBundleService() {
		locale = Locale.getDefault();
	}

	public MessageBundleService(Locale locale) {
		this.locale = locale;
	}
	
	public String findMessage(String key, Object... params) {
		return messageSource.getMessage(key, params, "["+this.getClass().getName()+"] Chave "+key+" não encontrada no arquivo de mensagens.", locale);
	}
	
	public String findMessage(String key) {
		return messageSource.getMessage(key, null, "["+this.getClass().getName()+"] Chave "+key+" não encontrada no arquivo de mensagens.", locale);
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
}