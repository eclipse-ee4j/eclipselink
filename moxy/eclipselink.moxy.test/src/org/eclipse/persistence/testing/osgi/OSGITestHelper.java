/*******************************************************************************
 * Copyright (c) 2015, 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Dmitry Kornilov - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.osgi;

import org.ops4j.pax.exam.Option;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Helper class with PAX options for different kind of OSGi tests.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
public class OSGITestHelper {
    // Environment variables defined in antbuild.properties/antbuild.xml
    private static final String PLUGINS_DIR = System.getProperty("moxytest.2.common.plugins.dir");
    private static final String QUALIFIER = System.getProperty("build.qualifier", "qualifier");
    private static final String RELEASE_VERSION = System.getProperty("release.version", "2.7.0");
    private static final String JAXB_API_JAR = System.getProperty("jaxb-api.jar", "javax.xml.bind_2.2.12.v201410011542.jar");
    private static final String JAXRS_JAR = System.getProperty("jaxrs.jar", "javax.ws.rs_1.1.1.v20101004-1200.jar");
    private static final String ASM_JAR = System.getProperty("asm.jar", "org.eclipse.persistence.asm_6.0.0.v201702131300.jar");
    private static final String BEAN_VALIDATION_LIB = System.getProperty("javax.validation.lib", "javax.validation_1.1.0.v201304101302.jar");

    public static Option[] getDefaultOptions() {
        return options(
                // JAXB API
                bundle("file:" + PLUGINS_DIR + JAXB_API_JAR),

                // JAX-RS API
                bundle("file:" + PLUGINS_DIR + JAXRS_JAR),

                // EclipseLink bundles
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.moxy_" + RELEASE_VERSION + "." + QUALIFIER + ".jar"),
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.core_" + RELEASE_VERSION + "." + QUALIFIER + ".jar"),
                bundle("file:" + PLUGINS_DIR + ASM_JAR),

                cleanCaches(),
                junitBundles());
    }

    public static Option[] getOptionsWithBeanValidation() {
        return options(
                mavenBundle().groupId("org.hibernate").artifactId("hibernate-validator").version("5.2.0.CR1"),
                mavenBundle().groupId("com.fasterxml").artifactId("classmate").version("1.1.0"),
                mavenBundle().groupId("javax.el").artifactId("javax.el-api").version("3.0.0"),
                mavenBundle().groupId("org.jboss.logging").artifactId("jboss-logging").version("3.2.1.Final"),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-api").version("2.3"),
                mavenBundle().groupId("org.apache.logging.log4j").artifactId("log4j-core").version("2.3"),

                // JAXB API
                bundle("file:" + PLUGINS_DIR + JAXB_API_JAR),

                // JAX-RS API
                bundle("file:" + PLUGINS_DIR + JAXRS_JAR),

                // EclipseLink bundles
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.moxy_" + RELEASE_VERSION + "." + QUALIFIER + ".jar"),
                bundle("file:" + PLUGINS_DIR + "org.eclipse.persistence.core_" + RELEASE_VERSION + "." + QUALIFIER + ".jar"),
                bundle("file:" + PLUGINS_DIR + ASM_JAR),
                bundle("file:" + BEAN_VALIDATION_LIB),

                cleanCaches(),
                junitBundles()
        );
    }
}
