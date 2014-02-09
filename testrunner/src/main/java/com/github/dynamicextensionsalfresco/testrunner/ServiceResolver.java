package com.github.dynamicextensionsalfresco.testrunner;

import org.springframework.context.ApplicationContext;

/**
 * @author Laurent Van der Linden
 */
public interface ServiceResolver {
    <T> T getService(String extensionName, Class<T> serviceType);

    <T> T getService(String bundleName, Class<T> requiredType, String beanName);

    ApplicationContext getApplicationContext(String bundleName);
}
