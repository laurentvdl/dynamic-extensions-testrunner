package org.dynamicextensions.testrunner;

import org.dynamicextensions.testrunner.util.ContextUtils;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

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
        final String targetHeader = bundle.getHeaders().get(Constants.TEST_RUNNER_TARGET);
        final String packagesHeader = bundle.getHeaders().get(Constants.TEST_RUNNER_PACKAGES);
        if (targetHeader != null && packagesHeader != null) {
          final String[] packages = packagesHeader.split(",");
          for (String aPackage : packages) {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            final ApplicationContext targetContext = ContextUtils.findApplicationContext(bundle.getSymbolicName());
            if (targetContext != null) {
              scanner.setResourceLoader(targetContext);
              scanner.addIncludeFilter(new AnnotationTypeFilter(RunWith.class));
              logger.debug("scan for tests in {},{}", bundle.getSymbolicName(), aPackage);
              try {
                final Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(aPackage);
                for (BeanDefinition candidateComponent : candidateComponents) {
                  allTests.add(new BundleTest(candidateComponent.getBeanClassName(), bundle.getBundleId(), bundle.getSymbolicName()));
                  logger.debug("add test <{}>", candidateComponent.getBeanClassName());
                }
              } catch (RuntimeException rte) {
                logger.error("classpath scanning failed", rte);
              }
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
