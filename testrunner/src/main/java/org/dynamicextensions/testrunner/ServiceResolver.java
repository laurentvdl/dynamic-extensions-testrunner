package org.dynamicextensions.testrunner;

import org.dynamicextensions.testrunner.osgi.ExportOsgiService;
import org.dynamicextensions.testrunner.util.ContextUtils;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Helper for retrieving services from other bundles.
 *
 * @author Laurent Van der Linden
 */
@Component @ExportOsgiService(interfaces = ServiceResolver.class, headers = {})
public class ServiceResolver implements InitializingBean {
  @Autowired
  private BundleContext bundleContext;

  public <T> T getService(String extensionName, Class<T> serviceType) {
    return getService(extensionName, serviceType, null);
  }

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

  public ApplicationContext getApplicationContext(String bundleName) {
    return ContextUtils.findApplicationContext(bundleName);
  }

  @Override
  public void afterPropertiesSet() throws Exception {

  }
}
