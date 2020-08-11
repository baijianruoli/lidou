package com.zut.lpf.lidou.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public  class  BeanScannerConfigurer implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public  void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        AnnotationScanner scanner = AnnotationScanner.getScanner((BeanDefinitionRegistry) configurableListableBeanFactory, LidouService.class);
        String property = applicationContext.getEnvironment().getProperty("lidou.servicePackage");
        scanner.setResourceLoader(applicationContext);
        int count=scanner.scan(property);
        Map<String, Object> beansWithAnnotation = configurableListableBeanFactory.getBeansWithAnnotation(LidouService.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
           this.applicationContext=applicationContext;
    }
}
