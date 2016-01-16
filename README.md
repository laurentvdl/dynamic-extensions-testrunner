Dynamic Extensions TestRunner
=============================

The <a href="http://github.com/laurentvdl/dynamic-extensions-for-alfresco">Dynamic extensions</a> project enables rapid
development of Alfresco extensions.

This opens the possibility to hot deploy integration tests as well as separate extensions/bundles.

Tests are run manually from a webscript: <a href="http://localhost:8080/alfresco/service/testrunner/">testrunner</a>.

* write your test as a Spring component and inject the `ServiceResolver` explicitly to fetch the service you want to test:
```java
@Component
@RunWith(TestRunner.class)
public class SelfTestComponent {
  @Autowired
  private ServiceResolver serviceResolver;

  @Test
  public void testAutowiring() {
    final TestRunnerWebscript webscript = serviceResolver.getService("<symbolic bundle name>", TestRunnerWebscript.class);
    Assert.assertNotNull("test dependency not found", webscript);
  }
}
```

Alternatively, if your test target is an Osgi service:
```java
@OsgiService
public class DefaultCustomService implements CustomService { ... }
```
then you can @Autowired that service from your tests.

Notes
=====
* the TestRunner-Target approach was removed as it was too obtrusive
