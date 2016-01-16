package com.github.dynamicextensionsalfresco.testrunner;

import com.github.dynamicextensionsalfresco.osgi.OsgiService;
import com.github.dynamicextensionsalfresco.testrunner.util.ContextUtils;
import com.google.common.base.Joiner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper for retrieving services from other bundles.
 *
 * @author Laurent Van der Linden
 */
@Component @OsgiService(interfaces = ServiceResolver.class)
public class DefaultServiceResolver implements InitializingBean, ServiceResolver {
  @Autowired
  BundleContext bundleContext;

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
    final ApplicationContext applicationContext = ContextUtils.findApplicationContext(bundleName);
    if (applicationContext == null) {
      List<String> bundleNames = new ArrayList<String>();
      for (Bundle bundle : bundleContext.getBundles()) {
        bundleNames.add(bundle.getSymbolicName());
      }
      throw new IllegalArgumentException(
              String.format("getService failed: could not find a bundle named %s, perhaps one of these: %s ?",
                      bundleName,
                      Joiner.on(',').join(bundleNames)
              )
      );
    }
    return applicationContext;
  }

  @Override
  public void afterPropertiesSet() throws Exception {

  }
}
