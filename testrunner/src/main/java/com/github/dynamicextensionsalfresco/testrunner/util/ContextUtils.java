package com.github.dynamicextensionsalfresco.testrunner.util;

import org.osgi.framework.*;
import org.springframework.context.ApplicationContext;

/**
 * Static utilities for finding an ApplicationContext in the OSGi environment.
 *
 * @author Laurent Van der Linden
 */
public class ContextUtils {
  public static BundleContext getBundleContext() {
    final Bundle bundle = FrameworkUtil.getBundle(ContextUtils.class);
    return bundle.getBundleContext();
  }

  public static ApplicationContext findApplicationContext(final String bundleName) {
    try {
      final BundleContext bundleContext = getBundleContext();
      if (bundleContext != null) {
        ServiceReference<?>[] references = bundleContext.getAllServiceReferences(
            ApplicationContext.class.getName(),
            String.format("(org.springframework.context.service.name=%s)", bundleName)
        );
        if (references != null && references.length > 0) {
          final ServiceReference<?> contextServiceReference = references[0];
          return (ApplicationContext) bundleContext.getService(contextServiceReference);
        }
      }
    } catch (InvalidSyntaxException e) {
      throw new RuntimeException(e);
    }
    return null;
  }
}
