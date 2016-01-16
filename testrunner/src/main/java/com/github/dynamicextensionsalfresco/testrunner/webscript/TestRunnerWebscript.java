package com.github.dynamicextensionsalfresco.testrunner.webscript;

import com.github.dynamicextensionsalfresco.testrunner.BundleTest;
import com.github.dynamicextensionsalfresco.testrunner.TestScanner;
import com.github.dynamicextensionsalfresco.webscripts.annotations.HttpMethod;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Transaction;
import com.github.dynamicextensionsalfresco.webscripts.annotations.TransactionType;
import com.github.dynamicextensionsalfresco.webscripts.annotations.Uri;
import com.github.dynamicextensionsalfresco.webscripts.annotations.WebScript;
import com.google.common.collect.ImmutableMap;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Webscript for test listing and reporting.
 *
 * @author Laurent Van der Linden
 */
@Component
@WebScript(families = "testrunner", defaultFormat = "json")
@Transaction(TransactionType.NONE)
public class TestRunnerWebscript {
  private final static Logger logger = LoggerFactory.getLogger(TestRunnerWebscript.class);

  @Autowired
  TestScanner testFinder;

  @Autowired
  BundleContext bundleContext;

  @Uri(value = "/testrunner/", defaultFormat = "html")
  public Map<String,Object> index(WebScriptResponse response) {
    // force IE to Edge mode
    response.setHeader("X-UA-Compatible", "edge");

    return ImmutableMap.<String,Object>of("version", bundleContext.getBundle().getHeaders().get("Bnd-LastModified"));
  }

  @Uri(value = "/testrunner/tests")
  public void listTests(final WebScriptResponse response) throws Exception {
    final JSONWriter json = new JSONWriter(response.getWriter());
    json.array();
    final Set<BundleTest> tests = testFinder.getTests();
    if (tests != null) {
      for (BundleTest test : tests) {
        json.object()
          .key("className").value(test.getClassName())
          .key("bundleId").value(test.getBundleId())
          .key("bundleName").value(test.getBundleName())
        .endObject();
      }
    }
    json.endArray();
  }

  @Uri(value = "/testrunner/run", method = HttpMethod.POST)
  public void launch(final WebScriptRequest request, final WebScriptResponse response) throws Exception {
    final JSONObject testFilter = new JSONObject(request.getContent().getContent());

    final JSONWriter jr = new JSONWriter(response.getWriter());

    jr.array();

    JUnitCore core = new JUnitCore();
    core.addListener(new RunListener() {
      @Override
      public void testRunStarted(Description description) throws Exception {
      }

      @Override
      public void testFinished(Description description) throws Exception {
        logger.info("testFinished(" + description.getDisplayName() + ")");
        jr.endArray(); // end failure array
        jr.endObject(); // end test object
      }

      @Override
      public void testRunFinished(Result result) throws Exception {
        logger.info("testRunFinished: failures: " + result.getFailureCount() + " runcount: " + result.getRunCount() + " ran for " + result.getRunTime());
        jr.endArray(); // end methods
        jr.key("summary").object()
            .key("failurecount").value(result.getFailureCount())
            .key("runcount").value(result.getRunCount())
            .key("runtime").value(result.getRunTime())
            .key("ignorecount").value(result.getIgnoreCount())
            .endObject();
      }

      @Override
      public void testAssumptionFailure(Failure failure) {
        logger.info("testAssumptionFailure(" + failure + ")");
      }

      @Override
      public void testStarted(Description description) throws Exception {
        logger.info("testStarted(" + description.getClassName() + " - " + description.getDisplayName() + " " + description.getMethodName() + ")");
        jr.object()
            .key("method").value(description.getMethodName())
            .key("failures").array()
        ;
      }

      @Override
      public void testFailure(Failure failure) throws Exception {
        logger.error("testFailure(" + failure + ")", failure.getException());
        jr.object()
            .key("message").value(failure.getMessage())
            .key("trace").value(failure.getTrace())
            .endObject();
      }

      @Override
      public void testIgnored(Description description) throws Exception {
        logger.info("testIgnored(" + description + ")");
      }
    });
    final Set<BundleTest> tests = testFinder.getTests();
    if (tests != null) {
      final String classNameFilter = testFilter.has("className") ? testFilter.getString("className") : null;
      final String bundleNameFilter = testFilter.has("bundleName") ? testFilter.getString("bundleName") : null;
      for (BundleTest bundleTest : tests) {
        if (
            (classNameFilter == null || bundleTest.getClassName().toLowerCase().contains(classNameFilter.toLowerCase())) &&
            (bundleNameFilter == null || bundleTest.getBundleName().toLowerCase().contains(bundleNameFilter))
            ) {
          jr.object()
              .key("bundleName").value(bundleTest.getBundleName())
              .key("className").value(bundleTest.getClassName())
              .key("methods").array();
          logger.info("testing <{}>", bundleTest);
          final Bundle bundle = bundleContext.getBundle(bundleTest.getBundleId());
          core.run(bundle.loadClass(bundleTest.getClassName()));
          jr.endObject();
        }
      }
    }
    jr.endArray();
  }
}
