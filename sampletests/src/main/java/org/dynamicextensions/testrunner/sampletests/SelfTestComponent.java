package org.dynamicextensions.testrunner.sampletests;

import junit.framework.Assert;
import org.dynamicextensions.testrunner.ServiceResolver;
import org.dynamicextensions.testrunner.junit.TestRunner;
import org.dynamicextensions.testrunner.webscript.TestRunnerWebscript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sample integration test using the {@link ServiceResolver} to fetch test subjects.
 */
@Component
@RunWith(TestRunner.class)
public class SelfTestComponent {
  @Autowired
  private ServiceResolver serviceResolver;

  @Test
  public void testAutowiring() {
    final TestRunnerWebscript webscript = serviceResolver.getService("dynamic-extensions.testrunner", TestRunnerWebscript.class);
    Assert.assertNotNull("test dependency not found", webscript);
  }
}
