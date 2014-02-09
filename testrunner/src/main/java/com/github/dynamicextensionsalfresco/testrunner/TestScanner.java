package com.github.dynamicextensionsalfresco.testrunner;

import com.github.dynamicextensionsalfresco.testrunner.util.ContextUtils;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Gather classes annotated with {@link RunWith} from Bundles that have the {@code Testrunner-Target} header.
 *
 * @author Laurent Van der Linden
 */
@Component
public class TestScanner implements BundleContextAware {
  private final static Logger logger = LoggerFactory.getLogger(TestScanner.class);

  private BundleContext bundleContext;

  @SuppressWarnings("unchecked")
  public Set<BundleTest> getTests() throws InvalidSyntaxException {
    final Set<BundleTest> allTests = new TreeSet<BundleTest>();
    final Bundle[] bundles = bundleContext.getBundles();
    for (Bundle bundle : bundles) {
      if (bundle.getState() == Bundle.ACTIVE) {
          if ("true".equalsIgnoreCase(bundle.getHeaders().get("Alfresco-Dynamic-Extension"))) {
              ApplicationContext applicationContext = ContextUtils.findApplicationContext(bundle.getSymbolicName());
              if (applicationContext != null) {
                  Map<String,Object> testComponents = applicationContext.getBeansWithAnnotation(RunWith.class);
                  logger.debug("Looking for JUnit tests in {}", bundle.getSymbolicName());
                  for (Object test : testComponents.values()) {
                      BundleTest bundleTest = new BundleTest(test.getClass().getName(), bundle.getBundleId(), bundle.getSymbolicName());
                      logger.debug("Found test: {}", test);
                      allTests.add(bundleTest);
                  }
              }
          }
      }
    }
    return allTests;
  }

  @Override
  public void setBundleContext(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }
}
