/*
*  Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.net.MalformedURLException;
import java.nio.file.Path;

import static org.ops4j.pax.exam.CoreOptions.bundle;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;

/**
 * Helper class with PAX options for different kind of OSGi tests.
 *
 * @author Dmitry Kornilov
 * @since 2.7.0
 */
public class OSGITestHelper {
    // Environment variables defined in antbuild.properties/antbuild.xml
    private static final String PLUGINS_DIR = System.getProperty("plugins.dir","target/osgi-test-plugins/");
    private static final String ACTIVATION_JAR = System.getProperty("activation.jar", "jakarta.activation-api.jar");
    private static final String JAXB_API_JAR = System.getProperty("jaxb-api.jar", "jakarta.xml.bind-api.jar");
    private static final String JAXB_OSGI = System.getProperty("jaxb-osgi.jar", "jaxb-xjc.jar");
    private static final String JAXRS_JAR = System.getProperty("jaxrs.jar", "jakarta.ws.rs-api.jar");
    private static final String ASM_JAR = System.getProperty("asm.jar", "org.eclipse.persistence.asm.jar");

    public static Option[] getDefaultOptions() {

        return options(
                // JAXB API
                bundle(pathToUrl(PLUGINS_DIR + ACTIVATION_JAR)),
                bundle(pathToUrl(PLUGINS_DIR + JAXB_API_JAR)),
                //JAXB_OSGI
                bundle(pathToUrl(PLUGINS_DIR + JAXB_OSGI)),

                systemPackage("javax.rmi"),
                systemPackage("javax.rmi.CORBA"),
                systemPackage("org.omg.CORBA"),
                systemPackage("org.omg.CORBA.portable"),
                systemPackage("org.omg.CORBA_2_3.portable"),
                // JAX-RS API
                bundle(pathToUrl(PLUGINS_DIR + JAXRS_JAR)),

                // EclipseLink bundles
                bundle(pathToUrl(PLUGINS_DIR + "org.eclipse.persistence.moxy.jar")),
                bundle(pathToUrl(PLUGINS_DIR + "org.eclipse.persistence.core.jar")),
                bundle(pathToUrl(PLUGINS_DIR + ASM_JAR)),

                cleanCaches(),
                junitBundles());
    }

    public static Option[] getOptionsWithBeanValidation() {
        return options(
                // JAXB API
                bundle(pathToUrl(PLUGINS_DIR + ACTIVATION_JAR)),
                bundle(pathToUrl(PLUGINS_DIR + JAXB_API_JAR)),

                // JAX-RS API
                bundle(pathToUrl(PLUGINS_DIR + JAXRS_JAR)),

                // EclipseLink bundles
                bundle(pathToUrl(PLUGINS_DIR + "org.eclipse.persistence.moxy.jar")),
                bundle(pathToUrl(PLUGINS_DIR + "org.eclipse.persistence.core.jar")),
                bundle(pathToUrl(PLUGINS_DIR + ASM_JAR)),

                bundle(pathToUrl(PLUGINS_DIR + "jboss-logging.jar")),
                bundle(pathToUrl(PLUGINS_DIR + "jakarta.validation-api.jar")),
                bundle(pathToUrl(PLUGINS_DIR + "hibernate-validator.jar")),
                bundle(pathToUrl(PLUGINS_DIR + "classmate.jar")),
                bundle(pathToUrl(PLUGINS_DIR + "jakarta.el-api.jar")),
                bundle(pathToUrl(PLUGINS_DIR + "expressly.jar")),
                cleanCaches(),
                junitBundles()
        );
    }

    private static String pathToUrl(String path) {
        try {
            return Path.of(path).toUri().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
