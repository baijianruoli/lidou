package io.github.baijianruoli.lidou.config;

import io.github.baijianruoli.lidou.annotation.LidouService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


@Component
public class BeanScannerConfigurer implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        AnnotationScanner scanner = AnnotationScanner.getScanner((BeanDefinitionRegistry) configurableListableBeanFactory, LidouService.class);
        String property = applicationContext.getEnvironment().getProperty("lidou.servicePackage");
        if (property == null)
            property = "io.github.baijianruoli";
        scanner.setResourceLoader(applicationContext);
        int count = scanner.scan(property);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
