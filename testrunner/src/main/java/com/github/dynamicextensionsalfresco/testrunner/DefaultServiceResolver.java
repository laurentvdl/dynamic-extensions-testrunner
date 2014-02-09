package com.github.dynamicextensionsalfresco.testrunner;

import com.github.dynamicextensionsalfresco.osgi.OsgiService;
import com.github.dynamicextensionsalfresco.testrunner.util.ContextUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Helper for retrieving services from other bundles.
 *
 * @author Laurent Van der Linden
 */
@Component @OsgiService(interfaces = ServiceResolver.class)
public class DefaultServiceResolver implements InitializingBean, ServiceResolver {
  @Override
  public <T> T getService(String extensionName, Class<T> serviceType) {
    return getService(extensionName, serviceType, null);
  }

  @Override
  public <T> T getService(String bundleName, Class<T> requiredType, String beanName) {
    final ApplicationContext applicationContext = getApplicationContext(bundleName);
    if (applicationContext != null) {
      if (beanName != null) {
        return applicationContext.getBean(beanName, requiredType);
      } else {
        return applicationContext.getBean(requiredType);
      }
    }
    return null;
  }

  @Override
  public ApplicationContext getApplicationContext(String bundleName) {
    return ContextUtils.findApplicationContext(bundleName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {

  }
}
