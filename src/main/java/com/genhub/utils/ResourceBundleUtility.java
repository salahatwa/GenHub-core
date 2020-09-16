package com.genhub.utils;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class ResourceBundleUtility {
	@Autowired
   private  ResourceBundleMessageSource messageSource;

   public  String getMessage(String msgKey) {
      Locale locale = LocaleContextHolder.getLocale();
      return messageSource.getMessage(msgKey, null, locale);
   }

   public  String getMessage(String msgKey,Locale locale) {
	      return messageSource.getMessage(msgKey, null, locale);
	   }
   
   public  String getMessage(String msgKey,Object[] params,Locale locale) {
	      return messageSource.getMessage(msgKey, params, locale);
	   }
}
