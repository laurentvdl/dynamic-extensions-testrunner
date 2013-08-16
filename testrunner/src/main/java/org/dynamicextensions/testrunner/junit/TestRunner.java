package org.dynamicextensions.testrunner.junit;

import org.dynamicextensions.testrunner.Constants;
import org.dynamicextensions.testrunner.util.ContextUtils;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * Instantiates tests using the test-target Spring context instance.
 *
 * @author Laurent Van der Linden
 */
public class TestRunner extends BlockJUnit4ClassRunner {
  private final static Logger logger = LoggerFactory.getLogger(TestRunner.class);

  private Class<?> testClazz;

  public TestRunner(Class<?> klass) throws InitializationError {
    super(klass);
    this.testClazz = klass;
  }

  @Override
  protected Object createTest() throws Exception {
    final Bundle testBundle = FrameworkUtil.getBundle(testClazz);

    // check if test is defined as Spring component
    final ApplicationContext testContext = ContextUtils.findApplicationContext(testBundle.getSymbolicName());
    if (testContext != null) {
      try {
        return testContext.getBean(testClazz);
      } catch (NoSuchBeanDefinitionException ignore) {
      }
    }

    // inject testclass into target context
    final String testRunnerTarget = testBundle.getHeaders().get(Constants.TEST_RUNNER_TARGET);
    if (testRunnerTarget != null) {
      logger.debug("injecting test {} into Spring context[{}]", testClazz, testRunnerTarget);
      final ApplicationContext applicationContext = ContextUtils.findApplicationContext(testRunnerTarget);
      if (applicationContext != null) {
        return applicationContext.getAutowireCapableBeanFactory().createBean(testClazz);
      }
    }
    return testClazz.newInstance();
  }
}
