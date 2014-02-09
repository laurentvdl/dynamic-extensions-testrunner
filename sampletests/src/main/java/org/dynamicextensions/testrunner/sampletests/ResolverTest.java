package org.dynamicextensions.testrunner.sampletests;

import com.github.dynamicextensionsalfresco.testrunner.ServiceResolver;
import com.github.dynamicextensionsalfresco.testrunner.junit.TestRunner;
import com.github.dynamicextensionsalfresco.testrunner.webscript.TestRunnerWebscript;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sample integration test using the {@link com.github.dynamicextensionsalfresco.testrunner.DefaultServiceResolver} to fetch test subjects.
 */
@Component
@RunWith(TestRunner.class)
public class ResolverTest {
  @Autowired
  private ServiceResolver serviceResolver;

  @Test
  public void testGetViaResolver() {
    final TestRunnerWebscript webscript = serviceResolver.getService("dynamic-extensions.testrunner", TestRunnerWebscript.class);
    Assert.assertNotNull("test dependency not found", webscript);
  }
}
