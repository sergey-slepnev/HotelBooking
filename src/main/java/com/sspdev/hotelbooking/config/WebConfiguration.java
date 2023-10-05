package com.sspdev.hotelbooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(Jsr310Converters.StringToLocalDateConverter.INSTANCE);
    }
}
