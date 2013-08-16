Dynamic Extensions TestRunner
=============================

The <a href="http://github.com/lfridael/dynamic-extensions-for-alfresco">Dynamic extensions</a> project enables rapid
development of Alfresco extensions.

This opens the possibility to hot deploy integration tests as well as separate extensions/bundles.

Tests are run manually from a webscript: <a href="http://localhost:8080/alfresco/service/testrunner/">testrunner</a>.

This extension enables 2 ways of accessing dependencies from integration tests:
* write your test as a Spring component and inject the `ServiceResolver` explicitly to fetch the service you want to test:
```java
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
```
* or specify a target-bundle bundle in your test-bundle manifest:
```groovy
instruction 'Testrunner-Target', 'dynamic-extensions.testrunner'
instruction 'Testrunner-Packages', 'org.dynamicextensions.testrunner.sampletests'
```
and your test class will be injected into the target-bundle's Spring context:
```java
@RunWith(TestRunner.class)
public class SelfTest {
  @Autowired
  private TestRunnerWebscript testRunnerWebscript;

  @Test
  public void testAutowiring() {
    Assert.assertNotNull("test dependency not injected", testRunnerWebscript);
  }
}

```

This allows for autowiring target-bundle services into your test, but is more of a hack.

Improvements
============

* if dynamic-extensions would allow for bundle contexts to become child contexts of another bundle, the autowiring of tests
 would become more elegant
* integrate test execution as a gradle task
