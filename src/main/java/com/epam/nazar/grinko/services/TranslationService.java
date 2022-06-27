package com.epam.nazar.grinko.services;

import lombok.AllArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@AllArgsConstructor
public class TranslationService {

    //private ResourceBundleMessageSource messageSource;

    public String toLocale(String code) {
        //Locale locale = LocaleContextHolder.getLocale();
        return null; //messageSource.getMessage(code, null, locale);
    }

}
