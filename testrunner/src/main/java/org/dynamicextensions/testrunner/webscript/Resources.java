package org.dynamicextensions.testrunner.webscript;

import nl.runnable.alfresco.webscripts.annotations.*;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Resource handler config.
 *
 * @author Laurent Van der Linden
 */
@Component
@WebScript
@Authentication(AuthenticationType.NONE)
public class Resources extends AbstractBundleResourceHandler {
    private final String packagePath;

    public Resources() {
        packagePath = this.getClass().getPackage().getName().replace('.', '/');
    }

    @Uri(value = "/testrunner/resources/{path}", formatStyle = FormatStyle.ARGUMENT)
    public void handleResources(@UriVariable final String path, final WebScriptResponse response) throws IOException {
        handleResource(path, response);
    }

    @Override
    protected String getBundleEntryPath(final String path) {
        return String.format("%s/%s", packagePath, path);
    }
}
