package org.dynamicextensions.testrunner.osgi;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

/**
 * Exports beans annotated with {@link ExportOsgiService} as an OSGi service, making them available for autowiring in other bundles.
 *
 * @author Laurent Van der Linden
 */
@Component
public class OsgiExporter implements InitializingBean, ApplicationContextAware, BundleContextAware {
    private ApplicationContext applicationContext;
    private BundleContext bundleContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        final Map<String,Object> exportables = applicationContext.getBeansWithAnnotation(ExportOsgiService.class);
        for (Map.Entry<String, Object> entry : exportables.entrySet()) {
            final ExportOsgiService exportDefinition = applicationContext
                    .findAnnotationOnBean(entry.getKey(), ExportOsgiService.class);

            final String[] classNames;
            final int nbrInterfaces = exportDefinition.interfaces().length;
            if (nbrInterfaces == 0) {
                classNames = new String[] {entry.getValue().getClass().getName()};
            } else {
                classNames = new String[nbrInterfaces];
                for (int i = 0; i < nbrInterfaces; i++) {
                    Class aClass = exportDefinition.interfaces()[i];
                    classNames[i] = aClass.getName();
                }
            }

            final Dictionary<String,String> dictionary = new Hashtable<String,String>(exportDefinition.headers().length);
            for (int i = 0; i < exportDefinition.headers().length; i++) {
                ExportOsgiService.ExportHeader header = exportDefinition.headers()[i];
                dictionary.put(header.key(), header.value());
            }

            bundleContext.registerService(classNames, entry.getValue(), dictionary);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
}
