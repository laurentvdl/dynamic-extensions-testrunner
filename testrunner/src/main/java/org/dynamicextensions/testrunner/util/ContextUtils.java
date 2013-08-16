package org.dynamicextensions.testrunner.util;

import org.osgi.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Static utilities for finding an ApplicationContext in the OSGi environment.
 *
 * @author Laurent Van der Linden
 */
public class ContextUtils {
  private final static Logger logger = LoggerFactory.getLogger(ContextUtils.class);

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
        logger.warn(
            "could not find spring context for bundle[{}], make sure the {} name is correct",
            bundleName, org.dynamicextensions.testrunner.Constants.TEST_RUNNER_TARGET
        );
      }
    } catch (InvalidSyntaxException e) {
      throw new RuntimeException(e);
    }
    return null;
  }
}
