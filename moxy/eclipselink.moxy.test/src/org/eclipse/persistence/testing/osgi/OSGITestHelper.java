/*
 * Copyright (c) 2016, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Dmitry Kornilov - initial implementation
package org.eclipse.persistence.testing.osgi;

import org.ops4j.pax.exam.Option;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.vmOptions;
import static org.ops4j.pax.exam.CoreOptions.when;

import org.eclipse.persistence.internal.helper.JavaSEPlatform;

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
    private static final String ACTIVATION_JAR = System.getProperty("activation.jar", "jakarta.activation.jar");
    private static final String JAXB_API_JAR = System.getProperty("jaxb-api.jar", "jakarta.xml.bind-api.jar");
    private static final String JAXRS_JAR = System.getProperty("jaxrs.jar", "javax.ws.rs_1.1.1.v20101004-1200.jar");
    private static final String ASM_JAR = System.getProperty("asm.jar", "org.eclipse.persistence.asm_7.3.1.v202003311108.jar");
    private static final String ASM_VERSION = System.getProperty("asm.version", "7.1.0.v201909231337");
    private static final String BEAN_VALIDATION_LIB = System.getProperty("javax.validation.lib", "jakarta.validation-api.jar");

    public static Option[] getDefaultOptions() {
        return options(
                systemProperty("asm.version").value(ASM_VERSION),
                // JAXB API
                bundle("file:" + PLUGINS_DIR + ACTIVATION_JAR),
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
                mavenBundle().groupId("org.hibernate.validator").artifactId("hibernate-validator").version("6.0.7.Final"),
                mavenBundle().groupId("com.fasterxml").artifactId("classmate").version("1.3.1"),
                mavenBundle().groupId("org.glassfish").artifactId("javax.el").version("3.0.1-b08"),
                mavenBundle().groupId("org.jboss.logging").artifactId("jboss-logging").version("3.3.0.Final"),

                // JAXB API
                bundle("file:" + PLUGINS_DIR + ACTIVATION_JAR),
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
