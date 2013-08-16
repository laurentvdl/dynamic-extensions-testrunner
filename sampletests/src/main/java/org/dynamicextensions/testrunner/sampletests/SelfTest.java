package org.dynamicextensions.testrunner.sampletests;

import junit.framework.Assert;
import org.dynamicextensions.testrunner.junit.TestRunner;
import org.dynamicextensions.testrunner.webscript.TestRunnerWebscript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Sample integration test. This class should not be annotated as a Spring component.
 */
@RunWith(TestRunner.class)
public class SelfTest {
  @Autowired
  private TestRunnerWebscript testRunnerWebscript;

  @Test
  public void testAutowiring() {
    Assert.assertNotNull("test dependency not injected", testRunnerWebscript);
  }
}
