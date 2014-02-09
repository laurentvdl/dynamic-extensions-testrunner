package com.github.dynamicextensionsalfresco.testrunner.junit;

import com.github.dynamicextensionsalfresco.testrunner.util.ContextUtils;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

/**
 * Instantiates tests using the test-target Spring context instance.
 *
 * @author Laurent Van der Linden
 */
public class TestRunner extends BlockJUnit4ClassRunner {
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
      } catch (NoSuchBeanDefinitionException ignore) {}
    }
    return testClazz.newInstance();
  }
}
